package asu.tool.gui;

import asu.tool.tools.GUITools;
import asu.tool.util.Bytes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.undo.UndoManager;
import org.nutz.http.Header;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

/**
 * ${document}
 * <p>2017 Victor All rights reserved.</p>
 *
 * @author <a href="mailto:victor.su@gwtsz.net">Victor Su&lt;victor.su@gwtsz.net&gt;</a>
 * @version 1.0.0
 * @since 2017/3/3 9:44
 */
public class MockHttpServerTabbed {
  private JTextField mockHttpServerPath;
  private JTextField mockAction;
  private JPanel contentPanel;
  private JComboBox type;
  private JTextField respDelay;
  private JCheckBox replaceOrAppend;
  private JTextArea data;
  private JButton btnSend;

  private UndoManager um = new UndoManager();//撤销管理类
  private JTabbedPane tabbedPane;
  private MockHttpServerDataBean bean = new MockHttpServerDataBean();
  private Thread executeThread;

  public MockHttpServerTabbed() {
    btnSend.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       *
       * @param e ActionEvent
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        onOK();
      }
    });

    GUITools.attachKeyListener(um, mockHttpServerPath, mockAction, respDelay, data);
  }


  public JPanel getContentPanel() {
    return contentPanel;
  }

  public JTabbedPane getTabbedPane() {
    return tabbedPane;
  }

  public void setTabbedPane(JTabbedPane tabbedPane) {
    this.tabbedPane = tabbedPane;
  }

  public void setData(MockHttpServerDataBean bean) {
    mockHttpServerPath.setText(bean.getPath());
    mockAction.setText(bean.getMockAction());
    data.setText(bean.getData());
    respDelay.setText(bean.getRespDelay());
    replaceOrAppend.setSelected(bean.isReplace());
    type.setSelectedItem(bean.getType());
  }

  public void getData(MockHttpServerDataBean bean) {
    bean.setPath(mockHttpServerPath.getText());
    bean.setMockAction(mockAction.getText());
    bean.setData(data.getText());
    bean.setRespDelay(respDelay.getText());
    bean.setReplace(replaceOrAppend.isSelected());
    String selectedItem = (String)type.getSelectedItem();
    bean.setType(selectedItem);
  }

  public boolean isModified(MockHttpServerDataBean bean) {
    if (mockHttpServerPath.getText() != null ? !mockHttpServerPath.getText().equals(bean.getPath()) : bean.getPath() != null)
      return true;
    if (mockAction.getText() != null ? !mockAction.getText().equals(bean.getMockAction()) : bean.getMockAction() != null)
      return true;
    if (data.getText() != null ? !data.getText().equals(bean.getData()) : bean.getData() != null)
      return true;
    if (respDelay.getText() != null ? !respDelay.getText().equals(bean.getRespDelay()) : bean.getRespDelay() != null)
      return true;
    if (replaceOrAppend.isSelected() != bean.isReplace()) return true;
    return false;
  }

  private void onOK() {
    getData(bean);
    if (executeThread != null) {
      executeThread.interrupt();
    }
    SwingUtilities.invokeLater(() -> {
      btnSend.setEnabled(false);
    });
    executeThread = new Thread(new ExecuteTask());
    executeThread.start();
  }

  private void fireTaskFinished(String resultData) {
    SwingUtilities.invokeLater(() -> {
      System.out.println(resultData);
      btnSend.setEnabled(true);
    });
  }


  public class ExecuteTask implements Runnable {
    public void run() {
      String resultData = "";
      try {
        Response resp = doPost();

        String encoding = resp.getEncodeType();
        if (Strings.isBlank(encoding)) {
          encoding = "utf-8";
        }

        if (resp.isOK()) {
          StringBuilder builder = new StringBuilder();
          Header header = resp.getHeader();
          builder.append(header.get(null)).append('\n');
          for (String k : header.keys()) {
            if (k == null) continue;

            builder.append(k).append(':').append(header.get(k)).append('\n');
          }
          builder.append('\n');
          boolean isChunked = "chunked".equals(resp.getHeader().get("Transfer-Encoding"));
          int length = resp.getHeader().getInt("Content-Length", -1);
          InputStream stream = resp.getStream();
          byte[] bytes = Bytes.readBytes(length, isChunked, stream);
          Streams.safeClose(stream);
          if (Strings.isNotBlank(encoding)) {
            builder.append(Bytes.toString(bytes, Charset.forName(encoding)))
                .append('\n');
          } else {
            builder.append(Bytes.toString(bytes)).append('\n');
          }
          resultData = builder.toString();
        } else {
          resultData = resp.getDetail() + " " + resp.getStatus();
        }
      } finally {
        fireTaskFinished(resultData);
      }
    }

    public Response doPost() {
      String url = bean.getPath();
      String mimeType = "application/x-json";
      Map map = new HashMap();
      map.put("mockAction", bean.getMockAction());
      map.put("mode", bean.isReplace() ? "replace" : "append");
      map.put("type", bean.getType());
      map.put("respDelay", bean.getRespDelay());
      map.put("data", bean.getData());
      String body = "action.mock=" + Json.toJson(map, JsonFormat.compact());
      System.out.println("sending body: " + body);
      String enc = "utf-8";
      int timeout = 3000;
      Header header = Header.create();
      if (Strings.isNotBlank(mimeType)) {
        header.set("ContentType", mimeType);
      }

      Request req = Request.create(url, Request.METHOD.POST).setHeader(header);

      if (body != null) {
        req.setData(body);
      }
      if (Strings.isNotBlank(enc)) {
        req.setEnc(enc);
      }
      return Sender.create(req).setTimeout(timeout).send();
    }

  }
}
