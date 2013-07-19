/*    */ package utils.sync;
/*    */ 
/*    */ public class UnqueuedSemaphore
/*    */ {
/* 11 */   Object LOCK = new Object();
/*    */   private int val;
/*    */ 
/*    */   public UnqueuedSemaphore(int initial)
/*    */   {
/* 16 */     this.val = (initial - 1);
/*    */   }
/*    */ 
/*    */   public void doWait(int count) {
/* 20 */     synchronized (this.LOCK) {
/* 21 */       for (int i = 0; i < count; i++) {
/* 22 */         this.val -= 1;
/*    */         try {
/* 24 */           if (this.val < 0)
/* 25 */             this.LOCK.wait();
/*    */         }
/*    */         catch (InterruptedException e) {
/* 28 */           e.printStackTrace();
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public void doSignal(int count) {
/* 35 */     synchronized (this.LOCK) {
/* 36 */       for (int i = 0; i < count; i++) {
/* 37 */         if (this.val < 2147483647) this.val += 1;
/* 38 */         if (this.val <= 0) this.LOCK.notify();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.sync.UnqueuedSemaphore
 * JD-Core Version:    0.6.2
 */