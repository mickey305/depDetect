package com.cm55.depDetect.impl;

import java.util.*;
import java.util.stream.*;

import com.cm55.depDetect.*;

/**
 * パッケージを表すノード
 * @author ysugimura
 */
public class PkgNodeImpl extends NodeImpl implements PkgNode {

  private Map<String, NodeImpl>nodeMap = new HashMap<>();
  
  private Refs deps;
  
  private Refs Refs;
  
  /** ルートパッケージノードを作成する */
  public static PkgNodeImpl createRoot() {
    return new PkgNodeImpl();
  }
  
  /** ルートパッケージノードを作成する */
  private PkgNodeImpl() {
    super(null, "");
  }
  
  /**
   * 親パッケージ、名称を指定する
   * @param parent
   * @param name
   */
  public PkgNodeImpl(PkgNodeImpl parent, String name) {
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
  public PkgNodeImpl ensurePackage(String name) {
    PkgNodeImpl node = (PkgNodeImpl)nodeMap.get(name);
    if (node != null) return node;   
    node = new PkgNodeImpl(this, name);
    nodeMap.put(name,  node);
    return node;
   }
  
  /**
   * このパッケージノード下の、指定された名称のクラスを作成する。
   * 既に存在する場合は何もせずnullを返す。作成した場合はその{@link ClsNodeImpl}を返す。
   * @param name
   * @param imports
   * @return
   */
  public ClsNodeImpl createClass(String name, Imports imports) {
    ClsNodeImpl node = (ClsNodeImpl)nodeMap.get(name);
    if (node != null) return null;
    node = new ClsNodeImpl(this, name, imports);
    nodeMap.put(name,  node);
    return node;
  }

  /** このパッケージの下のすべてのノードを返す。パッケージノード、クラスノードが混在する */
  public Stream<NodeImpl>nodeStream() {
    return nodeMap.values().stream().sorted();
  }
  
  /** このパッケージの下のすべてのクラスノードのストリームを返す */
  public Stream<ClsNodeImpl>classStream() {
    return nodeMap.values().stream()
        .filter(node->node instanceof ClsNodeImpl).map(node->(ClsNodeImpl)node);
  }
  
  /** このパッケージの下のすべてのパッケージノードのストリームを返す */
  public Stream<PkgNodeImpl>packageStream() {
    return nodeMap.values().stream()
        .filter(node->node instanceof PkgNodeImpl).map(node->(PkgNodeImpl)node);
  }
  

  /** このパッケージノード以下のすべてをツリー構造として文字列化する。デバッグ用 */
  public String treeString() {
    StringBuilder s = new StringBuilder();
    new Object() {
      void printChild(NodeImpl node, String indent) {
        s.append(indent + node.name + "\n");
        if (node instanceof PkgNodeImpl) {
          ((PkgNodeImpl)node).nodeStream().forEach(child-> {
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
  public NodeImpl findNode(String path) {
    int dot = path.indexOf(".");
    String nodeName = dot < 0? path:path.substring(0, dot);    
    NodeImpl node = nodeMap.get(nodeName);
    if (node == null) return this;     
    if (dot < 0) return node;
    if (node instanceof ClsNodeImpl) return node;
    return ((PkgNodeImpl)node).findNode(path.substring(dot + 1));
  }

  /** このパッケージノードの依存セットを作成する */
  public void buildDeps() {
    Refs.Builder builder = new Refs.Builder();
    classStream().forEach(clsNode-> {
      Refs d = clsNode.buildDeps();
      builder.add(d);
    });
    deps = builder.build();
    this.packageStream().forEach(child->child.buildDeps());
  }
  
  /** 
   * 循環参照ノード集合を作成する
   * {@link Refs}にある全パッケージについて、こちら側を参照しているものがあれば
   * それを{@link Refs}オブジェクトとしてまとめる
   */
  public void buildCyclics() {
    PkgNodeImpl rootNode = getRoot();
    Refs.Builder builder = new Refs.Builder();
    deps.stream().forEach(pkgNode-> {      
      if (pkgNode.getDeps().contains(this))
        builder.add(pkgNode);
    });
    Refs = builder.build();
    packageStream().forEach(pkg->pkg.buildCyclics());
  }
  
  public Refs getDeps() {
    return deps;
  }
  
  public Refs getCyclics() {
    return Refs;    
  }
  
  /** このノード以下のすべてのノードを訪問する */
  @Override
  public void visit(Visitor<NodeImpl> visitor) {
    visitor.visited(this);
    nodeStream().forEach(child->child.visit(visitor));
  }

  /** このノード以下のすべてのクラスノードを訪問する */
  @Override
  public void visitClasses(Visitor<ClsNodeImpl>visitor) {    
    packageStream().forEach(child->child.visitClasses(visitor));
  }

  /** このノード以下のすべてのパッケージノードを訪問する */
  @Override
  public void visitPackages(Visitor<PkgNodeImpl>visitor) {
    visitor.visited(this);
    packageStream().forEach(child->child.visitPackages(visitor));
  }
  
}
