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
   * 指定パッケージあるいはクラスを最長一致で取得する。
   * <p>
   * "com.cm55.depDetect"であればパッケージノードが、"com.cm55.depDetect.PkgNode"であればクラスノードを返す。
   * ただし、完全一致でなくてもよい。例えば"com.cm55.foobar"が与えられた場合は、"com.cm55"のパッケージノードが返される。
   * 当然ながら、"org.foobar.somthing"が与えられた場合には、ルートノード（""）が返される。
   * </p>
   * @param path パス
   * @return 見つかったパッケージノードあるいはクラスノード。nullになることは無い。
   */
  public Node findMaximum(String path);

  /** 
   * 指定パッケージあるいはクラスを完全一致で取得する。
   * <p>
   * "com.cm55.depDetect"であればパッケージノードが、"com.cm55.depDetect.PkgNode"であればクラスノードを返す。
   * 完全一致するものがなければnullが返される。
   * </p>
   * @param path パス
   * @return 見つかったパッケージノードあるいはクラスノード。見つからなかった場合にはnullを返す。
   */
  public Node findExact(String path);
  
  /** 
   * このノード以下のノードをすべて訪問する 
   * @param order 木構造探索順序
   * @param visitor 訪問時コールバック
   */
  public void visit(VisitOrder order, Consumer<Node> visitor);

  /** 
   * このノード以下のすべてのパッケージノードを訪問する
   * @param order 木構造探索順序
   * @param visitor 訪問時コールバック
   */
  public void visitPackages(VisitOrder order, Consumer<PkgNode>visitor);
    
  /** 
   * このノード以下のすべてのクラスノードを訪問する
   * @param visitor 訪問時コールバック
   */
  public void visitClasses(Consumer<ClsNode>visitor);
  
  public String treeString();
}
