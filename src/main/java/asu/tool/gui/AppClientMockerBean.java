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

public class AppClientMockerBean {
  public String clientGatewayHost = "127.0.0.1";
  public String clientGatewayPort = "7001";
  public boolean clientGatewayMsgCompressable = false;
  public boolean clientGatewayMsgEncryptable = true;
  public String apiAccountKey = "1";
  public String companyId = "1";
  public String apiAccount = "1";
  private String clientGatewaySendData;


  public String getClientGatewaySendData() {
    return clientGatewaySendData;
  }

  public void setClientGatewaySendData(String clientGatewaySendData) {
    this.clientGatewaySendData = clientGatewaySendData;
  }

  public String getClientGatewayHost() {
    return clientGatewayHost;
  }

  public void setClientGatewayHost(String clientGatewayHost) {
    this.clientGatewayHost = clientGatewayHost;
  }

  public String getClientGatewayPort() {
    return clientGatewayPort;
  }

  public int getClientGatewayPortInt() {
    return Integer.parseInt(clientGatewayPort);
  }

  public void setClientGatewayPort(String clientGatewayPort) {
    this.clientGatewayPort = clientGatewayPort;
  }

  public boolean isClientGatewayMsgCompressable() {
    return clientGatewayMsgCompressable;
  }


  public void setClientGatewayMsgCompressable(boolean clientGatewayMsgCompressable) {
    this.clientGatewayMsgCompressable = clientGatewayMsgCompressable;
  }

  public boolean isClientGatewayMsgEncryptable() {
    return clientGatewayMsgEncryptable;
  }


  public void setClientGatewayMsgEncryptable(boolean clientGatewayMsgEncryptable) {
    this.clientGatewayMsgEncryptable = clientGatewayMsgEncryptable;
  }

  public String getApiAccountKey() {
    return apiAccountKey;
  }

  public void setApiAccountKey(String apiAccountKey) {
    this.apiAccountKey = apiAccountKey;
  }

  public String getCompanyId() {
    return companyId;
  }

  public int getCompanyIdInt() {
    return Integer.parseInt(companyId);
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public String getApiAccount() {
    return apiAccount;
  }

  public void setApiAccount(String apiAccount) {
    this.apiAccount = apiAccount;
  }

}
