package com.cm55.depDetect.impl;

import java.util.*;
import java.util.stream.*;

public class VarImports implements Imports {

  Set<String>set = new HashSet<>();
  
  public VarImports() {
    // TODO Auto-generated constructor stub
  }
  
  void add(String imp) {
    set.add(imp);
  }
  
  Stream<String>stream() {
    return set.stream().sorted();
  }

  /** このimport文配列による依存パッケージ集合を作成する */
  @Override
  public ClsDeps createDependencies(PkgNodeImpl thisPkg) {
    PkgNodeImpl root = thisPkg.getRoot();
    RefsImpl depends = new RefsImpl();
    UnknownsImpl unknowns = new UnknownsImpl();
    stream().forEach(imp-> {
      PkgNodeImpl node = (PkgNodeImpl)root.findExact(imp);
      if (node == thisPkg) return;
      if (node != null) depends.add(node);
      unknowns.add(imp);
    });
    return new ClsDeps(depends, unknowns);
  } 
  
}
