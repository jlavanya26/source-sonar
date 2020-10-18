/*    */ package org.sonar.samples.java;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.sonar.plugins.java.api.CheckRegistrar;
/*    */ import org.sonar.plugins.java.api.JavaCheck;
/*    */ import org.sonarsource.api.sonarlint.SonarLintSide;
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
/*    */ @SonarLintSide
/*    */ public class MyJavaFileCheckRegistrar
/*    */   implements CheckRegistrar
/*    */ {
/*    */   public void register(CheckRegistrar.RegistrarContext registrarContext) {
/* 51 */     registrarContext.registerClassesForRepository("mycompany-java", checkClasses(), testCheckClasses());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<Class<? extends JavaCheck>> checkClasses() {
/* 58 */     return RulesList.getJavaChecks();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<Class<? extends JavaCheck>> testCheckClasses() {
/* 65 */     return RulesList.getJavaTestChecks();
/*    */   }
/*    */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\MyJavaFileCheckRegistrar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */