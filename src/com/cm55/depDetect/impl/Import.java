package com.cm55.depDetect.impl;

public class Import  {

  /**
   * フルのimportパス
   * com.cm55.*, com.cm55.SampleClass.*等
   */
  public final String fullPath;
  
  /** static参照 */
  public final boolean statical;
  
  /** 
   * このimportが参照するパッケージ。
   * import自体がクラスを参照していても、ここではパッケージを取得する。
   */
  PkgNodeImpl pkgNode;
  
  /** import名称とstaticフラグを指定する */
  public Import(String fullPath, boolean statical) {
    this.fullPath = fullPath;
    this.statical = statical;
  }
  
  @Override
  public String toString() {
    return fullPath + "," + statical;
  }
  
  /** ルートノードを指定し、このimportのノードを取得する */
  public void setNode(PkgNodeImpl root) {

    // nullが返されることは無い
    NodeImpl foundNode = root.findNode(fullPath);
    
    // クラスノードが見つかった場合。クラスノードの親のパッケージノードを設定する
    if (foundNode instanceof ClsNodeImpl) {
      pkgNode = foundNode.parent;
      return;
    }
    
    // パッケージノードの場合。見つかったノードのパスを差し引き、残りが".*"の場合にのみOK
    String foundPath = foundNode.toString();
    String restPath = fullPath.substring(foundPath.length());
    if (restPath.equals(".*")) {
      pkgNode = (PkgNodeImpl)foundNode;
      return;      
    }
  }
  
  /**
   * このimport文の依存パッケージを求める
   * @param thisPkg このimport文が存在するクラスのパッケージ
   * @return 依存パッケージ、もしくはnull（不明）
   */
  public PkgNodeImpl getDependency(PkgNodeImpl thisPkg) {

    // ルートノードを取得する
    PkgNodeImpl root = thisPkg.getRoot();
    
    // nullが返されることは無い
    NodeImpl foundNode = root.findNode(fullPath);
    
    // クラスノードが見つかった場合。クラスノードの親のパッケージノードを設定する
    if (foundNode instanceof ClsNodeImpl) {
      if (foundNode.parent == thisPkg) return null;
      return foundNode.parent;
    }
    
    // パッケージノードの場合。見つかったノードのパスを差し引き、残りが".*"の場合にのみOK
    if (foundNode == thisPkg) return null;
    String foundPath = foundNode.toString();
    String restPath = fullPath.substring(foundPath.length());
    if (restPath.equals(".*")) {
      return (PkgNodeImpl)foundNode;
    }
    
    return null;
  }
}
