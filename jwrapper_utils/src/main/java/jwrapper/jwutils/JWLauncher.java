/*    */ package jwrapper.jwutils;
/*    */ 
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class JWLauncher
/*    */ {
/*    */   public static void setCreateDebugLogs(Properties lp)
/*    */   {
/* 13 */     lp.setProperty("debug_logging", "true");
/*    */   }
/*    */ 
/*    */   public static void setCreateDebugLogsUntil(Properties lp, long stopCreatingLogsAfter) {
/* 17 */     lp.setProperty("debug_logging_until", stopCreatingLogsAfter);
/*    */   }
/*    */ 
/*    */   public static void setElevateToAdminOrRootViaDialog(Properties lp) {
/* 21 */     lp.setProperty("launch_elevate", "true");
/*    */   }
/*    */ 
/*    */   public static void setElevateToRootAutomatically(Properties lp, String password)
/*    */   {
/* 32 */     lp.setProperty("launch_elevate_silent", password);
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.jwutils.JWLauncher
 * JD-Core Version:    0.6.2
 */