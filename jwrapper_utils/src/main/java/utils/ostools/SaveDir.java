/*     */ package utils.ostools;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import utils.string.FnvHash;
/*     */ 
/*     */ public class SaveDir
/*     */ {
/*   8 */   static Object LOCK = new Object();
/*   9 */   static String myHome = null;
/*     */ 
/*     */   private static boolean isRoaming(String path)
/*     */   {
/*  13 */     return (path.length() > 2) && (path.charAt(0) == '\\') && (path.charAt(1) == '\\');
/*     */   }
/*     */ 
/*     */   private static boolean isASCII(String s)
/*     */   {
/*  18 */     for (int i = 0; i < s.length(); i++)
/*     */     {
/*  20 */       if (s.charAt(i) > '')
/*  21 */         return false;
/*     */     }
/*  23 */     return true;
/*     */   }
/*     */ 
/*     */   private static void getMyHome()
/*     */   {
/*  28 */     synchronized (LOCK)
/*     */     {
/*  30 */       if (myHome == null)
/*     */       {
/*  32 */         if (OS.isWindows())
/*     */         {
/*  34 */           String dir = SlowWEnv.getEnv("APPDATA");
/*     */ 
/*  36 */           if (isRoaming(dir))
/*     */           {
/*  39 */             if (OS.isWindowsVistaOrAbove())
/*     */             {
/*  41 */               dir = SlowWEnv.getEnv("LOCALAPPDATA");
/*  42 */               System.out.println("[SaveDir] Local AppData directory resolved to " + dir);
/*     */             }
/*     */             else
/*     */             {
/*  46 */               File fileDir = new File(System.getProperty("user.home"));
/*  47 */               fileDir = new File(fileDir, "Local Settings");
/*  48 */               fileDir = new File(fileDir, "Application Data");
/*  49 */               dir = fileDir.getAbsolutePath();
/*     */ 
/*  51 */               System.out.println("[SaveDir] Local AppData directory resolved to " + dir);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*  56 */           File test = new File(dir);
/*  57 */           if ((!test.exists()) || (!isASCII(dir)))
/*     */           {
/*  60 */             String username = System.getProperty("user.name");
/*  61 */             byte[] normalised = new byte[username.length()];
/*  62 */             for (int i = 0; i < username.length(); i++)
/*     */             {
/*  64 */               char c = username.charAt(i);
/*  65 */               if ((c >= 0) && (c <= ''))
/*  66 */                 normalised[i] = ((byte)(0xFF & c));
/*     */               else {
/*  68 */                 normalised[i] = 95;
/*     */               }
/*     */             }
/*     */             try
/*     */             {
/*  73 */               String normalisedUsername = new String(normalised, "ASCII");
/*  74 */               int hash = FnvHash.hash(username.getBytes());
/*  75 */               hash = Math.abs(hash);
/*     */               File parent;
/*     */               File parent;
/*  78 */               if (OS.isWindowsVistaOrAbove())
/*  79 */                 parent = new File(SlowWEnv.getEnv("PROGRAMDATA"));
/*     */               else {
/*  81 */                 parent = new File(SlowWEnv.getEnv("ALLUSERSPROFILE"));
/*     */               }
/*  83 */               test = new File(parent, "JWUser-" + hash + "-" + normalisedUsername);
/*  84 */               dir = test.getCanonicalPath();
/*     */             }
/*     */             catch (Throwable t)
/*     */             {
/*  88 */               t.printStackTrace();
/*     */             }
/*     */           }
/*  91 */           if (!dir.endsWith(File.separator)) dir = dir + File.separator;
/*  92 */           myHome = dir;
/*  93 */         } else if (OS.isMacOS()) {
/*  94 */           String dir = System.getProperty("user.home");
/*  95 */           if (!dir.endsWith(File.separator)) dir = dir + File.separator;
/*  96 */           dir = dir + "Library/Application Support/";
/*  97 */           myHome = dir;
/*  98 */         } else if (OS.isLinux()) {
/*  99 */           String dir = System.getProperty("user.home");
/* 100 */           if (!dir.endsWith(File.separator)) dir = dir + File.separator;
/* 101 */           myHome = dir;
/*     */         } else {
/* 103 */           String dir = System.getProperty("user.home");
/* 104 */           if (!dir.endsWith(File.separator)) dir = dir + File.separator;
/* 105 */           myHome = dir;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getUserSpecificSaveDirWithSlash(String folderName)
/*     */   {
/* 115 */     String dir = getUserSpecificSaveDirNameWithSlash(folderName);
/* 116 */     new File(dir).mkdirs();
/* 117 */     return dir;
/*     */   }
/*     */ 
/*     */   private static String getUserSpecificSaveDirNameWithSlash(String folderName) {
/* 121 */     getMyHome();
/* 122 */     if (OS.isWindows())
/* 123 */       return myHome + folderName + File.separator;
/* 124 */     if (OS.isMacOS())
/* 125 */       return myHome + folderName + File.separator;
/* 126 */     if (OS.isLinux())
/*     */     {
/* 128 */       return myHome + ".JWrapper/" + folderName + File.separator;
/*     */     }
/* 130 */     return myHome + "." + folderName + File.separator;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 136 */     getMyHome();
/* 137 */     System.out.println(myHome);
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.ostools.SaveDir
 * JD-Core Version:    0.6.2
 */