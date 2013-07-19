/*     */ package jwrapper.ui;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Container;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Image;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.KeyAdapter;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.FilteredImageSource;
/*     */ import java.awt.image.RGBImageFilter;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import jwrapper.jwutils.JWSystemUI;
/*     */ 
/*     */ public class JWLanguageChooserFrame
/*     */ {
/*     */   private JDialog dialog;
/*     */   private Translation[] translatedLanguageStrings;
/*     */   private ImageIcon logo;
/*     */   private static final int outerPadding = 30;
/*     */   private static final int innerPadding = 5;
/*  44 */   private static LightGrayFilter filter = new LightGrayFilter(true, 40);
/*  45 */   private int returnIndex = -1;
/*     */ 
/*     */   public JWLanguageChooserFrame(ImageIcon logo, String[] supported) throws IOException
/*     */   {
/*  49 */     this.logo = logo;
/*     */ 
/*  51 */     if (supported == null) {
/*  52 */       supported = new String[] { "en", "de", "es", "fr", "it", "nl", "pt", "sv" };
/*     */     }
/*  54 */     loadTranslations(supported);
/*  55 */     initFrame();
/*     */   }
/*     */ 
/*     */   public String getSelectedLanguageCountryCode()
/*     */   {
/*  71 */     if (this.returnIndex == -1)
/*  72 */       return null;
/*  73 */     return this.translatedLanguageStrings[this.returnIndex].countryCode;
/*     */   }
/*     */ 
/*     */   private void loadTranslations(String[] supported) throws IOException
/*     */   {
/*  78 */     ArrayList translations = new ArrayList();
/*  79 */     for (int i = 0; i < supported.length; i++)
/*     */     {
/*  81 */       String key = ("LANGUAGE_" + supported[i]).toUpperCase();
/*     */ 
/*  83 */       if (JWLanguage.containsTranslationFor(key))
/*     */       {
/*  87 */         Translation t = new Translation();
/*  88 */         t.countryCode = supported[i];
/*  89 */         t.translation = JWLanguage.getString(key);
/*  90 */         translations.add(t);
/*     */       }
/*     */     }
/*  93 */     this.translatedLanguageStrings = new Translation[translations.size()];
/*  94 */     translations.toArray(this.translatedLanguageStrings);
/*     */   }
/*     */ 
/*     */   private void closeFrame()
/*     */   {
/*  99 */     this.dialog.setVisible(false);
/*     */   }
/*     */ 
/*     */   private void initFrame() throws IOException
/*     */   {
/* 104 */     int frameWidth = 500;
/* 105 */     int frameHeight = 550;
/*     */ 
/* 107 */     this.dialog = new JDialog(null, true);
/*     */ 
/* 109 */     this.dialog.setDefaultCloseOperation(2);
/* 110 */     this.dialog.setUndecorated(true);
/* 111 */     this.dialog.setSize(frameWidth, frameHeight);
/* 112 */     this.dialog.setLocationRelativeTo(null);
/*     */ 
/* 114 */     JPanel contentPanel = new CanvasPanel();
/* 115 */     contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
/* 116 */     contentPanel.setLayout(new BorderLayout(0, 0));
/*     */ 
/* 118 */     contentPanel.add("North", new JLabel(this.logo));
/*     */ 
/* 120 */     JPanel languagePanel = new JPanel();
/* 121 */     languagePanel.setOpaque(false);
/* 122 */     languagePanel.setLayout(new GridLayout(1, 1));
/*     */ 
/* 124 */     final JList languageList = new JList(this.translatedLanguageStrings);
/* 125 */     languageList.addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mouseClicked(MouseEvent e) {
/* 128 */         if (e.getClickCount() == 2)
/*     */         {
/* 130 */           JWLanguageChooserFrame.this.returnIndex = languageList.getSelectedIndex();
/* 131 */           JWLanguageChooserFrame.this.closeFrame();
/*     */         }
/*     */       }
/*     */     });
/* 135 */     languageList.validate();
/* 136 */     JScrollPane pane = new JScrollPane(languageList);
/*     */ 
/* 138 */     languagePanel.add(pane);
/*     */ 
/* 140 */     int requiredWidth = languageList.getPreferredSize().width;
/* 141 */     int padding = (frameWidth - requiredWidth - 60) / 2 - 20;
/* 142 */     languagePanel.setBorder(BorderFactory.createEmptyBorder(30, padding, 30, padding));
/*     */ 
/* 144 */     contentPanel.add("Center", languagePanel);
/*     */ 
/* 148 */     JPanel panel = new JPanel(new FlowLayout(1));
/* 149 */     panel.setOpaque(false);
/* 150 */     InputStream fin = getClass().getResourceAsStream("next.png");
/*     */ 
/* 152 */     BufferedImage enabledImage = ImageIO.read(fin);
/*     */ 
/* 154 */     ImageIcon enabledIcon = new ImageIcon(enabledImage);
/* 155 */     ImageIcon disabledIcon = getDisabledVersion(enabledIcon);
/* 156 */     final JLabel button = new JLabel(enabledIcon);
/* 157 */     button.setDisabledIcon(disabledIcon);
/*     */ 
/* 159 */     button.setBorder(null);
/* 160 */     button.setSize(100, 60);
/* 161 */     button.setMaximumSize(button.getSize());
/* 162 */     button.setEnabled(false);
/*     */ 
/* 164 */     button.addMouseListener(new MouseListener() {
/*     */       public void mouseClicked(MouseEvent e) {
/*     */       }
/*     */       public void mousePressed(MouseEvent e) {  } 
/* 168 */       public void mouseReleased(MouseEvent e) { if (button.isEnabled())
/* 169 */           JWLanguageChooserFrame.this.closeFrame(); }
/*     */ 
/*     */       public void mouseEntered(MouseEvent e) {
/* 172 */         if (button.isEnabled())
/* 173 */           button.setCursor(Cursor.getPredefinedCursor(12)); 
/*     */       }
/*     */ 
/* 176 */       public void mouseExited(MouseEvent e) { if (button.isEnabled())
/* 177 */           button.setCursor(Cursor.getDefaultCursor());
/*     */       }
/*     */     });
/* 181 */     panel.add(button);
/* 182 */     contentPanel.add("South", panel);
/*     */ 
/* 184 */     this.dialog.getContentPane().add(contentPanel);
/*     */ 
/* 186 */     languageList.addListSelectionListener(new ListSelectionListener() {
/*     */       public void valueChanged(ListSelectionEvent e) {
/* 188 */         JWLanguageChooserFrame.this.returnIndex = languageList.getSelectedIndex();
/* 189 */         if (languageList.getSelectedIndex() >= 0)
/* 190 */           button.setEnabled(true);
/*     */         else
/* 192 */           button.setEnabled(false);
/* 193 */         button.repaint();
/*     */       }
/*     */     });
/* 197 */     CloseKeyListener closeKeyListener = new CloseKeyListener();
/* 198 */     this.dialog.addKeyListener(closeKeyListener);
/* 199 */     contentPanel.addKeyListener(closeKeyListener);
/* 200 */     languageList.addKeyListener(closeKeyListener);
/*     */ 
/* 202 */     this.dialog.setVisible(true);
/*     */   }
/*     */ 
/*     */   private static ImageIcon getDisabledVersion(ImageIcon icon)
/*     */   {
/* 207 */     Image image = icon.getImage();
/* 208 */     Image grayImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), filter));
/* 209 */     icon = new ImageIcon(grayImage);
/* 210 */     return icon;
/*     */   }
/*     */ 
/*     */   public static String chooseLanguage(String[] supported)
/*     */     throws IOException
/*     */   {
/* 259 */     ImageIcon icon = null;
/*     */     try {
/* 261 */       Image img = ImageIO.read(new ByteArrayInputStream(JWSystemUI.getAppBundleLogoPNG()));
/* 262 */       icon = new ImageIcon(img);
/*     */     } catch (Exception localException) {
/*     */     }
/* 265 */     JWLanguageChooserFrame jwLanguageChooserFrame = new JWLanguageChooserFrame(icon, supported);
/* 266 */     return jwLanguageChooserFrame.getSelectedLanguageCountryCode();
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws IOException
/*     */   {
/* 271 */     Image img = ImageIO.read(new File("../PaidMedia/SimpleHelp/Logos/New/logo/green/sh_256.png"));
/* 272 */     ImageIcon icon = new ImageIcon(img);
/*     */ 
/* 274 */     JWLanguageChooserFrame jwLanguageChooserFrame = new JWLanguageChooserFrame(icon, null);
/* 275 */     System.out.println("RESULT = " + jwLanguageChooserFrame.getSelectedLanguageCountryCode());
/*     */   }
/*     */ 
/*     */   class CloseKeyListener extends KeyAdapter
/*     */   {
/*     */     CloseKeyListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void keyReleased(KeyEvent e)
/*     */     {
/* 217 */       if (e.getKeyCode() == 27)
/*     */       {
/* 219 */         JWLanguageChooserFrame.this.returnIndex = -1;
/* 220 */         JWLanguageChooserFrame.this.closeFrame();
/*     */       }
/* 222 */       else if (e.getKeyCode() == 10) {
/* 223 */         JWLanguageChooserFrame.this.closeFrame();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class LightGrayFilter extends RGBImageFilter {
/*     */     private boolean brighter;
/*     */     private int percent;
/*     */ 
/*     */     public LightGrayFilter(boolean b, int p) {
/* 234 */       this.brighter = b;
/* 235 */       this.percent = p;
/* 236 */       this.canFilterIndexColorModel = true;
/*     */     }
/*     */ 
/*     */     public int filterRGB(int x, int y, int rgb)
/*     */     {
/* 243 */       int gray = (int)((0.9D * (rgb >> 16 & 0xFF) + 0.9D * (rgb >> 8 & 0xFF) + 0.8100000000000001D * (rgb & 0xFF)) / 3.0D);
/*     */ 
/* 245 */       if (this.brighter)
/* 246 */         gray = 255 - (255 - gray) * (100 - this.percent) / 100;
/*     */       else {
/* 248 */         gray = gray * (100 - this.percent) / 100;
/*     */       }
/* 250 */       if (gray < 0)
/* 251 */         gray = 0;
/* 252 */       if (gray > 255)
/* 253 */         gray = 255;
/* 254 */       return rgb & 0xFF000000 | gray << 16 | gray << 8 | gray << 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   class Translation
/*     */   {
/*     */     String countryCode;
/*     */     String translation;
/*     */ 
/*     */     Translation()
/*     */     {
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  65 */       return this.translation;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.ui.JWLanguageChooserFrame
 * JD-Core Version:    0.6.2
 */