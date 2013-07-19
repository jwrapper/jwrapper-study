/*    */ package utils.files;
/*    */ 
/*    */ import java.io.File;
/*    */ 
/*    */ public class PathUtil
/*    */ {
/*    */   public static String makePathNonWindows(String path)
/*    */   {
/*  8 */     path = path.replace('\\', '/');
/*  9 */     return path;
/*    */   }
/*    */ 
/*    */   public static String makePathNative(String path) {
/* 13 */     path = path.replace('\\', File.separatorChar);
/* 14 */     path = path.replace('/', File.separatorChar);
/* 15 */     return path;
/*    */   }
/*    */   public static String makePathForwardSlashes(String path) {
/* 18 */     path = path.replace('/', File.separatorChar);
/* 19 */     return path;
/*    */   }
/*    */   public static String removeTrailing(String path) {
/* 22 */     if ((path.endsWith("\\")) || (path.endsWith("/"))) {
/* 23 */       path = path.substring(0, path.length() - 1);
/*    */     }
/* 25 */     return path;
/*    */   }
/*    */   public static String ensureTrailing(String path) {
/* 28 */     return removeTrailing(path) + File.separator;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.files.PathUtil
 * JD-Core Version:    0.6.2
 */