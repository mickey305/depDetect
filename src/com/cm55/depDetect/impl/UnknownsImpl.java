package com.cm55.depDetect.impl;

import java.util.*;
import java.util.stream.*;

import com.cm55.depDetect.*;

public class UnknownsImpl implements Unknowns {

  Set<String>set = new HashSet<String>();

  void add(String unknown) {
    set.add(unknown);
  }
  
  void add(UnknownsImpl that) {
    set.addAll(that.set);
  }
  
  public Stream<String>stream() {
    return set.stream().sorted();
  }
}
