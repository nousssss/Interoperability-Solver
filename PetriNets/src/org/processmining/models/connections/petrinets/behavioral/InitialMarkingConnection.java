package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.semantics.petrinet.Marking;

@Plugin(name = "Initial Marking Connection Factory", parameterLabels = { "Net", "Marking" }, returnTypes = InitialMarkingConnection.class, returnLabels = "Marked Net connection", userAccessible = false)
@ConnectionObjectFactory
public class InitialMarkingConnection extends AbstractMarkingNetConnection {

	public InitialMarkingConnection(PetrinetGraph net, Marking m) {
		super(net, m);
		// TODO Auto-generated constructor stub
	}

	private static InitialMarkingConnection constructMarkedNetConnection(PluginContext context, PetrinetGraph net,
			Marking m) {
		if (net.getPlaces().containsAll(m.baseSet())) {
			InitialMarkingConnection connection = new InitialMarkingConnection(net, m);
			context.getFutureResult(0).setLabel(connection.getLabel());
			return connection;
		} else {
			return null;
		}
	}

	@PluginVariant(requiredParameterLabels = { 0, 1 })
	public static InitialMarkingConnection markedNetConnectionFactory(PluginContext context, Petrinet net, Marking m) {
		return constructMarkedNetConnection(context, net, m);
	}

	@PluginVariant(requiredParameterLabels = { 0, 1 })
	public static InitialMarkingConnection markedNetConnectionFactory(PluginContext context, InhibitorNet net, Marking m) {
		return constructMarkedNetConnection(context, net, m);
	}

	@PluginVariant(requiredParameterLabels = { 0, 1 })
	public static InitialMarkingConnection markedNetConnectionFactory(PluginContext context, ResetNet net, Marking m) {
		return constructMarkedNetConnection(context, net, m);
	}

	@PluginVariant(requiredParameterLabels = { 0, 1 })
	public static InitialMarkingConnection markedNetConnectionFactory(PluginContext context, ResetInhibitorNet net,
			Marking m) {
		return constructMarkedNetConnection(context, net, m);
	}

}
