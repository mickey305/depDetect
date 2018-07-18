package com.cm55.depDetect.impl;

/**
 * 一つのimport文を保持するオブジェクト
 * @author ysugimura
 */
class Import  {

  /**
   * フルのimportパス
   * com.cm55.*, com.cm55.SampleClass.*等
   */
  final String fullPath;
  
  /** static参照。import static文の場合 */
  final boolean statical;
  
  /** import名称とstaticフラグを指定する */
  Import(String fullPath, boolean statical) {
    this.fullPath = fullPath;
    this.statical = statical;
  }
  
  /**
   * このimport文が指し示すパッケージを取得する。
   * importが仮にクラスを示していたとしても、そのクラスの所属するパッケージが取得される。
   * <p>
   * 取得されるパッケージは元々解析対象ソースとして指定されたもののみになる。
   * 一般にimport文は、字面だけをみても、パッケージを参照するのかクラスを参照するのかはわからない。例えば、以下の様な場合
   * </p>
   * <pre>
   * import com.cm55.depDetect.Const.*;
   * </pre>
   * <p>
   * Constがクラスであれば、クラス内のすべての内部クラスを指定することになるし、Constがパッケージであれば、パッケージ内のすべてのクラスとなる。
   * このように字面からは判断ができず、実際のパッケージツリーがあってはじめて判別可能になる。
   * </p>
   * 
   * @param thisPkg このimport文が存在するクラスのパッケージ
   * @return このimport文が指しします依存パッケージ、もしくはnull（不明）
   */
  ImportDependency getDependency(PkgNodeImpl thisPkg) {

    // ルートノードを取得する
    PkgNodeImpl root = thisPkg.getRoot();
    
    // ルートから、import文のフルパスを探す。nullが返されることは無い
    JavaNodeImpl foundNode = root.findMaximum(fullPath);
    
    // クラスノードが見つかった場合。クラスノードの親のパッケージノードを設定する
    if (foundNode instanceof ClsNodeImpl) {
      // 自分のパッケージへの依存は無視すること
      if (foundNode.parent == thisPkg) return new ImportDependency();
      return new ImportDependency(foundNode.parent);
    }
    
    // パッケージノードが見つかった場合。
    // 見つかったノードのパスを差し引き、残りが".*"の場合にのみ完全なパッケージが見つかったことになる。
    if (foundNode == thisPkg) return null;
    String foundPath = foundNode.toString();
    String restPath = fullPath.substring(foundPath.length());
    if (restPath.equals(".*")) {
      return new ImportDependency((PkgNodeImpl)foundNode);
    }
    
    // 上記以外の場合は見つからない。    
    return new ImportDependency(fullPath);
  }

  /** import文から取得した依存 */
  public static class ImportDependency {
    /** 自己参照フラグ */
    final boolean selfRef;
    
    /** 見つかった依存パッケージノード */
    final PkgNodeImpl found;
    
    /** 見つからなかった依存パッケージノード */
    final String notFound;
    
    ImportDependency() {
      this.selfRef = true;
      found = null;
      notFound = null;
    }
    ImportDependency(PkgNodeImpl found) {
      selfRef = false;
      this.found = found;
      notFound = null;
    }
    ImportDependency(String notFound) {
      selfRef = false;
      found = null;
      this.notFound = notFound;
    }
  }
  
  /** デバッグ用文字列化 */
  @Override
  public String toString() {
    return fullPath + "," + statical;
  }
}
