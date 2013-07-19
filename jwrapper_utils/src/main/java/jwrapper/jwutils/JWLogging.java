/*    */ package jwrapper.jwutils;
/*    */ 
/*    */ import java.io.File;
/*    */ import jwrapper.logging.LogPoller;
/*    */ import jwrapper.logging.ProcessOutputUtil;
/*    */ 
/*    */ public class JWLogging
/*    */ {
/*    */   public static void writeProcessResult(int processResultCode)
/*    */   {
/* 17 */     String source = JWSystem.getMyAppName();
/* 18 */     ProcessOutputUtil.logProcessResult(source, processResultCode);
/*    */   }
/*    */ 
/*    */   public static void appendProcessMessage(String msg)
/*    */   {
/* 27 */     String source = JWSystem.getMyAppName();
/* 28 */     ProcessOutputUtil.logProcessMessage(source, msg);
/*    */   }
/*    */ 
/*    */   public static void appendProcessError(String err)
/*    */   {
/* 36 */     String source = JWSystem.getMyAppName();
/* 37 */     ProcessOutputUtil.logProcessError(source, err);
/*    */   }
/*    */ 
/*    */   public static void appendProcessTrace(Throwable t)
/*    */   {
/* 45 */     String source = JWSystem.getMyAppName();
/* 46 */     ProcessOutputUtil.logProcessTrace(source, t);
/*    */   }
/*    */ 
/*    */   public static LogPoller newLogPoller()
/*    */   {
/* 56 */     return new LogPoller(JWSystem.getAppFolder().getParentFile());
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.jwutils.JWLogging
 * JD-Core Version:    0.6.2
 */