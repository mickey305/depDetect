package com.cm55.depDetect;

public class Import  {

  /**
   * フルのimportパス
   * com.cm55.*, com.cm55.SampleClass.*等
   */
  public final String fullPath;

  /** パッケージ部分のみのパス。"com.cm55"等 */
  public final String pkgPath;
  
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
    this.pkgPath = getPkgPath(fullPath, statical);
  }
  
  static String getPkgPath(String fullPath, boolean statical) {
    int omitCount = 1;
    if (statical) omitCount++;
    String temp = fullPath;
    for (int i = 0; i < omitCount; i++)
      temp = temp.substring(0, temp.lastIndexOf("."));
    return temp;
  }
  
  @Override
  public String toString() {
    return fullPath + "," + statical;
  }
  
  /** ルートノードを指定し、このimportのノードを取得する */
  public void setNode(PkgNode root) {
    pkgNode = root.findPackage(pkgPath);
  }
}
