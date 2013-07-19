/*     */ package jwrapper.ui;
/*     */ 
/*     */ import java.awt.Container;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.InputStream;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.UIManager;
/*     */ import utils.swing.SwingUtil;
/*     */ import utils.swing.components.JWrapLabel;
/*     */ import utils.swing.components.MessagePasswordTextField;
/*     */ import utils.swing.components.MessageTextField;
/*     */ import utils.swing.layout.GbPanel;
/*     */ 
/*     */ public class ProxyCredentialsDialog extends JDialog
/*     */   implements ActionListener, KeyListener
/*     */ {
/*  30 */   private JTextField proxyUsernameField = new MessageTextField(JWLanguage.getString("USERNAME"), null);
/*  31 */   private MessagePasswordTextField proxyPasswordField = new MessagePasswordTextField(JWLanguage.getString("PASSWORD"), null);
/*     */   private JButton okButton;
/*     */   private JButton cancelButton;
/*     */   private String proxyPassword;
/*     */   private String proxyUsername;
/*     */   private String appName;
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  40 */     new ProxyCredentialsDialog("Remote Support");
/*     */   }
/*     */ 
/*     */   public String getPassword()
/*     */   {
/*  45 */     return this.proxyPassword;
/*     */   }
/*     */ 
/*     */   public String getUsername()
/*     */   {
/*  50 */     return this.proxyUsername;
/*     */   }
/*     */ 
/*     */   public static ProxyCredentialsDialog showDialog(String name)
/*     */   {
/*     */     try {
/*  56 */       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
/*     */     }
/*     */     catch (Throwable e) {
/*  59 */       e.printStackTrace();
/*     */     }
/*     */ 
/*  62 */     return new ProxyCredentialsDialog(name);
/*     */   }
/*     */ 
/*     */   private ProxyCredentialsDialog(String appName)
/*     */   {
/*  67 */     super(null);
/*     */ 
/*  69 */     this.appName = appName;
/*     */ 
/*  71 */     setSize(420, 190);
/*  72 */     setModal(true);
/*     */ 
/*  74 */     this.proxyPasswordField.setMessageText(JWLanguage.getString("PASSWORD"));
/*     */ 
/*  76 */     GbPanel proxyPanel = new GbPanel();
/*  77 */     initGraphics(proxyPanel);
/*     */ 
/*  79 */     getContentPane().add(proxyPanel);
/*     */ 
/*  81 */     setTitle(JWLanguage.getString("PROXY_DIALOG_TITLE"));
/*     */ 
/*  83 */     setLocationRelativeTo(null);
/*  84 */     setResizable(false);
/*     */ 
/*  86 */     addKeyListener(this);
/*     */ 
/*  88 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   private void initGraphics(GbPanel proxyPanel)
/*     */   {
/*  93 */     int Y = 0;
/*     */ 
/*  95 */     InputStream fin = getClass().getResourceAsStream("padlock.png");
/*     */ 
/*  97 */     BufferedImage padlockImage = null;
/*     */     try
/*     */     {
/* 100 */       padlockImage = ImageIO.read(fin);
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 104 */       t.printStackTrace();
/*     */     }
/*     */ 
/* 107 */     ImageIcon padlockIcon = new ImageIcon(padlockImage);
/*     */ 
/* 109 */     JLabel desc = new JWrapLabel("<html>" + this.appName + " " + JWLanguage.getString("PROXY_DESCRIPTION") + "</html>");
/* 110 */     this.okButton = new JButton(JWLanguage.getString("AUTHENTICATE"));
/* 111 */     this.cancelButton = new JButton(JWLanguage.getString("CANCEL"));
/*     */ 
/* 113 */     this.okButton.addActionListener(this);
/* 114 */     this.cancelButton.addActionListener(this);
/*     */ 
/* 116 */     this.proxyUsernameField.addKeyListener(this);
/* 117 */     this.proxyPasswordField.addKeyListener(this);
/*     */ 
/* 119 */     this.okButton.setEnabled(false);
/*     */ 
/* 121 */     GbPanel buttonsPanel = new GbPanel();
/* 122 */     buttonsPanel.add(this.cancelButton, 0, 0, 1, 1, 100, 1, 13, 0);
/* 123 */     buttonsPanel.add(this.okButton, 1, 0, 1, 1, 0, 1, 13, 0, new Insets(0, 10, 0, 0));
/*     */ 
/* 125 */     SwingUtil.setWidth(this.proxyUsernameField, 250);
/* 126 */     SwingUtil.setWidth(this.proxyPasswordField, 250);
/*     */ 
/* 128 */     proxyPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
/* 129 */     proxyPanel.add(desc, 0, Y++, 2, 1, 0, 1, 10, 2, new Insets(5, 10, 0, 10));
/* 130 */     proxyPanel.add(new JLabel(padlockIcon), 0, Y, 1, 2, 0, 1, 10, 2, new Insets(10, 10, 10, 10));
/* 131 */     proxyPanel.add(this.proxyUsernameField, 1, Y++, 1, 1, 1, 1, 17, 0, new Insets(10, 10, 10, 0));
/* 132 */     proxyPanel.add(this.proxyPasswordField, 1, Y++, 1, 1, 1, 1, 17, 0, new Insets(0, 10, 10, 0));
/* 133 */     proxyPanel.add(buttonsPanel, 0, Y++, 2, 1, 1, 0, 10, 2, new Insets(0, 0, 0, 0));
/* 134 */     proxyPanel.add(new JPanel(), 0, Y++, 1, 1, 0, 100, 10, 2, new Insets(0, 0, 0, 0));
/*     */   }
/*     */ 
/*     */   private void updateEnabled()
/*     */   {
/* 139 */     if (this.proxyUsernameField.getText().length() > 0)
/* 140 */       this.okButton.setEnabled(true);
/*     */     else
/* 142 */       this.okButton.setEnabled(false);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 147 */     processAction(e.getSource());
/*     */   }
/*     */ 
/*     */   private void processAction(Object source)
/*     */   {
/* 152 */     if (source == this.okButton)
/*     */     {
/* 154 */       this.proxyUsername = this.proxyUsernameField.getText();
/* 155 */       this.proxyPassword = this.proxyPasswordField.getText();
/*     */     }
/*     */     else {
/* 158 */       this.proxyUsername = null;
/*     */     }
/* 160 */     setVisible(false);
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void keyReleased(KeyEvent e)
/*     */   {
/* 169 */     updateEnabled();
/* 170 */     if (e.getKeyCode() == 10)
/* 171 */       processAction(this.okButton);
/* 172 */     else if (e.getKeyCode() == 27)
/* 173 */       setVisible(false);
/*     */   }
/*     */ 
/*     */   public void keyTyped(KeyEvent e)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.ui.ProxyCredentialsDialog
 * JD-Core Version:    0.6.2
 */