/*     */ package org.sonar.samples.java;
/*     */ 
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import java.util.List;
/*     */ import org.sonar.plugins.java.api.JavaCheck;
/*     */ import org.sonar.samples.java.checks.AvoidADKAPIGetInfoListInloopRule;
/*     */ import org.sonar.samples.java.checks.AvoidADKAPIfindObjectInloopRule;
/*     */ import org.sonar.samples.java.checks.AvoidADKAPIgetRelatedObjectInloopRule;
/*     */ import org.sonar.samples.java.checks.AvoidAnnotationRule;
/*     */ import org.sonar.samples.java.checks.AvoidBrandInMethodNamesRule;
/*     */ import org.sonar.samples.java.checks.AvoidConnectInsideLoopRule;
/*     */ import org.sonar.samples.java.checks.AvoidDisconnectInsideLoopRule;
/*     */ import org.sonar.samples.java.checks.AvoidEnoviaMQLCommandsRule;
/*     */ import org.sonar.samples.java.checks.AvoidHardcodedFilePathRule;
/*     */ import org.sonar.samples.java.checks.AvoidMethodDeclarationRule;
/*     */ import org.sonar.samples.java.checks.AvoidMultiplefindObjectRule;
/*     */ import org.sonar.samples.java.checks.AvoidMultiplegetInfoListRule;
/*     */ import org.sonar.samples.java.checks.AvoidMultiplegetInfoRule;
/*     */ import org.sonar.samples.java.checks.AvoidMultiplegetRelatedObjectRule;
/*     */ import org.sonar.samples.java.checks.AvoidSuperClassRule;
/*     */ import org.sonar.samples.java.checks.AvoidTransactionsRule;
/*     */ import org.sonar.samples.java.checks.AvoidUnmodifiableListRule;
/*     */ import org.sonar.samples.java.checks.AvoidUseDomainObjectRule;
/*     */ import org.sonar.samples.java.checks.AvoidsetRPEValueRule;
/*     */ import org.sonar.samples.java.checks.MyCustomSubscriptionRule;
/*     */ import org.sonar.samples.java.checks.PLMCheckMQLCommandRule;
/*     */ import org.sonar.samples.java.checks.PLMCheckRelSyntaxRule;
/*     */ import org.sonar.samples.java.checks.PLMCheckTriggerTransactionRule;
/*     */ import org.sonar.samples.java.checks.PLMGetRelatedBoolRule;
/*     */ import org.sonar.samples.java.checks.PLMGetRelatedRelationshipAvoidStarRule;
/*     */ import org.sonar.samples.java.checks.PLMGetRelatedRelationshipLevelRule;
/*     */ import org.sonar.samples.java.checks.PLMGetRelatedRelationshipStringLiteralRule;
/*     */ import org.sonar.samples.java.checks.PLMGetRelatedRelationshipUseNullRule;
/*     */ import org.sonar.samples.java.checks.PLMGetRelatedSelectableRule;
/*     */ import org.sonar.samples.java.checks.PLMHistoryCommandCheckRule;
/*     */ import org.sonar.samples.java.checks.PLMJPOInvokeRule;
/*     */ import org.sonar.samples.java.checks.PLMMailBodySubjectRule;
/*     */ import org.sonar.samples.java.checks.PLMMultipleGetInfoRule;
/*     */ import org.sonar.samples.java.checks.PLMMultipleSetAttributeValueOnObjectRule;
/*     */ import org.sonar.samples.java.checks.PLMMultipleSetAttributeValueOnRelationshipRule;
/*     */ import org.sonar.samples.java.checks.PLMVariableDeclarationInLoopRule;
/*     */ import org.sonar.samples.java.checks.PLMhttpRule;
/*     */ import org.sonar.samples.java.checks.PushContextNotInLoopMustBeInTryRule;
/*     */ import org.sonar.samples.java.checks.SecurityAnnotationMandatoryRule;
/*     */ import org.sonar.samples.java.checks.SpringControllerRequestMappingEntityRule;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RulesList
/*     */ {
/*     */   public static List<Class> getChecks() {
/*  74 */     return (List<Class>)ImmutableList.builder().addAll(getJavaChecks()).addAll(getJavaTestChecks()).build();
/*     */   }
/*     */   
/*     */   public static List<Class<? extends JavaCheck>> getJavaChecks() {
/*  78 */     return (List<Class<? extends JavaCheck>>)ImmutableList.builder()
/*  79 */       .add(SpringControllerRequestMappingEntityRule.class)
/*  80 */       .add(AvoidAnnotationRule.class)
/*  81 */       .add(AvoidBrandInMethodNamesRule.class)
/*  82 */       .add(AvoidMethodDeclarationRule.class)
/*  83 */       .add(AvoidSuperClassRule.class)
/*  84 */       .add(AvoidUnmodifiableListRule.class)
/*  85 */       .add(MyCustomSubscriptionRule.class)
/*  86 */       .add(SecurityAnnotationMandatoryRule.class)
/*  87 */       .add(AvoidEnoviaMQLCommandsRule.class)
/*  88 */       .add(PLMMultipleGetInfoRule.class)
/*  89 */       .add(AvoidMultiplefindObjectRule.class)
/*  90 */       .add(AvoidMultiplegetRelatedObjectRule.class)
/*  91 */       .add(AvoidMultiplegetInfoListRule.class)
/*  92 */       .add(AvoidADKAPIGetInfoListInloopRule.class)
/*  93 */       .add(AvoidADKAPIfindObjectInloopRule.class)
/*  94 */       .add(AvoidADKAPIgetRelatedObjectInloopRule.class)
/*  95 */       .add(AvoidsetRPEValueRule.class)
/*     */       
/*  97 */       .add(PLMGetRelatedSelectableRule.class)
/*  98 */       .add(AvoidUseDomainObjectRule.class)
/*  99 */       .add(PLMVariableDeclarationInLoopRule.class)
/* 100 */       .add(AvoidMultiplegetInfoRule.class)
/* 101 */       .add(PLMMultipleSetAttributeValueOnObjectRule.class)
/* 102 */       .add(PLMMultipleSetAttributeValueOnRelationshipRule.class)
/* 103 */       .add(PLMCheckRelSyntaxRule.class)
/* 104 */       .add(PLMCheckMQLCommandRule.class)
/* 105 */       .add(PLMCheckTriggerTransactionRule.class)
/* 106 */       .add(PLMHistoryCommandCheckRule.class)
/* 107 */       .add(PLMhttpRule.class)
/* 108 */       .add(PLMMailBodySubjectRule.class)
/* 109 */       .add(AvoidTransactionsRule.class)
/* 110 */       .add(PushContextNotInLoopMustBeInTryRule.class)
/* 111 */       .add(PLMGetRelatedBoolRule.class)
/* 112 */       .add(PLMGetRelatedRelationshipAvoidStarRule.class)
/* 113 */       .add(PLMGetRelatedRelationshipLevelRule.class)
/* 114 */       .add(PLMGetRelatedRelationshipStringLiteralRule.class)
/* 115 */       .add(PLMGetRelatedRelationshipUseNullRule.class)
/* 116 */       .add(AvoidConnectInsideLoopRule.class)
/* 117 */       .add(AvoidDisconnectInsideLoopRule.class)
/* 118 */       .add(AvoidHardcodedFilePathRule.class)
/* 119 */       .add(PLMJPOInvokeRule.class)
/* 120 */       .build();
/*     */   }
/*     */   
/*     */   public static List<Class<? extends JavaCheck>> getJavaTestChecks() {
/* 124 */     return (List<Class<? extends JavaCheck>>)ImmutableList.builder()
/* 125 */       .build();
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\RulesList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */