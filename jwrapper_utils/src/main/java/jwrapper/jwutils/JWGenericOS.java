/*     */ package jwrapper.jwutils;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import jwrapper.hidden.JWNativeAPI;
/*     */ import utils.ostools.OS;
/*     */ import utils.stream.ProcessPrinter;
/*     */ 
/*     */ public abstract class JWGenericOS
/*     */ {
/*  15 */   protected static JWWindowsOS winInstance = new JWWindowsOS();
/*  16 */   protected static JWLinuxOS linInstance = new JWLinuxOS();
/*  17 */   protected static JWMacOS macInstance = new JWMacOS();
/*     */ 
/*     */   public static JWGenericOS getInstance()
/*     */   {
/*  21 */     if (OS.isWindows())
/*  22 */       return winInstance;
/*  23 */     if (OS.isMacOS())
/*  24 */       return macInstance;
/*  25 */     return linInstance;
/*     */   }
/*     */ 
/*     */   public String getOSName()
/*     */   {
/*  33 */     return System.getProperty("os.name");
/*     */   }
/*     */ 
/*     */   public String getOSNameWithBitness()
/*     */   {
/*  41 */     return getOSName() + " " + System.getProperty("os.arch");
/*     */   }
/*     */ 
/*     */   public static void setCurrentDirectory(File dir)
/*     */     throws IOException
/*     */   {
/*  50 */     String directoryPath = dir.getCanonicalPath();
/*  51 */     JWNativeAPI.getInstance().setCurrentDirectory(directoryPath);
/*  52 */     System.setProperty("user.dir", directoryPath);
/*     */   }
/*     */ 
/*     */   public static void setWritableForAllUsersAndWait(File dir, boolean recursive)
/*     */     throws IOException
/*     */   {
/*  61 */     Process p = setWritableForAllUsers(dir, recursive);
/*  62 */     if (p != null)
/*     */       try {
/*  64 */         int retCode = p.waitFor();
/*  65 */         System.out.println("[AutoChmod] Completed " + dir + " chmod (" + retCode + ")");
/*     */       } catch (InterruptedException x) {
/*  67 */         System.out.println("[AutoChmod] Interrupted " + dir + " chmod");
/*     */       }
/*     */   }
/*     */ 
/*     */   public static Process setWritableForAllUsers(File dir, boolean recursive) throws IOException {
/*  72 */     if ((OS.isLinux()) || (OS.isMacOS()))
/*     */     {
/*     */       Process p;
/*     */       Process p;
/*  74 */       if (recursive)
/*  75 */         p = Runtime.getRuntime().exec(new String[] { "chmod", "-R", "777", dir.getCanonicalPath() });
/*     */       else {
/*  77 */         p = Runtime.getRuntime().exec(new String[] { "chmod", "777", dir.getCanonicalPath() });
/*     */       }
/*  79 */       new ProcessPrinter(p, System.out, System.err);
/*  80 */       return p;
/*     */     }
/*  82 */     return null;
/*     */   }
/*     */ 
/*     */   public static void setReadableForAllUsersAndWait(File dir, boolean recursive)
/*     */     throws IOException
/*     */   {
/*  90 */     Process p = setReadableForAllUsers(dir, recursive);
/*  91 */     if (p != null)
/*     */       try {
/*  93 */         int retCode = p.waitFor();
/*  94 */         System.out.println("[AutoChmod] Completed " + dir + " chmod (" + retCode + ")");
/*     */       } catch (InterruptedException x) {
/*  96 */         System.out.println("[AutoChmod] Interrupted " + dir + " chmod");
/*     */       }
/*     */   }
/*     */ 
/*     */   public static Process setReadableForAllUsers(File dir, boolean recursive) throws IOException {
/* 101 */     if ((OS.isLinux()) || (OS.isMacOS()))
/*     */     {
/*     */       Process p;
/*     */       Process p;
/* 103 */       if (recursive)
/* 104 */         p = Runtime.getRuntime().exec(new String[] { "chmod", "-R", "755", dir.getCanonicalPath() });
/*     */       else {
/* 106 */         p = Runtime.getRuntime().exec(new String[] { "chmod", "755", dir.getCanonicalPath() });
/*     */       }
/* 108 */       new ProcessPrinter(p, System.out, System.err);
/* 109 */       return p;
/*     */     }
/* 111 */     return null;
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.jwutils.JWGenericOS
 * JD-Core Version:    0.6.2
 */