package com.cm55.depDetect;

import java.util.*;
import java.util.stream.*;

public class Cyclics {

  Set<PkgNode>set;
  
  public Cyclics(Set<PkgNode>set) {
    this.set = set;
  }
  
  public int count() {
    return set.size();
  }
  
  @Override
  public String toString() {
    return set.stream().map(pkgNode->pkgNode.toString()).collect(Collectors.joining("\n"));
  }
}
