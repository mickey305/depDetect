package com.cm55.depDetect;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


public class PkgNodeTest {

  @Test
  public void test() {
    PkgNode root = PkgNode.createRoot();
    PkgNode com = root.ensurePackage("com");
    PkgNode cm55 = com.ensurePackage("cm55");
    PkgNode depDetect = cm55.ensurePackage("depDetect");
        
    assertThat(root.findNode("com.cm55.depDetect"), equalTo(depDetect));   
    assertThat(root.findNode("com.cm55.test"), equalTo(null)); 
  }

}
