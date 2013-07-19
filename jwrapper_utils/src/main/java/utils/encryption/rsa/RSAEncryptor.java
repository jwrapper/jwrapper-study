/*    */ package utils.encryption.rsa;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ public class RSAEncryptor
/*    */   implements RSAConstants
/*    */ {
/* 13 */   private int MAXBITS = 960;
/* 14 */   private int MAXLEN = this.MAXBITS / 8;
/* 15 */   private BigInteger BASE = new BigInteger("2").pow(this.MAXBITS);
/*    */   private BigInteger[] key;
/*    */ 
/*    */   public RSAEncryptor(BigInteger[] public_key)
/*    */   {
/* 24 */     this.key = public_key;
/*    */ 
/* 26 */     BigInteger n = this.key[0];
/* 27 */     this.MAXBITS = ((int)(n.bitLength() * 0.9D));
/* 28 */     this.MAXLEN = (this.MAXBITS / 8);
/* 29 */     this.MAXBITS = (this.MAXLEN * 8);
/* 30 */     this.BASE = new BigInteger("2").pow(this.MAXBITS);
/*    */   }
/*    */ 
/*    */   private void encryptAndWrite(BigInteger toencrypt, OutputStream out)
/*    */     throws IOException
/*    */   {
/* 39 */     toencrypt = toencrypt.add(this.BASE);
/* 40 */     toencrypt = toencrypt.modPow(this.key[1], this.key[0]);
/* 41 */     byte[] b = toencrypt.toByteArray();
/*    */ 
/* 44 */     int x = b.length;
/* 45 */     out.write((byte)(x >>> 24 & 0xFF));
/* 46 */     out.write((byte)(x >>> 16 & 0xFF));
/* 47 */     out.write((byte)(x >>> 8 & 0xFF));
/* 48 */     out.write((byte)(x >>> 0 & 0xFF));
/*    */ 
/* 51 */     out.write(b);
/*    */   }
/*    */ 
/*    */   public byte[] encrypt(byte[] data)
/*    */   {
/* 62 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/*    */     try
/*    */     {
/* 66 */       byte[] tmp = new byte[this.MAXLEN];
/* 67 */       int n = 0;
/*    */ 
/* 69 */       encryptAndWrite(new BigInteger(data.length), bout);
/*    */ 
/* 71 */       while (n < data.length)
/*    */       {
/* 73 */         for (int i = 0; i < tmp.length; i++) tmp[i] = 0;
/*    */ 
/* 75 */         System.arraycopy(data, n, tmp, 0, Math.min(tmp.length, data.length - n));
/*    */ 
/* 83 */         BigInteger intdat = new BigInteger(tmp);
/*    */ 
/* 85 */         encryptAndWrite(intdat, bout);
/*    */ 
/* 87 */         n += this.MAXLEN;
/*    */       }
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 92 */       return null;
/*    */     }
/*    */ 
/* 96 */     return bout.toByteArray();
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.encryption.rsa.RSAEncryptor
 * JD-Core Version:    0.6.2
 */