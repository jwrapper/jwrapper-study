/*     */ package utils.stream;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.Properties;
/*     */ import java.util.Random;
/*     */ 
/*     */ public class StreamParameteriser
/*     */ {
/*  12 */   boolean done = false;
/*     */ 
/*  14 */   boolean writing = false;
/*     */ 
/*  16 */   int absPos = 0;
/*     */ 
/*  18 */   int markerPos = 0;
/*     */   byte[] marker;
/*  22 */   int payloadPos = 0;
/*     */   byte[] payload;
/*     */ 
/*     */   public StreamParameteriser(byte[] marker, byte[] payload)
/*     */   {
/*  27 */     this.marker = marker;
/*  28 */     this.payload = payload;
/*     */   }
/*     */ 
/*     */   public void nextBlockToBeTransferred(byte[] dat, int off, int len) {
/*  32 */     if (!this.done)
/*     */     {
/*  34 */       int i = 0;
/*  35 */       int tot = off + len;
/*     */ 
/*  37 */       if (!this.writing)
/*     */       {
/*  39 */         for (; i < tot; i++)
/*     */         {
/*  41 */           this.absPos += 1;
/*     */ 
/*  43 */           if (dat[i] == this.marker[this.markerPos]) {
/*  44 */             this.markerPos += 1;
/*  45 */             if (this.markerPos == this.marker.length)
/*     */             {
/*  47 */               System.out.println("[StreamParameteriser] Writing " + this.payload.length + " bytes of data at " + this.absPos + " (marker found at " + (this.absPos - this.marker.length) + ")");
/*     */ 
/*  49 */               this.writing = true;
/*  50 */               i++; break;
/*     */             }
/*     */           }
/*     */           else {
/*  54 */             this.markerPos = 0;
/*     */           }
/*     */         }
/*     */       }
/*  58 */       if (this.writing)
/*     */       {
/*  60 */         for (; i < tot; i++) {
/*  61 */           dat[i] = this.payload[(this.payloadPos++)];
/*     */ 
/*  63 */           if (this.payloadPos == this.payload.length) {
/*  64 */             this.done = true;
/*  65 */             i++; break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception
/*     */   {
/*  74 */     Random r = new Random();
/*  75 */     int prefix = r.nextInt(100000);
/*  76 */     int postfix = r.nextInt(100000);
/*     */ 
/*  78 */     test(r, prefix, postfix);
/*     */   }
/*     */ 
/*     */   static void test(Random r, int prefix, int postfix) throws Exception
/*     */   {
/*  83 */     System.out.println("Param block will be at " + prefix);
/*     */ 
/*  85 */     GenericParameteriser gp = new GenericParameteriser();
/*     */ 
/*  87 */     File test = new File("StreamParameteriserTest.deleteme");
/*  88 */     File testSP = new File("StreamParameteriserTest-parameterised.deleteme");
/*     */ 
/*  90 */     RandomAccessFile f = new RandomAccessFile(test, "rw");
/*     */ 
/*  92 */     for (int i = 0; i < prefix; i++) {
/*  93 */       f.write((byte)r.nextInt());
/*     */     }
/*  95 */     f.write(gp.getUnparameterisedBlock(10000));
/*  96 */     for (int i = 0; i < postfix; i++) {
/*  97 */       f.write((byte)r.nextInt());
/*     */     }
/*     */ 
/* 100 */     f.close();
/*     */ 
/* 102 */     Properties props = new Properties();
/* 103 */     props.setProperty("p1", "one");
/* 104 */     props.setProperty("p2", "two");
/* 105 */     props.setProperty("p3", "three or something");
/* 106 */     props.setProperty("p4", "%$^&*U");
/*     */ 
/* 108 */     StreamParameteriser sp = gp.newStreamParametiser(props);
/*     */ 
/* 110 */     FileOutputStream fout = new FileOutputStream(testSP);
/*     */ 
/* 112 */     FileInputStream fin = new FileInputStream(test);
/* 113 */     byte[] dat = new byte[5];
/* 114 */     int n = 0;
/*     */ 
/* 116 */     while (n != -1) {
/* 117 */       n = fin.read(dat);
/* 118 */       if (n > 0) {
/* 119 */         sp.nextBlockToBeTransferred(dat, 0, n);
/* 120 */         fout.write(dat, 0, n);
/*     */       }
/*     */     }
/*     */ 
/* 124 */     fin.close();
/* 125 */     fout.close();
/*     */ 
/* 127 */     System.out.println(gp.getParameters(testSP));
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.stream.StreamParameteriser
 * JD-Core Version:    0.6.2
 */