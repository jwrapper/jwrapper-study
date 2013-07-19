/*    */ package utils.swing.components;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import javax.swing.Icon;
/*    */ import javax.swing.JLabel;
/*    */ 
/*    */ public class JWrapLabel extends JLabel
/*    */ {
/* 10 */   int minWidth = 1;
/*    */ 
/*    */   public JWrapLabel(int minWidth)
/*    */   {
/* 15 */     this.minWidth = minWidth;
/*    */   }
/*    */ 
/*    */   public JWrapLabel(String text)
/*    */   {
/* 20 */     super(text);
/*    */   }
/*    */ 
/*    */   public JWrapLabel()
/*    */   {
/*    */   }
/*    */ 
/*    */   public JWrapLabel(Icon icon, String text)
/*    */   {
/* 30 */     super(text);
/* 31 */     setIcon(icon);
/*    */   }
/*    */ 
/*    */   public Dimension getPreferredSize()
/*    */   {
/* 36 */     Dimension superPreferred = super.getPreferredSize();
/* 37 */     return new Dimension((int)Math.min(this.minWidth, superPreferred.getWidth()), (int)superPreferred.getHeight());
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.components.JWrapLabel
 * JD-Core Version:    0.6.2
 */