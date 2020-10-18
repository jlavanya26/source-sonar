/*     */ package org.sonar.samples.java;
/*     */ 
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.collect.Iterables;
/*     */ import com.google.common.io.Resources;
/*     */ import com.google.gson.Gson;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import javax.annotation.Nullable;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ import org.sonar.api.rule.RuleStatus;
/*     */ import org.sonar.api.rules.RuleType;
/*     */ import org.sonar.api.server.debt.DebtRemediationFunction;
/*     */ import org.sonar.api.server.rule.RulesDefinition;
/*     */ import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;
/*     */ import org.sonar.api.utils.AnnotationUtils;
/*     */ import org.sonar.check.Cardinality;
/*     */ import org.sonar.check.Rule;
/*     */ import org.sonar.java.RspecKey;
/*     */ import org.sonar.squidbridge.annotations.RuleTemplate;
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
/*     */ 
/*     */ public class MyJavaRulesDefinition
/*     */   implements RulesDefinition
/*     */ {
/*     */   private static final String RESOURCE_BASE_PATH = "/org/sonar/l10n/java/rules/squid";
/*     */   public static final String REPOSITORY_KEY = "mycompany-java";
/*  55 */   private final Gson gson = new Gson();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void define(RulesDefinition.Context context) {
/*  61 */     RulesDefinition.NewRepository repository = context.createRepository("mycompany-java", "java").setName("MyCompany Custom Repository");
/*     */     
/*  63 */     List<Class<?>> checks = RulesList.getChecks();
/*  64 */     (new RulesDefinitionAnnotationLoader()).load((RulesDefinition.NewExtendedRepository)repository, (Class[])Iterables.toArray(checks, Class.class));
/*     */     
/*  66 */     for (Class<?> ruleClass : checks) {
/*  67 */       newRule(ruleClass, repository);
/*     */     }
/*  69 */     repository.done();
/*     */   }
/*     */ 
/*     */   
/*     */   @VisibleForTesting
/*     */   protected void newRule(Class<?> ruleClass, RulesDefinition.NewRepository repository) {
/*  75 */     Rule ruleAnnotation = (Rule)AnnotationUtils.getAnnotation(ruleClass, Rule.class);
/*  76 */     if (ruleAnnotation == null) {
/*  77 */       throw new IllegalArgumentException("No Rule annotation was found on " + ruleClass);
/*     */     }
/*  79 */     String ruleKey = ruleAnnotation.key();
/*  80 */     if (StringUtils.isEmpty(ruleKey)) {
/*  81 */       throw new IllegalArgumentException("No key is defined in Rule annotation of " + ruleClass);
/*     */     }
/*  83 */     RulesDefinition.NewRule rule = repository.rule(ruleKey);
/*  84 */     if (rule == null) {
/*  85 */       throw new IllegalStateException("No rule was created for " + ruleClass + " in " + repository.key());
/*     */     }
/*  87 */     ruleMetadata(ruleClass, rule);
/*     */     
/*  89 */     rule.setTemplate((AnnotationUtils.getAnnotation(ruleClass, RuleTemplate.class) != null));
/*  90 */     if (ruleAnnotation.cardinality() == Cardinality.MULTIPLE) {
/*  91 */       throw new IllegalArgumentException("Cardinality is not supported, use the RuleTemplate annotation instead for " + ruleClass);
/*     */     }
/*     */   }
/*     */   
/*     */   private String ruleMetadata(Class<?> ruleClass, RulesDefinition.NewRule rule) {
/*  96 */     String metadataKey = rule.key();
/*  97 */     RspecKey rspecKeyAnnotation = (RspecKey)AnnotationUtils.getAnnotation(ruleClass, RspecKey.class);
/*  98 */     if (rspecKeyAnnotation != null) {
/*  99 */       metadataKey = rspecKeyAnnotation.value();
/* 100 */       rule.setInternalKey(metadataKey);
/*     */     } 
/* 102 */     addHtmlDescription(rule, metadataKey);
/* 103 */     addMetadata(rule, metadataKey);
/* 104 */     return metadataKey;
/*     */   }
/*     */   
/*     */   private void addMetadata(RulesDefinition.NewRule rule, String metadataKey) {
/* 108 */     URL resource = MyJavaRulesDefinition.class.getResource("/org/sonar/l10n/java/rules/squid/" + metadataKey + "_java.json");
/* 109 */     if (resource != null) {
/* 110 */       RuleMetatada metatada = (RuleMetatada)this.gson.fromJson(readResource(resource), RuleMetatada.class);
/* 111 */       rule.setSeverity(metatada.defaultSeverity.toUpperCase(Locale.US));
/* 112 */       rule.setName(metatada.title);
/* 113 */       rule.addTags(metatada.tags);
/* 114 */       rule.setType(RuleType.valueOf(metatada.type));
/* 115 */       rule.setStatus(RuleStatus.valueOf(metatada.status.toUpperCase(Locale.US)));
/* 116 */       if (metatada.remediation != null) {
/* 117 */         rule.setDebtRemediationFunction(metatada.remediation.remediationFunction(rule.debtRemediationFunctions()));
/* 118 */         rule.setGapDescription(metatada.remediation.linearDesc);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void addHtmlDescription(RulesDefinition.NewRule rule, String metadataKey) {
/* 124 */     URL resource = MyJavaRulesDefinition.class.getResource("/org/sonar/l10n/java/rules/squid/" + metadataKey + "_java.html");
/* 125 */     if (resource != null) {
/* 126 */       rule.setHtmlDescription(readResource(resource));
/*     */     }
/*     */   }
/*     */   
/*     */   private static String readResource(URL resource) {
/*     */     try {
/* 132 */       return Resources.toString(resource, StandardCharsets.UTF_8);
/* 133 */     } catch (IOException e) {
/* 134 */       throw new IllegalStateException("Failed to read: " + resource, e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class RuleMetatada
/*     */   {
/*     */     String title;
/*     */     String status;
/*     */     @Nullable
/*     */     MyJavaRulesDefinition.Remediation remediation;
/*     */     String type;
/*     */     String[] tags;
/*     */     String defaultSeverity;
/*     */   }
/*     */   
/*     */   private static class Remediation {
/*     */     String func;
/*     */     String constantCost;
/*     */     String linearDesc;
/*     */     String linearOffset;
/*     */     String linearFactor;
/*     */     
/*     */     public DebtRemediationFunction remediationFunction(RulesDefinition.DebtRemediationFunctions drf) {
/* 157 */       if (this.func.startsWith("Constant")) {
/* 158 */         return drf.constantPerIssue(this.constantCost.replace("mn", "min"));
/*     */       }
/* 160 */       if ("Linear".equals(this.func)) {
/* 161 */         return drf.linear(this.linearFactor.replace("mn", "min"));
/*     */       }
/* 163 */       return drf.linearWithOffset(this.linearFactor.replace("mn", "min"), this.linearOffset.replace("mn", "min"));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1245817\Downloads\java-custom-rules-1.0-SNAPSHOT\java-custom-rules-1.0-SNAPSHOT.jar!\org\sonar\samples\java\MyJavaRulesDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */