/*    */ package org.sonar.samples.java.checks;
/*    */ 
/*    */ import com.google.common.collect.ImmutableList;
/*    */ import java.util.List;
/*    */ import org.sonar.check.Rule;
/*    */ import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
/*    */ import org.sonar.plugins.java.api.tree.NewClassTree;
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
/*    */ @Rule(key = "AvoidUnmodifiableList")
/*    */ public class AvoidUnmodifiableListRule
/*    */   extends IssuableSubscriptionVisitor
/*    */ {
/*    */   public List<Tree.Kind> nodesToVisit() {
/* 36 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.NEW_CLASS);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void visitNode(Tree tree) {
/* 42 */     if (((NewClassTree)tree).symbolType().isSubtypeOf("org.apache.commons.collections4.list.UnmodifiableList"))
/* 43 */       reportIssue(tree, "Avoid using UnmodifiableList"); 
/*    */   }
/*    */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidUnmodifiableListRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */