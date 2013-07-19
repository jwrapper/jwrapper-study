/*    */ package utils.files;
/*    */ 
/*    */ import java.io.File;
/*    */ 
/*    */ public class AtomicSafeDeleter
/*    */ {
/*    */   public static boolean deleteIfNotInUse(File folder)
/*    */   {
/* 12 */     File safeToDelete = new File(folder + ".pending_deletion");
/* 13 */     if (folder.renameTo(safeToDelete)) {
/* 14 */       FileUtil.deleteDir(safeToDelete);
/* 15 */       return true;
/*    */     }
/* 17 */     return false;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.files.AtomicSafeDeleter
 * JD-Core Version:    0.6.2
 */