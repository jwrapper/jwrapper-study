/*    */ package jwrapper;
/*    */ 
/*    */ import SevenZip.LzmaAlone;
/*    */ import java.io.File;
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class LzmaUtil
/*    */ {
/*    */   public static void compress(File f, boolean compression)
/*    */     throws Exception
/*    */   {
/*  9 */     if (!compression)
/* 10 */       System.out.println("Compressing (but not really!)...");
/*    */     else {
/* 12 */       System.out.println("Compressing...");
/*    */     }
/* 14 */     File ef = new File(f.getAbsolutePath() + ".encoding");
/*    */     String[] args;
/*    */     String[] args;
/* 17 */     if (!compression)
/*    */     {
/* 19 */       args = new String[] { 
/* 20 */         "e", 
/* 23 */         "-d0", 
/* 24 */         f.getAbsolutePath(), 
/* 25 */         ef.getAbsolutePath() };
/*    */     }
/*    */     else
/*    */     {
/* 30 */       args = new String[] { 
/* 31 */         "e", 
/* 34 */         f.getAbsolutePath(), 
/* 35 */         ef.getAbsolutePath() };
/*    */     }
/*    */ 
/* 39 */     LzmaAlone.main(args);
/*    */ 
/* 41 */     f.delete();
/* 42 */     ef.renameTo(f);
/*    */   }
/*    */ 
/*    */   public static void compress(File f) throws Exception {
/* 46 */     compress(f, false);
/*    */   }
/*    */   public static File decompress(File f, File dest) throws Exception {
/* 49 */     System.out.println("Decompressing...");
/* 50 */     String[] args = { 
/* 51 */       "d", 
/* 52 */       f.getAbsolutePath(), 
/* 53 */       dest.getAbsolutePath() };
/*    */ 
/* 56 */     LzmaAlone.main(args);
/*    */ 
/* 58 */     return dest;
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) throws Exception
/*    */   {
/* 63 */     decompress(new File("build/1.4.2.l2"), new File("build/1.4.2.source"));
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.LzmaUtil
 * JD-Core Version:    0.6.2
 */