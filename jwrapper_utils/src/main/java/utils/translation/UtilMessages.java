/*    */ package utils.translation;
/*    */ 
/*    */ import java.util.MissingResourceException;
/*    */ import java.util.ResourceBundle;
/*    */ 
/*    */ public class UtilMessages
/*    */ {
/*    */   private static final String BUNDLE_NAME = "utils.translation.messages";
/* 10 */   private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("utils.translation.messages");
/*    */ 
/*    */   public static String getString(String key)
/*    */   {
/*    */     try
/*    */     {
/* 20 */       return RESOURCE_BUNDLE.getString(key);
/*    */     }
/*    */     catch (MissingResourceException e) {
/*    */     }
/* 24 */     return '!' + key + '!';
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.translation.UtilMessages
 * JD-Core Version:    0.6.2
 */