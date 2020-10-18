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
/*     */ @Rule(key = "AvoidADKAPIfindObjectInloop")
/*     */ public class AvoidADKAPIfindObjectInloopRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  39 */   public int max = 2;
/*     */   boolean bLoggingActive = false;
/*     */   boolean bForLoop = false;
/*  42 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  47 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  57 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  59 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  60 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  62 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  63 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  65 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  74 */     BlockTree blocktree = methodTree.block();
/*     */     
/*  76 */     Tree.Kind tk = methodTree.kind();
/*  77 */     log("&&&& - " + tk.toString() + " --> " + methodTree.simpleName().name());
/*     */     
/*  79 */     this.htReportIssue = new Hashtable<>();
/*     */ 
/*     */     
/*  82 */     if (blocktree != null)
/*     */     {
/*  84 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  91 */     log("5");
/*  92 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  93 */       log("6");
/*     */       
/*  95 */       addressEachTree(trees);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void log(String str) {
/* 101 */     if (this.bLoggingActive) {
/* 102 */       System.out.println(str);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private Hashtable bumpUpCount(String sMethodName) {
/* 108 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 109 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 110 */       int iNewSize = iSize.intValue() + 1;
/* 111 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 113 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/* 115 */     log("########################### - bumpUp ht : " + this.htReportIssue);
/* 116 */     return this.htReportIssue;
/*     */   }
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/*     */     String sRet;
/*     */     AssignmentExpressionTree aet;
/* 122 */     ExpressionStatementTree est = (ExpressionStatementTree)eachLineTree;
/* 123 */     ExpressionTree expressionTree = est.expression();
/* 124 */     switch (expressionTree.kind()) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case METHOD_INVOCATION:
/* 130 */         sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 131 */         log("line - " + sRet);
/*     */         break;
/*     */       case ASSIGNMENT:
/* 134 */         aet = (AssignmentExpressionTree)expressionTree;
/* 135 */         invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 144 */     TypeCastTree tct = (TypeCastTree)et;
/* 145 */     ExpressionTree ext = tct.expression();
/* 146 */     String sRet = "";
/*     */ 
/*     */     
/* 149 */     switch (ext.kind()) {
/*     */       case METHOD_INVOCATION:
/* 151 */         sRet = invokeMethodInvocationTreeMethod((Tree)ext, eachLineTree);
/* 152 */         log("line - " + sRet);
/*     */       
/*     */       case MEMBER_SELECT:
/* 155 */         sRet = invokeMemberSelectMethod((Tree)ext, eachLineTree);
/* 156 */         log("line - " + sRet);
/*     */       
/*     */       case IDENTIFIER:
/*     */         return;
/*     */     } 
/* 161 */     log("unexpected kind in invokeTypeCastTreeMethod - " + ext.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree et, Tree eachLineTree) {
/*     */     String sRet;
/* 168 */     switch (et.kind()) {
/*     */       case STRING_LITERAL:
/*     */       case BOOLEAN_LITERAL:
/*     */       case NULL_LITERAL:
/*     */       case IDENTIFIER:
/*     */         return;
/*     */ 
/*     */ 
/*     */       
/*     */       case TYPE_CAST:
/* 178 */         invokeTypeCastTreeMethod(et, eachLineTree);
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
/* 214 */         sRet = invokeMethodInvocationTreeMethod(et, eachLineTree);
/* 215 */         log("line - " + sRet);
/*     */     } 
/*     */     
/* 218 */     log("unexpected kind in switch invokeAssignmentExpressionStatementTreeMethod - " + et.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/* 225 */     if (this.bForLoop) {
/* 226 */       ExpressionTree expressionTree; Tree tree; String sRet = "";
/* 227 */       VariableTree variableTree = (VariableTree)eachLineTree;
/* 228 */       log("*** variable kind *** - " + variableTree.type().kind().toString());
/*     */       
/* 230 */       VariableTree myVtt = (VariableTree)variableTree.symbol().declaration();
/* 231 */       String myVariableName = variableTree.symbol().name();
/* 232 */       String myVariableType = variableTree.symbol().type().name();
/* 233 */       VariableTree variableTree1 = myVtt;
/*     */       try {
/* 235 */         expressionTree = myVtt.initializer();
/* 236 */         log("MYETT  - " + expressionTree.kind().toString());
/* 237 */       } catch (Exception ex) {
/* 238 */         log(" --- inside exception --" + ex);
/* 239 */         if (expressionTree == null) {
/* 240 */           tree = eachLineTree;
/*     */         }
/*     */       } 
/*     */       
/* 244 */       switch (tree.kind()) {
/*     */         case STRING_LITERAL:
/*     */         case INT_LITERAL:
/*     */         case DOUBLE_LITERAL:
/*     */         case NULL_LITERAL:
/*     */         case BOOLEAN_LITERAL:
/*     */         case NEW_CLASS:
/*     */         case PLUS:
/*     */         case MINUS:
/*     */         case DIVIDE:
/*     */         case REMAINDER:
/*     */           return;
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
/*     */         case TYPE_CAST:
/* 275 */           invokeTypeCastTreeMethod(tree, eachLineTree);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case NEW_ARRAY:
/*     */         case VARIABLE:
/*     */         case ARRAY_ACCESS_EXPRESSION:
/*     */         case IDENTIFIER:
/*     */           return;
/*     */ 
/*     */ 
/*     */         
/*     */         case MEMBER_SELECT:
/* 289 */           sRet = invokeMemberSelectMethod(tree, eachLineTree);
/* 290 */           log("line - " + myVariableType + " " + myVariableName + " = " + sRet + "--args--");
/*     */         
/*     */         case METHOD_INVOCATION:
/* 293 */           sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */       } 
/*     */ 
/*     */       
/* 297 */       log("unexpected kind in switch - " + tree.kind().toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 305 */     MethodInvocationTree myMit = (MethodInvocationTree)mytree;
/* 306 */     ExpressionTree expressionTree = myMit.methodSelect();
/* 307 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 308 */     String sMyArgs = "";
/*     */     
/* 310 */     if (myMit.arguments().size() != 0) {
/* 311 */       Arguments<Tree> arguments = myMit.arguments();
/* 312 */       sMyArgs = "(";
/* 313 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/*     */         
/* 315 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 316 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 317 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 318 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/*     */         
/*     */         } 
/*     */       } 
/* 322 */       sMyArgs = sMyArgs + "--)";
/* 323 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 325 */       sMyArgs = "()";
/*     */     } 
/*     */     
/* 328 */     return sRet + sMyArgs; } private String invokeMemberSelectMethod(Tree ettemp, Tree eachLineTree) {
/*     */     MemberSelectExpressionTree mset;
/*     */     String myDeclarationMethodName;
/*     */     IdentifierTree myItt;
/* 332 */     Tree tree = ettemp.parent().parent().parent();
/*     */     
/* 334 */     String str = "";
/*     */     
/* 336 */     switch (ettemp.kind())
/*     */     
/*     */     { case MEMBER_SELECT:
/* 339 */         mset = (MemberSelectExpressionTree)ettemp;
/* 340 */         myDeclarationMethodName = mset.firstToken().text();
/*     */ 
/*     */         
/* 343 */         if (mset.identifier().name().equals("findObjects")) {
/* 344 */           reportIssue(eachLineTree, "findObjects() method is used in for loop, avoid inside loop");
/* 345 */           bumpUpCount(myDeclarationMethodName);
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
/*     */         
/* 362 */         return str;case IDENTIFIER: myItt = (IdentifierTree)ettemp; if (myItt.name().equals("findObjects")) { bumpUpCount(myItt.name()); reportIssue(eachLineTree, "findObjects() method is used in for loop, avoid inside loop"); }  return str; }  log("unexpected kind in iinvokeMemberSelectMethod - " + ettemp.kind().toString()); return str;
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 366 */     Tree eachLineTree = null;
/*     */     
/* 368 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/*     */ 
/*     */     
/* 371 */     StatementTree et = forStmtTree.statement();
/*     */     
/* 373 */     if (forStmtTree.kind().toString().equals("FOR_STATEMENT"))
/*     */     {
/*     */       
/* 376 */       if ("BLOCK".equals(et.kind().toString())) {
/* 377 */         this.bForLoop = true;
/* 378 */         BlockTree bt = (BlockTree)forStmtTree.statement();
/*     */ 
/*     */ 
/*     */         
/* 382 */         checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 383 */         this.bForLoop = false;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 391 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 392 */     BlockTree bt = (BlockTree)forEachStmt.statement();
/* 393 */     checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 398 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */     
/* 400 */     ExpressionTree et = ifStmtTree.condition();
/*     */     
/* 402 */     if ("LOGICAL_COMPLEMENT".equals(et.kind().toString())) {
/* 403 */       UnaryExpressionTree uet = (UnaryExpressionTree)et;
/* 404 */       ExpressionTree newet = uet.expression();
/*     */       
/* 406 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 407 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 408 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */ 
/*     */       
/*     */       }
/*     */     
/* 413 */     } else if ("METHOD_INVOCATION".equals(et.kind().toString())) {
/*     */       
/* 415 */       invokeMethodInvocationTreeMethod((Tree)et, ifLoopTree);
/*     */     } 
/*     */ 
/*     */     
/* 419 */     StatementTree st = ifStmtTree.thenStatement();
/*     */ 
/*     */     
/* 422 */     invokeIfElseStatementTreeMethod(st);
/*     */ 
/*     */     
/*     */     try {
/* 426 */       while (ifStmtTree.elseStatement() != null) {
/* 427 */         st = ifStmtTree.elseStatement();
/*     */         
/* 429 */         if ("IF_STATEMENT".equals(st.kind().toString())) {
/*     */           
/* 431 */           ifStmtTree = (IfStatementTree)st;
/* 432 */           StatementTree newst = ifStmtTree.thenStatement();
/* 433 */           invokeIfElseStatementTreeMethod(newst);
/*     */           continue;
/*     */         } 
/* 436 */         invokeIfElseStatementTreeMethod(st);
/* 437 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 440 */     } catch (Exception ex) {
/* 441 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree st) {
/*     */     BlockTree bt;
/* 446 */     switch (st.kind()) {
/*     */       case BLOCK:
/* 448 */         bt = (BlockTree)st;
/* 449 */         checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */ 
/*     */       
/*     */       case EXPRESSION_STATEMENT:
/* 453 */         invokeExpressionStatementTreeMethod((Tree)st);
/*     */ 
/*     */       
/*     */       case BREAK_STATEMENT:
/*     */       case RETURN_STATEMENT:
/*     */         return;
/*     */     } 
/*     */ 
/*     */     
/* 462 */     log("unexpected kind in is stmt tree - " + st.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 468 */     Tree eachLineTree = null;
/* 469 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/*     */       TryStatementTree tst; BlockTree btTryStmtTree; List<? extends CatchTree> catches; int iCatchCnt; WhileStatementTree wst; StatementTree st;
/* 471 */       eachLineTree = listOfTrees.get(iLine);
/* 472 */       switch (eachLineTree.kind()) {
/*     */         case VARIABLE:
/* 474 */           invokeVariableTreeMethod(eachLineTree);
/*     */           break;
/*     */         case EXPRESSION_STATEMENT:
/* 477 */           invokeExpressionStatementTreeMethod(eachLineTree);
/*     */           break;
/*     */         
/*     */         case METHOD_INVOCATION:
/*     */           break;
/*     */         case TRY_STATEMENT:
/* 483 */           tst = (TryStatementTree)eachLineTree;
/* 484 */           btTryStmtTree = tst.block();
/*     */ 
/*     */           
/* 487 */           checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */           
/* 489 */           catches = tst.catches();
/*     */           
/* 491 */           for (iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 492 */             CatchTree ct = catches.get(iCatchCnt);
/* 493 */             BlockTree btCatch = ct.block();
/*     */             
/* 495 */             checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */           } 
/*     */ 
/*     */           
/*     */           try {
/* 500 */             BlockTree btTryStmtFinallyTree = tst.finallyBlock();
/* 501 */             log("btTryStmtFinallyTree  - " + btTryStmtFinallyTree.kind().toString());
/*     */ 
/*     */ 
/*     */             
/* 505 */             checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */           
/*     */           }
/* 508 */           catch (Exception ex) {
/* 509 */             log(" --- no finally block available exception -- " + ex);
/*     */           } 
/*     */           break;
/*     */         case FOR_STATEMENT:
/* 513 */           invokeForStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case FOR_EACH_STATEMENT:
/* 516 */           invokeForEachStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case IF_STATEMENT:
/* 519 */           invokeIfStmtTreeMethod(eachLineTree);
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
/*     */         
/*     */         case WHILE_STATEMENT:
/* 535 */           wst = (WhileStatementTree)eachLineTree;
/* 536 */           st = wst.statement();
/*     */           
/* 538 */           invokeIfElseStatementTreeMethod(st);
/*     */           break;
/*     */ 
/*     */         
/*     */         case THROW_STATEMENT:
/*     */         case EMPTY_STATEMENT:
/*     */           break;
/*     */         
/*     */         default:
/* 547 */           log("unexpected kind in switch 444  - " + eachLineTree.kind().toString());
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidADKAPIfindObjectInloopRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */