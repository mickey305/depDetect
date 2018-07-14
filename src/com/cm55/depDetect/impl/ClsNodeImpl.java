package com.cm55.depDetect.impl;

import com.cm55.depDetect.*;

/**
 * クラスノード
 * @author ysugimura
 */
public class ClsNodeImpl extends NodeImpl implements ClsNode {
 
  /** import文配列 */
  public final Imports imports;

  /** このクラスが依存するパッケージノード集合 */
  private ClsDeps depsTo;

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

  public ClsDeps getDepsTo() {
    return depsTo;
  }
  
  /** 依存を構築し、自身に格納する */
  ClsDeps buildDeps() {
    return depsTo = imports.createDependencies(this.parent);
  }
  
  /** すべてのノードを訪問する場合 */
  @Override
  public void visit(Visitor<Node>visitor) {    
    visitor.visited(this);
  }
  
  /** クラスノードのみを訪問する場合 */
  @Override
  public void visitClasses(Visitor<ClsNode>visitor) {    
    visitor.visited(this);
  }

  /** パッケージノードのみを訪問する場合 */
  @Override
  public void visitPackages(Visitor<PkgNode>visitor) {
    // nop
  }
}
