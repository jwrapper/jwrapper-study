/*     */ package utils.vm;
/*     */ 
/*     */ import [Ljava.lang.String;;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.Socket;
/*     */ 
/*     */ public class Preloader
/*     */ {
/*  28 */   public static int COMMS_VERSION = -10002;
/*     */   public static final boolean SIMULATE_PRELOADER_PROCESS_FAILURE = false;
/*     */   public static final boolean SIMULATE_PRELOADER_COMMS_FAILURE = false;
/*     */   public static final boolean SIMULATE_PRELOADER_COMMS_HANG = false;
/*  34 */   public static long DEFAULT_SPIN_UP_WAIT = 15000L;
/*     */ 
/*  36 */   boolean cancelled = false;
/*  37 */   Object LOCK = new Object();
/*     */   CachedJvmCommunicator cached;
/*  39 */   VMFork forker = new VMFork(Preloader.class.getName(), new String[0]);
/*     */   String[] preJvmExec;
/*     */   ReplaceThread immediateRpt;
/* 191 */   Object fork_LOCK = new Object();
/*     */ 
/* 260 */   private static Socket MAIN_COMMS_SOCKET = null;
/*     */ 
/* 441 */   private static boolean doPreloadFinish = false;
/*     */ 
/*     */   public String getForkerAsString()
/*     */   {
/*  46 */     return this.forker;
/*     */   }
/*     */ 
/*     */   public void startup(VMFork forkWithThis, long waitBeforeSpinUp) throws IOException {
/*  50 */     startup(forkWithThis, waitBeforeSpinUp, null);
/*     */   }
/*     */ 
/*     */   public void startup(VMFork forkWithThis, long waitBeforeSpinUp, String[] preJvmExec) throws IOException {
/*  54 */     this.preJvmExec = preJvmExec;
/*  55 */     this.forker = forkWithThis;
/*  56 */     this.forker.setClassName(Preloader.class.getName());
/*  57 */     this.forker.setClassArgs(new String[0]);
/*  58 */     System.out.println("[Preloader] starting up");
/*  59 */     synchronized (this.LOCK) {
/*  60 */       if (waitBeforeSpinUp == 0L) {
/*  61 */         this.immediateRpt = new ReplaceThread(waitBeforeSpinUp);
/*  62 */         this.immediateRpt.start();
/*     */       } else {
/*  64 */         new ReplaceThread(waitBeforeSpinUp).start();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void cancelAndShutdown()
/*     */   {
/* 109 */     this.cancelled = true;
/*     */     try {
/* 111 */       this.cached.destroy();
/*     */     } catch (Throwable localThrowable) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public Process launch(String claz, String[] args) throws IOException {
/* 117 */     return launch(claz, args, false);
/*     */   }
/*     */ 
/*     */   public Process launch(String claz, String[] args, boolean justLaunchNoPreload) throws IOException {
/* 121 */     if (justLaunchNoPreload) {
/* 122 */       System.out.println("[Preloader] launching " + claz + " " + args.length + " with NO PRELOAD");
/*     */ 
/* 124 */       return startNonPreloadedVM(claz, args);
/*     */     }
/*     */ 
/* 127 */     System.out.println("[Preloader] launching " + claz + " " + args.length);
/* 128 */     CachedJvmCommunicator mine = null;
/*     */     try {
/* 130 */       if (this.immediateRpt != null) {
/* 131 */         System.out.println("[Preloader] joining immediate startup thread");
/* 132 */         this.immediateRpt.join();
/* 133 */         this.immediateRpt = null;
/* 134 */         System.out.println("[Preloader] done joining immediate startup thread");
/*     */       }
/*     */     } catch (Exception localException1) {
/*     */     }
/* 138 */     synchronized (this.LOCK) {
/* 139 */       if (this.cached != null) {
/* 140 */         mine = this.cached;
/* 141 */         this.cached = null;
/* 142 */         new ReplaceThread().start();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 148 */     if (mine == null) {
/* 149 */       System.out.println("[Preloader] nothing cached, launching now");
/* 150 */       mine = startCachedVM();
/*     */     }
/*     */     else {
/* 153 */       System.out.println("[Preloader] using cached");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 158 */       out = mine.getOutputStream();
/*     */     }
/*     */     catch (Exception x)
/*     */     {
/*     */       OutputStream out;
/* 160 */       System.out.println("[Preloader] Preloading failed so attempting to start standard VM (" + x + ")");
/* 161 */       x.printStackTrace();
/*     */ 
/* 163 */       return startNonPreloadedVM(claz, args);
/*     */     }
/*     */     OutputStream out;
/* 166 */     writeStringUTF8(out, claz);
/* 167 */     writeInt(out, args.length);
/* 168 */     for (int i = 0; i < args.length; i++) {
/* 169 */       writeStringUTF8(out, args[i]);
/*     */     }
/* 171 */     out.flush();
/*     */ 
/* 173 */     new CachedJvmCommunicatorShutdown(mine).start();
/*     */ 
/* 175 */     return mine.getProcess();
/*     */   }
/*     */ 
/*     */   private Process startNonPreloadedVM(String clazz, String[] cargs)
/*     */     throws IOException
/*     */   {
/* 197 */     this.forker.setClassName(clazz);
/* 198 */     this.forker.setClassArgs(cargs);
/*     */ 
/* 200 */     System.out.println("[Preloader] (non-preloaded) forking " + this.forker);
/*     */     Process p;
/*     */     Process p;
/* 202 */     if (this.preJvmExec != null)
/* 203 */       p = this.forker.fork(this.preJvmExec);
/*     */     else {
/* 205 */       p = this.forker.fork();
/*     */     }
/*     */ 
/* 208 */     return p;
/*     */   }
/*     */ 
/*     */   private CachedJvmCommunicator startCachedVM()
/*     */     throws IOException
/*     */   {
/* 214 */     synchronized (this.fork_LOCK)
/*     */     {
/* 218 */       CachedJvmCommunicator comm = new CachedJvmCommunicator();
/*     */       try
/*     */       {
/* 225 */         this.forker.setClassName(Preloader.class.getName());
/* 226 */         this.forker.setClassArgs(comm.getPreloaderClassArgs());
/*     */ 
/* 228 */         System.out.println("[Preloader] forking " + this.forker);
/*     */         Process p;
/*     */         Process p;
/* 230 */         if (this.preJvmExec != null)
/* 231 */           p = this.forker.fork(this.preJvmExec);
/*     */         else {
/* 233 */           p = this.forker.fork();
/*     */         }
/*     */ 
/* 236 */         new Printer(p.getErrorStream(), System.out).start();
/* 237 */         new Printer(p.getInputStream(), System.out).start();
/*     */ 
/* 240 */         comm.setProcess(p);
/*     */ 
/* 242 */         return comm;
/*     */       } catch (IOException xx) {
/* 244 */         comm.destroy();
/* 245 */         throw xx;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void load(String claz) {
/* 251 */     if (claz.indexOf(".CVS") != -1)
/* 252 */       return;
/*     */     try {
/* 254 */       Preloader.class.getClassLoader().loadClass(claz);
/*     */     }
/*     */     catch (Throwable localThrowable)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void killCommsAndExitToForceNoPreload()
/*     */   {
/* 263 */     if (MAIN_COMMS_SOCKET != null) {
/*     */       try {
/* 265 */         MAIN_COMMS_SOCKET.close();
/* 266 */         Thread.sleep(750L); } catch (Throwable localThrowable) {
/*     */       }
/* 268 */       System.exit(0);
/*     */     } else {
/* 270 */       System.out.println("WARNING unable to kill main comms socket (null) ?");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 280 */     System.setProperty("apple.awt.UIElement", "true");
/*     */ 
/* 292 */     for (int i = 0; i < Loadme.loadme.length; i++) {
/* 293 */       load(Loadme.loadme[i]);
/*     */     }
/*     */ 
/* 300 */     System.out.println("[Preloader] Trying preload startup");
/*     */     try {
/* 302 */       Preloader.class.getClassLoader().loadClass("com.vm.preloader.PreloadStartup").newInstance();
/* 303 */       System.out.println("[Preloader] loaded PreloadStartup OK");
/*     */ 
/* 306 */       doPreloadFinish = true;
/*     */     } catch (Throwable t) {
/* 308 */       System.out.println(t);
/*     */     }
/*     */ 
/* 311 */     System.out.println("[Preloader] Trying custom preloaders");
/*     */     try {
/* 313 */       Preloader.class.getClassLoader().loadClass("com.aem.SHPreload").newInstance();
/* 314 */       System.out.println("[Preloader] loaded SHPreload OK");
/*     */     } catch (Throwable t) {
/* 316 */       System.out.println(t);
/*     */     }
/*     */     try {
/* 319 */       Preloader.class.getClassLoader().loadClass("com.aem.SHPreloadUI").newInstance();
/* 320 */       System.out.println("[Preloader] loaded SHPreloadUI OK");
/*     */     } catch (Throwable t) {
/* 322 */       System.out.println(t);
/*     */     }
/*     */     try {
/* 325 */       Preloader.class.getClassLoader().loadClass("javax.swing.ImageIcon").newInstance();
/* 326 */       System.out.println("[Preloader] loaded ImageIcon OK");
/*     */     } catch (Throwable t) {
/* 328 */       System.out.println(t);
/*     */     }
/*     */     try {
/* 331 */       Preloader.class.getClassLoader().loadClass("javax.swing.JTree").newInstance();
/* 332 */       System.out.println("[Preloader] loaded JTree OK");
/*     */     } catch (Throwable t) {
/* 334 */       System.out.println(t);
/*     */     }
/*     */     try
/*     */     {
/* 338 */       Socket sock = null;
/*     */ 
/* 343 */       sock = new Socket("localhost", Integer.parseInt(args[0]));
/*     */ 
/* 345 */       MAIN_COMMS_SOCKET = sock;
/*     */ 
/* 347 */       InputStream in = sock.getInputStream();
/*     */ 
/* 359 */       int CLIENT_COMMS_VERSION = readInt(in);
/*     */ 
/* 364 */       System.out.println("[Preloader] client comms version is " + CLIENT_COMMS_VERSION);
/* 365 */       System.out.println("[Preloader] our comms version is " + COMMS_VERSION);
/*     */ 
/* 367 */       sock.setSoTimeout(0);
/*     */ 
/* 369 */       String clazz = null;
/*     */ 
/* 372 */       if ((CLIENT_COMMS_VERSION == 1) || 
/* 373 */         (CLIENT_COMMS_VERSION < 0))
/*     */       {
/* 378 */         if (CLIENT_COMMS_VERSION == 1) {
/* 379 */           System.out.println("[Preloader] client side is mid-way 3.12 release so protocol is acceptable");
/*     */         }
/* 382 */         else if (CLIENT_COMMS_VERSION > COMMS_VERSION)
/*     */         {
/* 384 */           System.out.println("[Preloader] client side is too new for us to communicate, exiting");
/* 385 */           sock.close();
/* 386 */           System.exit(0);
/*     */         }
/* 388 */         else if (CLIENT_COMMS_VERSION == COMMS_VERSION) {
/* 389 */           System.out.println("[Preloader] client side comms version matches so protocol is acceptable");
/*     */         }
/*     */         else
/*     */         {
/* 393 */           System.out.println("[Preloader] client side is too old for us to communicate, exiting");
/* 394 */           System.exit(0);
/*     */         }
/*     */ 
/* 404 */         StreamDeathDetector sdd = new StreamDeathDetector(sock.getInputStream(), sock.getOutputStream());
/* 405 */         if (!sdd.waitUntilReadyOrDead()) {
/* 406 */           System.out.println("[Preloader] stream died, exiting");
/* 407 */           System.exit(0);
/*     */         }
/*     */       }
/*     */       else {
/* 411 */         clazz = readStringUTF8(in, CLIENT_COMMS_VERSION);
/*     */ 
/* 413 */         System.out.println("[Preloader] Early 3.12 SG install, unable to check for mismatched console session and restart - must repair + upgrade service");
/*     */ 
/* 416 */         doPreloadFinish = false;
/*     */       }
/*     */ 
/* 419 */       if (clazz == null) {
/* 420 */         clazz = readStringUTF8(in);
/*     */       }
/* 422 */       int argc = readInt(in);
/* 423 */       String[] cargs = new String[argc];
/* 424 */       for (int i = 0; i < argc; i++) {
/* 425 */         cargs[i] = readStringUTF8(in);
/*     */       }
/*     */ 
/* 428 */       if (sock != null) {
/* 429 */         sock.close();
/*     */       }
/*     */ 
/* 432 */       Class target = Preloader.class.getClassLoader().loadClass(clazz);
/* 433 */       Method main = target.getDeclaredMethod("main", new Class[] { [Ljava.lang.String.class });
/* 434 */       main.invoke(null, new Object[] { cargs });
/*     */     } catch (Throwable t) {
/* 436 */       t.printStackTrace();
/* 437 */       System.exit(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final void preloadFinish()
/*     */   {
/* 447 */     if (!doPreloadFinish) return;
/*     */ 
/* 449 */     System.out.println("[Preloader] Trying preload finish");
/*     */     try {
/* 451 */       Preloader.class.getClassLoader().loadClass("com.vm.preloader.PreloadFinish").newInstance();
/* 452 */       System.out.println("[Preloader] loaded PreloadFinish OK");
/*     */     } catch (Throwable t) {
/* 454 */       System.out.println(t);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeStringUTF8(OutputStream out, String n)
/*     */     throws IOException
/*     */   {
/* 582 */     writeBytes(out, n.getBytes("UTF-8"));
/*     */   }
/*     */ 
/*     */   public static void writeBytes(OutputStream out, byte[] b) throws IOException {
/* 586 */     writeInt(out, b.length);
/* 587 */     out.write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public static String readStringUTF8(InputStream in) throws IOException {
/* 591 */     return new String(readBytes(in), "UTF-8");
/*     */   }
/*     */ 
/*     */   public static byte[] readBytes(InputStream in) throws IOException
/*     */   {
/* 596 */     int len = readInt(in);
/* 597 */     byte[] b = new byte[len];
/*     */ 
/* 599 */     int red = 0;
/* 600 */     int tot = 0;
/*     */ 
/* 602 */     while (tot < len) {
/* 603 */       red = in.read(b, tot, len - tot);
/* 604 */       if (red == -1) {
/* 605 */         throw new EOFException("End of stream");
/*     */       }
/* 607 */       tot += red;
/*     */     }
/*     */ 
/* 611 */     return b;
/*     */   }
/*     */ 
/*     */   public static String readStringUTF8(InputStream in, int len) throws IOException {
/* 615 */     return new String(readBytes(in, len), "UTF-8");
/*     */   }
/*     */ 
/*     */   public static byte[] readBytes(InputStream in, int len) throws IOException {
/* 619 */     byte[] b = new byte[len];
/*     */ 
/* 621 */     int red = 0;
/* 622 */     int tot = 0;
/*     */ 
/* 624 */     while (tot < len) {
/* 625 */       red = in.read(b, tot, len - tot);
/* 626 */       if (red == -1) {
/* 627 */         throw new EOFException("End of stream");
/*     */       }
/* 629 */       tot += red;
/*     */     }
/*     */ 
/* 633 */     return b;
/*     */   }
/*     */   public static int readInt(InputStream in) throws IOException {
/* 636 */     int n = 0;
/* 637 */     int r = 0;
/*     */ 
/* 639 */     for (int i = 0; i < 4; i++) {
/* 640 */       r = in.read();
/* 641 */       if (r == -1) throw new EOFException("End of stream");
/* 642 */       n = n << 8 | r;
/*     */     }
/*     */ 
/* 645 */     return n;
/*     */   }
/*     */ 
/*     */   public static void writeInt(OutputStream out, int n) throws IOException
/*     */   {
/* 650 */     out.write((byte)(n >>> 24));
/* 651 */     out.write((byte)(n >>> 16));
/* 652 */     out.write((byte)(n >>> 8));
/* 653 */     out.write((byte)n);
/*     */   }
/*     */ 
/*     */   public static void writeBoolean(OutputStream out, boolean n)
/*     */     throws IOException
/*     */   {
/* 659 */     if (n)
/* 660 */       out.write(255);
/*     */     else
/* 662 */       out.write(238);
/*     */   }
/*     */ 
/*     */   public static boolean readBoolean(InputStream in)
/*     */     throws IOException
/*     */   {
/* 669 */     int n = in.read();
/* 670 */     if (n == -1) throw new EOFException("End of stream");
/*     */ 
/* 672 */     if (n == 255) {
/* 673 */       return true;
/*     */     }
/* 675 */     return false;
/*     */   }
/*     */ 
/*     */   class CachedJvmCommunicatorShutdown extends Thread
/*     */   {
/*     */     CachedJvmCommunicator jvm;
/*     */ 
/*     */     public CachedJvmCommunicatorShutdown(CachedJvmCommunicator jvm)
/*     */     {
/* 181 */       this.jvm = jvm;
/*     */     }
/*     */     public void run() {
/*     */       try {
/* 185 */         Thread.sleep(5000L); } catch (Exception localException) {
/*     */       }
/* 187 */       this.jvm.shutdownCommunications();
/*     */     }
/*     */   }
/*     */ 
/*     */   class Printer extends Thread
/*     */   {
/*     */     InputStream in;
/*     */     OutputStream out;
/*     */     IOException exception;
/*     */ 
/*     */     public Printer(InputStream in, OutputStream out)
/*     */     {
/* 686 */       super();
/* 687 */       this.in = in;
/* 688 */       this.out = out;
/*     */     }
/*     */ 
/*     */     public void run() {
/*     */       try {
/* 693 */         byte[] buf = new byte[2048];
/* 694 */         int n = 0;
/* 695 */         while (n != -1) {
/* 696 */           n = this.in.read(buf, 0, 2048);
/* 697 */           if (n > 0)
/* 698 */             this.out.write(buf, 0, n);
/*     */         }
/*     */       }
/*     */       catch (IOException e) {
/* 702 */         this.exception = e;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class ReplaceThread extends Thread
/*     */   {
/*     */     long ms;
/*     */ 
/*     */     public ReplaceThread()
/*     */     {
/*  72 */       this.ms = Preloader.DEFAULT_SPIN_UP_WAIT;
/*     */     }
/*     */     public ReplaceThread(long ms) {
/*  75 */       this.ms = ms;
/*     */     }
/*     */     public void run() {
/*     */       try {
/*  79 */         if (Preloader.this.cached != null) {
/*  80 */           System.out.println("[Preloader] replacement not necessary, cached exists");
/*  81 */           return;
/*     */         }
/*  83 */         Thread.sleep(this.ms);
/*  84 */         if (Preloader.this.cancelled) {
/*  85 */           System.out.println("[Preloader] replacement cancelled");
/*  86 */           return;
/*     */         }
/*  88 */         synchronized (Preloader.this.LOCK) {
/*  89 */           if (Preloader.this.cached == null) {
/*  90 */             System.out.println("[Preloader] caching now (sleep was " + this.ms + ")");
/*  91 */             Preloader.this.cached = Preloader.this.startCachedVM();
/*  92 */             System.out.println("[Preloader] got cached OK " + Preloader.this.cached);
/*     */           } else {
/*  94 */             System.out.println("[Preloader] replacement not necessary, cached exists");
/*  95 */             return;
/*     */           }
/*     */         }
/*  98 */         if (Preloader.this.cancelled) {
/*  99 */           Preloader.this.cached.destroy();
/* 100 */           return;
/*     */         }
/*     */       } catch (Exception x) {
/* 103 */         x.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class StreamDeathDetector
/*     */   {
/*     */     InputStream in;
/*     */     OutputStream out;
/* 462 */     int dieIfNoDataFor = 5000;
/*     */ 
/* 464 */     boolean diedFailure = false;
/*     */ 
/* 466 */     Object LOCK = new Object();
/* 467 */     boolean haveNotified = false;
/*     */ 
/* 469 */     Reader r = new Reader();
/* 470 */     Writer w = new Writer();
/*     */ 
/* 472 */     long lastRead = System.currentTimeMillis();
/*     */     private static final boolean DEBUG = true;
/* 475 */     public String ME = "";
/*     */ 
/*     */     public StreamDeathDetector(InputStream in, OutputStream out) {
/* 478 */       this(in, out, "");
/*     */     }
/*     */     public StreamDeathDetector(InputStream in, OutputStream out, String name) {
/* 481 */       this.ME = name;
/* 482 */       this.in = in;
/* 483 */       this.out = out;
/* 484 */       this.r.start();
/* 485 */       this.w.start();
/*     */     }
/*     */ 
/*     */     public void stopAndMakeReady() {
/* 489 */       this.w.stop = true;
/*     */     }
/*     */ 
/*     */     public boolean waitUntilReadyOrDead() {
/*     */       try {
/* 494 */         while (!this.r.ready) {
/* 495 */           Thread.sleep(25L);
/* 496 */           if (System.currentTimeMillis() - this.lastRead > this.dieIfNoDataFor)
/*     */           {
/* 498 */             return false;
/*     */           }
/* 500 */           if (this.diedFailure)
/*     */           {
/* 502 */             return false;
/*     */           }
/*     */         }
/* 505 */         return true;
/*     */       } catch (Exception x) {
/*     */       }
/* 508 */       return false;
/*     */     }
/*     */ 
/*     */     private void notifyDeath()
/*     */     {
/* 513 */       this.diedFailure = true;
/*     */     }
/*     */ 
/*     */     class Reader extends Thread {
/* 517 */       boolean ready = false;
/*     */ 
/*     */       Reader() {  } 
/*     */       public void run() { try { System.out.println("[SDD] Reader started" + Preloader.StreamDeathDetector.this.ME);
/* 521 */           while (!Preloader.StreamDeathDetector.this.diedFailure) {
/* 522 */             boolean makeReady = Preloader.readBoolean(Preloader.StreamDeathDetector.this.in);
/* 523 */             Preloader.StreamDeathDetector.this.lastRead = System.currentTimeMillis();
/*     */ 
/* 525 */             if (makeReady) {
/* 526 */               System.out.println("[SDD] Reader read request to make ready" + Preloader.StreamDeathDetector.this.ME);
/*     */ 
/* 528 */               Preloader.access$1();
/*     */ 
/* 530 */               Preloader.StreamDeathDetector.this.stopAndMakeReady();
/*     */ 
/* 532 */               System.out.println("[SDD] Reader waiting for writer to terminate" + Preloader.StreamDeathDetector.this.ME);
/* 533 */               Preloader.StreamDeathDetector.this.w.join();
/*     */ 
/* 536 */               this.ready = true;
/* 537 */               break;
/*     */             }
/*     */           }
/*     */         } catch (Throwable t)
/*     */         {
/* 542 */           Preloader.StreamDeathDetector.this.notifyDeath();
/*     */         }
/* 544 */         System.out.println("[SDD] Reader exiting" + Preloader.StreamDeathDetector.this.ME); }
/*     */     }
/*     */ 
/*     */     class Writer extends Thread {
/* 548 */       boolean stop = false;
/*     */ 
/*     */       Writer() {
/*     */       }
/*     */       public void run() { try { System.out.println("[SDD] Writer started" + Preloader.StreamDeathDetector.this.ME);
/* 553 */           long T = System.currentTimeMillis() + 500L;
/* 554 */           while ((!this.stop) && (!
/* 555 */             Preloader.StreamDeathDetector.this.diedFailure)) {
/* 556 */             if (System.currentTimeMillis() > T) {
/* 557 */               Preloader.writeBoolean(Preloader.StreamDeathDetector.this.out, false);
/* 558 */               Preloader.StreamDeathDetector.this.out.flush();
/* 559 */               T = System.currentTimeMillis() + 500L;
/*     */             }
/* 561 */             Thread.sleep(25L);
/*     */           }
/* 563 */           if ((this.stop) && (!Preloader.StreamDeathDetector.this.diedFailure)) {
/* 564 */             System.out.println("[SDD] Writer told to make ready" + Preloader.StreamDeathDetector.this.ME);
/* 565 */             Preloader.writeBoolean(Preloader.StreamDeathDetector.this.out, true);
/* 566 */             Preloader.StreamDeathDetector.this.out.flush();
/* 567 */             System.out.println("[SDD] Writer sent ready request" + Preloader.StreamDeathDetector.this.ME);
/*     */           }
/*     */         } catch (Throwable t)
/*     */         {
/* 571 */           Preloader.StreamDeathDetector.this.notifyDeath();
/*     */         }
/* 573 */         System.out.println("[SDD] Writer exiting" + Preloader.StreamDeathDetector.this.ME);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.vm.Preloader
 * JD-Core Version:    0.6.2
 */