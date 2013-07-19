/*    */ package jwrapper.jwutils;
/*    */ 
/*    */ public class JWJreVerifierApp
/*    */ {
/*    */   public static void exitJvm_JreIsSupported()
/*    */   {
/* 13 */     System.exit(72);
/*    */   }
/*    */ 
/*    */   public static void exitJvm_JreIsNotSupported()
/*    */   {
/* 19 */     System.exit(73);
/*    */   }
/*    */ 
/*    */   public static boolean isJvm14() {
/* 23 */     return System.getProperty("java.version").startsWith("1.4");
/*    */   }
/*    */   public static boolean isJvm15() {
/* 26 */     return System.getProperty("java.version").startsWith("1.5");
/*    */   }
/*    */   public static boolean isJvm16() {
/* 29 */     return System.getProperty("java.version").startsWith("1.6");
/*    */   }
/*    */   public static boolean isJvm17() {
/* 32 */     return System.getProperty("java.version").startsWith("1.7");
/*    */   }
/*    */   public static boolean isJvm18() {
/* 35 */     return System.getProperty("java.version").startsWith("1.8");
/*    */   }
/*    */   public static boolean isJvmOpenJDK() {
/* 38 */     String vmname = System.getProperty("java.vm.name");
/* 39 */     return vmname.toLowerCase().indexOf("openjdk") != -1;
/*    */   }
/*    */   public static boolean isJvmVendorSunOrOracle() {
/* 42 */     String vendor = System.getProperty("java.vendor");
/* 43 */     String vendorurl = System.getProperty("java.vendor.url");
/* 44 */     if ((vendorurl.indexOf("java.sun.com") != -1) || 
/* 45 */       (vendorurl.indexOf("java.oracle.com") != -1) || 
/* 46 */       (vendor.indexOf("Sun Microsystems") != -1) || 
/* 47 */       (vendor.indexOf("Oracle") != -1))
/*    */     {
/* 49 */       return true;
/*    */     }
/* 51 */     return false;
/*    */   }
/*    */ 
/*    */   public static boolean isJvm64bit() {
/* 55 */     String vmname = System.getProperty("java.vm.name");
/* 56 */     String sunarch = System.getProperty("sun.arch.data.model");
/*    */     boolean is64bit;
/*    */     try
/*    */     {
/* 62 */       if (sunarch.trim().length() == 0) {
/* 63 */         sunarch = null;
/*    */       }
/* 65 */       is64bit = sunarch.indexOf("64") != -1;
/*    */     }
/*    */     catch (Exception x)
/*    */     {
/*    */       boolean is64bit;
/* 68 */       is64bit = vmname.indexOf("64") != -1;
/*    */     }
/* 70 */     return is64bit;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.jwutils.JWJreVerifierApp
 * JD-Core Version:    0.6.2
 */