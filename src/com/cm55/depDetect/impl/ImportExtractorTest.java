package com.cm55.depDetect.impl;

import java.io.*;
import java.nio.file.*;

import org.junit.*;

public class ImportExtractorTest {

  @Test
  public void test() throws IOException {
    Imports imports = ImportExtractor.extract(Paths.get("src/com/cm55/depDetect/impl/CommentRemover.java"));
    //ystem.out.println(imports);
  }


}
