/*    */ package org.sonar.samples.java.checks;
/*    */ 
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ import org.sonar.check.Rule;
/*    */ import org.sonar.plugins.java.api.JavaCheck;
/*    */ import org.sonar.plugins.java.api.JavaFileScanner;
/*    */ import org.sonar.plugins.java.api.JavaFileScannerContext;
/*    */ import org.sonar.plugins.java.api.semantic.Symbol;
/*    */ import org.sonar.plugins.java.api.semantic.SymbolMetadata;
/*    */ import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
/*    */ import org.sonar.plugins.java.api.tree.MethodTree;
/*    */ import org.sonar.plugins.java.api.tree.Tree;
/*    */ import org.sonar.plugins.java.api.tree.TypeTree;
/*    */ import org.sonar.plugins.java.api.tree.VariableTree;
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
/*    */ @Rule(key = "SpringControllerRequestMappingEntity")
/*    */ public class SpringControllerRequestMappingEntityRule
/*    */   extends BaseTreeVisitor
/*    */   implements JavaFileScanner
/*    */ {
/* 37 */   private static final Logger LOGGER = LoggerFactory.getLogger(SpringControllerRequestMappingEntityRule.class);
/*    */   
/*    */   private JavaFileScannerContext context;
/*    */ 
/*    */   
/*    */   public void scanFile(JavaFileScannerContext context) {
/* 43 */     this.context = context;
/*    */     
/* 45 */     scan((Tree)context.getTree());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void visitMethod(MethodTree tree) {
/* 55 */     Symbol.MethodSymbol methodSymbol = tree.symbol();
/*    */     
/* 57 */     SymbolMetadata parentClassOwner = methodSymbol.owner().metadata();
/* 58 */     boolean isControllerContext = parentClassOwner.isAnnotatedWith("org.springframework.stereotype.Controller");
/*    */     
/* 60 */     if (isControllerContext)
/*    */     {
/* 62 */       if (methodSymbol.metadata().isAnnotatedWith("org.springframework.web.bind.annotation.RequestMapping"))
/*    */       {
/* 64 */         for (VariableTree param : tree.parameters()) {
/* 65 */           TypeTree typeOfParam = param.type();
/* 66 */           if (typeOfParam.symbolType().symbol().metadata().isAnnotatedWith("javax.persistence.Entity")) {
/* 67 */             this.context.reportIssue((JavaCheck)this, (Tree)typeOfParam, String.format("Don't use %s here because it's an @Entity", new Object[] { typeOfParam.symbolType().name() }));
/*    */           }
/*    */         } 
/*    */       }
/*    */     }
/*    */ 
/*    */     
/* 74 */     super.visitMethod(tree);
/*    */   }
/*    */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\SpringControllerRequestMappingEntityRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */