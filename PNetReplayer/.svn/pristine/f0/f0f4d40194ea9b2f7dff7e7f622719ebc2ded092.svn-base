/**
 * 
 */
package org.processmining.plugins.petrinet.initmarkingprovider;

import java.util.Collection;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.finalmarkingprovider.MarkingEditorPanel;

/**
 * @author aadrians
 * Jun 9, 2012
 *
 */
@ConnectionObjectFactory
@Plugin(name = "Create Initial Marking", level = PluginLevel.PeerReviewed, returnLabels = { "Connection Initial marking", "Initial Marking" }, returnTypes = { InitialMarkingConnection.class, Marking.class }, parameterLabels = {
		"Petri net" }, help = "Create an initial marking for a Petri net.", mostSignificantResult = 2, userAccessible = true)
public class InitMarkingFactory {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Arya Adriansyah", email = "a.adriansyah@tue.nl", pack = "PNetReplayer")
	@PluginVariant(variantLabel = "Use GUI Editor", requiredParameterLabels = { 0 })
	public Object[] constructInitMarking(UIPluginContext context, PetrinetGraph net){
		MarkingEditorPanel editor = new MarkingEditorPanel("Initial Marking");
		Marking initMarking = editor.getMarking(context, net);
		
		if (initMarking == null){
			context.getFutureResult(0).cancel(true);
			return null;
		}
		
		// return the first final marking
		return constructInitMarking(context, net, initMarking );
	}
	
	public Object[] constructInitMarking(PluginContext context, PetrinetGraph net, Marking initMarking){
		if (initMarking == null){
			throw new IllegalArgumentException("No init marking is provided");
		}
		
		Collection<Place> colPlaces = net.getPlaces();
		for (Place p : initMarking){
			if (!colPlaces.contains(p)){
				throw new IllegalArgumentException("Initial marking contains places outside of the net");
			}
		}
		InitialMarkingConnection conn = new InitialMarkingConnection(net, initMarking);
		context.getFutureResult(0).setLabel("Connection init marking of " + net.getLabel());
		context.getFutureResult(1).setLabel("Init marking of " + net.getLabel());
		context.addConnection(conn);
		return new Object[] { conn, initMarking };
	}
}
