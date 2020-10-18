/*     */ package org.sonar.samples.java.checks;
/*     */ 
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import org.sonar.check.Rule;
/*     */ import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
/*     */ import org.sonar.plugins.java.api.semantic.Symbol;
/*     */ import org.sonar.plugins.java.api.semantic.Type;
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
/*     */ @Rule(key = "PushContextNotInLoopMustBeInTry")
/*     */ public class PushContextNotInLoopMustBeInTryRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   boolean bLoggingActive = false;
/*     */   boolean finallyBlockVar = false;
/*     */   boolean pushContextTry = false;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  81 */     MethodTree methodTree = (MethodTree)tree;
/*     */ 
/*     */     
/*  84 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  85 */     Type returnType = methodSymbol.returnType().type();
/*     */ 
/*     */     
/*  88 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  89 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  91 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  97 */     BlockTree blocktree = methodTree.block();
/*  98 */     Tree.Kind treeKind = methodTree.kind();
/*  99 */     log("&&&& - " + treeKind.toString() + " --> " + methodTree.simpleName().name());
/* 100 */     this.htReportIssue = new Hashtable<>();
/*     */     
/* 102 */     if (blocktree != null) {
/* 103 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/* 108 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/* 109 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(String str) {
/* 114 */     if (this.bLoggingActive);
/*     */   }
/*     */ 
/*     */   
/*     */   private Hashtable bumpUpCount(String sMethodName) {
/* 119 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 120 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 121 */       int iNewSize = iSize.intValue() + 1;
/* 122 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 124 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/* 126 */     log("########################### - bumpUp ht : " + this.htReportIssue);
/* 127 */     return this.htReportIssue;
/*     */   }
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String sMethodName, Tree eachLineTree, String strPopContext) {
/* 131 */     if (this.htReportIssue.size() > 0) {
/* 132 */       log("########################### - report ? ht : " + this.htReportIssue);
/*     */     }
/* 134 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 135 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 136 */       if (iSize.intValue() >= 1) {
/* 137 */         if ("popContext".equals(strPopContext)) {
/* 138 */           if (!this.finallyBlockVar)
/*     */           {
/*     */             
/* 141 */             reportIssue(eachLineTree, "SOGETI --> " + sMethodName + " pop Context is not allowed out side finally block ");
/*     */           }
/*     */         }
/* 144 */         else if (this.pushContextTry) {
/* 145 */           reportIssue(eachLineTree, "SOGETI Rule: Push Context is not allowed");
/*     */         } else {
/* 147 */           reportIssue(eachLineTree, "SOGETI --> " + sMethodName + " Push Context is not allowed out side try block ");
/*     */         } 
/*     */         
/* 150 */         return true;
/*     */       } 
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
/*     */       
/* 169 */       String sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 170 */       log("line - " + sRet);
/* 171 */     } else if ("ASSIGNMENT".equals(expressionTree.kind().toString())) {
/*     */       
/* 173 */       AssignmentExpressionTree aet = (AssignmentExpressionTree)expressionTree;
/* 174 */       invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */     } else {
/*     */       
/* 177 */       log("unexpected kind in switch 222 - " + expressionTree.kind().toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 182 */     TypeCastTree typeCastTree = (TypeCastTree)et;
/* 183 */     ExpressionTree expressionTree = typeCastTree.expression();
/* 184 */     String sRet = null;
/* 185 */     log("inside invokeTypeCastTreeMethod" + expressionTree.kind().toString());
/*     */     
/* 187 */     if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 188 */       sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 189 */       log("line - " + sRet);
/* 190 */     } else if ("MEMBER_SELECT".equals(expressionTree.kind().toString())) {
/* 191 */       sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 192 */       log("line - " + sRet);
/*     */     } else {
/* 194 */       log("unexpected kind in invokeTypeCastTreeMethod - " + expressionTree.kind().toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree et, Tree eachLineTree) {
/* 199 */     log("et kind of aest - " + et.kind().toString());
/* 200 */     if ("TYPE_CAST".equals(et.kind().toString())) {
/* 201 */       invokeTypeCastTreeMethod(et, eachLineTree);
/* 202 */     } else if ("METHOD_INVOCATION".equals(et.kind().toString())) {
/* 203 */       String sRet = invokeMethodInvocationTreeMethod(et, eachLineTree);
/* 204 */       log("line - " + sRet);
/*     */     } else {
/* 206 */       log("unexpected kind in switch invokeAssignmentExpressionStatementTreeMethod - " + et.kind().toString());
/*     */     } 
/*     */   } private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 211 */     String sRet = null;
/* 212 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 213 */     log("*** variable kind *** - " + variableTree.type().kind().toString());
/*     */     
/* 215 */     VariableTree myVtt = (VariableTree)variableTree.symbol().declaration();
/* 216 */     String myVariableName = variableTree.symbol().name();
/* 217 */     String myVariableType = variableTree.symbol().type().name();
/* 218 */     VariableTree variableTree1 = myVtt;
/*     */     try {
/* 220 */       expressionTree = myVtt.initializer();
/* 221 */       log("MYETT  - " + expressionTree.kind().toString());
/* 222 */     } catch (Exception ex) {
/* 223 */       log(" --- inside exception --" + ex);
/* 224 */       if (expressionTree == null) {
/* 225 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/* 228 */     if ("TYPE_CAST".equals(tree.kind().toString())) {
/* 229 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/*     */     }
/* 231 */     else if ("MEMBER_SELECT".equals(tree.kind().toString())) {
/* 232 */       sRet = invokeMemberSelectMethod(tree, eachLineTree);
/* 233 */       log("line - " + myVariableType + " " + myVariableName + " = " + sRet + "--args--");
/*     */     }
/* 235 */     else if ("METHOD_INVOCATION".equals(tree.kind().toString())) {
/* 236 */       sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/* 237 */       log("line - " + myVariableType + " " + myVariableName + " = " + sRet);
/*     */     } else {
/* 239 */       log("unexpected kind in switch - " + tree.kind().toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 244 */     MethodInvocationTree myMit = (MethodInvocationTree)mytree;
/* 245 */     ExpressionTree expressionTree = myMit.methodSelect();
/* 246 */     log("dinakar  - " + expressionTree.kind().toString());
/* 247 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 248 */     String sMyArgs = "";
/* 249 */     if (myMit.arguments().size() != 0) {
/* 250 */       log("****** in mit  *********" + myMit.arguments().size());
/* 251 */       Arguments<Tree> arguments = myMit.arguments();
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
/* 272 */     String strVar = null;
/* 273 */     if ("MEMBER_SELECT".equals(ettemp.kind().toString())) {
/*     */       
/* 275 */       MemberSelectExpressionTree mset = (MemberSelectExpressionTree)ettemp;
/* 276 */       String myDeclarationMethodName = mset.firstToken().text();
/* 277 */       String myDeclarationCallingMethodName = mset.identifier().name();
/* 278 */       strVar = myDeclarationMethodName + "." + myDeclarationCallingMethodName;
/*     */       
/* 280 */       if ("pushContext".equals(mset.identifier().name()) || "popContext".equals(mset.identifier().name())) {
/* 281 */         bumpUpCount(myDeclarationMethodName);
/* 282 */         bCheckAndReportIssueNow(myDeclarationMethodName, eachLineTree, mset.identifier().name());
/*     */       } 
/*     */     } else {
/* 285 */       log("unexpected kind in iinvokeMemberSelectMethod - " + ettemp.kind().toString());
/*     */     } 
/* 287 */     return strVar;
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 291 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 292 */     log("*** inside invokeForStmtTreeMethod kind *** - " + forStmtTree.kind().toString());
/* 293 */     StatementTree statementTree = forStmtTree.statement();
/* 294 */     log("*** et kind *** - " + statementTree.kind().toString());
/* 295 */     if ("BLOCK".equals(statementTree)) {
/* 296 */       BlockTree bt = (BlockTree)forStmtTree.statement();
/* 297 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 298 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 299 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 304 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */     
/* 306 */     ExpressionTree expressionTree = ifStmtTree.condition();
/* 307 */     log("*** if stmt condition kind *** - " + expressionTree.kind().toString());
/*     */     
/* 309 */     if ("LOGICAL_COMPLEMENT".equals(expressionTree.kind().toString())) {
/* 310 */       UnaryExpressionTree uet = (UnaryExpressionTree)expressionTree;
/* 311 */       ExpressionTree newet = uet.expression();
/* 312 */       log("*** logical complement kind *** - " + newet.kind().toString());
/*     */       
/* 314 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 315 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/*     */       }
/* 317 */     } else if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 318 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, ifLoopTree);
/*     */     } 
/*     */     
/* 321 */     StatementTree st = ifStmtTree.thenStatement();
/* 322 */     log("*** if stmt 2 kind *** - " + st.kind().toString());
/*     */     
/* 324 */     invokeIfElseStatementTreeMethod(st);
/*     */     
/*     */     try {
/* 327 */       while (ifStmtTree.elseStatement() != null) {
/* 328 */         st = ifStmtTree.elseStatement();
/* 329 */         log("*** if stmt 222 kind *** - " + st.kind().toString());
/*     */         
/* 331 */         if ("IF_STATEMENT".equals(st.kind().toString())) {
/*     */           
/* 333 */           ifStmtTree = (IfStatementTree)st;
/* 334 */           log("*** if stmt two kind *** - " + ifStmtTree.kind().toString());
/* 335 */           StatementTree newst = ifStmtTree.thenStatement();
/* 336 */           invokeIfElseStatementTreeMethod(newst); continue;
/*     */         } 
/* 338 */         log("*** inside else of if fif if ****");
/*     */         
/* 340 */         invokeIfElseStatementTreeMethod(st);
/* 341 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 344 */     } catch (Exception ex) {
/* 345 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree st) {
/* 350 */     if ("BLOCK".equals(st.kind().toString())) {
/* 351 */       BlockTree bt = (BlockTree)st;
/* 352 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 353 */     } else if ("EXPRESSION_STATEMENT".equals(st.kind().toString())) {
/* 354 */       invokeExpressionStatementTreeMethod((Tree)st);
/*     */     } else {
/* 356 */       log("unexpected kind in is stmt tree - " + st.kind().toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 365 */     Tree eachLineTree = null;
/* 366 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 367 */       eachLineTree = listOfTrees.get(iLine);
/* 368 */       log("*** kind inside for loop *** ------------------------------------------- " + eachLineTree.kind().toString());
/* 369 */       if ("VARIABLE".equals(eachLineTree.kind().toString())) {
/* 370 */         invokeVariableTreeMethod(eachLineTree);
/* 371 */       } else if ("EXPRESSION_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 372 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 373 */       } else if ("TRY_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 374 */         TryStatementTree tryst = (TryStatementTree)eachLineTree;
/* 375 */         BlockTree btTryStmtTree = tryst.block();
/* 376 */         this.pushContextTry = true;
/*     */         
/* 378 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/* 379 */         this.pushContextTry = false;
/*     */         
/* 381 */         List<? extends CatchTree> catches = tryst.catches();
/* 382 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 383 */           CatchTree ct = catches.get(iCatchCnt);
/* 384 */           BlockTree btCatch = ct.block();
/* 385 */           checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */         } 
/*     */         try {
/* 388 */           BlockTree btTryStmtFinallyTree = tryst.finallyBlock();
/* 389 */           log("btTryStmtFinallyTree  - " + btTryStmtFinallyTree.kind().toString());
/*     */           
/* 391 */           this.finallyBlockVar = true;
/* 392 */           checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/* 393 */           this.finallyBlockVar = false;
/* 394 */         } catch (Exception ex) {
/* 395 */           log(" --- no finally block available exception -- " + ex);
/*     */         } 
/* 397 */       } else if ("FOR_STATEMENT".equals(eachLineTree.kind().toString())) {
/*     */         
/* 399 */         invokeForStmtTreeMethod(eachLineTree);
/*     */       }
/* 401 */       else if ("IF_STATEMENT".equals(eachLineTree.kind().toString())) {
/*     */         
/* 403 */         invokeIfStmtTreeMethod(eachLineTree);
/*     */       }
/* 405 */       else if ("WHILE_STATEMENT".equals(eachLineTree.kind().toString())) {
/*     */         
/* 407 */         WhileStatementTree wst = (WhileStatementTree)eachLineTree;
/* 408 */         StatementTree statementTree = wst.statement();
/* 409 */         invokeIfElseStatementTreeMethod(statementTree);
/*     */       } else {
/*     */         
/* 412 */         log("unexpected kind in switch 444  - " + eachLineTree.kind().toString());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PushContextNotInLoopMustBeInTryRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */