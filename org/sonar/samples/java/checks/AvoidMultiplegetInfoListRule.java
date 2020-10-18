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
/*     */ @Rule(key = "AvoidMultiplegetInfoList")
/*     */ public class AvoidMultiplegetInfoListRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  47 */   public int max = 2;
/*     */   
/*     */   boolean bLoggingActive = false;
/*     */   
/*  51 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  57 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  68 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  70 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  71 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  73 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  74 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  76 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  82 */     BlockTree blocktree = methodTree.block();
/*     */     
/*  84 */     Tree.Kind tk = methodTree.kind();
/*  85 */     log("MILRs - " + tk.toString() + " --> " + methodTree.simpleName().name());
/*     */     
/*  87 */     this.htReportIssue = new Hashtable<>();
/*  88 */     if (blocktree != null)
/*     */     {
/*  90 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  96 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  97 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(String str) {
/* 102 */     if (this.bLoggingActive) {
/* 103 */       System.out.println(str);
/*     */     }
/*     */   }
/*     */   
/*     */   private Hashtable bumpUpCount(String sMethodName) {
/* 108 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 109 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 110 */       int iNewSize = iSize.intValue() + 1;
/* 111 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 113 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/*     */     
/* 116 */     return this.htReportIssue;
/*     */   }
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String sMethodName, Tree eachLineTree) {
/* 120 */     if (this.htReportIssue.size() > 0);
/*     */ 
/*     */     
/* 123 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 124 */       Integer iSize = this.htReportIssue.get(sMethodName);
/*     */       
/* 126 */       if (iSize.intValue() > 1) {
/* 127 */         reportIssue(eachLineTree, "Avoid multiple getInfoList inside the method or loop");
/* 128 */         return true;
/*     */       } 
/* 130 */       return false;
/*     */     } 
/*     */     
/* 133 */     return false;
/*     */   } private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/*     */     LiteralTree myLtt;
/*     */     IdentifierTree myItt;
/*     */     String sRet;
/*     */     AssignmentExpressionTree aet;
/* 139 */     ExpressionStatementTree est = (ExpressionStatementTree)eachLineTree;
/* 140 */     ExpressionTree expressionTree = est.expression();
/*     */     
/* 142 */     switch (expressionTree.kind()) {
/*     */       
/*     */       case STRING_LITERAL:
/* 145 */         myLtt = (LiteralTree)expressionTree;
/* 146 */         log("value is GIL- MILRs " + myLtt.value());
/*     */         break;
/*     */       
/*     */       case IDENTIFIER:
/* 150 */         myItt = (IdentifierTree)expressionTree;
/* 151 */         log("name is GIL- MILRs" + myItt.name());
/*     */         break;
/*     */       case METHOD_INVOCATION:
/* 154 */         sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 155 */         log("line - MILRs" + sRet);
/*     */         break;
/*     */       
/*     */       case ASSIGNMENT:
/* 159 */         aet = (AssignmentExpressionTree)expressionTree;
/* 160 */         invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 170 */     TypeCastTree tct = (TypeCastTree)et;
/* 171 */     ExpressionTree ext = tct.expression();
/* 172 */     String sRet = "";
/*     */     
/* 174 */     switch (ext.kind()) {
/*     */       case METHOD_INVOCATION:
/* 176 */         sRet = invokeMethodInvocationTreeMethod((Tree)ext, eachLineTree);
/* 177 */         log("line - MILRs" + sRet);
/*     */       
/*     */       case MEMBER_SELECT:
/* 180 */         sRet = invokeMemberSelectMethod((Tree)ext, eachLineTree);
/* 181 */         log("line -MILRs " + sRet);
/*     */       
/*     */       case IDENTIFIER:
/*     */         return;
/*     */     } 
/* 186 */     log("unexpected kind in invokeTypeCastTreeMethod - " + ext.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree et, Tree eachLineTree) {
/*     */     String sRet;
/* 193 */     switch (et.kind()) {
/*     */       case STRING_LITERAL:
/*     */       case BOOLEAN_LITERAL:
/*     */       case NULL_LITERAL:
/*     */       case IDENTIFIER:
/*     */         return;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case TYPE_CAST:
/* 204 */         invokeTypeCastTreeMethod(et, eachLineTree);
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
/*     */       case NEW_CLASS:
/*     */       case MEMBER_SELECT:
/*     */       case PLUS:
/*     */       case CONDITIONAL_AND:
/*     */       case PARENTHESIZED_EXPRESSION:
/*     */       case MULTIPLY:
/*     */       case CONDITIONAL_EXPRESSION:
/*     */       case ARRAY_ACCESS_EXPRESSION:
/*     */       case NEW_ARRAY:
/*     */       case MINUS:
/*     */       case PREFIX_INCREMENT:
/*     */         return;
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
/* 240 */         sRet = invokeMethodInvocationTreeMethod(et, eachLineTree);
/* 241 */         log("line -MILRs " + sRet);
/*     */     } 
/*     */     
/* 244 */     log("unexpected kind in switch  - MILRs" + et.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 252 */     String sRet = "";
/* 253 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 254 */     log("variable kind *** - MILRs  " + variableTree.type().kind().toString());
/*     */     
/* 256 */     VariableTree myVtt = (VariableTree)variableTree.symbol().declaration();
/* 257 */     String myVariableName = variableTree.symbol().name();
/* 258 */     String myVariableType = variableTree.symbol().type().name();
/*     */     
/* 260 */     VariableTree variableTree1 = myVtt;
/*     */     try {
/* 262 */       expressionTree = myVtt.initializer();
/* 263 */       log("MYETT  - MILRs" + expressionTree.kind().toString());
/* 264 */     } catch (Exception ex) {
/* 265 */       log(" --- inside exception --MILRs" + ex);
/* 266 */       if (expressionTree == null) {
/* 267 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/*     */     
/* 271 */     switch (tree.kind()) {
/*     */       case STRING_LITERAL:
/*     */       case INT_LITERAL:
/*     */       case DOUBLE_LITERAL:
/*     */       case NULL_LITERAL:
/*     */       case BOOLEAN_LITERAL:
/*     */       case NEW_CLASS:
/*     */       case PLUS:
/*     */       case MINUS:
/*     */       case DIVIDE:
/*     */       case REMAINDER:
/*     */         return;
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
/* 303 */         invokeTypeCastTreeMethod(tree, eachLineTree);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case NEW_ARRAY:
/*     */       case VARIABLE:
/*     */       case ARRAY_ACCESS_EXPRESSION:
/*     */       case IDENTIFIER:
/*     */         return;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case MEMBER_SELECT:
/* 318 */         sRet = invokeMemberSelectMethod(tree, eachLineTree);
/* 319 */         log("line - MILRs" + myVariableType + " " + myVariableName + " = " + sRet + "--args--");
/*     */ 
/*     */       
/*     */       case METHOD_INVOCATION:
/* 323 */         sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/* 324 */         log("line - MILRs" + myVariableType + " " + myVariableName + " = " + sRet);
/*     */     } 
/*     */     
/* 327 */     log("unexpected kind in switch - MILRs" + tree.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 334 */     MethodInvocationTree myMit = (MethodInvocationTree)mytree;
/*     */     
/* 336 */     ExpressionTree expressionTree = myMit.methodSelect();
/* 337 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 338 */     String sMyArgs = "";
/*     */     
/* 340 */     if (myMit.arguments().size() != 0) {
/* 341 */       Arguments<Tree> arguments = myMit.arguments();
/*     */       
/* 343 */       sMyArgs = "(";
/* 344 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/*     */ 
/*     */         
/* 347 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 348 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 349 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 350 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 351 */           sRet = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 354 */       sMyArgs = sMyArgs + "--)";
/* 355 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 357 */       sMyArgs = "()";
/*     */     } 
/*     */     
/* 360 */     return sRet + sMyArgs; } private String invokeMemberSelectMethod(Tree ettemp, Tree eachLineTree) {
/*     */     MemberSelectExpressionTree mset;
/*     */     String myDeclarationMethodName, myDeclarationCallingMethodName;
/*     */     IdentifierTree myItt;
/* 364 */     String str = "";
/* 365 */     switch (ettemp.kind())
/*     */     { case MEMBER_SELECT:
/* 367 */         mset = (MemberSelectExpressionTree)ettemp;
/* 368 */         myDeclarationMethodName = mset.firstToken().text();
/* 369 */         myDeclarationCallingMethodName = mset.identifier().name();
/* 370 */         str = myDeclarationMethodName + "." + myDeclarationCallingMethodName;
/*     */         
/* 372 */         if (mset.identifier().name().equals("getInfoList")) {
/* 373 */           bumpUpCount(myDeclarationMethodName);
/* 374 */           bCheckAndReportIssueNow(myDeclarationMethodName, eachLineTree);
/*     */         } 
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
/* 390 */         return str;case IDENTIFIER: myItt = (IdentifierTree)ettemp; if (myItt.name().equals("getInfoList")) { bumpUpCount(myItt.name()); reportIssue(eachLineTree, "Avoid multiple getInfoList inside the method or loop"); }  return str; }  log("unexpected kind in iinvokeMemberSelectMethod -MILRs " + ettemp.kind().toString()); return str;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 395 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 396 */     StatementTree et = forStmtTree.statement();
/*     */     
/* 398 */     if ("BLOCK".equals(et.kind().toString())) {
/* 399 */       BlockTree bt = (BlockTree)forStmtTree.statement();
/* 400 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 401 */     } else if ("EXPRESSION_STATEMENT".equals(et.kind().toString())) {
/* 402 */       invokeExpressionStatementTreeMethod((Tree)et);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 408 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 409 */     BlockTree bt = (BlockTree)forEachStmt.statement();
/* 410 */     checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 416 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */ 
/*     */     
/* 419 */     ExpressionTree et = ifStmtTree.condition();
/* 420 */     log("MILRs*** if stmt condition kind *** - " + et.kind().toString());
/*     */     
/* 422 */     if ("LOGICAL_COMPLEMENT".equals(et.kind().toString())) {
/* 423 */       UnaryExpressionTree uet = (UnaryExpressionTree)et;
/* 424 */       ExpressionTree newet = uet.expression();
/* 425 */       log("MILRs*** logical complement kind *** - " + newet.kind().toString());
/*     */       
/* 427 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 428 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 429 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */ 
/*     */       
/*     */       }
/*     */     
/* 434 */     } else if ("METHOD_INVOCATION".equals(et.kind().toString())) {
/* 435 */       invokeMethodInvocationTreeMethod((Tree)et, ifLoopTree);
/*     */     } 
/*     */ 
/*     */     
/* 439 */     StatementTree st = ifStmtTree.thenStatement();
/* 440 */     log("*** if stmt MILRs*** - " + st.kind().toString());
/*     */     
/* 442 */     invokeIfElseStatementTreeMethod(st);
/*     */ 
/*     */     
/*     */     try {
/* 446 */       while (ifStmtTree.elseStatement() != null) {
/* 447 */         st = ifStmtTree.elseStatement();
/*     */         
/* 449 */         if ("IF_STATEMENT".equals(st.kind().toString())) {
/*     */           
/* 451 */           ifStmtTree = (IfStatementTree)st;
/* 452 */           log("*** if stmt two MILRs*** - " + ifStmtTree.kind().toString());
/* 453 */           StatementTree newst = ifStmtTree.thenStatement();
/*     */           
/* 455 */           invokeIfElseStatementTreeMethod(newst);
/*     */           
/*     */           continue;
/*     */         } 
/* 459 */         invokeIfElseStatementTreeMethod(st);
/* 460 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 463 */     } catch (Exception ex) {
/* 464 */       log(" --- no else block OR exiting from else --MILRs" + ex);
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
/* 485 */     log("unexpected kind in is stmt tree -MILRs " + st.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 492 */     Tree eachLineTree = null;
/*     */     
/* 494 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/*     */       TryStatementTree tst; BlockTree btTryStmtTree; List<? extends CatchTree> catches; int iCatchCnt; WhileStatementTree wst; StatementTree st;
/* 496 */       eachLineTree = listOfTrees.get(iLine);
/* 497 */       log("*** kind inside for loop ***MILRs " + eachLineTree.kind().toString());
/*     */       
/* 499 */       switch (eachLineTree.kind()) {
/*     */         case VARIABLE:
/* 501 */           invokeVariableTreeMethod(eachLineTree);
/*     */           break;
/*     */         case EXPRESSION_STATEMENT:
/* 504 */           invokeExpressionStatementTreeMethod(eachLineTree);
/*     */           break;
/*     */         
/*     */         case METHOD_INVOCATION:
/*     */           break;
/*     */         case TRY_STATEMENT:
/* 510 */           tst = (TryStatementTree)eachLineTree;
/* 511 */           btTryStmtTree = tst.block();
/*     */ 
/*     */           
/* 514 */           checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */           
/* 516 */           catches = tst.catches();
/*     */           
/* 518 */           for (iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 519 */             CatchTree ct = catches.get(iCatchCnt);
/* 520 */             BlockTree btCatch = ct.block();
/* 521 */             checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */           } 
/*     */           
/*     */           try {
/* 525 */             BlockTree btTryStmtFinallyTree = tst.finallyBlock();
/* 526 */             log("btTryStmtFinallyTree  -MILRs " + btTryStmtFinallyTree.kind().toString());
/*     */ 
/*     */             
/* 529 */             checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree
/* 530 */                 .body());
/*     */           }
/* 532 */           catch (Exception ex) {
/* 533 */             log(" --- no finally block available exception -- MILRs" + ex);
/*     */           } 
/*     */           break;
/*     */         case FOR_STATEMENT:
/* 537 */           invokeForStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case FOR_EACH_STATEMENT:
/* 540 */           invokeForEachStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case IF_STATEMENT:
/* 543 */           invokeIfStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */ 
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
/*     */         
/*     */         case WHILE_STATEMENT:
/* 558 */           wst = (WhileStatementTree)eachLineTree;
/* 559 */           st = wst.statement();
/* 560 */           invokeIfElseStatementTreeMethod(st);
/*     */           break;
/*     */         
/*     */         case THROW_STATEMENT:
/*     */         case EMPTY_STATEMENT:
/*     */           break;
/*     */         
/*     */         default:
/* 568 */           log("unexpected kind in MILRs" + eachLineTree.kind().toString());
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidMultiplegetInfoListRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */