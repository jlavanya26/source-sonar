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
/*     */ @Rule(key = "PLMGetRelatedSelectable")
/*     */ public class PLMGetRelatedSelectableRule
/*     */   extends IssuableSubscriptionVisitor
/*     */ {
/*     */   private static final int DEFAULT_MAX = 2;
/*  38 */   public int max = 2;
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
/*     */   
/*     */   public void visitNode(Tree tree) {
/*  55 */     MethodTree methodTree = (MethodTree)tree;
/*     */     
/*  57 */     Symbol.MethodSymbol methodSymbol = methodTree.symbol();
/*  58 */     Type returnType = methodSymbol.returnType().type();
/*     */     
/*  60 */     if (methodSymbol.parameterTypes().size() == 1) {
/*  61 */       Type argType = methodSymbol.parameterTypes().get(0);
/*     */       
/*  63 */       if (argType.is(returnType.fullyQualifiedName()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  69 */     BlockTree blocktree = methodTree.block();
/*     */     
/*  71 */     Tree.Kind tk = methodTree.kind();
/*  72 */     log("&&&& - " + tk.toString() + " --> " + methodTree.simpleName().name());
/*     */     
/*  74 */     this.htReportIssue = new Hashtable<>();
/*     */     
/*  76 */     if (blocktree != null) {
/*  77 */       checkBlockBody(blocktree.openBraceToken(), blocktree.closeBraceToken(), blocktree.body());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkBlockBody(SyntaxToken openBraceToken, SyntaxToken closeBraceToken, List<? extends Tree> trees) {
/*  83 */     log("5");
/*  84 */     if (openBraceToken.line() != closeBraceToken.line() && !trees.isEmpty()) {
/*  85 */       log("6");
/*     */       
/*  87 */       addressEachTree(trees);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void log(String str) {
/*  92 */     if (this.bLoggingActive) {
/*  93 */       System.out.println(str);
/*     */     }
/*     */   }
/*     */   
/*     */   private void invokeExpressionStatementTreeMethod(Tree eachLineTree) {
/*  98 */     log("inside expr stmt");
/*  99 */     ExpressionStatementTree est = (ExpressionStatementTree)eachLineTree;
/* 100 */     log("est kind of  - " + est.kind().toString());
/*     */ 
/*     */     
/* 103 */     ExpressionTree expressionTree = est.expression();
/*     */     
/* 105 */     log("kind expr stmt " + expressionTree.kind().toString());
/* 106 */     if ("METHOD_INVOCATION".equals(expressionTree.kind().toString())) {
/*     */       
/* 108 */       String strReturn = invokeMethodInvocationTreeMethod((Tree)expressionTree, eachLineTree);
/* 109 */       log("line - " + strReturn);
/* 110 */     } else if ("ASSIGNMENT".equals(expressionTree.kind().toString())) {
/* 111 */       AssignmentExpressionTree aet = (AssignmentExpressionTree)expressionTree;
/* 112 */       invokeAssignmentExpressionStatementTreeMethod((Tree)aet.expression(), eachLineTree);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeTypeCastTreeMethod(Tree et, Tree eachLineTree) {
/* 117 */     TypeCastTree tct = (TypeCastTree)et;
/* 118 */     ExpressionTree ext = tct.expression();
/* 119 */     String sRet = "";
/* 120 */     log("inside invokeTypeCastTreeMethod" + ext.kind().toString());
/*     */     
/* 122 */     if ("METHOD_INVOCATION".equals(ext.kind().toString())) {
/*     */       
/* 124 */       sRet = invokeMethodInvocationTreeMethod((Tree)ext, eachLineTree);
/* 125 */       log("line - " + sRet);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void invokeAssignmentExpressionStatementTreeMethod(Tree et, Tree eachLineTree) {
/* 130 */     log("et kind of aest - " + et.kind().toString());
/* 131 */     if ("TYPE_CAST".equals(et.kind().toString())) {
/* 132 */       invokeTypeCastTreeMethod(et, eachLineTree);
/* 133 */     } else if ("METHOD_INVOCATION".equals(et.kind().toString())) {
/* 134 */       String sRet = invokeMethodInvocationTreeMethod(et, eachLineTree);
/* 135 */       log("line - " + sRet);
/*     */     } 
/*     */   }
/*     */   private void invokeVariableTreeMethod(Tree eachLineTree) {
/*     */     ExpressionTree expressionTree;
/*     */     Tree tree;
/* 141 */     String sRet = "";
/* 142 */     VariableTree variableTree = (VariableTree)eachLineTree;
/* 143 */     log("*** variable kind *** - " + variableTree.type().kind().toString());
/* 144 */     VariableTree myVtt = (VariableTree)variableTree.symbol().declaration();
/* 145 */     String myVariableName = variableTree.symbol().name();
/* 146 */     String myVariableType = variableTree.symbol().type().name();
/* 147 */     VariableTree variableTree1 = myVtt;
/*     */     try {
/* 149 */       expressionTree = myVtt.initializer();
/* 150 */       log("MYETT  - " + expressionTree.kind().toString());
/* 151 */     } catch (Exception ex) {
/* 152 */       log(" --- inside exception --" + ex);
/* 153 */       if (expressionTree == null) {
/* 154 */         tree = eachLineTree;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 159 */     if ("TYPE_CAST".equals(tree.kind().toString())) {
/*     */       
/* 161 */       invokeTypeCastTreeMethod(tree, eachLineTree);
/*     */     }
/* 163 */     else if ("METHOD_INVOCATION".equals(tree.kind().toString())) {
/*     */       
/* 165 */       sRet = invokeMethodInvocationTreeMethod(tree, eachLineTree);
/* 166 */       log("line - " + myVariableType + " " + myVariableName + " = " + sRet);
/*     */     }
/* 168 */     else if ("STRING_LITERAL".equals(tree.kind().toString())) {
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String invokeMethodInvocationTreeMethod(Tree mytree, Tree eachLineTree) {
/* 177 */     MethodInvocationTree myMit = (MethodInvocationTree)mytree;
/* 178 */     ExpressionTree expressionTree = myMit.methodSelect();
/* 179 */     log("InvMethTree Selectable Rule " + expressionTree.kind().toString());
/* 180 */     String sRet = invokeMemberSelectMethod((Tree)expressionTree, eachLineTree, (Tree)myMit);
/*     */     
/* 182 */     String sMyArgs = "";
/*     */     
/* 184 */     if (myMit.arguments().size() > 1) {
/* 185 */       log("****** in mit  *********" + myMit.arguments().size());
/* 186 */       Arguments<Tree> arguments = myMit.arguments();
/* 187 */       sMyArgs = "(";
/* 188 */       for (int iArgCnt = 0; iArgCnt < arguments.size(); iArgCnt++) {
/* 189 */         log("inside arguments - " + ((Tree)arguments.get(iArgCnt)).kind().toString());
/*     */ 
/*     */         
/* 192 */         if ("TYPE_CAST".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 193 */           TypeCastTree tct = (TypeCastTree)arguments.get(iArgCnt);
/* 194 */           invokeTypeCastTreeMethod((Tree)tct, eachLineTree);
/* 195 */         } else if ("METHOD_INVOCATION".equals(((Tree)arguments.get(iArgCnt)).kind().toString())) {
/* 196 */           sRet = invokeMethodInvocationTreeMethod(arguments.get(iArgCnt), eachLineTree);
/*     */         } 
/*     */       } 
/* 199 */       sMyArgs = sMyArgs + "--)";
/* 200 */       sMyArgs = sMyArgs.replace(", --", "");
/*     */     } else {
/* 202 */       sMyArgs = "()";
/*     */     } 
/*     */ 
/*     */     
/* 206 */     return sRet + sMyArgs;
/*     */   }
/*     */   
/*     */   private String invokeMemberSelectMethod(Tree ettemp, Tree eachLineTree, Tree Mit) {
/* 210 */     String str = "";
/* 211 */     if ("MEMBER_SELECT".equals(ettemp.kind().toString())) {
/*     */       
/* 213 */       MemberSelectExpressionTree mset = (MemberSelectExpressionTree)ettemp;
/* 214 */       String myDeclarationMethodName = mset.firstToken().text();
/* 215 */       String myDeclarationCallingMethodName = mset.identifier().name();
/* 216 */       str = myDeclarationMethodName + "." + myDeclarationCallingMethodName;
/* 217 */       MethodInvocationTree myMit = (MethodInvocationTree)Mit;
/*     */       
/* 219 */       if (myDeclarationCallingMethodName.equals("add") || myDeclarationCallingMethodName.equals("addElement")) {
/* 220 */         Arguments<Tree> arguments = myMit.arguments();
/* 221 */         if (((Tree)arguments.get(0)).lastToken().text().contains(".value")) {
/* 222 */           reportIssue(eachLineTree, "Selectable  Rule: .value, id has been used");
/*     */         }
/*     */       } 
/* 225 */       log("unexpected kind in iinvokeMemberSelectMethod - " + ettemp.kind().toString());
/*     */     } 
/* 227 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeForStmtTreeMethod(Tree forLoopTree) {
/* 232 */     ForStatementTree forStmtTree = (ForStatementTree)forLoopTree;
/* 233 */     log("*** inside invokeForStmtTreeMethod kind *** - " + forStmtTree.kind().toString());
/*     */     
/* 235 */     StatementTree et = forStmtTree.statement();
/* 236 */     log("*** et kind *** - " + et.kind().toString());
/* 237 */     if ("BLOCK".equals(et.kind().toString())) {
/* 238 */       BlockTree bt = (BlockTree)forStmtTree.statement();
/* 239 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 240 */     } else if ("EXPRESSION_STATEMENT".equals(et.kind().toString())) {
/* 241 */       invokeExpressionStatementTreeMethod((Tree)et);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeForEachStmtTreeMethod(Tree forEachLoopTree) {
/* 248 */     ForEachStatement forEachStmt = (ForEachStatement)forEachLoopTree;
/* 249 */     log("*** for each stmt kind *** - " + forEachStmt.kind().toString());
/*     */     
/* 251 */     BlockTree bt = (BlockTree)forEachStmt.statement();
/* 252 */     checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfStmtTreeMethod(Tree ifLoopTree) {
/* 257 */     IfStatementTree ifStmtTree = (IfStatementTree)ifLoopTree;
/*     */     
/* 259 */     ExpressionTree et = ifStmtTree.condition();
/* 260 */     log("*** if stmt condition kind *** - " + et.kind().toString());
/*     */     
/* 262 */     if ("LOGICAL_COMPLEMENT".equals(et.kind().toString())) {
/* 263 */       UnaryExpressionTree uet = (UnaryExpressionTree)et;
/* 264 */       ExpressionTree newet = uet.expression();
/* 265 */       log("*** logical complement kind *** - " + newet.kind().toString());
/*     */       
/* 267 */       if ("METHOD_INVOCATION".equals(newet.kind().toString())) {
/* 268 */         invokeMethodInvocationTreeMethod((Tree)newet, ifLoopTree);
/* 269 */       } else if ("IDENTIFIER".equals(newet.kind().toString())) {
/*     */ 
/*     */       
/*     */       }
/*     */     
/* 274 */     } else if ("METHOD_INVOCATION".equals(et.kind().toString())) {
/*     */       
/* 276 */       invokeMethodInvocationTreeMethod((Tree)et, ifLoopTree);
/*     */     } 
/*     */ 
/*     */     
/* 280 */     StatementTree st = ifStmtTree.thenStatement();
/*     */     
/* 282 */     invokeIfElseStatementTreeMethod(st);
/*     */     
/*     */     try {
/* 285 */       while (ifStmtTree.elseStatement() != null) {
/* 286 */         st = ifStmtTree.elseStatement();
/*     */         
/* 288 */         if ("IF_STATEMENT".equals(st.kind().toString())) {
/*     */           
/* 290 */           ifStmtTree = (IfStatementTree)st;
/* 291 */           StatementTree newst = ifStmtTree.thenStatement();
/*     */           
/* 293 */           invokeIfElseStatementTreeMethod(newst);
/*     */           continue;
/*     */         } 
/* 296 */         invokeIfElseStatementTreeMethod(st);
/* 297 */         ifStmtTree = null;
/*     */       }
/*     */     
/* 300 */     } catch (Exception ex) {
/* 301 */       log(" --- no else block OR exiting from else --" + ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeIfElseStatementTreeMethod(StatementTree st) {
/* 307 */     if ("BLOCK".equals(st.kind().toString())) {
/* 308 */       BlockTree bt = (BlockTree)st;
/* 309 */       checkBlockBody(bt.openBraceToken(), bt.closeBraceToken(), bt.body());
/* 310 */     } else if ("EXPRESSION_STATEMENT".equals(st.kind().toString())) {
/* 311 */       invokeExpressionStatementTreeMethod((Tree)st);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void addressEachTree(List<Tree> listOfTrees) {
/* 317 */     Tree eachLineTree = null;
/* 318 */     for (int iLine = 0; iLine < listOfTrees.size(); iLine++) {
/*     */       
/* 320 */       eachLineTree = listOfTrees.get(iLine);
/* 321 */       if ("VARIABLE".equals(eachLineTree.kind().toString())) {
/* 322 */         invokeVariableTreeMethod(eachLineTree);
/* 323 */       } else if ("EXPRESSION_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 324 */         invokeExpressionStatementTreeMethod(eachLineTree);
/* 325 */       } else if ("TRY_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 326 */         TryStatementTree tst = (TryStatementTree)eachLineTree;
/* 327 */         BlockTree btTryStmtTree = tst.block();
/*     */ 
/*     */         
/* 330 */         checkBlockBody(btTryStmtTree.openBraceToken(), btTryStmtTree.closeBraceToken(), btTryStmtTree.body());
/*     */         
/* 332 */         List<? extends CatchTree> catches = tst.catches();
/*     */         
/* 334 */         for (int iCatchCnt = 0; iCatchCnt < catches.size(); iCatchCnt++) {
/* 335 */           CatchTree ct = catches.get(iCatchCnt);
/* 336 */           BlockTree btCatch = ct.block();
/* 337 */           checkBlockBody(btCatch.openBraceToken(), btCatch.closeBraceToken(), btCatch.body());
/*     */         } 
/*     */         
/*     */         try {
/* 341 */           BlockTree btTryStmtFinallyTree = tst.finallyBlock();
/* 342 */           log("btTryStmtFinallyTree  - " + btTryStmtFinallyTree.kind().toString());
/*     */ 
/*     */           
/* 345 */           checkBlockBody(btTryStmtFinallyTree.openBraceToken(), btTryStmtFinallyTree.closeBraceToken(), btTryStmtFinallyTree.body());
/*     */         }
/* 347 */         catch (Exception ex) {
/* 348 */           log(" --- no finally block available exception -- " + ex);
/*     */         } 
/* 350 */       } else if ("FOR_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 351 */         invokeForStmtTreeMethod(eachLineTree);
/* 352 */       } else if ("FOR_EACH_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 353 */         invokeForEachStmtTreeMethod(eachLineTree);
/* 354 */       } else if ("IF_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 355 */         invokeIfStmtTreeMethod(eachLineTree);
/* 356 */       } else if ("WHILE_STATEMENT".equals(eachLineTree.kind().toString())) {
/* 357 */         WhileStatementTree wst = (WhileStatementTree)eachLineTree;
/* 358 */         StatementTree st = wst.statement();
/* 359 */         invokeIfElseStatementTreeMethod(st);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PLMGetRelatedSelectableRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */