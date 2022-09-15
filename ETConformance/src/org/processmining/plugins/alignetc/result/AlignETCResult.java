/**
 * 
 */
package org.processmining.plugins.alignetc.result;

import org.processmining.plugins.alignetc.core.ReplayAutomaton;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;

/**
 * Results and Settings of the ETConformance Oracle analysis.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class AlignETCResult {
	
	//SETTINGS
	/** Threshold parameter value for defining the escaping states */
	public double escTh;
	
	//RESULTS
	/** Alignment ETCPrecision Metric */
	public double ap = 0;	
	/** Alignment ETCPrecision metric Numerator */
	public double apNumerator = 0;
	/** Alignment ETCPrecision metric Denominator */
	public double apDenominator = 0;
	
	/** Number of IN states in the automaton */
	public int nStates = 0;
	/** Number of imprecisions in the automaton */
	public int nImprecisions = 0;
	/** Number of cut imprecisions in the automaton */
	public int nCut = 0;
	
	//OBJECTS
	/** Alignments */
	public PNMatchInstancesRepResult alignments = null;
	/** Replay Automaton */
	public ReplayAutomaton ra = null;
	
	
	/**
	 * Create a new Result object and set all the fields to default values.
	 */
	public AlignETCResult(){
		//Default values of Settings
		escTh = 0.00;
	}

}
