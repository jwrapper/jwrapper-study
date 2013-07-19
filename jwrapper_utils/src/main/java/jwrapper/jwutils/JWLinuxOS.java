/*     */ package jwrapper.jwutils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import jwrapper.os.linux.util.DesktopShortcutUtil;
/*     */ import utils.progtools.Cache;
/*     */ 
/*     */ public class JWLinuxOS extends JWGenericOS
/*     */ {
/*     */   public static final String SHORTCUT_CATEGORY_AUDIOVIDEO = "AudioVideo";
/*     */   public static final String SHORTCUT_CATEGORY_DEVELOPMENT = "Development";
/*     */   public static final String SHORTCUT_CATEGORY_EDUCATION = "Education";
/*     */   public static final String SHORTCUT_CATEGORY_GAME = "Game";
/*     */   public static final String SHORTCUT_CATEGORY_GRAPHICS = "Graphics";
/*     */   public static final String SHORTCUT_CATEGORY_NETWORK = "Network";
/*     */   public static final String SHORTCUT_CATEGORY_OFFICE = "Office";
/*     */   public static final String SHORTCUT_CATEGORY_SCIENCE = "Science";
/*     */   public static final String SHORTCUT_CATEGORY_SETTINGS = "Settings";
/*     */   public static final String SHORTCUT_CATEGORY_SYSTEM = "System";
/*     */   public static final String SHORTCUT_CATEGORY_UTILITY = "Utility";
/* 100 */   static Cache cmdCache = new Cache(100);
/*     */ 
/*     */   public static JWLinuxOS getLinuxInstance()
/*     */   {
/*  15 */     return JWGenericOS.linInstance;
/*     */   }
/*     */ 
/*     */   public static void deleteApplicationStartShortcut(String linkFilename, boolean allUsers)
/*     */     throws IOException
/*     */   {
/*  31 */     DesktopShortcutUtil util = new DesktopShortcutUtil(1, "ignore", null);
/*  32 */     if (allUsers)
/*  33 */       util.deleteForAllUsers(linkFilename);
/*     */     else
/*  35 */       util.deleteForThisUser(linkFilename);
/*     */   }
/*     */ 
/*     */   public static void createApplicationStartShortcut(String linkFilename, String name, String targetCommand, boolean allUsers) throws IOException
/*     */   {
/*  40 */     DesktopShortcutUtil util = new DesktopShortcutUtil(1, name, targetCommand);
/*  41 */     if (allUsers)
/*  42 */       util.writeForAllUsers(linkFilename);
/*     */     else
/*  44 */       util.writeForThisUser(linkFilename);
/*     */   }
/*     */ 
/*     */   public static void createApplicationDesktopShortcut(String name, String targetCommand) throws IOException {
/*  48 */     DesktopShortcutUtil util = new DesktopShortcutUtil(1, name, targetCommand);
/*  49 */     util.writeDesktopShortcutForThisUser(name);
/*     */   }
/*     */ 
/*     */   public static void deleteWebpageStartShortcut(String linkFilename, boolean allUsers) throws IOException {
/*  53 */     DesktopShortcutUtil util = new DesktopShortcutUtil(2, "ignore", null);
/*  54 */     if (allUsers)
/*  55 */       util.deleteForAllUsers(linkFilename);
/*     */     else
/*  57 */       util.deleteForThisUser(linkFilename);
/*     */   }
/*     */ 
/*     */   public static void createWebpageStartShortcut(String linkFilename, String name, String url, boolean allUsers) throws IOException
/*     */   {
/*  62 */     DesktopShortcutUtil util = new DesktopShortcutUtil(2, name, null);
/*  63 */     util.setURL(url);
/*  64 */     if (allUsers)
/*  65 */       util.writeForAllUsers(linkFilename);
/*     */     else
/*  67 */       util.writeForThisUser(linkFilename);
/*     */   }
/*     */ 
/*     */   public static void createWebpageDesktopShortcut(String name, String url) throws IOException {
/*  71 */     DesktopShortcutUtil util = new DesktopShortcutUtil(2, name, null);
/*  72 */     util.setURL(url);
/*  73 */     util.writeDesktopShortcutForThisUser(name);
/*     */   }
/*     */ 
/*     */   public static boolean isCommandAvailable(String command)
/*     */   {
/*     */     try
/*     */     {
/*  83 */       Boolean available = (Boolean)cmdCache.getFromCache(command);
/*     */ 
/*  85 */       if (available == null) {
/*  86 */         Process p = Runtime.getRuntime().exec("which " + command);
/*  87 */         available = new Boolean(p.waitFor() == 0);
/*  88 */         cmdCache.addToCache(command, available);
/*     */       }
/*     */ 
/*  91 */       return available.booleanValue();
/*     */     } catch (IOException x) {
/*  93 */       System.out.println("[JWLinuxOS] Unable to check for command " + command + " (" + x + ") will return false");
/*  94 */       return false;
/*     */     } catch (InterruptedException x) {
/*  96 */       System.out.println("[JWLinuxOS] Unable to check for command " + command + " (" + x + ") will return false");
/*  97 */     }return false;
/*     */   }
/*     */ 
/*     */   public static boolean isGkSudoAvailable()
/*     */   {
/* 108 */     return isCommandAvailable("gksudo");
/*     */   }
/*     */ 
/*     */   public static boolean isKdeSudoAvailable() {
/* 112 */     return isCommandAvailable("kdesudo");
/*     */   }
/*     */ 
/*     */   public static int getGkSudoElevationFailedReturnCode()
/*     */   {
/* 120 */     return 1;
/*     */   }
/*     */ 
/*     */   public static int getGkSudoElevationCancelledReturnCode()
/*     */   {
/* 128 */     return 255;
/*     */   }
/*     */ 
/*     */   public static boolean isPkExecAvailable()
/*     */   {
/* 138 */     return isCommandAvailable("pkexec");
/*     */   }
/*     */ 
/*     */   public static int getPkExecElevationFailedReturnCode()
/*     */   {
/* 147 */     return 127;
/*     */   }
/*     */ 
/*     */   public static int getPkExecElevationCancelledReturnCode()
/*     */   {
/* 155 */     return 126;
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.jwutils.JWLinuxOS
 * JD-Core Version:    0.6.2
 */