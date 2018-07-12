package com.cm55.depDetect;

import java.io.*;
import java.nio.file.*;

import org.junit.*;

public class ImportExtractorTest {

  @Test
  public void test() throws IOException {
    Imports imports = ImportExtractor.extract(Paths.get("src/com/cm55/depDetect/CommentRemover.java"));
    System.out.println(imports);
  }


}
