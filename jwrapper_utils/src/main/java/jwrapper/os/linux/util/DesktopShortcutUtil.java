/*     */ package jwrapper.os.linux.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import utils.files.FileUtil;
/*     */ 
/*     */ public class DesktopShortcutUtil
/*     */ {
/*     */   public static final int TYPE_APPLICATION = 1;
/*     */   public static final int TYPE_LINK = 2;
/*     */   public static final int TYPE_DIRECTORY = 3;
/*  16 */   public final String GLOBAL_TARGET_DIRECTORY = "/usr/share/applications";
/*     */   public String LOCAL_TARGET_DIRECTORY;
/*     */   public String DESKTOP_SHORTCUT_DIRECTORY;
/*  20 */   private ArrayList unityShortcuts = new ArrayList();
/*  21 */   private ArrayList actions = new ArrayList();
/*     */   String type;
/*     */   String name;
/*     */   String exec;
/* 270 */   String version = "1.0";
/*     */ 
/* 274 */   String encoding = "UTF-8";
/*     */   String genericName;
/* 285 */   String noDisplay = null;
/*     */ 
/* 289 */   String comment = null;
/*     */ 
/* 293 */   String hidden = null;
/*     */ 
/* 298 */   String icon = null;
/*     */ 
/* 303 */   String path = null;
/*     */ 
/* 308 */   String terminal = null;
/*     */ 
/* 313 */   String mimeType = null;
/*     */ 
/* 320 */   String categories = null;
/*     */ 
/* 325 */   String URL = null;
/*     */ 
/*     */   public DesktopShortcutUtil(int type, String name, String exec)
/*     */   {
/*  31 */     this.LOCAL_TARGET_DIRECTORY = (System.getProperty("user.home") + "/.local/share/applications");
/*  32 */     this.DESKTOP_SHORTCUT_DIRECTORY = (System.getProperty("user.home") + "/Desktop");
/*     */ 
/*  34 */     switch (type) {
/*     */     case 1:
/*  36 */       this.type = "Application"; break;
/*     */     case 2:
/*  37 */       this.type = "Link"; break;
/*     */     case 3:
/*  38 */       this.type = "Directory"; break;
/*     */     default:
/*  39 */       this.type = "Application";
/*     */     }
/*  41 */     this.name = name;
/*  42 */     this.exec = exec;
/*     */   }
/*     */ 
/*     */   public void addUnitySpecificRightClickOptions(String shortNameID, String displayName, String customisedExec)
/*     */   {
/*  83 */     UnityShortcut shortcut = new UnityShortcut();
/*  84 */     shortcut.shortNameID = shortNameID;
/*  85 */     shortcut.displayName = displayName;
/*  86 */     shortcut.customisedExec = customisedExec;
/*  87 */     this.unityShortcuts.add(shortcut);
/*     */   }
/*     */ 
/*     */   public void addAction(String actionID, String actionName, String actionIcon, String actionExec)
/*     */   {
/*  97 */     Action action = new Action();
/*  98 */     action.actionID = actionID;
/*  99 */     action.name = actionName;
/* 100 */     action.icon = actionIcon;
/* 101 */     action.exec = actionExec;
/* 102 */     this.actions.add(action);
/*     */   }
/*     */ 
/*     */   private File getFile(String parent, String filename)
/*     */   {
/* 107 */     String extension = ".desktop";
/* 108 */     if ((this.type != null) && (this.type.equals("Directory")))
/* 109 */       extension = ".directory";
/* 110 */     if (!filename.endsWith(extension))
/* 111 */       filename = filename + extension;
/* 112 */     return new File(parent, filename);
/*     */   }
/*     */ 
/*     */   public void deleteForAllUsers(String desiredFileName) throws IOException
/*     */   {
/* 117 */     File targetFile = getFile("/usr/share/applications", desiredFileName);
/*     */ 
/* 119 */     FileUtil.deleteDir(targetFile);
/*     */   }
/*     */ 
/*     */   public void deleteForThisUser(String desiredFileName) throws IOException
/*     */   {
/* 124 */     File targetFile = getFile(this.LOCAL_TARGET_DIRECTORY, desiredFileName);
/*     */ 
/* 126 */     FileUtil.deleteDir(targetFile);
/*     */   }
/*     */ 
/*     */   public void writeForAllUsers(String desiredFileName) throws IOException
/*     */   {
/* 131 */     File targetFile = getFile("/usr/share/applications", desiredFileName);
/*     */ 
/* 133 */     new File("/usr/share/applications").mkdirs();
/*     */ 
/* 135 */     writeShortcut(targetFile);
/*     */   }
/*     */ 
/*     */   public void writeForThisUser(String desiredFileName) throws IOException
/*     */   {
/* 140 */     File targetFile = getFile(this.LOCAL_TARGET_DIRECTORY, desiredFileName);
/*     */ 
/* 142 */     new File(this.LOCAL_TARGET_DIRECTORY).mkdirs();
/*     */ 
/* 144 */     writeShortcut(targetFile);
/*     */   }
/*     */ 
/*     */   public void writeDesktopShortcutForThisUser(String desiredFileName) throws IOException
/*     */   {
/* 149 */     File targetFile = getFile(this.DESKTOP_SHORTCUT_DIRECTORY, desiredFileName);
/*     */ 
/* 151 */     new File(this.DESKTOP_SHORTCUT_DIRECTORY).mkdirs();
/*     */ 
/* 153 */     writeShortcut(targetFile);
/*     */   }
/*     */ 
/*     */   private void writeShortcut(File target) throws IOException
/*     */   {
/* 158 */     StringBuffer buffer = new StringBuffer();
/* 159 */     buffer.append("[Desktop Entry]").append("\n");
/* 160 */     addNameValuePair(buffer, "Version", this.version);
/* 161 */     addNameValuePair(buffer, "Type", this.type);
/* 162 */     addNameValuePair(buffer, "Name", this.name);
/* 163 */     addNameValuePair(buffer, "Exec", this.exec);
/* 164 */     addNameValuePair(buffer, "Encoding", this.encoding);
/* 165 */     addNameValuePair(buffer, "GenericName", this.genericName);
/* 166 */     addNameValuePair(buffer, "Comment", this.comment);
/* 167 */     addNameValuePair(buffer, "Hidden", this.hidden);
/* 168 */     addNameValuePair(buffer, "Icon", this.icon);
/* 169 */     addNameValuePair(buffer, "Path", this.path);
/* 170 */     addNameValuePair(buffer, "Terminal", this.terminal);
/* 171 */     addNameValuePair(buffer, "MimeType", this.mimeType);
/* 172 */     addNameValuePair(buffer, "Categories", this.categories);
/* 173 */     addNameValuePair(buffer, "NoDisplay", this.noDisplay);
/* 174 */     addNameValuePair(buffer, "URL", this.URL);
/*     */ 
/* 176 */     StringBuffer actionIds = new StringBuffer();
/* 177 */     for (int i = 0; i < this.actions.size(); i++)
/* 178 */       actionIds.append(((Action)this.actions.get(i)).actionID).append(";");
/* 179 */     if (this.actions.size() > 0) {
/* 180 */       addNameValuePair(buffer, "Actions", actionIds.toString());
/*     */     }
/* 182 */     actionIds = new StringBuffer();
/* 183 */     for (int i = 0; i < this.unityShortcuts.size(); i++)
/*     */     {
/* 185 */       actionIds.append(((UnityShortcut)this.unityShortcuts.get(i)).shortNameID).append(";");
/*     */     }
/* 187 */     if (this.unityShortcuts.size() > 0) {
/* 188 */       addNameValuePair(buffer, "X-Ayatana-Desktop-Shortcuts", actionIds.toString());
/*     */     }
/* 190 */     for (int i = 0; i < this.actions.size(); i++)
/*     */     {
/* 192 */       Action action = (Action)this.actions.get(i);
/* 193 */       String groupID = action.getActionIdentifierGroup();
/* 194 */       if (groupID != null)
/*     */       {
/* 196 */         buffer.append("\n");
/* 197 */         buffer.append("[").append(groupID).append("]").append("\n");
/* 198 */         addNameValuePair(buffer, "Name", action.name);
/* 199 */         addNameValuePair(buffer, "Icon", action.icon);
/* 200 */         addNameValuePair(buffer, "Exec", action.exec);
/*     */       }
/*     */     }
/*     */ 
/* 204 */     for (int i = 0; i < this.unityShortcuts.size(); i++)
/*     */     {
/* 206 */       UnityShortcut action = (UnityShortcut)this.unityShortcuts.get(i);
/* 207 */       groupID = action.getShortcutGroup();
/* 208 */       if (groupID != null)
/*     */       {
/* 210 */         buffer.append("\n");
/* 211 */         buffer.append("[").append(groupID).append("]").append("\n");
/* 212 */         addNameValuePair(buffer, "Name", action.displayName);
/* 213 */         addNameValuePair(buffer, "Exec", action.customisedExec);
/* 214 */         addNameValuePair(buffer, "TargetEnvironment", "Unity");
/*     */       }
/*     */     }
/*     */ 
/* 218 */     byte[] utf8Result = null;
/*     */     try
/*     */     {
/* 221 */       utf8Result = buffer.toString().getBytes("UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*     */     }
/* 225 */     if (utf8Result == null)
/*     */     {
/*     */       try
/*     */       {
/* 229 */         utf8Result = buffer.toString().getBytes("UTF8");
/*     */       }
/*     */       catch (UnsupportedEncodingException localUnsupportedEncodingException1)
/*     */       {
/*     */       }
/*     */     }
/* 235 */     FileOutputStream fout = new FileOutputStream(target);
/*     */     try
/*     */     {
/* 238 */       fout.write(utf8Result);
/*     */     }
/*     */     finally
/*     */     {
/* 242 */       fout.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addNameValuePair(StringBuffer buffer, String key, String value)
/*     */   {
/* 248 */     if ((key != null) && (value != null))
/* 249 */       buffer.append(key).append("=").append(value).append("\n");
/*     */   }
/*     */ 
/*     */   public void setURL(String url)
/*     */   {
/* 332 */     this.URL = url;
/*     */   }
/*     */ 
/*     */   public void setVersion(String version)
/*     */   {
/* 340 */     this.version = version;
/*     */   }
/*     */ 
/*     */   public void setGenericName(String genericName)
/*     */   {
/* 358 */     this.genericName = genericName;
/*     */   }
/*     */ 
/*     */   public void setNoDisplay(boolean noDisplay)
/*     */   {
/* 368 */     this.noDisplay = Boolean.toString(noDisplay);
/*     */   }
/*     */ 
/*     */   public void setComment(String comment)
/*     */   {
/* 376 */     this.comment = comment;
/*     */   }
/*     */ 
/*     */   public void setHidden(boolean hidden)
/*     */   {
/* 384 */     this.hidden = Boolean.toString(hidden);
/*     */   }
/*     */ 
/*     */   public void setIcon(String icon)
/*     */   {
/* 392 */     this.icon = icon;
/*     */   }
/*     */ 
/*     */   public void setPath(String path)
/*     */   {
/* 400 */     this.path = path;
/*     */   }
/*     */ 
/*     */   public void setTerminal(boolean terminal)
/*     */   {
/* 408 */     this.terminal = Boolean.toString(terminal);
/*     */   }
/*     */ 
/*     */   public void setMimeType(String mimeType)
/*     */   {
/* 416 */     this.mimeType = mimeType;
/*     */   }
/*     */ 
/*     */   public void setCategories(String categories)
/*     */   {
/* 426 */     this.categories = categories;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws IOException
/*     */   {
/* 431 */     DesktopShortcutUtil util = new DesktopShortcutUtil(1, "Test Application", "runapp 123");
/* 432 */     util.addAction("action1", "action 1", null, "run action 1");
/* 433 */     util.addAction("action2", "action 2", null, "run action 2");
/*     */ 
/* 435 */     util.addUnitySpecificRightClickOptions("uaction1", "uaction 1", "run uaction1");
/* 436 */     util.addUnitySpecificRightClickOptions("uaction2", "uaction 2", "run uaction2");
/* 437 */     util.writeDesktopShortcutForThisUser("file1");
/*     */   }
/*     */ 
/*     */   class Action
/*     */   {
/*     */     String actionID;
/*     */     String name;
/*     */     String exec;
/*     */     String icon;
/*     */ 
/*     */     Action()
/*     */     {
/*     */     }
/*     */ 
/*     */     public String getActionIdentifierGroup()
/*     */     {
/*  68 */       if (this.actionID != null)
/*  69 */         return "Desktop Action " + this.actionID;
/*  70 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   class UnityShortcut
/*     */   {
/*     */     String shortNameID;
/*     */     String displayName;
/*     */     String customisedExec;
/*     */ 
/*     */     UnityShortcut()
/*     */     {
/*     */     }
/*     */ 
/*     */     public String getShortcutGroup()
/*     */     {
/*  53 */       if (this.shortNameID != null)
/*  54 */         return this.shortNameID + " Shortcut Group";
/*  55 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.os.linux.util.DesktopShortcutUtil
 * JD-Core Version:    0.6.2
 */