package com.cm55.depDetect.impl;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import com.cm55.depDetect.*;

/**
 * Javaソース・ファイルからの木構造クリエータ
 * @author ysugimura
 */
public class SrcTreeCreator {

  public static PkgNode create(File...paths) throws IOException {
    return create(Arrays.stream(paths).map(path->path.toPath()).collect(Collectors.toList()));
  }
  
  public static PkgNode create(String...paths) throws IOException {
    return create(Arrays.stream(paths).collect(Collectors.toList()));
  }
  
  public static PkgNode create(Collection<String>paths) throws IOException {
    return create(paths.stream().map(path->Paths.get(path)).collect(Collectors.toList()));
  }
  
  public static PkgNode create(Path...tops) throws IOException {
    return create(Arrays.stream(tops).collect(Collectors.toList()));
  }
  
  public static PkgNode create(List<Path>tops) throws IOException {
    PkgNodeImpl root = PkgNodeImpl.createRoot();
    
    // ツリーを作成する
    for (Path top: tops) create(root, top);

    // 依存をビルドする。
    // まずすべてのクラスの依存パッケージ集合を得てから、それぞれのパッケージの依存パッケージ集合を得る
    buildRefs(root);
    
    return root;
  }
  
  static void buildRefs(PkgNodeImpl root) {
    root.depsTo = new RefsImpl();
    root.unknowns = new UnknownsImpl();
    
    // このパッケージ下のクラスの依存を取得する
    root.duClassStream().forEach(clsNode-> {
      ClsDeps clsDeps = buildDeps(clsNode);
      root.depsTo.add(clsDeps.depends);
      root.unknowns.add(clsDeps.unknowns);
    });

    // 依存先のrefsFromを設定
    root.depsTo.stream().forEach(to->((PkgNodeImpl)to).depsFrom.add(root));    
    
    // 下位のパッケージノードを処理
    root.duPackageStream().forEach(child->buildRefs(child));
  }
  
  static ClsDeps buildDeps(ClsNodeImpl cls) {    
    ClsDeps clsDeps = ((BulkImports)cls.imports).createDependencies(cls.parent);
    cls.depsTo = clsDeps.depends;
    cls.unknowns = clsDeps.unknowns;
    return clsDeps;
    
  }
  
  static void create(PkgNodeImpl root, Path top) throws IOException {
    new Object() {
      void processChild(PkgNodeImpl pkg, Path path) throws IOException {
        for (Path child: Files.list(path).collect(Collectors.toList())) {   
          String childName = child.getName(child.getNameCount() - 1).toString();
          if (Files.isDirectory(child)) {
            processChild(pkg.ensureChildPackage(childName), child);
            continue;
          }
          if (!childName.endsWith(".java")) continue;
          String javaClass = childName.substring(0, childName.length() - 5);
          ClsNodeImpl cls = pkg.createChildClass(javaClass, true);
          if (cls== null) {
            throw new IllegalStateException("duplicated class " + path);
          }
          cls.imports = SrcImportExtractor.extract(child);          
        };
      }
    }.processChild(root, top);
  }

}
