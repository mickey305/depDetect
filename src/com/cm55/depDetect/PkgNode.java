package com.cm55.depDetect;

public interface PkgNode extends Node {

  /** 依存集合を取得する */
  public Refs getDeps();
  
  /** 循環依存集合を取得する */
  public Refs getCyclics();
  
  
}
