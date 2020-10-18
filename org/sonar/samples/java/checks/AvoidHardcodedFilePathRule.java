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
/*     */ import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
/*     */ import org.sonar.plugins.java.api.tree.MethodInvocationTree;
/*     */ import org.sonar.plugins.java.api.tree.MethodTree;
/*     */ import org.sonar.plugins.java.api.tree.NewClassTree;
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
/*     */ @Rule(key = "AvoidHardcodedFilePath")
/*     */ public class AvoidHardcodedFilePathRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  61 */   public int max = 2;
/*     */   
/*     */   boolean bLoggingActive = false;
/*     */   
/*  65 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  70 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  77 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  79 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  80 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  82 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  83 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  85 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  91 */     BlockTree blocktree = methodTree.block();
/*  92 */     this.htReportIssue = new Hashtable<>();
/*     */     
/*  94 */     if (blocktree != null) {
/*  95 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/* 102 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/* 103 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(String str) {
/* 108 */     if (this.bLoggingActive);
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
/* 120 */     return this.htReportIssue;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String sFileName, Tree eachLineTree) {
/* 125 */     if (this.htReportIssue.containsKey(sFileName)) {
/*     */       
/* 127 */       reportIssue(eachLineTree, "SOGETI --> Filename contains hardcoding, avoid hardcoded File Paths.");
/*     */       
/* 129 */       return true;
/*     */     } 
/*     */     
/* 132 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/* 137 */     ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree)eachLineTree;
/* 138 */     ExpressionTree expressionTree = expressionStatementTree.expression();
/*     */     
/* 140 */     if (!expressionTree.kind().toString().equals("STRING_LITERAL") && 
/* 141 */       !expressionTree.kind().toString().equals("IDENTIFIER")) {
/* 142 */       if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/* 143 */         String str = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 144 */       } else if (expressionTree.kind().toString().equals("ASSIGNMENT")) {
/* 145 */         AssignmentExpressionTree aet = (AssignmentExpressionTree)expressionTree;
/* 146 */         invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 153 */     TypeCastTree typeCastTree = (TypeCastTree)et;
/* 154 */     ExpressionTree expressionTree = typeCastTree.expression();
/* 155 */     String sRet = "";
/*     */     
/* 157 */     if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/* 158 */       sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/*     */     }
/* 160 */     else if (expressionTree.kind().toString().equals("MEMBER_SELECT")) {
/* 161 */       sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/*     */     }
/* 163 */     else if (expressionTree.kind().toString().equals("IDENTIFIER")) {
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree tree, Tree eachLineTree) {
/* 173 */     ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree)eachLineTree;
/* 174 */     String myVariableType = expressionStatementTree.expression().symbolType().symbol().name();
/*     */     
/* 176 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 177 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 178 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 179 */       String str = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     }
/* 181 */     else if (tree.kind().toString().equals("NEW_CLASS")) {
/* 182 */       NewClassTree newClassTree = (NewClassTree)tree;
/*     */       
/* 184 */       if (myVariableType.equals("File")) {
/* 185 */         String sFileName1 = ((ExpressionTree)newClassTree.arguments().get(0)).firstToken().text();
/* 186 */         String sFileName2 = ((ExpressionTree)newClassTree.arguments().get(0)).lastToken().text();
/*     */ 
/*     */         
/* 189 */         String sFileName = sFileName1.equals(sFileName2) ? sFileName1 : (sFileName1 + " " + sFileName2);
/*     */ 
/*     */         
/* 192 */         if (sFileName.contains("/") || sFileName.contains(".") || sFileName.contains("\"")) {
/* 193 */           bumpUpCount(sFileName);
/* 194 */           bCheckAndReportIssueNow(sFileName, eachLineTree);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 203 */     String sRet = "";
/* 204 */     VariableTree variableTree = (VariableTree)eachLineTree;
/*     */     
/* 206 */     VariableTree myVariableTree = (VariableTree)variableTree.symbol().declaration();
/* 207 */     String sVariableType = variableTree.symbol().type().name();
/*     */     
/* 209 */     VariableTree variableTree1 = myVariableTree;
/*     */     try {
/* 211 */       expressionTree = myVariableTree.initializer();
/* 212 */       log("MYETT  - " + expressionTree.kind().toString());
/* 213 */     } catch (Exception ex) {
/* 214 */       log(" --- inside exception --" + ex);
/* 215 */       if (expressionTree == null) {
/* 216 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/* 219 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 220 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 221 */     } else if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 222 */       sRet = invokeMemberSelectMethod(tree, eachLineTree);
/* 223 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 224 */       sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/* 225 */     } else if (tree.kind().toString().equals("NEW_CLASS")) {
/* 226 */       NewClassTree newClassTree = (NewClassTree)tree;
/*     */       
/* 228 */       if (sVariableType.equals("File")) {
/* 229 */         String sFileName1 = ((ExpressionTree)newClassTree.arguments().get(0)).firstToken().text();
/* 230 */         String sFileName2 = ((ExpressionTree)newClassTree.arguments().get(0)).lastToken().text();
/*     */ 
/*     */         
/* 233 */         String sFileName = sFileName1.equals(sFileName2) ? sFileName1 : (sFileName1 + " " + sFileName2);
/*     */ 
/*     */         
/* 236 */         if (sFileName.contains("/") || sFileName.contains(".") || sFileName.contains("\"")) {
/* 237 */           bumpUpCount(sFileName);
/* 238 */           bCheckAndReportIssueNow(sFileName, eachLineTree);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 248 */     MethodInvocationTree methodInvocationTree = (MethodInvocationTree)mytree;
/* 249 */     ExpressionTree expressionTree = methodInvocationTree.methodSelect();
/*     */     
/* 251 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/*     */     
/* 253 */     String sMyArgs = "";
/*     */     
/* 255 */     if (methodInvocationTree.arguments().size() != 0) {
/*     */       
/* 257 */       Arguments<Tree> arguments = methodInvocationTree.arguments();
/* 258 */       sMyArgs = "(";
/* 259 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/*     */         
/* 261 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 262 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 263 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 264 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 265 */           sRet = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 268 */       sMyArgs = sMyArgs + "--)";
/* 269 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 271 */       sMyArgs = "()";
/*     */     } 
/* 273 */     return sRet + sMyArgs;
/*     */   }
/*     */   
/*     */   private String invokeMemberSelectMethod(Tree ettemp, Tree eachLineTree) {
/* 277 */     String str = "";
/*     */     
/* 279 */     if (ettemp.kind().toString().equals("MEMBER_SELECT")) {
/* 280 */       MemberSelectExpressionTree memberSelectExpressionTree = (MemberSelectExpressionTree)ettemp;
/* 281 */       String sDeclarationMethodName = memberSelectExpressionTree.firstToken().text();
/* 282 */       String sDeclarationCallingMethodName = memberSelectExpressionTree.identifier().name();
/* 283 */       str = sDeclarationMethodName + "." + sDeclarationCallingMethodName;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 288 */     return str;
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 292 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/*     */     
/* 294 */     StatementTree statementTree = forStmtTree.statement();
/* 295 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 296 */       BlockTree blockTree = (BlockTree)forStmtTree.statement();
/* 297 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 298 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 299 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 304 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/*     */     
/* 306 */     StatementTree statementTree = forEachStmt.statement();
/* 307 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 308 */       BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 309 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 310 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 311 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 316 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/* 317 */     ExpressionTree expressionTree = ifStmtTree.condition();
/*     */     
/* 319 */     if ("LOGICAL_COMPLEMENT".equals(expressionTree.kind().toString())) {
/* 320 */       UnaryExpressionTree unaryExpressionTree = (UnaryExpressionTree)expressionTree;
/* 321 */       ExpressionTree newet = unaryExpressionTree.expression();
/*     */       
/* 323 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 324 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 325 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */ 
/*     */       
/*     */       } 
/* 329 */     } else if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 330 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, ifLoopTree);
/*     */     } 
/*     */     
/* 333 */     StatementTree statementTree = ifStmtTree.thenStatement();
/*     */     
/* 335 */     invokeIfElseStatementTreeMethod(statementTree);
/*     */     
/*     */     try {
/* 338 */       while (ifStmtTree.elseStatement() != null) {
/* 339 */         statementTree = ifStmtTree.elseStatement();
/*     */         
/* 341 */         if ("IF_STATEMENT".equals(statementTree.kind().toString())) {
/* 342 */           ifStmtTree = (IfStatementTree)statementTree;
/* 343 */           StatementTree newst = ifStmtTree.thenStatement();
/* 344 */           invokeIfElseStatementTreeMethod(newst); continue;
/*     */         } 
/* 346 */         invokeIfElseStatementTreeMethod(statementTree);
/* 347 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 350 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree statementTree) {
/* 355 */     if (statementTree.kind().toString().equals("BLOCK")) {
/* 356 */       BlockTree blockTree = (BlockTree)statementTree;
/* 357 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */     }
/* 359 */     else if (statementTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 360 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 367 */     Tree eachLineTree = null;
/* 368 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 369 */       eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 371 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 372 */         invokeVariableTreeMethod(eachLineTree);
/* 373 */       } else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 374 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 375 */       } else if (!eachLineTree.kind().toString().equals("METHOD_INVOCATION")) {
/*     */         
/* 377 */         if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 378 */           TryStatementTree tryStatementTree = (TryStatementTree)eachLineTree;
/* 379 */           BlockTree btTryStmtTree = tryStatementTree.block();
/*     */           
/* 381 */           checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */           
/* 383 */           List<? extends CatchTree> catches = tryStatementTree.catches();
/*     */           
/* 385 */           for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 386 */             CatchTree ct = catches.get(iCatchCnt);
/* 387 */             BlockTree btCatch = ct.block();
/* 388 */             checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */           } 
/*     */           
/*     */           try {
/* 392 */             BlockTree btTryStmtFinallyTree = tryStatementTree.finallyBlock();
/*     */             
/* 394 */             checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */           }
/* 396 */           catch (Exception exception) {}
/*     */         }
/* 398 */         else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 399 */           invokeForStmtTreeMethod(eachLineTree);
/* 400 */         } else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 401 */           invokeForEachStmtTreeMethod(eachLineTree);
/* 402 */         } else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 403 */           invokeIfStmtTreeMethod(eachLineTree);
/* 404 */         } else if (eachLineTree.kind().toString().equals("WHILE_STATEMENT")) {
/* 405 */           WhileStatementTree wst = (WhileStatementTree)eachLineTree;
/* 406 */           StatementTree st = wst.statement();
/* 407 */           invokeIfElseStatementTreeMethod(st);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidHardcodedFilePathRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */