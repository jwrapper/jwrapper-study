/*    */ package utils.files;
/*    */ 
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URI;
/*    */ import java.net.URISyntaxException;
/*    */ import java.net.URL;
/*    */ 
/*    */ public class URIUtil
/*    */ {
/*    */   public static URI getSafeURIFrom(URL targetURL)
/*    */     throws URISyntaxException
/*    */   {
/* 16 */     return new URI(targetURL.getProtocol(), 
/* 17 */       null, 
/* 18 */       targetURL.getHost(), 
/* 19 */       targetURL.getPort(), 
/* 20 */       targetURL.getPath(), 
/* 21 */       targetURL.getQuery(), 
/* 22 */       null);
/*    */   }
/*    */ 
/*    */   public static URL getSafeURLFrom(URL targetURL) throws URISyntaxException, MalformedURLException
/*    */   {
/* 27 */     URI safeURI = getSafeURIFrom(targetURL);
/* 28 */     return safeURI.toURL();
/*    */   }
/*    */ 
/*    */   public static URL tryGetSafeURLFrom(URL targetURL)
/*    */   {
/*    */     try
/*    */     {
/* 35 */       URI safeURI = getSafeURIFrom(targetURL);
/* 36 */       return safeURI.toURL();
/*    */     }
/*    */     catch (Throwable t)
/*    */     {
/* 40 */       t.printStackTrace();
/* 41 */     }return targetURL;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.files.URIUtil
 * JD-Core Version:    0.6.2
 */