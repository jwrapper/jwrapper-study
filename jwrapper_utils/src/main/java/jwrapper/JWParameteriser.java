/*    */ package jwrapper;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.util.Properties;
/*    */ import java.util.Set;
/*    */ import utils.stream.GenericParameteriser;
/*    */ import utils.string.HexData;
/*    */ 
/*    */ public class JWParameteriser extends GenericParameteriser
/*    */ {
/* 12 */   static final byte[] start = { 89, 
/* 13 */     -67, -55, 82, -86, 53, -83, -69, 16, -17, -34, 
/* 14 */     103, 44, 71, -32, -75, -44, -126, 31, -127, 82, 
/* 15 */     -95, 2, 68, 42, -21, -86, 123, 121, 59, -109, 
/* 16 */     69, -82, -46, 9, 122, 62, 47, 46, -6, -1, 
/* 17 */     29, -16, -101, 25, 124, 21, -72, 30, 109, -58, 
/* 18 */     16, 65, 126, 92, -19, 81, -79, 25, 28, 60, 
/* 19 */     -30, -108, -40 };
/*    */ 
/* 21 */   static final byte[] end = { -40, -108, -30, 60, 
/* 22 */     28, 25, -79, 81, -19, 92, 126, 65, 16, -58, 
/* 23 */     109, 30, -72, 21, 124, 25, -101, -16, 29, -1, 
/* 24 */     -6, 46, 47, 62, 122, 9, -46, -82, 69, -109, 
/* 25 */     59, 121, 123, -86, -21, 42, 68, 2, -95, 82, 
/* 26 */     -127, 31, -126, -44, -75, -32, 71, 44, 103, -34, 
/* 27 */     -17, 16, -69, -83, 53, -86, 82, -55, -67, 89 };
/*    */ 
/*    */   public byte[] getStartMarker()
/*    */   {
/* 31 */     return start;
/*    */   }
/*    */ 
/*    */   public byte[] getEndMarker() {
/* 35 */     return end;
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) throws Exception {
/* 39 */     if (args.length == 0) {
/* 40 */       System.out.println("Usage: JWParameteriser <file> <command> [other options]");
/* 41 */       System.out.println("Commands:");
/* 42 */       System.out.println("  validate                  validate that an unparameterised file has a correct and uncorrupted parameterisation block");
/* 43 */       System.out.println("  length                    print the length of the current parameters as a block (interpreted as C code would)");
/* 44 */       System.out.println("  print                     print any properties stored in a files parameterisation block");
/* 45 */       System.out.println("  remove                    remove all properties from a file's paramterisation block");
/*    */ 
/* 47 */       System.out.println("  store <props file>        overwrite properties from a properties file into the target file's paramterisation block");
/* 48 */       System.exit(0);
/*    */     }
/*    */ 
/* 51 */     File f = new File(args[0]);
/* 52 */     String cmd = args[1];
/* 53 */     if (cmd.equals("validate")) {
/* 54 */       JWParameteriser jwparams = new JWParameteriser();
/* 55 */       if (jwparams.validateUnparameterised(f))
/* 56 */         System.out.println("Parameter block is VALID");
/*    */       else
/* 58 */         System.out.println("Parameter block is NOT VALID");
/*    */     }
/* 60 */     else if (cmd.equals("length")) {
/* 61 */       JWParameteriser jwparams = new JWParameteriser();
/* 62 */       Properties props = jwparams.getParameters(f);
/* 63 */       if (jwparams.validateCLength(f))
/* 64 */         System.out.println("Parameter block is VALID");
/*    */       else
/* 66 */         System.out.println("Parameter block is NOT VALID");
/*    */     }
/* 68 */     else if (cmd.equals("print")) {
/* 69 */       JWParameteriser jwparams = new JWParameteriser();
/* 70 */       Properties props = jwparams.getParameters(f);
/* 71 */       System.out.println(props);
/* 72 */     } else if (cmd.equals("remove")) {
/* 73 */       JWParameteriser jwparams = new JWParameteriser();
/* 74 */       jwparams.setParameters(new Properties(), f, true);
/* 75 */     } else if (cmd.equals("store")) {
/* 76 */       Properties props = new Properties();
/* 77 */       props.load(new FileInputStream(args[2]));
/*    */ 
/* 79 */       Object[] keys = props.keySet().toArray();
/* 80 */       for (int i = 0; i < keys.length; i++) {
/* 81 */         String key = (String)keys[i];
/* 82 */         String value = props.getProperty(key);
/* 83 */         if (value.startsWith("HEXENCODE:")) {
/* 84 */           System.out.println("Hex encoding " + key);
/* 85 */           props.put(key, HexData.stringToHexString(value));
/*    */         }
/*    */       }
/*    */ 
/* 89 */       System.out.println(props);
/* 90 */       JWParameteriser jwparams = new JWParameteriser();
/* 91 */       jwparams.setParameters(props, f, true);
/*    */     } else {
/* 93 */       System.out.println("Unrecognised command " + cmd);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.JWParameteriser
 * JD-Core Version:    0.6.2
 */