package com.cm55.depDetect.impl;

import java.util.*;

import org.junit.*;

public class BinTreeDetailTest {

  @Test
  public void test() {
    PkgNodeImpl root = PkgNodeImpl.createRoot();
    PkgNodeImpl com = root.ensureChildPackage("com");
    PkgNodeImpl cm55 = com.ensureChildPackage("cm55");
    PkgNodeImpl depDetect = cm55.ensureChildPackage("depDetect");
    PkgNodeImpl impl = depDetect.ensureChildPackage("impl");
    ClsNodeImpl binTreeCreator = impl.createChildClass("BinTreeCreator",  true);
    
    String[]sourcePaths = new String[] {
      "C:\\devel\\workspace-neon\\github_depDetect\\bin\\default"
    };
    
    String result = BinTreeDetail.getClassDetail(null,  Arrays.stream(sourcePaths), binTreeCreator);
    System.out.println(result);
  }

}
