package org.processmining.models.graphbased.directed.petrinet.impl;

import java.util.Map;

import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.Cast;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;

@Plugin(name = "Convert to Reset/Inhibitor-net", returnLabels = { "Reset/Inhibitor-net", "Marking" }, returnTypes = {
		ResetInhibitorNet.class, Marking.class }, parameterLabels = { "Net", "Marking" }, help = "Converts nets to Reset/Inhibitor-nets.", userAccessible = true)
public class ToResetInhibitorNet {

	@PluginVariant(variantLabel = "From Marked Petrinet", requiredParameterLabels = { 0, 1 })
	public static Object[] fromPetrinet(PluginContext context, Petrinet net, Marking marking)
			throws ConnectionCannotBeObtained {
		AbstractResetInhibitorNet tempNet = (AbstractResetInhibitorNet) net;
		return fromPetrinetInternal(context, tempNet, marking);
	}

	@PluginVariant(variantLabel = "From Unmarked Petrinet", requiredParameterLabels = { 0 })
	public static Object[] fromPetrinet(PluginContext context, Petrinet net) throws ConnectionCannotBeObtained {
		AbstractResetInhibitorNet tempNet = (AbstractResetInhibitorNet) net;
		return fromPetrinetInternal(context, tempNet, null);
	}

	@PluginVariant(variantLabel = "From Unmarked InhibitorNet", requiredParameterLabels = { 0 })
	public static Object[] toResetInhibitorNet(PluginContext context, InhibitorNet net) throws Exception {
		// call main method
		AbstractResetInhibitorNet tempNet = (AbstractResetInhibitorNet) net;
		Object[] result = fromPetrinetInternal(context, tempNet, null);

		return result;
	}

	@PluginVariant(variantLabel = "From Marked InhibitorNet", requiredParameterLabels = { 0, 1 })
	public static Object[] toResetInhibitorNet(PluginContext context, InhibitorNet net, Marking marking)
			throws Exception {
		// call main method
		AbstractResetInhibitorNet tempNet = (AbstractResetInhibitorNet) net;
		Object[] result = fromPetrinetInternal(context, tempNet, marking);

		return result;
	}

	@PluginVariant(variantLabel = "From Unmarked ResetNet", requiredParameterLabels = { 0 })
	public static Object[] toResetInhibitorNet(PluginContext context, ResetNet net) throws Exception {
		// call main method
		AbstractResetInhibitorNet tempNet = (AbstractResetInhibitorNet) net;
		Object[] result = fromPetrinetInternal(context, tempNet, null);

		return result;
	}

	@PluginVariant(variantLabel = "From Marked ResetNet", requiredParameterLabels = { 0, 1 })
	public static Object[] toResetInhibitorNet(PluginContext context, ResetNet net, Marking marking) throws Exception {
		// call main method
		AbstractResetInhibitorNet tempNet = (AbstractResetInhibitorNet) net;
		Object[] result = fromPetrinetInternal(context, tempNet, marking);

		return result;
	}

	static Marking cloneMarking(Marking marking, Map<DirectedGraphElement, DirectedGraphElement> mapping) {
		Marking newMarking = new Marking();
		if (marking != null) {
			for (Place p : marking.baseSet()) {
				Place newP = Cast.<Place>cast(mapping.get(p));
				newMarking.add(newP, marking.occurrences(p));
			}
		}
		return newMarking;
	}

	private static Object[] fromPetrinetInternal(PluginContext context, AbstractResetInhibitorNet net, Marking marking)
			throws ConnectionCannotBeObtained {
		if (marking != null) {
			// Check for connection
			context.getConnectionManager().getFirstConnection(InitialMarkingConnection.class, context, net, marking);
		}

		ResetInhibitorNetImpl newNet = new ResetInhibitorNetImpl(net.getLabel());
		Map<DirectedGraphElement, DirectedGraphElement> mapping = newNet.cloneFrom(net);

		Marking newMarking = ToResetInhibitorNet.cloneMarking(marking, mapping);

		context.addConnection(new InitialMarkingConnection(newNet, newMarking));
		context.getFutureResult(0).setLabel(net.getLabel());
		context.getFutureResult(1).setLabel("Initial Marking of " + net.getLabel());

		return new Object[] { newNet, newMarking };
	}

}
