package com.cm55.depDetect.impl;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import java.util.stream.*;

import com.cm55.depDetect.*;

/**
 * .class、フォルダ、jarファイルからの木構造クリエータ
 * @author ysugimura
 */
public class BinTreeCreator {
  
  static Pattern LINE = Pattern.compile("^\\s+([^\\s]+)\\s+->\\s+([^\\s]+)\\s+([^\\s]?.*)$");
  public static PkgNode create(String jdeps, Stream<String>files) throws IOException {
    PkgNodeImpl root = PkgNodeImpl.createRoot();
    
    List<String>cmd = new ArrayList<>();
    if (jdeps == null) cmd.add("jdeps");
    cmd.add("-J-Duser.language=en");
    cmd.add("-v");
    cmd.addAll(files.collect(Collectors.toList()));
    
    System.out.println(cmd.stream().collect(Collectors.joining(" ")));
      
    ProcessBuilder builder = new ProcessBuilder(cmd);
    Process process = builder.start();
    InputStream in = process.getInputStream();
    BufferedReader r = new BufferedReader(new InputStreamReader(in));    
    ExecutorService service = Executors.newSingleThreadExecutor();
    service.submit(()-> {
      while (true) {
        String line = r.readLine();
        if (line == null) break;
        if (line.indexOf("->") < 0) {        
          System.out.println("unknown:" + line);
          continue;
        }
        Matcher m = LINE.matcher(line);
        if (!m.matches()) {
          System.out.println("unmatched:" + line);
          continue;
        }
        PackageClass from = new PackageClass(m.group(1));
        PackageClass to = new PackageClass(m.group(2));        
        // これは取得するだけ無意味。"default"は何の意味か不明、java.lang等は"not found"にならない
        //String attr = m.group(3);
        
        // 同じパッケージの場合は無視する
        if (from.pkg.equals(to.pkg)) continue; 
        /*
        if (m.group(3).equals("default"))
        System.out.println(m.group(1) + "," + m.group(2) + "," + m.group(3));
        */
        System.out.println(from + " " + to.pkg);
        
        // 参照元パッケージを取得する
        PkgNodeImpl pkg = ensurePackage(root, from.pkg);
        System.out.println(pkg.getPath());
        
        // クラスを取得する
        ClsNodeImpl cls = pkg.ensureChildClass(from.cls);
        
      }
      return null;
    });
    service.shutdown();
    try {
      process.waitFor();
    } catch (InterruptedException ex) {}
    int ret = process.exitValue();
    
    /*
    // ツリーを作成する
    for (Path top: tops) create(root, top);

    // 依存をビルドする。
    // まずすべてのクラスの依存パッケージ集合を得てから、それぞれのパッケージの依存パッケージ集合を得る
    root.buildRefs();
    */
    
    return root;
  }

  /** "com.cm55.depDetect"といったパッケージ文字列からパッケージノードを取得する */
  static PkgNodeImpl ensurePackage(PkgNodeImpl root, String pkg) {
    if (pkg.length() == 0) return root;
    PkgNodeImpl node = root;
    for (String element: pkg.split("\\.")) {
      node = node.ensureChildPackage(element);
    }
    return node;
  }
  
  static class PackageClass {
    public final String pkg;
    public final String cls;
    PackageClass(String name) {
      String clsName = name;
      int dot = name.lastIndexOf('.');
      if (dot < 0) {
        pkg = "";
        clsName = name;
      } else {
        pkg = name.substring(0, dot);
        clsName = name.substring(dot + 1);
      }
      int doller = clsName.indexOf('$');
      if (doller < 0) cls = clsName;
      else cls = clsName.substring(0, doller);
    }
    public String toString() {
      return pkg + "/" + cls;
    }
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
          if (pkg.createChildClass(javaClass, SrcImportExtractor.extract(child)) == null) {
            throw new IllegalStateException("duplicated class " + path);
          }
        };
      }
    }.processChild(root, top);
  }

}
