/*     */ package utils.swing.icons;
/*     */ 
/*     */ import java.awt.Image;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import javax.imageio.ImageIO;
/*     */ 
/*     */ public class ICNSReader
/*     */ {
/*     */   public static Image getImageFromICNSFile(File targetICNSFile)
/*     */     throws IOException
/*     */   {
/*  22 */     return (Image)getFromICNSFile(targetICNSFile, false);
/*     */   }
/*     */ 
/*     */   public static byte[] getPNGFromICNSFile(File targetICNSFile) throws IOException {
/*  26 */     return (byte[])getFromICNSFile(targetICNSFile, true);
/*     */   }
/*     */ 
/*     */   private static Object getFromICNSFile(File targetICNSFile, boolean bytesOnly) throws IOException {
/*  30 */     DataInputStream din = new DataInputStream(new BufferedInputStream(new FileInputStream(targetICNSFile)));
/*     */ 
/*  32 */     HashMap map = new HashMap();
/*     */ 
/*  34 */     byte[] temp = new byte[4];
/*  35 */     din.readFully(temp);
/*  36 */     if (!Arrays.equals(temp, new byte[] { 105, 99, 110, 115 })) {
/*  37 */       throw new IOException("Target file is not an ICNS file.");
/*     */     }
/*  39 */     din.readInt();
/*  40 */     ArrayList images = new ArrayList();
/*  41 */     while (din.available() > 0)
/*     */     {
/*  43 */       din.readFully(temp);
/*  44 */       int imageData = din.readInt() - 8;
/*     */ 
/*  46 */       int size = 0;
/*  47 */       boolean isMask = temp[3] == 107;
/*  48 */       int keyIndex = 0;
/*  49 */       if (!isMask)
/*  50 */         keyIndex = 1;
/*  51 */       switch (temp[keyIndex]) {
/*     */       case 115:
/*  53 */         size = 16; break;
/*     */       case 108:
/*  54 */         size = 32; break;
/*     */       case 104:
/*  55 */         size = 48; break;
/*     */       case 116:
/*  56 */         size = 128;
/*     */       }
/*     */ 
/*  59 */       if (size > 0)
/*     */       {
/*  61 */         BufferedImage image = (BufferedImage)map.get(new Integer(size));
/*  62 */         if (image == null)
/*     */         {
/*  64 */           image = new BufferedImage(size, size, 2);
/*  65 */           map.put(new Integer(size), image);
/*     */         }
/*     */ 
/*  68 */         if (!isMask)
/*     */         {
/*  70 */           for (int y = 0; y < image.getHeight(); y++)
/*     */           {
/*  72 */             for (int x = 0; x < image.getWidth(); x++)
/*     */             {
/*  74 */               int rgb = image.getRGB(x, y);
/*  75 */               rgb &= -16777216;
/*  76 */               int pixel = din.readInt();
/*  77 */               pixel &= 16777215;
/*  78 */               image.setRGB(x, y, pixel | rgb);
/*     */             }
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*  84 */           for (int y = 0; y < image.getHeight(); y++)
/*     */           {
/*  86 */             for (int x = 0; x < image.getWidth(); x++)
/*     */             {
/*  88 */               int rgb = image.getRGB(x, y);
/*  89 */               int alpha = din.readByte();
/*  90 */               alpha <<= 24;
/*  91 */               rgb &= 16777215;
/*  92 */               rgb |= alpha;
/*  93 */               image.setRGB(x, y, rgb);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 100 */         boolean png = true;
/*     */ 
/* 102 */         if (Arrays.equals(temp, new byte[] { 105, 99, 48, 56 }))
/* 103 */           size = 256;
/* 104 */         else if (Arrays.equals(temp, new byte[] { 105, 99, 48, 57 }))
/* 105 */           size = 512;
/* 106 */         else if (Arrays.equals(temp, new byte[] { 105, 99, 49, 48 })) {
/* 107 */           size = 1024;
/*     */         }
/* 109 */         if (size > 0)
/*     */         {
/* 111 */           byte[] pngData = new byte[imageData];
/* 112 */           din.readFully(pngData);
/*     */ 
/* 114 */           if ((size == 256) && (bytesOnly)) {
/*     */             try {
/* 116 */               din.close(); } catch (Exception localException) {
/*     */             }
/* 118 */             return pngData;
/*     */           }
/*     */ 
/* 121 */           ByteArrayInputStream bin = new ByteArrayInputStream(pngData);
/* 122 */           images.add(ImageIO.read(bin));
/*     */         }
/*     */       }
/*     */     }
/* 125 */     Set keys = map.keySet();
/* 126 */     Iterator it = keys.iterator();
/* 127 */     while (it.hasNext())
/*     */     {
/* 129 */       Object key = it.next();
/* 130 */       images.add(map.get(key));
/*     */     }
/*     */ 
/* 133 */     int max = 0;
/* 134 */     Image maxImage = null;
/*     */ 
/* 136 */     for (int i = 0; i < images.size(); i++)
/*     */     {
/* 138 */       BufferedImage image = (BufferedImage)images.get(i);
/* 139 */       if (i == 0)
/*     */       {
/* 141 */         max = image.getWidth();
/* 142 */         maxImage = image;
/*     */       }
/* 146 */       else if (image.getWidth() > max)
/*     */       {
/* 148 */         max = image.getWidth();
/* 149 */         maxImage = image;
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 155 */       din.close();
/*     */     } catch (Exception localException1) {
/*     */     }
/* 158 */     return maxImage;
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.icons.ICNSReader
 * JD-Core Version:    0.6.2
 */