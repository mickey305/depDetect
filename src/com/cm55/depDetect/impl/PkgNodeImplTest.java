package com.cm55.depDetect.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.stream.*;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import com.cm55.depDetect.*;


public class PkgNodeImplTest {

  PkgNodeImpl root;
  PkgNodeImpl com;
  PkgNodeImpl cm55;
  PkgNodeImpl depDetect;
  
  @Before
  public void before() {
    root = PkgNodeImpl.createRoot();
    com = root.ensurePackage("com");
    cm55 = com.ensurePackage("cm55");
    depDetect = cm55.ensurePackage("depDetect");
  }
  
  //@Test
  public void findMaximum() {        
    assertThat(root.findMaximum("com.cm55.depDetect"), equalTo(depDetect));   
    assertThat(root.findMaximum("com.cm55.test"), equalTo(cm55)); 
  }
  
  @Test
  public void visit() {
    assertThat(
      root.visitStream(VisitOrder.PRE).map(n->n.toString()).collect(Collectors.joining("\n")) + "\n",
      equalTo(
       "\n" + 
       "com\n" + 
       "com.cm55\n" + 
       "com.cm55.depDetect\n"
    ));
    assertThat(
        root.visitStream(VisitOrder.POST).map(n->n.toString()).collect(Collectors.joining("\n")) + "\n",
        equalTo(
            "com.cm55.depDetect\n" +
            "com.cm55\n" + 
            "com\n" + 
            "\n"
      ));
  }

}
