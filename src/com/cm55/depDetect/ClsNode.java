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

  /** このクラスの依存先パッケージ集合を得る */
  public Refs getDeps();
}
