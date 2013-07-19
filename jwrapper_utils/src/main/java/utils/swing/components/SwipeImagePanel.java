/*     */ package utils.swing.components;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import utils.swing.ExponentialSlider;
/*     */ 
/*     */ public class SwipeImagePanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*     */   protected Image oldimage;
/*     */   protected Image image;
/*     */   int width;
/*     */   int height;
/*     */   int oldwidth;
/*     */   int oldheight;
/*     */   int oldX;
/*     */   int newX;
/*     */   static Image[] swipes;
/*     */   static int index;
/*     */   Slide current;
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/*  33 */     index += 1;
/*  34 */     if (index == swipes.length) index = 0;
/*  35 */     swipeTo(swipes[index]);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception
/*     */   {
/*  40 */     index = 0;
/*     */ 
/*  42 */     JFrame frame = new JFrame();
/*     */ 
/*  44 */     SwipeImagePanel rp = new SwipeImagePanel();
/*     */ 
/*  46 */     swipes = new Image[] { 
/*  47 */       ImageIO.read(new File("Querying.png")), 
/*  48 */       ImageIO.read(new File("Download.png")), 
/*  49 */       ImageIO.read(new File("NoInternet.png")) };
/*     */ 
/*  52 */     rp.setImage(swipes[0]);
/*     */ 
/*  54 */     JPanel tmp = new JPanel();
/*  55 */     tmp.setLayout(new BorderLayout());
/*  56 */     tmp.add("Center", rp);
/*     */ 
/*  58 */     JButton b = new JButton("Swipe");
/*  59 */     tmp.add("South", b);
/*     */ 
/*  61 */     b.addActionListener(rp);
/*     */ 
/*  63 */     frame.getContentPane().add(tmp);
/*     */ 
/*  65 */     frame.setBounds(0, 0, 400, 400);
/*  66 */     frame.setVisible(true);
/*     */   }
/*     */ 
/*     */   public Image getImage()
/*     */   {
/*  73 */     return this.image;
/*     */   }
/*     */ 
/*     */   public void waitForAllSwipes()
/*     */   {
/*  78 */     Slide now = this.current;
/*  79 */     if (now != null)
/*     */       try {
/*  81 */         now.join();
/*     */       }
/*     */       catch (Throwable localThrowable)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   public void swipeTo(Image image) {
/*  89 */     Slide waitFor = this.current;
/*     */ 
/*  91 */     this.current = new Slide();
/*  92 */     this.current.newImage = image;
/*  93 */     this.current.waitFor = waitFor;
/*  94 */     this.current.start();
/*     */   }
/*     */ 
/*     */   public void setImage(Image image)
/*     */   {
/* 151 */     if (image != null) {
/* 152 */       this.width = image.getWidth(null);
/* 153 */       this.height = image.getHeight(null);
/*     */     }
/* 155 */     this.image = image;
/*     */   }
/*     */ 
/*     */   public void paintComponent(Graphics g1) {
/* 159 */     Graphics2D g = (Graphics2D)g1;
/*     */ 
/* 161 */     double panw = getWidth();
/* 162 */     double panh = getHeight();
/*     */ 
/* 165 */     g.setColor(Color.white);
/* 166 */     g.fillRect(0, 0, (int)panw, (int)panh);
/*     */ 
/* 168 */     g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
/*     */ 
/* 170 */     if (this.oldimage != null) {
/* 171 */       int xoff = (int)(panw - this.oldwidth) / 2;
/* 172 */       int yoff = (int)(panh - this.oldheight) / 2;
/*     */ 
/* 174 */       g.drawImage(this.oldimage, this.oldX + xoff, yoff, this.oldwidth, this.oldheight, null);
/*     */     }
/*     */ 
/* 177 */     if (this.image != null) {
/* 178 */       int xoff = ((int)panw - this.width) / 2;
/* 179 */       int yoff = ((int)panh - this.height) / 2;
/*     */ 
/* 181 */       g.drawImage(this.image, this.newX + xoff, yoff, this.width, this.height, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   class Slide extends Thread
/*     */   {
/*     */     Slide waitFor;
/*     */     Image newImage;
/*     */ 
/*     */     Slide()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 101 */       if (this.waitFor != null) {
/*     */         try {
/* 103 */           this.waitFor.join();
/*     */         } catch (Exception x) {
/* 105 */           x.printStackTrace();
/* 106 */           return;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 111 */       SwipeImagePanel.this.oldimage = SwipeImagePanel.this.image;
/* 112 */       SwipeImagePanel.this.oldwidth = SwipeImagePanel.this.width;
/* 113 */       SwipeImagePanel.this.oldheight = SwipeImagePanel.this.height;
/*     */ 
/* 115 */       SwipeImagePanel.this.image = this.newImage;
/* 116 */       if (this.newImage != null) {
/* 117 */         SwipeImagePanel.this.width = this.newImage.getWidth(null);
/* 118 */         SwipeImagePanel.this.height = this.newImage.getHeight(null);
/*     */       }
/*     */ 
/* 121 */       SwipeImagePanel.this.oldX = 0;
/* 122 */       SwipeImagePanel.this.newX = SwipeImagePanel.this.getWidth();
/*     */ 
/* 125 */       int W = SwipeImagePanel.this.getWidth();
/*     */ 
/* 127 */       ExponentialSlider epx = new ExponentialSlider(W, 500L, 3.5D, true);
/*     */ 
/* 129 */       for (int i = 0; i < epx.getPositionCount(); i++) {
/* 130 */         int offset = epx.getPositionForStepRounded(i);
/* 131 */         SwipeImagePanel.this.oldX = (0 - offset);
/* 132 */         SwipeImagePanel.this.newX = (W - offset);
/*     */ 
/* 134 */         SwingUtilities.invokeLater(new SwipeImagePanel.1(this));
/*     */         try
/*     */         {
/* 142 */           Thread.sleep(epx.get25fpsSleep());
/*     */         } catch (Exception localException1) {
/*     */         }
/*     */       }
/* 146 */       SwipeImagePanel.this.current = null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.components.SwipeImagePanel
 * JD-Core Version:    0.6.2
 */