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
import asu.tool.util.Bytes;
import asu.tool.util.RabbitMQTool;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.nutz.json.Json;

public class MQTest {
  public static final String testCaseId = "mq";

  private final GUIDataBean config;
  private final GUI gui;
  ConnectionFactory factory;
  RabbitMQTool rabbitMQTool;
  String EXCHANGE_NAME = "test-exchange";
  String QUEUE_NAME = "test";
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSS");

  public MQTest(GUIDataBean config, GUI gui) throws Exception {
    this.config = config;
    this.gui = gui;
    factory = getConnectionFactory();
    rabbitMQTool = new RabbitMQTool(factory);
//        testConsumer();
  }

  public void close() {
    if (rabbitMQTool != null) {
      rabbitMQTool.close();
    }
  }

  private void appendOutputText(String txt) {
    StringBuilder buf = new StringBuilder();
    buf.append(sdf.format(new Date())).append('\t').append(txt).append('\n');
    gui.appendResult(testCaseId, buf.toString());
  }


  public void test() {
    int times = config.getMqTestTimesInt();
    if (times < 1) times = 20;

    String routingKey = "test.mqtest";
    appendOutputText("测试basicPublish EXCHANGE:" + EXCHANGE_NAME + ", QUEUE:" + QUEUE_NAME);
    appendOutputText("预热：");
    for (int i = 0; i < 10; i++) {
      long t1 = System.nanoTime();
      while (true) {
        Channel channel = null;
        String msg = "This is a test at " + System.currentTimeMillis();
        try {
          channel = createChannel();
          channel.confirmSelect();
          channel.basicPublish(
              EXCHANGE_NAME,
              routingKey,
              MessageProperties.PERSISTENT_TEXT_PLAIN,
              Bytes.toBytes(Json.toJson(msg)));
          // not ack, cause IOException
          channel.waitForConfirmsOrDie();
          break;
        } catch (IOException e) {
          System.err.println(e.getMessage());
          continue;
        } catch (InterruptedException e) {
          System.err.println(e.getMessage());
        } finally {
          rabbitMQTool.closeQuietly(channel);
        }
      }
      long t2 = System.nanoTime();
      appendOutputText("花费：" + (t2 - t1) + " ns," + (t2 - t1) / 1000000 + " ms.");
    }
    appendOutputText("测试" + times + "次：");
    for (int i = 0; i < times; i++) {
      long t1 = System.nanoTime();
      String msg = "This is a test at " + i + " times.";
      while (true) {
        Channel channel = null;
        try {
          channel = createChannel();
          channel.confirmSelect();
          channel.basicPublish(
              EXCHANGE_NAME,
              routingKey,
              MessageProperties.PERSISTENT_TEXT_PLAIN,
              Bytes.toBytes(Json.toJson(msg)));
          // not ack, cause IOException
          channel.waitForConfirmsOrDie();
          break;
        } catch (IOException e) {
          System.err.println(e.getMessage());
          continue;
        } catch (InterruptedException e) {
          System.err.println(e.getMessage());
        } finally {
          rabbitMQTool.closeQuietly(channel);
        }
      }
      long t2 = System.nanoTime();
      appendOutputText("花费：" + (t2 - t1) + " ns," + (t2 - t1) / 1000000 + " ms.");
    }
    appendOutputText("测试完成!");
  }

  public void testConsumer() throws IOException {
    Channel channel = createChannelForConsumer();
    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope,
                                 AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        appendOutputText("[x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
      }
    };
    channel.basicConsume(QUEUE_NAME, true, consumer);
  }

  private Channel createChannel() throws IOException {
    Channel channel = rabbitMQTool.connection().createChannel();
    channel.queueDeclare(QUEUE_NAME, true, false, false, null);//声明消息队列，且为可持久化的
    channel.exchangeDeclare(EXCHANGE_NAME, "topic");
    return channel;
  }

  private Channel createChannelForConsumer() throws IOException {
    Channel channel = rabbitMQTool.connection().createChannel();
    channel.queueDeclare(QUEUE_NAME, true, false, false, null);//声明消息队列，且为可持久化的
    channel.exchangeDeclare(EXCHANGE_NAME, "topic");
    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "test.*");

    return channel;
  }


  private ConnectionFactory getConnectionFactory() throws Exception {
    ConnectionFactory factory = RabbitMQTool.factory();
    factory = new ConnectionFactory();
    factory.setAutomaticRecoveryEnabled(true);
    factory.setHost(config.getMqHost());
    factory.setPort(config.getMqPortInt());
    factory.setVirtualHost(config.getMqVhost());
    factory.setUsername(config.getMqUsername());
    factory.setPassword(config.getMqPassword());
    return factory;
  }
}
