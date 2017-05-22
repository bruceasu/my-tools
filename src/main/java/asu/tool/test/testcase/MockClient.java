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

import asu.tool.gui.AppClientMockerBean;
import asu.tool.gui.AppClientMockerTabbed;
import asu.tool.util.Bytes;
import asu.tool.util.ED;
import asu.tool.util.MD5;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.nutz.lang.Xmls;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.w3c.dom.Document;

public class MockClient {
  private final Log logger = Logs.get();
  private CountDownLatch loginCD;
  private final AppClientMockerBean config;
  private final AppClientMockerTabbed gui;
  public static String testCaseId = "client-gateway";
  public EventLoopGroup group;
  public Bootstrap bootstrap;
  public Channel channel;
  private String host;
  private int port;
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSS");
  volatile boolean login = false;
  private boolean connected = false;


  public MockClient(AppClientMockerBean config, AppClientMockerTabbed gui) {
    this.loginCD = new CountDownLatch(1);
    this.config = config;
    this.gui = gui;
    this.host = config.getClientGatewayHost();
    this.port = config.getClientGatewayPortInt();


    group = new NioEventLoopGroup();
    bootstrap = getBootstrap();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));
  }


  public Channel connect() throws InterruptedException {
    channel = createChannel(config.getClientGatewayHost(), config.getClientGatewayPortInt());
    appendOutputText("waiting for login...");
    loginCD.await();
    if (!login) {
      appendOutputText("login fail");
    }
    return channel;
  }

  /**
   * 初始化Bootstrap
   *
   * @return Bootstrap
   */
  public final Bootstrap getBootstrap() {
    Bootstrap b = new Bootstrap();
    b.group(group).channel(NioSocketChannel.class);
    b.handler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS));
        pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(64 * 1024, 0, 4, 0, 4));
        pipeline.addLast("encoder", new LengthFieldPrepender(4));
        pipeline.addLast("handler", new TcpClientHandler());
      }
    });
    b.option(ChannelOption.SO_KEEPALIVE, true);
    b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
    b.option(ChannelOption.SO_TIMEOUT, 10000);
    return b;
  }

  private final Channel createChannel(String host, int port) {
    Channel channel = null;
    try {
      channel = bootstrap.connect(host, port).sync().channel();
    } catch (Exception e) {
      appendOutputText(String.format("连接Server(IP[%s],PORT[%s])失败", host, port) + e.getMessage());
      loginCD.countDown();
      gui.fireCGWLoginSuc(2);
      return null;
    }
    return channel;
  }

  public void closeChannel() throws InterruptedException {
    if (channel != null && channel.isOpen()) {
      channel.close().sync().awaitUninterruptibly();
    }
    channel = null;
    connected = false;
    login = false;
    loginCD = new CountDownLatch(1);
    appendOutputText("disconnected!");
    gui.fireCGWDisconnected();
  }

  public void shutdown() {
    try {
      closeChannel();
      if (group != null) {
        group.shutdownGracefully().sync();
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
    group = null;
  }


  public class TcpClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg)
        throws Exception {
      // messageReceived方法,名称很别扭，像是一个内部方法.
      if (!login) {
        // 第一个信息包应该是登录后命令后的响应，明文
        int i = msg.readableBytes();
        byte[] bytes = new byte[i];
        msg.readBytes(bytes);
        appendOutputText("login return: " + Bytes.toString(bytes));
        Document xml = Xmls.xml(new ByteArrayInputStream(bytes));
        String status = Xmls.get(xml.getDocumentElement(), "status");
        if ("0".equals(status)) {
          login = true;
          appendOutputText("login success." + login);
          loginCD.countDown();
          gui.fireCGWLoginSuc(0);
        } else {
          // login fail
          appendOutputText("login fail.");
          loginCD.countDown();
          gui.fireCGWLoginSuc(1);
        }
      } else {
        int i = msg.readableBytes();
        byte[] bytes = new byte[i];
        msg.readBytes(bytes);
        ED ed = new ED();
        ed.setCompressable(config.isClientGatewayMsgCompressable());
        ed.setEncryptable(config.isClientGatewayMsgEncryptable());
        ed.setKey(config.getApiAccountKey());
        byte[] decrypt = ed.decrypt(bytes);
        Document xml = Xmls.xml(new ByteArrayInputStream(decrypt));
        appendOutputText("received: " + outputXml(xml));
//                String status = Xmls.get(xml.getDocumentElement(), "status");

//                // 不是每个response都有status字段
//                if (!"0".equals(status)) {
//                    new Thread(){
//                        public void run() {
//                            try {
//                                MockClient.this.closeChannel();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }.start();
//                }
      }

            /* response of login
             <!--  成功 -->
            <message type="response" msgnum="5486">
                <status>0</status>
                <information>123456</information>
                <alert_change_pwd>false</alert_change_pwd>
                <force_change_pwd>false</force_change_pwd>
                <pwd_expiry_date>2009-09-23</pwd_expiry_date>
                <last_login_time>2009-02-01 15:00:34</last_login_time>
                <recovery_order_count>0</recovery_order_count>
            </message>

            <!--  失败 -->
            <message type=”response” msgnum=”5865”>
                <status>1</status>
                <error>Client Cannot Be Found</error>
                <errorCode>Error.NotFound </errorCode>
            </message>
             */

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      appendOutputText("channelInActive");
      login = false;
      connected = false;
      ctx.fireChannelInactive();
      gui.fireCGWDisconnected();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
        throws Exception {
      ctx.fireExceptionCaught(cause);
      closeChannel();
//            System.exit(2);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      ctx.fireChannelActive();
      connected = true;
      // send login
      ED ed = new ED();
      ed.setCompressable(config.isClientGatewayMsgCompressable());
      ed.setEncryptable(config.isClientGatewayMsgEncryptable());
      ed.setKey(config.getApiAccountKey());
      MakeLoginPackage makeLoginPackage = new MakeLoginPackage(ed).invoke();
      String sendMsg = makeLoginPackage.getSendMsg();

      appendOutputText("send login: " + sendMsg);

      ctx.channel().writeAndFlush(
          Unpooled.wrappedBuffer(Bytes.toBytes(sendMsg))).addListener(
          (ChannelFutureListener) future -> {
            if (future.isSuccess()) {
              appendOutputText("[x] send login command success.");
            } else {
              appendOutputText("[x] send login command failure, closeChannel.");
              // future.channel().closeChannel();
              closeChannel();
              appendOutputText(future.cause().getMessage());
              loginCD.countDown();
            }

          });
    }


    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
      if (evt instanceof IdleStateEvent) {
        // send heartbeat
        String xml = "<message type=\"order_action:keep_alive\" msgnum=\"5485\" />";
        ED ed = new ED();
        ed.setCompressable(config.isClientGatewayMsgCompressable());
        ed.setEncryptable(config.isClientGatewayMsgEncryptable());
        ed.setKey(config.getApiAccountKey());
        byte[] encrypt = ed.encrypt(Bytes.toBytes(xml));
//                System.out.println(Hex.encodeHexString(encrypt));
        ctx.writeAndFlush(Unpooled.wrappedBuffer(encrypt))
            .addListener((ChannelFutureListener) future -> {
              if (!future.isSuccess()) {
                appendOutputText("[x] send heart beat fail, closeChannel. ");
                closeChannel();
              } else {
                appendOutputText("[x] send heart beat success.");
              }
            });
        appendOutputText("[x] send heart beat.");
//                IdleStateEvent e = (IdleStateEvent) evt;
//                if (e.state() == IdleState.READER_IDLE) {
//                    ctx.closeChannel();
//                } else if (e.state() == IdleState.WRITER_IDLE) {
//
//                }
      } else {
        super.userEventTriggered(ctx, evt);
      }
    }
  }


  private class MakeLoginPackage {
    private final ED ed;
    private String sendMsg;

    public MakeLoginPackage(ED ed) {
      this.ed = ed;
    }

    public String getSendMsg() {
      return sendMsg;
    }

    public MakeLoginPackage invoke() throws Exception {
      String xml = "<message type=\"login\" msgnum=\"5486\">" +
          "            <site>1</site>" +
          "            <station>1</station>" +
          "            <user>1</user>" +
          "            <password>%s</password>" +
          "            <order_recovery>Y</order_recovery>" +
          "            <last_updated_time>2016-07-29 00:00:00</last_updated_time>" +
          "            <disable_notification>N</disable_notification>" +
          "            <timestamp>%s</timestamp>" +
          "            <conn_type>BRIDGE</conn_type>" +
          "        </message>";

      //
      long timestamp = System.currentTimeMillis() / 1000;

      String msg = String.join("", "1", "1", "1", "Y",
          "2016-07-29 00:00:00", "N", String.valueOf(timestamp),
          "BRIDGE");

      byte[] encrypt = ed.encrypt(Bytes.toBytes(msg));
      String newPassword = MD5.encodeByMD5(encrypt);

      sendMsg = String.format(xml, newPassword, String.valueOf(timestamp));

      return this;
    }
  }

  /**
   * 将XML文件输出到指定的路径
   *
   * @param doc Document Object
   * @return xml text
   * @throws Exception 异常
   */
  private static String outputXml(Document doc) throws Exception {
        /*
        Document doc = Xmls.xmls().newDocument();
            Element message = doc.createElement("message");
            doc.appendChild(message);
            Element status = doc.createElement("status");
            Element error = doc.createElement("error");
            Element errorCode = doc.createElement("errorCode");

            message.setAttribute("type", "response");
            message.setAttribute("msgnum", msgnum);
            message.appendChild(status);
            message.appendChild(error);
            message.appendChild(errorCode);

            Text statusTextNode = doc.createTextNode("1");
            status.appendChild(statusTextNode);

            Text errorTextNode = doc.createTextNode("Connection Type is Not Support");
            error.appendChild(errorTextNode);

            Text errorCodeTextNode = doc.createTextNode("Error.ConnectionTypeNotSupport");
            errorCode.appendChild(errorCodeTextNode);
            String xml = outputXml(doc);
         */
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    DOMSource source = new DOMSource(doc);
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");//设置文档的换行与缩进
    StringWriter sw = new StringWriter();
    StreamResult result = new StreamResult(sw);
    transformer.transform(source, result);

    return sw.toString();
  }

  public void sendMessage(byte[] data) {
    if (channel == null) {
      channel = createChannel(config.getClientGatewayHost(), config.getClientGatewayPortInt());
    }
    channel.writeAndFlush(Unpooled.wrappedBuffer(data));
  }

  public void send(String xml) throws Exception {
    {
      // Intellij idea 和 eclipse 的 运行 workdir 是不样的，
      // ProjectPath.parentProjectPath 只适合用于eclipse IDE
      // String basePath = ProjectPath.parentProjectPath + "\\freeib-gateway\\trunk";
//            String basePath = "D:\\IdeaProjects\\freeIB\\freeib-client-gateway";
//            String configs = basePath + "\\src\\main\\resources";
//            ExtClasspathLoader.addResourceDir(configs);
    }

    if (!login) {
      appendOutputText("not login");
      return;
    }

    try {
      ED ed = new ED();
      ed.setCompressable(config.isClientGatewayMsgCompressable());
      ed.setEncryptable(config.isClientGatewayMsgEncryptable());
      ed.setKey(config.getApiAccountKey());
      byte[] encrypt = ed.encrypt(xml.getBytes());
      appendOutputText(">>> sending message ...");
      sendMessage(encrypt);
    } catch (Exception e) {
      appendOutputText(e.getMessage());
    } finally {
    }
  }

  private void appendOutputText(String txt) {
    StringBuilder buf = new StringBuilder();
    buf.append(sdf.format(new Date())).append('\t').append(txt);
    gui.appendResult(buf.toString());
  }

}
