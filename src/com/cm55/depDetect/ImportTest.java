package com.cm55.depDetect;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class ImportTest {

  @Test
  public void test() {
    assertThat(Import.getPkgPath("com.cm55.gs.anydb.AnyDatabase.*", true), equalTo("com.cm55.gs.anydb"));
    assertThat(Import.getPkgPath("com.cm55.gs.anydb.*", false), equalTo("com.cm55.gs.anydb"));
  }
}
