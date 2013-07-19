/*    */ package jwrapper;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.PrintStream;
/*    */ import java.lang.reflect.Method;
/*    */ import java.net.URI;
/*    */ import java.net.URL;
/*    */ import java.net.URLClassLoader;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import jwrapper.jwutils.JWGenericOS;
/*    */ import utils.vm.VMFork;
/*    */ 
/*    */ public class VMTransmuter extends Thread
/*    */ {
/*    */   Method main;
/*    */   String[] params;
/*    */ 
/*    */   private VMTransmuter(Method main, String[] params)
/*    */   {
/* 19 */     setName("JWTransmuteMain");
/* 20 */     this.main = main;
/* 21 */     this.params = params;
/*    */   }
/*    */ 
/*    */   public static void transmuteInto(VMFork fork) throws Exception {
/* 25 */     ArrayList urls = new ArrayList();
/*    */ 
/* 27 */     HashMap seen = new HashMap();
/*    */ 
/* 29 */     String[] jars = fork.getClasspath().split(System.getProperty("path.separator"));
/* 30 */     for (int i = 0; i < jars.length; i++) {
/* 31 */       String jar = jars[i].trim();
/*    */ 
/* 33 */       if (!seen.containsKey(jar)) {
/* 34 */         seen.put(jar, jar);
/*    */ 
/* 36 */         urls.add(new File(jar).toURI().toURL());
/*    */ 
/* 38 */         System.setProperty("java.class.path", System.getProperty("java.class.path") + System.getProperty("path.separator") + new File(jar).getCanonicalPath());
/*    */       }
/*    */     }
/*    */ 
/* 42 */     URL[] all = new URL[urls.size()];
/* 43 */     urls.toArray(all);
/*    */ 
/* 45 */     URLClassLoader loader = new URLClassLoader(all, ClassLoader.getSystemClassLoader());
/*    */ 
/* 58 */     Class target = loader.loadClass(fork.getClassname());
/* 59 */     Method main = target.getDeclaredMethod("main", new Class[] { [Ljava.lang.String.class });
/*    */ 
/* 67 */     System.out.println("Changing dir to " + fork.getWorkingDir() + " for transmute");
/*    */ 
/* 70 */     JWGenericOS.setCurrentDirectory(fork.getWorkingDir());
/*    */ 
/* 73 */     System.out.println("Transmuting, dir is now " + new File(".").getCanonicalPath());
/*    */ 
/* 75 */     new VMTransmuter(main, fork.getParams()).start();
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     try {
/* 81 */       this.main.invoke(null, new Object[] { this.params });
/*    */     } catch (Throwable t) {
/* 83 */       t.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.VMTransmuter
 * JD-Core Version:    0.6.2
 */