/*     */ package utils.files;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class AtomicRenamer
/*     */ {
/*     */   File instructionFile;
/*  14 */   ArrayList todo = new ArrayList();
/*     */ 
/*     */   public AtomicRenamer(File instructionFile)
/*     */   {
/*  11 */     this.instructionFile = instructionFile;
/*     */   }
/*     */ 
/*     */   public void addRenameInstruction(File from, File to)
/*     */   {
/*  17 */     this.todo.add(from);
/*  18 */     this.todo.add(to);
/*     */   }
/*     */ 
/*     */   public void cancelAtomicMultiRename() throws IOException {
/*  22 */     this.todo.clear();
/*  23 */     this.instructionFile.delete();
/*     */   }
/*     */ 
/*     */   public boolean performAtomicMultiRenameNow(long giveUpAfter) throws IOException {
/*  27 */     if (this.todo.size() == 0)
/*     */     {
/*  29 */       return completeAnyFailedAtomicMultiRenameNow(giveUpAfter);
/*     */     }
/*     */ 
/*  32 */     StringBuffer sb = new StringBuffer();
/*  33 */     for (int i = 0; i < this.todo.size(); i++) {
/*  34 */       sb.append(this.todo.get(i)).append("\n");
/*     */     }
/*     */ 
/*  37 */     File instIncomplete = new File(this.instructionFile.getAbsolutePath() + ".ar-inst-incomplete");
/*     */ 
/*  40 */     FileUtil.writeFileAsString(instIncomplete.getAbsolutePath(), sb.toString());
/*     */ 
/*  43 */     instIncomplete.renameTo(this.instructionFile);
/*     */ 
/*  47 */     boolean worked = completeAnyFailedAtomicMultiRenameNow(giveUpAfter);
/*     */ 
/*  49 */     this.todo.clear();
/*     */ 
/*  51 */     return worked;
/*     */   }
/*     */ 
/*     */   public boolean completeAnyFailedAtomicMultiRenameNow(long t) throws IOException {
/*  55 */     long giveUp = System.currentTimeMillis() + t;
/*  56 */     boolean ok = false;
/*  57 */     while (!ok) {
/*  58 */       ok = true;
/*     */ 
/*  60 */       if (this.instructionFile.exists()) {
/*  61 */         String lines = FileUtil.readFileAsString(this.instructionFile.getAbsolutePath());
/*  62 */         String[] all = lines.split("\n");
/*  63 */         for (int i = 0; i < all.length; i += 2)
/*     */         {
/*  65 */           String from = all[i];
/*  66 */           String to = all[(i + 1)];
/*  67 */           System.out.println("[AtomicRenamer] Renaming From: " + from);
/*  68 */           System.out.println("[AtomicRenamer] Renaming To: " + to);
/*     */ 
/*  70 */           File fileFrom = new File(from);
/*  71 */           File fileTo = new File(to);
/*     */ 
/*  73 */           if ((!fileFrom.exists()) && (fileTo.exists())) {
/*  74 */             System.out.println("[AtomicRenamer] (Already been done)");
/*     */           }
/*  76 */           else if (!fileFrom.renameTo(fileTo)) {
/*  77 */             System.out.println("[AtomicRenamer] (Failed)");
/*     */ 
/*  79 */             System.out.println("[AtmoicRenamer] fromFile exists? " + fileFrom.exists());
/*  80 */             System.out.println("[AtmoicRenamer] toFile exists? " + fileTo.exists());
/*     */ 
/*  82 */             if (System.currentTimeMillis() >= giveUp) {
/*  83 */               System.out.println("[AtomicRenamer] Giving up (after " + t + ")");
/*  84 */               return false;
/*     */             }
/*     */ 
/*  88 */             ok = false;
/*     */           }
/*     */           else {
/*  91 */             System.out.println("[AtomicRenamer] (OK) [" + fileTo.exists() + "]");
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*  98 */         if (ok) {
/*  99 */           this.instructionFile.delete();
/*     */         } else {
/* 101 */           System.out.println("[AtomicRenamer] will retry after 0.2s...");
/*     */           try {
/* 103 */             Thread.sleep(200L);
/*     */           } catch (Exception localException) {
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 109 */     return true;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 115 */     System.out.println(FileUtil.listDir(new File(".")));
/*     */ 
/* 117 */     AtomicRenamer at = new AtomicRenamer(new File("atomic_rename_instructions"));
/*     */ 
/* 119 */     if (at.instructionFile.exists()) {
/* 120 */       System.out.println("Finishing up then...");
/* 121 */       at.completeAnyFailedAtomicMultiRenameNow(5000L);
/*     */ 
/* 123 */       System.out.println(FileUtil.listDir(new File(".")));
/*     */ 
/* 125 */       System.exit(0);
/*     */     }
/*     */ 
/* 128 */     System.out.println("Creating files...");
/* 129 */     for (int i = 0; i < 5; i++) {
/* 130 */       File f = new File(i + ".old");
/* 131 */       File nf = new File(i + ".new");
/*     */ 
/* 133 */       new FileOutputStream(f).close();
/* 134 */       at.addRenameInstruction(f, nf);
/*     */     }
/* 136 */     System.out.println(FileUtil.listDir(new File(".")));
/*     */ 
/* 138 */     System.out.println("Renaming files...");
/* 139 */     at.performAtomicMultiRenameNow(5000L);
/*     */ 
/* 141 */     System.out.println(FileUtil.listDir(new File(".")));
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.files.AtomicRenamer
 * JD-Core Version:    0.6.2
 */