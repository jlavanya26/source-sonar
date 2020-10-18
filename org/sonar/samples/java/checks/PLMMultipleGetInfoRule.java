/*     */ package org.sonar.samples.java.checks;
/*     */ 
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import com.google.common.collect.Lists;
/*     */ import java.util.Deque;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import org.sonar.check.Rule;
/*     */ import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
/*     */ import org.sonar.plugins.java.api.semantic.Symbol;
/*     */ import org.sonar.plugins.java.api.tree.Arguments;
/*     */ import org.sonar.plugins.java.api.tree.AssignmentExpressionTree;
/*     */ import org.sonar.plugins.java.api.tree.BlockTree;
/*     */ import org.sonar.plugins.java.api.tree.CatchTree;
/*     */ import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.ExpressionTree;
/*     */ import org.sonar.plugins.java.api.tree.ForEachStatement;
/*     */ import org.sonar.plugins.java.api.tree.ForStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.IdentifierTree;
/*     */ import org.sonar.plugins.java.api.tree.IfStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.LiteralTree;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Rule(key = "PLMMultipleGetInfo")
/*     */ public class PLMMultipleGetInfoRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  94 */   public int max = 2;
/*     */ 
/*     */ 
/*     */   
/*     */   boolean bLoggingActive = false;
/*     */ 
/*     */   
/* 101 */   private final Deque<ImmutableMap<String, VariableTree>> fields = Lists.newLinkedList();
/* 102 */   private final Deque<List<VariableTree>> excludedVariables = Lists.newLinkedList();
/* 103 */   private final List<VariableTree> flattenExcludedVariables = Lists.newArrayList();
/* 104 */   private Hashtable<String, Integer> htReportIssue = null;
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
/* 119 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/* 128 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/* 130 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/* 131 */     BlockTree blocktree = methodTree.block();
/* 132 */     this.htReportIssue = new Hashtable<>();
/* 133 */     if (blocktree != null) {
/* 134 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/* 140 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty())
/*     */     {
/* 142 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(String str) {
/* 147 */     if (this.bLoggingActive) {
/* 148 */       System.out.println(str);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(Exception ex) {
/* 153 */     if (this.bLoggingActive) {
/* 154 */       System.out.println(ex);
/*     */     }
/*     */   }
/*     */   
/*     */   private Hashtable bumpUpCount(String sMethodName) {
/* 159 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 160 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 161 */       int iNewSize = iSize.intValue() + 1;
/* 162 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 164 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/* 166 */     return this.htReportIssue;
/*     */   }
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String sMethodName, Tree eachLineTree) {
/* 170 */     if (this.htReportIssue.size() > 0);
/*     */     
/* 172 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 173 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 174 */       if (iSize.intValue() > 1) {
/* 175 */         reportIssue(eachLineTree, "SOGETI --> " + sMethodName + ".getInfo() method is used more than once, use .getInfoList()");
/* 176 */         return true;
/*     */       } 
/* 178 */       return false;
/*     */     } 
/*     */     
/* 181 */     return false;
/*     */   } private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/*     */     LiteralTree myLtt;
/*     */     IdentifierTree myItt;
/*     */     String sRet;
/*     */     AssignmentExpressionTree aet;
/* 187 */     ExpressionStatementTree est = (ExpressionStatementTree)eachLineTree;
/* 188 */     ExpressionTree expressionTree = est.expression();
/*     */     
/* 190 */     switch (expressionTree.kind()) {
/*     */       case STRING_LITERAL:
/* 192 */         myLtt = (LiteralTree)expressionTree;
/*     */         return;
/*     */       case IDENTIFIER:
/* 195 */         myItt = (IdentifierTree)expressionTree;
/*     */         return;
/*     */       case METHOD_INVOCATION:
/* 198 */         sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 199 */         log("line - " + sRet);
/*     */         return;
/*     */       case ASSIGNMENT:
/* 202 */         aet = (AssignmentExpressionTree)expressionTree;
/* 203 */         invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */         return;
/*     */     } 
/* 206 */     log("unexpected kind in switch 222 - " + expressionTree.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 212 */     TypeCastTree tct = (TypeCastTree)et;
/* 213 */     ExpressionTree ext = tct.expression();
/* 214 */     String sRet = "";
/*     */     
/* 216 */     switch (ext.kind()) {
/*     */       case METHOD_INVOCATION:
/* 218 */         sRet = invokeMethodInvocationTreeMethod((Tree)ext, eachLineTree);
/* 219 */         log("line - " + sRet);
/*     */       
/*     */       case MEMBER_SELECT:
/* 222 */         sRet = invokeMemberSelectMethod((Tree)ext, eachLineTree);
/*     */       
/*     */       case IDENTIFIER:
/*     */         return;
/*     */     } 
/* 227 */     log("unexpected kind in invokeTypeCastTreeMethod - " + ext.kind().toString());
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree et, Tree eachLineTree) {
/*     */     String sRet;
/* 233 */     log("et kind of aest - " + et.kind().toString());
/* 234 */     switch (et.kind()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case TYPE_CAST:
/* 244 */         invokeTypeCastTreeMethod(et, eachLineTree);
/*     */         break;
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
/*     */       case METHOD_INVOCATION:
/* 269 */         sRet = invokeMethodInvocationTreeMethod(et, eachLineTree);
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 277 */     String sRet = "";
/* 278 */     VariableTree variableTree = (VariableTree)eachLineTree;
/*     */     
/* 280 */     VariableTree myVtt = (VariableTree)variableTree.symbol().declaration();
/* 281 */     String myVariableName = variableTree.symbol().name();
/* 282 */     String myVariableType = variableTree.symbol().type().name();
/* 283 */     VariableTree variableTree1 = myVtt;
/*     */     try {
/* 285 */       expressionTree = myVtt.initializer();
/* 286 */       log("MYETT  - " + expressionTree.kind().toString());
/* 287 */     } catch (Exception ex) {
/* 288 */       if (expressionTree == null) {
/* 289 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/*     */     
/* 293 */     switch (tree.kind()) {
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
/*     */       case TYPE_CAST:
/* 315 */         invokeTypeCastTreeMethod(tree, eachLineTree);
/*     */         break;
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
/*     */       case METHOD_INVOCATION:
/* 329 */         sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 337 */     MethodInvocationTree myMit = (MethodInvocationTree)mytree;
/* 338 */     ExpressionTree expressionTree = myMit.methodSelect();
/*     */     
/* 340 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/*     */     
/* 342 */     String sMyArgs = "";
/* 343 */     if (myMit.arguments().size() != 0) {
/*     */       
/* 345 */       Arguments<Tree> arguments = myMit.arguments();
/*     */       
/* 347 */       sMyArgs = "(";
/* 348 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/*     */ 
/*     */         
/* 351 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 352 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 353 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 354 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 355 */           sRet = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 358 */       sMyArgs = sMyArgs + "--)";
/* 359 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 361 */       sMyArgs = "()";
/*     */     } 
/*     */     
/* 364 */     return sRet + sMyArgs;
/*     */   }
/*     */   private String invokeMemberSelectMethod(Tree ettemp, Tree eachLineTree) {
/*     */     MemberSelectExpressionTree mset;
/* 368 */     String myDeclarationMethodName, myDeclarationCallingMethodName, str = "";
/*     */     
/* 370 */     switch (ettemp.kind()) {
/*     */       case MEMBER_SELECT:
/* 372 */         mset = (MemberSelectExpressionTree)ettemp;
/* 373 */         myDeclarationMethodName = mset.firstToken().text();
/* 374 */         myDeclarationCallingMethodName = mset.identifier().name();
/* 375 */         str = myDeclarationMethodName + "." + myDeclarationCallingMethodName;
/* 376 */         if (mset.identifier().name().equals("getInfo")) {
/*     */           
/* 378 */           bumpUpCount(myDeclarationMethodName);
/* 379 */           bCheckAndReportIssueNow(myDeclarationMethodName, eachLineTree);
/*     */         } 
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 389 */     return str;
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 393 */     Tree eachLineTree = null;
/* 394 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 395 */     StatementTree et = forStmtTree.statement();
/* 396 */     if ("BLOCK".equals(et.kind().toString())) {
/* 397 */       BlockTree bt = (BlockTree)forStmtTree.statement();
/* 398 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 399 */     } else if ("EXPRESSION_STATEMENT".equals(et.kind().toString())) {
/* 400 */       invokeExpressionStatementTreeMethod((Tree)et);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 406 */     Tree eachLineTree = null;
/* 407 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/*     */     
/* 409 */     BlockTree bt = (BlockTree)forEachStmt.statement();
/* 410 */     checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 419 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */ 
/*     */ 
/*     */     
/* 423 */     ExpressionTree et = ifStmtTree.condition();
/*     */     
/* 425 */     if ("LOGICAL_COMPLEMENT".equals(et.kind().toString())) {
/* 426 */       UnaryExpressionTree uet = (UnaryExpressionTree)et;
/* 427 */       ExpressionTree newet = uet.expression();
/*     */       
/* 429 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 430 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 431 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */ 
/*     */       
/*     */       }
/*     */     
/* 436 */     } else if ("METHOD_INVOCATION".equals(et.kind().toString())) {
/*     */ 
/*     */       
/* 439 */       invokeMethodInvocationTreeMethod((Tree)et, ifLoopTree);
/*     */     } 
/*     */ 
/*     */     
/* 443 */     StatementTree st = ifStmtTree.thenStatement();
/*     */     
/* 445 */     invokeIfElseStatementTreeMethod(st);
/*     */ 
/*     */     
/*     */     try {
/* 449 */       while (ifStmtTree.elseStatement() != null) {
/* 450 */         st = ifStmtTree.elseStatement();
/*     */         
/* 452 */         if ("IF_STATEMENT".equals(st.kind().toString())) {
/*     */           
/* 454 */           ifStmtTree = (IfStatementTree)st;
/* 455 */           StatementTree newst = ifStmtTree.thenStatement();
/* 456 */           invokeIfElseStatementTreeMethod(newst);
/*     */           continue;
/*     */         } 
/* 459 */         invokeIfElseStatementTreeMethod(st);
/* 460 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 463 */     } catch (Exception ex) {
/* 464 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree st) {
/*     */     BlockTree bt;
/* 469 */     switch (st.kind()) {
/*     */       case BLOCK:
/* 471 */         bt = (BlockTree)st;
/* 472 */         checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */ 
/*     */       
/*     */       case EXPRESSION_STATEMENT:
/* 476 */         invokeExpressionStatementTreeMethod((Tree)st);
/*     */ 
/*     */       
/*     */       case BREAK_STATEMENT:
/*     */       case RETURN_STATEMENT:
/*     */         return;
/*     */     } 
/*     */ 
/*     */     
/* 485 */     log("unexpected kind in is stmt tree - " + st.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 491 */     String eachLineText = "";
/* 492 */     Tree eachLineTree = null;
/* 493 */     int iCount = 0;
/* 494 */     int iMethodCount = 0;
/* 495 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 496 */       TryStatementTree tst; BlockTree btTryStmtTree; List<? extends CatchTree> catches; int iCatchCnt; WhileStatementTree wst; StatementTree st; eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 498 */       switch (eachLineTree.kind()) {
/*     */         case VARIABLE:
/* 500 */           invokeVariableTreeMethod(eachLineTree);
/*     */           break;
/*     */         case EXPRESSION_STATEMENT:
/* 503 */           invokeExpressionStatementTreeMethod(eachLineTree);
/*     */           break;
/*     */         case METHOD_INVOCATION:
/*     */           break;
/*     */         case TRY_STATEMENT:
/* 508 */           tst = (TryStatementTree)eachLineTree;
/* 509 */           btTryStmtTree = tst.block();
/*     */ 
/*     */           
/* 512 */           checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */           
/* 514 */           catches = tst.catches();
/*     */           
/* 516 */           for (iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 517 */             CatchTree ct = catches.get(iCatchCnt);
/* 518 */             BlockTree btCatch = ct.block();
/* 519 */             checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */           } 
/*     */           
/*     */           try {
/* 523 */             BlockTree btTryStmtFinallyTree = tst.finallyBlock();
/* 524 */             log("btTryStmtFinallyTree  - " + btTryStmtFinallyTree.kind().toString());
/*     */ 
/*     */             
/* 527 */             checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */           }
/* 529 */           catch (Exception ex) {
/* 530 */             log(" --- no finally block available exception -- " + ex);
/*     */           } 
/*     */           break;
/*     */         case FOR_STATEMENT:
/* 534 */           invokeForStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case FOR_EACH_STATEMENT:
/* 537 */           invokeForEachStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case IF_STATEMENT:
/* 540 */           invokeIfStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */ 
/*     */ 
/*     */         
/*     */         case STRING_LITERAL:
/*     */         case RETURN_STATEMENT:
/*     */         case BREAK_STATEMENT:
/*     */         case CONTINUE_STATEMENT:
/*     */           break;
/*     */ 
/*     */         
/*     */         case WHILE_STATEMENT:
/* 553 */           wst = (WhileStatementTree)eachLineTree;
/* 554 */           st = wst.statement();
/* 555 */           invokeIfElseStatementTreeMethod(st);
/*     */           break;
/*     */         
/*     */         case THROW_STATEMENT:
/*     */         case EMPTY_STATEMENT:
/*     */           break;
/*     */         
/*     */         default:
/* 563 */           log("unexpected kind in switch 444  - " + eachLineTree.kind().toString());
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMMultipleGetInfoRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */