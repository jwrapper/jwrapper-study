/*     */ package utils.progtools;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class MapOfLists
/*     */ {
/*  13 */   private HashMap keyToLinkedList = new HashMap();
/*     */   private boolean permitDuplicates;
/*     */ 
/*     */   public Collection keySet()
/*     */   {
/*  17 */     return this.keyToLinkedList.keySet();
/*     */   }
/*     */   public Collection values() {
/*  20 */     return this.keyToLinkedList.values();
/*     */   }
/*     */ 
/*     */   public MapOfLists(boolean permitDuplicates)
/*     */   {
/*  25 */     this.permitDuplicates = permitDuplicates;
/*     */   }
/*     */ 
/*     */   public void add(Object key, Object value)
/*     */   {
/*  54 */     synchronized (this.keyToLinkedList)
/*     */     {
/*  56 */       LinkedList list = (LinkedList)this.keyToLinkedList.get(key);
/*  57 */       if (list == null)
/*     */       {
/*  59 */         list = new LinkedList();
/*  60 */         this.keyToLinkedList.put(key, list);
/*     */       }
/*  62 */       if (!this.permitDuplicates)
/*     */       {
/*  64 */         if (list.contains(value))
/*  65 */           return;
/*     */       }
/*  67 */       list.add(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public List get(Object key)
/*     */   {
/*  73 */     synchronized (this.keyToLinkedList)
/*     */     {
/*  75 */       return (List)this.keyToLinkedList.get(key);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeAll(Object key)
/*     */   {
/*  81 */     synchronized (this.keyToLinkedList)
/*     */     {
/*  83 */       this.keyToLinkedList.remove(key);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void remove(Object key, Object value)
/*     */   {
/*  89 */     synchronized (this.keyToLinkedList)
/*     */     {
/*  91 */       LinkedList list = (LinkedList)this.keyToLinkedList.get(key);
/*  92 */       if (list == null)
/*  93 */         return;
/*  94 */       list.remove(value);
/*  95 */       if (list.size() == 0)
/*  96 */         this.keyToLinkedList.remove(key);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean exists(Object key, Object value)
/*     */   {
/* 102 */     synchronized (this.keyToLinkedList)
/*     */     {
/* 104 */       LinkedList list = (LinkedList)this.keyToLinkedList.get(key);
/* 105 */       if (list == null)
/* 106 */         return false;
/* 107 */       return list.contains(value);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.progtools.MapOfLists
 * JD-Core Version:    0.6.2
 */