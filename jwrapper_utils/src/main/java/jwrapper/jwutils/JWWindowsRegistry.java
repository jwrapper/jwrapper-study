/*    */ package jwrapper.jwutils;
/*    */ 
/*    */ import jwrapper.hidden.JWNativeAPI;
/*    */ 
/*    */ public class JWWindowsRegistry
/*    */ {
/*    */   public static final String TYPE_BINARY = "REG_BINARY";
/*    */   public static final String TYPE_DWORD = "REG_DWORD";
/*    */   public static final String TYPE_DWORD_LE = "REG_DWORD_LE";
/*    */   public static final String TYPE_DWORD_BE = "REG_DWORD_BE";
/*    */   public static final String TYPE_EXPAND_SZ = "REG_EXPAND_SZ";
/*    */   public static final String TYPE_LINK = "REG_LINK";
/*    */   public static final String TYPE_MULTI_SZ = "REG_MULTI_SZ";
/*    */   public static final String TYPE_NONE = "REG_NONE";
/*    */   public static final String TYPE_QWORD = "REG_QWORD";
/*    */   public static final String TYPE_QWORD_LE = "REG_QWORD_LE";
/*    */   public static final String TYPE_SZ = "REG_SZ";
/*    */ 
/*    */   public boolean deleteValue(String hkey, String keyname, String valueName)
/*    */   {
/* 22 */     return JWNativeAPI.getInstance().regDeleteValue(hkey, keyname, valueName);
/*    */   }
/*    */   public static void deleteKey(String hkey, String pathToParentKey, String keyToDeleteName) {
/* 25 */     JWNativeAPI.getInstance().regDeleteKey(hkey, pathToParentKey, keyToDeleteName);
/*    */   }
/*    */   public static boolean regSet(String hkey, String keyName, String valueName, String valueValue, String type) {
/* 28 */     return JWNativeAPI.getInstance().regSet(hkey, keyName, valueName, valueValue, type);
/*    */   }
/*    */   public static String regGet(String hkey, String keyName, String valueName) {
/* 31 */     return JWNativeAPI.getInstance().regGet(hkey, keyName, valueName);
/*    */   }
/*    */   public static String[] regGetChildren(String hkey, String keyName) {
/* 34 */     return JWNativeAPI.getInstance().regGetChildren(hkey, keyName);
/*    */   }
/*    */   public static boolean regCreateKey(String rootKeyName, String subKeyName) {
/* 37 */     return JWNativeAPI.getInstance().regCreateKey(rootKeyName, subKeyName);
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.jwutils.JWWindowsRegistry
 * JD-Core Version:    0.6.2
 */