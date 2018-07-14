package com.cm55.depDetect.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.*;


public class PkgNodeTest {

  @Test
  public void test() {
    PkgNodeImpl root = PkgNodeImpl.createRoot();
    PkgNodeImpl com = root.ensurePackage("com");
    PkgNodeImpl cm55 = com.ensurePackage("cm55");
    PkgNodeImpl depDetect = cm55.ensurePackage("depDetect");
        
    assertThat(root.findMaximum("com.cm55.depDetect"), equalTo(depDetect));   
    assertThat(root.findMaximum("com.cm55.test"), equalTo(cm55)); 
  }

}
