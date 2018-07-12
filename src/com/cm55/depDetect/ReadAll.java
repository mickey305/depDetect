package com.cm55.depDetect;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class ReadAll {

  public static PkgNode readAll(Path...tops) throws IOException {
    PkgNode root = PkgNode.createRoot();
    for (Path top: tops) readAll(root, top);
    Set<String>noNodeSet = new HashSet<>();
    root.visit(node-> {
      if (!(node instanceof ClsNode)) return;
      noNodeSet.addAll(
        ((ClsNode)node).imports.setNode(root)
      );
    });
    noNodeSet.stream().sorted().forEach(System.out::println);
    return root;
  }
  
  static void readAll(PkgNode root, Path top) throws IOException {
    new Object() {
      void processChild(PkgNode pkg, Path path) throws IOException {
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
    PkgNode root = readAll( 
      Paths.get("C:\\Users\\admin\\git\\shouhinstaff\\shouhinstaff\\src_base"),
      Paths.get("C:\\Users\\admin\\git\\shouhinstaff\\shouhinstaff\\src_common"),
      Paths.get("C:\\Users\\admin\\git\\shouhinstaff\\shouhinstaff\\src_server"),
      Paths.get("C:\\Users\\admin\\git\\shouhinstaff\\shouhinstaff\\src_term")
    );

  }
}
