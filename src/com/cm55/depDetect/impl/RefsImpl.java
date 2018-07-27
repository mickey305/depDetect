package com.cm55.depDetect.impl;

import java.util.*;
import java.util.stream.*;

import com.cm55.depDetect.*;

/**
 * パッケージ参照実装
 * @author ysugimura
 */
public class RefsImpl implements Refs {

  /** パッケージ集合 */
  final Set<PkgNodeImpl>set;

  /** 集合を指定する */
  private RefsImpl(Set<PkgNodeImpl>set) {
    this.set = set;
  }

  /** 空集合を作成する */
  RefsImpl() {
    set = new HashSet<>();
  }
  
  /** パッケージを追加する */
  void add(PkgNodeImpl node) {
    set.add(node);
  }

  /** 他の集合を追加する */
  void add(RefsImpl refs) {
    set.addAll(refs.set);
  }

  /** 数を取得する */
  @Override
  public int size() {
    return set.size();
  }
  
  /** 指定パッケージが含まれるかを調べる */
  @Override
  public boolean contains(PkgNode node) {
    return set.contains(node);
  }
  
  @Override
  public boolean containsUnder(PkgNode node) {
    return stream().filter(n->node.isAscendOf(n)).findAny().orElse(null) != null;
  }
  
  /** 全パッケージを名前順で取得する */
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

  /** 共通部分があるかを調べる */
  @Override
  public boolean containsAny(Refs that) {
    return ((RefsImpl)that).set.stream().filter(n->set.contains(n)).findAny().isPresent();
  }
  
  /** 指定パッケージ以下のパッケージを削除する */
  void removeDescend(PkgNode parent) {
    Arrays.stream(set.toArray(new PkgNodeImpl[0])).forEach(pkg-> {
      if (parent.isAscendOf(pkg)) set.remove(pkg);
    });
  }
}