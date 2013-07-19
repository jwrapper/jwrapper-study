/*     */ package utils.swing.images;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.image.AffineTransformOp;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.FilteredImageSource;
/*     */ import java.awt.image.ImageFilter;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.RGBImageFilter;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import javax.swing.JTextPane;
/*     */ import javax.swing.text.MutableAttributeSet;
/*     */ import javax.swing.text.SimpleAttributeSet;
/*     */ import javax.swing.text.StyleConstants;
/*     */ import javax.swing.text.StyledDocument;
/*     */ 
/*     */ public class ImageHelper
/*     */ {
/*  31 */   public static float LEFT = 0.0F;
/*  32 */   public static float CENTER = 0.5F;
/*  33 */   public static float RIGHT = 1.0F;
/*     */   static RenderingHints highQuality;
/*     */ 
/*     */   public static RenderingHints getQualityRenderingHints()
/*     */   {
/*  42 */     if (highQuality == null) {
/*  43 */       RenderingHints hints = new RenderingHints(new HashMap());
/*  44 */       hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
/*     */ 
/*  46 */       hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */ 
/*  48 */       hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
/*  49 */       hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
/*     */ 
/*  51 */       hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
/*  52 */       hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
/*     */ 
/*  54 */       hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
/*     */ 
/*  56 */       hints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
/*     */ 
/*  59 */       highQuality = hints;
/*     */     }
/*  61 */     return highQuality;
/*     */   }
/*     */ 
/*     */   public static BufferedImage toBufferedImageARGB(Image img) {
/*  65 */     return toBufferedImageType(img, 2);
/*     */   }
/*     */ 
/*     */   public static BufferedImage toBufferedImageRGB(Image img) {
/*  69 */     return toBufferedImageType(img, 1);
/*     */   }
/*     */ 
/*     */   public static BufferedImage toBufferedImageType(Image img, int imgType)
/*     */   {
/*  76 */     if (imgType == 0) {
/*  77 */       imgType = 2;
/*     */     }
/*     */ 
/*  80 */     BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), imgType);
/*  81 */     bimg.getGraphics().drawImage(img, 0, 0, null);
/*  82 */     return bimg;
/*     */   }
/*     */ 
/*     */   public static BufferedImage toBufferedImageEquivalent(Image img, BufferedImage sameAsThis) {
/*  86 */     return toBufferedImageType(img, sameAsThis.getType());
/*     */   }
/*     */ 
/*     */   public static BufferedImage replaceColor(BufferedImage img, Color orig, Color replace) {
/*  90 */     int origRGB = orig.getRGB();
/*  91 */     int repRGB = replace.getRGB();
/*     */ 
/*  93 */     int height = img.getHeight(null);
/*  94 */     int width = img.getWidth(null);
/*     */ 
/*  97 */     img = toBufferedImageARGB(img.getScaledInstance(width, height, 4));
/*     */ 
/*  99 */     for (int y = 0; y < height; y++) {
/* 100 */       for (int x = 0; x < width; x++) {
/* 101 */         int n = img.getRGB(x, y);
/* 102 */         if (n == origRGB) {
/* 103 */           img.setRGB(x, y, repRGB);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 108 */     return img;
/*     */   }
/*     */ 
/*     */   public static BufferedImage scale(BufferedImage image, int width, int height)
/*     */   {
/* 119 */     return toBufferedImageARGB(image.getScaledInstance(width, height, 4));
/*     */   }
/*     */ 
/*     */   public static BufferedImage scaleDownToFitInside(BufferedImage image, int width, int height)
/*     */   {
/* 130 */     double iwidth = image.getWidth();
/* 131 */     double iheight = image.getHeight();
/*     */ 
/* 133 */     iwidth = width / iwidth;
/* 134 */     iheight = height / iheight;
/*     */ 
/* 136 */     double iscale = Math.min(iwidth, iheight);
/*     */ 
/* 138 */     if (iscale > 1.0D) return image;
/*     */ 
/* 140 */     return scale(image, iscale, iscale);
/*     */   }
/*     */ 
/*     */   public static BufferedImage scaleToFitInside(BufferedImage image, int width, int height)
/*     */   {
/* 151 */     double iwidth = image.getWidth();
/* 152 */     double iheight = image.getHeight();
/*     */ 
/* 154 */     iwidth = width / iwidth;
/* 155 */     iheight = height / iheight;
/*     */ 
/* 157 */     double iscale = Math.min(iwidth, iheight);
/*     */ 
/* 159 */     return scale(image, iscale, iscale);
/*     */   }
/*     */ 
/*     */   public static BufferedImage scale(BufferedImage image, double xscale, double yscale)
/*     */   {
/* 170 */     return toBufferedImageARGB(image.getScaledInstance((int)(image.getWidth(null) * xscale), (int)(image.getHeight(null) * yscale), 4));
/*     */   }
/*     */ 
/*     */   public static BufferedImage scale(BufferedImage image, double xyscale)
/*     */   {
/* 180 */     return toBufferedImageARGB(image.getScaledInstance((int)(image.getWidth(null) * xyscale), (int)(image.getHeight(null) * xyscale), 4));
/*     */   }
/*     */ 
/*     */   public static BufferedImage rotate(BufferedImage image, double rotations, Color bgcolor)
/*     */   {
/* 192 */     if (rotations > 0.5D) {
/* 193 */       rotations = 0.5D - rotations;
/*     */     }
/*     */ 
/* 196 */     AffineTransform transform = AffineTransform.getRotateInstance(rotations * 3.141592653589793D * 2.0D, image.getWidth(null) / 2, image.getHeight(null) / 2);
/* 197 */     RenderingHints hints = getQualityRenderingHints();
/* 198 */     AffineTransformOp op = new AffineTransformOp(transform, hints);
/* 199 */     BufferedImage dest = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
/* 200 */     if (bgcolor == null) {
/* 201 */       dest.getGraphics().clearRect(0, 0, image.getWidth(), image.getHeight());
/*     */     } else {
/* 203 */       Graphics g = dest.getGraphics();
/* 204 */       g.setColor(bgcolor);
/* 205 */       g.fillRect(0, 0, image.getWidth(), image.getHeight());
/*     */     }
/*     */ 
/* 210 */     op.filter(image, dest);
/* 211 */     return dest;
/*     */   }
/*     */ 
/*     */   public static BufferedImage rotateTransparent(BufferedImage image, double rotations) {
/* 215 */     return rotate(image, rotations, null);
/*     */   }
/*     */ 
/*     */   public static void applyRGBFilter(BufferedImage img, Rectangle bounds, RGBImageFilter filter) {
/* 219 */     BufferedImage bimg = img.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);
/* 220 */     Image grayImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(bimg.getSource(), filter));
/* 221 */     img.getGraphics().drawImage(grayImage, bounds.x, bounds.y, null);
/*     */   }
/*     */ 
/*     */   public static void applyReplacingFilter(BufferedImage img, Rectangle bounds, ImageFilter filter)
/*     */     throws IOException
/*     */   {
/* 227 */     BufferedImage bimg = img.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);
/* 228 */     Image grayImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(bimg.getSource(), filter));
/*     */ 
/* 230 */     grayImage = toBufferedImageARGB(grayImage);
/*     */ 
/* 234 */     Graphics2D g2 = (Graphics2D)img.getGraphics();
/* 235 */     g2.setBackground(new Color(0, 0, 0, 0));
/* 236 */     g2.clearRect(bounds.x, bounds.y, bounds.width, bounds.height);
/*     */ 
/* 238 */     img.getGraphics().drawImage(grayImage, bounds.x, bounds.y, null);
/*     */   }
/*     */ 
/*     */   public static void drawText(Graphics2D g2, Rectangle bounds, Font f, String text, float align) {
/* 242 */     drawText(g2, bounds, f, text, align, Color.black);
/*     */   }
/*     */ 
/*     */   public static void drawText(Graphics2D g2, Rectangle bounds, Font f, String text, float align, Color color)
/*     */   {
/* 248 */     JTextPane area = new JTextPane();
/* 249 */     StyledDocument doc = area.getStyledDocument();
/*     */ 
/* 251 */     MutableAttributeSet standard = new SimpleAttributeSet();
/* 252 */     StyleConstants.setAlignment(standard, 1);
/* 253 */     StyleConstants.setFontFamily(standard, f.getFontName());
/* 254 */     StyleConstants.setFontSize(standard, f.getSize());
/* 255 */     StyleConstants.setForeground(standard, color);
/* 256 */     doc.setParagraphAttributes(0, 0, standard, true);
/*     */ 
/* 258 */     area.setText(text);
/* 259 */     area.setBackground(new Color(0, 0, 0, 0));
/* 260 */     area.setOpaque(false);
/*     */ 
/* 264 */     area.setSize(bounds.width, bounds.height);
/* 265 */     area.setFont(f);
/*     */ 
/* 267 */     g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
/* 268 */     g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
/*     */ 
/* 270 */     area.paint(g2.create(bounds.x, bounds.y, bounds.width, bounds.height));
/*     */   }
/*     */ 
/*     */   public static BufferedImage addBorder(BufferedImage image, Color col, Insets is)
/*     */   {
/* 319 */     int type = image.getType();
/* 320 */     if (type == 0) type = 2;
/* 321 */     BufferedImage dest = new BufferedImage(image.getWidth() + is.left + is.right, image.getHeight() + is.top + is.bottom, type);
/* 322 */     Graphics g = dest.getGraphics();
/* 323 */     g.setColor(col);
/* 324 */     g.fillRect(0, 0, dest.getWidth(), dest.getHeight());
/*     */ 
/* 326 */     g.drawImage(image, is.left, is.top, null);
/*     */ 
/* 328 */     return dest;
/*     */   }
/*     */ 
/*     */   public static BufferedImage[] getFullyRotated(BufferedImage orig, int count)
/*     */   {
/* 339 */     int N = count;
/* 340 */     BufferedImage[] images = new BufferedImage[count];
/* 341 */     for (int i = 0; i < N; i++) {
/* 342 */       double rot = i * (1.0D / N);
/* 343 */       images[i] = rotateTransparent(orig, rot);
/*     */     }
/* 345 */     return images;
/*     */   }
/*     */ 
/*     */   public static BufferedImage getSimilarImage(BufferedImage img, int nwidth, int nheight) {
/* 349 */     int biType = img.getType();
/*     */     BufferedImage bimg;
/*     */     BufferedImage bimg;
/* 352 */     if (biType == 0) {
/* 353 */       bimg = new BufferedImage(nwidth, nheight, 2);
/*     */     }
/*     */     else
/*     */     {
/*     */       BufferedImage bimg;
/* 354 */       if ((img.getColorModel() instanceof IndexColorModel))
/* 355 */         bimg = new BufferedImage(nwidth, nheight, biType, (IndexColorModel)img.getColorModel());
/*     */       else {
/* 357 */         bimg = new BufferedImage(nwidth, nheight, biType);
/*     */       }
/*     */     }
/* 360 */     return bimg;
/*     */   }
/*     */ 
/*     */   public static BufferedImage createOverlay(Image main, Image overlay, boolean bottom, boolean right, double overlayProportionalSize, boolean scaleUpIfNecessary) {
/* 364 */     BufferedImage bimg = toBufferedImageARGB(main);
/*     */ 
/* 366 */     int oTargetWidth = (int)(main.getWidth(null) * overlayProportionalSize);
/* 367 */     int oTargetHeight = (int)(main.getHeight(null) * overlayProportionalSize);
/*     */ 
/* 369 */     if (scaleUpIfNecessary)
/* 370 */       overlay = scaleToFitInside(toBufferedImageARGB(overlay), oTargetWidth, oTargetHeight);
/*     */     else {
/* 372 */       overlay = scaleDownToFitInside(toBufferedImageARGB(overlay), oTargetWidth, oTargetHeight);
/*     */     }
/*     */ 
/* 375 */     Graphics g = bimg.getGraphics();
/*     */ 
/* 377 */     int x = 0;
/* 378 */     int y = 0;
/*     */ 
/* 380 */     if (bottom) {
/* 381 */       y = main.getHeight(null) - overlay.getHeight(null);
/*     */     }
/* 383 */     if (right) {
/* 384 */       x = main.getWidth(null) - overlay.getWidth(null);
/*     */     }
/*     */ 
/* 387 */     g.drawImage(overlay, x, y, null);
/*     */ 
/* 389 */     return bimg;
/*     */   }
/*     */ 
/*     */   public static void repeatOffsetText(BufferedImage img, String text, Color textcol)
/*     */   {
/* 404 */     repeatOffsetText(img, text, textcol, 3);
/*     */   }
/*     */   public static void repeatOffsetText(BufferedImage img, String text, Color textcol, int perline) {
/* 407 */     Graphics g = img.getGraphics();
/*     */ 
/* 409 */     int fsize = g.getFont().getSize();
/* 410 */     while (g.getFontMetrics().stringWidth(text) < img.getWidth() / perline) {
/* 411 */       fsize = (int)(fsize * 1.5D);
/* 412 */       g.setFont(g.getFont().deriveFont(fsize));
/*     */     }
/*     */ 
/* 415 */     g.setColor(textcol);
/*     */ 
/* 417 */     FontMetrics fm = g.getFontMetrics();
/* 418 */     int chunkw = (int)(1.75D * fm.stringWidth(text));
/* 419 */     int chunkh = (int)(1.75D * (fm.getAscent() + fm.getDescent()));
/* 420 */     boolean on = true;
/* 421 */     for (int k = 20; k < img.getHeight(); k += chunkh) {
/* 422 */       for (int i = 0; i < img.getWidth(); i += chunkw) {
/* 423 */         if (on)
/* 424 */           g.drawString(text, i, k);
/*     */         else {
/* 426 */           g.drawString(text, i + chunkw / 2, k);
/*     */         }
/*     */       }
/* 429 */       on = !on;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.images.ImageHelper
 * JD-Core Version:    0.6.2
 */