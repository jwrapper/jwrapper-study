/*     */ package jwrapper.logging;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import utils.progtools.MapOfLists;
/*     */ 
/*     */ public class LogPoller
/*     */ {
/*   9 */   public static int POLL_FREQ = 2000;
/*     */   File master;
/*  12 */   long after = 0L;
/*     */ 
/*  21 */   Object ANY = new Object();
/*  22 */   MapOfLists conditions = new MapOfLists(false);
/*     */ 
/*     */   public LogPoller(File master)
/*     */   {
/*  15 */     this.master = master;
/*  16 */     System.out.println("[LogPoller] Set up for " + master);
/*  17 */     returnResultsAfter(System.currentTimeMillis());
/*  18 */     System.out.println("[LogPoller] Returning results after " + this.after);
/*     */   }
/*     */ 
/*     */   public void addReturnOnLaunchedOkCase(String vapp)
/*     */   {
/*  29 */     System.out.println("[LogPoller] Returning if [" + vapp + "] = (OK)");
/*  30 */     this.conditions.add(vapp, new Integer(4));
/*     */   }
/*     */ 
/*     */   public void addReturnOnSuccessCase(String vapp)
/*     */   {
/*  38 */     System.out.println("[LogPoller] Returning if [" + vapp + "] = (OK)");
/*  39 */     this.conditions.add(vapp, new Integer(1));
/*     */   }
/*     */ 
/*     */   public void addReturnOnErrorCase(String vapp)
/*     */   {
/*  47 */     System.out.println("[LogPoller] Returning if [" + vapp + "] = (ERROR)");
/*  48 */     this.conditions.add(vapp, new Integer(2));
/*     */   }
/*     */ 
/*     */   public void addReturnOnCancelledCase(String vapp)
/*     */   {
/*  56 */     System.out.println("[LogPoller] Returning if [" + vapp + "] = (CANCELLED)");
/*  57 */     this.conditions.add(vapp, new Integer(3));
/*     */   }
/*     */ 
/*     */   public void addReturnOnAnyCase(String vapp)
/*     */   {
/*  65 */     System.out.println("[LogPoller] Returning if [" + vapp + "] = (ANY)");
/*  66 */     this.conditions.add(vapp, this.ANY);
/*     */   }
/*     */ 
/*     */   public void addReturnOnCustomCase(String vapp, int resultCode)
/*     */   {
/*  74 */     System.out.println("[LogPoller] Returning if [" + vapp + "] = " + resultCode);
/*  75 */     this.conditions.add(vapp, new Integer(resultCode));
/*     */   }
/*     */ 
/*     */   public void addReturnIfAutoupdateFailed()
/*     */   {
/*  82 */     addReturnOnErrorCase("JWrapper");
/*  83 */     addReturnOnCancelledCase("JWrapper");
/*     */   }
/*     */ 
/*     */   public void returnResultsAfter(long time)
/*     */   {
/*  91 */     this.after = time;
/*  92 */     System.out.println("[LogPoller] Returning results after " + this.after);
/*     */   }
/*     */ 
/*     */   public ProcessResult waitForResult(long timeout)
/*     */     throws InterruptedException
/*     */   {
/* 103 */     long giveUp = System.currentTimeMillis() + timeout;
/*     */     try
/*     */     {
/*     */       while (true)
/*     */       {
/*     */         try {
/* 109 */           if (!this.master.exists()) {
/* 110 */             System.out.println("Waiting for Process Result (master install folder does not exist yet)");
/*     */           }
/*     */           else {
/* 113 */             File logs = new File(this.master, "logs");
/*     */ 
/* 115 */             if (!logs.exists()) {
/* 116 */               System.out.println("Waiting for Process Result (logs folder does not exist yet)");
/*     */             }
/*     */             else {
/* 119 */               File[] files = logs.listFiles();
/*     */ 
/* 121 */               if (files.length == 0) {
/* 122 */                 System.out.println("Waiting for Process Result (no log files found yet)");
/*     */               }
/*     */               else {
/* 125 */                 int i = 0; continue;
/* 126 */                 System.out.println("[LogPoller] Checking " + files[i]);
/* 127 */                 ProcessResult[] prs = ProcessOutputUtil.parseFileForAllReturnCodes(files[i], "");
/* 128 */                 System.out.println("[LogPoller] Found " + prs.length + " process results");
/* 129 */                 int prIndex = 0; continue;
/*     */ 
/* 131 */                 ProcessResult pr = prs[prIndex];
/*     */ 
/* 133 */                 System.out.println("[LogPoller] Found a result " + pr);
/*     */ 
/* 135 */                 if (pr.time >= this.after)
/*     */                 {
/* 137 */                   String source = pr.source;
/*     */ 
/* 139 */                   System.out.println("[LogPoller] Source is [" + source + "]");
/*     */ 
/* 141 */                   if (this.conditions.exists(source, this.ANY))
/*     */                   {
/* 143 */                     return pr;
/*     */                   }
/*     */ 
/* 146 */                   if (this.conditions.exists(source, new Integer(pr.code)))
/*     */                   {
/* 148 */                     return pr;
/*     */                   }
/* 150 */                   System.out.println("[LogPoller] No condition satisfied. Skipping.");
/*     */                 } else {
/* 152 */                   System.out.println("[LogPoller] Result too old - " + pr.time + " < " + this.after);
/*     */                 }
/* 129 */                 prIndex++; if (prIndex < prs.length)
/*     */                   continue;
/* 125 */                 i++; if (i < files.length)
/*     */                 {
/*     */                   continue;
/*     */                 }
/*     */ 
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */         catch (Exception localException)
/*     */         {
/*     */         }
/*     */ 
/* 164 */         if (System.currentTimeMillis() > giveUp) {
/*     */           break;
/*     */         }
/* 167 */         Thread.sleep(POLL_FREQ);
/*     */       }
/*     */     } catch (InterruptedException x) {
/* 170 */       System.out.println("Waiting for Process Result (finished - due to interrupt!)");
/*     */     }
/*     */ 
/* 173 */     return null;
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.logging.LogPoller
 * JD-Core Version:    0.6.2
 */