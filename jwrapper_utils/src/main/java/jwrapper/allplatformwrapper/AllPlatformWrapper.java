/*     */ package jwrapper.allplatformwrapper;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import jwrapper.JRECopy;
/*     */ import jwrapper.jwutils.JWWindowsOS;
/*     */ import jwrapper.logging.LogPoller;
/*     */ import jwrapper.proxy.JWDetectedProxy;
/*     */ import jwrapper.proxy.JWProxyAuthenticator;
/*     */ import jwrapper.proxy.JWProxyCredentials;
/*     */ import jwrapper.proxy.JWProxyCredentials.Credentials;
/*     */ import jwrapper.proxy.JWProxyList;
/*     */ import jwrapper.updater.GenericUpdater;
/*     */ import jwrapper.updater.JWApp;
/*     */ import jwrapper.updater.LaunchFile;
/*     */ import jwrapper.updater.VersionUtil;
/*     */ import utils.files.AtomicFileOutputStream;
/*     */ import utils.files.FileUtil;
/*     */ import utils.files.URIUtil;
/*     */ import utils.ostools.OS;
/*     */ import utils.ostools.SaveDir;
/*     */ import utils.ostools.SlowWEnv;
/*     */ import utils.stream.CFriendlyStreamUtils;
/*     */ import utils.stream.StreamUtils;
/*     */ 
/*     */ public class AllPlatformWrapper
/*     */ {
/*     */   public static final String INSTALLTYPE_TEMP_USER = "temp_user";
/*     */   public static final String INSTALLTYPE_CUR_USER = "perm_user";
/*     */   public static final String INSTALLTYPE_ALL_USER = "perm_all";
/*     */   APWrapperListener listener;
/*     */   File master;
/*     */   String gu_version;
/*     */   String updateURL;
/*     */   Properties guargs;
/*  49 */   boolean haveTailInClasspath = false;
/*     */ 
/* 361 */   String dynpropJreName = null;
/*     */ 
/*     */   public static File getStandardMasterFolder(String appName, boolean perm_all)
/*     */   {
/*     */     File master;
/*     */     File master;
/*  53 */     if (perm_all)
/*     */     {
/*     */       File parent;
/*     */       File parent;
/*  55 */       if (OS.isWindows()) {
/*  56 */         if (OS.isWindowsVistaOrAbove())
/*     */         {
/*     */           File parent;
/*     */           try {
/*  60 */             JWWindowsOS win = new JWWindowsOS();
/*  61 */             parent = new File(win.getEnvironmentVariable("PROGRAMDATA"));
/*     */           }
/*     */           catch (Throwable x)
/*     */           {
/*     */             File parent;
/*  63 */             System.out.println("[AllPlatformWrapper] had to use WEnv to get environment variables.");
/*  64 */             parent = new File(SlowWEnv.getEnv("PROGRAMDATA"));
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*     */           String allUsers;
/*     */           try
/*     */           {
/*  72 */             JWWindowsOS win = new JWWindowsOS();
/*  73 */             allUsers = win.getEnvironmentVariable("ALLUSERSPROFILE");
/*     */           }
/*     */           catch (Throwable x)
/*     */           {
/*     */             String allUsers;
/*  75 */             System.out.println("[AllPlatformWrapper] had to use WEnv to get environment variables.");
/*  76 */             allUsers = SlowWEnv.getEnv("ALLUSERSPROFILE");
/*     */           }
/*  78 */           parent = new File(new File(allUsers), "Application Data");
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*     */         File parent;
/*  80 */         if (OS.isMacOS()) {
/*  81 */           parent = new File("/Library/Application Support/");
/*     */         }
/*     */         else
/*  84 */           parent = new File("/opt");
/*     */       }
/*  86 */       master = new File(parent, "JWrapper-" + appName);
/*     */     }
/*     */     else {
/*  89 */       master = new File(SaveDir.getUserSpecificSaveDirWithSlash("JWrapper-" + appName));
/*     */     }
/*  91 */     return master;
/*     */   }
/*     */ 
/*     */   public static AllPlatformWrapper createApwFromWrapperProperties(String staticUpdateURL, Properties lprops, boolean haveTailInClasspath)
/*     */   {
/*     */     String updateURL;
/* 103 */     if (staticUpdateURL.equals("D")) {
/* 104 */       String updateURL = lprops.getProperty("update_url");
/* 105 */       System.out.println("[OSXWrapper] Dynamic update URL is " + updateURL);
/*     */     } else {
/* 107 */       updateURL = staticUpdateURL;
/* 108 */       System.out.println("[OSXWrapper] Dynamic update URL ignored, Static update URL is " + updateURL);
/*     */     }
/*     */ 
/* 111 */     String appName = lprops.getProperty("app_name");
/* 112 */     String installType = lprops.getProperty("install_type");
/*     */ 
/* 114 */     boolean canOverrideSplash = false;
/*     */     try
/*     */     {
/* 117 */       canOverrideSplash = lprops.getProperty("can_override_splash").equalsIgnoreCase("true");
/* 118 */       if (canOverrideSplash)
/* 119 */         System.out.println("[AllPlatformWrapper] Can override splash");
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/* 124 */     boolean matchVersions = false;
/*     */     try
/*     */     {
/* 127 */       matchVersions = lprops.getProperty("match_versions").equalsIgnoreCase("true");
/* 128 */       if (matchVersions)
/* 129 */         System.out.println("[AllPlatformWrapper] Matching client version to update server version");
/*     */     }
/*     */     catch (Exception localException1)
/*     */     {
/*     */     }
/* 134 */     return new AllPlatformWrapper(appName, updateURL, installType, canOverrideSplash, null, matchVersions, lprops, haveTailInClasspath);
/*     */   }
/*     */ 
/*     */   public AllPlatformWrapper(String appName, String updateURL, String installType, boolean canOverrideSplash, String dynamicAppletSplash, boolean matchVersions, Properties lprops, boolean haveTailInClasspath)
/*     */   {
/* 139 */     this.haveTailInClasspath = haveTailInClasspath;
/*     */ 
/* 141 */     if (updateURL.endsWith("/")) {
/* 142 */       updateURL = updateURL.substring(0, updateURL.length() - 1);
/*     */     }
/*     */ 
/* 145 */     this.master = getStandardMasterFolder(appName, installType.equals("perm_all"));
/*     */ 
/* 147 */     this.guargs = new Properties();
/*     */ 
/* 149 */     this.guargs.setProperty("update_url", updateURL);
/* 150 */     this.guargs.setProperty("app_name", appName);
/*     */ 
/* 152 */     if (lprops.getProperty("show_no_ui") != null) {
/* 153 */       this.guargs.setProperty("show_no_ui", lprops.getProperty("show_no_ui"));
/*     */     }
/* 155 */     setDynamicPropertyJreName(lprops.getProperty("jre_name"));
/* 156 */     this.guargs.setProperty("jre_name", getTargetJREName(this.master));
/*     */ 
/* 158 */     if (canOverrideSplash)
/* 159 */       this.guargs.setProperty("can_override_splash", "1");
/*     */     else
/* 161 */       this.guargs.setProperty("can_override_splash", "0");
/* 162 */     if ((dynamicAppletSplash != null) && (dynamicAppletSplash.length() > 0)) {
/* 163 */       this.guargs.setProperty("splash_image", dynamicAppletSplash);
/*     */     }
/*     */ 
/* 166 */     String splash = this.guargs.getProperty("min_splash_ms");
/* 167 */     if (splash == null)
/* 168 */       this.guargs.setProperty("min_splash_ms", "700");
/* 169 */     else if (splash.trim().length() == 0) {
/* 170 */       this.guargs.setProperty("min_splash_ms", "700");
/*     */     }
/*     */ 
/* 173 */     this.guargs.setProperty("match_versions", matchVersions);
/* 174 */     this.guargs.setProperty("install_type", installType);
/*     */ 
/* 176 */     if (lprops != null)
/*     */     {
/* 178 */       Object[] keys = lprops.keySet().toArray();
/* 179 */       for (int i = 0; i < keys.length; i++) {
/* 180 */         String key = (String)keys[i];
/* 181 */         String value = lprops.getProperty(key);
/* 182 */         this.guargs.setProperty(key, value);
/*     */       }
/*     */     }
/*     */ 
/* 186 */     this.updateURL = updateURL;
/*     */   }
/*     */ 
/*     */   public LogPoller getLogPoller()
/*     */   {
/* 194 */     return new LogPoller(this.master);
/*     */   }
/*     */ 
/*     */   public void setAPWrapperListener(APWrapperListener listener) {
/* 198 */     this.listener = listener;
/*     */   }
/*     */ 
/*     */   public boolean canLaunchWithoutInstall()
/*     */     throws IOException
/*     */   {
/* 204 */     System.out.println("[AllPlatformWrapper] Checking if able to launch without install");
/* 205 */     if (this.master.exists()) {
/* 206 */       System.out.println("[AllPlatformWrapper] Master folder exists");
/*     */ 
/* 208 */       File latest = LaunchFile.getLatestVersionOf("JWrapper", this.master);
/*     */ 
/* 210 */       if (latest != null) {
/* 211 */         System.out.println("[AllPlatformWrapper] GU exists");
/*     */ 
/* 213 */         if (!OS.isMacOS()) {
/* 214 */           String jreName = getTargetJREName(this.master);
/*     */ 
/* 216 */           File jreFile = null;
/*     */ 
/* 218 */           if (jreName.length() > 2) {
/* 219 */             jreFile = LaunchFile.getLatestVersionOf(jreName, this.master);
/*     */ 
/* 221 */             if (jreFile == null) {
/* 222 */               System.out.println("[AllPlatformWrapper] JRE does not exist (so no)");
/*     */ 
/* 224 */               return false;
/*     */             }
/* 226 */             System.out.println("[AllPlatformWrapper] JRE exists");
/*     */           }
/*     */         }
/*     */         else {
/* 230 */           System.out.println("[AllPlatformWrapper] Not checking for JRE (on MacOS)");
/*     */         }
/*     */ 
/* 233 */         return true;
/*     */       }
/* 235 */       System.out.println("[AllPlatformWrapper] GU does not exist (so no)");
/* 236 */       return false;
/*     */     }
/*     */ 
/* 239 */     System.out.println("[AllPlatformWrapper] Master does not exist (so no)");
/* 240 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean prepareForLaunch() throws IOException
/*     */   {
/*     */     try {
/* 246 */       String jreName = getTargetJREName(this.master);
/*     */ 
/* 248 */       if (carryingNewerGU()) {
/* 249 */         System.out.println("[AllPlatformWrapper] We are carrying a newer GU version so will update before launching");
/* 250 */       } else if (carryingNewerApp()) {
/* 251 */         System.out.println("[AllPlatformWrapper] We are carrying a newer App version so will update before launching");
/* 252 */       } else if (this.master.exists()) {
/* 253 */         System.out.println("[AllPlatformWrapper] Master folder exists");
/*     */ 
/* 255 */         File latest = LaunchFile.getLatestVersionOf("JWrapper", this.master);
/*     */ 
/* 257 */         if (latest != null)
/*     */         {
/* 259 */           if (!OS.isMacOS())
/*     */           {
/* 261 */             File jreFile = null;
/*     */ 
/* 263 */             if (jreName.length() > 2) {
/* 264 */               jreFile = LaunchFile.getLatestVersionOf(jreName, this.master);
/*     */ 
/* 266 */               if (jreFile == null) {
/* 267 */                 System.out.println("[AllPlatformWrapper] Master folder exists but no JRE, will copy");
/*     */ 
/* 269 */                 copyJRE();
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 277 */           if (this.listener != null) this.listener.canLaunch();
/*     */ 
/* 302 */           return true;
/*     */         }
/*     */ 
/* 305 */         System.out.println("[AllPlatformWrapper] No versions of JWrapper found in " + this.master);
/*     */       }
/*     */       else
/*     */       {
/* 309 */         System.out.println("[AllPlatformWrapper] No master folder " + this.master + " found, must update before launching");
/*     */       }
/*     */ 
/* 312 */       System.out.println("[AllPlatformWrapper] Will download or extract GU and set up");
/*     */ 
/* 314 */       if (!this.haveTailInClasspath) {
/* 315 */         this.gu_version = downloadAsStringUTF8(this.updateURL, GenericUpdater.getVersionFileNameFor("JWrapper"));
/*     */       }
/*     */ 
/* 318 */       this.master.mkdirs();
/*     */ 
/* 320 */       if ((!OS.isMacOS()) && 
/* 321 */         (jreName.length() > 2)) {
/* 322 */         copyJRE();
/*     */       }
/*     */ 
/* 326 */       updateGU();
/*     */ 
/* 328 */       if (this.listener != null) this.listener.canLaunch();
/*     */ 
/* 330 */       return true; } catch (InterruptedException xx) {
/*     */     }
/* 332 */     throw new IOException("Interrupted I/O");
/*     */   }
/*     */ 
/*     */   public void launchNow()
/*     */     throws Exception
/*     */   {
/* 338 */     launchNow(new String[0]);
/*     */   }
/*     */ 
/*     */   public void launchNow(String[] cmdlineArgs) throws Exception {
/* 342 */     System.out.println("[AllPlatformWrapper] Launching App");
/* 343 */     String jreName = getTargetJREName(this.master);
/*     */ 
/* 346 */     File jreFile = null;
/*     */ 
/* 348 */     if (jreName.length() > 2) {
/* 349 */       jreFile = LaunchFile.getLatestVersionOf(jreName, this.master);
/*     */     }
/*     */ 
/* 353 */     System.out.println("[AllPlatformWrapper] Using JRE " + jreFile);
/*     */ 
/* 355 */     File latest = LaunchFile.getLatestVersionOf("JWrapper", this.master);
/*     */ 
/* 357 */     LaunchFile.runVirtualAppFromNoExit(latest, this.guargs, jreFile, this.updateURL, cmdlineArgs, "JWrapper", false);
/*     */   }
/*     */ 
/*     */   private void setDynamicPropertyJreName(String name)
/*     */   {
/* 364 */     if ((name != null) && 
/* 365 */       (name.length() > 0))
/* 366 */       this.dynpropJreName = name;
/*     */   }
/*     */ 
/*     */   private String getTargetJREName(File master)
/*     */   {
/* 373 */     File jwapps = JWApp.getJWAppsFolder(master);
/* 374 */     File jreoverride = new File(jwapps, GenericUpdater.getJreNameOverrideFileName());
/*     */     try
/*     */     {
/* 377 */       AtomicFileOutputStream.prepareForReading(jreoverride);
/*     */ 
/* 379 */       if (jreoverride.exists()) {
/* 380 */         InputStream in = FileUtil.ropen(jreoverride);
/* 381 */         String jreName = CFriendlyStreamUtils.readString(in);
/* 382 */         in.close();
/*     */ 
/* 384 */         System.out.println("[AllPlatformWrapper] JRE override name is " + jreName);
/* 385 */         return jreName;
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */       String jreName;
/* 392 */       if (this.dynpropJreName != null) {
/* 393 */         String jreName = this.dynpropJreName;
/* 394 */         System.out.println("[AllPlatformWrapper] No JRE override name, will return dynprop specified name " + jreName);
/*     */       } else {
/* 396 */         jreName = bestGuessJREName();
/* 397 */         System.out.println("[AllPlatformWrapper] No JRE override name and no dynprop JRE name, will return best guess " + jreName);
/*     */       }
/*     */ 
/* 400 */       return jreName;
/*     */     }
/*     */   }
/*     */ 
/*     */   private String bestGuessJREName()
/*     */   {
/* 409 */     if (OS.isWindows())
/*     */       try {
/* 411 */         JWWindowsOS win = new JWWindowsOS();
/* 412 */         if (win.is64BitWindowsOS()) {
/* 413 */           return GenericUpdater.JRE_WIN64_APP;
/*     */         }
/* 415 */         return GenericUpdater.JRE_WIN32_APP;
/*     */       }
/*     */       catch (Throwable t) {
/* 418 */         System.out.println("[AllPlatformWrapper] Unable to natively detect Windows OS bitness, assuming 32");
/* 419 */         return GenericUpdater.JRE_WIN32_APP;
/*     */       }
/* 421 */     if (OS.isLinux())
/*     */     {
/* 425 */       if (OS.isLinux64bit()) {
/* 426 */         return GenericUpdater.JRE_LIN64_APP;
/*     */       }
/* 428 */       return GenericUpdater.JRE_LIN32_APP;
/*     */     }
/*     */ 
/* 431 */     return "-";
/*     */   }
/*     */ 
/*     */   private void copyJRE()
/*     */     throws IOException, InterruptedException
/*     */   {
/* 437 */     String jhome = System.getProperty("java.home");
/* 438 */     File src = new File(jhome);
/*     */ 
/* 440 */     System.out.println("[AllPlatformWrapper] My JRE home is " + src);
/*     */ 
/* 442 */     String jreName = getTargetJREName(this.master);
/*     */ 
/* 445 */     File temp = createTempJreFolder();
/*     */ 
/* 453 */     System.out.println("[AllPlatformWrapper] Setting up JRE in " + temp);
/* 454 */     JRECopy.copyJRE(src, temp);
/*     */ 
/* 457 */     File target = new File(this.master, GenericUpdater.getAppFolderNameFor(jreName, VersionUtil.padVersion(0)));
/* 458 */     System.out.println("[AllPlatformWrapper] Renaming JRE to " + target + " (next failure to delete target is OK, we delete it just in case)");
/* 459 */     FileUtil.deleteDir(target);
/*     */ 
/* 461 */     long giveUp = System.currentTimeMillis() + 4000L;
/* 462 */     while (!temp.renameTo(target)) {
/* 463 */       Thread.sleep(200L);
/* 464 */       if (System.currentTimeMillis() > giveUp) throw new IOException("Could not rename JRE folder to proper name");
/*     */     }
/*     */   }
/*     */ 
/*     */   private File createTempJreFolder()
/*     */   {
/* 470 */     File temp = new File(this.master, "JWrapperTemp-" + System.currentTimeMillis() + "-" + this.master.list().length + "-jre");
/* 471 */     return temp;
/*     */   }
/*     */ 
/*     */   private File createTempGuFolder()
/*     */   {
/* 476 */     File temp = new File(this.master, "JWrapperTemp-" + System.currentTimeMillis() + "-" + this.master.list().length + "-gu");
/* 477 */     return temp;
/*     */   }
/*     */ 
/*     */   private File createTempAppFolder()
/*     */   {
/* 482 */     File temp = new File(this.master, "JWrapperTemp-" + System.currentTimeMillis() + "-" + this.master.list().length + "-app");
/* 483 */     return temp;
/*     */   }
/*     */ 
/*     */   private static void extractAndUnpack(File dir, String app, String base) throws IOException {
/* 487 */     InputStream in = AllPlatformWrapper.class.getResourceAsStream("/" + app);
/*     */ 
/* 489 */     if (in == null) throw new IOException("[AllPlatformWrapper] archive " + app + " not found");
/*     */ 
/* 491 */     File dest = new File(dir, app);
/* 492 */     OutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
/*     */ 
/* 494 */     byte[] buf = new byte[64000];
/* 495 */     int n = 0;
/* 496 */     while (n != -1) {
/* 497 */       n = in.read(buf);
/* 498 */       if (n > 0) {
/* 499 */         out.write(buf, 0, n);
/*     */       }
/*     */     }
/*     */ 
/* 503 */     in.close();
/* 504 */     out.close();
/*     */ 
/* 507 */     GenericUpdater.unpackArchive(dir, dest, base);
/*     */   }
/*     */ 
/*     */   private boolean carryingNewerGU() {
/* 511 */     if (this.haveTailInClasspath)
/*     */     {
/* 513 */       System.out.println("[AllPlatformsWrapper] Checking to see if our GU version is newer");
/* 514 */       String jwVersion = this.guargs.getProperty("wrapper_gu_version");
/* 515 */       System.out.println("[AllPlatformsWrapper] Our GU version is " + jwVersion);
/*     */ 
/* 517 */       if (jwVersion == null) {
/* 518 */         jwVersion = "";
/*     */       }
/* 520 */       jwVersion = jwVersion.trim();
/*     */ 
/* 522 */       if (jwVersion.length() == 0) {
/* 523 */         System.out.println("[AllPlatformsWrapper] We have no GU version (looks like we are a launcher)");
/*     */       }
/*     */       else
/*     */       {
/* 527 */         File latest = LaunchFile.getLatestVersionOf("JWrapper", this.master);
/*     */ 
/* 529 */         if (latest == null) {
/* 530 */           System.out.println("[AllPlatformsWrapper] No existing GU found (so we are newer)");
/* 531 */           return true;
/*     */         }
/*     */ 
/* 534 */         String curVersion = LaunchFile.pickVersionFromAppFolder(latest);
/*     */ 
/* 536 */         System.out.println("[AllPlatformsWrapper] Current GU version is " + curVersion);
/*     */ 
/* 538 */         if (LaunchFile.versionIsLater(curVersion, jwVersion))
/*     */         {
/* 540 */           System.out.println("[AllPlatformsWrapper] Our version of GU is newer " + jwVersion + " > " + curVersion);
/* 541 */           return true;
/*     */         }
/* 543 */         System.out.println("[AllPlatformsWrapper] Current GU version is same or newer");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 548 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean carryingNewerApp() {
/* 552 */     if (this.haveTailInClasspath)
/*     */     {
/* 554 */       System.out.println("[AllPlatformsWrapper] Checking to see if our App version is newer");
/* 555 */       String appName = this.guargs.getProperty("app_name");
/* 556 */       String appVersion = this.guargs.getProperty("wrapper_app_version");
/*     */ 
/* 558 */       if (appVersion == null) {
/* 559 */         appVersion = "";
/*     */       }
/* 561 */       appVersion = appVersion.trim();
/*     */ 
/* 563 */       if (appVersion.length() == 0) {
/* 564 */         System.out.println("[AllPlatformsWrapper] We have no App version (we are onine wrapper or launcher)");
/*     */       }
/*     */       else
/*     */       {
/* 568 */         File latest = LaunchFile.getLatestVersionOf(appName, this.master);
/*     */ 
/* 570 */         if (latest == null) {
/* 571 */           System.out.println("[AllPlatformsWrapper] No existing App found (so we are newer)");
/* 572 */           return true;
/*     */         }
/*     */ 
/* 575 */         String curVersion = LaunchFile.pickVersionFromAppFolder(latest);
/* 576 */         if (LaunchFile.versionIsLater(curVersion, appVersion))
/*     */         {
/* 578 */           System.out.println("[AllPlatformsWrapper] Our version of " + appName + " is newer " + appVersion + " > " + curVersion);
/* 579 */           return true;
/*     */         }
/* 581 */         System.out.println("[AllPlatformsWrapper] Current App version is same or newer");
/*     */       }
/*     */     }
/*     */ 
/* 585 */     return false;
/*     */   }
/*     */ 
/*     */   private void updateGU()
/*     */     throws IOException, InterruptedException
/*     */   {
/* 591 */     System.out.println("[AllPlatformWrapper] Updating GU (will extract and unpack tail too if we have one)");
/*     */ 
/* 593 */     if (this.haveTailInClasspath) {
/* 594 */       String appName = this.guargs.getProperty("app_name");
/*     */ 
/* 596 */       String jwVersion = this.guargs.getProperty("wrapper_gu_version");
/* 597 */       if (jwVersion == null) {
/* 598 */         jwVersion = "";
/*     */       }
/* 600 */       jwVersion = jwVersion.trim();
/*     */ 
/* 602 */       String appVersion = this.guargs.getProperty("wrapper_app_version");
/*     */ 
/* 605 */       if ((appVersion != null) && (carryingNewerApp()))
/*     */       {
/* 607 */         File f = createTempAppFolder();
/* 608 */         f.mkdir();
/*     */ 
/* 610 */         System.out.println("[AllPlatformWrapper] Extracting App to " + f.getName());
/*     */ 
/* 612 */         extractAndUnpack(f, GenericUpdater.getArchiveNameFor(appName, appVersion), this.updateURL);
/*     */ 
/* 614 */         f.renameTo(new File(this.master, "JWrapper-" + appName + "-" + appVersion + "-complete"));
/*     */       }
/*     */       else
/*     */       {
/* 618 */         System.out.println("[AllPlatformWrapper] No App in tail (online wrapper)");
/*     */       }
/*     */ 
/* 621 */       if ((jwVersion != null) && (carryingNewerGU())) {
/* 622 */         File f = createTempGuFolder();
/* 623 */         f.mkdir();
/*     */ 
/* 625 */         System.out.println("[AllPlatformWrapper] Extracting GU to " + f.getName());
/*     */ 
/* 627 */         extractAndUnpack(f, GenericUpdater.getArchiveNameFor("JWrapper", jwVersion), this.updateURL);
/*     */ 
/* 630 */         File target = new File(this.master, "JWrapper-JWrapper-" + jwVersion + "-complete");
/* 631 */         f.renameTo(target);
/*     */ 
/* 633 */         return;
/*     */       }
/* 635 */       System.out.println("[AllPlatformWrapper] Tail specified in classpath but no JW version in properties.  Likely we are an OSX launcher .app with no tail but installation is gone, exiting...");
/*     */ 
/* 638 */       return;
/*     */     }
/*     */ 
/* 641 */     File temp = createTempGuFolder();
/*     */ 
/* 643 */     temp.mkdirs();
/*     */ 
/* 645 */     String item = GenericUpdater.getArchiveNameFor("JWrapper", this.gu_version);
/*     */ 
/* 647 */     File archive = new File(temp, item);
/*     */ 
/* 649 */     System.out.println("[AllPlatformWrapper] Downloading " + this.updateURL);
/* 650 */     downloadToFile(this.updateURL, item, archive);
/*     */ 
/* 652 */     GenericUpdater.unpackArchive(temp, archive, this.updateURL);
/*     */ 
/* 654 */     File target = new File(this.master, GenericUpdater.getAppFolderNameFor("JWrapper", this.gu_version));
/*     */ 
/* 656 */     long giveUp = System.currentTimeMillis() + 4000L;
/* 657 */     while (!temp.renameTo(target)) {
/* 658 */       Thread.sleep(200L);
/* 659 */       if (System.currentTimeMillis() > giveUp) throw new IOException("Could not rename App folder to proper name"); 
/*     */     }
/*     */   }
/*     */ 
/*     */   private byte[] download(String updateURL, String item)
/*     */     throws IOException
/*     */   {
/* 665 */     URL url = new URL(updateURL + "/" + item);
/* 666 */     url = URIUtil.tryGetSafeURLFrom(url);
/* 667 */     InputStream in = new BufferedInputStream(url.openStream(), 10000);
/* 668 */     byte[] dat = StreamUtils.readAll(in);
/* 669 */     in.close();
/* 670 */     return dat;
/*     */   }
/*     */ 
/*     */   private String downloadAsStringUTF8(String updateURL, String item) throws IOException {
/* 674 */     return new String(download(updateURL, item), "UTF8");
/*     */   }
/*     */ 
/*     */   private void downloadToFile(String updateURL, String item, File target) throws IOException {
/* 678 */     URL url = new URL(updateURL + "/" + item);
/* 679 */     url = URIUtil.tryGetSafeURLFrom(url);
/* 680 */     URLConnection conn = url.openConnection();
/* 681 */     InputStream in = new BufferedInputStream(conn.getInputStream(), 10000);
/*     */ 
/* 683 */     OutputStream out = new BufferedOutputStream(new FileOutputStream(target), 10000);
/*     */ 
/* 685 */     double max = 500000.0D;
/*     */     try {
/* 687 */       max = Double.parseDouble(conn.getHeaderField("content-length").trim());
/*     */     } catch (Exception x) {
/* 689 */       System.out.println("[AllPlatformWrapper] Unable to get file download size");
/*     */     }
/*     */ 
/* 692 */     boolean SIMULATE_SLOW_DOWNLOAD = false;
/*     */ 
/* 694 */     byte[] dat = new byte[20000];
/*     */ 
/* 698 */     int n = 0;
/* 699 */     int written = 0;
/*     */ 
/* 701 */     while (n != -1) {
/* 702 */       n = in.read(dat);
/* 703 */       if (n > 0) {
/* 704 */         out.write(dat, 0, n);
/* 705 */         written += n;
/* 706 */         if (this.listener != null) this.listener.setDownloadProgress(Math.min(1.0D, 0.0D + 1.0D / max * written));
/*     */ 
/* 708 */         if (SIMULATE_SLOW_DOWNLOAD)
/*     */           try {
/* 710 */             Thread.sleep(500L);
/*     */           }
/*     */           catch (Exception localException1)
/*     */           {
/*     */           }
/*     */       }
/*     */     }
/* 717 */     out.flush();
/*     */ 
/* 719 */     in.close();
/* 720 */     out.close();
/*     */   }
/*     */ 
/*     */   public void setProxyToUse(Proxy proxy, JWProxyCredentials.Credentials proxyCredentials) throws IOException
/*     */   {
/* 725 */     if (proxy != null)
/*     */     {
/* 727 */       if (proxyCredentials != null)
/* 728 */         System.out.println("[AllPlatformWrapper] Using proxy details " + proxy + " with credentials.");
/*     */       else {
/* 730 */         System.out.println("[AllPlatformWrapper] Using proxy details: " + proxy);
/*     */       }
/* 732 */       JWDetectedProxy.DETECTED_PROXY_OK = true;
/* 733 */       JWDetectedProxy.DETECTED_PROXY = proxy;
/*     */ 
/* 735 */       if (proxyCredentials != null)
/*     */       {
/* 737 */         JWDetectedProxy.setDetectedProxyAsDefault();
/* 738 */         JWProxyCredentials proxyCreds = new JWProxyCredentials();
/* 739 */         proxyCreds.setCredentialsFor(proxy, proxyCredentials);
/* 740 */         new JWProxyAuthenticator(proxyCreds);
/*     */       }
/*     */ 
/* 744 */       File appsSharedConfigFolder = new File(this.master, "JWAppsSharedConfig");
/* 745 */       if (!appsSharedConfigFolder.exists())
/*     */       {
/* 747 */         System.out.println("[AllPlatformWrapper] Creating JWAppsSharedConfig folder");
/* 748 */         appsSharedConfigFolder.mkdirs();
/*     */       }
/*     */ 
/* 751 */       File detectedProxies = new File(appsSharedConfigFolder, "DetectedProxies");
/* 752 */       File credentialsFile = new File(appsSharedConfigFolder, "ProxyCredentials");
/*     */ 
/* 754 */       JWProxyList proxyList = new JWProxyList(10);
/* 755 */       proxyList.addProxyToFront(proxy);
/* 756 */       proxyList.saveToFile(detectedProxies);
/*     */ 
/* 758 */       if (proxyCredentials != null)
/*     */       {
/* 760 */         JWProxyCredentials credentials = new JWProxyCredentials();
/* 761 */         credentials.loadFromFile(credentialsFile);
/* 762 */         credentials.setCredentialsFor(proxy, proxyCredentials);
/* 763 */         credentials.saveToFile(credentialsFile);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.allplatformwrapper.AllPlatformWrapper
 * JD-Core Version:    0.6.2
 */