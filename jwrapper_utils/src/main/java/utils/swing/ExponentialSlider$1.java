/*     */ package utils.swing;
/*     */ 
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ class ExponentialSlider$1
/*     */   implements Runnable
/*     */ {
/*     */   final ExponentialSlider.ExampleSlide this$1;
/*     */ 
/*     */   ExponentialSlider$1(ExponentialSlider.ExampleSlide paramExampleSlide)
/*     */   {
/*   1 */     this.this$1 = paramExampleSlide;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 127 */     this.this$1.tmp.removeAll();
/* 128 */     this.this$1.tmp.invalidate();
/* 129 */     this.this$1.tmp.revalidate();
/* 130 */     this.this$1.repaint();
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.ExponentialSlider.1
 * JD-Core Version:    0.6.2
 */