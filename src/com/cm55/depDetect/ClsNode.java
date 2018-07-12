package com.cm55.depDetect;

/**
 * クラスノード
 * @author ysugimura
 */
public class ClsNode extends Node {
 
  /** import文配列 */
  public final Imports imports;

  /** 依存パッケージ集合 */
  private Deps dependencies;
  
  /**
   * 親ノード、クラス名称、import文配列を指定する
   * @param parent このクラスノードの親となるパッケージノード
   * @param name クラス名称
   * @param imports import文配列
   */
  public ClsNode(PkgNode parent, String name, Imports imports) {
    super(parent, name);
    check();
    this.imports = imports;
  } 
  
  /** {@link Import}の中に{@link PkgNode}が含まれるか */
  public boolean containsInImports(PkgNode pkgNode) {
    return imports.contains(pkgNode);
  }

  /** 依存を構築し、自身に格納する */
  public Deps buildDeps() {
    return dependencies = imports.createDependencies(this.parent);
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
