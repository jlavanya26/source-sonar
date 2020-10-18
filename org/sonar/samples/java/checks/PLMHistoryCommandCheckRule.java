/*     */ package org.sonar.samples.java.checks;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import org.sonar.check.Rule;
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
/*     */ @Rule(key = "PLMHistoryCommandCheck")
/*     */ public class PLMHistoryCommandCheckRule extends IssuableSubscriptionVisitor {
/*     */   public boolean bIsHistoryOn = false;
/*     */   public boolean bIsHistoryOff = false;
/*     */   public boolean bIsHistoryOnFinally = false;
/*  34 */   private Hashtable<String, Integer> htReportIssue = null;
/*     */   
/*     */   public List<Tree.Kind> nodesToVisit() {
/*  37 */     return (List<Tree.Kind>)ImmutableList.of(Tree.Kind.METHOD);
/*     */   }
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  41 */     MethodTree methodTree = (MethodTree)tree;
/*  42 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  43 */     Type returnType = methodSymbol.returnType().type();
/*  44 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  45 */       Type argType = methodSymbol.parameterTypes().get(0);
/*  46 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */     
/*  50 */     BlockTree blockTree = methodTree.block();
/*  51 */     this.htReportIssue = new Hashtable<>();
/*  52 */     if (blockTree != null)
/*  53 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body()); 
/*     */   }
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  57 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty())
/*  58 */       addressEachTree(trees); 
/*     */   }
/*     */   
/*     */   private Hashtable<String, Integer> bumpUpCount(String strMethodName) {
/*  62 */     if (this.htReportIssue.containsKey(strMethodName)) {
/*  63 */       Integer iSize = this.htReportIssue.get(strMethodName);
/*  64 */       int iNewSize = iSize.intValue() + 1;
/*  65 */       this.htReportIssue.put(strMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/*  67 */       this.htReportIssue.put(strMethodName, Integer.valueOf(1));
/*     */     } 
/*  69 */     return this.htReportIssue;
/*     */   }
/*     */   private boolean bCheckAndReportIssueNow(String strMethodName, Tree eachLineTree) {
/*  72 */     if (this.htReportIssue.size() > 0);
/*     */     
/*  74 */     if (this.htReportIssue.containsKey(strMethodName)) {
/*  75 */       reportIssue(eachLineTree, "ENOVIA --> history off is used , avoid to use this  / if no way around  handle properly wih history on/history off in finally");
/*  76 */       return true;
/*     */     } 
/*  78 */     return false;
/*     */   }
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/*  82 */     ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree)eachLineTree;
/*  83 */     ExpressionTree expressionTree = expressionStatementTree.expression();
/*  84 */     if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/*  85 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/*  86 */     } else if (expressionTree.kind().toString().equals("ASSIGNMENT")) {
/*  87 */       AssignmentExpressionTree assignmentExpressionTree = (AssignmentExpressionTree)expressionTree;
/*  88 */       invokeAssignmentExpressionStatementTreeMethod((Tree)assignmentExpressionTree.expression(), eachLineTree);
/*     */     } 
/*     */   }
/*     */   private void invokeTypeCastTreeMethod(Tree tree, Tree eachLineTree) {
/*  92 */     TypeCastTree typeCastTree = (TypeCastTree)tree;
/*  93 */     ExpressionTree expressionTree = typeCastTree.expression();
/*  94 */     if (expressionTree.kind().toString().equals("METHOD_INVOCATION")) {
/*  95 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/*  96 */     } else if (expressionTree.kind().toString().equals("MEMBER_SELECT")) {
/*  97 */       invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/*     */     } 
/*     */   }
/*     */   
/* 101 */   private void invokeAssignmentExpressionStatementTreeMethod(Tree tree, Tree eachLineTree) { if (tree.kind().toString().equals("TYPE_CAST")) {
/* 102 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 103 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 104 */       invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     }  } private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 108 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 109 */     VariableTree newVariableTree = (VariableTree)variableTree.symbol().declaration();
/* 110 */     VariableTree variableTree1 = newVariableTree;
/*     */     try {
/* 112 */       expressionTree = newVariableTree.initializer();
/* 113 */     } catch (Exception exception) {}
/*     */     
/* 115 */     if (expressionTree == null) {
/* 116 */       tree = eachLineTree;
/*     */     }
/* 118 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 119 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 120 */     } else if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 121 */       invokeMemberSelectMethod(tree, eachLineTree);
/* 122 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 123 */       invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     } 
/*     */   }
/*     */   private String invokeMethodInvocationTreeMethod(Tree tree, Tree eachLineTree) {
/* 127 */     MethodInvocationTree methodInvocationTree = (MethodInvocationTree)tree;
/* 128 */     ExpressionTree expressionTree = methodInvocationTree.methodSelect();
/* 129 */     String strClassMethodName = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 130 */     String strMethodArgs = "";
/* 131 */     if (methodInvocationTree.arguments().size() != 0) {
/* 132 */       Arguments<Tree> arguments = methodInvocationTree.arguments();
/* 133 */       strMethodArgs = "(";
/* 134 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/* 135 */         if (((Tree)arguments.get(iArgCnt)).kind().toString().equals("STRING_LITERAL")) {
/* 136 */           invokeMemberSelectMethodForHistoryOff((Tree)expressionTree, eachLineTree, ((Tree)arguments.get(iArgCnt)).firstToken().text());
/*     */         }
/* 138 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 139 */           TypeCastTree typeCastTree = (TypeCastTree)arguments.get(iArgCnt);
/* 140 */           invokeTypeCastTreeMethod((Tree)typeCastTree, eachLineTree);
/* 141 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 142 */           strClassMethodName = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 145 */       strMethodArgs = strMethodArgs + "--)";
/* 146 */       strMethodArgs = strMethodArgs.replace(", --", "");
/*     */     } else {
/* 148 */       strMethodArgs = "()";
/*     */     } 
/* 150 */     return strClassMethodName + strMethodArgs;
/*     */   }
/*     */   private String invokeMemberSelectMethod(Tree tree, Tree eachLineTree) {
/* 153 */     String strClassAndMethodName = "";
/* 154 */     if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 155 */       MemberSelectExpressionTree memberSelectExpressionTree = (MemberSelectExpressionTree)tree;
/* 156 */       String strDeclarationMethodName = memberSelectExpressionTree.firstToken().text();
/* 157 */       String strDeclarationCallingMethodName = memberSelectExpressionTree.identifier().name();
/* 158 */       strClassAndMethodName = strDeclarationMethodName + "." + strDeclarationCallingMethodName;
/*     */     } 
/* 160 */     return strClassAndMethodName;
/*     */   }
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 163 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 164 */     StatementTree statementTree = forStmtTree.statement();
/* 165 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 166 */       BlockTree blockTree = (BlockTree)forStmtTree.statement();
/* 167 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 168 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 169 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 173 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 174 */     BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 175 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */   }
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 178 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/* 179 */     ExpressionTree expressionTree = ifStmtTree.condition();
/* 180 */     if ("LOGICAL_COMPLEMENT".equals(expressionTree.kind().toString())) {
/* 181 */       UnaryExpressionTree unaryExpressoionTree = (UnaryExpressionTree)expressionTree;
/* 182 */       ExpressionTree newExpressionTree = unaryExpressoionTree.expression();
/* 183 */       if ("METHOD_INVOCATION".equals(newExpressionTree.kind().toString())) {
/* 184 */         invokeMethodInvocationTreeMethod((Tree)newExpressionTree, ifLoopTree);
/*     */       }
/* 186 */     } else if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 187 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, ifLoopTree);
/*     */     } 
/* 189 */     StatementTree statementTree = ifStmtTree.thenStatement();
/* 190 */     invokeIfElseStatementTreeMethod(statementTree);
/*     */     try {
/* 192 */       while (ifStmtTree.elseStatement() != null) {
/* 193 */         statementTree = ifStmtTree.elseStatement();
/* 194 */         if ("IF_STATEMENT".equals(statementTree.kind().toString())) {
/* 195 */           ifStmtTree = (IfStatementTree)statementTree;
/* 196 */           StatementTree newStatementTree = ifStmtTree.thenStatement();
/* 197 */           invokeIfElseStatementTreeMethod(newStatementTree); continue;
/*     */         } 
/* 199 */         invokeIfElseStatementTreeMethod(statementTree);
/* 200 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 203 */     } catch (Exception exception) {}
/*     */   }
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree statementTree) {
/* 207 */     if (statementTree.kind().toString().equals("BLOCK")) {
/* 208 */       BlockTree blockTree = (BlockTree)statementTree;
/* 209 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 210 */     } else if (statementTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 211 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   private void addressEachTree(List<? extends Tree> listOfTrees) {
/* 215 */     Tree eachLineTree = null;
/* 216 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 217 */       eachLineTree = listOfTrees.get(iLine);
/* 218 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 219 */         invokeVariableTreeMethod(eachLineTree);
/* 220 */       } else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 221 */         invokeExpressionStatementTreeMethod(eachLineTree);
/*     */       }
/* 223 */       else if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 224 */         TryStatementTree tryStatementTree = (TryStatementTree)eachLineTree;
/* 225 */         BlockTree btTryStmtTree = tryStatementTree.block();
/* 226 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */         
/* 228 */         List<? extends CatchTree> catches = tryStatementTree.catches();
/*     */         
/* 230 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 231 */           CatchTree catchTree = catches.get(iCatchCnt);
/* 232 */           BlockTree blockTreeCatch = catchTree.block();
/* 233 */           checkBlockBody(blockTreeCatch.openBraceToken(), blockTreeCatch.closeBraceToken(), blockTreeCatch.body());
/*     */         } 
/*     */         try {
/* 236 */           BlockTree btTryStmtFinallyTree = tryStatementTree.finallyBlock();
/* 237 */           if (btTryStmtFinallyTree.kind().toString().equals("BLOCK")) {
/* 238 */             this.bIsHistoryOnFinally = true;
/* 239 */             checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */           } 
/* 241 */         } catch (Exception exception) {}
/*     */       
/*     */       }
/* 244 */       else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 245 */         invokeForStmtTreeMethod(eachLineTree);
/*     */       }
/* 247 */       else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 248 */         invokeForEachStmtTreeMethod(eachLineTree);
/*     */       }
/* 250 */       else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 251 */         invokeIfStmtTreeMethod(eachLineTree);
/*     */       }
/* 253 */       else if (eachLineTree.kind().toString().equals("WHILE_STATEMENT")) {
/* 254 */         WhileStatementTree wst = (WhileStatementTree)eachLineTree;
/* 255 */         StatementTree st = wst.statement();
/* 256 */         invokeIfElseStatementTreeMethod(st);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeMemberSelectMethodForHistoryOff(Tree tree, Tree eachLineTree, String strArgument) {
/* 264 */     if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 265 */       MemberSelectExpressionTree memeberSelectExpressionTree = (MemberSelectExpressionTree)tree;
/* 266 */       String strDeclarationMethodName = memeberSelectExpressionTree.firstToken().text();
/* 267 */       String strDeclarationCallingMethodName = memeberSelectExpressionTree.identifier().name();
/* 268 */       if (memeberSelectExpressionTree.identifier().name().equals("mqlCommand") && strDeclarationMethodName.equals("MqlUtil") && strArgument.contains("history") && (strArgument.contains("off") || strArgument.contains("on"))) {
/* 269 */         if (strArgument.contains("history off")) {
/* 270 */           this.bIsHistoryOff = true;
/*     */         }
/* 272 */         if (strArgument.contains("history on")) {
/* 273 */           this.bIsHistoryOn = true;
/*     */         }
/* 275 */         if (this.bIsHistoryOff)
/* 276 */           if (this.bIsHistoryOn) {
/*     */             
/* 278 */             bumpUpCountHistoryTransaction(strDeclarationCallingMethodName);
/* 279 */             bCheckAndReportIssueHistoryTransaction(strDeclarationCallingMethodName, eachLineTree, strArgument, strDeclarationMethodName);
/*     */           } else {
/*     */             
/* 282 */             bumpUpCount(strDeclarationCallingMethodName);
/* 283 */             bCheckAndReportIssueNow(strDeclarationCallingMethodName, eachLineTree);
/*     */           }  
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private Hashtable<String, Integer> bumpUpCountHistoryTransaction(String sMethodName) {
/* 290 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 291 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 292 */       int iNewSize = iSize.intValue() + 1;
/* 293 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 295 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/* 297 */     return this.htReportIssue;
/*     */   }
/*     */   private boolean bCheckAndReportIssueHistoryTransaction(String sMethodName, Tree eachLineTree, String strArgument, String strDeclarationMethodName) {
/* 300 */     if (this.htReportIssue.size() > 0);
/*     */     
/* 302 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 303 */       if (this.bIsHistoryOn) {
/* 304 */         if (this.bIsHistoryOnFinally) {
/*     */           
/* 306 */           reportIssue(eachLineTree, "ENOVIA --> " + sMethodName + " of class " + strDeclarationMethodName + " with " + strArgument + " is used , this is also available  properly in finally block still try to avoid history off ");
/*     */         } else {
/*     */           
/* 309 */           reportIssue(eachLineTree, "ENOVIA --> " + sMethodName + " of class " + strDeclarationMethodName + " with " + strArgument + "  is appeared put this in finally (Recomended to remove the history off ) ");
/*     */         } 
/* 311 */         this.bIsHistoryOn = false;
/*     */       } 
/* 313 */       return true;
/*     */     } 
/* 315 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMHistoryCommandCheckRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */