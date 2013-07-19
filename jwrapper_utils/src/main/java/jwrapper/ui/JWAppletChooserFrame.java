/*     */ package jwrapper.ui;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.event.KeyAdapter;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.font.FontRenderContext;
/*     */ import java.awt.font.TextLayout;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JPanel;
/*     */ import jwrapper.updater.JWApp;
/*     */ 
/*     */ public class JWAppletChooserFrame
/*     */ {
/*     */   static final int outerPadding = 30;
/*     */   static final int innerPadding = 5;
/*     */   static final int appWidth = 170;
/*     */   private AppOption[] options;
/*     */   private AppOption returnOption;
/*     */   private RoundRectBorder roundBorder;
/*     */   private RoundRectBorder roundBorderGray;
/*     */   private JDialog frame;
/*     */ 
/*     */   public static String chooseVirtualApp(File appdir, JWApp[] apps)
/*     */     throws IOException
/*     */   {
/*  68 */     AppOption[] options = new AppOption[apps.length];
/*     */ 
/*  70 */     for (int i = 0; i < apps.length; i++) {
/*  71 */       options[i] = new AppOption(
/*  72 */         apps[i].getUserVisibleName(), 
/*  73 */         ImageIO.read(new ByteArrayInputStream(apps[i].getLogoPNG())));
/*     */     }
/*     */ 
/*  77 */     JWAppletChooserFrame chooser = new JWAppletChooserFrame(options);
/*     */ 
/*  79 */     AppOption option = chooser.getChosenApp();
/*     */ 
/*  81 */     return option.name;
/*     */   }
/*     */ 
/*     */   public JWAppletChooserFrame(AppOption[] options)
/*     */   {
/*  86 */     this.options = options;
/*  87 */     initFrame();
/*     */   }
/*     */ 
/*     */   private void initBorders()
/*     */   {
/*  92 */     this.roundBorder = new RoundRectBorder(Color.LIGHT_GRAY);
/*  93 */     this.roundBorder.setFillColor(Color.white);
/*  94 */     this.roundBorder.overrideWithPaint(new GradientPaint(0.0F, 0.0F, new Color(105, 190, 244), 0.0F, 200.0F, new Color(0, 135, 193)));
/*     */ 
/*  96 */     this.roundBorderGray = new RoundRectBorder(Color.LIGHT_GRAY);
/*  97 */     this.roundBorderGray.setFillColor(Color.white);
/*  98 */     this.roundBorderGray.overrideWithPaint(new GradientPaint(0.0F, 0.0F, new Color(229, 229, 229), 0.0F, 200.0F, new Color(186, 186, 186)));
/*     */   }
/*     */ 
/*     */   private void closeFrame()
/*     */   {
/* 103 */     this.frame.setVisible(false);
/*     */   }
/*     */ 
/*     */   private void initFrame()
/*     */   {
/* 108 */     this.frame = new JDialog(null, true);
/* 109 */     this.frame.setDefaultCloseOperation(2);
/* 110 */     this.frame.setUndecorated(true);
/*     */ 
/* 112 */     int rowsOfApps = this.options.length / 3;
/* 113 */     if (this.options.length % 3 > 0) {
/* 114 */       rowsOfApps++;
/*     */     }
/* 116 */     int width = Math.min(this.options.length, 3) * 170 + 60 + Math.min(this.options.length - 1, 2) * 30;
/* 117 */     int height = 220 * rowsOfApps + 60 + 30 * (rowsOfApps - 1);
/*     */ 
/* 119 */     this.frame.setSize(width, height);
/*     */ 
/* 121 */     this.frame.setLocationRelativeTo(null);
/*     */ 
/* 123 */     initBorders();
/*     */ 
/* 126 */     JPanel contentPanel = new CanvasPanel();
/* 127 */     contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
/* 128 */     contentPanel.setLayout(new GridLayout(rowsOfApps, 1, 30, 30));
/*     */ 
/* 130 */     for (int row = 0; row < rowsOfApps; row++)
/*     */     {
/* 132 */       JPanel rowPanel = new CanvasPanel();
/* 133 */       GridBagLayout gbl = new GridBagLayout();
/* 134 */       rowPanel.setLayout(gbl);
/*     */ 
/* 136 */       contentPanel.add(rowPanel);
/*     */ 
/* 138 */       int appsInThisRow = Math.min(this.options.length - row * 3, 3);
/*     */ 
/* 152 */       for (int i = row * 3; i < row * 3 + appsInThisRow; i++)
/*     */       {
/* 154 */         JPanel borderPanel = new JPanel(new GridLayout(1, 1));
/* 155 */         borderPanel.setOpaque(false);
/*     */ 
/* 157 */         JPanel appPanel = new AppPanel(this.options[i]);
/* 158 */         appPanel.setOpaque(false);
/*     */ 
/* 161 */         appPanel.setBorder(this.roundBorderGray);
/* 162 */         appPanel.addMouseListener(new BorderChangerListener());
/*     */ 
/* 164 */         borderPanel.add(appPanel);
/*     */ 
/* 166 */         if (i == row * 3)
/* 167 */           gbl.setConstraints(borderPanel, new GridBagConstraints(i, 0, 1, 1, 0.0D, 1.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
/*     */         else
/* 169 */           gbl.setConstraints(borderPanel, new GridBagConstraints(i, 0, 1, 1, 0.0D, 1.0D, 10, 1, new Insets(0, 30, 0, 0), 0, 0));
/* 170 */         rowPanel.add(borderPanel);
/*     */       }
/*     */     }
/* 173 */     this.frame.getContentPane().add(contentPanel);
/*     */ 
/* 175 */     CloseKeyListener closeKeyListener = new CloseKeyListener();
/* 176 */     this.frame.addKeyListener(closeKeyListener);
/* 177 */     contentPanel.addKeyListener(closeKeyListener);
/*     */ 
/* 179 */     this.frame.setVisible(true);
/*     */   }
/*     */ 
/*     */   public AppOption getChosenApp()
/*     */   {
/* 312 */     return this.returnOption;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws IOException
/*     */   {
/* 317 */     Image img1 = ImageIO.read(new File("../PaidMedia/SimpleHelp/Logos/New/logo/green/sh_256.png"));
/* 318 */     Image img2 = ImageIO.read(new File("../PaidMedia/SimpleHelp/Logos/New/logo/normal/sh_256.png"));
/* 319 */     Image img3 = ImageIO.read(new File("../PaidMedia/SimpleHelp/Logos/New/logo/red/sh_256.png"));
/*     */ 
/* 322 */     JWAppletChooserFrame chooser = new JWAppletChooserFrame(new AppOption[] { 
/* 323 */       new AppOption("SimpleGateway\nService Configuration", img1), 
/* 324 */       new AppOption("Run SimpleGateway", img2) });
/*     */ 
/* 326 */     System.out.println("You picked: " + chooser.getChosenApp());
/*     */   }
/*     */ 
/*     */   public static class AppOption
/*     */   {
/*     */     public String name;
/*     */     public Image image;
/*     */ 
/*     */     public AppOption(String name, Image image)
/*     */     {
/*  50 */       this.name = name;
/*  51 */       this.image = image;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  56 */       return this.name;
/*     */     }
/*     */   }
/*     */ 
/*     */   class AppPanel extends JPanel
/*     */   {
/* 214 */     int padding = 10;
/*     */     private JWAppletChooserFrame.AppOption option;
/*     */     private BufferedImage shadowImage;
/*     */ 
/*     */     public AppPanel(JWAppletChooserFrame.AppOption option)
/*     */     {
/* 220 */       this.option = option;
/*     */     }
/*     */ 
/*     */     public Dimension getPreferredSize()
/*     */     {
/* 252 */       Dimension dim = super.getPreferredSize();
/* 253 */       dim.width = 170;
/* 254 */       return dim;
/*     */     }
/*     */ 
/*     */     public Dimension getMinimumSize()
/*     */     {
/* 259 */       Dimension dim = super.getPreferredSize();
/* 260 */       dim.width = 170;
/* 261 */       return dim;
/*     */     }
/*     */ 
/*     */     public Dimension getMaximumSize()
/*     */     {
/* 266 */       Dimension dim = super.getPreferredSize();
/* 267 */       dim.width = 170;
/* 268 */       return dim;
/*     */     }
/*     */ 
/*     */     public void paintComponent(Graphics g)
/*     */     {
/* 273 */       super.paintComponent(g);
/* 274 */       Graphics2D g2d = (Graphics2D)g;
/* 275 */       g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
/* 276 */       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 277 */       g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
/*     */ 
/* 279 */       Insets insets = getInsets();
/*     */ 
/* 281 */       g2d.setColor(getBackground());
/* 282 */       g2d.fillRect(insets.left, insets.top, getWidth() - insets.left - insets.right, getHeight() - insets.top - insets.bottom);
/*     */ 
/* 284 */       int desiredWidth = getWidth() - 2 * this.padding - insets.left - insets.right;
/* 285 */       g2d.drawImage(this.option.image, this.padding + insets.left, this.padding + insets.top, desiredWidth + this.padding + insets.left, desiredWidth + this.padding + insets.top, 0, 0, this.option.image.getWidth(null), this.option.image.getHeight(null), null);
/*     */ 
/* 288 */       String[] rows = this.option.name.split("\n");
/* 289 */       FontRenderContext frc = g2d.getFontRenderContext();
/* 290 */       Font font = g2d.getFont();
/*     */ 
/* 292 */       for (int i = 0; i < rows.length; i++)
/*     */       {
/* 294 */         String text = rows[i];
/* 295 */         TextLayout layout = new TextLayout(text, font, frc);
/* 296 */         float textWidth = (float)layout.getBounds().getWidth();
/* 297 */         float textHeight = (float)layout.getBounds().getHeight();
/* 298 */         float targetX = getWidth() / 2 - textWidth / 2.0F;
/* 299 */         float targetY = insets.top + 2 * this.padding + 15 + desiredWidth + (textHeight + 5.0F) * i;
/*     */ 
/* 301 */         g2d.setColor(Color.WHITE);
/* 302 */         layout.draw(g2d, targetX, targetY + 1.0F);
/*     */ 
/* 304 */         g2d.setColor(new Color(30, 30, 30));
/* 305 */         layout.draw(g2d, targetX, targetY);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class BorderChangerListener extends MouseAdapter
/*     */   {
/*     */     BorderChangerListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void mouseReleased(MouseEvent e)
/*     */     {
/* 185 */       JWAppletChooserFrame.this.returnOption = ((JWAppletChooserFrame.AppPanel)e.getSource()).option;
/* 186 */       JWAppletChooserFrame.this.closeFrame();
/*     */     }
/*     */ 
/*     */     public void mouseEntered(MouseEvent e) {
/* 190 */       ((JComponent)e.getSource()).setBorder(JWAppletChooserFrame.this.roundBorder);
/* 191 */       ((JComponent)e.getSource()).setCursor(Cursor.getPredefinedCursor(12));
/*     */     }
/*     */ 
/*     */     public void mouseExited(MouseEvent e) {
/* 195 */       ((JComponent)e.getSource()).setBorder(JWAppletChooserFrame.this.roundBorderGray);
/* 196 */       ((JComponent)e.getSource()).setCursor(Cursor.getDefaultCursor());
/*     */     }
/*     */   }
/*     */ 
/*     */   class CloseKeyListener extends KeyAdapter {
/*     */     CloseKeyListener() {
/*     */     }
/*     */ 
/* 204 */     public void keyReleased(KeyEvent e) { if (e.getKeyCode() == 27)
/*     */       {
/* 206 */         JWAppletChooserFrame.this.returnOption = null;
/* 207 */         JWAppletChooserFrame.this.closeFrame();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.ui.JWAppletChooserFrame
 * JD-Core Version:    0.6.2
 */