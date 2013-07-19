/*    */ package jwrapper.logging;
/*    */ 
/*    */ public class ProcessResult
/*    */ {
/*  5 */   public StringBuffer logMessages = new StringBuffer();
/*  6 */   public StringBuffer logErrors = new StringBuffer();
/*  7 */   public String stacktrace = "";
/*  8 */   public String source = "";
/*  9 */   public int code = 0;
/* 10 */   public long time = 0L;
/*    */   public static final int UNKNOWN = 0;
/*    */   public static final int OK = 1;
/*    */   public static final int ERROR = 2;
/*    */   public static final int CANCELLED = 3;
/*    */   public static final int LAUNCHED_OK = 4;
/*    */ 
/*    */   public String toString()
/*    */   {
/* 21 */     StringBuffer buffer = new StringBuffer();
/* 22 */     buffer.append("ProcessResult <").append(this.code).append(">\n");
/* 23 */     if ((this.logMessages != null) && (this.logMessages.length() > 0))
/* 24 */       buffer.append("Log: " + this.logMessages).append("\n");
/* 25 */     if ((this.logErrors != null) && (this.logErrors.length() > 0))
/* 26 */       buffer.append("Error: " + this.logErrors).append("\n");
/* 27 */     if ((this.stacktrace != null) && (this.stacktrace.length() > 0))
/* 28 */       buffer.append("StackTrace: " + this.stacktrace).append("\n");
/* 29 */     return buffer.toString();
/*    */   }
/*    */ 
/*    */   public boolean isUnknown()
/*    */   {
/* 34 */     return this.code == 0;
/*    */   }
/*    */ 
/*    */   public boolean isLaunchedOK()
/*    */   {
/* 39 */     return this.code == 4;
/*    */   }
/*    */ 
/*    */   public boolean isOK()
/*    */   {
/* 44 */     return this.code == 1;
/*    */   }
/*    */ 
/*    */   public boolean isError()
/*    */   {
/* 49 */     return this.code == 2;
/*    */   }
/*    */ 
/*    */   public boolean isCancelled()
/*    */   {
/* 54 */     return this.code == 3;
/*    */   }
/*    */ 
/*    */   public String getErrors()
/*    */   {
/* 59 */     return this.logErrors.toString();
/*    */   }
/*    */ 
/*    */   public String getStacktrace()
/*    */   {
/* 64 */     return this.stacktrace;
/*    */   }
/*    */ 
/*    */   public String getInfoMessages()
/*    */   {
/* 69 */     return this.logMessages.toString();
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.logging.ProcessResult
 * JD-Core Version:    0.6.2
 */