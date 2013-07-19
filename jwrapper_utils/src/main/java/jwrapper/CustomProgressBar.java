/*    */ package jwrapper;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Font;
/*    */ import java.awt.GradientPaint;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.RenderingHints;
/*    */ import java.awt.font.TextLayout;
/*    */ import java.awt.geom.Rectangle2D;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class CustomProgressBar extends JPanel
/*    */ {
/* 16 */   protected static final Color BACKGROUND = Color.WHITE;
/* 17 */   protected static Color BORDERCOLOR = new Color(80, 129, 190);
/* 18 */   protected static Color TEXTCOLOR = new Color(30, 30, 30);
/* 19 */   protected static Color SHADE_TOP = new Color(185, 225, 247);
/* 20 */   protected static Color SHADE_BOTTOM = new Color(157, 199, 230);
/*    */   private int value;
/*    */   private int min;
/*    */   private int max;
/*    */   private Dimension dim;
/* 24 */   private String text = "";
/* 25 */   private String postfix = "";
/*    */ 
/*    */   public CustomProgressBar(int min, int max, Color background)
/*    */   {
/* 29 */     this.min = min;
/* 30 */     this.max = max;
/* 31 */     this.dim = new Dimension();
/* 32 */     this.dim.height = 22;
/*    */ 
/* 34 */     this.dim.width = 500;
/*    */   }
/*    */ 
/*    */   public void setText(String text)
/*    */   {
/* 39 */     this.text = text;
/*    */   }
/*    */ 
/*    */   public void setPostFixText(String postfix)
/*    */   {
/* 44 */     this.postfix = postfix;
/*    */   }
/*    */ 
/*    */   public void setValue(int value)
/*    */   {
/* 49 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public void paintComponent(Graphics g)
/*    */   {
/* 54 */     super.paintComponent(g);
/* 55 */     float frac = this.value / (this.max - this.min);
/* 56 */     Graphics2D g2d = (Graphics2D)g;
/*    */ 
/* 58 */     int totalWidth = getWidth();
/* 59 */     int totalHeight = getHeight();
/*    */ 
/* 61 */     int leftBuffer = Math.max(0, (totalWidth - getWidth()) / 2) + 4;
/* 62 */     int buffer = 2;
/* 63 */     int fullWidth = (int)(frac * (totalWidth - 2 * leftBuffer - 3));
/*    */ 
/* 65 */     g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
/* 66 */     g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
/*    */ 
/* 68 */     g2d.setColor(BACKGROUND);
/* 69 */     g2d.fillRect(0, 0, getWidth(), getHeight());
/*    */ 
/* 72 */     g2d.setColor(BORDERCOLOR);
/* 73 */     g2d.drawRect(leftBuffer, 0, totalWidth - 2 * leftBuffer, getHeight() - 1);
/*    */ 
/* 76 */     GradientPaint gp1 = new GradientPaint(leftBuffer, buffer, SHADE_TOP, leftBuffer, totalHeight - 2 * buffer, SHADE_BOTTOM);
/* 77 */     g2d.setPaint(gp1);
/* 78 */     g2d.fillRect(leftBuffer + buffer, buffer, fullWidth, totalHeight - 2 * buffer);
/*    */ 
/* 81 */     g2d.setColor(TEXTCOLOR);
/* 82 */     Font font = Font.decode("SansSerif-BOLD-10");
/*    */ 
/* 84 */     g2d.setFont(font);
/*    */ 
/* 87 */     String textToShow = this.text + " ";
/* 88 */     TextLayout tl = new TextLayout(textToShow, font, g2d.getFontRenderContext());
/* 89 */     tl.draw(g2d, (float)(getWidth() - tl.getBounds().getWidth()) / 2.0F, (float)(getHeight() + tl.getBounds().getHeight()) / 2.0F - 1.0F);
/*    */   }
/*    */   public Dimension getPreferredSize() {
/* 92 */     return this.dim; } 
/* 93 */   public Dimension getMaximumSize() { return this.dim; } 
/* 94 */   public Dimension getMinimumSize() { return this.dim; }
/*    */ 
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.CustomProgressBar
 * JD-Core Version:    0.6.2
 */