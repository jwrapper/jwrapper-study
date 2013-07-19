/*      */ package utils.string;
/*      */ 
/*      */ import java.io.PrintStream;
/*      */ 
/*      */ public class CharStack
/*      */ {
/*      */   char[] cs;
/*      */   int cslen;
/*      */   int index;
/*    9 */   int[] marks = new int[10];
/*   10 */   int markptr = 0;
/*      */ 
/*   12 */   boolean fakingEndTag = false;
/*   13 */   int lastStartTag = -1;
/*   14 */   int lastEndTag = -1;
/*      */ 
/*   24 */   boolean forward = true;
/*      */   char[] rev;
/*      */ 
/*      */   public void popUntilNumber()
/*      */   {
/*   18 */     while ((!isEmpty()) && (!Character.isDigit(peek())))
/*   19 */       pop();
/*      */   }
/*      */ 
/*      */   public String popUntil(String[] stop, boolean returnString)
/*      */   {
/*   29 */     char[][] ars = new char[stop.length][];
/*   30 */     for (int i = 0; i < stop.length; i++) {
/*   31 */       ars[i] = stop[i].toCharArray();
/*      */     }
/*   33 */     return popUntil(ars, returnString);
/*      */   }
/*      */   public String popUntil(char[][] stop, boolean returnString) {
/*   36 */     if (isEmpty()) return "";
/*      */ 
/*   38 */     int n = this.index;
/*      */ 
/*   40 */     boolean matched = false;
/*   41 */     int z = 0;
/*   42 */     int zmatch = 0;
/*      */     do
/*      */     {
/*   47 */       for (z = 0; z < stop.length; z++) {
/*   48 */         for (int k = 0; k < stop[z].length; k++) {
/*   49 */           if ((n + k >= this.cslen) || (this.cs[(n + k)] != stop[z][k])) break;
/*   50 */           if (k == stop[z].length - 1) {
/*   51 */             matched = true;
/*   52 */             zmatch = z;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*   61 */       if (matched) {
/*   62 */         n += stop[zmatch].length;
/*   63 */         break;
/*      */       }
/*      */ 
/*   66 */       n++;
/*   67 */     }while (n != this.cslen);
/*      */ 
/*   70 */     if (returnString) {
/*   71 */       if (n == this.index) return "";
/*      */ 
/*   73 */       if (matched)
/*      */       {
/*   75 */         String ret = weebleString(this.cs, this.index, n - this.index);
/*   76 */         this.index = n;
/*   77 */         return ret;
/*      */       }
/*   79 */       String ret = weebleString(this.cs, this.index, n - this.index);
/*   80 */       this.index = n;
/*   81 */       return ret;
/*      */     }
/*      */ 
/*   84 */     this.index = n;
/*   85 */     return null;
/*      */   }
/*      */ 
/*      */   private static char[] cloneReversed(char[] tmp)
/*      */   {
/*   90 */     char[] ntmp = new char[tmp.length];
/*   91 */     int ni = tmp.length - 1;
/*   92 */     for (int i = 0; i < tmp.length; i++) {
/*   93 */       ntmp[ni] = tmp[i];
/*   94 */       ni--;
/*      */     }
/*   96 */     return ntmp;
/*      */   }
/*      */   private static char[] cloneReversed(char[] tmp, int off, int len) {
/*   99 */     char[] ntmp = new char[len];
/*  100 */     for (int i = 0; i < len; i++) {
/*  101 */       ntmp[(len - 1 - i)] = tmp[(off + i)];
/*      */     }
/*  103 */     return ntmp;
/*      */   }
/*      */ 
/*      */   private static char[] reverseInPlace(char[] tmp) {
/*  107 */     int len = tmp.length;
/*  108 */     int lenm1 = tmp.length - 1;
/*  109 */     for (int i = 0; i < len; i++) {
/*  110 */       char c = tmp[i];
/*  111 */       tmp[i] = tmp[(lenm1 - i)];
/*  112 */       tmp[(lenm1 - i)] = c;
/*      */     }
/*  114 */     return tmp;
/*      */   }
/*      */ 
/*      */   public CharStack switchDirection() {
/*  118 */     if (this.forward)
/*  119 */       backward();
/*      */     else {
/*  121 */       forward();
/*      */     }
/*  123 */     return this;
/*      */   }
/*      */ 
/*      */   private void changeDirection() {
/*  127 */     this.forward = (!this.forward);
/*  128 */     char[] tmp = this.rev;
/*  129 */     this.rev = this.cs;
/*  130 */     this.cs = tmp;
/*  131 */     this.index = (this.cslen - this.index);
/*      */   }
/*      */ 
/*      */   public CharStack backward() {
/*  135 */     if (this.forward) {
/*  136 */       if (this.rev == null)
/*      */       {
/*  138 */         this.rev = cloneReversed(this.cs);
/*      */       }
/*  140 */       changeDirection();
/*      */     }
/*  142 */     return this;
/*      */   }
/*      */ 
/*      */   private String weebleString(char[] cs, int off, int len) {
/*  146 */     if (this.forward) {
/*  147 */       return new String(cs, off, len);
/*      */     }
/*  149 */     return new String(cloneReversed(cs, off, len));
/*      */   }
/*      */ 
/*      */   public CharStack forward()
/*      */   {
/*  154 */     if (!this.forward) {
/*  155 */       changeDirection();
/*      */     }
/*  157 */     return this;
/*      */   }
/*      */ 
/*      */   public boolean isNextNumber()
/*      */   {
/*      */     try
/*      */     {
/*  164 */       peekNumber();
/*  165 */       return true;
/*      */     }
/*      */     catch (NumberFormatException ex) {
/*      */     }
/*  169 */     return false;
/*      */   }
/*      */ 
/*      */   public int getIndex()
/*      */   {
/*  175 */     return this.index;
/*      */   }
/*      */ 
/*      */   public CharStack(char[] s) {
/*  179 */     this.cs = s;
/*  180 */     this.cslen = s.length;
/*  181 */     this.index = 0;
/*      */   }
/*      */   public CharStack(String s) {
/*  184 */     this.cs = s.toCharArray();
/*  185 */     this.cslen = this.cs.length;
/*  186 */     this.index = 0;
/*      */   }
/*      */   public CharStack(String s, int index) {
/*  189 */     this.cs = s.toCharArray();
/*  190 */     this.cslen = this.cs.length;
/*  191 */     this.index = index;
/*      */   }
/*      */   public boolean isEmpty() {
/*  194 */     return this.index >= this.cslen;
/*      */   }
/*      */   public char peek() {
/*  197 */     return this.cs[this.index];
/*      */   }
/*      */   public char pop() {
/*  200 */     return this.cs[(this.index++)];
/*      */   }
/*      */   public void popToEnd() {
/*  203 */     this.index = this.cslen;
/*      */   }
/*      */ 
/*      */   public void mark()
/*      */   {
/*  218 */     this.marks[(this.markptr++)] = this.index;
/*      */ 
/*  220 */     if (this.markptr == this.marks.length) {
/*  221 */       int[] tmp = new int[this.marks.length * 2];
/*  222 */       System.arraycopy(this.marks, 0, tmp, 0, this.marks.length);
/*  223 */       this.marks = tmp;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getStringSinceMark() {
/*  228 */     return new String(this.cs, this.marks[(this.markptr - 1)], this.index - this.marks[(this.markptr - 1)]);
/*      */   }
/*      */ 
/*      */   public CharStack replaceStringSinceMark(String rep) {
/*  232 */     String before = new String(this.cs, 0, this.marks[(this.markptr - 1)]);
/*  233 */     String after = new String(this.cs, this.index, this.cs.length - this.index);
/*  234 */     CharStack cs = new CharStack(before + rep + after);
/*  235 */     cs.index = (before.length() + rep.length());
/*  236 */     return cs;
/*      */   }
/*      */ 
/*      */   public void resetToMark() {
/*  240 */     this.markptr -= 1;
/*  241 */     if (this.markptr == -1) {
/*  242 */       this.markptr = 0;
/*      */     }
/*  244 */     this.index = this.marks[this.markptr];
/*      */ 
/*  246 */     this.fakingEndTag = false;
/*      */   }
/*      */ 
/*      */   public void popMark() {
/*  250 */     this.markptr -= 1;
/*  251 */     if (this.markptr == -1)
/*  252 */       this.markptr = 0;
/*      */   }
/*      */ 
/*      */   public double peekNumber() throws NumberFormatException
/*      */   {
/*  257 */     int tmp = this.index;
/*  258 */     double d = popNumber();
/*  259 */     this.index = tmp;
/*  260 */     return d;
/*      */   }
/*      */ 
/*      */   public long popInteger() throws NumberFormatException
/*      */   {
/*  265 */     int n = this.index;
/*  266 */     char c = this.cs[n];
/*      */ 
/*  268 */     if (c == '-') {
/*  269 */       n++;
/*  270 */       c = this.cs[n];
/*      */     }
/*      */ 
/*  273 */     while (Character.isDigit(c)) {
/*  274 */       n++;
/*  275 */       if (n == this.cslen) break;
/*  276 */       c = this.cs[n];
/*      */     }
/*      */ 
/*  279 */     if (n == this.index) throw new NumberFormatException("no digits found " + new String(this.cs, this.index, Math.min(this.index + 5, this.cslen) - this.index));
/*      */ 
/*  281 */     long d = Long.parseLong(new String(this.cs, this.index, n - this.index));
/*      */ 
/*  283 */     this.index = n;
/*  284 */     return d;
/*      */   }
/*      */ 
/*      */   public double popNumber() throws NumberFormatException {
/*  288 */     int n = this.index;
/*  289 */     char c = this.cs[n];
/*      */ 
/*  291 */     if (c == '-') {
/*  292 */       n++;
/*  293 */       c = this.cs[n];
/*      */     }
/*      */ 
/*  296 */     while ((Character.isDigit(c)) || (c == '.')) {
/*  297 */       n++;
/*  298 */       if (n == this.cslen) break;
/*  299 */       c = this.cs[n];
/*      */     }
/*      */ 
/*  303 */     if ((n != this.cslen) && (
/*  304 */       (this.cs[n] == 'E') || (this.cs[n] == 'e'))) {
/*  305 */       n++;
/*  306 */       if (n != this.cslen) {
/*  307 */         if (this.cs[n] == '-') {
/*  308 */           n++;
/*      */         }
/*  310 */         while (Character.isDigit(this.cs[n])) {
/*  311 */           n++;
/*  312 */           if (n == this.cslen) {
/*      */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  318 */     if (n == this.index) throw new NumberFormatException("no digits found " + new String(this.cs, this.index, Math.min(this.index + 5, this.cslen) - this.index));
/*      */ 
/*  320 */     double d = Double.parseDouble(new String(this.cs, this.index, n - this.index));
/*      */ 
/*  322 */     this.index = n;
/*  323 */     return d;
/*      */   }
/*      */ 
/*      */   public void popWhitespace() {
/*  327 */     if (this.index == this.cslen) return;
/*  328 */     int n = this.index;
/*  329 */     char ch = this.cs[n];
/*  330 */     while ((ch == ' ') || 
/*  331 */       (ch == '\t') || 
/*  332 */       (ch == '\n') || 
/*  333 */       (ch == '\r'))
/*      */     {
/*  335 */       n++;
/*  336 */       if (n == this.cslen) break;
/*  337 */       ch = this.cs[n];
/*      */     }
/*  339 */     this.index = n;
/*      */   }
/*      */   public String popText(boolean returnString) {
/*  342 */     if (isEmpty()) return "";
/*      */ 
/*  344 */     int n = this.index;
/*      */ 
/*  346 */     while ((this.cs[n] != ' ') && (this.cs[n] != '\t') && (this.cs[n] != '\r') && (this.cs[n] != '\n')) {
/*  347 */       n++;
/*  348 */       if (n == this.cslen)
/*      */         break;
/*      */     }
/*  351 */     if (returnString) {
/*  352 */       if (n == this.index) return "";
/*      */ 
/*  354 */       String ret = new String(this.cs, this.index, n - this.index);
/*  355 */       this.index = n;
/*  356 */       return ret;
/*      */     }
/*  358 */     this.index = n;
/*  359 */     return null;
/*      */   }
/*      */ 
/*      */   public void popUntil(String stop) {
/*  363 */     popUntil(stop.toCharArray(), false);
/*      */   }
/*      */   public String popUntil(String stop, boolean returnString) {
/*  366 */     return popUntil(stop.toCharArray(), returnString);
/*      */   }
/*      */   public String popUntil(char[] stop, boolean returnString) {
/*  369 */     if (isEmpty()) return "";
/*      */ 
/*  371 */     int n = this.index;
/*      */ 
/*  373 */     boolean matched = false;
/*      */     do
/*      */     {
/*  378 */       for (int k = 0; k < stop.length; k++) {
/*  379 */         if ((n + k >= this.cslen) || (this.cs[(n + k)] != stop[k])) break;
/*  380 */         if (k == stop.length - 1) {
/*  381 */           matched = true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  389 */       if (matched) {
/*  390 */         n += stop.length;
/*  391 */         break;
/*      */       }
/*      */ 
/*  394 */       n++;
/*  395 */     }while (n != this.cslen);
/*      */ 
/*  398 */     if (returnString) {
/*  399 */       if (n == this.index) return "";
/*      */ 
/*  401 */       if (matched) {
/*  402 */         String ret = new String(this.cs, this.index, n - stop.length - this.index);
/*  403 */         this.index = n;
/*  404 */         return ret;
/*      */       }
/*  406 */       String ret = new String(this.cs, this.index, n - this.index);
/*  407 */       this.index = n;
/*  408 */       return ret;
/*      */     }
/*      */ 
/*  411 */     this.index = n;
/*  412 */     return null;
/*      */   }
/*      */ 
/*      */   public String popUntil(char stop, boolean returnString) {
/*  416 */     if (isEmpty()) return "";
/*      */ 
/*  418 */     int n = this.index;
/*      */ 
/*  420 */     boolean matched = true;
/*      */ 
/*  422 */     while (this.cs[n] != stop) {
/*  423 */       n++;
/*  424 */       if (n == this.cslen) {
/*  425 */         matched = false;
/*  426 */         break;
/*      */       }
/*      */     }
/*  429 */     if (n != this.cslen) n++;
/*      */ 
/*  431 */     if (returnString) {
/*  432 */       if (n == this.index) return "";
/*      */ 
/*  443 */       if ((n == this.cslen) && (!matched)) {
/*  444 */         String ret = new String(this.cs, this.index, n - this.index);
/*  445 */         this.index = n;
/*  446 */         return ret;
/*      */       }
/*  448 */       String ret = new String(this.cs, this.index, n - 1 - this.index);
/*  449 */       this.index = n;
/*  450 */       return ret;
/*      */     }
/*      */ 
/*  453 */     this.index = n;
/*  454 */     return null;
/*      */   }
/*      */ 
/*      */   public String popXmlText(boolean returnString)
/*      */   {
/*  464 */     if (isEmpty()) return "";
/*      */ 
/*  466 */     int n = this.index;
/*  467 */     boolean hasEscaped = false;
/*      */ 
/*  469 */     while (this.cs[n] != '<')
/*      */     {
/*  472 */       if (this.cs[n] == '&') hasEscaped = true;
/*      */ 
/*  474 */       n++;
/*  475 */       if (n == this.cslen)
/*      */         break;
/*      */     }
/*  478 */     if (returnString) {
/*  479 */       if (n == this.index) return "";
/*      */ 
/*  481 */       String ret = new String(this.cs, this.index, n - this.index);
/*      */ 
/*  483 */       if (hasEscaped) ret = fromXMLString(ret);
/*  484 */       this.index = n;
/*  485 */       return ret;
/*      */     }
/*  487 */     this.index = n;
/*  488 */     return null;
/*      */   }
/*      */ 
/*      */   public String popJavaFullClassname(boolean returnString)
/*      */   {
/*  493 */     if (isEmpty()) return "";
/*  494 */     int n = this.index;
/*  495 */     char ch = this.cs[n];
/*  496 */     while (((ch > '/') && (ch < ':')) || 
/*  497 */       ((ch > '@') && (ch < '[')) || 
/*  498 */       ((ch > '`') && (ch < '{')) || 
/*  499 */       (ch == '_') || 
/*  500 */       (ch == '.'))
/*      */     {
/*  503 */       n++;
/*  504 */       if (n == this.cslen) break;
/*  505 */       ch = this.cs[n];
/*      */     }
/*  507 */     if (returnString) {
/*  508 */       if (n == this.index) return "";
/*      */ 
/*  510 */       String ret = new String(this.cs, this.index, n - this.index);
/*  511 */       this.index = n;
/*  512 */       return ret;
/*      */     }
/*  514 */     this.index = n;
/*  515 */     return null;
/*      */   }
/*      */ 
/*      */   public String popXmlIdentifier(boolean returnString)
/*      */   {
/*  520 */     if (isEmpty()) return "";
/*  521 */     int n = this.index;
/*  522 */     char ch = this.cs[n];
/*  523 */     while (((ch > '/') && (ch < ';')) || 
/*  524 */       ((ch > '@') && (ch < '[')) || 
/*  525 */       ((ch > '`') && (ch < '{')) || 
/*  526 */       (ch == '_'))
/*      */     {
/*  529 */       n++;
/*  530 */       if (n == this.cslen) break;
/*  531 */       ch = this.cs[n];
/*      */     }
/*  533 */     if (returnString) {
/*  534 */       if (n == this.index) return "";
/*      */ 
/*  536 */       String ret = new String(this.cs, this.index, n - this.index);
/*  537 */       this.index = n;
/*  538 */       return ret;
/*      */     }
/*  540 */     this.index = n;
/*  541 */     return null;
/*      */   }
/*      */ 
/*      */   public String popXmlQuoted(boolean returnString)
/*      */   {
/*  546 */     if (isEmpty()) return "";
/*  547 */     char quote = pop();
/*  548 */     boolean hasEscaped = false;
/*      */ 
/*  550 */     int n = this.index;
/*  551 */     while (this.cs[n] != quote)
/*      */     {
/*  554 */       if (this.cs[n] == '&') hasEscaped = true;
/*      */ 
/*  556 */       n++;
/*  557 */       if (n == this.cslen)
/*      */         break;
/*      */     }
/*  560 */     if (returnString) {
/*  561 */       if (n == this.index) return "";
/*      */ 
/*  563 */       String ret = new String(this.cs, this.index, n - this.index);
/*      */ 
/*  565 */       if (hasEscaped) ret = fromXMLString(ret);
/*  566 */       this.index = (n + 1);
/*  567 */       return ret;
/*      */     }
/*  569 */     this.index = (n + 1);
/*  570 */     return null;
/*      */   }
/*      */ 
/*      */   public String popXmlElementStart()
/*      */   {
/*  575 */     popXmlText(false);
/*      */ 
/*  577 */     if (isEmpty()) return "";
/*      */ 
/*  579 */     int startIndex = this.index;
/*      */ 
/*  582 */     pop();
/*  583 */     if (peek() == '/') {
/*  584 */       pop();
/*  585 */       popWhitespace();
/*  586 */       String id = popXmlIdentifier(true);
/*  587 */       return "/" + id;
/*      */     }
/*  589 */     popWhitespace();
/*  590 */     this.lastStartTag = startIndex;
/*  591 */     String id = popXmlIdentifier(true);
/*  592 */     if (this.fakingEndTag) {
/*  593 */       this.index = this.lastEndTag;
/*  594 */       return "/" + id;
/*      */     }
/*  596 */     return id;
/*      */   }
/*      */ 
/*      */   public void popXmlAttributes()
/*      */   {
/*  602 */     String attr = popXmlAttributeName();
/*  603 */     while (attr.length() > 0)
/*      */     {
/*  606 */       popWhitespace();
/*  607 */       pop();
/*  608 */       popWhitespace();
/*  609 */       popXmlQuoted(false);
/*      */ 
/*  611 */       attr = popXmlAttributeName();
/*      */     }
/*      */   }
/*      */ 
/*      */   public String popXmlAttributeName() {
/*  616 */     popWhitespace();
/*      */ 
/*  618 */     return popXmlIdentifier(true);
/*      */   }
/*      */ 
/*      */   public String popXmlAttributeValue() {
/*  622 */     popWhitespace();
/*  623 */     pop();
/*  624 */     popWhitespace();
/*      */ 
/*  626 */     return popXmlQuoted(true);
/*      */   }
/*      */ 
/*      */   public void popXmlElementEnd() {
/*  630 */     popWhitespace();
/*      */ 
/*  632 */     if (this.fakingEndTag) {
/*  633 */       this.fakingEndTag = false;
/*  634 */       this.index = this.lastEndTag;
/*      */     }
/*      */ 
/*  638 */     if (peek() == '/') {
/*  639 */       pop();
/*      */ 
/*  642 */       this.fakingEndTag = true;
/*  643 */       this.lastEndTag = this.index;
/*      */ 
/*  645 */       pop();
/*      */ 
/*  648 */       this.index = this.lastStartTag;
/*      */     }
/*      */     else {
/*  651 */       this.fakingEndTag = false;
/*  652 */       pop();
/*      */     }
/*      */   }
/*      */ 
/*      */   public String popXmlSubTree(boolean returnString) {
/*  657 */     int begin = this.index;
/*      */ 
/*  659 */     boolean done = false;
/*      */ 
/*  661 */     while (!done)
/*      */     {
/*  664 */       popXmlText(false);
/*      */ 
/*  666 */       int tmp = this.index;
/*  667 */       String start = popXmlElementStart();
/*      */ 
/*  669 */       if (start.length() > 0) {
/*  670 */         if (start.charAt(0) == '/')
/*      */         {
/*  672 */           this.index = tmp;
/*  673 */           done = true;
/*      */         }
/*      */         else
/*      */         {
/*  677 */           popXmlAttributes();
/*  678 */           popXmlElementEnd();
/*      */ 
/*  680 */           popXmlSubTree(false);
/*      */ 
/*  682 */           popXmlElementStart();
/*  683 */           popXmlElementEnd();
/*      */         }
/*      */       }
/*      */       else {
/*  687 */         done = true;
/*      */       }
/*      */     }
/*      */ 
/*  691 */     popXmlText(false);
/*      */ 
/*  693 */     if (returnString) {
/*  694 */       return new String(this.cs, begin, this.index - begin);
/*      */     }
/*  696 */     return null;
/*      */   }
/*      */ 
/*      */   public String toFullString()
/*      */   {
/*  786 */     return new String(this.cs);
/*      */   }
/*      */   public String toString() {
/*  789 */     return new String(this.cs, this.index, Math.min(this.index + 20, this.cslen) - this.index) + "...";
/*      */   }
/*      */   public String toString(int N) {
/*  792 */     return new String(this.cs, this.index, Math.min(this.index + N, this.cslen) - this.index) + "...";
/*      */   }
/*      */   public String toString(char c) {
/*  795 */     int loc = this.index;
/*  796 */     while ((loc < this.cs.length) && (this.cs[loc] != '\n'))
/*      */     {
/*  798 */       loc++;
/*      */     }
/*  800 */     return new String(this.cs, this.index, Math.min(loc, this.cslen) - this.index) + "...";
/*      */   }
/*      */ 
/*      */   public void debugStack(String s) {
/*  804 */     System.err.println("STACK PARSING=" + s);
/*  805 */     System.err.println("STACK SIZE=" + this.cslen);
/*  806 */     System.err.println("STACK PTR=" + this.index);
/*  807 */     System.err.println("STACK FAKE END=" + this.fakingEndTag);
/*  808 */     System.err.println("STACK CONTENTS=" + new String(this.cs, this.index, Math.min(this.index + 20, this.cslen) - this.index) + "...");
/*      */   }
/*      */ 
/*      */   public String getStack() {
/*  812 */     return new String(this.cs, 0, this.cslen);
/*      */   }
/*      */ 
/*      */   private static void testXML() {
/*  816 */     StringBuffer sb = new StringBuffer();
/*  817 */     sb.append(" <\tUSAddress  \t country= \"US\">");
/*  818 */     sb.append(" TEXT");
/*  819 */     sb.append(" </USAddress> TEXT &lt;&gt;&quot;&apos;&amp;");
/*  820 */     CharStack cs = new CharStack(sb.toString());
/*  821 */     String tmp = cs.popXmlElementStart();
/*  822 */     if (!tmp.equals("USAddress")) {
/*  823 */       System.err.println("Popped element incorrectly - [" + tmp + "]");
/*      */     }
/*  825 */     tmp = cs.popXmlAttributeName();
/*  826 */     if (!tmp.equals("country")) {
/*  827 */       System.err.println("Popped attribute name incorrectly - [" + tmp + "]");
/*      */     }
/*  829 */     tmp = cs.popXmlAttributeValue();
/*  830 */     if (!tmp.equals("US")) {
/*  831 */       System.err.println("Popped attribute value incorrectly - [" + tmp + "]");
/*      */     }
/*  833 */     cs.popXmlElementEnd();
/*  834 */     tmp = cs.popXmlText(true);
/*  835 */     if (!tmp.equals(" TEXT ")) {
/*  836 */       System.err.println("Popped text incorrectly - [" + tmp + "]");
/*      */     }
/*  838 */     tmp = cs.popXmlElementStart();
/*  839 */     if (!tmp.equals("/USAddress")) {
/*  840 */       System.err.println("Popped element incorrectly - [" + tmp + "]");
/*      */     }
/*  842 */     cs.popXmlElementEnd();
/*  843 */     tmp = cs.popXmlElementStart();
/*  844 */     if (tmp.length() > 0) {
/*  845 */       System.err.println("popped element name from text - [" + tmp + "]");
/*      */     }
/*  847 */     if (!cs.isEmpty()) System.err.println("Finished reading all XML but not empty??");
/*      */ 
/*  849 */     sb = new StringBuffer();
/*      */ 
/*  851 */     sb.append("OTHER TEXT1 <MINI_NODE /> OTHER TEXT2 <N1><N2><MINI_NODE2 /></N2></N1> OTHER TEXT3 </flinder>");
/*      */ 
/*  853 */     cs = new CharStack(sb.toString());
/*      */ 
/*  855 */     String ret = cs.popXmlSubTree(true);
/*  856 */     if (!ret.equals("OTHER TEXT1 <MINI_NODE /> OTHER TEXT2 <N1><N2><MINI_NODE2 /></N2></N1> OTHER TEXT3 "))
/*  857 */       System.out.println("Popped subtree incorrectly:[" + ret + "]");
/*      */     else {
/*  859 */       System.out.println("Popped subtree correctly:" + ret);
/*      */     }
/*      */ 
/*  862 */     String soapTest = "   <result xsi:type=\"ns2:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ns2=\"http://www.w3.org/2001/XMLSchema\">ni hao</result>";
/*  863 */     cs = new CharStack(soapTest);
/*  864 */     cs.popXmlText(false);
/*  865 */     tmp = cs.popXmlElementStart();
/*  866 */     if (!tmp.equals("result"))
/*  867 */       System.out.println("popped SOAP element incorrectly - [" + tmp + "]");
/*      */     else
/*  869 */       System.out.println("popped SOAP element OK - [" + tmp + "]");
/*      */   }
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/*  876 */     String test = "Hello, this is a test}";
/*  877 */     CharStack stack = new CharStack(test.toCharArray());
/*      */     try {
/*  879 */       System.out.println("Test String = " + test);
/*  880 */       System.out.println("Running stack.popUntil('}',true);....");
/*  881 */       System.out.println("Result = " + stack.popUntil('}', true));
/*  882 */       System.out.println();
/*  883 */       System.out.println();
/*      */     } catch (Exception e) {
/*  885 */       e.printStackTrace();
/*      */     }
/*      */     try {
/*  888 */       test = "Hello, this is a test";
/*  889 */       stack = new CharStack(test.toCharArray());
/*      */ 
/*  891 */       System.out.println("Test String = " + test);
/*  892 */       System.out.println("Running stack.popUntil('}',true);....");
/*  893 */       System.out.println("Result = " + stack.popUntil('}', true));
/*  894 */       System.out.println();
/*  895 */       System.out.println();
/*      */     } catch (Exception e) {
/*  897 */       e.printStackTrace();
/*      */     }
/*      */ 
/*  900 */     test = "Hello, this} is a test";
/*  901 */     stack = new CharStack(test.toCharArray());
/*      */ 
/*  903 */     System.out.println("Test String = " + test);
/*  904 */     System.out.println("Running stack.popUntil('}',true);....");
/*  905 */     System.out.println("Result = " + stack.popUntil('}', true));
/*  906 */     System.out.println();
/*  907 */     System.out.println();
/*      */ 
/*  909 */     test = "Hello, this}} is a test";
/*  910 */     stack = new CharStack(test.toCharArray());
/*      */ 
/*  912 */     System.out.println("Test String = " + test);
/*  913 */     System.out.println("Running stack.popUntil('}',true);....");
/*  914 */     System.out.println("Result = " + stack.popUntil('}', true));
/*  915 */     System.out.println("Result = " + stack.popUntil('}', true));
/*  916 */     System.out.println();
/*  917 */     System.out.println();
/*      */ 
/*  919 */     test = "}Hello, this is a test";
/*  920 */     stack = new CharStack(test.toCharArray());
/*      */ 
/*  922 */     System.out.println("Test String = " + test);
/*  923 */     System.out.println("Running stack.popUntil('}',true);....");
/*  924 */     System.out.println("Result = " + stack.popUntil('}', true));
/*      */   }
/*      */ 
/*      */   public static String fromXMLString(String s)
/*      */   {
/*  931 */     char[] cs = s.toCharArray();
/*  932 */     int cslen = cs.length;
/*      */ 
/*  934 */     StringBuffer ret = new StringBuffer();
/*      */ 
/*  936 */     char cs0 = '\000'; char cs1 = '\000'; char cs2 = '\000'; char cs3 = '\000'; char cs4 = '\000'; char cs5 = '\000';
/*  937 */     for (int i = 0; i < cslen; i++) {
/*  938 */       cs0 = cs[i];
/*      */ 
/*  940 */       if (cs0 == '&')
/*      */       {
/*  942 */         int len = cslen - i;
/*      */ 
/*  944 */         if (len > 3) {
/*  945 */           cs1 = cs[(i + 1)];
/*  946 */           cs2 = cs[(i + 2)];
/*  947 */           cs3 = cs[(i + 3)];
/*      */ 
/*  949 */           if (cs1 == '#')
/*      */           {
/*  951 */             int end_parse = charArrayIndexOf(';', cs, cslen, i + 1);
/*  952 */             if (end_parse != -1) {
/*  953 */               char ccode = (char)Integer.parseInt(new String(cs, i + 2, end_parse - (i + 2)));
/*  954 */               ret.append(String.valueOf(ccode));
/*  955 */               i = end_parse;
/*      */             }
/*      */           }
/*  958 */           else if ((cs1 == 'l') && 
/*  959 */             (cs2 == 't') && 
/*  960 */             (cs3 == ';'))
/*      */           {
/*  962 */             ret.append('<');
/*  963 */             i += 3;
/*      */           }
/*  965 */           else if ((cs1 == 'g') && 
/*  966 */             (cs2 == 't') && 
/*  967 */             (cs3 == ';'))
/*      */           {
/*  969 */             ret.append('>');
/*  970 */             i += 3;
/*      */           }
/*      */         }
/*      */ 
/*  974 */         if (len > 4) {
/*  975 */           cs4 = cs[(i + 4)];
/*      */ 
/*  977 */           if ((cs1 == 'a') && 
/*  978 */             (cs2 == 'm') && 
/*  979 */             (cs3 == 'p') && 
/*  980 */             (cs4 == ';'))
/*      */           {
/*  982 */             ret.append('&');
/*  983 */             i += 4;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  988 */         if (len > 5) {
/*  989 */           cs5 = cs[(i + 5)];
/*      */ 
/*  991 */           if ((cs1 == 'q') && 
/*  992 */             (cs2 == 'u') && 
/*  993 */             (cs3 == 'o') && 
/*  994 */             (cs4 == 't') && 
/*  995 */             (cs5 == ';'))
/*      */           {
/*  997 */             ret.append('"');
/*  998 */             i += 5;
/*      */           }
/* 1000 */           else if ((cs1 == 'a') && 
/* 1001 */             (cs2 == 'p') && 
/* 1002 */             (cs3 == 'o') && 
/* 1003 */             (cs4 == 's') && 
/* 1004 */             (cs5 == ';'))
/*      */           {
/* 1006 */             ret.append('\'');
/* 1007 */             i += 5;
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 1012 */         ret.append(cs0);
/*      */       }
/*      */     }
/*      */ 
/* 1016 */     return ret.toString();
/*      */   }
/*      */ 
/*      */   private static int charArrayIndexOf(char c, char[] cs, int cslen, int index) {
/* 1020 */     for (int i = index; i < cslen; i++) {
/* 1021 */       if (cs[i] == c) return i;
/*      */     }
/* 1023 */     return -1;
/*      */   }
/*      */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     utils.string.CharStack
 * JD-Core Version:    0.6.2
 */