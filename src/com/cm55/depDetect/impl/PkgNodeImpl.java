package com.cm55.depDetect.impl;

import java.util.*;
import java.util.stream.*;

import com.cm55.depDetect.*;

/**
 * パッケージを表すノード
 * @author ysugimura
 */
public class PkgNodeImpl extends NodeImpl implements PkgNode {

  /** 
   * このパッケージノード下のパッケージノードあるいはクラスノードの名前マップ
   * 名前は"com"あるいは"Sample"など
   */
  private Map<String, NodeImpl>nodeMap = new HashMap<>();
  
  /** このパッケージノードが依存するパッケージノードの集合 */
  private RefsImpl depsTo;

  /** このパッケージノードが依存する不明パッケージノード集合 */
  private UnknownsImpl unknowns;
  
  /** このパッケージノードが依存されているパッケージノードの集合 */
  private RefsImpl depsFrom = new RefsImpl();

  /** 循環依存パッケージノード。依存であり、被依存のパッケージノード */
  private RefsImpl cyclics;
  
  /** ルートパッケージノードを作成する。特殊な用途 */
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
  public void buildRefs() {
    depsTo = new RefsImpl();
    unknowns = new UnknownsImpl();
    
    // このパッケージ下のクラスの依存を取得する
    classStream().forEach(clsNode-> {
      ClsDeps clsDeps = clsNode.buildDeps();
      depsTo.add(clsDeps.depends);
      unknowns.add(clsDeps.unknowns);
    });

    // 依存先のrefsFromを設定
    depsTo.stream().forEach(to->to.depsFrom.add(this));    
    
    // 下位のパッケージノードを処理
    this.packageStream().forEach(child->child.buildRefs());
  }
  
  /** 
   * 循環参照ノード集合を作成する
   * {@link Refs}にある全パッケージについて、こちら側を参照しているものがあれば
   * それを{@link Refs}オブジェクトとしてまとめる
   */
  public void buildCyclics() {
    
    // 依存先と被依存元の共通部分を取得する
    cyclics = depsTo.intersect(depsFrom);
    
    // 下位のパッケージノードを処理
    packageStream().forEach(pkg->pkg.buildCyclics());
  }
  
  @Override
  public RefsImpl getDepsTo() {
    return depsTo;
  }
  
  @Override
  public UnknownsImpl getUnknowns() {
    return unknowns;
  }
  
  @Override
  public RefsImpl getDepsFrom() {
    return depsFrom;
  }
  
  @Override
  public RefsImpl getCyclics() {
    return cyclics;
  }
  
  @Override
  public UnknownsImpl getAllUnknowns() {
    UnknownsImpl allUnknowns = this.unknowns.duplicate();
    packageStream().forEach(pkg -> unknowns.add(pkg.getAllUnknowns()));
    return allUnknowns;
  }
  
  /** このノード以下のすべてのノードを訪問する */
  @Override
  public void visit(Visitor<Node> visitor) {
    visitor.visited(this);
    nodeStream().forEach(child->child.visit(visitor));
  }

  /** このノード以下のすべてのクラスノードを訪問する */
  @Override
  public void visitClasses(Visitor<ClsNode>visitor) {    
    packageStream().forEach(child->child.visitClasses(visitor));
  }

  /** このノード以下のすべてのパッケージノードを訪問する */
  @Override
  public void visitPackages(Visitor<PkgNode>visitor) {
    visitor.visited(this);
    packageStream().forEach(child->child.visitPackages(visitor));
  }
  
}
