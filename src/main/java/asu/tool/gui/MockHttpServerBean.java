package asu.tool.gui;

import lombok.Data;

@Data
public class MockHttpServerBean {
  private String path;
  private String mockAction;
  private boolean replace; // mode
  private String type;
  private String respDelay;
  private String data;

}

