/*    */ package jwrapper.updater;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class VersionUtil
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 12 */     long ver = Long.parseLong("00018512673");
/* 13 */     long tim = getTimeForVersion(ver);
/* 14 */     System.out.println(ver + " = " + new Date(tim));
/*    */   }
/*    */ 
/*    */   public static long getTimeForVersion(long ver) {
/* 18 */     ver = 4000L * (322000000L + ver);
/* 19 */     return ver;
/*    */   }
/*    */ 
/*    */   public static int getVersionForNow()
/*    */   {
/* 30 */     int ver = (int)(System.currentTimeMillis() / 4000L - 322000000L);
/* 31 */     return ver;
/*    */   }
/*    */ 
/*    */   public static String padVersion(int version)
/*    */   {
/* 39 */     String ver = version;
/* 40 */     while (ver.length() < 11) {
/* 41 */       ver = "0" + ver;
/*    */     }
/* 43 */     return ver;
/*    */   }
/*    */ 
/*    */   public static void writeAppVersionFile(File dir, String app, int version) throws IOException {
/* 47 */     writeAppVersionFile(dir, app, padVersion(version));
/*    */   }
/*    */   public static void writeAppVersionFile(File dir, String app, String version) throws IOException {
/* 50 */     File ver = new File(dir, GenericUpdater.getVersionFileNameFor(app));
/* 51 */     FileOutputStream fout = new FileOutputStream(ver);
/* 52 */     fout.write(version.getBytes("ASCII"));
/* 53 */     fout.close();
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.updater.VersionUtil
 * JD-Core Version:    0.6.2
 */