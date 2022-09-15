/**
 * 
 */
package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.AggCGGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * @author Arya Adriansyah
 * @email a.adriansyah@tue.nl
 * @version Jun 10, 2010
 */
public class AggCGGraphConnection extends AbstractConnection {
	public final static String NET = "Net";
	public final static String MARKING = "Marking";
	public final static String AGGCSGRAPH = "AggCSGraph";
	public final static String SEMANTICS = "Semantics";

	public AggCGGraphConnection(PetrinetGraph net, Marking marking, Semantics<Marking, Transition> semantics,
			AggCGGraph aggCGGraph) {
		super("Aggregated Transition Graph of " + net.getLabel());
		put(NET, net);
		put(MARKING, marking);
		put(AGGCSGRAPH, aggCGGraph);
		put(SEMANTICS, semantics);
	}
}
