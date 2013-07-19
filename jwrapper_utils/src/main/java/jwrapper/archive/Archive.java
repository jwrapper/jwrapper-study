/*     */ package jwrapper.archive;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.jar.JarFile;
/*     */ import jwrapper.LzmaUtil;
/*     */ import jwrapper.pack200.Pack200Compressor;
/*     */ import utils.stream.CFriendlyStreamUtils;
/*     */ import utils.stream.OpenByteArrayOutputStream;
/*     */ import utils.sync.UnqueuedSemaphore;
/*     */ 
/*     */ public class Archive
/*     */ {
/*     */   public static final int TYPE_FILE = 0;
/*     */   public static final int TYPE_FOLDER = 1;
/*     */   public static final int TYPE_FILE_P200 = 2;
/*  25 */   boolean pack200allowed = true;
/*     */   long preP200;
/*     */   long postP200;
/*     */   File dest;
/*     */   OutputStream out;
/*  42 */   byte[] buf = new byte[200000];
/*     */ 
/*  60 */   static boolean THREADED_P200 = false;
/*     */ 
/* 185 */   static OpenByteArrayOutputStream compressbuf = new OpenByteArrayOutputStream();
/*     */ 
/*     */   public Archive(File dest)
/*     */     throws IOException
/*     */   {
/*  34 */     this.dest = dest;
/*  35 */     this.out = new BufferedOutputStream(new FileOutputStream(dest), 10240);
/*     */   }
/*     */ 
/*     */   public void setPack200Allowed(boolean b) {
/*  39 */     this.pack200allowed = b;
/*     */   }
/*     */ 
/*     */   public void finishAndCompress(boolean compression)
/*     */     throws Exception
/*     */   {
/*  45 */     this.out.close();
/*     */ 
/*  47 */     if (compression)
/*  48 */       System.out.println("NOTE: Compressing");
/*     */     else {
/*  50 */       System.out.println("NOTE: NOT Compressing");
/*     */     }
/*  52 */     long len = this.dest.length();
/*  53 */     LzmaUtil.compress(this.dest, compression);
/*  54 */     long nlen = this.dest.length();
/*     */ 
/*  56 */     System.out.println("P2 - compressed from " + this.preP200 + " to " + this.postP200);
/*  57 */     System.out.println("L2 - compressed from " + len + " to " + nlen);
/*     */   }
/*     */ 
/*     */   public void addFile(File f, String relpath)
/*     */     throws IOException
/*     */   {
/*  63 */     addFile(f, relpath, null);
/*     */   }
/*     */   public void addFile(File f, String relpath, FileStripper[] fs) throws IOException {
/*  66 */     if (fs == null) fs = new FileStripper[0];
/*  67 */     long[] p200info = new long[2];
/*     */ 
/*  69 */     ArrayList tempjars = new ArrayList();
/*     */ 
/*  71 */     addFileToStream(this.pack200allowed, p200info, this.out, this.buf, f, relpath, fs, tempjars);
/*     */ 
/*  73 */     if (THREADED_P200) {
/*  74 */       UnqueuedSemaphore sem = new UnqueuedSemaphore(Runtime.getRuntime().availableProcessors() + 1);
/*     */ 
/*  76 */       ArrayList list = new ArrayList();
/*  77 */       for (int i = 0; i < tempjars.size(); i++) {
/*  78 */         System.out.println("Processing postponed file: " + tempjars.get(i));
/*  79 */         Object[] all = (Object[])tempjars.get(i);
/*  80 */         String jrelpath = (String)all[0];
/*  81 */         File jf = (File)all[1];
/*     */ 
/*  83 */         P2Thread thread = new P2Thread(sem, i, jf, jrelpath, fs);
/*  84 */         thread.start();
/*  85 */         list.add(thread);
/*     */       }
/*     */ 
/*  88 */       for (int i = 0; i < list.size(); i++) {
/*  89 */         P2Thread thread = (P2Thread)list.get(i);
/*     */         try {
/*  91 */           thread.join();
/*     */         } catch (InterruptedException xx) {
/*  93 */           IOException fail = new IOException("Failed to join with p2 processing thread");
/*  94 */           fail.initCause(xx);
/*  95 */           throw fail;
/*     */         }
/*  97 */         System.out.println("Adding postponed file: " + thread.jrelpath);
/*     */         try {
/*  99 */           thread.copyTo(this.out, this.buf, p200info);
/*     */         } catch (Throwable t) {
/* 101 */           t.printStackTrace();
/* 102 */           throw new IOException("Failed to add postponed file to archive because of previous failure " + t);
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 107 */       for (int i = 0; i < tempjars.size(); i++) {
/* 108 */         Object[] all = (Object[])tempjars.get(i);
/* 109 */         String jrelpath = (String)all[0];
/* 110 */         File jf = (File)all[1];
/*     */ 
/* 112 */         System.out.println("Adding postponed file: " + jrelpath + ", " + jf.getAbsolutePath());
/*     */ 
/* 115 */         addFileToStream(this.pack200allowed, p200info, this.out, this.buf, jf, jrelpath, fs, null);
/*     */       }
/*     */     }
/*     */ 
/* 119 */     this.preP200 += p200info[0];
/* 120 */     this.postP200 += p200info[1];
/*     */ 
/* 122 */     for (int i = 0; i < fs.length; i++)
/* 123 */       System.out.println("Stripping " + fs[i].getName() + " saved " + fs[i].getStrippedTotal() + " bytes");
/*     */   }
/*     */ 
/*     */   public static void addFileToStream(OutputStream out, byte[] buf, File f, String relpath, FileStripper[] fs)
/*     */     throws IOException
/*     */   {
/* 188 */     long[] tmp = new long[2];
/* 189 */     addFileToStream(true, tmp, out, buf, f, relpath, fs, null);
/*     */   }
/*     */ 
/*     */   private static void addFileToStream(boolean pack200allowed, long[] p200, OutputStream out, byte[] buf, File f, String relpath, FileStripper[] fs, ArrayList tempjars) throws IOException
/*     */   {
/* 194 */     System.out.println("[Archive] Adding file (" + f.length() + ") " + f);
/*     */ 
/* 196 */     String[] filesToKeep = { 
/* 197 */       "/sunjce_provider.jar", 
/* 198 */       "/jce.jar" };
/*     */ 
/* 201 */     for (int i = 0; i < fs.length; i++) {
/* 202 */       if (fs[i].canLeaveOutFile(f, filesToKeep)) {
/* 203 */         fs[i].addToStrippedTotal(f.length());
/*     */ 
/* 205 */         System.out.println("SKIPPING file (" + f.length() + ") " + f);
/*     */ 
/* 208 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 212 */     relpath = relpath.replace('\\', '/');
/* 213 */     if (relpath.startsWith("/")) {
/* 214 */       throw new IOException("Illegal relative path: " + relpath);
/*     */     }
/* 216 */     if (relpath.startsWith("./")) {
/* 217 */       throw new IOException("Illegal relative path: " + relpath);
/*     */     }
/* 219 */     if (relpath.startsWith("../")) {
/* 220 */       throw new IOException("Illegal relative path: " + relpath);
/*     */     }
/* 222 */     if (relpath.endsWith("/")) {
/* 223 */       relpath = relpath.substring(0, relpath.length() - 1);
/*     */     }
/*     */ 
/* 226 */     if (f.isDirectory()) {
/* 227 */       CFriendlyStreamUtils.writeInt(out, 1);
/* 228 */       CFriendlyStreamUtils.writeString(out, relpath);
/*     */ 
/* 230 */       System.out.println("Adding FOLDER (1) " + relpath);
/*     */ 
/* 232 */       File[] files = f.listFiles();
/* 233 */       for (int i = 0; i < files.length; i++) {
/* 234 */         addFileToStream(pack200allowed, p200, out, buf, files[i], relpath + "/" + files[i].getName(), fs, tempjars);
/*     */       }
/*     */ 
/* 237 */       return;
/*     */     }
/* 239 */     p200[0] += f.length();
/*     */ 
/* 245 */     boolean usePack200 = (pack200allowed) && 
/* 246 */       (relpath.toLowerCase().endsWith(".jar")) && 
/* 247 */       (Pack200Compressor.jarContainsAClass(f)) && 
/* 248 */       (!relpath.toLowerCase().endsWith("sunjce_provider.jar")) && 
/* 249 */       (!relpath.toLowerCase().endsWith("jce.jar"));
/*     */ 
/* 255 */     if (usePack200)
/*     */     {
/* 257 */       if (tempjars != null)
/*     */       {
/* 259 */         tempjars.add(new Object[] { relpath, f });
/* 260 */         return;
/*     */       }
/*     */ 
/* 263 */       CFriendlyStreamUtils.writeInt(out, 2);
/* 264 */       CFriendlyStreamUtils.writeString(out, relpath);
/*     */ 
/* 266 */       long MAX_MEM = 10240000L;
/*     */ 
/* 268 */       if (f.length() > MAX_MEM) {
/* 269 */         File compressTo = File.createTempFile("JWArchiveCompression", "p2");
/* 270 */         OutputStream cout = new BufferedOutputStream(new FileOutputStream(compressTo), 10240);
/* 271 */         Pack200Compressor.initMaxCompression().compressFileToOutputStream(new JarFile(f), cout);
/*     */         try
/*     */         {
/* 274 */           cout.close();
/*     */         } catch (Exception localException) {
/*     */         }
/* 277 */         CFriendlyStreamUtils.writeLong(out, compressTo.length());
/*     */ 
/* 279 */         InputStream in = new BufferedInputStream(new FileInputStream(compressTo), 10240);
/*     */ 
/* 281 */         int n = 0;
/* 282 */         while (n != -1) {
/* 283 */           n = in.read(buf);
/* 284 */           if (n > 0) {
/* 285 */             out.write(buf, 0, n);
/*     */           }
/*     */         }
/* 288 */         in.close();
/*     */ 
/* 290 */         p200[1] += compressTo.length();
/*     */ 
/* 292 */         compressTo.delete();
/*     */       }
/*     */       else {
/* 295 */         compressbuf.reset();
/*     */ 
/* 298 */         Pack200Compressor.initMaxCompression().compressFileToOutputStream(new JarFile(f), compressbuf);
/*     */ 
/* 300 */         CFriendlyStreamUtils.writeLong(out, compressbuf.size());
/*     */ 
/* 302 */         out.write(compressbuf.getByteArray(), 0, compressbuf.size());
/*     */ 
/* 304 */         p200[1] += compressbuf.size();
/*     */       }
/*     */ 
/* 307 */       System.out.println("Added P200-FILE " + relpath + " (" + compressbuf.size() + ")");
/*     */     }
/*     */     else {
/* 310 */       CFriendlyStreamUtils.writeInt(out, 0);
/* 311 */       CFriendlyStreamUtils.writeString(out, relpath);
/* 312 */       CFriendlyStreamUtils.writeLong(out, f.length());
/*     */ 
/* 314 */       System.out.println("Added FILE " + relpath + " (" + f.length() + ")");
/*     */ 
/* 316 */       int NB = 0;
/*     */ 
/* 318 */       InputStream in = new BufferedInputStream(new FileInputStream(f), 10240);
/* 319 */       int n = 0;
/* 320 */       while (n != -1) {
/* 321 */         n = in.read(buf);
/* 322 */         if (n > 0) {
/* 323 */           out.write(buf, 0, n);
/* 324 */           NB += n;
/*     */         }
/*     */       }
/* 327 */       in.close();
/*     */ 
/* 329 */       System.out.println("Wrote FILE " + relpath + " (" + NB + ")");
/*     */ 
/* 331 */       p200[1] += f.length();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void skip(InputStream in, long bytes) throws IOException
/*     */   {
/* 337 */     long N = 0L;
/* 338 */     while (N < bytes) {
/* 339 */       in.read();
/* 340 */       N += 1L;
/*     */     }
/*     */   }
/*     */ 
/* 344 */   public static void printArchive(File f) throws Exception { f = LzmaUtil.decompress(f, new File(f.getAbsolutePath() + ".decompressed"));
/*     */ 
/* 346 */     System.out.println("Reading archive " + f.getName());
/* 347 */     InputStream in = new BufferedInputStream(new FileInputStream(f), 10240);
/*     */     while (true)
/*     */     {
/*     */       try {
/* 351 */         type = CFriendlyStreamUtils.readInt(in);
/*     */       }
/*     */       catch (NumberFormatException x)
/*     */       {
/*     */         int type;
/* 353 */         x.printStackTrace();
/*     */ 
/* 355 */         in.close();
/*     */ 
/* 357 */         return;
/*     */       }
/*     */       int type;
/* 359 */       if (type == 2) {
/* 360 */         System.out.print("File (P200)  : ");
/* 361 */         System.out.print("path=" + CFriendlyStreamUtils.readString(in));
/* 362 */         long len = CFriendlyStreamUtils.readLong(in);
/* 363 */         System.out.print(", len=" + len);
/* 364 */         System.out.println();
/* 365 */         skip(in, len);
/* 366 */       } else if (type == 0) {
/* 367 */         System.out.print("File  : ");
/* 368 */         System.out.print("path=" + CFriendlyStreamUtils.readString(in));
/* 369 */         long len = CFriendlyStreamUtils.readLong(in);
/* 370 */         System.out.print(", len=" + len);
/* 371 */         System.out.println();
/* 372 */         skip(in, len);
/* 373 */       } else if (type == 1) {
/* 374 */         System.out.print("Folder: ");
/* 375 */         System.out.print("path=" + CFriendlyStreamUtils.readString(in));
/* 376 */         System.out.println();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 385 */     printArchive(new File("C:\\Users\\Administrator\\AppData\\Roaming\\JWrapper-JWTestApp\\JWrapper-JWrapper-00014567314-complete\\JWrapper-JWrapper-00014567314-archive.p2"));
/*     */   }
/*     */ 
/*     */   private class P2Thread extends Thread
/*     */   {
/*     */     UnqueuedSemaphore sem;
/*     */     File target;
/*     */     Throwable error;
/* 131 */     long[] p200info = new long[2];
/*     */     File jf;
/*     */     String jrelpath;
/*     */     FileStripper[] fs;
/*     */ 
/*     */     public P2Thread(UnqueuedSemaphore sem, int index, File jf, String jrelpath, FileStripper[] fs)
/*     */     {
/* 138 */       this.sem = sem;
/* 139 */       this.jf = jf;
/* 140 */       this.jrelpath = jrelpath;
/* 141 */       this.fs = fs;
/* 142 */       this.target = new File(Archive.this.dest.getAbsolutePath() + "p2-" + index);
/*     */     }
/*     */     public void run() {
/* 145 */       OutputStream out = null;
/* 146 */       this.sem.doWait(1);
/*     */       try {
/* 148 */         out = new BufferedOutputStream(new FileOutputStream(this.target), 50000);
/* 149 */         Archive.addFileToStream(Archive.this.pack200allowed, this.p200info, out, Archive.this.buf, this.jf, this.jrelpath, this.fs, null);
/* 150 */         out.close();
/*     */       } catch (Throwable x) {
/* 152 */         this.error = x;
/*     */         try {
/* 154 */           out.close();
/*     */         } catch (Exception localException) {
/*     */         }
/*     */       }
/* 158 */       this.sem.doSignal(1);
/*     */     }
/*     */ 
/*     */     public void copyTo(OutputStream stream, byte[] buf, long[] p200) throws Throwable {
/* 162 */       if (this.error != null) throw this.error;
/*     */ 
/* 164 */       InputStream in = new BufferedInputStream(new FileInputStream(this.target));
/*     */ 
/* 167 */       int n = 0;
/* 168 */       while (n != -1) {
/* 169 */         n = in.read(buf);
/* 170 */         if (n > 0) {
/* 171 */           Archive.this.out.write(buf, 0, n);
/*     */         }
/*     */       }
/*     */ 
/* 175 */       in.close();
/* 176 */       this.target.delete();
/*     */ 
/* 178 */       for (int i = 0; i < p200.length; i++)
/* 179 */         p200[i] += this.p200info[i];
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.archive.Archive
 * JD-Core Version:    0.6.2
 */