/*     */ package jwrapper.jwutils;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import jwrapper.IcoPng;
/*     */ import jwrapper.JWParameteriser;
/*     */ import jwrapper.proxy.JWAsyncProxyDetector;
/*     */ import jwrapper.proxy.JWDetectedProxy;
/*     */ import jwrapper.proxy.JWProxyCredentials;
/*     */ import jwrapper.proxy.JWProxyCredentials.Credentials;
/*     */ import jwrapper.proxy.JWProxyList;
/*     */ import jwrapper.updater.GenericUpdater;
/*     */ import jwrapper.updater.JWApp;
/*     */ import jwrapper.updater.JWLaunchProperties;
/*     */ import jwrapper.updater.LaunchFile;
/*     */ import utils.files.FileUtil;
/*     */ import utils.ostools.OS;
/*     */ 
/*     */ public class JWSystem
/*     */ {
/*     */   public void setLastUsedProxyAsDefault()
/*     */   {
/*  40 */     JWDetectedProxy.loadLastDetectedProxy();
/*     */   }
/*     */ 
/*     */   public boolean detectAndSetProxyFor(URL url, int timeoutMS)
/*     */   {
/*  53 */     return JWAsyncProxyDetector.detectAndSetProxyFor(getAppBundleName(), getAllAppVersionsSharedFolder(), url, timeoutMS);
/*     */   }
/*     */ 
/*     */   public Proxy getDefaultDetectedProxy()
/*     */   {
/*  62 */     if (JWDetectedProxy.DETECTED_PROXY_OK)
/*  63 */       return JWDetectedProxy.DETECTED_PROXY;
/*  64 */     return null;
/*     */   }
/*     */ 
/*     */   public static File getMyJreHome()
/*     */   {
/*  72 */     return new File(System.getProperty("java.home"));
/*     */   }
/*     */ 
/*     */   public static void forkVirtualApp(String virtualAppName)
/*     */     throws Exception
/*     */   {
/*  83 */     forkVirtualApp(virtualAppName, null, new String[0]);
/*     */   }
/*     */ 
/*     */   public static void forkVirtualApp(String virtualAppName, Properties launchProperties, String[] commandLineArgs)
/*     */     throws Exception
/*     */   {
/*  94 */     forkVirtualApp(virtualAppName, launchProperties, commandLineArgs, false, false);
/*     */   }
/*     */ 
/*     */   private static Properties buildInheritedLaunchProperties(Properties launchProperties, String virtualAppName, boolean elevateToAdmin)
/*     */   {
/*  99 */     Properties appArgs = JWLaunchProperties.getAsProperties();
/* 100 */     if (appArgs == null) {
/* 101 */       appArgs = new Properties();
/*     */     }
/* 103 */     if (launchProperties != null)
/*     */     {
/* 105 */       Object[] keys = launchProperties.keySet().toArray();
/* 106 */       for (int i = 0; i < keys.length; i++) {
/* 107 */         String key = (String)keys[i];
/* 108 */         appArgs.put(key, launchProperties.getProperty(key));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 113 */     appArgs.setProperty("gu_virt_app", virtualAppName);
/*     */ 
/* 115 */     if (elevateToAdmin) {
/* 116 */       appArgs.setProperty("launch_elevate", elevateToAdmin);
/*     */     }
/*     */ 
/* 119 */     return appArgs;
/*     */   }
/*     */ 
/*     */   public static void forkVirtualApp(String virtualAppName, Properties launchProperties, String[] commandLineArgs, boolean elevateToAdmin, boolean checkForUpdateFirst)
/*     */     throws Exception
/*     */   {
/* 131 */     forkVirtualApp(virtualAppName, getUpdateURL(), launchProperties, commandLineArgs, elevateToAdmin, checkForUpdateFirst);
/*     */   }
/*     */ 
/*     */   public static void forkVirtualApp(String virtualAppName, String staticUpdateURL, Properties launchProperties, String[] commandLineArgs, boolean elevateToAdmin, boolean checkForUpdateFirst)
/*     */     throws Exception
/*     */   {
/* 144 */     if (launchProperties == null) launchProperties = new Properties();
/* 145 */     if (commandLineArgs == null) commandLineArgs = new String[0];
/*     */ 
/* 148 */     Properties appArgs = buildInheritedLaunchProperties(launchProperties, virtualAppName, elevateToAdmin);
/*     */ 
/* 166 */     File jre = getMyJreHome();
/*     */ 
/* 168 */     if (JWOsType.isMacOS())
/*     */     {
/* 170 */       jre = null;
/*     */     }
/*     */ 
/* 173 */     if (checkForUpdateFirst)
/* 174 */       LaunchFile.runAutoupdatedVirtualAppFromNoExit(
/* 175 */         getAppFolder(), 
/* 176 */         appArgs, 
/* 177 */         jre, 
/* 178 */         staticUpdateURL, 
/* 179 */         commandLineArgs, 
/* 180 */         virtualAppName);
/*     */     else
/* 182 */       LaunchFile.runVirtualAppFromNoExit(
/* 183 */         getAppFolder(), 
/* 184 */         appArgs, 
/* 185 */         jre, 
/* 186 */         staticUpdateURL, 
/* 187 */         commandLineArgs, 
/* 188 */         virtualAppName, 
/* 189 */         false);
/*     */   }
/*     */ 
/*     */   public static File saveLauncherShortcutForVirtualApp(File dir, String shortcutName, String virtualAppName, Properties launchProperties, boolean elevateToAdmin)
/*     */     throws IOException
/*     */   {
/* 284 */     String app = getAppBundleName();
/*     */ 
/* 286 */     File appdir = getAppFolder();
/*     */ 
/* 288 */     System.out.println("[JWSystem] Setting launch properties");
/* 289 */     Properties appArgs = buildInheritedLaunchProperties(launchProperties, virtualAppName, elevateToAdmin);
/*     */ 
/* 291 */     return GenericUpdater.saveLauncherShortcutForVirtualApp(app, appdir, dir, shortcutName, virtualAppName, appArgs, elevateToAdmin);
/*     */   }
/*     */ 
/*     */   public static File getLauncherLocationForVirtualApp(File dir, String shortcutName)
/*     */   {
/*     */     String postfix;
/*     */     String postfix;
/* 299 */     if (OS.isWindows()) {
/* 300 */       postfix = ".exe";
/*     */     }
/*     */     else
/*     */     {
/*     */       String postfix;
/* 301 */       if (OS.isMacOS())
/* 302 */         postfix = ".app";
/*     */       else {
/* 304 */         postfix = "";
/*     */       }
/*     */     }
/* 307 */     return new File(dir, shortcutName + postfix);
/*     */   }
/*     */ 
/*     */   public static String getUpdateURL()
/*     */   {
/* 315 */     return JWLaunchProperties.getProperty("update_url");
/*     */   }
/*     */ 
/*     */   public static void overrideAppBundleUpdateURL(URL url)
/*     */     throws IOException
/*     */   {
/* 322 */     File appdir = getAppFolder();
/* 323 */     File master = appdir.getParentFile();
/* 324 */     File jwdir = JWApp.getJWAppsFolder(master);
/* 325 */     File override = new File(jwdir, GenericUpdater.getUpdateUrlOverrideFileName());
/* 326 */     FileUtil.writeFileAsStringUTF8(override.getAbsolutePath(), url.toString());
/*     */ 
/* 328 */     JWLaunchProperties.overrideProperty("update_url", url.toString());
/*     */   }
/*     */ 
/*     */   public static String getSourceLauncherUpdateURL()
/*     */     throws IOException
/*     */   {
/* 338 */     String path = JWLaunchProperties.getProperty("launched_from_dynprops");
/*     */ 
/* 340 */     System.out.println("[JWSystem] Source launcher path is " + path);
/*     */ 
/* 342 */     if (OS.isWindows()) {
/* 343 */       int last = path.lastIndexOf(':');
/* 344 */       if (last != -1)
/*     */       {
/* 346 */         if (last > 1)
/*     */         {
/* 348 */           path = path.substring(last - 1);
/* 349 */           System.out.println("[JWSystem] Trimmed source launcher path to " + path);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 354 */     File dynPropsFile = new File(path);
/* 355 */     System.out.println("[JWSystem] Final source launcher path is " + path);
/*     */ 
/* 357 */     JWParameteriser jwp = new JWParameteriser();
/* 358 */     Properties props = jwp.getParameters(dynPropsFile);
/*     */ 
/* 360 */     return props.getProperty("update_url");
/*     */   }
/*     */ 
/*     */   public static void overrideSourceLauncherUpdateURL(URL url)
/*     */     throws IOException
/*     */   {
/* 367 */     String path = JWLaunchProperties.getProperty("launched_from_dynprops");
/*     */ 
/* 369 */     if (OS.isWindows()) {
/* 370 */       int last = path.lastIndexOf(':');
/* 371 */       if (last != -1)
/*     */       {
/* 373 */         if (last > 1)
/*     */         {
/* 375 */           path = path.substring(last - 1);
/* 376 */           System.out.println("[JWSystem] Trimmed source launcher path to " + path);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 381 */     File dynPropsFile = new File(path);
/*     */ 
/* 383 */     System.out.println("[JWSystem] Overriding source launcher update URL at " + dynPropsFile + " with " + url);
/*     */ 
/* 385 */     JWParameteriser jwp = new JWParameteriser();
/* 386 */     Properties props = jwp.getParameters(dynPropsFile);
/*     */ 
/* 388 */     props.setProperty("update_url", url.toString());
/*     */ 
/* 390 */     jwp.setParameters(props, dynPropsFile, true);
/*     */   }
/*     */ 
/*     */   public static void removeOverrideAppBundleUpdateURL()
/*     */   {
/* 397 */     File appdir = getAppFolder();
/* 398 */     File master = appdir.getParentFile();
/* 399 */     File jwdir = JWApp.getJWAppsFolder(master);
/* 400 */     File override = new File(jwdir, GenericUpdater.getUpdateUrlOverrideFileName());
/* 401 */     override.delete();
/*     */   }
/*     */ 
/*     */   public static File getAppFolder()
/*     */   {
/* 409 */     return new File(JWLaunchProperties.getProperty("app_dir"));
/*     */   }
/*     */ 
/*     */   public static String getAppBundleVersion()
/*     */   {
/* 418 */     return LaunchFile.pickVersionFromAppFolder(getAppFolder());
/*     */   }
/*     */ 
/*     */   public static File getAllAppVersionsSharedFolder()
/*     */   {
/* 426 */     return getAllAppVersionsSharedFolder(getAppFolder().getParentFile());
/*     */   }
/*     */ 
/*     */   public static File getAllAppVersionsSharedFolder(File master)
/*     */   {
/* 434 */     File config = new File(master, "JWAppsSharedConfig");
/* 435 */     config.mkdirs();
/* 436 */     return config;
/*     */   }
/*     */ 
/*     */   public static File getAllAppLoggingFolder()
/*     */   {
/* 444 */     File logDir = new File(getAppFolder().getParentFile(), "logs");
/* 445 */     logDir.mkdirs();
/* 446 */     return logDir;
/*     */   }
/*     */ 
/*     */   public static String getMyAppName()
/*     */   {
/*     */     try
/*     */     {
/* 455 */       return JWApp.getMyVirtualApp().getUserVisibleName();
/*     */     } catch (Exception x) {
/* 457 */       x.printStackTrace();
/* 458 */     }return "ERROR";
/*     */   }
/*     */ 
/*     */   public static byte[] getMyAppLogoICO()
/*     */     throws IOException
/*     */   {
/* 467 */     return JWApp.getMyVirtualApp().getLogoICO();
/*     */   }
/*     */ 
/*     */   public static byte[] getMyAppLogoPNG()
/*     */     throws IOException
/*     */   {
/* 475 */     return JWApp.getMyVirtualApp().getLogoPNG();
/*     */   }
/*     */ 
/*     */   public static byte[] getAppBundleSplashPNG()
/*     */     throws IOException
/*     */   {
/* 483 */     return FileUtil.readFile(new File(getAppFolder(), GenericUpdater.getSplashFileNameFor(getAppBundleName())).getAbsolutePath());
/*     */   }
/*     */ 
/*     */   public static byte[] getAppBundleUninstallerICO()
/*     */     throws IOException
/*     */   {
/* 491 */     IcoPng icp = new IcoPng(new File(getAppFolder(), GenericUpdater.getUninstallerIcopngFileNameFor(getAppBundleName())));
/* 492 */     return icp.getICO();
/*     */   }
/*     */ 
/*     */   public static byte[] getAppBundleUninstallerPNG()
/*     */     throws IOException
/*     */   {
/* 500 */     IcoPng icp = new IcoPng(new File(getAppFolder(), GenericUpdater.getUninstallerIcopngFileNameFor(getAppBundleName())));
/* 501 */     return icp.getPNG();
/*     */   }
/*     */ 
/*     */   public static String getAppBundleName()
/*     */   {
/* 508 */     return JWLaunchProperties.getProperty("app_name");
/*     */   }
/*     */ 
/*     */   public static String getInstallType()
/*     */   {
/* 515 */     return JWLaunchProperties.getProperty("install_type");
/*     */   }
/*     */ 
/*     */   public static String getAppLaunchProperty(String propertyName)
/*     */   {
/* 524 */     return JWLaunchProperties.getProperty(propertyName);
/*     */   }
/*     */ 
/*     */   public static boolean matchingClientToServerVersion()
/*     */   {
/*     */     try
/*     */     {
/* 532 */       return JWLaunchProperties.getProperty("match_versions").equalsIgnoreCase("true"); } catch (Exception x) {
/*     */     }
/* 534 */     return false;
/*     */   }
/*     */ 
/*     */   public static void loadJWrapperProxies()
/*     */   {
/*     */   }
/*     */ 
/*     */   public static Proxy getJWrapperProxy()
/*     */   {
/* 549 */     if (JWDetectedProxy.DETECTED_PROXY_OK)
/* 550 */       return JWDetectedProxy.DETECTED_PROXY;
/* 551 */     return null;
/*     */   }
/*     */ 
/*     */   public static JWProxyCredentials.Credentials getJWrapperProxyCredentials()
/*     */   {
/* 560 */     Proxy proxy = getJWrapperProxy();
/* 561 */     if (proxy == null)
/* 562 */       return null;
/* 563 */     File sharedFolder = getAllAppVersionsSharedFolder();
/* 564 */     File credentialsFile = new File(sharedFolder, "ProxyCredentials");
/* 565 */     JWProxyCredentials credentials = new JWProxyCredentials();
/*     */     try
/*     */     {
/* 568 */       credentials.loadFromFile(credentialsFile);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 572 */       return null;
/*     */     }
/* 574 */     return credentials.getCredentialsFor(proxy);
/*     */   }
/*     */ 
/*     */   public static void addAppSpecificProxy(Proxy proxy)
/*     */     throws IOException
/*     */   {
/* 584 */     File appProxies = new File(getAllAppVersionsSharedFolder(), "AppProxies");
/* 585 */     JWProxyList proxyList = new JWProxyList(10);
/* 586 */     proxyList.loadFromFile(appProxies);
/* 587 */     proxyList.addProxyToFront(proxy);
/* 588 */     proxyList.saveToFile(appProxies);
/*     */   }
/*     */ 
/*     */   public static void addAppSpecificProxy(Proxy proxy, String username, String password)
/*     */     throws IOException
/*     */   {
/* 601 */     addAppSpecificProxy(proxy);
/*     */ 
/* 603 */     File credentialsFile = new File(getAllAppVersionsSharedFolder(), "ProxyCredentials");
/*     */ 
/* 606 */     JWProxyCredentials credentialsList = new JWProxyCredentials();
/* 607 */     credentialsList.loadFromFile(credentialsFile);
/* 608 */     credentialsList.setCredentialsFor(proxy, new JWProxyCredentials.Credentials(username, password));
/* 609 */     credentialsList.saveToFile(credentialsFile);
/*     */   }
/*     */ 
/*     */   public static String[] getSupportedLanguages()
/*     */   {
/* 617 */     String suplangs = JWLaunchProperties.getProperty("supported_langs");
/* 618 */     String[] alllangs = suplangs.split(",");
/* 619 */     ArrayList list = new ArrayList();
/* 620 */     for (int i = 0; i < alllangs.length; i++) {
/* 621 */       String tmp = alllangs[i];
/* 622 */       tmp = tmp.trim();
/* 623 */       if (tmp.length() > 0) {
/* 624 */         list.add(tmp);
/*     */       }
/*     */     }
/* 627 */     String[] ret = new String[list.size()];
/* 628 */     list.toArray(ret);
/* 629 */     return ret;
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.jwutils.JWSystem
 * JD-Core Version:    0.6.2
 */