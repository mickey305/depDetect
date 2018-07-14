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
    if (parent == null) return name;
    String parentPath = parent.toString();
    if (parentPath.length() == 0) return name;
    return parentPath + "." + name;
  }

  /** 名称でのソート用 */
  @Override
  public int compareTo(NodeImpl o) {
    return this.name.compareTo(o.name);
  }
  
  
  /** ルートノードを取得する */
  public PkgNodeImpl getRoot() {
    if (parent == null) return (PkgNodeImpl)this;
    return parent.getRoot();
  }
  
  /** このノード以下のノードをすべて訪問する */
  public abstract void visit(Visitor<Node> visitor);
  
  public abstract void visitClasses(Visitor<ClsNode>visitor);
  
  public abstract void visitPackages(Visitor<PkgNode>visitor);
  


}
