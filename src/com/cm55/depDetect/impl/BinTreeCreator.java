package com.cm55.depDetect.impl;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
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
    cmd.add("-J-Duser.language=en");
    cmd.addAll(files.collect(Collectors.toList()));

      
    ProcessBuilder builder = new ProcessBuilder(cmd);
    Process process = builder.start();
    InputStream in = process.getInputStream();
    BufferedReader r = new BufferedReader(new InputStreamReader(in));    
    ExecutorService service = Executors.newSingleThreadExecutor();
    service.submit(()-> {
      while (true) {
        String line = r.readLine();
        if (line == null) break;
        System.out.println(line);
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
          if (pkg.createClass(javaClass, SrcImportExtractor.extract(child)) == null) {
            throw new IllegalStateException("duplicated class " + path);
          }
        };
      }
    }.processChild(root, top);
  }

}
