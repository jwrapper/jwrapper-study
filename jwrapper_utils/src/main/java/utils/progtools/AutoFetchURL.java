/*    */ package utils.progtools;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ import java.util.HashMap;
/*    */ import utils.files.URIUtil;
/*    */ 
/*    */ public class AutoFetchURL extends Thread
/*    */ {
/* 16 */   static HashMap allUrls = new HashMap();
/*    */   URL url;
/*    */   int delay;
/* 38 */   ByteArrayOutputStream bout = new ByteArrayOutputStream();
/*    */   IOException error;
/*    */ 
/*    */   public static void preFetchUrl(String url, int delay)
/*    */     throws MalformedURLException
/*    */   {
/* 20 */     URL urlObject = new URL(url);
/* 21 */     urlObject = URIUtil.tryGetSafeURLFrom(urlObject);
/* 22 */     preFetchUrl(urlObject, delay);
/*    */   }
/*    */ 
/*    */   public static void preFetchUrl(URL url, int delay) {
/* 26 */     System.out.println("[AutoFetchURL] prefetching " + url + " (+" + delay + ")");
/* 27 */     allUrls.put(url.toString(), new AutoFetchURL(url, delay));
/*    */   }
/*    */ 
/*    */   public static AutoFetchURL getPreFetchedURL(URL url) {
/* 31 */     return (AutoFetchURL)allUrls.get(url.toString());
/*    */   }
/*    */ 
/*    */   public AutoFetchURL(URL url, int delay)
/*    */   {
/* 42 */     this.url = url;
/* 43 */     this.delay = delay;
/* 44 */     start();
/*    */   }
/*    */ 
/*    */   public void run() {
/*    */     try {
/*    */       try {
/* 50 */         if (this.delay > 0) Thread.sleep(this.delay); 
/*    */       }
/*    */       catch (Exception localException) {  }
/*    */ 
/* 53 */       InputStream in = new BufferedInputStream(this.url.openStream());
/*    */ 
/* 55 */       byte[] buf = new byte[20000];
/* 56 */       int n = 0;
/*    */ 
/* 58 */       while (n != -1) {
/* 59 */         n = in.read(buf);
/* 60 */         if (n > 0)
/* 61 */           this.bout.write(buf, 0, n);
/*    */       }
/*    */     }
/*    */     catch (IOException x)
/*    */     {
/* 66 */       this.error = x;
/*    */     }
/*    */   }
/*    */ 
/*    */   public InputStream getAsLocalStream(long timeout) throws IOException {
/* 71 */     return new ByteArrayInputStream(getAsBytes(timeout));
/*    */   }
/*    */   public String getAsUTF8(long timeout) throws IOException {
/* 74 */     return new String(getAsBytes(timeout), "UTF8");
/*    */   }
/*    */   public byte[] getAsBytes(long timeout) throws IOException {
/*    */     try {
/* 78 */       join(timeout);
/*    */     } catch (InterruptedException x) {
/* 80 */       throw new IOException("Interrupted");
/*    */     }
/* 82 */     if (this.error != null) throw this.error;
/* 83 */     return this.bout.toByteArray();
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) throws Exception {
/* 87 */     AutoFetchURL url = new AutoFetchURL(new URL("http://google.com"), 1000);
/* 88 */     System.out.println(url.getAsUTF8(0L));
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.progtools.AutoFetchURL
 * JD-Core Version:    0.6.2
 */