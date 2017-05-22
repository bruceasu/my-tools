/*
 * Copyright © 2016 Victor.su<victor.su@gwtsz.net>
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * “Software”), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package asu.tool.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.*;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.nutz.lang.Strings;

/**
 * Created by bruce on 3/11/15.
 */
public class ZKClient implements Closeable
{
    // example
    public class ZKWatch implements CuratorWatcher
    {
        private final String path;

        public String getPath()
        {
            return path;
        }

        public ZKWatch(String path)
        {
            this.path = path;
        }

        @Override
        public void process(WatchedEvent event) throws Exception
        {
            if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                // byte[] data = curator.getData().usingWatcher(this).forPath(path);
            }
        }

    }

    public class ZKWatchRegister implements CuratorWatcher
    {
        private String path;
        private byte[] value;

        public String getPath()
        {
            return path;
        }

        public ZKWatchRegister(String path, byte[] value)
        {
            this.path = path;
            this.value = value;
        }

        @Override
        public void process(WatchedEvent event) throws Exception
        {
            //            System.out.println(event.getType());
            Watcher.Event.EventType type = event.getType();
            if (type == Watcher.Event.EventType.NodeDataChanged) {
                //节点数据改变了，需要记录下来，以便session过期后，能够恢复到先前的数据状态
                byte[] data = curator.getData().usingWatcher(this).forPath(path);
                value = data;
            } else if (type == Watcher.Event.EventType.NodeDeleted) {
                //节点被删除了，需要创建新的节点
                //                System.out.println(path + ":" + path + " has been deleted.");
                Stat stat = curator.checkExists().usingWatcher(this).forPath(path);
                if (stat == null) {
                    curator.create()
                            .creatingParentsIfNeeded()
                            .withMode(CreateMode.EPHEMERAL)
                            .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                            .forPath(path);
                }
            } else if (type == Watcher.Event.EventType.NodeCreated) {
                //节点被创建时，需要添加监听事件
                // （创建可能是由于session过期后，curator的状态监听部分触发的）
                //                System.out.println(path + ":" + " has been created!" + "the current data is " + new String(value));
                curator.setData().forPath(path, value);
                curator.getData().usingWatcher(this).forPath(path);
            }
            if (event.getState() == Watcher.Event.KeeperState.Expired) {
                Stat stat = curator.checkExists().usingWatcher(this).forPath(path);
                if (stat == null) {
                    curator.create()
                            .creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                            .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                            .forPath(path); //创建的路径和值
                }
            }
        }
    }

    public enum ZookeeperWatcherType
    {
        GET_DATA, GET_CHILDREN, EXISTS, CREATE_ON_NO_EXITS
    }

    public static final String CHARSET = "utf-8";
    public static final String DEFAULT_ZK = "localhost:2181";
    public static final int DEFUALLT_ZKMAXRETRY = Integer.MAX_VALUE;
    private static Map<String, ZKClient> instances = new HashMap<>();

    protected Map<String, ConnectionStateListener> cacheConnStateListner = new ConcurrentHashMap<>();

    protected ConcurrentSkipListSet watchers = new ConcurrentSkipListSet();
    protected CuratorFramework curator;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                instances.values().stream().forEach(zkClient -> zkClient.curator.close());
            }
        });
    }

    public static ZKClient getClient(String namespace)
    {
        return instances.get(namespace);
    }

    public static ZKClient create(String namespace, String zookeeperConnectionString,
                                  int zkMaxRetry, Map<String, Object> configs) throws IOException
    {
        if (!instances.containsKey(namespace)) {
            synchronized (ZKClient.class) {
                if (!instances.containsKey(namespace)) {
                    ZKClient instance = new ZKClient(
                            namespace, zookeeperConnectionString, zkMaxRetry, configs);
                    instances.put(namespace, instance);
                }
            }
        }
        return getClient(namespace);
    }

    public static ZKClient createNoCache(String namespace, String zookeeperConnectionString,
                                         int zkMaxRetry, Map<String, Object> configs) throws IOException
    {
        return new ZKClient(namespace, zookeeperConnectionString, zkMaxRetry, configs);
    }

    public void removeReconnectionWatcher(final String path,
                                          final ZookeeperWatcherType type)
    {
        ConnectionStateListener connectionStateListener =
                cacheConnStateListner.get(path + "@" + type);
        if (connectionStateListener != null) {
            curator.getConnectionStateListenable().removeListener(connectionStateListener);
        }

    }

    public void addReconnectionWatcher(final String path,
                                       final ZookeeperWatcherType type,
                                       final CuratorWatcher watcher)
    {
        synchronized (this) {
            if (!watchers.contains(watcher.toString())) { // 不要添加重复的监听事件
                watchers.add(watcher.toString());

                // System.out.println("add new watcher " + watcher);
                // new ConnectionStateListener()
                ConnectionStateListener connectionStateListener = createConnectionStateListener(path, type, watcher);
                cacheConnStateListner.put(path + "@" + type, connectionStateListener);
                curator.getConnectionStateListenable().addListener(connectionStateListener);
            }
        }
    }

    private ConnectionStateListener createConnectionStateListener(
            String path, ZookeeperWatcherType type, CuratorWatcher watcher)
    {
        return (client, newState) -> {
            if (newState == ConnectionState.LOST) {//处理session过期
                try {
                    if (type == ZookeeperWatcherType.EXISTS) {
                        client.checkExists().usingWatcher(watcher).forPath(path);
                    } else if (type == ZookeeperWatcherType.GET_CHILDREN) {
                        client.getChildren().usingWatcher(watcher).forPath(path);
                    } else if (type == ZookeeperWatcherType.GET_DATA) {
                        client.getData().usingWatcher(watcher).forPath(path);
                    } else if (type == ZookeeperWatcherType.CREATE_ON_NO_EXITS) {
                        // ephemeral类型的节点session过期了，需要重新创建节点，
                        // 并且注册监听事件，之后监听事件中，
                        // 会处理create事件，将路径值恢复到先前状态
                        Stat stat = client.checkExists().usingWatcher(watcher).forPath(path);
                        if (stat == null) {
                            client.create()
                                    .creatingParentsIfNeeded()
                                    .withMode(CreateMode.EPHEMERAL)
                                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                                    .forPath(path);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public DistributedAtomicLong getDistributedAtomicLong(String name)
    {
        DistributedAtomicLong count = new DistributedAtomicLong(getCurator(),
                name, new RetryNTimes(10, 10));
        return count;
    }

    public void deleteIfExists(String path) throws Exception
    {
        Stat stat = checkExists().forPath(path);
        if (stat != null) {
            delete().deletingChildrenIfNeeded().forPath(path);
        }
    }

    public void unregister(final String registeNode) throws Exception
    {
        removeReconnectionWatcher(registeNode, ZookeeperWatcherType.CREATE_ON_NO_EXITS);
        Stat stat = checkExists().forPath(registeNode);
        if (stat != null) {
            delete().deletingChildrenIfNeeded().forPath(registeNode);
        }
    }

    public void register(final String registeNode) throws Exception
    {
        final byte[] data = "enable".getBytes(CHARSET);
        register(registeNode, data);
    }

    /**
     * @param registeNode
     *         节点路径
     * @param data
     *         节点值
     * @throws Exception
     *         zookeeper exception
     */
    public void register(final String registeNode, final byte[] data) throws Exception
    {
        Stat stat = curator.checkExists().forPath(registeNode);
        if (stat != null) {
            curator.delete().forPath(registeNode);
        }

        //添加到session过期监控事件中
        addReconnectionWatcher(registeNode,
                ZookeeperWatcherType.CREATE_ON_NO_EXITS,
                new ZKWatchRegister(registeNode, data));

        curator.create()
                .creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(registeNode, data); //创建的路径和值
    }

    public CuratorFramework getCurator()
    {
        return curator;
    }

    public static <T> byte[] serializer(T item, QueueSerializer<T> serializer)
            throws Exception
    {
        final int VERSION = 0x00010001;
        final int INITIAL_BUFFER_SIZE = 0x1000;
        final byte ITEM_OPCODE = 0x01;
        final byte EOF_OPCODE = 0x02;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(INITIAL_BUFFER_SIZE);
        DataOutputStream out = new DataOutputStream(bytes);
        out.writeInt(VERSION);

        byte[] itemBytes = serializer.serialize(item);
        out.writeByte(ITEM_OPCODE);
        out.writeInt(itemBytes.length);
        if (itemBytes.length > 0) {
            out.write(itemBytes);
        }
        out.writeByte(EOF_OPCODE);
        out.close();
        return bytes.toByteArray();
    }


    private ZKClient(String namespace, String zookeeperConnectionString,
                     int zkMaxRetry, Map<String, Object> configs) throws IOException
    {
        if (Strings.isBlank(zookeeperConnectionString)) {
            zookeeperConnectionString = DEFAULT_ZK;
        }
        if (zkMaxRetry < 1) {
            zkMaxRetry = DEFUALLT_ZKMAXRETRY;
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, zkMaxRetry);
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        builder.retryPolicy(retryPolicy);
        builder.connectString(zookeeperConnectionString);
        if (configs != null) {
            if (configs.containsKey("zkSessionTimeoutMs")) {
                builder.sessionTimeoutMs((Integer) configs.get("zkSessionTimeoutMs"));
            }
            if (configs.containsKey("zkConnectionTimeoutMs")) {
                builder.connectionTimeoutMs((Integer) configs.get("connectionTimeoutMs"));
            }
            if (configs.containsKey("zkMaxCloseWaitMs")) {
                builder.maxCloseWaitMs((Integer) configs.get("zkMaxCloseWaitMs"));
            }
        }

        //curator = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        if (Strings.isNotBlank(namespace)) {
            builder.namespace(namespace);
            //curator.usingNamespace(namespace);
        }
        curator = builder.build();
        curator.start();
        try {
            curator.blockUntilConnected();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 不会自动注册
        //MyConnectionStateListener stateListener = new MyConnectionStateListener(zkRegPathPrefix, regContent);
        //curator.getConnectionStateListenable().addListener(stateListener);

    }

    //    public PathChildrenCache pathChildrenCache(String path, Boolean cacheData) throws Exception {
    //        final PathChildrenCache cached = new PathChildrenCache(curator, path, cacheData);
    //        cached.getListenable().addListener(new PathChildrenCacheListener() {
    //            @Override
    //            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
    //                PathChildrenCacheEvent.Type eventType = event.getType();
    //                switch (eventType) {
    //                    case CONNECTION_RECONNECTED:
    //                        cached.rebuild();
    //                        break;
    //                    case CONNECTION_SUSPENDED:
    //                    case CONNECTION_LOST:
    //                        System.out.println("Connection error,waiting...");
    //                        break;
    //                    default:
    //                        System.out.println("Data:" + event.getData().toString());
    //                }
    //            }
    //        });
    //
    //        return cached;
    //    }

    public Listenable<CuratorListener> getCuratorListenable()
    {
        return curator.getCuratorListenable();
    }

    public GetACLBuilder getACL()
    {
        return curator.getACL();
    }

    public Listenable<UnhandledErrorListener> getUnhandledErrorListenable()
    {
        return curator.getUnhandledErrorListenable();
    }

    public void clearWatcherReferences(Watcher watcher)
    {
        curator.clearWatcherReferences(watcher);
    }

    public boolean blockUntilConnected(int i, TimeUnit timeUnit) throws InterruptedException
    {
        return curator.blockUntilConnected(i, timeUnit);
    }

    public String getNamespace()
    {
        return curator.getNamespace();
    }

    public GetDataBuilder getData()
    {
        return curator.getData();
    }

    public ExistsBuilder checkExists()
    {
        return curator.checkExists();
    }

    public SetDataBuilder setData()
    {
        return curator.setData();
    }

    public SyncBuilder sync()
    {
        return curator.sync();
    }

    public DeleteBuilder delete()
    {
        return curator.delete();
    }

    public void sync(String s, Object o)
    {
        curator.sync(s, o);
    }

    public SetACLBuilder setACL()
    {
        return curator.setACL();
    }

    /**
     * Start the client. Most mutator methods will not work until the client is
     * started
     */
    public void start()
    {
        curator.start();
    }

    /**
     * Stop the client
     */
    public void close()
    {
        String namesapce = getNamespace();
        if (Strings.isBlank(namesapce)) {
            namesapce = "";
        }
        if (instances.containsKey(namesapce)) {
            instances.remove(namesapce);
        }

        curator.close();
    }

    public void blockUntilConnected() throws InterruptedException
    {
        curator.blockUntilConnected();
    }

    public CuratorTransaction inTransaction()
    {
        return curator.inTransaction();
    }

    /**
     * Create all nodes in the specified path as containers if they don't
     * already exist
     *
     * @param path
     *         path to create
     * @throws Exception
     *         errors
     */
    public void createContainers(String path) throws Exception
    {
        curator.createContainers(path);
    }

    public CuratorFramework usingNamespace(String s)
    {
        return curator.usingNamespace(s);
    }


    public CuratorFrameworkState getState()
    {
        return curator.getState();
    }

    public CuratorZookeeperClient getZookeeperClient()
    {
        return curator.getZookeeperClient();
    }

    public CreateBuilder create()
    {
        return curator.create();
    }

    public Listenable<ConnectionStateListener> getConnectionStateListenable()
    {
        return curator.getConnectionStateListenable();
    }

    public GetChildrenBuilder getChildren()
    {
        return curator.getChildren();
    }
}
