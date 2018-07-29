package com.cm55.depDetect.impl;

import java.util.*;
import java.util.stream.*;

import com.cm55.depDetect.impl.Import.*;

/**
 * クラスのimport一覧
 * @author ysugimura
 */
class BulkImports implements Imports {

  /** 全importの配列 */
  public final Import[]imports;

  /** import無し */
  BulkImports() {
    imports = new Import[0];
  }

  /** {@link Import}配列を与える */
  BulkImports(Import[]imports) {
    this.imports = imports;
  }

  /** 全{@link Import}をストリームとして取得する */
  Stream<Import>stream() {
    return Arrays.stream(imports);
  }
  
  /** このimport文配列による依存パッケージ集合を作成する */
  ClsDeps createDependencies(PkgNodeImpl thisPkg) {
    RefsImpl depends = new RefsImpl();
    UnknownsImpl unknowns = new UnknownsImpl();
    stream().forEach(imp-> {
      ImportDependency impDep = imp.getDependency(thisPkg);
      if (impDep.selfRef) return;
      if (impDep.found != null) depends.add(impDep.found);
      if (impDep.notFound != null) unknowns.add(impDep.notFound);
    });
    return new ClsDeps(depends, unknowns);
  }  
}
