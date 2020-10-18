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
/*     */ @Rule(key = "PLMGetRelatedBool")
/*     */ public class PLMGetRelatedBoolRule
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
/* 115 */         reportIssue(eachLineTree, "Sogeti Selectable  Rule: Both from and to side relationship is true"); 
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
/* 205 */     String sInvokedMethod = null;
/* 206 */     MethodInvocationTree mInvocationTree = null;
/*     */     
/* 208 */     if (tempTree.kind().toString().equals("MEMBER_SELECT")) {
/* 209 */       mSelExpTree = (MemberSelectExpressionTree)tempTree;
/* 210 */       mDeclarationMethodName = mSelExpTree.firstToken().text();
/* 211 */       mDeclarationCallingMethodName = mSelExpTree.identifier().name();
/* 212 */       sMethodName = mDeclarationMethodName + "." + mDeclarationCallingMethodName;
/* 213 */       mInvocationTree = (MethodInvocationTree)Mit;
/*     */       
/* 215 */       if (mDeclarationCallingMethodName.equals("getRelatedObjects") || mDeclarationCallingMethodName.equals("findObjects")) {
/*     */         
/* 217 */         Arguments<Tree> arguments = mInvocationTree.arguments();
/* 218 */         if (mInvocationTree.arguments().size() > 1)
/*     */         {
/* 220 */           for (int iArgs = 0; iArgs < arguments.size(); iArgs++) {
/* 221 */             if (((Tree)arguments.get(iArgs)).kind().toString().equalsIgnoreCase("BOOLEAN_LITERAL")) {
/* 222 */               if (((Tree)arguments.get(iArgs)).firstToken().text().equals("true") && ((Tree)arguments.get(iArgs + 1)).firstToken().text().equals("true")) {
/* 223 */                 reportIssue(eachLineTree, "Sogeti Selectable  Rule: Both from and to side relationship is true");
/*     */               }
/* 225 */             } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 226 */               sInvokedMethod = invokeMethodInvocationTreeMethod(arguments.get(iArgs), eachLineTree);
/*     */             } 
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 236 */     return sMethodName;
/*     */   }
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 240 */     String sMyArgs = null;
/* 241 */     TypeCastTree objTypeCastTree = null;
/*     */     
/* 243 */     MethodInvocationTree objMethodInvocTree = (MethodInvocationTree)mytree;
/* 244 */     ExpressionTree expressionTree = objMethodInvocTree.methodSelect();
/* 245 */     String sResult = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree, (Tree)objMethodInvocTree);
/* 246 */     if (objMethodInvocTree.arguments().size() != 0) {
/* 247 */       Arguments<Tree> arguments = objMethodInvocTree.arguments();
/*     */       
/* 249 */       sMyArgs = "(";
/* 250 */       for (int iArgs = 0; iArgs < arguments.size(); iArgs++) {
/* 251 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 252 */           objTypeCastTree = (TypeCastTree)arguments.get(iArgs);
/* 253 */           invokeTypeCastTreeMethod((Tree)objTypeCastTree, eachLineTree);
/* 254 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgs)).kind().toString())) {
/* 255 */           sResult = invokeMethodInvocationTreeMethod(arguments.get(iArgs), eachLineTree);
/*     */         } 
/*     */       } 
/* 258 */       sMyArgs = sMyArgs + "--)";
/* 259 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 261 */       sMyArgs = "()";
/*     */     } 
/*     */     
/* 264 */     return sResult + sMyArgs;
/*     */   }
/*     */   
/*     */   private String invokeMemberSelectMethod(Tree tempTree, Tree eachLineTree) {
/* 268 */     String sMethodName = null;
/* 269 */     String sDeclarationMethodName = null;
/* 270 */     String sDeclarationCallingMethodName = null;
/* 271 */     MemberSelectExpressionTree memberSelect = null;
/*     */     
/* 273 */     if (tempTree.kind().toString().equals("MEMBER_SELECT")) {
/* 274 */       memberSelect = (MemberSelectExpressionTree)tempTree;
/* 275 */       sDeclarationMethodName = memberSelect.firstToken().text();
/* 276 */       sDeclarationCallingMethodName = memberSelect.identifier().name();
/* 277 */       sMethodName = sDeclarationMethodName + "." + sDeclarationCallingMethodName;
/*     */     } 
/*     */ 
/*     */     
/* 281 */     return sMethodName;
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 285 */     BlockTree blockTree = null;
/* 286 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 287 */     StatementTree stmtTree = forStmtTree.statement();
/* 288 */     if ("BLOCK".equals(stmtTree.kind().toString())) {
/* 289 */       blockTree = (BlockTree)forStmtTree.statement();
/* 290 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 291 */     } else if ("EXPRESSION_STATEMENT".equals(stmtTree.kind().toString())) {
/* 292 */       invokeExpressionStatementTreeMethod((Tree)stmtTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 297 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 298 */     BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 299 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */   }
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 303 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/* 304 */     ExpressionTree expTree = ifStmtTree.condition();
/*     */     
/* 306 */     if ("LOGICAL_COMPLEMENT".equals(expTree.kind().toString())) {
/* 307 */       UnaryExpressionTree uet = (UnaryExpressionTree)expTree;
/* 308 */       ExpressionTree newet = uet.expression();
/*     */       
/* 310 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 311 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 312 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */       
/*     */       } 
/* 315 */     } else if ("METHOD_INVOCATION".equals(expTree.kind().toString())) {
/* 316 */       invokeMethodInvocationTreeMethod((Tree)expTree, ifLoopTree);
/*     */     } 
/* 318 */     StatementTree stmtTree = ifStmtTree.thenStatement();
/* 319 */     invokeIfElseStatementTreeMethod(stmtTree);
/*     */     
/*     */     try {
/* 322 */       while (ifStmtTree.elseStatement() != null) {
/* 323 */         stmtTree = ifStmtTree.elseStatement();
/* 324 */         if ("IF_STATEMENT".equals(stmtTree.kind().toString())) {
/* 325 */           ifStmtTree = (IfStatementTree)stmtTree;
/* 326 */           StatementTree newst = ifStmtTree.thenStatement();
/* 327 */           invokeIfElseStatementTreeMethod(newst); continue;
/*     */         } 
/* 329 */         invokeIfElseStatementTreeMethod(stmtTree);
/* 330 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 333 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree stmtTree) {
/* 338 */     if (stmtTree.kind().toString().equals("BLOCK")) {
/* 339 */       BlockTree bt = (BlockTree)stmtTree;
/* 340 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 341 */     } else if (stmtTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 342 */       invokeExpressionStatementTreeMethod((Tree)stmtTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 347 */     Tree eachLineTree = null;
/* 348 */     TryStatementTree objTryStmtTree = null;
/* 349 */     BlockTree btTryStmtTree = null;
/* 350 */     CatchTree catchTree = null;
/* 351 */     BlockTree btCatch = null;
/* 352 */     BlockTree btTryStmtFinallyTree = null;
/* 353 */     WhileStatementTree whileStmtTree = null;
/* 354 */     StatementTree stmtTree = null;
/* 355 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 356 */       eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 358 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 359 */         invokeVariableTreeMethod(eachLineTree);
/* 360 */       } else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 361 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 362 */       } else if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 363 */         objTryStmtTree = (TryStatementTree)eachLineTree;
/* 364 */         btTryStmtTree = objTryStmtTree.block();
/* 365 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */         
/* 367 */         List<? extends CatchTree> catches = objTryStmtTree.catches();
/*     */         
/* 369 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 370 */           catchTree = catches.get(iCatchCnt);
/* 371 */           btCatch = catchTree.block();
/* 372 */           checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */         } 
/*     */         try {
/* 375 */           btTryStmtFinallyTree = objTryStmtTree.finallyBlock();
/* 376 */           checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */         }
/* 378 */         catch (Exception exception) {}
/*     */       }
/* 380 */       else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 381 */         invokeForStmtTreeMethod(eachLineTree);
/* 382 */       } else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 383 */         invokeForEachStmtTreeMethod(eachLineTree);
/* 384 */       } else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 385 */         invokeIfStmtTreeMethod(eachLineTree);
/* 386 */       } else if (eachLineTree.kind().toString().equals("WHILE_STATEMENT")) {
/* 387 */         whileStmtTree = (WhileStatementTree)eachLineTree;
/* 388 */         stmtTree = whileStmtTree.statement();
/* 389 */         invokeIfElseStatementTreeMethod(stmtTree);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMGetRelatedBoolRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */