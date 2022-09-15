package org.processmining.models.graphbased.directed.petrinet.impl;

import java.util.Map;

import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.semantics.petrinet.Marking;

@Plugin(name = "Convert to Reset-net", returnLabels = { "Reset-net", "Marking" }, returnTypes = { ResetNet.class,
		Marking.class }, parameterLabels = { "Net", "Marking" }, help = "Converts nets to Reset-nets.", userAccessible = true)
public class ToResetNet {

	@PluginVariant(variantLabel = "From Marked Petrinet", requiredParameterLabels = { 0, 1 })
	public static Object[] fromPetrinet(PluginContext context, Petrinet net, Marking marking)
			throws ConnectionCannotBeObtained {
		return fromPetrinetInternal(context, net, marking);
	}

	@PluginVariant(variantLabel = "From Unmarked Petrinet", requiredParameterLabels = { 0 })
	public static Object[] fromPetrinet(PluginContext context, Petrinet net) throws ConnectionCannotBeObtained {
		return fromPetrinetInternal(context, net, null);
	}

	private static Object[] fromPetrinetInternal(PluginContext context, Petrinet net, Marking marking)
			throws ConnectionCannotBeObtained {
		if (marking != null) {
			// Check for connection
			context.getConnectionManager().getFirstConnection(InitialMarkingConnection.class, context, net, marking);
		}

		ResetNetImpl newNet = new ResetNetImpl(net.getLabel());
		Map<DirectedGraphElement, DirectedGraphElement> mapping = newNet.cloneFrom(net);

		Marking newMarking = ToResetInhibitorNet.cloneMarking(marking, mapping);

		context.addConnection(new InitialMarkingConnection(newNet, newMarking));
		context.getFutureResult(0).setLabel(net.getLabel());
		context.getFutureResult(1).setLabel("Initial Marking of " + net.getLabel());

		return new Object[] { newNet, newMarking };
	}

}
