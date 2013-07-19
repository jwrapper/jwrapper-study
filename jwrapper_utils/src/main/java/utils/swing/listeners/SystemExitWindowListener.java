/*    */ package utils.swing.listeners;
/*    */ 
/*    */ import java.awt.event.WindowEvent;
/*    */ import java.awt.event.WindowListener;
/*    */ 
/*    */ public class SystemExitWindowListener
/*    */   implements WindowListener
/*    */ {
/*    */   public void windowActivated(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void windowClosed(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void windowClosing(WindowEvent e)
/*    */   {
/* 19 */     System.exit(0);
/*    */   }
/*    */ 
/*    */   public void windowDeactivated(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void windowDeiconified(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void windowIconified(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void windowOpened(WindowEvent e)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.listeners.SystemExitWindowListener
 * JD-Core Version:    0.6.2
 */