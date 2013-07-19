/*     */ package utils.swing;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.image.FilteredImageSource;
/*     */ import java.awt.image.RGBImageFilter;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.ImageIcon;
/*     */ import utils.stream.StreamUtils;
/*     */ 
/*     */ public class IconLoader
/*     */ {
/*     */   public static final int OVERLAY_ADD = 1;
/*     */   public static final int OVERLAY_ERROR = 2;
/*     */   public static final int OVERLAY_WARNING = 3;
/*     */   public static final int OVERLAY_REMOVE = 4;
/*  27 */   static HashMap cache = new HashMap();
/*  28 */   static HashMap disabledCache = new HashMap();
/*  29 */   public static final LightGrayFilter LIGHT_GRAY_FILTER = new LightGrayFilter(true, 40);
/*     */ 
/*  31 */   public static String CUSTOM_ICON_PATH = null;
/*     */ 
/*     */   public static byte[] getDataForIcon(String path)
/*     */   {
/*     */     try
/*     */     {
/*  37 */       InputStream in = IconLoader.class.getResourceAsStream("/icons/" + path);
/*  38 */       if (in == null)
/*     */       {
/*  41 */         File f = new File("icons/" + path);
/*  42 */         if (f.exists())
/*  43 */           System.out.println();
/*  44 */         in = new FileInputStream("icons/" + path);
/*     */       }
/*  46 */       if ((in == null) && (CUSTOM_ICON_PATH != null))
/*     */       {
/*  48 */         File f = new File(CUSTOM_ICON_PATH + path);
/*  49 */         if (f.exists())
/*  50 */           in = new FileInputStream(f);
/*     */         else
/*  52 */           in = IconLoader.class.getResourceAsStream(CUSTOM_ICON_PATH + path);
/*     */       }
/*  54 */       byte[] dat = StreamUtils.readAll(new BufferedInputStream(in));
/*  55 */       in.close();
/*  56 */       return dat;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  60 */       e.printStackTrace();
/*  61 */     }return null;
/*     */   }
/*     */ 
/*     */   public static ImageIcon loadDisabled(String string)
/*     */   {
/*     */     try
/*     */     {
/*  68 */       ImageIcon icon = (ImageIcon)disabledCache.get(string);
/*  69 */       if (icon == null)
/*     */       {
/*  71 */         Image image = load(string).getImage();
/*  72 */         Image grayImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), LIGHT_GRAY_FILTER));
/*  73 */         icon = new ImageIcon(grayImage);
/*  74 */         disabledCache.put(string, icon);
/*     */       }
/*  76 */       return icon;
/*     */     } catch (NullPointerException e) {
/*  78 */       System.out.println("ICON NOT FOUND: " + string);
/*  79 */     }return null;
/*     */   }
/*     */ 
/*     */   public static Icon disableIcon(Icon icon)
/*     */   {
/*  85 */     if ((icon instanceof ImageIcon))
/*     */     {
/*  87 */       Image i = ((ImageIcon)icon).getImage();
/*  88 */       Image grayImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(i.getSource(), LIGHT_GRAY_FILTER));
/*  89 */       return new ImageIcon(grayImage);
/*     */     }
/*  91 */     if ((icon instanceof OverlayedIcon))
/*     */     {
/*  93 */       OverlayedIcon overlayed = (OverlayedIcon)icon;
/*  94 */       return new OverlayedIcon(disableIcon(overlayed.bottom), disableIcon(overlayed.top));
/*     */     }
/*  96 */     return null;
/*     */   }
/*     */ 
/*     */   public static ImageIcon load(String path)
/*     */   {
/* 101 */     ImageIcon icon = (ImageIcon)cache.get(path);
/* 102 */     if (icon == null)
/*     */     {
/*     */       try
/*     */       {
/* 106 */         InputStream in = IconLoader.class.getResourceAsStream("/icons/" + path);
/* 107 */         if (in == null)
/*     */         {
/* 109 */           if (CUSTOM_ICON_PATH != null)
/*     */           {
/* 111 */             File f = new File(CUSTOM_ICON_PATH + path);
/* 112 */             if (f.exists())
/* 113 */               in = new FileInputStream(f);
/*     */             else
/* 115 */               in = IconLoader.class.getResourceAsStream(CUSTOM_ICON_PATH + path);
/*     */           }
/*     */           else {
/* 118 */             File f = new File("icons/" + path);
/* 119 */             in = new FileInputStream(f);
/*     */           }
/*     */         }
/* 122 */         byte[] dat = StreamUtils.readAll(new BufferedInputStream(in));
/* 123 */         in.close();
/* 124 */         icon = new ImageIcon(dat);
/* 125 */         cache.put(path, icon);
/* 126 */         return icon;
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 130 */         e.printStackTrace();
/* 131 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 136 */     return icon;
/*     */   }
/*     */ 
/*     */   private static ImageIcon getOverlay(int type)
/*     */   {
/* 142 */     switch (type) {
/*     */     case 1:
/* 144 */       return load("overlays/add_overlay.png");
/*     */     case 2:
/* 145 */       return load("overlays/error_overlay.png");
/*     */     case 3:
/* 146 */       return load("overlays/warning_overlay.png");
/*     */     case 4:
/* 147 */       return load("overlays/remove_overlay.png");
/*     */     }
/* 149 */     return null;
/*     */   }
/*     */ 
/*     */   public static Icon loadWithOverlay(String path, int overlayType)
/*     */   {
/* 154 */     ImageIcon icon = load(path);
/* 155 */     return applyOverlay(icon, overlayType);
/*     */   }
/*     */ 
/*     */   public static Icon createOverlayedIcon(Icon icon, Icon overlay)
/*     */   {
/* 160 */     return new OverlayedIcon(icon, overlay);
/*     */   }
/*     */ 
/*     */   public static Icon applyOverlay(Icon icon, int overlayType)
/*     */   {
/* 165 */     Icon overlay = getOverlay(overlayType);
/* 166 */     if (overlay == null)
/* 167 */       return icon;
/* 168 */     if (((icon instanceof OverlayedIcon)) || ((overlay instanceof OverlayedIcon)))
/* 169 */       throw new RuntimeException("Overlaying an overlayed icon is not a good idea");
/* 170 */     return new OverlayedIcon(icon, overlay);
/*     */   }
/*     */ 
/*     */   public static Icon removeOverlay(Icon icon)
/*     */   {
/* 175 */     if ((icon instanceof OverlayedIcon))
/* 176 */       return ((OverlayedIcon)icon).bottom;
/* 177 */     return icon;
/*     */   }
/*     */ 
/*     */   static class LightGrayFilter extends RGBImageFilter
/*     */   {
/*     */     private boolean brighter;
/*     */     private int percent;
/*     */ 
/*     */     public LightGrayFilter(boolean b, int p)
/*     */     {
/* 217 */       this.brighter = b;
/* 218 */       this.percent = p;
/* 219 */       this.canFilterIndexColorModel = true;
/*     */     }
/*     */ 
/*     */     public int filterRGB(int x, int y, int rgb)
/*     */     {
/* 226 */       int gray = (int)((0.9D * (rgb >> 16 & 0xFF) + 0.9D * (rgb >> 8 & 0xFF) + 0.8100000000000001D * (rgb & 0xFF)) / 3.0D);
/*     */ 
/* 228 */       if (this.brighter)
/* 229 */         gray = 255 - (255 - gray) * (100 - this.percent) / 100;
/*     */       else {
/* 231 */         gray = gray * (100 - this.percent) / 100;
/*     */       }
/* 233 */       if (gray < 0)
/* 234 */         gray = 0;
/* 235 */       if (gray > 255)
/* 236 */         gray = 255;
/* 237 */       return rgb & 0xFF000000 | gray << 16 | gray << 8 | gray << 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class OverlayedIcon
/*     */     implements Icon
/*     */   {
/* 182 */     private Icon bottom = null;
/* 183 */     private Icon top = null;
/*     */ 
/*     */     public OverlayedIcon(Icon bottom, Icon iconTop)
/*     */     {
/* 187 */       this.bottom = bottom;
/* 188 */       this.top = iconTop;
/*     */     }
/*     */ 
/*     */     public synchronized void paintIcon(Component c, Graphics g, int x, int y)
/*     */     {
/* 193 */       int xx = x + (this.bottom.getIconWidth() - this.top.getIconWidth());
/* 194 */       int yy = y + (this.bottom.getIconHeight() - this.top.getIconHeight());
/* 195 */       this.bottom.paintIcon(c, g, x, y);
/* 196 */       this.top.paintIcon(c, g, xx, yy);
/*     */     }
/*     */ 
/*     */     public int getIconHeight()
/*     */     {
/* 201 */       return Math.max(this.bottom.getIconHeight(), this.top.getIconHeight());
/*     */     }
/*     */ 
/*     */     public int getIconWidth()
/*     */     {
/* 206 */       return Math.max(this.bottom.getIconWidth(), this.top.getIconWidth());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.IconLoader
 * JD-Core Version:    0.6.2
 */