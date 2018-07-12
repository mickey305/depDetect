package com.cm55.depDetect;

/**
 * クラスノード
 * @author ysugimura
 */
public class ClsNode extends Node {
 
  public final Imports imports;
  
  public ClsNode(PkgNode parent, String name, Imports imports) {
    super(parent, name);
    this.imports = imports;
  }
  
 
}
