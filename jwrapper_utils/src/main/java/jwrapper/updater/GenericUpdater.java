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
/*      */ import java.math.BigInteger;
/*      */ import java.net.ConnectException;
/*      */ import java.net.URL;
/*      */ import java.security.Security;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import javax.net.ssl.SSLHandshakeException;
/*      */ import javax.swing.SwingUtilities;
/*      */ import jwrapper.HeadlessOsxUtil;
/*      */ import jwrapper.HeadlessSwipeLoadUtil;
/*      */ import jwrapper.HeadlessVirtualAppChooserUtil;
/*      */ import jwrapper.IcoPng;
/*      */ import jwrapper.JWParameteriser;
/*      */ import jwrapper.LPUninstallerListener;
/*      */ import jwrapper.SelfDelete;
/*      */ import jwrapper.hidden.JWNativeAPI;
/*      */ import jwrapper.hidden.JWSanityTest;
/*      */ import jwrapper.jwutils.JWGenericOS;
/*      */ import jwrapper.jwutils.JWInstallApp;
/*      */ import jwrapper.jwutils.JWSystem;
/*      */ import jwrapper.jwutils.JWWindowsOS;
/*      */ import jwrapper.logging.ProcessOutputUtil;
/*      */ import jwrapper.logging.StdLogging;
/*      */ import jwrapper.proxy.JWAsyncProxyDetector;
/*      */ import jwrapper.proxy.JWDetectedProxy;
/*      */ import jwrapper.ui.JWLanguage;
/*      */ import jwrapper.ui.SimpleErrorHandler;
/*      */ import utils.encryption.rsa.RSADecryptor;
/*      */ import utils.files.AtomicFileOutputStream;
/*      */ import utils.files.AtomicRenamer;
/*      */ import utils.files.AutoChmodFile;
/*      */ import utils.files.FileUtil;
/*      */ import utils.files.URIUtil;
/*      */ import utils.ostools.OS;
/*      */ import utils.progtools.AutoFetchURL;
/*      */ import utils.stream.CFriendlyStreamUtils;
/*      */ import utils.stream.StreamUtils;
/*      */ import utils.string.Base64;
/*      */ import utils.swing.EventThreadExceptionPrinter;
/*      */ import utils.vm.ProcessPrinter;
/*      */ 
/*      */ public class GenericUpdater
/*      */ {
/*   62 */   public static long VERSION_TIMEOUT = 7000L;
/*      */   private static String PADVER;
/*   66 */   public static String LOG_SOURCE = "JWrapper";
/*      */ 
/*   68 */   public static String TEMP_FOLDER_PREFIX = "JWrapperTemp-";
/*   69 */   public static String SUCCESS_FOLDER_PREFIX = "JWrapper-";
/*   70 */   public static String SUCCESS_FOLDER_SUFFIX = "-complete";
/*      */ 
/*   72 */   public static String JRE_WIN32_APP = "Windows32JRE";
/*   73 */   public static String JRE_WIN64_APP = "Windows64JRE";
/*   74 */   public static String JRE_LIN32_APP = "Linux32JRE";
/*   75 */   public static String JRE_LIN64_APP = "Linux64JRE";
/*      */ 
/*   77 */   public static String FIRST_RUN_FILE = "firstRun";
/*   78 */   public static String VAPP_UNINSTALLER = "JWRAPPER_UNINSTALLER";
/*   79 */   public static String VAPP_SANITYCHECK = "JWRAPPER_SANITYCHECK";
/*      */ 
/*   81 */   public static int MODE = 0;
/*   82 */   public static int MODE_NORMAL = 0;
/*   83 */   public static int MODE_FIRST_RUN_POST_INSTALL = 1;
/*   84 */   public static int MODE_FIRST_RUN_POST_UPDATE = 2;
/*      */ 
/*   86 */   public static boolean AM_SANITY_CHECK = false;
/*   87 */   public static boolean AM_UNINSTALLER = false;
/*      */   private static HeadlessSwipeLoadUtil swu;
/*   90 */   private static boolean alreadyDownloading = false;
/*      */   static boolean matchVersions;
/*      */   static LatestUpdate latestUpdate;
/*      */   static String latestFailedUpdateUiReason;
/*      */   static AtomicRenamer atomicUpdate;
/*      */   static Properties appArgs;
/*      */   static String virtualApp;
/*  883 */   static boolean SHOW_NO_UI = false;
/*      */ 
/*      */   public static String GetVersion()
/*      */   {
/*   93 */     if (PADVER == null) {
/*      */       try {
/*   95 */         PADVER = VersionUtil.padVersion(Integer.parseInt(grabStringFromURLNow(GenericUpdater.class.getResource("/gu_version")).trim()));
/*   96 */         System.out.println("[GenericUpdater] version " + PADVER);
/*      */       } catch (Exception x) {
/*   98 */         x.printStackTrace();
/*   99 */         PADVER = VersionUtil.padVersion(1);
/*  100 */         System.out.println("[GenericUpdater] WARNING NO VERSION FOUND! USING VERSION " + PADVER);
/*      */       }
/*      */     }
/*  103 */     return PADVER;
/*      */   }
/*      */ 
/*      */   public static String grabStringFromURLNow(URL url) throws IOException {
/*  107 */     InputStream vin = new BufferedInputStream(url.openStream());
/*  108 */     String ver = StreamUtils.readAllAsStringUTF8(vin);
/*  109 */     vin.close();
/*  110 */     return ver;
/*      */   }
/*      */ 
/*      */   public static String grabAutoFetchedURL(URL url, long timeout) throws IOException
/*      */   {
/*  115 */     AutoFetchURL fetch = AutoFetchURL.getPreFetchedURL(url);
/*  116 */     if (fetch != null) {
/*  117 */       return fetch.getAsUTF8(timeout);
/*      */     }
/*      */ 
/*  120 */     InputStream vin = new BufferedInputStream(url.openStream());
/*  121 */     String ver = StreamUtils.readAllAsStringUTF8(vin);
/*  122 */     vin.close();
/*  123 */     return ver;
/*      */   }
/*      */ 
/*      */   public static File getVerPatchExe(File master) {
/*  127 */     File latestGU = LaunchFile.getLatestVersionOf("JWrapper", master);
/*  128 */     return new File(latestGU, "verpatch.exe");
/*      */   }
/*      */ 
/*      */   public static String getSubURL(String base, String file) {
/*  132 */     if (base.endsWith("/")) {
/*  133 */       return base + file;
/*      */     }
/*  135 */     return base + "/" + file;
/*      */   }
/*      */ 
/*      */   public static String getLauncherNameFor(String app, boolean linux, boolean arch64, boolean macos)
/*      */   {
/*  140 */     if (macos)
/*  141 */       return app + "MacLauncher.app";
/*  142 */     if (linux) {
/*  143 */       if (arch64) {
/*  144 */         return app + "LinLauncher64";
/*      */       }
/*  146 */       return app + "LinLauncher32";
/*      */     }
/*      */ 
/*  150 */     return app + "WinLauncher.exe";
/*      */   }
/*      */ 
/*      */   public static String getRefusedUpdatesFolderName()
/*      */   {
/*  155 */     return "RefusedUpdates";
/*      */   }
/*      */ 
/*      */   public static String getVersionFileNameFor(String app) {
/*  159 */     return "JWrapper-" + app + "-version.txt";
/*      */   }
/*      */ 
/*      */   public static String getSplashFileNameFor(String app) {
/*  163 */     return "JWrapper-" + app + "-splash.png";
/*      */   }
/*      */ 
/*      */   public static String getIcnsFileNameFor(String app) {
/*  167 */     return "JWrapper-" + app + "-ICNS.icns";
/*      */   }
/*      */ 
/*      */   public static String getUninstallerIcoFileNameFor(String app) {
/*  171 */     return "JWrapper-" + app + "-UninstallerICO.ico";
/*      */   }
/*      */ 
/*      */   public static String getUninstallerIcopngFileNameFor(String app) {
/*  175 */     return "JWrapper-" + app + "-UninstallerICO.icopng";
/*      */   }
/*      */ 
/*      */   public static String getUpdateUrlOverrideFileName() {
/*  179 */     return "UpdateUrlOverride";
/*      */   }
/*      */ 
/*      */   public static String getJreNameOverrideFileName() {
/*  183 */     return "JreNameOverride";
/*      */   }
/*      */ 
/*      */   public static String getArchiveNameFor(String app, String version)
/*      */   {
/*  191 */     return "JWrapper-" + app + "-" + version + "-archive.p2.l2";
/*      */   }
/*      */ 
/*      */   public static String getAppFolderNameFor(String app, String version) {
/*  195 */     return SUCCESS_FOLDER_PREFIX + app + "-" + version + SUCCESS_FOLDER_SUFFIX;
/*      */   }
/*      */ 
/*      */   private static boolean updateNamed(File master, String base, String name, boolean delayFinalCopy, boolean matchVersions, boolean requiresChmod)
/*      */     throws IOException, GenericUpdater.LatestVersionExists
/*      */   {
/*  217 */     latestUpdate = null;
/*  218 */     latestFailedUpdateUiReason = null;
/*      */ 
/*  220 */     LatestUpdate tempUpdate = new LatestUpdate();
/*      */ 
/*  222 */     URL versionURL = new URL(getSubURL(base, getVersionFileNameFor(name)));
/*  223 */     versionURL = URIUtil.tryGetSafeURLFrom(versionURL);
/*      */ 
/*  225 */     System.out.println("[GenericUpdater] Querying " + versionURL + " for " + name + " version");
/*      */     try
/*      */     {
/*  230 */       String text = grabAutoFetchedURL(versionURL, VERSION_TIMEOUT).trim();
/*      */ 
/*  232 */       System.out.println("[GenericUpdater] Got back \"" + text + "\"");
/*      */ 
/*  234 */       intver = Integer.parseInt(text);
/*      */     }
/*      */     catch (SSLHandshakeException x)
/*      */     {
/*      */       int intver;
/*  237 */       x.printStackTrace();
/*  238 */       System.out.println("[GenericUpdater] Unable to query for version " + versionURL + ", will skip and just launch");
/*      */ 
/*  240 */       latestFailedUpdateUiReason = JWLanguage.getString("SERVER_SSL_INVALID");
/*      */ 
/*  242 */       return false;
/*      */     } catch (ConnectException x) {
/*  244 */       x.printStackTrace();
/*  245 */       System.out.println("[GenericUpdater] Unable to query for version " + versionURL + ", will skip and just launch");
/*      */ 
/*  247 */       latestFailedUpdateUiReason = JWLanguage.getString("SERVER_FAIL_CONNECT");
/*      */ 
/*  249 */       return false;
/*      */     } catch (Exception x) {
/*  251 */       x.printStackTrace();
/*  252 */       System.out.println("[GenericUpdater] Unable to query for version " + versionURL + ", will skip and just launch");
/*      */ 
/*  254 */       latestFailedUpdateUiReason = JWLanguage.getString("SERVER_FAIL_UNKNOWN");
/*      */ 
/*  256 */       return false;
/*      */     }
/*      */     int intver;
/*      */     String text;
/*  259 */     String padver = VersionUtil.padVersion(intver);
/*      */ 
/*  261 */     tempUpdate.paddedVersion = padver;
/*      */ 
/*  263 */     System.out.println("[GenericUpdater] Picked out version " + padver);
/*      */ 
/*  265 */     System.out.println("[GenericUpdater] Master folder " + master.getAbsoluteFile().getCanonicalPath());
/*      */ 
/*  267 */     String arcprefix = "JWrapper-" + name + "-" + padver + "-";
/*  268 */     String arcdir = SUCCESS_FOLDER_PREFIX + name + "-" + padver + SUCCESS_FOLDER_SUFFIX;
/*      */ 
/*  270 */     File refused = new File(master, getRefusedUpdatesFolderName());
/*  271 */     if (new File(refused, arcdir).exists()) {
/*  272 */       System.out.println("[GenericUpdater] This version has been downloaded and REFUSED already, we will wait for next update");
/*  273 */       return false;
/*      */     }
/*      */ 
/*  276 */     if (matchVersions) {
/*  277 */       System.out.println("[GenericUpdater] Checking if server-specific version is already available");
/*      */ 
/*  279 */       if (LaunchFile.getSpecificVersionOf(name, padver, master) != null)
/*      */       {
/*  281 */         System.out.println("[GenericUpdater] Specific version exists already (" + arcprefix + "...)");
/*      */ 
/*  283 */         throw new LatestVersionExists(padver);
/*      */       }
/*  285 */       System.out.println("[GenericUpdater] Specific version does not exist, will need to update");
/*      */     }
/*      */     else
/*      */     {
/*  289 */       System.out.println("[GenericUpdater] Checking if server or later version is already available");
/*      */ 
/*  291 */       File latestVer = LaunchFile.getLatestVersionOf(name, master);
/*      */ 
/*  293 */       if (latestVer != null) {
/*  294 */         String latestVersionAvailable = LaunchFile.pickVersionFromAppFolder(latestVer);
/*      */ 
/*  296 */         if (LaunchFile.versionIsSameOrLater(padver, latestVersionAvailable))
/*      */         {
/*  301 */           System.out.println("[GenericUpdater] Version exists already (" + arcprefix + "...)");
/*  302 */           throw new LatestVersionExists(latestVersionAvailable);
/*      */         }
/*      */ 
/*  305 */         System.out.println("[GenericUpdater] Versions exist but are older than server version, will need to update");
/*      */       }
/*      */       else
/*      */       {
/*  309 */         System.out.println("[GenericUpdater] No versions present, will need to update");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  316 */     if (!alreadyDownloading)
/*      */     {
/*  318 */       swu.swipeSmallTo("SmallDownload");
/*  319 */       swu.waitForAllSwipes();
/*      */     }
/*  321 */     swu.showFiniteProgress();
/*      */ 
/*  325 */     File latest = LaunchFile.getLatestVersionOf(name, master);
/*  326 */     if (latest != null) {
/*  327 */       System.out.println("[GenericUpdater] Existing version of app (" + latest.getName() + "), will try to run its update class");
/*      */ 
/*  329 */       File stopAsking = new File(latest, "StopAskingAboutVersion-" + padver);
/*      */ 
/*  331 */       if (stopAsking.exists())
/*      */       {
/*  333 */         System.out.println("[GenericUpdater] Have previously been told to stop asking about this version, will just run existing version without running update class");
/*  334 */         return false;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  339 */         swu.waitForAllSwipes();
/*      */         int decision;
/*      */         try {
/*  343 */           decision = LaunchFile.runHookAppFrom(latest, LaunchFile.JW_VAPP_UPDATE_APP, appArgs, null, base, new String[] { padver }, name + "Update");
/*      */         }
/*      */         catch (AppDoesNotExistException x)
/*      */         {
/*      */           int decision;
/*  345 */           System.out.println("[GenericUpdater] No virtual app for updates, will update and run");
/*  346 */           decision = 50;
/*      */         }
/*      */ 
/*  350 */         if (decision == 52) {
/*  351 */           System.out.println("[GenericUpdater] Update app told to cancel launch and exit");
/*      */ 
/*  353 */           ProcessOutputUtil.logProcessResult(LOG_SOURCE, 3);
/*  354 */           ProcessOutputUtil.logProcessError(LOG_SOURCE, "The app bundle update virtual application returned that the launch should be cancelled");
/*      */ 
/*  356 */           System.exit(0); } else {
/*  357 */           if (decision == 51) {
/*  358 */             System.out.println("[GenericUpdater] Update app told to just run existing version");
/*  359 */             return false;
/*  360 */           }if (decision == 53) {
/*  361 */             System.out.println("[GenericUpdater] Update app told to just run existing version and stop asking for this version");
/*  362 */             stopAsking.createNewFile();
/*  363 */             return false;
/*  364 */           }if (decision == 50)
/*  365 */             System.out.println("[GenericUpdater] Update app told to update and run");
/*      */           else
/*  367 */             System.out.println("[GenericUpdater] Update app told to do unrecognised decision " + decision + ", will update and run");
/*      */         }
/*      */       } catch (Exception x) {
/*  370 */         x.printStackTrace();
/*  371 */         System.out.println("[GenericUpdater] Existing version of app (" + latest.getName() + ") seems to be broken? will update + run");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  376 */     File temp = new File(master, TEMP_FOLDER_PREFIX + System.currentTimeMillis() + "-" + master.list().length);
/*  377 */     temp.mkdirs();
/*      */ 
/*  379 */     System.out.println("[GenericUpdater] Downloading into " + temp.getName());
/*      */ 
/*  381 */     String arcname = getArchiveNameFor(name, padver);
/*      */ 
/*  384 */     URL archive = new URL(getSubURL(base, arcname));
/*  385 */     archive = URIUtil.tryGetSafeURLFrom(archive);
/*      */ 
/*  387 */     File arctemp = new File(temp, arcname);
/*      */ 
/*  389 */     System.out.println("[GenericUpdater] Downloading archive from " + archive);
/*      */     try
/*      */     {
/*  392 */       swu.downloadUrlWithProgress(arctemp, archive);
/*      */     } catch (Exception x) {
/*  394 */       x.printStackTrace();
/*      */ 
/*  396 */       System.out.println("[GenericUpdater] Unable to download app " + x + " will show error and exit");
/*      */ 
/*  398 */       doNoInternetFailAndExit("An error occurred while download the app update bundle", JWLanguage.getString("ERROR_DOWNLOAD"), x);
/*      */     }
/*      */ 
/*  401 */     System.out.println("[GenericUpdater] Downloaded archive");
/*      */ 
/*  403 */     swu.showInfiniteProgress();
/*      */ 
/*  406 */     unpackArchive(temp, arctemp, base);
/*      */ 
/*  408 */     if (requiresChmod) {
/*  409 */       System.out.println("[GenericUpdater] Setting " + temp.getName() + " accessible for all users");
/*  410 */       JWGenericOS.setReadableForAllUsers(temp, true);
/*  411 */       JWGenericOS.setWritableForAllUsers(temp, false);
/*      */     }
/*      */ 
/*  414 */     System.out.println("[GenericUpdater] Moving " + temp.getName() + " to " + arcdir);
/*      */ 
/*  416 */     File finalFolder = new File(master, arcdir);
/*  417 */     if (delayFinalCopy) {
/*  418 */       addDelayedFinalCopy(temp, finalFolder);
/*  419 */       tempUpdate.tempUpdateFolder = temp;
/*  420 */       tempUpdate.finalUpdateFolder = finalFolder;
/*      */     } else {
/*  422 */       temp.renameTo(finalFolder);
/*  423 */       tempUpdate.tempUpdateFolder = finalFolder;
/*  424 */       tempUpdate.finalUpdateFolder = finalFolder;
/*      */     }
/*      */ 
/*  427 */     latestUpdate = tempUpdate;
/*      */ 
/*  429 */     return true;
/*      */   }
/*      */ 
/*      */   private static void completeAnyAtomicRenames(File folder)
/*      */   {
/*  449 */     atomicUpdate = new AtomicRenamer(new File(folder, "JWUpdateARInstruction"));
/*      */     try
/*      */     {
/*  452 */       atomicUpdate.completeAnyFailedAtomicMultiRenameNow(10000L);
/*      */     } catch (IOException x) {
/*  454 */       System.out.println("[GenericUpdater] Error in atomic rename? " + x);
/*  455 */       x.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void addDelayedFinalCopy(File from, File to)
/*      */   {
/*  475 */     atomicUpdate.addRenameInstruction(from, to);
/*      */ 
/*  479 */     System.out.println("[GenericUpdater] Set up for atomic rename: " + to + " (from: " + from + ")");
/*      */   }
/*      */ 
/*      */   private static void cancelAllOutstandingFinalCopies()
/*      */     throws IOException
/*      */   {
/*  485 */     System.out.println("[GenericUpdater] Cancelling all atomic renames now");
/*      */ 
/*  487 */     atomicUpdate.cancelAtomicMultiRename();
/*      */   }
/*      */ 
/*      */   private static void doAllOutstandingFinalCopies() throws IOException {
/*  491 */     System.out.println("[GenericUpdater] Performing all atomic renames now");
/*      */ 
/*  493 */     atomicUpdate.performAtomicMultiRenameNow(10000L);
/*      */   }
/*      */ 
/*      */   private static void doNoInternetFailAndExit(String errorMessage, String uiMessage, Throwable t)
/*      */   {
/*  515 */     System.out.println("[GenericUpdater] Showing app download failure image and exiting after 6 seconds...");
/*      */ 
/*  518 */     swu.swipeSmallTo("SmallNoInternet");
/*  519 */     swu.hideProgress();
/*  520 */     if (uiMessage != null) {
/*  521 */       swu.setMessage(uiMessage);
/*      */     }
/*  523 */     swu.waitForAllSwipes();
/*      */     try
/*      */     {
/*  526 */       Thread.sleep(6000L);
/*      */     } catch (Exception localException) {
/*      */     }
/*  529 */     System.out.println("[GenericUpdater] Exiting");
/*      */ 
/*  532 */     ProcessOutputUtil.logProcessResult(LOG_SOURCE, 2);
/*  533 */     if (t != null) ProcessOutputUtil.logProcessTrace(LOG_SOURCE, t);
/*  534 */     ProcessOutputUtil.logProcessError(LOG_SOURCE, errorMessage);
/*      */ 
/*  536 */     System.exit(0); } 
/*      */   // ERROR //
/*      */   public static void unpackArchive(File app, File archive, String base) throws IOException { // Byte code:
/*      */     //   0: new 235	java/io/File
/*      */     //   3: dup
/*      */     //   4: new 154	java/lang/StringBuilder
/*      */     //   7: dup
/*      */     //   8: invokespecial 663	java/lang/StringBuilder:<init>	()V
/*      */     //   11: aload_1
/*      */     //   12: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*      */     //   15: ldc_w 664
/*      */     //   18: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   21: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   24: invokespecial 666	java/io/File:<init>	(Ljava/lang/String;)V
/*      */     //   27: astore_3
/*      */     //   28: aload_1
/*      */     //   29: aload_3
/*      */     //   30: invokestatic 667	jwrapper/LzmaUtil:decompress	(Ljava/io/File;Ljava/io/File;)Ljava/io/File;
/*      */     //   33: pop
/*      */     //   34: goto +46 -> 80
/*      */     //   37: astore 4
/*      */     //   39: new 183	java/io/IOException
/*      */     //   42: dup
/*      */     //   43: new 154	java/lang/StringBuilder
/*      */     //   46: dup
/*      */     //   47: ldc_w 673
/*      */     //   50: invokespecial 158	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*      */     //   53: aload 4
/*      */     //   55: invokevirtual 675	java/lang/Exception:getMessage	()Ljava/lang/String;
/*      */     //   58: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   61: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   64: invokespecial 678	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   67: astore 5
/*      */     //   69: aload 5
/*      */     //   71: aload 4
/*      */     //   73: invokevirtual 679	java/io/IOException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
/*      */     //   76: pop
/*      */     //   77: aload 5
/*      */     //   79: athrow
/*      */     //   80: new 185	java/io/BufferedInputStream
/*      */     //   83: dup
/*      */     //   84: new 683	java/io/FileInputStream
/*      */     //   87: dup
/*      */     //   88: aload_3
/*      */     //   89: invokespecial 685	java/io/FileInputStream:<init>	(Ljava/io/File;)V
/*      */     //   92: invokespecial 193	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
/*      */     //   95: astore 4
/*      */     //   97: ldc_w 686
/*      */     //   100: newarray byte
/*      */     //   102: astore 5
/*      */     //   104: aload 4
/*      */     //   106: invokestatic 687	utils/stream/CFriendlyStreamUtils:readInt	(Ljava/io/InputStream;)I
/*      */     //   109: istore 6
/*      */     //   111: iload 6
/*      */     //   113: iconst_1
/*      */     //   114: if_icmpne +62 -> 176
/*      */     //   117: aload 4
/*      */     //   119: invokestatic 693	utils/stream/CFriendlyStreamUtils:readString	(Ljava/io/InputStream;)Ljava/lang/String;
/*      */     //   122: astore 7
/*      */     //   124: getstatic 148	java/lang/System:out	Ljava/io/PrintStream;
/*      */     //   127: new 154	java/lang/StringBuilder
/*      */     //   130: dup
/*      */     //   131: ldc_w 696
/*      */     //   134: invokespecial 158	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*      */     //   137: aload 7
/*      */     //   139: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   142: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   145: invokevirtual 168	java/io/PrintStream:println	(Ljava/lang/String;)V
/*      */     //   148: aload 7
/*      */     //   150: invokestatic 698	utils/files/PathUtil:makePathNative	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   153: astore 7
/*      */     //   155: new 235	java/io/File
/*      */     //   158: dup
/*      */     //   159: aload_0
/*      */     //   160: aload 7
/*      */     //   162: invokespecial 239	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
/*      */     //   165: astore 8
/*      */     //   167: aload 8
/*      */     //   169: invokevirtual 500	java/io/File:mkdirs	()Z
/*      */     //   172: pop
/*      */     //   173: goto -69 -> 104
/*      */     //   176: iload 6
/*      */     //   178: ifeq +9 -> 187
/*      */     //   181: iload 6
/*      */     //   183: iconst_2
/*      */     //   184: if_icmpne -80 -> 104
/*      */     //   187: aload 4
/*      */     //   189: invokestatic 693	utils/stream/CFriendlyStreamUtils:readString	(Ljava/io/InputStream;)Ljava/lang/String;
/*      */     //   192: astore 7
/*      */     //   194: getstatic 148	java/lang/System:out	Ljava/io/PrintStream;
/*      */     //   197: new 154	java/lang/StringBuilder
/*      */     //   200: dup
/*      */     //   201: ldc_w 703
/*      */     //   204: invokespecial 158	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*      */     //   207: aload 7
/*      */     //   209: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   212: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   215: invokevirtual 168	java/io/PrintStream:println	(Ljava/lang/String;)V
/*      */     //   218: aload 4
/*      */     //   220: invokestatic 705	utils/stream/CFriendlyStreamUtils:readLong	(Ljava/io/InputStream;)J
/*      */     //   223: lstore 8
/*      */     //   225: new 235	java/io/File
/*      */     //   228: dup
/*      */     //   229: aload_0
/*      */     //   230: aload 7
/*      */     //   232: invokespecial 239	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
/*      */     //   235: astore 10
/*      */     //   237: new 709	java/io/BufferedOutputStream
/*      */     //   240: dup
/*      */     //   241: new 711	java/io/FileOutputStream
/*      */     //   244: dup
/*      */     //   245: aload 10
/*      */     //   247: invokespecial 713	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
/*      */     //   250: invokespecial 714	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;)V
/*      */     //   253: astore 11
/*      */     //   255: goto +35 -> 290
/*      */     //   258: astore 12
/*      */     //   260: aload 10
/*      */     //   262: invokevirtual 500	java/io/File:mkdirs	()Z
/*      */     //   265: pop
/*      */     //   266: aload 10
/*      */     //   268: invokevirtual 717	java/io/File:delete	()Z
/*      */     //   271: pop
/*      */     //   272: new 709	java/io/BufferedOutputStream
/*      */     //   275: dup
/*      */     //   276: new 711	java/io/FileOutputStream
/*      */     //   279: dup
/*      */     //   280: aload 10
/*      */     //   282: invokespecial 713	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
/*      */     //   285: invokespecial 714	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;)V
/*      */     //   288: astore 11
/*      */     //   290: lconst_0
/*      */     //   291: lstore 12
/*      */     //   293: iconst_0
/*      */     //   294: istore 14
/*      */     //   296: goto +48 -> 344
/*      */     //   299: aload 4
/*      */     //   301: aload 5
/*      */     //   303: iconst_0
/*      */     //   304: aload 5
/*      */     //   306: arraylength
/*      */     //   307: lload 8
/*      */     //   309: lload 12
/*      */     //   311: lsub
/*      */     //   312: l2i
/*      */     //   313: invokestatic 720	java/lang/Math:min	(II)I
/*      */     //   316: invokevirtual 726	java/io/InputStream:read	([BII)I
/*      */     //   319: istore 14
/*      */     //   321: iload 14
/*      */     //   323: ifle +21 -> 344
/*      */     //   326: aload 11
/*      */     //   328: aload 5
/*      */     //   330: iconst_0
/*      */     //   331: iload 14
/*      */     //   333: invokevirtual 730	java/io/OutputStream:write	([BII)V
/*      */     //   336: lload 12
/*      */     //   338: iload 14
/*      */     //   340: i2l
/*      */     //   341: ladd
/*      */     //   342: lstore 12
/*      */     //   344: iload 14
/*      */     //   346: iconst_m1
/*      */     //   347: if_icmpeq +24 -> 371
/*      */     //   350: lload 12
/*      */     //   352: lload 8
/*      */     //   354: lcmp
/*      */     //   355: iflt -56 -> 299
/*      */     //   358: goto +13 -> 371
/*      */     //   361: astore 15
/*      */     //   363: aload 11
/*      */     //   365: invokevirtual 736	java/io/OutputStream:close	()V
/*      */     //   368: aload 15
/*      */     //   370: athrow
/*      */     //   371: aload 11
/*      */     //   373: invokevirtual 736	java/io/OutputStream:close	()V
/*      */     //   376: getstatic 148	java/lang/System:out	Ljava/io/PrintStream;
/*      */     //   379: new 154	java/lang/StringBuilder
/*      */     //   382: dup
/*      */     //   383: ldc_w 737
/*      */     //   386: invokespecial 158	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*      */     //   389: aload 7
/*      */     //   391: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   394: ldc_w 739
/*      */     //   397: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   400: lload 12
/*      */     //   402: invokevirtual 493	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
/*      */     //   405: ldc 247
/*      */     //   407: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   410: lload 8
/*      */     //   412: invokevirtual 493	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
/*      */     //   415: ldc_w 619
/*      */     //   418: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   421: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   424: invokevirtual 168	java/io/PrintStream:println	(Ljava/lang/String;)V
/*      */     //   427: iload 6
/*      */     //   429: iconst_2
/*      */     //   430: if_icmpne -326 -> 104
/*      */     //   433: new 235	java/io/File
/*      */     //   436: dup
/*      */     //   437: aload_0
/*      */     //   438: new 154	java/lang/StringBuilder
/*      */     //   441: dup
/*      */     //   442: aload 7
/*      */     //   444: iconst_0
/*      */     //   445: aload 7
/*      */     //   447: invokevirtual 741	java/lang/String:length	()I
/*      */     //   450: iconst_4
/*      */     //   451: isub
/*      */     //   452: invokevirtual 745	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   455: invokestatic 253	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   458: invokespecial 158	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*      */     //   461: ldc_w 749
/*      */     //   464: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   467: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   470: invokespecial 239	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
/*      */     //   473: astore 15
/*      */     //   475: aload 10
/*      */     //   477: aload 15
/*      */     //   479: invokevirtual 561	java/io/File:renameTo	(Ljava/io/File;)Z
/*      */     //   482: pop
/*      */     //   483: getstatic 148	java/lang/System:out	Ljava/io/PrintStream;
/*      */     //   486: new 154	java/lang/StringBuilder
/*      */     //   489: dup
/*      */     //   490: ldc_w 751
/*      */     //   493: invokespecial 158	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*      */     //   496: aload 7
/*      */     //   498: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   501: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   504: invokevirtual 168	java/io/PrintStream:println	(Ljava/lang/String;)V
/*      */     //   507: invokestatic 753	jwrapper/pack200/Pack200Decompressor:initDecompressor	()Ljwrapper/pack200/Pack200Decompressor;
/*      */     //   510: aload 15
/*      */     //   512: invokevirtual 759	jwrapper/pack200/Pack200Decompressor:decompressDirectory	(Ljava/io/File;)V
/*      */     //   515: goto -411 -> 104
/*      */     //   518: astore 5
/*      */     //   520: getstatic 148	java/lang/System:out	Ljava/io/PrintStream;
/*      */     //   523: ldc_w 762
/*      */     //   526: invokevirtual 168	java/io/PrintStream:println	(Ljava/lang/String;)V
/*      */     //   529: aload 4
/*      */     //   531: invokevirtual 202	java/io/InputStream:close	()V
/*      */     //   534: goto +13 -> 547
/*      */     //   537: astore 16
/*      */     //   539: aload 4
/*      */     //   541: invokevirtual 202	java/io/InputStream:close	()V
/*      */     //   544: aload 16
/*      */     //   546: athrow
/*      */     //   547: aload_1
/*      */     //   548: invokevirtual 717	java/io/File:delete	()Z
/*      */     //   551: pop
/*      */     //   552: aload_3
/*      */     //   553: invokevirtual 717	java/io/File:delete	()Z
/*      */     //   556: pop
/*      */     //   557: return
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   28	34	37	java/lang/Exception
/*      */     //   237	255	258	java/io/FileNotFoundException
/*      */     //   296	361	361	finally
/*      */     //   97	518	518	java/io/EOFException
/*      */     //   97	529	537	finally } 
/*      */   private static boolean updateGenericUpdater(File master, String base, boolean needChmod) throws IOException { try { return updateNamed(master, base, "JWrapper", true, false, needChmod);
/*      */     } catch (LatestVersionExists x) {
/*      */     }
/*  635 */     return false; }
/*      */ 
/*      */   private static boolean updateJavaApp(File master, String base, String app, boolean needChmod)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  642 */       return updateNamed(master, base, app, true, matchVersions, needChmod);
/*      */     } catch (LatestVersionExists x) {
/*      */     }
/*  645 */     return false;
/*      */   }
/*      */ 
/*      */   private static boolean updateJRE(File master, String base, String app, boolean serverOrLaterIsOK, boolean needChmod)
/*      */     throws IOException, GenericUpdater.LatestVersionExists
/*      */   {
/*  652 */     return updateNamed(master, base, app, true, !serverOrLaterIsOK, needChmod);
/*      */   }
/*      */ 
/*      */   static String autoCopyJWrapperUtils(File gudir, File dir) throws IOException {
/*  656 */     return "jwrapper_utils.jar";
/*      */   }
/*      */ 
/*      */   public static File[] getUninstallExtras()
/*      */   {
/*      */     File[] uninst_extras;
/*      */     File[] uninst_extras;
/*  695 */     if (OS.isWindows()) {
/*  696 */       uninst_extras = new File[] { 
/*  697 */         new JWWindowsOS().getAppStartMenuFolder() };
/*      */     }
/*      */     else
/*      */     {
/*      */       File[] uninst_extras;
/*  699 */       if (OS.isLinux())
/*      */       {
/*  701 */         uninst_extras = new File[0];
/*      */       }
/*  703 */       else uninst_extras = new File[0];
/*      */     }
/*  705 */     return uninst_extras;
/*      */   }
/*      */ 
/*      */   public static void removeAllStandardShortcuts(JWApp[] jwapps, boolean removeMacOsApplicationLaunchers) throws IOException {
/*  709 */     if (OS.isWindows()) {
/*  710 */       JWWindowsOS win = new JWWindowsOS();
/*      */ 
/*  714 */       File appmenu = win.getAppStartMenuFolder();
/*      */ 
/*  716 */       FileUtil.deleteDir(appmenu);
/*      */     }
/*  718 */     else if (OS.isLinux()) {
/*  719 */       System.out.println("[GenericUpdater] PostInstall App has requested standard setup of shortcuts etc");
/*      */ 
/*  721 */       for (int i = 0; i < jwapps.length; i++) {
/*  722 */         JWApp jwa = jwapps[i];
/*      */ 
/*  724 */         System.out.println("[GenericUpdater] Removing shortcut to " + jwa.getUserVisibleName());
/*      */ 
/*  726 */         JWInstallApp.removeAppShortcut(jwa.getUserVisibleName());
/*      */ 
/*  728 */         System.out.println("[GenericUpdater] Removing shortcut to " + jwa.getUserVisibleName());
/*      */       }
/*      */ 
/*  732 */       JWInstallApp.removeAppShortcut("Uninstall " + JWSystem.getAppBundleName());
/*      */     }
/*  734 */     else if (removeMacOsApplicationLaunchers)
/*      */     {
/*  736 */       for (int i = 0; i < jwapps.length; i++) {
/*  737 */         JWApp jwa = jwapps[i];
/*      */ 
/*  740 */         File launcher = JWSystem.getLauncherLocationForVirtualApp(new File("/Applications"), JWSystem.getAppBundleName());
/*      */ 
/*  742 */         FileUtil.deleteDir(launcher);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void loadDynamicImage(boolean canOverrideSplash, String dynamicBase64PNGSplash, File splashFile)
/*      */   {
/*      */     try
/*      */     {
/*  752 */       if ((canOverrideSplash) && (dynamicBase64PNGSplash != null) && (dynamicBase64PNGSplash.length() > 0))
/*      */       {
/*  754 */         System.out.println("[GenericUpdater] Saving custom slash image to " + splashFile);
/*  755 */         byte[] dynamicSplash = Base64.base64ToByteArray(dynamicBase64PNGSplash);
/*  756 */         FileOutputStream fout = new FileOutputStream(splashFile);
/*      */         try
/*      */         {
/*  759 */           fout.write(dynamicSplash);
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*  764 */           t.printStackTrace();
/*      */           try
/*      */           {
/*  770 */             fout.close(); } catch (Throwable localThrowable1) {  } } finally { try { fout.close(); } catch (Throwable localThrowable2) {  } } try { fout.close(); }
/*      */         catch (Throwable localThrowable3)
/*      */         {
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  779 */       t.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void setupAllStandardShortcuts(String app, File appdir, JWApp[] jwapps, Properties osxLaunchProperties, boolean createMacOsApplicationLauncher) throws IOException
/*      */   {
/*  785 */     setupAllStandardShortcuts(app, appdir, jwapps, osxLaunchProperties, createMacOsApplicationLauncher, null);
/*      */   }
/*      */ 
/*      */   public static void setupAllStandardShortcuts(String app, File appdir, JWApp[] jwapps, Properties osxLaunchProperties, boolean createMacOsApplicationLauncher, File targetFolder) throws IOException {
/*      */     try {
/*  790 */       if (!OS.isMacOS()) {
/*  791 */         System.out.println("[GenericUpdater] PostInstall App has requested standard setup of shortcuts etc");
/*      */ 
/*  793 */         for (int i = 0; i < jwapps.length; i++) {
/*  794 */           JWApp jwa = jwapps[i];
/*      */ 
/*  796 */           System.out.println("[GenericUpdater] Creating shortcut to " + jwa.getUserVisibleName());
/*      */ 
/*  798 */           String id = JWLaunchProperties.getProperty("windows_app_id");
/*  799 */           if ((id != null) && (id.length() > 0))
/*  800 */             JWInstallApp.addAppShortcutWithIDInFolder(jwa.getUserVisibleName(), jwa.getFilesystemName(), jwa.getICOFile(), 0, id, targetFolder);
/*      */           else {
/*  802 */             JWInstallApp.addAppShortcutInFolder(jwa.getUserVisibleName(), jwa.getFilesystemName(), jwa.getICOFile(), 0, targetFolder);
/*      */           }
/*  804 */           System.out.println("[GenericUpdater] Created shortcut to " + jwa.getUserVisibleName());
/*      */         }
/*      */ 
/*  809 */         JWInstallApp.addUninstallerShortcutInFolder("Uninstall " + JWSystem.getAppBundleName(), targetFolder);
/*      */       }
/*  811 */       else if (createMacOsApplicationLauncher)
/*      */       {
/*  813 */         Properties tmp = new Properties();
/*  814 */         Object[] keys = osxLaunchProperties.keySet().toArray();
/*  815 */         for (int i = 0; i < keys.length; i++) {
/*  816 */           tmp.setProperty((String)keys[i], (String)osxLaunchProperties.get(keys[i]));
/*      */         }
/*      */ 
/*  819 */         if (targetFolder == null) {
/*  820 */           targetFolder = new File("/Applications");
/*      */         }
/*  822 */         for (int i = 0; i < jwapps.length; i++) {
/*  823 */           JWApp jwa = jwapps[i];
/*      */ 
/*  825 */           tmp.setProperty("gu_virt_app", jwa.getUserVisibleName());
/*      */ 
/*  828 */           File launcher = saveLauncherShortcutForVirtualApp(app, appdir, targetFolder, JWSystem.getAppBundleName(), JWSystem.getMyAppName(), tmp, false);
/*      */ 
/*  830 */           JWGenericOS.setWritableForAllUsers(launcher, true);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Exception x) {
/*  835 */       x.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static File saveLauncherShortcutForVirtualApp(String app, File appdir, File dir, String shortcutName, String virtualAppName, Properties launchProperties, boolean elevateToAdmin) throws IOException {
/*  840 */     dir.mkdirs();
/*      */ 
/*  842 */     File target = JWSystem.getLauncherLocationForVirtualApp(dir, shortcutName);
/*  843 */     System.out.println("[GenericUpdater] Shortcut target is " + target);
/*      */ 
/*  845 */     File launcherSrc = new File(appdir, getLauncherNameFor(app, OS.isLinux(), OS.isLinux64bit(), OS.isMacOS()));
/*  846 */     System.out.println("[GenericUpdater] Shortcut launcher src is " + launcherSrc);
/*      */ 
/*  849 */     if (OS.isWindows())
/*      */     {
/*  851 */       FileUtil.copyFileOrDir(launcherSrc, target);
/*      */     }
/*      */     else
/*      */     {
/*  855 */       AutoChmodFile.autoChmodFile(launcherSrc, target, true);
/*      */     }
/*  857 */     System.out.println("[GenericUpdater] Copied launcher to shortcut");
/*      */     File paramsFile;
/*      */     File paramsFile;
/*  860 */     if (OS.isMacOS())
/*  861 */       paramsFile = new File(target, "Contents" + File.separator + "Resources" + File.separator + "AppParams.excludefromsigning");
/*      */     else {
/*  863 */       paramsFile = target;
/*      */     }
/*      */ 
/*  866 */     JWGenericOS.setWritableForAllUsers(paramsFile, true);
/*      */ 
/*  871 */     JWParameteriser jwp = new JWParameteriser();
/*      */ 
/*  873 */     jwp.setParameters(launchProperties, paramsFile, true);
/*      */ 
/*  875 */     System.out.println("[GenericUpdater] Parameterised launcher created OK");
/*      */ 
/*  877 */     return target;
/*      */   }
/*      */ 
/*      */   public static void main(String[] margs)
/*      */   {
/*  886 */     System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
/*      */ 
/*  888 */     System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
/*  889 */     System.setProperty("sun.net.client.defaultReadTimeout", "30000");
/*      */     try
/*      */     {
/*  892 */       Security.setProperty("networkaddress.cache.ttl", "30");
/*  893 */       Security.setProperty("networkaddress.cache.negative.ttl", "10");
/*      */     }
/*      */     catch (Throwable localThrowable1)
/*      */     {
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  914 */       String[] preargs = margs;
/*      */ 
/*  916 */       margs = JWLaunchProperties.argsToNormalArgs(margs);
/*      */ 
/*  918 */       String guWorkingDir = JWLaunchProperties.getProperty("app_dir");
/*      */ 
/*  921 */       File master = new File(guWorkingDir).getParentFile();
/*      */ 
/*  924 */       StdLogging.startDebugLogging(master, "GenericUpdater");
/*      */ 
/*  926 */       if ((JWLaunchProperties.getProperty("debug_logging").length() > 0) || (StdLogging.beforeDebugLogUntil(JWLaunchProperties.getProperty("debug_logging_until")))) {
/*  927 */         StdLogging.startLogging(master, "GenericUpdater");
/*      */       }
/*      */ 
/*  931 */       System.out.println("[GenericUpdater] Starting");
/*      */ 
/*  933 */       System.out.println(JWLaunchProperties.INSTANCE);
/*      */ 
/*  935 */       SHOW_NO_UI = JWLaunchProperties.getProperty("show_no_ui").equals("true");
/*      */ 
/*  937 */       System.out.println("Hiding UI on MacOS: " + SHOW_NO_UI);
/*      */ 
/*  952 */       for (int i = 0; i < preargs.length; i++) {
/*  953 */         System.out.println("preARG " + i + ": " + preargs[i]);
/*      */       }
/*      */ 
/*  956 */       for (int i = 0; i < margs.length; i++) {
/*  957 */         System.out.println("ARG " + i + ": " + margs[i]);
/*      */       }
/*      */ 
/*  960 */       String vapp = JWLaunchProperties.getProperty("gu_virt_app");
/*  961 */       if (vapp != null) {
/*  962 */         vapp = vapp.trim();
/*  963 */         if (vapp.length() > 0) {
/*  964 */           virtualApp = vapp;
/*      */ 
/*  966 */           System.out.println("Virtual app specified in launch properties: " + virtualApp);
/*      */         }
/*      */       }
/*      */ 
/*  970 */       for (int i = 0; i < 20; i++)
/*      */       {
/*  972 */         if (margs.length > 0) {
/*  973 */           if (margs[0].equalsIgnoreCase("JWVAPP")) {
/*  974 */             virtualApp = margs[1];
/*      */ 
/*  977 */             System.out.println("Virtual app specified in args: " + virtualApp);
/*      */ 
/*  979 */             String[] tmp = new String[margs.length - 2];
/*  980 */             System.arraycopy(margs, 2, tmp, 0, tmp.length);
/*  981 */             margs = tmp; } else {
/*  982 */             if (!margs[0].equalsIgnoreCase("JWHEADLESS")) break;
/*  983 */             SHOW_NO_UI = true;
/*      */ 
/*  985 */             System.out.println("Headless specified in args");
/*      */ 
/*  987 */             String[] tmp = new String[margs.length - 1];
/*  988 */             System.arraycopy(margs, 1, tmp, 0, tmp.length);
/*  989 */             margs = tmp;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1000 */       JWNativeAPI.loadLibraryFrom(new File(guWorkingDir));
/*      */ 
/* 1002 */       String base = JWLaunchProperties.getProperty("update_url");
/*      */ 
/* 1004 */       boolean noUpdateURL = base.equals("X");
/*      */ 
/* 1006 */       if (base.startsWith("http://0.0.254.254")) {
/* 1007 */         noUpdateURL = true;
/*      */       }
/*      */ 
/* 1010 */       if (noUpdateURL) {
/* 1011 */         System.out.println("[GenericUpdater] NO Update URL (" + base + ")");
/*      */       }
/*      */ 
/* 1014 */       boolean dynamicUpdateURL = JWLaunchProperties.isDynamicUpdateURL();
/*      */ 
/* 1016 */       String jreAppName = JWLaunchProperties.getProperty("jre_name");
/* 1017 */       String app = JWLaunchProperties.getProperty("app_name");
/* 1018 */       int minSplashMS = Integer.parseInt(JWLaunchProperties.getProperty("min_splash_ms"));
/* 1019 */       boolean canOverrideSplash = false;
/* 1020 */       String canOverrideSplashStr = JWLaunchProperties.getProperty("can_override_splash");
/* 1021 */       if ((canOverrideSplashStr != null) && (canOverrideSplashStr.equals("1")))
/* 1022 */         canOverrideSplash = true;
/* 1023 */       matchVersions = JWLaunchProperties.getProperty("match_versions").equalsIgnoreCase("true");
/* 1024 */       String installType = JWLaunchProperties.getProperty("install_type");
/* 1025 */       String dynamicBase64PNGSplash = JWLaunchProperties.getProperty("splash_image");
/*      */ 
/* 1028 */       appArgs = JWLaunchProperties.getAsProperties();
/*      */ 
/* 1030 */       System.out.println("[GenericUpdater] Loaded properties");
/*      */       try
/*      */       {
/* 1033 */         EventThreadExceptionPrinter.setup();
/*      */       } catch (Throwable localThrowable2) {
/*      */       }
/* 1036 */       System.out.println("[GenericUpdaterMain] Done event exception setup");
/*      */       try
/*      */       {
/* 1040 */         if (OS.isWindowsVistaOrAbove())
/*      */         {
/* 1042 */           String id = JWLaunchProperties.getProperty("windows_app_id");
/* 1043 */           if ((id != null) && (id.length() > 0))
/*      */           {
/* 1045 */             System.out.println("[GenericUpdater] Setting app ID to " + id);
/* 1046 */             JWWindowsOS windowsOS = new JWWindowsOS();
/* 1047 */             windowsOS.setWindowsAppID(id);
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/* 1053 */         t.printStackTrace();
/*      */       }
/*      */ 
/* 1057 */       if (OS.isMacOS())
/*      */       {
/* 1059 */         System.out.println("[GenericUpdaterMain] Setting OSX app name");
/*      */ 
/* 1061 */         HeadlessOsxUtil.setOSXAppName(app);
/*      */ 
/* 1064 */         if (!SHOW_NO_UI)
/*      */         {
/* 1066 */           SwingUtilities.invokeAndWait(new Runnable()
/*      */           {
/*      */             public void run() {
/* 1069 */               System.out.println("[GenericUpdaterMain] We invoke this via SwingUtilities to load native libraries as we can't do it later for some reason.");
/*      */             }
/*      */           });
/* 1072 */           System.out.println("[GenericUpdaterMain] Showing application");
/*      */ 
/* 1074 */           JWNativeAPI.getInstance().showApplication();
/*      */ 
/* 1076 */           HeadlessOsxUtil.requestForeground();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1081 */       if (virtualApp != null) appArgs.setProperty("gu_virt_app", virtualApp);
/*      */ 
/* 1083 */       if (virtualApp != null) {
/* 1084 */         if (virtualApp.equals(VAPP_SANITYCHECK)) {
/* 1085 */           System.out.println("[GenericUpdaterMain] doing SANITY CHECK only, will not launch or update");
/* 1086 */           AM_SANITY_CHECK = true;
/*      */         }
/* 1088 */         if (virtualApp.equals(VAPP_UNINSTALLER)) {
/* 1089 */           System.out.println("[GenericUpdaterMain] we are UNINSTALLER, will not launch or update");
/* 1090 */           AM_UNINSTALLER = true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1096 */       JWLaunchProperties.cleanDir(new File(guWorkingDir));
/*      */ 
/* 1098 */       File override = new File(JWApp.getJWAppsFolder(new File(guWorkingDir).getParentFile()), getUpdateUrlOverrideFileName());
/* 1099 */       if (override.exists()) {
/* 1100 */         String orig = base;
/*      */ 
/* 1102 */         System.out.println("[GenericUpdaterMain] (base url is being overridden by app)");
/* 1103 */         base = FileUtil.readFileAsStringUTF8(override.getAbsolutePath());
/*      */ 
/* 1106 */         appArgs.setProperty("update_url", base);
/*      */ 
/* 1109 */         JWLaunchProperties.overrideProperty("update_url", base);
/*      */ 
/* 1111 */         System.out.println("[GenericUpdaterMain] Base URL: " + base + " (overridden from " + orig + ")");
/*      */       } else {
/* 1113 */         System.out.println("[GenericUpdaterMain] Base URL: " + base);
/*      */       }
/*      */ 
/* 1116 */       System.out.println("[GenericUpdaterMain] My GU App Folder: " + guWorkingDir);
/* 1117 */       System.out.println("[GenericUpdaterMain] JRE App: " + jreAppName);
/* 1118 */       System.out.println("[GenericUpdaterMain] App: " + app);
/* 1119 */       System.out.println("[GenericUpdaterMain] Min Splash (ms): " + minSplashMS);
/* 1120 */       System.out.println("[GenericUpdaterMain] Can Override Splash: " + canOverrideSplash);
/* 1121 */       if (dynamicBase64PNGSplash != null)
/*      */       {
/* 1123 */         if (dynamicBase64PNGSplash.length() > 100)
/* 1124 */           System.out.println("[GenericUpdaterMain] Custom Splash: " + dynamicBase64PNGSplash.substring(0, 99) + "...");
/*      */         else {
/* 1126 */           System.out.println("[GenericUpdaterMain] Custom Splash: " + dynamicBase64PNGSplash);
/*      */         }
/*      */       }
/* 1129 */       File firstRun = new File(guWorkingDir + File.separator + FIRST_RUN_FILE);
/* 1130 */       if (firstRun.exists()) {
/* 1131 */         File master = new File(guWorkingDir).getParentFile();
/*      */ 
/* 1134 */         if (LaunchFile.getAllVersionsOf("JWrapper", master).length > 1)
/*      */         {
/* 1136 */           MODE = MODE_FIRST_RUN_POST_UPDATE;
/* 1137 */           System.out.println("[GenericUpdaterMain] Mode: first run post update");
/*      */ 
/* 1139 */           appArgs.setProperty("first_run_post_update", "true");
/*      */         }
/*      */         else {
/* 1142 */           MODE = MODE_FIRST_RUN_POST_INSTALL;
/* 1143 */           System.out.println("[GenericUpdaterMain] Mode: first run post install");
/*      */ 
/* 1145 */           appArgs.setProperty("first_run_post_install", "true");
/*      */         }
/*      */       } else {
/* 1148 */         System.out.println("[GenericUpdaterMain] Mode: normal run");
/*      */       }
/*      */ 
/* 1161 */       LaunchFile.setAutoCopyJWUtilsAndInclude(guWorkingDir);
/*      */ 
/* 1163 */       File me = new File(guWorkingDir);
/* 1164 */       File master = me.getParentFile();
/*      */ 
/* 1166 */       String appVersionURL = getSubURL(base, getVersionFileNameFor(app));
/*      */ 
/* 1169 */       File jwapps = JWApp.getJWAppsFolder(master);
/* 1170 */       File jreNameOverride = new File(jwapps, getJreNameOverrideFileName());
/*      */       try
/*      */       {
/* 1174 */         AtomicFileOutputStream.prepareForReading(jreNameOverride);
/*      */       } catch (IOException localIOException1) {
/*      */       }
/* 1177 */       if (jreNameOverride.exists())
/*      */       {
/* 1179 */         System.out.println("[GenericUpdater] Reading JRE name override file");
/*      */ 
/* 1182 */         FileInputStream in = new FileInputStream(jreNameOverride);
/* 1183 */         jreAppName = CFriendlyStreamUtils.readString(in);
/* 1184 */         in.close();
/*      */ 
/* 1186 */         System.out.println("[GenericUpdater] Read JRE name " + jreAppName);
/*      */       }
/*      */       else
/*      */       {
/* 1190 */         jwapps.mkdirs();
/*      */ 
/* 1192 */         System.out.println("[GenericUpdater] Writing JRE name override file");
/*      */ 
/* 1196 */         AtomicFileOutputStream fout = new AtomicFileOutputStream(jreNameOverride);
/* 1197 */         CFriendlyStreamUtils.writeString(fout, jreAppName);
/* 1198 */         fout.close();
/*      */ 
/* 1200 */         System.out.println("[GenericUpdater] Wrote JRE name override file");
/*      */       }
/*      */ 
/* 1208 */       File appsSharedConfigFolder = new File(master, "JWAppsSharedConfig");
/* 1209 */       if (!appsSharedConfigFolder.exists())
/*      */       {
/* 1211 */         System.out.println("[GenericUpdater] Creating JWAppsSharedConfig folder");
/* 1212 */         appsSharedConfigFolder.mkdirs();
/*      */       }
/*      */ 
/* 1215 */       File wrapperProxy = new File(appsSharedConfigFolder, "DetectedProxy");
/* 1216 */       File detectedProxies = new File(appsSharedConfigFolder, "DetectedProxies");
/* 1217 */       File userProxies = new File(appsSharedConfigFolder, "AppProxies");
/* 1218 */       File credentialsFile = new File(appsSharedConfigFolder, "ProxyCredentials");
/* 1219 */       File lastProxy = new File(appsSharedConfigFolder, "LastProxy");
/* 1220 */       if (!lastProxy.exists()) {
/* 1221 */         lastProxy.createNewFile();
/*      */       }
/* 1223 */       if ((installType.equalsIgnoreCase("perm_all")) && (MODE == MODE_FIRST_RUN_POST_INSTALL)) {
/* 1224 */         System.out.println("[GenericUpdater] Setting proxy files accessible for all users");
/* 1225 */         JWGenericOS.setWritableForAllUsers(wrapperProxy, false);
/* 1226 */         JWGenericOS.setWritableForAllUsers(detectedProxies, false);
/* 1227 */         JWGenericOS.setWritableForAllUsers(credentialsFile, false);
/* 1228 */         JWGenericOS.setWritableForAllUsers(userProxies, false);
/* 1229 */         JWGenericOS.setWritableForAllUsers(lastProxy, false);
/*      */       }
/*      */ 
/* 1233 */       if (!noUpdateURL)
/*      */       {
/* 1235 */         URL url = new URL(appVersionURL);
/* 1236 */         JWAsyncProxyDetector.detectAndSetProxyFor(app, appsSharedConfigFolder, url, 5000);
/*      */         try
/*      */         {
/* 1240 */           AutoFetchURL.preFetchUrl(getSubURL(base, getVersionFileNameFor("JWrapper")), 150);
/* 1241 */           AutoFetchURL.preFetchUrl(appVersionURL, 150);
/*      */         } catch (Exception x) {
/* 1243 */           x.printStackTrace();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1249 */       String[] remparams = margs;
/*      */ 
/* 1255 */       completeAnyAtomicRenames(master);
/*      */ 
/* 1257 */       long splashUntil = 0L;
/*      */       try
/*      */       {
/* 1262 */         File latest = LaunchFile.getLatestVersionOf(app, master);
/*      */ 
/* 1266 */         if (latest != null) {
/* 1267 */           System.out.println("[GenericUpdaterMain] Using splash + logo from latest app version: " + latest);
/*      */ 
/* 1272 */           Object logoPNG = HeadlessOsxUtil.loadImageFromICNS(new File(latest, getIcnsFileNameFor(app)));
/*      */ 
/* 1274 */           System.out.println("[GenericUpdaterMain] Loaded logo");
/*      */ 
/* 1276 */           if (!SHOW_NO_UI) {
/* 1277 */             HeadlessOsxUtil.setOSXAppDockImage(logoPNG);
/*      */ 
/* 1280 */             swu = new HeadlessSwipeLoadUtil();
/*      */ 
/* 1283 */             File splashImage = new File(latest, getSplashFileNameFor(app));
/* 1284 */             loadDynamicImage(canOverrideSplash, dynamicBase64PNGSplash, splashImage);
/*      */ 
/* 1287 */             swu.setBigTo(swu.loadImage(splashImage));
/* 1288 */             swu.setSmallTo(null);
/*      */ 
/* 1291 */             System.out.println("[GenericUpdaterMain] Opening splash frame now");
/* 1292 */             swu.makeFrame(app, logoPNG);
/* 1293 */             splashUntil = System.currentTimeMillis() + minSplashMS;
/*      */           }
/*      */         }
/*      */         else {
/* 1297 */           System.out.println("[GenericUpdaterMain] No latest app, will use own stored splash + logo");
/*      */ 
/* 1303 */           System.out.println("[GenericUpdaterMain] Processing ICNS file");
/* 1304 */           Object logoPNG = HeadlessOsxUtil.loadImageFromICNS(new File(me, getIcnsFileNameFor(app)));
/*      */ 
/* 1306 */           if (!SHOW_NO_UI) {
/* 1307 */             HeadlessOsxUtil.setOSXAppDockImage(logoPNG);
/*      */ 
/* 1310 */             swu = new HeadlessSwipeLoadUtil();
/*      */ 
/* 1313 */             File splashImage = new File(me, getSplashFileNameFor(app));
/* 1314 */             loadDynamicImage(canOverrideSplash, dynamicBase64PNGSplash, splashImage);
/*      */ 
/* 1316 */             swu.setBigTo(swu.loadImage(splashImage));
/* 1317 */             swu.setSmallTo(null);
/*      */ 
/* 1325 */             System.out.println("[GenericUpdaterMain] Opening splash frame now");
/* 1326 */             System.out.flush();
/* 1327 */             swu.makeFrame(app, logoPNG);
/*      */ 
/* 1329 */             splashUntil = System.currentTimeMillis() + minSplashMS;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/* 1339 */         System.out.println("[GenericUpdaterMain] Splash popup failed, headless?");
/* 1340 */         t.printStackTrace();
/*      */       }
/*      */ 
/* 1343 */       if (swu == null) {
/* 1344 */         System.out.println("[GenericUpdaterMain] Creating SWU");
/*      */ 
/* 1346 */         swu = new HeadlessSwipeLoadUtil();
/* 1347 */         System.out.println("[GenericUpdaterMain] Created SWU");
/*      */       }
/*      */ 
/* 1351 */       LatestUpdate appUpdate = null;
/* 1352 */       LatestUpdate guUpdate = null;
/* 1353 */       LatestUpdate jreUpdate = null;
/*      */ 
/* 1355 */       if (AM_SANITY_CHECK) {
/* 1356 */         System.out.println("[GenericUpdaterMain] Not checking for updates (sanity check)");
/*      */ 
/* 1358 */         if (matchVersions) {
/* 1359 */           System.out.println("[GenericUpdaterMain] Launch failed because we require matched versions but the latest version has not been sanity checked and installed yet (we are that sanity check) but sanity check will return OK");
/* 1360 */           JWSanityTest.exitAndReturnSanityCheckOK();
/*      */         }
/*      */       }
/* 1363 */       else if (AM_UNINSTALLER) {
/* 1364 */         System.out.println("[GenericUpdaterMain] Not checking for updates (uninstaller)");
/*      */       }
/* 1366 */       else if (!noUpdateURL)
/*      */       {
/*      */         try {
/* 1369 */           System.out.println("[GenericUpdaterMain] Checking for updates...");
/*      */ 
/* 1371 */           if (updateJavaApp(master, base, app, installType.equalsIgnoreCase("perm_all")))
/*      */           {
/* 1373 */             System.out.println("[GenericUpdaterMain] Updated App");
/*      */ 
/* 1375 */             appUpdate = latestUpdate;
/*      */ 
/* 1377 */             boolean mustDownloadJRE = true;
/*      */ 
/* 1380 */             File latestJRE = LaunchFile.getLatestVersionOf(jreAppName, master);
/*      */ 
/* 1382 */             if (latestJRE != null) {
/* 1383 */               if (OS.isMacOS()) {
/* 1384 */                 System.out.println("[GenericUpdater] JRE present but we are on MacOS so must use the provided (system-copied) JRE");
/* 1385 */                 mustDownloadJRE = false;
/*      */               } else { System.out.println("[GenericUpdaterMain] Checking for app compatibility with latest JRE: " + latestJRE.getName());
/*      */ 
/* 1389 */                 String latestJreVer = LaunchFile.pickVersionFromAppFolder(latestJRE);
/*      */                 int comp;
/*      */                 try {
/* 1393 */                   comp = LaunchFile.runHookAppFrom(appUpdate.tempUpdateFolder, LaunchFile.JW_VAPP_COMPATIBILITY_APP, appArgs, latestJRE, base, remparams, app + "ECompatibility");
/*      */                 }
/*      */                 catch (AppDoesNotExistException x)
/*      */                 {
/*      */                   int comp;
/* 1396 */                   System.out.println("[GenericUpdater] No virtual app for checking JRE compatibility, will assume incompatible");
/* 1397 */                   comp = 73;
/*      */                 }
/*      */ 
/* 1400 */                 if (comp == 72)
/*      */                 {
/* 1402 */                   System.out.println("[GenericUpdater] JRE is compatible (" + comp + "), App will be set to use JRE " + latestJreVer + " in the future (will not download latest JRE)");
/*      */ 
/* 1404 */                   LaunchFile.setJreVersionForApp(appUpdate.tempUpdateFolder, latestJreVer);
/*      */ 
/* 1406 */                   mustDownloadJRE = false;
/*      */                 } else {
/* 1408 */                   System.out.println("[GenericUpdater] JRE is not (explicitly) compatible with App so must download JRE");
/* 1409 */                   mustDownloadJRE = true;
/*      */                 } }
/*      */             }
/* 1412 */             else if (OS.isMacOS()) {
/* 1413 */               System.out.println("[GenericUpdater] No JRE but we are on MacOS so use the system JRE");
/* 1414 */               mustDownloadJRE = false;
/*      */             } else {
/* 1416 */               System.out.println("[GenericUpdater] No latest JRE found so will download JRE");
/*      */             }
/*      */ 
/* 1422 */             if (updateGenericUpdater(master, base, installType.equalsIgnoreCase("perm_all")))
/*      */             {
/* 1424 */               System.out.println("[GenericUpdaterMain] Updated GU");
/*      */ 
/* 1426 */               guUpdate = latestUpdate;
/*      */ 
/* 1440 */               autoCopyJWrapperUtils(guUpdate.tempUpdateFolder, appUpdate.tempUpdateFolder);
/*      */             }
/*      */             else {
/* 1443 */               System.out.println("[GenericUpdaterMain] GU not updated");
/*      */             }
/*      */ 
/* 1446 */             if (mustDownloadJRE) {
/* 1447 */               System.out.println("[GenericUpdater] App cannot use an existing JRE so will update to a new one");
/*      */               try
/*      */               {
/* 1451 */                 if (updateJRE(master, base, jreAppName, false, installType.equalsIgnoreCase("perm_all"))) {
/* 1452 */                   System.out.println("[GenericUpdaterMain] Updated JRE");
/*      */ 
/* 1456 */                   jreUpdate = latestUpdate;
/*      */ 
/* 1460 */                   String path = jreUpdate.tempUpdateFolder.getAbsolutePath();
/* 1461 */                   if (!path.endsWith(File.separator)) {
/* 1462 */                     path = path + File.separator;
/*      */                   }
/* 1464 */                   path = path + "bin" + File.separator + "java";
/* 1465 */                   if (OS.isWindows()) {
/* 1466 */                     path = path + "w.exe";
/*      */                   }
/*      */ 
/*      */                   try
/*      */                   {
/* 1473 */                     System.out.println("[GenericUpdaterMain] Regenerating jsa... [" + path + "]");
/*      */ 
/* 1475 */                     Process p = Runtime.getRuntime().exec(new String[] { path, "-Xshare:dump" });
/* 1476 */                     new ProcessPrinter(p, System.out, System.out);
/* 1477 */                     p.waitFor();
/*      */                   } catch (Exception x) {
/* 1479 */                     System.out.println("[GenericUpdaterMain] Couldn't generate jsa: " + x);
/*      */                   }
/*      */ 
/* 1483 */                   System.out.println("[GenericUpdaterMain] Setting App " + appUpdate.paddedVersion + " to use JRE " + jreAppName + "-" + jreUpdate.paddedVersion);
/* 1484 */                   LaunchFile.setJreVersionForApp(appUpdate.tempUpdateFolder, jreUpdate.paddedVersion);
/*      */                 }
/*      */                 else {
/* 1487 */                   System.out.println("[GenericUpdaterMain] JRE not updated (due to some failure, not because it already exists), App is not explicitly compatible with latest, so cannot run.  Will fail and exit.");
/*      */ 
/* 1489 */                   doNoInternetFailAndExit("The JRE update failed but the app is not explicitly compatible with the latest available JRE", 
/* 1490 */                     JWLanguage.getString("ERROR_JRE_UPDATE_FAILED"), 
/* 1491 */                     null);
/*      */                 }
/*      */               } catch (LatestVersionExists exver) {
/* 1494 */                 System.out.println("[GenericUpdaterMain] JRE paired with app for download already exists (may not have a compatibility app to automatically pick up latest or this may be an older app version), app will use its paired JRE");
/*      */ 
/* 1496 */                 System.out.println("[GenericUpdaterMain] Setting App " + appUpdate.paddedVersion + " to use JRE " + jreAppName + "-" + exver.version);
/* 1497 */                 LaunchFile.setJreVersionForApp(appUpdate.tempUpdateFolder, exver.version);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 1504 */             boolean updateSafe = true;
/*      */ 
/* 1506 */             if (guUpdate != null)
/*      */             {
/* 1509 */               System.out.println("[GenericUpdaterMain] GU has been updated, will run sanity check on " + guUpdate.tempUpdateFolder.getName());
/*      */               boolean sanityResult;
/*      */               boolean sanityResult;
/* 1513 */               if (jreUpdate != null) {
/* 1514 */                 System.out.println("[GenericUpdaterMain] JRE has been updated, will test GU using new JRE " + jreUpdate.tempUpdateFolder.getName());
/*      */ 
/* 1517 */                 sanityResult = LaunchFile.runGuSanityCheck(guUpdate.tempUpdateFolder, appArgs, jreUpdate.tempUpdateFolder, base, remparams, "JWSanCheck");
/*      */               }
/*      */               else {
/* 1520 */                 System.out.println("[GenericUpdaterMain] JRE has not been updated, will test GU using current JRE " + JWSystem.getMyJreHome().getName());
/*      */ 
/* 1523 */                 sanityResult = LaunchFile.runGuSanityCheck(guUpdate.tempUpdateFolder, appArgs, JWSystem.getMyJreHome(), base, remparams, "JWSanCheck");
/*      */               }
/*      */ 
/* 1528 */               if (sanityResult) {
/* 1529 */                 System.out.println("[GenericUpdaterMain] Sanity check returned OK");
/*      */               } else {
/* 1531 */                 System.out.println("[GenericUpdaterMain] Sanity check FAILED - will have to refuse this update");
/*      */ 
/* 1533 */                 updateSafe = false;
/*      */ 
/* 1535 */                 cancelAllOutstandingFinalCopies();
/* 1536 */                 File refused = new File(master, getRefusedUpdatesFolderName());
/* 1537 */                 refused.mkdirs();
/*      */ 
/* 1553 */                 new File(refused, appUpdate.finalUpdateFolder.getName()).createNewFile();
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 1572 */             if (updateSafe) doAllOutstandingFinalCopies(); 
/*      */           }
/* 1573 */           else if (latestFailedUpdateUiReason != null) {
/* 1574 */             System.out.println("[GenericUpdaterMain] Failure: unable to update java app " + latestFailedUpdateUiReason);
/*      */           }
/*      */         } catch (IOException x) {
/* 1577 */           System.out.println("[GenericUpdaterMain] Unable to update");
/* 1578 */           x.printStackTrace();
/*      */         }
/*      */       } else {
/* 1581 */         System.out.println("[GenericUpdaterMain] Unable to update (this app does not use an update URL)");
/*      */       }
/*      */ 
/* 1584 */       swu.showInfiniteProgress();
/*      */ 
/* 1586 */       boolean failDueToServerUnavailable = false;
/*      */       File latest;
/* 1590 */       if ((matchVersions) && (!AM_UNINSTALLER)) {
/*      */         File latest;
/*      */         try {
/* 1593 */           URL url2 = new URL(appVersionURL);
/* 1594 */           url2 = URIUtil.tryGetSafeURLFrom(url2);
/* 1595 */           String text = grabAutoFetchedURL(url2, VERSION_TIMEOUT).trim();
/* 1596 */           System.out.println("[GenericUpdater] Server app version matching required");
/* 1597 */           System.out.println("[GenericUpdater] Got back server app version of \"" + text + "\"");
/*      */ 
/* 1599 */           int intver = Integer.parseInt(text);
/* 1600 */           String serverPadver = VersionUtil.padVersion(intver);
/*      */ 
/* 1602 */           latest = LaunchFile.getSpecificVersionOf(app, serverPadver, master);
/*      */         }
/*      */         catch (NumberFormatException x)
/*      */         {
/*      */           File latest;
/* 1605 */           System.out.println("[GenericUpdater] Unable to parse server app version, assuming something is wrong with the URL right now, must fail");
/* 1606 */           x.printStackTrace();
/*      */ 
/* 1608 */           failDueToServerUnavailable = true;
/*      */ 
/* 1610 */           latest = LaunchFile.getLatestVersionOf(app, master);
/*      */         }
/*      */         catch (IOException x)
/*      */         {
/*      */           File latest;
/* 1613 */           System.out.println("[GenericUpdater] Problem fetching server version, assuming something is wrong with the URL right now, must fail");
/* 1614 */           x.printStackTrace();
/*      */ 
/* 1618 */           failDueToServerUnavailable = true;
/*      */ 
/* 1620 */           latest = LaunchFile.getLatestVersionOf(app, master);
/*      */         }
/*      */       } else {
/* 1623 */         if (AM_UNINSTALLER)
/* 1624 */           System.out.println("[GenericUpdater] Server app version matching not required (uninstaller), will run latest");
/*      */         else {
/* 1626 */           System.out.println("[GenericUpdater] Server app version matching not required, will run latest");
/*      */         }
/* 1628 */         latest = LaunchFile.getLatestVersionOf(app, master);
/*      */       }
/*      */ 
/* 1631 */       if (AM_SANITY_CHECK) {
/* 1632 */         System.out.println("[GenericUpdaterMain] Avoiding any further setup as we are sanity check, will exit and return OK");
/*      */ 
/* 1634 */         JWSanityTest.exitAndReturnSanityCheckOK();
/*      */       }
/*      */ 
/* 1637 */       if (latest == null) {
/* 1638 */         System.out.println("[GenericUpdater] No " + app + " apps found to launch!");
/* 1639 */         if (latestFailedUpdateUiReason != null)
/* 1640 */           doNoInternetFailAndExit("Unexpectedly, there were no extracted app bundles found even after the update process", 
/* 1641 */             latestFailedUpdateUiReason, 
/* 1642 */             null);
/*      */         else
/* 1644 */           doNoInternetFailAndExit("Unexpectedly, there were no extracted app bundles found even after the update process", 
/* 1645 */             JWLanguage.getString("ERROR_NO_APP_TO_LAUNCH"), 
/* 1646 */             null);
/*      */       }
/*      */       else {
/* 1649 */         System.out.println("[GenericUpdater] Will launch app " + latest.getName());
/*      */       }
/*      */       File useJRE;
/*      */       File useJRE;
/* 1659 */       if (OS.isMacOS())
/*      */       {
/* 1661 */         System.out.println("[GenericUpdater] No specific JRE required for MacOS");
/* 1662 */         useJRE = null;
/*      */       }
/*      */       else
/*      */       {
/* 1667 */         String specifiedVer = LaunchFile.getJreVersionForApp(latest);
/*      */         String jreVer;
/*      */         String jreVer;
/* 1669 */         if (specifiedVer == null) {
/* 1670 */           System.out.println("[GenericUpdater] No JRE specified for App " + latest.getName() + " so far");
/*      */ 
/* 1672 */           File latestJRE = LaunchFile.getLatestVersionOf(jreAppName, master);
/*      */ 
/* 1674 */           if (latestJRE == null) {
/* 1675 */             System.out.println("[GenericUpdater] No JRE (" + jreAppName + ") found to launch!");
/* 1676 */             doNoInternetFailAndExit("Unexpectedly, there were no extracted JRE bundles found even after the update process", 
/* 1677 */               JWLanguage.getString("ERROR_NO_RUNTIME_TO_LAUNCH"), 
/* 1678 */               null);
/*      */           }
/*      */ 
/* 1681 */           String latestJreVer = LaunchFile.pickVersionFromAppFolder(latestJRE);
/*      */ 
/* 1683 */           if (Long.parseLong(latestJreVer) == 0L) {
/* 1684 */             System.out.println("[GenericUpdater] Latest JRE is an existing system JRE, must check compatibility");
/*      */             int comp;
/*      */             try {
/* 1688 */               comp = LaunchFile.runHookAppFrom(latest, LaunchFile.JW_VAPP_COMPATIBILITY_APP, appArgs, latestJRE, base, remparams, app + "SCompatibility");
/*      */             }
/*      */             catch (AppDoesNotExistException x)
/*      */             {
/*      */               int comp;
/* 1690 */               System.out.println("[GenericUpdater] No virtual app for checking JRE compatibility, will assume incompatible");
/* 1691 */               comp = 73;
/*      */             }
/*      */             String jreVer;
/* 1695 */             if (comp == 72)
/*      */             {
/* 1697 */               System.out.println("[GenericUpdater] JRE is compatible (" + comp + "), App will be set to use JRE " + latestJreVer + " in the future");
/*      */ 
/* 1699 */               LaunchFile.setJreVersionForApp(latest, latestJreVer);
/* 1700 */               jreVer = latestJreVer;
/*      */             }
/*      */             else
/*      */             {
/* 1704 */               System.out.println("[GenericUpdater] JRE is NOT compatible (" + comp + "), will download updated JRE now");
/*      */               String jreVer;
/*      */               try
/*      */               {
/*      */                 String jreVer;
/* 1708 */                 if (updateJRE(master, base, jreAppName, false, installType.equalsIgnoreCase("perm_all"))) {
/* 1709 */                   System.out.println("[GenericUpdater] Updated JRE");
/*      */ 
/* 1714 */                   jreUpdate = latestUpdate;
/*      */ 
/* 1716 */                   System.out.println("[GenericUpdater] Setting App " + latest.getName() + " to use JRE " + jreAppName + "-" + jreUpdate.paddedVersion);
/*      */ 
/* 1720 */                   doAllOutstandingFinalCopies();
/*      */ 
/* 1722 */                   LaunchFile.setJreVersionForApp(latest, jreUpdate.paddedVersion);
/* 1723 */                   jreVer = jreUpdate.paddedVersion;
/*      */                 }
/*      */                 else {
/* 1726 */                   String jreVer = null;
/*      */ 
/* 1728 */                   System.out.println("[GenericUpdater] App is not compatible with existing system JRE and update to JRE failed");
/* 1729 */                   doNoInternetFailAndExit("The app is not compatible with the existing (system-imported) JRE and the attempt to fetch a compatible one failed", 
/* 1730 */                     JWLanguage.getString("ERROR_CANNOT_FETCH_RUNTIME"), 
/* 1731 */                     null);
/*      */                 }
/*      */               }
/*      */               catch (LatestVersionExists exver) {
/* 1735 */                 System.out.println("[GenericUpdater] WARNING: unexpected case.  Latest JRE version reported as 0 (system) yet JRE updater reports paired JRE (" + exver.version + ") as already existing!");
/* 1736 */                 System.out.println("[GenericUpdater] Setting App " + latest.getName() + " to use (apparently preexisting!?) JRE paired for download " + jreAppName + "-" + exver.version);
/*      */ 
/* 1738 */                 LaunchFile.setJreVersionForApp(latest, exver.version);
/* 1739 */                 jreVer = exver.version;
/*      */               }
/*      */             }
/*      */           } else {
/* 1743 */             System.out.println("[GenericUpdater] Latest JRE is downloaded/extracted JRE so is acceptable");
/*      */ 
/* 1745 */             System.out.println("[GenericUpdater] Will set App to use " + latestJreVer + " in the future");
/*      */ 
/* 1747 */             LaunchFile.setJreVersionForApp(latest, latestJreVer);
/* 1748 */             jreVer = latestJreVer;
/*      */           }
/*      */         } else {
/* 1751 */           jreVer = specifiedVer;
/*      */         }
/*      */ 
/* 1754 */         useJRE = new File(master, getAppFolderNameFor(jreAppName, jreVer));
/* 1755 */         if (!useJRE.exists()) {
/* 1756 */           System.out.println("[GenericUpdater] Specified App JRE " + jreAppName + "-" + jreVer + " does not exist!");
/* 1757 */           doNoInternetFailAndExit("Unexpectedly, the chosen JRE (" + jreAppName + "-" + jreVer + ") does not exist", 
/* 1758 */             JWLanguage.getString("ERROR_RUNTIME_DOES_NOT_EXIST"), 
/* 1759 */             null);
/*      */         }
/*      */ 
/* 1762 */         System.out.println("[GenericUpdater] Will use JRE " + useJRE.getName());
/*      */ 
/* 1764 */         System.out.println("[GenericUpdater] Launching app: " + latest.getName());
/* 1765 */         System.out.println("[GenericUpdater] Using JRE: " + useJRE.getName() + " [exists=" + useJRE.exists() + "]");
/*      */       }
/*      */ 
/* 1769 */       swu.waitForAllSwipes();
/* 1770 */       if (swu.isShowingFrame()) swu.swipeSmallTo("SmallLaunching");
/* 1771 */       swu.waitForAllSwipes();
/*      */ 
/* 1773 */       System.out.println("[GenericUpdater] Splash should be shown for " + (splashUntil - System.currentTimeMillis()));
/* 1774 */       if (swu.isShowingFrame()) {
/* 1775 */         System.out.println("[GenericUpdater] waiting for splash...");
/* 1776 */         while (System.currentTimeMillis() < splashUntil)
/*      */           try {
/* 1778 */             Thread.sleep(25L);
/*      */           } catch (Exception localException1) {
/*      */           }
/*      */       }
/*      */       else {
/* 1783 */         System.out.println("[GenericUpdater] not waiting for splash (none shown)");
/*      */       }
/*      */ 
/* 1786 */       if ((installType.equalsIgnoreCase("perm_all")) || 
/* 1787 */         (installType.equalsIgnoreCase("perm_user")))
/*      */       {
/* 1789 */         if ((MODE == MODE_FIRST_RUN_POST_INSTALL) || 
/* 1790 */           (MODE == MODE_FIRST_RUN_POST_UPDATE))
/*      */         {
/* 1801 */           File winLauncher = new File(latest, getLauncherNameFor(app, false, false, false));
/* 1802 */           File linLauncher32 = new File(latest, getLauncherNameFor(app, true, false, false));
/* 1803 */           File linLauncher64 = new File(latest, getLauncherNameFor(app, true, true, false));
/*      */ 
/* 1808 */           File winTarget = new File(master, getLauncherNameFor(app, false, false, false));
/* 1809 */           File linTarget32 = new File(master, getLauncherNameFor(app, true, false, false));
/* 1810 */           File linTarget64 = new File(master, getLauncherNameFor(app, true, true, false));
/*      */ 
/* 1814 */           JWParameteriser jwp = new JWParameteriser();
/*      */           try
/*      */           {
/* 1817 */             if (MODE != MODE_FIRST_RUN_POST_UPDATE)
/*      */             {
/* 1820 */               if (OS.isWindows()) {
/* 1821 */                 System.out.println("[GenericUpdater] Setting up/Updating windows app launcher");
/* 1822 */                 winTarget.delete();
/*      */ 
/* 1824 */                 FileUtil.copy(winLauncher, winTarget);
/* 1825 */                 System.out.println("[GenericUpdater] Set up windows app launcher");
/*      */ 
/* 1827 */                 jwp.setParameters(appArgs, winTarget, true);
/* 1828 */                 System.out.println("[GenericUpdater] Set up windows app launcher launch props");
/*      */               }
/* 1830 */               else if (OS.isLinux()) {
/* 1831 */                 System.out.println("[GenericUpdater] Setting up/Updating linux app launcher");
/*      */ 
/* 1833 */                 if (OS.isLinux64bit()) {
/* 1834 */                   linTarget64.delete();
/*      */ 
/* 1836 */                   AutoChmodFile.autoChmodFile(linLauncher64, linTarget64, true);
/* 1837 */                   System.out.println("[GenericUpdater] Set up linux 64 app launcher");
/*      */ 
/* 1839 */                   jwp.setParameters(appArgs, linTarget64, true);
/* 1840 */                   System.out.println("[GenericUpdater] Set up linux 64 app launcher launch props");
/*      */                 }
/*      */                 else {
/* 1843 */                   AutoChmodFile.autoChmodFile(linLauncher32, linTarget32, true);
/* 1844 */                   System.out.println("[GenericUpdater] Set up/Updating linux 32 app launcher");
/*      */ 
/* 1846 */                   jwp.setParameters(appArgs, linTarget32, true);
/* 1847 */                   System.out.println("[GenericUpdater] Set up linux 32 app launcher launch props");
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Exception x)
/*      */           {
/* 1854 */             System.out.println("***WARNING*** Failed to set up/update launcher: " + x);
/* 1855 */             x.printStackTrace();
/*      */           }
/*      */ 
/* 1858 */           JWApp[] jwapps = JWApp.getAllJWApps(latest, true);
/*      */ 
/* 1860 */           for (int i = 0; i < jwapps.length; i++) {
/* 1861 */             System.out.println("[GenericUpdater] Storing/Updating ICO file for " + jwapps[i].getUserVisibleName());
/* 1862 */             OutputStream out = new BufferedOutputStream(new FileOutputStream(jwapps[i].getICOFile()));
/* 1863 */             out.write(jwapps[i].logoICO);
/* 1864 */             out.close();
/*      */ 
/* 1867 */             JWGenericOS.setWritableForAllUsers(jwapps[i].getICOFile(), false);
/*      */           }
/*      */ 
/* 1871 */           IcoPng icp = new IcoPng(new File(latest, getUninstallerIcopngFileNameFor(app)));
/*      */ 
/* 1873 */           File uninstallerICO = new File(JWApp.getJWAppsFolder(master), getUninstallerIcoFileNameFor(app));
/*      */ 
/* 1875 */           FileUtil.writeFile(uninstallerICO, icp.getICO());
/*      */ 
/* 1878 */           JWGenericOS.setWritableForAllUsers(uninstallerICO, false);
/*      */ 
/* 1881 */           System.out.println("[GenericUpdater] Performing first time setup");
/*      */           int command;
/*      */           try {
/* 1886 */             command = LaunchFile.runHookAppFrom(latest, LaunchFile.JW_VAPP_POST_INSTALL_APP, appArgs, useJRE, base, remparams, app + "Install");
/*      */           }
/*      */           catch (AppDoesNotExistException x)
/*      */           {
/*      */             int command;
/* 1888 */             System.out.println("[GenericUpdater] No virtual app for post installation configuration, will perform standard config");
/* 1889 */             command = 41;
/*      */           }
/*      */ 
/* 1893 */           if (command == 42)
/*      */           {
/* 1895 */             System.out.println("[GenericUpdater] PostInstall App has finished its own post install setup and does not require standard setup");
/*      */           }
/* 1897 */           else if (command == 41)
/*      */           {
/* 1900 */             setupAllStandardShortcuts(app, latest, jwapps, appArgs, true);
/*      */           }
/* 1921 */           else if (command == 43) {
/* 1922 */             System.out.println("[GenericUpdater] PostInstall App has requested rollback and uninstall");
/*      */ 
/* 1924 */             if (MODE == MODE_FIRST_RUN_POST_INSTALL)
/*      */             {
/*      */               try
/*      */               {
/* 1928 */                 SelfDelete.deleteSelf(master, getUninstallExtras(), null, null, new byte[0], new byte[0]);
/*      */               } catch (IOException x) {
/* 1930 */                 x.printStackTrace();
/*      */               }
/*      */ 
/* 1933 */               ProcessOutputUtil.logProcessResult(LOG_SOURCE, 3);
/* 1934 */               ProcessOutputUtil.logProcessError(LOG_SOURCE, "The post-install virtual app for this app bundle requested that the install be rolled back");
/*      */ 
/* 1936 */               System.exit(0);
/*      */             } else {
/* 1938 */               System.out.println("[GenericUpdater] Will not roll back since this is an update");
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1950 */       if ((firstRun.exists()) && 
/* 1951 */         (!firstRun.delete())) {
/* 1952 */         System.out.println("[GenericUpdater] ERROR - UNABLE TO DELETE FIRST RUN FILE");
/*      */       }
/*      */ 
/* 1959 */       String[] suplangs = JWSystem.getSupportedLanguages();
/*      */ 
/* 1961 */       for (int i = 0; i < suplangs.length; i++) {
/* 1962 */         System.out.println("[GenericUpdater] Supported language: " + suplangs[i]);
/*      */       }
/*      */ 
/* 1965 */       String mylang = JWInstallApp.getChosenLanguage();
/*      */ 
/* 1967 */       System.out.println("[GenericUpdater] Current chosen language is: " + mylang);
/*      */ 
/* 1969 */       if (mylang == null) {
/* 1970 */         if (suplangs.length == 1) {
/* 1971 */           mylang = suplangs[0];
/* 1972 */           System.out.println("[GenericUpdater] Automatically choosing only supported language: " + mylang);
/* 1973 */           JWInstallApp.setChosenLanguage(mylang);
/*      */         }
/* 1975 */         else if (SHOW_NO_UI) {
/* 1976 */           mylang = suplangs[0];
/* 1977 */           System.out.println("[GenericUpdater] Automatically choosing first supported language (not allowed to show UI to ask): " + mylang);
/* 1978 */           JWInstallApp.setChosenLanguage(mylang);
/*      */         }
/*      */         else
/*      */         {
/* 1982 */           System.out.println("[GenericUpdater] Showing language chooser");
/*      */ 
/* 1984 */           mylang = JWInstallApp.showLanguageChooser();
/*      */ 
/* 1986 */           if (mylang == null)
/*      */           {
/* 1988 */             System.out.flush();
/* 1989 */             System.err.flush();
/* 1990 */             System.exit(88);
/*      */           }
/*      */ 
/* 1993 */           System.out.println("[GenericUpdater] New chosen language is: " + mylang);
/* 1994 */           JWInstallApp.setChosenLanguage(mylang);
/*      */         }
/*      */       }
/*      */ 
/* 1998 */       JWLanguage.loadTranslations(mylang);
/*      */ 
/* 2002 */       if (AM_UNINSTALLER)
/*      */       {
/*      */         int command;
/*      */         try {
/* 2006 */           command = LaunchFile.runHookAppFrom(latest, LaunchFile.JW_VAPP_PRE_UNINSTALL_APP, appArgs, useJRE, base, remparams, app + "Uninstall");
/*      */         }
/*      */         catch (AppDoesNotExistException x)
/*      */         {
/*      */           int command;
/* 2008 */           System.out.println("[GenericUpdater] No virtual app for pre uninstallation configuration, will ask user");
/* 2009 */           command = 62;
/*      */         }
/*      */ 
/* 2013 */         File splash = new File(latest, getSplashFileNameFor(app));
/* 2014 */         byte[] splashBytes = FileUtil.readFile(splash.getAbsolutePath());
/*      */ 
/* 2016 */         byte[] logoBytes = null;
/*      */         try {
/* 2018 */           logoBytes = (byte[])HeadlessOsxUtil.loadPngBytesFromICNS(new File(latest, getIcnsFileNameFor(app)));
/*      */         } catch (Exception x) {
/* 2020 */           x.printStackTrace();
/*      */         }
/*      */ 
/* 2023 */         if (command == 61) {
/* 2024 */           System.out.println("[GenericUpdater] Told to do immediate uninstall");
/*      */ 
/* 2026 */           new Uninstaller(master, getUninstallExtras(), splashBytes, logoBytes, latest, useJRE, base, remparams, app).start();
/*      */ 
/* 2028 */           return;
/*      */         }
/* 2030 */         if (command == 62) {
/* 2031 */           System.out.println("[GenericUpdater] Told to do ask user if uninstall (show usual uninstaller UI)");
/*      */ 
/* 2033 */           HeadlessSwipeLoadUtil suu = new HeadlessSwipeLoadUtil();
/* 2034 */           suu.setBigTo(suu.loadImage(splash));
/*      */ 
/* 2036 */           suu.setSmallTo(null);
/* 2037 */           suu.makeUninstaller(app, suu.loadImage(logoBytes), new Uninstaller(suu, master, getUninstallExtras(), splashBytes, logoBytes, latest, useJRE, base, remparams, app));
/* 2038 */           suu.ensureShowing();
/*      */ 
/* 2040 */           System.out.println("[GenericUpdater] Showing uninstaller UI to ask user");
/*      */ 
/* 2042 */           return;
/*      */         }
/*      */ 
/* 2046 */         ProcessOutputUtil.logProcessResult(LOG_SOURCE, 3);
/* 2047 */         ProcessOutputUtil.logProcessError(LOG_SOURCE, "The app bundle uninstaller virtual application returned that uninstallation should be cancelled");
/*      */ 
/* 2049 */         System.exit(0);
/*      */       }
/*      */ 
/* 2055 */       if (virtualApp == null) {
/* 2056 */         System.out.println("[GenericUpdater] No virtual app specified, will check count");
/*      */ 
/* 2058 */         JWApp[] apps = JWApp.getAllJWApps(latest, true);
/*      */ 
/* 2060 */         if (apps.length == 0)
/* 2061 */           throw new Exception("No JWAppSpec found!");
/* 2062 */         if (apps.length == 1) {
/* 2063 */           virtualApp = apps[0].name;
/* 2064 */           System.out.println("[GenericUpdater] Only one app, autoselecting " + virtualApp);
/*      */         } else {
/* 2066 */           System.out.println("[GenericUpdater] No virtual app specified, will check count");
/*      */ 
/* 2069 */           swu.hideFrame();
/*      */ 
/* 2071 */           virtualApp = HeadlessVirtualAppChooserUtil.chooseVirtualApp(latest, apps);
/* 2072 */           if (virtualApp == null)
/*      */           {
/* 2074 */             System.out.flush();
/* 2075 */             System.err.flush();
/* 2076 */             System.exit(89);
/*      */           }
/*      */         }
/*      */ 
/* 2080 */         System.out.println("[GenericUpdater] Virtual app set to: " + virtualApp);
/*      */ 
/* 2082 */         appArgs.setProperty("gu_virt_app", virtualApp);
/*      */       }
/*      */ 
/* 2085 */       swu.hideFrame();
/*      */ 
/* 2087 */       if (failDueToServerUnavailable) {
/* 2088 */         if (AM_SANITY_CHECK) {
/* 2089 */           System.out.println("[GenericUpdaterMain] Launch failed because server was unavailable (matched versions) but sanity check will return OK");
/* 2090 */           JWSanityTest.exitAndReturnSanityCheckOK();
/*      */         }
/*      */ 
/* 2094 */         int ret = LaunchFile.runHookAppFrom(latest, LaunchFile.JW_VAPP_MATCH_VERSIONS_SERVER_UNAVAILABLE, appArgs, useJRE, base, remparams, app + "Unavailable");
/* 2095 */         if (ret == 83)
/*      */         {
/* 2097 */           String newURL = JWSystem.getSourceLauncherUpdateURL();
/*      */ 
/* 2099 */           String oldURL = base;
/*      */ 
/* 2101 */           System.out.println("[GenericUpdaterMain] Server Unavailable App requested relaunch, old URL is " + oldURL + " new URL is " + newURL);
/*      */           try
/*      */           {
/* 2107 */             System.out.println("[GenericUpdaterMain] URL has changed, will relaunch");
/* 2108 */             File latestGU = LaunchFile.getLatestVersionOf("JWrapper", master);
/* 2109 */             System.out.println("[GenericUpdaterMain] Will launch GU app " + latestGU);
/*      */ 
/* 2111 */             appArgs.setProperty("update_url", newURL);
/*      */ 
/* 2115 */             LaunchFile.runVirtualAppFromNoExit(latestGU, appArgs, useJRE, dynamicUpdateURL ? "D" : base, new String[0], "JWrapper", false);
/*      */           }
/*      */           catch (Exception x)
/*      */           {
/* 2119 */             x.printStackTrace();
/*      */           }
/*      */ 
/* 2122 */           Thread.sleep(1500L);
/* 2123 */           System.exit(0);
/*      */         }
/* 2125 */         else if (ret == 82) {
/* 2126 */           System.out.println("Unable to launch app since it may be the wrong version (couldn't check the update URL server)");
/*      */ 
/* 2128 */           Exception x = new Exception("The server that this app connects to is offline (but is required for the app to launch)");
/*      */ 
/* 2131 */           ProcessOutputUtil.logProcessResult(LOG_SOURCE, 2);
/* 2132 */           ProcessOutputUtil.logProcessTrace(LOG_SOURCE, x);
/* 2133 */           ProcessOutputUtil.logProcessError(LOG_SOURCE, "The server that this app connects to is offline (but is required for the app to launch)");
/*      */ 
/* 2135 */           System.out.flush();
/* 2136 */           System.err.flush();
/* 2137 */           System.exit(1);
/*      */         }
/*      */         else {
/* 2140 */           throw new HumanMessageException("The server that this app connects to is offline (but is required for the app to launch)");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2145 */       ProcessOutputUtil.logProcessResult(LOG_SOURCE, 1);
/*      */ 
/* 2147 */       if (AM_SANITY_CHECK) {
/* 2148 */         System.out.println("[GenericUpdaterMain] Sanity check will return OK");
/* 2149 */         JWSanityTest.exitAndReturnSanityCheckOK();
/*      */       }
/*      */ 
/* 2153 */       if (JWDetectedProxy.DETECTED_PROXY_OK) {
/* 2154 */         JWDetectedProxy.revertDefaultProxySettings();
/*      */       }
/* 2156 */       MasterFolderCleaner.markUsed(latest);
/* 2157 */       MasterFolderCleaner.markUsed(new File(guWorkingDir));
/* 2158 */       if (useJRE != null) MasterFolderCleaner.markUsed(useJRE);
/*      */ 
/* 2160 */       int max_apps = 1;
/* 2161 */       int max_gus = 1;
/*      */       try {
/* 2163 */         max_apps = Integer.parseInt(JWLaunchProperties.getProperty("wrapper_app_versions")); } catch (Exception localException2) {
/*      */       }
/*      */       try {
/* 2166 */         max_gus = Integer.parseInt(JWLaunchProperties.getProperty("wrapper_gu_versions"));
/*      */       } catch (Exception localException3) {
/*      */       }
/* 2169 */       MasterFolderCleaner.clean(master, max_apps, max_gus);
/*      */ 
/* 2172 */       if ((installType.equalsIgnoreCase("perm_all")) && 
/* 2173 */         (MODE == MODE_FIRST_RUN_POST_INSTALL)) {
/* 2174 */         System.out.println("[GenericUpdater] Initial run of all-users installation, setting master folder accessible for all users");
/*      */ 
/* 2180 */         JWGenericOS.setWritableForAllUsers(master, false);
/*      */ 
/* 2182 */         File[] tmp = master.listFiles();
/* 2183 */         for (int i = 0; i < tmp.length; i++) {
/* 2184 */           JWGenericOS.setWritableForAllUsers(tmp[i], false);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2196 */       RSADecryptor rsaDec = new RSADecryptor(
/* 2197 */         new BigInteger[] { 
/* 2198 */         new BigInteger(new byte[] { 
/* 2199 */         0, -126, -92, -128, 105, -24, -21, 15, -79, 69, 30, 
/* 2200 */         -7, 122, -127, -33, 123, 8, -31, 62, -39, -109, 
/* 2201 */         42, 80, -41, -125, 85, -98, 18, 72, 33, -80, 
/* 2202 */         110, 116, -35, -42, 63, 115, 29, -99, 57, 45, 
/* 2203 */         127, 56, 62, -78, 70, -114, -57, -16, 118, -98, 
/* 2204 */         -102, 97, 23, -80, 98, -104, -54, 86, -59, -95, 
/* 2205 */         -31, -108, 118, -103, 16, -74, 44, 42, -44, -33, 
/* 2206 */         -63, -128, 12, 32, 55, -70, 2, -92, 125, -46, 
/* 2207 */         90, 80, -96, 39, 97, 84, -117, 49, 13, -48, 
/* 2208 */         31, 68, -115, -16, -107, -103, -79, 82, -93, -106, 
/* 2209 */         -86, -17, -46, 64, -51, 47, -27, 33, -52, -123, 
/* 2210 */         38, -9, 106, -16, 125, 99, -90, 34, -109, 90, 
/* 2211 */         58, -5, -57, -34, -20, -100, 4, 62, 12, -21, 
/* 2212 */         -128, 65, -110, 90, 90, 92, -111, 23, -34, 89, 
/* 2213 */         124, -11, 26, 8, 86, 100, 38, 14, -13, -98, 
/* 2214 */         -34, 78, 82, 95, 62, 45, 68, -93, 80, -116, 
/* 2215 */         86, -56, -37, 33, -42, 125, 76, -121, 62, -68, 
/* 2216 */         93, -69, 91, -102, 77, 12, -109, -100, -51, -76, 
/* 2217 */         110, 59, 8, -40, -12, 126, -108, -6, -18, -59, 
/* 2218 */         -29, -94, 57, -10, 72, 14, -16, 48, -52, -19, 
/* 2219 */         16, -111, 120, -102, 104, -81, 101, -65, 72, 40, 
/* 2220 */         -56, -25, -117, 0, -2, 68, -71, 115, -89, -113, 
/* 2221 */         -60, 77, 113, -76, 28, -117, -6, 72, -78, 87, 
/* 2222 */         20, -1, -14, -127, -37, -30, -104, -29, -19, -95, 
/* 2223 */         37, 68, -31, 67, -101, -5, -118, -3, -104, 39, 
/* 2224 */         -72, -82, 63, -81, 85, 70, -128, 94, 22, -8, 
/* 2225 */         103, 114, -96, -120, -101, -15, 22, -20, -56, 54, 
/* 2226 */         125, -53, 120, -39, 9, 73, 4, -98, 111, 57, 
/* 2227 */         -110, 40, 51, -90, 100, -76, 20, -117, 13, 4, 
/* 2228 */         -99, -80, 124, -56, -98, 28, 52, 69, -111, 29, 
/* 2229 */         46, -63, -88, -46, -71, -77, -20, 82, 33, 102, 
/* 2230 */         84, -4, 58, 34, -6, -28, -90, 56, 40, -84, 
/* 2231 */         -71, -55, -119, -91, 11, -76, 27, -127, 74, 21, 
/* 2232 */         43, 16, -32, -13, 58, -13, 70, 77, 115, -8, 
/* 2233 */         -41, -103, -121, 55, 35, 112, 0, 63, -18, 40, 
/* 2234 */         -40, 126, 25, -40, -9, -87, -70, 20, -5, -24, 
/* 2235 */         104, 50, -103, 19, 20, -34, -46, 21, -121, 120, 
/* 2236 */         27, -74, 12, 74, -12, -29, 50, -98, 44, 31, 
/* 2237 */         31, 51, 124, -2, 103, 20, 64, -80, -83, 95, 
/* 2238 */         118, 121 }), 
/* 2239 */         new BigInteger(new byte[] { 23 }) });
/*      */ 
/* 2244 */       File jwlic = new File(latest, "jwrapper_license");
/*      */ 
/* 2246 */       boolean licensed = false;
/*      */ 
/* 2248 */       if (jwlic.exists()) {
/* 2249 */         byte[] encrypted = FileUtil.readFile(jwlic.getPath());
/*      */ 
/* 2251 */         byte[] decrypted = rsaDec.decrypt(encrypted);
/*      */ 
/* 2253 */         String marshalled = new String(decrypted, "UTF8");
/*      */ 
/* 2255 */         StringBuffer expected = new StringBuffer();
/*      */ 
/* 2257 */         for (int i = 0; i < 10; i++) {
/* 2258 */           expected.append("[BUNDLE_NAME]").append(app);
/*      */         }
/*      */ 
/* 2261 */         if (marshalled.equals(expected.toString())) {
/* 2262 */           licensed = true;
/*      */         }
/*      */       }
/*      */ 
/* 2266 */       if (licensed)
/* 2267 */         System.out.println("[GenericUpdater] JWrapper is licensed OK");
/*      */       else {
/* 2269 */         System.out.println("[GenericUpdater] JWrapper is NOT LICENSED");
/*      */       }
/*      */ 
/* 2274 */       appArgs.setProperty("update_url", base);
/*      */ 
/* 2277 */       LaunchFile.runVirtualAppFromNoExit(latest, appArgs, useJRE, dynamicUpdateURL ? "D" : base, remparams, app, true);
/*      */ 
/* 2283 */       long printOutputUntil = System.currentTimeMillis() + LaunchFile.POST_LAUNCH_PRINT_FOR;
/*      */ 
/* 2293 */       while (System.currentTimeMillis() < printOutputUntil) {
/*      */         try {
/* 2295 */           Thread.sleep(25L);
/*      */         }
/*      */         catch (Exception localException4)
/*      */         {
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2304 */       System.out.flush();
/* 2305 */       System.err.flush();
/* 2306 */       System.exit(0);
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*      */       try
/*      */       {
/* 2316 */         if ((!SHOW_NO_UI) && (!JWLanguage.isBundleLoaded()))
/*      */         {
/* 2318 */           String[] suplangs = JWSystem.getSupportedLanguages();
/* 2319 */           if ((suplangs != null) && (suplangs.length > 0))
/* 2320 */             JWLanguage.loadTranslations(suplangs[0]);
/*      */         }
/*      */       }
/*      */       catch (Throwable tt)
/*      */       {
/* 2325 */         t.printStackTrace();
/*      */       }
/*      */ 
/* 2329 */       t.printStackTrace();
/*      */ 
/* 2331 */       if ((t instanceof HumanMessageException)) {
/* 2332 */         HumanMessageException hm = (HumanMessageException)t;
/* 2333 */         if (!SHOW_NO_UI)
/* 2334 */           SimpleErrorHandler.displayError(JWLanguage.getString("UPDATE_ERROR_TITLE"), hm.getMessage(), null);
/*      */       }
/*      */       else
/*      */       {
/* 2338 */         if (!SHOW_NO_UI) {
/* 2339 */           SimpleErrorHandler.displayThrowable(t, JWLanguage.getString("UPDATE_ERROR_TITLE"), JWLanguage.getString("UPDATE_ERROR_MESSAGE"), null, null);
/*      */         }
/* 2341 */         ProcessOutputUtil.logProcessResult(LOG_SOURCE, 2);
/* 2342 */         ProcessOutputUtil.logProcessError(LOG_SOURCE, "An unexpected problem occurred while updating and launching (" + t.getMessage() + ")");
/* 2343 */         ProcessOutputUtil.logProcessTrace(LOG_SOURCE, t);
/*      */       }
/*      */ 
/* 2346 */       System.out.flush();
/* 2347 */       System.err.flush();
/* 2348 */       System.exit(1);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class LatestUpdate
/*      */   {
/*      */     File tempUpdateFolder;
/*      */     String paddedVersion;
/*      */     File finalUpdateFolder;
/*      */   }
/*      */ 
/*      */   static class LatestVersionExists extends Throwable
/*      */   {
/*      */     public String version;
/*      */ 
/*      */     public LatestVersionExists(String version)
/*      */     {
/*  212 */       this.version = version;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class Uninstaller extends Thread
/*      */     implements LPUninstallerListener
/*      */   {
/*      */     File latest;
/*      */     File useJRE;
/*      */     String base;
/*      */     String[] remparams;
/*      */     String app;
/*      */     HeadlessSwipeLoadUtil swu;
/*      */     File master;
/*      */     File[] extras;
/*      */     byte[] splashPNG;
/*      */     byte[] logoPNG;
/*      */ 
/*      */     Uninstaller(HeadlessSwipeLoadUtil swu, File master, File[] extras, byte[] splashPNG, byte[] logoPNG, File latest, File useJRE, String base, String[] remparams, String app)
/*      */     {
/* 2366 */       this.swu = swu;
/* 2367 */       this.master = master;
/* 2368 */       this.extras = extras;
/* 2369 */       this.splashPNG = splashPNG;
/* 2370 */       this.logoPNG = logoPNG;
/*      */ 
/* 2372 */       this.latest = latest;
/* 2373 */       this.useJRE = useJRE;
/* 2374 */       this.base = base;
/* 2375 */       this.remparams = remparams;
/* 2376 */       this.app = app;
/*      */     }
/*      */     Uninstaller(File master, File[] extras, byte[] splashPNG, byte[] logoPNG, File latest, File useJRE, String base, String[] remparams, String app) {
/* 2379 */       this.master = master;
/* 2380 */       this.extras = extras;
/* 2381 */       this.splashPNG = splashPNG;
/* 2382 */       this.logoPNG = logoPNG;
/*      */ 
/* 2384 */       this.latest = latest;
/* 2385 */       this.useJRE = useJRE;
/* 2386 */       this.base = base;
/* 2387 */       this.remparams = remparams;
/* 2388 */       this.app = app;
/*      */     }
/*      */ 
/*      */     public void doUninstall() {
/* 2392 */       this.swu.disableButtons();
/*      */ 
/* 2395 */       this.swu.swipeSmallTo("SmallUninstall");
/* 2396 */       start();
/*      */     }
/*      */ 
/*      */     public void doExit()
/*      */     {
/* 2401 */       ProcessOutputUtil.logProcessResult(GenericUpdater.LOG_SOURCE, 1);
/* 2402 */       ProcessOutputUtil.logProcessMessage(GenericUpdater.LOG_SOURCE, "The user cancelled the uninstallation");
/*      */ 
/* 2404 */       System.exit(0);
/*      */     }
/*      */ 
/*      */     public void run() {
/*      */       int intercept;
/*      */       try {
/* 2410 */         intercept = LaunchFile.runHookAppFrom(this.latest, LaunchFile.JW_VAPP_POST_UNINSTALL_APP, GenericUpdater.appArgs, this.useJRE, this.base, this.remparams, this.app + "Uninstall");
/*      */       }
/*      */       catch (AppDoesNotExistException x)
/*      */       {
/*      */         int intercept;
/* 2413 */         System.out.println("[GenericUpdater] No virtual app for post uninstallation procedures");
/* 2414 */         intercept = 61;
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*      */         int intercept;
/* 2417 */         intercept = 63;
/* 2418 */         ProcessOutputUtil.logProcessResult(GenericUpdater.LOG_SOURCE, 2);
/* 2419 */         ProcessOutputUtil.logProcessError(GenericUpdater.LOG_SOURCE, "An unexpected problem occurred while running the post-uninstall app (" + t.getMessage() + ")");
/* 2420 */         ProcessOutputUtil.logProcessTrace(GenericUpdater.LOG_SOURCE, t);
/*      */ 
/* 2422 */         t.printStackTrace();
/* 2423 */         System.out.flush();
/* 2424 */         System.err.flush();
/* 2425 */         System.exit(1);
/*      */       }
/*      */ 
/* 2428 */       if (intercept == 63) {
/* 2429 */         ProcessOutputUtil.logProcessResult(GenericUpdater.LOG_SOURCE, 3);
/* 2430 */         ProcessOutputUtil.logProcessError(GenericUpdater.LOG_SOURCE, "The app bundle uninstaller virtual application returned that uninstallation should be cancelled");
/*      */ 
/* 2432 */         System.exit(0);
/*      */       }
/*      */ 
/* 2435 */       this.swu.waitForAllSwipes();
/*      */       try
/*      */       {
/* 2438 */         SelfDelete.deleteSelf(this.master, this.extras, this.swu, this.swu.getTitle(), this.splashPNG, this.logoPNG);
/*      */       } catch (IOException x) {
/* 2440 */         x.printStackTrace();
/*      */       }
/* 2442 */       System.exit(0);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.updater.GenericUpdater
 * JD-Core Version:    0.6.2
 */