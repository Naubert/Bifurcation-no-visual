/*     */ package bifurcation;
/*     */ 
/*     */
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.CompletionService;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.ExecutorCompletionService;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;


/*     */ 
import model.Constants;
/*     */ import model.OligoGraph;
/*     */ import model.OligoSystem;
/*     */ import model.SequenceVertex;
/*     */ import optimizers.cmaes.CMAEvolutionStrategy;
/*     */ import optimizers.cmaes.CMAEvolutionStrategy.StopCondition;
import optimizers.cmaes.fitness.IObjectiveFunction;
import utils.CodeGenerator;
/*     */ 
/*     */ public class PhaseOptimization
/*     */   implements IObjectiveFunction, Runnable
/*     */ {
	
	//global variables for settings
	boolean computeAll = true;
	static boolean satbyprod = true;
	static boolean satbytemp = true;
	static int numruns = 1;
	
	static boolean colorGradiant = true;
	static boolean adrian = false;
	static double mainFactor = 20.0;
	
	
	
	
/*  50 */   ArrayList<double[][][]> dataArray = new ArrayList<double[][][]>();
/*  51 */   ArrayList<HashMap<String,Object>> initialConditions = new ArrayList<HashMap<String,Object>>();
			ArrayList<boolean[][]> shouldCompute = new ArrayList<boolean[][]>();
			
			int diameter = 3;
/*     */   VisualFrame main;
/*  53 */   static double minConc1 = 0.0D;
/*  54 */   static double maxConc1 = 140.0D;
/*  55 */   static double minConc2 = 0.0D;
/*  56 */   static double maxConc2 = 200.0D;
/*     */   double initialK1;
/*     */   double initialK2;
/*  59 */   public static int typeOfClusters = 5;
		    public static double maxvalue = 20.0;
/*  60 */   public static int nbTargets = 1;
/*  61 */   

			protected int popSize = 50;
/*     */ 
/*  64 */   SequenceVertex[] toAdapt = {new SequenceVertex(Integer.valueOf(1), false), new SequenceVertex(Integer.valueOf(2), false), new SequenceVertex(Integer.valueOf(3), true), new SequenceVertex(Integer.valueOf(4), true) };
/*  65 */   final String[] tempKms = { "1->1", "2->2", "1->4", "2->3" };
			final String[] stacks = { "1->1", "2->2", "1->4", "2->3" };
/*  66 */   int paramSize = this.toAdapt.length + this.tempKms.length+ this.stacks.length+ 2; // not evolving exo anymore
/*     */   private Random rand;
/*  70 */   public CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
/*     */ 
/*     */   PhaseOptimization(double[][][] data, VisualFrame main) {
/*  73 */     this.dataArray.add(data);
/*  74 */     this.initialConditions.add(MyTableModelListener.generateTable(main.target));
/*  75 */     this.main = main;
/*     */ 	
/*  79 */     this.rand = new Random();
/*     */ 
/*  81 */     //this.cma.readProperties("../CMAEvolutionStrategy.properties");
/*  82 */     this.cma.readProperties();
				//this.cma.setDimension(this.paramSize);
/*  83 */     //@SuppressWarnings("unused")
//double[] initialX = this.cma.getInitialX();//new double[this.paramSize];
/*  84 */    // for (int i = 0; i < this.paramSize; i++) {
/*  85 */     //  initialX[i] = 1.0D;
/*     */    // }
/*     */ 
/*  88 */     //this.cma.setInitialX(initialX);
			  this.cma.parameters.setPopulationSize(popSize);
/*  89 */     this.cma.setInitialStandardDeviation(optimizer.Constants.sigma);
/*  90 */     this.cma.options.stopFitness = 0.001D;
/*     */   }
/*     */ 
/*     */   PhaseOptimization(int size, VisualFrame main, File open) {
/*  94 */     this.dataArray.add(generateData(size, open));
/*  95 */     this.initialConditions.add(MyTableModelListener.generateTable(main.target));
/*  96 */     this.main = main;
/*     */ 
/*  98 */     
/*     */ 
/* 101 */     
/* 109 */     System.out.println(main.target1 + " " + main.target2);
/*     */ 
/* 113 */     this.rand = new Random();
/*     */ 
/* 115 */     this.cma.readProperties();
/* 116 */     //this.cma.setDimension(this.paramSize);
/* 117 */     //double[] initialX = new double[this.paramSize];
/* 118 */     //for (int i = 0; i < this.paramSize; i++) {
/* 119 */     //  initialX[i] = 1.0D;
/*     */     //}
/*     */ 
/* 122 */     //this.cma.setInitialX(initialX);
		      this.cma.parameters.setPopulationSize(popSize);
/* 123 */     this.cma.setInitialStandardDeviation(optimizer.Constants.sigma);
/* 124 */     this.cma.options.stopFitness = 0.001D;
/*     */   }
/*     */ 

/*     */   PhaseOptimization(int conditions, int size, VisualFrame main) {
/* 128 */     this.nbTargets = conditions;
/* 129 */     this.main = main;
//System.out.println("No");
/* 130 */     for (int i = 0; i < conditions; i++) {
	//System.out.println("Can you repeat the question");
/* 131 */       generateDataTarget(i,size,null);
				
/*     */     }
/*     */ 
/* 148 */     //System.out.println(main.target1 + " " + main.target2);
/*     */ 
/* 152 */     this.rand = new Random();
/*     */ 
/* 154 */     this.cma.readProperties();
/* 155 */     //this.cma.setDimension(this.paramSize);
/* 156 */     //double[] initialX = new double[this.paramSize];
/* 157 */     //for (int i = 0; i < this.paramSize; i++) {
/* 158 */     //  initialX[i] = 1.0D;
/*     */     //}
/*     */ 
/* 161 */     //this.cma.setInitialX(initialX);
			  this.cma.parameters.setPopulationSize(popSize);
/* 162 */     this.cma.setInitialStandardDeviation(optimizer.Constants.sigma);
/* 163 */     this.cma.options.stopFitness = 0.001D;
/*     */   }


		PhaseOptimization(int size, VisualFrame main, File[] targets) {
			  int conditions = targets.length;
/* 128 */     this.nbTargets = conditions;
/* 129 */     this.main = main;
//System.out.println("No");
/* 130 */     for (int i = 0; i < conditions; i++) {
	//System.out.println("Can you repeat the question");
/* 131 */       generateDataTarget(i,size,targets[i]);
				
/*     */     }
/*     */ 
/* 148 */     //System.out.println(main.target1 + " " + main.target2);
/*     */ 
/* 152 */     this.rand = new Random();
/*     */ 
/* 154 */     this.cma.readProperties();
/* 155 */     //this.cma.setDimension(this.paramSize);
/* 156 */     //double[] initialX = new double[this.paramSize];
/* 157 */     //for (int i = 0; i < this.paramSize; i++) {
/* 158 */     //  initialX[i] = 1.0D;
/*     */     //}
/*     */ 
/* 161 */     //this.cma.setInitialX(initialX);
			  this.cma.parameters.setPopulationSize(popSize);
/* 162 */     this.cma.setInitialStandardDeviation(optimizer.Constants.sigma);
/* 163 */     this.cma.options.stopFitness = 0.001D;
/*     */   }
/*     */ 

			private void generateDataTarget(int index, int size, File t){
				
					//TODO MOAR HARDCODED MADNESS
					double[] values;
					if(t.getName().contains("T")){
						values = new double[]{0.1,10.0};
					} else {
						values = new double[]{10.0,0.1};
					}
					this.dataArray.add((colorGradiant?DiagramFileReader.generateDataInline(size,140.0/(size-1),200.0/(size-1),t):generateData(size,t)));
					this.initialConditions.add(VisualFrame.getInlineConditions(this.main.target, new String[]{"Init s1","Init s2"}, values));
					
				
			}

/*     */   
/*     */ 
/*     */   private OligoGraph<SequenceVertex, String> createGraph(int index, double[] hope) {
/* 171 */     OligoGraph<SequenceVertex,String> g = (OligoGraph<SequenceVertex,String>) this.main.target.clone();
/*     */ 
/* 174 */     HashMap<String,Object> t = this.initialConditions.get(index);
/*     */ 
/* 176 */     MyTableModelListener.updateAll(t, g);
/*     */ 
/* 178 */     for (int i = 0; i < this.toAdapt.length; i++) {
/* 179 */       SequenceVertex seq = (SequenceVertex)g.getEquivalentVertex(this.toAdapt[i]);
/* 180 */       SequenceVertex oldseq = (SequenceVertex)this.main.target.getEquivalentVertex(seq);
/* 181 */       g.K.put(seq, Double.valueOf(((Double)this.main.target.K.get(oldseq)).doubleValue() * hope[i]));
/*     */     }
/* 183 */     for (int i = 0; i < this.tempKms.length; i++) {
/* 184 */       g.setTemplateExoKm(this.tempKms[i], hope[(this.toAdapt.length + i)] * model.Constants.exoKmTemplate);
/*     */     }
			  for (int i = 0; i < this.stacks.length; i++) {
/* 184 */       g.stackSlowdown.put(this.stacks[i], hope[(this.toAdapt.length + this.tempKms.length + i)] * this.main.target.stackSlowdown.get(this.stacks[i]));
/*     */     }
/* 186 */     //g.exoConc = hope[(this.toAdapt.length + this.tempKms.length)];
/* 187 */     //g.polConc = hope[(this.toAdapt.length + this.tempKms.length +this.stacks.length)];
/* 188 */     //g.nickConc = hope[(this.toAdapt.length + this.tempKms.length +this.stacks.length+ 1)];
/* 189 */    // g.nickKmBoth = this.main.target.nickKmBoth*hope[(this.toAdapt.length + this.tempKms.length +this.stacks.length+ 2)];
/*     */ 
/* 191 */     return g;
/*     */   }
/*     */ 
/*     */   
/*     */ 
/*     */   public double[][][] simulateRange(int index, double[] value)
/*     */   {
/* 225 */     double[][][] data = (double[][][])this.dataArray.get(index);
/* 226 */     double[][][] values = new double[data.length][data[0].length][VisualFrame.numberOfTriggers];
/* 227 */     //final OligoGraph<SequenceVertex,String> g = createGraph(index, value);
/* 228 */     double concStep1 = (maxConc1 - minConc1) / data.length;
/* 229 */     double concStep2 = (maxConc2 - minConc2) / data.length;
/* 230 */     double value1 = minConc1;
/* 231 */     double value2 = minConc2;
/* 232 */     ArrayList<Callable> call = new ArrayList<Callable>();
/*     */ 
/* 234 */     for (int i = 0; i < data.length; i++) {
/* 235 */       for (int j = 0; j < data[0].length; j++) {
					if(!adrian){
/* 236 */         		call.add(generateCallObject(index, value1, value2, i, j, value));
					} else {
						call.add(generateCallObjectAdrian(index, value1, value2, i, j, value));
					}
/* 237 */         value2 += concStep2;
/*     */       }
/* 239 */       value1 += concStep1;
/* 240 */       value2 = minConc2;
/*     */     }
/*     */ 
/* 243 */     ExecutorService pool = Executors.newFixedThreadPool(this.main.numberOfThreads);
/* 244 */     CompletionService cservice = new ExecutorCompletionService(pool);
/* 245 */     int size = call.size();
/* 246 */     int completed = 0;
/* 247 */     for (Callable run : call) {
/* 248 */       cservice.submit(run);
/*     */     }
/* 250 */     while (completed < size) {
/*     */       try {
/* 252 */         Future future = cservice.take();
/*     */ 
/* 254 */         if (future != null) {
/* 255 */           completed++;
/* 256 */           ComputationResult res = (ComputationResult)future.get();
/* 257 */           values[res.i][res.j] = res.value;
/*     */         }
/*     */       } catch (ExecutionException ex) {
/* 260 */         System.err.println("FUBAR: " + ex);
/* 261 */         Logger.getLogger(PhaseOptimization.class.getName()).log(Level.SEVERE, null, ex);
/*     */       } catch (InterruptedException ex) {
/* 263 */         System.err.println("FUBAR: " + ex);
/* 264 */         Logger.getLogger(PhaseOptimization.class.getName()).log(Level.SEVERE, null, ex);
/*     */       }
/*     */     }
/* 267 */     pool.shutdown();
/* 268 */     return values;
/*     */   }
/*     */ 
/*     */   public double computeDistance(int index, double[][][] values) {
/* 272 */     double[][][] data = (double[][][])this.dataArray.get(index);
/* 273 */     double result = 0.0D;
/* 274 */     for (int i = 0; i < values.length; i++) {
/* 275 */       for (int j = 0; j < values[0].length; j++) {
				if(values[i][j][0] != -1 && data[i][j][0] != -1){ //-1 means we didn't want to compute it.
/* 276 */         //result += (data[i][j] != values[i][j]?1.0:0.0);
					for(int k = 0; k<data[i][j].length; k++)
					result += (data[i][j][k] - values[i][j][k]) * (data[i][j][k] - values[i][j][k]);
				}
/*     */       }
/*     */     }
/* 279 */     System.out.println("Distance to target: "+result);
/* 280 */     
/* 282 */     return result;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */ 
/* 302 */     double[] fitness = this.cma.init();
/*     */ 
/* 305 */     this.cma.writeToDefaultFilesHeaders(0);
/*     */ 
/* 307 */     int nbResample = 0;
/*     */ 
/* 311 */     while (this.cma.stopConditions.getNumber() == 0)
/*     */     {
/* 314 */       double[][] pop = this.cma.samplePopulation();
/* 315 */       for (int i = 0; i < pop.length; i++)
/*     */       {
/* 319 */         while (!isFeasible(pop[i])) {
/* 320 */           pop[i] = this.cma.resampleSingle(i);
/* 321 */           nbResample++;
/* 322 */           if (nbResample % 50 == 0) {
/* 323 */             //JOptionPane.showMessageDialog(this.main, "Resampling " + nbResample);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 329 */         fitness[i] = valueOf(pop[i]);
/*     */       }
/*     */ 
/* 334 */       this.cma.updateDistribution(fitness);
/*     */ 
/* 338 */       this.cma.writeToDefaultFiles();
/* 339 */       int outmod = 5;
/* 340 */       if (this.cma.getCountIter() % (15 * outmod) == 1L) {
/* 341 */         String output = this.cma.getPrintAnnotation();
/* 342 */         System.out.println(output);
/*     */       }
/*     */ 
/* 345 */       if (this.cma.getCountIter() % outmod == 1L) {
/* 346 */         String output = this.cma.getPrintLine();
/* 347 */         System.out.println(output);
/* 348 */         
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 354 */     //JOptionPane.showMessageDialog(this.main, "We're done here");
/* 355 */     String output = this.cma.getPrintLine();
/* 356 */     System.out.println(output);
/*     */ 
/* 358 */     output = "Terminated due to\n";
/*     */ 
/* 361 */     for (String s : this.cma.stopConditions.getMessages())
/* 362 */       output = output + "  " + s + "\n";
/* 363 */     output = output + "best function value " + this.cma.getBestFunctionValue() + " at evaluation " + this.cma.getBestEvaluationNumber();
/*     */ 
/* 366 */     System.out.println(output);
/* 367 */     //JOptionPane.showMessageDialog(this.main, output);
/*     */ 
/* 370 */     this.cma.setFitnessOfMeanX(valueOf(this.cma.getMeanX()));
/*     */ 
/* 373 */     this.cma.writeToDefaultFiles(1);
/*     */   }

			public static void launchBatch(int repeats,File gr,File t1,File t2){
				
				  VisualFrame vf = new VisualFrame();
				  
					  vf.loadGraph(gr);
				  if(!adrian){
					  vf.triggers[0] = "custom0.5 * temp2->3 < in2->3 + both2->3 + ext2->3";
					  vf.triggers[1] = "custom0.5 * temp1->4 < in1->4 + both1->4 + ext1->4";
					  } else {
					  //For Adrian model:
					  vf.triggers[0] = "custom20.0 < s2";
					  vf.triggers[1] = "custom20.0 < s1";
					  for(SequenceVertex seq : vf.target.K.keySet()){
						  vf.target.K.put(seq, 1.0); //strengths are really different in this model...
					  }
					  Constants.numberOfPoints= 100;
					  }
				  vf.target1 = "1->1";
				  vf.target2 = "2->2";
				  vf.target.nickSaturationByProduct = PhaseOptimization.satbyprod;
				  vf.target.nickKmBoth = 10;
				  model.Constants.nickKm = 30;
				  model.Constants.nickKmBoth = 30;
				  model.Constants.numberOfPoints = 600;
				  vf.target.setTemplateExoKm("1->1", 10);
				  vf.target.setTemplateExoKm("2->2", 10);
				  vf.target.setTemplateExoKm("2->3", 10);
				  vf.target.setTemplateExoKm("1->4", 10);
				  model.Constants.exoKmTemplate = 10;
				  vf.target.exoSaturationByFreeTemplates = PhaseOptimization.satbytemp;
				  vf.target.getEquivalentVertex(new SequenceVertex(1)).initialConcentration = 10.0;
				  vf.target.getEquivalentVertex(new SequenceVertex(2)).initialConcentration = 0.1;
				  vf.rangemax1 = 140;
				  vf.step1 = 7;
				  vf.step2 = 10;
				  vf.numberOfThreads = 44;
				  PhaseOptimization p;
				  if(t1==null && t2==null){
					  p = new PhaseOptimization(2,25,vf);
				  } else {
					  p = new PhaseOptimization(25,vf,new File[] {t1,t2});  
				  }
				  
				  for (int i =0; i<repeats; i++){
					  p.cma = new CMAEvolutionStrategy();
					  p.cma.readProperties();
					  
					  p.cma.parameters.setPopulationSize(p.popSize);
					  p.cma.setInitialStandardDeviation(optimizer.Constants.sigma);
					  p.cma.options.stopFitness = 0.001D;
					  p.cma.options.outputFileNamesPrefix += ""+i;
					  p.run();
				  }
			}
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
				
					if(args.length <= 2){
						System.out.println("Usage: graphFile target1 target2 [number of runs [isNickSatbyProd isExoSatbyTemp]]");
						
						return;
					}
					System.out.println(args[0]+" "+args[1]+" "+args[2]);
					File gr = new File(args[0]);
					File t1 = new File(args[1]);
					File t2 = new File(args[2]);
					if(args.length >=4){
						PhaseOptimization.numruns = Integer.parseInt(args[3]);
					}
					if(args.length >=5){
						PhaseOptimization.satbyprod = Boolean.valueOf(args[4]);
					}
					if(args.length >=6){
						PhaseOptimization.satbytemp = Boolean.valueOf(args[5]);
					}
					System.out.println("Number of runs :"+PhaseOptimization.numruns);
					launchBatch(PhaseOptimization.numruns,gr,t1,t2);
					
			
/*     */   }
/*     */ 
/*     */   public double valueOf(double[] doubles)
/*     */   {
/* 401 */     double distance = 0.0D;
/* 402 */     for (int index = 0; index < this.dataArray.size(); index++) {
/* 403 */       distance += computeDistance(index, simulateRange(index, doubles));
/*     */     }
/* 405 */     return distance;
/*     */   }
/*     */ 
/*     */   public boolean isFeasible(double[] doubles)
/*     */   {
/* 410 */     for (int i = 0; i < doubles.length; i++) {
/* 411 */       if (doubles[i] <= 0.0D || doubles[i] >= 1000.0D) {
/* 412 */         return false;
/*     */       }
/*     */     }
/* 415 */     return true;
/*     */   }
/*     */ 
/*     */   private double[] classify(double[] ofInterest, OligoSystem<String> model) {
/* 419 */     double[] result = new double[VisualFrame.numberOfTriggers];
/*     */ 
/* 421 */     int[] positions = new int[VisualFrame.numberOfTriggers];
			  
/*     */ 
/* 423 */     for (int i = 0; i < VisualFrame.numberOfTriggers; i++) {
/* 424 */       if (!this.main.triggers[i].startsWith("custom")) {
/* 425 */         positions[i] = Arrays.asList(model.giveNames()).indexOf(this.main.triggers[i]);
/* 426 */         if (positions[i] == -1) {
/* 427 */           positions[i] = i;
/*     */         }
/* 429 */         if (ofInterest[positions[i]] > this.main.threashold)
/* 430 */           result[i] = ofInterest[positions[i]];
/*     */       }
/*     */       else {
/* 433 */         ClusterFunction cf = ClusterFunction.parseClusterCondition(this.main.triggers[i].substring(6));
/*     */ 			if(colorGradiant){
						cf.isTargetState(ofInterest, model);
						result[i] = cf.valueRight/mainFactor;
						
					} else {
/* 435 */         	result[i] = (cf.isTargetState(ofInterest, model) ? this.main.threashold +1 : 0.0D);
					}
					
/*     */       }
/*     */     }
			  
/*     */ 		
/* 439 */     return result;
/*     */   }


/*     */ 
/*     */   public static double[][][] generateData(int size, File open) {
/* 443 */     double[][][] results = new double[size][size][VisualFrame.numberOfTriggers];
/* 444 */     double[][][] amt = new double[size][size][typeOfClusters];
/* 445 */     double step1 = (maxConc1 - minConc1) / (size - 1.0D);
/* 446 */     double step2 = (maxConc2 - minConc2) / (size - 1.0D);
/*     */ 
/* 450 */     
/* 452 */     if (open != null) {
/* 453 */       FileReader in = null;
/*     */       try {
/* 455 */        
/* 456 */         in = new FileReader(open);
/* 457 */         BufferedReader reader = new BufferedReader(in);
/* 458 */         String line = null;
/* 459 */         line = reader.readLine();
/*     */ 
/* 461 */         while (line.startsWith("#")) {
/* 462 */           line = reader.readLine();
/*     */         }
/* 464 */         while ((line = reader.readLine()) != null)
/* 465 */           if (!line.startsWith("#"))
/*     */           {
/* 468 */             String[] split = line.split("\t");
/*     */ 
/* 470 */             int x = (int)Math.floor(Double.parseDouble(split[0]) / step1);
						
/* 471 */             int y = (int)Math.floor(Double.parseDouble(split[3]) / step2);
/* 472 */             double type = Double.parseDouble(split[6]);
/*     */ 				if(x<=amt.length-1 && y <=amt[0].length-1){
/* 475 */             x = Math.max(x, 0);
/* 476 */             y = Math.max(y, 0);
/*     */ 
/* 478 */             amt[x][y][((int)type - 1)] += 1.0D;
						}
/*     */           }
/*     */       }
/*     */       catch (Exception ex) {
				  
/* 482 */         
/* 483 */         Logger.getLogger(PhaseOptimization.class.getName()).log(Level.SEVERE, null, ex);
/*     */       } finally {
/*     */         try {
/* 486 */           in.close();
/*     */         } catch (IOException ex) {
/* 488 */           
/* 489 */           Logger.getLogger(PhaseOptimization.class.getName()).log(Level.SEVERE, null, ex);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 494 */     for (int i = 0; i < size; i++) {
/* 495 */       String view = "";
/* 496 */       for (int j = 0; j < size; j++) {
/* 497 */         int max = (int)amt[i][j][0];
/* 498 */         int type = 0;
/* 499 */         for (int k = 1; k < typeOfClusters; k++) {
/* 500 */           if (max < amt[i][j][k]) {
/* 501 */             max = (int)amt[i][j][k];
/* 502 */             type = k;
/*     */           }
/*     */         }
					if(max < 0){
						type = -1;
					}
/* 505 */         results[i][j][0] = type;
/* 506 */         view = view + " " + type;
/*     */       }
/*     */ 
/* 509 */       System.out.println(view);
/*     */     }
/*     */ 
/* 512 */     return results;
/*     */   }
			
			
public static void exportGraph(OligoGraph<SequenceVertex,String> g, File selectedFile){
	
    try{
    if(selectedFile != null){
    	if (!selectedFile.exists()){
    		selectedFile.createNewFile();
    	}
    	
    	FileWriter fw = new FileWriter(selectedFile);
    	fw.write(CodeGenerator.exportToSBMLcode(g));
    	fw.flush();
    	fw.close();
    }
    } catch (IOException e) {
    	e.printStackTrace();
    }
	
}

private Callable generateCallObjectAdrian(final int index, final double local1, final double local2, final int i, final int j, final double[] doubles) {
/* 516 */     Callable obj = null;
/* 517 */     obj = new Callable()
/*     */     {
/* 519 */       OligoGraph<SequenceVertex,String> myGraph = PhaseOptimization.this.createGraph(index, doubles);
/*     */ 
/* 521 */       double value1 = local1;
/* 522 */       double value2 = local2;
/*     */ 
/*     */       public Object call()
/*     */         throws Exception
/*     */       {
/* 528 */         //this.myGraph.exoConc = PhaseOptimization.this.main.target.exoConc;
/* 529 */         //this.myGraph.polConc = PhaseOptimization.this.main.target.polConc;
/* 530 */         //this.myGraph.nickConc = PhaseOptimization.this.main.target.nickConc;
	
				  if(!computeAll && !PhaseOptimization.this.shouldCompute.get(index)[i][j]){
					  return new ComputationResult(i, j, new double[]{-1});
				  }
/*     */ 
/* 534 */         PhaseOptimization.this.main.setTarget1(this.value1 + PhaseOptimization.this.main.step1 / 2.0D, this.myGraph);
/* 535 */         PhaseOptimization.this.main.setTarget2(this.value2 + PhaseOptimization.this.main.step2 / 2.0D, this.myGraph);
/*     */ 
/* 540 */         OligoSystemAdrian modl = new OligoSystemAdrian(this.myGraph.getVertexCount(),this.myGraph);
/*     */ 
/* 546 */         double[][] res = modl.calculateTimeSeries();
/*     */ 
/* 548 */         double[] ofInterest = new double[res.length];
/* 549 */         for (int var = 0; var < res.length; var++) {
/* 550 */           ofInterest[var] = res[var][(res[var].length - 1)];
/*     */         }
/* 552 */         return new ComputationResult(i, j, PhaseOptimization.this.classify(ofInterest, modl));
/*     */       }
/*     */     };
/* 555 */     return obj;
/*     */   }


/*     */ 
/*     */   private Callable generateCallObject(final int index, final double local1, final double local2, final int i, final int j, final double[] doubles) {
/* 516 */     Callable obj = null;
/* 517 */     obj = new Callable()
/*     */     {
/* 519 */       OligoGraph<SequenceVertex,String> myGraph = PhaseOptimization.this.createGraph(index, doubles);
/*     */ 
/* 521 */       double value1 = local1;
/* 522 */       double value2 = local2;
/*     */ 
/*     */       public Object call()
/*     */         throws Exception
/*     */       {
/* 528 */         //this.myGraph.exoConc = PhaseOptimization.this.main.target.exoConc;
/* 529 */         //this.myGraph.polConc = PhaseOptimization.this.main.target.polConc;
/* 530 */         //this.myGraph.nickConc = PhaseOptimization.this.main.target.nickConc;
	
				  if(!computeAll && !PhaseOptimization.this.shouldCompute.get(index)[i][j]){
					  return new ComputationResult(i, j, new double[]{-1});
				  }
/*     */ 
/* 534 */         PhaseOptimization.this.main.setTarget1(this.value1 + PhaseOptimization.this.main.step1 / 2.0D, this.myGraph);
/* 535 */         PhaseOptimization.this.main.setTarget2(this.value2 + PhaseOptimization.this.main.step2 / 2.0D, this.myGraph);
/*     */ 
/* 540 */         OligoSystem<String> modl = new OligoSystem<String>(this.myGraph){
	@Override
	public void displayProfiling(){
		
	}
};
/*     */ 
/* 546 */         double[][] res = modl.calculateTimeSeries(null);
/*     */ 
/* 548 */         double[] ofInterest = new double[res.length];
/* 549 */         for (int var = 0; var < res.length; var++) {
/* 550 */           ofInterest[var] = res[var][(res[var].length - 1)];
/*     */         }
/* 552 */         return new ComputationResult(i, j, PhaseOptimization.this.classify(ofInterest, modl));
/*     */       }
/*     */     };
/* 555 */     return obj;
/*     */   }
/*     */ }

/* Location:           /Users/Lab_Member/Desktop/Nat/Bifurcation 2/dist/Bifurcation.jar
 * Qualified Name:     bifurcation.PhaseOptimization
 * JD-Core Version:    0.6.2
 */