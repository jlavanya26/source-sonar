/*    */ package org.sonar.samples.java.checks;
/*    */ 
/*    */ import com.google.common.collect.ImmutableList;
/*    */ import java.util.List;
/*    */ import org.sonar.check.Rule;
/*    */ import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
/*    */ import org.sonar.plugins.java.api.tree.ClassTree;
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
/*    */ @Rule(key = "AvoidSuperClass")
/*    */ public class AvoidSuperClassRule
/*    */   extends IssuableSubscriptionVisitor
/*    */ {
/* 40 */   public static final List<String> SUPER_CLASS_AVOID = (List<String>)ImmutableList.of("org.slf4j.Logger");
/*    */ 
/*    */ 
/*    */   
/*    */   public List<Tree.Kind> nodesToVisit() {
/* 45 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.CLASS);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void visitNode(Tree tree) {
/* 51 */     ClassTree treeClazz = (ClassTree)tree;
/*    */ 
/*    */     
/* 54 */     if (treeClazz.superClass() == null) {
/*    */       return;
/*    */     }
/*    */ 
/*    */     
/* 59 */     String superClassName = treeClazz.superClass().symbolType().fullyQualifiedName();
/*    */ 
/*    */     
/* 62 */     if (SUPER_CLASS_AVOID.contains(superClassName))
/* 63 */       reportIssue(tree, String.format("The usage of super class %s is forbidden", new Object[] { superClassName })); 
/*    */   }
/*    */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidSuperClassRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */