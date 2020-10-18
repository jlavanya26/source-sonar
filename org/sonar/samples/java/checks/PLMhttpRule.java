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
/*     */ @Rule(key = "PLMhttp")
/*     */ public class PLMhttpRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  58 */   public int max = 2;
/*     */   boolean bLoggingActive = false;
/*  60 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  64 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  70 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  72 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  73 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  75 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  76 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  78 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  83 */     BlockTree blocktree = methodTree.block();
/*  84 */     Tree.Kind treekind = methodTree.kind();
/*  85 */     log("&&&& - " + treekind.toString() + " --> " + methodTree.simpleName().name());
/*  86 */     this.htReportIssue = new Hashtable<>();
/*     */     
/*  88 */     if (blocktree != null) {
/*  89 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  96 */     log("5");
/*  97 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  98 */       log("6");
/*  99 */       addressEachTree(trees);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void log(String strLog) {
/* 104 */     if (this.bLoggingActive) {
/* 105 */       System.out.println(strLog);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(Exception ex) {
/* 110 */     if (this.bLoggingActive) {
/* 111 */       System.out.println(ex);
/*     */     }
/*     */   }
/*     */   
/*     */   private Hashtable bumpUpCount(String sMethodArgument) {
/* 116 */     if (this.htReportIssue.containsKey(sMethodArgument)) {
/* 117 */       Integer iSize = this.htReportIssue.get(sMethodArgument);
/* 118 */       int iNewSize = iSize.intValue() + 1;
/* 119 */       this.htReportIssue.put(sMethodArgument, Integer.valueOf(iNewSize));
/*     */     } else {
/* 121 */       this.htReportIssue.put(sMethodArgument, Integer.valueOf(1));
/*     */     } 
/* 123 */     log("########################### - bumpUp ht : " + this.htReportIssue);
/* 124 */     return this.htReportIssue;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String sMethodArgument, Tree eachLineTree) {
/* 129 */     if (this.htReportIssue.containsKey(sMethodArgument)) {
/* 130 */       reportIssue(eachLineTree, "SOGETI --> URL is hardcoded");
/* 131 */       return true;
/*     */     } 
/* 133 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/* 138 */     log("inside expr stmt");
/* 139 */     ExpressionStatementTree expressionStmtTree = (ExpressionStatementTree)eachLineTree;
/* 140 */     log("est kind of  - " + expressionStmtTree.kind().toString());
/*     */     
/* 142 */     ExpressionTree expressionTree = expressionStmtTree.expression();
/* 143 */     log("kind expr stmt " + expressionTree.kind().toString());
/* 144 */     if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/* 145 */       String strLog = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 146 */       log("line - " + strLog);
/* 147 */     } else if (expressionTree.kind().toString().equals("ASSIGNMENT")) {
/* 148 */       AssignmentExpressionTree assignmentExpressionTree = (AssignmentExpressionTree)expressionTree;
/* 149 */       invokeAssignmentExpressionStatementTreeMethod((Tree)assignmentExpressionTree.expression(), eachLineTree);
/*     */     } else {
/* 151 */       log("unexpected kind - " + expressionTree.kind().toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree tree, Tree eachLineTree) {
/* 157 */     TypeCastTree typeCastTree = (TypeCastTree)tree;
/* 158 */     ExpressionTree expressionTree = typeCastTree.expression();
/* 159 */     String strLog = "";
/* 160 */     log("inside invokeTypeCastTreeMethod" + expressionTree.kind().toString());
/*     */     
/* 162 */     if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/* 163 */       strLog = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 164 */       log("line - " + strLog);
/* 165 */     } else if (expressionTree.kind().toString().equals("MEMBER_SELECT")) {
/* 166 */       strLog = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 167 */       log("line - " + strLog);
/*     */     } else {
/* 169 */       log("unexpected kind in invokeTypeCastTreeMethod - " + expressionTree.kind().toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree tree, Tree eachLineTree) {
/* 174 */     log("et kind of aest - " + tree.kind().toString());
/*     */     
/* 176 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 177 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 178 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 179 */       String strLog = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/* 180 */       log("line - " + strLog);
/*     */     } else {
/* 182 */       log("unexpected kind in  invokeAssignmentExpressionStatementTreeMethod - " + tree.kind().toString());
/*     */     } 
/*     */   }
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 188 */     String strLog = "";
/* 189 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 190 */     log("*** variable kind *** - " + variableTree.type().kind().toString());
/*     */     
/* 192 */     VariableTree newVariableTree = (VariableTree)variableTree.symbol().declaration();
/* 193 */     String myVariableName = variableTree.toString();
/* 194 */     String myVariableType = variableTree.symbol().type().name();
/* 195 */     VariableTree variableTree1 = newVariableTree;
/*     */     try {
/* 197 */       expressionTree = newVariableTree.initializer();
/* 198 */       log("MYETT  - " + expressionTree.kind().toString());
/* 199 */     } catch (Exception ex) {
/* 200 */       log(" --- inside exception --" + ex);
/* 201 */       if (expressionTree == null) {
/* 202 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/*     */     
/* 206 */     if (tree.kind().toString().equals("STRING_LITERAL")) {
/* 207 */       LiteralTree literalTree = (LiteralTree)tree;
/* 208 */       String strValue = literalTree.value().toString();
/* 209 */       if (strValue.contains("http://") || strValue.contains("https://")) {
/* 210 */         bumpUpCount(strValue);
/* 211 */         bCheckAndReportIssueNow(strValue, eachLineTree);
/*     */       }
/*     */     
/* 214 */     } else if (tree.kind().toString().equals("TYPE_CAST")) {
/* 215 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/*     */     }
/* 217 */     else if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 218 */       strLog = invokeMemberSelectMethod(tree, eachLineTree);
/* 219 */       log("line - " + myVariableType + " " + myVariableName + " = " + strLog + "--args--");
/*     */     }
/* 221 */     else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 222 */       strLog = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/* 223 */       log("line - " + myVariableType + " " + myVariableName + " = " + strLog);
/*     */     } else {
/*     */       
/* 226 */       log("unexpected kind  - " + tree.kind().toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 232 */     MethodInvocationTree methodInvocationTree = (MethodInvocationTree)mytree;
/* 233 */     ExpressionTree expressionTree = methodInvocationTree.methodSelect();
/* 234 */     log("dinakar  - " + expressionTree.kind().toString());
/* 235 */     String strReturn = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 236 */     String strMyArgs = "";
/*     */     
/* 238 */     if (methodInvocationTree.arguments().size() != 0) {
/* 239 */       log("****** in method invocation tree  *********" + methodInvocationTree.arguments().size());
/* 240 */       Arguments<Tree> arguments = methodInvocationTree.arguments();
/*     */       
/* 242 */       strMyArgs = "(";
/* 243 */       String strArgument = null;
/* 244 */       for (int iArgCount = 0; iArgCount < arguments.size(); iArgCount++) {
/* 245 */         log("inside arguments loop 222 - " + ((Tree)arguments.get(iArgCount)).kind().toString());
/*     */         
/* 247 */         strArgument = ((Tree)arguments.get(iArgCount)).firstToken().text();
/*     */         
/* 249 */         if (strArgument.contains("http://") || strArgument.contains("https://")) {
/* 250 */           invokeMemberSelectMethodForHttp((Tree)expressionTree, eachLineTree, strArgument);
/*     */         }
/* 252 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCount)).kind().toString())) {
/* 253 */           TypeCastTree typeCastTree = (TypeCastTree)arguments.get(iArgCount);
/* 254 */           invokeTypeCastTreeMethod((Tree)typeCastTree, eachLineTree);
/* 255 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCount)).kind().toString())) {
/* 256 */           strReturn = invokeMethodInvocationTreeMethod(arguments.get(iArgCount), eachLineTree);
/*     */         } 
/*     */       } 
/* 259 */       strMyArgs = strMyArgs + "--)";
/* 260 */       strMyArgs = strMyArgs.replace(", --", "");
/*     */     } else {
/* 262 */       strMyArgs = "()";
/*     */     } 
/*     */     
/* 265 */     return strReturn + strMyArgs;
/*     */   }
/*     */   
/*     */   private String invokeMemberSelectMethod(Tree tree, Tree eachLineTree) {
/* 269 */     String strReturnString = "";
/*     */     
/* 271 */     if (tree.kind().toString().equals("MEMBER_SELECT")) {
/*     */       
/* 273 */       MemberSelectExpressionTree memberSelect = (MemberSelectExpressionTree)tree;
/* 274 */       String strDeclarationMethodName = memberSelect.firstToken().text();
/* 275 */       String strDeclarationCallingMethodName = memberSelect.identifier().name();
/* 276 */       strReturnString = strDeclarationMethodName + "." + strDeclarationCallingMethodName;
/*     */     } else {
/* 278 */       log("unexpected kind in iinvokeMemberSelectMethod - " + tree.kind().toString());
/*     */     } 
/*     */     
/* 281 */     return strReturnString;
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 285 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 286 */     log("*** inside invokeForStmtTreeMethod kind *** - " + forStmtTree.kind().toString());
/*     */     
/* 288 */     StatementTree statementTree = forStmtTree.statement();
/* 289 */     log("*** et kind *** - " + statementTree.kind().toString());
/* 290 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 291 */       BlockTree blockTree = (BlockTree)forStmtTree.statement();
/* 292 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 293 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 294 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 299 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 300 */     log("*** for each stmt kind *** - " + forEachStmt.kind().toString());
/* 301 */     BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 302 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 307 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */ 
/*     */     
/* 310 */     ExpressionTree expressionTree = ifStmtTree.condition();
/* 311 */     log("*** if stmt condition kind *** - " + expressionTree.kind().toString());
/*     */     
/* 313 */     if ("LOGICAL_COMPLEMENT".equals(expressionTree.kind().toString())) {
/* 314 */       UnaryExpressionTree unaryExpressionTree = (UnaryExpressionTree)expressionTree;
/* 315 */       ExpressionTree newExpressionTree = unaryExpressionTree.expression();
/* 316 */       log("*** logical complement kind *** - " + newExpressionTree.kind().toString());
/*     */       
/* 318 */       if ("METHOD_INVOCATION".equals(newExpressionTree.kind().toString())) {
/* 319 */         invokeMethodInvocationTreeMethod((Tree)newExpressionTree, ifLoopTree);
/*     */       }
/* 321 */     } else if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 322 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, ifLoopTree);
/*     */     } 
/*     */ 
/*     */     
/* 326 */     StatementTree statementTree = ifStmtTree.thenStatement();
/* 327 */     log("*** if stmt 2 kind *** - " + statementTree.kind().toString());
/*     */     
/* 329 */     invokeIfElseStatementTreeMethod(statementTree);
/*     */ 
/*     */     
/*     */     try {
/* 333 */       while (ifStmtTree.elseStatement() != null) {
/* 334 */         statementTree = ifStmtTree.elseStatement();
/* 335 */         log("*** if stmt 222 kind *** - " + statementTree.kind().toString());
/*     */         
/* 337 */         if ("IF_STATEMENT".equals(statementTree.kind().toString())) {
/* 338 */           ifStmtTree = (IfStatementTree)statementTree;
/* 339 */           log("*** if stmt two kind *** - " + ifStmtTree.kind().toString());
/* 340 */           StatementTree newstatementTree = ifStmtTree.thenStatement();
/* 341 */           invokeIfElseStatementTreeMethod(newstatementTree); continue;
/*     */         } 
/* 343 */         log("*** inside else of if fif if ****");
/*     */         
/* 345 */         invokeIfElseStatementTreeMethod(statementTree);
/* 346 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 349 */     } catch (Exception ex) {
/* 350 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree statementTree) {
/* 356 */     if (statementTree.kind().toString().equals("BLOCK")) {
/* 357 */       BlockTree blockTree = (BlockTree)statementTree;
/* 358 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 359 */     } else if (statementTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 360 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } else {
/* 362 */       log("unexpected kind in is stmt tree - " + statementTree.kind().toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 367 */     Tree eachLineTree = null;
/*     */     
/* 369 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 370 */       eachLineTree = listOfTrees.get(iLine);
/* 371 */       log("*** kind inside for loop *** --------href-------- " + eachLineTree.kind().toString());
/* 372 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 373 */         invokeVariableTreeMethod(eachLineTree);
/*     */       }
/* 375 */       else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 376 */         invokeExpressionStatementTreeMethod(eachLineTree);
/*     */       }
/* 378 */       else if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 379 */         TryStatementTree trystatementTree = (TryStatementTree)eachLineTree;
/* 380 */         BlockTree btTryStmtTree = trystatementTree.block();
/*     */ 
/*     */         
/* 383 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */         
/* 385 */         List<? extends CatchTree> catches = trystatementTree.catches();
/*     */         
/* 387 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 388 */           CatchTree catchTree = catches.get(iCatchCnt);
/* 389 */           BlockTree btCatch = catchTree.block();
/* 390 */           checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */         } 
/*     */         
/*     */         try {
/* 394 */           BlockTree btTryStmtFinallyTree = trystatementTree.finallyBlock();
/* 395 */           log("btTryStmtFinallyTree  - " + btTryStmtFinallyTree.kind().toString());
/*     */ 
/*     */           
/* 398 */           checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree
/* 399 */               .body());
/*     */         }
/* 401 */         catch (Exception ex) {
/* 402 */           log(" --- no finally block available exception -- " + ex);
/*     */         } 
/* 404 */       } else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 405 */         invokeForStmtTreeMethod(eachLineTree);
/*     */       }
/* 407 */       else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 408 */         invokeForEachStmtTreeMethod(eachLineTree);
/*     */       }
/* 410 */       else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 411 */         invokeIfStmtTreeMethod(eachLineTree);
/*     */       }
/* 413 */       else if (eachLineTree.kind().toString().equals("WHILE_STATEMENT")) {
/* 414 */         WhileStatementTree whilestatementTree = (WhileStatementTree)eachLineTree;
/* 415 */         StatementTree statementTree = whilestatementTree.statement();
/* 416 */         invokeIfElseStatementTreeMethod(statementTree);
/*     */       } else {
/*     */         
/* 419 */         log("unexpected kind  - " + eachLineTree.kind().toString());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeMemberSelectMethodForHttp(Tree tree, Tree eachLineTree, String strArgument) {
/* 430 */     if (strArgument.contains("http://") || strArgument.contains("https://")) {
/* 431 */       bumpUpCount(strArgument);
/* 432 */       bCheckAndReportIssueNow(strArgument, eachLineTree);
/*     */     }
/*     */     else {
/*     */       
/* 436 */       log("unexpected kind in invokeMemberSelectMethodForHttp - " + tree.kind().toString());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMhttpRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */