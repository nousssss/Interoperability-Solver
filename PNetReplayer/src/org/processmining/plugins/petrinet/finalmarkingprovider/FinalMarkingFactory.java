/**
 * 
 */
package org.processmining.plugins.petrinet.finalmarkingprovider;

import java.util.Collection;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * @author aadrians Nov 24, 2011
 * 
 */
@ConnectionObjectFactory
@Plugin(name = "Create Final Marking", level = PluginLevel.PeerReviewed, parameterLabels = { "Petrinet" }, returnLabels = { "Final Marking Connection",
		"Final Marking" }, returnTypes = { FinalMarkingConnection.class, Marking.class }, mostSignificantResult = 2, userAccessible = true)
public class FinalMarkingFactory {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Arya Adriansyah", email = "a.adriansyah@tue.nl", pack = "PNetReplayer")
	@PluginVariant(variantLabel = "Petrinet", requiredParameterLabels = { 0 })
	public Object[] constructFinalMarking(UIPluginContext context, PetrinetGraph net) {
		MarkingEditorPanel editor = new MarkingEditorPanel("Final Marking");
		Marking finalMarking = editor.getMarking(context, net);

		if (finalMarking == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		}

		// return the first final marking
		return constructFinalMarking(context, net, finalMarking);
	}

	public Object[] constructFinalMarking(PluginContext context, PetrinetGraph net, Marking finalMarking) {
		if (finalMarking == null) {
			throw new IllegalArgumentException("No final marking is provided");
		}

		Collection<Place> colPlaces = net.getPlaces();
		for (Place p : finalMarking) {
			if (!colPlaces.contains(p)) {
				throw new IllegalArgumentException("Final marking contains places outside of the net");
			}
		}
		FinalMarkingConnection conn = new FinalMarkingConnection(net, finalMarking);
		context.getFutureResult(0).setLabel("Connection final marking of " + net.getLabel());
		context.getFutureResult(1).setLabel("Final marking of " + net.getLabel());
		context.addConnection(conn);
		return new Object[] { conn , finalMarking };
	}
}
