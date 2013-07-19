/*     */ package utils.ostools.osx;
/*     */ 
/*     */ import java.awt.Image;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ 
/*     */ public class OSXAdapter
/*     */   implements InvocationHandler
/*     */ {
/*     */   protected Object targetObject;
/*     */   protected Method targetMethod;
/*     */   protected String proxySignature;
/*  15 */   static Object APPLICATION_LOCK = new Object();
/*     */   static Object macOSXApplication;
/*     */   static Class applicationClass;
/*     */ 
/*     */   public static void setQuitHandler(Object target, Method quitHandler)
/*     */   {
/*  22 */     setHandler(new OSXAdapter("handleQuit", target, quitHandler));
/*     */   }
/*     */ 
/*     */   public static void requestUserAttention(boolean isCritical)
/*     */   {
/*     */     try {
/*  28 */       init();
/*  29 */       Method enablePrefsMethod = macOSXApplication.getClass().getDeclaredMethod("requestUserAttention", new Class[] { Boolean.TYPE });
/*  30 */       enablePrefsMethod.invoke(macOSXApplication, new Object[] { Boolean.valueOf(isCritical) });
/*     */     } catch (Exception ex) {
/*  32 */       System.out.println("[OSXAdapter]  could not request user attention");
/*  33 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setDockIconBadge(String badge)
/*     */   {
/*     */     try {
/*  40 */       init();
/*  41 */       Method enablePrefsMethod = macOSXApplication.getClass().getDeclaredMethod("setDockIconBadge", new Class[] { String.class });
/*  42 */       enablePrefsMethod.invoke(macOSXApplication, new Object[] { badge });
/*     */     } catch (Exception ex) {
/*  44 */       System.out.println("[OSXAdapter]  could not set the dock icon badge");
/*  45 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setAppName(String name)
/*     */   {
/*  51 */     System.setProperty("com.apple.mrj.application.apple.menu.about.name", name);
/*     */   }
/*     */ 
/*     */   public static void setAboutHandler(Object target, Method aboutHandler)
/*     */   {
/*  58 */     boolean enableAboutMenu = (target != null) && (aboutHandler != null);
/*  59 */     if (enableAboutMenu) {
/*  60 */       setHandler(new OSXAdapter("handleAbout", target, aboutHandler));
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  65 */       Method enableAboutMethod = macOSXApplication.getClass().getDeclaredMethod("setEnabledAboutMenu", new Class[] { Boolean.TYPE });
/*  66 */       enableAboutMethod.invoke(macOSXApplication, new Object[] { Boolean.valueOf(enableAboutMenu) });
/*     */     } catch (Exception ex) {
/*  68 */       System.out.println("[OSXAdapter]  could not access the About Menu");
/*  69 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setPreferencesHandler(Object target, Method prefsHandler)
/*     */   {
/*  77 */     boolean enablePrefsMenu = (target != null) && (prefsHandler != null);
/*  78 */     if (enablePrefsMenu) {
/*  79 */       setHandler(new OSXAdapter("handlePreferences", target, prefsHandler));
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  84 */       Method enablePrefsMethod = macOSXApplication.getClass().getDeclaredMethod("setEnabledPreferencesMenu", new Class[] { Boolean.TYPE });
/*  85 */       enablePrefsMethod.invoke(macOSXApplication, new Object[] { Boolean.valueOf(enablePrefsMenu) });
/*     */     } catch (Exception ex) {
/*  87 */       System.out.println("[OSXAdapter]  could not access the About Menu");
/*  88 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setFileHandler(Object target, Method fileHandler)
/*     */   {
/*  96 */     setHandler(new OSXAdapter("handleOpenFile", target, fileHandler)
/*     */     {
/*     */       public boolean callTarget(Object appleEvent)
/*     */       {
/* 100 */         if (appleEvent != null)
/*     */           try {
/* 102 */             Method getFilenameMethod = appleEvent.getClass().getDeclaredMethod("getFilename", null);
/* 103 */             String filename = (String)getFilenameMethod.invoke(appleEvent, null);
/* 104 */             this.targetMethod.invoke(this.targetObject, new Object[] { filename });
/*     */           }
/*     */           catch (Exception localException)
/*     */           {
/*     */           }
/* 109 */         return true;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private static void init() throws Exception
/*     */   {
/* 116 */     synchronized (APPLICATION_LOCK)
/*     */     {
/* 118 */       if (macOSXApplication == null)
/*     */       {
/* 120 */         applicationClass = Class.forName("com.apple.eawt.Application");
/* 121 */         macOSXApplication = applicationClass.getConstructor(null).newInstance(null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setHandler(OSXAdapter adapter)
/*     */   {
/*     */     try {
/* 129 */       init();
/* 130 */       Class applicationListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
/* 131 */       Method addListenerMethod = applicationClass.getDeclaredMethod("addApplicationListener", new Class[] { applicationListenerClass });
/*     */ 
/* 133 */       Object osxAdapterProxy = Proxy.newProxyInstance(OSXAdapter.class.getClassLoader(), new Class[] { applicationListenerClass }, adapter);
/* 134 */       addListenerMethod.invoke(macOSXApplication, new Object[] { osxAdapterProxy });
/*     */     } catch (ClassNotFoundException cnfe) {
/* 136 */       System.err.println("This version of Mac OS X does not support the Apple EAWT.  ApplicationEvent handling has been disabled (" + cnfe + ")");
/*     */     } catch (Exception ex) {
/* 138 */       System.err.println("Mac OS X Adapter could not talk to EAWT:");
/* 139 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setDockIcon(Image image)
/*     */   {
/*     */     try
/*     */     {
/* 147 */       init();
/* 148 */       Method method = applicationClass.getMethod("setDockIconImage", new Class[] { Image.class });
/* 149 */       method.invoke(macOSXApplication, new Object[] { image });
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void requestForeground(boolean moveAllWindowsForeward)
/*     */   {
/*     */     try
/*     */     {
/* 162 */       init();
/* 163 */       Method method = applicationClass.getMethod("requestForeground", new Class[] { Boolean.TYPE });
/* 164 */       method.invoke(macOSXApplication, new Object[] { new Boolean(moveAllWindowsForeward) });
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   protected OSXAdapter(String proxySignature, Object target, Method handler)
/*     */   {
/* 176 */     this.proxySignature = proxySignature;
/* 177 */     this.targetObject = target;
/* 178 */     this.targetMethod = handler;
/*     */   }
/*     */ 
/*     */   public boolean callTarget(Object appleEvent)
/*     */     throws InvocationTargetException, IllegalAccessException
/*     */   {
/* 185 */     Object result = this.targetMethod.invoke(this.targetObject, null);
/* 186 */     if (result == null) {
/* 187 */       return true;
/*     */     }
/* 189 */     return Boolean.valueOf(result.toString()).booleanValue();
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method method, Object[] args)
/*     */     throws Throwable
/*     */   {
/* 195 */     if (isCorrectMethod(method, args)) {
/* 196 */       boolean handled = callTarget(args[0]);
/* 197 */       setApplicationEventHandled(args[0], handled);
/*     */     }
/*     */ 
/* 200 */     return null;
/*     */   }
/*     */ 
/*     */   protected boolean isCorrectMethod(Method method, Object[] args)
/*     */   {
/* 206 */     return (this.targetMethod != null) && (this.proxySignature.equals(method.getName())) && (args.length == 1);
/*     */   }
/*     */ 
/*     */   protected void setApplicationEventHandled(Object event, boolean handled)
/*     */   {
/* 212 */     if (event != null)
/*     */       try {
/* 214 */         Method setHandledMethod = event.getClass().getDeclaredMethod("setHandled", new Class[] { Boolean.TYPE });
/*     */ 
/* 216 */         setHandledMethod.invoke(event, new Object[] { Boolean.valueOf(handled) });
/*     */       } catch (Exception ex) {
/* 218 */         System.out.println("[OSXAdapter] was unable to handle an ApplicationEvent: " + event);
/* 219 */         ex.printStackTrace();
/*     */       }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.ostools.osx.OSXAdapter
 * JD-Core Version:    0.6.2
 */