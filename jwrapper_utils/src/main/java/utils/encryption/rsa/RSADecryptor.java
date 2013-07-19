/*     */ package utils.encryption.rsa;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ public class RSADecryptor
/*     */   implements RSAConstants
/*     */ {
/*  15 */   private int MAXBITS = 960;
/*  16 */   private int MAXLEN = this.MAXBITS / 8;
/*  17 */   private BigInteger BASE = new BigInteger("2").pow(this.MAXBITS);
/*     */ 
/*  19 */   private boolean CRT = false;
/*     */   private BigInteger[] key;
/*     */ 
/*     */   public RSADecryptor(BigInteger[] private_key)
/*     */   {
/*  27 */     this.key = private_key;
/*     */     BigInteger n;
/*     */     BigInteger n;
/*  31 */     if (this.key.length == 2)
/*     */     {
/*  33 */       this.CRT = false;
/*  34 */       n = this.key[0];
/*     */     }
/*     */     else {
/*  37 */       this.CRT = true;
/*  38 */       n = this.key[5];
/*     */     }
/*     */ 
/*  41 */     this.MAXBITS = ((int)(n.bitLength() * 0.9D));
/*  42 */     this.MAXLEN = (this.MAXBITS / 8);
/*  43 */     this.MAXBITS = (this.MAXLEN * 8);
/*  44 */     this.BASE = new BigInteger("2").pow(this.MAXBITS);
/*     */   }
/*     */ 
/*     */   private BigInteger readAndDecrypt(int siz, InputStream in)
/*     */     throws IOException
/*     */   {
/*  53 */     byte[] tmp = new byte[siz];
/*  54 */     int total = 0;
/*  55 */     while (total < tmp.length) {
/*  56 */       int x = in.read(tmp, total, tmp.length - total);
/*  57 */       if (x > 0) total += x;
/*     */ 
/*     */     }
/*     */ 
/*  61 */     BigInteger ciphertext = new BigInteger(tmp);
/*     */ 
/*  63 */     if (this.CRT)
/*     */     {
/*  65 */       BigInteger cDp = ciphertext.modPow(this.key[2], this.key[0]);
/*  66 */       BigInteger cDq = ciphertext.modPow(this.key[3], this.key[1]);
/*  67 */       BigInteger u = cDq.subtract(cDp).multiply(this.key[4]).remainder(this.key[1]);
/*  68 */       if (u.compareTo(BigInteger.ZERO) < 0) u = u.add(this.key[1]);
/*  69 */       ciphertext = cDp.add(u.multiply(this.key[0]));
/*     */     }
/*     */     else
/*     */     {
/*  73 */       ciphertext = ciphertext.modPow(this.key[1], this.key[0]);
/*     */     }
/*     */ 
/*  76 */     ciphertext = ciphertext.subtract(this.BASE);
/*     */ 
/*  78 */     return ciphertext;
/*     */   }
/*     */ 
/*     */   private int readN(InputStream in) throws IOException
/*     */   {
/*  83 */     int n = 0;
/*     */ 
/*  85 */     n = in.read() << 24;
/*  86 */     n |= in.read() << 16;
/*  87 */     n |= in.read() << 8;
/*  88 */     n |= in.read();
/*     */ 
/*  90 */     return n;
/*     */   }
/*     */ 
/*     */   public byte[] decrypt(byte[] data)
/*     */   {
/* 101 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/*     */     try
/*     */     {
/* 106 */       byte[] writedat = new byte[this.MAXLEN];
/* 107 */       ByteArrayInputStream bin = new ByteArrayInputStream(data);
/*     */ 
/* 109 */       int n = 0;
/*     */ 
/* 111 */       n = readN(bin);
/*     */ 
/* 113 */       BigInteger origlen = readAndDecrypt(n, bin);
/* 114 */       byte[] origdat = new byte[origlen.intValue()];
/*     */ 
/* 116 */       n = readN(bin);
/*     */ 
/* 118 */       while (n != -1)
/*     */       {
/* 120 */         for (int i = 0; i < writedat.length; i++) writedat[i] = 0;
/*     */ 
/* 122 */         BigInteger ciphertext = readAndDecrypt(n, bin);
/*     */ 
/* 124 */         byte[] plaintext = ciphertext.toByteArray();
/* 125 */         int tmpn = this.MAXLEN - plaintext.length;
/* 126 */         System.arraycopy(plaintext, 0, writedat, tmpn, plaintext.length);
/*     */ 
/* 133 */         bout.write(writedat);
/*     */ 
/* 135 */         n = readN(bin);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 141 */       return null;
/*     */     }
/* 146 */     byte[] origdat;
/* 145 */     byte[] tmp = bout.toByteArray();
/* 146 */     if (origdat.length > tmp.length) return null;
/*     */ 
/* 148 */     System.arraycopy(tmp, 0, origdat, 0, origdat.length);
/*     */ 
/* 150 */     return origdat;
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.encryption.rsa.RSADecryptor
 * JD-Core Version:    0.6.2
 */