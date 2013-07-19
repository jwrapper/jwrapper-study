/*     */ package jwrapper.pack200;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Enumeration;
/*     */ import java.util.SortedMap;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.jar.JarInputStream;
/*     */ import java.util.jar.Pack200;
/*     */ import java.util.jar.Pack200.Packer;
/*     */ 
/*     */ public class Pack200Compressor
/*     */ {
/*     */   private Pack200.Packer packer;
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  23 */     long start = System.currentTimeMillis();
/*  24 */     initDefaultCompression().compressDirectory(new File("/Users/gchristelis/Desktop/jre"));
/*  25 */     System.out.println("Compression took " + (System.currentTimeMillis() - start) + "ms");
/*     */   }
/*     */ 
/*     */   public static boolean jarContainsAClass(File jarFile)
/*     */   {
/*     */     try
/*     */     {
/*  33 */       JarFile jar = new JarFile(jarFile);
/*     */ 
/*  35 */       Enumeration e = jar.entries();
/*  36 */       while (e.hasMoreElements())
/*     */       {
/*  38 */         JarEntry entry = (JarEntry)e.nextElement();
/*  39 */         if (entry.getName().endsWith(".class"))
/*     */         {
/*  41 */           return true;
/*     */         }
/*     */       }
/*  44 */       return false;
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*  48 */       System.out.println("[Pack200Compressor] Jar appears to not be a jar (" + t.getMessage() + ") (" + jarFile.getName() + ")");
/*  49 */     }return false;
/*     */   }
/*     */ 
/*     */   public static Pack200Compressor initMaxCompression()
/*     */   {
/*  58 */     return new Pack200Compressor(-1, "latest", 9, "false");
/*     */   }
/*     */ 
/*     */   public static Pack200Compressor initGoodCompression()
/*     */   {
/*  66 */     return new Pack200Compressor(10000000, "latest", 9, "true");
/*     */   }
/*     */ 
/*     */   public static Pack200Compressor initDefaultCompression()
/*     */   {
/*  74 */     return new Pack200Compressor(1000000, "latest", 5, "true");
/*     */   }
/*     */ 
/*     */   public Pack200Compressor(int segmentLimitSetting, String modificationSetting, int effortSetting, String keepFileOrderSetting)
/*     */   {
/*  87 */     this.packer = Pack200.newPacker();
/*     */ 
/*  89 */     this.packer.properties().put("pack.segment.limit", Integer.toString(segmentLimitSetting));
/*     */ 
/*  91 */     this.packer.properties().put("pack.modification.time", modificationSetting);
/*     */ 
/*  93 */     this.packer.properties().put("pack.effort", Integer.toString(effortSetting));
/*     */ 
/*  95 */     this.packer.properties().put("pack.keep.file.order", keepFileOrderSetting);
/*     */   }
/*     */ 
/*     */   public void compressFileToOutputStream(JarFile jarFile, OutputStream out)
/*     */     throws IOException
/*     */   {
/* 102 */     this.packer.pack(jarFile, out);
/*     */   }
/*     */ 
/*     */   public void compressDirectory(File directory)
/*     */   {
/* 107 */     if (directory.isFile())
/*     */     {
/* 109 */       File file = directory;
/* 110 */       if (file.getName().endsWith(".jar"))
/*     */       {
/*     */         try
/*     */         {
/* 114 */           System.out.println("[Pack200Compressor] Compressing " + file.getName());
/* 115 */           File parent = file.getParentFile();
/* 116 */           String newName = file.getName().substring(0, file.getName().lastIndexOf('.'));
/* 117 */           File newTarget = new File(parent, newName + ".p2");
/* 118 */           JarInputStream jin = new JarInputStream(new BufferedInputStream(new FileInputStream(file)));
/* 119 */           BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(newTarget));
/*     */           try
/*     */           {
/* 122 */             this.packer.pack(jin, bout);
/*     */           }
/*     */           catch (IOException ex)
/*     */           {
/* 126 */             if (newTarget.exists())
/* 127 */               newTarget.delete();
/* 128 */             throw ex;
/*     */           }
/*     */           finally
/*     */           {
/* 132 */             bout.close();
/* 133 */             jin.close();
/*     */           }
/*     */ 
/* 137 */           file.delete();
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/* 141 */           System.out.println("[Pack200Util] Couldn't compress " + file.getAbsolutePath());
/* 142 */           t.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 148 */       File[] children = directory.listFiles();
/* 149 */       for (int i = 0; i < children.length; i++)
/* 150 */         compressDirectory(children[i]);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.pack200.Pack200Compressor
 * JD-Core Version:    0.6.2
 */