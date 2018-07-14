package com.cm55.depDetect;

import java.util.function.*;

/**
 * パッケージノード
 * @author ysugimura
 */
public interface PkgNode extends Node {

  /** 依存パッケージノード集合を取得する */
  public Refs getDepsTo();

  /** 不明依存ノード集合を取得する */
  public Unknowns getUnknowns();
  
  /** 被依存パッケージノード集合を取得する */
  public Refs getDepsFrom();
  
  /** 循環依存集合を取得する */
  public Refs getCyclics();
  
  /** このノード以下の不明依存ノード集合を取得する */
  public Unknowns getAllUnknowns();
  
  /** 
   * 指定パッケージあるいはクラスを取得する
   * "com.cm55.depDetect"であればパッケージノードが、"com.cm55.depDetect.PkgNode"であればクラスノードを返す。
   * 
   * @param path
   * @return
   */
  public Node findMaximum(String path);
  
  public Node findExact(String path);
  
  /** このノード以下のノードをすべて訪問する 
   * @param order TODO*/
  public void visit(VisitOrder order, Consumer<Node> visitor);
  
  /** このノード以下のすべてのクラスノードを訪問する 
   * @param order TODO*/
  public void visitClasses(VisitOrder order, Consumer<ClsNode>visitor);

  /** このノード以下のすべてのパッケージノードを訪問する 
   * @param order TODO*/
  public void visitPackages(VisitOrder order, Consumer<PkgNode>visitor);
}
