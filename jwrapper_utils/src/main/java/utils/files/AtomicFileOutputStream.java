/*     */ package utils.files;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class AtomicFileOutputStream extends OutputStream
/*     */ {
/*     */   private FileOutputStream tmp_fout;
/*     */   private File tmp_file;
/*     */   private File dest_file;
/*     */   private static final String SUFFIX = ".atomicfout_donotdelete";
/*     */ 
/*     */   public AtomicFileOutputStream(String file)
/*     */     throws IOException
/*     */   {
/*  23 */     this(new File(file));
/*     */   }
/*     */ 
/*     */   public AtomicFileOutputStream(File file) throws IOException {
/*  27 */     this.dest_file = file;
/*     */ 
/*  29 */     this.tmp_file = new File(file + ".atomicfout_donotdelete");
/*  30 */     this.tmp_fout = new FileOutputStream(this.tmp_file);
/*     */   }
/*     */ 
/*     */   public static void prepareForReading(File f) throws IOException {
/*  34 */     if (!f.exists())
/*     */     {
/*  36 */       File src = new File(f.getAbsolutePath() + ".atomicfout_donotdelete");
/*  37 */       if (src.exists())
/*     */       {
/*  39 */         System.out.println("AtomicFileOutputStream renaming source...");
/*  40 */         src.renameTo(f);
/*     */       }
/*     */       else
/*     */       {
/*  44 */         throw new FileNotFoundException("File " + f + " not found");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/*  51 */     this.tmp_fout.flush();
/*  52 */     this.tmp_fout.getFD().sync();
/*     */ 
/*  54 */     this.tmp_fout.close();
/*     */ 
/*  56 */     boolean dest_exists = this.dest_file.exists();
/*     */ 
/*  62 */     if (dest_exists) {
/*  63 */       System.out.println("[AtomicFileOutputStream] Destination file exists, deleting");
/*  64 */       if (!this.dest_file.delete()) {
/*  65 */         System.out.println("[AtomicFileOutputStream] Failed to delete destination file");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  75 */     if (!this.tmp_file.renameTo(this.dest_file)) {
/*  76 */       if (!this.tmp_file.delete()) {
/*  77 */         System.out.println("[AtomicFileOutputStream] Failed to delete temp file");
/*     */       }
/*  79 */       throw new IOException("[AtomicFileOutputStream] Could not rename new file to original name");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/*  91 */     this.tmp_fout.flush();
/*     */   }
/*     */   public void write(byte[] b) throws IOException {
/*  94 */     this.tmp_fout.write(b);
/*     */   }
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/*  97 */     this.tmp_fout.write(b, off, len);
/*     */   }
/*     */   public void write(int b) throws IOException {
/* 100 */     this.tmp_fout.write(b);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception {
/* 104 */     byte[] dat = new byte[5000];
/* 105 */     long t = System.currentTimeMillis() + 5000L;
/* 106 */     int count = 0;
/* 107 */     while (System.currentTimeMillis() < t) {
/* 108 */       OutputStream out = new AtomicFileOutputStream("afos_test");
/* 109 */       out.write(dat);
/* 110 */       out.close();
/* 111 */       count++;
/*     */     }
/* 113 */     System.out.println(count / 5 + " per second");
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.files.AtomicFileOutputStream
 * JD-Core Version:    0.6.2
 */