/*    */ package jwrapper.updater;
/*    */ 
/*    */ public class GenericUpdaterLaunch
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 12 */     String osname = System.getProperty("os.name");
/* 13 */     if ((osname.indexOf("mac") != -1) || 
/* 14 */       (osname.indexOf("darwin") != -1))
/*    */     {
/* 16 */       System.setProperty("apple.awt.UIElement", "true");
/*    */     }
/*    */ 
/* 24 */     StandalonePartitionedLauncher.launch(args, "jwrapper.updater.GenericUpdater");
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.updater.GenericUpdaterLaunch
 * JD-Core Version:    0.6.2
 */