import java.io.*;

import com.cm55.depDetect.*;
import com.cm55.depDetect.impl.*;

public class Main {
  public static void main(String[] args) throws IOException {
    
    PkgNode root = TreeCreator.create(
        /*
      "C:\\Users\\admin\\git\\shouhinstaff\\shouhinstaff\\src_base",
      "C:\\Users\\admin\\git\\shouhinstaff\\shouhinstaff\\src_common",
      "C:\\Users\\admin\\git\\shouhinstaff\\shouhinstaff\\src_server",
      "C:\\Users\\admin\\git\\shouhinstaff\\shouhinstaff\\src_term"
      */
        "C:\\devel\\workspace-neon\\github_depDetect\\src"
    );

    // 不明importを表示
    System.out.println("不明import");
    root.getAllUnknowns().stream().forEach(System.out::println);
    
    // 木構造を表示
    System.out.println("\n木構造");
    System.out.println(root.treeString());
    
    //root.visitPackages(VisitOrder.PRE,  System.out::println);
    
    // 循環参照を表示
    root.visitPackages(VisitOrder.PRE, pkg -> {
      Refs cyclics = pkg.getCyclics();
      if (cyclics.count() == 0)
        return;
      System.out.println("\n循環依存 " + pkg + "\n" + cyclics);
      pkg.visitClassesStream().forEach(cls-> {
        if (cls.getDepsTo().containsAny(cyclics)) {
          System.out.println("原因クラス " + cls);
        }
      });
    });

    // 不明パッケージを表示
    System.out.println("");
    Unknowns allUnkowns = root.getAllUnknowns();
    
  }

}
