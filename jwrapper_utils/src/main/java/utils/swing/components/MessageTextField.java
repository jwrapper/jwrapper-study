/*     */ package utils.swing.components;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import javax.swing.JTextField;
/*     */ 
/*     */ public class MessageTextField extends JTextField
/*     */   implements KeyListener, FocusListener
/*     */ {
/*     */   private Color defaultColor;
/*     */   private Color grayColor;
/*     */   private String searchText;
/*     */   private FilterListener filterListener;
/*     */ 
/*     */   public MessageTextField(String message, FilterListener filterListener)
/*     */   {
/*  19 */     this.filterListener = filterListener;
/*  20 */     this.searchText = message;
/*  21 */     this.defaultColor = getForeground();
/*  22 */     this.grayColor = Color.GRAY;
/*     */ 
/*  24 */     setText(this.searchText);
/*  25 */     setForeground(this.grayColor);
/*     */ 
/*  27 */     addKeyListener(this);
/*  28 */     addFocusListener(this);
/*     */   }
/*     */   public void keyPressed(KeyEvent e) {
/*     */   }
/*     */   public void keyTyped(KeyEvent e) {
/*     */   }
/*     */   public void keyReleased(KeyEvent e) {
/*  35 */     if (e.getSource().equals(this))
/*     */     {
/*  37 */       String filter = null;
/*  38 */       if (getText().length() > 0)
/*  39 */         filter = getText();
/*  40 */       if (this.filterListener != null)
/*  41 */         this.filterListener.setFilter(filter);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  47 */     return (getText().length() == 0) || (getText().equals(this.searchText));
/*     */   }
/*     */ 
/*     */   public void setMessageText(String message)
/*     */   {
/*  52 */     this.searchText = message;
/*     */ 
/*  54 */     if (getForeground().equals(this.grayColor))
/*  55 */       setText(this.searchText);
/*     */   }
/*     */ 
/*     */   public void programmaticallyEnterText(String text)
/*     */   {
/*  60 */     setText(text);
/*  61 */     setForeground(this.defaultColor);
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent e)
/*     */   {
/*  66 */     if (!hasEnteredText())
/*  67 */       setText("");
/*  68 */     setForeground(this.defaultColor);
/*     */   }
/*     */ 
/*     */   public void focusLost(FocusEvent e)
/*     */   {
/*  73 */     if (getText().length() == 0)
/*     */     {
/*  75 */       setText(this.searchText);
/*  76 */       setForeground(this.grayColor);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean hasEnteredText()
/*     */   {
/*  87 */     return getForeground().equals(this.defaultColor);
/*     */   }
/*     */ 
/*     */   public String getText()
/*     */   {
/*  92 */     if (hasEnteredText())
/*  93 */       return super.getText();
/*  94 */     return "";
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  99 */     if (this.filterListener != null)
/* 100 */       this.filterListener.setFilter(null);
/* 101 */     setText(this.searchText);
/* 102 */     setForeground(this.grayColor);
/*     */   }
/*     */ 
/*     */   public static abstract interface FilterListener
/*     */   {
/*     */     public abstract void setFilter(String paramString);
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.swing.components.MessageTextField
 * JD-Core Version:    0.6.2
 */