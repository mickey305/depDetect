package com.cm55.depDetect;

import java.util.*;
import java.util.stream.*;

/**
 * クラスのimport一覧
 * @author ysugimura
 */
public class Imports {

  /** 全importの配列 */
  public final Import[]imports;

  /** import無し */
  public Imports() {
    imports = new Import[0];
  }

  /** {@link Import}配列を与える */
  public Imports(Import[]imports) {
    this.imports = imports;
  }

  /** 全{@link Import}をストリームとして取得する */
  public Stream<Import>stream() {
    return Arrays.stream(imports);
  }
  
  /** 各{@link Import}に{@link Node}を設定する　*/
  public Set<String> setNode(PkgNode root) {
    Set<String>noNodeSet = new HashSet<String>();
    stream().forEach(imp-> {
      imp.setNode(root);
      if (imp.pkgNode == null) {
        noNodeSet.add(imp.toString());
      }
    });
    return noNodeSet;
  }
  
  /** このimport一覧に指定パッケージノードが含まれるか */
  public boolean contains(PkgNode node) {
    return stream().map(i->i.pkgNode == node).findAny().orElse(false);
  }
  
  
  public Deps createDependencies(PkgNode thisPkg) {
    Set<PkgNode>set = new HashSet<>();
    stream().forEach(imp-> {
      PkgNode dependency = imp.getDependency(thisPkg);
      if (dependency != null) set.add(dependency);
    });
    return new Deps(set);
  }
  
}
