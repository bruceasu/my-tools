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
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * @author Unmi
 */
public class ConsolePane extends JScrollPane {
  private PipedInputStream piOut;
  private PipedInputStream piErr;
  private PipedOutputStream poOut;
  private PipedOutputStream poErr;

  public JTextPane textPane = new JTextPane();

  private static ConsolePane console = null;

  public static synchronized ConsolePane getInstance() {
    if (console == null) {
      console = new ConsolePane();
    }
    return console;
  }

  private ConsolePane() {
    textPane.setEnabled(true);
    setViewportView(textPane);
    piOut = new PipedInputStream();
    piErr = new PipedInputStream();
    try {
      poOut = new PipedOutputStream(piOut);
      poErr = new PipedOutputStream(piErr);
    } catch (IOException e) {
    }

    // Set up System.out
    System.setOut(new PrintStream(poOut, true));

    // Set up System.err
    System.setErr(new PrintStream(poErr, true));

    textPane.setEditable(false);
    setPreferredSize(new Dimension(640, 120));

    // Create reader threads
    new ReaderThread(piOut).start();
    new ReaderThread(piErr).start();
  }

  public void clear() {
    textPane.setText("");
  }

  /**
   * Returns the number of lines in the document.
   *
   * @return line count
   */
  public final int getLineCount() {
    return textPane.getDocument().getDefaultRootElement().getElementCount();
  }

  /**
   * Returns the start offset of the specified line.
   *
   * @param line The line
   * @return The start offset of the specified line, or -1 if the line is
   * invalid
   */
  public int getLineStartOffset(int line) {
    Element lineElement = textPane.getDocument().getDefaultRootElement()
        .getElement(line);
    if (lineElement == null)
      return -1;
    else
      return lineElement.getStartOffset();
  }

  public void replaceRange(String str, int start, int end) {
    if (end < start) {
      throw new IllegalArgumentException("end before start");
    }
    Document doc = textPane.getDocument();
    if (doc != null) {
      try {
        if (doc instanceof AbstractDocument) {
          ((AbstractDocument) doc).replace(start, end - start, str,
              null);
        } else {
          doc.remove(start, end - start);
          doc.insertString(start, str, null);
        }
      } catch (BadLocationException e) {
        throw new IllegalArgumentException(e.getMessage());
      }
    }
  }

  class ReaderThread extends Thread {
    PipedInputStream pi;

    ReaderThread(PipedInputStream pi) {
      this.pi = pi;
    }

    public void run() {
      final byte[] buf = new byte[1024];

      while (true) {
        try {
          final int len = pi.read(buf);
          if (len == -1) {
            break;
          }
          StyledDocument doc = (StyledDocument) textPane
              .getDocument();
          // Create a style object and then set the style
          // attributes
          Style style = doc.addStyle("StyleName", null);
          Color foreground1 = pi == piOut ? Color.BLACK : Color.RED;
          // Foreground color
          StyleConstants.setForeground(style, foreground1);
          // Append to document
          String outstr = new String(buf, 0, len);
          SwingUtilities.invokeLater(() -> {
            try {
              doc.insertString(doc.getLength(), outstr, style);
            } catch (BadLocationException e) {
              // e.printStackTrace();
            }

            // Make sure the last line is always visible
            textPane.setCaretPosition(textPane.getDocument().getLength());

            // Keep the text area down to a certain line count
            int idealLine = 150;
            int maxExcess = 50;

            int excess = getLineCount() - idealLine;
            if (excess >= maxExcess) {
              replaceRange("", 0, getLineStartOffset(excess));
            }
          });
        } catch (IOException e) {
          // e.printStackTrace();
        }
      }
    }
  }
}
