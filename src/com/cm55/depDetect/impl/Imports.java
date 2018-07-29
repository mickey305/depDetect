package com.cm55.depDetect.impl;

public interface Imports {

  /** このimport文配列による依存パッケージ集合を作成する */
  ClsDeps createDependencies(PkgNodeImpl thisPkg);
}
