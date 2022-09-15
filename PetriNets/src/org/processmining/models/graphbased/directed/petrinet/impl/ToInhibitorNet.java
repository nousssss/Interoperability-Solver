/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/

package org.processmining.models.graphbased.directed.petrinet.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.ResetArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * This class convert a net to inhibitor net
 * 
 * @author arya, structure are added by Boudewijn V.D.
 * @email arya.adriansyah@gmail.com
 * @version Dec 15, 2008
 */

@Plugin(name = "Convert to Inhibitor-net", returnLabels = { "Inhibitor-net", "Marking" }, returnTypes = {
		InhibitorNet.class, Marking.class }, parameterLabels = { "Net", "Marking" }, help = "Converts nets to Inhibitor-nets.", userAccessible = true)
public class ToInhibitorNet {
	@PluginVariant(variantLabel = "From Marked Petrinet", requiredParameterLabels = { 0, 1 })
	public static Object[] fromPetrinet(PluginContext context, Petrinet net, Marking marking)
			throws ConnectionCannotBeObtained {
		return fromPetrinetInternal(context, net, marking);
	}

	@PluginVariant(variantLabel = "From Unmarked Petrinet", requiredParameterLabels = { 0 })
	public static Object[] fromPetrinet(PluginContext context, Petrinet net) throws ConnectionCannotBeObtained {
		return fromPetrinetInternal(context, net, null);
	}

	/**
	 * Convert ResetInhibitor-net to Inhibitor-net without marking as an input,
	 * produce GUI
	 * 
	 * @param context
	 * @param net
	 * @return
	 * @throws Exception
	 */
	@PluginVariant(variantLabel = "From Unmarked ResetInhibitorNet", requiredParameterLabels = { 0 })
	public static Object[] toInhibitorNet(PluginContext context, ResetInhibitorNet net) throws Exception {
		// call main method
		AbstractResetInhibitorNet tempNet = (AbstractResetInhibitorNet) net;
		Object[] result = fromResetInhibitorNetPrivate(context, tempNet, null);

		return result;
	}

	/**
	 * Convert ResetInhibitor-net to Inhibitor-net with marking as an input,
	 * produce GUI
	 * 
	 * @param context
	 * @param net
	 * @return
	 * @throws Exception
	 */
	@PluginVariant(variantLabel = "From Marked ResetInhibitorNet", requiredParameterLabels = { 0, 1 })
	public static Object[] toInhibitorNet(PluginContext context, ResetInhibitorNet net, Marking marking)
			throws Exception {
		// call main method
		AbstractResetInhibitorNet tempNet = (AbstractResetInhibitorNet) net;
		Object[] result = fromResetInhibitorNetPrivate(context, tempNet, marking);

		return result;
	}

	/**
	 * Convert ResetInhibitor-net to Inhibitor-net without marking as an input,
	 * produce GUI
	 * 
	 * @param context
	 * @param net
	 * @return
	 * @throws Exception
	 */
	@PluginVariant(variantLabel = "From Unmarked ResetNet", requiredParameterLabels = { 0 })
	public static Object[] toInhibitorNet(PluginContext context, ResetNet net) throws Exception {
		// call main method
		AbstractResetInhibitorNet tempNet = (AbstractResetInhibitorNet) net;
		Object[] result = fromResetInhibitorNetPrivate(context, tempNet, null);

		return result;
	}

	/**
	 * Convert ResetInhibitor-net to Inhibitor-net with marking as an input,
	 * produce GUI
	 * 
	 * @param context
	 * @param net
	 * @return
	 * @throws Exception
	 */
	@PluginVariant(variantLabel = "From Marked ResetNet", requiredParameterLabels = { 0, 1 })
	public static Object[] toInhibitorNet(PluginContext context, ResetNet net, Marking marking) throws Exception {
		// call main method
		AbstractResetInhibitorNet tempNet = (AbstractResetInhibitorNet) net;
		Object[] result = fromResetInhibitorNetPrivate(context, tempNet, marking);

		return result;
	}

	/**
	 * Main method to convert Reset/ResetInhibitor-net to Inhibitor-net
	 * 
	 * @param net
	 *            Reset-net which is going to be transformed to Inhibitor-net
	 * @param state
	 *            Marking of the original Reset-net. Provide null if there is no
	 *            marking as an input
	 * @return array of Object. Object[0] is an Inhibitor-net, while Object[1]
	 *         is a Marking for the Inhibitor-net
	 * @throws Exception
	 */
	private static Object[] fromResetInhibitorNetPrivate(PluginContext context, AbstractResetInhibitorNet tempNet,
			Marking state) throws Exception {
		InhibitorNetImpl newNet = new InhibitorNetImpl(tempNet.getLabel());

		Map<DirectedGraphElement, DirectedGraphElement> mapping = newNet.cloneFrom(tempNet, true, true, true, false,
				true);

		Marking newState = ToResetInhibitorNet.cloneMarking(state, mapping);

		// transform reset arc
		// for each transition which is connected to a reset net
		checkTransition: for (Transition t : tempNet.getTransitions()) {
			Set<Place> connectedPlaces = new HashSet<Place>();
			// check if there is a reset arcs connected to this transition
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : tempNet.getInEdges(t)) {
				if (edge instanceof ResetArc) {
					connectedPlaces.add((Place) edge.getSource());
				}
			}

			if (connectedPlaces.isEmpty()) {
				continue checkTransition;
			} else {
				// there is a reset arc connected to this transition
				// create place x, pt
				Place pt = newNet.addPlace(t.getLabel() + "-pt");
				Place x = newNet.addPlace(t.getLabel() + "-x");

				// create transition te
				Transition te = newNet.addTransition(t.getLabel() + "-te");

				// for out edges in t, move out edges to te
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edges : tempNet.getOutEdges(t)) {
					newNet.addArc(te, (Place) mapping.get(edges.getTarget()));
					newNet.removeArc((Transition) mapping.get(t), (Place) mapping.get(edges.getTarget()));
				}

				// add connection in x and pt
				newNet.addArc(te, x);
				newNet.addArc(x, (Transition) mapping.get(t));
				newNet.addArc((Transition) mapping.get(t), pt);
				newNet.addArc(pt, te);

				// add marking to x
				newState.add(x, 1);

				// for each reset arc, create tc and connect an inhibitor arc
				for (Place placeToBeReset : connectedPlaces) {
					newNet.removeResetArc((Place) mapping.get(placeToBeReset), (Transition) mapping.get(t));

					// add transition tc
					Transition tc = newNet.addTransition(t.getLabel() + "-tc");
					newNet.addArc((Place) mapping.get(placeToBeReset), tc);
					newNet.addArc(pt, tc);
					newNet.addArc(tc, pt);

					// add inhibitor
					newNet.addInhibitorArc((Place) mapping.get(placeToBeReset), te);
				}
			}
		}

		context.addConnection(new InitialMarkingConnection(newNet, newState));
		context.getFutureResult(0).setLabel("Inhibitor version of " + tempNet.getLabel());
		context.getFutureResult(1).setLabel("Initial Marking of I.V. of" + tempNet.getLabel());

		// return result
		return new Object[] { newNet, newState };
	}

	private static Object[] fromPetrinetInternal(PluginContext context, Petrinet net, Marking marking)
			throws ConnectionCannotBeObtained {
		if (marking != null) {
			// Check for connection
			context.getConnectionManager().getFirstConnection(InitialMarkingConnection.class, context, net, marking);
		}

		InhibitorNetImpl newNet = new InhibitorNetImpl(net.getLabel());
		Map<DirectedGraphElement, DirectedGraphElement> mapping = newNet.cloneFrom(net);

		Marking newMarking = ToResetInhibitorNet.cloneMarking(marking, mapping);

		context.addConnection(new InitialMarkingConnection(newNet, newMarking));
		context.getFutureResult(0).setLabel(net.getLabel());
		context.getFutureResult(1).setLabel("Initial Marking of " + net.getLabel());

		return new Object[] { newNet, newMarking };
	}

}
