/*      */ package jwrapper;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Image;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.math.BigInteger;
/*      */ import java.net.URL;
/*      */ import java.net.URLEncoder;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Date;
/*      */ import java.util.Properties;
/*      */ import java.util.zip.GZIPOutputStream;
/*      */ import javax.imageio.ImageIO;
/*      */ import javax.xml.parsers.DocumentBuilder;
/*      */ import javax.xml.parsers.DocumentBuilderFactory;
/*      */ import jwrapper.archive.Archive;
/*      */ import jwrapper.archive.FileStripper;
/*      */ import jwrapper.archive.LaunchableArchive;
/*      */ import jwrapper.filestrippers.CVSStripper;
/*      */ import jwrapper.filestrippers.JREStripper;
/*      */ import jwrapper.hidden.JWNativeAPI;
/*      */ import jwrapper.launch.JWCompiler;
/*      */ import jwrapper.lic.JWLicense;
/*      */ import jwrapper.lic.JWSplash;
/*      */ import jwrapper.osxwrapper.OSXWrapper;
/*      */ import jwrapper.updater.GenericUpdater;
/*      */ import jwrapper.updater.JWApp;
/*      */ import jwrapper.updater.LaunchFile;
/*      */ import jwrapper.updater.VersionUtil;
/*      */ import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
/*      */ import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
/*      */ import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
/*      */ import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
/*      */ import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ import utils.buildtools.common.signing.KeyStoreDetails;
/*      */ import utils.buildtools.osx.DmgInstallerBuilder;
/*      */ import utils.buildtools.osx.dsstore.DSConfig;
/*      */ import utils.buildtools.osx.dsstore.DSConfig.FileLocation;
/*      */ import utils.buildtools.osx.signer.CertificateInfo;
/*      */ import utils.buildtools.osx.signer.MacSigner;
/*      */ import utils.buildtools.win32.AuthenticodeSigner;
/*      */ import utils.encryption.rsa.RSADecryptor;
/*      */ import utils.encryption.rsa.RSAEncryptor;
/*      */ import utils.files.FileUtil;
/*      */ import utils.files.PathUtil;
/*      */ import utils.ostools.OS;
/*      */ import utils.ostools.win32.ExeReader;
/*      */ import utils.progtools.CheapTimingPrintStream;
/*      */ import utils.progtools.arrays.ArrayUtils;
/*      */ import utils.stream.CFriendlyStreamUtils;
/*      */ import utils.stream.NullOutputStream;
/*      */ import utils.stream.ProcessPrinter;
/*      */ import utils.stream.StreamUtils;
/*      */ import utils.stream.TinyWebServer;
/*      */ import utils.string.CharStack;
/*      */ import utils.string.StringReplace;
/*      */ import utils.string.WebBase64;
/*      */ import utils.swing.icons.ICNSWriter;
/*      */ import utils.swing.icons.ICNSWriter.ImageList;
/*      */ import utils.swing.icons.ICOWriter;
/*      */ import utils.swing.icons.ICOWriter.ImageList;
/*      */ import utils.swing.images.ImageHelper;
/*      */ import utils.sync.DualPrintstream;
/*      */ 
/*      */ public class JWrapperCompiler
/*      */ {
/*   89 */   public static final byte[] marker = { 
/*   90 */     29, 67, 42, 80, 91, 122, 31, 100, 35, 71, 33, 36, 23, 50, 60, 80, 33, 108, 4, 102, 41, 92, 41, 123, 11, 91, 25, 51, 47, 79, 
/*   91 */     15, 77, 39, 88, 24, 36, 21, 57, 78, 121, 43, 118, 21, 89, 61, 87, 73, 0, 17, 41, 5, 101, 43, 16, 112, 102, 94, 45, 64, 96, 
/*   92 */     117, 67, 48, 8, 97, 74, 116, 126, 64, 27, 83, 59, 86, 70, 55, 30, 75, 44, 114, 89, 48, 68, 58, 60, 47, 68, 120, 43, 38, 
/*   93 */     101, 113, 113, 57, 115, 37, 35, 16, 20, 81, 36, 49, 112, 16, 74, 13, 5, 87, 42, 6, 10, 83, 33, 77, 44, 19, 0, 54, 41, 26, 
/*   94 */     116, 71, 45, 26, 85, 44, 47, 66, 99 };
/*      */   static PrintStream always;
/*   99 */   static int PARAMS_BLOCK_LEN = 204800;
/*      */ 
/*  102 */   private static boolean buildMacOSWrappers = true;
/*  103 */   private static boolean buildLinuxWrappers = true;
/*  104 */   private static boolean buildWindowsWrappers = true;
/*      */ 
/*  106 */   private static boolean skipTimestamping = false;
/*      */ 
/*  321 */   static long time = 0L;
/*      */   private static long signingTime;
/*      */   private static long lzmaTime;
/* 3137 */   static byte[] buf = new byte[64000];
/*      */ 
/*      */   private static String getWrapperFor(String appElevation, boolean appUiAccess)
/*      */     throws Exception
/*      */   {
/*  109 */     if (appElevation.equalsIgnoreCase("None"))
/*  110 */       return "windowswrapper_noelevation.exe";
/*  111 */     if (appElevation.equalsIgnoreCase("AsInvoker")) {
/*  112 */       if (appUiAccess) {
/*  113 */         return "windowswrapper_asInvoker_0.exe";
/*      */       }
/*  115 */       return "windowswrapper_asInvoker_1.exe";
/*      */     }
/*  117 */     if (appElevation.equalsIgnoreCase("HighestAvailable")) {
/*  118 */       if (appUiAccess) {
/*  119 */         return "windowswrapper_highestAvailable_0.exe";
/*      */       }
/*  121 */       return "windowswrapper_highestAvailable_1.exe";
/*      */     }
/*  123 */     if (appElevation.equalsIgnoreCase("RequireAdministrator")) {
/*  124 */       if (appUiAccess) {
/*  125 */         return "windowswrapper_requireAdministrator_0.exe";
/*      */       }
/*  127 */       return "windowswrapper_requireAdministrator_1.exe";
/*      */     }
/*      */ 
/*  130 */     throw new Exception("Unrecognised elevation type: " + appElevation);
/*      */   }
/*      */ 
/*      */   private static RSAEncryptor createRsaEncryptor()
/*      */   {
/*  142 */     RSAEncryptor rsaEnc = new RSAEncryptor(
/*  143 */       new BigInteger[] { 
/*  144 */       new BigInteger(new byte[] { 
/*  145 */       0, -126, -92, -128, 105, -24, -21, 15, -79, 69, 30, 
/*  146 */       -7, 122, -127, -33, 123, 8, -31, 62, -39, -109, 
/*  147 */       42, 80, -41, -125, 85, -98, 18, 72, 33, -80, 
/*  148 */       110, 116, -35, -42, 63, 115, 29, -99, 57, 45, 
/*  149 */       127, 56, 62, -78, 70, -114, -57, -16, 118, -98, 
/*  150 */       -102, 97, 23, -80, 98, -104, -54, 86, -59, -95, 
/*  151 */       -31, -108, 118, -103, 16, -74, 44, 42, -44, -33, 
/*  152 */       -63, -128, 12, 32, 55, -70, 2, -92, 125, -46, 
/*  153 */       90, 80, -96, 39, 97, 84, -117, 49, 13, -48, 
/*  154 */       31, 68, -115, -16, -107, -103, -79, 82, -93, -106, 
/*  155 */       -86, -17, -46, 64, -51, 47, -27, 33, -52, -123, 
/*  156 */       38, -9, 106, -16, 125, 99, -90, 34, -109, 90, 
/*  157 */       58, -5, -57, -34, -20, -100, 4, 62, 12, -21, 
/*  158 */       -128, 65, -110, 90, 90, 92, -111, 23, -34, 89, 
/*  159 */       124, -11, 26, 8, 86, 100, 38, 14, -13, -98, 
/*  160 */       -34, 78, 82, 95, 62, 45, 68, -93, 80, -116, 
/*  161 */       86, -56, -37, 33, -42, 125, 76, -121, 62, -68, 
/*  162 */       93, -69, 91, -102, 77, 12, -109, -100, -51, -76, 
/*  163 */       110, 59, 8, -40, -12, 126, -108, -6, -18, -59, 
/*  164 */       -29, -94, 57, -10, 72, 14, -16, 48, -52, -19, 
/*  165 */       16, -111, 120, -102, 104, -81, 101, -65, 72, 40, 
/*  166 */       -56, -25, -117, 0, -2, 68, -71, 115, -89, -113, 
/*  167 */       -60, 77, 113, -76, 28, -117, -6, 72, -78, 87, 
/*  168 */       20, -1, -14, -127, -37, -30, -104, -29, -19, -95, 
/*  169 */       37, 68, -31, 67, -101, -5, -118, -3, -104, 39, 
/*  170 */       -72, -82, 63, -81, 85, 70, -128, 94, 22, -8, 
/*  171 */       103, 114, -96, -120, -101, -15, 22, -20, -56, 54, 
/*  172 */       125, -53, 120, -39, 9, 73, 4, -98, 111, 57, 
/*  173 */       -110, 40, 51, -90, 100, -76, 20, -117, 13, 4, 
/*  174 */       -99, -80, 124, -56, -98, 28, 52, 69, -111, 29, 
/*  175 */       46, -63, -88, -46, -71, -77, -20, 82, 33, 102, 
/*  176 */       84, -4, 58, 34, -6, -28, -90, 56, 40, -84, 
/*  177 */       -71, -55, -119, -91, 11, -76, 27, -127, 74, 21, 
/*  178 */       43, 16, -32, -13, 58, -13, 70, 77, 115, -8, 
/*  179 */       -41, -103, -121, 55, 35, 112, 0, 63, -18, 40, 
/*  180 */       -40, 126, 25, -40, -9, -87, -70, 20, -5, -24, 
/*  181 */       104, 50, -103, 19, 20, -34, -46, 21, -121, 120, 
/*  182 */       27, -74, 12, 74, -12, -29, 50, -98, 44, 31, 
/*  183 */       31, 51, 124, -2, 103, 20, 64, -80, -83, 95, 
/*  184 */       118, 121 }), 
/*  185 */       new BigInteger(new byte[] { 62, 
/*  186 */       123, 50, 72, -23, -44, -104, 51, 99, -41, 41, 
/*  187 */       103, 28, -72, -53, -119, -49, -26, 104, 14, -69, 
/*  188 */       49, -53, 62, -49, -25, 108, -22, -40, 117, -59, 
/*  189 */       -123, -50, 69, 19, 55, 14, 41, -51, 110, -51, 
/*  190 */       -118, 52, 63, 0, 90, -116, 37, 23, 86, -5, 
/*  191 */       -21, -89, 39, -42, 28, -115, 74, -28, 21, -60, 
/*  192 */       -19, -11, -16, 41, 98, 65, -91, 46, 40, 59, 
/*  193 */       38, -6, -85, 60, 11, 12, 100, -18, 67, 54, 
/*  194 */       83, 20, -15, 113, 84, -12, -88, 39, -1, 92, 
/*  195 */       -34, 1, 26, 4, -61, -16, -95, -11, 49, -52, 
/*  196 */       47, -22, 30, -3, -11, -125, -40, -125, 52, -115, 
/*  197 */       18, 40, 3, -74, 103, 79, 116, -75, -58, -6, 
/*  198 */       -47, 117, -39, -21, -104, -121, -104, 28, 112, -95, 
/*  199 */       -125, -120, -57, 9, -45, 58, 67, 17, 76, 48, 
/*  200 */       -95, -66, -119, -115, 125, -49, 107, 83, 31, 117, 
/*  201 */       115, 95, 12, 40, -35, -1, 111, 127, -111, 8, 
/*  202 */       29, 71, 105, 58, 15, 103, 98, 18, -33, -89, 
/*  203 */       67, 88, 84, -19, 50, -119, 97, 64, -3, 63, 
/*  204 */       -39, 115, -119, 39, 4, -29, 19, -42, 94, -92, 
/*  205 */       -123, 61, 28, -66, 73, 59, -75, -103, -16, -69, 
/*  206 */       -93, -64, -88, 114, 36, 104, -23, 59, -34, 66, 
/*  207 */       -108, -13, 67, 35, 105, -126, 88, 60, -54, 121, 
/*  208 */       36, 93, -22, -11, -5, -7, 70, 13, 54, 82, 
/*  209 */       -37, -47, -61, -89, 42, 10, -101, -78, 101, 13, 
/*  210 */       22, -15, -33, -46, -120, -24, 11, 82, -71, -24, 
/*  211 */       0, 99, -120, -63, 108, 48, -63, -3, -9, -11, 
/*  212 */       -120, 115, -128, 124, 91, -18, 10, -96, 105, -71, 
/*  213 */       65, 105, -102, -54, 71, -92, 112, -66, 42, 36, 
/*  214 */       -90, -91, 29, -36, -98, 81, 48, 82, 109, 24, 
/*  215 */       -75, 48, -40, 79, -58, -5, -39, 32, -46, 51, 
/*  216 */       -73, 73, 105, -86, -121, 2, -89, 103, 19, 1, 
/*  217 */       25, -80, 92, -43, -2, -1, 31, -86, -81, 6, 
/*  218 */       112, 52, 38, 44, 44, -112, -114, 40, -125, 50, 
/*  219 */       -62, 10, 26, -113, -53, -49, -125, -126, -47, -90, 
/*  220 */       0, -126, 47, 85, 91, -22, 17, 50, 43, 95, 
/*  221 */       108, -94, -86, -96, -31, -66, 69, -117, -115, 11, 
/*  222 */       104, 40, 63, -57, -13, 57, 69, -108, 42, -107, 
/*  223 */       116, -61, 127, 120, 17, -69, -81, 73, 71, 48, 
/*  224 */       -32, 105, -97, 73, -38, 84, -116, 93, -29, -81, 
/*  225 */       123 }) });
/*      */ 
/*  228 */     return rsaEnc;
/*      */   }
/*      */ 
/*      */   private static RSADecryptor createRsaDecryptor() {
/*  232 */     RSADecryptor rsaDec = new RSADecryptor(
/*  233 */       new BigInteger[] { 
/*  234 */       new BigInteger(new byte[] { 
/*  235 */       0, -126, -92, -128, 105, -24, -21, 15, -79, 69, 30, 
/*  236 */       -7, 122, -127, -33, 123, 8, -31, 62, -39, -109, 
/*  237 */       42, 80, -41, -125, 85, -98, 18, 72, 33, -80, 
/*  238 */       110, 116, -35, -42, 63, 115, 29, -99, 57, 45, 
/*  239 */       127, 56, 62, -78, 70, -114, -57, -16, 118, -98, 
/*  240 */       -102, 97, 23, -80, 98, -104, -54, 86, -59, -95, 
/*  241 */       -31, -108, 118, -103, 16, -74, 44, 42, -44, -33, 
/*  242 */       -63, -128, 12, 32, 55, -70, 2, -92, 125, -46, 
/*  243 */       90, 80, -96, 39, 97, 84, -117, 49, 13, -48, 
/*  244 */       31, 68, -115, -16, -107, -103, -79, 82, -93, -106, 
/*  245 */       -86, -17, -46, 64, -51, 47, -27, 33, -52, -123, 
/*  246 */       38, -9, 106, -16, 125, 99, -90, 34, -109, 90, 
/*  247 */       58, -5, -57, -34, -20, -100, 4, 62, 12, -21, 
/*  248 */       -128, 65, -110, 90, 90, 92, -111, 23, -34, 89, 
/*  249 */       124, -11, 26, 8, 86, 100, 38, 14, -13, -98, 
/*  250 */       -34, 78, 82, 95, 62, 45, 68, -93, 80, -116, 
/*  251 */       86, -56, -37, 33, -42, 125, 76, -121, 62, -68, 
/*  252 */       93, -69, 91, -102, 77, 12, -109, -100, -51, -76, 
/*  253 */       110, 59, 8, -40, -12, 126, -108, -6, -18, -59, 
/*  254 */       -29, -94, 57, -10, 72, 14, -16, 48, -52, -19, 
/*  255 */       16, -111, 120, -102, 104, -81, 101, -65, 72, 40, 
/*  256 */       -56, -25, -117, 0, -2, 68, -71, 115, -89, -113, 
/*  257 */       -60, 77, 113, -76, 28, -117, -6, 72, -78, 87, 
/*  258 */       20, -1, -14, -127, -37, -30, -104, -29, -19, -95, 
/*  259 */       37, 68, -31, 67, -101, -5, -118, -3, -104, 39, 
/*  260 */       -72, -82, 63, -81, 85, 70, -128, 94, 22, -8, 
/*  261 */       103, 114, -96, -120, -101, -15, 22, -20, -56, 54, 
/*  262 */       125, -53, 120, -39, 9, 73, 4, -98, 111, 57, 
/*  263 */       -110, 40, 51, -90, 100, -76, 20, -117, 13, 4, 
/*  264 */       -99, -80, 124, -56, -98, 28, 52, 69, -111, 29, 
/*  265 */       46, -63, -88, -46, -71, -77, -20, 82, 33, 102, 
/*  266 */       84, -4, 58, 34, -6, -28, -90, 56, 40, -84, 
/*  267 */       -71, -55, -119, -91, 11, -76, 27, -127, 74, 21, 
/*  268 */       43, 16, -32, -13, 58, -13, 70, 77, 115, -8, 
/*  269 */       -41, -103, -121, 55, 35, 112, 0, 63, -18, 40, 
/*  270 */       -40, 126, 25, -40, -9, -87, -70, 20, -5, -24, 
/*  271 */       104, 50, -103, 19, 20, -34, -46, 21, -121, 120, 
/*  272 */       27, -74, 12, 74, -12, -29, 50, -98, 44, 31, 
/*  273 */       31, 51, 124, -2, 103, 20, 64, -80, -83, 95, 
/*  274 */       118, 121 }), 
/*  275 */       new BigInteger(new byte[] { 23 }) });
/*      */ 
/*  279 */     return rsaDec;
/*      */   }
/*      */ 
/*      */   public static void signJar(File jardest, String jarSignerPath, KeyStoreDetails winSignKeystore, String winSignKeystoreType, String winSignTimestampURL) throws IOException, InterruptedException {
/*  283 */     ArrayList jarsign = new ArrayList();
/*  284 */     jarsign.add(jarSignerPath);
/*      */ 
/*  286 */     jarsign.add("-keystore");
/*  287 */     jarsign.add(winSignKeystore.getKeystoreLocation().getCanonicalFile().getAbsolutePath());
/*      */ 
/*  289 */     jarsign.add("-storetype");
/*  290 */     jarsign.add(winSignKeystoreType);
/*      */ 
/*  292 */     jarsign.add("-storepass");
/*  293 */     jarsign.add(winSignKeystore.getPassword());
/*      */ 
/*  295 */     jarsign.add("-keypass");
/*  296 */     jarsign.add(winSignKeystore.getPassword());
/*      */ 
/*  298 */     if (winSignTimestampURL != null) {
/*  299 */       jarsign.add("-tsa");
/*  300 */       jarsign.add(winSignTimestampURL);
/*      */     }
/*      */ 
/*  303 */     jarsign.add(jardest.getCanonicalFile().getAbsolutePath());
/*  304 */     jarsign.add(winSignKeystore.getAlias());
/*      */ 
/*  306 */     String[] args = new String[jarsign.size()];
/*  307 */     jarsign.toArray(args);
/*      */ 
/*  309 */     always.println(Arrays.toString(args));
/*      */ 
/*  311 */     Process p = Runtime.getRuntime().exec(args);
/*  312 */     new ProcessPrinter(p, always, always);
/*      */ 
/*  314 */     int N = p.waitFor();
/*  315 */     if (N != 0) {
/*  316 */       always.println("ERROR: Jarsigner failed with exit code " + N);
/*  317 */       System.exit(1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static long time()
/*      */   {
/*  329 */     long current = System.currentTimeMillis();
/*  330 */     long diff = current - time;
/*  331 */     time = current;
/*  332 */     return diff;
/*      */   }
/*      */ 
/*      */   public static void fail(String s)
/*      */   {
/*  365 */     always.println("**********************************");
/*  366 */     always.println("*          BUILD FAILED          *");
/*  367 */     always.println("**********************************");
/*  368 */     always.println("** Reason: " + s);
/*  369 */     System.exit(1);
/*      */   }
/*      */   public static void fail(Throwable x) {
/*  372 */     always.println("**********************************");
/*  373 */     always.println("*          BUILD FAILED          *");
/*  374 */     always.println("**********************************");
/*  375 */     always.println("** Reason: (see stacktrace below)\n");
/*  376 */     x.printStackTrace(always);
/*  377 */     System.exit(1);
/*      */   }
/*      */ 
/*      */   public static void main(String[] args) throws Exception
/*      */   {
/*      */     try
/*      */     {
/*  384 */       boolean fullBuild = true;
/*  385 */       if ((args.length > 1) && (args[1].toString().toLowerCase().equals("false"))) {
/*  386 */         fullBuild = false;
/*      */       }
/*  388 */       boolean simulateBrokenGU = false;
/*      */ 
/*  391 */       always = new PrintStream(new FileOutputStream(FileDescriptor.out), true);
/*      */ 
/*  394 */       always.println("JWrapperCompiler started");
/*      */ 
/*  397 */       System.setOut(new PrintStream(new NullOutputStream()));
/*      */ 
/*  399 */       while (args.length > 0) {
/*  400 */         String s = args[0];
/*  401 */         if (s.equals("-debug"))
/*      */         {
/*  403 */           utils.files.ZipUtils.VERBOSE = true;
/*  404 */           ExeReader.VERBOSE_STRUCTURE = true;
/*      */ 
/*  406 */           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
/*      */ 
/*  408 */           File logfile = new File("jwcompiler-debug-" + sdf.format(new Date()) + ".log");
/*      */ 
/*  411 */           PrintStream fsout = new PrintStream(new CheapTimingPrintStream(FileUtil.wopen(logfile)), true);
/*  412 */           PrintStream stdout = new PrintStream(new FileOutputStream(FileDescriptor.out), true);
/*      */ 
/*  415 */           System.setOut(fsout);
/*  416 */           System.setErr(fsout);
/*      */ 
/*  419 */           DualPrintstream dp = new DualPrintstream();
/*  420 */           dp.addThroughStream(fsout);
/*  421 */           dp.addThroughStream(stdout);
/*      */ 
/*  423 */           always = new PrintStream(dp.addOutputStream());
/*      */ 
/*  425 */           System.out.println("[Debugging] Debug log set up, logging to " + logfile);
/*      */ 
/*  427 */           args = ArrayUtils.popFirst(args);
/*      */         }
/*  429 */         else if (s.equals("-simulateBrokenGU")) {
/*  430 */           simulateBrokenGU = true;
/*      */ 
/*  432 */           args = ArrayUtils.popFirst(args);
/*      */         }
/*  434 */         else if (s.equals("-runTestServer")) {
/*  435 */           JWCompiler.CMDOPTION_DEMO = true;
/*      */ 
/*  437 */           args = ArrayUtils.popFirst(args);
/*      */         } else {
/*  439 */           if (!s.equals("-testNewStuff")) break;
/*  440 */           always.println("Testing new dev stuff...");
/*  441 */           if (OS.isWindows()) {
/*  442 */             JWNativeAPI.loadLibraryFrom(new File("wrappers"));
/*  443 */             always.println("[Broken] User Start Menu/Programs folder: " + JWNativeAPI.getInstance().getWindowsUserProgramsFolder());
/*  444 */             always.println("[Broken] All Start Menu/Programs folder: " + JWNativeAPI.getInstance().getWindowsAllProgramsFolder());
/*      */           }
/*  446 */           always.flush();
/*      */ 
/*  448 */           System.exit(0);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  454 */       if (args.length == 0) {
/*  455 */         always.println("Usage: JWrapperCompiler <JW XML File>");
/*  456 */         new UpdateCheck().run();
/*      */ 
/*  458 */         System.exit(1);
/*      */       }
/*      */ 
/*  461 */       new UpdateCheck().start();
/*      */ 
/*  463 */       skipTimestamping = !fullBuild;
/*      */ 
/*  465 */       System.out.println("fullBuild is set to " + fullBuild);
/*      */ 
/*  467 */       String xmlfile = args[0];
/*      */ 
/*  469 */       always.println("[Config] Building file is " + xmlfile);
/*      */ 
/*  474 */       DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
/*      */       try
/*      */       {
/*  477 */         doc = builder.parse(new BufferedInputStream(new FileInputStream(xmlfile)));
/*      */       }
/*      */       catch (IOException x)
/*      */       {
/*      */         Document doc;
/*  479 */         throw new FailException("Unable to read input XML file " + xmlfile + " - " + x);
/*      */       }
/*      */       Document doc;
/*  482 */       Element root = doc.getDocumentElement();
/*      */       try
/*      */       {
/*  485 */         bundleName = root.getElementsByTagName("BundleName").item(0).getTextContent();
/*      */       }
/*      */       catch (NullPointerException x)
/*      */       {
/*      */         String bundleName;
/*  487 */         throw new FailException("Missing required BundleName tag");
/*      */       }String bundleName;
/*      */       String shortBundleName;
/*      */       try { shortBundleName = root.getElementsByTagName("ShortBundleName").item(0).getTextContent(); }
/*      */       catch (Exception x)
/*      */       {
/*      */         String shortBundleName;
/*  493 */         shortBundleName = bundleName;
/*      */       }
/*      */       try
/*      */       {
/*  497 */         splashPNG = root.getElementsByTagName("SplashPNG").item(0).getTextContent();
/*      */       }
/*      */       catch (NullPointerException x)
/*      */       {
/*      */         String splashPNG;
/*  499 */         throw new FailException("Missing required SplashPNG tag");
/*      */       }
/*  501 */       String dmgSplashSize = null;
/*  502 */       long dmgSplashSizeBytes = 0L;
/*      */       try
/*      */       {
/*  506 */         dmgSplashSize = root.getElementsByTagName("PadDMGSplashToBytes").item(0).getTextContent();
/*      */         try
/*      */         {
/*  509 */           dmgSplashSizeBytes = Long.parseLong(dmgSplashSize);
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*  513 */           t.printStackTrace();
/*      */         }
/*      */       }
/*      */       catch (NullPointerException localNullPointerException1)
/*      */       {
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  522 */         bundleLogoPNG = root.getElementsByTagName("BundleLogoPNG").item(0).getTextContent();
/*      */       }
/*      */       catch (NullPointerException x)
/*      */       {
/*      */         String bundleLogoPNG;
/*  524 */         throw new FailException("Missing required BundleLogoPNG tag");
/*      */       }
/*      */ 
/*  527 */       boolean deleteElevationFiles = root.getElementsByTagName("KeepElevationFiles").getLength() == 0;
/*      */       try
/*      */       {
/*  544 */         windows32JRE = root.getElementsByTagName("Windows32JRE").item(0).getTextContent();
/*      */       }
/*      */       catch (NullPointerException x)
/*      */       {
/*      */         String windows32JRE;
/*  546 */         throw new FailException("Missing required Windows32JRE tag");
/*      */       }
/*      */       try
/*      */       {
/*  550 */         windows64JRE = root.getElementsByTagName("Windows64JRE").item(0).getTextContent();
/*      */       }
/*      */       catch (NullPointerException x)
/*      */       {
/*      */         String windows64JRE;
/*  552 */         throw new FailException("Missing required Windows64JRE tag");
/*      */       }
/*      */       try
/*      */       {
/*  556 */         linux32JRE = root.getElementsByTagName("Linux32JRE").item(0).getTextContent();
/*      */       }
/*      */       catch (NullPointerException x)
/*      */       {
/*      */         String linux32JRE;
/*  558 */         throw new FailException("Missing required Linux32JRE tag");
/*      */       }
/*      */       try
/*      */       {
/*  562 */         linux64JRE = root.getElementsByTagName("Linux64JRE").item(0).getTextContent();
/*      */       }
/*      */       catch (NullPointerException x)
/*      */       {
/*      */         String linux64JRE;
/*  564 */         throw new FailException("Missing required Linux64JRE tag");
/*      */       }
/*      */       try
/*      */       {
/*  568 */         if (bundleName.equals("Remote Support")) {
/*  569 */           HeadlessOsxUtil.setOSXAppName("Porthos! (" + bundleName + ")");
/*      */         }
/*  571 */         else if (bundleName.equals("SimpleHelp Technician")) {
/*  572 */           HeadlessOsxUtil.setOSXAppName("Aramis! (" + bundleName + ")");
/*      */         }
/*  574 */         else if (bundleName.equals("Remote Access")) {
/*  575 */           HeadlessOsxUtil.setOSXAppName("Athos! (" + bundleName + ")");
/*      */         }
/*      */         else {
/*  578 */           HeadlessOsxUtil.setOSXAppName("JWrapper Compiling '" + bundleName + "'");
/*      */         }
/*      */ 
/*  581 */         File logoFile = new File(PathUtil.makePathNative(bundleLogoPNG));
/*      */ 
/*  583 */         File doodad = new File(new File("wrappers"), "jwlogo64.png");
/*      */ 
/*  585 */         Image logo = ImageIO.read(logoFile);
/*      */ 
/*  587 */         if (!doodad.exists()) {
/*  588 */           HeadlessOsxUtil.setOSXAppDockImage(logo);
/*      */         } else {
/*  590 */           Image overlay = ImageIO.read(doodad);
/*  591 */           logo = ImageHelper.createOverlay(logo, overlay, true, true, 0.4D, true);
/*  592 */           HeadlessOsxUtil.setOSXAppDockImage(logo);
/*      */         }
/*      */       }
/*      */       catch (Throwable localThrowable1) {
/*      */       }
/*  597 */       String buildDir = "build";
/*      */       try
/*      */       {
/*  600 */         buildDir = root.getElementsByTagName("BuildOutputFolder").item(0).getTextContent();
/*      */       }
/*      */       catch (Exception localException1) {
/*      */       }
/*  604 */       File build = new File(buildDir);
/*  605 */       build.mkdirs();
/*      */ 
/*  607 */       ArrayList apps = new ArrayList();
/*  608 */       NodeList nl = root.getElementsByTagName("App");
/*  609 */       for (int i = 0; i < nl.getLength(); i++) {
/*  610 */         Element el = (Element)nl.item(i);
/*      */ 
/*  612 */         JWApp app = new JWApp();
/*      */         try {
/*  614 */           app.name = el.getElementsByTagName("Name").item(0).getTextContent();
/*      */         } catch (NullPointerException x) {
/*  616 */           throw new FailException("App tag is missing required Name tag");
/*      */         }
/*      */ 
/*  619 */         app.userAccessible = true;
/*      */         try {
/*  621 */           app.userAccessible = el.getElementsByTagName("UserAccessible").item(0).getTextContent().equalsIgnoreCase("true");
/*      */         }
/*      */         catch (Exception localException2) {
/*      */         }
/*      */         try {
/*  626 */           logoPNG = PathUtil.makePathNative(el.getElementsByTagName("LogoPNG").item(0).getTextContent());
/*      */         }
/*      */         catch (NullPointerException x)
/*      */         {
/*      */           String logoPNG;
/*  628 */           throw new FailException("App tag is missing required LogoPNG tag");
/*      */         }
/*      */         String logoPNG;
/*  659 */         File png = new File(logoPNG);
/*      */ 
/*  661 */         ICOWriter.ImageList list = new ICOWriter.ImageList();
/*  662 */         list.image16 = png;
/*  663 */         list.image24 = png;
/*  664 */         list.image32 = png;
/*  665 */         list.image48 = png;
/*  666 */         list.image256 = png;
/*      */ 
/*  668 */         File outico = new File(build, "out.ico");
/*      */ 
/*  670 */         int[] pngInfo = ICOWriter.writeIcoFile(outico, list);
/*      */ 
/*  672 */         app.logoICO = FileUtil.readFile(outico.getAbsolutePath());
/*      */ 
/*  674 */         app.pngIndex = pngInfo[0];
/*  675 */         app.pngLen = pngInfo[1];
/*      */ 
/*  677 */         outico.delete();
/*      */         try
/*      */         {
/*  714 */           app.mainClass = el.getElementsByTagName("MainClass").item(0).getTextContent();
/*      */         } catch (NullPointerException x) {
/*  716 */           throw new FailException("App tag is missing required MainClass tag");
/*      */         }
/*      */ 
/*  719 */         if (app.name.indexOf('-') != -1) {
/*  720 */           throw new Exception("Dashes '-' not allowed in application name");
/*      */         }
/*      */ 
/*  723 */         NodeList alist = el.getElementsByTagName("Param");
/*  724 */         for (int k = 0; k < alist.getLength(); k++) {
/*  725 */           app.args.add(alist.item(k).getTextContent());
/*      */         }
/*      */ 
/*  728 */         apps.add(app);
/*      */       }
/*      */ 
/*  731 */       if (apps.size() == 0) {
/*  732 */         throw new FailException("No apps specified (<App> tags)");
/*      */       }
/*      */ 
/*  735 */       boolean AutoTesting = false;
/*      */       try {
/*  737 */         AutoTesting = root.getElementsByTagName("AutoTesting").item(0).getTextContent().equalsIgnoreCase("true");
/*      */       } catch (Exception localException3) {
/*      */       }
/*  740 */       boolean mustFork = false;
/*      */       try {
/*  742 */         mustFork = root.getElementsByTagName("MustFork").item(0).getTextContent().equalsIgnoreCase("true");
/*      */       } catch (Exception localException4) {
/*      */       }
/*  745 */       boolean noStripJREs = false;
/*      */       try {
/*  747 */         noStripJREs = root.getElementsByTagName("NoStripJREs").item(0).getTextContent().equalsIgnoreCase("true");
/*      */       } catch (Exception localException5) {
/*      */       }
/*  750 */       CertificateInfo macCertInfo = null;
/*  751 */       String macSignID = null;
/*      */       try
/*      */       {
/*  754 */         Element macSign = (Element)root.getElementsByTagName("SignForMac").item(0);
/*  755 */         CertificateInfo info = new CertificateInfo();
/*  756 */         info.appleIncRootCertficate = new File(macSign.getElementsByTagName("AppleRootCertificate").item(0).getTextContent());
/*  757 */         info.developerIDCACertificate = new File(macSign.getElementsByTagName("DeveloperIdCertificate").item(0).getTextContent());
/*  758 */         info.developerIDp12 = new File(macSign.getElementsByTagName("DeveloperIdP12").item(0).getTextContent());
/*  759 */         info.developerIDAlias = macSign.getElementsByTagName("DeveloperIdAlias").item(0).getTextContent();
/*  760 */         info.developerIDPassword = macSign.getElementsByTagName("DeveloperIdPassword").item(0).getTextContent();
/*  761 */         macCertInfo = info;
/*      */ 
/*  763 */         macSignID = info.developerIDAlias;
/*      */       }
/*      */       catch (Exception localException6)
/*      */       {
/*      */       }
/*      */ 
/*  790 */       KeyStoreDetails winSignKeystore = null;
/*  791 */       String winSignKeystoreType = "JKS";
/*  792 */       String winSignTimestampURL = null;
/*  793 */       String appletSignTimestampURL = null;
/*  794 */       String jarSignerPath = null;
/*      */       try {
/*  796 */         Element sign = (Element)root.getElementsByTagName("SignForWindowsAndApplet").item(0);
/*  797 */         if (sign == null) throw new NullPointerException("No SignForWindowsAndApplet tag");
/*      */ 
/*      */         try
/*      */         {
/*  801 */           keystore = sign.getElementsByTagName("KeyStore").item(0).getTextContent();
/*      */         }
/*      */         catch (NullPointerException x)
/*      */         {
/*      */           String keystore;
/*  803 */           throw new FailException("SignForWindowsAndApplet tag is missing required KeyStore tag");
/*      */         }String keystore;
/*      */         try {
/*  806 */           winSignKeystoreType = sign.getElementsByTagName("KeyStoreType").item(0).getTextContent();
/*      */         }
/*      */         catch (Exception localException7) {
/*      */         }
/*      */         try {
/*  811 */           alias = sign.getElementsByTagName("Alias").item(0).getTextContent();
/*      */         }
/*      */         catch (NullPointerException x)
/*      */         {
/*      */           String alias;
/*  813 */           throw new FailException("SignForWindowsAndApplet tag is missing required Alias tag");
/*      */         }
/*      */         String alias;
/*      */         try {
/*  817 */           password = sign.getElementsByTagName("Password").item(0).getTextContent();
/*      */         }
/*      */         catch (NullPointerException x)
/*      */         {
/*      */           String password;
/*  819 */           throw new FailException("SignForWindowsAndApplet tag is missing required Password tag");
/*      */         }
/*      */         String password;
/*  822 */         if (!skipTimestamping)
/*      */         {
/*      */           try {
/*  825 */             winSignTimestampURL = sign.getElementsByTagName("WinTimestampURL").item(0).getTextContent();
/*      */           }
/*      */           catch (Exception localException8) {
/*      */           }
/*      */           try {
/*  830 */             appletSignTimestampURL = sign.getElementsByTagName("AppletTimestampURL").item(0).getTextContent();
/*      */           }
/*      */           catch (Exception localException9) {
/*      */           }
/*      */         }
/*      */         else {
/*  836 */           winSignTimestampURL = null;
/*  837 */           appletSignTimestampURL = null;
/*      */         }
/*      */         try
/*      */         {
/*  841 */           NodeList list = sign.getElementsByTagName("JarSignerPath");
/*  842 */           for (int i = 0; i < list.getLength(); i++) {
/*  843 */             String tmp = list.item(i).getTextContent();
/*      */ 
/*  845 */             if (new File(tmp).exists()) {
/*  846 */               jarSignerPath = tmp;
/*      */             }
/*      */           }
/*      */ 
/*  850 */           if ((list.getLength() > 0) && (jarSignerPath == null)) {
/*  851 */             always.println("WARNING: none of the jarsigner paths specified existed on this system");
/*      */           }
/*      */         }
/*      */         catch (Exception localException10)
/*      */         {
/*      */         }
/*  857 */         winSignKeystore = new KeyStoreDetails(new File(keystore), alias, password.toCharArray());
/*      */       }
/*      */       catch (FailException x) {
/*  860 */         throw x;
/*      */       }
/*      */       catch (Exception localException11)
/*      */       {
/*      */       }
/*      */       String appElevation;
/*      */       try {
/*  867 */         appElevation = root.getElementsByTagName("WindowsElevation").item(0).getTextContent();
/*      */       }
/*      */       catch (NullPointerException x)
/*      */       {
/*      */         String appElevation;
/*  869 */         appElevation = "None";
/*      */       }
/*  871 */       boolean appUiAccess = false;
/*      */       try {
/*  873 */         appUiAccess = root.getElementsByTagName("WindowsElevationUiAccess").item(0).getTextContent().trim().equalsIgnoreCase("true");
/*      */       }
/*      */       catch (Exception localException12) {
/*      */       }
/*      */       try {
/*  878 */         installType = root.getElementsByTagName("InstallType").item(0).getTextContent();
/*      */       }
/*      */       catch (NullPointerException x)
/*      */       {
/*      */         String installType;
/*  880 */         throw new FailException("Missing required InstallType tag");
/*      */       }
/*      */       String installType;
/*  883 */       if (installType.equalsIgnoreCase("AllUsers")) {
/*  884 */         installType = "perm_all";
/*      */       }
/*  886 */       if (installType.equalsIgnoreCase("CurrentUser")) {
/*  887 */         installType = "perm_user";
/*      */       }
/*  889 */       if (installType.equalsIgnoreCase("NoInstall")) {
/*  890 */         installType = "temp_user";
/*      */       }
/*      */ 
/*  896 */       String splashPNG = PathUtil.makePathNative(splashPNG);
/*  897 */       String bundleLogoPNG = PathUtil.makePathNative(bundleLogoPNG);
/*      */ 
/*  899 */       String windows32JRE = PathUtil.makePathNative(windows32JRE);
/*  900 */       String windows64JRE = PathUtil.makePathNative(windows64JRE);
/*  901 */       String linux32JRE = PathUtil.makePathNative(linux32JRE);
/*  902 */       String linux64JRE = PathUtil.makePathNative(linux64JRE);
/*      */ 
/*  904 */       String supportedLanguages = "en";
/*      */       try {
/*  906 */         supportedLanguages = root.getElementsByTagName("SupportedLanguages").item(0).getTextContent();
/*  907 */         supportedLanguages = supportedLanguages.trim();
/*      */       }
/*      */       catch (Exception localException13)
/*      */       {
/*      */       }
/*      */ 
/*  916 */       String failURL = "http://0.0.254.254/";
/*      */ 
/*  918 */       boolean haveUpdateURL = false;
/*  919 */       String updateURL = failURL;
/*      */       try {
/*  921 */         updateURL = root.getElementsByTagName("UpdateURL").item(0).getTextContent();
/*  922 */         haveUpdateURL = true;
/*  923 */         if (updateURL.endsWith("/"))
/*  924 */           updateURL = updateURL.substring(0, updateURL.length() - 1);
/*      */       }
/*      */       catch (Exception localException14) {
/*      */       }
/*  928 */       boolean dynamicUpdateURL = false;
/*      */       try {
/*  930 */         dynamicUpdateURL = root.getElementsByTagName("DynamicUpdateURL").item(0).getTextContent().equalsIgnoreCase("true");
/*  931 */         if (dynamicUpdateURL) {
/*  932 */           haveUpdateURL = true;
/*  933 */           updateURL = "D";
/*      */         }
/*      */       } catch (Exception localException15) {
/*      */       }
/*  937 */       boolean matchClientVersionToServerVersion = false;
/*      */       try {
/*  939 */         matchClientVersionToServerVersion = root.getElementsByTagName("MatchClientVersionToServerVersion").item(0).getTextContent().equalsIgnoreCase("true");
/*      */       }
/*      */       catch (Exception localException16) {
/*      */       }
/*  943 */       String mainClassOnUpdate = null;
/*      */       try {
/*  945 */         mainClassOnUpdate = root.getElementsByTagName("MainClassOnUpdate").item(0).getTextContent();
/*      */       } catch (Exception localException17) {
/*      */       }
/*  948 */       String mainClassVerifyJRE = null;
/*      */       try {
/*  950 */         mainClassVerifyJRE = root.getElementsByTagName("MainClassVerifyJRE").item(0).getTextContent();
/*      */       } catch (Exception localException18) {
/*      */       }
/*  953 */       String mainClassPostInstall = null;
/*      */       try {
/*  955 */         mainClassPostInstall = root.getElementsByTagName("MainClassPostInstall").item(0).getTextContent();
/*      */       } catch (Exception localException19) {
/*      */       }
/*  958 */       String mainClassPreUninstall = null;
/*      */       try {
/*  960 */         mainClassPreUninstall = root.getElementsByTagName("MainClassPreUninstall").item(0).getTextContent();
/*      */       } catch (Exception localException20) {
/*      */       }
/*  963 */       int splashMinMS = 850;
/*      */       try {
/*  965 */         splashMinMS = Integer.parseInt(root.getElementsByTagName("SplashMinTimeMS").item(0).getTextContent().trim());
/*      */       } catch (NumberFormatException x) {
/*  967 */         throw new Exception("Invalid SplashMinTimeMS");
/*      */       } catch (Exception localException21) {
/*      */       }
/*  970 */       boolean canOverrideSplash = false;
/*      */       try {
/*  972 */         canOverrideSplash = Boolean.parseBoolean(root.getElementsByTagName("CanOverrideSplash").item(0).getTextContent().trim());
/*      */       } catch (NumberFormatException x) {
/*  974 */         throw new Exception("Invalid CanOverrideSplash");
/*      */       }
/*      */       catch (Exception localException22) {
/*      */       }
/*  978 */       int tgzCompression = 0;
/*      */ 
/*  986 */       boolean paramZeroUpdateUrl = false;
/*      */ 
/*  992 */       ArrayList jvmOptions = new ArrayList();
/*      */       try {
/*  994 */         Element opts = (Element)root.getElementsByTagName("JvmOptions").item(0);
/*  995 */         NodeList list = opts.getElementsByTagName("JvmOption");
/*  996 */         for (int i = 0; i < list.getLength(); i++) {
/*  997 */           String tmp = list.item(i).getTextContent();
/*      */ 
/*  999 */           jvmOptions.add(tmp.trim());
/*      */         }
/*      */       }
/*      */       catch (Exception localException23) {
/*      */       }
/* 1004 */       String silentInstallParameter = null;
/*      */       try
/*      */       {
/* 1007 */         silentInstallParameter = root.getElementsByTagName("SilentInstallSwitch").item(0).getTextContent();
/*      */ 
/* 1010 */         if ((silentInstallParameter != null) && (silentInstallParameter.length() == 0)) {
/* 1011 */           silentInstallParameter = null;
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (Exception localException24)
/*      */       {
/*      */       }
/*      */ 
/* 1030 */       for (int i = 0; i < apps.size(); i++) {
/* 1031 */         always.println("[App " + (i + 1) + " of " + apps.size() + "] " + apps.get(i));
/*      */       }
/*      */ 
/* 1035 */       always.println("[Config] Build Folder: " + build);
/* 1036 */       if (dynamicUpdateURL)
/* 1037 */         always.println("[Config] UpdateURL: (dynamic)");
/* 1038 */       else if (!updateURL.equals(failURL))
/* 1039 */         always.println("[Config] UpdateURL: " + updateURL);
/*      */       else {
/* 1041 */         always.println("[Config] UpdateURL: (no update URL specified)");
/*      */       }
/* 1043 */       if (matchClientVersionToServerVersion) {
/* 1044 */         always.println("[Config] Client launches will be matched to update server version");
/*      */       }
/* 1046 */       if (mainClassOnUpdate != null)
/* 1047 */         always.println("[Config] MainClass on update: " + mainClassOnUpdate);
/*      */       else {
/* 1049 */         always.println("[Config] MainClass on update: (none specified)");
/*      */       }
/* 1051 */       if (mainClassVerifyJRE != null)
/* 1052 */         always.println("[Config] MainClass to verify JRE compatibility: " + mainClassVerifyJRE);
/*      */       else {
/* 1054 */         always.println("[Config] MainClass to verify JRE compatibility: (none specified)");
/*      */       }
/* 1056 */       if (mainClassPostInstall != null)
/* 1057 */         always.println("[Config] MainClass to run post-install: " + mainClassPostInstall);
/*      */       else {
/* 1059 */         always.println("[Config] MainClass to run post-install: (none specified)");
/*      */       }
/* 1061 */       if (mainClassPreUninstall != null)
/* 1062 */         always.println("[Config] MainClass to run pre-uninstall: " + mainClassPreUninstall);
/*      */       else {
/* 1064 */         always.println("[Config] MainClass to run pre-uninstall: (none specified)");
/*      */       }
/* 1066 */       always.println("[Config] Minimum splash image time: " + splashMinMS + "ms");
/* 1067 */       always.println("[Config] Splash PNG: " + splashPNG);
/* 1068 */       always.println("[Config] Can Override Splash: " + canOverrideSplash);
/* 1069 */       always.println("[Config] Bundle Logo PNG: " + bundleLogoPNG);
/*      */ 
/* 1073 */       for (int i = 0; i < jvmOptions.size(); i++) {
/* 1074 */         always.println("[Config] JVM Option: " + jvmOptions.get(i));
/*      */       }
/* 1076 */       always.println("[Config] Must Fork: " + mustFork);
/* 1077 */       always.println("[Config] OSX TGZ Compression level: " + tgzCompression);
/*      */ 
/* 1079 */       if (macCertInfo != null)
/* 1080 */         always.println("[Config] Will sign MacOS app under Developer Alias: " + macSignID);
/*      */       else {
/* 1082 */         always.println("[Config] Will produce an UNSIGNED app for MacOS (note OS GateKeeper will block this on Mountain Lion (10.8) and later)");
/*      */       }
/*      */ 
/* 1085 */       if (winSignKeystore != null) {
/* 1086 */         if (winSignTimestampURL != null)
/* 1087 */           always.println("[Config] Will sign Windows executables with timestamp (" + winSignTimestampURL + ")");
/*      */         else
/* 1089 */           always.println("[Config] Will sign Windows executables without timestamp (note non-timestamped signed exe's will cease to be considered signed when the certificate runs out)");
/*      */       }
/*      */       else {
/* 1092 */         always.println("[Config] Will produce an UNSIGNED app for Windows (note elevated apps will have a more severe warning and Antivirus programs may complain)");
/*      */ 
/* 1094 */         if (appUiAccess) {
/* 1095 */           always.println("[Config] *** WARNING, elevation for Windows specifies that app needs uiAccess but Windows requires the app to be signed for uiAccess");
/*      */         }
/*      */       }
/*      */ 
/* 1099 */       if (silentInstallParameter != null) {
/* 1100 */         always.println("[Config] Silent install parameter is set to " + silentInstallParameter);
/*      */       }
/*      */ 
/* 1103 */       Properties dynamicProps = new Properties();
/* 1104 */       if (updateURL != null) {
/* 1105 */         dynamicProps.setProperty("update_url", updateURL);
/*      */       }
/* 1107 */       if (matchClientVersionToServerVersion) {
/* 1108 */         dynamicProps.setProperty("match_versions", "true");
/*      */       }
/* 1110 */       if (AutoTesting) {
/* 1111 */         dynamicProps.setProperty("wrapper_autotest", "true");
/*      */       }
/* 1113 */       dynamicProps.setProperty("supported_langs", supportedLanguages);
/* 1114 */       dynamicProps.setProperty("wrapper_app_versions", "3");
/* 1115 */       dynamicProps.setProperty("wrapper_gu_versions", "2");
/*      */ 
/* 1120 */       File[] files = build.listFiles();
/* 1121 */       for (int i = 0; i < files.length; i++) {
/* 1122 */         if (files[i].getName().indexOf("JRE") == -1) {
/* 1123 */           FileUtil.deleteDir(files[i]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1128 */       build.mkdir();
/*      */ 
/* 1130 */       JWParameteriser jwparams = new JWParameteriser();
/*      */ 
/* 1134 */       String[] allJREs = { 
/* 1135 */         GenericUpdater.JRE_WIN32_APP, 
/* 1136 */         GenericUpdater.JRE_WIN64_APP, 
/* 1137 */         GenericUpdater.JRE_LIN32_APP, 
/* 1138 */         GenericUpdater.JRE_LIN64_APP };
/*      */ 
/* 1141 */       String[] jreSource = { 
/* 1142 */         windows32JRE, 
/* 1143 */         windows64JRE, 
/* 1144 */         linux32JRE, 
/* 1145 */         linux64JRE };
/*      */ 
/* 1150 */       FileStripper[] appStrippers = { 
/* 1151 */         new CVSStripper() };
/*      */       FileStripper[] jreStrippers;
/*      */       FileStripper[] jreStrippers;
/* 1156 */       if (noStripJREs) {
/* 1157 */         always.println("[Config] Will not strip JRE (leaving optional files)");
/* 1158 */         jreStrippers = new FileStripper[] { 
/* 1159 */           new CVSStripper() };
/*      */       }
/*      */       else {
/* 1162 */         always.println("[Config] Will strip JRE");
/* 1163 */         jreStrippers = new FileStripper[] { 
/* 1164 */           new CVSStripper(), 
/* 1165 */           new JREStripper() };
/*      */       }
/*      */ 
/* 1169 */       always.println("[JRE] About to archive all JREs, **IMPORTANT** this can take a long time the first time, future builds will be much faster...");
/*      */ 
/* 1171 */       for (int i = 0; i < allJREs.length; i++)
/*      */       {
/* 1173 */         String jreName = allJREs[i];
/*      */ 
/* 1175 */         always.println("[JRE] Archiving JRE " + jreName);
/*      */ 
/* 1177 */         File latestWin32 = LaunchFile.getLatestVersionOf(jreName, build);
/*      */ 
/* 1179 */         while ((latestWin32 != null) && (latestWin32.getName().endsWith(".encoding"))) {
/* 1180 */           always.println("[JRE] Deleting unfinished encode " + latestWin32.getName());
/* 1181 */           latestWin32.delete();
/* 1182 */           latestWin32 = LaunchFile.getLatestVersionOf(jreName, build);
/*      */         }
/*      */ 
/* 1185 */         if (latestWin32 != null)
/*      */         {
/* 1187 */           String jreVersion = LaunchFile.pickVersionFromAppArchive(latestWin32);
/*      */ 
/* 1189 */           always.println("[JRE] Archived " + jreName + " (" + jreVersion + ") already exists so skipping");
/*      */         }
/*      */         else
/*      */         {
/* 1193 */           String newVersion = VersionUtil.padVersion(VersionUtil.getVersionForNow());
/*      */ 
/* 1196 */           if (jreName.toUpperCase().startsWith("WIN")) {
/* 1197 */             File jre = new File(jreSource[i]);
/* 1198 */             if (!jre.exists()) {
/* 1199 */               throw new Exception("Windows JRE path does not exist, a valid Windows JRE must be specified");
/*      */             }
/*      */ 
/* 1202 */             File up200 = new File(jre, "bin" + File.separator + "unpack200.exe");
/* 1203 */             System.out.println("Checking for " + up200.getAbsolutePath());
/* 1204 */             if (!up200.exists())
/* 1205 */               throw new Exception("unpack200.exe not found in Windows JRE bin path");
/*      */           }
/* 1207 */           else if (jreName.toUpperCase().startsWith("LIN")) {
/* 1208 */             File jre = new File(jreSource[i]);
/* 1209 */             if (!jre.exists()) {
/* 1210 */               throw new Exception("Linux JRE path does not exist, a valid Linux JRE must be specified");
/*      */             }
/*      */ 
/* 1213 */             File up200 = new File(jre, "bin" + File.separator + "unpack200");
/* 1214 */             System.out.println("Checking for " + up200.getAbsolutePath());
/* 1215 */             if (!up200.exists()) {
/* 1216 */               throw new Exception("unpack200 not found in Linux JRE bin path");
/*      */             }
/*      */           }
/*      */ 
/* 1220 */           System.out.println("Unpack200 found OK");
/*      */ 
/* 1223 */           always.println("[JRE] New " + jreName + " version is " + newVersion);
/*      */ 
/* 1227 */           File jreArchive = new File(build, GenericUpdater.getArchiveNameFor(jreName, newVersion));
/*      */ 
/* 1229 */           File tempOut = new File(jreArchive.getAbsolutePath() + ".encoding");
/*      */ 
/* 1231 */           Archive archive = new Archive(tempOut);
/*      */ 
/* 1233 */           File jre = new File(jreSource[i]);
/* 1234 */           File[] files = jre.listFiles();
/*      */ 
/* 1237 */           for (int k = 0; k < files.length; k++) {
/* 1238 */             if (files[k].getName().equalsIgnoreCase("bin")) {
/* 1239 */               archive.addFile(files[k], files[k].getName(), jreStrippers);
/*      */             }
/*      */           }
/* 1242 */           for (int k = 0; k < files.length; k++) {
/* 1243 */             if (!files[k].getName().equalsIgnoreCase("bin")) {
/* 1244 */               archive.addFile(files[k], files[k].getName(), jreStrippers);
/*      */             }
/*      */           }
/* 1247 */           archive.finishAndCompress(fullBuild);
/*      */ 
/* 1249 */           VersionUtil.writeAppVersionFile(build, jreName, newVersion);
/*      */ 
/* 1251 */           tempOut.renameTo(jreArchive);
/*      */         }
/*      */ 
/* 1254 */         always.println("[JRE] JRE " + jreName + " finished");
/*      */       }
/*      */ 
/* 1262 */       always.println("[Licensing] Checking JWrapper licensing");
/*      */ 
/* 1264 */       File bundlelic = new File(build, "jwrapper_licensing");
/*      */ 
/* 1266 */       JWLicense devlic = null;
/*      */       try {
/* 1268 */         devlic = JWLicense.load(new File("jwlicense"));
/*      */       } catch (Exception localException25) {
/*      */       }
/*      */       try {
/* 1272 */         devlic = JWLicense.load(new File("jwlicense.txt"));
/*      */       }
/*      */       catch (Exception localException26) {
/*      */       }
/* 1276 */       if ((devlic != null) && 
/* 1277 */         (devlic.hasExpired()))
/* 1278 */         devlic = null;
/*      */       boolean LIC_allowRebranding;
/*      */       boolean LIC_showTrialDialog;
/*      */       boolean LIC_allowRebranding;
/* 1285 */       if (devlic != null) {
/* 1286 */         always.println("[Licensing] JWrapper License found, " + devlic.getName() + " / " + devlic.getCompany());
/* 1287 */         boolean LIC_showTrialDialog = devlic.mustShowTrial();
/* 1288 */         LIC_allowRebranding = devlic.allowRebranding();
/*      */       }
/*      */       else {
/* 1291 */         always.println("[Licensing] No JWrapper license found");
/* 1292 */         LIC_showTrialDialog = false;
/* 1293 */         LIC_allowRebranding = false;
/*      */       }
/*      */ 
/* 1297 */       if (!LIC_showTrialDialog)
/*      */       {
/* 1301 */         StringBuffer licData = new StringBuffer();
/*      */ 
/* 1303 */         for (int i = 0; i < 10; i++) {
/* 1304 */           licData.append("[BUNDLE_NAME]").append(bundleName);
/*      */         }
/*      */ 
/* 1310 */         long t = System.currentTimeMillis();
/*      */ 
/* 1313 */         RSAEncryptor rsaEnc = createRsaEncryptor();
/*      */ 
/* 1315 */         byte[] licenseData = rsaEnc.encrypt(licData.toString().getBytes("UTF8"));
/*      */ 
/* 1317 */         FileUtil.writeFile(bundlelic, licenseData);
/*      */ 
/* 1319 */         t = System.currentTimeMillis() - t;
/* 1320 */         System.out.println("[JWrapperCompiler] JWrapper Licensing took " + t + "ms to set up");
/*      */ 
/* 1324 */         t = System.currentTimeMillis();
/*      */ 
/* 1326 */         RSADecryptor rsaDec = createRsaDecryptor();
/*      */ 
/* 1328 */         String check = new String(rsaDec.decrypt(licenseData), "UTF8");
/*      */ 
/* 1330 */         t = System.currentTimeMillis() - t;
/*      */ 
/* 1333 */         if (!check.equals(licData.toString())) {
/* 1334 */           throw new Exception("[JWrapperCompiler] Licensing not working on this platform!");
/*      */         }
/* 1336 */         System.out.println("[JWrapperCompiler] JWrapper Licensing verified OK");
/*      */       }
/*      */ 
/* 1341 */       String guversion = GenericUpdater.GetVersion();
/*      */ 
/* 1343 */       String appversion = VersionUtil.padVersion(VersionUtil.getVersionForNow());
/*      */ 
/* 1346 */       if (!LIC_allowRebranding) {
/* 1347 */         always.println("[Licensing] Rebranding not allowed, using JWrapper splash and logo");
/*      */ 
/* 1349 */         File wrappers = new File("wrappers");
/*      */ 
/* 1351 */         splashPNG = new File(wrappers, "jwbigsplash600w.png").getAbsolutePath();
/* 1352 */         byte[] dat = JWSplash.SPLASH(wrappers);
/* 1353 */         FileUtil.writeFile(splashPNG, dat);
/*      */       }
/*      */       else
/*      */       {
/* 1370 */         always.println("[Licensing] Rebranding allowed, using App splash");
/*      */       }
/* 1372 */       File bundleSplash = new File(build, GenericUpdater.getSplashFileNameFor(bundleName));
/*      */ 
/* 1376 */       BufferedImage msplash = ImageHelper.scaleDownToFitInside(ImageIO.read(new File(splashPNG)), 300, 200);
/* 1377 */       ImageIO.write(msplash, "PNG", bundleSplash);
/*      */ 
/* 1380 */       int osxBgHeight = 350;
/*      */ 
/* 1382 */       File appOsxBG = new File(build, "osxbg.png");
/*      */ 
/* 1385 */       BufferedImage msplash = ImageHelper.scaleDownToFitInside(ImageIO.read(new File(splashPNG)), 600, 350);
/* 1386 */       int width = msplash.getWidth();
/*      */ 
/* 1388 */       osxBgHeight = msplash.getHeight();
/*      */ 
/* 1390 */       System.out.println("OS BG Height is " + osxBgHeight);
/*      */ 
/* 1392 */       int diff = (600 - width) / 2;
/* 1393 */       if (diff > 0) {
/* 1394 */         msplash = ImageHelper.addBorder(msplash, Color.white, new Insets(0, diff, 0, diff));
/*      */       }
/*      */ 
/* 1397 */       ImageIO.write(msplash, "PNG", appOsxBG);
/*      */ 
/* 1400 */       File wrappers = new File("wrappers");
/*      */ 
/* 1402 */       File appUninstallerICO = new File(build, GenericUpdater.getUninstallerIcopngFileNameFor(bundleName));
/*      */ 
/* 1404 */       File png = new File(wrappers, "uninstaller.png");
/*      */ 
/* 1406 */       ICOWriter.ImageList list = new ICOWriter.ImageList();
/* 1407 */       list.image16 = png;
/* 1408 */       list.image24 = png;
/* 1409 */       list.image32 = png;
/* 1410 */       list.image48 = png;
/* 1411 */       list.image256 = png;
/*      */ 
/* 1413 */       File outico = new File(build, "out.ico");
/*      */ 
/* 1415 */       int[] pngInfo = ICOWriter.writeIcoFile(outico, list);
/*      */ 
/* 1417 */       byte[] ICO = FileUtil.readFile(outico.getAbsolutePath());
/*      */ 
/* 1419 */       IcoPng icopng = new IcoPng(ICO, pngInfo[0], pngInfo[1]);
/*      */ 
/* 1421 */       outico.delete();
/*      */ 
/* 1423 */       icopng.save(appUninstallerICO);
/*      */ 
/* 1426 */       File appICNS = new File(build, GenericUpdater.getIcnsFileNameFor(bundleName));
/*      */ 
/* 1432 */       ICNSWriter.ImageList imageList = new ICNSWriter.ImageList();
/*      */ 
/* 1434 */       File logoFile = new File(bundleLogoPNG);
/*      */ 
/* 1436 */       Image logo = ImageIO.read(logoFile);
/*      */ 
/* 1444 */       int logoMax = Math.max(logo.getWidth(null), logo.getHeight(null));
/*      */ 
/* 1446 */       if (logoMax >= 1024) {
/* 1447 */         imageList.image1024 = logoFile;
/* 1448 */       } else if (logoMax >= 512) {
/* 1449 */         imageList.image512 = logoFile;
/* 1450 */       } else if (logoMax >= 256) {
/* 1451 */         imageList.image256 = logoFile;
/* 1452 */       } else if (logoMax >= 128) {
/* 1453 */         imageList.image128 = logoFile;
/* 1454 */       } else if (logoMax >= 48) {
/* 1455 */         imageList.image48 = logoFile;
/* 1456 */       } else if (logoMax >= 32) {
/* 1457 */         imageList.image32 = logoFile;
/* 1458 */         imageList.image16 = logoFile;
/*      */       }
/*      */ 
/* 1461 */       ICNSWriter.writeIcnsFile(appICNS, imageList);
/*      */ 
/* 1465 */       always.println("[JWrapper] Building JWrapper " + guversion);
/*      */ 
/* 1467 */       if (simulateBrokenGU) {
/* 1468 */         guversion = VersionUtil.padVersion(VersionUtil.getVersionForNow());
/* 1469 */         System.out.println("Simulating broken GU with new version: " + guversion);
/*      */       }
/*      */ 
/* 1475 */       LaunchableArchive jwarchive = new LaunchableArchive(
/* 1476 */         simulateBrokenGU ? 
/* 1477 */         "jwrapper.BROKEN.GenericUpdaterLaunch" : 
/* 1478 */         "jwrapper.updater.GenericUpdaterLaunch", 
/* 1479 */         null, 
/* 1480 */         "jwrapper.updater.GenericUpdaterJreVerifierLaunch", 
/* 1481 */         null, 
/* 1482 */         null, 
/* 1483 */         bundleName, 
/* 1484 */         null, 
/* 1485 */         true, 
/* 1486 */         new File(build, GenericUpdater.getArchiveNameFor("JWrapper", guversion)));
/*      */ 
/* 1488 */       jwarchive.addJvmOption(LaunchFile.JWRAPPER_JVM_OPTION);
/*      */ 
/* 1493 */       jwarchive.setPack200Allowed(false);
/*      */ 
/* 1495 */       jwarchive.addFile(true, "lib/jwstandalonelaunch.jar");
/*      */ 
/* 1498 */       jwarchive.addFile(false, "lib/jwstandalone.jar");
/* 1499 */       jwarchive.addFile(false, "lib/sevenzip.jar");
/*      */ 
/* 1502 */       jwarchive.addFile(false, bundleSplash, bundleSplash.getName());
/*      */ 
/* 1505 */       jwarchive.addFile(false, appICNS, appICNS.getName());
/*      */ 
/* 1508 */       jwarchive.addFile(false, new File(wrappers, "jwutils_win32.dll"), "jwutils_win32.dll");
/* 1509 */       jwarchive.addFile(false, new File(wrappers, "jwutils_win64.dll"), "jwutils_win64.dll");
/* 1510 */       jwarchive.addFile(false, new File(wrappers, "libjwutils_linux32.so"), "libjwutils_linux32.so");
/* 1511 */       jwarchive.addFile(false, new File(wrappers, "libjwutils_linux64.so"), "libjwutils_linux64.so");
/* 1512 */       jwarchive.addFile(false, new File(wrappers, "libjwutils_macos32.jnilib"), "libjwutils_macos32.jnilib");
/* 1513 */       jwarchive.addFile(false, new File(wrappers, "libjwutils_macos64.jnilib"), "libjwutils_macos64.jnilib");
/*      */ 
/* 1515 */       File firstRun = new File(build, "firstRun");
/* 1516 */       firstRun.createNewFile();
/* 1517 */       jwarchive.addFile(false, firstRun, firstRun.getName());
/*      */ 
/* 1533 */       time();
/* 1534 */       jwarchive.finishAndCompress(fullBuild);
/* 1535 */       lzmaTime += time();
/*      */ 
/* 1537 */       firstRun.delete();
/*      */ 
/* 1540 */       VersionUtil.writeAppVersionFile(build, "JWrapper", guversion);
/*      */ 
/* 1542 */       always.println("[JWrapper] JWrapper " + guversion + " finished");
/*      */ 
/* 1545 */       always.println("[App] Rescaling logos");
/*      */ 
/* 1552 */       File logoFile = new File(bundleLogoPNG);
/*      */ 
/* 1554 */       Image logo = ImageIO.read(logoFile);
/*      */ 
/* 1562 */       BufferedImage image = ImageHelper.toBufferedImageARGB(logo);
/*      */ 
/* 1564 */       ByteArrayOutputStream bout = new ByteArrayOutputStream();
/*      */ 
/* 1566 */       bout.reset();
/* 1567 */       ImageIO.write(ImageHelper.scale(image, 16, 16), "PNG", bout);
/* 1568 */       byte[] logo16 = bout.toByteArray();
/*      */ 
/* 1570 */       bout.reset();
/* 1571 */       ImageIO.write(ImageHelper.scale(image, 24, 24), "PNG", bout);
/* 1572 */       byte[] logo24 = bout.toByteArray();
/*      */ 
/* 1574 */       bout.reset();
/* 1575 */       ImageIO.write(ImageHelper.scale(image, 32, 32), "PNG", bout);
/* 1576 */       byte[] logo32 = bout.toByteArray();
/*      */ 
/* 1578 */       bout.reset();
/* 1579 */       ImageIO.write(ImageHelper.scale(image, 48, 48), "PNG", bout);
/* 1580 */       byte[] logo48 = bout.toByteArray();
/*      */ 
/* 1582 */       bout.reset();
/* 1583 */       ImageIO.write(ImageHelper.scale(image, 256, 256), "PNG", bout);
/* 1584 */       byte[] logo256 = bout.toByteArray();
/*      */ 
/* 1590 */       always.println("[App] Building launchers");
/* 1591 */       ArrayList launchers = new ArrayList();
/*      */ 
/* 1593 */       always.println("[App] Building Windows launcher...");
/*      */ 
/* 1596 */       File winLauncher = new File(wrappers, getWrapperFor(appElevation, appUiAccess));
/*      */ 
/* 1598 */       File applauncher_icon = new File(build, bundleName + "Launcher_icon.exe");
/*      */ 
/* 1600 */       ExeReader.embedIcons(winLauncher.getAbsolutePath(), applauncher_icon.getAbsolutePath(), logo16, logo24, logo32, logo48, logo256);
/*      */ 
/* 1603 */       OutputStream lout = new BufferedOutputStream(new FileOutputStream(applauncher_icon, true));
/*      */ 
/* 1607 */       dynamicProps.setProperty("jre_name", "Windows32JRE");
/*      */ 
/* 1609 */       if (winSignKeystore == null) {
/* 1610 */         lout.write(jwparams.getParameterisedBlock(dynamicProps, PARAMS_BLOCK_LEN));
/*      */       }
/*      */ 
/* 1614 */       lout.write(marker);
/*      */ 
/* 1616 */       CFriendlyStreamUtils.writeString(lout, bundleName);
/* 1617 */       CFriendlyStreamUtils.writeString(lout, "");
/* 1618 */       CFriendlyStreamUtils.writeString(lout, "");
/* 1619 */       CFriendlyStreamUtils.writeString(lout, "");
/* 1620 */       CFriendlyStreamUtils.writeString(lout, splashMinMS);
/* 1621 */       if (canOverrideSplash)
/* 1622 */         CFriendlyStreamUtils.writeString(lout, "1");
/*      */       else
/* 1624 */         CFriendlyStreamUtils.writeString(lout, "0");
/* 1625 */       CFriendlyStreamUtils.writeString(lout, installType);
/* 1626 */       if (silentInstallParameter != null)
/* 1627 */         CFriendlyStreamUtils.writeString(lout, silentInstallParameter);
/*      */       else {
/* 1629 */         CFriendlyStreamUtils.writeString(lout, "");
/*      */       }
/* 1631 */       CFriendlyStreamUtils.writeString(lout, updateURL);
/* 1632 */       lout.close();
/*      */ 
/* 1637 */       File applauncher = new File(build, GenericUpdater.getLauncherNameFor(bundleName, false, false, false));
/*      */ 
/* 1639 */       if (winSignKeystore != null) {
/* 1640 */         always.println("[App] Signing launcher exe...");
/*      */ 
/* 1642 */         int X = 0;
/*      */         while (true) {
/* 1644 */           X++;
/*      */           try
/*      */           {
/* 1647 */             time();
/* 1648 */             new AuthenticodeSigner(applauncher_icon, applauncher, bundleName, winSignTimestampURL, winSignKeystore).sign(jwparams.getParameterisedBlock(dynamicProps, PARAMS_BLOCK_LEN));
/* 1649 */             signingTime = time();
/* 1650 */             always.println("Signed OK");
/*      */           }
/*      */           catch (IOException x) {
/* 1653 */             if (X == 10) {
/* 1654 */               throw x;
/*      */             }
/* 1656 */             always.println("[App] Problem with signing (could be timestamp URL issue, will try again in 3s)...");
/* 1657 */             Thread.sleep(3000L);
/*      */           }
/*      */         }
/*      */       }
/* 1661 */       FileUtil.copy(applauncher_icon, applauncher);
/*      */ 
/* 1665 */       applauncher_icon.delete();
/*      */ 
/* 1669 */       launchers.add(applauncher);
/*      */ 
/* 1671 */       always.println("[App] Windows launcher finished");
/*      */ 
/* 1674 */       for (int BITS = 32; BITS <= 64; BITS += 32) {
/* 1675 */         always.println("[App] Building Linux " + BITS + " bit launcher...");
/* 1676 */         File linLauncher = new File(wrappers, "linuxwrapper" + BITS);
/*      */ 
/* 1678 */         File applauncher = new File(build, GenericUpdater.getLauncherNameFor(bundleName, true, BITS == 64, false));
/* 1679 */         FileUtil.copy(linLauncher, applauncher);
/*      */ 
/* 1681 */         OutputStream lout = new BufferedOutputStream(new FileOutputStream(applauncher, true));
/*      */ 
/* 1683 */         dynamicProps.setProperty("jre_name", "Linux" + BITS + "JRE");
/*      */ 
/* 1685 */         lout.write(jwparams.getParameterisedBlock(dynamicProps, PARAMS_BLOCK_LEN));
/*      */ 
/* 1688 */         lout.write(marker);
/*      */ 
/* 1690 */         CFriendlyStreamUtils.writeString(lout, bundleName);
/*      */ 
/* 1692 */         CFriendlyStreamUtils.writeString(lout, "");
/* 1693 */         CFriendlyStreamUtils.writeString(lout, "");
/* 1694 */         CFriendlyStreamUtils.writeString(lout, "");
/* 1695 */         CFriendlyStreamUtils.writeString(lout, splashMinMS);
/* 1696 */         if (canOverrideSplash)
/* 1697 */           CFriendlyStreamUtils.writeString(lout, "1");
/*      */         else {
/* 1699 */           CFriendlyStreamUtils.writeString(lout, "0");
/*      */         }
/* 1701 */         CFriendlyStreamUtils.writeString(lout, installType);
/* 1702 */         if (silentInstallParameter != null)
/* 1703 */           CFriendlyStreamUtils.writeString(lout, silentInstallParameter);
/*      */         else {
/* 1705 */           CFriendlyStreamUtils.writeString(lout, "");
/*      */         }
/* 1707 */         CFriendlyStreamUtils.writeString(lout, updateURL);
/*      */ 
/* 1709 */         lout.close();
/*      */ 
/* 1713 */         launchers.add(applauncher);
/*      */ 
/* 1715 */         always.println("[App] Linux launcher finished");
/*      */       }
/*      */ 
/* 1719 */       always.println("[App] Building MacOS launcher...");
/*      */ 
/* 1721 */       Properties jwp = new Properties();
/* 1722 */       jwp.setProperty("app_name", bundleName);
/*      */ 
/* 1724 */       jwp.setProperty("wrapper_gu_version", "");
/* 1725 */       jwp.setProperty("min_splash_ms", splashMinMS);
/* 1726 */       if (canOverrideSplash)
/* 1727 */         jwp.setProperty("can_override_splash", "1");
/*      */       else
/* 1729 */         jwp.setProperty("can_override_splash", "0");
/* 1730 */       jwp.setProperty("install_type", installType);
/* 1731 */       if (silentInstallParameter != null) {
/* 1732 */         jwp.setProperty("silent_parameter", silentInstallParameter);
/*      */       }
/* 1734 */       File jwprops = new File(build, "jwrapper_properties");
/* 1735 */       OutputStream out = new BufferedOutputStream(new FileOutputStream(jwprops));
/* 1736 */       jwp.store(out, "");
/* 1737 */       out.close();
/*      */ 
/* 1739 */       File[] files = { 
/* 1740 */         jwprops };
/*      */ 
/* 1744 */       FileUtil.deleteDir(new File(build, "osxwrapper.jar"));
/* 1745 */       addFilesToExistingZip(new File(wrappers, "osxwrapper.jar"), new File(build, "osxwrapper.jar"), files);
/*      */ 
/* 1747 */       File dotApp = new File(build, GenericUpdater.getLauncherNameFor(bundleName, false, false, true));
/*      */ 
/* 1750 */       FileUtil.deleteDir(dotApp);
/* 1751 */       FileUtil.copyFileOrDir(new File(wrappers, "Blank.app"), dotApp);
/*      */ 
/* 1754 */       FileUtil.removeCVS(dotApp);
/*      */ 
/* 1756 */       dynamicProps.setProperty("jre_name", "-");
/*      */ 
/* 1759 */       File appParams = new File(dotApp, "Contents" + File.separator + "Resources" + File.separator + "AppParams.excludefromsigning");
/* 1760 */       appParams.mkdirs();
/* 1761 */       appParams.delete();
/* 1762 */       FileOutputStream apout = new FileOutputStream(appParams);
/* 1763 */       apout.write(jwparams.getParameterisedBlock(dynamicProps, PARAMS_BLOCK_LEN));
/* 1764 */       apout.close();
/*      */ 
/* 1769 */       File icns = new File(dotApp, "Contents" + File.separator + "Resources" + File.separator + OSXWrapper.getICNSFileName());
/* 1770 */       FileUtil.copyFileOrDir(appICNS, icns);
/*      */ 
/* 1772 */       File splash = new File(dotApp, "Contents" + File.separator + "Resources" + File.separator + OSXWrapper.getAppSplashName());
/* 1773 */       FileUtil.copyFileOrDir(bundleSplash, splash);
/*      */ 
/* 1779 */       File plistFile = new File(dotApp, "Contents" + File.separator + "Info.plist");
/*      */ 
/* 1781 */       String plist = FileUtil.readFileAsString(plistFile.getAbsolutePath());
/*      */ 
/* 1783 */       StringBuffer longEmptyForSH = new StringBuffer();
/* 1784 */       for (int i = 0; i < 2048; i++) longEmptyForSH.append(' ');
/*      */ 
/* 1786 */       int index = plist.indexOf("OSX_JW_ARGUMENT_0_HERE");
/* 1787 */       String result = plist.substring(0, index) + 
/* 1788 */         updateURL + 
/* 1789 */         longEmptyForSH.toString() + 
/* 1790 */         plist.substring(index + "OSX_JW_ARGUMENT_1_HERE".length());
/*      */ 
/* 1792 */       result = result.replace("OSX_JW_BUNDLE_NAME", bundleName);
/* 1793 */       result = result.replace("OSX_JW_BUNDLE_ID", "jwrapper.osx." + normaliseID(bundleName));
/*      */ 
/* 1795 */       FileUtil.writeFile(plistFile, result.getBytes("UTF8"));
/*      */ 
/* 1797 */       File javadir = new File(dotApp, "Contents" + File.separator + "Resources" + File.separator + "Java");
/* 1798 */       javadir.mkdirs();
/*      */ 
/* 1800 */       FileUtil.copyFileOrDir(new File(build, "osxwrapper.jar"), new File(javadir, "osxwrapper.jar"));
/* 1801 */       FileUtil.copyFileOrDir(new File("lib", "sevenzip.jar"), new File(javadir, "sevenzip.jar"));
/*      */ 
/* 1803 */       FileUtil.deleteDir(new File(build, "osxwrapper.jar"));
/* 1804 */       FileUtil.deleteDir(new File(build, "jwrapper_properties"));
/*      */ 
/* 1806 */       if (macCertInfo != null) {
/* 1807 */         always.println("[App] Signing OSX Launcher App " + dotApp.getPath());
/* 1808 */         time();
/* 1809 */         MacSigner.signApp(macCertInfo, new File(build, GenericUpdater.getLauncherNameFor(bundleName, false, false, true)));
/* 1810 */         signingTime += time();
/*      */ 
/* 1812 */         FileUtil.deleteDir(new File(dotApp, "Contents" + File.separator + "MacOS" + File.separator + "JavaApplicationStub.backup"));
/*      */       }
/*      */ 
/* 1815 */       launchers.add(dotApp);
/*      */ 
/* 1817 */       always.println("[App] OSX launcher finished");
/*      */ 
/* 1823 */       always.println("[App] Archiving App " + bundleName + " " + appversion);
/*      */ 
/* 1825 */       LaunchableArchive jwarchive = new LaunchableArchive(
/* 1826 */         JWrapper.class.getName(), 
/* 1827 */         mainClassOnUpdate, 
/* 1828 */         mainClassVerifyJRE, 
/* 1829 */         mainClassPostInstall, 
/* 1830 */         mainClassPreUninstall, 
/* 1831 */         bundleName, 
/* 1832 */         appICNS.getName(), 
/* 1833 */         paramZeroUpdateUrl, 
/* 1834 */         new File(build, GenericUpdater.getArchiveNameFor(bundleName, appversion)));
/*      */ 
/* 1837 */       if (fullBuild)
/* 1838 */         jwarchive.setPack200Allowed(true);
/*      */       else {
/* 1840 */         jwarchive.setPack200Allowed(false);
/*      */       }
/* 1842 */       if (mustFork) {
/* 1843 */         jwarchive.setMustFork(true);
/*      */       }
/*      */ 
/* 1846 */       if (jvmOptions != null) {
/* 1847 */         for (int i = 0; i < jvmOptions.size(); i++) {
/* 1848 */           jwarchive.addJvmOption((String)jvmOptions.get(i));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1853 */       if (bundlelic.exists()) {
/* 1854 */         jwarchive.addFile(false, bundlelic, "jwrapper_license");
/*      */       }
/*      */ 
/* 1857 */       File session_win = new File(build, "session_win.exe");
/*      */ 
/* 1860 */       if (winSignKeystore != null) {
/* 1861 */         always.println("[App] Signing Windows session launcher...");
/*      */ 
/* 1863 */         int X = 0;
/*      */         while (true) {
/* 1865 */           X++;
/*      */           try
/*      */           {
/* 1868 */             time();
/* 1869 */             new AuthenticodeSigner(new File(wrappers, "session_win.exe"), session_win, bundleName, winSignTimestampURL, winSignKeystore).sign();
/* 1870 */             signingTime += time();
/* 1871 */             always.println("[App] Windows session launcher signed OK");
/*      */           }
/*      */           catch (IOException x) {
/* 1874 */             if (X == 10) {
/* 1875 */               throw x;
/*      */             }
/* 1877 */             always.println("[App] Problem signing Windows session launcher (could be a timestamp URL issue, will try again in 3s...)");
/* 1878 */             Thread.sleep(3000L);
/*      */           }
/*      */         }
/*      */ 
/* 1882 */         always.println("[App] Adding Windows session launcher to archive...");
/* 1883 */         jwarchive.addFile(false, session_win, session_win.getName());
/*      */       } else {
/* 1885 */         always.println("[App] No Windows signing information - App will be UNABLE TO ELEVATE LAUNCH INTO SESSION ON WINDOWS");
/*      */       }
/*      */ 
/* 1889 */       File elev_win = new File(build, "elev_win.exe");
/*      */ 
/* 1892 */       if (winSignKeystore != null) {
/* 1893 */         always.println("[App] Signing Windows elevation...");
/*      */ 
/* 1895 */         int X = 0;
/*      */         while (true) {
/* 1897 */           X++;
/*      */           try
/*      */           {
/* 1900 */             time();
/* 1901 */             new AuthenticodeSigner(new File(wrappers, "elev_win.exe"), elev_win, bundleName, winSignTimestampURL, winSignKeystore).sign();
/* 1902 */             signingTime += time();
/* 1903 */             always.println("[App] Windows elevation signed OK");
/*      */           }
/*      */           catch (IOException x) {
/* 1906 */             if (X == 10) {
/* 1907 */               throw x;
/*      */             }
/* 1909 */             always.println("[App] Problem signing Windows elevation (could be a timestamp URL issue, will try again in 3s...)");
/* 1910 */             Thread.sleep(3000L);
/*      */           }
/*      */         }
/*      */ 
/* 1914 */         always.println("[app] Adding Windows elevation to archive...");
/* 1915 */         jwarchive.addFile(false, elev_win, elev_win.getName());
/*      */       } else {
/* 1917 */         always.println("[App] No Windows signing information - App will be UNABLE TO ELEVATE ON WINDOWS");
/*      */       }
/*      */ 
/* 1920 */       always.println("[App] Adding MacOS elevation to archive...");
/* 1921 */       File elev_mac = new File(wrappers, "elev_mac");
/*      */ 
/* 1923 */       jwarchive.addFile(false, elev_mac, elev_mac.getName());
/*      */ 
/* 1932 */       always.println("[App] Adding Linux elevation to archive...");
/*      */ 
/* 1947 */       NodeList list = root.getElementsByTagName("File");
/*      */ 
/* 1949 */       for (int i = 0; i < list.getLength(); i++) {
/* 1950 */         Element el = (Element)list.item(i);
/*      */ 
/* 1952 */         File file = new File(el.getTextContent());
/*      */ 
/* 1954 */         String path = file.getName();
/* 1955 */         if (el.hasAttribute("path")) {
/* 1956 */           path = el.getAttribute("path");
/*      */         }
/* 1958 */         boolean cpath = false;
/* 1959 */         if (el.hasAttribute("classpath")) {
/* 1960 */           cpath = true;
/*      */ 
/* 1962 */           if (file.isDirectory()) {
/* 1963 */             throw new Exception("Folder " + path + " cannot be part of classpath!");
/*      */           }
/*      */         }
/*      */ 
/* 1967 */         always.println("[App] Adding file to archive... (" + file + ") (=" + path + ") (classpath=" + cpath + ")");
/*      */ 
/* 1971 */         if (path.indexOf("jwrapper_license") != -1) {
/* 1972 */           throw new Exception("Files containing 'jwrapper_license' are not allowed");
/*      */         }
/*      */ 
/* 1975 */         jwarchive.addFile(cpath, file, path);
/*      */       }
/*      */ 
/* 1987 */       always.println("[App] Adding supplementary files to archive...");
/* 1988 */       ArrayList deleteme = new ArrayList();
/*      */ 
/* 1991 */       for (int i = 0; i < apps.size(); i++) {
/* 1992 */         JWApp jwapp = (JWApp)apps.get(i);
/* 1993 */         File appfile = jwapp.save(build);
/* 1994 */         deleteme.add(appfile);
/*      */ 
/* 1996 */         jwarchive.addFile(false, appfile, appfile.getName());
/*      */       }
/*      */ 
/* 2000 */       jwarchive.addFile(false, bundleSplash, bundleSplash.getName());
/*      */ 
/* 2003 */       jwarchive.addFile(false, appICNS, appICNS.getName());
/*      */ 
/* 2006 */       jwarchive.addFile(false, appUninstallerICO, appUninstallerICO.getName());
/*      */ 
/* 2009 */       for (int i = 0; i < launchers.size(); i++) {
/* 2010 */         File f = (File)launchers.get(i);
/* 2011 */         jwarchive.addFile(false, f, f.getName());
/*      */       }
/*      */ 
/* 2017 */       jwarchive.addFile(true, new File("lib/jwrapper_utils.jar"), "jwrapper_utils.jar");
/*      */ 
/* 2020 */       jwarchive.addFile(false, new File(wrappers, "jwutils_win32.dll"), "jwutils_win32.dll");
/* 2021 */       jwarchive.addFile(false, new File(wrappers, "jwutils_win64.dll"), "jwutils_win64.dll");
/* 2022 */       jwarchive.addFile(false, new File(wrappers, "libjwutils_linux32.so"), "libjwutils_linux32.so");
/* 2023 */       jwarchive.addFile(false, new File(wrappers, "libjwutils_linux64.so"), "libjwutils_linux64.so");
/* 2024 */       jwarchive.addFile(false, new File(wrappers, "libjwutils_macos32.jnilib"), "libjwutils_macos32.jnilib");
/* 2025 */       jwarchive.addFile(false, new File(wrappers, "libjwutils_macos64.jnilib"), "libjwutils_macos64.jnilib");
/*      */ 
/* 2027 */       always.println("[App] Compressing archive...");
/*      */ 
/* 2030 */       jwarchive.finishAndCompress(fullBuild);
/*      */ 
/* 2032 */       for (int i = 0; i < deleteme.size(); i++) {
/* 2033 */         ((File)deleteme.get(i)).delete();
/*      */       }
/*      */ 
/* 2037 */       VersionUtil.writeAppVersionFile(build, bundleName, appversion);
/*      */ 
/* 2049 */       if (deleteElevationFiles)
/* 2050 */         elev_win.delete();
/* 2051 */       session_win.delete();
/* 2052 */       bundlelic.delete();
/*      */ 
/* 2055 */       always.println("[App] App archive finished");
/*      */ 
/* 2058 */       File guArchive = new File(build, GenericUpdater.getArchiveNameFor("JWrapper", guversion));
/* 2059 */       File guVersionFile = new File(build, GenericUpdater.getVersionFileNameFor("JWrapper"));
/* 2060 */       File appArchive = new File(build, GenericUpdater.getArchiveNameFor(bundleName, appversion));
/* 2061 */       File appVersionFile = new File(build, GenericUpdater.getVersionFileNameFor(bundleName));
/*      */ 
/* 2066 */       always.println("[CmdLine] Building Command Line Java Wrapper");
/*      */ 
/* 2069 */       File szsrc = new File(new File("lib"), "sevenzip.jar");
/* 2070 */       File szdest = new File(build, bundleName + "-java-online_sz.jar");
/* 2071 */       FileUtil.copyFileOrDir(szsrc, szdest);
/*      */ 
/* 2074 */       File jarsrc = new File(wrappers, "jwrapperapplet.jar");
/* 2075 */       File cmdlineJar = new File(build, bundleName + "-java-online.jar");
/*      */ 
/* 2077 */       always.println("[CmdLine] Constructing static properties file...");
/*      */ 
/* 2079 */       Properties staticProperties = new Properties();
/* 2080 */       staticProperties.put("app_name", bundleName);
/* 2081 */       staticProperties.put("min_splash_ms", splashMinMS);
/* 2082 */       if (canOverrideSplash)
/* 2083 */         staticProperties.put("can_override_splash", "1");
/*      */       else {
/* 2085 */         staticProperties.put("can_override_splash", "0");
/*      */       }
/* 2087 */       staticProperties.put("match_versions", matchClientVersionToServerVersion);
/*      */ 
/* 2089 */       if (silentInstallParameter != null) {
/* 2090 */         staticProperties.put("silent_parameter", silentInstallParameter);
/*      */       }
/* 2092 */       if (!dynamicUpdateURL) {
/* 2093 */         staticProperties.put("update_url", updateURL);
/*      */       }
/* 2095 */       staticProperties.put("install_type", installType);
/*      */ 
/* 2100 */       File staticPropertiesFile = new File(build, "static.properties");
/* 2101 */       FileOutputStream fout = new FileOutputStream(staticPropertiesFile);
/* 2102 */       staticProperties.storeToXML(fout, "");
/* 2103 */       fout.close();
/*      */ 
/* 2108 */       addFilesToExistingZip(jarsrc, cmdlineJar, 
/* 2109 */         new File[] { 
/* 2110 */         staticPropertiesFile, 
/* 2111 */         new File(wrappers, "jwutils_win32.dll"), 
/* 2112 */         new File(wrappers, "jwutils_win64.dll") });
/*      */ 
/* 2115 */       staticPropertiesFile.delete();
/*      */ 
/* 2119 */       boolean haveProducedApplet = false;
/*      */ 
/* 2122 */       always.println("[Applet] Building Applet wrapper");
/*      */       try
/*      */       {
/* 2125 */         if (jarSignerPath == null) {
/* 2126 */           always.println("[Applet] No explicit jarsigner path set, will try to autodetect from current JRE");
/* 2127 */           String home = System.getProperty("java.home");
/*      */ 
/* 2130 */           String tmp = home + File.separator + "bin" + File.separator + "jarsigner";
/* 2131 */           if (OS.isWindows()) {
/* 2132 */             tmp = tmp + ".exe";
/*      */           }
/* 2134 */           File jarsigner = new File(tmp);
/*      */ 
/* 2137 */           if (!jarsigner.exists())
/*      */           {
/* 2139 */             String tmp = home + File.separator + ".." + File.separator + "bin" + File.separator + "jarsigner";
/* 2140 */             if (OS.isWindows()) {
/* 2141 */               tmp = tmp + ".exe";
/*      */             }
/* 2143 */             jarsigner = new File(tmp);
/*      */           }
/*      */ 
/* 2146 */           if (jarsigner.exists())
/* 2147 */             jarSignerPath = jarsigner.getAbsoluteFile().getCanonicalPath();
/*      */         }
/*      */       }
/*      */       catch (Exception x) {
/* 2151 */         always.println("[Applet] Failed to autodetect jarsigner path (" + x + ")");
/*      */       }
/*      */ 
/* 2154 */       if (winSignKeystore == null) {
/* 2155 */         always.println("[Applet] No applet signing info, unable to produce applet (must be signed to work properly)");
/* 2156 */       } else if (jarSignerPath == null) {
/* 2157 */         always.println("[Applet] No jarsigner path, unable to produce applet, please add <JarSignerPath> inside <SignForWindowsAndApplet>");
/*      */       }
/*      */       else {
/* 2160 */         haveProducedApplet = true;
/*      */ 
/* 2167 */         File jarsrc = cmdlineJar;
/* 2168 */         File jardest = new File(build, bundleName + "Applet.jar");
/* 2169 */         FileUtil.copyFileOrDir(jarsrc, jardest);
/*      */ 
/* 2222 */         always.println("[Applet] Signing applet jar 1 of 2...");
/* 2223 */         time();
/* 2224 */         signJar(jardest, jarSignerPath, winSignKeystore, winSignKeystoreType, appletSignTimestampURL);
/* 2225 */         signingTime += time();
/*      */ 
/* 2227 */         jarsrc = new File(new File("lib"), "sevenzip.jar");
/* 2228 */         jardest = new File(build, bundleName + "Applet_sz.jar");
/* 2229 */         FileUtil.copyFileOrDir(jarsrc, jardest);
/*      */ 
/* 2231 */         always.println("[Applet] Signing applet jar 2 of 2...");
/* 2232 */         time();
/* 2233 */         signJar(jardest, jarSignerPath, winSignKeystore, winSignKeystoreType, appletSignTimestampURL);
/* 2234 */         signingTime += time();
/*      */       }
/*      */ 
/* 2237 */       always.println("[Applet] Applet wrapper finished");
/*      */ 
/* 2243 */       always.println("[JavaScript] Building JS Download Embed Script");
/*      */ 
/* 2246 */       File csssrc = new File(wrappers, "JWrapperEmbed.css");
/* 2247 */       File cssdest = new File(build, bundleName + "Embed.css");
/* 2248 */       FileUtil.copyFileOrDir(csssrc, cssdest);
/*      */ 
/* 2252 */       String encodedBundleName = URLEncoder.encode(bundleName, "UTF-8");
/*      */ 
/* 2254 */       String js = FileUtil.readFileAsString(new File(wrappers, "JWrapperEmbed.js").getAbsolutePath());
/*      */ 
/* 2256 */       js = StringReplace.replaceAll(js, "JWRAPPER_APP_NAME", bundleName);
/* 2257 */       js = StringReplace.replaceAll(js, "JWRAPPER_ARCHIVE_PATH", encodedBundleName + "Applet.jar," + encodedBundleName + "Applet_sz.jar");
/* 2258 */       js = StringReplace.replaceAll(js, "JWRAPPER_IMAGE_SRC_TAG", WebBase64.byteArrayToBase64(FileUtil.readFile(bundleSplash.getAbsolutePath())));
/*      */ 
/* 2260 */       FileUtil.writeFileAsString(new File(build, bundleName + "Embed.js").getAbsolutePath(), js);
/*      */ 
/* 2262 */       String ht = FileUtil.readFileAsString(new File(wrappers, "JWrapperEmbedExample.html").getAbsolutePath());
/*      */ 
/* 2264 */       if (haveUpdateURL)
/* 2265 */         ht = StringReplace.replaceAll(ht, "EMBED_JS_URL", updateURL + "/" + bundleName + "Embed.js");
/*      */       else {
/* 2267 */         ht = StringReplace.replaceAll(ht, "EMBED_JS_URL", "http://localhost:33333/" + bundleName + "Embed.js");
/*      */       }
/* 2269 */       if (haveUpdateURL) {
/* 2270 */         if (haveProducedApplet)
/* 2271 */           ht = StringReplace.replaceAll(ht, "EMBED_JS_ORDER", "online*,offline,applet");
/*      */         else {
/* 2273 */           ht = StringReplace.replaceAll(ht, "EMBED_JS_ORDER", "online*,offline");
/*      */         }
/*      */       }
/* 2276 */       else if (haveProducedApplet)
/* 2277 */         ht = StringReplace.replaceAll(ht, "EMBED_JS_ORDER", "offline*,applet");
/*      */       else {
/* 2279 */         ht = StringReplace.replaceAll(ht, "EMBED_JS_ORDER", "offline*");
/*      */       }
/*      */ 
/* 2283 */       File embedHtml = new File(build, bundleName + "EmbedExample.html");
/* 2284 */       FileUtil.writeFileAsString(embedHtml.getAbsolutePath(), ht);
/*      */ 
/* 2286 */       always.println("[JavaScript] JS Download Embed Script finished");
/*      */ 
/* 2289 */       if (buildWindowsWrappers)
/*      */       {
/* 2291 */         for (int BITS = 32; BITS <= 64; BITS += 32) {
/* 2292 */           for (int OX = 0; OX < 2; OX++) {
/* 2293 */             boolean OFFLINE = OX == 0;
/*      */ 
/* 2295 */             if ((OFFLINE) || (haveUpdateURL))
/*      */             {
/*      */               File wfile;
/*      */               File wfile;
/* 2298 */               if (OFFLINE) {
/* 2299 */                 always.println("[Windows] Building Windows Offline wrapper");
/* 2300 */                 wfile = new File(build, bundleName + "-windows" + BITS + "-offline.exe");
/*      */               } else {
/* 2302 */                 always.println("[Windows] Building Windows Online wrapper");
/* 2303 */                 wfile = new File(build, bundleName + "-windows" + BITS + "-online.exe");
/*      */               }
/*      */               File jre;
/* 2307 */               if (BITS == 32) {
/* 2308 */                 File jre = LaunchFile.getLatestVersionOf(GenericUpdater.JRE_WIN32_APP, build);
/*      */ 
/* 2310 */                 dynamicProps.setProperty("jre_name", GenericUpdater.JRE_WIN32_APP);
/*      */               }
/*      */               else
/*      */               {
/* 2314 */                 jre = LaunchFile.getLatestVersionOf(GenericUpdater.JRE_WIN64_APP, build);
/*      */ 
/* 2316 */                 dynamicProps.setProperty("jre_name", GenericUpdater.JRE_WIN64_APP);
/*      */               }
/*      */ 
/* 2331 */               if (installType.equals("perm_all"))
/*      */               {
/* 2333 */                 FileUtil.copy(new File(wrappers, "windowswrapper_requireAdministrator_0.exe"), wfile);
/*      */               }
/* 2338 */               else if (installType.equals("temp_user"))
/*      */               {
/* 2340 */                 FileUtil.copy(new File(wrappers, getWrapperFor(appElevation, appUiAccess)), wfile);
/*      */               }
/*      */               else {
/* 2343 */                 FileUtil.copy(new File(wrappers, "windowswrapper_noelevation.exe"), wfile);
/*      */               }
/*      */ 
/* 2351 */               OutputStream fout = new BufferedOutputStream(new FileOutputStream(wfile, true));
/*      */ 
/* 2354 */               fout.write(marker);
/*      */ 
/* 2356 */               CFriendlyStreamUtils.writeString(fout, bundleName);
/* 2357 */               if (OFFLINE) {
/* 2358 */                 CFriendlyStreamUtils.writeString(fout, appversion);
/* 2359 */                 CFriendlyStreamUtils.writeString(fout, LaunchFile.pickVersionFromAppArchive(jre));
/*      */               }
/*      */               else {
/* 2362 */                 CFriendlyStreamUtils.writeString(fout, "");
/* 2363 */                 CFriendlyStreamUtils.writeString(fout, "");
/*      */               }
/* 2365 */               CFriendlyStreamUtils.writeString(fout, guversion);
/* 2366 */               CFriendlyStreamUtils.writeString(fout, splashMinMS);
/* 2367 */               if (canOverrideSplash)
/* 2368 */                 CFriendlyStreamUtils.writeString(fout, "1");
/*      */               else
/* 2370 */                 CFriendlyStreamUtils.writeString(fout, "0");
/* 2371 */               CFriendlyStreamUtils.writeString(fout, installType);
/* 2372 */               if (silentInstallParameter != null)
/* 2373 */                 CFriendlyStreamUtils.writeString(fout, silentInstallParameter);
/*      */               else {
/* 2375 */                 CFriendlyStreamUtils.writeString(fout, "");
/*      */               }
/* 2377 */               CFriendlyStreamUtils.writeString(fout, updateURL);
/*      */ 
/* 2384 */               boolean FORCE_WINDOWS_SUBSYSTEM = false;
/*      */ 
/* 2386 */               boolean MAKE_WIN_WRAPPER_USE_CONSOLE = false;
/*      */ 
/* 2388 */               File lzma = new File(build, "lzma.exe");
/* 2389 */               FileUtil.copy(new File(wrappers, "lzma.exe"), lzma);
/* 2390 */               if (FORCE_WINDOWS_SUBSYSTEM) ExeReader.setUseSubsystem(lzma.getAbsolutePath(), false);
/*      */ 
/* 2397 */               File downloadpng = new File(build, "nativesplash.png");
/* 2398 */               FileUtil.copy(bundleSplash, downloadpng);
/*      */ 
/* 2460 */               File verPack = new File(build, "verpatch.exe.l2");
/* 2461 */               FileUtil.copy(new File(wrappers, "verpatch.exe"), verPack);
/* 2462 */               if (FORCE_WINDOWS_SUBSYSTEM) ExeReader.setUseSubsystem(verPack.getAbsolutePath(), false);
/* 2463 */               LzmaUtil.compress(verPack);
/*      */               File[] files;
/*      */               File[] files;
/* 2466 */               if (OFFLINE) {
/* 2467 */                 files = new File[] { 
/* 2468 */                   lzma, 
/* 2470 */                   downloadpng, 
/* 2471 */                   verPack, 
/* 2474 */                   guArchive, 
/* 2475 */                   guVersionFile, 
/* 2476 */                   jre, 
/* 2477 */                   appArchive, 
/* 2478 */                   appVersionFile };
/*      */               }
/*      */               else {
/* 2481 */                 files = new File[] { 
/* 2482 */                   lzma, 
/* 2484 */                   downloadpng, 
/* 2485 */                   verPack, 
/* 2488 */                   guArchive, 
/* 2489 */                   guVersionFile };
/*      */               }
/*      */ 
/* 2495 */               byte[] buf = new byte[200000];
/* 2496 */               for (int i = 0; i < files.length; i++) {
/* 2497 */                 if (files[i] != null) {
/* 2498 */                   Archive.addFileToStream(fout, buf, files[i], files[i].getName(), appStrippers);
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 2516 */               FileUtil.deleteDir(downloadpng);
/* 2517 */               FileUtil.deleteDir(verPack);
/*      */ 
/* 2520 */               FileUtil.deleteDir(lzma);
/*      */ 
/* 2526 */               if (winSignKeystore == null) {
/* 2527 */                 fout.write(jwparams.getParameterisedBlock(dynamicProps, PARAMS_BLOCK_LEN));
/*      */               }
/*      */ 
/* 2531 */               fout.close();
/*      */ 
/* 2533 */               File icofile = new File(wfile.getAbsolutePath() + "_icon.exe");
/*      */ 
/* 2535 */               always.println("[Windows] Embedding icon...");
/*      */ 
/* 2537 */               ExeReader.embedIcons(wfile.getAbsolutePath(), icofile.getAbsolutePath(), logo16, logo24, logo32, logo48, logo256);
/*      */ 
/* 2539 */               if (MAKE_WIN_WRAPPER_USE_CONSOLE) {
/* 2540 */                 ExeReader.setUseSubsystem(icofile.getAbsolutePath(), true);
/*      */               }
/*      */ 
/* 2543 */               if (!wfile.delete()) {
/* 2544 */                 throw new Exception("Unable to delete original EXE " + wfile);
/*      */               }
/*      */ 
/* 2547 */               if (winSignKeystore != null) {
/* 2548 */                 always.println("[Windows] Signing exe " + wfile + "...");
/*      */ 
/* 2550 */                 int X = 0;
/*      */                 while (true) {
/* 2552 */                   X++;
/*      */                   try
/*      */                   {
/* 2555 */                     time();
/* 2556 */                     new AuthenticodeSigner(icofile, wfile, bundleName, winSignTimestampURL, winSignKeystore).sign(jwparams.getParameterisedBlock(dynamicProps, PARAMS_BLOCK_LEN));
/* 2557 */                     signingTime += time();
/* 2558 */                     always.println("[Windows] Signed OK");
/*      */                   }
/*      */                   catch (IOException x) {
/* 2561 */                     if (X == 10) {
/* 2562 */                       throw x;
/*      */                     }
/* 2564 */                     always.println("[Windows] Problem with signing (could be a timestamp URL issue, will try again in 3s...)");
/* 2565 */                     Thread.sleep(3000L);
/*      */                   }
/*      */                 }
/*      */ 
/* 2569 */                 if (!icofile.delete()) {
/* 2570 */                   throw new Exception("Unable to delete temporary unsigned EXE");
/*      */                 }
/*      */               }
/* 2573 */               else if (!icofile.renameTo(wfile)) {
/* 2574 */                 throw new Exception("Unable to rename iconified to original EXE");
/*      */               }
/*      */ 
/* 2578 */               always.println("[Windows] Windows wrapper finished");
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2583 */       if (buildLinuxWrappers)
/*      */       {
/* 2585 */         for (int BITS = 32; BITS <= 64; BITS += 32) {
/* 2586 */           for (int OX = 0; OX < 2; OX++) {
/* 2587 */             boolean OFFLINE = OX == 0;
/*      */ 
/* 2589 */             if ((OFFLINE) || (haveUpdateURL))
/*      */             {
/*      */               File wfile;
/*      */               File wfile;
/* 2592 */               if (OFFLINE) {
/* 2593 */                 always.println("[Linux] Building Linux Offline wrapper");
/* 2594 */                 wfile = new File(build, bundleName + "-linux" + BITS + "-offline");
/*      */               } else {
/* 2596 */                 always.println("[Linux] Building Linux Online wrapper");
/* 2597 */                 wfile = new File(build, bundleName + "-linux" + BITS + "-online");
/*      */               }
/*      */               File jre;
/* 2601 */               if (BITS == 32) {
/* 2602 */                 File jre = LaunchFile.getLatestVersionOf(GenericUpdater.JRE_LIN32_APP, build);
/*      */ 
/* 2604 */                 dynamicProps.setProperty("jre_name", GenericUpdater.JRE_LIN32_APP);
/*      */               }
/*      */               else
/*      */               {
/* 2608 */                 jre = LaunchFile.getLatestVersionOf(GenericUpdater.JRE_LIN64_APP, build);
/*      */ 
/* 2610 */                 dynamicProps.setProperty("jre_name", GenericUpdater.JRE_LIN64_APP);
/*      */               }
/*      */ 
/* 2621 */               if (installType.equals("perm_all"))
/*      */               {
/* 2623 */                 FileUtil.copy(new File(wrappers, "linuxwrapper" + BITS + "_su"), wfile);
/*      */               }
/*      */               else {
/* 2626 */                 FileUtil.copy(new File(wrappers, "linuxwrapper" + BITS), wfile);
/*      */               }
/*      */ 
/* 2630 */               OutputStream fout = new BufferedOutputStream(new FileOutputStream(wfile, true));
/*      */ 
/* 2633 */               fout.write(marker);
/*      */ 
/* 2635 */               CFriendlyStreamUtils.writeString(fout, bundleName);
/* 2636 */               if (OFFLINE) {
/* 2637 */                 CFriendlyStreamUtils.writeString(fout, appversion);
/* 2638 */                 CFriendlyStreamUtils.writeString(fout, LaunchFile.pickVersionFromAppArchive(jre));
/*      */               }
/*      */               else {
/* 2641 */                 CFriendlyStreamUtils.writeString(fout, "");
/* 2642 */                 CFriendlyStreamUtils.writeString(fout, "");
/*      */               }
/* 2644 */               CFriendlyStreamUtils.writeString(fout, guversion);
/* 2645 */               CFriendlyStreamUtils.writeString(fout, splashMinMS);
/* 2646 */               if (canOverrideSplash)
/* 2647 */                 CFriendlyStreamUtils.writeString(fout, "1");
/*      */               else
/* 2649 */                 CFriendlyStreamUtils.writeString(fout, "0");
/* 2650 */               CFriendlyStreamUtils.writeString(fout, installType);
/* 2651 */               if (silentInstallParameter != null)
/* 2652 */                 CFriendlyStreamUtils.writeString(fout, silentInstallParameter);
/*      */               else {
/* 2654 */                 CFriendlyStreamUtils.writeString(fout, "");
/*      */               }
/* 2656 */               CFriendlyStreamUtils.writeString(fout, updateURL);
/*      */ 
/* 2664 */               File lzma = new File(build, "lzma" + BITS);
/* 2665 */               FileUtil.copy(new File(wrappers, "lzma" + BITS), lzma);
/*      */ 
/* 2671 */               File downloadpng = new File(build, "nativesplash.png");
/* 2672 */               FileUtil.copyFileOrDir(bundleSplash, downloadpng);
/*      */               File[] files;
/*      */               File[] files;
/* 2711 */               if (OFFLINE) {
/* 2712 */                 files = new File[] { 
/* 2713 */                   lzma, 
/* 2715 */                   downloadpng, 
/* 2717 */                   guArchive, 
/* 2718 */                   guVersionFile, 
/* 2719 */                   jre, 
/* 2720 */                   appArchive, 
/* 2721 */                   appVersionFile };
/*      */               }
/*      */               else {
/* 2724 */                 files = new File[] { 
/* 2725 */                   lzma, 
/* 2727 */                   downloadpng, 
/* 2729 */                   guArchive, 
/* 2730 */                   guVersionFile };
/*      */               }
/*      */ 
/* 2735 */               byte[] buf = new byte[200000];
/* 2736 */               for (int i = 0; i < files.length; i++) {
/* 2737 */                 if (files[i] != null) {
/* 2738 */                   Archive.addFileToStream(fout, buf, files[i], files[i].getName(), appStrippers);
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 2746 */               FileUtil.deleteDir(downloadpng);
/* 2747 */               FileUtil.deleteDir(lzma);
/*      */ 
/* 2753 */               fout.write(jwparams.getParameterisedBlock(dynamicProps, PARAMS_BLOCK_LEN));
/*      */ 
/* 2755 */               fout.close();
/*      */ 
/* 2758 */               addAllToNewTar(new File(build, wfile.getName() + ".tar"), new File[] { wfile });
/*      */ 
/* 2760 */               wfile.delete();
/*      */ 
/* 2762 */               always.println("[Linux] Linux wrapper finished");
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2767 */       if (buildMacOSWrappers)
/*      */       {
/* 2769 */         for (int OX = 0; OX < 2; OX++) {
/* 2770 */           boolean OFFLINE = OX == 0;
/*      */ 
/* 2772 */           if (OFFLINE)
/* 2773 */             always.println("[MacOS] Building MacOS Offline wrapper");
/*      */           else {
/* 2775 */             always.println("[MacOS] Building MacOS Online wrapper");
/*      */           }
/*      */ 
/* 2778 */           if ((OFFLINE) || (haveUpdateURL))
/*      */           {
/* 2780 */             dynamicProps.setProperty("jre_name", "-");
/*      */ 
/* 2782 */             Properties jwp = new Properties();
/* 2783 */             jwp.setProperty("app_name", bundleName);
/* 2784 */             if (OFFLINE)
/*      */             {
/* 2786 */               jwp.setProperty("wrapper_app_version", appversion);
/*      */             }
/* 2788 */             jwp.setProperty("wrapper_gu_version", guversion);
/* 2789 */             jwp.setProperty("min_splash_ms", splashMinMS);
/* 2790 */             if (canOverrideSplash)
/* 2791 */               jwp.setProperty("can_override_splash", "1");
/*      */             else
/* 2793 */               jwp.setProperty("can_override_splash", "0");
/* 2794 */             jwp.setProperty("install_type", installType);
/* 2795 */             if (silentInstallParameter != null) {
/* 2796 */               jwp.setProperty("silent_parameter", silentInstallParameter);
/*      */             }
/* 2798 */             File jwprops = new File(build, "jwrapper_properties");
/* 2799 */             OutputStream out = new BufferedOutputStream(new FileOutputStream(jwprops));
/* 2800 */             jwp.store(out, "");
/* 2801 */             out.close();
/*      */             File[] files;
/*      */             File[] files;
/* 2804 */             if (OFFLINE) {
/* 2805 */               files = new File[] { 
/* 2806 */                 jwprops, 
/* 2807 */                 guArchive, 
/* 2808 */                 guVersionFile, 
/* 2809 */                 appArchive, 
/* 2810 */                 appVersionFile };
/*      */             }
/*      */             else {
/* 2813 */               files = new File[] { 
/* 2814 */                 jwprops, 
/* 2815 */                 guArchive, 
/* 2816 */                 guVersionFile };
/*      */             }
/*      */ 
/* 2821 */             FileUtil.deleteDir(new File(build, "osxwrapper.jar"));
/* 2822 */             addFilesToExistingZip(new File(wrappers, "osxwrapper.jar"), new File(build, "osxwrapper.jar"), files);
/*      */ 
/* 2824 */             File dotApp = new File(build, bundleName + ".app");
/*      */ 
/* 2827 */             FileUtil.deleteDir(dotApp);
/* 2828 */             FileUtil.copyFileOrDir(new File(wrappers, "Blank.app"), dotApp);
/*      */ 
/* 2831 */             FileUtil.removeCVS(dotApp);
/*      */ 
/* 2834 */             File appParams = new File(dotApp, "Contents" + File.separator + "Resources" + File.separator + "AppParams.excludefromsigning");
/* 2835 */             appParams.mkdirs();
/* 2836 */             appParams.delete();
/* 2837 */             FileOutputStream apout = new FileOutputStream(appParams);
/* 2838 */             apout.write(jwparams.getParameterisedBlock(dynamicProps, PARAMS_BLOCK_LEN));
/* 2839 */             apout.close();
/*      */ 
/* 2844 */             File icns = new File(dotApp, "Contents" + File.separator + "Resources" + File.separator + OSXWrapper.getICNSFileName());
/* 2845 */             FileUtil.copyFileOrDir(appICNS, icns);
/*      */ 
/* 2847 */             File splash = new File(dotApp, "Contents" + File.separator + "Resources" + File.separator + OSXWrapper.getAppSplashName());
/* 2848 */             FileUtil.copyFileOrDir(bundleSplash, splash);
/*      */ 
/* 2851 */             File osxWrapperElevate = new File(dotApp, "Contents" + File.separator + "Resources" + File.separator + "elev_mac");
/* 2852 */             FileUtil.copyFileOrDir(new File(wrappers, "elev_mac"), osxWrapperElevate);
/*      */ 
/* 2854 */             File plistFile = new File(dotApp, "Contents" + File.separator + "Info.plist");
/*      */ 
/* 2856 */             String plist = FileUtil.readFileAsString(plistFile.getAbsolutePath());
/*      */ 
/* 2858 */             StringBuffer longEmptyForSH = new StringBuffer();
/* 2859 */             for (int i = 0; i < 2048; i++) longEmptyForSH.append(' ');
/*      */ 
/* 2861 */             int index = plist.indexOf("OSX_JW_ARGUMENT_0_HERE");
/* 2862 */             String result = plist.substring(0, index) + 
/* 2863 */               updateURL + 
/* 2864 */               longEmptyForSH.toString() + 
/* 2865 */               plist.substring(index + "OSX_JW_ARGUMENT_1_HERE".length());
/*      */ 
/* 2867 */             result = result.replace("OSX_JW_BUNDLE_NAME", bundleName);
/* 2868 */             result = result.replace("OSX_JW_BUNDLE_ID", "jwrapper.osx." + normaliseID(bundleName));
/*      */ 
/* 2870 */             FileUtil.writeFile(plistFile, result.getBytes("UTF8"));
/*      */ 
/* 2872 */             File javadir = new File(dotApp, "Contents" + File.separator + "Resources" + File.separator + "Java");
/* 2873 */             javadir.mkdirs();
/*      */ 
/* 2875 */             FileUtil.copyFileOrDir(new File(build, "osxwrapper.jar"), new File(javadir, "osxwrapper.jar"));
/* 2876 */             FileUtil.copyFileOrDir(new File("lib", "sevenzip.jar"), new File(javadir, "sevenzip.jar"));
/*      */ 
/* 2878 */             FileUtil.deleteDir(new File(build, "osxwrapper.jar"));
/* 2879 */             FileUtil.deleteDir(new File(build, "jwrapper_properties"));
/*      */ 
/* 2881 */             if (macCertInfo != null) {
/* 2882 */               always.println("[MacOS] Signing OSX App");
/* 2883 */               time();
/* 2884 */               MacSigner.signApp(macCertInfo, new File(build, bundleName + ".app"));
/* 2885 */               signingTime += time();
/*      */ 
/* 2887 */               FileUtil.deleteDir(new File(dotApp, "Contents" + File.separator + "MacOS" + File.separator + "JavaApplicationStub.backup"));
/*      */             }
/*      */             String name;
/*      */             String name;
/* 2891 */             if (OFFLINE)
/* 2892 */               name = bundleName + "-macos-offline";
/*      */             else {
/* 2894 */               name = bundleName + "-macos-online"; } always.println("[MacOS] Compressing...");
/*      */ 
/* 2899 */             DSConfig config = new DSConfig();
/* 2900 */             config.showSidebar = false;
/* 2901 */             config.showToolbar = false;
/* 2902 */             config.keepArrangedBy = "none";
/*      */ 
/* 2904 */             config.windowBounds = new Rectangle(350, 250, 600, 
/* 2905 */               osxBgHeight + config.iconSize + config.iconSize / 2 + 25);
/*      */ 
/* 2910 */             config.yCoordOfArrow = (osxBgHeight + config.iconSize / 2 + 7);
/* 2911 */             config.locations = new DSConfig.FileLocation[] { 
/* 2912 */               new DSConfig.FileLocation("Applications", new Point(450, config.yCoordOfArrow)), 
/* 2913 */               new DSConfig.FileLocation(bundleName + ".app", new Point(160, config.yCoordOfArrow)) };
/*      */ 
/* 2917 */             config.dmgName = shortBundleName;
/*      */ 
/* 2925 */             long estimatedSize = 0L;
/* 2926 */             estimatedSize += DmgInstallerBuilder.estimateSizeOf(dotApp);
/* 2927 */             estimatedSize += DmgInstallerBuilder.getSizeOfAncillaryFiles();
/* 2928 */             estimatedSize += appICNS.length();
/* 2929 */             estimatedSize += appOsxBG.length();
/* 2930 */             estimatedSize += 15360L;
/*      */ 
/* 2934 */             System.out.println("Approx Size: " + estimatedSize);
/*      */ 
/* 2936 */             estimatedSize += 10240000L;
/*      */ 
/* 2938 */             File dmgFile = new File(build, name + ".dmg");
/*      */             DmgInstallerBuilder dmg;
/*      */             try { dmg = new DmgInstallerBuilder(dmgFile, estimatedSize / 1024L, config.dmgName); }
/*      */             catch (IllegalArgumentException x)
/*      */             {
/*      */               DmgInstallerBuilder dmg;
/* 2944 */               config.dmgName = "Install";
/*      */ 
/* 2946 */               dmg = new DmgInstallerBuilder(dmgFile, estimatedSize / 1024L, config.dmgName);
/*      */             }
/* 2948 */             dmg.setupForApplicationInstall(dotApp, appOsxBG, appICNS, config, dmgSplashSizeBytes);
/* 2949 */             dmg.finish();
/*      */ 
/* 2951 */             InputStream fin = new BufferedInputStream(new FileInputStream(dmgFile));
/*      */ 
/* 2953 */             long zeroString = 0L;
/*      */             while (true)
/*      */             {
/* 2956 */               int n = fin.read();
/* 2957 */               if (n == -1) break;
/* 2958 */               if (n == 0)
/* 2959 */                 zeroString += 1L;
/*      */               else {
/* 2961 */                 zeroString = 0L;
/*      */               }
/*      */             }
/*      */ 
/* 2965 */             fin.close();
/*      */ 
/* 2969 */             zeroString -= 10240L;
/*      */ 
/* 2971 */             long actualSize = 1024L + estimatedSize - zeroString;
/*      */ 
/* 2973 */             System.out.println("Rebuilding to: " + actualSize / 1024L + "k");
/*      */             try
/*      */             {
/* 2976 */               dmg = new DmgInstallerBuilder(dmgFile, actualSize / 1024L, config.dmgName);
/*      */             } catch (IllegalArgumentException x) {
/* 2978 */               config.dmgName = "Install";
/* 2979 */               always.println("[MacOS] Had to give DMG name 'Install' as app name was too long");
/* 2980 */               dmg = new DmgInstallerBuilder(dmgFile, actualSize / 1024L, config.dmgName);
/*      */             }
/* 2982 */             File modifiedBackgroundFile = dmg.setupForApplicationInstall(dotApp, appOsxBG, appICNS, config, dmgSplashSizeBytes);
/* 2983 */             dmg.finish();
/*      */ 
/* 2985 */             if (dmgSplashSizeBytes > 0L)
/*      */             {
/* 2987 */               File localCopyOfModifiedBackgroundFile = new File(build, "osx_bg.png");
/* 2988 */               FileUtil.copy(modifiedBackgroundFile, localCopyOfModifiedBackgroundFile);
/*      */             }
/*      */ 
/* 2992 */             FileUtil.deleteDir(dotApp);
/*      */ 
/* 2994 */             System.out.println("[MacOS] MacOS wrapper finished");
/*      */           }
/*      */         }
/*      */       }
/* 2998 */       System.out.println("Trying to cleanup...");
/* 2999 */       for (int i = 0; i < launchers.size(); i++) {
/* 3000 */         File f = (File)launchers.get(i);
/* 3001 */         System.out.println("Removing " + f);
/* 3002 */         FileUtil.deleteDir(f);
/*      */       }
/*      */ 
/* 3007 */       System.out.println("Removing " + appOsxBG);
/* 3008 */       FileUtil.deleteDir(appOsxBG);
/* 3009 */       System.out.println("Removing " + appICNS);
/* 3010 */       FileUtil.deleteDir(appICNS);
/* 3011 */       System.out.println("Removing " + appUninstallerICO);
/* 3012 */       FileUtil.deleteDir(appUninstallerICO);
/*      */ 
/* 3014 */       always.println("[MacOS] Finished cleanup");
/* 3015 */       System.out.println("Total signing time: " + signingTime);
/* 3016 */       System.out.println("Total LZMA time: " + lzmaTime);
/*      */ 
/* 3018 */       always.println("[FINISHED] JWrapper build is complete");
/*      */ 
/* 3020 */       if (JWCompiler.CMDOPTION_DEMO) {
/* 3021 */         always.println("Starting demo web server for app deployment, visit http://localhost:33333/" + embedHtml.getName());
/* 3022 */         new TinyWebServer(33333, build);
/*      */         while (true) {
/* 3024 */           Thread.sleep(5000L);
/*      */         }
/*      */       }
/*      */ 
/* 3028 */       System.exit(0);
/*      */     }
/*      */     catch (FailException x) {
/* 3031 */       fail(x.getMessage());
/*      */     } catch (Throwable x) {
/* 3033 */       fail(x);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static String normaliseID(String bundleName)
/*      */   {
/* 3049 */     bundleName = bundleName.replaceAll("\\s+", "");
/* 3050 */     return bundleName;
/*      */   }
/*      */ 
/*      */   public static void addAllToNewTGZ(File dest, File[] files, int compressionLevel) throws IOException {
/* 3054 */     GZIPOutputStream gout = new LevelGzipOutputStream(new BufferedOutputStream(new FileOutputStream(dest)), compressionLevel);
/*      */ 
/* 3056 */     TarArchiveOutputStream out = new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(dest)));
/*      */     try
/*      */     {
/* 3060 */       for (int i = 0; i < files.length; i++) {
/* 3061 */         addAllToTar(out, files[i], "");
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3066 */       out.close();
/* 3067 */       gout.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void addAllToNewTar(File dest, File[] files) throws IOException {
/* 3072 */     TarArchiveOutputStream out = new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(dest)));
/*      */     try
/*      */     {
/* 3075 */       for (int i = 0; i < files.length; i++) {
/* 3076 */         addAllToTar(out, files[i], "");
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3081 */       out.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void addAllToTar(TarArchiveOutputStream out, File file, String mypath) throws IOException
/*      */   {
/* 3087 */     if (file.isDirectory())
/*      */     {
/* 3089 */       if (file.getName().equals("CVS")) return;
/*      */ 
/* 3091 */       TarArchiveEntry ze = new TarArchiveEntry(mypath + file.getName() + "/");
/* 3092 */       ze.setMode(509);
/*      */ 
/* 3094 */       out.putArchiveEntry(ze);
/* 3095 */       out.closeArchiveEntry();
/*      */ 
/* 3097 */       String subpath = mypath + file.getName() + "/";
/*      */ 
/* 3099 */       File[] files = file.listFiles();
/* 3100 */       for (int i = 0; i < files.length; i++)
/* 3101 */         addAllToTar(out, files[i], subpath);
/*      */     }
/*      */     else
/*      */     {
/* 3105 */       InputStream in = new BufferedInputStream(new FileInputStream(file));
/*      */ 
/* 3107 */       TarArchiveEntry ze = new TarArchiveEntry(mypath + file.getName());
/*      */ 
/* 3110 */       String name = file.getName();
/* 3111 */       if ((name.endsWith("JavaApplicationStub")) || 
/* 3112 */         (name.endsWith(".exe")) || 
/* 3113 */         (name.endsWith("java")) || 
/* 3114 */         (name.endsWith(".sh")))
/*      */       {
/* 3116 */         System.out.println("Making executable: " + name);
/* 3117 */         ze.setMode(509);
/*      */       } else {
/* 3119 */         ze.setMode(511);
/*      */       }
/*      */ 
/* 3122 */       ze.setSize(file.length());
/*      */ 
/* 3124 */       out.putArchiveEntry(ze);
/*      */       int len;
/* 3127 */       while ((len = in.read(buf)) > 0)
/*      */       {
/*      */         int len;
/* 3128 */         out.write(buf, 0, len);
/*      */       }
/*      */ 
/* 3131 */       out.closeArchiveEntry();
/* 3132 */       in.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void addFilesToExistingZip(File srcFile, File destZip, File[] files)
/*      */     throws IOException
/*      */   {
/* 3140 */     ZipArchiveOutputStream out = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(destZip)));
/* 3141 */     ZipArchiveInputStream zin = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(srcFile)));
/*      */ 
/* 3143 */     ZipArchiveEntry entry = zin.getNextZipEntry();
/* 3144 */     while (entry != null) {
/* 3145 */       String name = entry.getName();
/*      */ 
/* 3156 */       out.putArchiveEntry(new ZipArchiveEntry(name));
/*      */       int len;
/* 3159 */       while ((len = zin.read(buf)) > 0)
/*      */       {
/*      */         int len;
/* 3160 */         out.write(buf, 0, len);
/*      */       }
/*      */ 
/* 3163 */       entry = zin.getNextZipEntry();
/*      */     }
/*      */ 
/* 3166 */     zin.close();
/*      */ 
/* 3169 */     for (int i = 0; i < files.length; i++) {
/* 3170 */       if (!files[i].getName().equals("CVS")) {
/* 3171 */         InputStream in = new BufferedInputStream(new FileInputStream(files[i]));
/*      */ 
/* 3173 */         out.putArchiveEntry(new ZipArchiveEntry(files[i].getName()));
/*      */         int len;
/* 3176 */         while ((len = in.read(buf)) > 0)
/*      */         {
/*      */           int len;
/* 3177 */           out.write(buf, 0, len);
/*      */         }
/*      */ 
/* 3180 */         out.closeArchiveEntry();
/* 3181 */         in.close();
/*      */       }
/*      */     }
/*      */ 
/* 3185 */     out.close();
/*      */   }
/*      */ 
/*      */   static class FailException extends Exception
/*      */   {
/*      */     public FailException(String s)
/*      */     {
/* 3039 */       super();
/*      */     }
/*      */   }
/*      */ 
/*      */   static class UpdateCheck extends Thread
/*      */   {
/*      */     public void run()
/*      */     {
/*  337 */       JWrapperCompiler.always.println("[Updates] Checking for updates...");
/*      */       try
/*      */       {
/*  340 */         URL url = new URL("http://simple-help.com/media/static/jwrapper/version.html");
/*      */ 
/*  342 */         String val = StreamUtils.readAllAsString(url.openStream());
/*      */ 
/*  344 */         CharStack cs = new CharStack(val);
/*  345 */         cs.popUntil("LATEST_BUILD_");
/*  346 */         long version = ()cs.popNumber();
/*      */ 
/*  348 */         long mine = Long.parseLong(GenericUpdater.GetVersion());
/*      */ 
/*  350 */         JWrapperCompiler.always.println("[Updates] Update check: " + mine + " vs " + version + " (online)");
/*      */ 
/*  352 */         if (version > mine) {
/*  353 */           JWrapperCompiler.always.println("[Updates] *********************************************");
/*  354 */           JWrapperCompiler.always.println("[Updates] *           NEW VERSION AVAILABLE           *");
/*  355 */           JWrapperCompiler.always.println("[Updates] *********************************************");
/*      */         }
/*      */       }
/*      */       catch (Exception x) {
/*  359 */         x.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.JWrapperCompiler
 * JD-Core Version:    0.6.2
 */