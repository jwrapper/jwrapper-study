/*    */ package utils.vm;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.InputStreamReader;
/*    */ 
/*    */ public class LoadmeGenerator
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 12 */     StringBuffer sb = new StringBuffer();
/*    */ 
/* 14 */     sb.append("package utils.vm;\n");
/* 15 */     sb.append("public class Loadme {\n\n");
/* 16 */     sb.append("public static String[] loadme = new String[]{\n");
/*    */ 
/* 18 */     BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream("Classes_142"))));
/*    */ 
/* 20 */     String kage = "";
/*    */ 
/* 22 */     String line = br.readLine();
/* 23 */     while (line != null) {
/* 24 */       line = line.trim();
/* 25 */       if (line.startsWith("[")) {
/* 26 */         kage = line.substring(1, line.length() - 1);
/*    */       }
/* 28 */       else if (line.length() > 0) {
/* 29 */         String claz = kage + "." + line;
/* 30 */         sb.append("\"" + claz + "\",\n");
/*    */       }
/*    */ 
/* 34 */       line = br.readLine();
/*    */     }
/* 36 */     sb.append("};\n");
/* 37 */     sb.append("}\n");
/* 38 */     FileOutputStream fout = new FileOutputStream("src" + File.separator + "utils" + File.separator + "vm" + File.separator + "Loadme.java");
/* 39 */     fout.write(sb.toString().getBytes());
/* 40 */     fout.close();
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.vm.LoadmeGenerator
 * JD-Core Version:    0.6.2
 */