/*     */ package bifurcation;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Collection;
import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;

/*     */ 
/*     */ import model.Constants;
/*     */ import model.OligoGraph;
/*     */ import model.SequenceVertex;
/*     */ 
/*     */ public class MyTableModelListener
/*     */   
/*     */ {
/*     */   OligoGraph<SequenceVertex, String> target;
/*     */   
/*  23 */   private static int BASE = 8;
/*     */ 
/*     */   MyTableModelListener(OligoGraph<SequenceVertex, String> target) {
/*  26 */     this.target = target;
/*  27 */     
/*     */   }
/*     */ 
/*     */  
/*     */ 
/*     */   
/*     */ 
/*     */   protected SequenceVertex findSeq(String name) {
/*  98 */     SequenceVertex seq = null;
/*  99 */     if ((name.startsWith("s")) || (name.startsWith("I"))) {
/* 100 */       int id = Integer.parseInt(name.substring(1));
/* 101 */       seq = (SequenceVertex)this.target.getEquivalentVertex(new SequenceVertex(Integer.valueOf(id)));
/*     */     }
/* 103 */     return seq;
/*     */   }
/*     */ 
/*     */   protected static SequenceVertex findSeq(String name, OligoGraph<SequenceVertex, String> g) {
/* 107 */     SequenceVertex seq = null;
/* 108 */     if ((name.startsWith("s")) || (name.startsWith("I"))) {
/* 109 */       int id = Integer.parseInt(name.substring(1));
/* 110 */       seq = (SequenceVertex)g.getEquivalentVertex(new SequenceVertex(Integer.valueOf(id)));
/*     */     }
/* 112 */     return seq;
/*     */   }
/*     */ 
/*     */  
/*     */ 
/*     */   public static void updateAll(HashMap<String,Object> map, OligoGraph<SequenceVertex, String> graph)
/*     */   {
/* 152 */     graph.exoConc = (double) map.get("Exo conc");
/* 153 */     graph.polConc =  (double) map.get("Pol conc");
/* 154 */     graph.nickConc = (double) map.get("Nick conc");
/* 155 */     Constants.alpha = (double) map.get("Alpha");
/* 156 */     graph.exoSaturationByFreeTemplates = (boolean) map.get("Exo inhib by temp");
/* 157 */     Constants.exoKmTemplate = (double) map.get("Exo Km for temp");
/* 158 */     graph.nickSaturationByProduct = (boolean) map.get("Nick inhib by both");
/* 159 */     Constants.nickKmBoth = (double) map.get("Nick Km for both");
/*     */ 
/* 161 */     for (String elem: map.keySet()) { //other cases
/* 162 */       
/* 163 */       if (elem.contains("->")) {
/* 164 */         if (elem.contains("Km")) {
/* 165 */           graph.setTemplateExoKm(elem.split(" ")[0], (double) map.get(elem));
/*     */         }
/*     */         else
/* 168 */           graph.setTemplateConcentration(elem, (double) map.get(elem));
/*     */       }
/*     */       else {
/* 171 */         String[] parts = elem.split(" ");
					if(parts.length <=1){
						continue; // Alpha... already done
					}
/* 172 */         SequenceVertex seq = findSeq(parts[1], graph);
/* 173 */         if (seq == null) {
					// Not a seq
					continue;
/* 174 */           //System.err.println("Unknown sequence"); 
/*     */         }
/* 176 */         if (parts[0].equals("Init"))
/* 177 */           seq.initialConcentration = (double) map.get(elem);
/* 178 */         else if (parts[0].equals("K"))
/* 179 */           graph.K.put(seq, (double) map.get(elem));
/*     */         else
/* 181 */           System.err.println("Unknown parameter");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static HashMap<String,Object> generateTable(OligoGraph<SequenceVertex, String> graph)
/*     */   {
/* 190 */     HashMap<String,Object> table = new HashMap<String,Object>();
/*     */ 
/* 192 */     
/*     */ 
/* 194 */     table.put("Exo conc", graph.exoConc);
/* 195 */     table.put("Pol conc", graph.polConc);
/* 196 */     table.put("Nick conc", graph.nickConc);
/* 197 */     table.put("Alpha",  Constants.alpha);
/* 198 */     table.put("Exo inhib by temp", graph.exoSaturationByFreeTemplates);
/* 199 */     table.put( "Exo Km for temp",  Constants.exoKmTemplate);
/* 200 */     table.put("Nick inhib by both",  graph.nickSaturationByProduct);
/* 201 */     table.put("Nick Km for both", Constants.nickKmBoth);
/*     */ 
/* 203 */     Iterator it = graph.getVertices().iterator();
/* 204 */     while (it.hasNext()) {
/* 205 */       SequenceVertex sv = (SequenceVertex)it.next();
/* 206 */       String s = sv.toString();
/*     */ 
/* 208 */       table.put("Init " + s,  sv.initialConcentration );
/* 209 */       table.put("K " + s,  graph.K.get(sv) );
/*     */     }
/* 211 */     it = graph.getEdges().iterator();
/* 212 */     while (it.hasNext()) {
/* 213 */       String temp = (String)it.next();
/* 214 */       table.put( temp,  graph.getTemplateConcentration(temp) );
/* 215 */       table.put(temp + " exoKm",  graph.getTemplateExoKm(temp) );
/*     */     }
/* 217 */     
/* 218 */     return table;
/*     */   }
/*     */ }

/* Location:           /Users/Lab_Member/Desktop/Nat/Bifurcation 2/dist/Bifurcation.jar
 * Qualified Name:     bifurcation.MyTableModelListener
 * JD-Core Version:    0.6.2
 */