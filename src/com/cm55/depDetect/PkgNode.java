package com.cm55.depDetect;

import java.util.*;
import java.util.stream.*;

/**
 * パッケージを表すノード
 * @author ysugimura
 */
public class PkgNode extends Node {

  private Map<String, Node>nodeMap = new HashMap<>();
  
  /** ルートパッケージノードを作成する */
  public static PkgNode createRoot() {
    return new PkgNode();
  }
  
  /** ルートパッケージノードを作成する */
  private PkgNode() {
    super(null, "");
  }
  
  /**
   * 親パッケージ、名称を指定する
   * @param parent
   * @param name
   */
  public PkgNode(PkgNode parent, String name) {
    super(parent, name);
    check();
  }
  
  /** 
   * このパッケージノード下の、指定された名称のパッケージノードを確保する。
   * あればそれを返し、無ければ作成して返す。
   * 例えば"com.cm55"というパッケージの下の"gs"というパッケージ
   * @param name
   * @return
   */
  public PkgNode ensurePackage(String name) {
    PkgNode node = (PkgNode)nodeMap.get(name);
    if (node != null) return node;   
    node = new PkgNode(this, name);
    nodeMap.put(name,  node);
    return node;
   }
  
  /**
   * このパッケージノード下の、指定された名称のクラスを作成する。
   * 既に存在する場合は何もせずnullを返す。作成した場合はその{@link ClsNode}を返す。
   * @param name
   * @param imports
   * @return
   */
  public ClsNode createClass(String name, Imports imports) {
    ClsNode node = (ClsNode)nodeMap.get(name);
    if (node != null) return null;
    node = new ClsNode(this, name, imports);
    nodeMap.put(name,  node);
    return node;
  }

  /** このパッケージの下のすべてのノードを返す。パッケージノード、クラスノードが混在する */
  public Stream<Node>nodes() {
    return nodeMap.values().stream().sorted();
  }

  /** このパッケージノード以下のすべてをツリー構造として印刷する */
  public String printTree() {
    StringBuilder s = new StringBuilder();
    new Object() {
      void printChild(Node node, String indent) {
        s.append(indent + node.name + "\n");
        if (node instanceof PkgNode) {
          ((PkgNode)node).nodes().forEach(child-> {
            printChild(child, indent + " ");
          });
        }
      }
    }.printChild(this, "");
    return s.toString();
  }

  /** 
   * このパッケージ下の指定されたパスのノードを取得する。
   * パスに完全に一致するものが存在しなくとも、途中まで一致するノードを返す。
   * @param path "com.cm55.gs.*", "com.cm55.Sample", "com.cm55.Sample.*"など
   * @return 指定されたパスに最大限一致するノード。全く一致していない場合でも、このノードを返す。
   */
  public Node findNode(String path) {
    int dot = path.indexOf(".");
    String nodeName = dot < 0? path:path.substring(0, dot);    
    Node node = nodeMap.get(nodeName);
    if (node == null) return this;     
    if (dot < 0) return node;
    if (node instanceof ClsNode) return node;
    return ((PkgNode)node).findNode(path.substring(dot + 1));
  }

  /** このノード以下のすべてのノードを訪問する */
  @Override
  public void visit(Visitor visitor) {
    super.visit(visitor);
    nodeMap.values().stream().forEach(child->child.visit(visitor));
  }
}
