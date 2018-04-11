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

import static javax.swing.JOptionPane.ERROR_MESSAGE;

import asu.tool.tool.GUITools;
import asu.tool.util.ZKClient;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import org.apache.zookeeper.data.Stat;
import org.nutz.lang.Strings;

public class ZKDataCopyTabbed {
  private JPanel contentPanel;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JTextField srcZKStr;
  private JTextField destZKStr;
  private JTextField syncZKPath;
  private JTextField syncToZKPath;
  private ZKDataCopyBean data = new ZKDataCopyBean();
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSS");
  private Thread executeThread;

  public ZKDataCopyTabbed() {

    buttonOK.addActionListener(e -> onOK());

    buttonCancel.addActionListener(e -> onCancel());


    // call onCancel() on ESCAPE
    contentPanel.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    GUITools.attachKeyListener(srcZKStr, destZKStr, syncZKPath, syncToZKPath);

    // init
    data.setSrcZKStr("192.168.35.61:2181,192.168.35.62:2181,192.168.35.63:2181");
    data.setDestZKStr("127.0.0.1:2181");
    data.setSyncZKPath("/freeib/global_configs");

    setData(data);
  }


  private void onOK() {
    buttonOK.setEnabled(false);
    // add your code here
    if (executeThread != null) {
      executeThread.stop();
    }
    executeThread = new Thread(() -> {
      try {
        execute();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", ERROR_MESSAGE);
      }
      buttonOK.setEnabled(true);
    });
    executeThread.start();
  }

  private void onCancel() {
    // add your code here if necessary
    if (executeThread != null) {
      executeThread.stop();
    }
    buttonOK.setEnabled(true);
  }


  public void execute() throws Exception {
    ConsolePane.getInstance().clear();
    getData(data);
    //旧zk配置
    ZKClient oldZK = ZKClient.createNoCache("", data.getSrcZKStr(), -1, null);
    ZKClient newZK = ZKClient.createNoCache("", data.getDestZKStr(), -1, null);
    //迁移的节点
    String node = data.getSyncZKPath();
    List<String> children = oldZK.getChildren().forPath(node);
    copy(oldZK, newZK, children, node);
    oldZK.close();
    newZK.close();
  }

  private void copy(ZKClient oldZK, ZKClient newZK, List<String> children, String parent)
      throws Exception {
    if (children == null || children.isEmpty()) {
      return;
    } else {
      for (String child : children) {
        String c = parent + "/" + child;
        byte[] bytes = oldZK.getData().forPath(c);
        String newPath = c;
        if (Strings.isNotBlank(data.getSyncToZKPath())) {
          newPath = c.replace(data.getSrcZKStr(), data.getSyncToZKPath());
        }
        Stat stat = newZK.checkExists().forPath(newPath);
        if (stat != null) {
          newZK.setData().forPath(newPath, bytes);
        } else {
          newZK.create().creatingParentsIfNeeded().forPath(newPath, bytes);
        }
        appendOutput("copy " + c + " to " + newPath);
        List<String> myChildren = oldZK.getChildren().forPath(c);
        copy(oldZK, newZK, myChildren, c);
      }
    }
  }

  private void appendOutput(String str) {
    StringBuilder buf = new StringBuilder();
    buf.append(sdf.format(new Date()));
    buf.append(" ");
    buf.append(str);
    buf.append("\n");
    System.out.println(buf.toString());

  }

  public JPanel getContentPanel() {
    return contentPanel;
  }

  public void setData(ZKDataCopyBean data) {
    srcZKStr.setText(data.getSrcZKStr());
    destZKStr.setText(data.getDestZKStr());
    syncZKPath.setText(data.getSyncZKPath());
    syncToZKPath.setText(data.getSyncToZKPath());
  }

  public void getData(ZKDataCopyBean data) {
    data.setSrcZKStr(srcZKStr.getText());
    data.setDestZKStr(destZKStr.getText());
    data.setSyncZKPath(syncZKPath.getText());
    data.setSyncToZKPath(syncToZKPath.getText());
  }

  public boolean isModified(ZKDataCopyBean data) {
    if (srcZKStr.getText() != null ? !srcZKStr.getText().equals(data.getSrcZKStr()) : data.getSrcZKStr() != null)
      return true;
    if (destZKStr.getText() != null ? !destZKStr.getText().equals(data.getDestZKStr()) : data.getDestZKStr() != null)
      return true;
    if (syncZKPath.getText() != null ? !syncZKPath.getText().equals(data.getSyncZKPath()) : data.getSyncZKPath() != null)
      return true;
    if (syncToZKPath.getText() != null ? !syncToZKPath.getText().equals(data.getSyncToZKPath()) : data.getSyncToZKPath() != null)
      return true;
    return false;
  }

}
