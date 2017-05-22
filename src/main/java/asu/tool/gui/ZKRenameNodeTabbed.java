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

import asu.tool.tools.GUITools;
import asu.tool.util.ZKClient;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import javax.swing.*;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;
import org.nutz.lang.Strings;

public class ZKRenameNodeTabbed {
  private JTextField host;
  private JTextField oldPath;
  private JTextField newPath;
  private JButton cpBtn;
  private JButton mvBtn;
  private JPanel contentPanel;
  private ZKRenameNodeBean bean = new ZKRenameNodeBean();
  private ZKClient zkClient;

  public ZKRenameNodeTabbed() {
    cpBtn.addMouseListener(new MouseAdapter() {
      /**
       * {@inheritDoc}
       *
       * @param e MouseEvent
       */
      @Override
      public void mouseClicked(MouseEvent e) {
        cpBtn.setEnabled(false);
        try {
          if (checkArgumentState())
            return;
          doCopy();
          JOptionPane.showMessageDialog(null, "Copied!");
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, "paths should not be empty.", "ERROR", ERROR_MESSAGE);
          ex.printStackTrace();
        } finally {
          cpBtn.setEnabled(true);
        }
      }
    });

    mvBtn.addMouseListener(new MouseAdapter() {
      /**
       * {@inheritDoc}
       *
       * @param e MouseEvent
       */
      @Override
      public void mouseClicked(MouseEvent e) {
        mvBtn.setEnabled(false);


        try {
          if (checkArgumentState())
            return;
          doMove();
          JOptionPane.showMessageDialog(null, "Moved!");
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, "paths should not be empty.", "ERROR", ERROR_MESSAGE);
          ex.printStackTrace();
        } finally {
          mvBtn.setEnabled(true);
        }
      }
    });

    GUITools.attachKeyListener(host, oldPath, newPath);
    // init
    bean.setHost("127.0.0.1:2181");
    setData(bean);


  }


  private boolean checkArgumentState() throws Exception {
    if (host.getText() != null ? !host.getText().equals(bean.getHost()) : bean.getHost() != null) {
      getData(bean);
      if (zkClient != null) {
        zkClient.close();
        zkClient = null;
      }
    }
    if (zkClient == null) {
      try {
        zkClient = ZKClient.createNoCache("", bean.getHost(), -1, null);
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", ERROR_MESSAGE);
        ex.printStackTrace();
        return true;
      }
    }

    if (Strings.isBlank(bean.getNewPath()) || Strings.isBlank(bean.getOldPath())) {
      JOptionPane.showMessageDialog(null, "paths should not be empty.", "ERROR", ERROR_MESSAGE);
      return true;
    }
    Stat oldPathStat = zkClient.checkExists().forPath(bean.getOldPath());
    if (oldPathStat == null) {
      JOptionPane.showMessageDialog(null, bean.getOldPath() + " is not exists.", "ERROR", ERROR_MESSAGE);
      return true;
    }

    Stat newPathStat = zkClient.checkExists().forPath(bean.getNewPath());
    if (newPathStat != null) {
      JOptionPane.showMessageDialog(null, bean.getNewPath() + " is exists.", "ERROR", ERROR_MESSAGE);
      return true;
    }
    return false;
  }

  private void doCopy() throws Exception {
    TreeMap<String, byte[]> m = new TreeMap();
    traverseTree(m, bean.getOldPath());
    for (String key : m.keySet()) {
      String newKey = key.replace(bean.getOldPath(), bean.getNewPath());
      byte[] value = m.get(key);
      zkClient.create().creatingParentsIfNeeded().forPath(newKey, value);
    }

  }

  private void doMove() throws Exception {
    TreeMap<String, byte[]> m = new TreeMap();
    traverseTree(m, bean.getOldPath());
    for (String key : m.keySet()) {
      String newKey = key.replace(bean.getOldPath(), bean.getNewPath());
      byte[] value = m.get(key);
      zkClient.create().creatingParentsIfNeeded().forPath(newKey, value);
    }
    zkClient.deleteIfExists(bean.getOldPath());

  }

  public void traverseTree(TreeMap<String, byte[]> m, String path) throws Exception {
    if (Strings.isBlank(path))
      return;
    try {
      byte[] bytes = zkClient.getData().forPath(path);
      m.put(path, bytes);
      List<String> children = zkClient.getChildren().forPath(path);
      if (children != null && !children.isEmpty()) {
        for (String child : children) {
          traverseTree(m, ZKPaths.makePath(path, child));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public JPanel getContentPanel() {
    return contentPanel;
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("ZKRenameNodeTabbed");
    frame.setContentPane(new ZKRenameNodeTabbed().contentPanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  public void setData(ZKRenameNodeBean data) {
    host.setText(data.getHost());
    oldPath.setText(data.getOldPath());
    newPath.setText(data.getNewPath());
  }

  public void getData(ZKRenameNodeBean data) {
    data.setHost(host.getText());
    data.setOldPath(oldPath.getText());
    data.setNewPath(newPath.getText());
  }

  public boolean isModified(ZKRenameNodeBean data) {
    if (host.getText() != null ? !host.getText().equals(data.getHost()) : data.getHost() != null)
      return true;
    if (oldPath.getText() != null ? !oldPath.getText().equals(data.getOldPath()) : data.getOldPath() != null)
      return true;
    if (newPath.getText() != null ? !newPath.getText().equals(data.getNewPath()) : data.getNewPath() != null)
      return true;
    return false;
  }

}
