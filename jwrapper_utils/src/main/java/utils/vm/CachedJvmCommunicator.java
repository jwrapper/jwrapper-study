/*     */ package utils.vm;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ 
/*     */ public class CachedJvmCommunicator extends Thread
/*     */ {
/*     */   ServerSocket ssock;
/*     */   Process p;
/*     */   Socket sock;
/*     */   Preloader.StreamDeathDetector sdd;
/*     */   ExitDetector ed;
/*     */ 
/*     */   private void waitForSocket()
/*     */     throws IOException
/*     */   {
/*  23 */     while (this.sock == null) {
/*  24 */       if (this.ed == null) {
/*  25 */         throw new IOException("[PreloaderComms] Asked to wait for socket but no PL JVM Process has been set");
/*     */       }
/*  27 */       if (!this.ed.isAlive())
/*  28 */         throw new IOException("[PreloaderComms] PL JVM Process has terminated and we have no socket, returning");
/*     */       try
/*     */       {
/*  31 */         Thread.sleep(25L); } catch (Exception localException) {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream() throws IOException {
/*  37 */     waitForSocket();
/*     */ 
/*  39 */     long tWaitUntil = System.currentTimeMillis() + 5000L;
/*  40 */     while (this.sdd == null)
/*     */     {
/*     */       try {
/*  43 */         Thread.sleep(25L); } catch (Exception localException) {
/*     */       }
/*  45 */       if (System.currentTimeMillis() > tWaitUntil) {
/*  46 */         throw new IOException("[PreloaderComms] PL JVM started a socket but *we* never started SDD?");
/*     */       }
/*     */     }
/*     */ 
/*  50 */     this.sdd.stopAndMakeReady();
/*  51 */     if (!this.sdd.waitUntilReadyOrDead()) {
/*  52 */       throw new IOException("PL JVM comms has died");
/*     */     }
/*     */ 
/*  55 */     return this.sock.getOutputStream();
/*     */   }
/*     */ 
/*     */   public CachedJvmCommunicator() throws IOException {
/*  59 */     this.ssock = new ServerSocket(0, 5, InetAddress.getByName("localhost"));
/*  60 */     start();
/*     */   }
/*     */ 
/*     */   public void setProcess(Process p) {
/*  64 */     this.p = p;
/*  65 */     this.ed = new ExitDetector();
/*     */   }
/*     */ 
/*     */   public Process getProcess() {
/*  69 */     return this.p;
/*     */   }
/*     */ 
/*     */   public String[] getPreloaderClassArgs() {
/*  73 */     return new String[] { this.ssock.getLocalPort() };
/*     */   }
/*     */ 
/*     */   public void destroy() {
/*     */     try {
/*  78 */       this.ssock.close(); } catch (Throwable localThrowable) {
/*     */     }
/*     */     try {
/*  81 */       this.sock.close(); } catch (Throwable localThrowable1) {
/*     */     }
/*     */     try {
/*  84 */       this.p.destroy(); } catch (Throwable localThrowable2) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public void shutdownCommunications() {
/*     */     try {
/*  90 */       this.ssock.close(); } catch (Throwable localThrowable) {
/*     */     }
/*     */     try {
/*  93 */       this.sock.close(); } catch (Throwable localThrowable1) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run() {
/*     */     try {
/*  99 */       this.sock = this.ssock.accept();
/*     */ 
/* 101 */       Preloader.writeInt(this.sock.getOutputStream(), Preloader.COMMS_VERSION);
/* 102 */       this.sock.getOutputStream().flush();
/*     */ 
/* 104 */       this.sdd = new Preloader.StreamDeathDetector(this.sock.getInputStream(), this.sock.getOutputStream());
/*     */     } catch (Exception x) {
/* 106 */       System.out.println("[Preloader] failed to accept socket from launched JVM");
/*     */     }
/*     */   }
/*     */ 
/*     */   class ExitDetector extends Thread {
/*     */     public ExitDetector() {
/* 112 */       start();
/*     */     }
/*     */     public void run() {
/*     */       try {
/* 116 */         int exitCode = CachedJvmCommunicator.this.p.waitFor();
/* 117 */         System.out.println("[Preloader] PL JVM died with exit code " + exitCode);
/*     */       } catch (Exception x) {
/* 119 */         System.out.println("[Preloader] Problem in ExitListener " + x);
/* 120 */         x.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.vm.CachedJvmCommunicator
 * JD-Core Version:    0.6.2
 */