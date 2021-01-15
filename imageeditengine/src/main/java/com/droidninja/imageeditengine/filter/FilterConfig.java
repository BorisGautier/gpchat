package com.droidninja.imageeditengine.filter;


public class FilterConfig {
  public int opacity;
  public int type;

  public FilterConfig(int position, int opacity) {
    this.opacity = opacity;
    this.type = position;
  }
}
