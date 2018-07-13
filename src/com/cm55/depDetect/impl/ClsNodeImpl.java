package com.cm55.depDetect.impl;

import com.cm55.depDetect.*;

/**
 * クラスノード
 * @author ysugimura
 */
public class ClsNodeImpl extends NodeImpl implements ClsNode {
 
  /** import文配列 */
  public final Imports imports;

  /** 依存パッケージ集合 */
  private Deps deps;

  /**
   * 親ノード、クラス名称、import文配列を指定する
   * @param parent このクラスノードの親となるパッケージノード
   * @param name クラス名称
   * @param imports import文配列
   */
  public ClsNodeImpl(PkgNodeImpl parent, String name, Imports imports) {
    super(parent, name);
    check();
    this.imports = imports;
  }   

  public Deps getDeps() {
    return deps;
  }
  
  /** 依存を構築し、自身に格納する */
  Deps buildDeps() {
    return deps = imports.createDependencies(this.parent);
  }
  
  /** すべてのノードを訪問する場合 */
  @Override
  public void visit(Visitor<NodeImpl>visitor) {    
    visitor.visited(this);
  }
  
  /** クラスノードのみを訪問する場合 */
  @Override
  public void visitClasses(Visitor<ClsNodeImpl>visitor) {    
    visitor.visited(this);
  }

  /** パッケージノードのみを訪問する場合 */
  @Override
  public void visitPackages(Visitor<PkgNodeImpl>visitor) {
    // nop
  }
}
