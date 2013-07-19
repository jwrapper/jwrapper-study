/*    */ package jwrapper.pack200;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.BufferedOutputStream;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.util.jar.JarOutputStream;
/*    */ import java.util.jar.Pack200;
/*    */ import java.util.jar.Pack200.Unpacker;
/*    */ 
/*    */ public class Pack200Decompressor
/*    */ {
/* 16 */   private Pack200.Unpacker unpacker = Pack200.newUnpacker();
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/* 20 */     long start = System.currentTimeMillis();
/* 21 */     new Pack200Decompressor().decompressDirectory(new File("/Users/gchristelis/Desktop/jre"));
/* 22 */     System.out.println("Decompression took " + (System.currentTimeMillis() - start) + "ms");
/*    */   }
/*    */ 
/*    */   public static Pack200Decompressor initDecompressor()
/*    */   {
/* 27 */     return new Pack200Decompressor();
/*    */   }
/*    */ 
/*    */   public void decompressFile(InputStream in, JarOutputStream out) throws IOException
/*    */   {
/* 32 */     this.unpacker.unpack(in, out);
/*    */   }
/*    */ 
/*    */   public void decompressDirectory(File directory)
/*    */   {
/* 37 */     if (directory.isFile())
/*    */     {
/* 39 */       File file = directory;
/* 40 */       if (file.getName().endsWith(".p2"))
/*    */       {
/*    */         try
/*    */         {
/* 44 */           System.out.println("[Pack200Decompressor] Decompressing " + file.getName());
/*    */ 
/* 46 */           File parent = file.getParentFile();
/* 47 */           String newName = file.getName().substring(0, file.getName().lastIndexOf('.'));
/* 48 */           File newTarget = new File(parent, newName + ".jar");
/* 49 */           BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
/* 50 */           JarOutputStream jout = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(newTarget)));
/*    */           try
/*    */           {
/* 53 */             this.unpacker.unpack(file, jout);
/*    */           }
/*    */           catch (IOException ex)
/*    */           {
/* 57 */             if (newTarget.exists())
/* 58 */               newTarget.delete();
/* 59 */             throw ex;
/*    */           }
/*    */           finally
/*    */           {
/* 63 */             System.out.println("[Pack200Decompressor] Closing streams...");
/*    */             try
/*    */             {
/* 66 */               bin.close();
/*    */             }
/*    */             catch (Throwable localThrowable1) {
/*    */             }
/*    */             try {
/* 71 */               jout.close();
/*    */             }
/*    */             catch (Throwable localThrowable2)
/*    */             {
/*    */             }
/*    */           }
/* 77 */           file.delete();
/*    */         }
/*    */         catch (Throwable t)
/*    */         {
/* 81 */           System.out.println("[Pack200Util] Couldn't decompress " + file.getAbsolutePath());
/* 82 */           t.printStackTrace();
/*    */         }
/*    */       }
/*    */     }
/*    */     else
/*    */     {
/* 88 */       File[] children = directory.listFiles();
/* 89 */       for (int i = 0; i < children.length; i++)
/* 90 */         decompressDirectory(children[i]);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.pack200.Pack200Decompressor
 * JD-Core Version:    0.6.2
 */