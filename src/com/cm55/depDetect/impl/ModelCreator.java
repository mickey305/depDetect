package com.cm55.depDetect.impl;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

public class ModelCreator {

  public static Model create(Path...tops) throws IOException {
    PkgNodeImpl root = PkgNodeImpl.createRoot();
    
    // ツリーを作成する
    for (Path top: tops) create(root, top);

    // 依存をビルドする。
    // まずすべてのクラスの依存パッケージ集合を得てから、それぞれのパッケージの依存パッケージ集合を得る
    root.buildDeps();
    
    // 循環依存をビルドする
    // あるパッケージの依存するパッケージから逆方向の依存があれば循環依存としてマークする
    root.buildCyclics();
        
    root.visitPackages(pkg-> {
      if (pkg.getCyclics().count() == 0) return;
      System.out.println("-------" + pkg + "\n" + pkg.getCyclics());
    });
    
    return new Model(root, null);
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
  
  
  public static void main(String[]args) throws IOException {
    Model model = create( 
        
      Paths.get("C:\\Users\\admin\\git\\shouhinstaff\\shouhinstaff\\src_base"),
      Paths.get("C:\\Users\\admin\\git\\shouhinstaff\\shouhinstaff\\src_common"),
      Paths.get("C:\\Users\\admin\\git\\shouhinstaff\\shouhinstaff\\src_server"),
      Paths.get("C:\\Users\\admin\\git\\shouhinstaff\\shouhinstaff\\src_term")
      
      //  Paths.get("src")
    );
  }
}
