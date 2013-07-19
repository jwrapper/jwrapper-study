/*    */ package utils.stream;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import java.util.Properties;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class CFriendlyProperties
/*    */ {
/*    */   public static byte[] encode(Properties props)
/*    */     throws IOException
/*    */   {
/* 13 */     Object[] keys = props.keySet().toArray();
/* 14 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/*    */ 
/* 18 */     for (int i = 0; i < keys.length; i++) {
/* 19 */       String key = (String)keys[i];
/* 20 */       String value = (String)props.get(key);
/* 21 */       if (value == null) value = "";
/*    */ 
/* 23 */       CFriendlyStreamUtils.writeString(bout, key);
/* 24 */       CFriendlyStreamUtils.writeString(bout, value);
/*    */     }
/*    */ 
/* 27 */     bout.flush();
/* 28 */     return bout.toByteArray();
/*    */   }
/*    */ 
/*    */   public static Properties decode(byte[] dat) throws IOException {
/* 32 */     ByteArrayInputStream bin = new ByteArrayInputStream(dat);
/*    */ 
/* 34 */     Properties props = new Properties();
/*    */     try
/*    */     {
/*    */       while (true)
/*    */       {
/* 40 */         String key = CFriendlyStreamUtils.readString(bin);
/* 41 */         String value = CFriendlyStreamUtils.readString(bin);
/*    */ 
/* 43 */         if ((value.length() == 1) && (value.charAt(0) == '\004'))
/*    */         {
/* 45 */           value = "";
/*    */         }
/* 47 */         props.setProperty(key, value);
/*    */ 
/* 49 */         if ((value != null) && (value.length() > 200))
/* 50 */           System.out.println("JW Launch Prop: " + key + "=[" + value.substring(0, 200) + "...]");
/*    */         else
/* 52 */           System.out.println("JW Launch Prop: " + key + "=[" + value + "]"); 
/*    */       }
/*    */     } catch (IOException localIOException) {  }
/*    */ 
/* 55 */     return props;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.stream.CFriendlyProperties
 * JD-Core Version:    0.6.2
 */