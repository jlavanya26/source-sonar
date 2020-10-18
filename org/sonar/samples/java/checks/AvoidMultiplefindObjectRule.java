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
/*     */ @Rule(key = "AvoidMultiplefindObject")
/*     */ public class AvoidMultiplefindObjectRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  41 */   public int max = 2;
/*     */   boolean bLoggingActive = false;
/*  43 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  48 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  58 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  60 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  61 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  63 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  64 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  66 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  72 */     BlockTree blocktree = methodTree.block();
/*     */     
/*  74 */     Tree.Kind tk = methodTree.kind();
/*  75 */     log("MultipleFindObjectRule - " + tk.toString() + " --> " + methodTree.simpleName().name());
/*     */     
/*  77 */     this.htReportIssue = new Hashtable<>();
/*     */     
/*  79 */     if (blocktree != null)
/*     */     {
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
/*     */   private void log(String str) {
/*  93 */     if (this.bLoggingActive) {
/*  94 */       System.out.println(str);
/*     */     }
/*     */   }
/*     */   
/*     */   private Hashtable bumpUpCount(String sMethodName) {
/*  99 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 100 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 101 */       int iNewSize = iSize.intValue() + 1;
/* 102 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 104 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/*     */     
/* 107 */     return this.htReportIssue;
/*     */   }
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String sMethodName, Tree eachLineTree) {
/* 111 */     if (this.htReportIssue.size() > 0);
/*     */ 
/*     */     
/* 114 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 115 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 116 */       if (iSize.intValue() > 1) {
/* 117 */         reportIssue(eachLineTree, "Avoid multiple findObjects inside the method or loop");
/* 118 */         return true;
/*     */       } 
/* 120 */       return false;
/*     */     } 
/*     */     
/* 123 */     return false;
/*     */   } private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/*     */     LiteralTree myLtt;
/*     */     IdentifierTree myItt;
/*     */     String sRet;
/*     */     AssignmentExpressionTree aet;
/* 129 */     ExpressionStatementTree est = (ExpressionStatementTree)eachLineTree;
/* 130 */     ExpressionTree expressionTree = est.expression();
/*     */     
/* 132 */     switch (expressionTree.kind()) {
/*     */       case STRING_LITERAL:
/* 134 */         myLtt = (LiteralTree)expressionTree;
/* 135 */         log("value is MFObs- " + myLtt.value());
/*     */         break;
/*     */       
/*     */       case IDENTIFIER:
/* 139 */         myItt = (IdentifierTree)expressionTree;
/* 140 */         log("name is MFObs- " + myItt.name());
/*     */         break;
/*     */       case METHOD_INVOCATION:
/* 143 */         sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 144 */         log("line - MFObs" + sRet);
/*     */         break;
/*     */       case ASSIGNMENT:
/* 147 */         aet = (AssignmentExpressionTree)expressionTree;
/* 148 */         invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 158 */     TypeCastTree tct = (TypeCastTree)et;
/* 159 */     ExpressionTree ext = tct.expression();
/* 160 */     String sRet = "";
/*     */     
/* 162 */     switch (ext.kind()) {
/*     */       case METHOD_INVOCATION:
/* 164 */         sRet = invokeMethodInvocationTreeMethod((Tree)ext, eachLineTree);
/* 165 */         log("line - MFObs" + sRet);
/*     */       
/*     */       case MEMBER_SELECT:
/* 168 */         sRet = invokeMemberSelectMethod((Tree)ext, eachLineTree);
/* 169 */         log("line - MFObs" + sRet);
/*     */       
/*     */       case IDENTIFIER:
/*     */         return;
/*     */     } 
/* 174 */     log("MFObs - unexpected kind in invokeTypeCastTreeMethod " + ext.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree et, Tree eachLineTree) {
/*     */     String sRet;
/* 181 */     switch (et.kind()) {
/*     */       case STRING_LITERAL:
/*     */       case BOOLEAN_LITERAL:
/*     */       case NULL_LITERAL:
/*     */       case IDENTIFIER:
/*     */         return;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case TYPE_CAST:
/* 193 */         invokeTypeCastTreeMethod(et, eachLineTree);
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
/* 229 */         sRet = invokeMethodInvocationTreeMethod(et, eachLineTree);
/* 230 */         log("line -MFObs " + sRet);
/*     */     } 
/*     */     
/* 233 */     log("unexpected kind in switch invokeAssignmentExpressionStatementTreeMethod - MFObs" + et.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 241 */     String sRet = "";
/* 242 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 243 */     log("*** variable kind *** - MFObs" + variableTree.type().kind().toString());
/*     */     
/* 245 */     VariableTree myVtt = (VariableTree)variableTree.symbol().declaration();
/* 246 */     String myVariableName = variableTree.symbol().name();
/* 247 */     String myVariableType = variableTree.symbol().type().name();
/*     */     
/* 249 */     VariableTree variableTree1 = myVtt;
/*     */     try {
/* 251 */       expressionTree = myVtt.initializer();
/* 252 */     } catch (Exception ex) {
/*     */       
/* 254 */       if (expressionTree == null) {
/* 255 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/*     */     
/* 259 */     switch (tree.kind()) {
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
/* 291 */         invokeTypeCastTreeMethod(tree, eachLineTree);
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
/* 306 */         sRet = invokeMemberSelectMethod(tree, eachLineTree);
/* 307 */         log("line - MFObs" + myVariableType + " " + myVariableName + " = " + sRet + "--args--");
/*     */ 
/*     */       
/*     */       case METHOD_INVOCATION:
/* 311 */         sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/* 312 */         log("line - " + myVariableType + " " + myVariableName + " = " + sRet);
/*     */     } 
/*     */     
/* 315 */     log("unexpected kind in switch - MFObs" + tree.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 322 */     MethodInvocationTree myMit = (MethodInvocationTree)mytree;
/* 323 */     ExpressionTree expressionTree = myMit.methodSelect();
/* 324 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 325 */     String sMyArgs = "";
/*     */     
/* 327 */     if (myMit.arguments().size() != 0) {
/*     */       
/* 329 */       Arguments<Tree> arguments = myMit.arguments();
/*     */       
/* 331 */       sMyArgs = "(";
/* 332 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/*     */         
/* 334 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 335 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 336 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 337 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 338 */           sRet = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 341 */       sMyArgs = sMyArgs + "--)";
/* 342 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 344 */       sMyArgs = "()";
/*     */     } 
/*     */     
/* 347 */     return sRet + sMyArgs; } private String invokeMemberSelectMethod(Tree ettemp, Tree eachLineTree) {
/*     */     MemberSelectExpressionTree mset;
/*     */     String myDeclarationMethodName, myDeclarationCallingMethodName;
/*     */     IdentifierTree myItt;
/* 351 */     String str = "";
/* 352 */     switch (ettemp.kind())
/*     */     { case MEMBER_SELECT:
/* 354 */         mset = (MemberSelectExpressionTree)ettemp;
/* 355 */         myDeclarationMethodName = mset.firstToken().text();
/* 356 */         myDeclarationCallingMethodName = mset.identifier().name();
/* 357 */         str = myDeclarationMethodName + "." + myDeclarationCallingMethodName;
/*     */         
/* 359 */         if (mset.identifier().name().equals("findObjects")) {
/* 360 */           bumpUpCount(myDeclarationMethodName);
/* 361 */           bCheckAndReportIssueNow(myDeclarationMethodName, eachLineTree);
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
/* 379 */         return str;case IDENTIFIER: myItt = (IdentifierTree)ettemp; if (myItt.name().equals("findObjects")) { bumpUpCount(myItt.name()); reportIssue(eachLineTree, "Avoid multiple findObjects inside the method or loop"); }  return str; }  log("unexpected kind in iinvokeMemberSelectMethod - " + ettemp.kind().toString()); return str;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 384 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 385 */     StatementTree et = forStmtTree.statement();
/*     */     
/* 387 */     if ("BLOCK".equals(et.kind().toString())) {
/* 388 */       BlockTree bt = (BlockTree)forStmtTree.statement();
/* 389 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 390 */     } else if ("EXPRESSION_STATEMENT".equals(et.kind().toString())) {
/* 391 */       invokeExpressionStatementTreeMethod((Tree)et);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 398 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 399 */     BlockTree bt = (BlockTree)forEachStmt.statement();
/* 400 */     checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 405 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */     
/* 407 */     ExpressionTree et = ifStmtTree.condition();
/*     */     
/* 409 */     if ("LOGICAL_COMPLEMENT".equals(et.kind().toString())) {
/* 410 */       UnaryExpressionTree uet = (UnaryExpressionTree)et;
/* 411 */       ExpressionTree newet = uet.expression();
/*     */       
/* 413 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 414 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 415 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */ 
/*     */       
/*     */       }
/*     */     
/* 420 */     } else if ("METHOD_INVOCATION".equals(et.kind().toString())) {
/*     */ 
/*     */ 
/*     */       
/* 424 */       invokeMethodInvocationTreeMethod((Tree)et, ifLoopTree);
/*     */     } 
/*     */ 
/*     */     
/* 428 */     StatementTree st = ifStmtTree.thenStatement();
/*     */     
/* 430 */     invokeIfElseStatementTreeMethod(st);
/*     */ 
/*     */     
/*     */     try {
/* 434 */       while (ifStmtTree.elseStatement() != null) {
/* 435 */         st = ifStmtTree.elseStatement();
/*     */         
/* 437 */         if ("IF_STATEMENT".equals(st.kind().toString())) {
/*     */           
/* 439 */           ifStmtTree = (IfStatementTree)st;
/*     */           
/* 441 */           StatementTree newst = ifStmtTree.thenStatement();
/*     */           
/* 443 */           invokeIfElseStatementTreeMethod(newst);
/*     */           
/*     */           continue;
/*     */         } 
/* 447 */         invokeIfElseStatementTreeMethod(st);
/* 448 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 451 */     } catch (Exception ex) {
/* 452 */       log(" MFObs --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree st) {
/*     */     BlockTree bt;
/* 457 */     switch (st.kind()) {
/*     */       case BLOCK:
/* 459 */         bt = (BlockTree)st;
/* 460 */         checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */ 
/*     */       
/*     */       case EXPRESSION_STATEMENT:
/* 464 */         invokeExpressionStatementTreeMethod((Tree)st);
/*     */ 
/*     */       
/*     */       case BREAK_STATEMENT:
/*     */       case RETURN_STATEMENT:
/*     */         return;
/*     */     } 
/*     */ 
/*     */     
/* 473 */     log("unexpected kind in is stmt tree - " + st.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 480 */     Tree eachLineTree = null;
/* 481 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 482 */       TryStatementTree tst; BlockTree btTryStmtTree; List<? extends CatchTree> catches; int iCatchCnt; WhileStatementTree wst; StatementTree st; eachLineTree = listOfTrees.get(iLine);
/* 483 */       switch (eachLineTree.kind()) {
/*     */         case VARIABLE:
/* 485 */           invokeVariableTreeMethod(eachLineTree);
/*     */           break;
/*     */         case EXPRESSION_STATEMENT:
/* 488 */           invokeExpressionStatementTreeMethod(eachLineTree);
/*     */           break;
/*     */         
/*     */         case METHOD_INVOCATION:
/*     */           break;
/*     */         case TRY_STATEMENT:
/* 494 */           tst = (TryStatementTree)eachLineTree;
/* 495 */           btTryStmtTree = tst.block();
/*     */           
/* 497 */           checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */           
/* 499 */           catches = tst.catches();
/*     */           
/* 501 */           for (iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 502 */             CatchTree ct = catches.get(iCatchCnt);
/* 503 */             BlockTree btCatch = ct.block();
/* 504 */             checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */           } 
/*     */           
/*     */           try {
/* 508 */             BlockTree btTryStmtFinallyTree = tst.finallyBlock();
/* 509 */             log("btTryStmtFinallyTree  - " + btTryStmtFinallyTree.kind().toString());
/*     */ 
/*     */             
/* 512 */             checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree
/* 513 */                 .body());
/*     */           }
/* 515 */           catch (Exception ex) {
/* 516 */             log(" MFObs--- no finally block available exception -- " + ex);
/*     */           } 
/*     */           break;
/*     */         case FOR_STATEMENT:
/* 520 */           invokeForStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case FOR_EACH_STATEMENT:
/* 523 */           invokeForEachStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case IF_STATEMENT:
/* 526 */           invokeIfStmtTreeMethod(eachLineTree);
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
/* 542 */           wst = (WhileStatementTree)eachLineTree;
/* 543 */           st = wst.statement();
/*     */           
/* 545 */           invokeIfElseStatementTreeMethod(st);
/*     */           break;
/*     */ 
/*     */         
/*     */         case THROW_STATEMENT:
/*     */         case EMPTY_STATEMENT:
/*     */           break;
/*     */         
/*     */         default:
/* 554 */           log("unexpected kind in switch 444  - " + eachLineTree.kind().toString());
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidMultiplefindObjectRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */