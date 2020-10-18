/*     */ package org.sonar.samples.java.checks;
/*     */ 
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import org.sonar.check.Rule;
/*     */ import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
/*     */ import org.sonar.plugins.java.api.semantic.Symbol;
/*     */ import org.sonar.plugins.java.api.semantic.Type;
/*     */ import org.sonar.plugins.java.api.tree.AssignmentExpressionTree;
/*     */ import org.sonar.plugins.java.api.tree.BlockTree;
/*     */ import org.sonar.plugins.java.api.tree.CatchTree;
/*     */ import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.ExpressionTree;
/*     */ import org.sonar.plugins.java.api.tree.ForEachStatement;
/*     */ import org.sonar.plugins.java.api.tree.ForStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.IfStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.MethodTree;
/*     */ import org.sonar.plugins.java.api.tree.NewClassTree;
/*     */ import org.sonar.plugins.java.api.tree.StatementTree;
/*     */ import org.sonar.plugins.java.api.tree.SyntaxToken;
/*     */ import org.sonar.plugins.java.api.tree.Tree;
/*     */ import org.sonar.plugins.java.api.tree.TryStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.UnaryExpressionTree;
/*     */ import org.sonar.plugins.java.api.tree.VariableTree;
/*     */ import org.sonar.plugins.java.api.tree.WhileStatementTree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Rule(key = "AvoidUseDomainObject")
/*     */ public class AvoidUseDomainObjectRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   boolean bLoggingActive = false;
/*  39 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  54 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  60 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  62 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  63 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  65 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  66 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  68 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  74 */     BlockTree blocktree = methodTree.block();
/*  75 */     Tree.Kind tk = methodTree.kind();
/*  76 */     log("&&&& - " + tk.toString() + " --> " + methodTree.simpleName().name());
/*     */     
/*  78 */     this.htReportIssue = new Hashtable<>();
/*     */     
/*  80 */     if (blocktree != null) {
/*  81 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  87 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  88 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void log(String str) {
/*  94 */     if (this.bLoggingActive) {
/*  95 */       System.out.println(str);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 103 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 104 */     VariableTree myVariabletree = (VariableTree)variableTree.symbol().declaration();
/*     */     
/* 106 */     VariableTree variableTree1 = myVariabletree;
/*     */     try {
/* 108 */       expressionTree = myVariabletree.initializer();
/* 109 */       log("MYETT  - " + expressionTree.kind().toString());
/*     */     }
/* 111 */     catch (Exception ex) {
/* 112 */       log(" --- inside exception --" + ex);
/* 113 */       if (expressionTree == null) {
/* 114 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 119 */     if (tree.kind().toString().equals("NEW_CLASS")) {
/* 120 */       NewClassTree domObjNewTree = (NewClassTree)tree;
/* 121 */       if (domObjNewTree.identifier().toString().equals("DomainObject")) {
/* 122 */         reportIssue(eachLineTree, "SOGETI --> DomainObject.newInstance should be used.");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/* 130 */     ExpressionStatementTree expSatetTree = (ExpressionStatementTree)eachLineTree;
/* 131 */     ExpressionTree expressionTree = expSatetTree.expression();
/*     */     
/* 133 */     if (expressionTree.kind().toString().equals("ASSIGNMENT")) {
/* 134 */       AssignmentExpressionTree aet = (AssignmentExpressionTree)expressionTree;
/* 135 */       invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree experessionTree, Tree eachLineTree) {
/* 143 */     if (experessionTree.kind().toString().equals("NEW_CLASS")) {
/* 144 */       NewClassTree domNewTree = (NewClassTree)experessionTree;
/* 145 */       if (domNewTree.identifier().toString().equals("DomainObject")) {
/* 146 */         reportIssue(eachLineTree, "SOGETI --> DomainObject.newInstance should be used.");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 153 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 154 */     StatementTree stateTree = forStmtTree.statement();
/*     */     
/* 156 */     if ("BLOCK".equals(stateTree.kind().toString())) {
/* 157 */       BlockTree blockTree = (BlockTree)forStmtTree.statement();
/* 158 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 159 */     } else if ("EXPRESSION_STATEMENT".equals(stateTree.kind().toString())) {
/* 160 */       invokeExpressionStatementTreeMethod((Tree)stateTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 167 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 168 */     log("*** for each stmt kind *** - " + forEachStmt.kind().toString());
/*     */     
/* 170 */     BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 171 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 178 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */ 
/*     */     
/* 181 */     ExpressionTree exprTree = ifStmtTree.condition();
/* 182 */     if ("LOGICAL_COMPLEMENT".equals(exprTree.kind().toString())) {
/* 183 */       UnaryExpressionTree unaryExprtree = (UnaryExpressionTree)exprTree;
/* 184 */       ExpressionTree newExprTree = unaryExprtree.expression();
/* 185 */       log("*** logical complement kind *** - " + newExprTree.kind().toString());
/*     */       
/* 187 */       if (!"METHOD_INVOCATION".equals(newExprTree.kind().toString()))
/*     */       {
/* 189 */         if ("IDENTIFIER".equals(newExprTree.kind().toString()));
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 194 */     else if ("METHOD_INVOCATION".equals(exprTree.kind().toString())) {
/*     */     
/*     */     } 
/*     */ 
/*     */     
/* 199 */     StatementTree stateTree = ifStmtTree.thenStatement();
/*     */     
/* 201 */     invokeIfElseStatementTreeMethod(stateTree);
/*     */ 
/*     */     
/*     */     try {
/* 205 */       while (ifStmtTree.elseStatement() != null) {
/* 206 */         stateTree = ifStmtTree.elseStatement();
/* 207 */         log("*** if stmt 222 kind *** - " + stateTree.kind().toString());
/*     */         
/* 209 */         if ("IF_STATEMENT".equals(stateTree.kind().toString())) {
/* 210 */           ifStmtTree = (IfStatementTree)stateTree;
/* 211 */           log("*** if stmt two kind *** - " + ifStmtTree.kind().toString());
/* 212 */           StatementTree newStateTree = ifStmtTree.thenStatement();
/* 213 */           invokeIfElseStatementTreeMethod(newStateTree);
/*     */           continue;
/*     */         } 
/* 216 */         invokeIfElseStatementTreeMethod(stateTree);
/* 217 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 220 */     } catch (Exception ex) {
/* 221 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree stateTree) {
/* 228 */     if (stateTree.kind().toString().equals("BLOCK")) {
/* 229 */       BlockTree blockTree = (BlockTree)stateTree;
/* 230 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 231 */     } else if (stateTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 232 */       invokeExpressionStatementTreeMethod((Tree)stateTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 242 */     Tree eachLineTree = null;
/*     */     
/* 244 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/*     */       
/* 246 */       eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 248 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 249 */         invokeVariableTreeMethod(eachLineTree);
/* 250 */       } else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 251 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 252 */       } else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 253 */         invokeIfStmtTreeMethod(eachLineTree);
/* 254 */       } else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 255 */         invokeForStmtTreeMethod(eachLineTree);
/* 256 */       } else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 257 */         invokeForEachStmtTreeMethod(eachLineTree);
/* 258 */       } else if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 259 */         TryStatementTree tryStateTree = (TryStatementTree)eachLineTree;
/* 260 */         BlockTree btTryStmtTree = tryStateTree.block();
/*     */ 
/*     */         
/* 263 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */         
/* 265 */         List<? extends CatchTree> catches = tryStateTree.catches();
/*     */         
/* 267 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 268 */           CatchTree ct = catches.get(iCatchCnt);
/* 269 */           BlockTree btCatch = ct.block();
/* 270 */           checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */         } 
/*     */         
/*     */         try {
/* 274 */           BlockTree btTryStmtFinallyTree = tryStateTree.finallyBlock();
/* 275 */           log("btTryStmtFinallyTree  - " + btTryStmtFinallyTree.kind().toString());
/*     */ 
/*     */           
/* 278 */           checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */         }
/* 280 */         catch (Exception ex) {
/* 281 */           log(" --- no finally block available exception -- " + ex);
/*     */         } 
/* 283 */       } else if (eachLineTree.kind().toString().equals("WHILE_STATEMENT")) {
/* 284 */         WhileStatementTree whileStateTree = (WhileStatementTree)eachLineTree;
/* 285 */         StatementTree stateTree = whileStateTree.statement();
/* 286 */         invokeIfElseStatementTreeMethod(stateTree);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidUseDomainObjectRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */