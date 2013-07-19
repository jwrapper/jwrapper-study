/*     */ package utils.ostools;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.OutputStream;
/*     */ import utils.stream.ProcessPrinter;
/*     */ 
/*     */ public class RunCommandGetOutput
/*     */ {
/*     */   public static final int STDOUT = 0;
/*     */   public static final int STDERR = 1;
/*     */   private static final boolean WAIT_FOR_OUTPUT = true;
/*     */ 
/*     */   public static void runCommandIgnoreOutput(String cmd)
/*     */   {
/*     */     try
/*     */     {
/*  46 */       Process p = Runtime.getRuntime().exec(cmd);
/*     */ 
/*  48 */       p.getOutputStream().close();
/*     */ 
/*  50 */       pp = new ProcessPrinter(p, null, null);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */       ProcessPrinter pp;
/*  52 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static int runCommandGetReturnCode(String[] cmd) {
/*     */     try {
/*  58 */       Process p = Runtime.getRuntime().exec(cmd);
/*     */ 
/*  60 */       p.getOutputStream().close();
/*     */ 
/*  62 */       new ProcessPrinter(p, System.out, System.err);
/*  63 */       return p.waitFor();
/*     */     } catch (Exception e) {
/*  65 */       e.printStackTrace();
/*  66 */     }return -1;
/*     */   }
/*     */ 
/*     */   public static String[] runCommandGetOutput(String cmd)
/*     */   {
/*  71 */     return runCommandGetOutput(cmd, null);
/*     */   }
/*     */   public static String[] runCommandGetOutput(String cmd, File wdir) {
/*     */     try {
/*  75 */       Process p = Runtime.getRuntime().exec(cmd, null, wdir);
/*     */ 
/*  77 */       p.getOutputStream().close();
/*     */ 
/*  79 */       ByteArrayOutputStream sout = new ByteArrayOutputStream();
/*  80 */       ByteArrayOutputStream eout = new ByteArrayOutputStream();
/*     */ 
/*  82 */       ProcessPrinter pp = new ProcessPrinter(p, sout, eout);
/*     */ 
/*  84 */       p.waitFor();
/*     */ 
/*  86 */       pp.waitForAllOutput();
/*     */ 
/*  88 */       return new String[] { new String(sout.toByteArray()), new String(eout.toByteArray()) };
/*     */     } catch (Exception e) {
/*  90 */       e.printStackTrace();
/*  91 */     }return null;
/*     */   }
/*     */ 
/*     */   public static String[] runCommandGetOutput(String[] cmd) {
/*  95 */     return runCommandGetOutput(cmd, null);
/*     */   }
/*     */   public static String[] runCommandGetOutput(String[] cmd, File wdir) {
/*     */     try {
/*  99 */       Process p = Runtime.getRuntime().exec(cmd, null, wdir);
/*     */ 
/* 101 */       p.getOutputStream().close();
/*     */ 
/* 103 */       ByteArrayOutputStream sout = new ByteArrayOutputStream();
/* 104 */       ByteArrayOutputStream eout = new ByteArrayOutputStream();
/*     */ 
/* 106 */       ProcessPrinter pp = new ProcessPrinter(p, sout, eout);
/*     */ 
/* 108 */       p.waitFor();
/*     */ 
/* 110 */       pp.waitForAllOutput();
/*     */ 
/* 112 */       return new String[] { new String(sout.toByteArray()), new String(eout.toByteArray()) };
/*     */     } catch (Exception e) {
/* 114 */       e.printStackTrace();
/* 115 */     }return null;
/*     */   }
/*     */ 
/*     */   public static String[] runCommandGetOutputThrowErrors(String cmd)
/*     */     throws Exception
/*     */   {
/* 121 */     Process p = Runtime.getRuntime().exec(cmd);
/*     */ 
/* 123 */     p.getOutputStream().close();
/*     */ 
/* 125 */     ByteArrayOutputStream sout = new ByteArrayOutputStream();
/* 126 */     ByteArrayOutputStream eout = new ByteArrayOutputStream();
/*     */ 
/* 128 */     ProcessPrinter pp = new ProcessPrinter(p, sout, eout);
/*     */ 
/* 130 */     p.waitFor();
/*     */ 
/* 132 */     pp.waitForAllOutput();
/*     */ 
/* 134 */     return new String[] { new String(sout.toByteArray()), new String(eout.toByteArray()) };
/*     */   }
/*     */ 
/*     */   public static String[] runCommandGetOutputThrowErrors(String[] cmd) throws Exception
/*     */   {
/* 139 */     Process p = Runtime.getRuntime().exec(cmd);
/*     */ 
/* 141 */     p.getOutputStream().close();
/*     */ 
/* 143 */     ByteArrayOutputStream sout = new ByteArrayOutputStream();
/* 144 */     ByteArrayOutputStream eout = new ByteArrayOutputStream();
/*     */ 
/* 146 */     ProcessPrinter pp = new ProcessPrinter(p, sout, eout);
/*     */ 
/* 148 */     p.waitFor();
/*     */ 
/* 150 */     pp.waitForAllOutput();
/*     */ 
/* 152 */     return new String[] { new String(sout.toByteArray()), new String(eout.toByteArray()) };
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.ostools.RunCommandGetOutput
 * JD-Core Version:    0.6.2
 */