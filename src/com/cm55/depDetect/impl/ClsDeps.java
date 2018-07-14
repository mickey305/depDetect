package com.cm55.depDetect.impl;

/**
 * クラスによる依存
 * @author ysugimura
 *
 */
class ClsDeps {
  RefsImpl depends;
  UnknownsImpl unknowns;
  ClsDeps(RefsImpl depends, UnknownsImpl unknowns) {
    this.depends = depends;
    this.unknowns = unknowns;
  }
}
