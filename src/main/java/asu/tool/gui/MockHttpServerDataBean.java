package asu.tool.gui;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MockHttpServerDataBean {
  private String path;
  private String mockAction;
  private boolean replace; // mode
  private String type;
  private String respDelay;
  private String data;

}

