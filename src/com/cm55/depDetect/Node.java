package com.cm55.depDetect;

/**
 * パッケージあるいはクラスを表すノード
 * @author ysugimura
 */
public interface Node {

  /** 
   * 名称を取得する。"com"、"Sample"など
   * @return ノード名称
   */
  public String getName();

  /** 
   * パスを取得する。"com.cm55.depDetect", "com.cm55.depDetect.Node"など
   * @return　ノードパス
   */
  public String getPath();
  
  /** 
   * ノード種類を取得する。
   * @return {@link NodeKind}
   */
  public NodeKind getKind();
  
  /** 
   * このノードが所属するツリー構造のルートを取得する
   * @return このノードが所属するツリー構造のルート
   */
  public PkgNode getRoot();
  
  /** 
   * このノードの親パッケージを得る。ルートノードの場合はnullが返される
   * @return 親パッケージあるいはnull
   */
  public PkgNode getParent();
}
