/*     */ package utils.swing.components;
/*     */ 
/*     */ class SwipeImagePanel$1
/*     */   implements Runnable
/*     */ {
/*     */   final SwipeImagePanel.Slide this$1;
/*     */ 
/*     */   SwipeImagePanel$1(SwipeImagePanel.Slide paramSlide)
/*     */   {
/*   1 */     this.this$1 = paramSlide;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 137 */     SwipeImagePanel.Slide.access$0(this.this$1).repaint();
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.components.SwipeImagePanel.1
 * JD-Core Version:    0.6.2
 */