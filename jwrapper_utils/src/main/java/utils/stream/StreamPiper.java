/*    */ package utils.stream;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InterruptedIOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class StreamPiper
/*    */ {
/*    */   public static void pipe(InputStream in, OutputStream out, boolean flushAtEndOfData, boolean flushOnEveryWrite, boolean flushOnReadTimeout)
/*    */     throws IOException
/*    */   {
/* 11 */     pipe(in, out, 50000, null, flushAtEndOfData, flushOnEveryWrite, flushOnReadTimeout);
/*    */   }
/*    */ 
/*    */   public static void pipe(InputStream in, OutputStream out, int bufSize, boolean flushAtEndOfData, boolean flushOnEveryWrite, boolean flushOnReadTimeout) throws IOException {
/* 15 */     pipe(in, out, bufSize, null, flushAtEndOfData, flushOnEveryWrite, flushOnReadTimeout);
/*    */   }
/*    */ 
/*    */   public static void pipe(InputStream in, OutputStream out, int bufSize, StreamStatusListener listener, boolean flushAtEndOfData, boolean flushOnEveryWrite, boolean flushOnReadTimeout) throws IOException {
/* 19 */     int n = 0;
/* 20 */     byte[] buf = new byte[bufSize];
/* 21 */     while (n != -1) {
/*    */       try {
/* 23 */         n = in.read(buf, 0, bufSize);
/* 24 */         if (n > 0) {
/* 25 */           out.write(buf, 0, n);
/* 26 */           if (flushOnEveryWrite)
/* 27 */             out.flush();
/*    */         }
/*    */       }
/*    */       catch (InterruptedIOException x) {
/* 31 */         if (flushOnReadTimeout) {
/* 32 */           if (x.bytesTransferred > 0) {
/* 33 */             out.write(buf, 0, x.bytesTransferred);
/*    */           }
/* 35 */           out.flush();
/*    */         }
/*    */       }
/*    */     }
/* 39 */     if (flushAtEndOfData) {
/* 40 */       out.flush();
/*    */     }
/* 42 */     if (listener != null)
/* 43 */       listener.sourceStreamIsClosed();
/*    */   }
/*    */ 
/*    */   public static Thread pipeAsync(InputStream in, OutputStream out) throws IOException {
/* 47 */     Thread t = new AsyncPipe(in, out, 50000, null, false, false, false);
/* 48 */     t.start();
/* 49 */     return t;
/*    */   }
/*    */ 
/*    */   public static Thread pipeAsync(InputStream in, OutputStream out, int bufSize) throws IOException {
/* 53 */     Thread t = new AsyncPipe(in, out, bufSize, null, false, false, false);
/* 54 */     t.start();
/* 55 */     return t;
/*    */   }
/*    */ 
/*    */   public static Thread pipeAsync(InputStream in, OutputStream out, int bufSize, StreamStatusListener listener, boolean flushAtEndOfData, boolean flushOnEveryWrite, boolean flushOnReadTimeout) throws IOException {
/* 59 */     Thread t = new AsyncPipe(in, out, bufSize, listener, flushAtEndOfData, flushOnEveryWrite, flushOnReadTimeout);
/* 60 */     t.start();
/* 61 */     return t; } 
/*    */   private static class AsyncPipe extends Thread { InputStream in;
/*    */     OutputStream out;
/*    */     private int bufSize;
/*    */     private StreamPiper.StreamStatusListener listener;
/*    */     boolean flushAtEndOfData;
/*    */     boolean flushOnEveryWrite;
/*    */     boolean flushOnReadTimeout;
/*    */ 
/* 74 */     AsyncPipe(InputStream in, OutputStream out, int bufSize, StreamPiper.StreamStatusListener listener, boolean flushAtEndOfData, boolean flushOnEveryWrite, boolean flushOnReadTimeout) { super();
/* 75 */       this.in = in;
/* 76 */       this.out = out;
/* 77 */       this.bufSize = bufSize;
/* 78 */       this.listener = listener;
/* 79 */       this.flushAtEndOfData = flushAtEndOfData;
/* 80 */       this.flushOnEveryWrite = flushOnEveryWrite;
/* 81 */       this.flushOnReadTimeout = flushOnReadTimeout; }
/*    */ 
/*    */     public void run() {
/*    */       try {
/* 85 */         StreamPiper.pipe(this.in, this.out, this.bufSize, this.listener, this.flushAtEndOfData, this.flushOnEveryWrite, this.flushOnReadTimeout);
/*    */       }
/*    */       catch (IOException localIOException)
/*    */       {
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public static abstract interface StreamStatusListener
/*    */   {
/*    */     public abstract void sourceStreamIsClosed();
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.stream.StreamPiper
 * JD-Core Version:    0.6.2
 */