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

public class GUIDataBean {
  private String mqHost = "192.168.35.62";
  private String mqUsername = "devuser";
  private String mqPort = "5672";
  private String mqPassword = "devuser";
  private String mqVhost = "/";
  private String mqTestTimes = "10";

  private String zkConnStr = "192.168.35.61:2181,192.168.35.62:2181,192.168.35.63:2181";
  private String zkTestTimes = "10";

  private String redisHost = "127.0.0.1";
  private String redisPort = "6379";
  private String redisTestTime = "10";


  public String getMqHost() {
    return mqHost;
  }

  public void setMqHost(final String mqHost) {
    this.mqHost = mqHost;
  }

  public String getMqUsername() {
    return mqUsername;
  }

  public void setMqUsername(final String mqUsername) {
    this.mqUsername = mqUsername;
  }

  public String getMqPort() {
    return mqPort;
  }

  public int getMqPortInt() {
    return Integer.parseInt(mqPort);
  }

  public void setMqPort(final String mqPort) {
    this.mqPort = mqPort;
  }


  public String getMqPassword() {
    return mqPassword;
  }

  public void setMqPassword(final String mqPassword) {
    this.mqPassword = mqPassword;
  }

  public String getMqVhost() {
    return mqVhost;
  }

  public void setMqVhost(final String mqVhost) {
    this.mqVhost = mqVhost;
  }

  public String getMqTestTimes() {
    return mqTestTimes;
  }

  public int getMqTestTimesInt() {
    return Integer.parseInt(mqTestTimes);
  }

  public void setMqTestTimes(String mqTestTimes) {
    this.mqTestTimes = mqTestTimes;
  }

  public String getZkConnStr() {
    return zkConnStr;
  }

  public void setZkConnStr(String zkConnStr) {
    this.zkConnStr = zkConnStr;
  }

  public String getZkTestTimes() {
    return zkTestTimes;
  }

  public int getZkTestTimesInt() {
    return Integer.parseInt(zkTestTimes);
  }

  public void setZkTestTimes(String zkTestTimes) {
    this.zkTestTimes = zkTestTimes;
  }

  public String getRedisHost() {
    return redisHost;
  }

  public void setRedisHost(String redisHost) {
    this.redisHost = redisHost;
  }

  public String getRedisPort() {
    return redisPort;
  }

  public int getRedisPortInt() {
    return Integer.parseInt(redisPort);
  }

  public void setRedisPort(String redisPort) {
    this.redisPort = redisPort;
  }

  public String getRedisTestTime() {
    return redisTestTime;
  }

  public int getRedisTestTimeInt() {
    return Integer.parseInt(redisTestTime);
  }

  public void setRedisTestTime(String redisTestTime) {
    this.redisTestTime = redisTestTime;
  }
}