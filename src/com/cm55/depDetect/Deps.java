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
  
  public void add(Deps that) {
    this.set.addAll(that.set);
  }
  
  public static class Builder {
    Set<PkgNode>set = new HashSet<>();
    public void add(Deps that) {
      this.set.addAll(that.set);
    }
    public void add(PkgNode pkgNode) {
      set.add(pkgNode);
    }
    public Deps build() {
      return new Deps(set);
    }
  }
}
