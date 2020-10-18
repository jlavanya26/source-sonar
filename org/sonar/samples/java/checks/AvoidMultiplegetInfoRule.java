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
/*     */ 
/*     */ @Rule(key = "AvoidMultiplegetInfo")
/*     */ public class AvoidMultiplegetInfoRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  48 */   public int max = 2;
/*     */   boolean bLoggingActive = false;
/*  50 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  56 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
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
/*  67 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  69 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  70 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  72 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  73 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  75 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  82 */     BlockTree blocktree = methodTree.block();
/*     */     
/*  84 */     Tree.Kind tk = methodTree.kind();
/*  85 */     log("&&&& - " + tk.toString() + " --> " + methodTree.simpleName().name());
/*  86 */     this.htReportIssue = new Hashtable<>();
/*     */ 
/*     */     
/*  89 */     if (blocktree != null)
/*     */     {
/*  91 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  98 */     log("5");
/*  99 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/* 100 */       log("6");
/* 101 */       addressEachTree(trees);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void log(String str) {
/* 106 */     if (this.bLoggingActive) {
/* 107 */       System.out.println(str);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private Hashtable bumpUpCount(String sMethodName) {
/* 113 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 114 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 115 */       int iNewSize = iSize.intValue() + 1;
/* 116 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 118 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/*     */     
/* 121 */     return this.htReportIssue;
/*     */   }
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String sMethodName, Tree eachLineTree) {
/* 125 */     if (this.htReportIssue.size() > 0);
/*     */ 
/*     */ 
/*     */     
/* 129 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 130 */       Integer iSize = this.htReportIssue.get(sMethodName);
/*     */       
/* 132 */       if (iSize.intValue() > 1) {
/* 133 */         reportIssue(eachLineTree, "Avoid multiple getInfo inside the method or loop");
/* 134 */         return true;
/*     */       } 
/* 136 */       return false;
/*     */     } 
/*     */     
/* 139 */     return false;
/*     */   } private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/*     */     LiteralTree myLtt;
/*     */     IdentifierTree myItt;
/*     */     String sRet;
/*     */     AssignmentExpressionTree aet;
/* 145 */     ExpressionStatementTree est = (ExpressionStatementTree)eachLineTree;
/*     */     
/* 147 */     ExpressionTree expressionTree = est.expression();
/*     */     
/* 149 */     switch (expressionTree.kind()) {
/*     */       case STRING_LITERAL:
/* 151 */         myLtt = (LiteralTree)expressionTree;
/* 152 */         log("value is GIL- " + myLtt.value());
/*     */         break;
/*     */       
/*     */       case IDENTIFIER:
/* 156 */         myItt = (IdentifierTree)expressionTree;
/* 157 */         log("name is GIL- " + myItt.name());
/*     */         break;
/*     */       case METHOD_INVOCATION:
/* 160 */         sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 161 */         log("line - " + sRet);
/*     */         break;
/*     */       
/*     */       case ASSIGNMENT:
/* 165 */         aet = (AssignmentExpressionTree)expressionTree;
/* 166 */         invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 176 */     TypeCastTree tct = (TypeCastTree)et;
/* 177 */     ExpressionTree ext = tct.expression();
/* 178 */     String sRet = "";
/* 179 */     switch (ext.kind()) {
/*     */       case METHOD_INVOCATION:
/* 181 */         sRet = invokeMethodInvocationTreeMethod((Tree)ext, eachLineTree);
/* 182 */         log("line - " + sRet);
/*     */       
/*     */       case MEMBER_SELECT:
/* 185 */         sRet = invokeMemberSelectMethod((Tree)ext, eachLineTree);
/* 186 */         log("line - " + sRet);
/*     */       
/*     */       case IDENTIFIER:
/*     */         return;
/*     */     } 
/* 191 */     log("unexpected kind in invokeTypeCastTreeMethod - " + ext.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree et, Tree eachLineTree) {
/*     */     String sRet;
/* 198 */     switch (et.kind()) {
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
/* 209 */         invokeTypeCastTreeMethod(et, eachLineTree);
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
/* 245 */         sRet = invokeMethodInvocationTreeMethod(et, eachLineTree);
/* 246 */         log("line - " + sRet);
/*     */     } 
/*     */     
/* 249 */     log("unexpected kind in switch invokeAssignmentExpressionStatementTreeMethod - " + et.kind().toString());
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 256 */     String sRet = "";
/* 257 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 258 */     log("*** variable kind *** - " + variableTree.type().kind().toString());
/*     */     
/* 260 */     VariableTree myVtt = (VariableTree)variableTree.symbol().declaration();
/* 261 */     String myVariableName = variableTree.symbol().name();
/* 262 */     String myVariableType = variableTree.symbol().type().name();
/*     */     
/* 264 */     VariableTree variableTree1 = myVtt;
/*     */     try {
/* 266 */       expressionTree = myVtt.initializer();
/* 267 */       log("MYETT  - " + expressionTree.kind().toString());
/* 268 */     } catch (Exception ex) {
/* 269 */       log(" --- inside exception --" + ex);
/* 270 */       if (expressionTree == null) {
/* 271 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/*     */     
/* 275 */     switch (tree.kind()) {
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
/* 307 */         invokeTypeCastTreeMethod(tree, eachLineTree);
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
/* 322 */         sRet = invokeMemberSelectMethod(tree, eachLineTree);
/* 323 */         log("line - " + myVariableType + " " + myVariableName + " = " + sRet + "--args--");
/*     */ 
/*     */       
/*     */       case METHOD_INVOCATION:
/* 327 */         sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/* 328 */         log("line - " + myVariableType + " " + myVariableName + " = " + sRet);
/*     */     } 
/*     */     
/* 331 */     log("unexpected kind in switch - " + tree.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 338 */     MethodInvocationTree myMit = (MethodInvocationTree)mytree;
/* 339 */     ExpressionTree expressionTree = myMit.methodSelect();
/* 340 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 341 */     String sMyArgs = "";
/*     */     
/* 343 */     if (myMit.arguments().size() != 0) {
/* 344 */       Arguments<Tree> arguments = myMit.arguments();
/*     */       
/* 346 */       sMyArgs = "(";
/* 347 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/*     */ 
/*     */         
/* 350 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 351 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 352 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 353 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 354 */           sRet = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 357 */       sMyArgs = sMyArgs + "--)";
/* 358 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 360 */       sMyArgs = "()";
/*     */     } 
/*     */ 
/*     */     
/* 364 */     return sRet + sMyArgs; } private String invokeMemberSelectMethod(Tree ettemp, Tree eachLineTree) {
/*     */     MemberSelectExpressionTree mset;
/*     */     String myDeclarationMethodName, myDeclarationCallingMethodName;
/*     */     IdentifierTree myItt;
/* 368 */     String str = "";
/* 369 */     switch (ettemp.kind())
/*     */     { case MEMBER_SELECT:
/* 371 */         mset = (MemberSelectExpressionTree)ettemp;
/* 372 */         myDeclarationMethodName = mset.firstToken().text();
/* 373 */         myDeclarationCallingMethodName = mset.identifier().name();
/* 374 */         str = myDeclarationMethodName + "." + myDeclarationCallingMethodName;
/*     */         
/* 376 */         if (mset.identifier().name().equals("getInfo")) {
/* 377 */           bumpUpCount(myDeclarationMethodName);
/* 378 */           bCheckAndReportIssueNow(myDeclarationMethodName, eachLineTree);
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
/* 394 */         return str;case IDENTIFIER: myItt = (IdentifierTree)ettemp; if (myItt.name().equals("getInfo")) { bumpUpCount(myItt.name()); reportIssue(eachLineTree, "Avoid multiple getInfo inside the method or loop"); }  return str; }  log("unexpected kind in iinvokeMemberSelectMethod - " + ettemp.kind().toString()); return str;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 399 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 400 */     StatementTree et = forStmtTree.statement();
/*     */     
/* 402 */     if ("BLOCK".equals(et.kind().toString())) {
/* 403 */       BlockTree bt = (BlockTree)forStmtTree.statement();
/* 404 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 405 */     } else if ("EXPRESSION_STATEMENT".equals(et.kind().toString())) {
/* 406 */       invokeExpressionStatementTreeMethod((Tree)et);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 413 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/*     */ 
/*     */     
/* 416 */     BlockTree bt = (BlockTree)forEachStmt.statement();
/* 417 */     checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 423 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */     
/* 425 */     ExpressionTree et = ifStmtTree.condition();
/*     */     
/* 427 */     if ("LOGICAL_COMPLEMENT".equals(et.kind().toString())) {
/* 428 */       UnaryExpressionTree uet = (UnaryExpressionTree)et;
/* 429 */       ExpressionTree newet = uet.expression();
/* 430 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 431 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 432 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */ 
/*     */       
/*     */       }
/*     */     
/* 437 */     } else if ("METHOD_INVOCATION".equals(et.kind().toString())) {
/*     */ 
/*     */       
/* 440 */       invokeMethodInvocationTreeMethod((Tree)et, ifLoopTree);
/*     */     } 
/*     */ 
/*     */     
/* 444 */     StatementTree st = ifStmtTree.thenStatement();
/*     */ 
/*     */     
/* 447 */     invokeIfElseStatementTreeMethod(st);
/*     */ 
/*     */     
/*     */     try {
/* 451 */       while (ifStmtTree.elseStatement() != null) {
/* 452 */         st = ifStmtTree.elseStatement();
/*     */         
/* 454 */         if ("IF_STATEMENT".equals(st.kind().toString())) {
/*     */           
/* 456 */           ifStmtTree = (IfStatementTree)st;
/* 457 */           StatementTree newst = ifStmtTree.thenStatement();
/* 458 */           invokeIfElseStatementTreeMethod(newst);
/*     */           
/*     */           continue;
/*     */         } 
/* 462 */         invokeIfElseStatementTreeMethod(st);
/* 463 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 466 */     } catch (Exception ex) {
/* 467 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree st) {
/*     */     BlockTree bt;
/* 472 */     switch (st.kind()) {
/*     */       case BLOCK:
/* 474 */         bt = (BlockTree)st;
/* 475 */         checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */       
/*     */       case EXPRESSION_STATEMENT:
/* 478 */         invokeExpressionStatementTreeMethod((Tree)st);
/*     */ 
/*     */       
/*     */       case BREAK_STATEMENT:
/*     */       case RETURN_STATEMENT:
/*     */         return;
/*     */     } 
/*     */     
/* 486 */     log("unexpected kind in is stmt tree - " + st.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 493 */     Tree eachLineTree = null;
/*     */     
/* 495 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/*     */       TryStatementTree tst; BlockTree btTryStmtTree; List<? extends CatchTree> catches; int iCatchCnt; WhileStatementTree wst; StatementTree st;
/* 497 */       eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 499 */       switch (eachLineTree.kind()) {
/*     */         case VARIABLE:
/* 501 */           invokeVariableTreeMethod(eachLineTree);
/*     */           break;
/*     */         case EXPRESSION_STATEMENT:
/* 504 */           invokeExpressionStatementTreeMethod(eachLineTree);
/*     */           break;
/*     */ 
/*     */ 
/*     */         
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
/* 526 */             log("btTryStmtFinallyTree  - " + btTryStmtFinallyTree.kind().toString());
/*     */ 
/*     */             
/* 529 */             checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */           }
/* 531 */           catch (Exception ex) {
/* 532 */             log(" --- no finally block available exception -- " + ex);
/*     */           } 
/*     */           break;
/*     */         case FOR_STATEMENT:
/* 536 */           invokeForStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case FOR_EACH_STATEMENT:
/* 539 */           invokeForEachStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case IF_STATEMENT:
/* 542 */           invokeIfStmtTreeMethod(eachLineTree);
/*     */           break;
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
/*     */         case WHILE_STATEMENT:
/* 558 */           wst = (WhileStatementTree)eachLineTree;
/* 559 */           st = wst.statement();
/*     */           
/* 561 */           invokeIfElseStatementTreeMethod(st);
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidMultiplegetInfoRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */