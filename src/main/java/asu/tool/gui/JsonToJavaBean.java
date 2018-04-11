/*
 * Copyright (c) 2018 Suk Honzeon
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package asu.tool.gui;

import asu.tool.tool.GUITools;
import asu.tool.tool.JsonTools;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

public class JsonToJavaBean extends JDialog {

	private JPanel contentPane;
	private JButton buttonOK;
	private JTextArea jsonCode;
	private JTextField javaPackage;
	private JTextField javaClass;
	private JButton btnGenerate;
	private JTextPane javaCode;

	public JsonToJavaBean() {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		buttonOK.addActionListener(this::onOK);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onOK(null);
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(this::onOK, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		btnGenerate.addActionListener(this::onExecute);
		btnGenerate.registerKeyboardAction(this::onExecute, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK),
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		myInit();
	}

	private void myInit() {
		setTitle("Json to Java");
		javaCode.getDocument().addDocumentListener(new SyntaxHighlighter(javaCode));
		GUITools.attachKeyListener(jsonCode, javaPackage, javaClass, javaCode);
	}
	private void onOK(ActionEvent e) {
		// add your code here
		dispose();
	}

	private void onExecute(ActionEvent e) {
		// add your code here
		String json = jsonCode.getText();
		String javaPkg = javaPackage.getText();
		String javaCls = javaClass.getText();
		String code = JsonTools.getJavaFromJson(json, javaCls, javaPkg);
		javaCode.setText(code);
	}

	public static void main(String[] args) {
		JsonToJavaBean dialog = new JsonToJavaBean();
		dialog.pack();
		GUITools.center(dialog);
		dialog.setVisible(true);
		System.exit(0);
	}
}
