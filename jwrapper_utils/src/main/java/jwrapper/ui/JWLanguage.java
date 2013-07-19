/*     */ package jwrapper.ui;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import utils.stream.StreamUtils;
/*     */ 
/*     */ public class JWLanguage
/*     */ {
/*  11 */   private static HashMap translationsMap = null;
/*     */ 
/*  13 */   private static boolean bundleLoaded = false;
/*     */ 
/*     */   public static boolean isBundleLoaded()
/*     */   {
/*  17 */     return bundleLoaded;
/*     */   }
/*     */ 
/*     */   public static void loadTranslations(String bundle)
/*     */   {
/*  22 */     loadCoreTranslations();
/*     */     try
/*     */     {
/*  26 */       readFileIntoMap("translations/" + bundle.toLowerCase() + ".properties", translationsMap);
/*     */ 
/*  28 */       bundleLoaded = true;
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*  33 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void loadCoreTranslations()
/*     */   {
/*  39 */     if (translationsMap != null) {
/*  40 */       return;
/*     */     }
/*     */     try
/*     */     {
/*  44 */       translationsMap = new HashMap();
/*  45 */       readFileIntoMap("translations/translations.properties", translationsMap);
/*     */ 
/*  48 */       readFileIntoMap("translations/en.properties", translationsMap);
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*  52 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void readFileIntoMap(String filename, HashMap targetMap) throws IOException
/*     */   {
/*  58 */     InputStream fin = JWLanguage.class.getResourceAsStream(filename);
/*  59 */     String allStrings = StreamUtils.readAllAsStringUTF8(fin);
/*  60 */     String[] strings = allStrings.split("\\n");
/*  61 */     for (int i = 0; i < strings.length; i++)
/*     */     {
/*  63 */       String text = strings[i];
/*  64 */       if (text != null)
/*     */       {
/*  66 */         int indexOfEquals = text.indexOf('=');
/*  67 */         if (indexOfEquals != -1)
/*     */         {
/*  70 */           String key = text.substring(0, indexOfEquals).trim();
/*  71 */           String value = text.substring(indexOfEquals + 1).trim();
/*     */ 
/*  73 */           targetMap.put(key, value);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*  79 */   public static String getString(String key) { loadCoreTranslations();
/*     */ 
/*  81 */     if (translationsMap == null) {
/*  82 */       return key;
/*     */     }
/*  84 */     Object result = translationsMap.get(key);
/*  85 */     if (result != null)
/*  86 */       return result.toString();
/*  87 */     return key; }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws IOException
/*     */   {
/*  92 */     System.out.println(getString("LANGUAGE_EN"));
/*  93 */     loadTranslations("en");
/*  94 */     System.out.println(getString("LANGUAGE_EN"));
/*     */   }
/*     */ 
/*     */   public static boolean containsTranslationFor(String key)
/*     */   {
/*  99 */     loadCoreTranslations();
/*     */ 
/* 101 */     if (translationsMap == null) {
/* 102 */       return false;
/*     */     }
/* 104 */     return translationsMap.containsKey(key);
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.ui.JWLanguage
 * JD-Core Version:    0.6.2
 */