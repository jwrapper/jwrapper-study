/*    */ package utils.stream;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class ProcessPrinter
/*    */ {
/*    */   Printer pout;
/*    */   Printer perr;
/*    */ 
/*    */   public ProcessPrinter(Process p, OutputStream out, OutputStream err)
/*    */   {
/* 13 */     this.pout = new Printer();
/* 14 */     this.pout.in = p.getInputStream();
/* 15 */     this.pout.out = out;
/* 16 */     this.pout.start();
/*    */ 
/* 18 */     this.perr = new Printer();
/* 19 */     this.perr.in = p.getErrorStream();
/* 20 */     this.perr.out = err;
/* 21 */     this.perr.start();
/*    */   }
/*    */ 
/*    */   public void join() throws InterruptedException {
/* 25 */     this.perr.join();
/*    */   }
/*    */ 
/*    */   public void waitForAllOutput() throws InterruptedException {
/* 29 */     this.pout.join();
/* 30 */     this.perr.join();
/*    */   }
/*    */ 
/*    */   public boolean isAlive() {
/* 34 */     return this.perr.isAlive();
/*    */   }
/*    */   class Printer extends Thread { InputStream in;
/*    */     OutputStream out;
/*    */     IOException exception;
/*    */ 
/*    */     Printer() {  } 
/*    */     public void run() { try { byte[] buf = new byte[1024];
/* 44 */         int n = 0;
/* 45 */         while (n != -1) {
/* 46 */           n = this.in.read(buf, 0, 1024);
/* 47 */           if ((n > 0) && 
/* 48 */             (this.out != null)) this.out.write(buf, 0, n); 
/*    */         }
/*    */       }
/*    */       catch (IOException e)
/*    */       {
/* 52 */         this.exception = e;
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.stream.ProcessPrinter
 * JD-Core Version:    0.6.2
 */