package com.cm55.depDetect.impl;

import java.util.*;
import java.util.stream.*;

import com.cm55.depDetect.*;
import com.cm55.depDetect.impl.Import.*;

/**
 * クラスのimport一覧
 * @author ysugimura
 */
class Imports {

  /** 全importの配列 */
  public final Import[]imports;

  /** import無し */
  Imports() {
    imports = new Import[0];
  }

  /** {@link Import}配列を与える */
  Imports(Import[]imports) {
    this.imports = imports;
  }

  /** 全{@link Import}をストリームとして取得する */
  Stream<Import>stream() {
    return Arrays.stream(imports);
  }
  
  /** このimport文配列による依存パッケージ集合を作成する */
  Deps createDependencies(PkgNodeImpl thisPkg) {
    Deps.Builder builder = new Deps.Builder();
    stream().forEach(imp-> {
      ImportDependency impDep = imp.getDependency(thisPkg);
      if (!impDep.selfRef && impDep.found != null) {
        builder.add(impDep.found);
      }
    });
    return builder.build();
  }  
}
