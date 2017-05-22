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

package asu.tool.gui;

import asu.tool.tools.GUITools;
import asu.tool.util.Bytes;
import asu.tool.util.Hex;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.swing.*;
import org.nutz.http.Header;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

public class HttpWin extends JFrame {
  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JTextField url;
  private JRadioButton getRBtn;
  private JRadioButton postRBtn;
  private JTextArea header;
  private JTextField mimeType;
  private JTextArea body;
  private JTextArea result;
  private JTextField encoding;
  private JCheckBox showInHexCK;

  private HttpBean bean = new HttpBean();
  private Thread executeThread;

  public HttpWin() {
    setContentPane(contentPane);
//        setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    buttonOK.addActionListener(e -> onOK());

    buttonCancel.addActionListener(e -> onCancel());

    // call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });

    // call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onCancel();
      }
    }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    bean.setUrl("http://www.baidu.com");
    bean.setGetMethod(true);
    setData(bean);
    setTitle("Http Tool");
    GUITools.attachKeyListener(url, header, mimeType, body, result, encoding);
  }

  private void onOK() {
    // add your code here
//        dispose();
    getData(bean);
    if (executeThread != null) {
      executeThread.interrupt();
    }
    SwingUtilities.invokeLater(() -> {
      buttonOK.setEnabled(false);
      result.setText("");
    });

    executeThread = new Thread(new ExecuteTask());
    executeThread.start();

  }

  private void fireTaskFinished(String resultData) {
    SwingUtilities.invokeLater(() -> {
      result.setText(resultData);
      buttonOK.setEnabled(true);
    });

  }

  private void onCancel() {
    // add your code here if necessary
    dispose();
  }

  public static void main(String[] args) {
    HttpWin dialog = new HttpWin();
    dialog.pack();
    dialog.setVisible(true);
    GUITools.center(dialog);
//        System.exit(0);
  }

  public void setData(HttpBean data) {
    url.setText(data.getUrl());
    encoding.setText(data.getEncoding());
    mimeType.setText(data.getMimeType());
    showInHexCK.setSelected(data.isShowInHex());
    header.setText(data.getHeader());
    body.setText(data.getBody());
    if (data.isGetMethod()) {
      getRBtn.setSelected(true);
      postRBtn.setSelected(false);
    } else {
      getRBtn.setSelected(false);
      postRBtn.setSelected(true);
    }
  }

  public void getData(HttpBean data) {
    data.setUrl(url.getText());
    data.setEncoding(encoding.getText());
    data.setMimeType(mimeType.getText());
    data.setShowInHex(showInHexCK.isSelected());
    data.setHeader(header.getText());
    data.setBody(body.getText());
    data.setGetMethod(getRBtn.isSelected());
  }

  public boolean isModified(HttpBean data) {
    if (url.getText() != null ? !url.getText().equals(data.getUrl()) : data.getUrl() != null)
      return true;
    if (encoding.getText() != null ? !encoding.getText().equals(data.getEncoding()) : data.getEncoding() != null)
      return true;
    if (mimeType.getText() != null ? !mimeType.getText().equals(data.getMimeType()) : data.getMimeType() != null)
      return true;
    if (showInHexCK.isSelected() != data.isShowInHex()) return true;
    if (header.getText() != null ? !header.getText().equals(data.getHeader()) : data.getHeader() != null)
      return true;
    if (body.getText() != null ? !body.getText().equals(data.getBody()) : data.getBody() != null)
      return true;
    if (getRBtn.isSelected() != data.isGetMethod()) return true;
    return false;
  }

  public class ExecuteTask implements Runnable {
    public void run() {
      String resultData = "";
      try {
        Response resp = null;
        if (bean.isGetMethod()) {
          resp = doGet();
        } else {
          resp = doPost();
        }

        String encoding = resp.getEncodeType();
        if (Strings.isBlank(encoding)) {
          encoding = bean.getEncoding();
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
          if (bean.isShowInHex()) {
            InputStream stream = resp.getStream();
            // Transfer-Encoding:chunked;
            boolean isChunked = "chunked".equals(resp.getHeader().get("Transfer-Encoding"));
            int length = resp.getHeader().getInt("Content-Length", -1);
            byte[] bytes = readBytes(length, isChunked, stream);
            Streams.safeClose(stream);
            builder.append(Hex.encodeHexString(bytes)).append('\n');
          } else {
            boolean isChunked = "chunked".equals(resp.getHeader().get("Transfer-Encoding"));
            int length = resp.getHeader().getInt("Content-Length", -1);
            InputStream stream = resp.getStream();
            byte[] bytes = readBytes(length, isChunked, stream);
            Streams.safeClose(stream);
            if (Strings.isNotBlank(encoding)) {
              builder.append(Bytes.toString(bytes, Charset.forName(encoding)))
                  .append('\n');
            } else {
              builder.append(Bytes.toString(bytes)).append('\n');
            }
          }
          resultData = builder.toString();
        } else {
          resultData = resp.getDetail() + " " + resp.getStatus();
        }
      } finally {
        fireTaskFinished(resultData);
      }
    }

    public byte[] readBytes(int length, boolean isChunked, InputStream in) {
      byte[] buff = new byte[4096];
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      try {
        int len = in.read(buff);
        while (len != -1) {
//                    System.out.println("len = " + len);
          out.write(buff, 0, Math.min(len, buff.length));
          if (!isChunked) {
            if (out.size() == length) {
              break;
            }
          }
          len = in.read(buff);
        }
      } catch (IOException e) {
        e.printStackTrace();
        throw Lang.wrapThrow(e);
      }

      return out.toByteArray();
    }


    public Response doGet() {
      String url = bean.getUrl();
      String mimeType = bean.getMimeType();
      String head = bean.getHeader();
      String body = bean.getBody();
      String enc = bean.getEncoding();
      int timeout = 10000;

      Header header = Header.create();
      if (Strings.isNotBlank(mimeType)) {
        header.set("ContentType", mimeType);
      }

      if (Strings.isNotBlank(head)) {
        String[] split = head.split("\r\n|\r|\n");
        for (String line : split) {
          if (Strings.isBlank(line.trim())) {
            continue;
          }
          String[] split1 = line.split(":");
          if (split1.length == 2) {
            header.set(Strings.sBlank(split1[0]), Strings.sBlank(split1[1]));
          }
        }
      }
      Request req = Request.create(url, Request.METHOD.GET).setHeader(header);

      if (body != null) {
        req.setData(body);
      }
      if (Strings.isNotBlank(enc)) {
        req.setEnc(enc);
      }
      return Sender.create(req).setTimeout(timeout).send();
    }

    public Response doPost() {
      String url = bean.getUrl();
      String mimeType = bean.getMimeType();
      String head = bean.getHeader();
      String body = bean.getBody();
      String enc = bean.getEncoding();
      int timeout = 10000;
      Header header = Header.create();
      if (Strings.isNotBlank(mimeType)) {
        header.set("ContentType", mimeType);
      }

      if (Strings.isNotBlank(head)) {
        String[] split = head.split("\r\n|\r|\n");
        for (String line : split) {
          if (Strings.isBlank(line.trim())) {
            continue;
          }
          String[] split1 = line.split(":");
          if (split1.length == 2) {
            header.set(Strings.sBlank(split1[0]), Strings.sBlank(split1[1]));
          }
        }
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
