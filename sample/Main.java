import java.io.*;
import java.util.*;

import com.cm55.depDetect.*;
import com.cm55.depDetect.impl.*;

public class Main {
  public static void main(String[] args) throws IOException {
    
    PkgNode root = BinTreeCreator.create(
      null,
      Arrays.stream(new String[] {
        "C:\\devel\\workspace-neon\\github_depDetect\\bin\\default"
        //  "C:\\devel\\workspace-neon\\github_depDetect\\bin\\default\\com\\cm55\\depDetect\\ClsNode.class"
      })
    );

    /*
    // 不明importを表示
    System.out.println("不明import");
    root.getUnknowns(true).stream().forEach(System.out::println);
    
    // 木構造を表示
    System.out.println("\n木構造");
    System.out.println(root.treeString());
    
    // 循環参照を表示
    root.visitPackages(VisitOrder.PRE, pkg -> {
      Refs cyclics = pkg.getCyclics(false);
      if (cyclics.size() == 0)
        return;
      System.out.println("\n循環依存発生パッケージ:" + pkg);
      cyclics.stream().forEach(cyc-> {
        System.out.println(" 依存先パッケージ：" + cyc);
        pkg.childClasses(true).forEach(cls-> {
          if (cls.getDepsTo().contains(cyc)) {
            System.out.println("  原因クラス:" + cls);
          }
        });
      });
    });    
    */
  }
}
