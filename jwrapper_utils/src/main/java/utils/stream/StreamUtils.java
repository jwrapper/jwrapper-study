/*     */ package utils.stream;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import utils.translation.UtilMessages;
/*     */ 
/*     */ public class StreamUtils
/*     */ {
/*     */   private static final byte CLASS_INT = 0;
/*     */   private static final byte CLASS_LONG = 1;
/*     */   private static final byte CLASS_BOOLEAN = 3;
/*     */   private static final byte CLASS_DOUBLE = 4;
/*     */   private static final byte CLASS_FLOAT = 5;
/*     */   private static final byte CLASS_COLLECTION = 6;
/*     */   private static final byte CLASS_STRING = 7;
/*     */   private static final byte CLASS_CHAR = 8;
/*     */ 
/*     */   public static Object readObject(InputStream in)
/*     */     throws Exception
/*     */   {
/*  32 */     String claz = readString(in);
/*     */ 
/*  34 */     Class clazz = Class.forName(claz);
/*  35 */     Object o = clazz.newInstance();
/*     */ 
/*  37 */     int fieldcount = readInt(in);
/*     */ 
/*  39 */     Field[] fields = clazz.getDeclaredFields();
/*     */ 
/*  41 */     for (int i = 0; i < fieldcount; i++)
/*     */     {
/*  43 */       String fname = readString(in);
/*  44 */       Field field = null;
/*     */ 
/*  46 */       for (int f = 0; f < fields.length; f++) {
/*  47 */         if (fields[f].getName().equals(fname)) {
/*  48 */           field = fields[f];
/*  49 */           break;
/*     */         }
/*     */       }
/*     */ 
/*  53 */       if (field == null) throw new Exception(UtilMessages.getString("StreamUtils.0") + fname + UtilMessages.getString("StreamUtils.1"));
/*     */ 
/*  55 */       field.setAccessible(true);
/*     */ 
/*  57 */       int tmp = in.read();
/*  58 */       if (tmp == -1) throw new EOFException(UtilMessages.getString("StreamUtils.2"));
/*  59 */       byte fclaz = (byte)tmp;
/*     */ 
/*  61 */       if (fclaz == 0) {
/*  62 */         field.setInt(o, readInt(in));
/*     */       }
/*  64 */       else if (fclaz == 1) {
/*  65 */         field.setLong(o, readLong(in));
/*     */       }
/*  67 */       else if (fclaz == 3) {
/*  68 */         field.setBoolean(o, readBoolean(in));
/*     */       }
/*  70 */       else if (fclaz == 4) {
/*  71 */         field.setDouble(o, readDouble(in));
/*     */       }
/*  73 */       else if (fclaz == 5) {
/*  74 */         field.setFloat(o, readFloat(in));
/*     */       }
/*  76 */       else if (fclaz == 8) {
/*  77 */         field.setChar(o, readChar(in));
/*     */       }
/*  79 */       else if (fclaz == 7) {
/*  80 */         field.set(o, readString(in));
/*     */       }
/*  82 */       else if (fclaz == 6) {
/*  83 */         String colname = readString(in);
/*  84 */         Class colclazz = Class.forName(colname);
/*  85 */         Collection col = (Collection)colclazz.newInstance();
/*     */ 
/*  87 */         int colsiz = readInt(in);
/*     */ 
/*  89 */         for (int z = 0; z < colsiz; z++) {
/*  90 */           col.add(readString(in));
/*     */         }
/*     */ 
/*  93 */         field.set(o, col);
/*     */       }
/*     */       else {
/*  96 */         throw new IllegalArgumentException(UtilMessages.getString("StreamUtils.3") + fclaz + UtilMessages.getString("StreamUtils.4"));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 101 */     return o;
/*     */   }
/*     */ 
/*     */   public static void writeObject(OutputStream out, Object o)
/*     */     throws Exception
/*     */   {
/* 116 */     String claz = o.getClass().getName();
/* 117 */     writeString(out, claz);
/*     */ 
/* 119 */     Class clazz = o.getClass();
/* 120 */     Field[] fields = clazz.getDeclaredFields();
/*     */ 
/* 122 */     writeInt(out, fields.length);
/*     */ 
/* 124 */     for (int i = 0; i < fields.length; i++) {
/* 125 */       fields[i].setAccessible(true);
/*     */ 
/* 127 */       String fname = fields[i].getName();
/* 128 */       writeString(out, fname);
/*     */ 
/* 130 */       Class fclazz = fields[i].getType();
/* 131 */       if (fclazz == Integer.TYPE) {
/* 132 */         out.write(0);
/* 133 */         writeInt(out, fields[i].getInt(o));
/*     */       }
/* 135 */       else if (fclazz == Long.TYPE) {
/* 136 */         out.write(1);
/* 137 */         writeLong(out, fields[i].getLong(o));
/*     */       }
/* 139 */       else if (fclazz == Boolean.TYPE) {
/* 140 */         out.write(3);
/* 141 */         writeBoolean(out, fields[i].getBoolean(o));
/*     */       }
/* 143 */       else if (fclazz == Double.TYPE) {
/* 144 */         out.write(4);
/* 145 */         writeDouble(out, fields[i].getDouble(o));
/*     */       }
/* 147 */       else if (fclazz == Float.TYPE) {
/* 148 */         out.write(5);
/* 149 */         writeFloat(out, fields[i].getFloat(o));
/*     */       }
/* 151 */       else if (fclazz == Character.TYPE) {
/* 152 */         out.write(8);
/* 153 */         writeChar(out, fields[i].getChar(o));
/*     */       }
/* 155 */       else if (fclazz == String.class) {
/* 156 */         out.write(7);
/* 157 */         String s = (String)fields[i].get(o);
/* 158 */         writeString(out, s);
/*     */       }
/* 160 */       else if (Collection.class.isAssignableFrom(fclazz)) {
/* 161 */         out.write(6);
/* 162 */         writeString(out, fclazz.getName());
/* 163 */         Collection col = (Collection)fields[i].get(o);
/*     */ 
/* 165 */         writeInt(out, col.size());
/*     */ 
/* 167 */         Iterator it = col.iterator();
/*     */ 
/* 169 */         while (it.hasNext()) {
/* 170 */           String s = (String)it.next();
/* 171 */           writeString(out, s);
/*     */         }
/*     */       } else {
/* 174 */         throw new IllegalArgumentException(UtilMessages.getString("StreamUtils.5") + fclazz + UtilMessages.getString("StreamUtils.6"));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String readAllAsString(InputStream in)
/*     */     throws IOException
/*     */   {
/* 187 */     byte[] dat = readAll(in);
/* 188 */     return new String(dat, 0, dat.length, "ISO-8859-1");
/*     */   }
/*     */ 
/*     */   public static String readAllAsStringUTF8(InputStream in)
/*     */     throws IOException
/*     */   {
/* 197 */     byte[] dat = readAll(in);
/* 198 */     return new String(dat, 0, dat.length, "UTF-8");
/*     */   }
/*     */ 
/*     */   public static String readAllAsStringASCII(InputStream in) throws IOException {
/* 202 */     byte[] dat = readAll(in);
/* 203 */     return new String(dat, 0, dat.length, "ASCII");
/*     */   }
/*     */ 
/*     */   public static byte[] readAllAndClose(InputStream in) throws IOException {
/* 207 */     byte[] dat = readAll(in);
/* 208 */     in.close();
/* 209 */     return dat;
/*     */   }
/*     */ 
/*     */   public static byte[] readAll(InputStream in)
/*     */     throws IOException
/*     */   {
/* 218 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/* 219 */     byte[] buf = new byte[65536];
/* 220 */     int n = 0;
/*     */ 
/* 222 */     while (n != -1) {
/* 223 */       n = in.read(buf, 0, 65536);
/* 224 */       if (n > 0) {
/* 225 */         bout.write(buf, 0, n);
/*     */       }
/*     */     }
/*     */ 
/* 229 */     return bout.toByteArray();
/*     */   }
/*     */ 
/*     */   public static byte[] readAllPossible(InputStream in)
/*     */     throws IOException
/*     */   {
/* 238 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/* 239 */     byte[] buf = new byte[65536];
/* 240 */     int n = 0;
/*     */     try
/*     */     {
/* 243 */       while (n != -1) {
/* 244 */         n = in.read(buf, 0, 65536);
/* 245 */         if (n > 0) {
/* 246 */           bout.write(buf, 0, n);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (EOFException localEOFException)
/*     */     {
/*     */     }
/* 253 */     return bout.toByteArray();
/*     */   }
/*     */ 
/*     */   public static void writeBytes(OutputStream out, byte[] b)
/*     */     throws IOException
/*     */   {
/* 265 */     writeInt(out, b.length);
/* 266 */     out.write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public static void writeBytes(OutputStream out, byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 278 */     writeInt(out, len);
/* 279 */     out.write(b, off, len);
/*     */   }
/*     */ 
/*     */   public static void writeString(OutputStream out, String n)
/*     */     throws IOException
/*     */   {
/* 288 */     writeBytes(out, n.getBytes());
/*     */   }
/*     */   public static void writeStringUTF8(OutputStream out, String n) throws IOException {
/* 291 */     writeBytes(out, n.getBytes("UTF-8"));
/*     */   }
/*     */   public static void writeStringASCII(OutputStream out, String n) throws IOException {
/* 294 */     writeBytes(out, n.getBytes("ASCII"));
/*     */   }
/*     */ 
/*     */   public static void writeLong(OutputStream out, long n)
/*     */     throws IOException
/*     */   {
/* 304 */     out.write((byte)(int)(n >>> 56));
/* 305 */     out.write((byte)(int)(n >>> 48));
/* 306 */     out.write((byte)(int)(n >>> 40));
/* 307 */     out.write((byte)(int)(n >>> 32));
/* 308 */     out.write((byte)(int)(n >>> 24));
/* 309 */     out.write((byte)(int)(n >>> 16));
/* 310 */     out.write((byte)(int)(n >>> 8));
/* 311 */     out.write((byte)(int)n);
/*     */   }
/*     */ 
/*     */   public static void writeDouble(OutputStream out, double n)
/*     */     throws IOException
/*     */   {
/* 322 */     writeLong(out, Double.doubleToLongBits(n));
/*     */   }
/*     */ 
/*     */   public static void writeInt(OutputStream out, int n)
/*     */     throws IOException
/*     */   {
/* 333 */     out.write((byte)(n >>> 24));
/* 334 */     out.write((byte)(n >>> 16));
/* 335 */     out.write((byte)(n >>> 8));
/* 336 */     out.write((byte)n);
/*     */   }
/*     */ 
/*     */   public static void writeFloat(OutputStream out, float n)
/*     */     throws IOException
/*     */   {
/* 347 */     writeInt(out, Float.floatToIntBits(n));
/*     */   }
/*     */ 
/*     */   public static void writeShort(OutputStream out, short n)
/*     */     throws IOException
/*     */   {
/* 358 */     out.write((byte)(n >>> 8));
/* 359 */     out.write((byte)n);
/*     */   }
/*     */ 
/*     */   public static void writeChar(OutputStream out, char v)
/*     */     throws IOException
/*     */   {
/* 370 */     out.write((byte)(0xFF & v >> '\b'));
/* 371 */     out.write((byte)(0xFF & v));
/*     */   }
/*     */ 
/*     */   public static void writeBoolean(OutputStream out, boolean n)
/*     */     throws IOException
/*     */   {
/* 382 */     if (n)
/* 383 */       out.write(255);
/*     */     else
/* 385 */       out.write(238);
/*     */   }
/*     */ 
/*     */   public static byte[] readBytes(InputStream in, int len)
/*     */     throws IOException
/*     */   {
/* 397 */     byte[] b = new byte[len];
/*     */ 
/* 399 */     int red = 0;
/* 400 */     int tot = 0;
/* 401 */     while (tot < len) {
/* 402 */       red = in.read(b, tot, len - tot);
/* 403 */       if (red == -1) {
/* 404 */         throw new EOFException(UtilMessages.getString("StreamUtils.8"));
/*     */       }
/* 406 */       tot += red;
/*     */     }
/*     */ 
/* 410 */     return b;
/*     */   }
/*     */ 
/*     */   public static String readLineAsStringUTF8(InputStream in) throws IOException {
/* 414 */     return new String(readLine(in), "UTF-8");
/*     */   }
/*     */ 
/*     */   public static byte[] readLine(InputStream in) throws IOException {
/* 418 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/*     */ 
/* 425 */     int last = -1;
/* 426 */     int c = in.read();
/*     */ 
/* 428 */     if (c == -1) throw new EOFException(UtilMessages.getString("StreamUtils.9"));
/*     */ 
/* 430 */     while ((c != 10) && (c != -1))
/*     */     {
/* 432 */       if (last != -1) bout.write(last);
/* 433 */       last = c;
/*     */ 
/* 435 */       c = in.read();
/*     */     }
/*     */ 
/* 438 */     if ((last != -1) && (last != 13)) {
/* 439 */       bout.write(last);
/*     */     }
/*     */ 
/* 442 */     return bout.toByteArray();
/*     */   }
/*     */   public static byte[] readLineInclusive(InputStream in) throws IOException {
/* 445 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/*     */ 
/* 448 */     int c = in.read();
/*     */ 
/* 450 */     if (c == -1) throw new EOFException(UtilMessages.getString("StreamUtils.10"));
/*     */ 
/* 452 */     while ((c != 10) && (c != -1))
/*     */     {
/* 454 */       bout.write(c);
/*     */ 
/* 458 */       c = in.read();
/*     */     }
/*     */ 
/* 464 */     if (c != -1) {
/* 465 */       bout.write(c);
/*     */     }
/*     */ 
/* 468 */     return bout.toByteArray();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static byte[] readBytes(InputStream in)
/*     */     throws IOException
/*     */   {
/* 481 */     int len = readInt(in);
/* 482 */     byte[] b = new byte[len];
/*     */ 
/* 484 */     int red = 0;
/* 485 */     int tot = 0;
/* 486 */     while (tot < len) {
/* 487 */       red = in.read(b, tot, len - tot);
/* 488 */       if (red == -1) {
/* 489 */         throw new EOFException(UtilMessages.getString("StreamUtils.11"));
/*     */       }
/* 491 */       tot += red;
/*     */     }
/*     */ 
/* 495 */     return b;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static String readString(InputStream in)
/*     */     throws IOException
/*     */   {
/* 505 */     return new String(readBytes(in));
/*     */   }
/*     */   public static String readStringUTF8(InputStream in) throws IOException {
/* 508 */     return new String(readBytes(in), "UTF-8");
/*     */   }
/*     */ 
/*     */   public static byte[] readNBytes(InputStream in, int maxlen)
/*     */     throws IOException
/*     */   {
/* 521 */     int len = readInt(in);
/*     */ 
/* 523 */     if (len < 0) {
/* 524 */       throw new IOException(UtilMessages.getString("StreamUtils.12") + len + UtilMessages.getString("StreamUtils.13"));
/*     */     }
/* 526 */     if (len > maxlen) {
/* 527 */       throw new IOException(UtilMessages.getString("StreamUtils.14") + len + UtilMessages.getString("StreamUtils.15"));
/*     */     }
/*     */ 
/* 530 */     byte[] b = new byte[len];
/*     */ 
/* 532 */     int red = 0;
/* 533 */     int tot = 0;
/* 534 */     while (tot < len) {
/* 535 */       red = in.read(b, tot, len - tot);
/* 536 */       if (red == -1) {
/* 537 */         throw new EOFException(UtilMessages.getString("StreamUtils.16"));
/*     */       }
/* 539 */       tot += red;
/*     */     }
/*     */ 
/* 543 */     return b;
/*     */   }
/*     */ 
/*     */   public static String readNString(InputStream in, int maxlen)
/*     */     throws IOException
/*     */   {
/* 553 */     return new String(readNBytes(in, maxlen));
/*     */   }
/*     */ 
/*     */   public static long readLong(InputStream in)
/*     */     throws IOException
/*     */   {
/* 563 */     long n = 0L;
/* 564 */     int r = 0;
/*     */ 
/* 566 */     for (int i = 0; i < 8; i++) {
/* 567 */       r = in.read();
/* 568 */       if (r == -1) throw new EOFException(UtilMessages.getString("StreamUtils.17"));
/* 569 */       n = n << 8 | r;
/*     */     }
/*     */ 
/* 572 */     return n;
/*     */   }
/*     */ 
/*     */   public static double readDouble(InputStream in)
/*     */     throws IOException
/*     */   {
/* 581 */     long l = readLong(in);
/* 582 */     return Double.longBitsToDouble(l);
/*     */   }
/*     */ 
/*     */   public static int readInt(InputStream in)
/*     */     throws IOException
/*     */   {
/* 591 */     int n = 0;
/* 592 */     int r = 0;
/*     */ 
/* 594 */     for (int i = 0; i < 4; i++) {
/* 595 */       r = in.read();
/* 596 */       if (r == -1) throw new EOFException(UtilMessages.getString("StreamUtils.18"));
/* 597 */       n = n << 8 | r;
/*     */     }
/*     */ 
/* 600 */     return n;
/*     */   }
/*     */ 
/*     */   public static float readFloat(InputStream in)
/*     */     throws IOException
/*     */   {
/* 609 */     int i = readInt(in);
/* 610 */     return Float.intBitsToFloat(i);
/*     */   }
/*     */ 
/*     */   public static short readShort(InputStream in)
/*     */     throws IOException
/*     */   {
/* 619 */     short n = 0;
/* 620 */     int r = 0;
/*     */ 
/* 622 */     for (int i = 0; i < 2; i++) {
/* 623 */       r = in.read();
/* 624 */       if (r == -1) throw new EOFException(UtilMessages.getString("StreamUtils.19"));
/* 625 */       n = (short)(n << 8 | r);
/*     */     }
/*     */ 
/* 628 */     return n;
/*     */   }
/*     */ 
/*     */   public static char readChar(InputStream in)
/*     */     throws IOException
/*     */   {
/* 637 */     int a = in.read();
/* 638 */     int b = in.read();
/* 639 */     if ((a == -1) || (b == -1)) throw new EOFException(UtilMessages.getString("StreamUtils.20"));
/* 640 */     return (char)(a << 8 | b & 0xFF);
/*     */   }
/*     */ 
/*     */   public static boolean readBoolean(InputStream in)
/*     */     throws IOException
/*     */   {
/* 650 */     int n = in.read();
/* 651 */     if (n == -1) throw new EOFException(UtilMessages.getString("StreamUtils.21"));
/*     */ 
/* 653 */     if (n == 255) {
/* 654 */       return true;
/*     */     }
/* 656 */     return false;
/*     */   }
/*     */ 
/*     */   public static void writeIntArray(OutputStream out, int[] array)
/*     */     throws IOException
/*     */   {
/* 662 */     writeInt(out, array.length);
/* 663 */     for (int i = 0; i < array.length; i++)
/* 664 */       writeInt(out, array[i]);
/*     */   }
/*     */ 
/*     */   public static void writeDoubleArray(OutputStream out, double[] array) throws IOException
/*     */   {
/* 669 */     writeInt(out, array.length);
/* 670 */     for (int i = 0; i < array.length; i++)
/* 671 */       writeDouble(out, array[i]);
/*     */   }
/*     */ 
/*     */   public static void writeLongArray(OutputStream out, long[] array) throws IOException
/*     */   {
/* 676 */     writeInt(out, array.length);
/* 677 */     for (int i = 0; i < array.length; i++)
/* 678 */       writeLong(out, array[i]);
/*     */   }
/*     */ 
/*     */   public static void readIntArray(InputStream inStream, int[] array) throws IOException
/*     */   {
/* 683 */     int length = readInt(inStream);
/* 684 */     for (int i = 0; i < length; i++)
/* 685 */       array[i] = readInt(inStream);
/*     */   }
/*     */ 
/*     */   public static int[] readIntArray(InputStream inStream) throws IOException
/*     */   {
/* 690 */     int length = readInt(inStream);
/* 691 */     int[] array = new int[length];
/* 692 */     for (int i = 0; i < length; i++)
/* 693 */       array[i] = readInt(inStream);
/* 694 */     return array;
/*     */   }
/*     */ 
/*     */   public static void readDoubleArray(InputStream inStream, double[] array) throws IOException
/*     */   {
/* 699 */     int length = readInt(inStream);
/* 700 */     for (int i = 0; i < length; i++)
/* 701 */       array[i] = readDouble(inStream);
/*     */   }
/*     */ 
/*     */   public static double[] readDoubleArray(InputStream inStream) throws IOException
/*     */   {
/* 706 */     int length = readInt(inStream);
/* 707 */     double[] array = new double[length];
/* 708 */     for (int i = 0; i < length; i++)
/* 709 */       array[i] = readDouble(inStream);
/* 710 */     return array;
/*     */   }
/*     */ 
/*     */   public static void readLongArray(InputStream inStream, long[] array) throws IOException
/*     */   {
/* 715 */     int length = readInt(inStream);
/* 716 */     for (int i = 0; i < length; i++)
/* 717 */       array[i] = readLong(inStream);
/*     */   }
/*     */ 
/*     */   public static long[] readLongArray(InputStream inStream) throws IOException
/*     */   {
/* 722 */     int length = readInt(inStream);
/* 723 */     long[] array = new long[length];
/* 724 */     for (int i = 0; i < length; i++)
/* 725 */       array[i] = readLong(inStream);
/* 726 */     return array;
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.stream.StreamUtils
 * JD-Core Version:    0.6.2
 */