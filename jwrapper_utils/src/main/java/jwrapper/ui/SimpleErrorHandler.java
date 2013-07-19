/*     */ package jwrapper.ui;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.InputStream;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.UnsupportedLookAndFeelException;
/*     */ 
/*     */ public class SimpleErrorHandler
/*     */ {
/*     */   public static void displayError(String title, String message, Component component)
/*     */   {
/*  37 */     displayThrowable(null, title, message, null, component);
/*     */   }
/*     */ 
/*     */   public static void displayThrowable(Throwable throwable, String title, String submissionURL, Component component)
/*     */   {
/*  45 */     displayThrowable(throwable, title, null, submissionURL, component);
/*     */   }
/*     */ 
/*     */   public static void displayThrowable(final Throwable throwable, String title, final String message, String submissionURL, Component component)
/*     */   {
/*  61 */     String msg = message;
/*     */     try
/*     */     {
/*  64 */       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
/*     */     }
/*     */     catch (Throwable e) {
/*  67 */       e.printStackTrace();
/*     */     }
/*     */ 
/*  70 */     if (throwable != null)
/*     */     {
/*  72 */       String className = throwable.getClass().getName();
/*  73 */       className = className.substring(className.lastIndexOf('.') + 1);
/*     */ 
/*  76 */       msg = throwable.getMessage();
/*  77 */       msg = className + (msg != null ? ": " + msg : "");
/*     */     }
/*     */ 
/*  80 */     if (message != null) {
/*  81 */       msg = message;
/*     */     }
/*  83 */     final String basicMessage = msg;
/*     */ 
/*  87 */     final JLabel messageLabel = new JLabel(basicMessage);
/*     */ 
/*  92 */     JButton detailsButton = new JButton(JWLanguage.getString("SIMPLEERROR_DETAILS"));
/*     */ 
/* 100 */     ImageIcon icon = null;
/*     */     try
/*     */     {
/* 103 */       InputStream fin = SimpleErrorHandler.class.getResourceAsStream("stop.png");
/* 104 */       BufferedImage enabledImage = ImageIO.read(fin);
/* 105 */       icon = new ImageIcon(enabledImage);
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 109 */       icon = null;
/*     */     }
/*     */     JOptionPane pane;
/*     */     JOptionPane pane;
/* 113 */     if (throwable != null)
/* 114 */       pane = new JOptionPane(messageLabel, 0, 
/* 115 */         0, icon, 
/* 116 */         new Object[] { JWLanguage.getString("SIMPLEERROR_EXIT"), detailsButton });
/*     */     else {
/* 118 */       pane = new JOptionPane(messageLabel, 0, 
/* 119 */         0, icon, 
/* 120 */         new Object[] { JWLanguage.getString("SIMPLEERROR_EXIT") });
/*     */     }
/*     */ 
/* 124 */     final JDialog dialog = pane.createDialog(component, title);
/*     */ 
/* 127 */     detailsButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent event) {
/* 130 */         String label = SimpleErrorHandler.this.getText();
/* 131 */         if (label.startsWith(JWLanguage.getString("SIMPLEERROR_DETAILS")))
/*     */         {
/* 133 */           messageLabel.setText(SimpleErrorHandler.getHTMLDetails(message, throwable));
/* 134 */           SimpleErrorHandler.this.setText(JWLanguage.getString("SIMPLEERROR_HIDEDETAILS"));
/* 135 */           dialog.pack();
/* 136 */           dialog.setLocationRelativeTo(null);
/*     */         }
/*     */         else {
/* 139 */           messageLabel.setText(basicMessage);
/* 140 */           SimpleErrorHandler.this.setText(JWLanguage.getString("SIMPLEERROR_DETAILS"));
/* 141 */           dialog.pack();
/*     */         }
/*     */       }
/*     */     });
/* 148 */     dialog.setVisible(true);
/*     */   }
/*     */ 
/*     */   public static String getHTMLDetails(String message, Throwable throwable)
/*     */   {
/* 160 */     StringBuffer b = new StringBuffer("<html>");
/* 161 */     int lengthOfLastTrace = 1;
/* 162 */     if (message != null) {
/* 163 */       b.append(message).append("<BR><BR>");
/*     */     }
/*     */ 
/* 167 */     while (throwable != null)
/*     */     {
/* 169 */       b.append("<b>" + throwable.getClass().getName() + "</b>: " + 
/* 170 */         throwable.getMessage() + "<ul>");
/*     */ 
/* 174 */       StackTraceElement[] stack = throwable.getStackTrace();
/*     */ 
/* 176 */       for (int i = 0; i <= stack.length - lengthOfLastTrace; i++) {
/* 177 */         b.append("<li><span> in " + stack[i].getClassName() + ".<b>" + 
/* 178 */           stack[i].getMethodName() + "</b>( ) at <tt>" + 
/* 179 */           stack[i].getFileName() + ":" + 
/* 180 */           stack[i].getLineNumber() + "</tt></span></li>");
/*     */       }
/* 182 */       b.append("</ul>");
/*     */ 
/* 184 */       throwable = throwable.getCause();
/* 185 */       if (throwable != null)
/*     */       {
/* 187 */         b.append("<i>Caused by: </i>");
/*     */ 
/* 190 */         lengthOfLastTrace = stack.length;
/*     */       }
/*     */     }
/* 193 */     b.append("</html>");
/* 194 */     return b.toString();
/*     */   }
/*     */ 
/*     */   public static class Test
/*     */   {
/*     */     public void a()
/*     */     {
/* 201 */       b();
/*     */     }
/*     */ 
/*     */     public void b() {
/* 205 */       c();
/*     */     }
/*     */ 
/*     */     public void c()
/*     */     {
/* 210 */       d();
/*     */     }
/*     */ 
/*     */     public void d()
/*     */     {
/* 215 */       e();
/*     */     }
/*     */ 
/*     */     public void e()
/*     */     {
/* 220 */       f();
/*     */     }
/*     */ 
/*     */     public void f()
/*     */     {
/* 225 */       g();
/*     */     }
/*     */ 
/*     */     public void g()
/*     */     {
/* 230 */       h();
/*     */     }
/*     */ 
/*     */     public void h()
/*     */     {
/* 235 */       i();
/*     */     }
/*     */ 
/*     */     public void i()
/*     */     {
/* 240 */       j();
/*     */     }
/*     */ 
/*     */     public void j()
/*     */     {
/* 245 */       Integer.parseInt("aaa");
/*     */     }
/*     */ 
/*     */     public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
/* 249 */       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
/*     */ 
/* 251 */       SimpleErrorHandler.displayError("poo", "woot", null);
/*     */ 
/* 253 */       String url = args.length > 0 ? args[0] : null;
/*     */       try { foo();
/*     */       } catch (Throwable e) {
/* 256 */         SimpleErrorHandler.displayThrowable(e, JWLanguage.getString("UPDATE_ERROR_TITLE"), JWLanguage.getString("UPDATE_ERROR_MESSAGE"), null, null);
/* 257 */         System.exit(1);
/*     */       }
/*     */     }
/*     */ 
/* 261 */     public static void foo() { bar(null); } 
/*     */     public static void bar(Object o) {
/*     */       try { blah(o); }
/*     */       catch (NullPointerException e)
/*     */       {
/* 267 */         throw ((IllegalArgumentException)
/* 268 */           new IllegalArgumentException("null argument").initCause(e));
/*     */       }
/*     */     }
/*     */ 
/* 272 */     public static void blah(Object o) { Class c = o.getClass(); }
/*     */ 
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.ui.SimpleErrorHandler
 * JD-Core Version:    0.6.2
 */