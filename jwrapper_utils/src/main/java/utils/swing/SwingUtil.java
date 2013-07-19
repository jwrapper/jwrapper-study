/*     */ package utils.swing;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.text.View;
/*     */ 
/*     */ public class SwingUtil
/*     */ {
/*     */   public static final String PROPERTY_COLOR = "color";
/*     */   public static final String KEY_COLOR_BLUE = "blue";
/*     */   public static final String KEY_COLOR_RED = "red";
/*     */   public static final String PROPERTY_SEGMENTED = "segmented";
/*     */   public static final String KEY_SEGMENTED_LEFT = "left";
/*     */   public static final String KEY_SEGMENTED_CENTER = "center";
/*     */   public static final String KEY_SEGMENTED_RIGHT = "right";
/*  22 */   public static final Object KEY_SEGMENTED_MIDDLE = "middle";
/*  23 */   public static final Object KEY_SEGMENTED_TOP = "top";
/*  24 */   public static final Object KEY_SEGMENTED_BOTTOM = "bottom";
/*     */ 
/*  26 */   public static final JTextField JTEXTFIELD_REF = new JTextField();
/*     */ 
/*  99 */   private static final JLabel resizer = new JLabel();
/*     */ 
/*     */   public static void setSize(JComponent c, Dimension dim)
/*     */   {
/*  30 */     c.setSize(dim);
/*  31 */     c.setMaximumSize(dim);
/*  32 */     c.setMinimumSize(dim);
/*  33 */     c.setPreferredSize(dim);
/*     */   }
/*     */ 
/*     */   public static void setSize(JComponent c, int width, int height) {
/*  37 */     Dimension dim = new Dimension(width, height);
/*  38 */     setSize(c, dim);
/*     */   }
/*     */ 
/*     */   public static void setWidth(JComponent c, int width)
/*     */   {
/*  43 */     Dimension dim = c.getPreferredSize();
/*  44 */     setSize(c, width, dim.height);
/*     */   }
/*     */ 
/*     */   public static void setHeight(JComponent c, int height)
/*     */   {
/*  49 */     Dimension dim = c.getPreferredSize();
/*  50 */     setSize(c, dim.width, height);
/*     */   }
/*     */ 
/*     */   public static void setSizesToWidest(JComponent c1, JComponent c2)
/*     */   {
/*  55 */     Dimension dim = c1.getPreferredSize();
/*  56 */     if (dim.width < c2.getPreferredSize().width) {
/*  57 */       dim = c2.getPreferredSize();
/*     */     }
/*  59 */     c1.setPreferredSize(dim);
/*  60 */     c2.setPreferredSize(dim);
/*     */   }
/*     */ 
/*     */   public static void setSizesToWidest(JComponent[] components)
/*     */   {
/*  65 */     Dimension dim = components[0].getPreferredSize();
/*  66 */     for (int i = 1; i < components.length; i++)
/*     */     {
/*  68 */       if (dim.width < components[i].getPreferredSize().width) {
/*  69 */         dim = components[i].getPreferredSize();
/*     */       }
/*     */     }
/*  72 */     for (int i = 0; i < components.length; i++)
/*     */     {
/*  74 */       components[i].setPreferredSize(dim);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void makeButtonRed(JButton button)
/*     */   {
/*  80 */     button.putClientProperty("color", "red");
/*     */   }
/*     */ 
/*     */   public static void makeButtonBlue(JButton button)
/*     */   {
/*  85 */     button.putClientProperty("color", "blue");
/*     */   }
/*     */ 
/*     */   public static void fixSizeToPreferredSize(JComponent component)
/*     */   {
/*  90 */     component.setSize(component.getPreferredSize());
/*  91 */     component.setMaximumSize(component.getPreferredSize());
/*  92 */     component.setMinimumSize(component.getPreferredSize());
/*     */   }
/*     */ 
/*     */   public static Dimension getPreferredSizeOfHtml(String html, boolean width, int prefSize)
/*     */   {
/* 102 */     resizer.setText(html);
/*     */ 
/* 104 */     View view = (View)resizer.getClientProperty("html");
/*     */ 
/* 106 */     view.setSize(width ? prefSize : 0, width ? 0 : prefSize);
/*     */ 
/* 108 */     float w = view.getPreferredSpan(0);
/* 109 */     float h = view.getPreferredSpan(1);
/*     */ 
/* 111 */     return new Dimension((int)Math.ceil(w), (int)Math.ceil(h));
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.SwingUtil
 * JD-Core Version:    0.6.2
 */