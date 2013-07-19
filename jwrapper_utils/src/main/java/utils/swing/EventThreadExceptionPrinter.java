/*    */ package utils.swing;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import javax.swing.SwingUtilities;
/*    */ 
/*    */ public class EventThreadExceptionPrinter
/*    */   implements Thread.UncaughtExceptionHandler
/*    */ {
/* 26 */   static boolean inited = false;
/*    */ 
/*    */   public void handle(Throwable thrown)
/*    */   {
/*    */     try
/*    */     {
/* 10 */       handleException(Thread.currentThread().getName(), thrown);
/*    */     } catch (Throwable localThrowable) {
/*    */     }
/*    */   }
/*    */ 
/*    */   public void uncaughtException(Thread thread, Throwable thrown) {
/*    */     try {
/* 17 */       handleException(thread.getName(), thrown); } catch (Throwable localThrowable) {
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void handleException(String tname, Throwable thrown) {
/* 22 */     System.err.println("[AWT/Swing Exception] Exception on thread " + tname + " (" + thrown + ")");
/* 23 */     thrown.printStackTrace();
/*    */   }
/*    */ 
/*    */   public static void setup()
/*    */   {
/* 28 */     if (!inited) {
/* 29 */       inited = true;
/* 30 */       Thread.setDefaultUncaughtExceptionHandler(new EventThreadExceptionPrinter());
/* 31 */       System.setProperty("sun.awt.exception.handler", EventThreadExceptionPrinter.class.getName());
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) throws Exception {
/* 36 */     setup();
/*    */ 
/* 39 */     SwingUtilities.invokeLater(new Runnable() {
/*    */       public void run() {
/* 41 */         null.toString();
/*    */       }
/*    */     });
/* 46 */     null.toString();
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.EventThreadExceptionPrinter
 * JD-Core Version:    0.6.2
 */