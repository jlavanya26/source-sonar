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
/*     */ @Rule(key = "PLMGetRelatedRelationshipAvoidStar")
/*     */ public class PLMGetRelatedRelationshipAvoidStarRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  63 */   public int max = 2;
/*     */   boolean bLoggingActive = false;
/*  65 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  69 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  74 */     MethodTree methodTree = (MethodTree)tree;
/*  75 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  76 */     Type returnType = methodSymbol.returnType().type();
/*  77 */     BlockTree blocktree = methodTree.block();
/*  78 */     Tree.Kind treekind = methodTree.kind();
/*  79 */     this.htReportIssue = new Hashtable<>();
/*     */     
/*  81 */     if (blocktree != null) {
/*  82 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  87 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  88 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(String str) {
/*  93 */     if (this.bLoggingActive);
/*     */   }
/*     */ 
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
/*     */   }
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String sMethodName, Tree eachLineTree) {
/* 110 */     if (this.htReportIssue.size() > 0);
/*     */     
/* 112 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 113 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 114 */       if (iSize.intValue() > 0)
/* 115 */         reportIssue(eachLineTree, "Sogeti Selectable  Rule : Avoid using * as argument in findObjects/getRelatedObjects/getRelatedObject"); 
/* 116 */       return true;
/*     */     } 
/* 118 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/* 123 */     ExpressionStatementTree expressionTreeStmt = (ExpressionStatementTree)eachLineTree;
/* 124 */     ExpressionTree expressionTree = expressionTreeStmt.expression();
/* 125 */     AssignmentExpressionTree assignmentExpTree = null;
/* 126 */     LiteralTree objLiteralTree = null;
/* 127 */     IdentifierTree objIdentifierTree = null;
/* 128 */     String sResult = null;
/*     */     
/* 130 */     if (expressionTree.kind().toString().equals("STRING_LITERAL")) {
/* 131 */       objLiteralTree = (LiteralTree)expressionTree;
/* 132 */     } else if (expressionTree.kind().toString().equals("IDENTIFIER")) {
/* 133 */       objIdentifierTree = (IdentifierTree)expressionTree;
/* 134 */     } else if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/* 135 */       sResult = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 136 */     } else if (expressionTree.kind().toString().equals("ASSIGNMENT")) {
/* 137 */       assignmentExpTree = (AssignmentExpressionTree)expressionTree;
/* 138 */       invokeAssignmentExpressionStatementTreeMethod((Tree)assignmentExpTree.expression(), eachLineTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree tree, Tree eachLineTree) {
/* 145 */     TypeCastTree objTypeCastTree = (TypeCastTree)tree;
/* 146 */     ExpressionTree expTree = objTypeCastTree.expression();
/* 147 */     String sResult = null;
/*     */     
/* 149 */     if (expTree.kind().toString().equals("METHOD_INVOCATION")) {
/* 150 */       sResult = invokeMethodInvocationTreeMethod((Tree)expTree, eachLineTree);
/* 151 */     } else if (expTree.kind().toString().equals("MEMBER_SELECT")) {
/* 152 */       sResult = invokeMemberSelectMethod((Tree)expTree, eachLineTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree tree, Tree eachLineTree) {
/* 159 */     String sResult = null;
/* 160 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 161 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 162 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 163 */       sResult = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 170 */     String sResult = null;
/* 171 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 172 */     VariableTree varTree = (VariableTree)variableTree.symbol().declaration();
/* 173 */     VariableTree variableTree1 = varTree;
/*     */     try {
/* 175 */       expressionTree = varTree.initializer();
/* 176 */       log("MYETT  - " + expressionTree.kind().toString());
/* 177 */     } catch (Exception ex) {
/* 178 */       if (expressionTree == null) {
/* 179 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/*     */     
/* 183 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 184 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 185 */     } else if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 186 */       sResult = invokeMemberSelectMethod(tree, eachLineTree);
/* 187 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 188 */       sResult = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMemberSelectMethod(Tree tempTree, Tree eachLineTree, Tree Mit) {
/* 201 */     String sMethodName = null;
/* 202 */     MemberSelectExpressionTree mSelExpTree = null;
/* 203 */     String mDeclarationMethodName = null;
/* 204 */     String mDeclarationCallingMethodName = null;
/* 205 */     String sInvokedMethodName = null;
/* 206 */     MethodInvocationTree mInvocTree = null;
/*     */     
/* 208 */     if (tempTree.kind().toString().equals("MEMBER_SELECT")) {
/* 209 */       mSelExpTree = (MemberSelectExpressionTree)tempTree;
/* 210 */       mDeclarationMethodName = mSelExpTree.firstToken().text();
/* 211 */       mDeclarationCallingMethodName = mSelExpTree.identifier().name();
/* 212 */       sMethodName = mDeclarationMethodName + "." + mDeclarationCallingMethodName;
/* 213 */       mInvocTree = (MethodInvocationTree)Mit;
/*     */       
/* 215 */       if (mDeclarationCallingMethodName.equals("getRelatedObjects")) {
/*     */         
/* 217 */         Arguments<Tree> arguments = mInvocTree.arguments();
/* 218 */         if (mInvocTree.arguments().size() > 1) {
/* 219 */           for (int iArgs = 0; iArgs < arguments.size(); iArgs++) {
/* 220 */             if (((Tree)arguments.get(iArgs)).firstToken().text().equalsIgnoreCase("\"*\"")) {
/* 221 */               reportIssue(eachLineTree, "Sogeti Selectable  Rule : Avoid using * as argument in APIs."); break;
/*     */             } 
/* 223 */             if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 224 */               sInvokedMethodName = invokeMethodInvocationTreeMethod(arguments.get(iArgs), eachLineTree);
/*     */             }
/*     */           } 
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 231 */       if (mDeclarationCallingMethodName.equals("getRelatedObject")) {
/*     */         
/* 233 */         Arguments<Tree> arguments = mInvocTree.arguments();
/* 234 */         if (mInvocTree.arguments().size() > 1) {
/* 235 */           for (int iArgs = 0; iArgs < arguments.size(); iArgs++) {
/* 236 */             if (((Tree)arguments.get(iArgs)).firstToken().text().equalsIgnoreCase("\"*\"")) {
/* 237 */               reportIssue(eachLineTree, "Sogeti Selectable  Rule : Avoid using * as argument in getRelatedObject APIs."); break;
/*     */             } 
/* 239 */             if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 240 */               sInvokedMethodName = invokeMethodInvocationTreeMethod(arguments.get(iArgs), eachLineTree);
/*     */             }
/*     */           } 
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 247 */       if (mDeclarationCallingMethodName.equals("findObjects")) {
/*     */         
/* 249 */         Arguments<Tree> arguments = mInvocTree.arguments();
/* 250 */         if (mInvocTree.arguments().size() > 1) {
/* 251 */           for (int iArgs = 0; iArgs < arguments.size(); iArgs++) {
/* 252 */             if (((Tree)arguments.get(iArgs)).firstToken().text().equalsIgnoreCase("\"*\"")) {
/* 253 */               reportIssue(eachLineTree, "Sogeti Selectable  Rule : Avoid using * as argument in findObjects APIs."); break;
/*     */             } 
/* 255 */             if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 256 */               sInvokedMethodName = invokeMethodInvocationTreeMethod(arguments.get(iArgs), eachLineTree);
/*     */             }
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 267 */     return sMethodName;
/*     */   }
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 271 */     String sMyArgs = null;
/* 272 */     TypeCastTree objTypeCastTree = null;
/*     */     
/* 274 */     MethodInvocationTree objMethodInvocTree = (MethodInvocationTree)mytree;
/* 275 */     ExpressionTree expressionTree = objMethodInvocTree.methodSelect();
/* 276 */     String sResult = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree, (Tree)objMethodInvocTree);
/* 277 */     if (objMethodInvocTree.arguments().size() != 0) {
/* 278 */       Arguments<Tree> arguments = objMethodInvocTree.arguments();
/*     */       
/* 280 */       sMyArgs = "(";
/* 281 */       for (int iArgs = 0; iArgs < arguments.size(); iArgs++) {
/* 282 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 283 */           objTypeCastTree = (TypeCastTree)arguments.get(iArgs);
/* 284 */           invokeTypeCastTreeMethod((Tree)objTypeCastTree, eachLineTree);
/* 285 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 286 */           sResult = invokeMethodInvocationTreeMethod(arguments.get(iArgs), eachLineTree);
/*     */         } 
/*     */       } 
/* 289 */       sMyArgs = sMyArgs + "--)";
/* 290 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 292 */       sMyArgs = "()";
/*     */     } 
/*     */     
/* 295 */     return sResult + sMyArgs;
/*     */   }
/*     */   
/*     */   private String invokeMemberSelectMethod(Tree tempTree, Tree eachLineTree) {
/* 299 */     String sMethodName = null;
/* 300 */     String sDeclarationMethodName = null;
/* 301 */     String sDeclarationCallingMethodName = null;
/* 302 */     MemberSelectExpressionTree memberSelect = null;
/*     */     
/* 304 */     if (tempTree.kind().toString().equals("MEMBER_SELECT")) {
/* 305 */       memberSelect = (MemberSelectExpressionTree)tempTree;
/* 306 */       sDeclarationMethodName = memberSelect.firstToken().text();
/* 307 */       sDeclarationCallingMethodName = memberSelect.identifier().name();
/* 308 */       sMethodName = sDeclarationMethodName + "." + sDeclarationCallingMethodName;
/*     */     } 
/*     */ 
/*     */     
/* 312 */     return sMethodName;
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 316 */     BlockTree blockTree = null;
/* 317 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 318 */     StatementTree stmtTree = forStmtTree.statement();
/* 319 */     if ("BLOCK".equals(stmtTree.kind().toString())) {
/* 320 */       blockTree = (BlockTree)forStmtTree.statement();
/* 321 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 322 */     } else if ("EXPRESSION_STATEMENT".equals(stmtTree.kind().toString())) {
/* 323 */       invokeExpressionStatementTreeMethod((Tree)stmtTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 328 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 329 */     BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 330 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */   }
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 334 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/* 335 */     ExpressionTree expTree = ifStmtTree.condition();
/*     */     
/* 337 */     if ("LOGICAL_COMPLEMENT".equals(expTree.kind().toString())) {
/* 338 */       UnaryExpressionTree uet = (UnaryExpressionTree)expTree;
/* 339 */       ExpressionTree newet = uet.expression();
/*     */       
/* 341 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 342 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 343 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */       
/*     */       } 
/* 346 */     } else if ("METHOD_INVOCATION".equals(expTree.kind().toString())) {
/* 347 */       invokeMethodInvocationTreeMethod((Tree)expTree, ifLoopTree);
/*     */     } 
/* 349 */     StatementTree stmtTree = ifStmtTree.thenStatement();
/* 350 */     invokeIfElseStatementTreeMethod(stmtTree);
/*     */     
/*     */     try {
/* 353 */       while (ifStmtTree.elseStatement() != null) {
/* 354 */         stmtTree = ifStmtTree.elseStatement();
/* 355 */         if ("IF_STATEMENT".equals(stmtTree.kind().toString())) {
/* 356 */           ifStmtTree = (IfStatementTree)stmtTree;
/* 357 */           StatementTree newst = ifStmtTree.thenStatement();
/* 358 */           invokeIfElseStatementTreeMethod(newst); continue;
/*     */         } 
/* 360 */         invokeIfElseStatementTreeMethod(stmtTree);
/* 361 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 364 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree stmtTree) {
/* 369 */     if (stmtTree.kind().toString().equals("BLOCK")) {
/* 370 */       BlockTree bt = (BlockTree)stmtTree;
/* 371 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 372 */     } else if (stmtTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 373 */       invokeExpressionStatementTreeMethod((Tree)stmtTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 378 */     Tree eachLineTree = null;
/* 379 */     TryStatementTree objTryStmtTree = null;
/* 380 */     BlockTree btTryStmtTree = null;
/* 381 */     CatchTree catchTree = null;
/* 382 */     BlockTree btCatch = null;
/* 383 */     BlockTree btTryStmtFinallyTree = null;
/* 384 */     WhileStatementTree whileStmtTree = null;
/* 385 */     StatementTree stmtTree = null;
/* 386 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 387 */       eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 389 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 390 */         invokeVariableTreeMethod(eachLineTree);
/* 391 */       } else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 392 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 393 */       } else if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 394 */         objTryStmtTree = (TryStatementTree)eachLineTree;
/* 395 */         btTryStmtTree = objTryStmtTree.block();
/* 396 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */         
/* 398 */         List<? extends CatchTree> catches = objTryStmtTree.catches();
/*     */         
/* 400 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 401 */           catchTree = catches.get(iCatchCnt);
/* 402 */           btCatch = catchTree.block();
/* 403 */           checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */         } 
/*     */         try {
/* 406 */           btTryStmtFinallyTree = objTryStmtTree.finallyBlock();
/* 407 */           checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */         }
/* 409 */         catch (Exception exception) {}
/*     */       }
/* 411 */       else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 412 */         invokeForStmtTreeMethod(eachLineTree);
/* 413 */       } else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 414 */         invokeForEachStmtTreeMethod(eachLineTree);
/* 415 */       } else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 416 */         invokeIfStmtTreeMethod(eachLineTree);
/* 417 */       } else if (eachLineTree.kind().toString().equals("WHILE_STATEMENT")) {
/* 418 */         whileStmtTree = (WhileStatementTree)eachLineTree;
/* 419 */         stmtTree = whileStmtTree.statement();
/* 420 */         invokeIfElseStatementTreeMethod(stmtTree);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMGetRelatedRelationshipAvoidStarRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */