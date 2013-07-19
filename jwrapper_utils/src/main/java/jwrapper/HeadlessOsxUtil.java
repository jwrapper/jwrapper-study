/*    */ package jwrapper;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.File;
/*    */ import java.io.InputStream;
/*    */ import java.lang.reflect.Method;
/*    */ import jwrapper.updater.JWApp;
/*    */ 
/*    */ public class HeadlessOsxUtil
/*    */ {
/*    */   public static void setOSXAppName(String name)
/*    */   {
/*    */     try
/*    */     {
/* 12 */       Class.forName("utils.ostools.osx.OSXAdapter").getDeclaredMethod("setAppName", new Class[] { String.class }).invoke(null, new Object[] { name });
/*    */     } catch (Throwable localThrowable) {
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void requestForeground() {
/*    */     try {
/* 19 */       Class.forName("utils.ostools.osx.OSXAdapter").getDeclaredMethod("requestForeground", new Class[] { Boolean.TYPE }).invoke(null, new Object[] { Boolean.TRUE });
/*    */     } catch (Throwable localThrowable) {
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void setOSXAppDockImage(Object image) {
/*    */     try {
/* 26 */       Class.forName("utils.ostools.osx.OSXAdapter").getDeclaredMethod("setDockIcon", new Class[] { Class.forName("java.awt.Image") }).invoke(null, new Object[] { image });
/*    */     } catch (Throwable localThrowable) {
/*    */     }
/*    */   }
/*    */ 
/*    */   public static Object loadPngBytesFromICNS(File file) {
/*    */     try {
/* 33 */       return Class.forName("utils.swing.icons.ICNSReader").getDeclaredMethod("getPNGFromICNSFile", new Class[] { File.class }).invoke(null, new Object[] { file });
/*    */     }
/*    */     catch (Exception localException) {
/*    */     }
/* 37 */     return null;
/*    */   }
/*    */   public static Object loadImageFromICNS(File file) {
/*    */     try {
/* 41 */       return Class.forName("utils.swing.icons.ICNSReader").getDeclaredMethod("getImageFromICNSFile", new Class[] { File.class }).invoke(null, new Object[] { file });
/*    */     }
/*    */     catch (Exception localException) {
/*    */     }
/* 45 */     return null;
/*    */   }
/*    */   public static Object loadImageFromJWApp(JWApp jwa) {
/*    */     try {
/* 49 */       return Class.forName("javax.imageio.ImageIO").getDeclaredMethod("read", new Class[] { InputStream.class }).invoke(null, new Object[] { new ByteArrayInputStream(jwa.getLogoPNG()) });
/*    */     }
/*    */     catch (Exception x) {
/* 52 */       x.printStackTrace();
/*    */     }
/* 54 */     return null;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.HeadlessOsxUtil
 * JD-Core Version:    0.6.2
 */