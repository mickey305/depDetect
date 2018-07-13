package com.cm55.depDetect.impl;

import java.util.*;
import java.util.stream.*;

import com.cm55.depDetect.*;

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
  
  /** 各{@link Import}に{@link NodeImpl}を設定する　*/
  public Set<String> setNode(PkgNodeImpl root) {
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
  public boolean contains(PkgNodeImpl node) {
    return stream().map(i->i.pkgNode == node).findAny().orElse(false);
  }
  
  
  public Deps createDependencies(PkgNodeImpl thisPkg) {
    Deps.Builder builder = new Deps.Builder();
    stream().forEach(imp-> {
      PkgNodeImpl dependency = imp.getDependency(thisPkg);
      if (dependency != null) builder.add(dependency);
    });
    return builder.build();
  }
  
}
