/*    */ package org.sonar.samples.java.checks;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.sonar.check.Rule;
/*    */ import org.sonar.check.RuleProperty;
/*    */ import org.sonar.plugins.java.api.JavaCheck;
/*    */ import org.sonar.plugins.java.api.JavaFileScanner;
/*    */ import org.sonar.plugins.java.api.JavaFileScannerContext;
/*    */ import org.sonar.plugins.java.api.tree.AnnotationTree;
/*    */ import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
/*    */ import org.sonar.plugins.java.api.tree.IdentifierTree;
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
/*    */ @Rule(key = "AvoidAnnotation")
/*    */ public class AvoidAnnotationRule
/*    */   extends BaseTreeVisitor
/*    */   implements JavaFileScanner
/*    */ {
/*    */   private static final String DEFAULT_VALUE = "Inject";
/*    */   private JavaFileScannerContext context;
/*    */   @RuleProperty(defaultValue = "Inject", description = "Name of the annotation to avoid, without the prefix @, for instance 'Override'")
/*    */   protected String name;
/*    */   
/*    */   public void scanFile(JavaFileScannerContext context) {
/* 52 */     this.context = context;
/*    */     
/* 54 */     scan((Tree)context.getTree());
/*    */     
/* 56 */     System.out.println(PrinterVisitor.print((Tree)context.getTree()));
/*    */   }
/*    */ 
/*    */   
/*    */   public void visitMethod(MethodTree tree) {
/* 61 */     List<AnnotationTree> annotations = tree.modifiers().annotations();
/* 62 */     for (AnnotationTree annotationTree : annotations) {
/* 63 */       if (annotationTree.annotationType().is(new Tree.Kind[] { Tree.Kind.IDENTIFIER })) {
/* 64 */         IdentifierTree idf = (IdentifierTree)annotationTree.annotationType();
/*    */ 
/*    */         
/* 67 */         if (idf.name().equals(this.name)) {
/* 68 */           this.context.reportIssue((JavaCheck)this, (Tree)idf, String.format("Avoid using annotation @%s", new Object[] { this.name }));
/*    */         }
/*    */       } 
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 75 */     super.visitMethod(tree);
/*    */   }
/*    */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidAnnotationRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */