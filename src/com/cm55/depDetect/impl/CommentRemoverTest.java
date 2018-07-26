package com.cm55.depDetect.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import /* */ java.io.*;
import java.nio.file.*;

import org.junit.*;

import com.cm55.depDetect.impl.CommentRemover.*;

public class CommentRemoverTest {


  @Test
  public void test() throws IOException {
    String s = "C:\\Users\\admin\\git\\shouhinstaff\\shouhinstaff\\src_base\\com\\cm55\\gs\\dev\\impl\\JavaCommSerialPortDev.java";
   
    String r = CommentRemover.remove(Paths.get(s));
    //ystem.out.println(r);
  }


  @Test
  public void skipStringTest() throws IOException {
    CharReader r = new CharReader("test", "\\a\\\"\";");    
    String s = CommentRemover.skipStringOrChar('"', r);
    assertThat(s + r.getRest(), equalTo("\"\\a\\\"\";"));
  }
  
  @Test
  public void SkipBlockCommentTest1() throws IOException {
    CharReader r = new CharReader("test", " コメントの例 **/java.io.*;");    
    String s = CommentRemover.skipBlockComment(r);
    assertThat(s + r.getRest(), equalTo(" java.io.*;"));
  }
  
  @Test
  public void SkipBlockCommentTest2() throws IOException {
    CharReader r = new CharReader("test", " \nコメント\nの例 **/java.io.*;");    
    String s = CommentRemover.skipBlockComment(r);
    assertThat(s + r.getRest(), equalTo("\njava.io.*;"));
  }
  
  @Test
  public void SkipLineCommentTest1() throws IOException {
    CharReader r = new CharReader("test", "");    
    String s = CommentRemover.skipLineComment(r);
    assertThat(s + r.getRest(), equalTo(""));
  }
  
}
