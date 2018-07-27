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
  PkgNodeImpl test;
  ClsNodeImpl foo1;
  ClsNodeImpl foo2;
  ClsNodeImpl bar1;
  ClsNodeImpl bar2;
  ClsNodeImpl sample;
  
  @Before
  public void before() {
    /**
     * ""
     * +- com
     *    +- cm55
     *       +- depDetect
     *       |  +- Bar1
     *       |  +- Bar2
     *       +- test
     *       |  +- Sample
     *       +- Foo1
     *       +- FOo2  
     */
    root = PkgNodeImpl.createRoot();
    com = root.ensurePackage("com");
    cm55 = com.ensurePackage("cm55");
    
    foo1 = cm55.createClass("Foo1",  null);
    foo2 = cm55.createClass("Foo2",  null);
    
    depDetect = cm55.ensurePackage("depDetect");    
    
    bar1 = depDetect.createClass("Bar1",  null);
    bar2 = depDetect.createClass("Bar2",  null);
    
    test = cm55.ensurePackage("test");
    sample = test.createClass("Sample",  null);    
  }

  @Test
  public void stream() {        
    assertThat(
      cm55.classStream(false).map(n->n.getPath()).collect(Collectors.joining(",")),
      equalTo("com.cm55.Foo1,com.cm55.Foo2")
    );
    //ystem.out.println(cm55.classStream(true).map(n->n.getPath()).collect(Collectors.joining(",")));
    assertThat(cm55.classStream(true).map(n->n.getPath()).collect(Collectors.joining(",")),
      equalTo("com.cm55.Foo1,com.cm55.Foo2," + 
          "com.cm55.depDetect.Bar1,com.cm55.depDetect.Bar2,com.cm55.test.Sample")
   );    
  }
  
  @Test
  public void findMaximum() {        
    assertThat(root.findMaximum("com.cm55.depDetect"), equalTo(depDetect));   
    assertThat(root.findMaximum("com.cm55.tttt"), equalTo(cm55)); 
  }
  
  @Test
  public void count() {
    assertThat(root.childNodeCount(false), equalTo(1));
    assertThat(root.childNodeCount(true), equalTo(9));
    
    assertThat(root.childClassCount(false), equalTo(0));
    assertThat(root.childClassCount(true), equalTo(5));
    assertThat(cm55.childClassCount(false), equalTo(2));
    assertThat(cm55.childClassCount(true), equalTo(5));
    
    assertThat(root.childPackageCount(false), equalTo(1));
    assertThat(root.childPackageCount(true), equalTo(4));
  }

  
  @Test
  public void visitPackages() {
//    assertThat(
//      root.packageStream(false).map(n->n.toString()).collect(Collectors.joining("\n")) + "\n",
//      equalTo(
//       "\n" + 
//       "com\n"
//    ));      
//    assertThat(
//      root.packageStream(true).map(n->n.toString()).collect(Collectors.joining("\n")) + "\n",
//      equalTo(
//       "\n" + 
//       "com\n" + 
//       "com.cm55\n" + 
//       "com.cm55.depDetect\n"
//    ));
//    /*
//    assertThat(
//        root.visitStream(VisitOrder.POST).map(n->n.toString()).collect(Collectors.joining("\n")) + "\n",
//        equalTo(
//            "com.cm55.depDetect\n" +
//            "com.cm55\n" + 
//            "com\n" + 
//            "\n"
//      ));
//    */
  }

}
