/*    */ package jwrapper.jwutils;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.ArrayList;
/*    */ import utils.ostools.osx.OSXAdapter;
/*    */ import utils.string.CharStack;
/*    */ 
/*    */ public class JWMacOS extends JWGenericOS
/*    */ {
/*    */   public static JWMacOS getMacOSInstance()
/*    */   {
/* 16 */     return JWGenericOS.macInstance;
/*    */   }
/*    */ 
/*    */   public static String getJvmUserAccount()
/*    */   {
/* 24 */     return utils.ostools.RunCommandGetOutput.runCommandGetOutput("whoami")[0];
/*    */   }
/*    */ 
/*    */   public static String[] getAllConsoleUsers()
/*    */   {
/* 32 */     String output = utils.ostools.RunCommandGetOutput.runCommandGetOutput(new String[] { "w", "-h" })[0];
/* 33 */     String[] lines = output.split("\n");
/*    */ 
/* 35 */     ArrayList users = new ArrayList();
/*    */ 
/* 37 */     for (int i = 0; i < lines.length; i++) {
/* 38 */       CharStack cs = new CharStack(lines[i]);
/* 39 */       cs.popWhitespace();
/* 40 */       String user = cs.popText(true);
/* 41 */       cs.popWhitespace();
/* 42 */       String console = cs.popText(true);
/* 43 */       if (console.toLowerCase().startsWith("console")) {
/* 44 */         System.out.println(user);
/* 45 */         users.add(user);
/*    */       } else {
/* 47 */         System.out.println("NOT " + lines[i]);
/*    */       }
/*    */     }
/*    */ 
/* 51 */     String[] all = new String[users.size()];
/* 52 */     users.toArray(all);
/* 53 */     return all;
/*    */   }
/*    */ 
/*    */   public static void setMyOSXAppName(String name)
/*    */   {
/* 61 */     OSXAdapter.setAppName(name);
/*    */   }
/*    */ 
/*    */   public static void setMyOSXAppBadge(String text)
/*    */   {
/* 69 */     OSXAdapter.setDockIconBadge(text);
/*    */   }
/*    */ 
/*    */   public static void requestUserAttention(boolean isCritical)
/*    */   {
/* 77 */     OSXAdapter.requestUserAttention(isCritical);
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 81 */     String[] tmp = getAllConsoleUsers();
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.jwutils.JWMacOS
 * JD-Core Version:    0.6.2
 */