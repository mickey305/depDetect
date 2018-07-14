package com.cm55.depDetect.impl;

import com.cm55.depDetect.*;

/**
 * パッケージあるいはクラスのノード
 * 下位の{@link PkgNodeImpl}がパッケージを表し、{@link ClsNodeImpl}がクラスを表す。
 * @author ysugimura
 */
public abstract class NodeImpl implements Comparable<NodeImpl>, Node {

  /** 上位パッケージ */
  public final PkgNodeImpl parent;

  @Override
  public PkgNodeImpl getParent() { return parent; }
  
  /** パッケージあるいはクラスの名称。"com", "SampleClass"等になる */
  public final String name;

  @Override
  public String getName() { return name; }
  
  /** 上位パッケージ、名称を指定する */
  protected NodeImpl(PkgNodeImpl parent, String name) {
    this.parent = parent;
    this.name = name;
  }
  
  /** 上位パッケージ、名称のチェック */
  protected void check() {
    if (parent == null || name == null) throw new NullPointerException();
    if (name.length() == 0) throw new IllegalArgumentException("no name");
  }
  
  /** このノードのフルパス文字列を取得する */
  @Override
  public String toString() {
    return getPath();
  }

  @Override
  public String getPath() {
    if (parent == null) return name;
    String parentPath = parent.toString();
    if (parentPath.length() == 0) return name;
    return parentPath + "." + name;
  }

  /** 
   * ソート用。
   * パッケージノード、クラスノード混在の場合には、パッケージを先とする。
   * それぞれの中でパス順でソートする。
   */
  @Override
  public int compareTo(NodeImpl that) {  
    int r = this.getKind().ordinal() - that.getKind().ordinal();
    if (r != 0) return r;
    return this.getPath().compareTo(that.getPath());
  }
  
  
  /** ルートノードを取得する */
  public PkgNodeImpl getRoot() {
    if (parent == null) return (PkgNodeImpl)this;
    return parent.getRoot();
  }
}
