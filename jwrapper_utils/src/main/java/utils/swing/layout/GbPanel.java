/*    */ package utils.swing.layout;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.GridBagConstraints;
/*    */ import java.awt.GridBagLayout;
/*    */ import java.awt.Insets;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class GbPanel extends JPanel
/*    */ {
/*    */   public static final int CENTER = 10;
/*    */   public static final int NORTH = 11;
/*    */   public static final int SOUTH = 15;
/*    */   public static final int EAST = 13;
/*    */   public static final int WEST = 17;
/*    */   public static final int NORTHEAST = 12;
/*    */   public static final int NORTHWEST = 18;
/*    */   public static final int SOUTHEAST = 14;
/*    */   public static final int SOUTHWEST = 16;
/*    */   public static final int HORIZONTAL = 2;
/*    */   public static final int VERTICAL = 3;
/*    */   public static final int BOTH = 1;
/*    */   public static final int NONE = 0;
/* 26 */   GridBagLayout gbl = new GridBagLayout();
/*    */   Insets is;
/*    */ 
/*    */   public void resetLayout()
/*    */   {
/* 32 */     this.gbl = new GridBagLayout();
/* 33 */     setLayout(this.gbl);
/*    */   }
/*    */ 
/*    */   public GbPanel() {
/* 37 */     setLayout(this.gbl);
/* 38 */     this.is = new Insets(0, 0, 0, 0);
/*    */   }
/*    */ 
/*    */   public GbPanel(Insets is) {
/* 42 */     setLayout(this.gbl);
/* 43 */     this.is = is;
/*    */   }
/*    */ 
/*    */   public void add(Component c, int x, int y, int w, int h, int weightx, int weighty, int align, int spread) {
/* 47 */     add(c);
/* 48 */     this.gbl.setConstraints(c, new GridBagConstraints(x, y, w, h, weightx, weighty, align, spread, this.is, 0, 0));
/*    */   }
/*    */ 
/*    */   public void add(Component c, int x, int y, int w, int h, int weightx, int weighty, int align, int spread, Insets is) {
/* 52 */     add(c);
/* 53 */     this.gbl.setConstraints(c, new GridBagConstraints(x, y, w, h, weightx, weighty, align, spread, is, 0, 0));
/*    */   }
/*    */ 
/*    */   public GridBagLayout getGridBagLayout()
/*    */   {
/* 58 */     return this.gbl;
/*    */   }
/*    */ 
/*    */   public Insets getGbPanelInsets()
/*    */   {
/* 63 */     return this.is;
/*    */   }
/*    */ 
/*    */   public void setInsets(Insets is)
/*    */   {
/* 68 */     this.is = is;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.layout.GbPanel
 * JD-Core Version:    0.6.2
 */