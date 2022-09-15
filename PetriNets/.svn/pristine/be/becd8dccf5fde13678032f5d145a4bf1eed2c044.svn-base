/**
 * 
 */
package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.framework.connections.impl.AbstractStrongReferencingConnection;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * @author arya
 * Connection to end marking of a net
 */
public class FinalMarkingConnection extends AbstractStrongReferencingConnection {
	public final static String NET = "Net";
	public final static String MARKING = "Marking";

	public FinalMarkingConnection(PetrinetGraph net, Marking m) {
		super("Connection " + net.getLabel() + " with final marking " + m.toString());
		put(NET, net);
		putStrong(MARKING, m);
	}

	private static FinalMarkingConnection constructMarkedNetConnection(PluginContext context, PetrinetGraph net,
			Marking m) {
		if (net.getPlaces().containsAll(m.baseSet())) {
			FinalMarkingConnection connection = new FinalMarkingConnection(net, m);
			context.getFutureResult(0).setLabel(connection.getLabel());
			return connection;
		} else {
			return null;
		}
	}

	@PluginVariant(requiredParameterLabels = { 0, 1 })
	public static FinalMarkingConnection markedNetConnectionFactory(PluginContext context, Petrinet net, Marking m) {
		return constructMarkedNetConnection(context, net, m);
	}

	@PluginVariant(requiredParameterLabels = { 0, 1 })
	public static FinalMarkingConnection markedNetConnectionFactory(PluginContext context, InhibitorNet net, Marking m) {
		return constructMarkedNetConnection(context, net, m);
	}

	@PluginVariant(requiredParameterLabels = { 0, 1 })
	public static FinalMarkingConnection markedNetConnectionFactory(PluginContext context, ResetNet net, Marking m) {
		return constructMarkedNetConnection(context, net, m);
	}

	@PluginVariant(requiredParameterLabels = { 0, 1 })
	public static FinalMarkingConnection markedNetConnectionFactory(PluginContext context, ResetInhibitorNet net,
			Marking m) {
		return constructMarkedNetConnection(context, net, m);
	}

}