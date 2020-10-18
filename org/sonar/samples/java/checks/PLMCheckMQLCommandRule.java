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
/*     */ @Rule(key = "PLMCheckMQLCommand")
/*     */ public class PLMCheckMQLCommandRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*  35 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  38 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  42 */     MethodTree methodTree = (MethodTree)tree;
/*  43 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  44 */     Type returnType = methodSymbol.returnType().type();
/*  45 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  46 */       Type argType = methodSymbol.parameterTypes().get(0);
/*  47 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */     
/*  51 */     BlockTree blocktree = methodTree.block();
/*  52 */     this.htReportIssue = new Hashtable<>();
/*  53 */     if (blocktree != null)
/*  54 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body()); 
/*     */   }
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  58 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty())
/*  59 */       addressEachTree(trees); 
/*     */   }
/*     */   
/*     */   private Hashtable<String, Integer> bumpUpCount(String sMethodName) {
/*  63 */     if (this.htReportIssue.containsKey(sMethodName)) {
/*  64 */       Integer iSize = this.htReportIssue.get(sMethodName);
/*  65 */       int iNewSize = iSize.intValue() + 1;
/*  66 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/*  68 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/*  70 */     return this.htReportIssue;
/*     */   }
/*     */   private boolean bCheckAndReportIssueNow(String sMethodName, Tree eachLineTree) {
/*  73 */     if (this.htReportIssue.containsKey(sMethodName)) {
/*  74 */       reportIssue(eachLineTree, "ENOVIA -->Avoid MQLCommand/MqlUtil class instead use corresponding ADK");
/*  75 */       return true;
/*     */     } 
/*  77 */     return false;
/*     */   }
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/*  81 */     ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree)eachLineTree;
/*  82 */     ExpressionTree expressionTree = expressionStatementTree.expression();
/*  83 */     if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/*  84 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/*  85 */     } else if (expressionTree.kind().toString().equals("ASSIGNMENT")) {
/*  86 */       AssignmentExpressionTree assignmentExpressionTree = (AssignmentExpressionTree)expressionTree;
/*  87 */       invokeAssignmentExpressionStatementTreeMethod((Tree)assignmentExpressionTree.expression(), eachLineTree);
/*     */     } 
/*     */   }
/*     */   private void invokeTypeCastTreeMethod(Tree tree, Tree eachLineTree) {
/*  91 */     TypeCastTree typeCastTree = (TypeCastTree)tree;
/*  92 */     ExpressionTree expressionTree = typeCastTree.expression();
/*  93 */     if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/*  94 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/*  95 */     } else if (expressionTree.kind().toString().equals("MEMBER_SELECT")) {
/*  96 */       invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/*     */     } 
/*     */   }
/*     */   
/* 100 */   private void invokeAssignmentExpressionStatementTreeMethod(Tree tree, Tree eachLineTree) { if (tree.kind().toString().equals("TYPE_CAST")) {
/* 101 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 102 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 103 */       invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     }  } private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 107 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 108 */     VariableTree newVariableTree = (VariableTree)variableTree.symbol().declaration();
/* 109 */     VariableTree variableTree1 = newVariableTree;
/*     */     try {
/* 111 */       expressionTree = newVariableTree.initializer();
/* 112 */     } catch (Exception ex) {
/* 113 */       if (expressionTree == null) {
/* 114 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/* 117 */     if (tree == null) {
/* 118 */       tree = eachLineTree;
/*     */     }
/* 120 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 121 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 122 */     } else if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 123 */       invokeMemberSelectMethod(tree, eachLineTree);
/* 124 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 125 */       invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     } 
/*     */   }
/*     */   private String invokeMethodInvocationTreeMethod(Tree tree, Tree eachLineTree) {
/* 129 */     MethodInvocationTree methodInvocationTree = (MethodInvocationTree)tree;
/* 130 */     ExpressionTree expressionTree = methodInvocationTree.methodSelect();
/* 131 */     String strClassMethodName = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 132 */     String strMethodArgs = "";
/* 133 */     if (methodInvocationTree.arguments().size() != 0) {
/* 134 */       Arguments<Tree> arguments = methodInvocationTree.arguments();
/* 135 */       strMethodArgs = "(";
/* 136 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/* 137 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 138 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 139 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 140 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 141 */           strClassMethodName = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 144 */       strMethodArgs = strMethodArgs + "--)";
/* 145 */       strMethodArgs = strMethodArgs.replace(", --", "");
/*     */     } else {
/* 147 */       strMethodArgs = "()";
/*     */     } 
/* 149 */     return strClassMethodName + strMethodArgs;
/*     */   }
/*     */ 
/*     */   
/*     */   private String invokeMemberSelectMethod(Tree tree, Tree eachLineTree) {
/* 154 */     String strClassMethodName = "";
/* 155 */     if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 156 */       MemberSelectExpressionTree mset = (MemberSelectExpressionTree)tree;
/* 157 */       String strDeclarationMethodName = mset.firstToken().text();
/* 158 */       String strDeclarationCallingMethodName = mset.identifier().name();
/* 159 */       strClassMethodName = strDeclarationMethodName + "." + strDeclarationCallingMethodName;
/*     */       
/* 161 */       if (strDeclarationMethodName.equals("MQLCommand") || strDeclarationMethodName.equals("MqlUtil")) {
/* 162 */         bumpUpCount(strDeclarationCallingMethodName);
/* 163 */         bCheckAndReportIssueNow(strDeclarationCallingMethodName, eachLineTree);
/*     */       } 
/*     */     } 
/* 166 */     return strClassMethodName;
/*     */   }
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 169 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 170 */     StatementTree statementTree = forStmtTree.statement();
/* 171 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 172 */       BlockTree blockTree = (BlockTree)forStmtTree.statement();
/* 173 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 174 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 175 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 179 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 180 */     BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 181 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */   }
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 184 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/* 185 */     ExpressionTree expressionTreet = ifStmtTree.condition();
/* 186 */     if ("LOGICAL_COMPLEMENT".equals(expressionTreet.kind().toString())) {
/* 187 */       UnaryExpressionTree unaryExpressionTree = (UnaryExpressionTree)expressionTreet;
/* 188 */       ExpressionTree newExpressionTree = unaryExpressionTree.expression();
/* 189 */       if ("METHOD_INVOCATION".equals(newExpressionTree.kind().toString())) {
/* 190 */         invokeMethodInvocationTreeMethod((Tree)newExpressionTree, ifLoopTree);
/*     */       }
/* 192 */     } else if ("METHOD_INVOCATION".equals(expressionTreet.kind().toString())) {
/* 193 */       invokeMethodInvocationTreeMethod((Tree)expressionTreet, ifLoopTree);
/*     */     } 
/* 195 */     StatementTree statementTree = ifStmtTree.thenStatement();
/* 196 */     invokeIfElseStatementTreeMethod(statementTree);
/*     */     try {
/* 198 */       while (ifStmtTree.elseStatement() != null) {
/* 199 */         statementTree = ifStmtTree.elseStatement();
/* 200 */         if ("IF_STATEMENT".equals(statementTree.kind().toString())) {
/* 201 */           ifStmtTree = (IfStatementTree)statementTree;
/* 202 */           StatementTree newStatementTree = ifStmtTree.thenStatement();
/* 203 */           invokeIfElseStatementTreeMethod(newStatementTree); continue;
/*     */         } 
/* 205 */         invokeIfElseStatementTreeMethod(statementTree);
/* 206 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 209 */     } catch (Exception exception) {}
/*     */   }
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree statementTree) {
/* 213 */     if (statementTree.kind().toString().equals("BLOCK")) {
/* 214 */       BlockTree blockTree = (BlockTree)statementTree;
/* 215 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 216 */     } else if (statementTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 217 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   private void addressEachTree(List<? extends Tree> listOfTrees) {
/* 221 */     Tree eachLineTree = null;
/* 222 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 223 */       eachLineTree = listOfTrees.get(iLine);
/* 224 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 225 */         invokeVariableTreeMethod(eachLineTree);
/* 226 */       } else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 227 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 228 */       } else if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 229 */         TryStatementTree tryStatementTree = (TryStatementTree)eachLineTree;
/* 230 */         BlockTree blockTree = tryStatementTree.block();
/* 231 */         checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 232 */         List<? extends CatchTree> catches = tryStatementTree.catches();
/* 233 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 234 */           CatchTree catchTree = catches.get(iCatchCnt);
/* 235 */           BlockTree blockTreeCatch = catchTree.block();
/* 236 */           checkBlockBody(blockTreeCatch.openBraceToken(), blockTreeCatch.closeBraceToken(), blockTreeCatch.body());
/*     */         } 
/*     */         try {
/* 239 */           BlockTree btTryStmtFinallyTree = tryStatementTree.finallyBlock();
/* 240 */           checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/* 241 */         } catch (Exception exception) {}
/*     */       }
/* 243 */       else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 244 */         invokeForStmtTreeMethod(eachLineTree);
/*     */       }
/* 246 */       else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 247 */         invokeForEachStmtTreeMethod(eachLineTree);
/* 248 */       } else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 249 */         invokeIfStmtTreeMethod(eachLineTree);
/* 250 */       } else if (eachLineTree.kind().toString().equals("WHILE_STATEMENT")) {
/* 251 */         WhileStatementTree whileStatementTree = (WhileStatementTree)eachLineTree;
/* 252 */         StatementTree statementTree = whileStatementTree.statement();
/* 253 */         invokeIfElseStatementTreeMethod(statementTree);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMCheckMQLCommandRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */