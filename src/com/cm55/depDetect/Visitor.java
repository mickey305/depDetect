package com.cm55.depDetect;

/** ノードビジター */
public interface Visitor<T extends Node> {
  public void visited(T node);
}
