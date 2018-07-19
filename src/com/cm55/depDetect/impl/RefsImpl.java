package com.cm55.depDetect.impl;

import java.util.*;
import java.util.stream.*;

import com.cm55.depDetect.*;

public class RefsImpl implements Refs {


  final Set<PkgNodeImpl>set;

  private RefsImpl(Set<PkgNodeImpl>set) {
    this.set = set;
  }
  
  RefsImpl() {
    set = new HashSet<>();
  }
  
  void add(PkgNodeImpl node ) {
    set.add(node);
  }
  
  void add(RefsImpl refs) {
    set.addAll(refs.set);
  }
  
  @Override
  public int size() {
    return set.size();
  }
  

  @Override
  public boolean contains(PkgNode node) {
    return set.contains(node);
  }
  
  @Override
  public Stream<PkgNode>stream() {
    return set.stream().map(n->(PkgNode)n).sorted();
  }

  /** 共通部分を取得する */
  @Override
  public RefsImpl getIntersect(Refs that) {
    Set<PkgNodeImpl>set = new HashSet<>(this.set);
    set.retainAll(((RefsImpl)that).set);
    return new RefsImpl(set);
  }

  /** デバッグ用文字列化 */
  @Override
  public String toString() {
    return set.stream().map(n->n.toString()).sorted().collect(Collectors.joining("\n"));
  }

  @Override
  public boolean containsAny(Refs that) {
    return ((RefsImpl)that).set.stream().filter(n->set.contains(n)).findAny().isPresent();
  }
}
