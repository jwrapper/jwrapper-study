/*     */ package utils.swing.nolaf;
/*     */ 
/*     */ import java.awt.Container;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JProgressBar;
/*     */ import javax.swing.Timer;
/*     */ import javax.swing.plaf.ProgressBarUI;
/*     */ 
/*     */ public class NoLafProgressBarUI extends ProgressBarUI
/*     */   implements ActionListener
/*     */ {
/*     */   public static final String KEY_COLOR = "COLOR";
/*  25 */   public static final Integer COLOR_GREEN = new Integer(2);
/*  26 */   public static final Integer COLOR_DARKBLUE = new Integer(3);
/*  27 */   public static final Integer COLOR_LIGHBLUE = new Integer(4);
/*  28 */   public static final Integer COLOR_GREY = new Integer(5);
/*     */   private BufferedImage progressBarImage;
/*     */   private BufferedImage trackImage;
/*     */   private Timer timer;
/*     */   private int maxFrames;
/*  34 */   private int currentFrame = 0;
/*     */   private JProgressBar progressBar;
/*     */ 
/*     */   public void installUI(JComponent c)
/*     */   {
/*  39 */     this.progressBar = ((JProgressBar)c);
/*     */ 
/*  41 */     int imageIndex = 2;
/*     */ 
/*  43 */     Integer color = (Integer)c.getClientProperty("COLOR");
/*  44 */     if (color != null) {
/*  45 */       imageIndex = color.intValue();
/*     */     }
/*     */     try
/*     */     {
/*  49 */       if (this.progressBar.isIndeterminate())
/*  50 */         this.progressBarImage = ImageIO.read(NoLafProgressBarUI.class.getResourceAsStream("/utils/swing/nolaf/progress_piece" + imageIndex + ".png"));
/*     */       else
/*  52 */         this.progressBarImage = ImageIO.read(NoLafProgressBarUI.class.getResourceAsStream("/utils/swing/nolaf/progress" + imageIndex + ".png"));
/*  53 */       this.trackImage = ImageIO.read(NoLafProgressBarUI.class.getResourceAsStream("/utils/swing/nolaf/progress_track.png"));
/*     */     } catch (IOException e) {
/*  55 */       e.printStackTrace();
/*     */     }
/*     */ 
/*  58 */     if ((this.progressBarImage != null) && (this.trackImage != null))
/*     */     {
/*  60 */       this.maxFrames = this.progressBarImage.getWidth();
/*     */ 
/*  62 */       this.timer = new Timer(30, this);
/*  63 */       this.timer.setInitialDelay(0);
/*  64 */       this.timer.start();
/*     */     }
/*     */     else {
/*  67 */       System.out.println("[NoLafProgressBarUI] ERROR - No progress bar Image loaded! (" + (this.progressBarImage != null) + ")(" + (this.trackImage != null) + ")");
/*     */     }
/*     */   }
/*     */ 
/*  71 */   public void uninstallUI(JComponent c) { this.progressBar = null; }
/*     */ 
/*     */ 
/*     */   public void paint(Graphics g, JComponent c)
/*     */   {
/*  76 */     if (!(g instanceof Graphics2D)) {
/*  77 */       return;
/*     */     }
/*  79 */     Graphics2D g2d = (Graphics2D)g;
/*     */ 
/*  81 */     JProgressBar bar = (JProgressBar)c;
/*     */ 
/*  83 */     if (bar.isIndeterminate())
/*     */     {
/*  85 */       if (this.progressBarImage != null)
/*     */       {
/*  87 */         int index = this.currentFrame - this.maxFrames;
/*  88 */         while (index < c.getWidth())
/*     */         {
/*  90 */           g2d.drawImage(this.progressBarImage, index, 0, null);
/*  91 */           index += this.progressBarImage.getWidth();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*  97 */     else if ((this.progressBarImage != null) && (this.trackImage != null))
/*     */     {
/*  99 */       int value = bar.getValue();
/* 100 */       int max = bar.getMaximum();
/* 101 */       int min = bar.getMinimum();
/* 102 */       int width = bar.getWidth();
/* 103 */       int paintToPoint = width * value / (max - min);
/*     */ 
/* 105 */       int startX = paintToPoint;
/* 106 */       while (startX < width - this.trackImage.getWidth())
/*     */       {
/* 108 */         g2d.drawImage(this.trackImage, startX, 0, null);
/* 109 */         startX += this.trackImage.getWidth() - 1;
/*     */       }
/* 111 */       g2d.drawImage(this.trackImage, width - this.trackImage.getWidth(), 0, null);
/*     */ 
/* 113 */       if (paintToPoint == 0) {
/* 114 */         return;
/*     */       }
/* 116 */       startX = 0 - this.progressBarImage.getWidth();
/* 117 */       while (startX < paintToPoint - this.progressBarImage.getWidth())
/*     */       {
/* 119 */         g2d.drawImage(this.progressBarImage, startX, 0, null);
/* 120 */         startX += this.progressBarImage.getWidth() - 1;
/*     */       }
/* 122 */       g2d.drawImage(this.progressBarImage, paintToPoint - this.progressBarImage.getWidth(), 0, null);
/*     */     }
/*     */ 
/* 126 */     Toolkit.getDefaultToolkit().sync();
/*     */ 
/* 128 */     g2d.dispose();
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws IOException
/*     */   {
/* 133 */     JFrame frame = new JFrame();
/* 134 */     frame.setSize(300, 300);
/* 135 */     frame.setDefaultCloseOperation(3);
/*     */ 
/* 137 */     JPanel panel = new JPanel(new GridLayout(9, 1));
/* 138 */     panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
/*     */ 
/* 140 */     ArrayList bars = new ArrayList();
/*     */ 
/* 142 */     for (int i = 2; i < 6; i++)
/*     */     {
/* 144 */       JProgressBar bar1 = new JProgressBar();
/* 145 */       bar1.putClientProperty("COLOR", new Integer(i));
/* 146 */       bar1.putClientProperty("IMAGE", "progress_piece" + i + ".png");
/* 147 */       bar1.setIndeterminate(true);
/* 148 */       bar1.setUI(new NoLafProgressBarUI());
/* 149 */       panel.add(bar1);
/*     */     }
/*     */ 
/* 152 */     for (int i = 2; i < 6; i++)
/*     */     {
/* 154 */       JProgressBar bar1 = new JProgressBar();
/* 155 */       bar1.putClientProperty("COLOR", new Integer(i));
/* 156 */       bar1.setValue(i * 20);
/* 157 */       bar1.putClientProperty("IMAGE", "progress" + i + ".png");
/* 158 */       bar1.setUI(new NoLafProgressBarUI());
/* 159 */       panel.add(bar1);
/* 160 */       bars.add(bar1);
/*     */     }
/*     */ 
/* 164 */     frame.getContentPane().add(panel);
/*     */ 
/* 166 */     frame.setVisible(true);
/*     */ 
/* 168 */     new Thread()
/*     */     {
/*     */       public void run()
/*     */       {
/*     */         while (true) {
/*     */           try {
/* 174 */             Thread.sleep(30L);
/*     */           }
/*     */           catch (InterruptedException e) {
/* 177 */             e.printStackTrace();
/*     */           }
/* 179 */           for (int i = 0; i < NoLafProgressBarUI.this.size(); i++)
/*     */           {
/* 181 */             JProgressBar bar = (JProgressBar)NoLafProgressBarUI.this.get(i);
/* 182 */             int val = bar.getValue() + 1;
/* 183 */             if (val > bar.getMaximum())
/* 184 */               val = bar.getMinimum();
/* 185 */             bar.setValue(val);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 189 */     .start();
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 194 */     this.currentFrame += 1;
/* 195 */     if (this.currentFrame > this.maxFrames)
/* 196 */       this.currentFrame = 0;
/* 197 */     if (this.progressBar != null)
/* 198 */       this.progressBar.repaint();
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.nolaf.NoLafProgressBarUI
 * JD-Core Version:    0.6.2
 */