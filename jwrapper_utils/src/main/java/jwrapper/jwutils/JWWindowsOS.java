/*     */ package jwrapper.jwutils;
/*     */ 
/*     */ import java.awt.Frame;
/*     */ import java.awt.Window;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import jwrapper.hidden.JWNativeAPI;
/*     */ import utils.ostools.OS;
/*     */ 
/*     */ public class JWWindowsOS extends JWGenericOS
/*     */ {
/*     */   public static JWWindowsOS getWindowsInstance()
/*     */   {
/*  21 */     return JWGenericOS.winInstance;
/*     */   }
/*     */ 
/*     */   public String getOSNameWithBitness()
/*     */   {
/*  38 */     if (is64BitWindowsOS()) {
/*  39 */       return getOSName() + " x64";
/*     */     }
/*  41 */     return getOSName() + " x86";
/*     */   }
/*     */ 
/*     */   public String getOSName()
/*     */   {
/*  46 */     OSVersionInfo info = getOSVersionInfo();
/*     */ 
/*  49 */     if ((info.dwMajorVersion == 6) && (info.dwMinorVersion == 2) && (info.wProductType == 1)) return "Windows 8";
/*  50 */     if ((info.dwMajorVersion == 6) && (info.dwMinorVersion == 2) && (info.wProductType != 1)) return "Windows Server 2012";
/*  51 */     if ((info.dwMajorVersion == 6) && (info.dwMinorVersion == 1) && (info.wProductType == 1)) return "Windows 7";
/*  52 */     if ((info.dwMajorVersion == 6) && (info.dwMinorVersion == 1) && (info.wProductType != 1)) return "Windows Server 2008 R2";
/*  53 */     if ((info.dwMajorVersion == 6) && (info.dwMinorVersion == 0) && (info.wProductType != 1)) return "Windows Server 2008";
/*  54 */     if ((info.dwMajorVersion == 6) && (info.dwMinorVersion == 0) && (info.wProductType == 1)) return "Windows Vista";
/*  55 */     if ((info.dwMajorVersion == 5) && (info.dwMinorVersion == 2) && ((info.wSuiteMask & 0x8000) != 0)) return "Windows Home Server";
/*  56 */     if ((info.dwMajorVersion == 5) && (info.dwMinorVersion == 1)) return "Windows XP";
/*  57 */     if ((info.dwMajorVersion == 5) && (info.dwMinorVersion == 0)) return "Windows 2000";
/*  58 */     return System.getProperty("os.name");
/*     */   }
/*     */ 
/*     */   public OSVersionInfo getOSVersionInfo()
/*     */   {
/*  66 */     OSVersionInfo info = new OSVersionInfo();
/*  67 */     JWNativeAPI.getInstance().getWindowsVersionInfo(info);
/*  68 */     return info;
/*     */   }
/*     */ 
/*     */   public String getHostname()
/*     */   {
/*  77 */     return JWNativeAPI.getInstance().getHostname();
/*     */   }
/*     */ 
/*     */   public boolean setWindowsAppID(String appID)
/*     */   {
/*  86 */     return JWNativeAPI.getInstance().setAppID(appID);
/*     */   }
/*     */ 
/*     */   public String getUserStartMenuProgramsFolder()
/*     */   {
/*  94 */     return JWNativeAPI.getInstance().getWindowsUserProgramsFolder();
/*     */   }
/*     */ 
/*     */   public String getAllUsersStartMenuProgramsFolder()
/*     */   {
/* 102 */     return JWNativeAPI.getInstance().getWindowsAllProgramsFolder();
/*     */   }
/*     */ 
/*     */   public File getAppStartMenuFolder()
/*     */   {
/* 110 */     return getStartMenuFolder(JWSystem.getAppBundleName());
/*     */   }
/*     */ 
/*     */   public File getStartMenuFolder(String group)
/*     */   {
/*     */     String pfolder;
/*     */     String pfolder;
/* 121 */     if (JWSystem.getInstallType().equalsIgnoreCase("perm_all")) {
/* 122 */       if (OS.isWindows())
/*     */       {
/* 124 */         String pfolder = getAllUsersStartMenuProgramsFolder();
/*     */ 
/* 126 */         if (pfolder.endsWith(File.separator)) {
/* 127 */           pfolder = pfolder.substring(0, pfolder.length() - 1);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 151 */         pfolder = "ERROR - not a windows OS";
/*     */       }
/*     */     }
/*     */     else {
/* 155 */       pfolder = getUserStartMenuProgramsFolder();
/*     */ 
/* 157 */       if (pfolder.endsWith(File.separator)) {
/* 158 */         pfolder = pfolder.substring(0, pfolder.length() - 1);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 174 */     File startmenu = new File(pfolder);
/*     */ 
/* 176 */     if (group == null) {
/* 177 */       return startmenu;
/*     */     }
/*     */ 
/* 180 */     File appmenu = new File(startmenu, group);
/* 181 */     appmenu.mkdirs();
/*     */ 
/* 183 */     return appmenu;
/*     */   }
/*     */ 
/*     */   public void deleteShortcut(File linkfile) throws IOException {
/* 187 */     String linkPath = linkfile.getCanonicalPath();
/* 188 */     if (!linkPath.toLowerCase().endsWith(".lnk")) {
/* 189 */       linkPath = linkPath + ".lnk";
/*     */     }
/* 191 */     new File(linkPath).delete();
/*     */   }
/*     */   public void createShortcut(File linkfile, File target) throws IOException {
/* 194 */     createShortcut(linkfile, "", target, "", target, 0);
/*     */   }
/*     */   public void createShortcut(File linkfile, File target, String args) throws IOException {
/* 197 */     createShortcut(linkfile, "", target, args, target, 0);
/*     */   }
/*     */   public void createShortcut(File linkfile, String linkDesc, File target, String args, File icon, int iconIndex) throws IOException {
/* 200 */     String linkPath = linkfile.getCanonicalPath();
/* 201 */     String targetPath = target.getCanonicalPath();
/* 202 */     String iconPath = icon.getCanonicalPath();
/*     */ 
/* 204 */     if (!linkPath.toLowerCase().endsWith(".lnk")) {
/* 205 */       linkPath = linkPath + ".lnk";
/*     */     }
/*     */ 
/* 208 */     JWNativeAPI.getInstance().createShortcut(linkPath, linkDesc, targetPath, args, iconPath, iconIndex);
/*     */   }
/*     */   public void createShortcutWithAppID(File linkfile, File target, String appID) throws IOException {
/* 211 */     createShortcutWithAppID(linkfile, "", target, "", target, 0, appID);
/*     */   }
/*     */   public void createShortcutWithAppID(File linkfile, File target, String args, String appID) throws IOException {
/* 214 */     createShortcutWithAppID(linkfile, "", target, args, target, 0, appID);
/*     */   }
/*     */   public void createShortcutWithAppID(File linkfile, String linkDesc, File target, String args, File icon, int iconIndex, String appID) throws IOException {
/* 217 */     String linkPath = linkfile.getCanonicalPath();
/* 218 */     String targetPath = target.getCanonicalPath();
/* 219 */     String iconPath = icon.getCanonicalPath();
/*     */ 
/* 221 */     if (!linkPath.toLowerCase().endsWith(".lnk")) {
/* 222 */       linkPath = linkPath + ".lnk";
/*     */     }
/*     */ 
/* 225 */     JWNativeAPI.getInstance().createShortcutWithID(linkPath, linkDesc, targetPath, args, iconPath, iconIndex, appID);
/*     */   }
/*     */   public void deleteWebShortcut(File linkfile) throws IOException {
/* 228 */     new File(linkfile + ".url").delete();
/*     */   }
/*     */   public void createWebShortcut(File linkfile, String targetURL) throws IOException {
/* 231 */     PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(linkfile + ".url")));
/*     */ 
/* 233 */     out.print("[InternetShortcut]\r\n");
/* 234 */     out.print("URL=" + targetURL + "\r\n");
/*     */ 
/* 236 */     out.close();
/*     */   }
/*     */   public void createWebShortcut(File linkfile, String targetURL, File iconFile, int iconIndex) throws IOException {
/* 239 */     PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(linkfile + ".url")));
/*     */ 
/* 241 */     out.print("[InternetShortcut]\r\n");
/* 242 */     out.print("URL=" + targetURL + "\r\n");
/* 243 */     out.print("IconIndex=" + iconIndex + "\r\n");
/* 244 */     out.print("IconFile=" + iconFile.getCanonicalPath() + "\r\n");
/*     */ 
/* 246 */     out.close();
/*     */   }
/*     */ 
/*     */   public int getCurrentProcessID()
/*     */   {
/* 252 */     return JWNativeAPI.getInstance().getProcessID();
/*     */   }
/*     */ 
/*     */   public String getLoggedOnUsername()
/*     */   {
/* 258 */     return JWNativeAPI.getInstance().getUsernameForSession(JWNativeAPI.getInstance().getTsMySessionId());
/*     */   }
/*     */ 
/*     */   public String getLoggedOnUserSID()
/*     */   {
/* 264 */     return JWNativeAPI.getInstance().getSidForUsername(getLoggedOnUsername());
/*     */   }
/*     */ 
/*     */   public void setFrameAlwaysOnTop(Frame window)
/*     */   {
/* 270 */     JWNativeAPI.getInstance().setFrameAlwaysOnTop(window);
/*     */   }
/*     */ 
/*     */   public void setWindowAlwaysOnTop(Window window)
/*     */   {
/* 276 */     JWNativeAPI.getInstance().setWindowAlwaysOnTop(window);
/*     */   }
/*     */ 
/*     */   public String getEnvironmentVariable(String name)
/*     */   {
/* 284 */     return JWNativeAPI.getInstance().getWindowsEnv(name);
/*     */   }
/*     */ 
/*     */   public void makeCurrentProcessHighPriority()
/*     */   {
/* 290 */     JWNativeAPI.getInstance().setProcessPriorityHigh(-1);
/*     */   }
/*     */ 
/*     */   public void makeCurrentProcessNormalPriority()
/*     */   {
/* 296 */     JWNativeAPI.getInstance().setProcessPriorityNormal(-1);
/*     */   }
/*     */ 
/*     */   public void makeCurrentProcessLowPriority()
/*     */   {
/* 302 */     JWNativeAPI.getInstance().setProcessPriorityLow(-1);
/*     */   }
/*     */ 
/*     */   public int[] getMouseLocation()
/*     */   {
/* 309 */     return new int[] { 
/* 310 */       JWNativeAPI.getInstance().getPointerX(), 
/* 311 */       JWNativeAPI.getInstance().getPointerY() };
/*     */   }
/*     */ 
/*     */   public int[] getCaretLocation()
/*     */   {
/* 319 */     return new int[] { 
/* 320 */       JWNativeAPI.getInstance().getCaretX(), 
/* 321 */       JWNativeAPI.getInstance().getCaretY() };
/*     */   }
/*     */ 
/*     */   public boolean is64BitWindowsOS()
/*     */   {
/* 326 */     return !JWNativeAPI.getInstance().is32Bit();
/*     */   }
/*     */ 
/*     */   public class OSVersionInfo
/*     */   {
/*     */     public int dwMajorVersion;
/*     */     public int dwMinorVersion;
/*     */     public int dwBuildNumber;
/*     */     public int dwPlatformId;
/*     */     public short wServicePackMajor;
/*     */     public short wServicePackMinor;
/*     */     public short wSuiteMask;
/*     */     public byte wProductType;
/*     */ 
/*     */     public OSVersionInfo()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.jwutils.JWWindowsOS
 * JD-Core Version:    0.6.2
 */