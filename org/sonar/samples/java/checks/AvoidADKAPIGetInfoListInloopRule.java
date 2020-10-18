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
/*     */ @Rule(key = "AvoidADKAPIGetInfoListInloop")
/*     */ public class AvoidADKAPIGetInfoListInloopRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  45 */   public int max = 2;
/*     */   boolean bLoggingActive = false;
/*     */   boolean bForLoop = false;
/*  48 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  53 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
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
/*  64 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  66 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  67 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  69 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  70 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  72 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  80 */     BlockTree blocktree = methodTree.block();
/*     */     
/*  82 */     Tree.Kind tk = methodTree.kind();
/*  83 */     log("&&&& - " + tk.toString() + " --> " + methodTree.simpleName().name());
/*     */     
/*  85 */     this.htReportIssue = new Hashtable<>();
/*     */ 
/*     */     
/*  88 */     if (blocktree != null)
/*     */     {
/*  90 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  97 */     log("5");
/*  98 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  99 */       log("6");
/*     */       
/* 101 */       addressEachTree(trees);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void log(String str) {
/* 107 */     if (this.bLoggingActive) {
/* 108 */       System.out.println(str);
/*     */     }
/*     */   }
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
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/*     */     LiteralTree myLtt;
/*     */     IdentifierTree myItt;
/*     */     String sRet;
/*     */     AssignmentExpressionTree aet;
/* 128 */     ExpressionStatementTree est = (ExpressionStatementTree)eachLineTree;
/* 129 */     ExpressionTree expressionTree = est.expression();
/*     */     
/* 131 */     switch (expressionTree.kind()) {
/*     */       case STRING_LITERAL:
/* 133 */         myLtt = (LiteralTree)expressionTree;
/* 134 */         log("value is 222- " + myLtt.value());
/*     */         break;
/*     */       
/*     */       case IDENTIFIER:
/* 138 */         myItt = (IdentifierTree)expressionTree;
/* 139 */         log("name is 222- " + myItt.name());
/*     */         break;
/*     */       case METHOD_INVOCATION:
/* 142 */         sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 143 */         log("line - " + sRet);
/*     */         break;
/*     */       
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
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 157 */     TypeCastTree tct = (TypeCastTree)et;
/* 158 */     ExpressionTree ext = tct.expression();
/* 159 */     String sRet = "";
/* 160 */     switch (ext.kind()) {
/*     */       case METHOD_INVOCATION:
/* 162 */         sRet = invokeMethodInvocationTreeMethod((Tree)ext, eachLineTree);
/* 163 */         log("line - " + sRet);
/*     */       
/*     */       case MEMBER_SELECT:
/* 166 */         sRet = invokeMemberSelectMethod((Tree)ext, eachLineTree);
/* 167 */         log("line - " + sRet);
/*     */       
/*     */       case IDENTIFIER:
/*     */         return;
/*     */     } 
/* 172 */     log("unexpected kind in invokeTypeCastTreeMethod - " + ext.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree et, Tree eachLineTree) {
/*     */     String sRet;
/* 179 */     switch (et.kind()) {
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
/* 190 */         invokeTypeCastTreeMethod(et, eachLineTree);
/*     */ 
/*     */ 
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
/* 226 */         sRet = invokeMethodInvocationTreeMethod(et, eachLineTree);
/* 227 */         log("line - " + sRet);
/*     */     } 
/*     */     
/* 230 */     log("unexpected kind in switch invokeAssignmentExpressionStatementTreeMethod - " + et.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/* 238 */     if (this.bForLoop) {
/* 239 */       ExpressionTree expressionTree; Tree tree; String sRet = "";
/* 240 */       VariableTree variableTree = (VariableTree)eachLineTree;
/* 241 */       log("*** variable kind *** - " + variableTree.type().kind().toString());
/*     */       
/* 243 */       VariableTree myVtt = (VariableTree)variableTree.symbol().declaration();
/* 244 */       String myVariableName = variableTree.symbol().name();
/* 245 */       String myVariableType = variableTree.symbol().type().name();
/*     */       
/* 247 */       VariableTree variableTree1 = myVtt;
/*     */       try {
/* 249 */         expressionTree = myVtt.initializer();
/* 250 */         log("MYETT  - " + expressionTree.kind().toString());
/* 251 */       } catch (Exception ex) {
/* 252 */         log(" --- inside exception --" + ex);
/* 253 */         if (expressionTree == null) {
/* 254 */           tree = eachLineTree;
/*     */         }
/*     */       } 
/*     */       
/* 258 */       switch (tree.kind()) {
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
/* 289 */           invokeTypeCastTreeMethod(tree, eachLineTree);
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
/* 304 */           sRet = invokeMemberSelectMethod(tree, eachLineTree);
/* 305 */           log("line - " + myVariableType + " " + myVariableName + " = " + sRet + "--args--");
/*     */ 
/*     */         
/*     */         case METHOD_INVOCATION:
/* 309 */           sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */       } 
/*     */ 
/*     */       
/* 313 */       log("unexpected kind in switch - " + tree.kind().toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 321 */     MethodInvocationTree myMit = (MethodInvocationTree)mytree;
/*     */     
/* 323 */     ExpressionTree expressionTree = myMit.methodSelect();
/*     */     
/* 325 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/*     */     
/* 327 */     String sMyArgs = "";
/* 328 */     if (myMit.arguments().size() != 0) {
/*     */       
/* 330 */       Arguments<Tree> arguments = myMit.arguments();
/* 331 */       sMyArgs = "(";
/* 332 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/*     */ 
/*     */         
/* 335 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 336 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 337 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 338 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/*     */         
/*     */         } 
/*     */       } 
/* 342 */       sMyArgs = sMyArgs + "--)";
/* 343 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 345 */       sMyArgs = "()";
/*     */     } 
/* 347 */     return sMyArgs; } private String invokeMemberSelectMethod(Tree ettemp, Tree eachLineTree) {
/*     */     MemberSelectExpressionTree mset;
/*     */     String myDeclarationMethodName, myDeclarationCallingMethodName;
/*     */     IdentifierTree myItt;
/* 351 */     Tree tree = ettemp.parent().parent().parent();
/* 352 */     String str = "";
/* 353 */     switch (ettemp.kind())
/*     */     
/*     */     { case MEMBER_SELECT:
/* 356 */         mset = (MemberSelectExpressionTree)ettemp;
/* 357 */         myDeclarationMethodName = mset.firstToken().text();
/* 358 */         myDeclarationCallingMethodName = mset.identifier().name();
/* 359 */         str = myDeclarationMethodName + "." + myDeclarationCallingMethodName;
/*     */         
/* 361 */         if (mset.identifier().name().equals("getInfoList") && this.bForLoop) {
/* 362 */           reportIssue(eachLineTree, "getInfoList() method is used in for loop, avoid inside loop******");
/* 363 */           bumpUpCount(myDeclarationMethodName);
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
/* 379 */         return str;case IDENTIFIER: myItt = (IdentifierTree)ettemp; if (myItt.name().equals("getInfoList")) { bumpUpCount(myItt.name()); reportIssue(eachLineTree, "getInfoList() method is used in for loop, avoid inside loop"); }  return str; }  log("unexpected kind in iinvokeMemberSelectMethod - " + ettemp.kind().toString()); return str;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 384 */     Tree eachLineTree = null;
/*     */ 
/*     */     
/* 387 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/*     */     
/* 389 */     StatementTree et = forStmtTree.statement();
/*     */     
/* 391 */     if (forStmtTree.kind().toString().equals("FOR_STATEMENT"))
/*     */     {
/*     */       
/* 394 */       if ("BLOCK".equals(et.kind().toString())) {
/* 395 */         this.bForLoop = true;
/* 396 */         BlockTree bt = (BlockTree)forStmtTree.statement();
/*     */ 
/*     */ 
/*     */         
/* 400 */         checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 401 */         this.bForLoop = false;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 409 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 410 */     BlockTree bt = (BlockTree)forEachStmt.statement();
/* 411 */     checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 416 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */ 
/*     */     
/* 419 */     ExpressionTree et = ifStmtTree.condition();
/*     */     
/* 421 */     if ("LOGICAL_COMPLEMENT".equals(et.kind().toString())) {
/* 422 */       UnaryExpressionTree uet = (UnaryExpressionTree)et;
/* 423 */       ExpressionTree newet = uet.expression();
/*     */       
/* 425 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 426 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 427 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */ 
/*     */       
/*     */       }
/*     */     
/* 432 */     } else if ("METHOD_INVOCATION".equals(et.kind().toString())) {
/*     */       
/* 434 */       invokeMethodInvocationTreeMethod((Tree)et, ifLoopTree);
/*     */     } 
/*     */ 
/*     */     
/* 438 */     StatementTree st = ifStmtTree.thenStatement();
/*     */ 
/*     */     
/* 441 */     invokeIfElseStatementTreeMethod(st);
/*     */ 
/*     */     
/*     */     try {
/* 445 */       while (ifStmtTree.elseStatement() != null) {
/* 446 */         st = ifStmtTree.elseStatement();
/*     */ 
/*     */         
/* 449 */         if ("IF_STATEMENT".equals(st.kind().toString())) {
/*     */           
/* 451 */           ifStmtTree = (IfStatementTree)st;
/*     */           
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
/* 464 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree st) {
/*     */     BlockTree bt;
/* 469 */     switch (st.kind()) {
/*     */       case BLOCK:
/* 471 */         bt = (BlockTree)st;
/*     */         
/* 473 */         checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */ 
/*     */ 
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
/*     */     
/* 487 */     log("unexpected kind in is stmt tree - " + st.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 494 */     Tree eachLineTree = null;
/*     */     
/* 496 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/*     */       TryStatementTree tst; BlockTree btTryStmtTree; List<? extends CatchTree> catches; int iCatchCnt; WhileStatementTree wst; StatementTree st;
/* 498 */       eachLineTree = listOfTrees.get(iLine);
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
/*     */           
/* 515 */           checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */ 
/*     */           
/* 518 */           catches = tst.catches();
/*     */           
/* 520 */           for (iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 521 */             CatchTree ct = catches.get(iCatchCnt);
/* 522 */             BlockTree btCatch = ct.block();
/*     */             
/* 524 */             checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/*     */           try {
/* 530 */             BlockTree btTryStmtFinallyTree = tst.finallyBlock();
/* 531 */             log("btTryStmtFinallyTree  - " + btTryStmtFinallyTree.kind().toString());
/*     */ 
/*     */ 
/*     */             
/* 535 */             checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */           
/*     */           }
/* 538 */           catch (Exception ex) {
/* 539 */             log(" --- no finally block available exception -- " + ex);
/*     */           } 
/*     */           break;
/*     */         case FOR_STATEMENT:
/* 543 */           invokeForStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case FOR_EACH_STATEMENT:
/* 546 */           this.bForLoop = true;
/* 547 */           invokeForEachStmtTreeMethod(eachLineTree);
/* 548 */           this.bForLoop = false;
/*     */           break;
/*     */         case IF_STATEMENT:
/* 551 */           invokeIfStmtTreeMethod(eachLineTree);
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
/* 566 */           this.bForLoop = true;
/* 567 */           wst = (WhileStatementTree)eachLineTree;
/* 568 */           st = wst.statement();
/*     */           
/* 570 */           invokeIfElseStatementTreeMethod(st);
/* 571 */           this.bForLoop = false;
/*     */           break;
/*     */ 
/*     */         
/*     */         case THROW_STATEMENT:
/*     */         case EMPTY_STATEMENT:
/*     */           break;
/*     */         
/*     */         default:
/* 580 */           log("unexpected kind in switch 444  - " + eachLineTree.kind().toString());
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidADKAPIGetInfoListInloopRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */