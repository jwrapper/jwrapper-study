/*    */ package jwrapper.updater;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.PrintStream;
/*    */ import jwrapper.jwutils.JWJreVerifierApp;
/*    */ import jwrapper.jwutils.JWSystem;
/*    */ import utils.progtools.CheapTimingPrintStream;
/*    */ 
/*    */ public class GenericUpdaterJreVerifier
/*    */ {
/*    */   public static final int UJRE_OK = 72;
/*    */   public static final int UJRE_NOTSUPPORTED = 73;
/*    */ 
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 18 */     args = JWLaunchProperties.argsToNormalArgs(args);
/*    */     try
/*    */     {
/* 21 */       PrintStream out = new CheapTimingPrintStream(new FileOutputStream(new File(JWSystem.getAppFolder(), "GenericUpdaterJreVerifier.log")));
/* 22 */       System.setErr(out);
/* 23 */       System.setOut(out);
/*    */     }
/*    */     catch (Throwable localThrowable) {
/*    */     }
/* 27 */     String vendor = System.getProperty("java.vendor").toUpperCase();
/* 28 */     String version = System.getProperty("java.version");
/*    */ 
/* 30 */     System.out.println("Vendor: " + vendor);
/* 31 */     System.out.println("Version: " + version);
/*    */ 
/* 33 */     if ((vendor.indexOf("SUN") == -1) && (vendor.indexOf("ORACLE") == -1)) {
/* 34 */       System.out.println("Only Sun or Oracle JREs supported as yet");
/* 35 */       JWJreVerifierApp.exitJvm_JreIsNotSupported();
/*    */     }
/*    */ 
/* 38 */     if ((version.startsWith("1.0")) || 
/* 39 */       (version.startsWith("1.1")) || 
/* 40 */       (version.startsWith("1.2")) || 
/* 41 */       (version.startsWith("1.3"))) {
/* 42 */       System.out.println("Only Java 1.4 or later JREs supported");
/* 43 */       JWJreVerifierApp.exitJvm_JreIsNotSupported();
/*    */     }
/*    */ 
/* 46 */     JWJreVerifierApp.exitJvm_JreIsSupported();
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.updater.GenericUpdaterJreVerifier
 * JD-Core Version:    0.6.2
 */