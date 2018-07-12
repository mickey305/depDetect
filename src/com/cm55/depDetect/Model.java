package com.cm55.depDetect;

import java.util.*;

public class Model {

  private PkgNode root;
  private Set<String>noNodeSet;
  
  public Model(PkgNode root, Set<String>noNodeSet) {
    this.root = root;
    this.noNodeSet = noNodeSet;
  }
}
