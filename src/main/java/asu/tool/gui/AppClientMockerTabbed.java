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

import asu.tool.test.testcase.MockClient;
import asu.tool.tools.GUITools;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.undo.UndoManager;

public class AppClientMockerTabbed {
  private JTextField testCGWHost;
  private JTextField testCGWPort;
  private JCheckBox isCompressableCB;
  private JCheckBox isEncryptableCB;
  private JTextField testCGWCompanyId;
  private JTextField testCGWApiAcc;
  private JPasswordField testCGWApiAccKey;
  private JButton testCGWConnectBtn;
  private JButton testCGWSendBtn;
  private JTextArea testCGWSendData;
  private JButton testCGWDisconnetBtn;
  private JPanel appClientPanel;
  private UndoManager um = new UndoManager();//撤销管理类
  private MockClient mockClient;
  AppClientMockerBean bean = new AppClientMockerBean();


  public AppClientMockerTabbed() {
    testCGWSendBtn.addMouseListener(new MouseAdapter() {
      /**
       * {@inheritDoc}
       *
       * @param e
       */
      @Override
      public void mouseClicked(MouseEvent e) {
        new Thread(() -> {
          getData(bean);
          if (mockClient == null) {
            try {
              mockClient = new MockClient(bean, AppClientMockerTabbed.this);
              mockClient.connect();
              testCGWSendBtn.setEnabled(true);
              testCGWDisconnetBtn.setEnabled(true);
            } catch (Throwable ex) {
              appendResult(ex.getMessage());
            }
          }
          try {
            mockClient.send(bean.getClientGatewaySendData());
          } catch (Exception e1) {
            appendResult(e1.getMessage());
          }
        }).start();
      }
    });
    testCGWConnectBtn.addMouseListener(new MouseAdapter() {
      /**
       * {@inheritDoc}
       *
       * @param e
       */
      @Override
      public void mouseClicked(MouseEvent e) {
        new Thread(getTestCaseOfCGW()).start();
        testCGWConnectBtn.setEnabled(false);
      }
    });
    testCGWDisconnetBtn.addMouseListener(new MouseAdapter() {
      /**
       * {@inheritDoc}
       *
       * @param e
       */
      @Override
      public void mouseClicked(MouseEvent e) {

        new Thread(() -> {
          if (mockClient != null) {
            try {
              mockClient.closeChannel();
            } catch (InterruptedException e1) {
              appendResult(e1.getMessage());
            }
          }
          fireCGWDisconnected();
        }).start();


      }
    });

    GUITools.attachKeyListener(um, testCGWHost, testCGWPort, testCGWCompanyId, testCGWApiAcc,
        testCGWApiAccKey, testCGWSendData);
  }

  public void appendResult(String text) {
    System.out.println(text);
  }

  public void fireCGWDisconnected() {
    SwingUtilities.invokeLater(() -> {
      testCGWConnectBtn.setEnabled(true);
      testCGWDisconnetBtn.setEnabled(false);
      testCGWSendBtn.setEnabled(false);
    });
  }

  public void fireCGWLoginSuc(int i) {
    if (i == 0) {
      // suc
      SwingUtilities.invokeLater(() -> {
        testCGWConnectBtn.setEnabled(false);
        testCGWSendBtn.setEnabled(true);
        testCGWDisconnetBtn.setEnabled(true);
      });
    } else {
      // fail
      SwingUtilities.invokeLater(() -> {
        testCGWConnectBtn.setEnabled(true);
        testCGWSendBtn.setEnabled(false);
        testCGWDisconnetBtn.setEnabled(false);
      });
    }
  }


  public Runnable getTestCaseOfCGW() {
    return () -> {
      getData(bean);
      if (mockClient != null) {
        try {
          mockClient.closeChannel();
        } catch (InterruptedException e) {
          appendResult(e.getMessage());
        }
      } else {
        mockClient = new MockClient(bean, this);
      }
      try {
        mockClient.connect();
        testCGWSendBtn.setEnabled(true);
        testCGWDisconnetBtn.setEnabled(true);
      } catch (InterruptedException e) {
        appendResult(e.getMessage());
      }
    };
  }

  public void setData(AppClientMockerBean data) {
    testCGWHost.setText(data.getClientGatewayHost());
    testCGWPort.setText(data.getClientGatewayPort());
    testCGWCompanyId.setText(data.getCompanyId());
    isEncryptableCB.setSelected(data.isClientGatewayMsgEncryptable());
    isCompressableCB.setSelected(data.isClientGatewayMsgCompressable());
    testCGWApiAcc.setText(data.getApiAccount());
    testCGWApiAccKey.setText(data.getApiAccountKey());
    testCGWSendData.setText(data.getClientGatewaySendData());
  }

  public void getData(AppClientMockerBean data) {
    data.setClientGatewayHost(testCGWHost.getText());
    data.setClientGatewayPort(testCGWPort.getText());
    data.setCompanyId(testCGWCompanyId.getText());
    data.setClientGatewayMsgEncryptable(isEncryptableCB.isSelected());
    data.setClientGatewayMsgCompressable(isCompressableCB.isSelected());
    data.setApiAccount(testCGWApiAcc.getText());
    data.setApiAccountKey(testCGWApiAccKey.getText());
    data.setClientGatewaySendData(testCGWSendData.getText());
  }

  public boolean isModified(AppClientMockerBean data) {
    if (testCGWHost.getText() != null ? !testCGWHost.getText().equals(data.getClientGatewayHost()) : data.getClientGatewayHost() != null)
      return true;
    if (testCGWPort.getText() != null ? !testCGWPort.getText().equals(data.getClientGatewayPort()) : data.getClientGatewayPort() != null)
      return true;
    if (testCGWCompanyId.getText() != null ? !testCGWCompanyId.getText().equals(data.getCompanyId()) : data.getCompanyId() != null)
      return true;
    if (isEncryptableCB.isSelected() != data.isClientGatewayMsgEncryptable()) return true;
    if (isCompressableCB.isSelected() != data.isClientGatewayMsgCompressable()) return true;
    if (testCGWApiAcc.getText() != null ? !testCGWApiAcc.getText().equals(data.getApiAccount()) : data.getApiAccount() != null)
      return true;
    if (testCGWApiAccKey.getText() != null ? !testCGWApiAccKey.getText().equals(data.getApiAccountKey()) : data.getApiAccountKey() != null)
      return true;
    if (testCGWSendData.getText() != null ? !testCGWSendData.getText().equals(data.getClientGatewaySendData()) : data.getClientGatewaySendData() != null)
      return true;
    return false;
  }

  public JPanel getAppClientPanel() {
    return appClientPanel;
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("AppClientMockerTabbed");
    frame.setContentPane(new AppClientMockerTabbed().appClientPanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
}
