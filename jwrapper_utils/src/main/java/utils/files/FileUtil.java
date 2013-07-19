/*     */ package utils.files;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import utils.stream.StreamPiper;
/*     */ import utils.stream.StreamUtils;
/*     */ 
/*     */ public class FileUtil
/*     */ {
/*     */   public static boolean same(File f1, File f2)
/*     */     throws IOException
/*     */   {
/*  19 */     return f1.getCanonicalPath().equals(f2.getCanonicalPath());
/*     */   }
/*     */ 
/*     */   public static InputStream ropen(File f) throws IOException {
/*  23 */     return new BufferedInputStream(new FileInputStream(f));
/*     */   }
/*     */ 
/*     */   public static OutputStream wopen(File f) throws IOException {
/*  27 */     return new BufferedOutputStream(new FileOutputStream(f));
/*     */   }
/*     */ 
/*     */   public static String appendToNamePreservingExtension(String name, String app) {
/*  31 */     int n = name.lastIndexOf('.');
/*  32 */     if (n == -1) {
/*  33 */       return name + app;
/*     */     }
/*  35 */     return name.substring(0, n) + app + name.substring(n);
/*     */   }
/*     */ 
/*     */   public static File getSubFile(File dest, String name)
/*     */   {
/*  40 */     String path = dest.getAbsolutePath();
/*  41 */     if (!path.endsWith(File.separator))
/*  42 */       path = path + File.separator + name;
/*     */     else {
/*  44 */       path = path + name;
/*     */     }
/*  46 */     return new File(path);
/*     */   }
/*     */ 
/*     */   public static String stripExtension(String name)
/*     */   {
/*  51 */     int n = name.lastIndexOf('.');
/*  52 */     if (n == -1) {
/*  53 */       return name;
/*     */     }
/*  55 */     return name.substring(0, n);
/*     */   }
/*     */ 
/*     */   public static String replaceExtension(String name, String extension)
/*     */   {
/*  61 */     return stripExtension(name) + "." + extension;
/*     */   }
/*     */ 
/*     */   public static File replaceExtension(File file, String extension)
/*     */   {
/*  66 */     String name = file.getName();
/*  67 */     String newName = stripExtension(name) + "." + extension;
/*  68 */     return new File(file.getParentFile(), newName);
/*     */   }
/*     */ 
/*     */   public static void appendToFile(String path, String append) throws IOException {
/*  72 */     FileOutputStream fout = new FileOutputStream(path, true);
/*  73 */     fout.write(append.getBytes("UTF8"));
/*  74 */     fout.flush();
/*  75 */     fout.close();
/*     */   }
/*     */ 
/*     */   public static void writeFileAsString(String path, String dat) throws IOException {
/*  79 */     writeFile(path, dat.getBytes("ISO-8859-1"));
/*     */   }
/*     */   public static void writeFileAsStringUTF8(String path, String dat) throws IOException {
/*  82 */     writeFile(path, dat.getBytes("UTF8"));
/*     */   }
/*     */   public static void writeFile(String path, byte[] dat) throws IOException {
/*  85 */     File f = new File(path);
/*  86 */     writeFile(f, dat);
/*     */   }
/*     */   public static void writeFile(File f, byte[] dat) throws IOException {
/*  89 */     f.mkdirs();
/*  90 */     f.delete();
/*  91 */     FileOutputStream fout = new FileOutputStream(f);
/*     */     try {
/*  93 */       fout.write(dat);
/*  94 */       fout.flush();
/*     */     } catch (IOException x) {
/*  96 */       robustClose(fout);
/*  97 */       throw x;
/*     */     }
/*  99 */     fout.close();
/*     */   }
/*     */   public static byte[] readFile(String path) throws IOException {
/* 102 */     FileInputStream fin = new FileInputStream(path);
/* 103 */     BufferedInputStream bin = new BufferedInputStream(fin);
/*     */     try {
/* 105 */       byte[] s = StreamUtils.readAll(bin);
/* 106 */       robustClose(bin);
/* 107 */       return s;
/*     */     } catch (IOException e) {
/* 109 */       robustClose(bin);
/* 110 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/* 114 */   public static String readFileAsString(String path) throws IOException { FileInputStream fin = new FileInputStream(path);
/* 115 */     BufferedInputStream bin = new BufferedInputStream(fin);
/*     */     try {
/* 117 */       String s = StreamUtils.readAllAsString(bin);
/* 118 */       robustClose(bin);
/* 119 */       return s;
/*     */     } catch (IOException e) {
/* 121 */       robustClose(bin);
/* 122 */       throw e;
/*     */     } }
/*     */ 
/*     */   public static String readFileAsStringUTF8(String path) throws IOException {
/* 126 */     FileInputStream fin = new FileInputStream(path);
/* 127 */     BufferedInputStream bin = new BufferedInputStream(fin);
/*     */     try {
/* 129 */       String s = StreamUtils.readAllAsStringUTF8(bin);
/* 130 */       robustClose(bin);
/* 131 */       return s;
/*     */     } catch (IOException e) {
/* 133 */       robustClose(bin);
/* 134 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void copyFileOrDir(File src, File dest) throws IOException {
/* 139 */     if (!src.isDirectory())
/*     */     {
/* 141 */       copy(src, dest);
/*     */     }
/*     */     else {
/* 144 */       dest.delete();
/* 145 */       dest.mkdirs();
/*     */ 
/* 147 */       File[] files = src.listFiles();
/* 148 */       for (int i = 0; i < files.length; i++) {
/* 149 */         File ndest = new File(dest.getAbsolutePath() + File.separator + files[i].getName());
/*     */ 
/* 151 */         copyFileOrDir(files[i], ndest);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void copy(File src, File dest) throws IOException {
/* 157 */     FileInputStream fin = null;
/* 158 */     FileOutputStream fout = null;
/*     */     try
/*     */     {
/* 161 */       fin = new FileInputStream(src);
/* 162 */       fout = new FileOutputStream(dest);
/*     */ 
/* 164 */       BufferedInputStream bin = new BufferedInputStream(fin);
/* 165 */       BufferedOutputStream bout = new BufferedOutputStream(fout);
/*     */ 
/* 167 */       StreamPiper.pipe(bin, bout, 300000, true, false, false);
/*     */ 
/* 169 */       bout.flush();
/* 170 */       bin.close();
/* 171 */       bout.close();
/*     */ 
/* 173 */       dest.setLastModified(src.lastModified());
/*     */     }
/*     */     catch (IOException e) {
/* 176 */       robustClose(fin);
/* 177 */       robustClose(fout);
/*     */ 
/* 179 */       if (((e instanceof FileNotFoundException)) && 
/* 180 */         (e.getMessage().toLowerCase().indexOf("symbolic links") != -1)) return;
/*     */ 
/* 182 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void robustAppend(String path, String append) {
/*     */     try {
/* 188 */       appendToFile(path, append); } catch (Throwable localThrowable) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void robustClose(InputStream in) {
/*     */     try {
/* 194 */       if (in != null) in.close();  } catch (Throwable localThrowable) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void robustClose(OutputStream in) {
/*     */     try { if (in != null) in.close();  } catch (Throwable localThrowable) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void robustDelete(String s) {
/*     */     try {
/* 205 */       if (s != null) new File(s).delete();  } catch (Throwable localThrowable) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void robustDelete(File f) {
/*     */     try { if (f != null) f.delete();  } catch (Throwable localThrowable) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void deleteDir(File f) {
/* 215 */     if (!f.exists()) {
/* 216 */       return;
/*     */     }
/* 218 */     if (f.isDirectory()) {
/* 219 */       File[] files = f.listFiles();
/* 220 */       for (int i = 0; i < files.length; i++) {
/* 221 */         deleteDir(files[i]);
/*     */       }
/* 223 */       if (!f.delete()) {
/* 224 */         System.err.println("FAILED TO DELETE DIRECTORY" + f);
/*     */       }
/*     */     }
/* 227 */     else if (!f.delete()) {
/* 228 */       System.err.println("FAILED TO DELETE FILE " + f);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void removeCVS(File f) {
/* 233 */     if (f.isDirectory())
/*     */     {
/* 235 */       if (f.getName().equals("CVS")) {
/* 236 */         deleteDir(f);
/*     */       } else {
/* 238 */         File[] files = f.listFiles();
/* 239 */         for (int i = 0; i < files.length; i++)
/* 240 */           removeCVS(files[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String listDir(File dir)
/*     */   {
/* 248 */     StringBuffer sb = new StringBuffer();
/* 249 */     File[] files = dir.listFiles();
/* 250 */     for (int i = 0; i < files.length; i++) {
/* 251 */       sb.append(files[i].getName()).append(" - ").append(files[i].length()).append('\n');
/*     */     }
/* 253 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static String normaliseAsFilename(String potentialFilename)
/*     */   {
/* 258 */     String s = potentialFilename;
/* 259 */     int len = s.length();
/* 260 */     StringBuilder sb = new StringBuilder(len);
/* 261 */     for (int i = 0; i < len; i++)
/*     */     {
/* 263 */       char ch = s.charAt(i);
/* 264 */       if (Character.isLetterOrDigit(ch))
/* 265 */         sb.append(ch);
/*     */       else
/* 267 */         sb.append(Integer.toHexString(ch));
/*     */     }
/* 269 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.files.FileUtil
 * JD-Core Version:    0.6.2
 */