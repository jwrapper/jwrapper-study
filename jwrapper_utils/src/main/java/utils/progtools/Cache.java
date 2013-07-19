/*     */ package utils.progtools;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Random;
/*     */ 
/*     */ public class Cache
/*     */ {
/*   7 */   HashMap map = new HashMap();
/*   8 */   LinkedCacheList list = new LinkedCacheList();
/*     */ 
/*  10 */   int MAX_SIZE = 50;
/*     */ 
/*  12 */   double hits = 0.0D;
/*  13 */   double misses = 0.0D;
/*     */ 
/*     */   public double getTotalRequests() {
/*  16 */     return this.hits + this.misses;
/*     */   }
/*     */   public double getTotalHits() {
/*  19 */     return this.hits;
/*     */   }
/*     */   public double getTotalMisses() {
/*  22 */     return this.misses;
/*     */   }
/*     */   public double getHitRatePercent() {
/*  25 */     return 100.0D * (this.hits / (this.hits + this.misses));
/*     */   }
/*     */   public Cache() {
/*     */   }
/*     */ 
/*     */   public Cache(int max_size) {
/*  31 */     this.MAX_SIZE = max_size;
/*     */   }
/*     */ 
/*     */   public static int calculatePreferredSize(long sizeOfItem, long memoryToUse) {
/*  35 */     return (int)(memoryToUse / sizeOfItem);
/*     */   }
/*     */ 
/*     */   public void trimAndSetMaxSize(int max_size) {
/*  39 */     this.MAX_SIZE = max_size;
/*  40 */     while (this.map.size() > this.MAX_SIZE) {
/*  41 */       LinkedEntry rm = this.list.removeFirst();
/*     */ 
/*  43 */       rm.next = null;
/*  44 */       rm.prev = null;
/*  45 */       if (rm != null)
/*  46 */         this.map.remove(rm.valueKey);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object key)
/*     */   {
/*  52 */     return this.map.containsKey(key);
/*     */   }
/*     */ 
/*     */   public void clear() {
/*  56 */     this.map = new HashMap();
/*  57 */     this.list = new LinkedCacheList();
/*     */   }
/*     */ 
/*     */   public int maxSize() {
/*  61 */     return this.MAX_SIZE;
/*     */   }
/*     */   public int size() {
/*  64 */     return this.map.size();
/*     */   }
/*     */ 
/*     */   public void addToCache(Object key, Object value) {
/*  68 */     addToCache(key, value, null);
/*     */   }
/*     */ 
/*     */   public void addToCache(Object key, Object value, CacheListener listener)
/*     */   {
/*  73 */     LinkedEntry e = (LinkedEntry)this.map.get(key);
/*  74 */     if (e == null)
/*     */     {
/*  80 */       this.map.put(key, this.list.add(key, value));
/*     */ 
/*  82 */       if (this.map.size() > this.MAX_SIZE) {
/*  83 */         LinkedEntry rm = this.list.removeFirst();
/*     */ 
/*  85 */         rm.next = null;
/*  86 */         rm.prev = null;
/*  87 */         if (rm != null) {
/*  88 */           this.map.remove(rm.valueKey);
/*  89 */           if (listener != null)
/*  90 */             listener.removedFromCache(rm.valueKey, rm.valueValue);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object getFromCache(Object key)
/*     */   {
/*  98 */     LinkedEntry e = (LinkedEntry)this.map.get(key);
/*     */ 
/* 100 */     if (e == null)
/*     */     {
/* 102 */       this.misses += 1.0D;
/*     */ 
/* 104 */       return null;
/*     */     }
/*     */ 
/* 108 */     this.hits += 1.0D;
/*     */ 
/* 110 */     this.list.moveToFront(e);
/*     */ 
/* 112 */     return e.valueValue;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 201 */     Cache cache = new Cache(8);
/*     */ 
/* 203 */     Random rand = new Random(5L);
/*     */ 
/* 205 */     int hits = 0;
/* 206 */     int misses = 0;
/*     */ 
/* 208 */     double COUNT = 2000000.0D;
/*     */ 
/* 210 */     double t = System.currentTimeMillis();
/*     */ 
/* 212 */     for (int i = 0; i < COUNT; i++) {
/* 213 */       Integer key = new Integer(rand.nextInt(15));
/*     */ 
/* 217 */       String value = (String)cache.getFromCache(key);
/*     */ 
/* 219 */       if (value == null) {
/* 220 */         misses++;
/*     */ 
/* 223 */         value = "#" + key;
/* 224 */         cache.addToCache(key, value);
/*     */       } else {
/* 226 */         hits++;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 232 */     t = System.currentTimeMillis() - t;
/*     */ 
/* 234 */     System.out.println(hits + " hits");
/* 235 */     System.out.println(misses + " misses");
/* 236 */     System.out.println("Cache size: " + cache.size());
/*     */ 
/* 238 */     System.out.println(COUNT + " lookups in " + t + "ms");
/* 239 */     System.out.println(t / COUNT + " ms per lookups");
/*     */   }
/*     */ 
/*     */   class LinkedCacheList
/*     */   {
/*     */     Cache.LinkedEntry start;
/*     */     Cache.LinkedEntry end;
/*     */ 
/*     */     LinkedCacheList()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Cache.LinkedEntry add(Object key, Object value)
/*     */     {
/* 121 */       Cache.LinkedEntry e = new Cache.LinkedEntry(Cache.this);
/* 122 */       e.valueKey = key;
/* 123 */       e.valueValue = value;
/* 124 */       e.next = null;
/*     */ 
/* 126 */       if (this.end == null)
/*     */       {
/* 128 */         this.start = e;
/* 129 */         this.end = e;
/* 130 */         e.prev = null;
/*     */       }
/*     */       else {
/* 133 */         this.end.next = e;
/* 134 */         e.prev = this.end;
/* 135 */         this.end = e;
/*     */       }
/* 137 */       return e;
/*     */     }
/*     */ 
/*     */     public Cache.LinkedEntry removeFirst() {
/* 141 */       if (this.start != null) {
/* 142 */         if (this.start == this.end)
/*     */         {
/* 144 */           this.end = null;
/*     */         }
/*     */ 
/* 147 */         Cache.LinkedEntry ret = this.start;
/* 148 */         this.start = this.start.next;
/* 149 */         if (this.start != null) {
/* 150 */           this.start.prev = null;
/*     */         }
/* 152 */         return ret;
/*     */       }
/* 154 */       return null;
/*     */     }
/*     */ 
/*     */     public void moveToFront(Cache.LinkedEntry e)
/*     */     {
/* 161 */       if (e != this.end) {
/* 162 */         if (e == this.start) {
/* 163 */           this.start = e.next;
/* 164 */           this.start.prev = null;
/*     */         } else {
/* 166 */           e.prev.next = e.next;
/* 167 */           e.next.prev = e.prev;
/*     */         }
/* 169 */         this.end.next = e;
/* 170 */         e.prev = this.end;
/* 171 */         e.next = null;
/* 172 */         this.end = e;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class LinkedEntry
/*     */   {
/*     */     Object valueKey;
/*     */     Object valueValue;
/*     */     LinkedEntry prev;
/*     */     LinkedEntry next;
/*     */ 
/*     */     LinkedEntry()
/*     */     {
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 196 */       return this.valueKey;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.progtools.Cache
 * JD-Core Version:    0.6.2
 */