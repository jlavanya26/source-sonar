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
/*     */ import org.sonar.plugins.java.api.tree.BinaryExpressionTree;
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
/*     */ @Rule(key = "PLMCheckRelSyntax")
/*     */ public class PLMCheckRelSyntaxRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  37 */   public int max = 2;
/*  38 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  41 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  45 */     MethodTree methodTree = (MethodTree)tree;
/*  46 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  47 */     Type returnType = methodSymbol.returnType().type();
/*  48 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  49 */       Type argType = methodSymbol.parameterTypes().get(0);
/*  50 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  55 */     BlockTree blocktree = methodTree.block();
/*  56 */     this.htReportIssue = new Hashtable<>();
/*  57 */     if (blocktree != null)
/*  58 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body()); 
/*     */   }
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  62 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty())
/*  63 */       addressEachTree(trees); 
/*     */   }
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/*  67 */     ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree)eachLineTree;
/*  68 */     ExpressionTree expressionTree = expressionStatementTree.expression();
/*  69 */     if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/*  70 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/*  71 */     } else if (expressionTree.kind().toString().equals("ASSIGNMENT")) {
/*  72 */       AssignmentExpressionTree assignmentExpressionTree = (AssignmentExpressionTree)expressionTree;
/*  73 */       invokeAssignmentExpressionStatementTreeMethod((Tree)assignmentExpressionTree.expression(), eachLineTree);
/*     */     } 
/*     */   }
/*     */   private void invokeTypeCastTreeMethod(Tree tree, Tree eachLineTree) {
/*  77 */     TypeCastTree typeCastTree = (TypeCastTree)tree;
/*  78 */     ExpressionTree expressionTree = typeCastTree.expression();
/*  79 */     if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/*  80 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/*  81 */     } else if (expressionTree.kind().toString().equals("MEMBER_SELECT")) {
/*  82 */       invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/*     */     } 
/*     */   }
/*     */   
/*  86 */   private void invokeAssignmentExpressionStatementTreeMethod(Tree tree, Tree eachLineTree) { if (tree.kind().toString().equals("TYPE_CAST")) {
/*  87 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/*  88 */     } else if (tree.kind().toString().equals("PLUS")) {
/*  89 */       BinaryExpressionTree binaryExpressionTree = (BinaryExpressionTree)tree;
/*  90 */       invokeRestrictedArgumentCheck(binaryExpressionTree.firstToken().text(), tree);
/*  91 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/*  92 */       invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     }  } private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/*  96 */     VariableTree variableTree = (VariableTree)eachLineTree;
/*  97 */     VariableTree newVariabaleTree = (VariableTree)variableTree.symbol().declaration();
/*  98 */     VariableTree variableTree1 = newVariabaleTree;
/*     */     try {
/* 100 */       expressionTree = newVariabaleTree.initializer();
/* 101 */     } catch (Exception ex) {
/* 102 */       if (expressionTree == null) {
/* 103 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/* 106 */     if (tree == null) {
/* 107 */       tree = eachLineTree;
/*     */     }
/* 109 */     if (tree.kind().toString().equals("PLUS")) {
/* 110 */       BinaryExpressionTree bt = (BinaryExpressionTree)tree;
/* 111 */       invokeRestrictedArgumentCheck(bt.firstToken().text(), tree);
/* 112 */     } else if (tree.kind().toString().equals("TYPE_CAST")) {
/* 113 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 114 */     } else if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 115 */       invokeMemberSelectMethod(tree, eachLineTree);
/* 116 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 117 */       invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     } 
/*     */   }
/*     */   private String invokeMethodInvocationTreeMethod(Tree tree, Tree eachLineTree) {
/* 121 */     MethodInvocationTree methodInvocationTree = (MethodInvocationTree)tree;
/* 122 */     ExpressionTree expressionTree = methodInvocationTree.methodSelect();
/* 123 */     String strMethodName = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 124 */     String strMethodArgs = "";
/* 125 */     if (methodInvocationTree.arguments().size() != 0) {
/* 126 */       Arguments<Tree> arguments = methodInvocationTree.arguments();
/* 127 */       strMethodArgs = "(";
/* 128 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/* 129 */         invokeRestrictedArgumentCheck(((Tree)arguments.get(iArgCnt)).firstToken().text(), eachLineTree);
/* 130 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 131 */           TypeCastTree typeCastTree = (TypeCastTree)arguments.get(iArgCnt);
/* 132 */           invokeTypeCastTreeMethod((Tree)typeCastTree, eachLineTree);
/* 133 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 134 */           strMethodName = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 137 */       strMethodArgs = strMethodArgs + "--)";
/* 138 */       strMethodArgs = strMethodArgs.replace(", --", "");
/*     */     } else {
/* 140 */       strMethodArgs = "()";
/*     */     } 
/* 142 */     return strMethodName + strMethodArgs;
/*     */   }
/*     */   private String invokeMemberSelectMethod(Tree tree, Tree eachLineTree) {
/* 145 */     String strClassAndMethodName = "";
/* 146 */     if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 147 */       MemberSelectExpressionTree mset = (MemberSelectExpressionTree)tree;
/* 148 */       String declarationMethodName = mset.firstToken().text();
/* 149 */       String declarationCallingMethodName = mset.identifier().name();
/* 150 */       strClassAndMethodName = declarationMethodName + "." + declarationCallingMethodName;
/*     */     } 
/* 152 */     return strClassAndMethodName;
/*     */   }
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 155 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 156 */     StatementTree statementTree = forStmtTree.statement();
/* 157 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 158 */       BlockTree blockTree = (BlockTree)forStmtTree.statement();
/* 159 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 160 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 161 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 165 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 166 */     BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 167 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */   }
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 170 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/* 171 */     ExpressionTree expressionTree = ifStmtTree.condition();
/* 172 */     if ("LOGICAL_COMPLEMENT".equals(expressionTree.kind().toString())) {
/* 173 */       UnaryExpressionTree unaryExpressionTree = (UnaryExpressionTree)expressionTree;
/* 174 */       ExpressionTree newexpressionTree = unaryExpressionTree.expression();
/* 175 */       if ("METHOD_INVOCATION".equals(newexpressionTree.kind().toString())) {
/* 176 */         invokeMethodInvocationTreeMethod((Tree)newexpressionTree, ifLoopTree);
/*     */       }
/* 178 */     } else if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 179 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, ifLoopTree);
/*     */     } 
/* 181 */     StatementTree statementTree = ifStmtTree.thenStatement();
/* 182 */     invokeIfElseStatementTreeMethod(statementTree);
/*     */     try {
/* 184 */       while (ifStmtTree.elseStatement() != null) {
/* 185 */         statementTree = ifStmtTree.elseStatement();
/* 186 */         if ("IF_STATEMENT".equals(statementTree.kind().toString())) {
/* 187 */           ifStmtTree = (IfStatementTree)statementTree;
/* 188 */           StatementTree newstatementTree = ifStmtTree.thenStatement();
/* 189 */           invokeIfElseStatementTreeMethod(newstatementTree); continue;
/*     */         } 
/* 191 */         invokeIfElseStatementTreeMethod(statementTree);
/* 192 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 195 */     } catch (Exception exception) {}
/*     */   }
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree statementTree) {
/* 199 */     if (statementTree.kind().toString().equals("BLOCK")) {
/* 200 */       BlockTree blockTree = (BlockTree)statementTree;
/* 201 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 202 */     } else if (statementTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 203 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   private void addressEachTree(List<? extends Tree> listOfTrees) {
/* 207 */     Tree eachLineTree = null;
/* 208 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 209 */       eachLineTree = listOfTrees.get(iLine);
/* 210 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 211 */         invokeVariableTreeMethod(eachLineTree);
/* 212 */       } else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 213 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 214 */       } else if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 215 */         TryStatementTree tryStatementTree = (TryStatementTree)eachLineTree;
/* 216 */         BlockTree btTryStmtTree = tryStatementTree.block();
/* 217 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/* 218 */         List<? extends CatchTree> catches = tryStatementTree.catches();
/* 219 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 220 */           CatchTree catchTree = catches.get(iCatchCnt);
/* 221 */           BlockTree blockTreeCatch = catchTree.block();
/* 222 */           checkBlockBody(blockTreeCatch.openBraceToken(), blockTreeCatch.closeBraceToken(), blockTreeCatch.body());
/*     */         } 
/*     */         try {
/* 225 */           BlockTree btTryStmtFinallyTree = tryStatementTree.finallyBlock();
/* 226 */           checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/* 227 */         } catch (Exception exception) {}
/*     */       }
/* 229 */       else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 230 */         invokeForStmtTreeMethod(eachLineTree);
/* 231 */       } else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 232 */         invokeForEachStmtTreeMethod(eachLineTree);
/* 233 */       } else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 234 */         invokeIfStmtTreeMethod(eachLineTree);
/* 235 */       } else if (eachLineTree.kind().toString().equals("WHILE_STATEMENT")) {
/* 236 */         WhileStatementTree whileStatementTree = (WhileStatementTree)eachLineTree;
/* 237 */         StatementTree statementTree = whileStatementTree.statement();
/* 238 */         invokeIfElseStatementTreeMethod(statementTree);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeRestrictedArgumentCheck(String strArgument, Tree eachLineTree) {
/* 246 */     if (strArgument.contains("relationship[")) {
/*     */       
/* 248 */       bumpUpRestirctedArgumentCount(strArgument);
/* 249 */       bCheckAndReportRestrictedArgumentIssueNow(strArgument, eachLineTree);
/*     */     } 
/*     */   }
/*     */   private Hashtable<String, Integer> bumpUpRestirctedArgumentCount(String strArgument) {
/* 253 */     if (this.htReportIssue.containsKey(strArgument)) {
/* 254 */       Integer iSize = this.htReportIssue.get(strArgument);
/* 255 */       int iNewSize = iSize.intValue() + 1;
/* 256 */       this.htReportIssue.put(strArgument, Integer.valueOf(iNewSize));
/*     */     } else {
/* 258 */       this.htReportIssue.put(strArgument, Integer.valueOf(1));
/*     */     } 
/* 260 */     return this.htReportIssue;
/*     */   }
/*     */   private boolean bCheckAndReportRestrictedArgumentIssueNow(String strArgument, Tree eachLineTree) {
/* 263 */     if (this.htReportIssue.size() > 0);
/*     */     
/* 265 */     if (this.htReportIssue.containsKey(strArgument)) {
/* 266 */       reportIssue(eachLineTree, "ENOVIA --> relationship[  avoid this, try to comply with suggested solutions");
/* 267 */       return true;
/*     */     } 
/* 269 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMCheckRelSyntaxRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */