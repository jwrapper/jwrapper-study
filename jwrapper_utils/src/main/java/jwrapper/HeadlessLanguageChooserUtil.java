/*    */ package jwrapper;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public class HeadlessLanguageChooserUtil
/*    */ {
/*    */   public static String chooseLanguage(String[] supported)
/*    */   {
/* 12 */     if ((supported == null) || (supported.length == 0))
/* 13 */       return "en";
/* 14 */     if (supported.length == 1)
/* 15 */       return supported[0];
/*    */     try {
/* 17 */       Method method = Class.forName("jwrapper.ui.JWLanguageChooserFrame").getDeclaredMethod("chooseLanguage", new Class[] { [Ljava.lang.String.class });
/* 18 */       return (String)method.invoke(null, new Object[] { supported });
/*    */     } catch (Exception x) {
/* 20 */       x.printStackTrace();
/* 21 */     }return "en";
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.HeadlessLanguageChooserUtil
 * JD-Core Version:    0.6.2
 */