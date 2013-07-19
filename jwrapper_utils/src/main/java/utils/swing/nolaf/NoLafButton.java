/*     */ package utils.swing.nolaf;
/*     */ 
/*     */ import java.awt.Container;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Image;
/*     */ import java.awt.font.TextLayout;
/*     */ import java.awt.image.BufferedImage;
/*     */ import javax.swing.ButtonModel;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import utils.swing.images.ImageHelper;
/*     */ import utils.swing.images.ImageLoader;
/*     */ 
/*     */ public class NoLafButton extends JButton
/*     */ {
/*  20 */   static String PKG = "utils/swing/nolaf/";
/*     */ 
/*  22 */   BufferedImage[] normal = new BufferedImage[9];
/*  23 */   BufferedImage[] pressed = new BufferedImage[9];
/*  24 */   BufferedImage[] disabled = new BufferedImage[9];
/*     */ 
/*  26 */   int TL = 0;
/*  27 */   int NTH = 1;
/*  28 */   int TR = 2;
/*  29 */   int WST = 3;
/*  30 */   int CENT = 4;
/*  31 */   int EST = 5;
/*  32 */   int BL = 6;
/*  33 */   int STH = 7;
/*  34 */   int BR = 8;
/*     */   int sidew;
/*     */   int sideh;
/*     */   Icon icon;
/*     */   Icon iconDisabled;
/*     */   private String text;
/*     */ 
/*     */   private void splitUp(Image image, BufferedImage[] images)
/*     */   {
/*  48 */     BufferedImage bimg = ImageHelper.toBufferedImageRGB(image);
/*     */ 
/*  50 */     int w = bimg.getWidth();
/*  51 */     int h = bimg.getHeight();
/*     */ 
/*  53 */     this.sidew = ((w - 2) / 2);
/*  54 */     this.sideh = ((h - 2) / 2);
/*     */ 
/*  56 */     images[this.TL] = bimg.getSubimage(0, 0, this.sidew, this.sideh);
/*  57 */     images[this.TR] = bimg.getSubimage(w - this.sidew, 0, this.sidew, this.sideh);
/*  58 */     images[this.BL] = bimg.getSubimage(0, h - this.sideh, this.sidew, this.sideh);
/*  59 */     images[this.BR] = bimg.getSubimage(w - this.sidew, h - this.sideh, this.sidew, this.sideh);
/*     */ 
/*  61 */     images[this.WST] = bimg.getSubimage(0, this.sideh, this.sidew, h - this.sideh - this.sideh);
/*  62 */     images[this.EST] = bimg.getSubimage(w - this.sidew, this.sideh, this.sidew, h - this.sideh - this.sideh);
/*  63 */     images[this.NTH] = bimg.getSubimage(this.sidew, 0, w - this.sidew - this.sidew, this.sideh);
/*  64 */     images[this.STH] = bimg.getSubimage(this.sidew, h - this.sideh, w - this.sidew - this.sidew, this.sideh);
/*     */ 
/*  66 */     images[this.CENT] = bimg.getSubimage(this.sidew, this.sideh, w - this.sidew - this.sidew, h - this.sideh - this.sideh);
/*     */   }
/*     */ 
/*     */   public NoLafButton(Icon icon, Icon iconDisabled)
/*     */   {
/*  84 */     this(PKG + "blue-normal.png", PKG + "blue-pressed.png", PKG + "blue-disabled.png", icon, iconDisabled);
/*     */   }
/*     */   public NoLafButton(String imgNormal, String imgPressed, String imgDisabled, Icon icon, Icon iconDisabled) {
/*  87 */     this.icon = icon;
/*  88 */     this.iconDisabled = iconDisabled;
/*     */     try {
/*  90 */       splitUp(ImageLoader.loadImage(imgNormal), this.normal);
/*  91 */       splitUp(ImageLoader.loadImage(imgPressed), this.pressed);
/*  92 */       splitUp(ImageLoader.loadImage(imgDisabled), this.disabled);
/*     */     } catch (Exception x) {
/*  94 */       x.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setText(String text)
/*     */   {
/* 100 */     this.text = text;
/*     */   }
/*     */ 
/*     */   public void paintComponent(Graphics g) {
/* 104 */     Graphics2D g2 = (Graphics2D)g;
/*     */ 
/* 106 */     int w = getWidth();
/* 107 */     int h = getHeight();
/*     */     Icon cur;
/*     */     BufferedImage[] imgs;
/*     */     Icon cur;
/* 112 */     if (!getModel().isEnabled()) {
/* 113 */       BufferedImage[] imgs = this.disabled;
/* 114 */       cur = this.iconDisabled;
/*     */     }
/*     */     else
/*     */     {
/*     */       Icon cur;
/* 115 */       if (getModel().isPressed())
/*     */       {
/* 117 */         BufferedImage[] imgs = this.pressed;
/* 118 */         cur = this.icon;
/*     */       } else {
/* 120 */         imgs = this.normal;
/* 121 */         cur = this.icon;
/*     */       }
/*     */     }
/* 124 */     for (int y = 0; y < h; y += 2) {
/* 125 */       for (int x = 0; x < w; x += 2)
/* 126 */         g.drawImage(imgs[this.CENT], x, y, null);
/*     */     }
/* 128 */     for (int i = this.sidew; i < w; i += 2) {
/* 129 */       g.drawImage(imgs[this.NTH], i, 0, null);
/*     */     }
/* 131 */     for (int i = this.sidew; i < w; i += 2) {
/* 132 */       g.drawImage(imgs[this.STH], i, h - this.sideh, null);
/*     */     }
/* 134 */     for (int i = this.sideh; i < h; i += 2) {
/* 135 */       g.drawImage(imgs[this.WST], 0, i, null);
/*     */     }
/* 137 */     for (int i = this.sideh; i < h; i += 2) {
/* 138 */       g.drawImage(imgs[this.EST], w - this.sidew, i, null);
/*     */     }
/* 140 */     g.drawImage(imgs[this.TL], 0, 0, null);
/* 141 */     g.drawImage(imgs[this.TR], w - this.sidew, 0, null);
/* 142 */     g.drawImage(imgs[this.BL], 0, h - this.sideh, null);
/* 143 */     g.drawImage(imgs[this.BR], w - this.sidew, h - this.sideh, null);
/*     */ 
/* 145 */     if (cur != null)
/* 146 */       if (this.text == null)
/*     */       {
/* 148 */         int iw = cur.getIconWidth();
/* 149 */         int ih = cur.getIconHeight();
/* 150 */         cur.paintIcon(this, g, w / 2 - iw / 2, h / 2 - ih / 2);
/*     */       }
/*     */       else
/*     */       {
/* 154 */         int iw = cur.getIconWidth();
/* 155 */         int ih = cur.getIconHeight();
/* 156 */         cur.paintIcon(this, g, 10, h / 2 - ih / 2);
/*     */ 
/* 159 */         TextLayout tl = new TextLayout(this.text, g2.getFont(), g2.getFontRenderContext());
/* 160 */         tl.draw(g2, 20 + iw, h / 2 + 2);
/*     */       }
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception
/*     */   {
/* 166 */     String DIR = "src/utils/swing/nolaf/";
/*     */ 
/* 168 */     Icon norm = ImageLoader.getImageIcon("Querying.png");
/* 169 */     Icon dis = ImageLoader.getImageIconDisabled("Querying.png");
/*     */ 
/* 172 */     JFrame frame = new JFrame();
/* 173 */     frame.setBounds(0, 0, 600, 280);
/*     */ 
/* 175 */     JPanel tmp = new JPanel();
/* 176 */     tmp.setLayout(new GridLayout(1, 3));
/* 177 */     NoLafButton noLafButton = new NoLafButton(DIR + "blue-normal.png", DIR + "blue-pressed.png", DIR + "blue-disabled.png", norm, dis);
/* 178 */     noLafButton.setText("aAsdakjsdljkYYjhltyjkrtyy");
/*     */ 
/* 180 */     tmp.add(noLafButton);
/* 181 */     tmp.add(new NoLafButton(DIR + "blue-normal.png", DIR + "blue-pressed.png", DIR + "blue-disabled.png", norm, dis));
/* 182 */     JButton disabled = new NoLafButton(DIR + "blue-normal.png", DIR + "blue-pressed.png", DIR + "blue-disabled.png", norm, dis);
/* 183 */     disabled.setEnabled(false);
/* 184 */     tmp.add(disabled);
/*     */ 
/* 186 */     frame.getContentPane().add(tmp);
/* 187 */     frame.setVisible(true);
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.nolaf.NoLafButton
 * JD-Core Version:    0.6.2
 */