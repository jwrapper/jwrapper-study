/*    */ package utils.swing.layout;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Container;
/*    */ import java.awt.GridBagConstraints;
/*    */ import java.awt.GridBagLayout;
/*    */ import java.awt.Insets;
/*    */ 
/*    */ public class GbPanelWrapper
/*    */ {
/* 11 */   private GridBagLayout gbl = new GridBagLayout();
/*    */   private Insets is;
/*    */   private Container wrappedPanel;
/*    */ 
/*    */   public GbPanelWrapper(Container wrappedPanel)
/*    */   {
/* 16 */     wrappedPanel.setLayout(this.gbl);
/* 17 */     this.is = new Insets(0, 0, 0, 0);
/* 18 */     this.wrappedPanel = wrappedPanel;
/*    */   }
/*    */ 
/*    */   public GbPanelWrapper(Container wrappedPanel, Insets is) {
/* 22 */     wrappedPanel.setLayout(this.gbl);
/* 23 */     this.is = is;
/* 24 */     this.wrappedPanel = wrappedPanel;
/*    */   }
/*    */ 
/*    */   public void add(Component c, int x, int y, int w, int h, int weightx, int weighty, int align, int spread) {
/* 28 */     this.wrappedPanel.add(c);
/* 29 */     this.gbl.setConstraints(c, new GridBagConstraints(x, y, w, h, weightx, weighty, align, spread, this.is, 0, 0));
/*    */   }
/*    */ 
/*    */   public void add(Component c, int x, int y, int w, int h, int weightx, int weighty, int align, int spread, Insets is) {
/* 33 */     this.wrappedPanel.add(c);
/* 34 */     this.gbl.setConstraints(c, new GridBagConstraints(x, y, w, h, weightx, weighty, align, spread, is, 0, 0));
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.layout.GbPanelWrapper
 * JD-Core Version:    0.6.2
 */