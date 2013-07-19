/*    */ package utils.files;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import utils.stream.ProcessPrinter;
/*    */ 
/*    */ public class AutoChmodFile
/*    */ {
/*    */   public static File autoChmodFile(File unchmod, File chmod, boolean force)
/*    */     throws IOException
/*    */   {
/* 11 */     FileUtil.deleteDir(chmod);
/*    */ 
/* 13 */     if ((!force) && (chmod.exists()))
/*    */     {
/* 15 */       return chmod;
/*    */     }
/*    */ 
/* 20 */     System.out.println("[AutoChmod] chmodding " + unchmod + "...");
/*    */     try
/*    */     {
/* 23 */       Process p = Runtime.getRuntime().exec(new String[] { "chmod", "-R", "755", unchmod.getCanonicalPath() });
/* 24 */       new ProcessPrinter(p, System.out, System.err);
/* 25 */       int retCode = p.waitFor();
/*    */ 
/* 27 */       System.out.println("[AutoChmod] Completed " + unchmod + " chmod (" + retCode + ")");
/*    */ 
/* 29 */       p = Runtime.getRuntime().exec(new String[] { "cp", "-rf", unchmod.getCanonicalPath(), chmod.getCanonicalPath() });
/* 30 */       new ProcessPrinter(p, System.out, System.err);
/* 31 */       retCode = p.waitFor();
/*    */ 
/* 33 */       System.out.println("[AutoChmod] Copied " + unchmod + " chmod (" + retCode + " / " + chmod + ")");
/*    */     } catch (InterruptedException xx) {
/* 35 */       throw new IOException("Interrupted while waiting for command to finish");
/*    */     }
/*    */ 
/* 39 */     return chmod;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.files.AutoChmodFile
 * JD-Core Version:    0.6.2
 */