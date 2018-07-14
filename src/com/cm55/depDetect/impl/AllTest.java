package com.cm55.depDetect.impl;

import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Suite.*;


@RunWith(Suite.class)
@SuiteClasses( { 
  CommentRemoverTest.class,
  ImportExtractorTest.class,
  ImportTest.class,
  PkgNodeImplTest.class,
})
public class AllTest {
  public static void main(String[] args) {
    JUnitCore.main(AllTest.class.getName());
  }
}
