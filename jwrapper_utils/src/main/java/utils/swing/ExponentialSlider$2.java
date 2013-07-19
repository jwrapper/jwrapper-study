/*     */ package utils.swing;
/*     */ 
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ class ExponentialSlider$2
/*     */   implements Runnable
/*     */ {
/*     */   final ExponentialSlider.ExampleSlide this$1;
/*     */ 
/*     */   ExponentialSlider$2(ExponentialSlider.ExampleSlide paramExampleSlide)
/*     */   {
/*   1 */     this.this$1 = paramExampleSlide;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 159 */     this.this$1.tmp.add(new JLabel("DONE!", 0));
/* 160 */     this.this$1.tmp.invalidate();
/* 161 */     this.this$1.tmp.revalidate();
/* 162 */     this.this$1.repaint();
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.ExponentialSlider.2
 * JD-Core Version:    0.6.2
 */