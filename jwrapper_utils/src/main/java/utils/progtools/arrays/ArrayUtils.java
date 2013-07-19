/*    */ package utils.progtools.arrays;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class ArrayUtils
/*    */ {
/*    */   public static Object[] concat(Object[] first, Object[] second)
/*    */   {
/* 10 */     Object[] result = new Object[first.length + second.length];
/* 11 */     System.arraycopy(first, 0, result, 0, first.length);
/* 12 */     System.arraycopy(second, 0, result, first.length, second.length);
/* 13 */     return result;
/*    */   }
/*    */ 
/*    */   public static String[] concat(String[] first, String[] second)
/*    */   {
/* 18 */     String[] result = new String[first.length + second.length];
/* 19 */     System.arraycopy(first, 0, result, 0, first.length);
/* 20 */     System.arraycopy(second, 0, result, first.length, second.length);
/* 21 */     return result;
/*    */   }
/*    */ 
/*    */   public static int[] concat(int[] first, int[] second)
/*    */   {
/* 27 */     int[] result = new int[first.length + second.length];
/* 28 */     System.arraycopy(first, 0, result, 0, first.length);
/* 29 */     System.arraycopy(second, 0, result, first.length, second.length);
/* 30 */     return result;
/*    */   }
/*    */ 
/*    */   public static double[] concat(double[] first, double[] second)
/*    */   {
/* 35 */     double[] result = new double[first.length + second.length];
/* 36 */     System.arraycopy(first, 0, result, 0, first.length);
/* 37 */     System.arraycopy(second, 0, result, first.length, second.length);
/* 38 */     return result;
/*    */   }
/*    */ 
/*    */   public static byte[] concat(byte[] first, byte[] second)
/*    */   {
/* 43 */     byte[] result = new byte[first.length + second.length];
/* 44 */     System.arraycopy(first, 0, result, 0, first.length);
/* 45 */     System.arraycopy(second, 0, result, first.length, second.length);
/* 46 */     return result;
/*    */   }
/*    */ 
/*    */   public static String[] pop(String[] orig, int index) {
/* 50 */     String[] tmp = new String[orig.length - 1];
/* 51 */     int N = 0;
/* 52 */     for (int i = 0; i < orig.length; i++) {
/* 53 */       if (i != index) {
/* 54 */         tmp[(N++)] = orig[i];
/*    */       }
/*    */     }
/* 57 */     return tmp;
/*    */   }
/*    */ 
/*    */   public static String[] popFirst(String[] orig) {
/* 61 */     return subArray(orig, 1, orig.length - 1);
/*    */   }
/*    */ 
/*    */   public static String[] subArray(String[] orig, int index, int len) {
/* 65 */     String[] tmp = new String[len];
/* 66 */     System.arraycopy(orig, index, tmp, 0, len);
/* 67 */     return tmp;
/*    */   }
/*    */ 
/*    */   public static ArrayList toList(Object[] array) {
/* 71 */     ArrayList list = new ArrayList();
/* 72 */     for (int i = 0; i < array.length; i++) {
/* 73 */       list.add(array[i]);
/*    */     }
/* 75 */     return list;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.progtools.arrays.ArrayUtils
 * JD-Core Version:    0.6.2
 */