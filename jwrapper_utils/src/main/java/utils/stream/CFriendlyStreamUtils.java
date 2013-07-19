/*     */ package utils.stream;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ 
/*     */ public class CFriendlyStreamUtils
/*     */ {
/*     */   public static void writeString(OutputStream out, String s)
/*     */     throws IOException
/*     */   {
/*  14 */     byte[] dat = s.getBytes("ASCII");
/*  15 */     for (int i = 0; i < dat.length; i++) {
/*  16 */       if (dat[i] == 0) {
/*  17 */         throw new IOException("ERROR: NUL character not allowed in CFriendly strings!");
/*     */       }
/*     */     }
/*  20 */     out.write(dat);
/*  21 */     out.write(0);
/*     */   }
/*     */   public static void writeLong(OutputStream out, long x) throws IOException {
/*  24 */     String s = Long.toString(x);
/*  25 */     writeString(out, s);
/*     */   }
/*     */   public static void writeInt(OutputStream out, int x) throws IOException {
/*  28 */     writeLong(out, x);
/*     */   }
/*     */   public static void writeBoolean(OutputStream out, boolean x) throws IOException {
/*  31 */     if (x)
/*  32 */       writeLong(out, 1L);
/*     */     else
/*  34 */       writeLong(out, 0L);
/*     */   }
/*     */ 
/*     */   public static void writeString(RandomAccessFile out, String s) throws IOException {
/*  38 */     byte[] dat = s.getBytes("ASCII");
/*  39 */     for (int i = 0; i < dat.length; i++) {
/*  40 */       if (dat[i] == 0) {
/*  41 */         throw new IOException("ERROR: NUL character not allowed in CFriendly strings!");
/*     */       }
/*     */     }
/*  44 */     out.write(dat);
/*  45 */     out.write(0);
/*     */   }
/*     */   public static void writeLong(RandomAccessFile out, long x) throws IOException {
/*  48 */     String s = Long.toString(x);
/*  49 */     writeString(out, s);
/*     */   }
/*     */   public static void writeInt(RandomAccessFile out, int x) throws IOException {
/*  52 */     writeLong(out, x);
/*     */   }
/*     */   public static void writeBoolean(RandomAccessFile out, boolean x) throws IOException {
/*  55 */     if (x)
/*  56 */       writeLong(out, 1L);
/*     */     else
/*  58 */       writeLong(out, 0L);
/*     */   }
/*     */ 
/*     */   public static String readString(InputStream in) throws IOException
/*     */   {
/*  63 */     StringBuffer sb = new StringBuffer();
/*  64 */     int n = in.read();
/*     */ 
/*  66 */     if (n == -1) {
/*  67 */       throw new EOFException("stream finished");
/*     */     }
/*     */ 
/*  70 */     while ((n != 0) && (n != -1)) {
/*  71 */       sb.append((char)n);
/*  72 */       n = in.read();
/*     */     }
/*     */ 
/*  75 */     return sb.toString();
/*     */   }
/*     */   public static long readLong(InputStream in) throws IOException {
/*  78 */     return Long.parseLong(readString(in));
/*     */   }
/*     */   public static int readInt(InputStream in) throws IOException {
/*  81 */     return Integer.parseInt(readString(in));
/*     */   }
/*     */   public static boolean readBoolean(InputStream in) throws IOException {
/*  84 */     return readInt(in) == 1;
/*     */   }
/*     */   public static String readString(RandomAccessFile in) throws IOException {
/*  87 */     StringBuffer sb = new StringBuffer();
/*  88 */     int n = in.read();
/*     */ 
/*  90 */     if (n == -1) {
/*  91 */       throw new EOFException("stream finished");
/*     */     }
/*     */ 
/*  94 */     while ((n != 0) && (n != -1)) {
/*  95 */       sb.append((char)n);
/*  96 */       n = in.read();
/*     */     }
/*  98 */     return sb.toString();
/*     */   }
/*     */   public static long readLong(RandomAccessFile in) throws IOException {
/* 101 */     return Long.parseLong(readString(in));
/*     */   }
/*     */   public static int readInt(RandomAccessFile in) throws IOException {
/* 104 */     return Integer.parseInt(readString(in));
/*     */   }
/*     */   public static boolean readBoolean(RandomAccessFile in) throws IOException {
/* 107 */     return readInt(in) == 1;
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.stream.CFriendlyStreamUtils
 * JD-Core Version:    0.6.2
 */