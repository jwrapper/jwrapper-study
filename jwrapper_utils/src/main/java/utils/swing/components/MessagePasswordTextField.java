/*     */ package utils.swing.components;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import javax.swing.JPasswordField;
/*     */ import javax.swing.LookAndFeel;
/*     */ import javax.swing.UIDefaults;
/*     */ import javax.swing.UIManager;
/*     */ import utils.ostools.OS;
/*     */ 
/*     */ public class MessagePasswordTextField extends JPasswordField
/*     */   implements KeyListener, FocusListener
/*     */ {
/*     */   private Color defaultColor;
/*     */   private Color grayColor;
/*     */   private String searchText;
/*     */   private FilterListener filterListener;
/*     */   private char echoChar;
/*     */ 
/*     */   public MessagePasswordTextField(String message, FilterListener filterListener)
/*     */   {
/*  23 */     this.echoChar = super.getEchoChar();
/*  24 */     if (OS.isMacOS()) {
/*  25 */       this.echoChar = '•';
/*     */     }
/*  27 */     setFont(UIManager.getLookAndFeel().getDefaults().getFont("TextField.font"));
/*     */ 
/*  29 */     this.filterListener = filterListener;
/*  30 */     this.searchText = message;
/*  31 */     this.defaultColor = getForeground();
/*  32 */     this.grayColor = Color.GRAY;
/*     */ 
/*  34 */     setText(this.searchText);
/*  35 */     setForeground(this.grayColor);
/*  36 */     setBackground(UIManager.getLookAndFeel().getDefaults().getColor("TextField.background"));
/*     */ 
/*  38 */     addKeyListener(this);
/*  39 */     addFocusListener(this);
/*     */   }
/*     */   public void keyPressed(KeyEvent e) {
/*     */   }
/*     */   public void keyTyped(KeyEvent e) {
/*     */   }
/*     */   public void keyReleased(KeyEvent e) {
/*  46 */     if (e.getSource().equals(this))
/*     */     {
/*  48 */       String filter = null;
/*  49 */       if (getText().length() > 0)
/*  50 */         filter = getText();
/*  51 */       if (this.filterListener != null)
/*  52 */         this.filterListener.setFilter(filter);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  58 */     return (getText().length() == 0) || (getText().equals(this.searchText));
/*     */   }
/*     */ 
/*     */   public void setMessageText(String message)
/*     */   {
/*  63 */     this.searchText = message;
/*     */ 
/*  65 */     if (getForeground().equals(this.grayColor))
/*     */     {
/*  67 */       setEchoChar('\000');
/*  68 */       setText(this.searchText);
/*     */     }
/*     */     else
/*     */     {
/*  72 */       setEchoChar(this.echoChar);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void programmaticallyEnterText(String text)
/*     */   {
/*  78 */     setText(text);
/*  79 */     setForeground(this.defaultColor);
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent e)
/*     */   {
/*  84 */     if (!hasEnteredText())
/*     */     {
/*  86 */       setEchoChar(this.echoChar);
/*  87 */       setText("");
/*     */     }
/*  89 */     setForeground(this.defaultColor);
/*     */   }
/*     */ 
/*     */   public void focusLost(FocusEvent e)
/*     */   {
/*  94 */     if (getText().length() == 0)
/*     */     {
/*  96 */       setText(this.searchText);
/*  97 */       setForeground(this.grayColor);
/*  98 */       setEchoChar('\000');
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean hasEnteredText()
/*     */   {
/* 109 */     return getForeground().equals(this.defaultColor);
/*     */   }
/*     */ 
/*     */   public String getText()
/*     */   {
/* 114 */     if (hasEnteredText())
/* 115 */       return super.getText();
/* 116 */     return "";
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 121 */     if (this.filterListener != null)
/* 122 */       this.filterListener.setFilter(null);
/* 123 */     setText(this.searchText);
/* 124 */     setForeground(this.grayColor);
/*     */   }
/*     */ 
/*     */   public static abstract interface FilterListener
/*     */   {
/*     */     public abstract void setFilter(String paramString);
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.components.MessagePasswordTextField
 * JD-Core Version:    0.6.2
 */