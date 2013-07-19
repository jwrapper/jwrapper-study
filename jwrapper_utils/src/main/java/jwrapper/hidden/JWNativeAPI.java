/*     */ package jwrapper.hidden;
/*     */ 
/*     */ import java.awt.Frame;
/*     */ import java.awt.Window;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ import jwrapper.jwutils.JWWindowsOS.OSVersionInfo;
/*     */ 
/*     */ public class JWNativeAPI
/*     */ {
/*     */   public static final String JWUTILS_PREFIX = "jwutils_";
/*     */   private static final String LIB_WINDOWS_32 = "jwutils_win32";
/*     */   private static final String LIB_WINDOWS_64 = "jwutils_win64";
/*     */   private static final String LIB_MACOS_32 = "jwutils_macos32";
/*     */   private static final String LIB_MACOS_64 = "jwutils_macos64";
/*     */   private static final String LIB_LINUX_32 = "jwutils_linux32";
/*     */   private static final String LIB_LINUX_64 = "jwutils_linux64";
/* 155 */   public static int TS_GET_SESSION_FAILED = -999;
/*     */   private KeybookHookCallback keyboardHookCallback;
/* 261 */   private boolean isKeyboardHookEnabled = false;
/* 262 */   private boolean hasUIGotFocus = true;
/* 263 */   private Object HOOKS_LOCK = new Object();
/*     */ 
/*     */   public static String getAppropriateLibName(boolean fullname)
/*     */   {
/*  25 */     boolean x86_64 = System.getProperty("os.arch").toLowerCase().indexOf("64") != -1;
/*  26 */     String osname = System.getProperty("os.name").toLowerCase();
/*     */ 
/*  28 */     if (x86_64)
/*  29 */       System.out.println("[JWrapperNative] Detected 64-bit architecture");
/*     */     else {
/*  31 */       System.out.println("[JWrapperNative] Detected 32-bit architecture");
/*     */     }
/*     */ 
/*  34 */     if (osname.indexOf("win") != -1) {
/*  35 */       System.out.println("[JWrapperNative] Detected Windows OS");
/*  36 */       if (x86_64) {
/*  37 */         if (fullname) {
/*  38 */           return "jwutils_win64.dll";
/*     */         }
/*  40 */         return "jwutils_win64";
/*     */       }
/*     */ 
/*  43 */       if (fullname) {
/*  44 */         return "jwutils_win32.dll";
/*     */       }
/*  46 */       return "jwutils_win32";
/*     */     }
/*     */ 
/*  49 */     if ((osname.indexOf("mac") != -1) || (osname.indexOf("darwin") != -1)) {
/*  50 */       System.out.println("[JWrapperNative] Detected Mac OS");
/*  51 */       if (x86_64) {
/*  52 */         if (fullname) {
/*  53 */           return "libjwutils_macos64.jnilib";
/*     */         }
/*  55 */         return "jwutils_macos64";
/*     */       }
/*     */ 
/*  58 */       if (fullname) {
/*  59 */         return "libjwutils_macos32.jnilib";
/*     */       }
/*  61 */       return "jwutils_macos32";
/*     */     }
/*     */ 
/*  64 */     if (osname.indexOf("lin") != -1) {
/*  65 */       System.out.println("[JWrapperNative] Detected Linux OS");
/*  66 */       if (x86_64) {
/*  67 */         if (fullname) {
/*  68 */           return "libjwutils_linux64.so";
/*     */         }
/*  70 */         return "jwutils_linux64";
/*     */       }
/*     */ 
/*  73 */       if (fullname) {
/*  74 */         return "libjwutils_linux32.so";
/*     */       }
/*  76 */       return "jwutils_linux32";
/*     */     }
/*     */ 
/*  80 */     System.out.println("[JWrapperNative] Unknown OS");
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */   public static void loadLibraryFrom(File dir)
/*     */     throws IOException
/*     */   {
/* 130 */     System.load(new File(dir, getAppropriateLibName(true)).getCanonicalPath());
/*     */   }
/*     */ 
/*     */   public static JWNativeAPI getInstance() {
/* 134 */     return new JWNativeAPI();
/*     */   }
/*     */ 
/*     */   public native String getHostname();
/*     */ 
/*     */   public native void setPointer(int paramInt1, int paramInt2);
/*     */ 
/*     */   public native void mouseDown(int paramInt);
/*     */ 
/*     */   public native void mouseUp(int paramInt);
/*     */ 
/*     */   public native int getTsMySessionId();
/*     */ 
/*     */   public native int getTsConsoleSessionId();
/*     */ 
/*     */   public native int getPointerX();
/*     */ 
/*     */   public native int getPointerY();
/*     */ 
/*     */   public native int getCaretX();
/*     */ 
/*     */   public native int getCaretY();
/*     */ 
/*     */   public native boolean is32Bit();
/*     */ 
/*     */   public native boolean setAppID(String paramString);
/*     */ 
/*     */   public native void createShortcut(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt);
/*     */ 
/*     */   public native void createShortcutWithID(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt, String paramString6);
/*     */ 
/*     */   public void setFrameAlwaysOnTop(Frame window)
/*     */   {
/* 179 */     setForceOnTop(window.getTitle());
/*     */   }
/*     */   public void setWindowAlwaysOnTop(Window window) {
/* 182 */     String name = System.currentTimeMillis() + "_" + window.hashCode();
/*     */     try
/*     */     {
/* 185 */       Object peer = window.getPeer();
/* 186 */       Class c = peer.getClass();
/* 187 */       while (c != null) {
/*     */         try {
/* 189 */           Method method = c.getDeclaredMethod("setTitle", new Class[] { String.class });
/* 190 */           method.setAccessible(true);
/* 191 */           method.invoke(peer, new Object[] { name });
/* 192 */           setForceOnTop(name);
/*     */         } catch (NoSuchMethodException localNoSuchMethodException) {
/*     */         }
/* 195 */         c = c.getSuperclass();
/*     */       }
/*     */     } catch (Exception e) {
/* 198 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public native String getWindowsEnv(String paramString);
/*     */ 
/*     */   public native String getWindowsUserProgramsFolder();
/*     */ 
/*     */   public native String getWindowsAllProgramsFolder();
/*     */ 
/*     */   public native void setForceOnTop(String paramString);
/*     */ 
/*     */   public native void jiggleMouseBySendInput();
/*     */ 
/*     */   public native boolean regDeleteValue(String paramString1, String paramString2, String paramString3);
/*     */ 
/*     */   public native boolean regDeleteKey(String paramString1, String paramString2, String paramString3);
/*     */ 
/*     */   public native boolean regSet(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5);
/*     */ 
/*     */   public native String regGet(String paramString1, String paramString2, String paramString3);
/*     */ 
/*     */   public native String[] regGetChildren(String paramString1, String paramString2);
/*     */ 
/*     */   public native boolean regCreateKey(String paramString1, String paramString2);
/*     */ 
/*     */   private native void installHook();
/*     */ 
/*     */   public void installKeyboardHookAndBlockThread(KeybookHookCallback keyboardHookCallback)
/*     */   {
/* 234 */     System.out.println("[JWrapperNative] Installing keyboard hook");
/* 235 */     this.keyboardHookCallback = keyboardHookCallback;
/* 236 */     installHook();
/*     */   }
/*     */ 
/*     */   public native void uninstallHook();
/*     */ 
/*     */   private native void setHookEnabled(boolean paramBoolean);
/*     */ 
/*     */   public void setKeyboardHookEnabled(boolean enabled)
/*     */   {
/* 253 */     System.out.println("[JWrapperNative] Keyboard hook enabled state has changed (setting to " + enabled + ")");
/* 254 */     synchronized (this.HOOKS_LOCK)
/*     */     {
/* 256 */       this.isKeyboardHookEnabled = enabled;
/* 257 */       setHookEnabled((this.isKeyboardHookEnabled) && (this.hasUIGotFocus));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setUIHasFocus(boolean hasFocus)
/*     */   {
/* 267 */     System.out.println("[JWrapperNative] UI has focus has changed (now is " + hasFocus + ")");
/* 268 */     synchronized (this.HOOKS_LOCK)
/*     */     {
/* 270 */       this.hasUIGotFocus = hasFocus;
/* 271 */       setHookEnabled((this.isKeyboardHookEnabled) && (hasFocus));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBlockInputHookEnabled(boolean enabled)
/*     */   {
/* 282 */     System.out.println("[JWrapperNative] Input hook enabled state has changed (now is " + enabled + ")");
/* 283 */     setInputHookEnabled(enabled);
/*     */   }
/*     */ 
/*     */   private native void setInputHookEnabled(boolean paramBoolean);
/*     */ 
/*     */   public native void installMouseHook();
/*     */ 
/*     */   public native void uninstallMouseHook();
/*     */ 
/*     */   public native void setMouseHookEnabled(boolean paramBoolean);
/*     */ 
/*     */   public native String getUsernameForSession(int paramInt);
/*     */ 
/*     */   public native String getCurrentUserSid();
/*     */ 
/*     */   public native int getProcessID();
/*     */ 
/*     */   public native boolean setProcessPriorityHigh(int paramInt);
/*     */ 
/*     */   public native boolean setProcessPriorityLow(int paramInt);
/*     */ 
/*     */   public native boolean setProcessPriorityNormal(int paramInt);
/*     */ 
/*     */   public native String getSidForUsername(String paramString);
/*     */ 
/*     */   public native boolean setCurrentDirectory(String paramString);
/*     */ 
/*     */   public native void showApplication();
/*     */ 
/*     */   public String getConsoleUserSid()
/*     */   {
/* 346 */     int console = getInstance().getTsConsoleSessionId();
/* 347 */     System.out.println("[JWrapperNative] Console session is " + console);
/* 348 */     String username = getInstance().getUsernameForSession(console);
/* 349 */     System.out.println("[JWrapperNative] Username for session " + console + " is " + username);
/* 350 */     String sid = getInstance().getSidForUsername(username);
/* 351 */     System.out.println("[JWrapperNative] Sid for " + username + " is " + sid);
/* 352 */     return sid;
/*     */   }
/*     */ 
/*     */   public native void getWindowsVersionInfo(JWWindowsOS.OSVersionInfo paramOSVersionInfo);
/*     */ 
/*     */   public static abstract interface KeybookHookCallback
/*     */   {
/*     */     public abstract void keyEvent(int paramInt, boolean paramBoolean);
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.hidden.JWNativeAPI
 * JD-Core Version:    0.6.2
 */