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
/*     */ @Rule(key = "PLMGetRelatedRelationshipLevel")
/*     */ public class PLMGetRelatedRelationshipLevelRule
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
/* 114 */         reportIssue(eachLineTree, "Sogeti Selectable  Rule : Avoid using Level 0 or less than 0 as argument in APIs."); 
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
/* 201 */     String sMethodinvoked = null;
/* 202 */     String mDeclarationCallingMethodName = null;
/* 203 */     String mDeclarationMethodName = null;
/* 204 */     MethodInvocationTree mInvocTree = null;
/* 205 */     MemberSelectExpressionTree mSelExpTree = null;
/* 206 */     String slevel = null;
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
/* 220 */             if (((Tree)arguments.get(iArgs)).kind().toString().equalsIgnoreCase("BOOLEAN_LITERAL") && ((Tree)arguments.get(iArgs + 1)).kind().toString().equalsIgnoreCase("BOOLEAN_LITERAL")) {
/* 221 */               slevel = ((Tree)arguments.get(iArgs + 2)).lastToken().text();
/* 222 */               if (!slevel.contains("ev") && (
/* 223 */                 new Integer(slevel)).intValue() <= 0) {
/* 224 */                 reportIssue(eachLineTree, "Sogeti Selectable  Rule : Avoid using Level 0 or less than 0 as argument in APIs.");
/*     */                 
/*     */                 break;
/*     */               } 
/* 228 */             } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 229 */               sMethodinvoked = invokeMethodInvocationTreeMethod(arguments.get(iArgs), eachLineTree);
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
/* 240 */     return sMethodName;
/*     */   }
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 244 */     String sMyArgs = null;
/* 245 */     TypeCastTree objTypeCastTree = null;
/*     */     
/* 247 */     MethodInvocationTree objMethodInvocTree = (MethodInvocationTree)mytree;
/* 248 */     ExpressionTree expressionTree = objMethodInvocTree.methodSelect();
/* 249 */     String sResult = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree, (Tree)objMethodInvocTree);
/* 250 */     if (objMethodInvocTree.arguments().size() != 0) {
/* 251 */       Arguments<Tree> arguments = objMethodInvocTree.arguments();
/*     */       
/* 253 */       sMyArgs = "(";
/* 254 */       for (int iArgs = 0; iArgs < arguments.size(); iArgs++) {
/* 255 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 256 */           objTypeCastTree = (TypeCastTree)arguments.get(iArgs);
/* 257 */           invokeTypeCastTreeMethod((Tree)objTypeCastTree, eachLineTree);
/* 258 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 259 */           sResult = invokeMethodInvocationTreeMethod(arguments.get(iArgs), eachLineTree);
/*     */         } 
/*     */       } 
/* 262 */       sMyArgs = sMyArgs + "--)";
/* 263 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 265 */       sMyArgs = "()";
/*     */     } 
/*     */     
/* 268 */     return sResult + sMyArgs;
/*     */   }
/*     */   
/*     */   private String invokeMemberSelectMethod(Tree tempTree, Tree eachLineTree) {
/* 272 */     String sMethodName = null;
/* 273 */     String sDeclarationMethodName = null;
/* 274 */     String sDeclarationCallingMethodName = null;
/* 275 */     MemberSelectExpressionTree memberSelect = null;
/*     */     
/* 277 */     if (tempTree.kind().toString().equals("MEMBER_SELECT")) {
/* 278 */       memberSelect = (MemberSelectExpressionTree)tempTree;
/* 279 */       sDeclarationMethodName = memberSelect.firstToken().text();
/* 280 */       sDeclarationCallingMethodName = memberSelect.identifier().name();
/* 281 */       sMethodName = sDeclarationMethodName + "." + sDeclarationCallingMethodName;
/*     */     } 
/*     */ 
/*     */     
/* 285 */     return sMethodName;
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 289 */     BlockTree blockTree = null;
/* 290 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 291 */     StatementTree stmtTree = forStmtTree.statement();
/* 292 */     if ("BLOCK".equals(stmtTree.kind().toString())) {
/* 293 */       blockTree = (BlockTree)forStmtTree.statement();
/* 294 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 295 */     } else if ("EXPRESSION_STATEMENT".equals(stmtTree.kind().toString())) {
/* 296 */       invokeExpressionStatementTreeMethod((Tree)stmtTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 301 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 302 */     BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 303 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */   }
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 307 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/* 308 */     ExpressionTree expTree = ifStmtTree.condition();
/*     */     
/* 310 */     if ("LOGICAL_COMPLEMENT".equals(expTree.kind().toString())) {
/* 311 */       UnaryExpressionTree uet = (UnaryExpressionTree)expTree;
/* 312 */       ExpressionTree newet = uet.expression();
/*     */       
/* 314 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 315 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 316 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */       
/*     */       } 
/* 319 */     } else if ("METHOD_INVOCATION".equals(expTree.kind().toString())) {
/* 320 */       invokeMethodInvocationTreeMethod((Tree)expTree, ifLoopTree);
/*     */     } 
/* 322 */     StatementTree stmtTree = ifStmtTree.thenStatement();
/* 323 */     invokeIfElseStatementTreeMethod(stmtTree);
/*     */     
/*     */     try {
/* 326 */       while (ifStmtTree.elseStatement() != null) {
/* 327 */         stmtTree = ifStmtTree.elseStatement();
/* 328 */         if ("IF_STATEMENT".equals(stmtTree.kind().toString())) {
/* 329 */           ifStmtTree = (IfStatementTree)stmtTree;
/* 330 */           StatementTree newst = ifStmtTree.thenStatement();
/* 331 */           invokeIfElseStatementTreeMethod(newst); continue;
/*     */         } 
/* 333 */         invokeIfElseStatementTreeMethod(stmtTree);
/* 334 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 337 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree stmtTree) {
/* 342 */     if (stmtTree.kind().toString().equals("BLOCK")) {
/* 343 */       BlockTree bt = (BlockTree)stmtTree;
/* 344 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 345 */     } else if (stmtTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 346 */       invokeExpressionStatementTreeMethod((Tree)stmtTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 351 */     Tree eachLineTree = null;
/* 352 */     TryStatementTree objTryStmtTree = null;
/* 353 */     BlockTree btTryStmtTree = null;
/* 354 */     CatchTree catchTree = null;
/* 355 */     BlockTree btCatch = null;
/* 356 */     BlockTree btTryStmtFinallyTree = null;
/* 357 */     WhileStatementTree whileStmtTree = null;
/* 358 */     StatementTree stmtTree = null;
/* 359 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 360 */       eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 362 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 363 */         invokeVariableTreeMethod(eachLineTree);
/* 364 */       } else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 365 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 366 */       } else if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 367 */         objTryStmtTree = (TryStatementTree)eachLineTree;
/* 368 */         btTryStmtTree = objTryStmtTree.block();
/* 369 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */         
/* 371 */         List<? extends CatchTree> catches = objTryStmtTree.catches();
/*     */         
/* 373 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 374 */           catchTree = catches.get(iCatchCnt);
/* 375 */           btCatch = catchTree.block();
/* 376 */           checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */         } 
/*     */         try {
/* 379 */           btTryStmtFinallyTree = objTryStmtTree.finallyBlock();
/* 380 */           checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */         }
/* 382 */         catch (Exception exception) {}
/*     */       }
/* 384 */       else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 385 */         invokeForStmtTreeMethod(eachLineTree);
/* 386 */       } else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 387 */         invokeForEachStmtTreeMethod(eachLineTree);
/* 388 */       } else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 389 */         invokeIfStmtTreeMethod(eachLineTree);
/* 390 */       } else if (eachLineTree.kind().toString().equals("WHILE_STATEMENT")) {
/* 391 */         whileStmtTree = (WhileStatementTree)eachLineTree;
/* 392 */         stmtTree = whileStmtTree.statement();
/* 393 */         invokeIfElseStatementTreeMethod(stmtTree);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMGetRelatedRelationshipLevelRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */