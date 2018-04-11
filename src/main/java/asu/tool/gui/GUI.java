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

import asu.tool.test.testcase.MQTest;
import asu.tool.test.testcase.RedisTest;
import asu.tool.test.testcase.ZKTest;
import asu.tool.tool.GUITools;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

public class GUI {

	private JTabbedPane tabbedPane1;
	private JPanel mainPanel;
	private JButton testMQStartBtn;
	private JTextField testMQServer;
	private JPasswordField testMQPassword;
	private JTextField testUserName;
	private JButton clearBtn;
	private JTextField testMQPort;
	private JTextField testMQVhost;
	private JTextField testZkStr;
	private JButton testZKStartBtn;
	private JTextField testMQTestTimes;
	private JTextField testZKTestTimes;
	private JTextField testRedisServer;
	private JTextField testRedisTimes;
	private JButton testRedisStarBtn;
	private JTextField testRedisPort;

	private JPanel bottomPanel;
	private JButton closeCurrTabBtn;

	private MQTest mqTest;
	private GUIDataBean guiDataBean = new GUIDataBean();
	private volatile ZKTest zkTest;
	private RedisTest redisTest;

	ConsolePane consolePane = ConsolePane.getInstance();
	/** 撤销管理类 */
	private UndoManager um = new UndoManager();
	SSHTabbed sshTabbed = new SSHTabbed();
	ZKDataCopyTabbed zkDataCopyTabbed = new ZKDataCopyTabbed();
	AppClientMockerTabbed appClientMockerTabbed = new AppClientMockerTabbed();
	ZKRenameNodeTabbed zkRenameNodeTabbed = new ZKRenameNodeTabbed();
	MockHttpServerTabbed mockHttpServerTabbed = new MockHttpServerTabbed();

