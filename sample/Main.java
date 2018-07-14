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

    // 循環参照を表示
    root.visitPackages(VisitOrder.PRE, pkg -> {
      if (pkg.getCyclics().count() == 0)
        return;
      System.out.println("-------" + pkg + "\n" + pkg.getCyclics());
    });

    // 不明パッケージを表示
    System.out.println("");
    Unknowns allUnkowns = root.getAllUnknowns();
    
  }

}
