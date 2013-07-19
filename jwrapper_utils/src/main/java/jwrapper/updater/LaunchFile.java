/*      */ package jwrapper.updater;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import jwrapper.InsideApplet;
/*      */ import jwrapper.VMTransmuter;
/*      */ import jwrapper.archive.Archive;
/*      */ import jwrapper.jwutils.JWLinuxOS;
/*      */ import utils.files.AutoChmodFile;
/*      */ import utils.files.FileUtil;
/*      */ import utils.files.PathUtil;
/*      */ import utils.ostools.OS;
/*      */ import utils.stream.CFriendlyStreamUtils;
/*      */ import utils.vm.ProcessPrinter;
/*      */ import utils.vm.VMFork;
/*      */ 
/*      */ public class LaunchFile
/*      */ {
/*   30 */   public static String JW_VAPP_UPDATE_APP = "JWrapperUpdateApp";
/*   31 */   public static String JW_VAPP_COMPATIBILITY_APP = "JWrapperJreCompatibilityApp";
/*   32 */   public static String JW_VAPP_POST_INSTALL_APP = "JWrapperPostInstallApp";
/*   33 */   public static String JW_VAPP_PRE_UNINSTALL_APP = "JWrapperPreUninstallApp";
/*   34 */   public static String JW_VAPP_POST_UNINSTALL_APP = "JWrapperPostUninstallApp";
/*   35 */   public static String JW_VAPP_MATCH_VERSIONS_SERVER_UNAVAILABLE = "JWrapperMatchedVersionServerUnavailable";
/*      */ 
/*   38 */   public static String JWRAPPER_JVM_OPTION = "-Djwrapper.JWrapper=true";
/*      */ 
/*   52 */   static boolean AUTOCOPY_GU_JW_UTILS_AND_INCLUDE = false;
/*   53 */   static String GU_DIR = null;
/*      */ 
/*   60 */   public static int POST_LAUNCH_PRINT_FOR = 100;
/*      */   public static final int LAUNCHFILE_VAPP_DOES_NOT_EXIST = 999001;
/*      */   public static final int PINST_CONTINUE_USE_STANDARD = 41;
/*      */   public static final int PINST_CONTINUE_SKIP_STANDARD = 42;
/*      */   public static final int PINST_QUIT_AND_ROLL_BACK = 43;
/*      */   public static final int UAPP_UPDATE_AND_RUN = 50;
/*      */   public static final int UAPP_JUST_RUN = 51;
/*      */   public static final int UAPP_EXIT = 52;
/*      */   public static final int UAPP_JUST_RUN_STOP_TELLING_ME = 53;
/*      */   public static final int PUNIN_DO_UNINSTALL = 61;
/*      */   public static final int PUNIN_ASK_IF_UNINSTALL = 62;
/*      */   public static final int PUNIN_CANCEL_UNINSTALL = 63;
/*      */   public static final int UJRE_OK = 72;
/*      */   public static final int UJRE_NOTSUPPORTED = 73;
/*      */   public static final int UNAVAIL_SHOW_ERROR = 81;
/*      */   public static final int UNAVAIL_HIDE_ERROR = 82;
/*      */   public static final int UNAVAIL_RELAUNCH_SOURCELAUNCHER = 83;
/*      */   private static final int LF_VERSION = 6;
/*      */   public static final String FILE_NAME = "JWrapperLaunch";
/*      */   public static final String JREVER_FILE_NAME = "JWrapperUseJRE";
/*      */   String mainClass;
/*      */   String mainClassOnUpdate;
/*      */   String mainClassForCompatibility;
/*      */   String mainClassPostInstall;
/*      */   String mainClassPreUninstall;
/*      */   String appName;
/* 1034 */   ArrayList classpath = new ArrayList();
/* 1035 */   ArrayList params = new ArrayList();
/*      */ 
/* 1038 */   ArrayList jvmOptions = new ArrayList();
/* 1039 */   boolean willRequireURL = false;
/* 1040 */   String osxIcnsFile = "";
/* 1041 */   boolean mustFork = false;
/*      */ 
/*      */   public static File getJvmLastSuccessfulOptionsFile(File master, File jre)
/*      */   {
/*   42 */     File jwapps = new File(master, "JWApps");
/*   43 */     jwapps.mkdirs();
/*   44 */     if (jre == null) {
/*   45 */       String jvm_home = System.getProperty("java.home");
/*   46 */       jre = new File(jvm_home);
/*      */     }
/*   48 */     return new File(jwapps, "JRE-LastSuccessfulOptions-" + jre.getName());
/*      */   }
/*      */ 
/*      */   static void setAutoCopyJWUtilsAndInclude(String gu_dir)
/*      */   {
/*   56 */     GU_DIR = gu_dir;
/*   57 */     AUTOCOPY_GU_JW_UTILS_AND_INCLUDE = true;
/*      */   }
/*      */ 
/*      */   public static void main(String[] args)
/*      */     throws Exception
/*      */   {
/*   95 */     System.out.println("yeah");
/*   96 */     getLaunchCommandFor(new File("/Users/aem/Library/Application Support/JWrapper-JWTestApp/JWrapper-JWrapper-00015753914-complete"));
/*      */   }
/*      */ 
/*      */   public static LaunchFile getLaunchCommandFor(File app) throws IOException {
/*  100 */     File command = new File(app, "JWrapperLaunch");
/*      */ 
/*  102 */     if (!command.exists()) {
/*  103 */       return null;
/*      */     }
/*      */ 
/*  106 */     InputStream in = new BufferedInputStream(new FileInputStream(command));
/*      */ 
/*  108 */     int VER = CFriendlyStreamUtils.readInt(in);
/*      */ 
/*  112 */     String clazz = CFriendlyStreamUtils.readString(in);
/*  113 */     String onupdate = CFriendlyStreamUtils.readString(in);
/*  114 */     String appName = CFriendlyStreamUtils.readString(in);
/*  115 */     String osxIcnsFile = CFriendlyStreamUtils.readString(in);
/*  116 */     boolean urlreq = CFriendlyStreamUtils.readBoolean(in);
/*      */ 
/*  118 */     LaunchFile lf = new LaunchFile(clazz, urlreq, onupdate, osxIcnsFile, appName);
/*      */ 
/*  120 */     System.out.println(CFriendlyStreamUtils.readInt(in));
/*  121 */     System.out.println(CFriendlyStreamUtils.readInt(in));
/*      */ 
/*  125 */     int n = CFriendlyStreamUtils.readInt(in);
/*  126 */     for (int i = 0; i < n; i++) {
/*  127 */       lf.addClasspath(CFriendlyStreamUtils.readString(in));
/*      */     }
/*      */ 
/*  130 */     n = CFriendlyStreamUtils.readInt(in);
/*  131 */     for (int i = 0; i < n; i++) {
/*  132 */       lf.addParam(CFriendlyStreamUtils.readString(in));
/*      */     }
/*      */ 
/*  135 */     if (VER >= 3) {
/*  136 */       lf.setMainClassForJreCompatibility(CFriendlyStreamUtils.readString(in));
/*      */     }
/*      */ 
/*  139 */     if (VER >= 4) {
/*  140 */       lf.setMainClassForPostInstall(CFriendlyStreamUtils.readString(in));
/*  141 */       lf.setMainClassForPreUninstall(CFriendlyStreamUtils.readString(in));
/*      */     }
/*      */ 
/*  144 */     if (VER >= 5) {
/*  145 */       n = CFriendlyStreamUtils.readInt(in);
/*  146 */       for (int i = 0; i < n; i++) {
/*  147 */         lf.addJvmOption(CFriendlyStreamUtils.readString(in));
/*      */       }
/*      */     }
/*      */ 
/*  151 */     if (VER >= 6) {
/*  152 */       lf.setMustFork(CFriendlyStreamUtils.readBoolean(in));
/*      */     }
/*      */ 
/*  155 */     return lf;
/*      */   }
/*      */ 
/*      */   public static ArrayList getFilesStartingWith(File dir, String prefix, boolean removeVersionFiles) {
/*  159 */     File[] files = dir.listFiles();
/*  160 */     ArrayList list = new ArrayList();
/*      */ 
/*  162 */     if (files != null) {
/*  163 */       for (int i = 0; i < files.length; i++) {
/*  164 */         if ((files[i].getName().startsWith(prefix)) && (
/*  165 */           (!removeVersionFiles) || (!files[i].getName().endsWith("version.txt")))) {
/*  166 */           list.add(files[i]);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  172 */     return list;
/*      */   }
/*      */ 
/*      */   public static boolean versionIsLater(String orig, String compare) {
/*  176 */     int i1 = Integer.parseInt(orig.trim());
/*  177 */     int i2 = Integer.parseInt(compare.trim());
/*  178 */     return i2 > i1;
/*      */   }
/*      */ 
/*      */   public static boolean versionIsSame(String orig, String compare) {
/*  182 */     int i1 = Integer.parseInt(orig.trim());
/*  183 */     int i2 = Integer.parseInt(compare.trim());
/*  184 */     return i2 == i1;
/*      */   }
/*      */ 
/*      */   public static boolean versionIsSameOrLater(String orig, String compare) {
/*  188 */     int i1 = Integer.parseInt(orig.trim());
/*  189 */     int i2 = Integer.parseInt(compare.trim());
/*  190 */     return i2 >= i1;
/*      */   }
/*      */ 
/*      */   public static String pickAppNameFromAppFolder(File app) {
/*  194 */     String name = app.getName();
/*      */ 
/*  196 */     name = name.substring(name.indexOf('-') + 1);
/*  197 */     name = name.substring(0, name.indexOf('-'));
/*      */ 
/*  199 */     return name;
/*      */   }
/*      */ 
/*      */   public static int pickIntegerVersionFromAppFolder(File app)
/*      */   {
/*  204 */     return (int)Long.parseLong(pickVersionFromAppFolder(app));
/*      */   }
/*      */ 
/*      */   public static String pickVersionFromAppFolder(File app) {
/*  208 */     String name = app.getName();
/*      */ 
/*  210 */     name = name.substring(name.indexOf('-') + 1);
/*  211 */     name = name.substring(name.indexOf('-') + 1);
/*  212 */     String version = name.substring(0, name.indexOf('-'));
/*  213 */     return version;
/*      */   }
/*      */ 
/*      */   public static boolean isAppArchive(File test) {
/*  217 */     String name = test.getName();
/*      */ 
/*  219 */     if (name.startsWith("JWrapper-")) {
/*  220 */       if (name.endsWith("-archive.p2.l2")) {
/*      */         try {
/*  222 */           pickIntegerVersionFromAppArchive(test);
/*  223 */           return true;
/*      */         } catch (Exception x) {
/*  225 */           return false;
/*      */         }
/*      */       }
/*  228 */       return false;
/*      */     }
/*      */ 
/*  231 */     return false;
/*      */   }
/*      */ 
/*      */   public static boolean isAppFolder(File test)
/*      */   {
/*  236 */     String name = test.getName();
/*      */ 
/*  238 */     if (test.isDirectory()) {
/*  239 */       if (name.startsWith("JWrapper-")) {
/*  240 */         if (name.endsWith("-complete")) {
/*      */           try {
/*  242 */             pickIntegerVersionFromAppFolder(test);
/*  243 */             return true;
/*      */           } catch (Exception x) {
/*  245 */             return false;
/*      */           }
/*      */         }
/*  248 */         return false;
/*      */       }
/*      */ 
/*  251 */       return false;
/*      */     }
/*      */ 
/*  254 */     return false;
/*      */   }
/*      */ 
/*      */   public static int pickIntegerVersionFromAppArchive(File app)
/*      */   {
/*  259 */     return (int)Long.parseLong(pickVersionFromAppArchive(app));
/*      */   }
/*      */ 
/*      */   public static String pickVersionFromAppArchive(File app) {
/*  263 */     String name = app.getName();
/*      */ 
/*  265 */     name = name.substring(name.indexOf('-') + 1);
/*  266 */     name = name.substring(name.indexOf('-') + 1);
/*  267 */     String version = name.substring(0, name.indexOf('-'));
/*  268 */     return version;
/*      */   }
/*      */ 
/*      */   public static boolean fileExistsStartingWith(File dir, String prefix, boolean removeVersionFiles) {
/*  272 */     return getFilesStartingWith(dir, prefix, removeVersionFiles).size() > 0;
/*      */   }
/*      */ 
/*      */   public static File[] getAllVersionsOf(String app, File master)
/*      */   {
/*  277 */     ArrayList various = getFilesStartingWith(master, "JWrapper-" + app + "-", true);
/*      */ 
/*  279 */     File[] tmp = new File[various.size()];
/*  280 */     various.toArray(tmp);
/*      */ 
/*  282 */     return tmp;
/*      */   }
/*      */ 
/*      */   public static File getSpecificVersionOf(String app, String version, File master)
/*      */   {
/*  287 */     File f = new File(master, "JWrapper-" + app + "-" + version + "-complete");
/*      */ 
/*  289 */     if (f.exists()) {
/*  290 */       System.out.println("[LaunchFile] Specific app version " + app + "-" + version + " does exist");
/*  291 */       return f;
/*      */     }
/*  293 */     System.out.println("[LaunchFile] Specific app version " + app + "-" + version + " does NOT exist");
/*  294 */     return null;
/*      */   }
/*      */ 
/*      */   public static File getLatestVersionOf(String app, File master)
/*      */   {
/*  299 */     ArrayList various = getFilesStartingWith(master, "JWrapper-" + app + "-", true);
/*      */ 
/*  301 */     if (various.size() == 0) {
/*  302 */       return null;
/*      */     }
/*      */ 
/*  305 */     Collections.sort(various);
/*      */ 
/*  307 */     File latest = (File)various.get(various.size() - 1);
/*      */ 
/*  309 */     System.out.println("[LaunchFile] Most recent app version is " + latest.getName());
/*      */ 
/*  311 */     return latest;
/*      */   }
/*      */ 
/*      */   public static void setJreVersionForApp(File appFolder, String jreVersion) throws Exception {
/*  315 */     File f = new File(appFolder, "JWrapperUseJRE");
/*  316 */     FileOutputStream fout = new FileOutputStream(f);
/*  317 */     CFriendlyStreamUtils.writeString(fout, jreVersion);
/*  318 */     fout.close();
/*      */   }
/*      */ 
/*      */   public static String getJreVersionForApp(File appFolder) {
/*  322 */     File f = new File(appFolder, "JWrapperUseJRE");
/*  323 */     if (!f.exists()) return null; try
/*      */     {
/*  325 */       FileInputStream fin = new FileInputStream(f);
/*  326 */       String ver = CFriendlyStreamUtils.readString(fin);
/*  327 */       fin.close();
/*  328 */       return ver; } catch (Exception x) {
/*      */     }
/*  330 */     return null;
/*      */   }
/*      */ 
/*      */   public static void runAutoupdatedVirtualAppFrom(File dir, Properties args, File jre, String urlbase, String[] otherParams, String runAsExe)
/*      */     throws Exception
/*      */   {
/*  338 */     File master = dir.getParentFile();
/*  339 */     File gulatest = getLatestVersionOf("JWrapper", master);
/*      */ 
/*  341 */     runAppFrom(gulatest, args, jre, urlbase, otherParams, runAsExe, true, false, false);
/*      */   }
/*      */ 
/*      */   public static void runAutoupdatedVirtualAppFromNoExit(File dir, Properties args, File jre, String urlbase, String[] otherParams, String runAsExe)
/*      */     throws Exception
/*      */   {
/*  348 */     File master = dir.getParentFile();
/*  349 */     File gulatest = getLatestVersionOf("JWrapper", master);
/*      */ 
/*  351 */     runAppFrom(gulatest, args, jre, urlbase, otherParams, runAsExe, false, false, false);
/*      */   }
/*      */ 
/*      */   static boolean runGuSanityCheck(File dir, Properties args, File jre, String urlbase, String[] otherParams, String runAsExe)
/*      */     throws AppDoesNotExistException, Exception
/*      */   {
/*  358 */     System.out.println("[LaunchFile] Launching GU sanity test from " + dir);
/*      */ 
/*  360 */     Properties tmp = new Properties();
/*  361 */     Object[] keys = args.keySet().toArray();
/*  362 */     for (int i = 0; i < keys.length; i++) {
/*  363 */       tmp.setProperty((String)keys[i], (String)args.get(keys[i]));
/*      */     }
/*  365 */     tmp.setProperty("gu_virt_app", GenericUpdater.VAPP_SANITYCHECK);
/*  366 */     tmp.setProperty("show_no_ui", "true");
/*  367 */     tmp.setProperty("launch_in_session", "false");
/*  368 */     tmp.setProperty("launch_elevate", "false");
/*  369 */     tmp.setProperty("launch_elevate_silent", "");
/*      */     try {
/*  371 */       int returnCode = runAppFrom(dir, tmp, jre, urlbase, otherParams, runAsExe, false, false, true);
/*  372 */       System.out.println("[LaunchFile] Sanity check returned: " + returnCode);
/*  373 */       return returnCode == 55;
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  377 */       System.out.println("[LaunchFile] Sanity check failed!");
/*  378 */       t.printStackTrace();
/*  379 */     }return false;
/*      */   }
/*      */ 
/*      */   public static int runHookAppFrom(File dir, String vapp, Properties args, File jre, String urlbase, String[] otherParams, String runAsExe)
/*      */     throws AppDoesNotExistException, Exception
/*      */   {
/*  387 */     System.out.println("[LaunchFile] Launching jw system virtual app " + vapp + " from " + dir);
/*      */     try {
/*  389 */       JWApp jwapp = JWApp.getJWApp(dir, vapp);
/*  390 */       System.out.println("[LaunchFile] JWApp exists, virtual app exists");
/*      */     } catch (Exception x) {
/*  392 */       System.out.println("[LaunchFile] Virtual app " + vapp + " does not exist");
/*  393 */       throw new AppDoesNotExistException();
/*      */     }
/*      */ 
/*  396 */     Properties tmp = new Properties();
/*  397 */     Object[] keys = args.keySet().toArray();
/*  398 */     for (int i = 0; i < keys.length; i++) {
/*  399 */       tmp.setProperty((String)keys[i], (String)args.get(keys[i]));
/*      */     }
/*  401 */     tmp.setProperty("gu_virt_app", vapp);
/*  402 */     return runAppFrom(dir, tmp, jre, urlbase, otherParams, runAsExe, false, false, true);
/*      */   }
/*      */ 
/*      */   public static void runVirtualAppFrom(File dir, Properties args, File jre, String urlbase, String[] otherParams, String runAsExe, boolean transmute)
/*      */     throws Exception
/*      */   {
/*  409 */     runAppFrom(dir, args, jre, urlbase, otherParams, runAsExe, true, transmute, false);
/*      */   }
/*      */ 
/*      */   public static void runVirtualAppFromNoExit(File dir, Properties args, File jre, String urlbase, String[] otherParams, String runAsExe, boolean transmute)
/*      */     throws Exception
/*      */   {
/*  416 */     runAppFrom(dir, args, jre, urlbase, otherParams, runAsExe, false, transmute, false);
/*      */   }
/*      */ 
/*      */   private static int runAppFrom(File dir, Properties args, File jre, String urlbase, String[] otherParams, String runAsExe, boolean mustExit, boolean transmute, boolean waitForRetcode) throws Exception
/*      */   {
/*  421 */     boolean launchingGU = false;
/*      */ 
/*  423 */     String bundleName = pickAppNameFromAppFolder(dir);
/*      */ 
/*  425 */     if (bundleName.equals("JWrapper")) {
/*  426 */       System.out.println("[LaunchFile] Asked to launch GU (we must be in a wrapper)");
/*  427 */       launchingGU = true;
/*      */     } else {
/*  429 */       System.out.println("[LaunchFile] Asked to launch from app bundle " + bundleName + " (" + dir + ")");
/*      */     }
/*      */ 
/*  432 */     LaunchFile launch = getLaunchCommandFor(dir);
/*      */ 
/*  434 */     if ((launch.mustFork) && (transmute)) {
/*  435 */       System.out.println("[LaunchFile] App does not allow transmuting");
/*  436 */       transmute = false;
/*      */     }
/*      */ 
/*  439 */     String virtualApp = args.getProperty("gu_virt_app");
/*  440 */     if (launchingGU)
/*      */     {
/*  442 */       virtualApp = null;
/*      */     }
/*  444 */     else System.out.println("[LaunchFile] Asked to launch virtual app " + virtualApp);
/*      */ 
/*  447 */     if (virtualApp == null) {
/*  448 */       System.out.println("[LaunchFile] Virtual app not specified, GU Launch?");
/*      */     }
/*  450 */     else if (virtualApp.equals(GenericUpdater.VAPP_SANITYCHECK)) {
/*  451 */       System.out.println("[LaunchFile] Running GU sanity check vapp - no JWAppSpec expected");
/*      */     }
/*  453 */     else if (virtualApp.equals(GenericUpdater.VAPP_UNINSTALLER))
/*  454 */       System.out.println("[LaunchFile] Running GU uninstaller vapp - no JWAppSpec expected");
/*      */     else {
/*      */       try
/*      */       {
/*  458 */         jwapp = JWApp.load(dir, virtualApp);
/*      */       }
/*      */       catch (Exception x)
/*      */       {
/*      */         JWApp jwapp;
/*  460 */         System.out.println("[LaunchFile] Unable to load virtual app [" + virtualApp + "] (" + x + "), returning that app does not exist");
/*  461 */         return 999001;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  518 */     System.out.println("[LaunchFile] will launch virtual app " + virtualApp + " for " + dir.getName() + ":\n" + launch);
/*      */ 
/*  522 */     JWLaunchProperties.cleanDir(dir);
/*      */ 
/*  524 */     JWLaunchProperties lprops = new JWLaunchProperties(dir);
/*      */ 
/*  526 */     lprops.addStaticProperty("update_url", urlbase);
/*  527 */     lprops.addStaticProperty("app_dir", dir.getAbsolutePath());
/*      */ 
/*  531 */     Object[] keys = args.keySet().toArray();
/*      */ 
/*  533 */     for (int i = 0; i < keys.length; i++) {
/*  534 */       String key = (String)keys[i];
/*  535 */       lprops.addDynamicProperty(key, args.getProperty(key));
/*      */     }
/*      */ 
/*  540 */     ArrayList list = launch.params;
/*  541 */     list.add(0, lprops.getFileRef());
/*      */ 
/*  547 */     if (otherParams != null) {
/*  548 */       for (int i = 0; i < otherParams.length; i++) {
/*  549 */         list.add(otherParams[i]);
/*      */       }
/*      */     }
/*      */ 
/*  553 */     for (int i = 0; i < list.size(); i++) {
/*  554 */       System.out.println("Param " + i + ": " + list.get(i));
/*      */     }
/*      */ 
/*  558 */     String[] params = new String[list.size()];
/*  559 */     list.toArray(params);
/*      */ 
/*  561 */     String path = dir.getAbsolutePath();
/*  562 */     path = PathUtil.makePathNative(path);
/*  563 */     path = PathUtil.ensureTrailing(path);
/*      */ 
/*  565 */     String[] jars = new String[launch.classpath.size()];
/*  566 */     for (int i = 0; i < jars.length; i++) {
/*  567 */       jars[i] = (path + launch.classpath.get(i));
/*      */     }
/*      */ 
/*  585 */     VMFork fork = new VMFork(launch.mainClass, params, dir);
/*      */ 
/*  587 */     fork.setBaseClasspath(jars);
/*      */ 
/*  589 */     String dirpath = dir.getCanonicalFile().getAbsolutePath();
/*  590 */     if (dirpath.endsWith(File.separator)) {
/*  591 */       dirpath = dirpath.substring(0, dirpath.length() - 1);
/*      */     }
/*      */ 
/*  596 */     if (AUTOCOPY_GU_JW_UTILS_AND_INCLUDE) {
/*  597 */       String utilsJarName = GenericUpdater.autoCopyJWrapperUtils(new File(GU_DIR), dir);
/*  598 */       fork.setClasspathExtras(new String[] { dirpath + File.separator + utilsJarName });
/*      */     }
/*      */ 
/*  601 */     if (jre != null)
/*      */     {
/*      */       File javaExe;
/*  604 */       if (OS.isWindows()) {
/*  605 */         File javaExe = new File(jre, "bin" + File.separator + "javaw.exe");
/*      */ 
/*  607 */         if (runAsExe != null) {
/*  608 */           File appExe = new File(jre, "bin" + File.separator + runAsExe + ".exe");
/*  609 */           if (!appExe.exists()) {
/*  610 */             System.out.println("[LaunchFile] Setting up (windows) exe " + runAsExe);
/*  611 */             FileUtil.copy(javaExe, appExe);
/*      */ 
/*  613 */             File verpatch = GenericUpdater.getVerPatchExe(dir.getParentFile());
/*  614 */             if ((OS.isWindows()) && 
/*  615 */               (verpatch != null) && (verpatch.exists()))
/*      */             {
/*      */               try
/*      */               {
/*  620 */                 System.out.println("[LaunchFile] Running verpatch now...");
/*  621 */                 String[] cmd = { 
/*  622 */                   verpatch.getAbsolutePath(), 
/*  623 */                   appExe.getAbsolutePath(), 
/*  624 */                   "4.0.0.0", 
/*  625 */                   "/s", 
/*  626 */                   "description", 
/*  627 */                   runAsExe, 
/*  628 */                   "/s", 
/*  629 */                   "ProductName", 
/*  630 */                   runAsExe, 
/*  631 */                   "/pv", 
/*  632 */                   "4.0.0.0", 
/*  633 */                   "/s", 
/*  634 */                   "Company", 
/*  635 */                   " ", 
/*  636 */                   "/s", 
/*  637 */                   "FullVersion", 
/*  638 */                   " ", 
/*  639 */                   "/s", 
/*  640 */                   "LegalCopyright", 
/*  641 */                   " " };
/*      */ 
/*  644 */                 Process p = Runtime.getRuntime().exec(cmd);
/*  645 */                 new ProcessPrinter(p, System.out, System.out);
/*  646 */                 int ret = p.waitFor();
/*  647 */                 System.out.println("[LaunchFile] Ran verpatch (" + ret + ")");
/*      */               }
/*      */               catch (Throwable t)
/*      */               {
/*  651 */                 t.printStackTrace();
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  657 */           if (appExe.exists()) {
/*  658 */             System.out.println("[LaunchFile] Using exe " + runAsExe);
/*  659 */             javaExe = appExe;
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  664 */         javaExe = new File(jre, "bin" + File.separator + "java");
/*      */ 
/*  666 */         if (runAsExe != null) {
/*  667 */           File appExe = new File(jre, "bin" + File.separator + runAsExe);
/*  668 */           if (!appExe.exists()) {
/*  669 */             System.out.println("[LaunchFile] Setting up (non windows) exe " + runAsExe);
/*      */ 
/*  672 */             AutoChmodFile.autoChmodFile(javaExe, appExe, false);
/*      */           }
/*      */ 
/*  679 */           if (appExe.exists()) {
/*  680 */             System.out.println("[LaunchFile] Using exe " + runAsExe);
/*  681 */             javaExe = appExe;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  687 */       System.out.println("[LaunchFile] Java Exe: " + javaExe);
/*  688 */       fork.setJavaExe(javaExe.getCanonicalFile().getAbsolutePath());
/*      */     }
/*      */ 
/*  691 */     if (launch.jvmOptions.size() > 0)
/*      */     {
/*  703 */       if (launchingGU) {
/*  704 */         System.out.println("[LaunchFile] Launching GU, will pick up last successful JVM options");
/*      */ 
/*  706 */         File lso = getJvmLastSuccessfulOptionsFile(dir.getParentFile(), jre);
/*  707 */         if (lso.exists())
/*      */         {
/*  709 */           InputStream in = new BufferedInputStream(new FileInputStream(lso));
/*      */           try {
/*  711 */             int N = CFriendlyStreamUtils.readInt(in);
/*  712 */             String[] options = new String[N];
/*  713 */             for (int i = 0; i < N; i++) {
/*  714 */               options[i] = CFriendlyStreamUtils.readString(in);
/*  715 */               System.out.println("[LaunchFile] JVM Option " + i + "/" + N + ": " + options[i]);
/*      */             }
/*      */ 
/*  718 */             fork.setVMSpecificArgs(options);
/*  719 */             System.out.println("[LaunchFile] Set options OK");
/*      */ 
/*  721 */             lprops.addStaticProperty("jvm_options_count", options.length);
/*  722 */             for (int i = 0; i < options.length; i++)
/*  723 */               lprops.addStaticProperty("jvm_options_" + i, options[i]);
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/*  727 */             t.printStackTrace();
/*      */           }
/*      */ 
/*  730 */           FileUtil.robustClose(in);
/*      */         } else {
/*  732 */           System.out.println("No existing JVM options");
/*  733 */           lprops.addStaticProperty("jvm_options_count", "0");
/*      */         }
/*      */       }
/*      */       else {
/*  737 */         String[] vmargs = new String[launch.jvmOptions.size()];
/*      */ 
/*  739 */         launch.jvmOptions.toArray(vmargs);
/*  740 */         fork.setVMSpecificArgs(vmargs);
/*      */ 
/*  742 */         lprops.addStaticProperty("jvm_options_count", vmargs.length);
/*  743 */         for (int i = 0; i < vmargs.length; i++) {
/*  744 */           lprops.addStaticProperty("jvm_options_" + i, vmargs[i]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  767 */     if (transmute) {
/*  768 */       System.out.println("[LaunchFile] Checking JVM folder ready for transmute");
/*  769 */       String myJvmHome = System.getProperty("java.home");
/*      */ 
/*  771 */       System.out.println("[LaunchFile] Current JVM folder: [" + myJvmHome + "]");
/*      */ 
/*  773 */       if (OS.isMacOS()) {
/*  774 */         System.out.println("[LaunchFile] On MacOS so always using same JVM");
/*      */       }
/*      */       else {
/*  777 */         String targetJvmHome = jre.getAbsolutePath();
/*      */ 
/*  779 */         System.out.println("[LaunchFile] Target JVM folder: [" + targetJvmHome + "]");
/*      */ 
/*  782 */         if (new File(myJvmHome).equals(new File(targetJvmHome))) {
/*  783 */           System.out.println("[LaunchFile] JVM folders match, OK to transmute");
/*      */         } else {
/*  785 */           System.out.println("[LaunchFile] JVM folders do not match, unable to transmute, will fork and exit");
/*      */ 
/*  787 */           transmute = false;
/*  788 */           mustExit = true;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  793 */     if (transmute) {
/*  794 */       System.out.println("[LaunchFile] Checking JVM Options ready for transmute");
/*      */ 
/*  796 */       System.out.println("[LaunchFile] Current JVM Options: [" + getMyJvmOptionString() + "]");
/*  797 */       System.out.println("[LaunchFile] Target  JVM Options: [" + launch.getJvmOptionString() + "]");
/*      */ 
/*  799 */       if (getMyJvmOptionString().equals(launch.getJvmOptionString())) {
/*  800 */         System.out.println("[LaunchFile] JVM Options match, OK to transmute");
/*      */       } else {
/*  802 */         System.out.println("[LaunchFile] JVM Options do not match, unable to transmute, will fork and exit");
/*      */ 
/*  804 */         transmute = false;
/*  805 */         mustExit = true;
/*      */       }
/*      */     }
/*      */ 
/*  809 */     ArrayList preJvmExecs = new ArrayList();
/*      */ 
/*  811 */     String insession = args.getProperty("launch_in_session");
/*      */ 
/*  816 */     File supplementaryExecutablesDir = dir;
/*      */ 
/*  818 */     if (launchingGU)
/*      */     {
/*  821 */       File master = dir.getParentFile();
/*  822 */       File appDir = getLatestVersionOf(args.getProperty("app_name"), master);
/*      */ 
/*  824 */       supplementaryExecutablesDir = appDir;
/*      */     }
/*      */ 
/*  827 */     if ((insession != null) && 
/*  828 */       (insession.trim().equalsIgnoreCase("true")))
/*      */     {
/*  830 */       System.out.println("[LaunchFile] Launch must run in session (unable to transmute)");
/*  831 */       transmute = false;
/*      */ 
/*  833 */       if (OS.isWindows()) {
/*  834 */         File elevator = new File(supplementaryExecutablesDir, "session_win.exe");
/*      */ 
/*  836 */         if (!elevator.exists()) {
/*  837 */           throw new IOException("Unable to run in session on Windows - this app has not been set up to launch virtual apps in session");
/*      */         }
/*      */ 
/*  840 */         preJvmExecs.add(elevator.getCanonicalPath());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  847 */     String pipePassword = null;
/*      */ 
/*  849 */     String elevateSilent = args.getProperty("launch_elevate_silent");
/*  850 */     if (elevateSilent == null) elevateSilent = "";
/*  851 */     if (launchingGU)
/*      */     {
/*  853 */       elevateSilent = "";
/*      */     }
/*  855 */     if (OS.isWindows())
/*      */     {
/*  857 */       elevateSilent = "";
/*      */     }
/*      */ 
/*  860 */     String elevate = args.getProperty("launch_elevate");
/*  861 */     if (elevate == null) elevate = "";
/*  862 */     if (launchingGU)
/*      */     {
/*  864 */       elevate = "";
/*      */     }
/*      */ 
/*  867 */     if (elevateSilent.length() > 0)
/*      */     {
/*  869 */       System.out.println("[LaunchFile] Launch must elevate silently via credentials (unable to transmute)");
/*  870 */       transmute = false;
/*      */ 
/*  872 */       if ((OS.isLinux()) || (OS.isMacOS())) {
/*  873 */         preJvmExecs.add("sudo");
/*  874 */         preJvmExecs.add("-S");
/*      */ 
/*  876 */         pipePassword = elevateSilent;
/*      */       }
/*      */     }
/*  879 */     else if (elevate.trim().equalsIgnoreCase("true"))
/*      */     {
/*  881 */       System.out.println("[LaunchFile] Launch must elevate via dialog (unable to transmute)");
/*  882 */       transmute = false;
/*      */ 
/*  884 */       if (OS.isLinux()) {
/*  885 */         if (JWLinuxOS.isGkSudoAvailable()) {
/*  886 */           fork.setMergeAllCommandsAfterPreExec(true);
/*  887 */           fork.setEscapeSpacesAfterPreExec(true);
/*      */ 
/*  889 */           System.out.println("Elevation command set up, using gksudo");
/*  890 */           preJvmExecs.add("gksudo");
/*  891 */         } else if (JWLinuxOS.isKdeSudoAvailable()) {
/*  892 */           fork.setMergeAllCommandsAfterPreExec(true);
/*  893 */           fork.setEscapeSpacesAfterPreExec(true);
/*      */ 
/*  895 */           System.out.println("Elevation command set up, using kdesudo");
/*  896 */           preJvmExecs.add("kdesudo");
/*      */         } else {
/*  898 */           System.out.println("Elevation command set up, using pkexec");
/*  899 */           preJvmExecs.add("pkexec");
/*      */         }
/*  901 */       } else if (OS.isMacOS()) {
/*  902 */         File elevator_nr = new File(supplementaryExecutablesDir, "elev_mac");
/*  903 */         if (!elevator_nr.exists()) {
/*  904 */           throw new IOException("Unable to elevate on Mac - this app has not been set up to launch elevated virtual apps");
/*      */         }
/*      */ 
/*  907 */         File elevator = new File(supplementaryExecutablesDir, "elev_mac_exe");
/*      */ 
/*  910 */         elevator = AutoChmodFile.autoChmodFile(elevator_nr, elevator, false);
/*      */ 
/*  912 */         String prompt = args.getProperty("app_name");
/*      */ 
/*  914 */         if (virtualApp != null) {
/*  915 */           prompt = prompt + " - " + virtualApp;
/*      */         }
/*      */ 
/*  918 */         System.out.println("Elevation command set up, using " + elevator);
/*  919 */         preJvmExecs.add(elevator.getCanonicalPath());
/*  920 */         preJvmExecs.add("--prompt='" + prompt + "'");
/*  921 */       } else if (OS.isWindows()) {
/*  922 */         File elevator = new File(supplementaryExecutablesDir, "elev_win.exe");
/*  923 */         if (!elevator.exists()) {
/*  924 */           throw new IOException("Unable to elevate on Windows - this app has not been set up to launch elevated virtual apps");
/*      */         }
/*  926 */         System.out.println("Elevation command set up, using " + elevator);
/*  927 */         preJvmExecs.add(elevator.getCanonicalPath());
/*      */       } else {
/*  929 */         throw new IOException("Unable to elevate on unknown base OS");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  934 */     String[] preJvmExec = new String[preJvmExecs.size()];
/*  935 */     preJvmExecs.toArray(preJvmExec);
/*      */ 
/*  938 */     System.out.println("[LaunchFile] Storing launch properties");
/*  939 */     lprops.store();
/*      */ 
/*  945 */     if (transmute) {
/*  946 */       System.out.println("[LaunchFile] Transmuting:\n" + fork);
/*      */ 
/*  948 */       VMTransmuter.transmuteInto(fork);
/*      */ 
/*  950 */       System.out.println("[LaunchFile] Transmuted process returned, pausing\n" + fork);
/*      */       while (true) {
/*      */         try
/*      */         {
/*  954 */           Thread.sleep(9999999L);
/*      */         } catch (Throwable t) {
/*  956 */           System.out.println("[LaunchFile] Transmute pause issue: " + t);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  961 */     System.out.println("[LaunchFile] Forking:\n" + fork);
/*      */     Process p;
/*      */     Process p;
/*  964 */     if (preJvmExec == null) {
/*  965 */       p = fork.fork();
/*      */     } else {
/*  967 */       for (int i = 0; i < preJvmExec.length; i++) {
/*  968 */         System.out.println("[LaunchFile] Pre-executing: " + preJvmExec[i]);
/*      */       }
/*  970 */       p = fork.fork(preJvmExec);
/*      */     }
/*      */ 
/*  973 */     if (pipePassword != null) {
/*  974 */       System.out.println("[LaunchFile] sending elevation password");
/*  975 */       OutputStream out = p.getOutputStream();
/*  976 */       out.write((pipePassword + "\n").getBytes());
/*  977 */       out.flush();
/*      */     }
/*      */ 
/*  980 */     ProcessPrinter.printAllOutputToStdout(p);
/*      */ 
/*  982 */     if (waitForRetcode) {
/*  983 */       System.out.println("[LaunchFile] launched, waiting for return code of app");
/*  984 */       int retcode = p.waitFor();
/*  985 */       System.out.println("[LaunchFile] got return code (" + retcode + ")");
/*  986 */       return retcode;
/*      */     }
/*      */ 
/* 1010 */     if (mustExit) {
/* 1011 */       System.out.println("[LaunchFile] *** launched, exiting in 0.5...");
/*      */       try
/*      */       {
/* 1014 */         Thread.sleep(POST_LAUNCH_PRINT_FOR);
/*      */       }
/*      */       catch (Exception localException1) {
/*      */       }
/* 1018 */       if (!InsideApplet.INSIDE_APPLET) System.exit(0); 
/*      */     }
/* 1020 */     else { System.out.println("[LaunchFile] *** launched (without exit, will print output below)"); }
/*      */ 
/*      */ 
/* 1025 */     return -1;
/*      */   }
/*      */ 
/*      */   private LaunchFile(String mainClass, boolean willRequireUrl, String mainClassOnUpdate, String osxIcnsFile, String appName)
/*      */   {
/* 1044 */     this(mainClass, willRequireUrl, mainClassOnUpdate, null, null, null, osxIcnsFile, appName);
/*      */   }
/*      */   public LaunchFile(String mainClass, boolean willRequireUrl, String mainClassOnUpdate, String mainClassForCompatibility, String mainClassPostInstall, String mainClassPreUninstall, String osxIcnsFile, String appName) {
/* 1047 */     this.mainClass = mainClass;
/* 1048 */     this.willRequireURL = willRequireUrl;
/* 1049 */     this.appName = appName;
/*      */ 
/* 1051 */     if (mainClassOnUpdate == null) mainClassOnUpdate = "";
/* 1052 */     this.mainClassOnUpdate = mainClassOnUpdate;
/*      */ 
/* 1054 */     if (mainClassForCompatibility == null) mainClassForCompatibility = "";
/* 1055 */     this.mainClassForCompatibility = mainClassForCompatibility;
/*      */ 
/* 1057 */     if (mainClassPostInstall == null) mainClassPostInstall = "";
/* 1058 */     this.mainClassPostInstall = mainClassPostInstall;
/*      */ 
/* 1060 */     if (mainClassPreUninstall == null) mainClassPreUninstall = "";
/* 1061 */     this.mainClassPreUninstall = mainClassPreUninstall;
/*      */ 
/* 1063 */     if (osxIcnsFile == null) osxIcnsFile = "";
/* 1064 */     this.osxIcnsFile = osxIcnsFile;
/*      */   }
/*      */   public void addClasspath(String s) {
/* 1067 */     this.classpath.add(s);
/*      */   }
/*      */   public void addParam(String s) {
/* 1070 */     this.params.add(s);
/*      */   }
/*      */ 
/*      */   public void addJvmOption(String s)
/*      */   {
/* 1079 */     this.jvmOptions.add(s);
/*      */   }
/*      */ 
/*      */   public void setMustFork(boolean b) {
/* 1083 */     this.mustFork = b;
/*      */   }
/*      */ 
/*      */   public static String getMyJvmOptionString()
/*      */   {
/* 1088 */     String s = " ";
/*      */     try {
/* 1090 */       int n = Integer.parseInt(JWLaunchProperties.getProperty("jvm_options_count"));
/*      */ 
/* 1092 */       for (int i = 0; i < n; i++) {
/* 1093 */         s = s + JWLaunchProperties.getProperty(new StringBuilder("jvm_options_").append(i).toString());
/* 1094 */         s = s + " ";
/*      */       }
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException) {
/*      */     }
/* 1099 */     return s;
/*      */   }
/*      */ 
/*      */   public String getJvmOptionString() {
/* 1103 */     String s = " ";
/* 1104 */     for (int i = 0; i < this.jvmOptions.size(); i++) {
/* 1105 */       s = s + this.jvmOptions.get(i);
/* 1106 */       s = s + " ";
/*      */     }
/* 1108 */     return s;
/*      */   }
/*      */ 
/*      */   public void setMainClassForJreCompatibility(String mainClassForCompatibility) {
/* 1112 */     if (mainClassForCompatibility == null) mainClassForCompatibility = "";
/* 1113 */     this.mainClassForCompatibility = mainClassForCompatibility;
/*      */   }
/*      */ 
/*      */   public void setMainClassForPostInstall(String mainClassForPostInstall) {
/* 1117 */     if (mainClassForPostInstall == null) mainClassForPostInstall = "";
/* 1118 */     this.mainClassPostInstall = mainClassForPostInstall;
/*      */   }
/*      */ 
/*      */   public void setMainClassForPreUninstall(String mainClassForPreUninstall) {
/* 1122 */     if (mainClassForPreUninstall == null) mainClassForPreUninstall = "";
/* 1123 */     this.mainClassPreUninstall = mainClassForPreUninstall;
/*      */   }
/*      */ 
/*      */   public File writeLaunchCommandFile() throws IOException {
/* 1127 */     File f = new File("JWrapperLaunchTemp");
/* 1128 */     OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
/* 1129 */     writeLaunchCommandFile(out);
/* 1130 */     out.close();
/* 1131 */     return f;
/*      */   }
/*      */ 
/*      */   public void writeLaunchCommandFile(OutputStream out) throws IOException {
/* 1135 */     CFriendlyStreamUtils.writeInt(out, 6);
/* 1136 */     CFriendlyStreamUtils.writeString(out, this.mainClass);
/* 1137 */     CFriendlyStreamUtils.writeString(out, this.mainClassOnUpdate);
/* 1138 */     CFriendlyStreamUtils.writeString(out, this.appName);
/* 1139 */     CFriendlyStreamUtils.writeString(out, this.osxIcnsFile);
/* 1140 */     CFriendlyStreamUtils.writeBoolean(out, this.willRequireURL);
/* 1141 */     CFriendlyStreamUtils.writeInt(out, 0);
/* 1142 */     CFriendlyStreamUtils.writeInt(out, 0);
/*      */ 
/* 1146 */     CFriendlyStreamUtils.writeInt(out, this.classpath.size());
/* 1147 */     for (int i = 0; i < this.classpath.size(); i++) {
/* 1148 */       CFriendlyStreamUtils.writeString(out, (String)this.classpath.get(i));
/*      */     }
/*      */ 
/* 1151 */     CFriendlyStreamUtils.writeInt(out, this.params.size());
/* 1152 */     for (int i = 0; i < this.params.size(); i++) {
/* 1153 */       CFriendlyStreamUtils.writeString(out, (String)this.params.get(i));
/*      */     }
/*      */ 
/* 1157 */     CFriendlyStreamUtils.writeString(out, this.mainClassForCompatibility);
/*      */ 
/* 1160 */     CFriendlyStreamUtils.writeString(out, this.mainClassPostInstall);
/* 1161 */     CFriendlyStreamUtils.writeString(out, this.mainClassPreUninstall);
/*      */ 
/* 1164 */     CFriendlyStreamUtils.writeInt(out, this.jvmOptions.size());
/* 1165 */     for (int i = 0; i < this.jvmOptions.size(); i++) {
/* 1166 */       CFriendlyStreamUtils.writeString(out, (String)this.jvmOptions.get(i));
/*      */     }
/*      */ 
/* 1170 */     CFriendlyStreamUtils.writeBoolean(out, this.mustFork);
/*      */   }
/*      */ 
/*      */   public void addToArchive(Archive archive) throws IOException {
/* 1174 */     archive.addFile(writeLaunchCommandFile(), "JWrapperLaunch");
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 1178 */     StringBuffer sb = new StringBuffer();
/* 1179 */     sb.append("Main Class: ").append(this.mainClass).append("\n");
/* 1180 */     sb.append("Main Class on Update: ").append(this.mainClassOnUpdate).append("\n");
/* 1181 */     sb.append("OSX ICNS file: ").append(this.osxIcnsFile).append("\n");
/* 1182 */     for (int i = 0; i < this.classpath.size(); i++) {
/* 1183 */       sb.append("Classpath: ").append(this.classpath.get(i)).append("\n");
/*      */     }
/* 1185 */     for (int i = 0; i < this.params.size(); i++) {
/* 1186 */       sb.append("Param: ").append(this.params.get(i)).append("\n");
/*      */     }
/* 1188 */     return sb.toString();
/*      */   }
/*      */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.updater.LaunchFile
 * JD-Core Version:    0.6.2
 */