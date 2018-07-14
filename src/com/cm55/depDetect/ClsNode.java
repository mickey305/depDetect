package com.cm55.depDetect;

/**
 * クラスノード
 * 
 * Javaのパッケージツリー構造中でクラスを表すノード。
 * <p>
 * このシステムの目的としては、importによる依存関係を把握することにあるので、外部クラスの表現にとどまり
 * 内部クラスは一切扱われない。
 * </p>
 * @author ysugimura
 */
public interface ClsNode extends Node {

  /** このクラスが依存するパッケージ集合を得る */
  public Refs getDepsTo();

  /** このクラスが依存する不明import文字列集合を得る */
  public Unknowns getUnknowns();
  
}
