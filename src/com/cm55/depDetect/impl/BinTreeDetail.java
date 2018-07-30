package com.cm55.depDetect.impl;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import com.cm55.depDetect.*;

public class BinTreeDetail {

  /**
   * 指定クラスについてのjdepsの出力結果を返す。
   * ただし、.classのフォルダパスは複数指定が可能。
   * 指定クラスのすべての内部クラスについても出力される。
   * @param jdeps jdepsのパス。nullの場合はデフォルトが仕様される。
   * @param sourcePaths .classフォルダ。複数可能
   * @param clsNode 対象とするクラス。
   * @return
   */
  public static String getClassDetail(String jdeps, Stream<String>sourcePaths, ClsNode clsNode) {    
    Path clsFile = findPath(sourcePaths, clsNode);
    if (clsFile == null) return null;
    
    try {
      List<String>paths = includesAllInternals(clsFile, clsNode.getName());
      //paths.stream().forEach(System.out::println);      
      String result = getJDeps(jdeps, paths);      
      return result;
    } catch (IOException ex) {}    
    return null;
  }

  /**
   * 複数のバイナリフォルダを調べ、指定クラスの.classファイルの絶対パスを取得する。
   * @param sourcePaths
   * @param clsNode
   * @return
   */
  private static Path findPath(Stream<String>sourcePaths, ClsNode clsNode) {
    
    // クラス名称を得る
    String classFile = clsNode.getName() + ".class";
    
    // パッケージ部分のパスを得る
    String pkgPath = clsNode.getParent().getPath().replace('.',  File.separatorChar);    
    
    // ファイルを探す
    Path clsFile = sourcePaths.map(sourcePath-> {
      Path cls = Paths.get(sourcePath, pkgPath, classFile);
      return cls;
    }).filter(cls->Files.exists(cls)).findAny().orElse(null);
        
    return clsFile;
  }
  
  /**
   * 対象とするクラスの.classが見つかった。このクラスの内部クラスを含むすべてのパスを取得する。
   * 例えば、A.classの場合A$1.class, A$Sample.classなどがある。
   * @param clsFile .classファイルのパス
   * @param clsName クラス名
   * @return 内部クラスも含めた全パス
   * @throws IOException
   */
  private static List<String>includesAllInternals(Path clsFile, String clsName) throws IOException {
    List<Path>files = new ArrayList<>();    
    files.add(clsFile);    
    Path dir = clsFile.getParent();    
    String internalStart = clsName + "$";    
    files.addAll(Files.list(dir).filter(f-> {
      String name = f.getFileName().toString();
      return name.startsWith(internalStart);      
    }).collect(Collectors.toList()));
        
    return files.stream().map(f->f.toString()).collect(Collectors.toList());
  }

  /** 
   * 指定された全.classファイルについてjdepsを実行する
   * @param jdeps jdepsのパス。nullの場合はデフォルトを呼び出す。
   * @param paths 対象とする全.classファイルのパス
   * @return jdepsの標準出力
   * @throws IOException
   */
  private static String getJDeps(String jdeps, List<String>paths) throws IOException {
    
    List<String>cmd = new ArrayList<>();
    if (jdeps == null) cmd.add("jdeps");
    else cmd.add(jdeps);
    cmd.add("-J-Duser.language=en");
    cmd.add("-v");
    cmd.addAll(paths);
    

    StringBuilder result = new StringBuilder();
    
    Process process = new ProcessBuilder(cmd).start();
    BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));    
    ExecutorService service = Executors.newSingleThreadExecutor();
    service.submit(()-> {
      while (true) {
        String line = r.readLine();
        if (line == null) break;
        result.append(line + "\n");
      }
      return null;
    });
    service.shutdown();
    try {
      process.waitFor();
    } catch (InterruptedException ex) {}
    int ret = process.exitValue(); 
    
    return result.toString();
  }
}
