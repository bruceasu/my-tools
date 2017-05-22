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

package asu.tool.test.testcase;

import asu.tool.gui.GUI;
import asu.tool.gui.GUIDataBean;
import asu.tool.util.retry.RetryLoop;
import asu.tool.util.retry.RetryUntilElapsed;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import redis.clients.jedis.Jedis;

public class RedisTest {
  public static final String testCaseId = "redis";

  private final GUI gui;
  private final GUIDataBean config;
  Jedis jredis;
  String key = "test::test.counter";
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSS");

  public RedisTest(GUIDataBean config, GUI gui) {
    System.out.println(">>>>>>>>>> redis test <<<<<<<<<<<<<");
    System.err.println(">>>>>>>>>> redis test <<<<<<<<<<<<<");
    this.gui = gui;
    this.config = config;
    // 6379
    jredis = new Jedis(config.getRedisHost(), config.getRedisPortInt());
//        System.out.println(clientGatewayHost + ":" + clientGatewayPort);
//        jredis.ping();//
//权限认证
//        jedis.auth("admin");
    jredis.connect();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        if (jredis != null)
          jredis.quit();
      }
    });
  }

  public void test() throws Exception {
    int times = config.getRedisTestTimeInt();
    if (times < 1) times = 20;
    appendOutputText("测试 redis counter: " + key);
    appendOutputText("预热：");
    for (int i = 0; i < 10; i++) {
      long t1 = System.nanoTime();
      Long data = RetryLoop.callWithRetry(
          new Callable<Long>() {
            public Long call() throws Exception {
              Long test = jredis.incr(key);
              return test;
            }

          },
          new RetryUntilElapsed(10000, 100),
          Exception.class);

      long t2 = System.nanoTime();
      appendOutputText("Got: " + data);
      appendOutputText("花费：" + (t2 - t1) + " ns, " + (t2 - t1) / 1000000 + " ms.");
    }
    appendOutputText("测试" + times + "次：");
    for (int i = 0; i < times; i++) {
      long t1 = System.nanoTime();
      Long data = RetryLoop.callWithRetry(
          new Callable<Long>() {
            public Long call() throws Exception {
              Long test = jredis.incr(key);
              return test;
            }

          },
          new RetryUntilElapsed(10000, 100),
          Exception.class);
      long t2 = System.nanoTime();
      appendOutputText("Got: " + data);
      appendOutputText("花费：" + (t2 - t1) + " ns, " + (t2 - t1) / 1000000 + " ms.");
    }

    appendOutputText("测试完成!");
  }

  private void appendOutputText(String txt) {
    StringBuilder buf = new StringBuilder();
    buf.append(sdf.format(new Date())).append('\t').append(txt).append('\n');
    gui.appendResult(testCaseId, buf.toString());
  }

  public void close() {
    if (jredis != null) {
      jredis.close();
      jredis = null;
    }
  }

}
