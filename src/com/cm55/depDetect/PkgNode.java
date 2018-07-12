package com.cm55.depDetect;

import java.util.*;
import java.util.stream.*;

/**
 * パッケージを表すノード
 * @author ysugimura
 */
public class PkgNode extends Node {

  private Map<String, Node>nodeMap = new HashMap<>();
  
  public PkgNode(PkgNode parent, String name) {
    super(parent, name);
  }
  
  public PkgNode ensurePackage(String name) {
    System.out.println("package " + name);
    PkgNode node = (PkgNode)nodeMap.get(name);
    if (node != null) return node;   
    node = new PkgNode(this, name);
    nodeMap.put(name,  node);
    return node;
   }
  
  public ClsNode createClass(String name, Imports imports) {
    System.out.println("class " + name);
    ClsNode node = (ClsNode)nodeMap.get(name);
    if (node != null) return node;
    node = new ClsNode(this, name, imports);
    nodeMap.put(name,  node);
    return node;
  }

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
}
