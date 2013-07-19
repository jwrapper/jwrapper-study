/*     */ package jwrapper.updater;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import utils.files.FileUtil;
/*     */ import utils.stream.CFriendlyProperties;
/*     */ 
/*     */ public class JWLaunchProperties
/*     */ {
/*     */   public static final String PROP_UPDATE_URL = "update_url";
/*     */   public static final String PROP_SPLASH_IMAGE = "splash_image";
/*     */   public static final String PROP_MY_DIR = "app_dir";
/*     */   public static final String PROP_APP_JRE_DIR = "app_jre_dir";
/*     */   public static final String PROP_APP_NAME = "app_name";
/*     */   public static final String PROP_JRE_NAME = "jre_name";
/*     */   public static final String PROP_MIN_SPLASH_MS = "min_splash_ms";
/*     */   public static final String PROP_SILENT_PARAMETER = "silent_parameter";
/*     */   public static final String PROP_CAN_OVERRIDE_SPLASH = "can_override_splash";
/*     */   public static final String PROP_MATCH_VERSIONS = "match_versions";
/*     */   public static final String PROP_INSTALL_TYPE = "install_type";
/*     */   public static final String PROP_FIRST_RUN_POST_UPDATE = "first_run_post_update";
/*     */   public static final String PROP_FIRST_RUN_POST_INSTALL = "first_run_post_install";
/*     */   public static final String PROP_JVM_OPTIONS = "jvm_options";
/*     */   public static final String PROP_SUPPORTED_LANGS = "supported_langs";
/*     */   public static final String PROP_SHOW_NO_UI = "show_no_ui";
/*     */   public static final String PROP_LAUNCHED_FROM_DYNPROPS = "launched_from_dynprops";
/*     */   public static final String PROP_WINDOWS_APP_ID = "windows_app_id";
/*     */   public static final String WRAPPER_APP_VERSION = "wrapper_app_version";
/*     */   public static final String WRAPPER_JW_VERSION = "wrapper_gu_version";
/*     */   public static final String WRAPPER_AUTO_TEST = "wrapper_autotest";
/*     */   public static final String WRAPPER_MAX_APP_VERSIONS = "wrapper_app_versions";
/*     */   public static final String WRAPPER_MAX_GU_VERSIONS = "wrapper_gu_versions";
/*     */   public static final String DEBUG_LOGGING = "debug_logging";
/*     */   public static final String DEBUG_LOGGING_UNTIL = "debug_logging_until";
/*     */   public static final String LAUNCHER_VIRTUAL_APP = "gu_virt_app";
/*     */   public static final String LAUNCH_ELEVATE = "launch_elevate";
/*     */   public static final String LAUNCH_ELEVATE_SILENT = "launch_elevate_silent";
/*     */   public static final String LAUNCH_IN_SESSION = "launch_in_session";
/*  46 */   Properties props = new Properties();
/*     */   File file;
/*     */   static JWLaunchProperties INSTANCE;
/*     */ 
/*     */   public void addStaticProperty(String name, String value)
/*     */   {
/*  49 */     if (value == null) value = "";
/*  50 */     this.props.setProperty("jwstat_" + name, value);
/*     */   }
/*     */   public void addDynamicProperty(String name, String value) {
/*  53 */     if (value == null) value = "";
/*  54 */     this.props.setProperty("jwdyna_" + name, value);
/*     */   }
/*     */ 
/*     */   public static boolean isNonEmpty(String value)
/*     */   {
/*  65 */     if (value == null) return false;
/*  66 */     if (value.trim().length() == 0) return false;
/*  67 */     return true;
/*     */   }
/*     */ 
/*     */   public String getFileRef()
/*     */   {
/*  72 */     return this.file.getAbsolutePath();
/*     */   }
/*     */ 
/*     */   void store() throws IOException {
/*  76 */     byte[] dat = CFriendlyProperties.encode(this.props);
/*  77 */     FileUtil.writeFile(this.file, dat);
/*     */   }
/*     */ 
/*     */   public void setLaunchFolder(File folder)
/*     */   {
/*  83 */     this.file = new File(folder, "JWLaunchProperties-" + System.currentTimeMillis() + "-" + folder.list().length);
/*     */   }
/*     */ 
/*     */   public JWLaunchProperties(File folder) {
/*  87 */     if (folder != null)
/*  88 */       this.file = new File(folder, "JWLaunchProperties-" + System.currentTimeMillis() + "-" + folder.list().length);
/*     */   }
/*     */ 
/*     */   private JWLaunchProperties() {
/*     */   }
/*     */ 
/*     */   public static void addAllProperties(Properties target, Properties from) {
/*  95 */     Object[] keys = from.keySet().toArray();
/*  96 */     for (int i = 0; i < keys.length; i++) {
/*  97 */       String key = (String)keys[i];
/*  98 */       target.setProperty(key, from.getProperty(key));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Properties getAsProperties() {
/* 103 */     if (INSTANCE == null) {
/* 104 */       System.out.println("[JWrapper] ERROR: JWLaunchProperties not set up, call 'argsToNormalArgs(...)'");
/* 105 */       return null;
/*     */     }
/*     */ 
/* 108 */     Properties tmp = new Properties();
/*     */ 
/* 110 */     Properties mine = INSTANCE.props;
/* 111 */     Object[] keys = mine.keySet().toArray();
/* 112 */     for (int i = 0; i < keys.length; i++) {
/* 113 */       String key = (String)keys[i];
/* 114 */       tmp.setProperty(key.substring(7), mine.getProperty(key));
/*     */     }
/*     */ 
/* 117 */     return tmp;
/*     */   }
/*     */ 
/*     */   public static void overrideProperty(String name, String value) {
/* 121 */     if (INSTANCE == null) {
/* 122 */       System.out.println("[JWrapper] ERROR: JWLaunchProperties not set up, call 'argsToNormalArgs(...)'");
/*     */     }
/*     */ 
/* 126 */     INSTANCE.addStaticProperty(name, value);
/*     */   }
/*     */ 
/*     */   public static String getProperty(String name) {
/* 130 */     String result = getPropertyPossiblyNull(name);
/* 131 */     if (result == null)
/* 132 */       return "";
/* 133 */     return result;
/*     */   }
/*     */ 
/*     */   public static boolean isDynamicUpdateURL() {
/* 137 */     String value = INSTANCE.props.getProperty("jwstat_update_url");
/* 138 */     if (value == null)
/* 139 */       return false;
/* 140 */     if (value.equals("D"))
/*     */     {
/* 142 */       return true;
/*     */     }
/* 144 */     return false;
/*     */   }
/*     */ 
/*     */   public static String getPropertyPossiblyNull(String name)
/*     */   {
/* 150 */     if (INSTANCE == null) {
/* 151 */       System.out.println("[JWrapper] ERROR: JWLaunchProperties not set up, call 'argsToNormalArgs(...)'");
/* 152 */       return "ERROR: JWLaunchProperties not set up, call 'argsToNormalArgs(...)'";
/*     */     }
/*     */ 
/* 156 */     String value = INSTANCE.props.getProperty("jwstat_" + name);
/* 157 */     if (value == null) {
/* 158 */       value = INSTANCE.props.getProperty("jwdyna_" + name);
/*     */     }
/* 161 */     else if ((name.equals("update_url")) && 
/* 162 */       (value.equals("D")) && 
/* 163 */       (INSTANCE.props.getProperty("jwdyna_" + name) != null))
/*     */     {
/* 165 */       value = INSTANCE.props.getProperty("jwdyna_" + name);
/*     */     }
/*     */ 
/* 168 */     return value;
/*     */   }
/*     */ 
/*     */   public static void cleanDir(File dir)
/*     */   {
/* 174 */     long hoursAgo = System.currentTimeMillis() - 7200000L;
/* 175 */     File[] files = dir.listFiles();
/* 176 */     for (int i = 0; i < files.length; i++)
/* 177 */       if ((files[i].getName().startsWith("JWLaunchProperties-")) && 
/* 178 */         (files[i].lastModified() < hoursAgo))
/* 179 */         files[i].delete();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 186 */     StringBuffer sb = new StringBuffer();
/*     */ 
/* 188 */     Object[] keys = this.props.keySet().toArray();
/* 189 */     Arrays.sort(keys);
/* 190 */     for (int i = 0; i < keys.length; i++)
/*     */     {
/* 192 */       String key = String.valueOf(keys[i]);
/* 193 */       String value = this.props.getProperty((String)keys[i]);
/* 194 */       if ((key.contains("splash_image")) && (value != null) && (value.length() > 100))
/* 195 */         value = value.substring(0, 99) + "...";
/* 196 */       sb.append("[JWrapperLaunchProperty] ").append(key).append("=[").append(value).append("]\n");
/*     */     }
/*     */ 
/* 199 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static String[] argsToNormalArgs(String[] args) {
/*     */     try {
/* 204 */       String loc = args[0];
/*     */ 
/* 206 */       System.out.println("[JWrapper] loading props from " + loc);
/*     */ 
/* 208 */       INSTANCE = new JWLaunchProperties();
/* 209 */       INSTANCE.props = CFriendlyProperties.decode(FileUtil.readFile(loc));
/*     */ 
/* 211 */       new File(loc).delete();
/*     */ 
/* 213 */       System.out.println(INSTANCE);
/*     */     }
/*     */     catch (IOException x) {
/* 216 */       x.printStackTrace();
/*     */     }
/*     */ 
/* 219 */     String[] tmp = new String[args.length - 1];
/* 220 */     if (tmp.length > 0) {
/* 221 */       System.arraycopy(args, 1, tmp, 0, tmp.length);
/*     */     }
/* 223 */     return tmp;
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.updater.JWLaunchProperties
 * JD-Core Version:    0.6.2
 */