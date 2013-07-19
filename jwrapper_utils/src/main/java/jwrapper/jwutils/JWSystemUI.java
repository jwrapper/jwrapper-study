/*    */ package jwrapper.jwutils;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import javax.imageio.ImageIO;
/*    */ import jwrapper.HeadlessOsxUtil;
/*    */ import jwrapper.updater.GenericUpdater;
/*    */ import jwrapper.updater.JWApp;
/*    */ 
/*    */ public class JWSystemUI
/*    */ {
/* 20 */   static Object LOCK = new Object();
/*    */   static Image cachedImage;
/*    */   static Image cachedBundleImage;
/*    */ 
/*    */   public static Image getMyAppLogoImage()
/*    */     throws IOException
/*    */   {
/* 28 */     synchronized (LOCK) {
/* 29 */       if (cachedImage == null) {
/* 30 */         cachedImage = ImageIO.read(new ByteArrayInputStream(JWApp.getMyVirtualApp().getLogoPNG()));
/*    */       }
/*    */     }
/* 33 */     return cachedImage;
/*    */   }
/*    */ 
/*    */   public static Image getAppBundleLogoImage()
/*    */     throws IOException
/*    */   {
/* 42 */     synchronized (LOCK) {
/* 43 */       if (cachedBundleImage == null) {
/* 44 */         cachedBundleImage = ImageIO.read(new ByteArrayInputStream(getAppBundleLogoPNG()));
/*    */       }
/*    */     }
/* 47 */     return cachedBundleImage;
/*    */   }
/*    */ 
/*    */   public static byte[] getAppBundleLogoPNG()
/*    */     throws IOException
/*    */   {
/* 54 */     return (byte[])HeadlessOsxUtil.loadPngBytesFromICNS(new File(JWSystem.getAppFolder(), GenericUpdater.getIcnsFileNameFor(JWSystem.getAppBundleName())));
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.jwutils.JWSystemUI
 * JD-Core Version:    0.6.2
 */