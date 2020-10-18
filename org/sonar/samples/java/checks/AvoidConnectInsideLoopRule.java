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
/*     */ @Rule(key = "AvoidConnectInsideLoop")
/*     */ public class AvoidConnectInsideLoopRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  55 */   public int max = 2;
/*     */   boolean bLoggingActive = false;
/*  57 */   private static int inLoop = 0;
/*  58 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  63 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  69 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  71 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  72 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  74 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  75 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  77 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  83 */     BlockTree blocktree = methodTree.block();
/*  84 */     this.htReportIssue = new Hashtable<>();
/*     */     
/*  86 */     if (blocktree != null) {
/*  87 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  94 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  95 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(String str) {
/* 100 */     if (this.bLoggingActive);
/*     */   }
/*     */ 
/*     */   
/*     */   private Hashtable bumpUpCount(String sMethodName) {
/* 105 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 106 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 107 */       int iNewSize = iSize.intValue() + 1;
/* 108 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 110 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/* 112 */     return this.htReportIssue;
/*     */   }
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String sMethodName, Tree eachLineTree) {
/* 116 */     if (this.htReportIssue.size() > 0);
/*     */     
/* 118 */     if (this.htReportIssue.containsKey(sMethodName)) {
/*     */       
/* 120 */       reportIssue(eachLineTree, "SOGETI --> DomainRelationship.connect() method should be used outside loop.");
/*     */       
/* 122 */       return true;
/*     */     } 
/*     */     
/* 125 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/* 130 */     ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree)eachLineTree;
/* 131 */     ExpressionTree expressionTree = expressionStatementTree.expression();
/*     */     
/* 133 */     if (!expressionTree.kind().toString().equals("STRING_LITERAL") && 
/* 134 */       !expressionTree.kind().toString().equals("IDENTIFIER")) {
/* 135 */       if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/* 136 */         String str = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 137 */       } else if (expressionTree.kind().toString().equals("ASSIGNMENT")) {
/* 138 */         AssignmentExpressionTree aet = (AssignmentExpressionTree)expressionTree;
/* 139 */         invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 146 */     TypeCastTree typeCastTree = (TypeCastTree)et;
/* 147 */     ExpressionTree expressionTree = typeCastTree.expression();
/* 148 */     String sRet = "";
/*     */     
/* 150 */     if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/* 151 */       sRet = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/*     */     }
/* 153 */     else if (expressionTree.kind().toString().equals("MEMBER_SELECT")) {
/* 154 */       sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/*     */     }
/* 156 */     else if (expressionTree.kind().toString().equals("IDENTIFIER")) {
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree tree, Tree eachLineTree) {
/* 166 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 167 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 168 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 169 */       String str = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 176 */     String sRet = "";
/* 177 */     VariableTree variableTree = (VariableTree)eachLineTree;
/*     */     
/* 179 */     VariableTree myVariableTree = (VariableTree)variableTree.symbol().declaration();
/*     */     
/* 181 */     VariableTree variableTree1 = myVariableTree;
/*     */     try {
/* 183 */       expressionTree = myVariableTree.initializer();
/* 184 */       log("MYETT  - " + expressionTree.kind().toString());
/* 185 */     } catch (Exception ex) {
/* 186 */       log(" --- inside exception --" + ex);
/* 187 */       if (expressionTree == null) {
/* 188 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/* 191 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 192 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 193 */     } else if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 194 */       sRet = invokeMemberSelectMethod(tree, eachLineTree);
/* 195 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 196 */       sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 203 */     MethodInvocationTree methodInvocationTree = (MethodInvocationTree)mytree;
/* 204 */     ExpressionTree expressionTree = methodInvocationTree.methodSelect();
/*     */     
/* 206 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/*     */     
/* 208 */     String sMyArgs = "";
/*     */     
/* 210 */     if (methodInvocationTree.arguments().size() != 0) {
/* 211 */       Arguments<Tree> arguments = methodInvocationTree.arguments();
/* 212 */       sMyArgs = "(";
/* 213 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/*     */         
/* 215 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 216 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 217 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 218 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 219 */           sRet = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 222 */       sMyArgs = sMyArgs + "--)";
/* 223 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 225 */       sMyArgs = "()";
/*     */     } 
/*     */     
/* 228 */     return sRet + sMyArgs;
/*     */   }
/*     */   
/*     */   private String invokeMemberSelectMethod(Tree ettemp, Tree eachLineTree) {
/* 232 */     String str = "";
/*     */     
/* 234 */     if (ettemp.kind().toString().equals("MEMBER_SELECT")) {
/* 235 */       MemberSelectExpressionTree mset = (MemberSelectExpressionTree)ettemp;
/* 236 */       String sDeclarationMethodName = mset.firstToken().text();
/* 237 */       String sDeclarationCallingMethodName = mset.identifier().name();
/* 238 */       str = sDeclarationMethodName + "." + sDeclarationCallingMethodName;
/*     */ 
/*     */       
/* 241 */       if (str.equals("DomainRelationship.connect") && inLoop > 0) {
/* 242 */         bumpUpCount(str);
/* 243 */         bCheckAndReportIssueNow(str, eachLineTree);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 248 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 253 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/*     */     
/* 255 */     StatementTree statementTree = forStmtTree.statement();
/* 256 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 257 */       BlockTree blockTree = (BlockTree)forStmtTree.statement();
/* 258 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 259 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 260 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeWhileStmtTreeMethod(Tree whileLoopTree) {
/* 266 */     WhileStatementTree whileStmtTree = (WhileStatementTree)whileLoopTree;
/*     */     
/* 268 */     StatementTree statementTree = whileStmtTree.statement();
/* 269 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 270 */       BlockTree blockTree = (BlockTree)whileStmtTree.statement();
/* 271 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 272 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 273 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 278 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/*     */     
/* 280 */     StatementTree statementTree = forEachStmt.statement();
/* 281 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 282 */       BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 283 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 284 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 285 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 290 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */     
/* 292 */     ExpressionTree expressionTree = ifStmtTree.condition();
/*     */     
/* 294 */     if ("LOGICAL_COMPLEMENT".equals(expressionTree.kind().toString())) {
/* 295 */       UnaryExpressionTree unaryExpressionTree = (UnaryExpressionTree)expressionTree;
/* 296 */       ExpressionTree newet = unaryExpressionTree.expression();
/*     */       
/* 298 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 299 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 300 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */       
/*     */       } 
/* 303 */     } else if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 304 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, ifLoopTree);
/*     */     } 
/*     */ 
/*     */     
/* 308 */     StatementTree statementTree = ifStmtTree.thenStatement();
/*     */     
/* 310 */     invokeIfElseStatementTreeMethod(statementTree);
/*     */ 
/*     */     
/*     */     try {
/* 314 */       while (ifStmtTree.elseStatement() != null) {
/* 315 */         statementTree = ifStmtTree.elseStatement();
/*     */         
/* 317 */         if ("IF_STATEMENT".equals(statementTree.kind().toString())) {
/* 318 */           ifStmtTree = (IfStatementTree)statementTree;
/* 319 */           StatementTree newst = ifStmtTree.thenStatement();
/* 320 */           invokeIfElseStatementTreeMethod(newst); continue;
/*     */         } 
/* 322 */         invokeIfElseStatementTreeMethod(statementTree);
/* 323 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 326 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree statementTree) {
/* 331 */     if (statementTree.kind().toString().equals("BLOCK")) {
/* 332 */       BlockTree blockTree = (BlockTree)statementTree;
/* 333 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */     }
/* 335 */     else if (statementTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 336 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 342 */     Tree eachLineTree = null;
/* 343 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 344 */       eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 346 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 347 */         invokeVariableTreeMethod(eachLineTree);
/* 348 */       } else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 349 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 350 */       } else if (!eachLineTree.kind().toString().equals("METHOD_INVOCATION")) {
/*     */         
/* 352 */         if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 353 */           TryStatementTree tryStatementTree = (TryStatementTree)eachLineTree;
/* 354 */           BlockTree btTryStmtTree = tryStatementTree.block();
/*     */           
/* 356 */           checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */           
/* 358 */           List<? extends CatchTree> catches = tryStatementTree.catches();
/*     */           
/* 360 */           for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 361 */             CatchTree catchTree = catches.get(iCatchCnt);
/* 362 */             BlockTree blockTree = catchTree.block();
/* 363 */             checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */           } 
/*     */           
/*     */           try {
/* 367 */             BlockTree btTryStmtFinallyTree = tryStatementTree.finallyBlock();
/*     */             
/* 369 */             checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */           }
/* 371 */           catch (Exception exception) {}
/*     */         }
/* 373 */         else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 374 */           inLoop++;
/* 375 */           invokeForStmtTreeMethod(eachLineTree);
/* 376 */           inLoop--;
/* 377 */         } else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 378 */           inLoop++;
/* 379 */           invokeForEachStmtTreeMethod(eachLineTree);
/* 380 */           inLoop--;
/* 381 */         } else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 382 */           invokeIfStmtTreeMethod(eachLineTree);
/* 383 */         } else if (eachLineTree.kind().toString().equals("WHILE_STATEMENT")) {
/* 384 */           inLoop++;
/* 385 */           invokeWhileStmtTreeMethod(eachLineTree);
/* 386 */           inLoop--;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\AvoidConnectInsideLoopRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */