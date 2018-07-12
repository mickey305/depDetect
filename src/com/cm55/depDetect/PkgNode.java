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

  /** このパッケージ下の指定されたパスのパッケージを取得する。無い場合はnullを返す */
  public PkgNode findPackage(String path) {
    int dot = path.indexOf(".");
    String element = dot < 0? path:path.substring(0, dot);
    PkgNode pkgNode;
    try {
      Node node = nodeMap.get(element);
      pkgNode = (PkgNode)node;
      if (pkgNode == null) return null;
    } catch (Exception ex) {
      return null;
    }
    if (dot < 0) return pkgNode;    
    return pkgNode.findPackage(path.substring(dot + 1));
  }

  /** このノード以下のすべてのノードを訪問する */
  @Override
  public void visit(Visitor visitor) {
    super.visit(visitor);
    nodeMap.values().stream().forEach(child->child.visit(visitor));
  }
}
