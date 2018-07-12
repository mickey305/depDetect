package com.cm55.depDetect;

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
  PkgNode pkgNode;
  
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
  public void setNode(PkgNode root) {

    // nullが返されることは無い
    Node foundNode = root.findNode(fullPath);
    
    // クラスノードが見つかった場合。クラスノードの親のパッケージノードを設定する
    if (foundNode instanceof ClsNode) {
      pkgNode = foundNode.parent;
      return;
    }
    
    // パッケージノードの場合。見つかったノードのパスを差し引き、残りが".*"の場合にのみOK
    String foundPath = foundNode.toString();
    String restPath = fullPath.substring(foundPath.length());
    if (restPath.equals(".*")) {
      pkgNode = (PkgNode)foundNode;
      return;      
    }
  }
  
  /**
   * このimport文の依存パッケージを求める
   * @param thisPkg このimport文が存在するクラスのパッケージ
   * @return 依存パッケージ、もしくはnull（不明）
   */
  public PkgNode getDependency(PkgNode thisPkg) {

    PkgNode root = thisPkg.getRoot();
    
    // nullが返されることは無い
    Node foundNode = root.findNode(fullPath);
    
    // クラスノードが見つかった場合。クラスノードの親のパッケージノードを設定する
    if (foundNode instanceof ClsNode) {
      if (foundNode.parent == thisPkg) return null;
      return foundNode.parent;
    }
    
    // パッケージノードの場合。見つかったノードのパスを差し引き、残りが".*"の場合にのみOK
    if (foundNode == thisPkg) return null;
    String foundPath = foundNode.toString();
    String restPath = fullPath.substring(foundPath.length());
    if (restPath.equals(".*")) {
      return (PkgNode)foundNode;
    }
    
    return null;
  }
}
