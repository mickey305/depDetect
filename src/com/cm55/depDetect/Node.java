package com.cm55.depDetect;

public interface Node {

  /** 名称を取得する */
  public String getName();
  
  /** ノード種類を取得する */
  public NodeKind getKind();
  
  /** ツリー構造のルートを取得する */
  public PkgNode getRoot();
  
  /** このノードの親パッケージを得る */
  public PkgNode getParent();
  

}
