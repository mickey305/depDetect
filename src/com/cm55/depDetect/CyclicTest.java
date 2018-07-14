package com.cm55.depDetect;

import com.cm55.depDetect.impl.*;

/**
 * このパッケージの中に強制的に循環依存を起こすためのクラス
 * @author ysugimura
 */
public class CyclicTest {

  public CyclicTest() {
    NodeImpl a;
  }

}
