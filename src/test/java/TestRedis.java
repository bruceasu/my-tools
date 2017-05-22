import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

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
public class TestRedis {
  public static void main(String[] args) throws IOException {
    Set<HostAndPort> set = new HashSet<>();
    set.add(new HostAndPort("192.168.35.233", 7000));
//        set.add(new HostAndPort("192.168.35.233",7001));
//        set.add(new HostAndPort("192.168.35.233",7002));
//        set.add(new HostAndPort("192.168.35.233",7003));
//        set.add(new HostAndPort("192.168.35.233",7004));
//        set.add(new HostAndPort("192.168.35.233",7005));
//        set.add(new HostAndPort("127.0.0.1",6379));
    JedisCluster jc = new JedisCluster(set);
//        jc.set("test::test-counter", );
    long start = System.currentTimeMillis();
    for (int i = 0; i < 10; i++) {
      Long incr = jc.incr("test::test-counter");
      System.out.println("incr = " + incr);
    }
    long end = System.currentTimeMillis();

    System.out.println("Pipelined@Pool SET: " + ((end - start) / 1000.0) + " seconds");
    jc.close();
  }
}
