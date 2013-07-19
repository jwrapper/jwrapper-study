/*    */ package jwrapper.hidden;
/*    */ 
/*    */ public class JWSanityTest
/*    */ {
/*    */   public static final int SANITY_CHECK_OK = 55;
/*    */   public static final int SANITY_CHECK_BROKEN = 88;
/*    */ 
/*    */   public static void exitAndReturnSanityCheckOK()
/*    */   {
/*  9 */     System.exit(55);
/*    */   }
/*    */ 
/*    */   public static void exitAndReturnSanityCheckBroken() {
/* 13 */     System.exit(88);
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.hidden.JWSanityTest
 * JD-Core Version:    0.6.2
 */