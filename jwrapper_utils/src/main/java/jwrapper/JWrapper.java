/*     */ package jwrapper;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.Security;
/*     */ import java.util.ArrayList;
/*     */ import jwrapper.hidden.JWNativeAPI;
/*     */ import jwrapper.jwutils.JWGenericOS;
/*     */ import jwrapper.jwutils.JWOsType;
/*     */ import jwrapper.jwutils.JWSystem;
/*     */ import jwrapper.jwutils.JWWindowsOS;
/*     */ import jwrapper.logging.StdLogging;
/*     */ import jwrapper.proxy.JWDetectedProxy;
/*     */ import jwrapper.updater.JWApp;
/*     */ import jwrapper.updater.JWLaunchProperties;
/*     */ import jwrapper.updater.LaunchFile;
/*     */ import utils.files.FileUtil;
/*     */ import utils.ostools.OS;
/*     */ import utils.stream.CFriendlyStreamUtils;
/*     */ import utils.swing.EventThreadExceptionPrinter;
/*     */ 
/*     */ public class JWrapper
/*     */ {
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  28 */     System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
/*     */ 
/*  30 */     System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
/*  31 */     System.setProperty("sun.net.client.defaultReadTimeout", "30000");
/*     */     try
/*     */     {
/*  34 */       Security.setProperty("networkaddress.cache.ttl", "30");
/*  35 */       Security.setProperty("networkaddress.cache.negative.ttl", "10");
/*     */     }
/*     */     catch (Throwable localThrowable1) {
/*     */     }
/*  39 */     args = JWLaunchProperties.argsToNormalArgs(args);
/*     */ 
/*  41 */     System.out.println("[JWrapper] About to set up logging to file (if configured)");
/*     */ 
/*  43 */     if ((JWLaunchProperties.getProperty("debug_logging").length() > 0) || (StdLogging.beforeDebugLogUntil(JWLaunchProperties.getProperty("debug_logging_until")))) {
/*  44 */       StdLogging.startLogging(JWSystem.getAppFolder().getParentFile(), JWSystem.getAppBundleName() + "-" + JWSystem.getMyAppName());
/*     */     }
/*  46 */     StdLogging.startDebugLogging(JWSystem.getAppFolder().getParentFile(), JWSystem.getAppBundleName() + "-" + JWSystem.getMyAppName());
/*     */ 
/*  48 */     boolean SHOW_NO_UI = JWLaunchProperties.getProperty("show_no_ui").equals("true");
/*     */     try
/*     */     {
/*  51 */       EventThreadExceptionPrinter.setup();
/*     */     }
/*     */     catch (Throwable localThrowable2)
/*     */     {
/*     */     }
/*     */     try {
/*  57 */       JWNativeAPI.loadLibraryFrom(JWSystem.getAppFolder());
/*     */     }
/*     */     catch (Throwable localThrowable3)
/*     */     {
/*     */     }
/*     */     try {
/*  63 */       if (OS.isWindowsVistaOrAbove())
/*     */       {
/*  65 */         String id = JWLaunchProperties.getProperty("windows_app_id");
/*  66 */         if ((id != null) && (id.length() > 0))
/*     */         {
/*  68 */           System.out.println("[JWrapper] Setting app ID to " + id);
/*  69 */           JWWindowsOS windowsOS = new JWWindowsOS();
/*  70 */           windowsOS.setWindowsAppID(id);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*  76 */       t.printStackTrace();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  81 */       JWDetectedProxy.loadLastDetectedProxy();
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*  85 */       t.printStackTrace();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*     */       String[] my_options;
/*     */       try
/*     */       {
/*  94 */         int njvmProps = Integer.parseInt(JWLaunchProperties.getProperty("jvm_options_count"));
/*     */ 
/*  96 */         String[] my_options = new String[njvmProps];
/*  97 */         for (int i = 0; i < my_options.length; i++) {
/*  98 */           my_options[i] = JWLaunchProperties.getProperty("jvm_options_" + i);
/*  99 */           System.out.println("JVM Option: " + my_options[i]);
/*     */         }
/*     */       }
/*     */       catch (NumberFormatException x)
/*     */       {
/* 104 */         my_options = new String[0];
/*     */       }
/*     */ 
/* 112 */       String jvm_home = System.getProperty("java.home");
/* 113 */       System.out.println("JVM Home: " + jvm_home);
/*     */ 
/* 115 */       File lso = LaunchFile.getJvmLastSuccessfulOptionsFile(JWSystem.getAppFolder().getParentFile(), new File(jvm_home));
/*     */ 
/* 118 */       OutputStream out = new BufferedOutputStream(new FileOutputStream(lso));
/*     */       try {
/* 120 */         CFriendlyStreamUtils.writeInt(out, my_options.length);
/* 121 */         for (int i = 0; i < my_options.length; i++)
/* 122 */           CFriendlyStreamUtils.writeString(out, my_options[i]);
/*     */       }
/*     */       catch (Throwable t) {
/* 125 */         System.out.println("Failed to write JVM Options: " + t);
/*     */       }
/* 127 */       FileUtil.robustClose(out);
/*     */ 
/* 129 */       JWGenericOS.setWritableForAllUsers(lso, false);
/*     */ 
/* 132 */       System.out.println("Wrote JVM Options OK");
/*     */     }
/*     */     catch (Throwable t) {
/* 135 */       t.printStackTrace();
/*     */     }
/*     */ 
/* 138 */     JWApp jwapp = JWApp.getMyVirtualApp();
/*     */     try
/*     */     {
/* 141 */       if (JWOsType.isMacOS())
/*     */       {
/* 143 */         HeadlessOsxUtil.setOSXAppName(jwapp.getUserVisibleName());
/* 144 */         if (!SHOW_NO_UI)
/*     */         {
/* 146 */           System.out.println("Loading virtual app");
/*     */ 
/* 148 */           System.out.println("Got virtual app " + jwapp.getUserVisibleName());
/*     */ 
/* 150 */           System.out.println("Setting OS dock info");
/* 151 */           Object logoPNG = HeadlessOsxUtil.loadImageFromJWApp(jwapp);
/*     */ 
/* 153 */           System.out.println("Image: " + logoPNG);
/* 154 */           HeadlessOsxUtil.setOSXAppDockImage(logoPNG);
/*     */ 
/* 157 */           JWNativeAPI.getInstance().showApplication();
/*     */ 
/* 159 */           HeadlessOsxUtil.requestForeground();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {
/* 164 */       t.printStackTrace();
/*     */     }
/*     */ 
/* 169 */     String[] nargs = new String[jwapp.args.size() + args.length];
/* 170 */     for (int i = 0; i < jwapp.args.size(); i++) {
/* 171 */       nargs[i] = ((String)jwapp.args.get(i));
/*     */     }
/* 173 */     for (int i = 0; i < args.length; i++) {
/* 174 */       nargs[(jwapp.args.size() + i)] = args[i];
/*     */     }
/*     */ 
/* 178 */     Class cl = Class.forName(jwapp.mainClass);
/*     */ 
/* 180 */     Method method = cl.getMethod("main", new Class[] { [Ljava.lang.String.class });
/*     */ 
/* 182 */     method.invoke(null, new Object[] { nargs });
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.JWrapper
 * JD-Core Version:    0.6.2
 */