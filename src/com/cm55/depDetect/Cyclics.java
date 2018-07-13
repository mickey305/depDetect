package com.cm55.depDetect;

import java.util.*;
import java.util.stream.*;

/**
 * 循環依存を表す
 * <p>
 * </p>
 * @author ysugimura
 */
public class Cyclics {

  Set<PkgNode>set;
  
  private Cyclics(Set<PkgNode>set) {
    this.set = set;
  }
  
  public int count() {
    return set.size();
  }
  
  @Override
  public String toString() {
    return set.stream().map(pkgNode->pkgNode.toString()).collect(Collectors.joining("\n"));
  }

  /** 循環依存ビルダ */
  public static class Builder {
    Set<PkgNode>set = new HashSet<>();
    public void add(PkgNode pkgNode) {
      set.add(pkgNode);
    }
    public Cyclics build() {
      return new Cyclics(set);
    }
  }
}
