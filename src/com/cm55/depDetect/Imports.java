package com.cm55.depDetect;

import java.util.*;

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

  /** 各{@link Import}に{@link Node}を設定する　*/
  public Set<String> setNode(PkgNode root) {
    Set<String>noNodeSet = new HashSet<String>();
    for (Import imp: imports) {
      imp.setNode(root);
      if (imp.pkgNode == null) {
        noNodeSet.add(imp.pkgPath + "," + imp);
      }
    }
    return noNodeSet;
  }
}
