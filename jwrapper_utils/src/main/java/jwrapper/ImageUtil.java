/*    */ package jwrapper;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.InputStream;
/*    */ import java.io.PrintStream;
/*    */ import javax.imageio.ImageIO;
/*    */ 
/*    */ public class ImageUtil
/*    */ {
/*    */   public static Image load(String name)
/*    */   {
/* 11 */     System.out.println("Loading image " + name);
/*    */     try {
/* 13 */       InputStream resourceAsStream = ImageUtil.class.getResourceAsStream("/" + name);
/* 14 */       if (resourceAsStream == null)
/*    */       {
/* 16 */         System.out.println("[ImageUtil] Unable to load image as resource '" + name + "'");
/* 17 */         return null;
/*    */       }
/* 19 */       return ImageIO.read(new BufferedInputStream(resourceAsStream));
/*    */     } catch (Exception x) {
/* 21 */       x.printStackTrace();
/* 22 */     }return null;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.ImageUtil
 * JD-Core Version:    0.6.2
 */