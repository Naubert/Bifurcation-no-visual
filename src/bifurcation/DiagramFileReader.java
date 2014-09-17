package bifurcation;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;



public class DiagramFileReader {
	
	static double stepx = 0.5; //nM
	static double stepy = 0.5; //nM
	
	
	
	public static double[][][] generateDataInline(int size, double desiredStepx, double desiredStepy, File open){
		double[][][] results = new double[size][size][VisualFrame.numberOfTriggers]; 
		int trueindexX = 0;
		int trueindexY = 0;
		
		double currentx = 0;
		double currenty = 0;
	

	/* 453 */       FileReader in = null;
	/*     */       try {
	/* 455 */        
	/* 456 */         in = new FileReader(open);
	/* 457 */         BufferedReader reader = new BufferedReader(in);
	/* 458 */         String line = null;
	/* 459 */         line = reader.readLine();
	/*     */ 
		
	/* 461 */        
	/* 464 */         while ((line = reader.readLine()) != null)
	/* 465 */           if (!line.startsWith("#"))
	/*     */           {
						  
						  String temp = line.substring(2, line.length()-1);
	/* 468 */             String[] split = temp.split("\\{");
							
							if(currentx % desiredStepx < stepx && trueindexX< size){
								currenty = 0;
								trueindexY = 0;
	/*     */ 					for(int index = 0; index < split.length; index++){
									//System.out.println(" desiredStepx "+desiredStepx+" desiredStepy "+desiredStepy+" valmod "+currenty);
									if(currenty % desiredStepy < stepy ){ //we are going to average
									String[] bothValues = split[index].substring(0,split[index].length()-(index == split.length-1?2:3)).split(", ");
									if(trueindexY >= size){
										break;
									}
									//blending the two. The first color is 1-val by the way because the reporter is inverted
									double val2 = 1 - Double.parseDouble(bothValues[0]);
									double val1 = Double.parseDouble(bothValues[1]);
									double totalIntensity =  Math.max(val1 + val2, 1.0);
									
									//System.out.println(" "+r+" "+g+" "+b);
									results[trueindexX][trueindexY][0] = val1;
									results[trueindexX][trueindexY][1] = val2;
									trueindexY++;
									}
									currenty += stepy;
								}
								trueindexX++;
							}
						  currentx += stepx;
						}
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
	/*     */     
	return results;
	}
	
	

}
