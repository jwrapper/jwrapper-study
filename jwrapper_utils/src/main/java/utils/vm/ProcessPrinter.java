/*    */ package utils.vm;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class ProcessPrinter
/*    */ {
/*    */   public static void printAllOutputToStdout(Process p)
/*    */   {
/* 15 */     new ProcessPrinter(p, System.out, System.out);
/*    */   }
/*    */ 
/*    */   public ProcessPrinter(Process p, OutputStream out, OutputStream err) {
/* 19 */     Printer pout = new Printer();
/* 20 */     pout.in = p.getInputStream();
/* 21 */     pout.out = out;
/* 22 */     pout.start();
/*    */ 
/* 24 */     Printer perr = new Printer();
/* 25 */     perr.in = p.getErrorStream();
/* 26 */     perr.out = err;
/* 27 */     perr.start(); } 
/*    */   class Printer extends Thread { InputStream in;
/*    */     OutputStream out;
/*    */     IOException exception;
/*    */ 
/* 34 */     public Printer() { super(); }
/*    */ 
/*    */     public void run() {
/*    */       try {
/* 38 */         byte[] buf = new byte[1024];
/* 39 */         int n = 0;
/* 40 */         while (n != -1) {
/* 41 */           n = this.in.read(buf, 0, 1024);
/* 42 */           if (n > 0)
/* 43 */             this.out.write(buf, 0, n);
/*    */         }
/*    */       }
/*    */       catch (IOException e) {
/* 47 */         this.exception = e;
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.vm.ProcessPrinter
 * JD-Core Version:    0.6.2
 */