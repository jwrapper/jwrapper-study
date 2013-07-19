/*    */ package jwrapper;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.lang.reflect.Method;
/*    */ import jwrapper.updater.JWApp;
/*    */ 
/*    */ public class HeadlessVirtualAppChooserUtil
/*    */ {
/*    */   public static String chooseVirtualApp(File appdir, JWApp[] apps)
/*    */   {
/*    */     try
/*    */     {
/* 15 */       Method method = Class.forName("jwrapper.ui.JWAppletChooserFrame").getDeclaredMethod("chooseVirtualApp", new Class[] { File.class, [Ljwrapper.updater.JWApp.class });
/* 16 */       return (String)method.invoke(null, new Object[] { appdir, apps });
/*    */     } catch (Exception x) {
/* 18 */       x.printStackTrace();
/* 19 */     }return null;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.HeadlessVirtualAppChooserUtil
 * JD-Core Version:    0.6.2
 */