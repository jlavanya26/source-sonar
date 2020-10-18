/*    */ package org.sonar.samples.java;
/*    */ 
/*    */ import org.sonar.api.Plugin;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MyJavaRulesPlugin
/*    */   implements Plugin
/*    */ {
/*    */   public void define(Plugin.Context context) {
/* 33 */     context.addExtension(MyJavaRulesDefinition.class);
/*    */ 
/*    */     
/* 36 */     context.addExtension(MyJavaFileCheckRegistrar.class);
/*    */   }
/*    */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\MyJavaRulesPlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */