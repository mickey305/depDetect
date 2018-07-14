package com.cm55.depDetect.impl;

import java.util.*;
import java.util.stream.*;

import com.cm55.depDetect.*;

public class UnknownsImpl implements Unknowns {

  private final Set<String>set;

  UnknownsImpl() {
    set = new HashSet<String>();
  }
  
  private UnknownsImpl(Set<String>set) {
    this.set = set;
  }
  
  void add(String unknown) {
    set.add(unknown);
  }
  
  void add(UnknownsImpl that) {
    set.addAll(that.set);
  }
  
  UnknownsImpl duplicate() {
    return new UnknownsImpl(new HashSet<>(set));
  }
  
  public Stream<String>stream() {
    return set.stream().sorted();
  }
}
