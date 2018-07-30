package com.cm55.depDetect.impl;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

import com.cm55.depDetect.*;

public class SrcTreeDetail {

  /**
   * 指定クラスのソースコードを返す。
   * ただし、.javaのフォルダパスは複数指定が可能。
   * 指定クラスのすべての内部クラスについても出力される。
   * @param sourcePaths .classフォルダ。複数可能
   * @param clsNode 対象とするクラス。
   * @return
   */
  public static String getClassDetail(Stream<String>sourcePaths, ClsNode clsNode) {
    Path clsFile = findPath(sourcePaths, clsNode);
    if (clsFile == null) return null;   
    try {
      return Files.readAllLines(clsFile).stream().collect(Collectors.joining("\n"));
    } catch (IOException ex) {
      return null;
    }
  }

  /**
   * 複数のバイナリフォルダを調べ、指定クラスの.classファイルの絶対パスを取得する。
   * @param sourcePaths
   * @param clsNode
   * @return
   */
  private static Path findPath(Stream<String>sourcePaths, ClsNode clsNode) {
    
    // クラス名称を得る
    String classFile = clsNode.getName() + ".java";
    
    // パッケージ部分のパスを得る
    String pkgPath = clsNode.getParent().getPath().replace('.',  File.separatorChar);    
    
    // ファイルを探す
    return sourcePaths.map(sourcePath-> {
      Path cls = Paths.get(sourcePath, pkgPath, classFile);
      return cls;
    }).filter(cls->Files.exists(cls)).findAny().orElse(null);
  }
  
}
