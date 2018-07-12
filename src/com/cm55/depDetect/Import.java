package com.cm55.depDetect;

public class Import {

  public final String name;
  public final boolean statical;
  
  public Import(String name, boolean statical) {
    this.name = name;
    this.statical = statical;
  }

  @Override
  public String toString() {
    return name + "," + statical;
  }
}
