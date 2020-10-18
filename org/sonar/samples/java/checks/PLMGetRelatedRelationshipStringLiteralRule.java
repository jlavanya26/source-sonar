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
/*     */ @Rule(key = "PLMGetRelatedRelationshipStringLiteral")
/*     */ public class PLMGetRelatedRelationshipStringLiteralRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  62 */   public int max = 2;
/*     */   boolean bLoggingActive = false;
/*  64 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  68 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  73 */     MethodTree methodTree = (MethodTree)tree;
/*  74 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  75 */     Type returnType = methodSymbol.returnType().type();
/*  76 */     BlockTree blocktree = methodTree.block();
/*  77 */     Tree.Kind treekind = methodTree.kind();
/*  78 */     this.htReportIssue = new Hashtable<>();
/*     */     
/*  80 */     if (blocktree != null) {
/*  81 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  86 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  87 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(String str) {
/*  92 */     if (this.bLoggingActive);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Hashtable bumpUpCount(String sMethodName) {
/*  98 */     if (this.htReportIssue.containsKey(sMethodName)) {
/*  99 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 100 */       int iNewSize = iSize.intValue() + 1;
/* 101 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 103 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/* 105 */     return this.htReportIssue;
/*     */   }
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String sMethodName, Tree eachLineTree) {
/* 109 */     if (this.htReportIssue.size() > 0);
/*     */     
/* 111 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 112 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 113 */       if (iSize.intValue() > 0)
/* 114 */         reportIssue(eachLineTree, "Sogeti Selectable  Rule : Hardcoded Arguments should be avoided for getRelatedObjects/getRelatedObject/findObjects API."); 
/* 115 */       return true;
/*     */     } 
/* 117 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/* 122 */     ExpressionStatementTree expressionTreeStmt = (ExpressionStatementTree)eachLineTree;
/* 123 */     ExpressionTree expressionTree = expressionTreeStmt.expression();
/* 124 */     AssignmentExpressionTree assignmentExpTree = null;
/* 125 */     LiteralTree objLiteralTree = null;
/* 126 */     IdentifierTree objIdentifierTree = null;
/* 127 */     String sResult = null;
/*     */     
/* 129 */     if (expressionTree.kind().toString().equals("STRING_LITERAL")) {
/* 130 */       objLiteralTree = (LiteralTree)expressionTree;
/* 131 */     } else if (expressionTree.kind().toString().equals("IDENTIFIER")) {
/* 132 */       objIdentifierTree = (IdentifierTree)expressionTree;
/* 133 */     } else if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/* 134 */       sResult = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 135 */     } else if (expressionTree.kind().toString().equals("ASSIGNMENT")) {
/* 136 */       assignmentExpTree = (AssignmentExpressionTree)expressionTree;
/* 137 */       invokeAssignmentExpressionStatementTreeMethod((Tree)assignmentExpTree.expression(), eachLineTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree tree, Tree eachLineTree) {
/* 144 */     TypeCastTree objTypeCastTree = (TypeCastTree)tree;
/* 145 */     ExpressionTree expTree = objTypeCastTree.expression();
/* 146 */     String sResult = null;
/*     */     
/* 148 */     if (expTree.kind().toString().equals("METHOD_INVOCATION")) {
/* 149 */       sResult = invokeMethodInvocationTreeMethod((Tree)expTree, eachLineTree);
/* 150 */     } else if (expTree.kind().toString().equals("MEMBER_SELECT")) {
/* 151 */       sResult = invokeMemberSelectMethod((Tree)expTree, eachLineTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree tree, Tree eachLineTree) {
/* 158 */     String sResult = null;
/* 159 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 160 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 161 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 162 */       sResult = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 169 */     String sResult = null;
/* 170 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 171 */     VariableTree varTree = (VariableTree)variableTree.symbol().declaration();
/* 172 */     VariableTree variableTree1 = varTree;
/*     */     try {
/* 174 */       expressionTree = varTree.initializer();
/* 175 */       log("MYETT  - " + expressionTree.kind().toString());
/* 176 */     } catch (Exception ex) {
/* 177 */       if (expressionTree == null) {
/* 178 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/*     */     
/* 182 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 183 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 184 */     } else if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 185 */       sResult = invokeMemberSelectMethod(tree, eachLineTree);
/* 186 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 187 */       sResult = invokeMethodInvocationTreeMethod(tree, eachLineTree);
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
/* 200 */     String sMethodName = null;
/* 201 */     String sInvokedMethodName = null;
/* 202 */     String mDeclarationCallingMethodName = null;
/* 203 */     String mDeclarationMethodName = null;
/* 204 */     MethodInvocationTree mInvocationTree = null;
/* 205 */     MemberSelectExpressionTree mSelExpTree = null;
/*     */     
/* 207 */     if (tempTree.kind().toString().equals("MEMBER_SELECT")) {
/* 208 */       mSelExpTree = (MemberSelectExpressionTree)tempTree;
/* 209 */       mDeclarationMethodName = mSelExpTree.firstToken().text();
/* 210 */       mDeclarationCallingMethodName = mSelExpTree.identifier().name();
/* 211 */       sMethodName = mDeclarationMethodName + "." + mDeclarationCallingMethodName;
/* 212 */       mInvocationTree = (MethodInvocationTree)Mit;
/*     */       
/* 214 */       if (mDeclarationCallingMethodName.equals("getRelatedObjects")) {
/* 215 */         Arguments<Tree> arguments = mInvocationTree.arguments();
/* 216 */         if (mInvocationTree.arguments().size() > 1) {
/* 217 */           for (int iArgs = 0; iArgs < arguments.size(); iArgs++) {
/* 218 */             if (((Tree)arguments.get(iArgs)).kind().toString().equalsIgnoreCase("STRING_LITERAL")) {
/* 219 */               reportIssue(eachLineTree, "Sogeti Selectable  Rule : Hardcoded Arguments should be avoided in getRelatedObjects APIs."); break;
/*     */             } 
/* 221 */             if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 222 */               sInvokedMethodName = invokeMethodInvocationTreeMethod(arguments.get(iArgs), eachLineTree);
/*     */             }
/*     */           } 
/*     */         }
/*     */       } 
/* 227 */       if (mDeclarationCallingMethodName.equals("getRelatedObject")) {
/* 228 */         Arguments<Tree> arguments = mInvocationTree.arguments();
/* 229 */         if (mInvocationTree.arguments().size() > 1) {
/* 230 */           for (int iArgs = 0; iArgs < arguments.size(); iArgs++) {
/* 231 */             if (((Tree)arguments.get(iArgs)).kind().toString().equalsIgnoreCase("STRING_LITERAL")) {
/* 232 */               reportIssue(eachLineTree, "Sogeti Selectable  Rule : Hardcoded Arguments should be avoided in getRelatedObject APIs."); break;
/*     */             } 
/* 234 */             if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 235 */               sInvokedMethodName = invokeMethodInvocationTreeMethod(arguments.get(iArgs), eachLineTree);
/*     */             }
/*     */           } 
/*     */         }
/*     */       } 
/* 240 */       if (mDeclarationCallingMethodName.equals("findObjects")) {
/* 241 */         Arguments<Tree> arguments = mInvocationTree.arguments();
/* 242 */         if (mInvocationTree.arguments().size() > 1) {
/* 243 */           log("****** in mit  *********" + mInvocationTree.arguments().size());
/* 244 */           for (int iArgs = 0; iArgs < arguments.size(); iArgs++) {
/* 245 */             log("Selectable arguments - " + ((Tree)arguments.get(iArgs)).kind().toString());
/* 246 */             if (((Tree)arguments.get(iArgs)).kind().toString().equalsIgnoreCase("STRING_LITERAL")) {
/* 247 */               reportIssue(eachLineTree, "Sogeti Selectable  Rule : Hardcoded Arguments should be avoided in findObjects APIs."); break;
/*     */             } 
/* 249 */             if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 250 */               sInvokedMethodName = invokeMethodInvocationTreeMethod(arguments.get(iArgs), eachLineTree);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 259 */     return sMethodName;
/*     */   }
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 263 */     String sMyArgs = null;
/* 264 */     TypeCastTree objTypeCastTree = null;
/*     */     
/* 266 */     MethodInvocationTree objMethodInvocTree = (MethodInvocationTree)mytree;
/* 267 */     ExpressionTree expressionTree = objMethodInvocTree.methodSelect();
/* 268 */     String sResult = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree, (Tree)objMethodInvocTree);
/* 269 */     if (objMethodInvocTree.arguments().size() != 0) {
/* 270 */       Arguments<Tree> arguments = objMethodInvocTree.arguments();
/*     */       
/* 272 */       sMyArgs = "(";
/* 273 */       for (int iArgs = 0; iArgs < arguments.size(); iArgs++) {
/* 274 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 275 */           objTypeCastTree = (TypeCastTree)arguments.get(iArgs);
/* 276 */           invokeTypeCastTreeMethod((Tree)objTypeCastTree, eachLineTree);
/* 277 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 278 */           sResult = invokeMethodInvocationTreeMethod(arguments.get(iArgs), eachLineTree);
/*     */         } 
/*     */       } 
/* 281 */       sMyArgs = sMyArgs + "--)";
/* 282 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 284 */       sMyArgs = "()";
/*     */     } 
/*     */     
/* 287 */     return sResult + sMyArgs;
/*     */   }
/*     */   
/*     */   private String invokeMemberSelectMethod(Tree tempTree, Tree eachLineTree) {
/* 291 */     String sMethodName = null;
/* 292 */     String sDeclarationMethodName = null;
/* 293 */     String sDeclarationCallingMethodName = null;
/* 294 */     MemberSelectExpressionTree memberSelect = null;
/*     */     
/* 296 */     if (tempTree.kind().toString().equals("MEMBER_SELECT")) {
/* 297 */       memberSelect = (MemberSelectExpressionTree)tempTree;
/* 298 */       sDeclarationMethodName = memberSelect.firstToken().text();
/* 299 */       sDeclarationCallingMethodName = memberSelect.identifier().name();
/* 300 */       sMethodName = sDeclarationMethodName + "." + sDeclarationCallingMethodName;
/*     */     } 
/*     */ 
/*     */     
/* 304 */     return sMethodName;
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 308 */     BlockTree blockTree = null;
/* 309 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 310 */     StatementTree stmtTree = forStmtTree.statement();
/* 311 */     if ("BLOCK".equals(stmtTree.kind().toString())) {
/* 312 */       blockTree = (BlockTree)forStmtTree.statement();
/* 313 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 314 */     } else if ("EXPRESSION_STATEMENT".equals(stmtTree.kind().toString())) {
/* 315 */       invokeExpressionStatementTreeMethod((Tree)stmtTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 320 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 321 */     BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 322 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */   }
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 326 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/* 327 */     ExpressionTree expTree = ifStmtTree.condition();
/*     */     
/* 329 */     if ("LOGICAL_COMPLEMENT".equals(expTree.kind().toString())) {
/* 330 */       UnaryExpressionTree uet = (UnaryExpressionTree)expTree;
/* 331 */       ExpressionTree newet = uet.expression();
/*     */       
/* 333 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 334 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 335 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */       
/*     */       } 
/* 338 */     } else if ("METHOD_INVOCATION".equals(expTree.kind().toString())) {
/* 339 */       invokeMethodInvocationTreeMethod((Tree)expTree, ifLoopTree);
/*     */     } 
/* 341 */     StatementTree stmtTree = ifStmtTree.thenStatement();
/* 342 */     invokeIfElseStatementTreeMethod(stmtTree);
/*     */     
/*     */     try {
/* 345 */       while (ifStmtTree.elseStatement() != null) {
/* 346 */         stmtTree = ifStmtTree.elseStatement();
/* 347 */         if ("IF_STATEMENT".equals(stmtTree.kind().toString())) {
/* 348 */           ifStmtTree = (IfStatementTree)stmtTree;
/* 349 */           StatementTree newst = ifStmtTree.thenStatement();
/* 350 */           invokeIfElseStatementTreeMethod(newst); continue;
/*     */         } 
/* 352 */         invokeIfElseStatementTreeMethod(stmtTree);
/* 353 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 356 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree stmtTree) {
/* 361 */     if (stmtTree.kind().toString().equals("BLOCK")) {
/* 362 */       BlockTree bt = (BlockTree)stmtTree;
/* 363 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 364 */     } else if (stmtTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 365 */       invokeExpressionStatementTreeMethod((Tree)stmtTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 370 */     Tree eachLineTree = null;
/* 371 */     TryStatementTree objTryStmtTree = null;
/* 372 */     BlockTree btTryStmtTree = null;
/* 373 */     CatchTree catchTree = null;
/* 374 */     BlockTree btCatch = null;
/* 375 */     BlockTree btTryStmtFinallyTree = null;
/* 376 */     WhileStatementTree whileStmtTree = null;
/* 377 */     StatementTree stmtTree = null;
/* 378 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 379 */       eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 381 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 382 */         invokeVariableTreeMethod(eachLineTree);
/* 383 */       } else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 384 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 385 */       } else if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 386 */         objTryStmtTree = (TryStatementTree)eachLineTree;
/* 387 */         btTryStmtTree = objTryStmtTree.block();
/* 388 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */         
/* 390 */         List<? extends CatchTree> catches = objTryStmtTree.catches();
/*     */         
/* 392 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 393 */           catchTree = catches.get(iCatchCnt);
/* 394 */           btCatch = catchTree.block();
/* 395 */           checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */         } 
/*     */         try {
/* 398 */           btTryStmtFinallyTree = objTryStmtTree.finallyBlock();
/* 399 */           checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */         }
/* 401 */         catch (Exception exception) {}
/*     */       }
/* 403 */       else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 404 */         invokeForStmtTreeMethod(eachLineTree);
/* 405 */       } else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 406 */         invokeForEachStmtTreeMethod(eachLineTree);
/* 407 */       } else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 408 */         invokeIfStmtTreeMethod(eachLineTree);
/* 409 */       } else if (eachLineTree.kind().toString().equals("WHILE_STATEMENT")) {
/* 410 */         whileStmtTree = (WhileStatementTree)eachLineTree;
/* 411 */         stmtTree = whileStmtTree.statement();
/* 412 */         invokeIfElseStatementTreeMethod(stmtTree);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMGetRelatedRelationshipStringLiteralRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */