/*     */ package utils.string;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Random;
/*     */ 
/*     */ public class Base64
/*     */ {
/*     */   private static final char nonAlphabet1 = '+';
/*     */   private static final char nonAlphabet2 = '/';
/*     */   private static final char specialchar = '=';
/*     */   private static final String specialchar2 = "==";
/*  53 */   private static final char[] intToBase64 = { 
/*  54 */     'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 
/*  55 */     'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 
/*  56 */     'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 
/*  57 */     'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
/*  58 */     '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
/*     */ 
/* 132 */   private static byte[] base64ToInt = new byte['ÿ'];
/*     */ 
/* 133 */   static { Arrays.fill(base64ToInt, (byte)-1);
/* 134 */     for (int i = 0; i < intToBase64.length; i++)
/* 135 */       base64ToInt[intToBase64[i]] = ((byte)i);
/*     */   }
/*     */ 
/*     */   public static String byteArrayToBase64(byte[] a)
/*     */   {
/*  14 */     int aLen = a.length;
/*  15 */     int numFullGroups = aLen / 3;
/*  16 */     int numBytesInPartialGroup = aLen - 3 * numFullGroups;
/*  17 */     int resultLen = 4 * ((aLen + 2) / 3);
/*  18 */     StringBuffer result = new StringBuffer(resultLen);
/*  19 */     char[] intToAlpha = intToBase64;
/*     */ 
/*  22 */     int inCursor = 0;
/*  23 */     for (int i = 0; i < numFullGroups; i++) {
/*  24 */       int byte0 = a[(inCursor++)] & 0xFF;
/*  25 */       int byte1 = a[(inCursor++)] & 0xFF;
/*  26 */       int byte2 = a[(inCursor++)] & 0xFF;
/*  27 */       result.append(intToAlpha[(byte0 >> 2)]);
/*  28 */       result.append(intToAlpha[(byte0 << 4 & 0x3F | byte1 >> 4)]);
/*  29 */       result.append(intToAlpha[(byte1 << 2 & 0x3F | byte2 >> 6)]);
/*  30 */       result.append(intToAlpha[(byte2 & 0x3F)]);
/*     */     }
/*     */ 
/*  34 */     if (numBytesInPartialGroup != 0) {
/*  35 */       int byte0 = a[(inCursor++)] & 0xFF;
/*  36 */       result.append(intToAlpha[(byte0 >> 2)]);
/*  37 */       if (numBytesInPartialGroup == 1) {
/*  38 */         result.append(intToAlpha[(byte0 << 4 & 0x3F)]);
/*  39 */         result.append("==");
/*     */       }
/*     */       else {
/*  42 */         int byte1 = a[(inCursor++)] & 0xFF;
/*  43 */         result.append(intToAlpha[(byte0 << 4 & 0x3F | byte1 >> 4)]);
/*  44 */         result.append(intToAlpha[(byte1 << 2 & 0x3F)]);
/*  45 */         result.append('=');
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  50 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public static byte[] base64ToByteArray(String s)
/*     */   {
/*  62 */     byte[] alphaToInt = base64ToInt;
/*  63 */     int sLen = s.length();
/*  64 */     int numGroups = sLen / 4;
/*  65 */     if (4 * numGroups != sLen)
/*  66 */       throw new IllegalArgumentException(
/*  67 */         "String length must be a multiple of four.");
/*  68 */     int missingBytesInLastGroup = 0;
/*  69 */     int numFullGroups = numGroups;
/*  70 */     if (sLen != 0) {
/*  71 */       if (s.charAt(sLen - 1) == '=') {
/*  72 */         missingBytesInLastGroup++;
/*  73 */         numFullGroups--;
/*     */       }
/*  75 */       if (s.charAt(sLen - 2) == '=')
/*  76 */         missingBytesInLastGroup++;
/*     */     }
/*  78 */     byte[] result = new byte[3 * numGroups - missingBytesInLastGroup];
/*     */ 
/*  81 */     int inCursor = 0; int outCursor = 0;
/*  82 */     for (int i = 0; i < numFullGroups; i++) {
/*  83 */       int ch0 = base64toInt(s.charAt(inCursor++), alphaToInt);
/*  84 */       int ch1 = base64toInt(s.charAt(inCursor++), alphaToInt);
/*  85 */       int ch2 = base64toInt(s.charAt(inCursor++), alphaToInt);
/*  86 */       int ch3 = base64toInt(s.charAt(inCursor++), alphaToInt);
/*  87 */       result[(outCursor++)] = ((byte)(ch0 << 2 | ch1 >> 4));
/*  88 */       result[(outCursor++)] = ((byte)(ch1 << 4 | ch2 >> 2));
/*  89 */       result[(outCursor++)] = ((byte)(ch2 << 6 | ch3));
/*     */     }
/*     */ 
/*  93 */     if (missingBytesInLastGroup != 0) {
/*  94 */       int ch0 = base64toInt(s.charAt(inCursor++), alphaToInt);
/*  95 */       int ch1 = base64toInt(s.charAt(inCursor++), alphaToInt);
/*  96 */       result[(outCursor++)] = ((byte)(ch0 << 2 | ch1 >> 4));
/*     */ 
/*  98 */       if (missingBytesInLastGroup == 1) {
/*  99 */         int ch2 = base64toInt(s.charAt(inCursor++), alphaToInt);
/* 100 */         result[(outCursor++)] = ((byte)(ch1 << 4 | ch2 >> 2));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 105 */     return result;
/*     */   }
/*     */ 
/*     */   private static int base64toInt(char c, byte[] alphaToInt)
/*     */   {
/* 116 */     int result = alphaToInt[c];
/* 117 */     if (result < 0)
/* 118 */       throw new IllegalArgumentException("Illegal character " + c + " (" + c + ")");
/* 119 */     return result;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 154 */     int numRuns = 1;
/* 155 */     int numBytes = 20;
/* 156 */     Random rnd = new Random();
/* 157 */     for (int i = 0; i < numRuns; i++)
/*     */     {
/* 159 */       int j = numBytes;
/* 160 */       byte[] arr = new byte[j];
/* 161 */       for (int k = 0; k < j; k++) {
/* 162 */         arr[k] = ((byte)rnd.nextInt());
/*     */       }
/* 164 */       String s = byteArrayToBase64(arr);
/* 165 */       byte[] b = base64ToByteArray(s);
/* 166 */       if (!Arrays.equals(arr, b)) {
/* 167 */         System.out.println("Dismal failure!");
/*     */       }
/* 169 */       System.out.println(s);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.string.Base64
 * JD-Core Version:    0.6.2
 */