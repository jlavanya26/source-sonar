/*     */ package org.sonar.samples.java.checks;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import org.sonar.check.Rule;
/*     */ import org.sonar.check.RuleProperty;
/*     */ import org.sonar.java.model.PackageUtils;
/*     */ import org.sonar.plugins.java.api.JavaCheck;
/*     */ import org.sonar.plugins.java.api.JavaFileScanner;
/*     */ import org.sonar.plugins.java.api.JavaFileScannerContext;
/*     */ import org.sonar.plugins.java.api.tree.AnnotationTree;
/*     */ import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
/*     */ import org.sonar.plugins.java.api.tree.ClassTree;
/*     */ import org.sonar.plugins.java.api.tree.CompilationUnitTree;
/*     */ import org.sonar.plugins.java.api.tree.IdentifierTree;
/*     */ import org.sonar.plugins.java.api.tree.ListTree;
/*     */ import org.sonar.plugins.java.api.tree.MethodTree;
/*     */ import org.sonar.plugins.java.api.tree.Tree;
/*     */ import org.sonar.plugins.java.api.tree.TypeTree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Rule(key = "SecurityAnnotationMandatory")
/*     */ public class SecurityAnnotationMandatoryRule
/*     */   extends BaseTreeVisitor
/*     */   implements JavaFileScanner
/*     */ {
/*  43 */   private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAnnotationMandatoryRule.class);
/*     */   
/*     */   private static final String DEFAULT_VALUE = "MySecurityAnnotation";
/*     */   
/*  47 */   private boolean implementsSpecificInterface = Boolean.FALSE.booleanValue();
/*     */ 
/*     */   
/*     */   private JavaFileScannerContext context;
/*     */ 
/*     */   
/*     */   @RuleProperty(defaultValue = "MySecurityAnnotation", description = "Name of the mandatory annotation")
/*     */   protected String name;
/*     */ 
/*     */   
/*     */   public void scanFile(JavaFileScannerContext context) {
/*  58 */     this.context = context;
/*  59 */     scan((Tree)context.getTree());
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitClass(ClassTree tree) {
/*  64 */     ListTree listTree = tree.superInterfaces();
/*  65 */     for (TypeTree typeTree : listTree) {
/*  66 */       LOGGER.info("implements Interface : " + typeTree);
/*  67 */       if ("MySecurityInterface".equals(typeTree.toString())) {
/*  68 */         this.implementsSpecificInterface = Boolean.TRUE.booleanValue();
/*     */       }
/*     */     } 
/*     */     
/*  72 */     super.visitClass(tree);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitCompilationUnit(CompilationUnitTree tree) {
/*  78 */     if (tree.packageDeclaration() != null) {
/*  79 */       String packageName = PackageUtils.packageName(tree.packageDeclaration(), ".");
/*  80 */       LOGGER.info("PackageName : " + packageName);
/*     */     } 
/*     */     
/*  83 */     super.visitCompilationUnit(tree);
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitMethod(MethodTree tree) {
/*  88 */     if (this.implementsSpecificInterface) {
/*  89 */       List<AnnotationTree> annotations = tree.modifiers().annotations();
/*     */       
/*  91 */       boolean isHavingMandatoryAnnotation = Boolean.FALSE.booleanValue();
/*     */       
/*  93 */       for (AnnotationTree annotationTree : annotations) {
/*  94 */         if (annotationTree.annotationType().is(new Tree.Kind[] { Tree.Kind.IDENTIFIER })) {
/*  95 */           IdentifierTree idf = (IdentifierTree)annotationTree.annotationType();
/*  96 */           LOGGER.info("Method Name {}", idf.name());
/*     */           
/*  98 */           if (idf.name().equals(this.name)) {
/*  99 */             isHavingMandatoryAnnotation = Boolean.TRUE.booleanValue();
/*     */           }
/*     */         } 
/*     */       } 
/* 103 */       if (!isHavingMandatoryAnnotation) {
/* 104 */         this.context.reportIssue((JavaCheck)this, (Tree)tree, String.format("Mandatory Annotation not set @%s", new Object[] { this.name }));
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 110 */     super.visitMethod(tree);
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\checks\SecurityAnnotationMandatoryRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */