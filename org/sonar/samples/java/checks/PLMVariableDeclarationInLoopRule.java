/*     */ package org.sonar.samples.java.checks;
/*     */ 
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.sonar.check.Rule;
/*     */ import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
/*     */ import org.sonar.plugins.java.api.tree.BlockTree;
/*     */ import org.sonar.plugins.java.api.tree.CatchTree;
/*     */ import org.sonar.plugins.java.api.tree.DoWhileStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.ForEachStatement;
/*     */ import org.sonar.plugins.java.api.tree.ForStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.IfStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.MethodTree;
/*     */ import org.sonar.plugins.java.api.tree.StatementTree;
/*     */ import org.sonar.plugins.java.api.tree.SyntaxToken;
/*     */ import org.sonar.plugins.java.api.tree.Tree;
/*     */ import org.sonar.plugins.java.api.tree.TryStatementTree;
/*     */ import org.sonar.plugins.java.api.tree.VariableTree;
/*     */ import org.sonar.plugins.java.api.tree.WhileStatementTree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Rule(key = "PLMVariableDeclarationInLoop")
/*     */ public class PLMVariableDeclarationInLoopRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  33 */   public int max = 2;
/*     */   
/*     */   boolean bLoggingActive = false;
/*     */   
/*  37 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */ 
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  41 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  46 */     MethodTree methodTree = (MethodTree)tree;
/*  47 */     BlockTree blocktree = methodTree.block();
/*  48 */     this.htReportIssue = new Hashtable<>();
/*  49 */     if (blocktree != null) {
/*  50 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  55 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  56 */       addressEachTree(trees);
/*     */     }
/*     */   }
/*     */   
/*     */   private void log(String strLog) {
/*  61 */     if (this.bLoggingActive) {
/*  62 */       System.out.println(strLog);
/*     */     }
/*     */   }
/*     */   
/*     */   private Hashtable<String, Integer> bumpUpCount(String strLoopName) {
/*  67 */     if (this.htReportIssue.containsKey(strLoopName)) {
/*  68 */       Integer iSize = this.htReportIssue.get(strLoopName);
/*  69 */       int iNewSize = iSize.intValue() + 1;
/*  70 */       this.htReportIssue.put(strLoopName, Integer.valueOf(iNewSize));
/*     */     } else {
/*  72 */       this.htReportIssue.put(strLoopName, Integer.valueOf(1));
/*     */     } 
/*  74 */     return this.htReportIssue;
/*     */   }
/*     */   
/*     */   private boolean bCheckAndReportIssueNow(String strVariableName, Tree eachLineTree) {
/*  78 */     if (this.htReportIssue.size() > 0) {
/*  79 */       log("Report ht : " + this.htReportIssue);
/*     */     }
/*  81 */     int iValuesSize = this.htReportIssue.values().size();
/*  82 */     if (iValuesSize > 0) {
/*  83 */       Iterator<Integer> itr = this.htReportIssue.values().iterator();
/*  84 */       while (itr.hasNext()) {
/*     */ 
/*     */ 
/*     */         
/*  88 */         if (((Integer)itr.next()).intValue() > 0) {
/*     */           
/*  90 */           reportIssue(eachLineTree, "SOGETI --> " + strVariableName + " variable is declared in a Loop, please declare it outside of the Loop");
/*  91 */           return true;
/*     */         } 
/*     */       } 
/*     */     } 
/*  95 */     return false;
/*     */   }
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree tree) {
/*  99 */     IfStatementTree ifStatementTree = (IfStatementTree)tree;
/*     */     
/* 101 */     StatementTree statementTree = ifStatementTree.thenStatement();
/* 102 */     invokeIfElseStatementTreeMethod(statementTree);
/*     */     
/*     */     try {
/* 105 */       while (ifStatementTree.elseStatement() != null) {
/* 106 */         statementTree = ifStatementTree.elseStatement();
/*     */         
/* 108 */         if ("IF_STATEMENT".equals(statementTree.kind().toString())) {
/* 109 */           ifStatementTree = (IfStatementTree)statementTree;
/* 110 */           StatementTree newStatementTree = ifStatementTree.thenStatement();
/* 111 */           invokeIfElseStatementTreeMethod(newStatementTree);
/*     */           continue;
/*     */         } 
/* 114 */         invokeIfElseStatementTreeMethod(statementTree);
/* 115 */         ifStatementTree = null;
/*     */       }
/*     */     
/* 118 */     } catch (Exception ex) {
/* 119 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree statementTree) {
/* 124 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 125 */       BlockTree blockTree = (BlockTree)statementTree;
/* 126 */       if (blockTree.kind().toString().equals("BLOCK")) {
/* 127 */         checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 133 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/*     */     
/* 135 */     StatementTree statementTree = forStmtTree.statement();
/* 136 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 137 */       BlockTree blockTree = (BlockTree)forStmtTree.statement();
/* 138 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */       
/* 140 */       bumpDownCount("ForLoop");
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 145 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/*     */     
/* 147 */     BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 148 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */     
/* 150 */     bumpDownCount("ForEachLoop");
/*     */   }
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 154 */     Tree eachLineTree = null;
/* 155 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 156 */       eachLineTree = listOfTrees.get(iLine);
/* 157 */       if ("VARIABLE".equals(eachLineTree.kind().toString())) {
/* 158 */         VariableTree variableTree = (VariableTree)eachLineTree;
/* 159 */         String strVariableName = variableTree.symbol().name();
/* 160 */         bCheckAndReportIssueNow(strVariableName, eachLineTree);
/* 161 */       } else if ("IF_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 162 */         invokeIfStmtTreeMethod(eachLineTree);
/* 163 */       } else if ("FOR_STATEMENT".equals(eachLineTree.kind().toString())) {
/*     */         
/* 165 */         bumpUpCount("ForLoop");
/* 166 */         invokeForStmtTreeMethod(eachLineTree);
/* 167 */       } else if ("FOR_EACH_STATEMENT".equals(eachLineTree.kind().toString())) {
/*     */         
/* 169 */         bumpUpCount("ForEachLoop");
/* 170 */         invokeForEachStmtTreeMethod(eachLineTree);
/* 171 */       } else if ("WHILE_STATEMENT".equals(eachLineTree.kind().toString())) {
/*     */         
/* 173 */         bumpUpCount("WhileLoop");
/* 174 */         WhileStatementTree whileStatementTree = (WhileStatementTree)eachLineTree;
/* 175 */         StatementTree statementTree = whileStatementTree.statement();
/* 176 */         invokeWhileStatementTreeMethod(statementTree);
/* 177 */       } else if ("DO_STATEMENT".equals(eachLineTree.kind().toString())) {
/*     */         
/* 179 */         bumpUpCount("DoWhileLoop");
/* 180 */         invokeDoWhileStmtTreeMethod(eachLineTree);
/* 181 */       } else if ("TRY_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 182 */         TryStatementTree tryStatementTree = (TryStatementTree)eachLineTree;
/* 183 */         BlockTree blockTree = tryStatementTree.block();
/*     */ 
/*     */         
/* 186 */         checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */         
/* 188 */         List<? extends CatchTree> lTryStatementTreeCatches = tryStatementTree.catches();
/*     */         
/* 190 */         for (int iCatchCnt = 0; iCatchCnt < lTryStatementTreeCatches.size(); iCatchCnt++) {
/* 191 */           CatchTree catchTree = lTryStatementTreeCatches.get(iCatchCnt);
/* 192 */           BlockTree blockTreeCatch = catchTree.block();
/* 193 */           checkBlockBody(blockTreeCatch.openBraceToken(), blockTreeCatch.closeBraceToken(), blockTreeCatch.body());
/*     */         } 
/*     */         try {
/* 196 */           BlockTree blockTreeFinally = tryStatementTree.finallyBlock();
/*     */ 
/*     */           
/* 199 */           checkBlockBody(blockTreeFinally.openBraceToken(), blockTreeFinally.closeBraceToken(), blockTreeFinally.body());
/*     */         }
/* 201 */         catch (Exception ex) {
/* 202 */           log(" --- no finally block available exception -- " + ex);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeWhileStatementTreeMethod(StatementTree statementTree) {
/* 209 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 210 */       BlockTree blockTree = (BlockTree)statementTree;
/* 211 */       if (blockTree.kind().toString().equals("BLOCK")) {
/* 212 */         checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */       }
/*     */       
/* 215 */       bumpDownCount("WhileLoop");
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeDoWhileStmtTreeMethod(Tree doWhileLoopTree) {
/* 220 */     DoWhileStatementTree doWhileStatementTree = (DoWhileStatementTree)doWhileLoopTree;
/*     */     
/* 222 */     StatementTree statementTree = doWhileStatementTree.statement();
/* 223 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 224 */       BlockTree blockTree = (BlockTree)doWhileStatementTree.statement();
/* 225 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */       
/* 227 */       bumpDownCount("DoWhileLoop");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private Hashtable<String, Integer> bumpDownCount(String strVariableName) {
/* 233 */     if (this.htReportIssue.containsKey(strVariableName)) {
/* 234 */       Integer iSize = this.htReportIssue.get(strVariableName);
/* 235 */       int iNewSize = iSize.intValue() - 1;
/* 236 */       this.htReportIssue.put(strVariableName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 238 */       this.htReportIssue.put(strVariableName, Integer.valueOf(1));
/*     */     } 
/* 240 */     return this.htReportIssue;
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMVariableDeclarationInLoopRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */