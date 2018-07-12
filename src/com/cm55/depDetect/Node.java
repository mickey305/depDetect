package com.cm55.depDetect;

public class Node implements Comparable<Node> {

  public final PkgNode parent;
  public final String name;

  
  protected Node(PkgNode parent, String name) {
    this.parent = parent;
    this.name = name;
  }
  
  @Override
  public String toString() {
    if (parent == null) return name;
    String parentPath = parent.toString();
    if (parentPath.length() == 0) return name;
    return parentPath + "." + name;
  }

  @Override
  public int compareTo(Node o) {
    return this.name.compareTo(o.name);
  }
}
