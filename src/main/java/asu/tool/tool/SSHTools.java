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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.*;
import org.nutz.lang.Strings;

public class SSHTools {
  private String user;
  private String host;
  private int port;
  private String password;
  MyUserInfo ui;
  Session session;
  JSch jsch = new JSch();

  public SSHTools(String user, String password, String host, int port) {
    this.user = user;
    this.password = password;
    this.host = host;
    this.port = port;
    if (port < 1 || port > 65535) {
      this.port = 22;
    }
    ui = new MyUserInfo();
  }


  public Session connect() throws JSchException {
    if (session != null && session.isConnected()) return session;

    session = jsch.getSession(user, host, port);
    // username and password will be given via UserInfo interface.

    session.setUserInfo(ui);
    session.connect();
    return session;
  }

  public void disconnect() {
    if (session != null && session.isConnected()) {
      session.disconnect();
    }
    session = null;
  }

  public boolean scpFrom(String src, String dest) {
    FileOutputStream fos = null;
    try {
      String prefix = null;
      if (new File(dest).isDirectory()) {
        prefix = dest + File.separator;
      }

      // exec 'scp -f rfile' remotely
      String command = "scp -f " + src;
      Channel channel = session.openChannel("exec");
      ((ChannelExec) channel).setCommand(command);

      channel.connect();

      // get I/O streams for remote scp
      try (OutputStream out = channel.getOutputStream();
           InputStream in = channel.getInputStream()) {
        byte[] buf = new byte[1024];

        // send '\0'
        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();
        long cnt = 0;
        long origSize = 0;
        long lastP = 0;
        while (true) {
          int c = checkAck(in);
          if (c != 'C') {
            break;
          }

          // read '0644 '
          in.read(buf, 0, 5);

          long filesize = getFilesize(in, buf);
          String destFilePath = getDestFilePath(dest, prefix, out, in, buf);
          fos = new FileOutputStream(destFilePath);
          int foo;
          origSize = filesize;
          long s = System.currentTimeMillis();
          while (true) {
            if (buf.length < filesize) foo = buf.length;
            else foo = (int) filesize;
            foo = in.read(buf, 0, foo);
            if (foo < 0) {
              // error
              break;
            }
            fos.write(buf, 0, foo);
            filesize -= foo;
            if (filesize == 0L) break;
            cnt += foo;
            long p = cnt * 100 / origSize;
            if (p != lastP) {
              lastP = p;
              System.out.println("download " + src + " " + cnt + "/" + origSize + ", " + p + "%.");
            }
          }
          fos.close();
          fos = null;
          long e = System.currentTimeMillis();
          System.out.println("download " + src + " to " + destFilePath + " DONE, cost " + (e - s) / 1000 + " seconds.");
          if (checkAck(in) != 0) {
            return false;
          }

          // send '\0'
          buf[0] = 0;
          out.write(buf, 0, 1);
          out.flush();
          return true;
        }
      }
    } catch (Exception e) {
      System.out.println(e);
      try {
        if (fos != null) fos.close();
      } catch (Exception ee) {
      }

    }
    return false;
  }

  private long getFilesize(InputStream in, byte[] buf) throws IOException {
    long filesize = 0L;
    while (true) {
      if (in.read(buf, 0, 1) < 0) {
        // error
        break;
      }
      if (buf[0] == ' ') break;
      filesize = filesize * 10L + (long) (buf[0] - '0');
    }
    return filesize;
  }

  private String getDestFilePath(String dest, String prefix, OutputStream out, InputStream in, byte[] buf) throws IOException {
    String file = null;
    for (int i = 0; ; i++) {
      in.read(buf, i, 1);
      if (buf[i] == (byte) 0x0a) {
        file = new String(buf, 0, i);
        break;
      }
    }

    //System.out.println("filesize="+filesize+", file="+file);

    // send '\0'
    buf[0] = 0;
    out.write(buf, 0, 1);
    out.flush();

    // read a content of lfile
    return prefix == null ? dest : prefix + file;
  }

