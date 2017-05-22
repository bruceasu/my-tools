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
import asu.tool.util.ZKClient;
import asu.tool.util.ZKCounter;
import asu.tool.util.retry.RetryLoop;
import asu.tool.util.retry.RetryUntilElapsed;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.zookeeper.KeeperException;

public class ZKTest {
  public static final String testCaseId = "zk";

  public final String zkConnStr;
  private final GUI gui;
  private final GUIDataBean config;
  private volatile ZKClient zkClient = null;
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSS");
  private ZKCounter zkCounter;
  private String ns = "test";
  String key = "/test-counter";

  public ZKTest(GUIDataBean config, GUI gui) {
    this.config = config;
    this.zkConnStr = config.getZkConnStr();
    this.gui = gui;
    try {
      this.zkClient = ZKClient.createNoCache(ns, zkConnStr, -1, null);
      zkCounter = new ZKCounter(zkClient);
//            zkClient.start();
//            System.out.println(zkClient);
    } catch (Exception e) {
      if (e instanceof KeeperException) {
        KeeperException ke = (KeeperException) e;
        KeeperException.Code code = ke.code();
        String path = ke.getPath();
        System.out.println("e.getClass() = " + e.getClass());
        System.out.println("code = " + code);
        System.out.println("path = " + path);
      } else
        e.printStackTrace();
    } finally {

    }

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        close();
      }
    });
  }


  public void close() {
    if (zkClient != null) {
      zkClient.close();
      zkClient = null;
    }
    if (zkCounter != null) {
      zkCounter.close();
    }
  }

  public void test() throws Exception {
    int times = config.getZkTestTimesInt();
    if (times < 1) times = 20;

    appendOutputText("测试 zookeeper counter:/test/test-counter");
    appendOutputText("预热：");

    final DistributedAtomicLong counter = zkCounter.getCounter(key);
    for (int i = 0; i < 10; i++) {
      long t1 = System.nanoTime();
//            AtomicValue<Long> increment = counter.increment();
      Long data = RetryLoop.callWithRetry(
          new Callable<Long>() {
            public Long call() throws Exception {
              AtomicValue<Long> increment = counter.increment();
              if (increment.succeeded()) {
                return increment.postValue();
              } else {
                throw new Exception("get counter value fail, counter: /test-counter");
              }
            }

          },
          new RetryUntilElapsed(10000, 100),
          Exception.class);
      long t2 = System.nanoTime();
      appendOutputText("花费：" + (t2 - t1) + " ns," + (t2 - t1) / 1000000 + " ms.");
    }
    appendOutputText("测试" + times + "次：");
    for (int i = 0; i < times; i++) {
      long t1 = System.nanoTime();
//            AtomicValue<Long> increment = counter.increment();
      Long data = RetryLoop.callWithRetry(
          () -> {
            AtomicValue<Long> increment = counter.increment();
            if (increment.succeeded()) {
              return increment.postValue();
            } else {
              throw new Exception("get counter value fail, counter: /test-counter");
            }
          },
          new RetryUntilElapsed(10000, 100),
          Exception.class);
      long t2 = System.nanoTime();
      appendOutputText("Get " + data + " 花费：" + (t2 - t1) + " ns," + (t2 - t1) / 1000000 + " ms.");
    }

    appendOutputText("测试完成!");
  }

  private void appendOutputText(String txt) {
    StringBuilder buf = new StringBuilder();
    buf.append(sdf.format(new Date())).append('\t').append(txt).append('\n');
    gui.appendResult(testCaseId, buf.toString());
  }

}
