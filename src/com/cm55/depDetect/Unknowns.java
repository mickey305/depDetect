package com.cm55.depDetect;

import java.util.stream.*;

/**
 * 指定されたソースツリー外へのimportによる参照文字列の集合。
 * @author ysugimura
 */
public interface Unknowns {
  
  /** 全文字列のソートされたストリームを返す */
  public Stream<String>stream();
  
}
