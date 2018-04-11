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

package asu.tool.tool;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import org.nutz.lang.Strings;

public class GUITools {
  public static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

  public static void center(Window win) {
    int windowWidth = win.getWidth(); // 获得窗口宽
    int windowHeight = win.getHeight(); // 获得窗口高
    Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
    Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
    int screenWidth = screenSize.width; // 获取屏幕的宽
    int screenHeight = screenSize.height; // 获取屏幕的高
    win.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);// 设置窗口居中显示
  }

  /**
   * attach Ctrl+a, Ctrl+x, Ctrl+v, Ctrl+z, Ctrl+r
   *
   * @param editors JTextComponent[]
   */
  public static void attachKeyListener(JTextComponent... editors) {
    UndoManager um = new UndoManager();//撤销管理类
    attachKeyListener(um, editors);
  }

  public static void attachKeyListener(UndoManager um, JTextComponent... editors) {
    for (JTextComponent editor : editors) {
      editor.addKeyListener(GUITools.getKeyAdapter(editor, um));
      editor.getDocument().addUndoableEditListener(e -> um.addEdit(e.getEdit()));//编辑撤销的监听
    }
  }

  public static void attachKeyListener(UndoManager um, java.util.List<JTextComponent> editors) {
    attachKeyListener(um, editors.toArray(new JTextComponent[0]));
  }

  public static void attachKeyListener(java.util.List<JTextComponent> editors) {
    UndoManager um = new UndoManager();//撤销管理类
    attachKeyListener(um, editors.toArray(new JTextComponent[0]));
  }


  /**
   * attach Ctrl+a, Ctrl+x, Ctrl+v, Ctrl+z, Ctrl+r
   *
   * @param component JTextComponent
   * @param um        UndoManager
   * @return KeyAdapter
   */
  public static KeyAdapter getKeyAdapter(final JTextComponent component, UndoManager um) {

    return new KeyAdapter() {
      /**
       * Invoked when a key has been pressed.
       *
       * @param e
       */
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_A/*全选*/
            || e.getKeyCode() == KeyEvent.VK_C/* 复制 */
            || e.getKeyCode() == KeyEvent.VK_X/*剪切*/
            || e.getKeyCode() == KeyEvent.VK_V
            || e.getKeyCode() == KeyEvent.VK_Z
            || e.getKeyCode() == KeyEvent.VK_R)) {
//                      System.out.println(e.getKeyCode());
        } else {
          return;
        }
        switch (e.getKeyCode()) {
          case KeyEvent.VK_A:
            component.setSelectionStart(0);
            component.setSelectionEnd(component.getText().length());
            break;
          case KeyEvent.VK_C:
            String selectedText = component.getSelectedText();

            if (Strings.isBlank(selectedText)) {
              selectedText = component.getText();
            }
            //设置字符串
            //构建String数据类型
            StringSelection selection = new StringSelection(selectedText);
            //添加文本到系统剪切板
            clipboard.setContents(selection, null);
            break;
          case KeyEvent.VK_V:
            Transferable content = clipboard.getContents(null);//从系统剪切板中获取数据
            if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {//判断是否为文本类型
              String text = null;//从数据中获取文本值
              try {
                text = (String) content.getTransferData(DataFlavor.stringFlavor);
              } catch (UnsupportedFlavorException e1) {
                e1.printStackTrace();
              } catch (IOException e1) {
                e1.printStackTrace();
              }
              if (text == null) {
                return;
              }
              if (component instanceof JTextArea) {
                ((JTextArea) component).replaceRange(text, component.getSelectionStart(), component.getSelectionEnd());
              } else if (component instanceof JTextComponent) {
                ((JTextComponent) component).replaceSelection(text);
              }
              component.paintImmediately(component.getBounds());
            }
            break;
          case KeyEvent.VK_X:
            String selectedText2 = component.getSelectedText();
            if (Strings.isBlank(selectedText2)) {
              selectedText2 = component.getText();
              component.setText("");
            } else {
              if (component instanceof JTextArea) {
                ((JTextArea) component).replaceRange("", component.getSelectionStart(), component.getSelectionEnd());
              } else if (component instanceof JTextComponent) {
                ((JTextComponent) component).replaceSelection("");
              }
            }
            //设置字符串
            //构建String数据类型
            StringSelection selection2 = new StringSelection(selectedText2);
            //添加文本到系统剪切板
            clipboard.setContents(selection2, null);
            break;
          case KeyEvent.VK_R:
            um.redo();
            break;
          case KeyEvent.VK_Z:
            um.undo();
          default:
            break;
        }
      }
    };
  }
}
