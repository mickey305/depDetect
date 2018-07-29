package com.cm55.depDetect.impl;

public class DepsBuilder {

  static void buildRefs(PkgNodeImpl node) {
    node.depsTo = new RefsImpl();
    node.unknowns = new UnknownsImpl();
    
    // このパッケージ下のクラスの依存を取得する
    node.duClassStream().forEach(clsNode-> {
      ClsDeps clsDeps = buildDeps(clsNode);
      node.depsTo.add(clsDeps.depends);
      node.unknowns.add(clsDeps.unknowns);
    });

    // 依存先のrefsFromを設定
    node.depsTo.stream().forEach(to->((PkgNodeImpl)to).depsFrom.add(node));    
    
    // 下位のパッケージノードを処理
    node.duPackageStream().forEach(child->buildRefs(child));
  }
  
  static ClsDeps buildDeps(ClsNodeImpl cls) {    
    ClsDeps clsDeps = cls.imports.createDependencies(cls.parent);
    cls.depsTo = clsDeps.depends;
    cls.unknowns = clsDeps.unknowns;
    return clsDeps;    
  }
}
