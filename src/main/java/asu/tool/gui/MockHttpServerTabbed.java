package asu.tool.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * ${document}
 * <p>2017 Victor All rights reserved.</p>
 *
 * @author <a href="mailto:victor.su@gwtsz.net">Victor Su&lt;victor.su@gwtsz.net&gt;</a>
 * @version 1.0.0
 * @since 2017/3/3 9:44
 */
public class MockHttpServerTabbed {
  private JTextField mockHttpServerPort;
  private JTextField mockHttpServerVerticleDir;
  private JButton btnBrowserDir;
  private JButton btnStartServer;
  private JButton btnStopServer;
  private JButton btnClose;
  private JPanel contentPanel;
  JTabbedPane tabbedPane;
  public MockHttpServerTabbed() {
    btnBrowserDir.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       *
       * @param e
       */
      @Override
      public void actionPerformed(ActionEvent e) {

      }
    });
    btnStartServer.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       *
       * @param e
       */
      @Override
      public void actionPerformed(ActionEvent e) {

      }
    });
    btnStopServer.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       *
       * @param e
       */
      @Override
      public void actionPerformed(ActionEvent e) {

      }
    });
    btnClose.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       *
       * @param e
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("going to close.");
        JTabbedPane tp = getTabbedPane();
        if (tp != null) {
          tp.remove(getContentPanel());
        }

      }
    });
  }

  public void setData(MockHttpServerBean data) {
    mockHttpServerPort.setText(data.getPort());
    mockHttpServerVerticleDir.setText(data.getVerticleDir());
  }

  public void getData(MockHttpServerBean data) {
    data.setPort(mockHttpServerPort.getText());
    data.setVerticleDir(mockHttpServerVerticleDir.getText());
  }

  public boolean isModified(MockHttpServerBean data) {
    if (mockHttpServerPort.getText() != null ? !mockHttpServerPort.getText().equals(data.getPort()) : data.getPort() != null)
      return true;
    if (mockHttpServerVerticleDir.getText() != null ? !mockHttpServerVerticleDir.getText().equals(data.getVerticleDir()) : data.getVerticleDir() != null)
      return true;
    return false;
  }

  private void onOK() {
    // add your code here
//        dispose();
  }

  private void onCancel() {
    // add your code here if necessary
//        dispose();
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

}
