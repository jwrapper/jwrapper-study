/*     */ package utils.stream;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.nio.MappedByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileChannel.MapMode;
/*     */ import java.nio.channels.FileLock;
/*     */ 
/*     */ public class MappedFile
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   public static final int SIZE_HIGH_PERFORMANCE = 500000;
/*     */   public static final int SIZE_MEM_EFFICIENT = 50000;
/*  39 */   int LOCK_COUNT = 2;
/*     */   static final int W = 0;
/*     */   static final int R = 10;
/*     */   static final int WALIVE = 20;
/*     */   static final int RALIVE = 30;
/*     */   String id;
/*     */   File raffile;
/*     */   RandomAccessFile raf;
/*     */   FileChannel chan;
/*     */   MappedByteBuffer buf;
/*  53 */   int DATAPOS = 40;
/*  54 */   int DATALEN = this.DATAPOS + 50000;
/*     */ 
/*  56 */   int MAXWRITE = this.DATALEN - this.DATAPOS;
/*     */   boolean writer;
/*     */   FileLock WN;
/*     */   FileLock RN;
/*     */   FileLock IAMALIVE;
/*  63 */   boolean highPerformance = false;
/*     */   byte[] data;
/* 208 */   int N = 0;
/*     */ 
/*     */   public void setHighPerformance(boolean b)
/*     */   {
/*  74 */     this.highPerformance = b;
/*     */   }
/*     */ 
/*     */   public MappedFile(File file, int size, boolean writer, long timeout) throws IOException, InterruptedException {
/*  78 */     this.writer = writer;
/*     */ 
/*  80 */     long fail = System.currentTimeMillis() + timeout;
/*     */ 
/*  82 */     this.MAXWRITE = size;
/*  83 */     this.DATALEN = (this.MAXWRITE + this.DATAPOS + 8);
/*     */ 
/*  85 */     this.raffile = file;
/*     */ 
/*  87 */     this.raf = new RandomAccessFile(file, "rw");
/*  88 */     this.raf.setLength(this.DATALEN);
/*     */ 
/*  90 */     if (!writer) this.data = new byte[this.MAXWRITE];
/*     */ 
/*  92 */     this.chan = this.raf.getChannel();
/*     */ 
/*  94 */     this.buf = this.chan.map(FileChannel.MapMode.READ_WRITE, 0L, this.raf.length());
/*     */ 
/*  96 */     if (!this.buf.isLoaded()) {
/*  97 */       System.out.println("Loading");
/*  98 */       this.buf.load();
/*     */     }
/*     */ 
/* 101 */     if (writer)
/*     */     {
/* 103 */       this.id = "[Writer] ";
/*     */ 
/* 105 */       this.WN = getLock(0, this.N);
/*     */ 
/* 107 */       this.IAMALIVE = getLock(20, 0);
/*     */ 
/* 109 */       while (!isLocked(30))
/*     */       {
/* 111 */         Thread.sleep(100L);
/*     */ 
/* 113 */         if (System.currentTimeMillis() > fail) throw new IOException("Timed out");
/*     */       }
/*     */ 
/* 116 */       System.out.println("[MappedFile] connected (writer)");
/*     */     }
/*     */     else
/*     */     {
/* 121 */       this.id = "[Reader] ";
/*     */ 
/* 123 */       this.RN = getLock(10, this.N + 1);
/*     */ 
/* 125 */       this.IAMALIVE = getLock(30, 0);
/*     */ 
/* 127 */       while (!isLocked(20))
/*     */       {
/* 129 */         Thread.sleep(100L);
/*     */ 
/* 131 */         if (System.currentTimeMillis() > fail) throw new IOException("Timed out");
/*     */       }
/*     */ 
/* 134 */       System.out.println("[MappedFile] connected (reader)");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close() {
/*     */     try {
/* 140 */       this.IAMALIVE.release();
/*     */     } catch (Exception localException) {
/*     */     }
/*     */     try {
/* 144 */       this.raf.close(); } catch (Exception localException1) {
/*     */     }
/*     */     try {
/* 147 */       this.raffile.delete(); } catch (Exception localException2) {
/*     */     }
/*     */   }
/*     */ 
/*     */   FileLock getLock(int SECTION, int N) throws IOException {
/* 152 */     long rCount = 0L;
/*     */ 
/* 154 */     if (!this.highPerformance) rCount = 200L;
/*     */ 
/*     */     try
/*     */     {
/* 164 */       FileLock lock = this.chan.tryLock(SECTION + N % this.LOCK_COUNT, 1L, false);
/* 165 */       if (lock != null)
/*     */       {
/* 169 */         return lock;
/*     */       }
/* 171 */       throw new IOException("ourdeadlock");
/*     */     }
/*     */     catch (IOException x) {
/* 174 */       while (x.getMessage().indexOf("deadlock") != -1)
/*     */       {
/*     */         try
/*     */         {
/* 179 */           rCount += 1L;
/*     */ 
/* 181 */           if (rCount < 200L)
/* 182 */             Thread.sleep(1L);
/* 183 */           else if (rCount < 400L)
/* 184 */             Thread.sleep(10L);
/*     */           else
/* 186 */             Thread.sleep(100L);
/*     */         } catch (InterruptedException xx) {
/* 188 */           xx.printStackTrace();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 193 */       x.printStackTrace();
/* 194 */       throw x;
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean isLocked(int SECTION) throws IOException {
/* 199 */     FileLock fl = this.chan.tryLock(SECTION, 1L, false);
/* 200 */     if (fl == null) {
/* 201 */       return true;
/*     */     }
/* 203 */     fl.release();
/* 204 */     return false;
/*     */   }
/*     */ 
/*     */   public byte[] readBlock()
/*     */     throws IOException
/*     */   {
/* 211 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/* 212 */     readBlock(bout);
/* 213 */     return bout.toByteArray();
/*     */   }
/*     */ 
/*     */   public void readBlock(OutputStream out) throws IOException {
/* 217 */     if (!isLocked(20)) {
/* 218 */       throw new IOException("MappedFile disconnected");
/*     */     }
/*     */ 
/* 221 */     int read = 0;
/* 222 */     int rem = 0;
/* 223 */     int tot = 0;
/*     */ 
/* 225 */     boolean done = false;
/* 226 */     boolean first = true;
/*     */ 
/* 228 */     while (!done)
/*     */     {
/* 232 */       FileLock WN = getLock(0, this.N);
/*     */ 
/* 235 */       this.buf.position(this.DATAPOS);
/*     */ 
/* 237 */       if (first) {
/* 238 */         rem = this.buf.getInt();
/* 239 */         first = false;
/*     */       }
/*     */ 
/* 242 */       if (rem > this.MAXWRITE) {
/* 243 */         this.buf.get(this.data, 0, this.MAXWRITE);
/* 244 */         read = this.MAXWRITE;
/* 245 */         rem -= read;
/*     */       } else {
/* 247 */         this.buf.get(this.data, 0, rem);
/* 248 */         read = rem;
/* 249 */         rem -= read;
/* 250 */         done = true;
/*     */       }
/*     */ 
/* 257 */       WN.release();
/*     */ 
/* 259 */       this.N += 1;
/*     */ 
/* 261 */       FileLock RNold = this.RN;
/* 262 */       this.RN = getLock(10, this.N + 1);
/*     */ 
/* 264 */       RNold.release();
/*     */ 
/* 272 */       out.write(this.data, 0, read);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeBlock(byte[] dat) throws IOException {
/* 277 */     writeBlock(dat, 0, dat.length);
/*     */   }
/*     */ 
/*     */   public void writeBlock(byte[] dat, int off, int len) throws IOException {
/* 281 */     if (!isLocked(30)) {
/* 282 */       throw new IOException("MappedFile disconnected");
/*     */     }
/*     */ 
/* 285 */     boolean done = false;
/* 286 */     boolean first = true;
/*     */ 
/* 288 */     System.out.println("[MappedFile] in writeBlock()");
/* 289 */     while (!done)
/*     */     {
/* 292 */       FileLock RN = getLock(10, this.N);
/*     */ 
/* 298 */       this.buf.position(this.DATAPOS);
/*     */ 
/* 300 */       if (first)
/*     */       {
/* 302 */         this.buf.putInt(dat.length);
/* 303 */         first = false;
/*     */       }
/*     */ 
/* 306 */       if (len > this.MAXWRITE) {
/* 307 */         this.buf.put(dat, off, this.MAXWRITE);
/* 308 */         off += this.MAXWRITE;
/* 309 */         len -= this.MAXWRITE;
/*     */       } else {
/* 311 */         this.buf.put(dat, off, len);
/* 312 */         done = true;
/*     */       }
/*     */ 
/* 315 */       RN.release();
/*     */ 
/* 318 */       this.N += 1;
/*     */ 
/* 320 */       FileLock WNold = this.WN;
/*     */ 
/* 322 */       this.WN = getLock(0, this.N);
/*     */ 
/* 324 */       WNold.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 332 */     int MFSIZ = 500000;
/* 333 */     int SIZ = 30000000;
/*     */ 
/* 335 */     if (args[0].equals("writer")) {
/* 336 */       MappedFile mf = new MappedFile(new File("mappedfile"), MFSIZ, true, 20000L);
/*     */ 
/* 338 */       ByteArrayOutputStream bout = new ByteArrayOutputStream(SIZ);
/* 339 */       for (int i = 0; i < SIZ / 4; i++) {
/* 340 */         StreamUtils.writeInt(bout, i);
/*     */       }
/*     */ 
/* 343 */       byte[] bb = bout.toByteArray();
/*     */ 
/* 345 */       mf.writeBlock(bb);
/* 346 */       mf.writeBlock(bb);
/*     */     }
/*     */     else {
/* 349 */       MappedFile mf = new MappedFile(new File("mappedfile"), MFSIZ, false, 20000L);
/*     */ 
/* 360 */       ByteArrayOutputStream bout = new ByteArrayOutputStream(SIZ);
/*     */ 
/* 362 */       mf.readBlock(bout);
/*     */ 
/* 364 */       long T = System.currentTimeMillis();
/*     */ 
/* 366 */       mf.readBlock(bout);
/*     */ 
/* 368 */       T = System.currentTimeMillis() - T;
/* 369 */       System.out.println(T + "ms");
/*     */ 
/* 371 */       double MB = SIZ / 1000000.0D;
/* 372 */       double sec = T / 1000.0D;
/*     */ 
/* 374 */       System.out.println(MB / sec + "MB/sec");
/*     */     }
/*     */ 
/* 377 */     Thread.sleep(5000L);
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.stream.MappedFile
 * JD-Core Version:    0.6.2
 */