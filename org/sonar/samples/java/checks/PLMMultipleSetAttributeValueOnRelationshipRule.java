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
/*     */ import org.sonar.plugins.java.api.tree.UnaryExpressionTree;
/*     */ import org.sonar.plugins.java.api.tree.WhileStatementTree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Rule(key = "PLMMultipleSetAttributeValueOnRelationship")
/*     */ public class PLMMultipleSetAttributeValueOnRelationshipRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  36 */   public int max = 2;
/*     */   
/*     */   boolean bLoggingActive = false;
/*     */   boolean bElseBlockReached = false;
/*  40 */   int count = 0;
/*     */   
/*  42 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  46 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  51 */     MethodTree methodTree = (MethodTree)tree;
/*  52 */     BlockTree blocktree = methodTree.block();
/*  53 */     this.htReportIssue = new Hashtable<>();
/*  54 */     if (blocktree != null) {
/*  55 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  60 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  61 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(String strLog) {
/*  66 */     if (this.bLoggingActive) {
/*  67 */       System.out.println(strLog);
/*     */     }
/*     */   }
/*     */   
/*     */   private Hashtable<String, Integer> bumpUpCount(String strRelationshipId) {
/*  72 */     if (this.htReportIssue.containsKey(strRelationshipId)) {
/*  73 */       Integer iSize = this.htReportIssue.get(strRelationshipId);
/*     */       
/*  75 */       if (this.bElseBlockReached && this.count == 0) {
/*     */         
/*  77 */         this.count++;
/*     */       }
/*     */       else {
/*     */         
/*  81 */         int iNewSize = iSize.intValue() + 1;
/*  82 */         this.htReportIssue.put(strRelationshipId, Integer.valueOf(iNewSize));
/*     */       } 
/*     */     } else {
/*  85 */       this.htReportIssue.put(strRelationshipId, Integer.valueOf(1));
/*     */     } 
/*  87 */     return this.htReportIssue;
/*     */   }
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String strRelationshipId, Tree eachLineTree) {
/*  91 */     if (this.htReportIssue.size() > 0) {
/*  92 */       log("Report ht : " + this.htReportIssue);
/*     */     }
/*  94 */     if (this.htReportIssue.containsKey(strRelationshipId)) {
/*  95 */       if (this.htReportIssue.size() > 0 && 
/*  96 */         this.htReportIssue.containsKey(strRelationshipId)) {
/*  97 */         Integer iSize = this.htReportIssue.get(strRelationshipId);
/*     */ 
/*     */         
/* 100 */         if (this.bElseBlockReached && iSize.intValue() == 2)
/*     */         {
/* 102 */           return false; } 
/* 103 */         if (this.bElseBlockReached && iSize.intValue() > 2) {
/*     */ 
/*     */           
/* 106 */           reportIssue(eachLineTree, "SOGETI --> .setAttributeValue() method is used more than once, use .setAttributeValues() of DomainRelationship");
/* 107 */           return true;
/* 108 */         }  if (!this.bElseBlockReached && iSize.intValue() > 1) {
/*     */ 
/*     */           
/* 111 */           reportIssue(eachLineTree, "SOGETI --> .setAttributeValue() method is used more than once, use .setAttributeValues() of DomainRelationship");
/* 112 */           return true;
/*     */         } 
/* 114 */         return false;
/*     */       } 
/*     */ 
/*     */       
/* 118 */       return false;
/*     */     } 
/* 120 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/* 125 */     ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree)eachLineTree;
/*     */     
/* 127 */     ExpressionTree expressionTree = expressionStatementTree.expression();
/* 128 */     if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 129 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 130 */     } else if ("ASSIGNMENT".equals(expressionTree.kind().toString())) {
/* 131 */       AssignmentExpressionTree assignmentExpressionTree = (AssignmentExpressionTree)expressionTree;
/* 132 */       invokeAssignmentExpressionStatementTreeMethod((Tree)assignmentExpressionTree.expression(), eachLineTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree tree, Tree eachLineTree) {
/* 137 */     if ("METHOD_INVOCATION".equals(tree.kind().toString())) {
/* 138 */       invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     }
/*     */   }
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree tree, Tree eachLineTree) {
/* 143 */     MethodInvocationTree methodInvocationTree = (MethodInvocationTree)tree;
/* 144 */     ExpressionTree expressionTree = methodInvocationTree.methodSelect();
/*     */     
/* 146 */     String strArguments = "";
/* 147 */     String strReturn = "";
/* 148 */     if (methodInvocationTree.arguments().size() != 0) {
/* 149 */       Arguments arguments = methodInvocationTree.arguments();
/* 150 */       strArguments = "(";
/* 151 */       strReturn = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree, (List<? extends Tree>)arguments);
/* 152 */       strArguments = strArguments + "--)";
/* 153 */       strArguments = strArguments.replace(", --", "");
/*     */     } else {
/* 155 */       strArguments = "()";
/*     */     } 
/*     */     
/* 158 */     return strReturn + strArguments;
/*     */   }
/*     */   
/*     */   private String invokeMemberSelectMethod(Tree tree, Tree eachLineTree, List<? extends Tree> lMethodInvocationTreeArgs) {
/* 162 */     String strMethodSelection = "";
/* 163 */     if ("MEMBER_SELECT".equals(tree.kind().toString())) {
/*     */       
/* 165 */       MemberSelectExpressionTree memberSelectExpressionTree = (MemberSelectExpressionTree)tree;
/* 166 */       String strMethodCalledBy = memberSelectExpressionTree.firstToken().text();
/* 167 */       String strMethodCalled = memberSelectExpressionTree.identifier().name();
/* 168 */       strMethodSelection = strMethodCalledBy + "." + strMethodCalled;
/*     */       
/* 170 */       if (strMethodCalled.equals("setAttributeValue") && strMethodCalledBy.equals("DomainRelationship")) {
/*     */         
/* 172 */         String strRelationshipId = ((Tree)lMethodInvocationTreeArgs.get(1)).toString();
/* 173 */         bumpUpCount(strRelationshipId);
/* 174 */         bCheckAndReportIssueNow(strRelationshipId, eachLineTree);
/*     */       } 
/*     */     } 
/* 177 */     return strMethodSelection;
/*     */   }
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree tree) {
/* 181 */     IfStatementTree ifStatementTree = (IfStatementTree)tree;
/*     */ 
/*     */     
/* 184 */     ExpressionTree expressionTree = ifStatementTree.condition();
/*     */     
/* 186 */     if ("LOGICAL_COMPLEMENT".equals(expressionTree.kind().toString())) {
/* 187 */       UnaryExpressionTree unaryExpressionTree = (UnaryExpressionTree)expressionTree;
/* 188 */       ExpressionTree newExpressionTree = unaryExpressionTree.expression();
/*     */       
/* 190 */       if ("METHOD_INVOCATION".equals(newExpressionTree.kind().toString())) {
/* 191 */         invokeMethodInvocationTreeMethod((Tree)newExpressionTree, tree);
/*     */       }
/* 193 */     } else if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 194 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, tree);
/*     */     } 
/*     */ 
/*     */     
/* 198 */     StatementTree statementTree = ifStatementTree.thenStatement();
/*     */     
/* 200 */     invokeIfElseStatementTreeMethod(statementTree);
/*     */ 
/*     */     
/*     */     try {
/* 204 */       while (ifStatementTree.elseStatement() != null) {
/* 205 */         statementTree = ifStatementTree.elseStatement();
/*     */         
/* 207 */         if ("IF_STATEMENT".equals(statementTree.kind().toString())) {
/* 208 */           ifStatementTree = (IfStatementTree)statementTree;
/* 209 */           StatementTree newStatementTree = ifStatementTree.thenStatement();
/* 210 */           invokeIfElseStatementTreeMethod(newStatementTree);
/*     */           continue;
/*     */         } 
/* 213 */         this.bElseBlockReached = true;
/*     */         
/* 215 */         invokeIfElseStatementTreeMethod(statementTree);
/* 216 */         ifStatementTree = null;
/*     */       }
/*     */     
/* 219 */     } catch (Exception ex) {
/* 220 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree statementTree) {
/* 226 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/*     */       
/* 228 */       BlockTree blockTree = (BlockTree)statementTree;
/* 229 */       if (blockTree.kind().toString().equals("BLOCK")) {
/* 230 */         checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */       }
/*     */       
/* 233 */       if (this.bElseBlockReached)
/* 234 */         this.bElseBlockReached = false; 
/* 235 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 236 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 241 */     ForStatementTree forStatementTree = (ForStatementTree)forLoopTree;
/*     */     
/* 243 */     StatementTree statementTree = forStatementTree.statement();
/* 244 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 245 */       BlockTree blockTree = (BlockTree)forStatementTree.statement();
/* 246 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 247 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 248 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 253 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/*     */     
/* 255 */     BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 256 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */   }
/*     */   
/*     */   private void addressEachTree(List<? extends Tree> listOfTrees) {
/* 260 */     Tree eachLineTree = null;
/* 261 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 262 */       eachLineTree = listOfTrees.get(iLine);
/*     */       
/* 264 */       if ("EXPRESSION_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 265 */         invokeExpressionStatementTreeMethod(eachLineTree);
/*     */       }
/* 267 */       else if ("TRY_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 268 */         TryStatementTree tryStatementTree = (TryStatementTree)eachLineTree;
/* 269 */         BlockTree blockTree = tryStatementTree.block();
/*     */ 
/*     */         
/* 272 */         checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */         
/* 274 */         List<? extends CatchTree> lTryStatementTreeCatches = tryStatementTree.catches();
/*     */         
/* 276 */         for (int iCatchCnt = 0; iCatchCnt < lTryStatementTreeCatches.size(); iCatchCnt++) {
/* 277 */           CatchTree catchTree = lTryStatementTreeCatches.get(iCatchCnt);
/* 278 */           BlockTree blockTreeCatch = catchTree.block();
/* 279 */           checkBlockBody(blockTreeCatch.openBraceToken(), blockTreeCatch.closeBraceToken(), blockTreeCatch.body());
/*     */         } 
/*     */         
/*     */         try {
/* 283 */           BlockTree blockTreeFinally = tryStatementTree.finallyBlock();
/*     */ 
/*     */           
/* 286 */           checkBlockBody(blockTreeFinally.openBraceToken(), blockTreeFinally.closeBraceToken(), blockTreeFinally.body());
/*     */         }
/* 288 */         catch (Exception ex) {
/* 289 */           log(" --- no finally block available exception -- " + ex);
/*     */         } 
/* 291 */       } else if ("IF_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 292 */         invokeIfStmtTreeMethod(eachLineTree);
/* 293 */       } else if ("FOR_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 294 */         invokeForStmtTreeMethod(eachLineTree);
/* 295 */       } else if ("FOR_EACH_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 296 */         invokeForEachStmtTreeMethod(eachLineTree);
/* 297 */       } else if ("WHILE_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 298 */         WhileStatementTree whileStatementTree = (WhileStatementTree)eachLineTree;
/* 299 */         StatementTree statementTree = whileStatementTree.statement();
/* 300 */         invokeIfElseStatementTreeMethod(statementTree);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMMultipleSetAttributeValueOnRelationshipRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */