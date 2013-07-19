/*    */ package utils.stream;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ 
/*    */ public class OpenByteArrayOutputStream extends ByteArrayOutputStream
/*    */ {
/*    */   public OpenByteArrayOutputStream()
/*    */   {
/*    */   }
/*    */ 
/*    */   public OpenByteArrayOutputStream(int size)
/*    */   {
/* 23 */     super(size);
/*    */   }
/*    */ 
/*    */   public byte[] getByteArray()
/*    */   {
/* 28 */     return this.buf;
/*    */   }
/*    */ 
/*    */   public void setSize(int count)
/*    */   {
/* 33 */     this.count = count;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.stream.OpenByteArrayOutputStream
 * JD-Core Version:    0.6.2
 */