/*    */ package utils.ostools;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class SlowWEnv
/*    */ {
/*    */   public static String getEnv(String name)
/*    */   {
/*  6 */     return RunCommandGetOutput.runCommandGetOutput("cmd.exe /c echo %" + name + "%")[0].trim();
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) throws Exception {
/* 10 */     System.out.println(getEnv("WINDIR"));
/* 11 */     System.out.println(getEnv(args[0]));
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.ostools.SlowWEnv
 * JD-Core Version:    0.6.2
 */