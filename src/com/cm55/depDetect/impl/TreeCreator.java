package com.cm55.depDetect.impl;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import com.cm55.depDetect.*;

/**
 * 木構造クリエータ
 * @author ysugimura
 */
public class TreeCreator {

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
    root.buildRefs();
    
    return root;
  }
  
  static void create(PkgNodeImpl root, Path top) throws IOException {
    new Object() {
      void processChild(PkgNodeImpl pkg, Path path) throws IOException {
        for (Path child: Files.list(path).collect(Collectors.toList())) {   
          String childName = child.getName(child.getNameCount() - 1).toString();
          if (Files.isDirectory(child)) {
            processChild(pkg.ensurePackage(childName), child);
            continue;
          }
          if (!childName.endsWith(".java")) continue;
          String javaClass = childName.substring(0, childName.length() - 5);
          if (pkg.createClass(javaClass, ImportExtractor.extract(child)) == null) {
            throw new IllegalStateException("duplicated class " + path);
          }
        };
      }
    }.processChild(root, top);
  }

}
