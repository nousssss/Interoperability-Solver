/**
 * 
 */
package org.processmining.plugins.petrinet.replayresult.exporting;

import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.InhibitorNetSemantics;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.models.semantics.petrinet.ResetInhibitorNetSemantics;
import org.processmining.models.semantics.petrinet.ResetNetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;

/**
 * @author aadrians
 * May 15, 2013
 *
 */
public class PetrinetGraphPlayOut {
	private enum Semantics { PETRINET, RESETINHIBITORNET, INHIBITORNET, RESETNET }
	
	private Semantics selectedSemantics = null;
	private PetrinetSemantics pnSemantics = null;
	private ResetInhibitorNetSemantics riSemantics = null;
	private ResetNetSemantics rSemantics = null;
	private InhibitorNetSemantics iSemantics = null;
	
	public PetrinetGraphPlayOut(PetrinetGraph net, Marking m){
		// identify semantics
		if (net instanceof ResetInhibitorNet){
			riSemantics = PetrinetSemanticsFactory.elementaryResetInhibitorNetSemantics(ResetInhibitorNet.class);
			riSemantics.setCurrentState(m);
			selectedSemantics = Semantics.RESETINHIBITORNET;
		} else if (net instanceof Petrinet){
			pnSemantics = PetrinetSemanticsFactory.elementaryPetrinetSemantics(Petrinet.class);
			pnSemantics.setCurrentState(m);
			selectedSemantics = Semantics.PETRINET;
		} else if (net instanceof ResetNet){
			rSemantics = PetrinetSemanticsFactory.elementaryResetNetSemantics(ResetNet.class);
			rSemantics.setCurrentState(m);
			selectedSemantics = Semantics.RESETNET;
		} else if (net instanceof InhibitorNet){
			iSemantics = PetrinetSemanticsFactory.elementaryInhibitorNetSemantics(InhibitorNet.class);
			iSemantics.setCurrentState(m);
			selectedSemantics = Semantics.INHIBITORNET;
		}
	}
	
	public void init(Marking m){
		switch(selectedSemantics){
			case PETRINET:
				pnSemantics.setCurrentState(m);
				break;
 			case RESETINHIBITORNET:
				riSemantics.setCurrentState(m);
				break;
			case RESETNET:
				rSemantics.setCurrentState(m);
				break;
			case INHIBITORNET:
				iSemantics.setCurrentState(m);
				break;
		}
	}
	
	public Marking getCurrentMarking(){
		switch(selectedSemantics){
			case PETRINET:
				return pnSemantics.getCurrentState();
 			case RESETINHIBITORNET:
				return riSemantics.getCurrentState();
			case RESETNET:
				return rSemantics.getCurrentState();
			case INHIBITORNET:
				return iSemantics.getCurrentState();
		}
		return null;
	}
	
	public Marking fire(Transition t) throws IllegalTransitionException{
		switch(selectedSemantics){
			case PETRINET:
				pnSemantics.executeExecutableTransition(t);
				return pnSemantics.getCurrentState();
 			case RESETINHIBITORNET:
				riSemantics.executeExecutableTransition(t);
				return riSemantics.getCurrentState();
			case RESETNET:
				rSemantics.executeExecutableTransition(t);
				return rSemantics.getCurrentState();
			case INHIBITORNET:
				iSemantics.executeExecutableTransition(t);
				return iSemantics.getCurrentState();
		}
		return null;
	}
}
