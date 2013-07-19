/*     */ package jwrapper;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.font.TextLayout;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public class LoadPanel extends JPanel
/*     */ {
/*  23 */   protected static final Color BACKGROUND = Color.WHITE;
/*  24 */   protected static Color BORDERCOLOR = new Color(80, 129, 190);
/*  25 */   protected static Color TEXTCOLOR = new Color(30, 30, 30);
/*  26 */   protected static Color SHADE_TOP = new Color(185, 225, 247);
/*  27 */   protected static Color SHADE_BOTTOM = new Color(157, 199, 230);
/*     */ 
/*  29 */   private ArrayList bars = new ArrayList();
/*     */   private CustomProgressBar pBar;
/*     */   private Image img;
/*  32 */   private ImagePanel imagePanel = new ImagePanel();
/*  33 */   private JPanel panel1 = new JPanel();
/*  34 */   private JPanel panel2 = new JPanel();
/*     */ 
/*     */   public LoadPanel()
/*     */   {
/*  39 */     GridBagLayout gbl = new GridBagLayout();
/*  40 */     setLayout(gbl);
/*     */ 
/*  42 */     this.pBar = new CustomProgressBar(0, 100, BACKGROUND);
/*  43 */     this.panel1.setBackground(BACKGROUND);
/*  44 */     this.panel2.setBackground(BACKGROUND);
/*     */ 
/*  46 */     gbl.setConstraints(this.panel1, new GridBagConstraints(0, 0, 1, 1, 1.0D, 10000.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
/*  47 */     gbl.setConstraints(this.imagePanel, new GridBagConstraints(0, 1, 1, 1, 1.0D, 0.0D, 10, 0, new Insets(2, 2, 20, 2), 0, 0));
/*  48 */     gbl.setConstraints(this.pBar, new GridBagConstraints(0, 2, 1, 1, 1.0D, 0.0D, 10, 1, new Insets(2, 2, 2, 2), 0, 0));
/*  49 */     gbl.setConstraints(this.panel2, new GridBagConstraints(0, 3, 1, 1, 1.0D, 10000.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
/*     */ 
/*  51 */     add(this.panel1);
/*  52 */     add(this.pBar);
/*  53 */     add(this.panel2);
/*  54 */     add(this.imagePanel);
/*     */ 
/*  56 */     setVisible(false);
/*     */   }
/*     */ 
/*     */   private Color hex2Rgb(String colorStr, Color defaultColour)
/*     */   {
/*  61 */     if ((colorStr == null) || (colorStr.trim().length() == 0))
/*  62 */       return defaultColour;
/*  63 */     colorStr = colorStr.trim();
/*  64 */     int offset = 0;
/*  65 */     if (colorStr.startsWith("#"))
/*  66 */       offset = 1;
/*  67 */     return new Color(Integer.valueOf(colorStr.substring(0 + offset, 2 + offset), 16).intValue(), 
/*  68 */       Integer.valueOf(colorStr.substring(2 + offset, 4 + offset), 16).intValue(), 
/*  69 */       Integer.valueOf(colorStr.substring(4 + offset, 6 + offset), 16).intValue());
/*     */   }
/*     */ 
/*     */   public void parseLoadingBarColours(String string)
/*     */   {
/*  74 */     if (string == null) {
/*  75 */       return;
/*     */     }
/*  77 */     String[] lines = string.split("\\n");
/*  78 */     if (lines == null)
/*  79 */       return;
/*  80 */     int start = -1; int end = -1;
/*  81 */     String line = null;
/*  82 */     for (int i = 0; (i < lines.length) && (start == -1); i++)
/*     */     {
/*  84 */       line = lines[i].trim();
/*  85 */       if (!line.startsWith("#"))
/*     */       {
/*  87 */         int index = line.indexOf("LOAD_PROGRESS_BAR");
/*  88 */         if (index != -1)
/*     */         {
/*  90 */           start = line.indexOf('=', start);
/*  91 */           if (start != -1)
/*     */           {
/*  93 */             start++;
/*  94 */             end = line.indexOf('\n', start);
/*  95 */             if (end == -1)
/*  96 */               end = line.length(); 
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*  99 */     if ((start == -1) || (line == null)) {
/* 100 */       return;
/*     */     }
/* 102 */     String colours = line.substring(start, end);
/* 103 */     if (colours != null)
/*     */     {
/* 105 */       System.out.println("[TinyApplet] Loading custom colours: " + colours);
/*     */ 
/* 107 */       colours = colours.trim();
/* 108 */       String[] cols = colours.split(",");
/* 109 */       System.out.println(colours);
/* 110 */       if (cols.length < 4)
/* 111 */         return;
/* 112 */       SHADE_TOP = hex2Rgb(cols[0], SHADE_TOP);
/* 113 */       SHADE_BOTTOM = hex2Rgb(cols[1], SHADE_BOTTOM);
/* 114 */       BORDERCOLOR = hex2Rgb(cols[2], BORDERCOLOR);
/* 115 */       TEXTCOLOR = hex2Rgb(cols[3], TEXTCOLOR);
/*     */ 
/* 117 */       System.out.println(SHADE_TOP + ", " + SHADE_BOTTOM + ", " + BORDERCOLOR + ", " + TEXTCOLOR);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void paintComponent(Graphics g)
/*     */   {
/* 125 */     if (this.img != null)
/*     */     {
/* 127 */       if (getWidth() < this.img.getWidth(null)) {
/* 128 */         this.imagePanel.setVisible(false);
/*     */       }
/* 131 */       else if (getHeight() < this.img.getHeight(null) + 20 + 2 + 2 + 2 + this.pBar.getHeight())
/* 132 */         this.imagePanel.setVisible(false);
/*     */       else {
/* 134 */         this.imagePanel.setVisible(true);
/*     */       }
/*     */     }
/*     */ 
/* 138 */     super.paintComponent(g);
/*     */ 
/* 140 */     Graphics2D g2d = (Graphics2D)g;
/* 141 */     g2d.setColor(BACKGROUND);
/* 142 */     g2d.fillRect(0, 0, getWidth(), getHeight());
/*     */   }
/*     */ 
/*     */   public void addProgressBar(double max, double progress)
/*     */   {
/* 147 */     MonitorBar mBar = new MonitorBar();
/* 148 */     mBar.total = max;
/* 149 */     mBar.progress = progress;
/* 150 */     this.bars.add(mBar);
/*     */ 
/* 152 */     updateBar();
/*     */   }
/*     */ 
/*     */   public void setLogo(Image img)
/*     */   {
/* 157 */     this.img = img;
/* 158 */     if (img != null) {
/* 159 */       this.imagePanel.setImage(img);
/*     */     }
/*     */ 
/* 162 */     invalidate();
/* 163 */     SwingUtilities.invokeLater(new Repainter(this));
/*     */   }
/*     */ 
/*     */   public void setProgress(int index, double outof1)
/*     */   {
/* 168 */     MonitorBar mBar = (MonitorBar)this.bars.get(index);
/* 169 */     if (mBar.total == 0.0D)
/*     */     {
/* 171 */       mBar.progress = outof1;
/* 172 */       mBar.total = outof1;
/*     */     }
/*     */     else {
/* 175 */       mBar.progress = outof1;
/*     */     }
/* 177 */     updateBar();
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean b)
/*     */   {
/* 182 */     this.pBar.setVisible(b);
/*     */   }
/*     */ 
/*     */   public void setProgress(int index, double thismuch, double outofthismuch)
/*     */   {
/* 187 */     MonitorBar mBar = (MonitorBar)this.bars.get(index);
/*     */ 
/* 190 */     if (outofthismuch < mBar.total)
/*     */     {
/* 192 */       mBar.total = outofthismuch;
/*     */     }
/*     */ 
/* 195 */     mBar.progress = (thismuch / outofthismuch);
/*     */ 
/* 197 */     updateBar();
/*     */   }
/*     */ 
/*     */   private void updateBar()
/*     */   {
/* 203 */     double progress = 0.0D;
/* 204 */     double total = 0.0D;
/* 205 */     for (int i = 0; i < this.bars.size(); i++)
/*     */     {
/* 207 */       MonitorBar mBar = (MonitorBar)this.bars.get(i);
/* 208 */       total += mBar.total;
/* 209 */       progress += mBar.progress * mBar.total;
/*     */     }
/*     */ 
/* 224 */     this.pBar.setPostFixText("");
/*     */ 
/* 226 */     this.pBar.setValue((int)(progress * 100.0D / total));
/*     */ 
/* 228 */     SwingUtilities.invokeLater(new Repainter(this.pBar));
/*     */   }
/*     */ 
/*     */   public void setText(String text)
/*     */   {
/* 233 */     this.pBar.setText(text);
/*     */   }
/*     */ 
/*     */   static class CustomProgressBar extends JPanel
/*     */   {
/*     */     private int value;
/*     */     private int min;
/*     */     private int max;
/*     */     private Dimension dim;
/* 246 */     private String text = "";
/* 247 */     private String postfix = "";
/*     */ 
/*     */     public CustomProgressBar(int min, int max, Color background)
/*     */     {
/* 251 */       this.min = min;
/* 252 */       this.max = max;
/* 253 */       this.dim = new Dimension();
/* 254 */       this.dim.height = 22;
/*     */ 
/* 256 */       this.dim.width = 500;
/*     */     }
/*     */ 
/*     */     public void setText(String text)
/*     */     {
/* 261 */       this.text = text;
/*     */     }
/*     */ 
/*     */     public void setPostFixText(String postfix)
/*     */     {
/* 266 */       this.postfix = postfix;
/*     */     }
/*     */ 
/*     */     public void setValue(int value)
/*     */     {
/* 271 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public void paintComponent(Graphics g)
/*     */     {
/* 276 */       super.paintComponent(g);
/* 277 */       float frac = this.value / (this.max - this.min);
/* 278 */       Graphics2D g2d = (Graphics2D)g;
/*     */ 
/* 280 */       int totalWidth = getWidth();
/* 281 */       int totalHeight = getHeight();
/* 282 */       int leftBuffer = Math.max(0, (totalWidth - 300) / 2) + 4;
/* 283 */       int buffer = 2;
/* 284 */       int fullWidth = (int)(frac * (totalWidth - 2 * leftBuffer - 3));
/*     */ 
/* 286 */       g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
/* 287 */       g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
/*     */ 
/* 289 */       g2d.setColor(LoadPanel.BACKGROUND);
/* 290 */       g2d.fillRect(0, 0, getWidth(), getHeight());
/*     */ 
/* 293 */       g2d.setColor(LoadPanel.BORDERCOLOR);
/* 294 */       g2d.drawRect(leftBuffer, 0, totalWidth - 2 * leftBuffer, getHeight() - 1);
/*     */ 
/* 297 */       GradientPaint gp1 = new GradientPaint(leftBuffer, buffer, LoadPanel.SHADE_TOP, leftBuffer, totalHeight - 2 * buffer, LoadPanel.SHADE_BOTTOM);
/* 298 */       g2d.setPaint(gp1);
/* 299 */       g2d.fillRect(leftBuffer + buffer, buffer, fullWidth, totalHeight - 2 * buffer);
/*     */ 
/* 302 */       g2d.setColor(LoadPanel.TEXTCOLOR);
/* 303 */       Font font = Font.decode("SansSerif-BOLD-10");
/*     */ 
/* 305 */       g2d.setFont(font);
/*     */ 
/* 308 */       String textToShow = this.text + " ";
/* 309 */       TextLayout tl = new TextLayout(textToShow, font, g2d.getFontRenderContext());
/* 310 */       tl.draw(g2d, (float)(getWidth() - tl.getBounds().getWidth()) / 2.0F, (float)(getHeight() + tl.getBounds().getHeight()) / 2.0F - 1.0F);
/*     */     }
/*     */     public Dimension getPreferredSize() {
/* 313 */       return this.dim; } 
/* 314 */     public Dimension getMaximumSize() { return this.dim; } 
/* 315 */     public Dimension getMinimumSize() { return this.dim; }
/*     */   }
/*     */ 
/*     */   public static class ImagePanel extends JPanel
/*     */   {
/*     */     private Image img;
/*     */     private Dimension dim;
/*     */ 
/*     */     public ImagePanel() {
/* 325 */       setOpaque(false);
/* 326 */       this.dim = new Dimension(0, 0);
/*     */     }
/*     */ 
/*     */     public void setImage(Image image)
/*     */     {
/* 331 */       this.img = image;
/* 332 */       if (this.img != null)
/*     */       {
/* 335 */         this.dim = new Dimension();
/* 336 */         this.dim.height = this.img.getHeight(null);
/* 337 */         this.dim.width = this.img.getWidth(null);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void paintComponent(Graphics g)
/*     */     {
/* 343 */       super.paintComponent(g);
/*     */ 
/* 345 */       Graphics2D g2d = (Graphics2D)g;
/* 346 */       if (this.img != null)
/*     */       {
/* 348 */         int x = (getWidth() - this.img.getWidth(null)) / 2;
/* 349 */         int y = (getHeight() - this.img.getHeight(null)) / 2;
/* 350 */         g2d.drawImage(this.img, x, y, null);
/*     */       }
/*     */     }
/*     */ 
/* 354 */     public Dimension getPreferredSize() { return this.dim; } 
/* 355 */     public Dimension getMaximumSize() { return this.dim; } 
/* 356 */     public Dimension getMinimumSize() { return this.dim; } 
/*     */   }
/*     */   static class MonitorBar { double progress;
/*     */     double total; }
/*     */ 
/*     */   static class Repainter implements Runnable { private Component c;
/*     */ 
/* 364 */     public Repainter(Component c) { this.c = c; }
/*     */ 
/*     */     public void run()
/*     */     {
/* 368 */       this.c.repaint();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.LoadPanel
 * JD-Core Version:    0.6.2
 */