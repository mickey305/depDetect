package com.cm55.depDetect.impl;

import java.util.*;

public class VarImports implements Imports {

  Set<String>set = new HashSet<>();
  
  public VarImports() {
    // TODO Auto-generated constructor stub
  }
  
  void add(String imp) {
    set.add(imp);
  }

}
