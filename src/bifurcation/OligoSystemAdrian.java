package bifurcation;

import java.util.ArrayList;

import model.InvalidConcentrationException;
import model.OligoGraph;
import model.OligoSystem;
import model.SequenceVertex;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.GraggBulirschStoerIntegrator;



import utils.MyEventHandler;
import utils.MyStepHandler;


public class OligoSystemAdrian extends OligoSystem<String> implements FirstOrderDifferentialEquations{

	/**
	 * 
	 */
	
	
	protected int NoOfSimpleSeq;
	protected OligoGraph<SequenceVertex,String> graph;
	protected ArrayList<SequenceVertex> sequences;
	static boolean debug = true;
	public double exo = 4.0; //Value from Anthony. Should be set or evolved.
	public int profiling = 0;
	
	
	public OligoSystemAdrian(int noOfSimpleSeq, OligoGraph<SequenceVertex,String> graph) {
		super(graph);
		this.NoOfSimpleSeq = noOfSimpleSeq;
		this.graph = graph;
		this.sequences = new ArrayList<SequenceVertex>(graph.getVertices());
	}
	
	
	


	@Override
	public void computeDerivatives(double t, double[] y, double[] ydot) {
		if(debug){
			profiling++;
		}
		this.setCurrentConcentration(y);
		
		SequenceVertex seq;
		int where = 0;
		for(int i=0; i<sequences.size(); i++){
			
				seq = sequences.get(i) ;
				if(seq!= null){
					ydot[where] = getTotalCurrentFlux(seq);
					where++;
				}
			}
		
		
	}


	@Override
	public int getDimension() {
		return sequences.size();
	}
	
	public double getTotalCurrentFlux(SequenceVertex s) {
		double flux = 0;
		if (s != null) {
			
			for (SequenceVertex i : sequences) {
				flux += getCurrentFlux(i, s);
			}
			flux += getCurrentNegativeFlux(s);
		}
		return flux;
	}

	public double getCurrentFlux(SequenceVertex i, SequenceVertex j) {
		if (!graph.isInhibitor(j)) {
			return getCurrentFluxSimple(i, j);
		}
		if (graph.isInhibitor(j)) {
			return getCurrentFluxInhib(i, j);
		}

		return 0;
	}

	/**
	 * Basic equation for a flux from i to j with an inhibitor inhib (which
	 * concentration may be 0) is Phyi->j = ([template i->j] *
	 * [i])/((1+[i]+lembda*[Iij]) with lembda Ki/KIij.
	 * 
	 * @param i,j
	 * @return the current flux from a given sequence to another
	 */
	public double getCurrentFluxSimple(SequenceVertex i, SequenceVertex j) {
		String template = graph.findEdge(i, j);
		
		if (template == null || i.getConcentration() == 0 || i == null) {
			return 0;
		}
		double templateitoj = graph.getTemplateConcentration(template);
		double denom = graph.K.get(i)+i.getConcentration();
		SequenceVertex inhib = graph.getInhibition(template).getLeft();
		
		if (inhib != null) {
			denom += inhib.getConcentration()*graph.K.get(i)/graph.K.get(inhib);
		}
		double debug = graph.stackSlowdown.get(template)*templateitoj
				* i.getConcentration() / denom; //TODO: dirty hack where I hide strength in the stackslowdown
		// System.out.println("Flux for "+j+": "+debug);
		return debug;
	}

	/**
	 * Almost the same as the equation from SimpleSequence to SimpleSequence,
	 * but since we don't model inhibitors of inhibitors, the equation is a bit
	 * simpler. Phyi->j = (alpha * [template i->j]*[i])/([i]+a0).
	 * 
	 * @param i
	 * @param j
	 * @return the current flux from a given sequence to an inhibiting sequence
	 */
	public double getCurrentFluxInhib(SequenceVertex i, SequenceVertex j) {
		String template = graph.findEdge(i, j);
		
		if (template == null || i.getConcentration() == 0) {
			return 0;
		}
		double templateitoj = graph.getTemplateConcentration(template);
		double denom = graph.K.get(i)+i.getConcentration();
		double debug =  graph.stackSlowdown.get(template)*templateitoj
				* i.getConcentration() / denom; //TODO same dirty hack as above
		// System.out.println("From "+i+" to "+j+": "+debug);
		return debug;
	}

	/**
	 * Action of the exonuclease enzyme.
	 * 
	 * @param j
	 * @return
	 */
	public double getCurrentNegativeFlux(SequenceVertex j) {
		return -exo*j.getConcentration();
	}

	public double[] initialConditions() {
		double concentration[] = new double[sequences.size()];

		int where = 0;
		for (int i = 0; i < sequences.size(); ++i) {
			if (this.sequences.get(i) != null) {
				concentration[where] = this.sequences.get(i)
						.initialConcentration;
				where++;
			} 
		}
		
		return (concentration);
	}

	// Corrected
	private void setCurrentConcentration(double concentration[]) {
		try {
			int where = 0;
			for (int i = 0; i < sequences.size(); ++i) {
				if (this.sequences.get(i) != null) {
					this.sequences.get(i).setConcentration(
							concentration[where]);
					where++;
				}
			}
			
		} catch (Exception e) {
			System.err.print("Invalid expression  " + e.toString());
			System.exit(1);
		}
	}
	
	public double[][] calculateTimeSeries() {
		//GraggBulirschStoerIntegrator myIntegrator = new GraggBulirschStoerIntegrator(
		//		1e-6, 10000, 1e-6, 1e-6);
		ClassicalRungeKuttaIntegrator myIntegrator = new ClassicalRungeKuttaIntegrator(1e-2); 
		// DormandPrince853Integrator myIntegrator = new
		// DormandPrince853Integrator(1e-11, 10000, 1e-10, 1e-10) ;
		
		
		MyStepHandler handler = new MyStepHandler();
		myIntegrator.addStepHandler(handler);
		
		
		double[] placeholder = this.initialConditions();
		if(debug){
			System.out.println("Starting integration, nb of points: "+model.Constants.numberOfPoints);
		}
		myIntegrator.integrate(this, 0, placeholder, model.Constants.numberOfPoints,
				placeholder);
		if(debug){
			System.out.println(profiling);
		}
		return handler.getTimeSerie();
	}


	public void computePartialDerivatives(double t, int offset, double[] y,
			double[] partialDerivatives) {
		int where = offset;
		for(int i=0;i<this.sequences.size();i++){
			
				if(this.sequences.get(i) != null){
					
						this.sequences.get(i).setConcentration(y[where]);
					
					where++;
				}
			}
		
	    where = 0; //because the partialDerivatives array only takes those sequences into account, not the global state
		for(int i=0;i<this.sequences.size();i++){
			
				if(this.sequences.get(i) != null){
					partialDerivatives[where] = this.getTotalCurrentFlux(this.sequences.get(i));
					where++;
				}
			}
		
		
	}
	
	@Override
	public String[] giveNames(){
		String[] res = new String[this.sequences.size()];
		for(int i=0; i<this.sequences.size();i++){
			res[i] = this.sequences.get(i).toString();
		}
		return res;
	}

}