  public void exec(String command) {
    try {

      /*
      String xhost="127.0.0.1";
      int xport=0;
      String display=JOptionPane.showInputDialog("Enter display name",
                                                 xhost+":"+xport);
      xhost=display.substring(0, display.indexOf(':'));
      xport=Integer.parseInt(display.substring(display.indexOf(':')+1));
      session.setX11Host(xhost);
      session.setX11Port(xport+6000);
      */
      if (Strings.isBlank(command)) {
        JOptionPane.showInputDialog("Enter command",
            "set|grep SSH");
      }
      Channel channel = session.openChannel("exec");
      ((ChannelExec) channel).setCommand(command);

      // X Forwarding
      // channel.setXForwarding(true);

      //channel.setInputStream(System.in);
      channel.setInputStream(null);

      //channel.setOutputStream(System.out);

      //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
      //((ChannelExec)channel).setErrStream(fos);
//            ((ChannelExec) channel).setErrStream(System.err);

      InputStream in = channel.getInputStream();

      channel.connect();

      byte[] tmp = new byte[1024];

      while (true) {
        while (in.available() > 0) {
          int i = in.read(tmp, 0, 1024);
          if (i < 0) break;
          System.out.write(tmp, 0, i);
        }
        if (channel.isClosed()) {
          if (in.available() > 0) continue;
          System.out.println("exit-status: " + channel.getExitStatus());
          break;
        }
        try {
          Thread.sleep(1000);
        } catch (Exception ee) {
        }
      }
      channel.disconnect();
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  public boolean scpTo(String src, String dest) {
    FileInputStream fis = null;
    Channel channel = null;
    try {

      boolean ptimestamp = false;
      // exec 'scp -t rfile' remotely
      String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + dest;

      channel = session.openChannel("exec");
      ((ChannelExec) channel).setCommand(command);

      long s = System.currentTimeMillis();
      // get I/O streams for remote scp
      try (OutputStream out = channel.getOutputStream();
           InputStream in = channel.getInputStream()) {
        channel.connect();

        if (checkAck(in) != 0) {
          System.out.println("error!");
          channel.disconnect();
          return false;
        }

        File lfile = new File(src);

        if (ptimestamp) {
          command = "T " + (lfile.lastModified() / 1000) + " 0";
          // The access time should be sent here,
          // but it is not accessible with JavaAPI ;-<
          command += (" " + (lfile.lastModified() / 1000) + " 0\n");
          out.write(command.getBytes());
          out.flush();
          if (checkAck(in) != 0) {
            return false;
          }
        }

        // send "C0644 filesize filename", where filename should not include '/'
        long filesize = lfile.length();
        command = "C0644 " + filesize + " ";
//                if (src.lastIndexOf('/') > 0) {
//                    command += src.substring(src.lastIndexOf('/') + 1);
//                } else {
//                    command += lfile;
//                }
        command += lfile.getName();
        command += "\n";
        out.write(command.getBytes());
        out.flush();
        if (checkAck(in) != 0) {
          return false;
        }

        // send a content of lfile
        fis = new FileInputStream(lfile);
        byte[] buf = new byte[1024];
        long cnt = 0;
        long lastP = 0;
        while (true) {
          int len = fis.read(buf, 0, buf.length);
          if (len <= 0) break;
          out.write(buf, 0, len); //out.flush();
          cnt += len;
          long p = cnt * 100 / filesize;
          if (p != lastP) {
            lastP = p;
            System.out.println("upload " + lfile + " " + cnt + "/" + filesize + ", " + p + "%.");
          }
        }
        fis.close();
        fis = null;
        long e = System.currentTimeMillis();
        System.out.println("upload " + lfile + " DONE, cost " + (e - s) / 1000 + " seconds.");
        // send '\0'
        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();
        if (checkAck(in) != 0) {
          return false;
        }
      }
      return true;
    } catch (Exception e) {
      System.out.println(e);
      try {
        if (fis != null) fis.close();
      } catch (Exception ee) {
      }

      return false;
    } finally {
      if (channel != null) {
        channel.disconnect();
      }
    }
  }

  static int checkAck(InputStream in) throws IOException {
    int b = in.read();
    // b may be 0 for success,
    //          1 for error,
    //          2 for fatal error,
    //          -1
    if (b == 0) return b;
    if (b == -1) return b;

    if (b == 1 || b == 2) {
      StringBuffer sb = new StringBuffer();
      int c;
      do {
        c = in.read();
        sb.append((char) c);
      }
      while (c != '\n');
      if (b == 1) { // error
        System.out.print(sb.toString());
      }
      if (b == 2) { // fatal error
        System.out.print(sb.toString());
      }
    }
    return b;
  }

  public class MyUserInfo implements UserInfo, UIKeyboardInteractive {
    public String getPassword() {
      return passwd;
    }

    public boolean promptYesNo(String str) {
//            Object[] options = {"yes", "no"};
//            int foo = JOptionPane.showOptionDialog(null,
//                    str,
//                    "Warning",
//                    JOptionPane.DEFAULT_OPTION,
//                    JOptionPane.WARNING_MESSAGE,
//                    null, options, options[0]);
//            return foo == 0;
      return true;
    }

    String passwd;
    JTextField passwordField = (JTextField) new JPasswordField(20);

    public String getPassphrase() {
      return null;
    }

    public boolean promptPassphrase(String message) {
      return true;
    }

    public boolean promptPassword(String message) {

      if (Strings.isNotBlank(SSHTools.this.password)) {
        passwd = SSHTools.this.password;
        SSHTools.this.password = null;
        return true;
      }
      Object[] ob = {passwordField};
      int result =
          JOptionPane.showConfirmDialog(null, ob, message,
              JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        passwd = passwordField.getText();
        return true;
      } else {
        return false;
      }
    }

    public void showMessage(String message) {
      JOptionPane.showMessageDialog(null, message);
    }

    final GridBagConstraints gbc =
        new GridBagConstraints(0, 0, 1, 1, 1, 1,
            GridBagConstraints.NORTHWEST,
            GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0);
    private Container panel;

    public String[] promptKeyboardInteractive(String destination,
                                              String name,
                                              String instruction,
                                              String[] prompt,
                                              boolean[] echo) {
      panel = new JPanel();
      panel.setLayout(new GridBagLayout());

      gbc.weightx = 1.0;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.gridx = 0;
      panel.add(new JLabel(instruction), gbc);
      gbc.gridy++;

      gbc.gridwidth = GridBagConstraints.RELATIVE;

      JTextField[] texts = new JTextField[prompt.length];
      for (int i = 0; i < prompt.length; i++) {
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.weightx = 1;
        panel.add(new JLabel(prompt[i]), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1;
        if (echo[i]) {
          texts[i] = new JTextField(20);
        } else {
          texts[i] = new JPasswordField(20);
        }
        panel.add(texts[i], gbc);
        gbc.gridy++;
      }

      if (JOptionPane.showConfirmDialog(null, panel,
          destination + ": " + name,
          JOptionPane.OK_CANCEL_OPTION,
          JOptionPane.QUESTION_MESSAGE)
          == JOptionPane.OK_OPTION) {
        String[] response = new String[prompt.length];
        for (int i = 0; i < prompt.length; i++) {
          response[i] = texts[i].getText();
        }
        return response;
      } else {
        return null;  // cancel
      }
    }
  }

  public static void main(String[] args) throws JSchException {
    SSHTools ssh = new SSHTools("root", "Gold1234{}", "192.168.35.61", 242);
    ssh.connect();
    // test exec
    ssh.exec("ls -l /");

    //  test scpTo
//        boolean suc = ssh.scpTo("D:\\备份\\02_java\\jdk\\jdk-8u60-windows-x64.exe", "/root/tool/");
//        System.out.println("suc = " + suc);

//        boolean suc = ssh.scpFrom("/root/tool/redis/redis-windows-latest.zip", "d:\\tmp\\");
//        System.out.println("suc = " + suc);
//        boolean suc2 = ssh.scpFrom("/root/tool/redis/redis-windows-latest.zip", "d:\\tmp\\aaaa.zip");
//        System.out.println("suc2 = " + suc2);
    ssh.disconnect();
  }
}
