/*    */ package utils.string;
/*    */ 
/*    */ public class FnvHash
/*    */ {
/*    */   private static final int offset = -2128831035;
/*    */   private static final int prime = 16777619;
/*    */ 
/*    */   public static int hash(String dat)
/*    */   {
/*  8 */     int hash = -2128831035;
/*    */ 
/* 10 */     int len = dat.length();
/*    */ 
/* 12 */     for (int i = 0; i < len; i++) {
/* 13 */       hash ^= 0xFFFF & dat.charAt(i);
/* 14 */       hash *= 16777619;
/*    */     }
/*    */ 
/* 17 */     return hash;
/*    */   }
/*    */   public static int hash(byte[] dat) {
/* 20 */     int hash = -2128831035;
/*    */ 
/* 22 */     int len = dat.length;
/*    */ 
/* 24 */     for (int i = 0; i < len; i++) {
/* 25 */       hash ^= 0xFF & dat[i];
/* 26 */       hash *= 16777619;
/*    */     }
/*    */ 
/* 29 */     return hash;
/*    */   }
/*    */   public static int hash(int[] dat) {
/* 32 */     return hash(dat, 1);
/*    */   }
/*    */   public static int hash(int[] dat, int increment) {
/* 35 */     int hash = -2128831035;
/*    */ 
/* 37 */     int len = dat.length;
/*    */ 
/* 39 */     for (int i = 0; i < len; i += increment) {
/* 40 */       hash ^= dat[i];
/* 41 */       hash *= 16777619;
/*    */     }
/*    */ 
/* 44 */     return hash;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.string.FnvHash
 * JD-Core Version:    0.6.2
 */