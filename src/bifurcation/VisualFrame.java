/*      */ package bifurcation;
/*      */ 
/*      */ 

/*      */ import graphical.EdgeFactory;
/*      */ import graphical.VertexFactory;


/*      */ 
/*      */ import java.io.File;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ 
import java.util.HashMap;
/*      */ 
/*      */ import java.util.concurrent.ExecutorService;
/*      */ import java.util.concurrent.Executors;
/*      */ import java.util.concurrent.Future;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;




/*      */ 
/*      */ import model.OligoGraph;
/*      */ 
/*      */ import model.SequenceVertex;
/*      */
/*      */ import utils.CodeGenerator;

/*      */ 
/*      */ public class VisualFrame
/*      */ {
/*   89 */   public static int replotDelay = 200;

			 public static boolean superverbose = false;
/*      */ 
/*   91 */   
/*   96 */   double limit = 0.025D;
/*      */ 
/*   98 */   double step1 = 10.0D;
/*      */ 
/*  100 */   double rangemin1 = 0.0D;
/*  101 */   double current1 = 0.0D;
/*  102 */   double rangemax1 = 200.0D;
/*      */ 
/*  104 */   double step2 = 10.0D;
/*      */ 
/*  106 */   double rangemin2 = 0.0D;
/*  107 */   double current2 = 0.0D;
/*  108 */   double rangemax2 = 200.0D;
/*      */ 
/*  110 */   static int numberOfTriggers = 2;
/*      */ 
/*  112 */   String[] triggers = { "s1", "s2" };
/*  113 */   boolean[] totalTriggers = { true, true };
/*      */ 
/*  115 */   boolean doExport = false;
/*      */ 
/*  117 */   
/*      */ 
/*  119 */   int numberOfThreads = 10;
/*      */ 
/*  121 */   int simTime = 600;
/*      */   Object target1;
/*      */   Object target2;
/*  126 */   double threashold = 20.0D;
			 boolean computeWithColors = false; //Instead of thresholds, just plot directly how much is present.
			
/*      */ 
/*  128 */   
/*      */ 
/*  130 */   OligoGraph<SequenceVertex, String> target = new OligoGraph<SequenceVertex,String>();
/*      */   ExecutorService globalPool;
/*      */   
/*      */ 
/*      */   protected void setTarget1(double value, OligoGraph<SequenceVertex,String> graph)
/*      */   {
/*  137 */     if (this.target1.getClass() == SequenceVertex.class) {
/*  138 */       ArrayList<SequenceVertex> indivs = new ArrayList<SequenceVertex>(graph.getVertices());
/*  139 */       SequenceVertex realTarg = (SequenceVertex)indivs.get(indivs.indexOf((SequenceVertex)this.target1));
/*  140 */       realTarg.setInitialConcentration(value);
/*      */     } else {
/*  142 */       graph.setTemplateConcentration((String)this.target1, Double.valueOf(value));
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setTarget2(double value, OligoGraph<SequenceVertex,String> graph) {
/*  147 */     if (this.target2.getClass() == SequenceVertex.class) {
/*  148 */       ArrayList<SequenceVertex> indivs = new ArrayList<SequenceVertex>(graph.getVertices());
/*  149 */       SequenceVertex realTarg = (SequenceVertex)indivs.get(indivs.indexOf((SequenceVertex)this.target2));
/*  150 */       realTarg.setInitialConcentration(value);
/*      */     } else {
/*  152 */       graph.setTemplateConcentration((String)this.target2, Double.valueOf(value));
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */ 
/*      */   public static void initGraph(OligoGraph<SequenceVertex, String> g)
/*      */   {
/*  196 */     g.initFactories(new VertexFactory<SequenceVertex>(g)
/*      */     {
/*      */       public SequenceVertex create() {
/*  199 */         SequenceVertex newvertex = (SequenceVertex)this.associatedGraph.popAvailableVertex();
/*  200 */         if (newvertex == null)
/*  201 */           newvertex = new SequenceVertex(Integer.valueOf(this.associatedGraph.getVertexCount() + 1));
/*      */         else {
/*  203 */           newvertex = new SequenceVertex(newvertex.ID);
/*      */         }
/*  205 */         return newvertex;
/*      */       }
/*      */ 
/*      */       public SequenceVertex copy(SequenceVertex original)
/*      */       {
/*  210 */         SequenceVertex ret = new SequenceVertex(original.ID);
/*  211 */         ret.inputs = original.inputs;
/*  212 */         return ret;
/*      */       }
/*      */     }
/*      */     , new EdgeFactory<SequenceVertex,String>(g)
/*      */     {
/*      */       public String createEdge(SequenceVertex v1, SequenceVertex v2)
/*      */       {
/*  216 */         return v1.ID + "->" + v2.ID;
/*      */       }
/*      */       public String inhibitorName(String s) {
/*  219 */         return "Inhib" + s;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public VisualFrame()
/*      */   {
/*  228 */    
/*      */ 
/*  230 */     initGraph(this.target);
/*      */   }
/*      */ 
/*      */   
/*      */ 
/*      */  
/*      */ 
/*      */  

			 protected void loadGraph(File open)
			 {
				 CodeGenerator.openGraph(this.target, null, open);
				 /*  863 */       
			 }
			 
			 public static HashMap<String,Object> getInlineConditions(OligoGraph<SequenceVertex, String> graph, String[] affected, double[] values){
					HashMap<String,Object> result =  MyTableModelListener.generateTable(graph);
					
					
						for(int j=0; j<affected.length; j++){ //TODO: I am planning to allow regexp here, so "affected" can be more than one element
							result.put(affected[j], values[j]);
						}
					
					return result;
				}
/*      */ 
/*      */   
/*      */ 
/*      */  
/*      */ }

/* Location:           /Users/Lab_Member/Desktop/Nat/Bifurcation 2/dist/Bifurcation.jar
 * Qualified Name:     bifurcation.VisualFrame
 * JD-Core Version:    0.6.2
 */