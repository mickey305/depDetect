package com.cm55.depDetect;

public interface PkgNode extends Node {

  /** 依存パッケージノード集合を取得する */
  public Refs getDepsTo();

  /** 不明依存ノード集合を取得する */
  public Unknowns getUnknowns();
  
  /** 被依存パッケージノード集合を取得する */
  public Refs getDepsFrom();
  
  /** 循環依存集合を取得する */
  public Refs getCyclics();
  
  
}
