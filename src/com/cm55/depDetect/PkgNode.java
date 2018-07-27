package com.cm55.depDetect;

import java.util.function.*;
import java.util.stream.*;

/**
 * パッケージノード
 * @author ysugimura
 */
public interface PkgNode extends JavaNode {

  /** 
   * 不明import文集合を取得する 
   * descend=trueの場合はこのパッケージノード以下のすべて
   * @param descend true:このパッケージ以下すべて、false:このパッケージのみ
   * @return 不明import文集合
   */
  public Unknowns getUnknowns(boolean descend);
  
  /** 依存パッケージノード集合を取得する
   * descend=trueの場合はこのパッケージノード以下のすべて
   * @param descend true:このパッケージ以下すべて、false:このパッケージのみ
   * @return 依存パッケージ集合
   */
  public Refs getDepsTo(boolean descend);
  
  /** 被依存パッケージノード集合を取得する
   * descend=trueの場合はこのパッケージノード以下のすべて
   * @param descend true:このパッケージ以下すべて、false:このパッケージのみ
   * @return 被依存パッケージノード集合
   */
  public Refs getDepsFrom(boolean descend);
  
  /** 循環依存集合を取得する
   * descend=trueの場合はこのパッケージノード以下のすべて
   * @param descend true:このパッケージ以下すべて、false:このパッケージのみ
   * @return 循環依存集合
   */
  public Refs getCyclics(boolean descend);
  
  /** 
   * このパッケージ下のすべてのクラスノードのストリームを返す。
   * descend=falseのときはパッケージ直下のクラスのみ、trueのときは祖先のクラスまでリストされる。
   * 名前順にソートされている。
   */
  public Stream<ClsNode>childClasses(boolean descend);

  /**
   * 
   * @param descend
   * @return
   */
  public Stream<PkgNode>childPackages(boolean descend);
  
  /**
   * descend=falseのときは、このノード直下のノード数を返す。このパッケージノードは含まない。
   * trueのときは、このノード以下すべてのノード数を返す。
   * @param descend
   * @return
   */
  public int childNodeCount(boolean descend);
  
  /**
   * descend=falseのときは、このノード直下のクラス数を返す。
   * trueのときは、このノード以下のすべてのクラス数を返す。
   * @return
   */
  public int childClassCount(boolean descend);
  
  /**
   * descend=falseのときは、このノード直下のパッケージノード数を返す。このパッケージノードは含まない。
   * trueのときは、このノード以下すべてのパッケージノード数を返す。
   * @return
   */
  public int childPackageCount(boolean descend);

  
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
  public JavaNode findMaximum(String path);

  /** 
   * 指定パッケージあるいはクラスを完全一致で取得する。
   * <p>
   * "com.cm55.depDetect"であればパッケージノードが、"com.cm55.depDetect.PkgNode"であればクラスノードを返す。
   * 完全一致するものがなければnullが返される。
   * </p>
   * @param path パス
   * @return 見つかったパッケージノードあるいはクラスノード。見つからなかった場合にはnullを返す。
   */
  public JavaNode findExact(String path);
  
  /** 
   * このノード以下のノードをすべて訪問する 。このパッケージノードも含む。
   * @param order 木構造探索順序
   * @param visitor 訪問時コールバック
   */
  public void visit(VisitOrder order, Consumer<JavaNode> visitor);
  
  /** 
   * このノード以下のすべてのパッケージノードを訪問する。このパッケージノードも含む。
   * @param order 木構造探索順序
   * @param visitor 訪問時コールバック
   */
  public void visitPackages(VisitOrder order, Consumer<PkgNode>visitor);

  
  /** 木構造文字列を取得する */
  public String treeString();
  
  /** このノードが、指定ノードと同じかあるいは祖先か */
  public boolean isAscendOf(PkgNode node);
}
