/*    */ package jwrapper.logging;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ import jwrapper.updater.LogFolderCleaner;
/*    */ import utils.ostools.OS;
/*    */ import utils.progtools.CheapTimingPrintStream;
/*    */ import utils.stream.ProcessPrinter;
/*    */ 
/*    */ public class StdLogging
/*    */ {
/*    */   public static final boolean DEBUG_LOG_ALL = true;
/*    */   public static final String LOG_DIR = "logs";
/* 36 */   static boolean logging = false;
/*    */ 
/*    */   public static boolean beforeDebugLogUntil(String propvalue)
/*    */   {
/*    */     try
/*    */     {
/* 22 */       long x = Long.parseLong(propvalue);
/* 23 */       return System.currentTimeMillis() < x;
/*    */     } catch (Exception x) {
/*    */     }
/* 26 */     return true;
/*    */   }
/*    */ 
/*    */   public static void startDebugLogging(File master, String name)
/*    */   {
/* 32 */     startLogging(master, name);
/*    */   }
/*    */ 
/*    */   public static void startLogging(File master, String name)
/*    */   {
/* 38 */     if (!logging) {
/* 39 */       File logs = new File(master, "logs");
/* 40 */       logs.mkdirs();
/*    */ 
/* 42 */       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
/*    */ 
/* 44 */       File file = new File(logs, name + "-" + sdf.format(new Date()) + ".log");
/*    */ 
/* 46 */       if (!OS.isWindows())
/*    */       {
/*    */         try {
/* 49 */           file.createNewFile();
/*    */         } catch (IOException e) {
/* 51 */           e.printStackTrace();
/*    */         }
/*    */ 
/*    */         try
/*    */         {
/* 56 */           Process p = Runtime.getRuntime().exec(new String[] { "chmod", "755", file.getCanonicalPath() });
/* 57 */           new ProcessPrinter(p, System.out, System.err);
/*    */         } catch (Throwable x) {
/* 59 */           x.printStackTrace();
/*    */         }
/*    */       }
/*    */ 
/* 63 */       logging = true;
/*    */       try {
/* 65 */         PrintStream out = new CheapTimingPrintStream(new FileOutputStream(file));
/* 66 */         System.setErr(out);
/* 67 */         System.setOut(out);
/*    */ 
/* 69 */         System.out.println("STDOUT test");
/* 70 */         System.err.println("STDERR test");
/*    */       }
/*    */       catch (Throwable localThrowable1) {
/*    */       }
/* 74 */       LogFolderCleaner.clean(logs);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.logging.StdLogging
 * JD-Core Version:    0.6.2
 */