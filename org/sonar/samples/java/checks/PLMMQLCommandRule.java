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
/*     */ @Rule(key = "PLMMQLCommand")
/*     */ public class PLMMQLCommandRule
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
/*     */   
/*     */   public void visitNode(Tree tree) {
/* 129 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/* 131 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/* 132 */     Type returnType = methodSymbol.returnType().type();
/*     */ 
/*     */     
/* 135 */     BlockTree blocktree = methodTree.block();
/*     */     
/* 137 */     Tree.Kind tk = methodTree.kind();
/* 138 */     log("&&&& - " + tk.toString() + " --> " + methodTree.simpleName().name());
/*     */     
/* 140 */     this.htReportIssue = new Hashtable<>();
/*     */ 
/*     */     
/* 143 */     if (blocktree != null) {
/* 144 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/* 150 */     log("5");
/* 151 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/* 152 */       log("6");
/*     */       
/* 154 */       addressEachTree(trees);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void log(String str) {
/* 160 */     if (this.bLoggingActive)
/* 161 */       System.out.println(str);  } private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/*     */     LiteralTree myLtt;
/*     */     IdentifierTree myItt;
/*     */     String sRet;
/*     */     AssignmentExpressionTree aet;
/* 166 */     ExpressionStatementTree est = (ExpressionStatementTree)eachLineTree;
/*     */ 
/*     */     
/* 169 */     ExpressionTree expressionTree = est.expression();
/*     */     
/* 171 */     switch (expressionTree.kind()) {
/*     */       case STRING_LITERAL:
/* 173 */         myLtt = (LiteralTree)expressionTree;
/*     */         break;
/*     */       case IDENTIFIER:
/* 176 */         myItt = (IdentifierTree)expressionTree;
/*     */         break;
/*     */       case METHOD_INVOCATION:
/* 179 */         sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/*     */         break;
/*     */       
/*     */       case ASSIGNMENT:
/* 183 */         aet = (AssignmentExpressionTree)expressionTree;
/* 184 */         invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 192 */     TypeCastTree tct = (TypeCastTree)et;
/* 193 */     ExpressionTree ext = tct.expression();
/* 194 */     String sRet = "";
/*     */     
/* 196 */     switch (ext.kind()) {
/*     */       case METHOD_INVOCATION:
/* 198 */         sRet = invokeMethodInvocationTreeMethod((Tree)ext, eachLineTree);
/* 199 */         log("line - " + sRet);
/*     */       
/*     */       case MEMBER_SELECT:
/* 202 */         sRet = invokeMemberSelectMethod((Tree)ext, eachLineTree);
/* 203 */         log("line - " + sRet);
/*     */       
/*     */       case IDENTIFIER:
/*     */         return;
/*     */     } 
/* 208 */     log("unexpected kind in invokeTypeCastTreeMethod - " + ext.kind().toString());
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree et, Tree eachLineTree) {
/*     */     String sRet;
/* 214 */     log("et kind of aest - " + et.kind().toString());
/*     */     
/* 216 */     switch (et.kind()) {
/*     */       case STRING_LITERAL:
/*     */       case BOOLEAN_LITERAL:
/*     */       case NULL_LITERAL:
/*     */       case IDENTIFIER:
/*     */         return;
/*     */ 
/*     */ 
/*     */       
/*     */       case TYPE_CAST:
/* 226 */         invokeTypeCastTreeMethod(et, eachLineTree);
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
/*     */       case METHOD_INVOCATION:
/* 251 */         sRet = invokeMethodInvocationTreeMethod(et, eachLineTree);
/*     */     } 
/*     */     
/* 254 */     log("unexpected kind in switch invokeAssignmentExpressionStatementTreeMethod - " + et.kind().toString());
/*     */   }
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 260 */     String sRet = "";
/* 261 */     VariableTree variableTree = (VariableTree)eachLineTree;
/*     */     
/* 263 */     VariableTree myVtt = (VariableTree)variableTree.symbol().declaration();
/* 264 */     String myVariableName = variableTree.symbol().name();
/* 265 */     String myVariableType = variableTree.symbol().type().name();
/* 266 */     VariableTree variableTree1 = myVtt;
/*     */     try {
/* 268 */       expressionTree = myVtt.initializer();
/* 269 */       log("MYETT  - " + expressionTree.kind().toString());
/* 270 */     } catch (Exception ex) {
/* 271 */       log(" --- inside exception --" + ex);
/* 272 */       if (expressionTree == null) {
/* 273 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/*     */     
/* 277 */     switch (tree.kind()) {
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
/*     */       case TYPE_CAST:
/* 299 */         invokeTypeCastTreeMethod(tree, eachLineTree);
/*     */ 
/*     */       
/*     */       case NEW_ARRAY:
/*     */       case VARIABLE:
/*     */       case ARRAY_ACCESS_EXPRESSION:
/*     */       case IDENTIFIER:
/*     */         return;
/*     */ 
/*     */       
/*     */       case MEMBER_SELECT:
/* 310 */         sRet = invokeMemberSelectMethod(tree, eachLineTree);
/*     */       
/*     */       case METHOD_INVOCATION:
/* 313 */         sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     } 
/*     */     
/* 316 */     log("unexpected kind in switch - " + tree.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 324 */     MethodInvocationTree myMit = (MethodInvocationTree)mytree;
/* 325 */     ExpressionTree expressionTree = myMit.methodSelect();
/* 326 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/*     */     
/* 328 */     String sMyArgs = "";
/* 329 */     if (myMit.arguments().size() != 0) {
/*     */       
/* 331 */       Arguments<Tree> arguments = myMit.arguments();
/*     */       
/* 333 */       sMyArgs = "(";
/* 334 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/*     */ 
/*     */         
/* 337 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 338 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 339 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 340 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 341 */           sRet = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 344 */       sMyArgs = sMyArgs + "--)";
/* 345 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 347 */       sMyArgs = "()";
/*     */     } 
/* 349 */     return sRet + sMyArgs;
/*     */   }
/*     */   private String invokeMemberSelectMethod(Tree ettemp, Tree eachLineTree) {
/*     */     MemberSelectExpressionTree mset;
/* 353 */     String myDeclarationMethodName, myDeclarationCallingMethodName, str = "";
/*     */     
/* 355 */     switch (ettemp.kind()) {
/*     */       case MEMBER_SELECT:
/* 357 */         mset = (MemberSelectExpressionTree)ettemp;
/* 358 */         myDeclarationMethodName = mset.firstToken().text();
/* 359 */         myDeclarationCallingMethodName = mset.identifier().name();
/* 360 */         str = myDeclarationMethodName + "." + myDeclarationCallingMethodName;
/* 361 */         if (myDeclarationCallingMethodName.equalsIgnoreCase("executeMQL") || myDeclarationCallingMethodName.equalsIgnoreCase("executeCommand") || myDeclarationCallingMethodName.equalsIgnoreCase("mqlCommand")) {
/* 362 */           reportIssue(eachLineTree, "Sogeti MQL Command Rule: Do not use MQL Commands in a JPO");
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case IDENTIFIER:
/* 372 */         return str;
/*     */     } 
/*     */     log("unexpected kind in iinvokeMemberSelectMethod - " + ettemp.kind().toString());
/*     */   } private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 376 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 377 */     StatementTree et = forStmtTree.statement();
/* 378 */     if ("BLOCK".equals(et.kind().toString())) {
/* 379 */       BlockTree bt = (BlockTree)forStmtTree.statement();
/* 380 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 381 */     } else if ("EXPRESSION_STATEMENT".equals(et.kind().toString())) {
/* 382 */       invokeExpressionStatementTreeMethod((Tree)et);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 387 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 388 */     BlockTree bt = (BlockTree)forEachStmt.statement();
/* 389 */     checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */   }
/*     */ 
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
/* 421 */     invokeIfElseStatementTreeMethod(st);
/*     */ 
/*     */     
/*     */     try {
/* 425 */       while (ifStmtTree.elseStatement() != null) {
/* 426 */         st = ifStmtTree.elseStatement();
/*     */         
/* 428 */         if ("IF_STATEMENT".equals(st.kind().toString())) {
/* 429 */           ifStmtTree = (IfStatementTree)st;
/* 430 */           StatementTree newst = ifStmtTree.thenStatement();
/* 431 */           invokeIfElseStatementTreeMethod(newst); continue;
/*     */         } 
/* 433 */         invokeIfElseStatementTreeMethod(st);
/* 434 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 437 */     } catch (Exception ex) {
/* 438 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree st) {
/*     */     BlockTree bt;
/* 443 */     switch (st.kind()) {
/*     */       case BLOCK:
/* 445 */         bt = (BlockTree)st;
/* 446 */         checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */       
/*     */       case EXPRESSION_STATEMENT:
/* 449 */         invokeExpressionStatementTreeMethod((Tree)st);
/*     */       
/*     */       case BREAK_STATEMENT:
/*     */       case RETURN_STATEMENT:
/*     */         return;
/*     */     } 
/*     */     
/* 456 */     log("unexpected kind in is stmt tree - " + st.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 462 */     String eachLineText = "";
/* 463 */     Tree eachLineTree = null;
/* 464 */     int iCount = 0;
/* 465 */     int iMethodCount = 0;
/* 466 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 467 */       TryStatementTree tst; BlockTree btTryStmtTree; List<? extends CatchTree> catches; int iCatchCnt; WhileStatementTree wst; StatementTree st; eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 469 */       switch (eachLineTree.kind()) {
/*     */         case VARIABLE:
/* 471 */           invokeVariableTreeMethod(eachLineTree);
/*     */           break;
/*     */         case EXPRESSION_STATEMENT:
/* 474 */           invokeExpressionStatementTreeMethod(eachLineTree);
/*     */           break;
/*     */         case METHOD_INVOCATION:
/*     */           break;
/*     */         case TRY_STATEMENT:
/* 479 */           tst = (TryStatementTree)eachLineTree;
/* 480 */           btTryStmtTree = tst.block();
/*     */           
/* 482 */           checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */           
/* 484 */           catches = tst.catches();
/*     */           
/* 486 */           for (iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 487 */             CatchTree ct = catches.get(iCatchCnt);
/* 488 */             BlockTree btCatch = ct.block();
/* 489 */             checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */           } 
/*     */           
/*     */           try {
/* 493 */             BlockTree btTryStmtFinallyTree = tst.finallyBlock();
/*     */ 
/*     */             
/* 496 */             checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */           }
/* 498 */           catch (Exception ex) {
/* 499 */             log(" --- no finally block available exception -- " + ex);
/*     */           } 
/*     */           break;
/*     */         case FOR_STATEMENT:
/* 503 */           invokeForStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case FOR_EACH_STATEMENT:
/* 506 */           invokeForEachStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case IF_STATEMENT:
/* 509 */           invokeIfStmtTreeMethod(eachLineTree);
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
/* 524 */           wst = (WhileStatementTree)eachLineTree;
/* 525 */           st = wst.statement();
/* 526 */           invokeIfElseStatementTreeMethod(st);
/*     */           break;
/*     */ 
/*     */         
/*     */         case THROW_STATEMENT:
/*     */         case EMPTY_STATEMENT:
/*     */           break;
/*     */         
/*     */         default:
/* 535 */           log("unexpected kind in switch 444  - " + eachLineTree.kind().toString());
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMMQLCommandRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */