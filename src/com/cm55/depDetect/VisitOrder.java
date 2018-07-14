package com.cm55.depDetect;

/**
 * 木構造探索時の訪問順
 * @author ysugimura
 *
 */
public enum VisitOrder {
  /** 前順、木の根部分から先に訪問していく */
  PRE,
  /** 後順、木の葉の方から先に訪問していく */
  POST;
}
