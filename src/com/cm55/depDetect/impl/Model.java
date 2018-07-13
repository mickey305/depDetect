package com.cm55.depDetect.impl;

import java.util.*;

public class Model {

  private PkgNodeImpl root;
  private Set<String>noNodeSet;
  
  public Model(PkgNodeImpl root, Set<String>noNodeSet) {
    this.root = root;
    this.noNodeSet = noNodeSet;
  }
}
