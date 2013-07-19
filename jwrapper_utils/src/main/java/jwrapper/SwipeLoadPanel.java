/*     */ package jwrapper;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JProgressBar;
/*     */ import javax.swing.SwingUtilities;
/*     */ import jwrapper.ui.JWLanguage;
/*     */ import utils.swing.IconLoader;
/*     */ import utils.swing.components.SwipeImagePanel;
/*     */ import utils.swing.layout.GbPanel;
/*     */ import utils.swing.nolaf.NoLafButton;
/*     */ import utils.swing.nolaf.NoLafProgressBarUI;
/*     */ 
/*     */ public class SwipeLoadPanel extends JPanel
/*     */   implements WindowListener
/*     */ {
/*     */   JFrame frame;
/*  35 */   private static final Color BORDER_COLOR = new Color(200, 200, 200);
/*     */   SwipeImagePanel big;
/*  38 */   SwipeImagePanel small = new SwipeImagePanel();
/*     */ 
/*  40 */   JProgressBar fin = new JProgressBar();
/*  41 */   JProgressBar inf = new JProgressBar();
/*     */ 
/*  48 */   public static Image SmallQuerying = ImageUtil.load("Small-Querying.png");
/*  49 */   public static Image SmallDownload = ImageUtil.load("Small-Download.png");
/*  50 */   public static Image SmallNoInternet = ImageUtil.load("Small-NoInternet.png");
/*  51 */   public static Image SmallLaunching = ImageUtil.load("Small-Launching.png");
/*     */ 
/*  53 */   public static Image SmallUninstall = ImageUtil.load("UninstallSmall.png");
/*     */ 
/*  55 */   public static String Undo = "UndoSmall.png";
/*  56 */   public static String Uninstall = "UninstallSmall.png";
/*     */   JPanel parent;
/*     */   GbPanel buttons;
/*     */   NoLafButton uninstall;
/*     */   NoLafButton cancel;
/*     */   LPUninstallerListener listener;
/*     */   private String message;
/*     */   private JLabel messageLabel;
/*     */ 
/*     */   public void makeUninstaller(String title, Object logo, LPUninstallerListener listener)
/*     */   {
/*  83 */     makeFrame(title, logo);
/*     */ 
/*  85 */     this.frame.addWindowListener(this);
/*     */ 
/*  87 */     this.listener = listener;
/*     */ 
/*  89 */     this.buttons = new GbPanel();
/*  90 */     this.buttons.setBackground(Color.white);
/*     */ 
/*  92 */     IconLoader.CUSTOM_ICON_PATH = "/";
/*     */ 
/*  94 */     this.uninstall = new NoLafButton(IconLoader.load(Uninstall), IconLoader.loadDisabled(Uninstall));
/*  95 */     this.cancel = new NoLafButton(IconLoader.load(Undo), IconLoader.loadDisabled(Undo));
/*     */ 
/*  97 */     this.uninstall.setText(JWLanguage.getString("UNINSTALL"));
/*  98 */     this.cancel.setText(JWLanguage.getString("CANCEL"));
/*     */ 
/* 100 */     this.uninstall.setPreferredSize(new Dimension(this.uninstall.getPreferredSize().width, 52));
/* 101 */     this.cancel.setPreferredSize(new Dimension(this.uninstall.getPreferredSize().width, 52));
/*     */ 
/* 103 */     int buffer = 7;
/* 104 */     this.buttons.add(this.uninstall, 0, 0, 1, 1, 10, 10, 10, 2, new Insets(0, buffer * 2, buffer * 2, buffer));
/* 105 */     this.buttons.add(this.cancel, 1, 0, 1, 1, 10, 10, 10, 2, new Insets(0, buffer, buffer * 2, buffer * 2));
/*     */ 
/* 107 */     this.parent.add("South", this.buttons);
/*     */ 
/* 109 */     if (listener != null) {
/* 110 */       this.cancel.addActionListener(new UninstallListener());
/* 111 */       this.uninstall.addActionListener(new UninstallListener());
/*     */     }
/*     */ 
/* 114 */     hideProgress();
/*     */   }
/*     */ 
/*     */   public void disableButtons() {
/* 118 */     this.cancel.setEnabled(false);
/* 119 */     this.uninstall.setEnabled(false);
/* 120 */     this.cancel.repaint();
/* 121 */     this.uninstall.repaint();
/*     */   }
/*     */ 
/*     */   public void makeFrame(String title, Object logo) {
/* 125 */     System.out.println("[SwipeLoadPanel] Asked to make frame (current=" + this.frame + ")");
/* 126 */     if (this.frame != null) return;
/* 127 */     this.frame = new JFrame();
/* 128 */     this.frame.setTitle(title);
/* 129 */     if (logo != null) {
/* 130 */       this.frame.setIconImage((Image)logo);
/*     */     }
/* 132 */     this.frame.setBounds(0, 0, 400, 300);
/* 133 */     this.frame.setLocationRelativeTo(null);
/* 134 */     this.frame.setResizable(false);
/* 135 */     this.frame.setUndecorated(true);
/*     */ 
/* 137 */     this.parent = new JPanel();
/* 138 */     this.parent.setLayout(new BorderLayout());
/* 139 */     this.parent.add("Center", this);
/* 140 */     this.parent.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
/*     */ 
/* 142 */     this.frame.getContentPane().add(this.parent);
/*     */   }
/*     */ 
/*     */   public void showFrame()
/*     */   {
/* 148 */     System.out.println("[SwipeLoadPanel] Showing frame (" + this.frame + ")");
/* 149 */     if (this.frame != null)
/* 150 */       this.frame.setVisible(true);
/*     */   }
/*     */ 
/*     */   public void hideFrame()
/*     */   {
/* 155 */     SwingUtilities.invokeLater(new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 159 */         System.out.println("[SwipeLoadPanel] Hiding frame");
/* 160 */         if (SwipeLoadPanel.this.frame != null)
/* 161 */           SwipeLoadPanel.this.frame.setVisible(false);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public int[] getPosition() {
/* 167 */     Rectangle r = this.frame.getBounds();
/* 168 */     int[] tmp = new int[4];
/* 169 */     tmp[0] = r.x;
/* 170 */     tmp[1] = r.y;
/* 171 */     tmp[2] = r.width;
/* 172 */     tmp[3] = r.height;
/* 173 */     return tmp;
/*     */   }
/*     */ 
/*     */   public void setPosition(int[] tmp) {
/* 177 */     Rectangle r = new Rectangle(tmp[0], tmp[1], tmp[2], tmp[3]);
/* 178 */     this.frame.setBounds(r);
/*     */   }
/*     */ 
/*     */   public void setBigTo(Image img) {
/* 182 */     this.big.setImage(img);
/*     */   }
/*     */ 
/*     */   public void setSmallTo(Image img) {
/* 186 */     this.small.setImage(img);
/*     */   }
/*     */ 
/*     */   public void swipeAllTo(Image big, Image small) {
/* 190 */     new OffsetSwipe(big, small).start();
/*     */   }
/*     */ 
/*     */   public void swipeBigTo(Image img)
/*     */   {
/* 211 */     this.big.swipeTo(img);
/*     */   }
/*     */ 
/*     */   public void swipeSmallTo(Image img) {
/* 215 */     this.small.swipeTo(img);
/*     */   }
/*     */ 
/*     */   public void setProgress(double outOfOne) {
/* 219 */     this.fin.setValue((int)(outOfOne * 100000.0D));
/* 220 */     this.fin.repaint();
/*     */   }
/*     */ 
/*     */   public void waitForAllSwipes() {
/* 224 */     this.big.waitForAllSwipes();
/* 225 */     this.small.waitForAllSwipes();
/*     */   }
/*     */ 
/*     */   public SwipeLoadPanel() {
/* 229 */     setLayout(new BorderLayout());
/*     */ 
/* 236 */     this.big = new SwipeImagePanel();
/* 237 */     this.small = new SwipeImagePanel() {
/*     */       public Dimension getPreferredSize() {
/* 239 */         Dimension d = super.getPreferredSize();
/* 240 */         d.height = 55;
/* 241 */         return d;
/*     */       }
/*     */     };
/* 245 */     this.messageLabel = new JLabel(" ");
/* 246 */     this.messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
/*     */ 
/* 248 */     this.messageLabel.setHorizontalAlignment(0);
/*     */ 
/* 250 */     this.messageLabel.setForeground(new Color(100, 100, 100));
/*     */ 
/* 252 */     JPanel messagePanel = new JPanel();
/* 253 */     messagePanel.setBackground(Color.WHITE);
/* 254 */     messagePanel.setLayout(new BorderLayout());
/* 255 */     messagePanel.add("North", this.small);
/* 256 */     messagePanel.add("South", this.messageLabel);
/*     */ 
/* 258 */     JPanel swipes = new JPanel();
/* 259 */     swipes.setLayout(new BorderLayout());
/* 260 */     swipes.add("Center", this.big);
/* 261 */     swipes.add("South", messagePanel);
/*     */ 
/* 264 */     this.fin.setMaximum(100000);
/* 265 */     this.fin.putClientProperty("COLOR", NoLafProgressBarUI.COLOR_LIGHBLUE);
/* 266 */     this.fin.putClientProperty("IMAGE", "progress_piece" + NoLafProgressBarUI.COLOR_LIGHBLUE + ".png");
/*     */ 
/* 268 */     this.fin.setUI(new NoLafProgressBarUI());
/* 269 */     this.fin.setPreferredSize(new Dimension(500, 22));
/*     */ 
/* 271 */     this.inf.putClientProperty("COLOR", NoLafProgressBarUI.COLOR_LIGHBLUE);
/* 272 */     this.inf.putClientProperty("IMAGE", "progress_piece" + NoLafProgressBarUI.COLOR_LIGHBLUE + ".png");
/* 273 */     this.inf.setIndeterminate(true);
/* 274 */     this.inf.setUI(new NoLafProgressBarUI());
/* 275 */     this.inf.setPreferredSize(new Dimension(500, 22));
/*     */ 
/* 277 */     add("Center", swipes);
/* 278 */     add("South", this.fin);
/*     */   }
/*     */ 
/*     */   public void hideProgress()
/*     */   {
/* 284 */     remove(this.fin);
/* 285 */     remove(this.inf);
/* 286 */     revalidate();
/* 287 */     repaint();
/*     */   }
/*     */ 
/*     */   public void showInfiniteProgress() {
/* 291 */     remove(this.fin);
/* 292 */     add("South", this.inf);
/* 293 */     invalidate();
/* 294 */     revalidate();
/* 295 */     repaint();
/*     */   }
/*     */   public void showFiniteProgress() {
/* 298 */     remove(this.inf);
/* 299 */     add("South", this.fin);
/* 300 */     invalidate();
/* 301 */     revalidate();
/* 302 */     repaint();
/*     */   }
/*     */ 
/*     */   public void setMessage(String string)
/*     */   {
/* 325 */     this.messageLabel.setText(string);
/* 326 */     this.messageLabel.setVisible(string != null);
/* 327 */     repaint();
/*     */   }
/*     */ 
/*     */   public void preventWindowClose() {
/* 331 */     this.frame.setDefaultCloseOperation(0);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception {
/* 335 */     JFrame frame = new JFrame();
/* 336 */     frame.setResizable(false);
/* 337 */     frame.setUndecorated(true);
/*     */ 
/* 339 */     frame.setBounds(0, 0, 400, 300);
/*     */ 
/* 341 */     SwipeLoadPanel swp = new SwipeLoadPanel();
/*     */ 
/* 343 */     swp.setBigTo(ImageIO.read(new File("/Users/gchristelis/Desktop/applet_splash.png")));
/*     */ 
/* 346 */     swp.setSmallTo(null);
/*     */ 
/* 348 */     frame.getContentPane().add(swp);
/* 349 */     frame.setVisible(true);
/* 350 */     swp.showInfiniteProgress();
/*     */ 
/* 352 */     Thread.sleep(3000L);
/*     */ 
/* 354 */     swp.hideProgress();
/* 355 */     Thread.sleep(3000L);
/*     */ 
/* 357 */     swp.waitForAllSwipes();
/* 358 */     swp.showFiniteProgress();
/* 359 */     for (double i = 0.0D; i < 40.0D; i += 1.0D) {
/* 360 */       swp.setProgress(i / 40.0D);
/* 361 */       Thread.sleep(100L);
/*     */     }
/*     */ 
/* 364 */     swp.waitForAllSwipes();
/* 365 */     swp.showInfiniteProgress();
/* 366 */     for (double i = 0.0D; i < 90.0D; i += 1.0D)
/*     */     {
/* 368 */       Thread.sleep(300L);
/*     */     }
/*     */ 
/* 371 */     swp.swipeSmallTo(SmallLaunching);
/*     */   }
/*     */   public void windowActivated(WindowEvent arg0) {
/*     */   }
/*     */   public void windowClosed(WindowEvent arg0) {
/*     */   }
/*     */ 
/*     */   public void windowClosing(WindowEvent arg0) {
/* 379 */     System.exit(0);
/*     */   }
/*     */ 
/*     */   public void windowDeactivated(WindowEvent arg0)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void windowDeiconified(WindowEvent arg0)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void windowIconified(WindowEvent arg0)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void windowOpened(WindowEvent arg0)
/*     */   {
/*     */   }
/*     */ 
/*     */   class OffsetSwipe extends Thread
/*     */   {
/*     */     Image big;
/*     */     Image small;
/*     */ 
/*     */     OffsetSwipe(Image big, Image small)
/*     */     {
/* 197 */       this.big = big;
/* 198 */       this.small = small;
/*     */     }
/*     */     public void run() {
/* 201 */       SwipeLoadPanel.this.swipeBigTo(this.big);
/*     */       try {
/* 203 */         Thread.sleep(150L);
/*     */       } catch (Exception localException) {
/*     */       }
/* 206 */       SwipeLoadPanel.this.swipeSmallTo(this.small);
/*     */     }
/*     */   }
/*     */ 
/*     */   class UninstallListener
/*     */     implements ActionListener
/*     */   {
/*     */     UninstallListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/*  73 */       Object o = e.getSource();
/*  74 */       if (o == SwipeLoadPanel.this.uninstall)
/*  75 */         SwipeLoadPanel.this.listener.doUninstall();
/*  76 */       else if (o == SwipeLoadPanel.this.cancel)
/*  77 */         SwipeLoadPanel.this.listener.doExit();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.SwipeLoadPanel
 * JD-Core Version:    0.6.2
 */