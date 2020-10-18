/*    */ package org.sonar.samples.java.checks;
/*    */ 
/*    */ import java.util.List;
/*    */ import javax.annotation.Nullable;
/*    */ import org.apache.commons.lang.StringUtils;
/*    */ import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
/*    */ import org.sonar.plugins.java.api.tree.Tree;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PrinterVisitor
/*    */   extends BaseTreeVisitor
/*    */ {
/*    */   private static final int INDENT_SPACES = 2;
/* 38 */   private final StringBuilder sb = new StringBuilder();
/* 39 */   private int indentLevel = 0;
/*    */ 
/*    */   
/*    */   public static String print(Tree tree) {
/* 43 */     PrinterVisitor pv = new PrinterVisitor();
/* 44 */     pv.scan(tree);
/* 45 */     return pv.sb.toString();
/*    */   }
/*    */   
/*    */   private StringBuilder indent() {
/* 49 */     return this.sb.append(StringUtils.leftPad("", 2 * this.indentLevel));
/*    */   }
/*    */ 
/*    */   
/*    */   protected void scan(List<? extends Tree> trees) {
/* 54 */     if (!trees.isEmpty()) {
/* 55 */       this.sb.deleteCharAt(this.sb.length() - 1);
/* 56 */       this.sb.append(" : [\n");
/* 57 */       super.scan(trees);
/* 58 */       indent().append("]\n");
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   protected void scan(@Nullable Tree tree) {
/* 64 */     if (tree != null && tree.getClass().getInterfaces() != null && (tree.getClass().getInterfaces()).length > 0) {
/* 65 */       String nodeName = tree.getClass().getInterfaces()[0].getSimpleName();
/* 66 */       indent().append(nodeName).append("\n");
/*    */     } 
/* 68 */     this.indentLevel++;
/* 69 */     super.scan(tree);
/* 70 */     this.indentLevel--;
/*    */   }
/*    */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\PrinterVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */