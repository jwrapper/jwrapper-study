/*    */ package jwrapper.ui;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class CanvasPanel extends JPanel
/*    */ {
/*    */   public void paintComponent(Graphics g)
/*    */   {
/* 13 */     super.paintComponent(g);
/*    */ 
/* 15 */     Graphics2D g2d = (Graphics2D)g;
/* 16 */     for (int i = 0; i < getWidth(); i += 2)
/*    */     {
/* 18 */       if (i % 4 == 0)
/* 19 */         g2d.setColor(new Color(250, 250, 250));
/*    */       else
/* 21 */         g2d.setColor(new Color(245, 245, 245));
/* 22 */       g2d.drawLine(i, 0, i, getHeight());
/* 23 */       g2d.drawLine(i + 1, 0, i + 1, getHeight());
/*    */     }
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.ui.CanvasPanel
 * JD-Core Version:    0.6.2
 */