	public GUI() {

		testRedisStarBtn.addMouseListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 *
			 * @param e
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				new Thread(getTestCaseOfRedis()).start();
				testRedisStarBtn.setEnabled(false);
			}
		});

		clearBtn.addActionListener(new ActionListener() {
			/**
			 * Invoked when an action occurs.
			 *
			 * @param e
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				consolePane.clear();

			}
		});

		testMQStartBtn.addMouseListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 *
			 * @param e
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				new Thread(getTestCaseOfMQ()).start();
				testMQStartBtn.setEnabled(false);
			}
		});

		testZKStartBtn.addMouseListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 *
			 * @param e
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				new Thread(getTestCaseOfZK()).start();
				testZKStartBtn.setEnabled(false);
			}
		});

		JTextComponent[] editors = new JTextComponent[]{
				testMQServer,
				testMQPassword,
				testUserName,
				testMQPort,
				testMQVhost,
				testZkStr,
				testMQTestTimes,
				testZKTestTimes,
				testRedisServer,
				testRedisTimes,
				testRedisPort,
				consolePane.textPane

		};
		GUITools.attachKeyListener(um, editors);

		bottomPanel.add(ConsolePane.getInstance(), BorderLayout.CENTER);
		sshTabbed.setTabbedPane(tabbedPane1);
		closeCurrTabBtn.addMouseListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 *
			 * @param e
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				tabbedPane1.removeTabAt(tabbedPane1.getSelectedIndex());
			}
		});

		mockHttpServerTabbed.setTabbedPane(tabbedPane1);
	}


	private Runnable getTestCaseOfRedis() {
		return () -> {
			try {
				if (redisTest != null) {
					redisTest.close();
				}
				try {
					getData(guiDataBean);
					redisTest = new RedisTest(guiDataBean, GUI.this);
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				}
				redisTest.test();
			} catch (Throwable e1) {
			}
			testRedisStarBtn.setEnabled(true);
		};
	}

	private Runnable getTestCaseOfMQ() {
		return () -> {
			if (mqTest != null) {
				mqTest.close();
			}
			getData(guiDataBean);
			try {
				mqTest = new MQTest(guiDataBean, GUI.this);
				mqTest.test();
			} catch (Exception e12) {
				e12.printStackTrace();
			} finally {
				testMQStartBtn.setEnabled(true);
			}


		};
	}

	private Runnable getTestCaseOfZK() {
		return () -> {
			getData(guiDataBean);
			if (zkTest != null && !zkTest.zkConnStr.equals(guiDataBean.getZkConnStr())) {
				zkTest.close();
				zkTest = null;
				zkTest = new ZKTest(guiDataBean, GUI.this);
			} else if (zkTest == null) {
				zkTest = new ZKTest(guiDataBean, GUI.this);
			}

			try {
				zkTest.test();
			} catch (Throwable e1) {
				e1.printStackTrace();
				zkTest.close();
				zkTest = null;
			}
			testZKStartBtn.setEnabled(true);
		};
	}

	public static void main(String[] args) {
		initLookAndFeel();
		JFrame frame = new JFrame("My Tools");
		GUI gui = new GUI();
		frame.setContentPane(gui.mainPanel);
		frame.setFont(Font.getFont("Sans"));
		gui.createMenuBar(frame);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		GUITools.center(frame);
		frame.setVisible(true);
	}


	public void createMenuBar(JFrame frame) {
		JMenuBar menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		JMenu menuFile = initFileMenu();


		JMenu menuEdit = new JMenu("编辑");
		JMenu menuTool = initToolMenu();
		JMenu menuSetting = new JMenu("设置");
		JMenu menuAbout = initAboutMenu(frame);

		menubar.add(menuFile);
		menubar.add(menuEdit);
		menubar.add(menuTool);
		menubar.add(menuSetting);
		menubar.add(menuAbout);
	}

	private JMenu initAboutMenu(JFrame frame) {
		JMenu menuAbout = new JMenu("关于(A)");
		menuAbout.setMnemonic('A');
		JMenuItem itemHelp = new JMenuItem("帮助(H)");
		itemHelp.setMnemonic('H');
		itemHelp.addActionListener(e -> {
			JOptionPane.showMessageDialog(frame, "This is a set of tool of Victor Suk!");
		});
		menuAbout.add(itemHelp);
		return menuAbout;
	}

	private JMenu initToolMenu() {
		JMenu menuTool = new JMenu("工具(T)");
		menuTool.setMnemonic('T');
		JMenuItem itemZKCopy = new JMenuItem("Zookeeper Data Copy");
		itemZKCopy.addActionListener(e -> {
			if (tabbedPane1.indexOfTab("Zookeeper DataCopy") == -1) {
				tabbedPane1.addTab("Zookeeper DataCopy", zkDataCopyTabbed.getContentPanel());
			}
			tabbedPane1.setSelectedIndex(tabbedPane1.indexOfTab("Zookeeper DataCopy"));
		});
		menuTool.add(itemZKCopy);

		JMenuItem itemZKMove = new JMenuItem("Zookeeper Node Rename");
		itemZKMove.addActionListener(e -> {
			if (tabbedPane1.indexOfTab("Zookeeper Node Rename") == -1) {
				tabbedPane1.addTab("Zookeeper Node Rename", zkRenameNodeTabbed.getContentPanel());
			}
			tabbedPane1.setSelectedIndex(tabbedPane1.indexOfTab("Zookeeper Node Rename"));
		});
		menuTool.add(itemZKMove);

		menuTool.addSeparator();

		JMenuItem itemSSH = new JMenuItem("SSH Tool");
		itemSSH.addActionListener(e -> {
			if (tabbedPane1.indexOfTab("sshTool") == -1) {
				tabbedPane1.addTab("sshTool", sshTabbed.getContentPanel());
			}
			tabbedPane1.setSelectedIndex(tabbedPane1.indexOfTab("sshTool"));
		});
		menuTool.add(itemSSH);
		menuTool.addSeparator();

		JMenuItem itemAppClientMocker = new JMenuItem("App Client Mocker");
		itemAppClientMocker.addActionListener(e -> {
			if (tabbedPane1.indexOfTab("App Client Mocker") == -1) {
				tabbedPane1.addTab("App Client Mocker", appClientMockerTabbed.getAppClientPanel());
			}
			tabbedPane1.setSelectedIndex(tabbedPane1.indexOfTab("App Client Mocker"));

		});
		menuTool.add(itemAppClientMocker);
		menuTool.addSeparator();
		JMenuItem itemHttpDlg = new JMenuItem("Http Sender");
		itemHttpDlg.addActionListener(e -> {
			HttpWin dialog = new HttpWin();
			dialog.pack();
			GUITools.center(dialog);
			dialog.setVisible(true);

		});
	/* Http Server Mock Data */
		menuTool.add(itemHttpDlg);
		JMenuItem itemHttpServer = new JMenuItem("Http Server Mock Data");
		itemHttpServer.addActionListener(e -> {
			if (tabbedPane1.indexOfTab("HttpServerMockData") == -1) {
				tabbedPane1.addTab("HttpServerMockData", mockHttpServerTabbed.getContentPanel());
			}
			tabbedPane1.setSelectedIndex(tabbedPane1.indexOfTab("HttpServerMockData"));

		});
		menuTool.add(itemHttpServer);

		menuTool.addSeparator();
		JMenuItem itemJ2JDlg = new JMenuItem("Json To Java");
		itemJ2JDlg.addActionListener(e -> {
			JsonToJavaBean dialog = new JsonToJavaBean();
			dialog.pack();
			GUITools.center(dialog);
			dialog.setVisible(true);

		});
		menuTool.add(itemJ2JDlg);

		return menuTool;
	}

	private JMenu initFileMenu() {
		JMenu menuFile = new JMenu("文件(F)");
		//快捷键
		menuFile.setMnemonic('F');
		JMenuItem itemQuit = new JMenuItem("退出");
		//ctrl+q;
		itemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		itemQuit.addActionListener(e -> {
			System.exit(0);
		});
		menuFile.add(itemQuit);
		// menuFile.addSeparator();
		return menuFile;
	}

	private static void initLookAndFeel() {
		String[] lafArray = new String[]{
				"ch.randelshofer.quaqua.leopard.Quaqua16LeopardLookAndFeel",
				"ch.randelshofer.quaqua.snowleopard.Quaqua16SnowLeopardLookAndFeel",
				"ch.randelshofer.quaqua.lion.Quaqua16LionLookAndFeel",
				"ch.randelshofer.quaqua.mountainlion.Quaqua16MountainLionLookAndFeel",
				"ch.randelshofer.quaqua.jaguar.Quaqua15JaguarLookAndFeel",
				"ch.randelshofer.quaqua.jaguar.Quaqua15JaguarLookAndFeel",
				"ch.randelshofer.quaqua.panther.Quaqua15PantherLookAndFeel",
				"ch.randelshofer.quaqua.panther.Quaqua15PantherLookAndFeel",
				"ch.randelshofer.quaqua.tiger.Quaqua15TigerLookAndFeel",
				"ch.randelshofer.quaqua.tiger.Quaqua15TigerLookAndFeel",
				"ch.randelshofer.quaqua.leopard.Quaqua15LeopardLookAndFeel",
				"ch.randelshofer.quaqua.leopard.Quaqua15LeopardLookAndFeel",
				"ch.randelshofer.quaqua.tiger.Quaqua15TigerCrossPlatformLookAndFeel",
				"ch.randelshofer.quaqua.leopard.Quaqua15LeopardCrossPlatformLookAndFeel",
				"ch.randelshofer.quaqua.leopard.Quaqua15LeopardCrossPlatformLookAndFeel",
				"com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
				"com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel",
				"javax.swing.plaf.nimbus.NimbusLookAndFeel",
				"com.sun.java.swing.plaf.gtk.GTKLookAndFeel",
				UIManager.getSystemLookAndFeelClassName(),
				UIManager.getCrossPlatformLookAndFeelClassName(),
				"com.sun.java.swing.plaf.motif.MotifLookAndFeel"

		};

		for (String lookAndFeel : lafArray) {
			try {
				UIManager.setLookAndFeel(lookAndFeel);

				// If L&F = "Metal", set the theme
				if (lookAndFeel.equals("Metal")) {
					MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
					UIManager.setLookAndFeel(new MetalLookAndFeel());
				}
				System.out.println("using " + lookAndFeel);
				break;
			} catch (ClassNotFoundException e) {
//                System.err.println("Couldn't find class for specified look and feel:"
//                        + lookAndFeel);
//                System.err.println("Did you include the L&F library in the class path?");
//                System.err.println("Using the default look and feel.");
			} catch (UnsupportedLookAndFeelException e) {
//                System.err.println("Can't use the specified look and feel ("
//                        + lookAndFeel
//                        + ") on this platform.");
//                System.err.println("Using the default look and feel.");
			} catch (Throwable e) {
//                System.err.println("Couldn't get specified look and feel ("
//                        + lookAndFeel
//                        + "), for some reason.");
//                e.printStackTrace();
			}
		}
	}


	public void appendResult(String testCaseId, String text) {
		System.out.println(text);
	}

	public void setData(GUIDataBean data) {
		testMQServer.setText(data.getMqHost());
		testUserName.setText(data.getMqUsername());
		testMQPort.setText(data.getMqPort());
		testMQPassword.setText(data.getMqPassword());
		testMQVhost.setText(data.getMqVhost());
		testMQTestTimes.setText(data.getMqTestTimes());
		testZkStr.setText(data.getZkConnStr());
		testZKTestTimes.setText(data.getZkTestTimes());
		testRedisServer.setText(data.getRedisHost());
		testRedisTimes.setText(data.getRedisTestTime());
		testRedisPort.setText(data.getRedisPort());
	}

	public void getData(GUIDataBean data) {
		data.setMqHost(testMQServer.getText());
		data.setMqUsername(testUserName.getText());
		data.setMqPort(testMQPort.getText());
		data.setMqPassword(testMQPassword.getText());
		data.setMqVhost(testMQVhost.getText());
		data.setMqTestTimes(testMQTestTimes.getText());
		data.setZkConnStr(testZkStr.getText());
		data.setZkTestTimes(testZKTestTimes.getText());
		data.setRedisHost(testRedisServer.getText());
		data.setRedisTestTime(testRedisTimes.getText());
		data.setRedisPort(testRedisPort.getText());
	}

	public boolean isModified(GUIDataBean data) {
		if (testMQServer.getText() != null ? !testMQServer.getText().equals(data.getMqHost())
				: data.getMqHost() != null) {
			return true;
		}
		if (testUserName.getText() != null ? !testUserName.getText().equals(data.getMqUsername())
				: data.getMqUsername() != null) {
			return true;
		}
		if (testMQPort.getText() != null ? !testMQPort.getText().equals(data.getMqPort())
				: data.getMqPort() != null) {
			return true;
		}
		if (testMQPassword.getText() != null ? !testMQPassword.getText()
				.equals(data.getMqPassword()) : data.getMqPassword() != null) {
			return true;
		}
		if (testMQVhost.getText() != null ? !testMQVhost.getText().equals(data.getMqVhost())
				: data.getMqVhost() != null) {
			return true;
		}
		if (testMQTestTimes.getText() != null ? !testMQTestTimes.getText()
				.equals(data.getMqTestTimes()) : data.getMqTestTimes() != null) {
			return true;
		}
		if (testZkStr.getText() != null ? !testZkStr.getText().equals(data.getZkConnStr())
				: data.getZkConnStr() != null) {
			return true;
		}
		if (testZKTestTimes.getText() != null ? !testZKTestTimes.getText()
				.equals(data.getZkTestTimes()) : data.getZkTestTimes() != null) {
			return true;
		}
		if (testRedisServer.getText() != null ? !testRedisServer.getText()
				.equals(data.getRedisHost()) : data.getRedisHost() != null) {
			return true;
		}
		if (testRedisTimes.getText() != null ? !testRedisTimes.getText()
				.equals(data.getRedisTestTime()) : data.getRedisTestTime() != null) {
			return true;
		}
		if (testRedisPort.getText() != null ? !testRedisPort.getText().equals(data.getRedisPort())
				: data.getRedisPort() != null) {
			return true;
		}
		return false;
	}


}
