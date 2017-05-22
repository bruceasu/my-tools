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

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class ZKGlobalConfig implements AutoCloseable
{
    private static final Log log = Logs.get();
    private final ZKClient zkClient;
    private final String cfgPath;
    private TreeCache cached;
    CountDownLatch initialed = new CountDownLatch(1);

    public static TreeCache treeCache(CuratorFramework client, String path, TreeCacheListener listener) throws Exception
    {
        TreeCache cached = new TreeCache(client, path);
        cached.getListenable().addListener(listener);
        return cached.start();

    }


    public ZKGlobalConfig(ZKClient zkClient, String cfgPath) throws Exception
    {
        this.zkClient = zkClient;
        this.cfgPath = cfgPath;
        treeCache();
    }

    /**
     * Close/end the cache.
     */
    public void close() throws Exception
    {
        if (cached != null) {
            cached.close();
        }
    }

    /**
     * Return the current set of children at the given path, mapped by child name. There are no
     * guarantees of accuracy; this is merely the most recent view of the data.  If there is no
     * node at this path, {@code null} is returned.
     *
     * @param fullPath full path to the node to check
     * @return a possibly-empty list of children if the node is alive, or null
     */
    public Map<String, ChildData> getCurrentChildren(String fullPath)
    {
        return cached.getCurrentChildren(fullPath);
    }

    /**
     * Return the current data for the given path. There are no guarantees of accuracy. This is
     * merely the most recent view of the data. If there is no node at the given path,
     * {@code null} is returned.
     *
     * @param fullPath full path to the node to check
     * @return data if the node is alive, or null
     */
    public ChildData getCurrentData(String fullPath)
    {
        return cached.getCurrentData(fullPath);
    }

    private void treeCache() throws Exception
    {
        //

        TreeCacheListener listener = (client, event) -> {
            TreeCacheEvent.Type eventType = event.getType();
            switch (eventType) {
                case CONNECTION_RECONNECTED:
                    log.trace("reconnected");
                    break;
                case CONNECTION_SUSPENDED:
                case CONNECTION_LOST:
                    log.trace("Connection error,waiting...");
                    break;
                case INITIALIZED:
                    log.infof("TreeCache of %s INITIALIZED", cfgPath);
                    if (initialed.getCount() > 0) {
                        initialed.countDown();
                    }
                    break;
                case NODE_REMOVED:
                    log.trace("TreeNode removed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                    break;
                case NODE_UPDATED:
                    log.trace("TreeNode changed: " + ZKPaths.getNodeFromPath(event.getData().getPath()) + ", value: "
                            + new String(event.getData().getData()));
                    break;
                case NODE_ADDED:
                    log.trace("TreeNode added: " + ZKPaths.getNodeFromPath(event.getData().getPath()) + ", value: "
                            + new String(event.getData().getData()));
                    break;
                default:
                    log.trace("Data:" + event.getData().toString());
            }
        };

        cached = treeCache(zkClient.getCurator(), cfgPath, listener);
        initialed.await();
    }

    public void addTreeCacheListener(TreeCacheListener listener)
    {
        cached.getListenable().addListener(listener);
    }

    public void removeTreeCacheListener(TreeCacheListener listener)
    {
        cached.getListenable().removeListener(listener);
    }

    public static void main(String[] args) throws Exception
    {
        String cfgPath = "/global_configs";
        ZKGlobalConfig zkGlobalConfig = new ZKGlobalConfig(ZKClient.createNoCache("freeib",
                "127.0.0.1:2181", -1, null), cfgPath);
        ChildData global_configs = zkGlobalConfig.getCurrentData(cfgPath);
        System.out.println(global_configs);
        System.out.println(new String(global_configs.getData()));

        displayChildrenNodeData(zkGlobalConfig, cfgPath);
        System.in.read();
        ChildData ss = zkGlobalConfig.getCurrentData(cfgPath + "/t/ss");
        System.out.println(ss);
        System.in.read();
    }

    private static void displayChildrenNodeData(ZKGlobalConfig zkGlobalConfig, String path) {
        Map<String, ChildData> currentChildren = zkGlobalConfig.getCurrentChildren(path);
        if (currentChildren == null || currentChildren.isEmpty()) {
            return;
        }
        for (Map.Entry<String, ChildData> e : currentChildren.entrySet()) {
            System.out.println(Strings.dup('-', 76));
//            System.out.println("path:      " + e.getKey());
            System.out.println("full path: " + e.getValue().getPath());
            System.out.println("data:      " + new String(e.getValue().getData()));
            displayChildrenNodeData(zkGlobalConfig, e.getValue().getPath() );
        }

    }

}
