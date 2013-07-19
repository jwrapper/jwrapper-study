/*     */ package utils.swing;
/*     */ 
/*     */ import java.awt.Container;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import utils.swing.listeners.SystemExitWindowListener;
/*     */ 
/*     */ public class ExponentialSlider
/*     */ {
/*     */   double distance;
/*     */   double[] positions;
/*     */ 
/*     */   public ExponentialSlider(double distance, boolean opening)
/*     */   {
/*  20 */     this(distance, 400L, 4.0D, opening);
/*     */   }
/*     */   public ExponentialSlider(double distance, long msecAt25fps, double acceleration, boolean startFast) {
/*  23 */     this(distance, (int)(msecAt25fps / 25L), acceleration, startFast);
/*     */   }
/*     */   public ExponentialSlider(double distance, int steps, double acceleration, boolean startFast) {
/*  26 */     this.distance = distance;
/*     */ 
/*  28 */     double factor = distance / Math.pow(steps, 1.0D + acceleration);
/*     */ 
/*  30 */     this.positions = new double[steps];
/*  31 */     for (int i = 1; i <= steps; i++) {
/*  32 */       this.positions[(i - 1)] = (factor * Math.pow(i, 1.0D + acceleration));
/*  33 */       if (i == steps - 1)
/*     */       {
/*  35 */         this.positions[(i - 1)] = distance;
/*     */       }
/*  37 */       if (startFast) {
/*  38 */         this.positions[(i - 1)] = (distance - this.positions[(i - 1)]);
/*     */       }
/*     */     }
/*  41 */     if (startFast) {
/*  42 */       double[] tmp = new double[this.positions.length];
/*  43 */       for (int i = 0; i < tmp.length; i++) {
/*  44 */         tmp[i] = this.positions[(tmp.length - 1 - i)];
/*     */       }
/*  46 */       this.positions = tmp;
/*     */     }
/*     */   }
/*     */ 
/*     */   public double getPositionForStep(int step) {
/*  51 */     return this.positions[step];
/*     */   }
/*     */ 
/*     */   public int getPositionForStepRounded(int step) {
/*  55 */     return (int)Math.round(this.positions[step]);
/*     */   }
/*     */ 
/*     */   public int getPositionCount() {
/*  59 */     return this.positions.length;
/*     */   }
/*     */ 
/*     */   public long get25fpsSleep() {
/*  63 */     return 30L;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception {
/*  67 */     int N = 400;
/*     */ 
/*  69 */     JButton button = new JButton("GO");
/*     */ 
/*  71 */     JPanel both = new JPanel();
/*  72 */     both.setLayout(new GridLayout(3, 1));
/*     */ 
/*  74 */     int[] pows = { 
/*  75 */       1, 
/*  76 */       5 };
/*     */ 
/*  79 */     for (int A = 0; A < pows.length; A++) {
/*  80 */       ExponentialSlider localExponentialSlider = new ExponentialSlider(50.0D, N, pows[A], true);
/*     */     }
/*     */ 
/*  83 */     both.add(button);
/*     */ 
/*  85 */     JFrame frame = new JFrame();
/*  86 */     frame.addWindowListener(new SystemExitWindowListener());
/*  87 */     frame.setBounds(200, 200, 450, 400);
/*  88 */     frame.getContentPane().add(both);
/*  89 */     frame.setVisible(true);
/*     */ 
/*  91 */     button.addActionListener(new ExampleSlide(20, 100));
/*  92 */     button.addActionListener(new ExampleSlide(220, 200));
/*  93 */     button.addActionListener(new ExampleSlide(420, 300));
/*  94 */     button.addActionListener(new ExampleSlide(620, 35));
/*     */   }
/*     */   static class ExampleSlide extends JFrame implements ActionListener, Runnable {
/*     */     int X;
/*     */     int height;
/*     */     JPanel tmp;
/* 103 */     boolean out = true;
/*     */     Thread prev;
/*     */     ExponentialSlider ex;
/*     */ 
/* 109 */     public ExampleSlide(int X, int H) { this.height = H;
/* 110 */       this.X = X;
/*     */ 
/* 112 */       this.ex = new ExponentialSlider(this.height, 250L, 2.0D, true);
/*     */ 
/* 115 */       setBounds(X, 0, 190, 50);
/* 116 */       this.tmp = new JPanel();
/* 117 */       this.tmp.setLayout(new GridLayout(1, 1));
/* 118 */       getContentPane().add(this.tmp);
/* 119 */       setVisible(true); }
/*     */ 
/*     */     public void run() {
/* 122 */       if (!this.out)
/*     */       {
/* 124 */         SwingUtilities.invokeLater(new ExponentialSlider.1(this));
/*     */       }
/*     */ 
/* 135 */       if (this.out)
/* 136 */         for (int i = 0; i < this.ex.getPositionCount(); i++) {
/* 137 */           setBoundsAndRepaint(this.X, 0, 190, 50 + (int)this.ex.getPositionForStep(i));
/*     */           try
/*     */           {
/* 140 */             Thread.sleep(this.ex.get25fpsSleep());
/*     */           }
/*     */           catch (Exception localException) {
/*     */           }
/*     */         }
/* 145 */       else for (int i = this.ex.getPositionCount() - 1; i >= 0; i--) {
/* 146 */           setBoundsAndRepaint(this.X, 0, 190, 50 + (int)this.ex.getPositionForStep(i));
/*     */           try
/*     */           {
/* 149 */             Thread.sleep(this.ex.get25fpsSleep());
/*     */           }
/*     */           catch (Exception localException1)
/*     */           {
/*     */           }
/*     */         } if (this.out)
/*     */       {
/* 156 */         SwingUtilities.invokeLater(new ExponentialSlider.2(this));
/*     */       }
/*     */ 
/* 166 */       this.out = (!this.out);
/*     */     }
/*     */ 
/*     */     private void setBoundsAndRepaint(int x2, int i, int j, int k) {
/* 170 */       SwingUtilities.invokeLater(new ExponentialSlider.3(this, x2, i, j, k));
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/* 180 */       if (this.prev != null)
/*     */         try {
/* 182 */           this.prev.join();
/*     */         }
/*     */         catch (InterruptedException localInterruptedException) {
/*     */         }
/* 186 */       this.prev = new Thread(this);
/* 187 */       this.prev.start();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.ExponentialSlider
 * JD-Core Version:    0.6.2
 */