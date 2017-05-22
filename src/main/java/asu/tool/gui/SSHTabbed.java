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

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class SSHTabbed {
  private JPanel contentPanel;
  private JButton connectBtn;
  private JButton disconnectBtn;
  private JTextField host;
  private JTextField port;
  private JTextField userName;
  private JPasswordField password;
  private JTextField remoteCmd;
  private JButton btnExecute;
  private JTextField ulSrc;
  private JTextField ulDest;
  private JButton srcFileBtn;
  private JButton uploadBtn;
  private JButton downloadBtn;
  private JButton destFileBtn;
  private JTextField dlSrc;
  private JTextField dlDest;
  private JButton closeBtn;

  JTabbedPane tabbedPane;

  public SSHTabbed() {
    connectBtn.addActionListener(e -> onOK());

    disconnectBtn.addActionListener(e -> onCancel());

    // call onCancel() on ESCAPE
    contentPanel.registerKeyboardAction(e -> onCancel(),
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    btnExecute.addMouseListener(new MouseAdapter() {
      /**
       * {@inheritDoc}
       *
       * @param e
       */
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
      }
    });
    srcFileBtn.addMouseListener(new MouseAdapter() {
      /**
       * {@inheritDoc}
       *
       * @param e
       */
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
      }
    });
    uploadBtn.addMouseListener(new MouseAdapter() {
      /**
       * {@inheritDoc}
       *
       * @param e
       */
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
      }
    });
    downloadBtn.addMouseListener(new MouseAdapter() {
      /**
       * {@inheritDoc}
       *
       * @param e
       */
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
      }
    });
    destFileBtn.addMouseListener(new MouseAdapter() {
      /**
       * {@inheritDoc}
       *
       * @param e
       */
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
      }
    });
    closeBtn.addMouseListener(new MouseAdapter() {
      /**
       * {@inheritDoc}
       *
       * @param e
       */
      @Override
      public void mouseClicked(MouseEvent e) {
        JTabbedPane tp = getTabbedPane();
        if (tp != null) {
          tp.remove(getContentPanel());
        }
      }
    });
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

  public static void main(String[] args) {
    SSHTabbed dialog = new SSHTabbed();
//        dialog.center();
//        dialog.pack();
//        dialog.setVisible(true);

    JFrame frame = new JFrame("GUI");
    frame.setContentPane(new SSHTabbed().contentPanel);
    frame.setFont(Font.getFont("Sans Serif"));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);

//        System.exit(0);
  }

}
