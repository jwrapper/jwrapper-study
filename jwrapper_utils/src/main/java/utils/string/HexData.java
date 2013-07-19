/*    */ package utils.string;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class HexData
/*    */ {
/* 12 */   private static char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*    */ 
/*    */   public static void byteToHex(byte b, StringBuffer buf) {
/* 15 */     int high = (b & 0xF0) >> 4;
/* 16 */     int low = b & 0xF;
/* 17 */     buf.append(hexChars[high]);
/* 18 */     buf.append(hexChars[low]);
/*    */   }
/*    */ 
/*    */   private static byte hexToByte(byte b)
/*    */   {
/* 23 */     if ((b >= 47) && (b <= 57))
/* 24 */       return (byte)(b - 48);
/* 25 */     if (b < 97) {
/* 26 */       return (byte)(b - 55);
/*    */     }
/* 28 */     return (byte)(b - 87);
/*    */   }
/*    */ 
/*    */   public static String stringToHexString(String s) {
/* 32 */     return byteArrayToHexString(s.getBytes());
/*    */   }
/*    */ 
/*    */   public static String hexStringToString(String s) {
/* 36 */     return new String(hexStringToByteArray(s));
/*    */   }
/*    */ 
/*    */   public static String byteArrayToHexString(byte[] bytes, int start, int length) {
/* 40 */     StringBuffer sb = new StringBuffer();
/* 41 */     for (int i = start; i < start + length; i++) {
/* 42 */       byteToHex(bytes[i], sb);
/*    */     }
/* 44 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   public static String byteArrayToHexString(byte[] bytes)
/*    */   {
/* 49 */     return byteArrayToHexString(bytes, 0, bytes.length);
/*    */   }
/*    */ 
/*    */   public static byte[] hexStringToByteArray(String s) {
/* 53 */     byte[] string_bytes = s.getBytes();
/* 54 */     byte[] output_bytes = new byte[string_bytes.length / 2];
/* 55 */     for (int i = 0; i < string_bytes.length; i += 2) {
/* 56 */       int hi = hexToByte(string_bytes[i]) << 4;
/* 57 */       int lo = hexToByte(string_bytes[(i + 1)]);
/* 58 */       byte b = (byte)(hi | lo);
/* 59 */       output_bytes[(i / 2)] = b;
/*    */     }
/* 61 */     return output_bytes;
/*    */   }
/*    */ 
/*    */   public static void printAsHexWithSpaces(byte[] encoded)
/*    */   {
/* 66 */     String result = byteArrayToHexString(encoded);
/* 67 */     for (int i = 0; i < result.length(); i += 2)
/*    */     {
/* 69 */       System.out.print(result.charAt(i));
/* 70 */       System.out.print(result.charAt(i + 1));
/* 71 */       System.out.print(' ');
/*    */     }
/* 73 */     System.out.println();
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.string.HexData
 * JD-Core Version:    0.6.2
 */