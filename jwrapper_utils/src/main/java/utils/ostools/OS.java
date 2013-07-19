/*     */ package utils.ostools;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class OS
/*     */ {
/*     */   public static final int BASE_WINDOWS = 0;
/*     */   public static final int BASE_MACOS = 1;
/*     */   public static final int BASE_LINUX = 2;
/*     */   public static final int BASE_UNKNOWN = 3;
/*     */   public static final int BASE_ALL = -1;
/*     */   private static final int VARIANT_WINDOWS_VISTA = 1;
/*     */   private static final int VARIANT_UNKNOWN = 2;
/*     */   private static final int VARIANT_WINDOWS_XP = 3;
/*     */   private static final int VARIANT_WINDOWS_2003 = 4;
/*     */   private static final int VARIANT_WINDOWS_PREXP = 5;
/*     */   private static final int VARIANT_WINDOWS_8_OR_ABOVE = 6;
/*     */   private static final int VARIANT_WINDOWS_7 = 7;
/*  24 */   public static int base_type = -1;
/*  25 */   public static int variant = -1;
/*  26 */   public static int linux_arch = -1;
/*     */   private static final int LARCH_32 = 1;
/*     */   private static final int LARCH_64 = 2;
/*     */ 
/*     */   public static boolean isVariantVistaOrAbove(int variant)
/*     */   {
/*  35 */     return (variant == 1) || 
/*  34 */       (variant == 6) || 
/*  35 */       (variant == 7);
/*     */   }
/*     */ 
/*     */   public static boolean isVariantWindows8OrAbove(int variant)
/*     */   {
/*  40 */     return variant == 6;
/*     */   }
/*     */ 
/*     */   public static boolean isVariantWindows7OrAbove(int variant)
/*     */   {
/*  45 */     return (variant == 7) || (variant == 6);
/*     */   }
/*     */ 
/*     */   public static boolean isVariantWindowsXPOrAbove(int variant)
/*     */   {
/*  50 */     return (isVariantVistaOrAbove(variant)) || (variant == 3) || (variant == 4);
/*     */   }
/*     */ 
/*     */   private static void detect() {
/*  54 */     if (base_type != -1) return;
/*     */ 
/*  56 */     String osname = System.getProperty("os.name");
/*  57 */     if (osname == null) {
/*  58 */       osname = "";
/*     */     }
/*  60 */     osname = osname.toLowerCase().trim();
/*     */ 
/*  62 */     String osver = System.getProperty("os.version");
/*  63 */     if (osver == null) {
/*  64 */       osver = "";
/*     */     }
/*  66 */     osver = osver.toLowerCase().trim();
/*     */ 
/*  71 */     if ((osname.indexOf("win") != -1) && (osname.indexOf("darwin") == -1)) {
/*  72 */       base_type = 0;
/*     */ 
/*  74 */       if ((osver.startsWith("6")) || (osver.startsWith("7")) || (osver.startsWith("8")) || (osver.startsWith("9")))
/*     */       {
/*  77 */         if (osver.equals("6.0"))
/*     */         {
/*  79 */           variant = 1;
/*  80 */           System.out.println("[OS] Detected Windows Vista (" + osname + ", " + osver + ")");
/*     */         }
/*  82 */         else if (osver.equals("6.1"))
/*     */         {
/*  84 */           variant = 7;
/*  85 */           System.out.println("[OS] Detected Windows 7 (" + osname + ", " + osver + ")");
/*     */         }
/*     */         else
/*     */         {
/*  89 */           variant = 6;
/*  90 */           System.out.println("[OS] Detected Windows >= 8 (" + osname + ", " + osver + ")");
/*     */         }
/*     */       }
/*  93 */       else if ((osver.startsWith("3")) || 
/*  94 */         (osver.startsWith("4")) || 
/*  95 */         (osver.startsWith("5.0"))) {
/*  96 */         variant = 5;
/*  97 */         System.out.println("[OS] Detected Windows < XP  (" + osname + ", " + osver + ")");
/*     */       }
/* 101 */       else if (osname.contains("2003"))
/*     */       {
/* 103 */         variant = 4;
/* 104 */         System.out.println("[OS] Detected Windows 2003  (" + osname + ", " + osver + ")");
/*     */       }
/*     */       else
/*     */       {
/* 108 */         variant = 3;
/* 109 */         System.out.println("[OS] Detected Windows XP  (" + osname + ", " + osver + ")");
/*     */       }
/*     */ 
/*     */     }
/* 113 */     else if ((osname.indexOf("mac") != -1) || 
/* 114 */       (osname.indexOf("darwin") != -1)) {
/* 115 */       base_type = 1;
/* 116 */       variant = 2;
/* 117 */       System.out.println("[OS] Detected MacOS  (" + osname + ", " + osver + ")");
/* 118 */     } else if (osname.indexOf("lin") != -1) {
/* 119 */       base_type = 2;
/* 120 */       variant = 2;
/* 121 */       System.out.println("[OS] Detected Linux (" + osname + ", " + osver + ")");
/*     */     } else {
/* 123 */       base_type = 3;
/* 124 */       variant = 2;
/* 125 */       System.out.println("[OS] Detected Unknown OS (" + osname + ", " + osver + ")");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean isWindows()
/*     */   {
/* 133 */     detect();
/* 134 */     return base_type == 0;
/*     */   }
/*     */ 
/*     */   public static boolean isLinux()
/*     */   {
/* 141 */     detect();
/* 142 */     return base_type == 2;
/*     */   }
/*     */ 
/*     */   public static boolean isMacOS()
/*     */   {
/* 149 */     detect();
/* 150 */     return base_type == 1;
/*     */   }
/*     */ 
/*     */   public static boolean isWindowsVistaOrAbove()
/*     */   {
/* 157 */     detect();
/* 158 */     return (isWindows()) && (isVariantVistaOrAbove(variant));
/*     */   }
/*     */ 
/*     */   public static boolean isWindows8OrAbove() {
/* 162 */     detect();
/* 163 */     return (isWindows()) && (isVariantWindows8OrAbove(variant));
/*     */   }
/*     */ 
/*     */   public static boolean isWindowsXpOr2003()
/*     */   {
/* 170 */     detect();
/* 171 */     return (isWindows()) && ((variant == 3) || (variant == 4));
/*     */   }
/*     */ 
/*     */   public static boolean isWindows2003() {
/* 175 */     detect();
/* 176 */     return (isWindows()) && (variant == 4);
/*     */   }
/*     */ 
/*     */   public static boolean isWindowsPreXp()
/*     */   {
/* 184 */     detect();
/* 185 */     return (isWindows()) && (variant == 5);
/*     */   }
/*     */ 
/*     */   public static boolean isLinux64bit()
/*     */   {
/* 193 */     detect();
/*     */ 
/* 198 */     if (!isLinux()) {
/* 199 */       return false;
/*     */     }
/* 201 */     if (linux_arch == -1)
/*     */     {
/*     */       try
/*     */       {
/* 205 */         Process p = Runtime.getRuntime().exec("uname -a");
/* 206 */         p.getErrorStream().close();
/* 207 */         ByteArrayOutputStream bout = new ByteArrayOutputStream();
/* 208 */         InputStream in = p.getInputStream();
/*     */ 
/* 210 */         byte[] buf = new byte[1024];
/* 211 */         int n = 0;
/*     */ 
/* 213 */         while (n != -1) {
/* 214 */           n = in.read(buf);
/* 215 */           if (n > 0) {
/* 216 */             bout.write(buf, 0, n);
/*     */           }
/*     */         }
/*     */ 
/* 220 */         output = new String(bout.toByteArray());
/*     */       }
/*     */       catch (Exception x)
/*     */       {
/*     */         try
/*     */         {
/*     */           String output;
/* 225 */           Process p = Runtime.getRuntime().exec("arch");
/* 226 */           p.getErrorStream().close();
/* 227 */           ByteArrayOutputStream bout = new ByteArrayOutputStream();
/* 228 */           InputStream in = p.getInputStream();
/*     */ 
/* 230 */           byte[] buf = new byte[1024];
/* 231 */           int n = 0;
/*     */ 
/* 233 */           while (n != -1) {
/* 234 */             n = in.read(buf);
/* 235 */             if (n > 0) {
/* 236 */               bout.write(buf, 0, n);
/*     */             }
/*     */           }
/*     */ 
/* 240 */           output = new String(bout.toByteArray());
/*     */         }
/*     */         catch (Exception xx)
/*     */         {
/*     */           String output;
/* 243 */           System.out.println("[OS] Failed to detect Linux architecture: " + xx);
/* 244 */           output = null;
/* 245 */           linux_arch = 2;
/* 246 */           return true;
/*     */         }
/*     */       }
/*     */ 
/* 250 */       String output = output.toLowerCase();
/*     */ 
/* 252 */       if (output.indexOf("x86_64") != -1)
/* 253 */         linux_arch = 2;
/* 254 */       else if (output.indexOf("x86_64") != -1)
/* 255 */         linux_arch = 2;
/*     */       else {
/* 257 */         linux_arch = 1;
/*     */       }
/*     */     }
/*     */ 
/* 261 */     return linux_arch == 2;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 265 */     System.out.println("Is Windows: " + isWindows());
/* 266 */     System.out.println("Is Windows Vista or above: " + isWindowsVistaOrAbove());
/* 267 */     System.out.println("Is Linux: " + isLinux());
/* 268 */     if (isLinux()) {
/* 269 */       System.out.println("Is Linux 64-bit: " + isLinux64bit());
/*     */     }
/* 271 */     System.out.println("Is MacOS: " + isMacOS());
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.ostools.OS
 * JD-Core Version:    0.6.2
 */