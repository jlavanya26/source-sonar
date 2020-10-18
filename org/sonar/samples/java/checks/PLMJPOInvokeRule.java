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
/*     */ 
/*     */ @Rule(key = "PLMJPOInvoke")
/*     */ public class PLMJPOInvokeRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   boolean bLoggingActive = false;
/*  58 */   private Hashtable<String, Integer> htReportIssue = null;
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
/*  73 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  78 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  80 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  81 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  83 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  84 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  86 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  92 */     BlockTree blocktree = methodTree.block();
/*  93 */     Tree.Kind tk = methodTree.kind();
/*  94 */     log("&&&& - " + tk.toString() + " --> " + methodTree.simpleName().name());
/*     */     
/*  96 */     this.htReportIssue = new Hashtable<>();
/*     */     
/*  98 */     if (blocktree != null) {
/*  99 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/* 105 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/* 106 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void log(String str) {
/* 112 */     if (this.bLoggingActive) {
/* 113 */       System.out.println(str);
/*     */     }
/*     */   }
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 120 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 121 */     VariableTree myVtt = (VariableTree)variableTree.symbol().declaration();
/*     */     
/* 123 */     VariableTree variableTree1 = myVtt;
/*     */     try {
/* 125 */       expressionTree = myVtt.initializer();
/* 126 */       log("MYETT  - " + expressionTree.kind().toString());
/*     */     }
/* 128 */     catch (Exception ex) {
/* 129 */       log(" --- inside exception --" + ex);
/* 130 */       if (expressionTree == null) {
/* 131 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/*     */     
/* 135 */     if (tree.kind().toString().equals("NEW_CLASS")) {
/* 136 */       NewClassTree domObjNewTree = (NewClassTree)tree;
/* 137 */       if (domObjNewTree.identifier().toString().contains("mxJPO")) {
/* 138 */         reportIssue(eachLineTree, "Sogeti JPO Rule: JPO.invoke should be used instaed of JPO instantiation");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/* 146 */     ExpressionStatementTree est = (ExpressionStatementTree)eachLineTree;
/* 147 */     ExpressionTree expressionTree = est.expression();
/*     */     
/* 149 */     if (expressionTree.kind().toString().equals("ASSIGNMENT")) {
/* 150 */       AssignmentExpressionTree aet = (AssignmentExpressionTree)expressionTree;
/* 151 */       invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree et, Tree eachLineTree) {
/* 158 */     if (et.kind().toString().equals("NEW_CLASS")) {
/* 159 */       NewClassTree domNewTree = (NewClassTree)et;
/* 160 */       if (domNewTree.identifier().toString().contains("mxJPO"))
/* 161 */         reportIssue(eachLineTree, "Sogeti JPO Rule: JPO.invoke should be used instaed of JPO instantiation"); 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 166 */     Tree eachLineTree = null;
/* 167 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 168 */     StatementTree et = forStmtTree.statement();
/*     */     
/* 170 */     if ("BLOCK".equals(et.kind().toString())) {
/* 171 */       BlockTree bt = (BlockTree)forStmtTree.statement();
/* 172 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 173 */     } else if ("EXPRESSION_STATEMENT".equals(et.kind().toString())) {
/* 174 */       invokeExpressionStatementTreeMethod((Tree)et);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 179 */     Tree eachLineTree = null;
/* 180 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 181 */     log("*** for each stmt kind *** - " + forEachStmt.kind().toString());
/*     */     
/* 183 */     BlockTree bt = (BlockTree)forEachStmt.statement();
/* 184 */     checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 189 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */ 
/*     */     
/* 192 */     ExpressionTree et = ifStmtTree.condition();
/* 193 */     if ("LOGICAL_COMPLEMENT".equals(et.kind().toString())) {
/* 194 */       UnaryExpressionTree uet = (UnaryExpressionTree)et;
/* 195 */       ExpressionTree newet = uet.expression();
/* 196 */       log("*** logical complement kind *** - " + newet.kind().toString());
/*     */       
/* 198 */       if (!"METHOD_INVOCATION".equals(newet.kind().toString()))
/*     */       {
/* 200 */         if ("IDENTIFIER".equals(newet.kind().toString()));
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 205 */     else if ("METHOD_INVOCATION".equals(et.kind().toString())) {
/*     */     
/*     */     } 
/*     */ 
/*     */     
/* 210 */     StatementTree st = ifStmtTree.thenStatement();
/*     */     
/* 212 */     invokeIfElseStatementTreeMethod(st);
/*     */ 
/*     */     
/*     */     try {
/* 216 */       while (ifStmtTree.elseStatement() != null) {
/* 217 */         st = ifStmtTree.elseStatement();
/* 218 */         log("*** if stmt 222 kind *** - " + st.kind().toString());
/*     */         
/* 220 */         if ("IF_STATEMENT".equals(st.kind().toString())) {
/* 221 */           ifStmtTree = (IfStatementTree)st;
/* 222 */           log("*** if stmt two kind *** - " + ifStmtTree.kind().toString());
/* 223 */           StatementTree newst = ifStmtTree.thenStatement();
/* 224 */           invokeIfElseStatementTreeMethod(newst);
/*     */           continue;
/*     */         } 
/* 227 */         invokeIfElseStatementTreeMethod(st);
/* 228 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 231 */     } catch (Exception ex) {
/* 232 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree st) {
/* 237 */     if (st.kind().toString().equals("BLOCK")) {
/* 238 */       BlockTree bt = (BlockTree)st;
/* 239 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 240 */     } else if (st.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 241 */       invokeExpressionStatementTreeMethod((Tree)st);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 247 */     Tree eachLineTree = null;
/*     */     
/* 249 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/*     */       
/* 251 */       eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 253 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 254 */         invokeVariableTreeMethod(eachLineTree);
/* 255 */       } else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 256 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 257 */       } else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 258 */         invokeIfStmtTreeMethod(eachLineTree);
/* 259 */       } else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 260 */         invokeForStmtTreeMethod(eachLineTree);
/* 261 */       } else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 262 */         invokeForEachStmtTreeMethod(eachLineTree);
/* 263 */       } else if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 264 */         TryStatementTree tst = (TryStatementTree)eachLineTree;
/* 265 */         BlockTree btTryStmtTree = tst.block();
/*     */ 
/*     */         
/* 268 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */         
/* 270 */         List<? extends CatchTree> catches = tst.catches();
/*     */         
/* 272 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 273 */           CatchTree ct = catches.get(iCatchCnt);
/* 274 */           BlockTree btCatch = ct.block();
/* 275 */           checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */         } 
/*     */         
/*     */         try {
/* 279 */           BlockTree btTryStmtFinallyTree = tst.finallyBlock();
/* 280 */           log("btTryStmtFinallyTree  - " + btTryStmtFinallyTree.kind().toString());
/*     */ 
/*     */           
/* 283 */           checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */         }
/* 285 */         catch (Exception ex) {
/* 286 */           log(" --- no finally block available exception -- " + ex);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMJPOInvokeRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */