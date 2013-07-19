/*     */ package utils.swing.images;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.FilteredImageSource;
/*     */ import java.awt.image.RGBImageFilter;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.ImageIcon;
/*     */ import utils.stream.StreamUtils;
/*     */ 
/*     */ public class ImageLoader
/*     */ {
/*  35 */   private static final Color NOTIFICATION_RED = new Color(188, 36, 16);
/*  36 */   private static Class cla = ImageLoader.class;
/*  37 */   private static Toolkit tk = Toolkit.getDefaultToolkit();
/*  38 */   private static HashMap loaded = new HashMap();
/*  39 */   private static HashMap disabledCache = new HashMap();
/*  40 */   private static HashMap ligherIconsCache = new HashMap();
/*  41 */   private static HashMap lighestIconsCache = new HashMap();
/*  42 */   private static LightGrayFilter filter = new LightGrayFilter(true, 40);
/*  43 */   private static BrightnessContrastFilter brighterFilter = new BrightnessContrastFilter();
/*  44 */   private static BrightnessContrastFilter brightestFilter = new BrightnessContrastFilter(1.0F, -0.2F);
/*     */ 
/*     */   public static byte[] getDataForIcon(String path)
/*     */   {
/*     */     try
/*     */     {
/*  50 */       InputStream in = null;
/*     */       try
/*     */       {
/*  53 */         in = new FileInputStream(path);
/*     */       } catch (Exception localException1) {
/*     */       }
/*  56 */       if (in == null)
/*     */         try {
/*  58 */           in = cla.getResourceAsStream(path);
/*     */         }
/*     */         catch (Exception localException2) {
/*     */         }
/*  62 */       if (in == null)
/*     */         try {
/*  64 */           in = cla.getResourceAsStream("/" + path);
/*     */         }
/*     */         catch (Exception localException3) {
/*     */         }
/*  68 */       if (in == null) throw new IOException("Could not get image as input stream: " + path);
/*     */ 
/*  70 */       byte[] dat = StreamUtils.readAll(new BufferedInputStream(in));
/*  71 */       in.close();
/*  72 */       return dat;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  76 */       e.printStackTrace();
/*  77 */     }return null;
/*     */   }
/*     */ 
/*     */   public static ImageIcon getImageIconLighter(String string)
/*     */   {
/*  83 */     return getImageIconLighter(string, false);
/*     */   }
/*     */ 
/*     */   public static ImageIcon getImageIconLighter(String string, boolean grayScale)
/*     */   {
/*     */     try {
/*  89 */       ImageIcon icon = (ImageIcon)ligherIconsCache.get(string);
/*  90 */       if (icon == null)
/*     */       {
/*     */         Image image;
/*     */         Image image;
/*  93 */         if (grayScale)
/*  94 */           image = getImageIconDisabled(string).getImage();
/*     */         else {
/*  96 */           image = loadImageIcon(string).getImage();
/*     */         }
/*  98 */         Image grayImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), brighterFilter));
/*  99 */         icon = new ImageIcon(grayImage);
/* 100 */         ligherIconsCache.put(string, icon);
/*     */       }
/* 102 */       return icon;
/*     */     } catch (Exception e) {
/* 104 */       System.out.println("ICON NOT FOUND: " + string);
/* 105 */     }return null;
/*     */   }
/*     */ 
/*     */   public static ImageIcon getImageIconLightest(String string, boolean grayScale)
/*     */   {
/*     */     try
/*     */     {
/* 112 */       ImageIcon icon = (ImageIcon)lighestIconsCache.get(string);
/* 113 */       if (icon == null)
/*     */       {
/*     */         Image image;
/*     */         Image image;
/* 116 */         if (grayScale)
/* 117 */           image = getImageIconDisabled(string).getImage();
/*     */         else
/* 119 */           image = loadImageIcon(string).getImage();
/* 120 */         Image grayImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), brightestFilter));
/* 121 */         icon = new ImageIcon(grayImage);
/* 122 */         lighestIconsCache.put(string, icon);
/*     */       }
/* 124 */       return icon;
/*     */     } catch (Exception e) {
/* 126 */       System.out.println("ICON NOT FOUND: " + string);
/* 127 */     }return null;
/*     */   }
/*     */ 
/*     */   public static ImageIcon getImageIconDisabled(String string)
/*     */   {
/*     */     try
/*     */     {
/* 134 */       ImageIcon icon = (ImageIcon)disabledCache.get(string);
/* 135 */       if (icon == null)
/*     */       {
/* 137 */         Image image = loadImageIcon(string).getImage();
/* 138 */         Image grayImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), filter));
/* 139 */         icon = new ImageIcon(grayImage);
/* 140 */         disabledCache.put(string, icon);
/*     */       }
/* 142 */       return icon;
/*     */     } catch (Exception e) {
/* 144 */       System.out.println("ICON NOT FOUND: " + string);
/* 145 */     }return null;
/*     */   }
/*     */ 
/*     */   public static ImageIcon loadImageIcon(String name)
/*     */     throws IOException
/*     */   {
/* 154 */     InputStream in = null;
/*     */     try
/*     */     {
/* 157 */       in = new FileInputStream(name.replace('\\', File.separatorChar).replace('/', File.separatorChar));
/*     */     } catch (Exception localException) {
/*     */     }
/* 160 */     if (in == null)
/*     */       try {
/* 162 */         in = cla.getResourceAsStream(name);
/*     */       }
/*     */       catch (Exception localException1) {
/*     */       }
/* 166 */     if (in == null)
/*     */       try {
/* 168 */         in = cla.getResourceAsStream("/" + name);
/*     */       }
/*     */       catch (Exception localException2) {
/*     */       }
/* 172 */     if (in == null) throw new IOException("Could not get image as input stream: " + name);
/*     */ 
/* 174 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/* 175 */     byte[] buf = new byte[512];
/* 176 */     int n = 0;
/*     */ 
/* 178 */     while (n != -1)
/*     */     {
/* 180 */       if (n > 0) {
/* 181 */         bout.write(buf, 0, n);
/*     */       }
/* 183 */       n = in.read(buf, 0, buf.length);
/*     */     }
/*     */ 
/* 187 */     Image image = tk.createImage(bout.toByteArray());
/* 188 */     ImageIcon icon = new ImageIcon(image);
/*     */ 
/* 190 */     loaded.put(name, icon);
/* 191 */     return icon;
/*     */   }
/*     */ 
/*     */   public static Image loadImage(String name)
/*     */     throws IOException
/*     */   {
/* 199 */     return loadImageIcon(name).getImage();
/*     */   }
/*     */ 
/*     */   public static Image getImage(String name)
/*     */   {
/*     */     try
/*     */     {
/* 208 */       ImageIcon onept = (ImageIcon)loaded.get(name);
/* 209 */       if (onept == null) {
/* 210 */         fix(name);
/* 211 */         onept = (ImageIcon)loaded.get(name);
/*     */       }
/* 213 */       if (onept == null) {
/* 214 */         onept = loadImageIcon(name);
/*     */       }
/* 216 */       return onept.getImage();
/*     */     } catch (Exception e) {
/* 218 */       System.out.println("IO error while loading image " + name + ": " + e.getMessage());
/* 219 */     }return null;
/*     */   }
/*     */ 
/*     */   public static ImageIcon getImageIcon(String name)
/*     */   {
/*     */     try
/*     */     {
/* 229 */       ImageIcon onept = (ImageIcon)loaded.get(name);
/* 230 */       if (onept == null) {
/* 231 */         fix(name);
/* 232 */         onept = (ImageIcon)loaded.get(name);
/*     */       }
/* 234 */       if (onept == null);
/* 235 */       return loadImageIcon(name);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 239 */       System.out.println("IO error while loading image " + name + ": " + e.getMessage());
/* 240 */     }return null;
/*     */   }
/*     */ 
/*     */   private static void fix(String name)
/*     */   {
/* 245 */     char c = File.separatorChar;
/* 246 */     if (c != '\\') {
/* 247 */       name.replace('\\', File.separatorChar);
/*     */     }
/* 249 */     if (c != '/')
/* 250 */       name.replace('/', File.separatorChar);
/*     */   }
/*     */ 
/*     */   public static Icon createOverlayedIcon(Icon icon, Icon overlay)
/*     */   {
/* 256 */     return new OverlayedIcon(icon, overlay);
/*     */   }
/*     */ 
/*     */   public static Icon removeOverlay(Icon icon)
/*     */   {
/* 261 */     if ((icon instanceof OverlayedIcon))
/* 262 */       return ((OverlayedIcon)icon).bottom;
/* 263 */     return icon;
/*     */   }
/*     */ 
/*     */   public static Icon getOverlayedIcon(Icon bottom, Icon top)
/*     */   {
/* 341 */     return new OverlayedIcon(bottom, top);
/*     */   }
/*     */ 
/*     */   public static Icon getIncrementalOverlayedIcon(Icon bottom, int number)
/*     */   {
/* 349 */     int size = 16;
/* 350 */     BufferedImage image = new BufferedImage(size, size, 2);
/* 351 */     Graphics2D g2d = image.createGraphics();
/* 352 */     g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */ 
/* 355 */     g2d.setColor(NOTIFICATION_RED);
/* 356 */     g2d.fillArc(0, 0, size, size, 0, 360);
/* 357 */     g2d.fillRoundRect(0, 0, size, size, 5, 5);
/*     */ 
/* 360 */     g2d.setColor(Color.WHITE);
/* 361 */     float fontSize = 12.0F;
/* 362 */     if (number > 9)
/* 363 */       fontSize = 10.0F;
/* 364 */     if (number > 99)
/* 365 */       fontSize = 8.0F;
/* 366 */     g2d.setFont(g2d.getFont().deriveFont(fontSize).deriveFont(1));
/*     */ 
/* 369 */     FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
/* 370 */     Rectangle2D rect = fm.getStringBounds(number, g2d);
/*     */ 
/* 372 */     int textHeight = (int)rect.getHeight();
/* 373 */     int textWidth = (int)rect.getWidth();
/* 374 */     int panelHeight = image.getHeight();
/* 375 */     int panelWidth = image.getWidth();
/*     */ 
/* 378 */     int x = (panelWidth - textWidth) / 2;
/* 379 */     int y = (panelHeight - textHeight) / 2 + fm.getAscent();
/*     */ 
/* 381 */     g2d.drawString(number, x, y);
/*     */ 
/* 383 */     g2d.dispose();
/* 384 */     return new OverlayedIcon(bottom, new ImageIcon(image));
/*     */   }
/*     */ 
/*     */   public static class BrightnessContrastFilter extends RGBImageFilter
/*     */   {
/* 420 */     private int[] mLUT = null;
/*     */ 
/*     */     public BrightnessContrastFilter()
/*     */     {
/* 429 */       this(0.3F, 0.3F);
/*     */     }
/*     */ 
/*     */     public BrightnessContrastFilter(float pBrightness, float pContrast)
/*     */     {
/* 458 */       this.mLUT = createLUT(pBrightness, pContrast);
/*     */     }
/*     */ 
/*     */     private int[] createLUT(float pBrightness, float pContrast) {
/* 462 */       int[] lut = new int[256];
/*     */ 
/* 465 */       double contrast = pContrast > 0.0F ? Math.pow(pContrast, 7.0D) * 127.0D : pContrast;
/*     */ 
/* 468 */       double brightness = pBrightness + 1.0D;
/*     */ 
/* 470 */       for (int i = 0; i < 256; i++) {
/* 471 */         lut[i] = clamp((int)(127.5D * brightness + (i - 127) * (contrast + 1.0D)));
/*     */       }
/*     */ 
/* 475 */       if (pContrast == 1.0F) {
/* 476 */         lut[127] = lut[126];
/*     */       }
/*     */ 
/* 479 */       return lut;
/*     */     }
/*     */ 
/*     */     private int clamp(int i) {
/* 483 */       if (i < 0) {
/* 484 */         return 0;
/*     */       }
/* 486 */       if (i > 255) {
/* 487 */         return 255;
/*     */       }
/* 489 */       return i;
/*     */     }
/*     */ 
/*     */     public int filterRGB(int pX, int pY, int pARGB)
/*     */     {
/* 505 */       int r = pARGB >> 16 & 0xFF;
/* 506 */       int g = pARGB >> 8 & 0xFF;
/* 507 */       int b = pARGB & 0xFF;
/*     */ 
/* 510 */       r = this.mLUT[r];
/* 511 */       g = this.mLUT[g];
/* 512 */       b = this.mLUT[b];
/*     */ 
/* 515 */       return pARGB & 0xFF000000 | r << 16 | g << 8 | b;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class LightGrayFilter extends RGBImageFilter
/*     */   {
/*     */     private boolean brighter;
/*     */     private int percent;
/*     */ 
/*     */     public LightGrayFilter(boolean b, int p)
/*     */     {
/* 394 */       this.brighter = b;
/* 395 */       this.percent = p;
/* 396 */       this.canFilterIndexColorModel = true;
/*     */     }
/*     */ 
/*     */     public int filterRGB(int x, int y, int rgb)
/*     */     {
/* 403 */       int gray = (int)((0.9D * (rgb >> 16 & 0xFF) + 0.9D * (rgb >> 8 & 0xFF) + 0.8100000000000001D * (rgb & 0xFF)) / 3.0D);
/*     */ 
/* 405 */       if (this.brighter)
/* 406 */         gray = 255 - (255 - gray) * (100 - this.percent) / 100;
/*     */       else {
/* 408 */         gray = gray * (100 - this.percent) / 100;
/*     */       }
/* 410 */       if (gray < 0)
/* 411 */         gray = 0;
/* 412 */       if (gray > 255)
/* 413 */         gray = 255;
/* 414 */       return rgb & 0xFF000000 | gray << 16 | gray << 8 | gray << 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class OverlayedIcon
/*     */     implements Icon
/*     */   {
/* 268 */     private Icon bottom = null;
/* 269 */     private Icon top = null;
/*     */     private Icon disabledBottom;
/*     */     private Icon disabledTop;
/*     */ 
/*     */     public OverlayedIcon(Icon bottom, Icon iconTop)
/*     */     {
/* 274 */       this.bottom = bottom;
/* 275 */       this.top = iconTop;
/*     */ 
/* 277 */       this.disabledBottom = new ImageIcon(Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(getImageFromIcon(bottom).getSource(), ImageLoader.filter)));
/* 278 */       this.disabledTop = new ImageIcon(Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(getImageFromIcon(iconTop).getSource(), ImageLoader.filter)));
/*     */     }
/*     */ 
/*     */     private Image getImageFromIcon(Icon icon)
/*     */     {
/* 283 */       BufferedImage bBottom = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), 2);
/* 284 */       Graphics graphics = bBottom.getGraphics();
/* 285 */       icon.paintIcon(null, graphics, 0, 0);
/* 286 */       graphics.dispose();
/* 287 */       return bBottom;
/*     */     }
/*     */ 
/*     */     public synchronized void paintIcon(Component c, Graphics g, int x, int y)
/*     */     {
/* 292 */       if (c.isEnabled())
/*     */       {
/* 294 */         if ((this.bottom == null) && (this.top != null))
/* 295 */           this.top.paintIcon(c, g, x, y);
/* 296 */         else if ((this.top == null) && (this.bottom != null))
/* 297 */           this.bottom.paintIcon(c, g, x, y);
/* 298 */         else if ((this.top == null) && (this.bottom == null)) {
/* 299 */           return;
/*     */         }
/* 301 */         int xx = x + (this.bottom.getIconWidth() - this.top.getIconWidth());
/* 302 */         int yy = y + (this.bottom.getIconHeight() - this.top.getIconHeight());
/* 303 */         this.bottom.paintIcon(c, g, x, y);
/* 304 */         this.top.paintIcon(c, g, xx, yy);
/*     */       }
/*     */       else
/*     */       {
/* 308 */         if ((this.disabledBottom == null) && (this.disabledTop != null))
/* 309 */           this.disabledTop.paintIcon(c, g, x, y);
/* 310 */         else if ((this.disabledTop == null) && (this.disabledBottom != null))
/* 311 */           this.disabledBottom.paintIcon(c, g, x, y);
/* 312 */         else if ((this.disabledTop == null) && (this.disabledBottom == null)) {
/* 313 */           return;
/*     */         }
/* 315 */         int xx = x + (this.disabledBottom.getIconWidth() - this.disabledTop.getIconWidth());
/* 316 */         int yy = y + (this.disabledBottom.getIconHeight() - this.disabledTop.getIconHeight());
/* 317 */         this.disabledBottom.paintIcon(c, g, x, y);
/* 318 */         this.disabledTop.paintIcon(c, g, xx, yy);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int getIconHeight()
/*     */     {
/* 324 */       if ((this.bottom != null) && (this.top != null)) {
/* 325 */         return Math.max(this.bottom.getIconHeight(), this.top.getIconHeight());
/*     */       }
/* 327 */       return 5;
/*     */     }
/*     */ 
/*     */     public int getIconWidth()
/*     */     {
/* 332 */       if ((this.bottom != null) && (this.top != null)) {
/* 333 */         return Math.max(this.bottom.getIconWidth(), this.top.getIconWidth());
/*     */       }
/* 335 */       return 5;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.images.ImageLoader
 * JD-Core Version:    0.6.2
 */