/*     */ package jwrapper;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import jwrapper.ui.JWLanguage;
/*     */ import utils.files.FileUtil;
/*     */ 
/*     */ public class HeadlessSwipeLoadUtil
/*     */ {
/*  22 */   public static boolean DEBUG_LOAD_SLOW = false;
/*     */   public static final String SmallDownload = "SmallDownload";
/*     */   public static final String SmallNoInternet = "SmallNoInternet";
/*     */   public static final String SmallLaunching = "SmallLaunching";
/*     */   public static final String SmallUninstall = "SmallUninstall";
/*     */   Class imgclazz;
/*     */   Class clazz;
/*     */   Object panel;
/*     */   byte[] buf;
/* 144 */   String title = "";
/*     */ 
/* 246 */   boolean isShowing = false;
/*     */   static Object exampleSplash;
/*     */   static byte[] exampleSplashPNG;
/*     */   static byte[] exampleLogoPNG;
/*     */ 
/*     */   public HeadlessSwipeLoadUtil()
/*     */   {
/*     */     try
/*     */     {
/*  44 */       this.buf = new byte[64000];
/*  45 */       this.clazz = Class.forName("jwrapper.SwipeLoadPanel");
/*  46 */       this.imgclazz = Class.forName("java.awt.Image");
/*  47 */       this.panel = this.clazz.newInstance();
/*  48 */       System.out.println("Loaded progress panel OK");
/*     */     } catch (Throwable t) {
/*  50 */       System.out.println("***WARNING - NON-UI SYSTEM?*** Unable to load panel " + t);
/*  51 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private Object getImage(String name)
/*     */   {
/*  57 */     if (this.panel == null) return null; try
/*     */     {
/*  59 */       return this.clazz.getDeclaredField(name).get(this.panel); } catch (Exception x) {
/*     */     }
/*  61 */     return null;
/*     */   }
/*     */ 
/*     */   public void downloadUrlWithProgress(File dest, URL url) throws IOException
/*     */   {
/*  66 */     URLConnection conn = url.openConnection();
/*  67 */     double len = 0.0D;
/*     */     try
/*     */     {
/*  70 */       len = Double.parseDouble(conn.getHeaderField("content-length"));
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/*  74 */     double sofar = 0.0D;
/*     */ 
/*  76 */     InputStream in = new BufferedInputStream(conn.getInputStream());
/*     */     try
/*     */     {
/*  79 */       OutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
/*     */       try
/*     */       {
/*  82 */         long time = System.currentTimeMillis();
/*     */ 
/*  84 */         Method pro = null;
/*     */         try {
/*  86 */           if (this.panel != null)
/*  87 */             pro = this.clazz.getDeclaredMethod("setProgress", new Class[] { Double.TYPE });
/*     */         } catch (Throwable t) {
/*  89 */           t.printStackTrace();
/*     */         }
/*     */ 
/*  92 */         int n = 0;
/*  93 */         while (n != -1) {
/*  94 */           if (DEBUG_LOAD_SLOW) {
/*  95 */             n = in.read(this.buf, 0, 1024);
/*  96 */             Thread.sleep(100L);
/*     */           } else {
/*  98 */             n = in.read(this.buf);
/*     */           }
/*     */ 
/* 101 */           if (n > 0) {
/* 102 */             out.write(this.buf, 0, n);
/* 103 */             sofar += n;
/*     */ 
/* 105 */             if (len > 0.0D)
/*     */               try {
/* 107 */                 if (System.currentTimeMillis() - time > 90L)
/* 108 */                   pro.invoke(this.panel, new Object[] { new Double(sofar / len) });
/*     */               } catch (Throwable t) {
/* 110 */                 t.printStackTrace();
/*     */               }
/*     */           }
/*     */         }
/*     */       } catch (Throwable t) {
/* 115 */         if ((t instanceof IOException)) throw ((IOException)t);
/*     */ 
/* 117 */         IOException x = new IOException("Problem downloading URL");
/* 118 */         x.initCause(t);
/* 119 */         throw x;
/*     */       }
/*     */       finally
/*     */       {
/*     */         try
/*     */         {
/* 125 */           out.close(); } catch (Throwable localThrowable1) {  } 
/*     */       }try { out.close();
/*     */       } catch (Throwable localThrowable2)
/*     */       {
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 134 */         in.close();
/*     */       } catch (Throwable localThrowable3) {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getTitle() {
/* 141 */     return this.title;
/*     */   }
/*     */ 
/*     */   public void makeUninstaller(String title, Object logo, LPUninstallerListener listener)
/*     */   {
/* 146 */     if (this.panel == null) return;
/* 147 */     this.title = title;
/*     */     try {
/* 149 */       this.clazz.getDeclaredMethod("makeUninstaller", new Class[] { String.class, Object.class, LPUninstallerListener.class }).invoke(this.panel, new Object[] { title, logo, listener }); } catch (Throwable t) {
/* 150 */       t.printStackTrace();
/*     */     }
/*     */   }
/* 153 */   public void makeFrame(String title, Object logo) { if (this.panel == null) return;
/* 154 */     this.title = title;
/*     */     try {
/* 156 */       this.clazz.getDeclaredMethod("makeFrame", new Class[] { String.class, Object.class }).invoke(this.panel, new Object[] { title, logo }); } catch (Throwable t) {
/* 157 */       t.printStackTrace();
/*     */     } } 
/*     */   public void disableButtons() {
/* 160 */     if (this.panel == null) return; try
/*     */     {
/* 162 */       this.clazz.getDeclaredMethod("disableButtons", new Class[0]).invoke(this.panel, new Object[0]); } catch (Throwable t) {
/* 163 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/* 167 */   public void hideProgress() { if (this.panel == null) return; try
/*     */     {
/* 169 */       this.clazz.getDeclaredMethod("hideProgress", new Class[0]).invoke(this.panel, new Object[0]); } catch (Throwable t) {
/* 170 */       t.printStackTrace();
/*     */     } } 
/*     */   public void hideFrame() {
/* 173 */     if (this.panel == null) return; try
/*     */     {
/* 175 */       this.clazz.getDeclaredMethod("hideFrame", new Class[0]).invoke(this.panel, new Object[0]); } catch (Throwable t) {
/* 176 */       t.printStackTrace();
/*     */     }
/*     */   }
/* 179 */   public void setPosition(int[] tmp) { if (this.panel == null) return; try
/*     */     {
/* 181 */       this.clazz.getDeclaredMethod("setPosition", new Class[] { [I.class }).invoke(this.panel, new Object[] { tmp }); } catch (Throwable t) {
/* 182 */       t.printStackTrace();
/*     */     } } 
/*     */   public int[] getPosition() {
/* 185 */     if (this.panel == null) return new int[0]; try
/*     */     {
/* 187 */       return (int[])this.clazz.getDeclaredMethod("getPosition", new Class[0]).invoke(this.panel, new Object[0]); } catch (Throwable t) {
/* 188 */       t.printStackTrace();
/* 189 */     }return new int[0];
/*     */   }
/*     */   public void showFiniteProgress() {
/* 192 */     if (this.panel == null) return; try
/*     */     {
/* 194 */       this.clazz.getDeclaredMethod("showFiniteProgress", new Class[0]).invoke(this.panel, new Object[0]); } catch (Throwable t) {
/* 195 */       t.printStackTrace();
/*     */     }
/*     */   }
/* 198 */   public void showInfiniteProgress() { if (this.panel == null) return; try
/*     */     {
/* 200 */       this.clazz.getDeclaredMethod("showInfiniteProgress", new Class[0]).invoke(this.panel, new Object[0]); } catch (Throwable t) {
/* 201 */       t.printStackTrace();
/*     */     } } 
/*     */   public void preventWindowClose() {
/* 204 */     if (this.panel == null) return; try
/*     */     {
/* 206 */       this.clazz.getDeclaredMethod("preventWindowClose", new Class[0]).invoke(this.panel, new Object[0]); } catch (Throwable t) {
/* 207 */       t.printStackTrace();
/*     */     }
/*     */   }
/* 210 */   public void waitForAllSwipes() { if (this.panel == null) return; try
/*     */     {
/* 212 */       this.clazz.getDeclaredMethod("waitForAllSwipes", new Class[0]).invoke(this.panel, new Object[0]); } catch (Throwable t) {
/* 213 */       t.printStackTrace();
/*     */     } } 
/*     */   public void swipeAllTo(String big, String small) {
/* 216 */     ensureShowing();
/* 217 */     if (this.panel == null) return; try
/*     */     {
/* 219 */       this.clazz.getDeclaredMethod("swipeAllTo", new Class[] { this.imgclazz, this.imgclazz }).invoke(this.panel, new Object[] { getImage(big), getImage(small) }); } catch (Throwable t) {
/* 220 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/* 224 */   public void swipeAllTo(Object big, Object small) { ensureShowing();
/* 225 */     if (this.panel == null) return; try
/*     */     {
/* 227 */       this.clazz.getDeclaredMethod("swipeAllTo", new Class[] { this.imgclazz, this.imgclazz }).invoke(this.panel, new Object[] { big, small }); } catch (Throwable t) {
/* 228 */       t.printStackTrace();
/*     */     } }
/*     */ 
/*     */   private void invokeImageMethod(String method, String image) {
/* 232 */     if (this.panel == null) return; try
/*     */     {
/* 234 */       this.clazz.getDeclaredMethod(method, new Class[] { this.imgclazz }).invoke(this.panel, new Object[] { getImage(image) }); } catch (Throwable t) {
/* 235 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/* 239 */   private void invokeImageMethod(String method, Object image) { if (this.panel == null) return; try
/*     */     {
/* 241 */       this.clazz.getDeclaredMethod(method, new Class[] { this.imgclazz }).invoke(this.panel, new Object[] { image }); } catch (Throwable t) {
/* 242 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isShowingFrame()
/*     */   {
/* 249 */     return this.isShowing;
/*     */   }
/*     */ 
/*     */   public void ensureShowing()
/*     */   {
/* 254 */     if (!this.isShowing) {
/* 255 */       this.isShowing = true;
/* 256 */       if (this.panel == null) return; try
/*     */       {
/* 258 */         this.clazz.getDeclaredMethod("showFrame", new Class[0]).invoke(this.panel, new Object[0]); } catch (Throwable t) {
/* 259 */         t.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 264 */   public void setBigTo(String image) { invokeImageMethod("setBigTo", image); }
/*     */ 
/*     */   public void setSmallTo(String image) {
/* 267 */     invokeImageMethod("setSmallTo", image);
/*     */   }
/*     */   public void swipeBigTo(String image) {
/* 270 */     ensureShowing();
/* 271 */     invokeImageMethod("swipeBigTo", image);
/*     */   }
/*     */   public void swipeSmallTo(String image) {
/* 274 */     ensureShowing();
/* 275 */     invokeImageMethod("swipeSmallTo", image);
/*     */   }
/*     */   public void setBigTo(Object image) {
/* 278 */     invokeImageMethod("setBigTo", image);
/*     */   }
/*     */   public void setSmallTo(Object image) {
/* 281 */     invokeImageMethod("setSmallTo", image);
/*     */   }
/*     */   public void swipeBigTo(Object image) {
/* 284 */     ensureShowing();
/* 285 */     invokeImageMethod("swipeBigTo", image);
/*     */   }
/*     */   public void swipeSmallTo(Object image) {
/* 288 */     ensureShowing();
/* 289 */     invokeImageMethod("swipeSmallTo", image);
/*     */   }
/*     */   public Object loadImage(byte[] dat) {
/*     */     try {
/* 293 */       return Class.forName("javax.imageio.ImageIO").getDeclaredMethod("read", new Class[] { InputStream.class }).invoke(null, new Object[] { new ByteArrayInputStream(dat) });
/*     */     } catch (Exception localException) {
/*     */     }
/* 296 */     return null;
/*     */   }
/*     */   public Object loadImage(File file) {
/*     */     try {
/* 300 */       return Class.forName("javax.imageio.ImageIO").getDeclaredMethod("read", new Class[] { File.class }).invoke(null, new Object[] { file });
/*     */     } catch (Exception localException) {
/*     */     }
/* 303 */     return null;
/*     */   }
/*     */   public Object loadImage(URL url) {
/*     */     try {
/* 307 */       return Class.forName("javax.imageio.ImageIO").getDeclaredMethod("read", new Class[] { URL.class }).invoke(null, new Object[] { url });
/*     */     } catch (Exception localException) {
/*     */     }
/* 310 */     return null;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 349 */     HeadlessSwipeLoadUtil swu = new HeadlessSwipeLoadUtil();
/*     */ 
/* 351 */     File splash = new File(new File("autotest"), "jwtest_splash.png");
/*     */ 
/* 353 */     exampleSplash = swu.loadImage(splash);
/* 354 */     exampleSplashPNG = FileUtil.readFile(splash.getAbsolutePath());
/*     */ 
/* 356 */     File logo = new File(new File("autotest"), "jwtest_logo.png");
/* 357 */     exampleLogoPNG = FileUtil.readFile(logo.getAbsolutePath());
/* 358 */     swu.setBigTo(exampleSplash);
/*     */ 
/* 360 */     swu.setSmallTo(null);
/*     */ 
/* 362 */     swu.makeFrame("SampleApp", null);
/* 363 */     swu.swipeSmallTo(SwipeLoadPanel.SmallNoInternet);
/* 364 */     swu.hideProgress();
/*     */ 
/* 366 */     swu.waitForAllSwipes();
/* 367 */     swu.ensureShowing();
/*     */ 
/* 369 */     Thread.sleep(1000L);
/*     */ 
/* 371 */     swu.setMessage(JWLanguage.getString("ERROR_DOWNLOAD"));
/*     */   }
/*     */ 
/*     */   public void setMessage(String string)
/*     */   {
/* 382 */     if (this.panel == null) return; try
/*     */     {
/* 384 */       this.clazz.getDeclaredMethod("setMessage", new Class[] { String.class }).invoke(this.panel, new Object[] { string }); } catch (Throwable t) {
/* 385 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   static class UninstallExample extends Thread
/*     */     implements LPUninstallerListener
/*     */   {
/*     */     HeadlessSwipeLoadUtil swu;
/*     */ 
/*     */     UninstallExample(HeadlessSwipeLoadUtil swu)
/*     */     {
/* 318 */       this.swu = swu;
/*     */     }
/*     */ 
/*     */     public void doUninstall() {
/* 322 */       this.swu.disableButtons();
/*     */ 
/* 324 */       this.swu.swipeSmallTo("SmallUninstall");
/* 325 */       start();
/*     */     }
/*     */ 
/*     */     public void doExit() {
/* 329 */       System.exit(0);
/*     */     }
/*     */ 
/*     */     public void run() {
/* 333 */       this.swu.waitForAllSwipes();
/*     */       try {
/* 335 */         SelfDelete.deleteSelf(new File("/tmp/doesntexist"), new File[0], this.swu, this.swu.getTitle(), HeadlessSwipeLoadUtil.exampleSplashPNG, HeadlessSwipeLoadUtil.exampleLogoPNG);
/*     */       } catch (IOException x) {
/* 337 */         x.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.HeadlessSwipeLoadUtil
 * JD-Core Version:    0.6.2
 */