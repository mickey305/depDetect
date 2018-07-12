package com.cm55.depDetect;

import java.util.*;
import java.util.stream.*;

/**
 * クラスあるいはパッケージが依存するパッケージの集合
 * @author ysugimura
 */
public class Deps {

  Set<PkgNode>set;
  
  public Deps(Set<PkgNode>set) {
    this.set = set;
  }
  
  public boolean contains(PkgNode node) {
    return set.contains(node);
  }
  
  public Stream<PkgNode>stream() {
    return set.stream();
  }
}
