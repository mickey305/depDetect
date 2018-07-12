package com.cm55.depDetect;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

public class ReadAll {

  public static void readAll(PkgNode root, Path top) throws IOException {
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
          pkg.createClass(javaClass, ImportExtractor.extract(child));
        };
      }
    }.processChild(root, top);
  }
  
  public static void main(String[]args) throws IOException {
    PkgNode root = new PkgNode(null, "");
    String s = "C:\\Users\\admin\\git\\shouhinstaff\\shouhinstaff\\src_base";
//    String s = "src";
    readAll(root, Paths.get(s));
    
    System.out.println("" + root.printTree());
    
  }
}
