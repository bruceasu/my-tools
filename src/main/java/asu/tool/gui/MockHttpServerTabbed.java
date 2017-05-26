package asu.tool.gui;

import asu.tool.tools.GUITools;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.undo.UndoManager;

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
  JTabbedPane tabbedPane;
  public MockHttpServerTabbed() {
    btnSend.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       *
       * @param e
       */
      @Override
      public void actionPerformed(ActionEvent e) {

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

}
