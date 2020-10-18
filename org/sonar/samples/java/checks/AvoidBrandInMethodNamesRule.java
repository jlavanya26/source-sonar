/*    */ package org.sonar.samples.java.checks;
/*    */ 
/*    */ import org.sonar.check.Rule;
/*    */ import org.sonar.plugins.java.api.JavaCheck;
/*    */ import org.sonar.plugins.java.api.JavaFileScanner;
/*    */ import org.sonar.plugins.java.api.JavaFileScannerContext;
/*    */ import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
/*    */ import org.sonar.plugins.java.api.tree.CaseGroupTree;
/*    */ import org.sonar.plugins.java.api.tree.CaseLabelTree;
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
/*    */ @Rule(key = "AvoidBrandInMethodNames")
/*    */ public class AvoidBrandInMethodNamesRule
/*    */   extends BaseTreeVisitor
/*    */   implements JavaFileScanner
/*    */ {
/*    */   private JavaFileScannerContext context;
/*    */   protected static final String COMPANY_NAME = "MyCompany";
/*    */   
/*    */   public void scanFile(JavaFileScannerContext context) {
/* 40 */     this.context = context;
/*    */ 
/*    */     
/* 43 */     scan((Tree)context.getTree());
/*    */ 
/*    */     
/* 46 */     System.out.println(PrinterVisitor.print((Tree)context.getTree()));
/*    */   }
/*    */ 
/*    */   
/*    */   public void visitCaseGroup(CaseGroupTree tree) {
/* 51 */     scan(tree.labels());
/* 52 */     scan(tree.body());
/*    */   }
/*    */   
/*    */   public void visitCaseLabel(CaseLabelTree tree) {
/* 56 */     scan((Tree)tree.expression());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void visitMethod(MethodTree tree) {
/* 67 */     if (tree.simpleName().name().toUpperCase().contains("MyCompany".toUpperCase()))
/*    */     {
/* 69 */       this.context.reportIssue((JavaCheck)this, (Tree)tree, "Avoid using Brand in method name");
/*    */     }
/*    */ 
/*    */     
/* 73 */     super.visitMethod(tree);
/*    */   }
/*    */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidBrandInMethodNamesRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */