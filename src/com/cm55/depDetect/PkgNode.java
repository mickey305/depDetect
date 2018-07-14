package com.cm55.depDetect;

import java.util.function.*;
import java.util.stream.*;

import com.cm55.depDetect.impl.*;

/**
 * パッケージノード
 * @author ysugimura
 */
public interface PkgNode extends Node {

  /** 不明import文集合を取得する */
  public Unknowns getUnknowns();
  
  /** 依存パッケージノード集合を取得する */
  public Refs getDepsTo();
  
  /** 被依存パッケージノード集合を取得する */
  public Refs getDepsFrom();
  
  /** 循環依存集合を取得する */
  public Refs getCyclics();
  
  /** このノード以下のすべてのノードの不明依存ノード集合の和を取得する */
  public Unknowns getAllUnknowns();
  
  /** 
   * このパッケージ直下のすべてのノードを返す。
   * パッケージノード、クラスノードが混在する。パッケージが先、クラスが後で、それぞれ名前順にソートされている。
   * {@link NodeImpl#compareTo(NodeImpl)}を参照のこと。
   */
  public Stream<Node>nodeStream();
  
  /** このパッケージ直下のすべてのクラスノードのストリームを返す。名前順にソートされている */
  public Stream<ClsNode>classStream();
  
  /** このパッケージ直下のすべてのパッケージノードのストリームを返す。名前順にソートされている */
  public Stream<PkgNode>packageStream();
    
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

  /** {@link #visit(VisitOrder, Consumer)}の訪問結果のストリームを取得する */
  public Stream<Node>visitStream(VisitOrder order);
  
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

  /** {@link #visitClasses(Consumer)}の訪問結果ストリームを取得する */
  public Stream<ClsNode>visitClassesStream();
  
  /** 木構造文字列を取得する */
  public String treeString();
}
