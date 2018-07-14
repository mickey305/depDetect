package com.cm55.depDetect;

import java.util.stream.*;

/**
 * クラスあるいはパッケージが依存するパッケージの集合
 * <p>
 * このクラスは様々な場所に使用される。
 * </p>
 * <ul>
 * <li>{@link ClsNode#getDepsTo()}：そのクラスが依存するパッケージ集合
 * <li>{@link PkgNode#getDepsTo()}：そのパッケージ「が」依存するパッケージ集合（そのパッケージ下の全クラスの依存の和集合)
 *　<li>{@link PkgNode#getDepsFrom()}：そのパッケージ「に」依存するパッケージの集合。
 * <li>{@link PkgNode#getCyclics()}：そのパッケージが相互参照を起こしているパッケージの集合
 * </ul>
 * @author ysugimura
 */
public interface Refs {

  /** パッケージ一覧ストリームを取得する。パス順にソートされる */
  public Stream<PkgNode>stream();

  /** 
   * 指定された{@link PkgNode}を含むかを返す
   * @param node
   * @return
   */
  public boolean contains(PkgNode node);

  /**
   * このパッケージ集合の中に、指定されたパッケージ集合のうちの一つでも存在するか
   * @param refs
   * @return
   */
  public boolean containsAny(Refs refs);
  
  /** この中に含まれるパッケージの数 */
  public int count();
}
