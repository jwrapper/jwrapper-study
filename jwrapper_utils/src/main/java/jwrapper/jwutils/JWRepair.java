/*    */ package jwrapper.jwutils;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import jwrapper.updater.GenericUpdater;
/*    */ import jwrapper.updater.LaunchFile;
/*    */ import jwrapper.updater.VersionUtil;
/*    */ 
/*    */ public class JWRepair
/*    */ {
/*    */   public static void incrementVersionOnBundlesForReDownload(File deployedBundleFolder, boolean jwrapper, boolean app, boolean jre)
/*    */     throws IOException
/*    */   {
/* 16 */     File[] files = deployedBundleFolder.listFiles();
/* 17 */     for (int i = 0; i < files.length; i++)
/* 18 */       if (LaunchFile.isAppArchive(files[i])) {
/* 19 */         String appName = LaunchFile.pickAppNameFromAppFolder(files[i]);
/*    */ 
/* 21 */         if (appName.equals("JWrapper")) {
/* 22 */           if (!jwrapper) {
/* 23 */             System.out.println("Skipping " + files[i].getName());
/* 24 */             continue;
/*    */           }
/* 26 */         } else if (appName.endsWith("JRE")) {
/* 27 */           if (!jre) {
/* 28 */             System.out.println("Skipping " + files[i].getName());
/* 29 */             continue;
/*    */           }
/*    */         }
/* 32 */         else if (!app) {
/* 33 */           System.out.println("Skipping " + files[i].getName());
/* 34 */           continue;
/*    */         }
/*    */ 
/* 38 */         int ver = LaunchFile.pickIntegerVersionFromAppArchive(files[i]);
/*    */ 
/* 41 */         ver++;
/*    */ 
/* 43 */         String newArchiveName = GenericUpdater.getArchiveNameFor(appName, VersionUtil.padVersion(ver));
/*    */ 
/* 45 */         System.out.println("[JWRepair] Moving " + files[i].getName() + " to " + newArchiveName);
/*    */ 
/* 48 */         files[i].renameTo(new File(deployedBundleFolder, newArchiveName));
/*    */ 
/* 51 */         VersionUtil.writeAppVersionFile(deployedBundleFolder, appName, ver);
/*    */       }
/*    */   }
/*    */ 
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 58 */     if (args.length == 0) {
/* 59 */       System.out.println("Usage: JWRepair <deployed app bundle folder> <update jwrapper (true/false)> <update app (true/false)> <update jre (true/false)>");
/*    */     } else {
/* 61 */       File f = new File(args[0]);
/* 62 */       System.out.println("[JWRepair] Incrementing version on " + f);
/* 63 */       incrementVersionOnBundlesForReDownload(f, args[1].equals("true"), args[2].equals("true"), args[3].equals("true"));
/*    */     }
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.jwutils.JWRepair
 * JD-Core Version:    0.6.2
 */