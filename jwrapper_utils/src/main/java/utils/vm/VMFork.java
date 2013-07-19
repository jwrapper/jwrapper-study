/*     */ package utils.vm;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import utils.ostools.OS;
/*     */ 
/*     */ public class VMFork
/*     */ {
/*     */   String cname;
/*     */   String[] args;
/*     */   String[] vmargs;
/* 152 */   int megs = 0;
/* 153 */   int stack = 0;
/*     */   String[] jars;
/*     */   String[] basejars;
/*     */   String[] libdirs;
/*     */   File dir;
/* 158 */   boolean mergeAllAfterPreExec = false;
/* 159 */   boolean escapeSpaces = false;
/* 160 */   private String java_exe = null;
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 28	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   3: new 34	java/lang/StringBuffer
/*     */     //   6: dup
/*     */     //   7: ldc 36
/*     */     //   9: invokespecial 38	java/lang/StringBuffer:<init>	(Ljava/lang/String;)V
/*     */     //   12: invokestatic 42	utils/vm/VMFork:getClassPathSeparator	()Ljava/lang/String;
/*     */     //   15: invokevirtual 46	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   18: ldc 50
/*     */     //   20: invokevirtual 46	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   23: invokevirtual 52	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*     */     //   26: invokevirtual 55	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   29: aload_0
/*     */     //   30: arraylength
/*     */     //   31: ifle +66 -> 97
/*     */     //   34: aload_0
/*     */     //   35: iconst_0
/*     */     //   36: aaload
/*     */     //   37: ldc 60
/*     */     //   39: invokevirtual 62	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
/*     */     //   42: ifeq +54 -> 96
/*     */     //   45: sipush 1024
/*     */     //   48: istore_1
/*     */     //   49: iload_1
/*     */     //   50: newarray byte
/*     */     //   52: astore_2
/*     */     //   53: iload_1
/*     */     //   54: iconst_2
/*     */     //   55: imul
/*     */     //   56: istore_1
/*     */     //   57: goto -8 -> 49
/*     */     //   60: astore_2
/*     */     //   61: invokestatic 68	java/lang/Runtime:getRuntime	()Ljava/lang/Runtime;
/*     */     //   64: astore_2
/*     */     //   65: getstatic 28	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   68: new 34	java/lang/StringBuffer
/*     */     //   71: dup
/*     */     //   72: ldc 74
/*     */     //   74: invokespecial 38	java/lang/StringBuffer:<init>	(Ljava/lang/String;)V
/*     */     //   77: iload_1
/*     */     //   78: sipush 1024
/*     */     //   81: idiv
/*     */     //   82: invokevirtual 76	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*     */     //   85: ldc 79
/*     */     //   87: invokevirtual 46	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   90: invokevirtual 52	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*     */     //   93: invokevirtual 55	java/io/PrintStream:println	(Ljava/lang/String;)V
/*     */     //   96: return
/*     */     //   97: new 1	utils/vm/VMFork
/*     */     //   100: dup
/*     */     //   101: ldc 81
/*     */     //   103: aconst_null
/*     */     //   104: new 83	java/io/File
/*     */     //   107: dup
/*     */     //   108: ldc 85
/*     */     //   110: invokespecial 87	java/io/File:<init>	(Ljava/lang/String;)V
/*     */     //   113: invokespecial 88	utils/vm/VMFork:<init>	(Ljava/lang/String;[Ljava/lang/String;Ljava/io/File;)V
/*     */     //   116: astore_1
/*     */     //   117: aload_1
/*     */     //   118: invokevirtual 91	utils/vm/VMFork:fork	()Ljava/lang/Process;
/*     */     //   121: astore_2
/*     */     //   122: new 95	utils/vm/ProcessPrinter
/*     */     //   125: dup
/*     */     //   126: aload_2
/*     */     //   127: getstatic 28	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   130: getstatic 97	java/lang/System:err	Ljava/io/PrintStream;
/*     */     //   133: invokespecial 100	utils/vm/ProcessPrinter:<init>	(Ljava/lang/Process;Ljava/io/OutputStream;Ljava/io/OutputStream;)V
/*     */     //   136: astore_3
/*     */     //   137: aload_2
/*     */     //   138: invokevirtual 103	java/lang/Process:waitFor	()I
/*     */     //   141: pop
/*     */     //   142: new 1	utils/vm/VMFork
/*     */     //   145: dup
/*     */     //   146: getstatic 109	utils/vm/VMFork:class$0	Ljava/lang/Class;
/*     */     //   149: dup
/*     */     //   150: ifnonnull +28 -> 178
/*     */     //   153: pop
/*     */     //   154: ldc 111
/*     */     //   156: invokestatic 113	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
/*     */     //   159: dup
/*     */     //   160: putstatic 109	utils/vm/VMFork:class$0	Ljava/lang/Class;
/*     */     //   163: goto +15 -> 178
/*     */     //   166: new 119	java/lang/NoClassDefFoundError
/*     */     //   169: dup_x1
/*     */     //   170: swap
/*     */     //   171: invokevirtual 121	java/lang/Throwable:getMessage	()Ljava/lang/String;
/*     */     //   174: invokespecial 126	java/lang/NoClassDefFoundError:<init>	(Ljava/lang/String;)V
/*     */     //   177: athrow
/*     */     //   178: invokevirtual 127	java/lang/Class:getName	()Ljava/lang/String;
/*     */     //   181: iconst_1
/*     */     //   182: anewarray 63	java/lang/String
/*     */     //   185: dup
/*     */     //   186: iconst_0
/*     */     //   187: ldc 60
/*     */     //   189: aastore
/*     */     //   190: new 83	java/io/File
/*     */     //   193: dup
/*     */     //   194: ldc 85
/*     */     //   196: invokespecial 87	java/io/File:<init>	(Ljava/lang/String;)V
/*     */     //   199: invokespecial 88	utils/vm/VMFork:<init>	(Ljava/lang/String;[Ljava/lang/String;Ljava/io/File;)V
/*     */     //   202: astore_1
/*     */     //   203: aload_1
/*     */     //   204: invokevirtual 91	utils/vm/VMFork:fork	()Ljava/lang/Process;
/*     */     //   207: astore_2
/*     */     //   208: aload_1
/*     */     //   209: iconst_1
/*     */     //   210: invokevirtual 130	utils/vm/VMFork:setMergeAllCommandsAfterPreExec	(Z)V
/*     */     //   213: getstatic 28	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   216: aload_1
/*     */     //   217: invokevirtual 134	java/io/PrintStream:println	(Ljava/lang/Object;)V
/*     */     //   220: new 95	utils/vm/ProcessPrinter
/*     */     //   223: dup
/*     */     //   224: aload_2
/*     */     //   225: getstatic 28	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   228: getstatic 97	java/lang/System:err	Ljava/io/PrintStream;
/*     */     //   231: invokespecial 100	utils/vm/ProcessPrinter:<init>	(Ljava/lang/Process;Ljava/io/OutputStream;Ljava/io/OutputStream;)V
/*     */     //   234: astore_3
/*     */     //   235: aload_2
/*     */     //   236: invokevirtual 103	java/lang/Process:waitFor	()I
/*     */     //   239: pop
/*     */     //   240: new 1	utils/vm/VMFork
/*     */     //   243: dup
/*     */     //   244: getstatic 109	utils/vm/VMFork:class$0	Ljava/lang/Class;
/*     */     //   247: dup
/*     */     //   248: ifnonnull +28 -> 276
/*     */     //   251: pop
/*     */     //   252: ldc 111
/*     */     //   254: invokestatic 113	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
/*     */     //   257: dup
/*     */     //   258: putstatic 109	utils/vm/VMFork:class$0	Ljava/lang/Class;
/*     */     //   261: goto +15 -> 276
/*     */     //   264: new 119	java/lang/NoClassDefFoundError
/*     */     //   267: dup_x1
/*     */     //   268: swap
/*     */     //   269: invokevirtual 121	java/lang/Throwable:getMessage	()Ljava/lang/String;
/*     */     //   272: invokespecial 126	java/lang/NoClassDefFoundError:<init>	(Ljava/lang/String;)V
/*     */     //   275: athrow
/*     */     //   276: invokevirtual 127	java/lang/Class:getName	()Ljava/lang/String;
/*     */     //   279: iconst_1
/*     */     //   280: anewarray 63	java/lang/String
/*     */     //   283: dup
/*     */     //   284: iconst_0
/*     */     //   285: ldc 60
/*     */     //   287: aastore
/*     */     //   288: new 83	java/io/File
/*     */     //   291: dup
/*     */     //   292: ldc 85
/*     */     //   294: invokespecial 87	java/io/File:<init>	(Ljava/lang/String;)V
/*     */     //   297: invokespecial 88	utils/vm/VMFork:<init>	(Ljava/lang/String;[Ljava/lang/String;Ljava/io/File;)V
/*     */     //   300: astore_1
/*     */     //   301: aload_1
/*     */     //   302: iconst_1
/*     */     //   303: anewarray 63	java/lang/String
/*     */     //   306: dup
/*     */     //   307: iconst_0
/*     */     //   308: ldc 137
/*     */     //   310: aastore
/*     */     //   311: invokevirtual 139	utils/vm/VMFork:setVMSpecificArgs	([Ljava/lang/String;)V
/*     */     //   314: aload_1
/*     */     //   315: invokevirtual 91	utils/vm/VMFork:fork	()Ljava/lang/Process;
/*     */     //   318: astore_2
/*     */     //   319: new 95	utils/vm/ProcessPrinter
/*     */     //   322: dup
/*     */     //   323: aload_2
/*     */     //   324: getstatic 28	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   327: getstatic 97	java/lang/System:err	Ljava/io/PrintStream;
/*     */     //   330: invokespecial 100	utils/vm/ProcessPrinter:<init>	(Ljava/lang/Process;Ljava/io/OutputStream;Ljava/io/OutputStream;)V
/*     */     //   333: astore_3
/*     */     //   334: aload_2
/*     */     //   335: invokevirtual 103	java/lang/Process:waitFor	()I
/*     */     //   338: pop
/*     */     //   339: new 1	utils/vm/VMFork
/*     */     //   342: dup
/*     */     //   343: getstatic 109	utils/vm/VMFork:class$0	Ljava/lang/Class;
/*     */     //   346: dup
/*     */     //   347: ifnonnull +28 -> 375
/*     */     //   350: pop
/*     */     //   351: ldc 111
/*     */     //   353: invokestatic 113	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
/*     */     //   356: dup
/*     */     //   357: putstatic 109	utils/vm/VMFork:class$0	Ljava/lang/Class;
/*     */     //   360: goto +15 -> 375
/*     */     //   363: new 119	java/lang/NoClassDefFoundError
/*     */     //   366: dup_x1
/*     */     //   367: swap
/*     */     //   368: invokevirtual 121	java/lang/Throwable:getMessage	()Ljava/lang/String;
/*     */     //   371: invokespecial 126	java/lang/NoClassDefFoundError:<init>	(Ljava/lang/String;)V
/*     */     //   374: athrow
/*     */     //   375: invokevirtual 127	java/lang/Class:getName	()Ljava/lang/String;
/*     */     //   378: iconst_1
/*     */     //   379: anewarray 63	java/lang/String
/*     */     //   382: dup
/*     */     //   383: iconst_0
/*     */     //   384: ldc 60
/*     */     //   386: aastore
/*     */     //   387: new 83	java/io/File
/*     */     //   390: dup
/*     */     //   391: ldc 85
/*     */     //   393: invokespecial 87	java/io/File:<init>	(Ljava/lang/String;)V
/*     */     //   396: invokespecial 88	utils/vm/VMFork:<init>	(Ljava/lang/String;[Ljava/lang/String;Ljava/io/File;)V
/*     */     //   399: astore_1
/*     */     //   400: aload_1
/*     */     //   401: bipush 99
/*     */     //   403: invokevirtual 142	utils/vm/VMFork:setJvmMemory	(I)V
/*     */     //   406: aload_1
/*     */     //   407: invokevirtual 91	utils/vm/VMFork:fork	()Ljava/lang/Process;
/*     */     //   410: astore_2
/*     */     //   411: new 95	utils/vm/ProcessPrinter
/*     */     //   414: dup
/*     */     //   415: aload_2
/*     */     //   416: getstatic 28	java/lang/System:out	Ljava/io/PrintStream;
/*     */     //   419: getstatic 97	java/lang/System:err	Ljava/io/PrintStream;
/*     */     //   422: invokespecial 100	utils/vm/ProcessPrinter:<init>	(Ljava/lang/Process;Ljava/io/OutputStream;Ljava/io/OutputStream;)V
/*     */     //   425: astore_3
/*     */     //   426: aload_2
/*     */     //   427: invokevirtual 103	java/lang/Process:waitFor	()I
/*     */     //   430: pop
/*     */     //   431: goto +8 -> 439
/*     */     //   434: astore_1
/*     */     //   435: aload_1
/*     */     //   436: invokevirtual 146	java/lang/Exception:printStackTrace	()V
/*     */     //   439: return
/*     */     //
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   49	60	60	java/lang/OutOfMemoryError
/*     */     //   154	159	166	java/lang/ClassNotFoundException
/*     */     //   252	257	264	java/lang/ClassNotFoundException
/*     */     //   351	356	363	java/lang/ClassNotFoundException
/*     */     //   97	431	434	java/lang/Exception
/*     */   }
/*     */ 
/*     */   public static String getJavaExe(String javaHome)
/*     */   {
/*  79 */     char c = File.separatorChar;
/*     */ 
/*  81 */     StringBuffer java = new StringBuffer(javaHome);
/*     */ 
/*  83 */     java.append(c);
/*  84 */     java.append("bin");
/*  85 */     java.append(c);
/*  86 */     java.append("java");
/*     */ 
/*  88 */     if (OS.isWindows()) {
/*  89 */       java.append("w.exe");
/*     */     }
/*     */ 
/*  92 */     return java.toString();
/*     */   }
/*     */ 
/*     */   public static String getJavaExe() {
/*  96 */     String jhome = System.getProperty("java.home");
/*  97 */     return getJavaExe(jhome);
/*     */   }
/*     */ 
/*     */   public static String getClassPathSeparator() {
/* 101 */     String sep = System.getProperty("path.separator");
/* 102 */     return sep;
/*     */   }
/*     */ 
/*     */   public static String getClassPath(String[] extra_jars) {
/* 106 */     return getClassPath(null, extra_jars);
/*     */   }
/*     */ 
/*     */   private static String getLibPath(String[] libdirs) {
/* 110 */     StringBuffer sb = new StringBuffer();
/* 111 */     String sep = getClassPathSeparator();
/* 112 */     if (libdirs != null) {
/* 113 */       for (int i = 0; i < libdirs.length; i++) {
/* 114 */         sb.append(libdirs[i]);
/* 115 */         if (i < libdirs.length - 1)
/*     */         {
/* 117 */           sb.append(sep);
/*     */         }
/*     */       }
/*     */     }
/* 121 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private static String getClassPath(String[] base_jars, String[] extra_jars)
/*     */   {
/* 126 */     String classpath = System.getProperty("java.class.path");
/* 127 */     String sep = getClassPathSeparator();
/*     */ 
/* 129 */     if (base_jars != null)
/*     */     {
/* 131 */       classpath = "";
/* 132 */       for (int i = 0; i < base_jars.length; i++) {
/* 133 */         classpath = classpath + base_jars[i] + sep;
/*     */       }
/*     */     }
/*     */ 
/* 137 */     if (extra_jars != null) {
/* 138 */       for (int i = 0; i < extra_jars.length; i++) {
/* 139 */         if (!classpath.endsWith(sep)) {
/* 140 */           classpath = classpath + sep;
/*     */         }
/* 142 */         classpath = classpath + extra_jars[i];
/*     */       }
/*     */     }
/*     */ 
/* 146 */     return classpath;
/*     */   }
/*     */ 
/*     */   public VMFork(String cname)
/*     */   {
/* 167 */     this.cname = cname;
/*     */   }
/*     */ 
/*     */   public VMFork(String cname, String[] args)
/*     */   {
/* 176 */     this.cname = cname;
/* 177 */     this.args = args;
/*     */   }
/*     */ 
/*     */   public VMFork(String cname, String[] args, File working_dir)
/*     */   {
/* 187 */     this.cname = cname;
/* 188 */     this.args = args;
/* 189 */     this.dir = working_dir;
/*     */   }
/*     */ 
/*     */   public void setClassName(String cname) {
/* 193 */     this.cname = cname;
/*     */   }
/*     */ 
/*     */   public void setClassArgs(String[] args) {
/* 197 */     this.args = args;
/*     */   }
/*     */ 
/*     */   public void setVMSpecificArgs(String[] args) {
/* 201 */     this.vmargs = args;
/*     */   }
/*     */ 
/*     */   public void setJvmMemory(int megs) {
/* 205 */     this.megs = megs;
/*     */   }
/*     */ 
/*     */   public void setJvmStack(int megs) {
/* 209 */     this.stack = megs;
/*     */   }
/*     */ 
/*     */   public void setClasspathExtras(String[] jars) {
/* 213 */     this.jars = jars;
/*     */   }
/*     */ 
/*     */   public void setMergeAllCommandsAfterPreExec(boolean b) {
/* 217 */     this.mergeAllAfterPreExec = b;
/*     */   }
/*     */ 
/*     */   public void setEscapeSpacesAfterPreExec(boolean b)
/*     */   {
/* 222 */     this.escapeSpaces = b;
/*     */   }
/*     */ 
/*     */   public void setLibpathExtras(String[] libdirs) {
/* 226 */     this.libdirs = libdirs;
/*     */   }
/*     */ 
/*     */   public void setWorkingDir(File dir) {
/* 230 */     this.dir = dir;
/*     */   }
/*     */ 
/*     */   public void setBaseClasspath(String[] jars) {
/* 234 */     this.basejars = jars;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 238 */     StringBuffer sb = new StringBuffer();
/*     */     try {
/* 240 */       sb.append("WDIR:" + this.dir + "\n");
/*     */ 
/* 242 */       ArrayList list = new ArrayList();
/* 243 */       fork(list, new String[0], null);
/*     */ 
/* 245 */       for (int i = 0; i < list.size(); i++)
/* 246 */         sb.append("CMD:" + list.get(i) + "\n");
/*     */     }
/*     */     catch (Exception e) {
/* 249 */       sb.append("VMFork problem: " + e);
/*     */     }
/* 251 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public Process fork()
/*     */     throws IOException
/*     */   {
/* 260 */     return fork(null, null, null);
/*     */   }
/*     */ 
/*     */   public Process fork(String[] preJvmExec) throws IOException {
/* 264 */     return fork(null, preJvmExec, null);
/*     */   }
/*     */ 
/*     */   public Process fork(Preloader usePreloader) throws IOException {
/* 268 */     return fork(null, null, usePreloader);
/*     */   }
/*     */ 
/*     */   public Process fork(String[] preJvmExec, Preloader usePreloader) throws IOException {
/* 272 */     return fork(null, preJvmExec, usePreloader);
/*     */   }
/*     */ 
/*     */   public String forkAsCommandString(String[] preJvmExec)
/*     */     throws IOException
/*     */   {
/* 280 */     ArrayList list = new ArrayList();
/* 281 */     fork(list, preJvmExec, null);
/*     */ 
/* 283 */     StringBuffer sb = new StringBuffer();
/* 284 */     for (int i = 0; i < list.size(); i++) {
/* 285 */       String cmd = (String)list.get(i);
/*     */ 
/* 287 */       if (!cmd.startsWith("\"")) {
/* 288 */         sb.append('"');
/* 289 */         sb.append(cmd);
/* 290 */         sb.append('"');
/*     */       } else {
/* 292 */         sb.append(cmd);
/*     */       }
/*     */ 
/* 295 */       if (i < list.size() - 1) {
/* 296 */         sb.append(' ');
/*     */       }
/*     */     }
/* 299 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public String getClasspath() {
/* 303 */     return getClassPath(this.basejars, this.jars);
/*     */   }
/*     */   public String getClassname() {
/* 306 */     return this.cname;
/*     */   }
/*     */   public String[] getParams() {
/* 309 */     if (this.args == null) {
/* 310 */       return new String[0];
/*     */     }
/* 312 */     return this.args;
/*     */   }
/*     */ 
/*     */   private Process fork(ArrayList cmds, String[] preJvmExec, Preloader usePreloader)
/*     */     throws IOException
/*     */   {
/* 318 */     if (this.java_exe == null) {
/* 319 */       this.java_exe = getJavaExe();
/*     */     }
/* 321 */     String libpath = getLibPath(this.libdirs);
/*     */ 
/* 323 */     String classpath = getClassPath(this.basejars, this.jars);
/*     */ 
/* 325 */     if (this.args == null) this.args = new String[0];
/* 326 */     if (this.vmargs == null) this.vmargs = new String[0];
/*     */ 
/* 328 */     ArrayList commands = new ArrayList();
/*     */ 
/* 330 */     commands.add(this.java_exe);
/* 331 */     if (libpath.length() > 0) {
/* 332 */       commands.add("-Djava.library.path=\"" + libpath + "\"");
/*     */     }
/* 334 */     if (classpath.length() > 0) {
/* 335 */       commands.add("-cp");
/* 336 */       if (OS.isWindows())
/*     */       {
/* 338 */         if ((this.mergeAllAfterPreExec) && (!this.escapeSpaces))
/* 339 */           commands.add("\"" + classpath + "\"");
/*     */         else
/* 341 */           commands.add(classpath);
/*     */       }
/*     */       else
/* 344 */         commands.add(classpath);
/*     */     }
/* 346 */     if (this.megs != 0) {
/* 347 */       commands.add("-Xmx" + this.megs + "m");
/*     */     }
/* 349 */     if (this.stack != 0) {
/* 350 */       commands.add("-Xss" + this.stack + "m");
/*     */     }
/* 352 */     for (int i = 0; i < this.vmargs.length; i++) {
/* 353 */       commands.add(this.vmargs[i]);
/*     */     }
/* 355 */     commands.add(this.cname);
/* 356 */     for (int i = 0; i < this.args.length; i++) {
/* 357 */       commands.add(this.args[i]);
/*     */     }
/*     */ 
/* 360 */     if (this.escapeSpaces)
/*     */     {
/* 362 */       for (int i = 0; i < commands.size(); i++)
/*     */       {
/* 364 */         String cmd = (String)commands.get(i);
/* 365 */         cmd = cmd.replace(" ", "\\ ");
/* 366 */         commands.set(i, cmd);
/*     */       }
/*     */     }
/*     */ 
/* 370 */     if (this.mergeAllAfterPreExec) {
/* 371 */       StringBuffer sb = new StringBuffer();
/* 372 */       for (int i = 0; i < commands.size(); i++) {
/* 373 */         if (i > 0) {
/* 374 */           sb.append(' ');
/*     */         }
/* 376 */         String cmd = (String)commands.get(i);
/* 377 */         sb.append(cmd);
/*     */       }
/*     */ 
/* 380 */       commands.clear();
/*     */ 
/* 382 */       commands.add(sb.toString());
/*     */     }
/*     */ 
/* 385 */     String[] commands_array = new String[commands.size()];
/* 386 */     commands.toArray(commands_array);
/*     */ 
/* 403 */     if (cmds == null) {
/* 404 */       if (preJvmExec != null) {
/* 405 */         String[] tmp = new String[commands_array.length + preJvmExec.length];
/* 406 */         System.arraycopy(preJvmExec, 0, tmp, 0, preJvmExec.length);
/* 407 */         System.arraycopy(commands_array, 0, tmp, preJvmExec.length, commands_array.length);
/* 408 */         StringBuffer buffer = new StringBuffer();
/* 409 */         for (int i = 0; i < tmp.length; i++)
/*     */         {
/* 411 */           if (i > 0)
/* 412 */             buffer.append(" ");
/* 413 */           buffer.append(tmp[i]);
/* 414 */           System.out.println(i + ") " + tmp[i]);
/*     */         }
/* 416 */         System.out.println("|" + buffer + "|");
/* 417 */         Process p = Runtime.getRuntime().exec(tmp, null, this.dir);
/* 418 */         return p;
/* 419 */       }if (usePreloader != null) {
/* 420 */         Process p = usePreloader.launch(this.cname, this.args);
/* 421 */         return p;
/*     */       }
/* 423 */       Process p = Runtime.getRuntime().exec(commands_array, null, this.dir);
/* 424 */       return p;
/*     */     }
/*     */ 
/* 427 */     if (preJvmExec != null) {
/* 428 */       for (int i = 0; i < preJvmExec.length; i++)
/*     */       {
/* 430 */         cmds.add(preJvmExec[i]);
/*     */       }
/*     */     }
/* 433 */     for (int i = 0; i < commands_array.length; i++)
/*     */     {
/* 435 */       cmds.add(commands_array[i]);
/*     */     }
/* 437 */     return null;
/*     */   }
/*     */ 
/*     */   public File getWorkingDir()
/*     */   {
/* 443 */     return this.dir;
/*     */   }
/*     */ 
/*     */   public void setJavaExe(String javaExe)
/*     */   {
/* 448 */     this.java_exe = javaExe;
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.vm.VMFork
 * JD-Core Version:    0.6.2
 */