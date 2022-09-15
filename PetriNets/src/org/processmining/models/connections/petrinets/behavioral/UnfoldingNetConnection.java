/**
 * 
 */
package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.CTMarking;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * Connection between petri net, marking, and unfolding
 * 
 * @author Arya Adriansyah, Dirk Fahland
 * @email a.adriansyah@tue.nl, dirk.fahland@service-technology.org
 * @version Mar 23, 2010
 * 
 */
public class UnfoldingNetConnection extends AbstractSemanticConnection {

	public final static String PREFIXNET = "complete prefix of unfolding";
	public final static String PREFIXNET_OMEGA = "finite prefix of omega-unfolding";
	public final static String PREFIXMARK = "prefix marking";

	public UnfoldingNetConnection(PetrinetGraph originalNet, Marking originalMarking,
			Semantics<Marking, Transition> semantics, PetrinetGraph prefixNet, CTMarking prefixMarking) {
		super("Finite prefix of " + originalNet.getLabel(), originalNet, originalMarking, semantics);
		put(PREFIXNET_OMEGA, prefixNet);
		put(PREFIXMARK, prefixMarking);
	}

	/**
	 * connect Petri net to its finite complete prefix
	 * 
	 * @param originalNet
	 * @param originalMarking
	 * @param semantics
	 * @param prefixNet
	 * @param prefixMarking
	 */
	public UnfoldingNetConnection(PetrinetGraph originalNet, Marking originalMarking,
			Semantics<Marking, Transition> semantics, PetrinetGraph prefixNet, Marking prefixMarking) {
		super("Connection to complete prefix of " + originalNet.getLabel(), originalNet, originalMarking, semantics);
		put(PREFIXNET, prefixNet);
		put(PREFIXMARK, prefixMarking);
	}

	/**
	 * connect Petri net to its finite complete prefix
	 * 
	 * @param originalNet
	 * @param originalMarking
	 * @param semantics
	 * @param prefixNet
	 */
	public UnfoldingNetConnection(PetrinetGraph originalNet, Marking originalMarking,
			Semantics<Marking, Transition> semantics, PetrinetGraph prefixNet) {
		super("Complete prefix of " + originalNet.getLabel(), originalNet, originalMarking, semantics);
		put(PREFIXNET, prefixNet);
	}

}
