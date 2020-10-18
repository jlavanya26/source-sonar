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
/*     */ @Rule(key = "PLMCheckTriggerTransaction")
/*     */ public class PLMCheckTriggerTransactionRule extends IssuableSubscriptionVisitor {
/*     */   public boolean bIsTriggOn = false;
/*     */   public boolean bIsTriggOff = false;
/*     */   public boolean bIsTriggOnFinally = false;
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
/*  75 */       reportIssue(eachLineTree, "ENOVIA --> trigg off is used , avoid to use this  / if no way around  handle properly wih trigg on/trigg off in finally");
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
/*  87 */       AssignmentExpressionTree aet = (AssignmentExpressionTree)expressionTree;
/*  88 */       invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
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
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree tree, Tree eachLineTree) {
/* 102 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 103 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 104 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 105 */       invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     }  } private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 109 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 110 */     VariableTree newVariableTree = (VariableTree)variableTree.symbol().declaration();
/* 111 */     VariableTree variableTree1 = newVariableTree;
/*     */     try {
/* 113 */       expressionTree = newVariableTree.initializer();
/* 114 */     } catch (Exception exception) {}
/*     */     
/* 116 */     if (expressionTree == null) {
/* 117 */       tree = eachLineTree;
/*     */     }
/* 119 */     if (tree.kind().toString().equals("TYPE_CAST")) {
/* 120 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/* 121 */     } else if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 122 */       invokeMemberSelectMethod(tree, eachLineTree);
/* 123 */     } else if (tree.kind().toString().equals("METHOD_INVOCATION")) {
/* 124 */       invokeMethodInvocationTreeMethod(tree, eachLineTree);
/*     */     } 
/*     */   }
/*     */   private String invokeMethodInvocationTreeMethod(Tree tree, Tree eachLineTree) {
/* 128 */     MethodInvocationTree methodInvocationTree = (MethodInvocationTree)tree;
/* 129 */     ExpressionTree expressionTree = methodInvocationTree.methodSelect();
/* 130 */     String strClassMethodName = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree);
/* 131 */     String strMethodArgs = "";
/* 132 */     if (methodInvocationTree.arguments().size() != 0) {
/* 133 */       Arguments<Tree> arguments = methodInvocationTree.arguments();
/* 134 */       strMethodArgs = "(";
/* 135 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/* 136 */         if (((Tree)arguments.get(iArgCnt)).kind().toString().equals("STRING_LITERAL")) {
/* 137 */           invokeMemberSelectMethodForTriggerOff((Tree)expressionTree, eachLineTree, ((Tree)arguments.get(iArgCnt)).firstToken().text());
/*     */         }
/* 139 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 140 */           TypeCastTree typeCastTree = (TypeCastTree)arguments.get(iArgCnt);
/* 141 */           invokeTypeCastTreeMethod((Tree)typeCastTree, eachLineTree);
/* 142 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 143 */           strClassMethodName = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 146 */       strMethodArgs = strMethodArgs + "--)";
/* 147 */       strMethodArgs = strMethodArgs.replace(", --", "");
/*     */     } else {
/* 149 */       strMethodArgs = "()";
/*     */     } 
/* 151 */     return strClassMethodName + strMethodArgs;
/*     */   }
/*     */   private String invokeMemberSelectMethod(Tree tree, Tree eachLineTree) {
/* 154 */     String strClassAndMethodName = "";
/* 155 */     if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 156 */       MemberSelectExpressionTree memberSelectExpressionTree = (MemberSelectExpressionTree)tree;
/* 157 */       String strDeclarationMethodName = memberSelectExpressionTree.firstToken().text();
/* 158 */       String strDeclarationCallingMethodName = memberSelectExpressionTree.identifier().name();
/* 159 */       strClassAndMethodName = strDeclarationMethodName + "." + strDeclarationCallingMethodName;
/*     */     } 
/* 161 */     return strClassAndMethodName;
/*     */   }
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 164 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 165 */     StatementTree statementTree = forStmtTree.statement();
/* 166 */     if ("BLOCK".equals(statementTree.kind().toString())) {
/* 167 */       BlockTree blockTree = (BlockTree)forStmtTree.statement();
/* 168 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 169 */     } else if ("EXPRESSION_STATEMENT".equals(statementTree.kind().toString())) {
/* 170 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 174 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 175 */     BlockTree blockTree = (BlockTree)forEachStmt.statement();
/* 176 */     checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/*     */   }
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 179 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/* 180 */     ExpressionTree expressionTree = ifStmtTree.condition();
/* 181 */     if ("LOGICAL_COMPLEMENT".equals(expressionTree.kind().toString())) {
/* 182 */       UnaryExpressionTree unaryExpressoionTree = (UnaryExpressionTree)expressionTree;
/* 183 */       ExpressionTree newExpressionTree = unaryExpressoionTree.expression();
/* 184 */       if ("METHOD_INVOCATION".equals(newExpressionTree.kind().toString())) {
/* 185 */         invokeMethodInvocationTreeMethod((Tree)newExpressionTree, ifLoopTree);
/*     */       }
/* 187 */     } else if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/* 188 */       invokeMethodInvocationTreeMethod((Tree)expressionTree, ifLoopTree);
/*     */     } 
/* 190 */     StatementTree statementTree = ifStmtTree.thenStatement();
/* 191 */     invokeIfElseStatementTreeMethod(statementTree);
/*     */     try {
/* 193 */       while (ifStmtTree.elseStatement() != null) {
/* 194 */         statementTree = ifStmtTree.elseStatement();
/* 195 */         if ("IF_STATEMENT".equals(statementTree.kind().toString())) {
/* 196 */           ifStmtTree = (IfStatementTree)statementTree;
/* 197 */           StatementTree newStatementTree = ifStmtTree.thenStatement();
/* 198 */           invokeIfElseStatementTreeMethod(newStatementTree); continue;
/*     */         } 
/* 200 */         invokeIfElseStatementTreeMethod(statementTree);
/* 201 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 204 */     } catch (Exception exception) {}
/*     */   }
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree statementTree) {
/* 208 */     if (statementTree.kind().toString().equals("BLOCK")) {
/* 209 */       BlockTree blockTree = (BlockTree)statementTree;
/* 210 */       checkBlockBody(blockTree.openBraceToken(), blockTree.closeBraceToken(), blockTree.body());
/* 211 */     } else if (statementTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 212 */       invokeExpressionStatementTreeMethod((Tree)statementTree);
/*     */     } 
/*     */   }
/*     */   private void addressEachTree(List<? extends Tree> listOfTrees) {
/* 216 */     Tree eachLineTree = null;
/* 217 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/* 218 */       eachLineTree = listOfTrees.get(iLine);
/* 219 */       if (eachLineTree.kind().toString().equals("VARIABLE")) {
/* 220 */         invokeVariableTreeMethod(eachLineTree);
/* 221 */       } else if (eachLineTree.kind().toString().equals("EXPRESSION_STATEMENT")) {
/* 222 */         invokeExpressionStatementTreeMethod(eachLineTree);
/*     */       }
/* 224 */       else if (eachLineTree.kind().toString().equals("TRY_STATEMENT")) {
/* 225 */         TryStatementTree tryStatementTree = (TryStatementTree)eachLineTree;
/* 226 */         BlockTree btTryStmtTree = tryStatementTree.block();
/* 227 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/* 228 */         List<? extends CatchTree> catches = tryStatementTree.catches();
/* 229 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 230 */           CatchTree catchTree = catches.get(iCatchCnt);
/* 231 */           BlockTree blockTreeCatch = catchTree.block();
/* 232 */           checkBlockBody(blockTreeCatch.openBraceToken(), blockTreeCatch.closeBraceToken(), blockTreeCatch.body());
/*     */         } 
/*     */         try {
/* 235 */           BlockTree btTryStmtFinallyTree = tryStatementTree.finallyBlock();
/* 236 */           if (btTryStmtFinallyTree.kind().toString().equals("BLOCK")) {
/* 237 */             this.bIsTriggOnFinally = true;
/* 238 */             checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */           } 
/* 240 */         } catch (Exception exception) {}
/*     */       
/*     */       }
/* 243 */       else if (eachLineTree.kind().toString().equals("FOR_STATEMENT")) {
/* 244 */         invokeForStmtTreeMethod(eachLineTree);
/*     */       }
/* 246 */       else if (eachLineTree.kind().toString().equals("FOR_EACH_STATEMENT")) {
/* 247 */         invokeForEachStmtTreeMethod(eachLineTree);
/*     */       }
/* 249 */       else if (eachLineTree.kind().toString().equals("IF_STATEMENT")) {
/* 250 */         invokeIfStmtTreeMethod(eachLineTree);
/*     */       }
/* 252 */       else if (eachLineTree.kind().toString().equals("WHILE_STATEMENT")) {
/* 253 */         WhileStatementTree wst = (WhileStatementTree)eachLineTree;
/* 254 */         StatementTree st = wst.statement();
/* 255 */         invokeIfElseStatementTreeMethod(st);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeMemberSelectMethodForTriggerOff(Tree tree, Tree eachLineTree, String strArgument) {
/* 263 */     if (tree.kind().toString().equals("MEMBER_SELECT")) {
/* 264 */       MemberSelectExpressionTree memeberSelectExpressionTree = (MemberSelectExpressionTree)tree;
/* 265 */       String strDeclarationMethodName = memeberSelectExpressionTree.firstToken().text();
/* 266 */       String strDeclarationCallingMethodName = memeberSelectExpressionTree.identifier().name();
/* 267 */       if (memeberSelectExpressionTree.identifier().name().equals("mqlCommand") && strDeclarationMethodName.equals("MqlUtil") && strArgument.contains("trigg")) {
/* 268 */         if (strArgument.contains("trigg") && strArgument.contains("off")) {
/* 269 */           this.bIsTriggOff = true;
/*     */         }
/* 271 */         if (strArgument.contains("trigg") && strArgument.contains("on")) {
/* 272 */           this.bIsTriggOn = true;
/*     */         }
/* 274 */         if (this.bIsTriggOff)
/* 275 */           if (this.bIsTriggOn) {
/*     */             
/* 277 */             bumpUpCountTriggerTransaction(strDeclarationCallingMethodName);
/* 278 */             bCheckAndReportIssueTriggerTransaction(strDeclarationCallingMethodName, eachLineTree, strArgument, strDeclarationMethodName);
/*     */           } else {
/*     */             
/* 281 */             bumpUpCount(strDeclarationCallingMethodName);
/* 282 */             bCheckAndReportIssueNow(strDeclarationCallingMethodName, eachLineTree);
/*     */           }  
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private Hashtable<String, Integer> bumpUpCountTriggerTransaction(String sMethodName) {
/* 289 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 290 */       Integer iSize = this.htReportIssue.get(sMethodName);
/* 291 */       int iNewSize = iSize.intValue() + 1;
/* 292 */       this.htReportIssue.put(sMethodName, Integer.valueOf(iNewSize));
/*     */     } else {
/* 294 */       this.htReportIssue.put(sMethodName, Integer.valueOf(1));
/*     */     } 
/* 296 */     return this.htReportIssue;
/*     */   }
/*     */   private boolean bCheckAndReportIssueTriggerTransaction(String sMethodName, Tree eachLineTree, String strArgument, String strDeclarationMethodName) {
/* 299 */     if (this.htReportIssue.size() > 0);
/*     */     
/* 301 */     if (this.htReportIssue.containsKey(sMethodName)) {
/* 302 */       if (this.bIsTriggOn) {
/* 303 */         if (this.bIsTriggOnFinally) {
/*     */           
/* 305 */           reportIssue(eachLineTree, "ENOVIA --> " + sMethodName + " of class " + strDeclarationMethodName + " with " + strArgument + " is used , this is also available  properly in finally block still try to avoid trigger off ");
/*     */         } else {
/*     */           
/* 308 */           reportIssue(eachLineTree, "ENOVIA --> " + sMethodName + " of class " + strDeclarationMethodName + " with " + strArgument + "  is appeared put this in finally (Recomended to remove the trigger off ) ");
/*     */         } 
/* 310 */         this.bIsTriggOn = false;
/*     */       } 
/* 312 */       return true;
/*     */     } 
/* 314 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMCheckTriggerTransactionRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */