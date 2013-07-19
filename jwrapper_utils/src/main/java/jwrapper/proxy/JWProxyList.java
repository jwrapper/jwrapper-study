/*     */ package jwrapper.proxy;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.Proxy.Type;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import utils.stream.StreamUtils;
/*     */ 
/*     */ public class JWProxyList
/*     */ {
/*  19 */   private LinkedList<Proxy> list = new LinkedList();
/*     */   private int maxSize;
/*     */ 
/*     */   public JWProxyList(int maxSize)
/*     */   {
/*  24 */     this.maxSize = maxSize;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*  29 */     JWProxyList newList = new JWProxyList(this.maxSize);
/*  30 */     newList.list = ((LinkedList)this.list.clone());
/*  31 */     return newList;
/*     */   }
/*     */ 
/*     */   public boolean containsProxy(Proxy proxy)
/*     */   {
/*  36 */     return this.list.contains(proxy);
/*     */   }
/*     */ 
/*     */   public void loadFromFile(File file) throws IOException
/*     */   {
/*  41 */     if (file.exists())
/*     */     {
/*  43 */       BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
/*     */       try
/*     */       {
/*  46 */         int proxyCount = StreamUtils.readInt(bin);
/*  47 */         for (int i = 0; i < proxyCount; i++)
/*     */         {
/*  49 */           String type = StreamUtils.readStringUTF8(bin);
/*  50 */           String hostname = StreamUtils.readStringUTF8(bin);
/*  51 */           int port = StreamUtils.readInt(bin);
/*     */           try
/*     */           {
/*  55 */             InetSocketAddress address = new InetSocketAddress(hostname, port);
/*  56 */             this.list.add(new Proxy(Proxy.Type.valueOf(type), address));
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/*  60 */             System.out.println("[JWProxyList] Could not load proxy " + type + ":" + hostname + ":" + port + ".");
/*  61 */             t.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*  67 */         t.printStackTrace();
/*     */       }
/*     */       finally
/*     */       {
/*  71 */         bin.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getHostnameFrom(InetSocketAddress isa)
/*     */   {
/*     */     try
/*     */     {
/*  81 */       Method method = InetSocketAddress.class.getDeclaredMethod("getHostString", new Class[0]);
/*  82 */       method.setAccessible(true);
/*     */ 
/*  84 */       Object result = method.invoke(isa, new Object[0]);
/*  85 */       if (result != null) {
/*  86 */         return result.toString();
/*     */       }
/*  88 */       System.out.println("[JWProxyList] Warning: JWProxyList returned null for getHostString");
/*  89 */       return isa.getHostName();
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*  93 */       System.out.println("[JWProxyList] Warning: JWProxyList failed to call getHostString (" + t.getMessage() + ")");
/*  94 */     }return isa.getHostName();
/*     */   }
/*     */ 
/*     */   public void saveToFile(File file)
/*     */     throws IOException
/*     */   {
/* 100 */     BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
/*     */     try
/*     */     {
/* 103 */       StreamUtils.writeInt(bout, this.list.size());
/* 104 */       for (int i = 0; i < this.list.size(); i++)
/*     */       {
/* 106 */         Proxy proxy = (Proxy)this.list.get(i);
/* 107 */         if (proxy != null)
/*     */         {
/* 109 */           InetSocketAddress address = (InetSocketAddress)proxy.address();
/* 110 */           StreamUtils.writeStringUTF8(bout, proxy.type().name());
/* 111 */           StreamUtils.writeStringUTF8(bout, getHostnameFrom(address));
/* 112 */           StreamUtils.writeInt(bout, address.getPort());
/*     */         }
/*     */       }
/* 115 */       System.out.println("[JWProxyList] Saved " + this.list.size() + " proxy descriptions");
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 119 */       t.printStackTrace();
/*     */     }
/*     */     finally
/*     */     {
/* 123 */       bout.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Iterator<Proxy> getProxyIterator()
/*     */   {
/* 129 */     return this.list.iterator();
/*     */   }
/*     */ 
/*     */   public void addProxyToFront(Proxy proxy)
/*     */   {
/* 134 */     if (proxy.address() == null) {
/* 135 */       return;
/*     */     }
/*     */ 
/* 138 */     if (containsProxy(proxy)) {
/* 139 */       return;
/*     */     }
/* 141 */     this.list.addFirst(proxy);
/* 142 */     if (this.list.size() > this.maxSize)
/* 143 */       this.list.removeLast();
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.proxy.JWProxyList
 * JD-Core Version:    0.6.2
 */