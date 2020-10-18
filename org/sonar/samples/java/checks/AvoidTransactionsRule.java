/*     */ package org.sonar.samples.java.checks;
/*     */ 
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import org.sonar.check.Rule;
/*     */ import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
/*     */ import org.sonar.plugins.java.api.tree.Arguments;
/*     */ import org.sonar.plugins.java.api.tree.AssignmentExpressionTree;
/*     */ import org.sonar.plugins.java.api.tree.BlockTree;
/*     */ import org.sonar.plugins.java.api.tree.CatchTree;
/*     */ import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.ExpressionTree;
/*     */ import org.sonar.plugins.java.api.tree.ForStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.IfStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
/*     */ import org.sonar.plugins.java.api.tree.MethodInvocationTree;
/*     */ import org.sonar.plugins.java.api.tree.MethodTree;
/*     */ import org.sonar.plugins.java.api.tree.StatementTree;
/*     */ import org.sonar.plugins.java.api.tree.SyntaxToken;
/*     */ import org.sonar.plugins.java.api.tree.Tree;
/*     */ import org.sonar.plugins.java.api.tree.TryStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.TypeCastTree;
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
/*     */ @Rule(key = "AvoidTransactions")
/*     */ public class AvoidTransactionsRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   boolean bLoggingActive = false;
/*     */   boolean commitTransactionFinally = false;
/*     */   boolean startTransactionTry = false;
/*     */   boolean abortTransactioncatch = false;
/*  57 */   private Hashtable<String, Integer> htReportIssue = null;
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
/*  72 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  82 */     MethodTree methodTree = (MethodTree)tree;
/*     */ 
/*     */     
/*  85 */     BlockTree blocktree = methodTree.block();
/*     */     
/*  87 */     Tree.Kind treeKind = methodTree.kind();
/*  88 */     log("&&&& - " + treeKind.toString() + " --> " + methodTree.simpleName().name());
/*     */     
/*  90 */     this.htReportIssue = new Hashtable<>();
/*     */     
/*  92 */     if (blocktree != null) {
/*  93 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  98 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  99 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(String str) {
/* 104 */     if (this.bLoggingActive);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Hashtable bumpUpCount(String sMethodName) {
/* 110 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 111 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 112 */       int iNewSize = iSize.intValue() + 1;
/* 113 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 115 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/* 117 */     log("########################### - bumpUp ht : " + this.htReportIssue);
/* 118 */     return this.htReportIssue;
/*     */   }
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String sMethodName, Tree eachLineTree, String strTransaction) {
/* 122 */     if (this.htReportIssue.size() > 0) {
/* 123 */       log("########################### - report ? ht : " + this.htReportIssue);
/*     */     }
/*     */     
/* 126 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 127 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 128 */       if (iSize.intValue() >= 1) {
/* 129 */         if ("commitTransaction".equals(strTransaction)) {
/* 130 */           if (!this.commitTransactionFinally)
/*     */           {
/*     */             
/* 133 */             reportIssue(eachLineTree, "SOGETI --> Commit Transaction is not allowed outside finally");
/*     */           }
/* 135 */         } else if ("abortTransaction".equals(strTransaction)) {
/* 136 */           if (!this.abortTransactioncatch)
/*     */           {
/*     */             
/* 139 */             reportIssue(eachLineTree, "SOGETI --> abort Transaction is not allowed outside catch block");
/*     */           
/*     */           }
/*     */         }
/* 143 */         else if (!this.startTransactionTry) {
/*     */ 
/*     */           
/* 146 */           reportIssue(eachLineTree, "SOGETI --> Commit Transaction is not allowed outside Try block");
/*     */         } 
/*     */         
/* 149 */         return true;
/*     */       } 
/*     */       
/* 152 */       return false;
/*     */     } 
/*     */     
/* 155 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/* 160 */     log("inside expr stmt");
/* 161 */     ExpressionStatementTree expSatetTree = (ExpressionStatementTree)eachLineTree;
/* 162 */     log("est kind of  - " + expSatetTree.kind().toString());
/*     */     
/* 164 */     ExpressionTree expressionTree = expSatetTree.expression();
/* 165 */     log("kind expr stmt " + expressionTree.kind().toString());
/*     */     
/* 167 */     if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 168 */       String sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 169 */       log("line - " + sRet);
/* 170 */     } else if ("ASSIGNMENT".equals(expressionTree.kind().toString())) {
/* 171 */       AssignmentExpressionTree aet = (AssignmentExpressionTree)expressionTree;
/* 172 */       invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */     } else {
/* 174 */       log("unexpected kind in switch 222 - " + expressionTree.kind().toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 179 */     TypeCastTree typeCastTree = (TypeCastTree)et;
/* 180 */     ExpressionTree expressionTree = typeCastTree.expression();
/* 181 */     String sRet = null;
/* 182 */     log("inside invokeTypeCastTreeMethod" + expressionTree.kind().toString());
/*     */     
/* 184 */     if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 185 */       sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 186 */       log("line - " + sRet);
/* 187 */     } else if ("MEMBER_SELECT".equals(expressionTree.kind().toString())) {
/* 188 */       sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 189 */       log("line - " + sRet);
/*     */     } else {
/* 191 */       log("unexpected kind in invokeTypeCastTreeMethod - " + expressionTree.kind().toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree et, Tree eachLineTree) {
/* 196 */     log("et kind of aest - " + et.kind().toString());
/*     */     
/* 198 */     if ("TYPE_CAST".equals(et.kind().toString())) {
/* 199 */       invokeTypeCastTreeMethod(et, eachLineTree);
/* 200 */     } else if ("METHOD_INVOCATION".equals(et.kind().toString())) {
/* 201 */       String sRet = invokeMethodInvocationTreeMethod(et, eachLineTree);
/* 202 */       log("line - " + sRet);
/*     */     } else {
/* 204 */       log("unexpected kind in switch invokeAssignmentExpressionStatementTreeMethod - " + et.kind().toString());
/*     */     } 
/*     */   } private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 209 */     String sRet = null;
/* 210 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 211 */     log("*** variable kind *** - " + variableTree.type().kind().toString());
/*     */     
/* 213 */     VariableTree myVtt = (VariableTree)variableTree.symbol().declaration();
/* 214 */     String myVariableName = variableTree.symbol().name();
/* 215 */     String myVariableType = variableTree.symbol().type().name();
/* 216 */     VariableTree variableTree1 = myVtt;
/*     */     try {
/* 218 */       expressionTree = myVtt.initializer();
/* 219 */       log("MYETT  - " + expressionTree.kind().toString());
/* 220 */     } catch (Exception ex) {
/* 221 */       log(" --- inside exception --" + ex);
/* 222 */       if (expressionTree == null) {
/* 223 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/* 226 */     if ("TYPE_CAST".equals(tree.kind().toString())) {
/* 227 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 228 */     } else if ("MEMBER_SELECT".equals(tree.kind().toString())) {
/* 229 */       sRet = invokeMemberSelectMethod(tree, eachLineTree);
/* 230 */       log("line - " + myVariableType + " " + myVariableName + " = " + sRet + "--args--");
/* 231 */     } else if ("METHOD_INVOCATION".equals(tree.kind().toString())) {
/* 232 */       sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/* 233 */       log("line - " + myVariableType + " " + myVariableName + " = " + sRet);
/*     */     } else {
/* 235 */       log("unexpected kind in switch - " + tree.kind().toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 240 */     MethodInvocationTree myMit = (MethodInvocationTree)mytree;
/* 241 */     ExpressionTree expressionTree = myMit.methodSelect();
/* 242 */     log("dinakar  - " + expressionTree.kind().toString());
/*     */     
/* 244 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 245 */     String sMyArgs = "";
/*     */     
/* 247 */     if (myMit.arguments().size() != 0) {
/* 248 */       log("****** in mit  *********" + myMit.arguments().size());
/*     */       
/* 250 */       Arguments<Tree> arguments = myMit.arguments();
/*     */       
/* 252 */       sMyArgs = "(";
/* 253 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/* 254 */         log("inside arguments loop 222 - " + ((Tree)arguments.get(iArgCnt)).kind().toString());
/*     */         
/* 256 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 257 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 258 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 259 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 260 */           sRet = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 263 */       sMyArgs = sMyArgs + "--)";
/* 264 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 266 */       sMyArgs = "()";
/*     */     } 
/* 268 */     return sRet + sMyArgs;
/*     */   }
/*     */   
/*     */   private String invokeMemberSelectMethod(Tree ettemp, Tree eachLineTree) {
/* 272 */     String strVar = "";
/*     */     
/* 274 */     if ("MEMBER_SELECT".equals(ettemp.kind().toString())) {
/* 275 */       MemberSelectExpressionTree mset = (MemberSelectExpressionTree)ettemp;
/* 276 */       String myDeclarationMethodName = mset.firstToken().text();
/* 277 */       String myDeclarationCallingMethodName = mset.identifier().name();
/* 278 */       strVar = myDeclarationMethodName + "." + myDeclarationCallingMethodName;
/*     */       
/* 280 */       if ("startTransaction".equals(mset.identifier().name()) || "commitTransaction".equals(mset.identifier().name()) || "abortTransaction".equals(mset.identifier().name())) {
/*     */         
/* 282 */         bumpUpCount(myDeclarationMethodName);
/* 283 */         bCheckAndReportIssueNow(myDeclarationMethodName, eachLineTree, mset.identifier().name());
/*     */       } 
/*     */     } else {
/*     */       
/* 287 */       log("unexpected kind in iinvokeMemberSelectMethod - " + ettemp.kind().toString());
/*     */     } 
/* 289 */     return strVar;
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 293 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 294 */     log("*** inside invokeForStmtTreeMethod kind *** - " + forStmtTree.kind().toString());
/*     */     
/* 296 */     StatementTree et = forStmtTree.statement();
/* 297 */     log("*** et kind *** - " + et.kind().toString());
/* 298 */     if ("BLOCK".equals(et.kind().toString())) {
/* 299 */       BlockTree bt = (BlockTree)forStmtTree.statement();
/* 300 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 301 */     } else if ("EXPRESSION_STATEMENT".equals(et.kind().toString())) {
/* 302 */       invokeExpressionStatementTreeMethod((Tree)et);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 307 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/* 308 */     ExpressionTree exprTree = ifStmtTree.condition();
/* 309 */     log("*** if stmt condition kind *** - " + exprTree.kind().toString());
/*     */     
/* 311 */     if ("LOGICAL_COMPLEMENT".equals(exprTree.kind().toString())) {
/* 312 */       UnaryExpressionTree uet = (UnaryExpressionTree)exprTree;
/* 313 */       ExpressionTree newet = uet.expression();
/* 314 */       log("*** logical complement kind *** - " + newet.kind().toString());
/*     */       
/* 316 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 317 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/*     */       }
/* 319 */     } else if ("METHOD_INVOCATION".equals(exprTree.kind().toString())) {
/* 320 */       invokeMethodInvocationTreeMethod((Tree)exprTree, ifLoopTree);
/*     */     } 
/*     */     
/* 323 */     StatementTree statementTree = ifStmtTree.thenStatement();
/* 324 */     log("*** if stmt 2 kind *** - " + statementTree.kind().toString());
/*     */     
/* 326 */     invokeIfElseStatementTreeMethod(statementTree);
/*     */ 
/*     */     
/*     */     try {
/* 330 */       while (ifStmtTree.elseStatement() != null) {
/* 331 */         statementTree = ifStmtTree.elseStatement();
/* 332 */         log("*** if stmt 222 kind *** - " + statementTree.kind().toString());
/*     */         
/* 334 */         if ("IF_STATEMENT".equals(statementTree.kind().toString())) {
/*     */           
/* 336 */           ifStmtTree = (IfStatementTree)statementTree;
/* 337 */           log("*** if stmt two kind *** - " + ifStmtTree.kind().toString());
/* 338 */           StatementTree newst = ifStmtTree.thenStatement();
/* 339 */           invokeIfElseStatementTreeMethod(newst); continue;
/*     */         } 
/* 341 */         log("*** inside else of if fif if ****");
/*     */         
/* 343 */         invokeIfElseStatementTreeMethod(statementTree);
/* 344 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 347 */     } catch (Exception ex) {
/* 348 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree st) {
/* 353 */     if ("BLOCK".equals(st.kind().toString())) {
/* 354 */       BlockTree bt = (BlockTree)st;
/* 355 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 356 */     } else if ("EXPRESSION_STATEMENT".equals(st.kind().toString())) {
/* 357 */       invokeExpressionStatementTreeMethod((Tree)st);
/*     */     } else {
/* 359 */       log("unexpected kind in is stmt tree - " + st.kind().toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 367 */     Tree eachLineTree = null;
/* 368 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/*     */       
/* 370 */       eachLineTree = listOfTrees.get(iLine);
/* 371 */       log("*** kind inside for loop *** ------------------------------------------- " + eachLineTree.kind().toString());
/* 372 */       if ("VARIABLE".equals(eachLineTree.kind().toString())) {
/* 373 */         invokeVariableTreeMethod(eachLineTree);
/* 374 */       } else if ("EXPRESSION_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 375 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 376 */       } else if ("TRY_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 377 */         TryStatementTree tst = (TryStatementTree)eachLineTree;
/* 378 */         BlockTree btTryStmtTree = tst.block();
/* 379 */         this.startTransactionTry = true;
/*     */         
/* 381 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/* 382 */         this.startTransactionTry = false;
/* 383 */         List<? extends CatchTree> catches = tst.catches();
/*     */         
/* 385 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 386 */           CatchTree ct = catches.get(iCatchCnt);
/* 387 */           BlockTree btCatch = ct.block();
/* 388 */           this.abortTransactioncatch = true;
/* 389 */           checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/* 390 */           this.abortTransactioncatch = false;
/*     */         } 
/*     */         try {
/* 393 */           BlockTree btTryStmtFinallyTree = tst.finallyBlock();
/* 394 */           log("btTryStmtFinallyTree  - " + btTryStmtFinallyTree.kind().toString());
/* 395 */           this.commitTransactionFinally = true;
/*     */           
/* 397 */           checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/* 398 */           this.commitTransactionFinally = false;
/* 399 */         } catch (Exception ex) {
/* 400 */           log(" --- no finally block available exception -- " + ex);
/*     */         } 
/* 402 */       } else if ("FOR_STATEMENT".equals(eachLineTree.kind().toString())) {
/*     */         
/* 404 */         invokeForStmtTreeMethod(eachLineTree);
/*     */       }
/* 406 */       else if ("IF_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 407 */         invokeIfStmtTreeMethod(eachLineTree);
/* 408 */       } else if ("WHILE_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 409 */         WhileStatementTree wst = (WhileStatementTree)eachLineTree;
/* 410 */         StatementTree st = wst.statement();
/* 411 */         invokeIfElseStatementTreeMethod(st);
/*     */       } else {
/* 413 */         log("unexpected kind in switch 444  - " + eachLineTree.kind().toString());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidTransactionsRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */