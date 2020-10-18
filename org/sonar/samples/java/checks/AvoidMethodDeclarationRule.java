/*    */ package org.sonar.samples.java.checks;
/*    */ 
/*    */ import org.sonar.check.Rule;
/*    */ import org.sonar.plugins.java.api.JavaCheck;
/*    */ import org.sonar.plugins.java.api.JavaFileScanner;
/*    */ import org.sonar.plugins.java.api.JavaFileScannerContext;
/*    */ import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
/*    */ import org.sonar.plugins.java.api.tree.MethodTree;
/*    */ import org.sonar.plugins.java.api.tree.Tree;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Rule(key = "AvoidMethodDeclaration")
/*    */ public class AvoidMethodDeclarationRule
/*    */   extends BaseTreeVisitor
/*    */   implements JavaFileScanner
/*    */ {
/*    */   private JavaFileScannerContext context;
/*    */   
/*    */   public void scanFile(JavaFileScannerContext context) {
/* 51 */     this.context = context;
/*    */ 
/*    */     
/* 54 */     scan((Tree)context.getTree());
/*    */ 
/*    */     
/* 57 */     System.out.println(PrinterVisitor.print((Tree)context.getTree()));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void visitMethod(MethodTree tree) {
/* 69 */     this.context.reportIssue((JavaCheck)this, (Tree)tree, "Avoid declaring methods (don't ask why)");
/*    */ 
/*    */ 
/*    */     
/* 73 */     super.visitMethod(tree);
/*    */   }
/*    */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidMethodDeclarationRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */