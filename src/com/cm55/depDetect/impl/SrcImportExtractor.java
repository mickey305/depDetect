package com.cm55.depDetect.impl;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

/**
 * Javaソースからimport文情報を取得する
 * @author ysugimura
 */
public class SrcImportExtractor {

  /** 指定されたJavaソース・ファイルのimport文を全て取得し、{@link Imports}オブジェクトを返す */
  public static Imports extract(Path path) throws IOException {
    List<String>lines = 
        Arrays.stream(CommentRemover.remove(path).split("\n"))
          .map(line->line.trim())
          .filter(line->line.length() > 0)
          .collect(Collectors.toList());   
    return extract(lines);
  }

  /** 
   * Javaソース・ファイルの行リストからimport文を全て取得し、{@link Imports}オブジェクトを返す。
   * ただし、linesは以下の条件を満たすこと。
   * <ul>
   * <li>コメントはすべて除去されている。
   * <li>空行はすべて除去されている
   * <li>行の前後の空白はすべて除去されている。
   * <ul>
   * <p>
   * したがって、一行名は必ずpackage文（オプション）となり、その次の行はimport文が連続することになる。
   * </p>
   * @param lines 入力Javaソース行リスト
   * @return {@link Imports}
   */
  static Imports extract(List<String>lines) {    
    
    List<Import>importList = new ArrayList<>();

    // package文もしくは最初のimport文を取得
    if (lines.size() > 0) {
      String line = lines.remove(0);
      String pkgName = getPackage(line);
      if (pkgName == null) {
        Import imp = getImport(line);
        if (imp == null) return new Imports();
        importList.add(imp);
      }
    }
    
    // import文が無くなるまで繰り返す
    while (lines.size() > 0) {
      Import imp = getImport(lines.remove(0));
      if (imp == null) break;
      importList.add(imp);
    }
    
    return new Imports(importList.toArray(new Import[0]));
  }
  
  /** package文パターン */
  static Pattern PACKAGE = Pattern.compile("^package\\s+([^;]+);$");
  
  /** import文パターン */
  static Pattern IMPORT = Pattern.compile("^import\\s+([^;]+);$");
  
  /** import static文パターン */
  static Pattern IMPORT_STATIC = Pattern.compile("^import\\s+static\\s+([^;]+);$");

  /** package文のパッケージ名称を取得する。package文でなければnullを返す */
  static String getPackage(String line) {
    Matcher m = PACKAGE.matcher(line);
    if (!m.matches()) return null;
    return m.group(1).replaceAll("\\s",  "");
  }
  
  /** import/import static文の依存パッケージを{@link Import}の形で返す */
  static Import getImport(String line) {
    Matcher m;
    m = IMPORT_STATIC.matcher(line);
    if (m.matches()) return new Import(m.group(1).replaceAll("\\s",  ""), true);
    m = IMPORT.matcher(line);
    if (m.matches()) return new Import(m.group(1).replaceAll("\\s",  ""), false);
    return null;
  }
}
