/*     */ package jwrapper.updater;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import jwrapper.jwutils.JWSystem;
/*     */ import utils.stream.StreamUtils;
/*     */ 
/*     */ public class JWApp
/*     */ {
/*     */   public String name;
/*     */   public byte[] logoICO;
/*     */   public int pngIndex;
/*     */   public int pngLen;
/*     */   public String mainClass;
/*  24 */   public ArrayList args = new ArrayList();
/*     */   public boolean userAccessible;
/*     */ 
/*     */   public byte[] getLogoICO()
/*     */   {
/*  28 */     return this.logoICO;
/*     */   }
/*     */ 
/*     */   public byte[] getLogoPNG() {
/*  32 */     byte[] dat = new byte[this.pngLen];
/*  33 */     System.arraycopy(this.logoICO, this.pngIndex, dat, 0, this.pngLen);
/*  34 */     return dat;
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  38 */     StringBuffer sb = new StringBuffer();
/*  39 */     sb.append("App (user:" + this.userAccessible + ") \"" + this.name + "\" = " + this.mainClass);
/*  40 */     for (int i = 0; i < this.args.size(); i++) {
/*  41 */       sb.append(" " + this.args.get(i));
/*     */     }
/*  43 */     sb.append(" [any further args]");
/*  44 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static String normaliseName(String app) {
/*  48 */     char[] tmp = app.toCharArray();
/*  49 */     for (int i = 0; i < tmp.length; i++) {
/*  50 */       if (!Character.isLetterOrDigit(tmp[i])) {
/*  51 */         tmp[i] = '_';
/*     */       }
/*     */     }
/*  54 */     return new String(tmp);
/*     */   }
/*     */ 
/*     */   public static File getJWAppsFolder(File master) {
/*  58 */     return new File(master, "JWApps");
/*     */   }
/*     */ 
/*     */   public File getICOFile() {
/*  62 */     File app = JWSystem.getAppFolder().getParentFile();
/*  63 */     File jwdirs = new File(app, "JWApps");
/*  64 */     if (!jwdirs.exists()) jwdirs.mkdirs();
/*  65 */     return new File(jwdirs, getFilesystemName() + "ICO.ico");
/*     */   }
/*     */   public String getFilesystemName() {
/*  68 */     return normaliseName(this.name);
/*     */   }
/*     */   public String getUserVisibleName() {
/*  71 */     return this.name;
/*     */   }
/*     */ 
/*     */   public File save(File folder) throws IOException
/*     */   {
/*  76 */     File target = new File(folder, "JWAppSpec-" + normaliseName(this.name));
/*     */ 
/*  78 */     OutputStream out = new BufferedOutputStream(new FileOutputStream(target));
/*     */ 
/*  80 */     StreamUtils.writeInt(out, 0);
/*  81 */     StreamUtils.writeStringUTF8(out, this.name);
/*  82 */     StreamUtils.writeStringUTF8(out, this.mainClass);
/*  83 */     StreamUtils.writeBoolean(out, this.userAccessible);
/*  84 */     StreamUtils.writeInt(out, this.args.size());
/*  85 */     for (int i = 0; i < this.args.size(); i++) {
/*  86 */       StreamUtils.writeStringUTF8(out, (String)this.args.get(i));
/*     */     }
/*     */ 
/*  89 */     StreamUtils.writeBytes(out, this.logoICO);
/*     */ 
/*  92 */     StreamUtils.writeInt(out, this.pngIndex);
/*  93 */     StreamUtils.writeInt(out, this.pngLen);
/*     */ 
/*  95 */     out.flush();
/*  96 */     out.close();
/*     */ 
/*  98 */     return target;
/*     */   }
/*     */ 
/*     */   private static JWApp load(File file) throws IOException {
/* 102 */     InputStream in = new BufferedInputStream(new FileInputStream(file));
/*     */ 
/* 104 */     JWApp tmp = new JWApp();
/* 105 */     StreamUtils.readInt(in);
/*     */ 
/* 107 */     tmp.name = StreamUtils.readStringUTF8(in);
/* 108 */     tmp.mainClass = StreamUtils.readStringUTF8(in);
/* 109 */     tmp.userAccessible = StreamUtils.readBoolean(in);
/* 110 */     int A = StreamUtils.readInt(in);
/* 111 */     for (int i = 0; i < A; i++) {
/* 112 */       tmp.args.add(StreamUtils.readStringUTF8(in));
/*     */     }
/*     */ 
/* 115 */     tmp.logoICO = StreamUtils.readNBytes(in, 10000000);
/*     */ 
/* 118 */     tmp.pngIndex = StreamUtils.readInt(in);
/* 119 */     tmp.pngLen = StreamUtils.readInt(in);
/*     */ 
/* 121 */     in.close();
/*     */ 
/* 123 */     return tmp;
/*     */   }
/*     */ 
/*     */   public static JWApp getMyVirtualApp() throws IOException {
/* 127 */     return load(JWSystem.getAppFolder(), JWLaunchProperties.getProperty("gu_virt_app"));
/*     */   }
/*     */ 
/*     */   public static JWApp load(File folder, String name) throws IOException {
/* 131 */     return load(new File(folder, "JWAppSpec-" + normaliseName(name)));
/*     */   }
/*     */ 
/*     */   public static JWApp getJWApp(File appBundleFolder, String name) throws IOException {
/* 135 */     return load(appBundleFolder, name);
/*     */   }
/*     */ 
/*     */   public static JWApp[] getAllJWApps(File appBundleFolder, boolean userAccessibleOnly) throws IOException {
/* 139 */     ArrayList apps = LaunchFile.getFilesStartingWith(appBundleFolder, "JWAppSpec-", false);
/* 140 */     ArrayList tmp = new ArrayList();
/* 141 */     for (int i = 0; i < apps.size(); i++) {
/* 142 */       JWApp jwapp = load((File)apps.get(i));
/* 143 */       if (userAccessibleOnly) {
/* 144 */         if (jwapp.userAccessible)
/* 145 */           tmp.add(jwapp);
/*     */       }
/*     */       else {
/* 148 */         tmp.add(jwapp);
/*     */       }
/*     */     }
/*     */ 
/* 152 */     JWApp[] all = new JWApp[tmp.size()];
/* 153 */     tmp.toArray(all);
/* 154 */     return all;
/*     */   }
/*     */ 
/*     */   public static JWApp getFirstJWApp(File appFolder, boolean userAccessibleOnly) throws IOException {
/* 158 */     return getAllJWApps(appFolder, userAccessibleOnly)[0];
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.updater.JWApp
 * JD-Core Version:    0.6.2
 */