package com.cm55.depDetect.impl;

import java.io.*;
import java.nio.file.*;

import org.junit.*;

public class SrcImportExtractorTest {

  @Test
  public void test() throws IOException {
    BulkImports imports = SrcImportExtractor.extract(Paths.get("src/com/cm55/depDetect/impl/CommentRemover.java"));
    //ystem.out.println(imports);
  }


}
