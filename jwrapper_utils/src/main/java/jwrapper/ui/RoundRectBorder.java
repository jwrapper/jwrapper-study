/*     */ package jwrapper.ui;
/*     */ 
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Paint;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Stroke;
/*     */ import javax.swing.border.Border;
/*     */ 
/*     */ public class RoundRectBorder
/*     */   implements Border
/*     */ {
/*     */   protected Color color;
/*  18 */   protected int radius = 4;
/*     */ 
/*  20 */   private int strokeWidth = 4;
/*  21 */   protected Stroke stroke = new BasicStroke(4.0F);
/*  22 */   private Color fillColor = null;
/*  23 */   private Paint overridePaint = null;
/*  24 */   private Insets shadowInsets = new Insets(0, 0, 3, 0);
/*  25 */   private int shadowWidth = 4;
/*     */ 
/*     */   public RoundRectBorder(Color color)
/*     */   {
/*  29 */     this.color = color;
/*     */   }
/*     */ 
/*     */   public void overrideWithPaint(Paint paint)
/*     */   {
/*  34 */     this.overridePaint = paint;
/*     */   }
/*     */ 
/*     */   public void setFillColor(Color fillColor)
/*     */   {
/*  39 */     this.fillColor = fillColor;
/*     */   }
/*     */ 
/*     */   public void setWidth(int strokeWidth)
/*     */   {
/*  44 */     this.strokeWidth = strokeWidth;
/*  45 */     this.stroke = new BasicStroke(strokeWidth);
/*     */   }
/*     */ 
/*     */   public Insets getBorderInsets(Component comp)
/*     */   {
/*  50 */     int value = this.strokeWidth;
/*  51 */     return new Insets(this.shadowInsets.top + value, 
/*  52 */       this.shadowInsets.left + value, 
/*  53 */       this.shadowInsets.bottom + value, 
/*  54 */       this.shadowInsets.right + value);
/*     */   }
/*     */ 
/*     */   public boolean isBorderOpaque()
/*     */   {
/*  59 */     return false;
/*     */   }
/*     */ 
/*     */   public void paintBorder(Component comp, Graphics g, int x, int y, int width, int height)
/*     */   {
/*  64 */     Insets insets = getBorderInsets(comp);
/*  65 */     int arc = this.radius * 3;
/*  66 */     Graphics2D g2 = (Graphics2D)g;
/*     */ 
/*  68 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  69 */     g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
/*     */ 
/*  86 */     g2.setStroke(new BasicStroke(this.shadowWidth));
/*  87 */     g2.setColor(new Color(180, 180, 180, 80));
/*  88 */     g2.drawRoundRect(x + this.strokeWidth / 2, 
/*  89 */       y + this.strokeWidth / 2, 
/*  90 */       width - this.strokeWidth - 1, 
/*  91 */       height - this.strokeWidth - 1, 
/*  92 */       arc + this.shadowInsets.left + this.shadowInsets.right, arc + this.shadowInsets.top + this.shadowInsets.bottom);
/*     */ 
/*  94 */     if (this.stroke != null) {
/*  95 */       g2.setStroke(this.stroke);
/*     */     }
/*  97 */     if (this.fillColor != null)
/*     */     {
/* 104 */       int borderX = x + this.shadowInsets.left;
/* 105 */       int borderY = y + this.shadowInsets.top;
/* 106 */       int borderWidth = width - this.shadowInsets.left - this.shadowInsets.right;
/* 107 */       int borderHeight = height - this.shadowInsets.top - this.shadowInsets.bottom;
/* 108 */       Insets borderInsets = new Insets(insets.top - this.shadowInsets.top, 
/* 109 */         insets.left - this.shadowInsets.left, 
/* 110 */         insets.bottom - this.shadowInsets.bottom, 
/* 111 */         insets.right - this.shadowInsets.right);
/* 112 */       g2.setColor(this.fillColor);
/*     */ 
/* 114 */       g2.setClip(borderX, borderY, borderWidth, borderInsets.top);
/* 115 */       g2.fillRoundRect(x + this.strokeWidth / 2 + this.shadowInsets.left, 
/* 116 */         y + this.strokeWidth / 2 + this.shadowInsets.top, 
/* 117 */         width - this.strokeWidth - this.shadowInsets.left - this.shadowInsets.right - 1, 
/* 118 */         height - this.strokeWidth - this.shadowInsets.top - this.shadowInsets.bottom - 1, 
/* 119 */         arc, arc);
/*     */ 
/* 121 */       g2.setClip(borderX, borderY, borderInsets.left, borderHeight);
/* 122 */       g2.fillRoundRect(x + this.strokeWidth / 2 + this.shadowInsets.left, 
/* 123 */         y + this.strokeWidth / 2 + this.shadowInsets.top, 
/* 124 */         width - this.strokeWidth - this.shadowInsets.left - this.shadowInsets.right - 1, 
/* 125 */         height - this.strokeWidth - this.shadowInsets.top - this.shadowInsets.bottom - 1, 
/* 126 */         arc, arc);
/*     */ 
/* 128 */       g2.setClip(borderX + borderWidth - borderInsets.right, borderY, borderInsets.right, borderHeight);
/* 129 */       g2.fillRoundRect(x + this.strokeWidth / 2 + this.shadowInsets.left + 1, 
/* 130 */         y + this.strokeWidth / 2 + this.shadowInsets.top, 
/* 131 */         width - this.strokeWidth - this.shadowInsets.left - this.shadowInsets.right - 1, 
/* 132 */         height - this.strokeWidth - this.shadowInsets.top - this.shadowInsets.bottom - 1, 
/* 133 */         arc, arc);
/*     */ 
/* 135 */       g2.setClip(borderX, height - insets.bottom, borderWidth, borderInsets.bottom);
/* 136 */       g2.fillRoundRect(x + this.strokeWidth / 2 + this.shadowInsets.left, 
/* 137 */         y + this.strokeWidth / 2 + this.shadowInsets.top + 1, 
/* 138 */         width - this.strokeWidth - this.shadowInsets.left - this.shadowInsets.right - 1, 
/* 139 */         height - this.strokeWidth - this.shadowInsets.top - this.shadowInsets.bottom - 1, 
/* 140 */         arc, arc);
/*     */     }
/*     */ 
/* 143 */     g2.setClip(x, y, width, height);
/*     */ 
/* 145 */     g2.setColor(this.color);
/* 146 */     if (this.overridePaint != null) {
/* 147 */       g2.setPaint(this.overridePaint);
/*     */     }
/* 149 */     g2.drawRoundRect(x + this.strokeWidth / 2 + this.shadowInsets.left, 
/* 150 */       y + this.strokeWidth / 2 + this.shadowInsets.top, 
/* 151 */       width - this.strokeWidth - this.shadowInsets.left - this.shadowInsets.right - 1, 
/* 152 */       height - this.strokeWidth - this.shadowInsets.top - this.shadowInsets.bottom - 1, 
/* 153 */       arc, arc);
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.ui.RoundRectBorder
 * JD-Core Version:    0.6.2
 */