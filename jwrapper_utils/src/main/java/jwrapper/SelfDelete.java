/*     */ package jwrapper;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import jwrapper.jwutils.JWGenericOS;
/*     */ import utils.files.FileUtil;
/*     */ import utils.ostools.OS;
/*     */ import utils.progtools.CheapTimingPrintStream;
/*     */ import utils.stream.MappedFile;
/*     */ import utils.vm.VMFork;
/*     */ 
/*     */ public class SelfDelete
/*     */ {
/*     */   public static void deleteSelf(File mydir, File[] extras, HeadlessSwipeLoadUtil swu, String app, byte[] splashPNG, byte[] logoPNG)
/*     */     throws IOException
/*     */   {
/*  30 */     if (app == null) app = "";
/*  31 */     if (splashPNG == null) splashPNG = new byte[0];
/*  32 */     if (logoPNG == null) logoPNG = new byte[0];
/*     */ 
/*  34 */     mydir = mydir.getCanonicalFile().getAbsoluteFile();
/*     */ 
/*  36 */     File tempdir = new File(System.getProperty("java.io.tmpdir"));
/*     */ 
/*  38 */     File jvmdir = new File(System.getProperty("java.home"));
/*     */ 
/*  40 */     File ddir = new File(tempdir, "selfdelete-" + System.currentTimeMillis());
/*  41 */     ddir.mkdirs();
/*     */ 
/*  43 */     boolean copyJVM = false;
/*     */ 
/*  45 */     System.out.println("JVM Dir:  " + jvmdir.getAbsolutePath());
/*  46 */     System.out.println("Deleting: " + ddir.getAbsolutePath());
/*     */ 
/*  48 */     if (jvmdir.getAbsolutePath().startsWith(mydir.getAbsolutePath())) {
/*  49 */       System.out.println("Have to copy JVM");
/*  50 */       copyJVM = true;
/*     */     }
/*     */ 
/*  54 */     if (copyJVM) {
/*  55 */       System.out.println("Copying JVM from " + jvmdir + " to " + ddir);
/*  56 */       FileUtil.copyFileOrDir(jvmdir, ddir);
/*     */ 
/*  58 */       if (!OS.isWindows()) {
/*  59 */         System.out.println("Making executable");
/*     */ 
/*  62 */         JWGenericOS.setWritableForAllUsersAndWait(ddir, true);
/*     */       }
/*     */     }
/*     */ 
/*  66 */     String csep = System.getProperty("path.separator");
/*  67 */     String[] jars = System.getProperty("java.class.path").split(csep);
/*     */ 
/*  69 */     String[] njars = new String[jars.length];
/*     */ 
/*  72 */     for (int i = 0; i < jars.length; i++) {
/*  73 */       File jar = new File(jars[i]);
/*     */ 
/*  75 */       File target = new File(ddir, "selfdeletejar" + i + ".jar");
/*  76 */       if (jar.isDirectory()) {
/*  77 */         target = new File(ddir, "selfdeletejar" + i);
/*     */       }
/*     */ 
/*  80 */       System.out.println("Copying " + jar + " to " + target);
/*     */ 
/*  82 */       if (jar.getName().equals("jwstandalonelaunch.jar")) {
/*  83 */         jar = new File(jar.getCanonicalFile().getParentFile(), "jwstandalone.jar");
/*  84 */         System.out.println("Switching to copy " + jar + " to " + target);
/*     */       }
/*     */ 
/*  87 */       FileUtil.copyFileOrDir(jar, target);
/*     */ 
/*  89 */       njars[i] = target.getAbsolutePath();
/*     */     }
/*     */ 
/*  97 */     File title = new File(ddir, "title");
/*  98 */     FileUtil.writeFileAsString(title.getAbsolutePath(), app);
/*     */ 
/* 100 */     File png = new File(ddir, "splash");
/* 101 */     FileUtil.writeFile(png, splashPNG);
/*     */ 
/* 103 */     File logo = new File(ddir, "logo");
/* 104 */     FileUtil.writeFile(logo, logoPNG);
/*     */ 
/* 106 */     VMFork fork = new VMFork(SelfDelete.class.getName());
/* 107 */     fork.setWorkingDir(ddir);
/* 108 */     fork.setBaseClasspath(njars);
/* 109 */     if (copyJVM) {
/* 110 */       if (OS.isWindows())
/* 111 */         fork.setJavaExe(new File(new File(ddir, "bin"), "javaw.exe").getAbsolutePath());
/*     */       else {
/* 113 */         fork.setJavaExe(new File(new File(ddir, "bin"), "java").getAbsolutePath());
/*     */       }
/*     */     }
/*     */ 
/* 117 */     String[] args = new String[extras.length + 1];
/* 118 */     args[0] = mydir.getAbsolutePath();
/* 119 */     for (int i = 0; i < extras.length; i++) {
/* 120 */       args[(1 + i)] = extras[i].getAbsolutePath();
/*     */     }
/*     */ 
/* 123 */     fork.setClassArgs(args);
/*     */ 
/* 125 */     System.out.println(fork);
/*     */ 
/* 127 */     Process p = fork.fork();
/*     */     try
/*     */     {
/* 130 */       System.out.println("Connecting to new JVM");
/*     */ 
/* 132 */       MappedFile mfw = new MappedFile(new File(ddir, "setupPosition"), 50000, true, 300000L);
/* 133 */       MappedFile mfr = new MappedFile(new File(ddir, "exitNotify"), 50000, false, 300000L);
/*     */ 
/* 135 */       mfr.readBlock();
/*     */ 
/* 139 */       ByteArrayOutputStream bout = new ByteArrayOutputStream();
/* 140 */       DataOutputStream dout = new DataOutputStream(bout);
/*     */ 
/* 142 */       if (swu != null) {
/* 143 */         int[] pos = swu.getPosition();
/* 144 */         System.out.println("Frame pos:");
/* 145 */         for (int i = 0; i < pos.length; i++) {
/* 146 */           System.out.println(pos[i]);
/* 147 */           dout.writeInt(pos[i]);
/*     */         }
/* 149 */         dout.flush();
/*     */       }
/*     */ 
/* 153 */       mfw.writeBlock(bout.toByteArray());
/*     */ 
/* 156 */       mfr.readBlock();
/*     */ 
/* 158 */       if (swu != null) {
/*     */         try {
/* 160 */           Thread.sleep(300L);
/*     */         } catch (Exception localException) {
/*     */         }
/* 163 */         swu.hideFrame();
/*     */       }
/*     */     }
/*     */     catch (InterruptedException localInterruptedException) {
/*     */     }
/* 168 */     System.exit(0);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 179 */       PrintStream out = new CheapTimingPrintStream(new FileOutputStream("DELETE.log"));
/* 180 */       System.setErr(out);
/* 181 */       System.setOut(out);
/*     */ 
/* 183 */       System.out.println("STDOUT test");
/* 184 */       System.err.println("STDERR test");
/*     */     }
/*     */     catch (Throwable localThrowable) {
/*     */     }
/* 188 */     MappedFile mfr = new MappedFile(new File("setupPosition"), 50000, false, 300000L);
/* 189 */     MappedFile mfw = new MappedFile(new File("exitNotify"), 50000, true, 300000L);
/*     */ 
/* 195 */     for (int i = 0; i < args.length; i++) {
/* 196 */       System.out.println("Deleting [" + args[i] + "]");
/*     */     }
/*     */ 
/* 202 */     HeadlessSwipeLoadUtil swu = new HeadlessSwipeLoadUtil();
/*     */ 
/* 204 */     String title = FileUtil.readFileAsString("title");
/*     */     Object splash;
/*     */     try
/*     */     {
/* 207 */       splash = swu.loadImage(new File("splash"));
/*     */     }
/*     */     catch (Exception x)
/*     */     {
/*     */       Object splash;
/* 209 */       splash = null;
/*     */     }
/*     */     Object logo;
/*     */     try {
/* 213 */       logo = swu.loadImage(new File("logo"));
/*     */     }
/*     */     catch (Exception x)
/*     */     {
/*     */       Object logo;
/* 215 */       logo = null;
/*     */     }
/*     */ 
/* 218 */     if (OS.isMacOS()) {
/*     */       try {
/* 220 */         HeadlessOsxUtil.setOSXAppName(title);
/*     */       }
/*     */       catch (Exception localException1) {
/*     */       }
/*     */       try {
/* 225 */         HeadlessOsxUtil.setOSXAppDockImage(logo);
/*     */       }
/*     */       catch (Exception localException2) {
/*     */       }
/*     */     }
/* 230 */     swu.setBigTo(splash);
/* 231 */     swu.setSmallTo("SmallUninstall");
/*     */ 
/* 233 */     swu.makeUninstaller(title, logo, null);
/*     */ 
/* 235 */     swu.disableButtons();
/* 236 */     swu.showInfiniteProgress();
/*     */ 
/* 239 */     mfw.writeBlock(new byte[1]);
/*     */ 
/* 242 */     byte[] dat = mfr.readBlock();
/* 243 */     DataInputStream din = new DataInputStream(new ByteArrayInputStream(dat));
/*     */ 
/* 245 */     if (title.length() > 0) {
/* 246 */       if (dat.length > 0)
/*     */       {
/* 248 */         int[] tmp = new int[4];
/* 249 */         for (int i = 0; i < tmp.length; i++) {
/* 250 */           tmp[i] = din.readInt();
/*     */         }
/* 252 */         swu.setPosition(tmp);
/*     */       }
/*     */ 
/* 256 */       swu.ensureShowing();
/* 257 */       swu.preventWindowClose();
/*     */     }
/*     */ 
/* 261 */     mfw.writeBlock(new byte[1]);
/*     */ 
/* 263 */     for (int i = 0; i < args.length; i++) {
/* 264 */       File todelete = new File(args[i]);
/* 265 */       System.out.println("Trying to delete " + todelete + " now...");
/*     */ 
/* 267 */       long showError = System.currentTimeMillis() + 120000L;
/*     */ 
/* 270 */       while (todelete.exists()) {
/* 271 */         FileUtil.deleteDir(todelete);
/*     */ 
/* 273 */         if (todelete.exists())
/*     */         {
/* 275 */           if (System.currentTimeMillis() > showError) {
/* 276 */             System.out.println("ERROR: Unable to delete " + todelete);
/* 277 */             System.exit(1);
/*     */           }
/*     */ 
/* 280 */           System.out.println("Unable to delete, will try again...");
/* 281 */           Thread.sleep(5000L);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 295 */       System.out.println("Deleted OK");
/*     */     }
/*     */ 
/* 298 */     System.out.println("Uninstallation complete");
/*     */ 
/* 300 */     System.out.flush();
/*     */ 
/* 302 */     System.exit(0);
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.SelfDelete
 * JD-Core Version:    0.6.2
 */