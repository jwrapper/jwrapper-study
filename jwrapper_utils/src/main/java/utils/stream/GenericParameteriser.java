/*     */ package utils.stream;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class GenericParameteriser
/*     */ {
/*  23 */   public static final byte[] defStart = { 127, 
/*  24 */     -16, -126, -54, 76, 117, -81, 21, 55, -61, -41, 
/*  25 */     24, -2, -121, 53, 42, -117, -95, -25, -106, 35, 
/*  26 */     -50, 93, -113, -120, 44, -84, -66, -113, -22, -93, 
/*  27 */     59, -11, -100, 7, -106, -106, -14, -30, 69, -42, 
/*  28 */     -26, -83, 119, 101, 5, -98, 44, 5, 83, 1, 
/*  29 */     -58, 117, 47, 71, -49, -44, -25, 116, 53, -9, 
/*  30 */     61, 19, 109 };
/*     */ 
/*  32 */   public static final byte[] defEnd = { 109, 19, 61, -9, 
/*  33 */     53, 116, -25, -44, -49, 71, 47, 117, -58, 1, 
/*  34 */     83, 5, 44, -98, 5, 101, 119, -83, -26, -42, 
/*  35 */     69, -30, -14, -106, -106, 7, -100, -11, 59, -93, 
/*  36 */     -22, -113, -66, -84, 44, -120, -113, 93, -50, 35, 
/*  37 */     -106, -25, -95, -117, 42, 53, -121, -2, 24, -41, 
/*  38 */     -61, 55, 21, -81, 117, 76, -54, -126, -16, 127 };
/*     */ 
/*     */   public byte[] getStartMarker()
/*     */   {
/*  16 */     return defStart;
/*     */   }
/*     */ 
/*     */   public byte[] getEndMarker() {
/*  20 */     return defEnd;
/*     */   }
/*     */ 
/*     */   public byte[] getUnparameterisedBlock(int spaceNotIncludingMarkers)
/*     */     throws Exception
/*     */   {
/*  42 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/*  43 */     bout.write(getStartMarker());
/*  44 */     for (int i = 0; i < spaceNotIncludingMarkers; i++) {
/*  45 */       bout.write(0);
/*     */     }
/*  47 */     bout.write(getEndMarker());
/*  48 */     return bout.toByteArray();
/*     */   }
/*     */ 
/*     */   private int findIndexBefore(byte[] mark, File file) throws IOException {
/*  52 */     int index = findIndexAfter(mark, file);
/*     */ 
/*  54 */     if (index == -1) return -1;
/*     */ 
/*  56 */     return index - mark.length;
/*     */   }
/*     */   private int findIndexAfter(byte[] mark, File file) throws IOException {
/*  59 */     InputStream in = new BufferedInputStream(new FileInputStream(file));
/*     */ 
/*  61 */     int index = 0;
/*     */ 
/*  63 */     int pos = 0;
/*  64 */     int n = in.read();
/*     */ 
/*  66 */     while ((pos < mark.length) && (n != -1))
/*     */     {
/*  68 */       int c = mark[pos];
/*     */ 
/*  70 */       if (n > 128) {
/*  71 */         n -= 256;
/*     */       }
/*     */ 
/*  74 */       if (c == n) {
/*  75 */         pos++;
/*     */       }
/*     */       else
/*     */       {
/*  79 */         pos = 0;
/*     */       }
/*     */ 
/*  82 */       n = in.read();
/*  83 */       index++;
/*     */     }
/*     */ 
/*  86 */     System.out.println("n=" + n + ", pos=" + pos + ", mark=" + mark.length);
/*  87 */     if (pos == mark.length) {
/*  88 */       in.close();
/*  89 */       return index;
/*     */     }
/*  91 */     in.close();
/*  92 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean validateCLength(File file) throws IOException
/*     */   {
/*  97 */     int start = findIndexAfter(getStartMarker(), file);
/*  98 */     int end = findIndexBefore(getEndMarker(), file);
/*     */ 
/* 100 */     if (start == -1) {
/* 101 */       System.out.println("Unparameterised start not found");
/* 102 */       return false;
/*     */     }
/* 104 */     if (end == -1) {
/* 105 */       System.out.println("Unparameterised end not found");
/* 106 */       return false;
/*     */     }
/*     */ 
/* 109 */     RandomAccessFile raf = new RandomAccessFile(file, "r");
/* 110 */     raf.seek(start);
/*     */ 
/* 112 */     System.out.println("Params at " + start);
/*     */ 
/* 114 */     int len = end - start;
/*     */ 
/* 116 */     System.out.println("Param block length is " + len);
/*     */ 
/* 118 */     boolean OK = true;
/*     */ 
/* 120 */     int oldn = 32;
/*     */ 
/* 122 */     for (int i = 0; i < len; i++) {
/* 123 */       int n = raf.read();
/*     */ 
/* 131 */       if ((oldn == 0) && (n == 0)) {
/* 132 */         System.out.println("Valid params length (as seen by C code) is " + i);
/* 133 */         break;
/*     */       }
/*     */ 
/* 136 */       oldn = n;
/*     */     }
/*     */ 
/* 139 */     raf.close();
/*     */ 
/* 141 */     return OK;
/*     */   }
/*     */ 
/*     */   public boolean validateUnparameterised(File file) throws IOException {
/* 145 */     int start = findIndexAfter(getStartMarker(), file);
/* 146 */     int end = findIndexBefore(getEndMarker(), file);
/*     */ 
/* 148 */     if (start == -1) {
/* 149 */       System.out.println("[GenericParameteriser] Unparameterised start not found");
/* 150 */       return false;
/*     */     }
/* 152 */     if (end == -1) {
/* 153 */       System.out.println("[GenericParameteriser] Unparameterised end not found");
/* 154 */       return false;
/*     */     }
/*     */ 
/* 157 */     RandomAccessFile raf = new RandomAccessFile(file, "r");
/* 158 */     raf.seek(start);
/*     */ 
/* 160 */     System.out.println("[GenericParameteriser] Params at " + start);
/*     */ 
/* 162 */     int len = end - start;
/*     */ 
/* 164 */     boolean OK = true;
/*     */ 
/* 166 */     for (int i = 0; i < len; i++) {
/* 167 */       int n = raf.read();
/*     */ 
/* 169 */       if (n != 0) {
/* 170 */         System.out.println("[GenericParameteriser] " + n + " at " + i);
/* 171 */         OK = false;
/* 172 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 176 */     raf.close();
/*     */ 
/* 178 */     return OK;
/*     */   }
/*     */ 
/*     */   public byte[] getParameterisedBlock(Properties props, int padToNotIncludingMarkers) throws Exception
/*     */   {
/* 183 */     Object[] keys = props.keySet().toArray();
/* 184 */     String[] names = new String[keys.length];
/* 185 */     String[] values = new String[keys.length];
/*     */ 
/* 187 */     for (int i = 0; i < keys.length; i++) {
/* 188 */       names[i] = keys[i];
/* 189 */       values[i] = props.getProperty(names[i]);
/*     */     }
/*     */ 
/* 192 */     return getParameterisedBlock(names, values, padToNotIncludingMarkers);
/*     */   }
/*     */ 
/*     */   public byte[] getParameterisedBlock(String[] names, String[] values, int padToNotIncludingMarkers) throws Exception {
/* 196 */     if (names.length != values.length) throw new Exception("Names and values do not match (different sizes)");
/*     */ 
/* 198 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/* 199 */     bout.write(getStartMarker());
/*     */ 
/* 201 */     CFriendlyStreamUtils.writeInt(bout, names.length);
/* 202 */     for (int i = 0; i < names.length; i++) {
/* 203 */       CFriendlyStreamUtils.writeString(bout, names[i]);
/* 204 */       CFriendlyStreamUtils.writeString(bout, values[i]);
/*     */     }
/*     */ 
/* 207 */     byte[] startMarker = getStartMarker();
/* 208 */     while (bout.size() < startMarker.length + padToNotIncludingMarkers) {
/* 209 */       bout.write(0);
/*     */     }
/* 211 */     bout.write(getEndMarker());
/* 212 */     return bout.toByteArray();
/*     */   }
/*     */ 
/*     */   public void setParameters(Properties props, File file, boolean overwrite) throws IOException {
/* 216 */     Object[] keys = props.keySet().toArray();
/* 217 */     String[] names = new String[keys.length];
/* 218 */     String[] values = new String[keys.length];
/*     */ 
/* 220 */     for (int i = 0; i < keys.length; i++) {
/* 221 */       names[i] = keys[i];
/* 222 */       values[i] = props.getProperty(names[i]);
/*     */     }
/*     */ 
/* 225 */     setParameters(names, values, file, overwrite);
/*     */   }
/*     */ 
/*     */   public void setParameters(String[] names, String[] values, File file, boolean overwrite) throws IOException {
/* 229 */     if (names.length != values.length) throw new IOException("Names and values do not match (different sizes)");
/*     */ 
/* 231 */     if ((!overwrite) && 
/* 232 */       (!validateUnparameterised(file))) {
/* 233 */       throw new IOException("Untainted parameter area not found in file " + file.getName());
/*     */     }
/*     */ 
/* 237 */     int start = findIndexAfter(getStartMarker(), file);
/*     */ 
/* 239 */     RandomAccessFile raf = new RandomAccessFile(file, "rw");
/* 240 */     raf.seek(start);
/* 241 */     CFriendlyStreamUtils.writeInt(raf, names.length);
/*     */ 
/* 243 */     for (int i = 0; i < names.length; i++) {
/* 244 */       CFriendlyStreamUtils.writeString(raf, names[i]);
/* 245 */       CFriendlyStreamUtils.writeString(raf, values[i]);
/*     */     }
/*     */ 
/* 248 */     raf.close();
/*     */   }
/*     */ 
/*     */   public StreamParameteriser newStreamParametiser(Properties props) throws IOException
/*     */   {
/* 253 */     Object[] keys = props.keySet().toArray();
/* 254 */     String[] names = new String[keys.length];
/* 255 */     String[] values = new String[keys.length];
/*     */ 
/* 257 */     for (int i = 0; i < keys.length; i++) {
/* 258 */       names[i] = keys[i];
/* 259 */       values[i] = props.getProperty(names[i]);
/*     */     }
/*     */ 
/* 262 */     return newStreamParametiser(names, values);
/*     */   }
/*     */   public StreamParameteriser newStreamParametiser(String[] names, String[] values) throws IOException {
/* 265 */     if (names.length != values.length) throw new IOException("Names and values do not match (different sizes)");
/*     */ 
/* 267 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/*     */ 
/* 269 */     CFriendlyStreamUtils.writeInt(bout, names.length);
/*     */ 
/* 271 */     for (int i = 0; i < names.length; i++) {
/* 272 */       CFriendlyStreamUtils.writeString(bout, names[i]);
/* 273 */       CFriendlyStreamUtils.writeString(bout, values[i]);
/*     */     }
/*     */ 
/* 276 */     byte[] payload = bout.toByteArray();
/* 277 */     bout = null;
/*     */ 
/* 279 */     return new StreamParameteriser(getStartMarker(), payload);
/*     */   }
/*     */ 
/*     */   public Properties getParameters(File file) throws IOException
/*     */   {
/* 284 */     int start = findIndexAfter(getStartMarker(), file);
/*     */ 
/* 286 */     RandomAccessFile raf = new RandomAccessFile(file, "r");
/* 287 */     raf.seek(start);
/*     */ 
/* 289 */     Properties props = new Properties();
/*     */ 
/* 291 */     int num = CFriendlyStreamUtils.readInt(raf);
/* 292 */     for (int i = 0; i < num; i++) {
/* 293 */       String name = CFriendlyStreamUtils.readString(raf);
/* 294 */       String value = CFriendlyStreamUtils.readString(raf);
/*     */ 
/* 296 */       if ((name != null) && (value != null) && (value.length() > 200))
/* 297 */         System.out.println("[GenericParameteriser] " + name + "=" + value.substring(0, 200) + "...");
/*     */       else {
/* 299 */         System.out.println("[GenericParameteriser] " + name + "=" + value);
/*     */       }
/* 301 */       props.setProperty(name, value);
/*     */     }
/*     */ 
/* 304 */     System.out.println("[GenericParameteriser] Total valid parameters length is " + (raf.getFilePointer() - start));
/*     */ 
/* 306 */     return props;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception
/*     */   {
/* 311 */     GenericParameteriser gp = new GenericParameteriser();
/*     */ 
/* 313 */     File file = new File("params.txt");
/*     */ 
/* 315 */     FileOutputStream f = new FileOutputStream(file);
/* 316 */     f.write(gp.getUnparameterisedBlock(500));
/* 317 */     f.close();
/*     */ 
/* 319 */     String[] names = { 
/* 320 */       "nameOne", 
/* 321 */       "nameTwo", 
/* 322 */       "name\n\tThree" };
/*     */ 
/* 324 */     String[] values = { 
/* 325 */       "123456", 
/* 326 */       "!@$%^", 
/* 327 */       "bit of text" };
/*     */ 
/* 330 */     gp.setParameters(names, values, file, false);
/*     */ 
/* 332 */     System.out.println(gp.getParameters(file));
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.stream.GenericParameteriser
 * JD-Core Version:    0.6.2
 */