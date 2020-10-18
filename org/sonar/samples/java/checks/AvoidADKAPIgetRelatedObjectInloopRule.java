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
/*     */ 
/*     */ 
/*     */ 
/*     */ @Rule(key = "AvoidADKAPIgetRelatedObjectInloop")
/*     */ public class AvoidADKAPIgetRelatedObjectInloopRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  42 */   public int max = 2;
/*     */   boolean bLoggingActive = false;
/*     */   boolean bForLoop = false;
/*  45 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  50 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
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
/*  61 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  63 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  64 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  66 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  67 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  69 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  77 */     BlockTree blocktree = methodTree.block();
/*  78 */     Tree.Kind tk = methodTree.kind();
/*  79 */     log("&&&& - " + tk.toString() + " --> " + methodTree.simpleName().name());
/*     */     
/*  81 */     this.htReportIssue = new Hashtable<>();
/*  82 */     if (blocktree != null) {
/*  83 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  89 */     log("5");
/*  90 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  91 */       log("6");
/*     */       
/*  93 */       addressEachTree(trees);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void log(String str) {
/*  99 */     if (this.bLoggingActive) {
/* 100 */       System.out.println(str);
/*     */     }
/*     */   }
/*     */   
/*     */   private Hashtable bumpUpCount(String sMethodName) {
/* 105 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 106 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 107 */       int iNewSize = iSize.intValue() + 1;
/* 108 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 110 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/*     */     
/* 113 */     return this.htReportIssue;
/*     */   }
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/*     */     String sRet;
/*     */     AssignmentExpressionTree aet;
/* 119 */     ExpressionStatementTree est = (ExpressionStatementTree)eachLineTree;
/* 120 */     ExpressionTree expressionTree = est.expression();
/* 121 */     switch (expressionTree.kind()) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case METHOD_INVOCATION:
/* 127 */         sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 128 */         log("line - " + sRet);
/*     */         break;
/*     */       
/*     */       case ASSIGNMENT:
/* 132 */         aet = (AssignmentExpressionTree)expressionTree;
/* 133 */         invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 141 */     TypeCastTree tct = (TypeCastTree)et;
/* 142 */     ExpressionTree ext = tct.expression();
/* 143 */     String sRet = "";
/* 144 */     switch (ext.kind()) {
/*     */       case METHOD_INVOCATION:
/* 146 */         sRet = invokeMethodInvocationTreeMethod((Tree)ext, eachLineTree);
/* 147 */         log("line - " + sRet);
/*     */       
/*     */       case MEMBER_SELECT:
/* 150 */         sRet = invokeMemberSelectMethod((Tree)ext, eachLineTree);
/* 151 */         log("line - " + sRet);
/*     */       
/*     */       case IDENTIFIER:
/*     */         return;
/*     */     } 
/* 156 */     log("unexpected kind in invokeTypeCastTreeMethod - " + ext.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree et, Tree eachLineTree) {
/*     */     String sRet;
/* 163 */     switch (et.kind()) {
/*     */       case STRING_LITERAL:
/*     */       case BOOLEAN_LITERAL:
/*     */       case NULL_LITERAL:
/*     */       case IDENTIFIER:
/*     */         return;
/*     */ 
/*     */ 
/*     */       
/*     */       case TYPE_CAST:
/* 173 */         invokeTypeCastTreeMethod(et, eachLineTree);
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
/*     */       case METHOD_INVOCATION:
/* 208 */         sRet = invokeMethodInvocationTreeMethod(et, eachLineTree);
/* 209 */         log("line - " + sRet);
/*     */     } 
/*     */     
/* 212 */     log("unexpected kind in switch invokeAssignmentExpressionStatementTreeMethod - " + et.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/* 219 */     if (this.bForLoop) {
/* 220 */       ExpressionTree expressionTree; Tree tree; String sRet = "";
/* 221 */       VariableTree variableTree = (VariableTree)eachLineTree;
/* 222 */       log("*** variable kind *** - " + variableTree.type().kind().toString());
/*     */       
/* 224 */       VariableTree myVtt = (VariableTree)variableTree.symbol().declaration();
/* 225 */       String myVariableName = variableTree.symbol().name();
/* 226 */       String myVariableType = variableTree.symbol().type().name();
/* 227 */       VariableTree variableTree1 = myVtt;
/*     */       try {
/* 229 */         expressionTree = myVtt.initializer();
/* 230 */         log("MYETT  - " + expressionTree.kind().toString());
/* 231 */       } catch (Exception ex) {
/* 232 */         log(" --- inside exception --" + ex);
/* 233 */         if (expressionTree == null) {
/* 234 */           tree = eachLineTree;
/*     */         }
/*     */       } 
/*     */       
/* 238 */       switch (tree.kind()) {
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
/* 269 */           invokeTypeCastTreeMethod(tree, eachLineTree);
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
/*     */         
/*     */         case MEMBER_SELECT:
/* 284 */           sRet = invokeMemberSelectMethod(tree, eachLineTree);
/* 285 */           log("line - " + myVariableType + " " + myVariableName + " = " + sRet + "--args--");
/*     */         
/*     */         case METHOD_INVOCATION:
/* 288 */           sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/* 289 */           log("line - " + myVariableType + " " + myVariableName + " = " + sRet);
/*     */       } 
/*     */       
/* 292 */       log("unexpected kind in switch - " + tree.kind().toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 300 */     MethodInvocationTree myMit = (MethodInvocationTree)mytree;
/* 301 */     ExpressionTree expressionTree = myMit.methodSelect();
/* 302 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/*     */     
/* 304 */     String sMyArgs = "";
/* 305 */     if (myMit.arguments().size() != 0) {
/* 306 */       Arguments<Tree> arguments = myMit.arguments();
/*     */       
/* 308 */       sMyArgs = "(";
/* 309 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/*     */         
/* 311 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 312 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 313 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 314 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/*     */         
/*     */         } 
/*     */       } 
/* 318 */       sMyArgs = sMyArgs + "--)";
/* 319 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 321 */       sMyArgs = "()";
/*     */     } 
/*     */     
/* 324 */     return sRet + sMyArgs; } private String invokeMemberSelectMethod(Tree ettemp, Tree eachLineTree) {
/*     */     MemberSelectExpressionTree mset;
/*     */     String myDeclarationMethodName, myDeclarationCallingMethodName;
/*     */     IdentifierTree myItt;
/* 328 */     Tree tree = ettemp.parent().parent().parent();
/*     */     
/* 330 */     String str = "";
/*     */     
/* 332 */     switch (ettemp.kind())
/*     */     { case MEMBER_SELECT:
/* 334 */         mset = (MemberSelectExpressionTree)ettemp;
/* 335 */         myDeclarationMethodName = mset.firstToken().text();
/* 336 */         myDeclarationCallingMethodName = mset.identifier().name();
/*     */ 
/*     */ 
/*     */         
/* 340 */         if (mset.identifier().name().equals("getRelatedObjects")) {
/* 341 */           reportIssue(eachLineTree, "getRelatedObjects() method is used in for loop, avoid inside loop");
/* 342 */           bumpUpCount(myDeclarationMethodName);
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
/*     */ 
/*     */         
/* 361 */         return str;case IDENTIFIER: myItt = (IdentifierTree)ettemp; if (myItt.name().equals("getRelatedObjects")) { bumpUpCount(myItt.name()); reportIssue(eachLineTree, "getRelatedObjects() method is used in for loop, avoid inside loop"); }  return str; }  log("unexpected kind in iinvokeMemberSelectMethod - " + ettemp.kind().toString()); return str;
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 365 */     Tree eachLineTree = null;
/*     */     
/* 367 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/*     */     
/* 369 */     StatementTree et = forStmtTree.statement();
/*     */     
/* 371 */     if (forStmtTree.kind().toString().equals("FOR_STATEMENT") && 
/* 372 */       "BLOCK".equals(et.kind().toString())) {
/* 373 */       this.bForLoop = true;
/* 374 */       BlockTree bt = (BlockTree)forStmtTree.statement();
/*     */ 
/*     */ 
/*     */       
/* 378 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 379 */       this.bForLoop = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 388 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 389 */     BlockTree bt = (BlockTree)forEachStmt.statement();
/* 390 */     checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 397 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */ 
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
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 469 */     Tree eachLineTree = null;
/* 470 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/*     */       TryStatementTree tst; BlockTree btTryStmtTree; List<? extends CatchTree> catches; int iCatchCnt; WhileStatementTree wst; StatementTree st;
/* 472 */       eachLineTree = listOfTrees.get(iLine);
/*     */ 
/*     */       
/* 475 */       switch (eachLineTree.kind()) {
/*     */         case VARIABLE:
/* 477 */           invokeVariableTreeMethod(eachLineTree);
/*     */           break;
/*     */         case EXPRESSION_STATEMENT:
/* 480 */           invokeExpressionStatementTreeMethod(eachLineTree);
/*     */           break;
/*     */         
/*     */         case METHOD_INVOCATION:
/*     */           break;
/*     */         case TRY_STATEMENT:
/* 486 */           tst = (TryStatementTree)eachLineTree;
/* 487 */           btTryStmtTree = tst.block();
/*     */ 
/*     */           
/* 490 */           checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */           
/* 492 */           catches = tst.catches();
/*     */           
/* 494 */           for (iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 495 */             CatchTree ct = catches.get(iCatchCnt);
/* 496 */             BlockTree btCatch = ct.block();
/* 497 */             checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */           } 
/*     */           
/*     */           try {
/* 501 */             BlockTree btTryStmtFinallyTree = tst.finallyBlock();
/* 502 */             log("btTryStmtFinallyTree  - " + btTryStmtFinallyTree.kind().toString());
/*     */ 
/*     */             
/* 505 */             checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */           }
/* 507 */           catch (Exception ex) {
/* 508 */             log(" --- no finally block available exception -- " + ex);
/*     */           } 
/*     */           break;
/*     */         case FOR_STATEMENT:
/* 512 */           invokeForStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case FOR_EACH_STATEMENT:
/* 515 */           invokeForEachStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case IF_STATEMENT:
/* 518 */           invokeIfStmtTreeMethod(eachLineTree);
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
/* 534 */           wst = (WhileStatementTree)eachLineTree;
/* 535 */           st = wst.statement();
/*     */           
/* 537 */           invokeIfElseStatementTreeMethod(st);
/*     */           break;
/*     */ 
/*     */ 
/*     */         
/*     */         case THROW_STATEMENT:
/*     */         case EMPTY_STATEMENT:
/*     */           break;
/*     */ 
/*     */ 
/*     */         
/*     */         default:
/* 549 */           log("unexpected kind in switch 444  - " + eachLineTree.kind().toString());
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidADKAPIgetRelatedObjectInloopRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */