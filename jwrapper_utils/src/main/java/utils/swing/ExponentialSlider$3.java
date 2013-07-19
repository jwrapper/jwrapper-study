/*     */ package utils.swing;
/*     */ 
/*     */ class ExponentialSlider$3
/*     */   implements Runnable
/*     */ {
/*     */   final ExponentialSlider.ExampleSlide this$1;
/*     */   private final int val$x2;
/*     */   private final int val$i;
/*     */   private final int val$j;
/*     */   private final int val$k;
/*     */ 
/*     */   ExponentialSlider$3(ExponentialSlider.ExampleSlide paramExampleSlide, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*   1 */     this.this$1 = paramExampleSlide; this.val$x2 = paramInt1; this.val$i = paramInt2; this.val$j = paramInt3; this.val$k = paramInt4;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 174 */     this.this$1.setBounds(this.val$x2, this.val$i, this.val$j, this.val$k);
/* 175 */     this.this$1.repaint();
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.ExponentialSlider.3
 * JD-Core Version:    0.6.2
 */