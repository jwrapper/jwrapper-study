/*     */ package jwrapper.proxy;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import utils.encryption.rsa.RSADecryptor;
/*     */ import utils.encryption.rsa.RSAEncryptor;
/*     */ import utils.stream.StreamUtils;
/*     */ 
/*     */ public class JWProxyCredentials
/*     */ {
/*  21 */   private HashMap map = new HashMap();
/*     */ 
/*     */   public void saveToFile(File targetFile) throws IOException
/*     */   {
/*  25 */     BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(targetFile));
/*     */     try
/*     */     {
/*  28 */       int size = this.map.size();
/*  29 */       StreamUtils.writeInt(bout, size);
/*  30 */       RSAEncryptor rsaEncryptor = getEncryptor();
/*     */ 
/*  32 */       Iterator it = this.map.keySet().iterator();
/*  33 */       while (it.hasNext())
/*     */       {
/*  35 */         String key = (String)it.next();
/*  36 */         Credentials value = (Credentials)this.map.get(key);
/*     */ 
/*  38 */         byte[] encryptedUsername = new byte[0];
/*  39 */         byte[] encryptedPassword = new byte[0];
/*  40 */         boolean hasPassword = value.password != null;
/*     */         try
/*     */         {
/*  44 */           encryptedUsername = encryptString(rsaEncryptor, value.username);
/*  45 */           if (hasPassword)
/*  46 */             encryptedPassword = encryptString(rsaEncryptor, value.password);
/*     */         }
/*     */         catch (Exception ex)
/*     */         {
/*  50 */           ex.printStackTrace();
/*     */         }
/*     */ 
/*  53 */         StreamUtils.writeStringUTF8(bout, key);
/*  54 */         StreamUtils.writeBytes(bout, encryptedUsername);
/*  55 */         StreamUtils.writeBoolean(bout, hasPassword);
/*  56 */         if (hasPassword)
/*  57 */           StreamUtils.writeBytes(bout, encryptedPassword);
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*  62 */       t.printStackTrace();
/*     */     }
/*     */     finally
/*     */     {
/*  66 */       bout.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void loadFromFile(File file) throws IOException
/*     */   {
/*  72 */     if (file.exists())
/*     */     {
/*  74 */       BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
/*     */       try
/*     */       {
/*  77 */         int size = StreamUtils.readInt(bin);
/*     */ 
/*  79 */         RSADecryptor descryptor = getDecryptor();
/*     */ 
/*  81 */         for (int i = 0; i < size; i++)
/*     */         {
/*  83 */           String key = StreamUtils.readStringUTF8(bin);
/*  84 */           byte[] encryptedUsername = StreamUtils.readBytes(bin);
/*  85 */           byte[] encryptedPassword = null;
/*  86 */           boolean hasPassword = StreamUtils.readBoolean(bin);
/*  87 */           if (hasPassword) {
/*  88 */             encryptedPassword = StreamUtils.readBytes(bin);
/*     */           }
/*  90 */           if (encryptedUsername.length != 0)
/*     */           {
/*     */             try
/*     */             {
/*  95 */               String username = decryptString(descryptor, encryptedUsername);
/*  96 */               String password = null;
/*  97 */               if (hasPassword) {
/*  98 */                 password = decryptString(descryptor, encryptedPassword);
/*     */               }
/* 100 */               this.map.put(key, new Credentials(username, password));
/*     */             }
/*     */             catch (Exception ex)
/*     */             {
/* 104 */               ex.printStackTrace();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable t) {
/* 110 */         t.printStackTrace();
/*     */       }
/*     */       finally
/*     */       {
/* 114 */         bin.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Credentials getCredentialsFor(Proxy proxy)
/*     */   {
/* 121 */     String key = getKey(proxy);
/* 122 */     Credentials credentials = (Credentials)this.map.get(key);
/* 123 */     return credentials;
/*     */   }
/*     */ 
/*     */   public Credentials getCredentialsFor(String hostname, int port)
/*     */   {
/* 128 */     String key = getKey(hostname, port);
/* 129 */     Credentials credentials = (Credentials)this.map.get(key);
/* 130 */     return credentials;
/*     */   }
/*     */ 
/*     */   public void setCredentialsFor(Proxy proxy, Credentials credentials)
/*     */   {
/* 135 */     String key = getKey(proxy);
/* 136 */     this.map.put(key, credentials);
/*     */   }
/*     */ 
/*     */   private String getKey(Proxy proxy)
/*     */   {
/* 153 */     InetSocketAddress address = (InetSocketAddress)proxy.address();
/* 154 */     return getKey(JWProxyList.getHostnameFrom(address), address.getPort());
/*     */   }
/*     */ 
/*     */   public static String getKey(String hostname, int port)
/*     */   {
/* 159 */     return hostname + ":" + port;
/*     */   }
/*     */ 
/*     */   private byte[] encryptString(RSAEncryptor rsaEncryptor, String string)
/*     */   {
/*     */     try
/*     */     {
/* 166 */       byte[] data = string.getBytes("UTF-8");
/* 167 */       return rsaEncryptor.encrypt(data);
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 170 */       e.printStackTrace();
/* 171 */     }return null;
/*     */   }
/*     */ 
/*     */   private String decryptString(RSADecryptor rsaDecryptor, byte[] encryptedData)
/*     */   {
/*     */     try
/*     */     {
/* 179 */       byte[] decryptedData = rsaDecryptor.decrypt(encryptedData);
/* 180 */       return new String(decryptedData, "UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 183 */       e.printStackTrace();
/* 184 */     }return null;
/*     */   }
/*     */ 
/*     */   public static RSAEncryptor getEncryptor()
/*     */   {
/* 190 */     return new RSAEncryptor(JWProxyKeys.getPrivate());
/*     */   }
/*     */ 
/*     */   private static RSADecryptor getDecryptor()
/*     */   {
/* 195 */     return new RSADecryptor(JWProxyKeys.getPublic());
/*     */   }
/*     */ 
/*     */   public static class Credentials
/*     */   {
/*     */     public String username;
/*     */     public String password;
/*     */ 
/*     */     public Credentials(String username, String password)
/*     */     {
/* 146 */       this.username = username;
/* 147 */       this.password = password;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.proxy.JWProxyCredentials
 * JD-Core Version:    0.6.2
 */