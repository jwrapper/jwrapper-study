/*    */ package utils.progtools;
/*    */ 
/*    */ import java.io.OutputStream;
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class CheapTimingPrintStream extends PrintStream
/*    */ {
/*  8 */   public static boolean usedForStdout = false;
/*    */ 
/* 10 */   long previous = getMillis();
/*    */ 
/*    */   public CheapTimingPrintStream(OutputStream out) {
/* 13 */     super(out);
/*    */   }
/*    */ 
/*    */   public long getMillis() {
/* 17 */     return System.currentTimeMillis() % 1000000L + 8000000L;
/*    */   }
/*    */ 
/*    */   public void println(Object o) {
/* 21 */     long now = getMillis();
/* 22 */     super.println(now + " (+" + (now - this.previous) + ") " + o);
/* 23 */     this.previous = now;
/*    */   }
/*    */ 
/*    */   public void println(String s) {
/* 27 */     println(s);
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.progtools.CheapTimingPrintStream
 * JD-Core Version:    0.6.2
 */