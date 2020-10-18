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
/*     */ @Rule(key = "AvoidMultiplegetRelatedObjects")
/*     */ public class AvoidMultiplegetRelatedObjectRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  40 */   public int max = 2;
/*     */   boolean bLoggingActive = false;
/*  42 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
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
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  59 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  61 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  62 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  64 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  65 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  67 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  74 */     BlockTree blocktree = methodTree.block();
/*     */     
/*  76 */     Tree.Kind tk = methodTree.kind();
/*  77 */     log("MGRObs &&&& - " + tk.toString() + " --> " + methodTree.simpleName().name());
/*     */     
/*  79 */     this.htReportIssue = new Hashtable<>();
/*     */     
/*  81 */     if (blocktree != null)
/*     */     {
/*  83 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  89 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  90 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(String str) {
/*  95 */     if (this.bLoggingActive) {
/*  96 */       System.out.println(str);
/*     */     }
/*     */   }
/*     */   
/*     */   private Hashtable bumpUpCount(String sMethodName) {
/* 101 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 102 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 103 */       int iNewSize = iSize.intValue() + 1;
/* 104 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 106 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/*     */     
/* 109 */     return this.htReportIssue;
/*     */   }
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String sMethodName, Tree eachLineTree) {
/* 113 */     if (this.htReportIssue.size() > 0);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 118 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 119 */       Integer iSize = this.htReportIssue.get(sMethodName);
/*     */       
/* 121 */       if (iSize.intValue() > 1) {
/* 122 */         reportIssue(eachLineTree, "Avoid multiple getRelatedObjects inside the method or loop");
/* 123 */         return true;
/*     */       } 
/* 125 */       return false;
/*     */     } 
/*     */     
/* 128 */     return false;
/*     */   } private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/*     */     LiteralTree myLtt;
/*     */     IdentifierTree myItt;
/*     */     String sRet;
/*     */     AssignmentExpressionTree aet;
/* 134 */     ExpressionStatementTree est = (ExpressionStatementTree)eachLineTree;
/*     */     
/* 136 */     ExpressionTree expressionTree = est.expression();
/*     */     
/* 138 */     switch (expressionTree.kind()) {
/*     */       
/*     */       case STRING_LITERAL:
/* 141 */         myLtt = (LiteralTree)expressionTree;
/* 142 */         log("value is MGRObs- " + myLtt.value());
/*     */         break;
/*     */       
/*     */       case IDENTIFIER:
/* 146 */         myItt = (IdentifierTree)expressionTree;
/* 147 */         log("name is MGRObs- " + myItt.name());
/*     */         break;
/*     */       case METHOD_INVOCATION:
/* 150 */         sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 151 */         log("line - MGRObs" + sRet);
/*     */         break;
/*     */       case ASSIGNMENT:
/* 154 */         aet = (AssignmentExpressionTree)expressionTree;
/* 155 */         invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 163 */     TypeCastTree tct = (TypeCastTree)et;
/* 164 */     ExpressionTree ext = tct.expression();
/* 165 */     String sRet = "";
/*     */     
/* 167 */     switch (ext.kind()) {
/*     */       case METHOD_INVOCATION:
/* 169 */         sRet = invokeMethodInvocationTreeMethod((Tree)ext, eachLineTree);
/* 170 */         log("line - MGRObs" + sRet);
/*     */       
/*     */       case MEMBER_SELECT:
/* 173 */         sRet = invokeMemberSelectMethod((Tree)ext, eachLineTree);
/* 174 */         log("line - MGRObs" + sRet);
/*     */       
/*     */       case IDENTIFIER:
/*     */         return;
/*     */     } 
/* 179 */     log("unexpected kind in invokeTypeCastTreeMethod - MGRObs" + ext.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree et, Tree eachLineTree) {
/*     */     String sRet;
/* 186 */     switch (et.kind()) {
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
/* 198 */         invokeTypeCastTreeMethod(et, eachLineTree);
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
/* 234 */         sRet = invokeMethodInvocationTreeMethod(et, eachLineTree);
/* 235 */         log("line - MGRObs" + sRet);
/*     */     } 
/*     */     
/* 238 */     log("unexpected kind in switch invokeAssignmentExpressionStatementTreeMethod - MGRObs" + et.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 246 */     String sRet = "";
/* 247 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 248 */     log("MGRObs variable kind *** - " + variableTree.type().kind().toString());
/*     */     
/* 250 */     VariableTree myVtt = (VariableTree)variableTree.symbol().declaration();
/* 251 */     String myVariableName = variableTree.symbol().name();
/* 252 */     String myVariableType = variableTree.symbol().type().name();
/*     */     
/* 254 */     VariableTree variableTree1 = myVtt;
/*     */     try {
/* 256 */       expressionTree = myVtt.initializer();
/* 257 */       log("MGRObs  - " + expressionTree.kind().toString());
/* 258 */     } catch (Exception ex) {
/* 259 */       log(" MGRObs--- inside exception --" + ex);
/* 260 */       if (expressionTree == null) {
/* 261 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/*     */     
/* 265 */     switch (tree.kind()) {
/*     */       
/*     */       case TYPE_CAST:
/* 268 */         invokeTypeCastTreeMethod(tree, eachLineTree);
/*     */       
/*     */       case IDENTIFIER:
/*     */         return;
/*     */       case MEMBER_SELECT:
/* 273 */         sRet = invokeMemberSelectMethod(tree, eachLineTree);
/* 274 */         log("line - MGRObs" + myVariableType + " " + myVariableName + " = " + sRet + "--args--");
/*     */ 
/*     */       
/*     */       case METHOD_INVOCATION:
/* 278 */         sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/* 279 */         log("line - " + myVariableType + " " + myVariableName + " = " + sRet);
/*     */     } 
/*     */     
/* 282 */     log("unexpected kind in switch -MGRObs " + tree.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 289 */     MethodInvocationTree myMit = (MethodInvocationTree)mytree;
/*     */     
/* 291 */     ExpressionTree expressionTree = myMit.methodSelect();
/*     */     
/* 293 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/*     */     
/* 295 */     String sMyArgs = "";
/*     */     
/* 297 */     if (myMit.arguments().size() != 0) {
/*     */       
/* 299 */       Arguments<Tree> arguments = myMit.arguments();
/*     */       
/* 301 */       sMyArgs = "(";
/* 302 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/*     */ 
/*     */         
/* 305 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 306 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 307 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 308 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 309 */           sRet = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 312 */       sMyArgs = sMyArgs + "--)";
/* 313 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 315 */       sMyArgs = "()";
/*     */     } 
/*     */     
/* 318 */     return sRet + sMyArgs; } private String invokeMemberSelectMethod(Tree ettemp, Tree eachLineTree) {
/*     */     MemberSelectExpressionTree mset;
/*     */     String myDeclarationMethodName, myDeclarationCallingMethodName;
/*     */     IdentifierTree myItt;
/* 322 */     String str = "";
/*     */     
/* 324 */     switch (ettemp.kind())
/*     */     { case MEMBER_SELECT:
/* 326 */         mset = (MemberSelectExpressionTree)ettemp;
/* 327 */         myDeclarationMethodName = mset.firstToken().text();
/* 328 */         myDeclarationCallingMethodName = mset.identifier().name();
/* 329 */         str = myDeclarationMethodName + "." + myDeclarationCallingMethodName;
/*     */         
/* 331 */         if (mset.identifier().name().equals("getRelatedObjects") || mset
/* 332 */           .identifier().name().equals("getRelatedObject")) {
/*     */           
/* 334 */           bumpUpCount(myDeclarationMethodName);
/* 335 */           bCheckAndReportIssueNow(myDeclarationMethodName, eachLineTree);
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
/* 353 */         return str;case IDENTIFIER: myItt = (IdentifierTree)ettemp; if (myItt.name().equals("getRelatedObjects") || myItt.name().equals("getRelatedObject")) { bumpUpCount(myItt.name()); reportIssue(eachLineTree, "Avoid multiple getRelatedObjects inside the method or loop"); }  return str; }  log("unexpected kind in iinvokeMemberSelectMethod - " + ettemp.kind().toString()); return str;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 358 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 359 */     StatementTree et = forStmtTree.statement();
/*     */     
/* 361 */     if ("BLOCK".equals(et.kind().toString())) {
/* 362 */       BlockTree bt = (BlockTree)forStmtTree.statement();
/* 363 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 364 */     } else if ("EXPRESSION_STATEMENT".equals(et.kind().toString())) {
/* 365 */       invokeExpressionStatementTreeMethod((Tree)et);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 372 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 373 */     BlockTree bt = (BlockTree)forEachStmt.statement();
/* 374 */     checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 379 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */ 
/*     */     
/* 382 */     ExpressionTree et = ifStmtTree.condition();
/*     */     
/* 384 */     if ("LOGICAL_COMPLEMENT".equals(et.kind().toString())) {
/* 385 */       UnaryExpressionTree uet = (UnaryExpressionTree)et;
/* 386 */       ExpressionTree newet = uet.expression();
/*     */       
/* 388 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 389 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 390 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */ 
/*     */       
/*     */       }
/*     */     
/* 395 */     } else if ("METHOD_INVOCATION".equals(et.kind().toString())) {
/*     */       
/* 397 */       invokeMethodInvocationTreeMethod((Tree)et, ifLoopTree);
/*     */     } 
/*     */ 
/*     */     
/* 401 */     StatementTree st = ifStmtTree.thenStatement();
/*     */     
/* 403 */     invokeIfElseStatementTreeMethod(st);
/*     */ 
/*     */     
/*     */     try {
/* 407 */       while (ifStmtTree.elseStatement() != null) {
/* 408 */         st = ifStmtTree.elseStatement();
/*     */         
/* 410 */         if ("IF_STATEMENT".equals(st.kind().toString())) {
/*     */           
/* 412 */           ifStmtTree = (IfStatementTree)st;
/*     */           
/* 414 */           StatementTree newst = ifStmtTree.thenStatement();
/*     */           
/* 416 */           invokeIfElseStatementTreeMethod(newst);
/*     */           
/*     */           continue;
/*     */         } 
/* 420 */         invokeIfElseStatementTreeMethod(st);
/* 421 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 424 */     } catch (Exception ex) {
/* 425 */       log(" MGRObs--- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree st) {
/*     */     BlockTree bt;
/* 430 */     switch (st.kind()) {
/*     */       case BLOCK:
/* 432 */         bt = (BlockTree)st;
/* 433 */         checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */ 
/*     */       
/*     */       case EXPRESSION_STATEMENT:
/* 437 */         invokeExpressionStatementTreeMethod((Tree)st);
/*     */ 
/*     */       
/*     */       case BREAK_STATEMENT:
/*     */       case RETURN_STATEMENT:
/*     */         return;
/*     */     } 
/*     */ 
/*     */     
/* 446 */     log("unexpected kind in is stmt tree - MGRObs" + st.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 453 */     Tree eachLineTree = null;
/*     */     
/* 455 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 456 */       TryStatementTree tst; BlockTree btTryStmtTree; List<? extends CatchTree> catches; int iCatchCnt; WhileStatementTree wst; StatementTree st; eachLineTree = listOfTrees.get(iLine);
/* 457 */       switch (eachLineTree.kind()) {
/*     */         case VARIABLE:
/* 459 */           invokeVariableTreeMethod(eachLineTree);
/*     */           break;
/*     */         case EXPRESSION_STATEMENT:
/* 462 */           invokeExpressionStatementTreeMethod(eachLineTree);
/*     */           break;
/*     */         
/*     */         case METHOD_INVOCATION:
/*     */           break;
/*     */         case TRY_STATEMENT:
/* 468 */           tst = (TryStatementTree)eachLineTree;
/* 469 */           btTryStmtTree = tst.block();
/*     */ 
/*     */           
/* 472 */           checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */           
/* 474 */           catches = tst.catches();
/*     */           
/* 476 */           for (iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 477 */             CatchTree ct = catches.get(iCatchCnt);
/* 478 */             BlockTree btCatch = ct.block();
/* 479 */             checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */           } 
/*     */           
/*     */           try {
/* 483 */             BlockTree btTryStmtFinallyTree = tst.finallyBlock();
/* 484 */             log("btTryStmtFinallyTree  -MGRObs " + btTryStmtFinallyTree.kind().toString());
/*     */ 
/*     */             
/* 487 */             checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree
/* 488 */                 .body());
/*     */           }
/* 490 */           catch (Exception ex) {
/* 491 */             log(" MGRObs--- no finally block available exception -- " + ex);
/*     */           } 
/*     */           break;
/*     */         case FOR_STATEMENT:
/* 495 */           invokeForStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case FOR_EACH_STATEMENT:
/* 498 */           invokeForEachStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case IF_STATEMENT:
/* 501 */           invokeIfStmtTreeMethod(eachLineTree);
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
/* 517 */           wst = (WhileStatementTree)eachLineTree;
/* 518 */           st = wst.statement();
/*     */           
/* 520 */           invokeIfElseStatementTreeMethod(st);
/*     */           break;
/*     */ 
/*     */         
/*     */         case THROW_STATEMENT:
/*     */         case EMPTY_STATEMENT:
/*     */           break;
/*     */         
/*     */         default:
/* 529 */           log("unexpected kind in switch MGRObs - " + eachLineTree.kind().toString());
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidMultiplegetRelatedObjectRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */