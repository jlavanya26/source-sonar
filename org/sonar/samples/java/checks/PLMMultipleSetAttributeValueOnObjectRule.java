/*     */ package org.sonar.samples.java.checks;
/*     */ 
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import org.sonar.check.Rule;
/*     */ import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
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
/*     */ @Rule(key = "PLMMultipleSetAttributeValueOnObject")
/*     */ public class PLMMultipleSetAttributeValueOnObjectRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  38 */   public int max = 2;
/*     */   
/*     */   boolean bLoggingActive = false;
/*     */   
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
/*     */   public void visitNode(Tree tree) {
/*  53 */     MethodTree methodTree = (MethodTree)tree;
/*  54 */     BlockTree blocktree = methodTree.block();
/*  55 */     this.htReportIssue = new Hashtable<>();
/*  56 */     if (blocktree != null) {
/*  57 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  63 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  64 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(String strLog) {
/*  69 */     if (this.bLoggingActive) {
/*  70 */       System.out.println(strLog);
/*     */     }
/*     */   }
/*     */   
/*     */   private Hashtable<String, Integer> bumpUpCount(String sMethodName) {
/*  75 */     if (this.htReportIssue.containsKey(sMethodName)) {
/*  76 */       Integer iSize = this.htReportIssue.get(sMethodName);
/*     */       
/*  78 */       int iNewSize = iSize.intValue() + 1;
/*  79 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/*  81 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/*  83 */     return this.htReportIssue;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String sMethodName, Tree eachLineTree) {
/*  88 */     if (this.htReportIssue.size() > 0) {
/*  89 */       log("Report ht : " + this.htReportIssue);
/*     */     }
/*  91 */     if (this.htReportIssue.containsKey(sMethodName)) {
/*     */       
/*  93 */       if (this.htReportIssue.size() > 0)
/*     */       {
/*  95 */         if (this.htReportIssue.containsKey(sMethodName)) {
/*     */           
/*  97 */           Integer iSize = this.htReportIssue.get(sMethodName);
/*     */           
/*  99 */           if (iSize.intValue() > 1) {
/*     */             
/* 101 */             reportIssue(eachLineTree, "SOGETI --> " + sMethodName + ".setAttributeValue() method is used more than once, use .setAttributeValues()");
/* 102 */             return true;
/*     */           } 
/*     */           
/* 105 */           return false;
/*     */         } 
/*     */       }
/*     */       
/* 109 */       return false;
/*     */     } 
/* 111 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/* 116 */     ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree)eachLineTree;
/*     */     
/* 118 */     ExpressionTree expressionTree = expressionStatementTree.expression();
/* 119 */     if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 120 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 121 */     } else if ("ASSIGNMENT".equals(expressionTree.kind().toString())) {
/* 122 */       AssignmentExpressionTree assignmentExpressionTree = (AssignmentExpressionTree)expressionTree;
/* 123 */       invokeAssignmentExpressionStatementTreeMethod((Tree)assignmentExpressionTree.expression(), eachLineTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree tree, Tree eachLineTree) {
/* 128 */     TypeCastTree typeCastTree = (TypeCastTree)tree;
/* 129 */     ExpressionTree expressionTree = typeCastTree.expression();
/*     */     
/* 131 */     if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 132 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 133 */     } else if ("MEMBER_SELECT".equals(expressionTree.kind().toString())) {
/* 134 */       invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree tree, Tree eachLineTree) {
/* 139 */     if ("METHOD_INVOCATION".equals(tree.kind().toString()))
/*     */     {
/* 141 */       invokeMethodInvocationTreeMethod(tree, eachLineTree); } 
/*     */   }
/*     */   
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 147 */     VariableTree variableTree = (VariableTree)eachLineTree;
/*     */     
/* 149 */     VariableTree variableTreeSymbol = (VariableTree)variableTree.symbol().declaration();
/* 150 */     VariableTree variableTree1 = variableTreeSymbol;
/*     */     try {
/* 152 */       expressionTree = variableTreeSymbol.initializer();
/* 153 */       log("tree  - " + expressionTree.kind().toString());
/* 154 */     } catch (Exception ex) {
/* 155 */       log(" --- inside exception --" + ex);
/* 156 */       if (expressionTree == null) {
/* 157 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/* 160 */     if ("TYPE_CAST".equals(tree.kind().toString())) {
/* 161 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 162 */     } else if ("MEMBER_SELECT".equals(tree.kind().toString())) {
/* 163 */       invokeMemberSelectMethod(tree, eachLineTree);
/* 164 */     } else if ("METHOD_INVOCATION".equals(tree.kind().toString())) {
/* 165 */       invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree tree, Tree eachLineTree) {
/* 170 */     MethodInvocationTree methodInvocationTree = (MethodInvocationTree)tree;
/* 171 */     ExpressionTree expressionTree = methodInvocationTree.methodSelect();
/*     */     
/* 173 */     String strReturn = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/*     */     
/* 175 */     String strArguments = "";
/* 176 */     if (methodInvocationTree.arguments().size() != 0) {
/*     */       
/* 178 */       Arguments<Tree> arguments = methodInvocationTree.arguments();
/*     */       
/* 180 */       strArguments = "(";
/* 181 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/*     */         
/* 183 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 184 */           TypeCastTree typeCastTree = (TypeCastTree)arguments.get(iArgCnt);
/* 185 */           invokeTypeCastTreeMethod((Tree)typeCastTree, eachLineTree);
/* 186 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 187 */           strReturn = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 190 */       strArguments = strArguments + "--)";
/* 191 */       strArguments = strArguments.replace(", --", "");
/*     */     } else {
/* 193 */       strArguments = "()";
/*     */     } 
/*     */     
/* 196 */     return strReturn + strArguments;
/*     */   }
/*     */   
/*     */   private String invokeMemberSelectMethod(Tree tree, Tree eachLineTree) {
/* 200 */     String strMethodSelection = "";
/*     */     
/* 202 */     if ("MEMBER_SELECT".equals(tree.kind().toString())) {
/* 203 */       MemberSelectExpressionTree memberSelectExpressionTree = (MemberSelectExpressionTree)tree;
/* 204 */       String strMethodCalledBy = memberSelectExpressionTree.firstToken().text();
/* 205 */       String strMethodCalled = memberSelectExpressionTree.identifier().name();
/* 206 */       strMethodSelection = strMethodCalledBy + "." + strMethodCalled;
/* 207 */       if (strMethodCalled.equals("setAttributeValue") && !strMethodCalledBy.equals("DomainRelationship")) {
/* 208 */         bumpUpCount(strMethodCalledBy);
/* 209 */         bCheckAndReportIssueNow(strMethodCalledBy, eachLineTree);
/*     */       } 
/*     */     } 
/* 212 */     return strMethodSelection;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 217 */     ForStatementTree forStatementTree = (ForStatementTree)forLoopTree;
/*     */     
/* 219 */     StatementTree statementTree = forStatementTree.statement();
/* 220 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 221 */       BlockTree blockTree = (BlockTree)forStatementTree.statement();
/* 222 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 223 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 224 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 229 */     ForEachStatement forEachStatement = (ForEachStatement)forEachLoopTree;
/*     */     
/* 231 */     BlockTree blockTree = (BlockTree)forEachStatement.statement();
/* 232 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree tree) {
/* 237 */     IfStatementTree ifStatementTree = (IfStatementTree)tree;
/* 238 */     ExpressionTree expressionTree = ifStatementTree.condition();
/*     */     
/* 240 */     if ("LOGICAL_COMPLEMENT".equals(expressionTree.kind().toString())) {
/* 241 */       UnaryExpressionTree unaryExpressionTree = (UnaryExpressionTree)expressionTree;
/* 242 */       ExpressionTree newExpressionTree = unaryExpressionTree.expression();
/*     */       
/* 244 */       if ("METHOD_INVOCATION".equals(newExpressionTree.kind().toString())) {
/* 245 */         invokeMethodInvocationTreeMethod((Tree)newExpressionTree, tree);
/*     */       }
/* 247 */     } else if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 248 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, tree);
/*     */     } 
/*     */ 
/*     */     
/* 252 */     StatementTree statementTree = ifStatementTree.thenStatement();
/*     */     
/* 254 */     invokeIfElseStatementTreeMethod(statementTree);
/*     */ 
/*     */     
/*     */     try {
/* 258 */       while (ifStatementTree.elseStatement() != null) {
/* 259 */         statementTree = ifStatementTree.elseStatement();
/*     */         
/* 261 */         if ("IF_STATEMENT".equals(statementTree.kind().toString())) {
/* 262 */           ifStatementTree = (IfStatementTree)statementTree;
/* 263 */           StatementTree newStatementTree = ifStatementTree.thenStatement();
/* 264 */           invokeIfElseStatementTreeMethod(newStatementTree);
/*     */           continue;
/*     */         } 
/* 267 */         this.htReportIssue.clear();
/*     */         
/* 269 */         invokeIfElseStatementTreeMethod(statementTree);
/* 270 */         ifStatementTree = null;
/*     */       }
/*     */     
/* 273 */     } catch (Exception ex) {
/* 274 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree statementTree) {
/* 279 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 280 */       BlockTree blockTree = (BlockTree)statementTree;
/* 281 */       if (blockTree.kind().toString().equals("BLOCK")) {
/* 282 */         checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */       }
/* 284 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 285 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addressEachTree(List<? extends Tree> listOfTrees) {
/* 290 */     Tree eachLineTree = null;
/*     */     
/* 292 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 293 */       eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 295 */       if ("VARIABLE".equals(eachLineTree.kind().toString())) {
/* 296 */         invokeVariableTreeMethod(eachLineTree);
/* 297 */       } else if ("EXPRESSION_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 298 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 299 */       } else if ("TRY_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 300 */         TryStatementTree tryStatementTree = (TryStatementTree)eachLineTree;
/* 301 */         BlockTree blockTree = tryStatementTree.block();
/*     */ 
/*     */         
/* 304 */         checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */         
/* 306 */         List<? extends CatchTree> lTryStatementTreeCatches = tryStatementTree.catches();
/*     */         
/* 308 */         for (int iCatchCnt = 0; iCatchCnt < lTryStatementTreeCatches.size(); iCatchCnt++) {
/* 309 */           CatchTree catchTree = lTryStatementTreeCatches.get(iCatchCnt);
/* 310 */           BlockTree blockTreeCatch = catchTree.block();
/* 311 */           checkBlockBody(blockTreeCatch.openBraceToken(), blockTreeCatch.closeBraceToken(), blockTreeCatch.body());
/*     */         } 
/*     */         
/*     */         try {
/* 315 */           BlockTree blockTreeFinally = tryStatementTree.finallyBlock();
/*     */ 
/*     */           
/* 318 */           checkBlockBody(blockTreeFinally.openBraceToken(), blockTreeFinally.closeBraceToken(), blockTreeFinally.body());
/*     */         }
/* 320 */         catch (Exception ex) {
/* 321 */           log(" --- no finally block available exception -- " + ex);
/*     */         } 
/* 323 */       } else if ("FOR_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 324 */         invokeForStmtTreeMethod(eachLineTree);
/* 325 */       } else if ("FOR_EACH_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 326 */         invokeForEachStmtTreeMethod(eachLineTree);
/* 327 */       } else if ("IF_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 328 */         invokeIfStmtTreeMethod(eachLineTree);
/* 329 */       } else if ("WHILE_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 330 */         WhileStatementTree whileStatementTree = (WhileStatementTree)eachLineTree;
/* 331 */         StatementTree statementTree = whileStatementTree.statement();
/* 332 */         invokeIfElseStatementTreeMethod(statementTree);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMMultipleSetAttributeValueOnObjectRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */