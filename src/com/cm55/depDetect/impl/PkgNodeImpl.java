package com.cm55.depDetect.impl;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import com.cm55.depDetect.*;
import com.cm55.depDetect.JavaNode;

/**
 * パッケージを表すノード
 * @author ysugimura
 */
public class PkgNodeImpl extends JavaNodeImpl implements PkgNode {

  /** 
   * このパッケージノード下のパッケージノードあるいはクラスノードの名前マップ
   * 名前は"com"あるいは"Sample"など
   */
  private Map<String, JavaNodeImpl>nodeMap = new HashMap<>();
  
  /** このパッケージノードが依存するパッケージノードの集合 */
  private RefsImpl depsTo;

  /** このパッケージノードが依存する不明パッケージノード集合 */
  private UnknownsImpl unknowns;
  
  /** このパッケージノードが依存されているパッケージノードの集合 */
  private RefsImpl depsFrom = new RefsImpl();

  /** 循環依存パッケージノード。依存であり、被依存のパッケージノード */
  private RefsImpl cyclics;
  
  /** ルートパッケージノードを作成する。特殊な用途 */
  static PkgNodeImpl createRoot() {
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
  PkgNodeImpl(PkgNodeImpl parent, String name) {
    super(parent, name);
    check();
  }
  
  /** {@inheritDoc} */
  @Override
  public JavaNodeKind getKind() {
    return JavaNodeKind.PACKAGE;
  }
  
  /** 
   * このパッケージノード下の、指定された名称のパッケージノードを確保する。
   * あればそれを返し、無ければ作成して返す。
   * 例えば"com.cm55"というパッケージの下の"gs"というパッケージ
   * @param name
   * @return
   */
  PkgNodeImpl ensurePackage(String name) {
    JavaNodeImpl node = nodeMap.get(name);
    if (node == null) {
      node = new PkgNodeImpl(this, name);
      nodeMap.put(name,  node);
      return (PkgNodeImpl)node;      
    }
    if (node instanceof ClsNodeImpl) {
      // クラスノードではいけない
      throw new IllegalStateException(node.getPath() + " is already registered as Class ");
    }
    return (PkgNodeImpl)node;
   }
  
  /**
   * このパッケージノード下の、指定された名称のクラスを作成する。
   * 既に存在する場合は例外。作成した場合はその{@link ClsNodeImpl}を返す。
   * @param name
   * @param imports
   * @return
   */
  ClsNodeImpl createClass(String name, Imports imports) {
    JavaNodeImpl node = nodeMap.get(name);
    if (node == null) {
      node = new ClsNodeImpl(this, name, imports);
      nodeMap.put(name,  node);
      return (ClsNodeImpl)node;      
    }
    throw new IllegalStateException(node.getPath() + " is already registered");
  }

  /** {@inheritDoc} */
  @Override
  public Stream<JavaNode>nodeStream(boolean descend) {
    return _nodeStream(descend).map(n->(JavaNode)n);
  }
  
  private Stream<JavaNodeImpl>_nodeStream(boolean descend) {
    if (!descend)
      return nodeMap.values().stream().sorted();
    List<JavaNodeImpl>nodes = new ArrayList<>();
    visit(VisitOrder.PRE, n->nodes.add((JavaNodeImpl)n));
    return nodes.stream();
  }
  
  /** {@inheritDoc} */
  @Override
  public Stream<ClsNode>classStream(boolean descend) {
    return _classStream(descend).map(n->(ClsNode)n);
  }

  private Stream<ClsNodeImpl>_classStream(boolean descend) {
    if (!descend)
      return nodeMap.values().stream()
        .filter(node->node instanceof ClsNodeImpl).map(n->(ClsNodeImpl)n).sorted();
    List<ClsNodeImpl>nodes = new ArrayList<>();
    this.visitClasses(n->nodes.add((ClsNodeImpl)n));
    return nodes.stream();
  }
  
  /** {@inheritDoc} */
  @Override
  public Stream<PkgNode>packageStream(boolean descend) {
    return _packageStream(descend).map(n->(PkgNode)n);
  }
  
  private Stream<PkgNodeImpl>_packageStream(boolean descend) {
    if (!descend)
      return nodeMap.values().stream()
        .filter(node->node instanceof PkgNodeImpl).map(n->(PkgNodeImpl)n).sorted();
    List<PkgNodeImpl>nodes = new ArrayList<>();
    this.visitPackages(VisitOrder.PRE, n->nodes.add((PkgNodeImpl)n));
    return nodes.stream();
  }
  
  /** このパッケージノード以下のすべてをツリー構造として文字列化する。デバッグ用 */
  @Override
  public String treeString() {
    StringBuilder s = new StringBuilder();
    new Object() {
      void printChild(PkgNodeImpl node, String childIndent) {
        node._nodeStream(false).forEach(child-> {
          s.append(childIndent + child.name + "\n");
          if (child instanceof PkgNode) 
            printChild((PkgNodeImpl)child, childIndent + " ");
        });        
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
  @Override
  public JavaNodeImpl findMaximum(String path) {
    return find(path, false);
  }
  
  /** {@inheritDoc} */
  @Override
  public JavaNodeImpl findExact(String path) {
    return find(path, true);
  }

  /**
   * 
   * @param path
   * @param exactMode
   * @return
   */
  private JavaNodeImpl find(String path, boolean exactMode) {
    
    // 最初の要素を取得し、直下のノードを調べる
    int dotPosition = path.indexOf(".");
    String nodeName = dotPosition < 0? path:path.substring(0, dotPosition);    
    JavaNodeImpl node = nodeMap.get(nodeName);
        
    // 最初の要素に対応するノードが存在しない場合
    if (node == null) {
      // 完全一致モードの場合はnullを返す
      if (exactMode) return null;
      
     // 完全一致モードでない場合は本ノードを返す。
      return this;     
    }
    
    // 最初の要素に対応するノードが存在する場合
    
    // これ以上パス要素が無い場合、あるいはクラスノードの場合には、このノードを返す。
    if (dotPosition < 0) return node;
    if (node instanceof ClsNodeImpl) return node;
    
    // 下位ノードに処理を任せる。
    return ((PkgNodeImpl)node).find(path.substring(dotPosition + 1), exactMode);
  }
  
  /** このパッケージノードの依存セットを作成する */
  public void buildRefs() {
    depsTo = new RefsImpl();
    unknowns = new UnknownsImpl();
    
    // このパッケージ下のクラスの依存を取得する
    _classStream(false).forEach(clsNode-> {
      ClsDeps clsDeps = clsNode.buildDeps();
      depsTo.add(clsDeps.depends);
      unknowns.add(clsDeps.unknowns);
    });

    // 依存先のrefsFromを設定
    depsTo.stream().forEach(to->((PkgNodeImpl)to).depsFrom.add(this));    
    
    // 下位のパッケージノードを処理
    this._packageStream(false).forEach(child->child.buildRefs());
  }
  
  /** 
   * 循環参照ノード集合を作成する
   * {@link Refs}にある全パッケージについて、こちら側を参照しているものがあれば
   * それを{@link Refs}オブジェクトとしてまとめる
   */
  public void buildCyclics() {
    
    // 依存先と被依存元の共通部分を取得する
    cyclics = depsTo.getIntersect(depsFrom);
    
    // 下位のパッケージノードを処理
    _packageStream(false).forEach(pkg->pkg.buildCyclics());
  }

  /** 
   * このパッケージの不明import集合を取得する。
   * descend=trueのときはこのパッケージ以下のすべての不明import集合
   */
  @Override
  public UnknownsImpl getUnknowns(boolean descend) {
    if (!descend) return unknowns;
    UnknownsImpl impl = new UnknownsImpl();
    impl.add(unknowns);
    _packageStream(false).forEach(child->impl.add(child.getUnknowns(true)));
    return impl;
  }
  
  /**
   * このパッケージの依存パッケージ集合を取得する。
   * descend=trueのときは、このパッケージ以下のすべての依存パッケージ集合を取得するが、
   * ただし、このパッケージ以下のサブツリー内のパッケージは除去する
   */
  @Override
  public RefsImpl getDepsTo(boolean descend) {    
    if (!descend) return depsTo;
    RefsImpl refs = getDepsToDescend();
    refs.removeDescend(this);
    return refs;
  }
  
  /** このパッケージ以下の依存パッケージ集合を取得する。このパッケージ以下のサブツリーのパッケージも含む */
  RefsImpl getDepsToDescend() {
    RefsImpl impl = new RefsImpl();
    impl.add(depsTo);
    this._packageStream(false).forEach(child->impl.add(child.getDepsToDescend()));
    return impl;
  }

  /**
   * このパッケージの被依存パッケージ集合を取得する。
   * descend=trueのときは、このパッケージ以下のすべての被依存パッケージ集合を取得するが、
   * ただし、このパッケージ以下のサブツリー内のパッケージは除去する。
   */
  @Override
  public RefsImpl getDepsFrom(boolean descend) {
    if (!descend) return depsFrom;
    RefsImpl refs = getDepsFromDescend();
    refs.removeDescend(this);
    return refs;
  }

  /** このパッケージ以下の被依存パッケージ集合を取得する。このパッケージ以下のサブツリーのパッケージも含む */
  RefsImpl getDepsFromDescend() {
    RefsImpl impl = new RefsImpl();
    impl.add(depsFrom);
    _packageStream(false).forEach(child->impl.add(child.getDepsFromDescend()));
    return impl;
  }
  
  /**
   * このパッケージの循環依存パッケージ集合を取得する。
   * descend=trueのときは、このパッケージ以下のサブツリー内全パッケージの循環依存パッケージ集合を取得するが、
   * ただし、サブ突っリー内のパッケージは除去する
   */
  @Override
  public RefsImpl getCyclics(boolean descend) {  
    if (!descend) return cyclics;
    RefsImpl refs = getCyclicsDescend();
    refs.removeDescend(this);
    return refs;
  }
  
  RefsImpl getCyclicsDescend() {
    RefsImpl impl = new RefsImpl();
    impl.add(cyclics);
    _packageStream(false).forEach(child->impl.add(child.getCyclicsDescend()));
    return impl;
  }

  
  /** このノード以下のすべてのノードを訪問する */
  @Override
  public void visit(VisitOrder order, Consumer<JavaNode> visitor) {
    if (order == VisitOrder.PRE) visitor.accept(this);
    nodeStream(false).forEach(child-> {
      if (child instanceof PkgNodeImpl) ((PkgNodeImpl)child).visit(order, visitor);
      else visitor.accept(child);
    });
    if (order == VisitOrder.POST) visitor.accept(this); 
  }

  /** {@inheritDoc} */
  @Override
  public Stream<JavaNode>visitStream(VisitOrder order) {
    List<JavaNode>nodes = new ArrayList<>();
    visit(order, n->nodes.add(n));
    return nodes.stream();
  }

  /** このノード以下のすべてのパッケージノードを訪問する */
  @Override
  public void visitPackages(VisitOrder order, Consumer<PkgNode>visitor) {
    visit(order, n-> {
      if (n instanceof PkgNode) visitor.accept((PkgNode)n);
    });
  }


  /** このノード以下のすべてのクラスノードを訪問する */
  @Override
  public void visitClasses(Consumer<ClsNode>visitor) {    
    visit(null, n-> {
      if (n instanceof ClsNode) visitor.accept((ClsNode)n);
    });
  }
  
  /** {@inheritDoc} */
  @Override
  public Stream<ClsNode>visitClassesStream() {
    List<ClsNode>nodes = new ArrayList<>();
    visitClasses(n->nodes.add(n));
    return nodes.stream();
  }

  @Override
  public int nodeCount(boolean descend) {
    return (int)nodeStream(descend).count();
  }

  @Override
  public int classCount(boolean descend) {
    return (int)classStream(descend).filter(n->n instanceof ClsNode).count();
  }

  @Override
  public int packageCount(boolean descend) {
    return (int)packageStream(descend).filter(n->n instanceof PkgNode).count();
  }
  
  /**
   * このノードが指定ノードと同じかあるいは先祖にあたるかを調べる
   */
  @Override
  public boolean isAscendOf(PkgNode node) {
    while (node != null) {
      if (this == node) return true;
      node = node.getParent();
    }
    return false;
  }
}
