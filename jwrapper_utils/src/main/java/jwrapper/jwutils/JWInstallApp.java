/*     */ package jwrapper.jwutils;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Properties;
/*     */ import jwrapper.HeadlessLanguageChooserUtil;
/*     */ import jwrapper.updater.GenericUpdater;
/*     */ import jwrapper.updater.JWApp;
/*     */ import jwrapper.updater.JWLaunchProperties;
/*     */ import utils.files.FileUtil;
/*     */ import utils.ostools.OS;
/*     */ 
/*     */ public class JWInstallApp
/*     */ {
/*     */   static String lang;
/*     */ 
/*     */   public static void exitJvm_ContinueAndSkipStandardSetup()
/*     */   {
/*  23 */     System.exit(42);
/*     */   }
/*     */ 
/*     */   public static void exitJvm_ContinueAndPerformStandardSetup()
/*     */   {
/*  30 */     System.exit(41);
/*     */   }
/*     */ 
/*     */   public static void exitJvm_QuitInstallAndRollBack()
/*     */   {
/*  37 */     System.exit(43);
/*     */   }
/*     */ 
/*     */   private static File getChosenLanguageFile() {
/*  41 */     File appdir = JWSystem.getAppFolder();
/*  42 */     File master = appdir.getParentFile();
/*  43 */     File jwdir = JWApp.getJWAppsFolder(master);
/*  44 */     File chosen = new File(jwdir, "ChosenLanguage");
/*  45 */     return chosen;
/*     */   }
/*     */ 
/*     */   public static String getChosenLanguage()
/*     */     throws IOException
/*     */   {
/*  54 */     if (lang != null) {
/*  55 */       return lang;
/*     */     }
/*  57 */     File file = getChosenLanguageFile();
/*  58 */     if (!file.exists()) return null;
/*     */ 
/*  60 */     lang = FileUtil.readFileAsString(file.getCanonicalPath());
/*  61 */     return lang;
/*     */   }
/*     */ 
/*     */   public static void setChosenLanguage(String code)
/*     */     throws IOException
/*     */   {
/*  68 */     File file = getChosenLanguageFile();
/*  69 */     FileUtil.writeFileAsString(file.getCanonicalPath(), code);
/*  70 */     lang = code;
/*     */ 
/*  72 */     JWGenericOS.setWritableForAllUsers(file, false);
/*     */   }
/*     */ 
/*     */   public static String showLanguageChooser()
/*     */   {
/*  81 */     return HeadlessLanguageChooserUtil.chooseLanguage(JWSystem.getSupportedLanguages());
/*     */   }
/*     */ 
/*     */   public static void addUninstallerShortcut(String shortcutName)
/*     */     throws IOException
/*     */   {
/*  91 */     addUninstallerShortcutInFolder(shortcutName, null);
/*     */   }
/*     */ 
/*     */   public static void addUninstallerShortcutInFolder(String shortcutName, File targetFolder)
/*     */     throws IOException
/*     */   {
/* 102 */     String app = JWSystem.getAppBundleName();
/*     */ 
/* 104 */     File appdir = JWSystem.getAppFolder();
/* 105 */     File master = appdir.getParentFile();
/*     */ 
/* 107 */     File uninstallerICO = new File(JWApp.getJWAppsFolder(master), GenericUpdater.getUninstallerIcoFileNameFor(app));
/*     */ 
/* 109 */     addAppShortcutInFolder(shortcutName, GenericUpdater.VAPP_UNINSTALLER, uninstallerICO, 0, targetFolder);
/*     */   }
/*     */ 
/*     */   public static void addUninstallerShortcut(String shortcutName, File iconFile, int iconIndex)
/*     */     throws IOException
/*     */   {
/* 118 */     addAppShortcut(shortcutName, GenericUpdater.VAPP_UNINSTALLER, iconFile, iconIndex);
/*     */   }
/*     */ 
/*     */   public static void addUninstallerShortcutInFolder(String shortcutName, File iconFile, int iconIndex, File targetFolder)
/*     */     throws IOException
/*     */   {
/* 128 */     addAppShortcutInFolder(shortcutName, GenericUpdater.VAPP_UNINSTALLER, iconFile, iconIndex, targetFolder);
/*     */   }
/*     */ 
/*     */   public static void addWebShortcut(String url, String shortcutName)
/*     */     throws IOException
/*     */   {
/* 138 */     JWApp jwapp = JWApp.getJWApp(JWSystem.getAppFolder(), url);
/* 139 */     addWebShortcut(shortcutName, jwapp.getFilesystemName(), null, 0);
/*     */   }
/*     */ 
/*     */   public static void addWebShortcut(String url, String shortcutName, File iconFile, int iconIndex)
/*     */     throws IOException
/*     */   {
/* 151 */     addShortcut(shortcutName, url, iconFile, iconIndex, true, null, null);
/*     */   }
/*     */ 
/*     */   public static void addAppShortcut(String shortcutName, String virtualAppName)
/*     */     throws IOException
/*     */   {
/* 161 */     JWApp jwapp = JWApp.getJWApp(JWSystem.getAppFolder(), virtualAppName);
/* 162 */     addAppShortcut(shortcutName, jwapp.getFilesystemName(), null, 0);
/*     */   }
/*     */ 
/*     */   public static void removeAppShortcut(String shortcutName) throws IOException {
/* 166 */     removeShortcut(shortcutName, false);
/*     */   }
/*     */ 
/*     */   public static void removeAppShortcut(String shortcutName, boolean web) throws IOException {
/* 170 */     removeShortcut(shortcutName, web);
/*     */   }
/*     */ 
/*     */   private static void removeShortcut(String shortcutName, boolean web) throws IOException
/*     */   {
/* 175 */     boolean allUsers = JWSystem.getInstallType().equalsIgnoreCase("perm_all");
/*     */ 
/* 177 */     if (OS.isWindows()) {
/* 178 */       JWWindowsOS win = new JWWindowsOS();
/*     */ 
/* 180 */       File appmenu = win.getAppStartMenuFolder();
/*     */ 
/* 182 */       File applink = new File(appmenu, shortcutName);
/*     */ 
/* 184 */       if (web)
/* 185 */         win.deleteWebShortcut(applink);
/*     */       else {
/* 187 */         win.deleteShortcut(applink);
/*     */       }
/*     */ 
/* 190 */       if (appmenu.listFiles().length == 0)
/* 191 */         appmenu.delete();
/*     */     }
/* 193 */     else if (OS.isLinux())
/*     */     {
/* 195 */       if (web)
/* 196 */         JWLinuxOS.deleteWebpageStartShortcut(shortcutName, allUsers);
/*     */       else
/* 198 */         JWLinuxOS.deleteApplicationStartShortcut(shortcutName, allUsers);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void addAppShortcut(String shortcutName, String virtualAppName, File iconFile, int iconIndex)
/*     */     throws IOException
/*     */   {
/* 213 */     addShortcut(shortcutName, virtualAppName, iconFile, iconIndex, false, null, null);
/*     */   }
/*     */ 
/*     */   public static void addAppShortcutWithID(String shortcutName, String virtualAppName, File iconFile, int iconIndex, String appID)
/*     */     throws IOException
/*     */   {
/* 226 */     addShortcut(shortcutName, virtualAppName, iconFile, iconIndex, false, appID, null);
/*     */   }
/*     */ 
/*     */   public static void addAppShortcutInFolder(String shortcutName, String virtualAppName, File iconFile, int iconIndex, File targetFolder)
/*     */     throws IOException
/*     */   {
/* 239 */     addShortcut(shortcutName, virtualAppName, iconFile, iconIndex, false, null, targetFolder);
/*     */   }
/*     */ 
/*     */   public static void addAppShortcutWithIDInFolder(String shortcutName, String virtualAppName, File iconFile, int iconIndex, String appID, File targetFolder)
/*     */     throws IOException
/*     */   {
/* 253 */     addShortcut(shortcutName, virtualAppName, iconFile, iconIndex, false, appID, targetFolder);
/*     */   }
/*     */ 
/*     */   private static void addShortcut(String shortcutName, String vappOrUrl, File iconFile, int iconIndex, boolean web, String appID, File targetDirectory)
/*     */     throws IOException
/*     */   {
/* 268 */     String app = JWSystem.getAppBundleName();
/*     */ 
/* 270 */     File appdir = JWSystem.getAppFolder();
/* 271 */     File master = appdir.getParentFile();
/*     */ 
/* 273 */     if (OS.isWindows())
/*     */     {
/* 276 */       File launcher = new File(master, GenericUpdater.getLauncherNameFor(app, false, false, false));
/*     */ 
/* 278 */       if (iconFile == null) {
/* 279 */         iconFile = launcher;
/*     */       }
/*     */ 
/* 282 */       JWWindowsOS win = new JWWindowsOS();
/*     */ 
/* 284 */       if (targetDirectory == null)
/*     */       {
/* 286 */         targetDirectory = win.getAppStartMenuFolder();
/* 287 */         System.out.println("[GenericUpdater] App start menu is " + targetDirectory);
/*     */ 
/* 289 */         targetDirectory.mkdirs();
/*     */       }
/*     */       else {
/* 292 */         System.out.println("[GenericUpdater] Target shortcut parent is " + targetDirectory);
/*     */       }
/* 294 */       File applink = new File(targetDirectory, shortcutName);
/* 295 */       System.out.println("[GenericUpdater] Creating shortcut to " + launcher.getAbsolutePath() + " from " + applink.getAbsolutePath());
/* 296 */       System.out.println("[GenericUpdater] Using icon file " + iconFile.getAbsolutePath());
/*     */ 
/* 298 */       if (web) {
/* 299 */         win.createWebShortcut(applink, vappOrUrl, iconFile, iconIndex);
/*     */       }
/* 301 */       else if (appID == null)
/* 302 */         win.createShortcut(applink, "", launcher, "JWVAPP " + JWApp.normaliseName(vappOrUrl), iconFile, iconIndex);
/*     */       else {
/* 304 */         win.createShortcutWithAppID(applink, "", launcher, "JWVAPP " + JWApp.normaliseName(vappOrUrl), iconFile, iconIndex, appID);
/*     */       }
/*     */     }
/* 307 */     else if (OS.isLinux())
/*     */     {
/* 309 */       File launcher = new File(master, GenericUpdater.getLauncherNameFor(app, true, OS.isLinux64bit(), false));
/*     */ 
/* 311 */       String command = "\"" + launcher.getCanonicalPath() + "\" JWVAPP " + JWApp.normaliseName(vappOrUrl);
/*     */ 
/* 313 */       boolean allUsers = JWSystem.getInstallType().equalsIgnoreCase("perm_all");
/*     */ 
/* 315 */       if (web)
/* 316 */         JWLinuxOS.createWebpageStartShortcut(shortcutName, shortcutName, vappOrUrl, allUsers);
/*     */       else
/* 318 */         JWLinuxOS.createApplicationStartShortcut(shortcutName, shortcutName, command, allUsers);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static File getWindowsOrLinuxShortcutLauncherFile()
/*     */   {
/* 330 */     return new File(JWSystem.getAppFolder().getParentFile(), GenericUpdater.getLauncherNameFor(JWSystem.getAppBundleName(), OS.isLinux(), OS.isLinux64bit(), false));
/*     */   }
/*     */ 
/*     */   public static void setupAllStandardShortcuts(boolean createMacOsApplicationLauncher)
/*     */     throws IOException
/*     */   {
/* 341 */     setupAllStandardShortcutsInFolder(createMacOsApplicationLauncher, null);
/*     */   }
/*     */ 
/*     */   public static void setupAllStandardShortcutsInFolder(boolean createMacOsApplicationLauncher, File targetFolder)
/*     */     throws IOException
/*     */   {
/* 351 */     JWApp[] jwapps = JWApp.getAllJWApps(JWSystem.getAppFolder(), true);
/* 352 */     Properties osxLaunchProperties = JWLaunchProperties.getAsProperties();
/*     */ 
/* 354 */     GenericUpdater.setupAllStandardShortcuts(JWSystem.getAppBundleName(), JWSystem.getAppFolder(), jwapps, osxLaunchProperties, createMacOsApplicationLauncher, targetFolder);
/*     */   }
/*     */ 
/*     */   public static void removeAllStandardShortcuts()
/*     */     throws IOException
/*     */   {
/* 362 */     JWApp[] jwapps = JWApp.getAllJWApps(JWSystem.getAppFolder(), true);
/* 363 */     GenericUpdater.removeAllStandardShortcuts(jwapps, true);
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.jwutils.JWInstallApp
 * JD-Core Version:    0.6.2
 */