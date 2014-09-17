/*     */ package bifurcation;
/*     */ 
/*     */ import de.congrace.exp4j.Calculable;
/*     */ import de.congrace.exp4j.ExpressionBuilder;
/*     */ import de.congrace.exp4j.UnknownFunctionException;
/*     */ import de.congrace.exp4j.UnparsableExpressionException;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ import model.OligoGraph;
/*     */ import model.OligoSystem;
/*     */ import model.Template;
/*     */ import utils.CodeGenerator;
/*     */ 
/*     */ public abstract class ClusterFunction
/*     */ {
			double valueLeft;
			double valueRight;
			
/*     */   public static ClusterFunction parseClusterCondition(final String condition)
/*     */   {
/*  34 */     ClusterFunction f = new ClusterFunction()
/*     */     {
/*     */       public boolean isTargetState(double[] results, OligoSystem<String> conditions)
/*     */       {
/*  39 */         int index = -1;
/*  40 */         int size = 1;
/*  41 */         String operator = "";
/*  42 */         String temp = condition;
/*  43 */         temp = temp.replaceAll("->", "");
/*     */ 
/*  45 */         if (condition.contains("<=")) {
/*  46 */           index = condition.indexOf("<=");
/*  47 */           operator = "<=";
/*  48 */           size = 2;
/*  49 */         } else if (condition.contains(">=")) {
/*  50 */           index = condition.indexOf(">=");
/*  51 */           operator = ">=";
/*  52 */           size = 2;
/*  53 */         } else if (condition.contains("==")) {
/*  54 */           index = condition.indexOf("==");
/*  55 */           operator = "==";
/*  56 */           size = 2;
/*  57 */         } else if (condition.contains("<")) {
/*  58 */           index = condition.indexOf("<");
/*  59 */           operator = "<";
/*     */         }
/*  61 */         else if (condition.contains("=")) {
/*  62 */           index = condition.indexOf("=");
/*  63 */           operator = "=";
/*  64 */         } else if (temp.contains(">"))
/*     */         {
/*  66 */           index = condition.indexOf(">");
/*  67 */           boolean satisfied = condition.charAt(index - 1) != '-';
/*  68 */           while (!satisfied) {
/*  69 */             index = condition.indexOf(">", index + 1);
/*  70 */             satisfied = condition.charAt(index - 1) != '-';
/*     */           }
/*  72 */           operator = ">";
/*     */         }
/*     */ 
/*  75 */          valueLeft = compute(replaceVariableByValue(condition.substring(0, index), results, conditions));
/*  76 */         valueRight = compute(replaceVariableByValue(condition.substring(index + size), results, conditions));
/*     */ 
/*  78 */         return compare(operator, valueLeft, valueRight);
/*     */       }
/*     */     };
/*  82 */     return f;
/*     */   }
/*     */ 
/*     */   public static double valueOf(String variable, double[] allValues, OligoSystem<String> conditions) {
/*  86 */     String[] names = conditions.giveNames();
/*  87 */     List l = Arrays.asList(names);
/*  88 */     double total = 0.0D;
/*  89 */     boolean computeTotal = variable.endsWith("_total");
/*  90 */     if (computeTotal) {
/*  91 */       variable = variable.split("_")[0];
/*     */     }
/*     */ 
/*  94 */     variable = variable.trim();
/*     */ 
/*  99 */     if (variable.startsWith("s"))
/*     */     {
/* 100 */       int baseindex = l.indexOf(variable);
/* 101 */       total += allValues[baseindex];
/* 102 */       if (!computeTotal);
/*     */     }
/* 107 */     if ((variable.startsWith("i")) && (!variable.startsWith("in"))) {
/* 108 */       int baseindex = l.indexOf(variable);
/* 109 */       total += allValues[baseindex];
/*     */     }
/*     */ 
/* 113 */     if (variable.contains("->")) {
/* 114 */       //System.out.println("C'est quoi cette variable " + variable);
/* 115 */       Template temp = getTemplate(variable, conditions);
/* 116 */       int index = new ArrayList(conditions.getTemplates()).indexOf(temp);
/*     */ 
/* 118 */       index *= 6;
/* 119 */       index += conditions.getSequences().size();
/* 120 */       if (variable.startsWith("temp")) {
/* 121 */         total = temp.totalConcentration;
/*     */       }
/* 123 */       else if (variable.startsWith("in")) {
/* 124 */         total = allValues[(index + 1)];
/* 125 */         //System.out.println("in" + total);
/* 126 */       } else if (variable.startsWith("out")) {
/* 127 */         total = allValues[(index + 2)];
/* 128 */       } else if (variable.startsWith("both")) {
/* 129 */         total = allValues[(index + 3)];
/* 130 */       } else if (variable.startsWith("inhib")) {
/* 131 */         total = allValues[(index + 5)];
/* 132 */       } else if (variable.startsWith("ext")) {
/* 133 */         total = allValues[(index + 4)];
/* 134 */       } else if (variable.startsWith("alone")) {
/* 135 */         total = allValues[index];
/*     */       }
/*     */     }
/*     */ 
/* 139 */     return total;
/*     */   }
/*     */   public abstract boolean isTargetState(double[] paramArrayOfDouble, OligoSystem<String> paramOligoSystem);
/*     */ 
/*     */   public static Template<String> getTemplate(String variable, OligoSystem<String> conditions) {
/* 144 */     String tempName = "";
/*     */ 
/* 147 */     if (variable.startsWith("temp"))
/* 148 */       tempName = variable.substring(4);
/* 149 */     else if (variable.startsWith("in"))
/* 150 */       tempName = variable.substring(2);
/* 151 */     else if (variable.startsWith("out"))
/* 152 */       tempName = variable.substring(3);
/* 153 */     else if (variable.startsWith("both"))
/* 154 */       tempName = variable.substring(4);
/* 155 */     else if (variable.startsWith("inhib"))
/* 156 */       tempName = variable.substring(5);
/* 157 */     else if (variable.startsWith("ext"))
/* 158 */       tempName = variable.substring(3);
/* 159 */     else if (variable.startsWith("alone"))
/* 160 */       tempName = variable.substring(5);
/*     */     else {
/* 162 */      // JOptionPane.showMessageDialog(null, "Invalid template name: " + variable);
/*     */     }
/*     */ 
/* 165 */     Template res = (Template)conditions.templates.get(tempName);
/* 166 */     if (res == null) {
/* 167 */       //JOptionPane.showMessageDialog(null, "Inexistant template: " + tempName);
/*     */     }
/*     */ 
/* 170 */     return res;
/*     */   }
/*     */ 
/*     */   public static String replaceVariableByValue(String original, double[] results, OligoSystem<String> conditions) {
/* 174 */     String result = original;
/*     */ 
/* 177 */     String[] split = original.split("[+\\-*/\\(\\)&|](?!>)");
/* 178 */     HashMap<String,Double> eq = new HashMap<String,Double>();
/*     */ 
/* 180 */     for (int i = 0; i < split.length; i++) {
/* 181 */       if (!Pattern.matches("[0-9.]+", split[i].trim()))
/*     */       {
/* 184 */         eq.put(split[i], Double.valueOf(valueOf(split[i].trim(), results, conditions)));
/*     */       }
/*     */     }
/*     */ 
/* 188 */     for (String s : eq.keySet()) {
/* 189 */       result = result.replace(s, "" + eq.get(s));
/*     */     }
/*     */ 
/* 192 */     return result;
/*     */   }
/*     */ 
/*     */   public static double compute(String mathExp)
/*     */   {
/* 198 */     Calculable calc = null;
/*     */     try {
/* 200 */       calc = new ExpressionBuilder(mathExp).build();
/* 201 */       return calc.calculate();
/*     */     } catch (UnknownFunctionException ex) {
/* 203 */       Logger.getLogger(ClusterFunction.class.getName()).log(Level.SEVERE, null, ex);
/*     */     } catch (UnparsableExpressionException ex) {
/* 205 */       Logger.getLogger(ClusterFunction.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/* 207 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public static boolean compare(String operator, double value1, double value2) {
/* 211 */     if (operator.equals("<"))
/* 212 */       return value1 < value2;
/* 213 */     if (operator.equals("<="))
/* 214 */       return value1 <= value2;
/* 215 */     if (operator.equals(">"))
/* 216 */       return value1 > value2;
/* 217 */     if (operator.equals(">="))
/* 218 */       return value1 >= value2;
/* 219 */     if ((operator.equals("=")) || (operator.equals("=="))) {
/* 220 */       return value1 == value2;
/*     */     }
/*     */ 
/* 223 */    // JOptionPane.showMessageDialog(null, "Invalid operator: " + operator);
/*     */ 
/* 225 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean combine(String operator, boolean value1, boolean value2) {
/* 229 */     if ((operator.equals("&")) || (operator.equals("&&")))
/* 230 */       return (value1) && (value2);
/* 231 */     if ((operator.equals("|")) || (operator.equals("||"))) {
/* 232 */       return (value1) || (value2);
/*     */     }
/*     */ 
/* 235 */    // JOptionPane.showMessageDialog(null, "Invalid operator: " + operator);
/*     */ 
/* 237 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */ }

/* Location:           /Users/Lab_Member/Desktop/Nat/Bifurcation 2/dist/Bifurcation.jar
 * Qualified Name:     bifurcation.ClusterFunction
 * JD-Core Version:    0.6.2
 */