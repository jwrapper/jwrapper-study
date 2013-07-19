/*    */ package jwrapper;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.BufferedOutputStream;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import utils.stream.StreamUtils;
/*    */ 
/*    */ public class IcoPng
/*    */ {
/*    */   byte[] ico;
/*    */   int pngIndex;
/*    */   int pngLen;
/*    */ 
/*    */   public IcoPng(byte[] ico, int pngIndex, int pngLen)
/*    */   {
/* 20 */     this.ico = ico;
/* 21 */     this.pngIndex = pngIndex;
/* 22 */     this.pngLen = pngLen;
/*    */   }
/*    */ 
/*    */   public IcoPng(File f) throws IOException {
/* 26 */     InputStream in = new BufferedInputStream(new FileInputStream(f));
/* 27 */     this.pngIndex = StreamUtils.readInt(in);
/* 28 */     this.pngLen = StreamUtils.readInt(in);
/* 29 */     this.ico = StreamUtils.readAll(in);
/* 30 */     in.close();
/*    */   }
/*    */ 
/*    */   public void save(File f) throws IOException {
/* 34 */     OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
/* 35 */     StreamUtils.writeInt(out, this.pngIndex);
/* 36 */     StreamUtils.writeInt(out, this.pngLen);
/* 37 */     out.write(this.ico);
/* 38 */     out.close();
/*    */   }
/*    */ 
/*    */   public byte[] getICO() {
/* 42 */     return this.ico;
/*    */   }
/*    */   public byte[] getPNG() {
/* 45 */     byte[] png = new byte[this.pngLen];
/* 46 */     System.arraycopy(this.ico, this.pngIndex, png, 0, this.pngLen);
/* 47 */     return png;
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.IcoPng
 * JD-Core Version:    0.6.2
 */