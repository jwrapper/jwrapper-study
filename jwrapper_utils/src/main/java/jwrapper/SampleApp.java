/*    */ package jwrapper;
/*    */ 
/*    */ import javax.swing.JOptionPane;
/*    */ import jwrapper.jwutils.JWSystem;
/*    */ 
/*    */ public class SampleApp
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/*    */     String myJwVersion;
/*    */     try
/*    */     {
/* 13 */       myJwVersion = JWSystem.getAppBundleVersion();
/*    */     }
/*    */     catch (Exception x)
/*    */     {
/*    */       String myJwVersion;
/* 16 */       myJwVersion = "(not running inside JWrapper)";
/*    */     }
/*    */ 
/* 19 */     JOptionPane.showMessageDialog(null, "Minimal App (Hello World) " + JWSystem.getAppBundleVersion());
/* 20 */     System.exit(0);
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.SampleApp
 * JD-Core Version:    0.6.2
 */