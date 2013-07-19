/*    */ package jwrapper.proxy;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.net.Authenticator;
/*    */ import java.net.PasswordAuthentication;
/*    */ import java.net.Proxy;
/*    */ 
/*    */ public class JWProxyAuthenticator extends Authenticator
/*    */ {
/*    */   private JWProxyCredentials proxyCredentials;
/* 12 */   private String interceptPassword = null;
/* 13 */   private String interceptUsername = null;
/*    */ 
/*    */   public JWProxyAuthenticator(JWProxyCredentials proxyCredentials)
/*    */   {
/* 17 */     this.proxyCredentials = proxyCredentials;
/*    */ 
/* 21 */     Authenticator.setDefault(this);
/*    */   }
/*    */ 
/*    */   public void revertDefault()
/*    */   {
/* 28 */     Authenticator.setDefault(null);
/*    */   }
/*    */ 
/*    */   public void saveInterceptCredentials(Proxy proxy)
/*    */   {
/* 33 */     System.out.println("[JWProxyAuthenticator] Saving intercept credentials");
/* 34 */     if (this.interceptUsername != null)
/*    */     {
/* 36 */       JWProxyCredentials.Credentials c = new JWProxyCredentials.Credentials(this.interceptUsername, this.interceptPassword);
/* 37 */       this.proxyCredentials.setCredentialsFor(proxy, c);
/*    */ 
/* 39 */       this.interceptUsername = null;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setInterceptCredentials(String username, String password)
/*    */   {
/* 45 */     if (username != null)
/* 46 */       System.out.println("[JWProxyAuthenticator] Setting intercept credentials");
/*    */     else
/* 48 */       System.out.println("[JWProxyAuthenticator] Clearing intercept credentials");
/* 49 */     this.interceptUsername = username;
/* 50 */     this.interceptPassword = password;
/*    */   }
/*    */ 
/*    */   protected PasswordAuthentication getPasswordAuthentication()
/*    */   {
/* 61 */     if (this.interceptUsername != null) {
/* 62 */       return new PasswordAuthentication(this.interceptUsername, this.interceptPassword.toCharArray());
/*    */     }
/* 64 */     JWProxyCredentials.Credentials credentials = this.proxyCredentials.getCredentialsFor(getRequestingHost(), getRequestingPort());
/* 65 */     if ((credentials != null) && (credentials.username != null))
/*    */     {
/* 67 */       if (credentials.password == null) {
/* 68 */         return new PasswordAuthentication(credentials.username, null);
/*    */       }
/* 70 */       return new PasswordAuthentication(credentials.username, credentials.password.toCharArray());
/*    */     }
/* 72 */     return null;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.proxy.JWProxyAuthenticator
 * JD-Core Version:    0.6.2
 */