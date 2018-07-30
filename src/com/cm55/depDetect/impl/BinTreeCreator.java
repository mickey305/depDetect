package com.cm55.depDetect.impl;

import java.io.*;
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
  

  public static PkgNode create(String jdeps, Stream<String>files) throws IOException {
    
    PkgNodeImpl root = PkgNodeImpl.createRoot();
    
    List<String>cmd = new ArrayList<>();
    if (jdeps == null) cmd.add("jdeps");
    else cmd.add(jdeps);
    cmd.add("-J-Duser.language=en");
    cmd.add("-v");
    cmd.addAll(files.collect(Collectors.toList()));

    //ystem.out.println(cmd.stream().collect(Collectors.joining(" ")));
    
    Process process = new ProcessBuilder(cmd).start();
    BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));    
    ExecutorService service = Executors.newSingleThreadExecutor();
    service.submit(()-> {
      while (true) {
        String line = r.readLine();
        if (line == null) break;
        processLine(root, line);
      }
      return null;
    });
    service.shutdown();
    try {
      process.waitFor();
    } catch (InterruptedException ex) {}
    int ret = process.exitValue();    
    
    DepsBuilder.buildRefs(root);
    
    return root;
  }

  static Pattern LINE = Pattern.compile("^\\s+([^\\s]+)\\s+->\\s+([^\\s]+)\\s+([^\\s]?.*)$");
  
  /** jdepsの標準出力から読み取った一行を処理する */
  static void processLine(PkgNodeImpl root, String line) {
    if (line.indexOf("->") < 0) {        
      System.out.println("unknown:" + line);
      return;
    }
    Matcher m = LINE.matcher(line);
    if (!m.matches()) {
      System.out.println("unmatched:" + line);
      return;
    }
    PackageClass from = new PackageClass(m.group(1));
    PackageClass to = new PackageClass(m.group(2));        
    // これは取得するだけ無意味。"default"は何の意味か不明、java.lang等は"not found"にならない
    //String attr = m.group(3);
    //ystem.out.println("" + from);
    
    // 同じパッケージの場合は無視する
    if (from.pkg.equals(to.pkg)) return;
    
    // 参照元パッケージを取得する
    PkgNodeImpl pkg = ensurePackage(root, from.pkg);
    
    // クラスを取得する
    ClsNodeImpl cls = pkg.createChildClass(from.cls, false);
    
    // importを追加する
    if (cls.imports == null) {
      VarImports imports = new VarImports();
      cls.imports = imports;
    }
    ((VarImports)cls.imports).add(to.pkg);    
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
}
