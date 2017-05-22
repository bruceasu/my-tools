package asu.tool.gui;

public class MockHttpServerBean {
  private String port;
  private String verticleDir;

  public MockHttpServerBean() {
  }

  public String getPort() {
    return port;
  }

  public void setPort(final String port) {
    this.port = port;
  }

  public String getVerticleDir() {
    return verticleDir;
  }

  public void setVerticleDir(final String verticleDir) {
    this.verticleDir = verticleDir;
  }
}