/*     */ package jwrapper.updater;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import jwrapper.jwutils.JWGenericOS;
/*     */ import utils.files.AtomicSafeDeleter;
/*     */ import utils.files.FileUtil;
/*     */ import utils.progtools.MapOfLists;
/*     */ import utils.progtools.arrays.ArrayUtils;
/*     */ 
/*     */ public class MasterFolderCleaner
/*     */ {
/*  42 */   private static long ONE_HOUR = 3600000L;
/*     */ 
/*     */   public static void markUsed(File appDir) {
/*  45 */     long lastUsed = System.currentTimeMillis();
/*     */ 
/*  47 */     System.out.println("[MasterFolderCleaner] Marking " + appDir.getName() + " as recently used in a launch (" + lastUsed + ")");
/*     */ 
/*  49 */     File used = new File(appDir, "jwLastRun");
/*  50 */     used.setLastModified(lastUsed);
/*     */     try {
/*  52 */       FileUtil.writeFileAsString(used.getPath(), lastUsed);
/*  53 */       JWGenericOS.setWritableForAllUsers(used, false);
/*     */     } catch (IOException x) {
/*  55 */       System.out.println("[MasterFolderCleaner] Unable to mark " + appDir.getName() + " as recently used in a launch (" + x + ")");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static long getLastUsed(File appDir) {
/*  60 */     File used = new File(appDir, "jwLastRun");
/*  61 */     if (!used.exists())
/*  62 */       return -1L;
/*     */     try
/*     */     {
/*  65 */       return Long.parseLong(FileUtil.readFileAsString(used.getPath()));
/*     */     } catch (Exception x) {
/*  67 */       System.out.println("[MasterFolderCleaner] Unable to check " + appDir.getName() + " last launch time");
/*  68 */     }return -1L;
/*     */   }
/*     */ 
/*     */   public static void clean(File master, int max_apps, int max_gus)
/*     */   {
/*  75 */     File[] files = master.listFiles();
/*     */ 
/*  77 */     System.out.println("[MasterFolderCleaner] Cleaning master folder " + master);
/*     */ 
/*  79 */     System.out.println("[MasterFolderCleaner] max Apps: " + max_apps);
/*  80 */     System.out.println("[MasterFolderCleaner] max GUs: " + max_gus);
/*     */ 
/*  82 */     HashMap usedJREs = new HashMap();
/*     */ 
/*  84 */     MapOfLists apps = new MapOfLists(true);
/*     */ 
/*  89 */     for (int i = 0; i < files.length; i++) {
/*  90 */       String name = files[i].getName();
/*  91 */       if (name.startsWith(GenericUpdater.TEMP_FOLDER_PREFIX)) {
/*  92 */         System.out.println("[MasterFolderCleaner] --- Temp Dir: " + name);
/*     */ 
/*  94 */         String suffix = name.substring(GenericUpdater.TEMP_FOLDER_PREFIX.length());
/*  95 */         suffix = suffix.substring(0, suffix.indexOf('-'));
/*     */ 
/*  97 */         long tempCreated = Long.parseLong(suffix);
/*     */ 
/*  99 */         long hours = (System.currentTimeMillis() - tempCreated) / ONE_HOUR;
/*     */ 
/* 101 */         System.out.println("[MasterFolderCleaner] Temp folder " + name + " is " + hours + " hours old");
/*     */ 
/* 103 */         if (hours >= 18L)
/*     */         {
/* 105 */           System.out.println("[MasterFolderCleaner] Will try to delete temp folder if not in use");
/* 106 */           if (AtomicSafeDeleter.deleteIfNotInUse(files[i]))
/* 107 */             System.out.println("[MasterFolderCleaner] Deleted " + name + " OK");
/*     */           else {
/* 109 */             System.out.println("[MasterFolderCleaner] Temp folder appears to be still in use");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 115 */     files = master.listFiles();
/*     */ 
/* 120 */     for (int i = 0; i < files.length; i++) {
/* 121 */       String name = files[i].getName();
/* 122 */       if (LaunchFile.isAppFolder(files[i])) {
/* 123 */         System.out.println("[MasterFolderCleaner] --- App Dir: " + name);
/*     */ 
/* 125 */         String appName = LaunchFile.pickAppNameFromAppFolder(files[i]);
/* 126 */         String appVer = LaunchFile.pickVersionFromAppFolder(files[i]);
/*     */ 
/* 137 */         long lastUsed = getLastUsed(files[i]);
/*     */ 
/* 139 */         if (lastUsed > 0L) {
/* 140 */           System.out.println("[MasterFolderCleaner] " + files[i].getName() + " last used at " + lastUsed);
/* 141 */           apps.add(appName, files[i]);
/*     */         } else {
/* 143 */           System.out.println("[MasterFolderCleaner] " + files[i].getName() + " has never been used, probably new (skipping it entirely)");
/* 144 */           usedJREs.put(appVer, appVer);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 149 */     System.out.println("[MasterFolderCleanup] Checking all versions of all Apps (including GU, excluding JREs)");
/*     */ 
/* 151 */     ArrayList all = ArrayUtils.toList(apps.keySet().toArray());
/* 152 */     for (int i = 0; i < all.size(); i++)
/*     */     {
/* 154 */       String appName = (String)all.get(i);
/*     */ 
/* 156 */       List app = apps.get(appName);
/*     */       int MAX;
/* 160 */       if (appName.equals("JWrapper")) {
/* 161 */         int MAX = max_gus;
/* 162 */         System.out.println("[MasterFolderCleanup] Will retain at most " + MAX + "/" + app.size() + " " + appName + " (GU versions)"); } else {
/* 163 */         if (appName.endsWith("JRE"))
/*     */         {
/*     */           continue;
/*     */         }
/* 167 */         MAX = max_apps;
/* 168 */         System.out.println("[MasterFolderCleanup] Will retain at most " + MAX + "/" + app.size() + " " + appName + " (App versions)");
/*     */       }
/*     */ 
/* 171 */       Collections.sort(app, new LastUsedComparator());
/*     */ 
/* 185 */       for (int k = 0; k < app.size(); k++) {
/* 186 */         File f = (File)app.get(k);
/*     */ 
/* 188 */         if (k > MAX) {
/* 189 */           System.out.println("[MasterFolderCleanup] Deleting " + f.getName() + " (last used " + getLastUsed(f) + ")");
/*     */ 
/* 191 */           if (AtomicSafeDeleter.deleteIfNotInUse(f))
/* 192 */             System.out.println("[MasterFolderCleanup] Deleted " + f);
/*     */           else
/* 194 */             System.out.println("[MasterFolderCleanup] Unable to delete " + f + " (in use?)");
/*     */         }
/*     */         else
/*     */         {
/* 198 */           String jreVer = LaunchFile.getJreVersionForApp(f);
/*     */ 
/* 200 */           System.out.println("[MasterFolderCleanup] Retaining " + f.getName() + " (last used " + getLastUsed(f) + ") (jre used " + jreVer + ")");
/*     */ 
/* 202 */           if (!usedJREs.containsKey(jreVer)) {
/* 203 */             System.out.println("[MasterFolderCleaner] JRE " + f.getName() + " is used by a retained app version (" + f.getName() + "), excluding it from deletion");
/* 204 */             usedJREs.put(jreVer, jreVer);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 211 */       System.out.println("[MasterFolderCleanup] " + appName + " cleanup done");
/*     */     }
/*     */ 
/* 214 */     System.out.println("[MasterFolderCleanup] Checking all versions of all JREs");
/*     */ 
/* 216 */     for (int i = 0; i < all.size(); i++)
/*     */     {
/* 218 */       String appName = (String)all.get(i);
/*     */ 
/* 220 */       if (appName.endsWith("JRE")) {
/* 221 */         List app = apps.get(appName);
/*     */ 
/* 223 */         System.out.println("[MasterFolderCleanup] Will retain all JREs (" + app.size() + ") used by retained Apps");
/*     */ 
/* 225 */         for (int k = 0; k < app.size(); k++) {
/* 226 */           File f = (File)app.get(k);
/*     */ 
/* 229 */           String appVer = LaunchFile.pickVersionFromAppFolder(f);
/*     */ 
/* 231 */           if (usedJREs.containsKey(appVer)) {
/* 232 */             System.out.println("[MasterFolderCleanup] Retaining JRE " + f.getName());
/*     */           } else {
/* 234 */             System.out.println("[MasterFolderCleanup] Deleting JRE " + f.getName());
/*     */ 
/* 236 */             if (AtomicSafeDeleter.deleteIfNotInUse(f))
/* 237 */               System.out.println("[MasterFolderCleanup] Deleted " + f);
/*     */             else {
/* 239 */               System.out.println("[MasterFolderCleanup] Unable to delete " + f + " (in use?)");
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 244 */         System.out.println("[MasterFolderCleanup] JRE cleanup done");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 253 */     clean(new File("/users/aem/Desktop/Launch NPE Problem/JWrapper-Remote Support 2"), 2, 1);
/*     */   }
/*     */ 
/*     */   static class LastUsedComparator
/*     */     implements Comparator
/*     */   {
/*     */     public int compare(Object o1, Object o2)
/*     */     {
/*  22 */       File f1 = (File)o1;
/*  23 */       File f2 = (File)o2;
/*     */       try
/*     */       {
/*  26 */         long diff = MasterFolderCleaner.getLastUsed(f2) - MasterFolderCleaner.getLastUsed(f1);
/*  27 */         if (diff > 0L)
/*  28 */           return 1;
/*  29 */         if (diff < 0L) {
/*  30 */           return -1;
/*     */         }
/*  32 */         return 0;
/*     */       }
/*     */       catch (Exception x)
/*     */       {
/*  36 */         x.printStackTrace();
/*  37 */       }return f2.getName().compareTo(f1.getName());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.updater.MasterFolderCleaner
 * JD-Core Version:    0.6.2
 */