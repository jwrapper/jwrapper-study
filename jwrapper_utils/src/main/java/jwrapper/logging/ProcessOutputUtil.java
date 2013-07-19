/*     */ package jwrapper.logging;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import utils.files.FileUtil;
/*     */ import utils.string.CharStack;
/*     */ 
/*     */ public class ProcessOutputUtil
/*     */ {
/*     */   public static final String END_TOKEN = "<<<<END>>>>";
/*     */   private PrintStream outputStream;
/*     */   private String prefix;
/*     */ 
/*     */   public ProcessOutputUtil(String prefix, PrintStream outputStream)
/*     */   {
/*  24 */     this.outputStream = outputStream;
/*  25 */     this.prefix = prefix;
/*     */   }
/*     */ 
/*     */   public ProcessOutputUtil(String prefix, File targetFile) throws FileNotFoundException
/*     */   {
/*  30 */     this(prefix, new PrintStream(new FileOutputStream(targetFile)));
/*     */   }
/*     */ 
/*     */   public PrintStream getPrintStream()
/*     */   {
/*  35 */     return this.outputStream;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*  40 */     this.outputStream.println("<<<<END>>>>");
/*  41 */     this.outputStream.flush();
/*  42 */     this.outputStream.close();
/*     */   }
/*     */ 
/*     */   public static String getStacktrace(Throwable t)
/*     */   {
/*  47 */     ByteArrayOutputStream os = new ByteArrayOutputStream();
/*  48 */     PrintStream ps = new PrintStream(os);
/*  49 */     t.printStackTrace(ps);
/*  50 */     return os.toString();
/*     */   }
/*     */ 
/*     */   public void logMessage(String message)
/*     */   {
/*  55 */     if (message == null)
/*  56 */       message = "";
/*  57 */     this.outputStream.println("<<<<LOGINFO=\"" + message.trim() + "\">>>>");
/*  58 */     this.outputStream.flush();
/*     */ 
/*  60 */     System.out.println("[" + this.prefix + "] " + message);
/*     */   }
/*     */ 
/*     */   public void startLogMessage()
/*     */   {
/*  65 */     this.outputStream.println("<<<<LOGINFO=\"");
/*     */   }
/*     */ 
/*     */   public void endLogMessage()
/*     */   {
/*  70 */     this.outputStream.println("\">>>>");
/*     */   }
/*     */ 
/*     */   public void logError(String error)
/*     */   {
/*  75 */     if (error == null)
/*  76 */       error = "";
/*  77 */     this.outputStream.println("<<<<LOGERROR=\"" + error.trim() + "\">>>>");
/*  78 */     this.outputStream.flush();
/*     */ 
/*  80 */     System.out.println("[" + this.prefix + "] " + error.trim());
/*     */   }
/*     */ 
/*     */   public void writeSuccess(boolean success)
/*     */   {
/*  95 */     this.outputStream.println("<<<<SUCCESS=\"" + success + "\">>>>");
/*  96 */     this.outputStream.flush();
/*     */ 
/*  98 */     System.out.println("[" + this.prefix + "] SUCCESS=" + success);
/*     */   }
/*     */ 
/*     */   public void writeProcessResult(String source, int code)
/*     */   {
/* 122 */     long time = System.currentTimeMillis();
/* 123 */     this.outputStream.println("<<<<PROCESSRESULT=\"" + time + "," + source + "," + code + "\">>>>");
/* 124 */     this.outputStream.flush();
/*     */ 
/* 126 */     System.out.println("[" + this.prefix + "] PROCESSRESULT=" + time + "," + source + "," + code);
/*     */   }
/*     */ 
/*     */   public void writeResult(int returnCode)
/*     */   {
/* 131 */     writeProcessResult("", returnCode);
/*     */   }
/*     */ 
/*     */   public void logStackTrace(Throwable t)
/*     */   {
/* 136 */     String text = getStacktrace(t).trim();
/* 137 */     this.outputStream.println("<<<<STACKTRACE=\"" + text + "\">>>>");
/* 138 */     this.outputStream.flush();
/* 139 */     System.out.println("[" + this.prefix + "] " + text);
/*     */   }
/*     */ 
/*     */   public static ProcessResult parseFileForLatestReturnCode(File f, String msgPrefix) throws IOException
/*     */   {
/* 144 */     String contents = FileUtil.readFileAsString(f.getAbsolutePath());
/* 145 */     return parseOutputForLatestReturnCode(contents, msgPrefix);
/*     */   }
/*     */ 
/*     */   public static ProcessResult[] parseFileForAllReturnCodes(File f, String msgPrefix) throws IOException
/*     */   {
/* 150 */     String contents = FileUtil.readFileAsString(f.getAbsolutePath());
/* 151 */     return parseOutputForAllReturnCodes(contents, msgPrefix);
/*     */   }
/*     */ 
/*     */   public static ProcessResult parseOutputForLatestReturnCode(String output, String prefix)
/*     */   {
/* 156 */     System.out.println("[ProcessOutputParserUtil] Parsing process output...");
/* 157 */     ProcessResult result = new ProcessResult();
/* 158 */     CharStack stack = new CharStack(output);
/* 159 */     while (!stack.isEmpty())
/*     */     {
/* 161 */       stack.popUntil("<<<<", false);
/* 162 */       String key = stack.popUntil('=', true);
/* 163 */       stack.popUntil('"', false);
/* 164 */       String value = stack.popUntil('"', true);
/* 165 */       if (value != null)
/*     */       {
/* 167 */         if (key.equals("PROCESSRESULT"))
/*     */         {
/*     */           try
/*     */           {
/* 171 */             value = value.trim();
/* 172 */             String[] values = value.split(",");
/* 173 */             result.time = Long.parseLong(values[0]);
/* 174 */             result.source = values[1];
/* 175 */             result.code = Integer.parseInt(values[2]);
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/* 179 */             result.time = 0L;
/* 180 */             result.source = "";
/* 181 */             result.code = 0;
/*     */           }
/*     */         }
/* 184 */         else if (key.equals("LOGINFO"))
/* 185 */           result.logMessages.append(prefix + " " + value.trim() + "\n");
/* 186 */         else if (key.equals("LOGERROR"))
/* 187 */           result.logErrors.append(value.trim() + "\n");
/* 188 */         else if (key.equals("STACKTRACE"))
/* 189 */           result.stacktrace = value.trim();
/*     */       }
/*     */     }
/* 192 */     return result;
/*     */   }
/*     */ 
/*     */   public static ProcessResult[] parseOutputForAllReturnCodes(String output, String prefix)
/*     */   {
/* 197 */     System.out.println("[ProcessOutputParserUtil] Parsing process output for every return code...");
/* 198 */     ArrayList results = new ArrayList();
/* 199 */     CharStack stack = new CharStack(output);
/* 200 */     ProcessResult result = null;
/* 201 */     while (!stack.isEmpty())
/*     */     {
/* 203 */       stack.popUntil("<<<<", false);
/* 204 */       String key = stack.popUntil('=', true);
/* 205 */       stack.popUntil('"', false);
/* 206 */       String value = stack.popUntil('"', true);
/* 207 */       if (value != null)
/*     */       {
/* 209 */         if (result == null)
/*     */         {
/* 211 */           result = new ProcessResult();
/* 212 */           results.add(result);
/*     */         }
/* 214 */         if (key.equals("PROCESSRESULT"))
/*     */         {
/*     */           try
/*     */           {
/* 218 */             value = value.trim();
/* 219 */             String[] values = value.split(",");
/* 220 */             result.time = Long.parseLong(values[0]);
/* 221 */             result.source = values[1];
/* 222 */             result.code = Integer.parseInt(values[2]);
/*     */ 
/* 225 */             result = null;
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/* 229 */             result.time = 0L;
/* 230 */             result.source = "";
/* 231 */             result.code = 0;
/*     */           }
/*     */         }
/* 234 */         else if (key.equals("LOGINFO"))
/* 235 */           result.logMessages.append(prefix + " " + value.trim() + "\n");
/* 236 */         else if (key.equals("LOGERROR"))
/* 237 */           result.logErrors.append(value.trim() + "\n");
/* 238 */         else if (key.equals("STACKTRACE"))
/* 239 */           result.stacktrace = value.trim();
/*     */       }
/*     */     }
/* 242 */     ProcessResult[] allResults = new ProcessResult[results.size()];
/* 243 */     results.toArray(allResults);
/* 244 */     return allResults;
/*     */   }
/*     */ 
/*     */   public static void logProcessMessage(String source, String msg) {
/* 248 */     ProcessOutputUtil po = new ProcessOutputUtil(source, System.out);
/* 249 */     po.logMessage(msg);
/* 250 */     System.out.flush();
/*     */   }
/*     */   public static void logProcessError(String source, String err) {
/* 253 */     ProcessOutputUtil po = new ProcessOutputUtil(source, System.out);
/* 254 */     po.logError(err);
/* 255 */     System.out.flush();
/*     */   }
/*     */   public static void logProcessTrace(String source, Throwable t) {
/* 258 */     ProcessOutputUtil po = new ProcessOutputUtil(source, System.out);
/* 259 */     po.logStackTrace(t);
/* 260 */     System.out.flush();
/*     */   }
/*     */   public static void logProcessResult(String source, int code) {
/* 263 */     ProcessOutputUtil po = new ProcessOutputUtil(source, System.out);
/* 264 */     po.writeProcessResult(source, code);
/* 265 */     System.out.flush();
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.logging.ProcessOutputUtil
 * JD-Core Version:    0.6.2
 */