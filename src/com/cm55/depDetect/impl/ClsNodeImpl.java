package com.cm55.depDetect.impl;

import com.cm55.depDetect.*;

/**
 * クラスノード
 * @author ysugimura
 */
public class ClsNodeImpl extends JavaNodeImpl implements ClsNode {
 
  /** import文配列 */
  Imports imports;

  /** このクラスが依存するパッケージノード集合 */
  RefsImpl depsTo;

  /** このクラスが依存する不明import文集合 */
  UnknownsImpl unknowns;
  
  /**
   * 親ノード、クラス名称、import文配列を指定する
   * @param parent このクラスノードの親となるパッケージノード
   * @param name クラス名称
   * @param imports import文配列
   */
  public ClsNodeImpl(PkgNodeImpl parent, String name) {
    super(parent, name);
    check();
  }   

  @Override
  public JavaNodeKind getKind() {
    return JavaNodeKind.CLASS;
  }
  
  @Override
  public RefsImpl getDepsTo() {
    return depsTo;
  }

  @Override
  public UnknownsImpl getUnknowns() {
    return unknowns;
  }

}
