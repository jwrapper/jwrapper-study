/*    */ package jwrapper.updater;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.PrintStream;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Arrays;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class LogFolderCleaner
/*    */ {
/*  9 */   static int MAX_COUNT = 6;
/* 10 */   static long MAX_AGE = 604800000L;
/*    */ 
/*    */   public static void clean(File logFolder) {
/* 13 */     String[] logs = logFolder.list();
/* 14 */     if (logs == null) return;
/* 15 */     Arrays.sort(logs);
/*    */ 
/* 17 */     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
/*    */ 
/* 19 */     String prev = "";
/* 20 */     int run = 0;
/*    */ 
/* 22 */     for (int i = logs.length - 1; i >= 0; i--) {
/* 23 */       String name = logs[i];
/*    */       try
/*    */       {
/* 26 */         int first = name.indexOf("-2");
/* 27 */         int last = name.lastIndexOf('.');
/*    */ 
/* 29 */         String date = name.substring(first + 1, last);
/* 30 */         name = name.substring(0, first);
/*    */ 
/* 32 */         if (name.equals(prev))
/* 33 */           run++;
/*    */         else {
/* 35 */           run = 0;
/*    */         }
/*    */ 
/* 38 */         if (run >= MAX_COUNT) {
/* 39 */           System.out.println("[LogFolderCleaner] Deleting " + logs[i]);
/* 40 */           new File(logFolder, logs[i]).delete();
/*    */         }
/*    */         else
/*    */         {
/*    */           try
/*    */           {
/* 47 */             long time = sdf.parse(date).getTime();
/*    */ 
/* 49 */             long now = System.currentTimeMillis();
/*    */ 
/* 51 */             if (now - time > MAX_AGE) {
/* 52 */               System.out.println("[LogFolderCleaner] Deleting " + logs[i]);
/* 53 */               new File(logFolder, logs[i]).delete();
/*    */             } else {
/* 55 */               System.out.println("[LogFolderCleaner] Keeping " + logs[i]);
/*    */             }
/*    */           } catch (Exception localException1) {
/*    */           }
/*    */         }
/* 60 */         prev = name;
/*    */       }
/*    */       catch (Exception x)
/*    */       {
/* 64 */         System.out.println("[LogFolderCleaner] ignoring " + name);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 70 */     clean(new File("/Users/aem/Library/Application Support/JWrapper-SimpleHelp Technician/logs"));
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.updater.LogFolderCleaner
 * JD-Core Version:    0.6.2
 */