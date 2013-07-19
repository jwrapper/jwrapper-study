/*    */ package jwrapper;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import utils.files.FileUtil;
/*    */ import utils.ostools.OS;
/*    */ 
/*    */ public class JRECopy
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 12 */     copyJRE(new File("/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/bundle/Home"), new File("/Users/aem/Desktop/JRE2"));
/*    */   }
/*    */ 
/*    */   private static void copyOsxBundleDir(File src, File dest) throws IOException {
/* 16 */     File[] all = src.listFiles();
/* 17 */     for (int i = 0; i < all.length; i++) {
/* 18 */       String name = all[i].getName();
/*    */ 
/* 20 */       if (name.equals("Home"))
/*    */       {
/* 22 */         Runtime.getRuntime().exec(
/* 23 */           new String[] { 
/* 24 */           "ln", 
/* 25 */           "-s", 
/* 26 */           "../", 
/* 27 */           "Home" }, 
/* 28 */           null, dest);
/*    */       }
/* 30 */       else FileUtil.copyFileOrDir(all[i], new File(dest, name));
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void copyJRE(File src, File dest)
/*    */     throws IOException
/*    */   {
/*    */     File[] all;
/* 36 */     if (OS.isMacOS())
/*    */     {
/* 39 */       dest.delete();
/* 40 */       dest.mkdirs();
/*    */ 
/* 42 */       all = src.listFiles();
/* 43 */       for (int i = 0; i < all.length; i++) {
/* 44 */         String name = all[i].getName();
/* 45 */         if (name.equals("bundle"))
/* 46 */           copyOsxBundleDir(all[i], new File(dest, "bundle"));
/*    */         else
/* 48 */           FileUtil.copyFileOrDir(all[i], new File(dest, name));
/*    */       }
/*    */     }
/*    */     else
/*    */     {
/* 53 */       FileUtil.copyFileOrDir(src, dest);
/*    */     }
/*    */ 
/* 56 */     if (!OS.isWindows())
/*    */     {
/* 58 */       all = Runtime.getRuntime().exec(
/* 59 */         new String[] { 
/* 60 */         "chmod", 
/* 61 */         "-R", 
/* 62 */         "755", 
/* 63 */         new File(dest, "bin").getCanonicalPath() });
/*    */     }
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.JRECopy
 * JD-Core Version:    0.6.2
 */