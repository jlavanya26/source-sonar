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
/*     */ @Rule(key = "AvoidsetRPEValue")
/*     */ public class AvoidsetRPEValueRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  40 */   public int max = 2;
/*     */   boolean bLoggingActive = false;
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
/*     */   public void visitNode(Tree tree) {
/*  56 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  58 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  59 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  61 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  62 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  64 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  69 */     BlockTree blocktree = methodTree.block();
/*     */     
/*  71 */     Tree.Kind tk = methodTree.kind();
/*  72 */     log("&&&& - " + tk.toString() + " --> " + methodTree.simpleName().name());
/*     */     
/*  74 */     this.htReportIssue = new Hashtable<>();
/*     */     
/*  76 */     if (blocktree != null) {
/*  77 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  83 */     log("5");
/*  84 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  85 */       log("6");
/*  86 */       addressEachTree(trees);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void log(String str) {
/*  92 */     if (this.bLoggingActive) {
/*  93 */       System.out.println(str);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private Hashtable bumpUpCount(String sMethodName) {
/*  99 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 100 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 101 */       int iNewSize = iSize.intValue() + 1;
/* 102 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 104 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/* 106 */     return this.htReportIssue;
/*     */   } private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/*     */     LiteralTree myLtt;
/*     */     IdentifierTree myItt;
/*     */     String sRet;
/*     */     AssignmentExpressionTree aet;
/* 112 */     ExpressionStatementTree est = (ExpressionStatementTree)eachLineTree;
/* 113 */     ExpressionTree expressionTree = est.expression();
/*     */     
/* 115 */     switch (expressionTree.kind()) {
/*     */       case STRING_LITERAL:
/* 117 */         myLtt = (LiteralTree)expressionTree;
/* 118 */         log("myLtt L- " + myLtt);
/*     */         break;
/*     */       case IDENTIFIER:
/* 121 */         myItt = (IdentifierTree)expressionTree;
/* 122 */         log("myLtt I- " + myItt);
/*     */         break;
/*     */       case METHOD_INVOCATION:
/* 125 */         sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 126 */         log("line - " + sRet);
/*     */         break;
/*     */       case ASSIGNMENT:
/* 129 */         aet = (AssignmentExpressionTree)expressionTree;
/* 130 */         invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 138 */     TypeCastTree tct = (TypeCastTree)et;
/* 139 */     ExpressionTree ext = tct.expression();
/* 140 */     String sRet = "";
/* 141 */     log(" --- RPE Set Value -from type tree-" + ext.kind());
/* 142 */     switch (ext.kind()) {
/*     */       case METHOD_INVOCATION:
/* 144 */         sRet = invokeMethodInvocationTreeMethod((Tree)ext, eachLineTree);
/* 145 */         log("line - " + sRet);
/*     */         return;
/*     */       case MEMBER_SELECT:
/* 148 */         sRet = invokeMemberSelectMethod((Tree)ext, eachLineTree);
/* 149 */         log("line - " + sRet);
/*     */         return;
/*     */       case IDENTIFIER:
/* 152 */         sRet = invokeMemberSelectMethod((Tree)ext, eachLineTree);
/*     */         return;
/*     */     } 
/* 155 */     log("unexpected kind in invokeTypeCastTreeMethod - " + ext.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree et, Tree eachLineTree) {
/*     */     String sRet;
/* 162 */     switch (et.kind()) {
/*     */       case STRING_LITERAL:
/*     */       case BOOLEAN_LITERAL:
/*     */       case NULL_LITERAL:
/*     */       case IDENTIFIER:
/*     */         return;
/*     */ 
/*     */ 
/*     */       
/*     */       case TYPE_CAST:
/* 172 */         invokeTypeCastTreeMethod(et, eachLineTree);
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
/* 197 */         sRet = invokeMethodInvocationTreeMethod(et, eachLineTree);
/* 198 */         log("line - " + sRet);
/*     */     } 
/*     */     
/* 201 */     log("unexpected kind in switch invokeAssignmentExpressionStatementTreeMethod - " + et.kind().toString());
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 208 */     String sRet = null;
/* 209 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 210 */     log("*** variable kind *** - " + variableTree.type().kind().toString());
/*     */     
/* 212 */     VariableTree myVtt = (VariableTree)variableTree.symbol().declaration();
/* 213 */     String myVariableName = variableTree.symbol().name();
/* 214 */     String myVariableType = variableTree.symbol().type().name();
/*     */     
/* 216 */     VariableTree variableTree1 = myVtt;
/*     */     try {
/* 218 */       expressionTree = myVtt.initializer();
/* 219 */       log("MYETT  - " + expressionTree.kind().toString());
/* 220 */     } catch (Exception ex) {
/* 221 */       log(" --- inside exception --" + ex);
/* 222 */       if (expressionTree == null) {
/* 223 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/* 226 */     switch (tree.kind()) {
/*     */       case TYPE_CAST:
/* 228 */         invokeTypeCastTreeMethod(tree, eachLineTree);
/*     */         return;
/*     */       
/*     */       case NEW_ARRAY:
/* 232 */         sRet = invokeMemberSelectMethod(tree, eachLineTree);
/* 233 */         log(" --Var Tree- RPE Set Value --" + tree.kind());
/*     */         return;
/*     */       case MEMBER_SELECT:
/* 236 */         sRet = invokeMemberSelectMethod(tree, eachLineTree);
/* 237 */         log("line - " + myVariableType + " " + myVariableName + " = " + sRet + "--args--");
/*     */         return;
/*     */       case METHOD_INVOCATION:
/* 240 */         sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/* 241 */         log("line - " + myVariableType + " " + myVariableName + " = " + sRet);
/*     */         return;
/*     */     } 
/* 244 */     log("unexpected kind in switch - " + tree.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 251 */     MethodInvocationTree myMit = (MethodInvocationTree)mytree;
/* 252 */     ExpressionTree expressionTree = myMit.methodSelect();
/*     */     
/* 254 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 255 */     String sMyArgs = "";
/*     */     
/* 257 */     if (myMit.arguments().size() != 0) {
/* 258 */       Arguments<Tree> arguments = myMit.arguments();
/*     */       
/* 260 */       sMyArgs = "(";
/* 261 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/*     */         
/* 263 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 264 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 265 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 266 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 267 */           sRet = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 270 */       sMyArgs = sMyArgs + "--)";
/* 271 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 273 */       sMyArgs = "()";
/*     */     } 
/* 275 */     return sRet + sMyArgs; } private String invokeMemberSelectMethod(Tree ettemp, Tree eachLineTree) {
/*     */     MemberSelectExpressionTree mset;
/*     */     String myDeclarationMethodName;
/*     */     IdentifierTree myItt;
/* 279 */     String strReturn = null;
/* 280 */     switch (ettemp.kind())
/*     */     { case MEMBER_SELECT:
/* 282 */         mset = (MemberSelectExpressionTree)ettemp;
/* 283 */         myDeclarationMethodName = mset.firstToken().text();
/*     */         
/* 285 */         if ("setRPEValue".equals(mset.identifier().name())) {
/* 286 */           bumpUpCount(myDeclarationMethodName);
/* 287 */           reportIssue(eachLineTree, "Avoid Environment varible setRPEValue() / getRPEValue inside JPO");
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
/* 303 */         return strReturn;case IDENTIFIER: myItt = (IdentifierTree)ettemp; if ("setRPEValue".equals(myItt.name())) { bumpUpCount(myItt.name()); reportIssue(eachLineTree, "Avoid Environment varible setRPEValue() / getRPEValue inside JPO"); }  return strReturn; }  log("unexpected kind in iinvokeMemberSelectMethod - " + ettemp.kind().toString()); return strReturn;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 308 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */ 
/*     */     
/* 311 */     ExpressionTree et = ifStmtTree.condition();
/*     */     
/* 313 */     if ("LOGICAL_COMPLEMENT".equals(et.kind().toString())) {
/* 314 */       UnaryExpressionTree uet = (UnaryExpressionTree)et;
/* 315 */       ExpressionTree newet = uet.expression();
/*     */       
/* 317 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 318 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 319 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */ 
/*     */       
/*     */       }
/*     */     
/* 324 */     } else if ("METHOD_INVOCATION".equals(et.kind().toString())) {
/* 325 */       invokeMethodInvocationTreeMethod((Tree)et, ifLoopTree);
/*     */     } 
/*     */ 
/*     */     
/* 329 */     StatementTree st = ifStmtTree.thenStatement();
/*     */     
/* 331 */     invokeIfElseStatementTreeMethod(st);
/*     */ 
/*     */     
/*     */     try {
/* 335 */       while (ifStmtTree.elseStatement() != null) {
/* 336 */         st = ifStmtTree.elseStatement();
/* 337 */         if ("IF_STATEMENT".equals(st.kind().toString())) {
/* 338 */           ifStmtTree = (IfStatementTree)st;
/* 339 */           StatementTree newst = ifStmtTree.thenStatement();
/* 340 */           invokeIfElseStatementTreeMethod(newst);
/*     */           continue;
/*     */         } 
/* 343 */         invokeIfElseStatementTreeMethod(st);
/* 344 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 347 */     } catch (Exception ex) {
/* 348 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree st) {
/*     */     BlockTree bt;
/* 353 */     switch (st.kind()) {
/*     */       case BLOCK:
/* 355 */         bt = (BlockTree)st;
/* 356 */         checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */         return;
/*     */       case EXPRESSION_STATEMENT:
/* 359 */         invokeExpressionStatementTreeMethod((Tree)st);
/*     */         return;
/*     */     } 
/* 362 */     log("unexpected kind in is stmt tree - " + st.kind().toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 369 */     Tree eachLineTree = null;
/* 370 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/*     */       TryStatementTree tst; BlockTree btTryStmtTree; List<? extends CatchTree> catches; int iCatchCnt; WhileStatementTree wst; StatementTree st;
/* 372 */       eachLineTree = listOfTrees.get(iLine);
/* 373 */       switch (eachLineTree.kind()) {
/*     */         case VARIABLE:
/* 375 */           invokeVariableTreeMethod(eachLineTree);
/*     */           break;
/*     */         case EXPRESSION_STATEMENT:
/* 378 */           invokeExpressionStatementTreeMethod(eachLineTree);
/*     */           break;
/*     */         case TRY_STATEMENT:
/* 381 */           tst = (TryStatementTree)eachLineTree;
/* 382 */           btTryStmtTree = tst.block();
/*     */ 
/*     */           
/* 385 */           checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */           
/* 387 */           catches = tst.catches();
/*     */           
/* 389 */           for (iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 390 */             CatchTree ct = catches.get(iCatchCnt);
/* 391 */             BlockTree btCatch = ct.block();
/* 392 */             checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */           } 
/*     */           
/*     */           try {
/* 396 */             BlockTree btTryStmtFinallyTree = tst.finallyBlock();
/* 397 */             log("btTryStmtFinallyTree  - " + btTryStmtFinallyTree.kind().toString());
/*     */ 
/*     */             
/* 400 */             checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */           }
/* 402 */           catch (Exception ex) {
/* 403 */             log(" --- no finally block available exception -- " + ex);
/*     */           } 
/*     */           break;
/*     */ 
/*     */         
/*     */         case FOR_STATEMENT:
/*     */         case FOR_EACH_STATEMENT:
/*     */           break;
/*     */         
/*     */         case IF_STATEMENT:
/* 413 */           invokeIfStmtTreeMethod(eachLineTree);
/*     */           break;
/*     */         case WHILE_STATEMENT:
/* 416 */           wst = (WhileStatementTree)eachLineTree;
/* 417 */           st = wst.statement();
/* 418 */           invokeIfElseStatementTreeMethod(st);
/*     */           break;
/*     */         default:
/* 421 */           log("unexpected kind in switch 444  - " + eachLineTree.kind().toString());
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidsetRPEValueRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */