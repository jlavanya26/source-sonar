/*    */ package org.sonar.samples.java.checks;
/*    */ 
/*    */ import com.google.common.collect.ImmutableList;
/*    */ import java.util.List;
/*    */ import org.sonar.check.Rule;
/*    */ import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
/*    */ import org.sonar.plugins.java.api.semantic.Symbol;
/*    */ import org.sonar.plugins.java.api.semantic.Type;
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
/*    */ @Rule(key = "AvoidMethodWithSameTypeInArgument")
/*    */ public class MyCustomSubscriptionRule
/*    */   extends IssuableSubscriptionVisitor
/*    */ {
/*    */   public List<Tree.Kind> nodesToVisit() {
/* 42 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void visitNode(Tree tree) {
/* 50 */     MethodTree methodTree = (MethodTree)tree;
/*    */     
/* 52 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/* 53 */     Type returnType = methodSymbol.returnType().type();
/*    */     
/* 55 */     if (methodSymbol.parameterTypes().size() == 1) {
/* 56 */       Type argType = methodSymbol.parameterTypes().get(0);
/*    */       
/* 58 */       if (argType.is(returnType.fullyQualifiedName()))
/*    */       {
/* 60 */         reportIssue(tree, "message");
/*    */       }
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\MyCustomSubscriptionRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */