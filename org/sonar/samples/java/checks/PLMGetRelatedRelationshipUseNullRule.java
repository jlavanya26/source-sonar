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
/*     */ 
/*     */ @Rule(key = "PLMGetRelatedRelationshipUseNull")
/*     */ public class PLMGetRelatedRelationshipUseNullRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  64 */   public int max = 2;
/*     */   boolean bLoggingActive = false;
/*  66 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  70 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  75 */     MethodTree methodTree = (MethodTree)tree;
/*  76 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  77 */     Type returnType = methodSymbol.returnType().type();
/*  78 */     BlockTree blocktree = methodTree.block();
/*  79 */     Tree.Kind treekind = methodTree.kind();
/*  80 */     this.htReportIssue = new Hashtable<>();
/*     */     
/*  82 */     if (blocktree != null) {
/*  83 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  88 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  89 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(String str) {
/*  94 */     if (this.bLoggingActive);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Hashtable bumpUpCount(String sMethodName) {
/* 100 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 101 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 102 */       int iNewSize = iSize.intValue() + 1;
/* 103 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 105 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/* 107 */     return this.htReportIssue;
/*     */   }
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String sMethodName, Tree eachLineTree) {
/* 111 */     if (this.htReportIssue.size() > 0);
/*     */     
/* 113 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 114 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 115 */       if (iSize.intValue() > 0)
/* 116 */         reportIssue(eachLineTree, "Sogeti Non compliance Code : Use Null instead of new StringList value getRelatedObjects/expandSelect API."); 
/* 117 */       return true;
/*     */     } 
/* 119 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/* 124 */     ExpressionStatementTree expressionTreeStmt = (ExpressionStatementTree)eachLineTree;
/* 125 */     ExpressionTree expressionTree = expressionTreeStmt.expression();
/* 126 */     AssignmentExpressionTree assignmentExpTree = null;
/* 127 */     LiteralTree objLiteralTree = null;
/* 128 */     IdentifierTree objIdentifierTree = null;
/* 129 */     String sResult = null;
/*     */     
/* 131 */     if (expressionTree.kind().toString().equals("STRING_LITERAL")) {
/* 132 */       objLiteralTree = (LiteralTree)expressionTree;
/* 133 */     } else if (expressionTree.kind().toString().equals("IDENTIFIER")) {
/* 134 */       objIdentifierTree = (IdentifierTree)expressionTree;
/* 135 */     } else if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/* 136 */       sResult = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 137 */     } else if (expressionTree.kind().toString().equals("ASSIGNMENT")) {
/* 138 */       assignmentExpTree = (AssignmentExpressionTree)expressionTree;
/* 139 */       invokeAssignmentExpressionStatementTreeMethod((Tree)assignmentExpTree.expression(), eachLineTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree tree, Tree eachLineTree) {
/* 146 */     TypeCastTree objTypeCastTree = (TypeCastTree)tree;
/* 147 */     ExpressionTree expTree = objTypeCastTree.expression();
/* 148 */     String sResult = null;
/*     */     
/* 150 */     if (expTree.kind().toString().equals("METHOD_INVOCATION")) {
/* 151 */       sResult = invokeMethodInvocationTreeMethod((Tree)expTree, eachLineTree);
/* 152 */     } else if (expTree.kind().toString().equals("MEMBER_SELECT")) {
/* 153 */       sResult = invokeMemberSelectMethod((Tree)expTree, eachLineTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree tree, Tree eachLineTree) {
/* 160 */     String sResult = null;
/* 161 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 162 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 163 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 164 */       sResult = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 171 */     String sResult = null;
/* 172 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 173 */     VariableTree varTree = (VariableTree)variableTree.symbol().declaration();
/* 174 */     VariableTree variableTree1 = varTree;
/*     */     try {
/* 176 */       expressionTree = varTree.initializer();
/* 177 */       log("MYETT  - " + expressionTree.kind().toString());
/* 178 */     } catch (Exception ex) {
/* 179 */       if (expressionTree == null) {
/* 180 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/*     */     
/* 184 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 185 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 186 */     } else if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 187 */       sResult = invokeMemberSelectMethod(tree, eachLineTree);
/* 188 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 189 */       sResult = invokeMethodInvocationTreeMethod(tree, eachLineTree);
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
/* 202 */     String sMethodName = null;
/* 203 */     String sMethodInvocationResult = null;
/* 204 */     String sDeclarationCallingMethodName = null;
/* 205 */     String sDeclarationMethodName = null;
/* 206 */     MethodInvocationTree mInvocTree = null;
/* 207 */     MemberSelectExpressionTree mSelExpTree = null;
/* 208 */     NewClassTree classTree = null;
/*     */     
/* 210 */     if (tempTree.kind().toString().equals("MEMBER_SELECT")) {
/* 211 */       mSelExpTree = (MemberSelectExpressionTree)tempTree;
/* 212 */       sDeclarationMethodName = mSelExpTree.firstToken().text();
/* 213 */       sDeclarationCallingMethodName = mSelExpTree.identifier().name();
/* 214 */       sMethodName = sDeclarationMethodName + "." + sDeclarationCallingMethodName;
/* 215 */       mInvocTree = (MethodInvocationTree)Mit;
/* 216 */       if (sDeclarationCallingMethodName.equals("getRelatedObjects") || sDeclarationCallingMethodName.equals("expandSelect") || sDeclarationCallingMethodName.equals("getRelatedObject")) {
/* 217 */         Arguments<Tree> arguments = mInvocTree.arguments();
/* 218 */         if (mInvocTree.arguments().size() > 1) {
/* 219 */           for (int iArgs = 0; iArgs < arguments.size(); iArgs++) {
/* 220 */             if (((Tree)arguments.get(iArgs)).kind().toString().equalsIgnoreCase("NEW_CLASS")) {
/* 221 */               classTree = (NewClassTree)arguments.get(iArgs);
/* 222 */               if (classTree.arguments().size() < 1) {
/* 223 */                 reportIssue(eachLineTree, "Sogeti Non compliance Code : Use Null instead of new StringList value getRelatedObjects/getRelatedObject/expandSelect API.");
/*     */                 break;
/*     */               } 
/* 226 */             } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 227 */               sMethodInvocationResult = invokeMethodInvocationTreeMethod(arguments.get(iArgs), eachLineTree);
/*     */             } 
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 235 */     return sMethodName;
/*     */   }
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 239 */     String sMyArgs = null;
/* 240 */     TypeCastTree objTypeCastTree = null;
/*     */     
/* 242 */     MethodInvocationTree objMethodInvocTree = (MethodInvocationTree)mytree;
/* 243 */     ExpressionTree expressionTree = objMethodInvocTree.methodSelect();
/* 244 */     String sResult = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree, (Tree)objMethodInvocTree);
/* 245 */     if (objMethodInvocTree.arguments().size() != 0) {
/* 246 */       Arguments<Tree> arguments = objMethodInvocTree.arguments();
/*     */       
/* 248 */       sMyArgs = "(";
/* 249 */       for (int iArgs = 0; iArgs < arguments.size(); iArgs++) {
/* 250 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 251 */           objTypeCastTree = (TypeCastTree)arguments.get(iArgs);
/* 252 */           invokeTypeCastTreeMethod((Tree)objTypeCastTree, eachLineTree);
/* 253 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 254 */           sResult = invokeMethodInvocationTreeMethod(arguments.get(iArgs), eachLineTree);
/*     */         } 
/*     */       } 
/* 257 */       sMyArgs = sMyArgs + "--)";
/* 258 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 260 */       sMyArgs = "()";
/*     */     } 
/*     */     
/* 263 */     return sResult + sMyArgs;
/*     */   }
/*     */   
/*     */   private String invokeMemberSelectMethod(Tree tempTree, Tree eachLineTree) {
/* 267 */     String sMethodName = null;
/* 268 */     String sDeclarationMethodName = null;
/* 269 */     String sDeclarationCallingMethodName = null;
/* 270 */     MemberSelectExpressionTree memberSelect = null;
/*     */     
/* 272 */     if (tempTree.kind().toString().equals("MEMBER_SELECT")) {
/* 273 */       memberSelect = (MemberSelectExpressionTree)tempTree;
/* 274 */       sDeclarationMethodName = memberSelect.firstToken().text();
/* 275 */       sDeclarationCallingMethodName = memberSelect.identifier().name();
/* 276 */       sMethodName = sDeclarationMethodName + "." + sDeclarationCallingMethodName;
/*     */     } 
/*     */ 
/*     */     
/* 280 */     return sMethodName;
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 284 */     BlockTree blockTree = null;
/* 285 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 286 */     StatementTree stmtTree = forStmtTree.statement();
/* 287 */     if ("BLOCK".equals(stmtTree.kind().toString())) {
/* 288 */       blockTree = (BlockTree)forStmtTree.statement();
/* 289 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 290 */     } else if ("EXPRESSION_STATEMENT".equals(stmtTree.kind().toString())) {
/* 291 */       invokeExpressionStatementTreeMethod((Tree)stmtTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 296 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 297 */     BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 298 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */   }
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 302 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/* 303 */     ExpressionTree expTree = ifStmtTree.condition();
/*     */     
/* 305 */     if ("LOGICAL_COMPLEMENT".equals(expTree.kind().toString())) {
/* 306 */       UnaryExpressionTree uet = (UnaryExpressionTree)expTree;
/* 307 */       ExpressionTree newet = uet.expression();
/*     */       
/* 309 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 310 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 311 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */       
/*     */       } 
/* 314 */     } else if ("METHOD_INVOCATION".equals(expTree.kind().toString())) {
/* 315 */       invokeMethodInvocationTreeMethod((Tree)expTree, ifLoopTree);
/*     */     } 
/* 317 */     StatementTree stmtTree = ifStmtTree.thenStatement();
/* 318 */     invokeIfElseStatementTreeMethod(stmtTree);
/*     */     
/*     */     try {
/* 321 */       while (ifStmtTree.elseStatement() != null) {
/* 322 */         stmtTree = ifStmtTree.elseStatement();
/* 323 */         if ("IF_STATEMENT".equals(stmtTree.kind().toString())) {
/* 324 */           ifStmtTree = (IfStatementTree)stmtTree;
/* 325 */           StatementTree newst = ifStmtTree.thenStatement();
/* 326 */           invokeIfElseStatementTreeMethod(newst); continue;
/*     */         } 
/* 328 */         invokeIfElseStatementTreeMethod(stmtTree);
/* 329 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 332 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree stmtTree) {
/* 337 */     if (stmtTree.kind().toString().equals("BLOCK")) {
/* 338 */       BlockTree bt = (BlockTree)stmtTree;
/* 339 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 340 */     } else if (stmtTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 341 */       invokeExpressionStatementTreeMethod((Tree)stmtTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 346 */     Tree eachLineTree = null;
/* 347 */     TryStatementTree objTryStmtTree = null;
/* 348 */     BlockTree btTryStmtTree = null;
/* 349 */     CatchTree catchTree = null;
/* 350 */     BlockTree btCatch = null;
/* 351 */     BlockTree btTryStmtFinallyTree = null;
/* 352 */     WhileStatementTree whileStmtTree = null;
/* 353 */     StatementTree stmtTree = null;
/* 354 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 355 */       eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 357 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 358 */         invokeVariableTreeMethod(eachLineTree);
/* 359 */       } else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 360 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 361 */       } else if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 362 */         objTryStmtTree = (TryStatementTree)eachLineTree;
/* 363 */         btTryStmtTree = objTryStmtTree.block();
/* 364 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */         
/* 366 */         List<? extends CatchTree> catches = objTryStmtTree.catches();
/*     */         
/* 368 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 369 */           catchTree = catches.get(iCatchCnt);
/* 370 */           btCatch = catchTree.block();
/* 371 */           checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */         } 
/*     */         try {
/* 374 */           btTryStmtFinallyTree = objTryStmtTree.finallyBlock();
/* 375 */           checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */         }
/* 377 */         catch (Exception exception) {}
/*     */       }
/* 379 */       else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 380 */         invokeForStmtTreeMethod(eachLineTree);
/* 381 */       } else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 382 */         invokeForEachStmtTreeMethod(eachLineTree);
/* 383 */       } else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 384 */         invokeIfStmtTreeMethod(eachLineTree);
/* 385 */       } else if (eachLineTree.kind().toString().equals("WHILE_STATEMENT")) {
/* 386 */         whileStmtTree = (WhileStatementTree)eachLineTree;
/* 387 */         stmtTree = whileStmtTree.statement();
/* 388 */         invokeIfElseStatementTreeMethod(stmtTree);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMGetRelatedRelationshipUseNullRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */