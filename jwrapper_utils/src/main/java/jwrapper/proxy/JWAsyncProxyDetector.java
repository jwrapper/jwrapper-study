/*     */ package jwrapper.proxy;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.Proxy.Type;
/*     */ import java.net.ProxySelector;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import jwrapper.jwutils.JWGenericOS;
/*     */ import jwrapper.ui.ProxyCredentialsDialog;
/*     */ import utils.files.URIUtil;
/*     */ import utils.ostools.OS;
/*     */ import utils.stream.StreamUtils;
/*     */ 
/*     */ public class JWAsyncProxyDetector
/*     */ {
/*  27 */   private ArrayList<Proxy> listOfProxiesToTry = new ArrayList();
/*     */   private JWProxyCredentials proxyCredentials;
/*     */   private JWProxyAuthenticator authenticator;
/*  31 */   private Object PROXY_LOCK = new Object();
/*     */   private URL targetURL;
/*     */   private int timeoutMS;
/*  93 */   private int threadCount = 0;
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws IOException
/*     */   {
/*  37 */     JWProxyList proxyList = new JWProxyList(5);
/*  38 */     proxyList.addProxyToFront(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("1.2.3.4", 80)));
/*  39 */     JWProxyCredentials credentials = new JWProxyCredentials();
/*  40 */     JWAsyncProxyDetector detector = new JWAsyncProxyDetector(new URL("http://google.com:80"), proxyList, credentials, 5000);
/*  41 */     detector.detectProxyAndBlock("App1");
/*     */   }
/*     */ 
/*     */   public JWAsyncProxyDetector(URL targetURL, JWProxyList proxyList, JWProxyCredentials proxyCredentials, int timeoutMS)
/*     */   {
/*  46 */     System.setProperty("java.net.useSystemProxies", "true");
/*     */ 
/*  48 */     this.targetURL = targetURL;
/*  49 */     this.timeoutMS = timeoutMS;
/*     */ 
/*  52 */     JWDetectedProxy.DEFAULT_SELECTOR = ProxySelector.getDefault();
/*     */ 
/*  55 */     this.authenticator = new JWProxyAuthenticator(proxyCredentials);
/*     */ 
/*  58 */     this.listOfProxiesToTry.add(Proxy.NO_PROXY);
/*     */ 
/*  61 */     Iterator it = proxyList.getProxyIterator();
/*  62 */     while (it.hasNext()) {
/*  63 */       this.listOfProxiesToTry.add((Proxy)it.next());
/*     */     }
/*     */     try
/*     */     {
/*     */       URI uri;
/*     */       try
/*     */       {
/*  70 */         uri = targetURL.toURI();
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */         URI uri;
/*  74 */         uri = URIUtil.getSafeURIFrom(targetURL);
/*     */       }
/*     */ 
/*  77 */       List l = ProxySelector.getDefault().select(uri);
/*  78 */       if (l != null)
/*     */       {
/*  80 */         for (Proxy proxy : l)
/*     */         {
/*  82 */           if (proxy.address() != null)
/*  83 */             this.listOfProxiesToTry.add(proxy);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*  89 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean detectProxyAndBlock(String appName)
/*     */   {
/* 101 */     System.out.println("[JWAsyncProxyDetector] Trying " + this.listOfProxiesToTry.size() + " proxies...");
/*     */ 
/* 103 */     ArrayList listOfDetectionThreads = new ArrayList();
/*     */ 
/* 105 */     synchronized (this.PROXY_LOCK)
/*     */     {
/* 108 */       ProxySelector.setDefault(JWDetectedProxy.DEFAULT_SELECTOR);
/*     */ 
/* 110 */       for (int i = 0; i < this.listOfProxiesToTry.size(); i++)
/*     */       {
/* 112 */         Proxy proxy = (Proxy)this.listOfProxiesToTry.get(i);
/* 113 */         ProxyDetectionThread thread = new ProxyDetectionThread(this.targetURL, proxy, false);
/* 114 */         new Thread(thread).start();
/* 115 */         listOfDetectionThreads.add(thread);
/*     */ 
/* 117 */         this.threadCount += 1;
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 122 */         Thread.sleep(1000L);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException) {
/*     */       }
/* 126 */       ProxySelector.setDefault(null);
/*     */ 
/* 128 */       int i = 0;
/*     */       while (true) {
/* 130 */         Proxy proxy = (Proxy)this.listOfProxiesToTry.get(i);
/* 131 */         ProxyDetectionThread thread = new ProxyDetectionThread(this.targetURL, proxy, true);
/* 132 */         new Thread(thread).start();
/* 133 */         listOfDetectionThreads.add(thread);
/*     */ 
/* 135 */         this.threadCount += 1;
/*     */ 
/* 128 */         i++; if (i >= this.listOfProxiesToTry.size())
/*     */         {
/* 138 */           break;
/*     */         }
/*     */       }
/*     */       do {
/*     */         try { this.PROXY_LOCK.wait();
/*     */         } catch (InterruptedException localInterruptedException1) {
/*     */         }
/* 145 */         System.out.println("[JWAsyncProxyDetector] " + this.threadCount + " remaining...");
/*     */ 
/* 138 */         if (this.threadCount <= 0) break;  } while (!JWDetectedProxy.DETECTED_PROXY_OK);
/*     */     }
/*     */ 
/* 148 */     if (!JWDetectedProxy.DETECTED_PROXY_OK)
/*     */     {
/* 151 */       for (int i = listOfDetectionThreads.size() - 1; i >= 0; i--)
/*     */       {
/* 153 */         ProxyDetectionThread thread = (ProxyDetectionThread)listOfDetectionThreads.get(i);
/* 154 */         if (thread.requiresAuthentication)
/* 155 */           thread.printMessage("Authentication required");
/*     */         else {
/* 157 */           listOfDetectionThreads.remove(i);
/*     */         }
/*     */       }
/*     */ 
/* 161 */       while ((listOfDetectionThreads.size() > 0) && (!JWDetectedProxy.DETECTED_PROXY_OK))
/*     */       {
/* 163 */         ProxyCredentialsDialog dialog = ProxyCredentialsDialog.showDialog(appName);
/* 164 */         String username = dialog.getUsername();
/* 165 */         String password = dialog.getPassword();
/*     */ 
/* 167 */         this.authenticator.setInterceptCredentials(username, password);
/*     */ 
/* 169 */         if (username == null) {
/*     */           break;
/*     */         }
/* 172 */         synchronized (this.PROXY_LOCK)
/*     */         {
/* 174 */           ProxySelector.setDefault(JWDetectedProxy.DEFAULT_SELECTOR);
/*     */ 
/* 176 */           int i = 0;
/*     */           while (true) {
/* 178 */             ProxyDetectionThread thread = (ProxyDetectionThread)listOfDetectionThreads.get(i);
/* 179 */             if ((thread.overrideSelector) && (ProxySelector.getDefault() != null))
/*     */             {
/*     */               try
/*     */               {
/* 183 */                 Thread.sleep(1000L);
/*     */               }
/*     */               catch (InterruptedException localInterruptedException2) {
/*     */               }
/* 187 */               ProxySelector.setDefault(null);
/*     */             }
/* 189 */             new Thread(thread).start();
/* 190 */             this.threadCount += 1;
/*     */ 
/* 176 */             i++; if (i >= listOfDetectionThreads.size())
/*     */             {
/* 193 */               break;
/*     */             }
/*     */           }
/*     */           do {
/*     */             try { this.PROXY_LOCK.wait();
/*     */             } catch (InterruptedException localInterruptedException3) {
/*     */             }
/* 200 */             System.out.println("[JWAsyncProxyDetector] " + this.threadCount + " remaining...");
/*     */ 
/* 193 */             if (this.threadCount <= 0) break;  } while (!JWDetectedProxy.DETECTED_PROXY_OK);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 205 */       this.authenticator.setInterceptCredentials(null, null);
/*     */ 
/* 207 */       if (!JWDetectedProxy.DETECTED_PROXY_OK)
/*     */       {
/* 210 */         System.out.println("[JWAsyncProxyDetector] None of the detected proxies worked. Reverting authenticator.");
/*     */ 
/* 212 */         this.authenticator.revertDefault();
/* 213 */         ProxySelector.setDefault(JWDetectedProxy.DEFAULT_SELECTOR);
/*     */ 
/* 215 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 219 */     for (int i = 0; i < listOfDetectionThreads.size(); i++)
/*     */     {
/* 221 */       ProxyDetectionThread thread = (ProxyDetectionThread)listOfDetectionThreads.get(i);
/* 222 */       if (thread.worked)
/*     */       {
/* 224 */         if (thread.overrideSelector)
/* 225 */           ProxySelector.setDefault(null);
/*     */         else {
/* 227 */           ProxySelector.setDefault(JWDetectedProxy.DEFAULT_SELECTOR);
/*     */         }
/* 229 */         JWDetectedProxy.DETECTED_PROXY_OVERWRITE_SELECTOR = thread.overrideSelector;
/*     */ 
/* 231 */         break;
/*     */       }
/*     */     }
/* 234 */     return true;
/*     */   }
/*     */ 
/*     */   public static boolean detectAndSetProxyFor(String appName, File appsSharedConfigFolder, URL url, int timeoutMS)
/*     */   {
/* 326 */     File wrapperProxy = new File(appsSharedConfigFolder, "DetectedProxy");
/* 327 */     File detectedProxies = new File(appsSharedConfigFolder, "DetectedProxies");
/* 328 */     File userProxies = new File(appsSharedConfigFolder, "AppProxies");
/* 329 */     File credentialsFile = new File(appsSharedConfigFolder, "ProxyCredentials");
/* 330 */     File lastProxy = new File(appsSharedConfigFolder, "LastProxy");
/*     */     try
/*     */     {
/* 333 */       if (!lastProxy.exists())
/* 334 */         lastProxy.createNewFile();
/*     */     }
/*     */     catch (IOException ex)
/*     */     {
/* 338 */       System.out.println("[JWAsyncProxyDetector] Unable to create LastProxy file.");
/* 339 */       ex.printStackTrace();
/*     */     }
/*     */ 
/* 342 */     JWProxyCredentials credentials = new JWProxyCredentials();
/*     */     try
/*     */     {
/* 345 */       credentials.loadFromFile(credentialsFile);
/*     */     }
/*     */     catch (IOException ex)
/*     */     {
/* 349 */       System.out.println("[JWAsyncProxyDetector] Unable to load existing credentials from file.");
/* 350 */       ex.printStackTrace();
/*     */     }
/*     */ 
/* 353 */     JWProxyList proxyList = new JWProxyList(10);
/* 354 */     processHintedProxy(wrapperProxy, proxyList, credentials);
/*     */     try
/*     */     {
/* 357 */       proxyList.loadFromFile(detectedProxies);
/*     */     }
/*     */     catch (IOException ex)
/*     */     {
/* 361 */       System.out.println("[JWAsyncProxyDetector] Unable to load proxy list from file.");
/* 362 */       ex.printStackTrace();
/*     */     }
/*     */ 
/* 368 */     JWProxyList clonedList = (JWProxyList)proxyList.clone();
/*     */     try
/*     */     {
/* 373 */       proxyList.loadFromFile(userProxies);
/*     */     }
/*     */     catch (IOException ex)
/*     */     {
/* 377 */       System.out.println("[JWAsyncProxyDetector] Unable to load proxy list from file.");
/*     */     }
/*     */ 
/* 380 */     url = URIUtil.tryGetSafeURLFrom(url);
/*     */ 
/* 382 */     JWAsyncProxyDetector proxyDetector = new JWAsyncProxyDetector(url, proxyList, credentials, timeoutMS);
/* 383 */     boolean worked = proxyDetector.detectProxyAndBlock(appName);
/*     */ 
/* 385 */     if (JWDetectedProxy.DETECTED_PROXY_OK)
/*     */     {
/* 387 */       System.out.println("[JWAsyncProxyDetector] Detected proxy succesfully");
/*     */       try
/*     */       {
/* 391 */         JWDetectedProxy.setDetectedProxyAsDefault();
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 395 */         System.out.println("[JWAsyncProxyDetector] Unable to set detected proxy as default");
/* 396 */         t.printStackTrace();
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 401 */         JWDetectedProxy.saveDetectedProxy();
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 405 */         System.out.println("[JWAsyncProxyDetector] Unable to save detected proxy to disk");
/* 406 */         t.printStackTrace();
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 412 */         if (JWDetectedProxy.DETECTED_PROXY == null) {
/*     */           break label388;
/*     */         }
/* 415 */         clonedList.addProxyToFront(JWDetectedProxy.DETECTED_PROXY);
/*     */ 
/* 417 */         System.out.println("[JWAsyncProxyDetector] Saving detected proxies...");
/* 418 */         clonedList.saveToFile(detectedProxies);
/*     */ 
/* 420 */         System.out.println("[JWAsyncProxyDetector] Saving credentials...");
/* 421 */         credentials.saveToFile(credentialsFile);
/*     */ 
/* 423 */         JWGenericOS.setWritableForAllUsers(detectedProxies, false);
/* 424 */         JWGenericOS.setWritableForAllUsers(credentialsFile, false);
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 429 */         System.out.println("[JWAsyncProxyDetector] Unable to save detected proxies and credentials");
/* 430 */         t.printStackTrace();
/*     */       }
/*     */     }
/*     */     else {
/* 434 */       System.out.println("[JWAsyncProxyDetector] No proxy detected");
/*     */     }
/* 436 */     label388: return worked;
/*     */   }
/*     */ 
/*     */   private static void processHintedProxy(File wrapperProxy, JWProxyList proxyList, JWProxyCredentials credentials)
/*     */   {
/*     */     try
/*     */     {
/* 444 */       if (wrapperProxy.exists())
/*     */       {
/* 446 */         FileInputStream fin = new FileInputStream(wrapperProxy);
/*     */         try
/*     */         {
/* 449 */           String hints = StreamUtils.readAllAsString(fin);
/* 450 */           String[] data = hints.split("\\n");
/* 451 */           String allProxyHosts = data[0].trim();
/* 452 */           String protocol = "http";
/*     */ 
/* 455 */           String bypassList = null;
/* 456 */           if (data.length > 1) {
/* 457 */             bypassList = data[1].trim();
/*     */           }
/* 459 */           ArrayList proxyHostsToParse = new ArrayList();
/*     */           String individualProxy;
/* 461 */           if (OS.isWindows())
/*     */           {
/* 465 */             String[] proxies = allProxyHosts.split(";");
/* 466 */             for (individualProxy : proxies)
/* 467 */               proxyHostsToParse.add(individualProxy.replace("=", "://"));
/*     */           }
/*     */           else
/*     */           {
/* 471 */             proxyHostsToParse.add(allProxyHosts);
/*     */           }
/*     */ 
/* 474 */           for (String proxyHost : proxyHostsToParse)
/*     */           {
/* 478 */             System.out.println("[JWAsyncProxyDetector] Parsing proxy " + proxyHost);
/*     */ 
/* 481 */             int doubleSlash = proxyHost.indexOf("://");
/* 482 */             if (doubleSlash != -1)
/*     */             {
/* 484 */               protocol = proxyHost.substring(0, doubleSlash);
/* 485 */               proxyHost = proxyHost.substring(doubleSlash + 3);
/*     */             }
/*     */ 
/* 490 */             if (proxyHost.endsWith("/")) {
/* 491 */               proxyHost = proxyHost.substring(0, proxyHost.length() - 1);
/*     */             }
/*     */ 
/* 494 */             int atIndex = proxyHost.indexOf('@');
/* 495 */             String usernameBit = null;
/* 496 */             String hostBit = proxyHost;
/* 497 */             if (atIndex != -1)
/*     */             {
/* 499 */               usernameBit = proxyHost.substring(0, atIndex);
/* 500 */               hostBit = proxyHost.substring(atIndex + 1);
/*     */             }
/*     */ 
/* 503 */             System.out.println("[JWAsyncProxyDetector] Protocol: " + protocol + " Host: " + hostBit + " User: " + usernameBit);
/*     */ 
/* 505 */             String username = null;
/* 506 */             String password = null;
/* 507 */             String hostname = null;
/* 508 */             String port = null;
/*     */ 
/* 510 */             if (hostBit.indexOf(':') == -1)
/*     */             {
/* 513 */               hostname = hostBit;
/* 514 */               port = "80";
/*     */             }
/*     */             else
/*     */             {
/* 518 */               String[] hostBits = hostBit.split(":");
/* 519 */               hostname = hostBits[0].trim();
/* 520 */               port = hostBits[1].trim();
/*     */             }
/*     */ 
/* 523 */             if ((usernameBit != null) && (usernameBit.indexOf(':') != -1))
/*     */             {
/* 525 */               String[] usernameBits = usernameBit.split(":");
/* 526 */               username = usernameBits[0];
/* 527 */               password = usernameBits[1];
/*     */             }
/*     */ 
/* 530 */             int portInt = 80;
/*     */             try
/*     */             {
/* 533 */               portInt = Integer.parseInt(port);
/*     */             }
/*     */             catch (Throwable t) {
/* 536 */               t.printStackTrace();
/*     */             }
/*     */ 
/* 539 */             System.out.println("[JWAsyncProxyDetector] Hostname: " + hostname + " Port: " + portInt);
/*     */ 
/* 541 */             Proxy.Type proxyType = Proxy.Type.HTTP;
/* 542 */             if (protocol != null)
/*     */             {
/* 544 */               if (protocol.equalsIgnoreCase("socks")) {
/* 545 */                 proxyType = Proxy.Type.SOCKS; } else {
/* 546 */                 if (!protocol.toLowerCase().startsWith("http")) continue;
/* 547 */                 proxyType = Proxy.Type.HTTP;
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 552 */             InetSocketAddress address = new InetSocketAddress(hostname, portInt);
/* 553 */             Proxy proxy = new Proxy(proxyType, address);
/*     */ 
/* 555 */             proxyList.addProxyToFront(proxy);
/* 556 */             if (username != null)
/*     */             {
/* 558 */               JWProxyCredentials.Credentials c = new JWProxyCredentials.Credentials(username, password);
/* 559 */               credentials.setCredentialsFor(proxy, c);
/*     */             }
/* 561 */             System.out.println("[JWAsyncProxyDetector] Constructed proxy " + proxy);
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/* 566 */           fin.close();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 572 */       System.out.println("[JWAsyncProxyDetector] Unable to process wrapper hinted proxy.");
/* 573 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   class ProxyDetectionThread
/*     */     implements Runnable
/*     */   {
/*     */     private Proxy proxy;
/*     */     private URL targetURL;
/* 241 */     private boolean requiresAuthentication = false;
/*     */     private boolean overrideSelector;
/* 243 */     private boolean worked = false;
/*     */ 
/*     */     public ProxyDetectionThread(URL targetURL, Proxy proxy, boolean overrideSelector)
/*     */     {
/* 247 */       this.proxy = proxy;
/* 248 */       this.targetURL = targetURL;
/* 249 */       this.overrideSelector = overrideSelector;
/*     */     }
/*     */ 
/*     */     public void printMessage(String message)
/*     */     {
/* 254 */       if (this.proxy != null)
/* 255 */         System.out.println("[JWAsyncProxyDetector][" + this.proxy + "][" + this.overrideSelector + "] " + message);
/*     */       else
/* 257 */         System.out.println("[JWAsyncProxyDetector][Default][" + this.overrideSelector + "] " + message);
/*     */     }
/*     */ 
/*     */     // ERROR //
/*     */     public void run()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: ldc 81
/*     */       //   3: invokevirtual 83	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:printMessage	(Ljava/lang/String;)V
/*     */       //   6: aconst_null
/*     */       //   7: astore_1
/*     */       //   8: aload_0
/*     */       //   9: getfield 29	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:proxy	Ljava/net/Proxy;
/*     */       //   12: ifnonnull +14 -> 26
/*     */       //   15: aload_0
/*     */       //   16: getfield 31	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:targetURL	Ljava/net/URL;
/*     */       //   19: invokevirtual 85	java/net/URL:openConnection	()Ljava/net/URLConnection;
/*     */       //   22: astore_1
/*     */       //   23: goto +15 -> 38
/*     */       //   26: aload_0
/*     */       //   27: getfield 31	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:targetURL	Ljava/net/URL;
/*     */       //   30: aload_0
/*     */       //   31: getfield 29	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:proxy	Ljava/net/Proxy;
/*     */       //   34: invokevirtual 91	java/net/URL:openConnection	(Ljava/net/Proxy;)Ljava/net/URLConnection;
/*     */       //   37: astore_1
/*     */       //   38: aload_1
/*     */       //   39: aload_0
/*     */       //   40: getfield 20	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:this$0	Ljwrapper/proxy/JWAsyncProxyDetector;
/*     */       //   43: invokestatic 94	jwrapper/proxy/JWAsyncProxyDetector:access$0	(Ljwrapper/proxy/JWAsyncProxyDetector;)I
/*     */       //   46: invokevirtual 100	java/net/URLConnection:setConnectTimeout	(I)V
/*     */       //   49: aload_1
/*     */       //   50: invokevirtual 106	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
/*     */       //   53: astore_2
/*     */       //   54: aload_2
/*     */       //   55: invokevirtual 110	java/io/InputStream:close	()V
/*     */       //   58: aload_0
/*     */       //   59: ldc 115
/*     */       //   61: invokevirtual 83	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:printMessage	(Ljava/lang/String;)V
/*     */       //   64: aload_0
/*     */       //   65: getfield 20	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:this$0	Ljwrapper/proxy/JWAsyncProxyDetector;
/*     */       //   68: invokestatic 117	jwrapper/proxy/JWAsyncProxyDetector:access$1	(Ljwrapper/proxy/JWAsyncProxyDetector;)Ljava/lang/Object;
/*     */       //   71: dup
/*     */       //   72: astore_3
/*     */       //   73: monitorenter
/*     */       //   74: getstatic 121	jwrapper/proxy/JWDetectedProxy:DETECTED_PROXY_OK	Z
/*     */       //   77: ifne +46 -> 123
/*     */       //   80: aload_0
/*     */       //   81: ldc 126
/*     */       //   83: invokevirtual 83	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:printMessage	(Ljava/lang/String;)V
/*     */       //   86: aload_0
/*     */       //   87: getfield 29	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:proxy	Ljava/net/Proxy;
/*     */       //   90: ifnull +17 -> 107
/*     */       //   93: aload_0
/*     */       //   94: getfield 20	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:this$0	Ljwrapper/proxy/JWAsyncProxyDetector;
/*     */       //   97: invokestatic 128	jwrapper/proxy/JWAsyncProxyDetector:access$2	(Ljwrapper/proxy/JWAsyncProxyDetector;)Ljwrapper/proxy/JWProxyAuthenticator;
/*     */       //   100: aload_0
/*     */       //   101: getfield 29	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:proxy	Ljava/net/Proxy;
/*     */       //   104: invokevirtual 132	jwrapper/proxy/JWProxyAuthenticator:saveInterceptCredentials	(Ljava/net/Proxy;)V
/*     */       //   107: iconst_1
/*     */       //   108: putstatic 121	jwrapper/proxy/JWDetectedProxy:DETECTED_PROXY_OK	Z
/*     */       //   111: aload_0
/*     */       //   112: getfield 29	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:proxy	Ljava/net/Proxy;
/*     */       //   115: putstatic 138	jwrapper/proxy/JWDetectedProxy:DETECTED_PROXY	Ljava/net/Proxy;
/*     */       //   118: aload_0
/*     */       //   119: iconst_1
/*     */       //   120: putfield 27	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:worked	Z
/*     */       //   123: aload_3
/*     */       //   124: monitorexit
/*     */       //   125: goto +109 -> 234
/*     */       //   128: aload_3
/*     */       //   129: monitorexit
/*     */       //   130: athrow
/*     */       //   131: astore_2
/*     */       //   132: aload_2
/*     */       //   133: invokevirtual 141	java/lang/Throwable:getMessage	()Ljava/lang/String;
/*     */       //   136: ldc 146
/*     */       //   138: invokevirtual 148	java/lang/String:indexOf	(Ljava/lang/String;)I
/*     */       //   141: iconst_m1
/*     */       //   142: if_icmpeq +8 -> 150
/*     */       //   145: aload_0
/*     */       //   146: iconst_1
/*     */       //   147: putfield 25	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:requiresAuthentication	Z
/*     */       //   150: aload_0
/*     */       //   151: new 47	java/lang/StringBuilder
/*     */       //   154: dup
/*     */       //   155: ldc 154
/*     */       //   157: invokespecial 51	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */       //   160: aload_2
/*     */       //   161: invokevirtual 156	java/lang/Object:getClass	()Ljava/lang/Class;
/*     */       //   164: invokevirtual 160	java/lang/Class:getName	()Ljava/lang/String;
/*     */       //   167: invokevirtual 59	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   170: ldc 165
/*     */       //   172: invokevirtual 59	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   175: aload_2
/*     */       //   176: invokevirtual 141	java/lang/Throwable:getMessage	()Ljava/lang/String;
/*     */       //   179: invokevirtual 59	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   182: invokevirtual 67	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */       //   185: invokevirtual 83	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:printMessage	(Ljava/lang/String;)V
/*     */       //   188: aload_1
/*     */       //   189: instanceof 167
/*     */       //   192: ifeq +61 -> 253
/*     */       //   195: aload_1
/*     */       //   196: checkcast 167	java/net/HttpURLConnection
/*     */       //   199: invokevirtual 169	java/net/HttpURLConnection:disconnect	()V
/*     */       //   202: goto +51 -> 253
/*     */       //   205: astore 5
/*     */       //   207: goto +46 -> 253
/*     */       //   210: astore 4
/*     */       //   212: aload_1
/*     */       //   213: instanceof 167
/*     */       //   216: ifeq +15 -> 231
/*     */       //   219: aload_1
/*     */       //   220: checkcast 167	java/net/HttpURLConnection
/*     */       //   223: invokevirtual 169	java/net/HttpURLConnection:disconnect	()V
/*     */       //   226: goto +5 -> 231
/*     */       //   229: astore 5
/*     */       //   231: aload 4
/*     */       //   233: athrow
/*     */       //   234: aload_1
/*     */       //   235: instanceof 167
/*     */       //   238: ifeq +15 -> 253
/*     */       //   241: aload_1
/*     */       //   242: checkcast 167	java/net/HttpURLConnection
/*     */       //   245: invokevirtual 169	java/net/HttpURLConnection:disconnect	()V
/*     */       //   248: goto +5 -> 253
/*     */       //   251: astore 5
/*     */       //   253: aload_0
/*     */       //   254: getfield 20	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:this$0	Ljwrapper/proxy/JWAsyncProxyDetector;
/*     */       //   257: invokestatic 117	jwrapper/proxy/JWAsyncProxyDetector:access$1	(Ljwrapper/proxy/JWAsyncProxyDetector;)Ljava/lang/Object;
/*     */       //   260: dup
/*     */       //   261: astore_2
/*     */       //   262: monitorenter
/*     */       //   263: aload_0
/*     */       //   264: getfield 20	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:this$0	Ljwrapper/proxy/JWAsyncProxyDetector;
/*     */       //   267: dup
/*     */       //   268: invokestatic 172	jwrapper/proxy/JWAsyncProxyDetector:access$3	(Ljwrapper/proxy/JWAsyncProxyDetector;)I
/*     */       //   271: iconst_1
/*     */       //   272: isub
/*     */       //   273: invokestatic 175	jwrapper/proxy/JWAsyncProxyDetector:access$4	(Ljwrapper/proxy/JWAsyncProxyDetector;I)V
/*     */       //   276: aload_0
/*     */       //   277: getfield 20	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:this$0	Ljwrapper/proxy/JWAsyncProxyDetector;
/*     */       //   280: invokestatic 117	jwrapper/proxy/JWAsyncProxyDetector:access$1	(Ljwrapper/proxy/JWAsyncProxyDetector;)Ljava/lang/Object;
/*     */       //   283: invokevirtual 179	java/lang/Object:notify	()V
/*     */       //   286: aload_0
/*     */       //   287: new 47	java/lang/StringBuilder
/*     */       //   290: dup
/*     */       //   291: ldc 182
/*     */       //   293: invokespecial 51	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */       //   296: aload_0
/*     */       //   297: getfield 20	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:this$0	Ljwrapper/proxy/JWAsyncProxyDetector;
/*     */       //   300: invokestatic 172	jwrapper/proxy/JWAsyncProxyDetector:access$3	(Ljwrapper/proxy/JWAsyncProxyDetector;)I
/*     */       //   303: invokevirtual 184	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */       //   306: ldc 187
/*     */       //   308: invokevirtual 59	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   311: invokevirtual 67	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */       //   314: invokevirtual 83	jwrapper/proxy/JWAsyncProxyDetector$ProxyDetectionThread:printMessage	(Ljava/lang/String;)V
/*     */       //   317: aload_2
/*     */       //   318: monitorexit
/*     */       //   319: goto +6 -> 325
/*     */       //   322: aload_2
/*     */       //   323: monitorexit
/*     */       //   324: athrow
/*     */       //   325: return
/*     */       //
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   74	125	128	finally
/*     */       //   128	130	128	finally
/*     */       //   8	131	131	java/lang/Throwable
/*     */       //   188	202	205	java/lang/Throwable
/*     */       //   8	188	210	finally
/*     */       //   212	226	229	java/lang/Throwable
/*     */       //   234	248	251	java/lang/Throwable
/*     */       //   263	319	322	finally
/*     */       //   322	324	322	finally
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.proxy.JWAsyncProxyDetector
 * JD-Core Version:    0.6.2
 */