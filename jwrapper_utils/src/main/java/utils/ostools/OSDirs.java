/*   */ package utils.ostools;
/*   */ 
/*   */ import java.io.File;
/*   */ 
/*   */ public class OSDirs
/*   */ {
/*   */   public static File getDesktopDir()
/*   */   {
/* 9 */     return new File(System.getProperty("user.home"), "Desktop");
/*   */   }
/*   */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.ostools.OSDirs
 * JD-Core Version:    0.6.2
 */