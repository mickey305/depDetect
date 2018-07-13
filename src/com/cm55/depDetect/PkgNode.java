package com.cm55.depDetect;

public interface PkgNode extends Node {

  /** 依存集合を取得する */
  public Deps getDeps();
  
  /** 循環依存集合を取得する */
  public Cyclics getCyclics();
  
  
}
