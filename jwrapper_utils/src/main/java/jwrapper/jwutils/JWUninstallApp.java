/*    */ package jwrapper.jwutils;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Method;
/*    */ import jwrapper.SelfDelete;
/*    */ import jwrapper.updater.GenericUpdater;
/*    */ import jwrapper.updater.JWApp;
/*    */ 
/*    */ public class JWUninstallApp
/*    */ {
/*    */   public static void exitJvm_DoUninstall()
/*    */   {
/* 18 */     System.exit(61);
/*    */   }
/*    */ 
/*    */   public static void exitJvm_CancelUninstall()
/*    */   {
/* 24 */     System.exit(63);
/*    */   }
/*    */ 
/*    */   public static void exitJvm_AskUserIfUninstall()
/*    */   {
/* 30 */     System.exit(62);
/*    */   }
/*    */ 
/*    */   public static void doUninstallOfEntireBundle(boolean showProgress)
/*    */     throws IOException
/*    */   {
/* 39 */     if (showProgress) {
/* 40 */       byte[] logo = new byte[0];
/*    */       try {
/* 42 */         logo = (byte[])Class.forName("jwrapper.jwutils.JWSystemUI").getDeclaredMethod("getAppBundleLogoPNG", new Class[0]).invoke(null, new Object[0]);
/*    */       } catch (Throwable t) {
/* 44 */         t.printStackTrace();
/*    */       }
/* 46 */       SelfDelete.deleteSelf(JWSystem.getAppFolder().getParentFile(), GenericUpdater.getUninstallExtras(), null, JWSystem.getAppBundleName(), JWSystem.getAppBundleSplashPNG(), logo);
/*    */     } else {
/* 48 */       SelfDelete.deleteSelf(JWSystem.getAppFolder().getParentFile(), GenericUpdater.getUninstallExtras(), null, "", new byte[0], new byte[0]);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void removeAllStandardShortcuts(boolean removeMacOsApplicationLauncher)
/*    */     throws IOException
/*    */   {
/* 58 */     JWApp[] jwapps = JWApp.getAllJWApps(JWSystem.getAppFolder(), true);
/* 59 */     GenericUpdater.removeAllStandardShortcuts(jwapps, removeMacOsApplicationLauncher);
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.jwutils.JWUninstallApp
 * JD-Core Version:    0.6.2
 */