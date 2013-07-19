/*     */ package jwrapper.proxy;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.Proxy.Type;
/*     */ import java.net.ProxySelector;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import jwrapper.jwutils.JWSystem;
/*     */ import utils.stream.StreamUtils;
/*     */ 
/*     */ public class JWDetectedProxy
/*     */ {
/*  21 */   public static boolean DETECTED_PROXY_OK = false;
/*  22 */   public static Proxy DETECTED_PROXY = null;
/*  23 */   public static boolean DETECTED_PROXY_OVERWRITE_SELECTOR = false;
/*     */ 
/*  42 */   private static String oldHttpHostProperty = null;
/*  43 */   private static String oldHttpPortProperty = null;
/*  44 */   private static String oldHttpsHostProperty = null;
/*  45 */   private static String oldHttpsPortProperty = null;
/*  46 */   private static String oldSocksHostProperty = null;
/*  47 */   private static String oldSocksPortProperty = null;
/*  48 */   public static ProxySelector DEFAULT_SELECTOR = null;
/*     */ 
/*     */   public static URLConnection openConnection(URL url)
/*     */     throws IOException
/*     */   {
/*  27 */     if (DETECTED_PROXY_OK)
/*     */     {
/*  29 */       return url.openConnection(DETECTED_PROXY);
/*     */     }
/*     */ 
/*  33 */     return url.openConnection();
/*     */   }
/*     */ 
/*     */   public static InputStream openStream(URL url)
/*     */     throws IOException
/*     */   {
/*  39 */     return openConnection(url).getInputStream();
/*     */   }
/*     */ 
/*     */   public static void revertDefaultProxySettings()
/*     */   {
/*  52 */     if (oldHttpHostProperty != null)
/*  53 */       System.setProperty("http.proxyHost", oldHttpHostProperty);
/*  54 */     if (oldHttpPortProperty != null)
/*  55 */       System.setProperty("http.proxyPort", oldHttpPortProperty);
/*  56 */     if (oldHttpsHostProperty != null)
/*  57 */       System.setProperty("https.proxyHost", oldHttpsHostProperty);
/*  58 */     if (oldHttpsPortProperty != null)
/*  59 */       System.setProperty("https.proxyPort", oldHttpsPortProperty);
/*  60 */     if (oldSocksHostProperty != null)
/*  61 */       System.setProperty("socksProxyHost", oldSocksHostProperty);
/*  62 */     if (oldSocksPortProperty != null)
/*  63 */       System.setProperty("socksProxyPort", oldSocksPortProperty);
/*     */   }
/*     */ 
/*     */   public static void saveDetectedProxy()
/*     */   {
/*  68 */     File sharedFolder = JWSystem.getAllAppVersionsSharedFolder();
/*  69 */     File lastWorking = new File(sharedFolder, "LastProxy");
/*  70 */     lastWorking.delete();
/*     */ 
/*  72 */     if ((DETECTED_PROXY_OK) && (DETECTED_PROXY != null) && (DETECTED_PROXY != Proxy.NO_PROXY))
/*     */     {
/*  74 */       SocketAddress address = DETECTED_PROXY.address();
/*  75 */       if ((address == null) || (!(address instanceof InetSocketAddress))) {
/*  76 */         return;
/*     */       }
/*  78 */       InetSocketAddress inetAddress = (InetSocketAddress)address;
/*  79 */       String hostname = JWProxyList.getHostnameFrom(inetAddress);
/*  80 */       int port = inetAddress.getPort();
/*     */ 
/*  82 */       if (hostname == null) {
/*  83 */         return;
/*     */       }
/*     */       try
/*     */       {
/*  87 */         FileOutputStream fout = new FileOutputStream(lastWorking);
/*     */         try
/*     */         {
/*  90 */           StreamUtils.writeBoolean(fout, DETECTED_PROXY.type() == Proxy.Type.HTTP);
/*  91 */           StreamUtils.writeStringUTF8(fout, hostname);
/*  92 */           StreamUtils.writeInt(fout, port);
/*  93 */           StreamUtils.writeBoolean(fout, DETECTED_PROXY_OVERWRITE_SELECTOR);
/*     */         }
/*     */         finally
/*     */         {
/*  97 */           fout.close();
/*     */         }
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 102 */         t.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void loadLastDetectedProxy()
/*     */   {
/* 109 */     File sharedFolder = JWSystem.getAllAppVersionsSharedFolder();
/* 110 */     File lastWorking = new File(sharedFolder, "LastProxy");
/* 111 */     File credentialsFile = new File(sharedFolder, "ProxyCredentials");
/*     */ 
/* 113 */     if ((!lastWorking.exists()) || (lastWorking.length() == 0L))
/*     */     {
/* 115 */       System.out.println("[JWDetectedProxy] No proxy configuration found to load.");
/* 116 */       DETECTED_PROXY_OK = false;
/* 117 */       return;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 123 */       FileInputStream fin = new FileInputStream(lastWorking);
/*     */       try
/*     */       {
/* 126 */         boolean isHTTP = StreamUtils.readBoolean(fin);
/* 127 */         String hostname = StreamUtils.readStringUTF8(fin);
/* 128 */         int port = StreamUtils.readInt(fin);
/* 129 */         boolean overwriteSelector = false;
/*     */         try
/*     */         {
/* 132 */           overwriteSelector = StreamUtils.readBoolean(fin);
/*     */         }
/*     */         catch (Throwable localThrowable1) {
/*     */         }
/* 136 */         InetSocketAddress isa = new InetSocketAddress(hostname, port);
/*     */         Proxy proxy;
/*     */         Proxy proxy;
/* 138 */         if (isHTTP)
/* 139 */           proxy = new Proxy(Proxy.Type.HTTP, isa);
/*     */         else {
/* 141 */           proxy = new Proxy(Proxy.Type.SOCKS, isa);
/*     */         }
/* 143 */         DETECTED_PROXY = proxy;
/* 144 */         DETECTED_PROXY_OK = true;
/* 145 */         DETECTED_PROXY_OVERWRITE_SELECTOR = overwriteSelector;
/*     */ 
/* 147 */         setDetectedProxyAsDefault();
/*     */ 
/* 149 */         System.out.println("[JWDetectedProxy] Loaded detected proxy configuration. Setting up authenticator.");
/*     */ 
/* 151 */         JWProxyCredentials credentials = new JWProxyCredentials();
/*     */         try
/*     */         {
/* 154 */           credentials.loadFromFile(credentialsFile);
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/* 160 */           t.printStackTrace();
/*     */         }
/*     */ 
/* 163 */         JWProxyAuthenticator authenticator = new JWProxyAuthenticator(credentials);
/* 164 */         System.out.println("[JWDetectedProxy] Set up authenticator.");
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 169 */         t.printStackTrace();
/* 170 */         DETECTED_PROXY_OK = false;
/*     */         try
/*     */         {
/* 176 */           fin.close(); } catch (IOException localIOException) {  } } finally { try { fin.close();
/*     */         } catch (IOException localIOException1)
/*     */         {
/*     */         } }
/*     */     }
/*     */     catch (FileNotFoundException e)
/*     */     {
/* 183 */       e.printStackTrace();
/* 184 */       DETECTED_PROXY_OK = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setDetectedProxyAsDefault()
/*     */   {
/* 191 */     if (DETECTED_PROXY_OK)
/*     */     {
/* 193 */       if ((ProxySelector.getDefault() != null) && (DETECTED_PROXY_OVERWRITE_SELECTOR))
/*     */       {
/* 195 */         DEFAULT_SELECTOR = ProxySelector.getDefault();
/* 196 */         ProxySelector.setDefault(null);
/*     */       }
/* 198 */       if ((ProxySelector.getDefault() == null) && (!DETECTED_PROXY_OVERWRITE_SELECTOR))
/* 199 */         ProxySelector.setDefault(DEFAULT_SELECTOR);
/*     */     }
/* 201 */     if ((DETECTED_PROXY_OK) && (DETECTED_PROXY != null) && (DETECTED_PROXY != Proxy.NO_PROXY))
/*     */     {
/* 203 */       SocketAddress address = DETECTED_PROXY.address();
/* 204 */       if ((address == null) || (!(address instanceof InetSocketAddress))) {
/* 205 */         return;
/*     */       }
/* 207 */       InetSocketAddress inetAddress = (InetSocketAddress)address;
/* 208 */       String hostname = JWProxyList.getHostnameFrom(inetAddress);
/* 209 */       int port = inetAddress.getPort();
/*     */ 
/* 211 */       oldHttpHostProperty = System.getProperty("http.proxyHost");
/* 212 */       oldHttpPortProperty = System.getProperty("http.proxyPort");
/* 213 */       oldHttpsHostProperty = System.getProperty("https.proxyHost");
/* 214 */       oldHttpsPortProperty = System.getProperty("https.proxyPort");
/* 215 */       oldSocksHostProperty = System.getProperty("socksProxyHost");
/* 216 */       oldSocksPortProperty = System.getProperty("socksProxyPort");
/*     */ 
/* 218 */       if (DETECTED_PROXY.type() == Proxy.Type.HTTP)
/*     */       {
/* 220 */         System.out.println("[JWDetectedProxy] Default proxy (" + hostname + ":" + port + ")");
/* 221 */         System.setProperty("http.proxyHost", hostname);
/* 222 */         System.setProperty("http.proxyPort", Integer.toString(port));
/* 223 */         System.setProperty("https.proxyHost", hostname);
/* 224 */         System.setProperty("https.proxyPort", Integer.toString(port));
/*     */       }
/* 226 */       else if (DETECTED_PROXY.type() == Proxy.Type.SOCKS)
/*     */       {
/* 228 */         System.out.println("[JWDetectedProxy] Default proxy (" + hostname + ":" + port + ")");
/* 229 */         System.setProperty("socksProxyHost", hostname);
/* 230 */         System.setProperty("socksProxyPort", Integer.toString(port));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.proxy.JWDetectedProxy
 * JD-Core Version:    0.6.2
 */