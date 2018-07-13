package com.cm55.depDetect.impl;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class ImportExtractor {

  
  public static Imports extract(Path path) throws IOException {
    List<String>lines = 
        Arrays.stream(CommentRemover.remove(path).split("\n"))
          .map(line->line.trim())
          .filter(line->line.length() > 0)
          .collect(Collectors.toList());    
    String pkgName = null;
    List<Import>imports = new ArrayList<>();
    if (lines.size() > 0) {
      String line = lines.remove(0);
      pkgName = getPackage(line);
      if (pkgName == null) {
        Import impName = getImport(line);
        if (impName == null) return new Imports();
        imports.add(impName);
      }
    }
    while (lines.size() > 0) {
      Import impName = getImport(lines.remove(0));
      if (impName == null) break;
      imports.add(impName);
    }
    return new Imports(imports.toArray(new Import[0]));
  }
  
  static Pattern PACKAGE = Pattern.compile("^package\\s+([^;]+);$");
  static Pattern IMPORT = Pattern.compile("^import\\s+([^;]+);$");
  static Pattern IMPORT_STATIC = Pattern.compile("^import\\s+static\\s+([^;]+);$");
  
  static String getPackage(String line) {
    Matcher m = PACKAGE.matcher(line);
    if (!m.matches()) return null;
    return m.group(1).replaceAll("\\s",  "");
  }
  
  static Import getImport(String line) {
    Matcher m;
    m = IMPORT_STATIC.matcher(line);
    if (m.matches()) return new Import(m.group(1).replaceAll("\\s",  ""), true);
    m = IMPORT.matcher(line);
    if (m.matches()) return new Import(m.group(1).replaceAll("\\s",  ""), false);
    return null;
  }
}
