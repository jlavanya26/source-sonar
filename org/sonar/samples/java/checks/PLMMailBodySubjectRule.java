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
/*     */ import org.sonar.plugins.java.api.tree.UnaryExpressionTree;
/*     */ import org.sonar.plugins.java.api.tree.VariableTree;
/*     */ import org.sonar.plugins.java.api.tree.WhileStatementTree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Rule(key = "PLMMailBodySubject")
/*     */ public class PLMMailBodySubjectRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   boolean bLoggingActive = false;
/*  40 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  45 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  54 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  56 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  57 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  59 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  60 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  62 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  68 */     BlockTree blocktree = methodTree.block();
/*  69 */     Tree.Kind treeKind = methodTree.kind();
/*  70 */     log("&&&& - " + treeKind.toString() + " --> " + methodTree.simpleName().name());
/*     */     
/*  72 */     this.htReportIssue = new Hashtable<>();
/*     */     
/*  74 */     if (blocktree != null) {
/*  75 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  81 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  82 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void log(String str) {
/*  88 */     if (this.bLoggingActive) {
/*  89 */       System.out.println(str);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/*  97 */     VariableTree variableTree = (VariableTree)eachLineTree;
/*  98 */     VariableTree myVariabletree = (VariableTree)variableTree.symbol().declaration();
/*  99 */     VariableTree variableTree1 = myVariabletree;
/*     */     
/*     */     try {
/* 102 */       expressionTree = myVariabletree.initializer();
/* 103 */       log("MYETT  - " + expressionTree.kind().toString());
/*     */     }
/* 105 */     catch (Exception ex) {
/* 106 */       log(" --- inside exception --" + ex);
/* 107 */       if (expressionTree == null) {
/* 108 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/*     */     
/* 112 */     if (tree.kind().toString().equals("STRING_LITERAL")) {
/* 113 */       LiteralTree literalTree = (LiteralTree)tree;
/* 114 */       if (literalTree.value().startsWith("\"") && literalTree.value().endsWith("\"")) {
/* 115 */         reportIssue(eachLineTree, "SOGETI --> String should not be hardcoded.");
/*     */       }
/* 117 */     } else if (tree.kind().toString().equals("PLUS")) {
/* 118 */       reportIssue(eachLineTree, "SOGETI --> Use stringbuilder/stringbuffer instead of string concatenated.");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/* 126 */     ExpressionStatementTree expSatetTree = (ExpressionStatementTree)eachLineTree;
/* 127 */     ExpressionTree expressionTree = expSatetTree.expression();
/* 128 */     if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/* 129 */       String str = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 130 */     } else if (expressionTree.kind().toString().equals("ASSIGNMENT")) {
/* 131 */       AssignmentExpressionTree assignExprsTree = (AssignmentExpressionTree)expressionTree;
/* 132 */       invokeAssignmentExpressionStatementTreeMethod((Tree)assignExprsTree.expression(), eachLineTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree tree, Tree eachLineTree) {
/* 139 */     if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 140 */       String str = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/* 141 */     } else if (tree.kind().toString().equals("PLUS")) {
/* 142 */       reportIssue(eachLineTree, "SOGETI --> Use stringbuilder/stringbuffer instead of string concatenated.");
/* 143 */     } else if (tree.kind().toString().equals("STRING_LITERAL")) {
/* 144 */       LiteralTree literalTree = (LiteralTree)tree;
/* 145 */       if (literalTree.value().startsWith("\"") && literalTree.value().endsWith("\"")) {
/* 146 */         reportIssue(eachLineTree, "SOGETI --> String should not be hardcoded.");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 155 */     MethodInvocationTree methodInvokeTree = (MethodInvocationTree)mytree;
/* 156 */     String strFinalValue = null;
/* 157 */     ExpressionTree expressionTree = methodInvokeTree.methodSelect();
/*     */     
/* 159 */     String sMyArgs = null;
/* 160 */     if (expressionTree.kind().toString().equals("MEMBER_SELECT")) {
/*     */       
/* 162 */       MemberSelectExpressionTree mset = (MemberSelectExpressionTree)expressionTree;
/* 163 */       String myDeclarationMethodName = mset.firstToken().text();
/* 164 */       String myDeclarationCallingMethodName = mset.identifier().name();
/* 165 */       strFinalValue = myDeclarationMethodName + "." + myDeclarationCallingMethodName;
/*     */       
/* 167 */       if (methodInvokeTree.arguments().size() != 0) {
/* 168 */         Arguments<Tree> arguments = methodInvokeTree.arguments();
/*     */         
/* 170 */         sMyArgs = "(";
/* 171 */         for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/*     */ 
/*     */           
/* 174 */           if (myDeclarationCallingMethodName.equals("sendNotificationToUser") || myDeclarationCallingMethodName.equals("sendNotification") || myDeclarationCallingMethodName.equals("sendMessage")) {
/* 175 */             if ("STRING_LITERAL".equals(((Tree)arguments.get(iArgCnt)).kind().toString()) && ((Tree)arguments.get(iArgCnt)).firstToken().text().startsWith("\"") && ((Tree)arguments.get(iArgCnt)).firstToken().text().endsWith("\"")) {
/* 176 */               reportIssue(eachLineTree, "SOGETI --> mail body or subject should not be hardcoded.");
/* 177 */             } else if ("PLUS".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 178 */               reportIssue(eachLineTree, "SOGETI --> Use stringbuilder/stringbuffer instead of string concatenated");
/*     */             } 
/* 180 */           } else if (myDeclarationCallingMethodName.equals("addElement") || myDeclarationCallingMethodName.equals("add") || myDeclarationCallingMethodName.equals("append")) {
/*     */             
/* 182 */             if ("STRING_LITERAL".equals(((Tree)arguments.get(iArgCnt)).kind().toString()) && ((Tree)arguments.get(iArgCnt)).firstToken().text().startsWith("\"") && ((Tree)arguments.get(iArgCnt)).firstToken().text().endsWith("\"")) {
/* 183 */               reportIssue(eachLineTree, "SOGETI --> string should not be hardcoded.");
/* 184 */             } else if ("PLUS".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 185 */               reportIssue(eachLineTree, "SOGETI --> Use stringbuilder/stringbuffer instead of string concatenated or remove hardcoding");
/* 186 */             } else if ("IDENTIFIER".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/*     */             
/*     */             } 
/*     */           } 
/*     */         } 
/* 191 */         sMyArgs = sMyArgs + "--)";
/* 192 */         sMyArgs = sMyArgs.replace(", --", "");
/*     */       } else {
/* 194 */         sMyArgs = "()";
/*     */       } 
/*     */     } 
/* 197 */     return strFinalValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 204 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */     
/* 206 */     ExpressionTree exprTree = ifStmtTree.condition();
/*     */     
/* 208 */     if ("LOGICAL_COMPLEMENT".equals(exprTree.kind().toString())) {
/* 209 */       UnaryExpressionTree unaryExpressionTree = (UnaryExpressionTree)exprTree;
/* 210 */       ExpressionTree newExpressionTree = unaryExpressionTree.expression();
/* 211 */       log("*** logical complement kind *** - " + newExpressionTree.kind().toString());
/*     */       
/* 213 */       if ("METHOD_INVOCATION".equals(newExpressionTree.kind().toString())) {
/* 214 */         invokeMethodInvocationTreeMethod((Tree)newExpressionTree, ifLoopTree);
/* 215 */       } else if ("IDENTIFIER".equals(newExpressionTree.kind().toString())) {
/*     */ 
/*     */       
/*     */       }
/*     */     
/* 220 */     } else if ("METHOD_INVOCATION".equals(exprTree.kind().toString())) {
/* 221 */       invokeMethodInvocationTreeMethod((Tree)exprTree, ifLoopTree);
/*     */     } 
/*     */ 
/*     */     
/* 225 */     StatementTree statementTree = ifStmtTree.thenStatement();
/*     */     
/* 227 */     invokeIfElseStatementTreeMethod(statementTree);
/*     */ 
/*     */     
/*     */     try {
/* 231 */       while (ifStmtTree.elseStatement() != null) {
/* 232 */         statementTree = ifStmtTree.elseStatement();
/*     */         
/* 234 */         if ("IF_STATEMENT".equals(statementTree.kind().toString())) {
/* 235 */           ifStmtTree = (IfStatementTree)statementTree;
/* 236 */           log("*** if stmt two kind *** - " + ifStmtTree.kind().toString());
/* 237 */           StatementTree newStatementTree = ifStmtTree.thenStatement();
/* 238 */           invokeIfElseStatementTreeMethod(newStatementTree);
/*     */           
/*     */           continue;
/*     */         } 
/* 242 */         invokeIfElseStatementTreeMethod(statementTree);
/* 243 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 246 */     } catch (Exception ex) {
/* 247 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree statementTree) {
/* 254 */     if (statementTree.kind().toString().equals("BLOCK")) {
/* 255 */       BlockTree blockTree = (BlockTree)statementTree;
/* 256 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 257 */     } else if (statementTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 258 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 265 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 266 */     log("*** inside invokeForStmtTreeMethod kind *** - " + forStmtTree.kind().toString());
/*     */     
/* 268 */     StatementTree stateTree = forStmtTree.statement();
/* 269 */     log("*** et kind *** - " + stateTree.kind().toString());
/*     */     
/* 271 */     if ("BLOCK".equals(stateTree.kind().toString())) {
/* 272 */       BlockTree blockTree = (BlockTree)forStmtTree.statement();
/* 273 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 274 */     } else if ("EXPRESSION_STATEMENT".equals(stateTree.kind().toString())) {
/* 275 */       invokeExpressionStatementTreeMethod((Tree)stateTree);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 282 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/*     */     
/* 284 */     BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 285 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 293 */     Tree eachLineTree = null;
/*     */     
/* 295 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 296 */       eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 298 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 299 */         invokeVariableTreeMethod(eachLineTree);
/* 300 */       } else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 301 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 302 */       } else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 303 */         invokeIfStmtTreeMethod(eachLineTree);
/* 304 */       } else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 305 */         invokeForStmtTreeMethod(eachLineTree);
/* 306 */       } else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 307 */         invokeForEachStmtTreeMethod(eachLineTree);
/* 308 */       } else if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 309 */         TryStatementTree tst = (TryStatementTree)eachLineTree;
/* 310 */         BlockTree btTryStmtTree = tst.block();
/*     */ 
/*     */         
/* 313 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */         
/* 315 */         List<? extends CatchTree> catches = tst.catches();
/*     */         
/* 317 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 318 */           CatchTree ct = catches.get(iCatchCnt);
/* 319 */           BlockTree btCatch = ct.block();
/* 320 */           checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */         } 
/*     */         
/*     */         try {
/* 324 */           BlockTree btTryStmtFinallyTree = tst.finallyBlock();
/* 325 */           log("btTryStmtFinallyTree  - " + btTryStmtFinallyTree.kind().toString());
/*     */ 
/*     */           
/* 328 */           checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */         }
/* 330 */         catch (Exception ex) {
/* 331 */           log(" --- no finally block available exception -- " + ex);
/*     */         } 
/* 333 */       } else if (eachLineTree.kind().toString().equals("WHILE_STATEMENT")) {
/* 334 */         WhileStatementTree whileStateTree = (WhileStatementTree)eachLineTree;
/* 335 */         StatementTree stateTree = whileStateTree.statement();
/* 336 */         invokeIfElseStatementTreeMethod(stateTree);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMMailBodySubjectRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */