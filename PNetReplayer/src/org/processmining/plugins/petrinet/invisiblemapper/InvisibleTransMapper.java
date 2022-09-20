/**
 * 
 */
package org.processmining.plugins.petrinet.invisiblemapper;

import java.util.Set;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
*
* @author aadrians
* @mail a.adriansyah@tue.nl
* @since Dec 31, 2010
*/
@Plugin(name = "Configure Visibility of Transitions", level = PluginLevel.PeerReviewed, returnLabels = { }, 
		returnTypes = { }, 
		parameterLabels = { "Petri net" }, 
		help = "Configure visibility of transitions", 
		userAccessible = true, mostSignificantResult=-1)
public class InvisibleTransMapper {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Arya Adriansyah", email = "a.adriansyah@tue.nl", pack="PNetReplayer")
	@PluginVariant(variantLabel = "Configure visibility of transitions", requiredParameterLabels = { 0 })
	public void mapInvisibleTransitions(final UIPluginContext context, PetrinetGraph net) throws Exception {
		mapInvisible(context, net);
	}
	
	@PluginVariant(variantLabel = "Configure visibility of transitions", requiredParameterLabels = { 0 })
	public void mapInvisibleSpecific(final UIPluginContext context, Petrinet net) throws Exception {
		mapInvisible(context, net);
	}

	@PluginVariant(variantLabel = "Configure visibility of transitions", requiredParameterLabels = { 0 })
	public void mapInvisibleSpecific(final UIPluginContext context, InhibitorNet net) throws Exception {
		mapInvisible(context, net);
	}

	@PluginVariant(variantLabel = "Configure visibility of transitions", requiredParameterLabels = { 0 })
	public void mapInvisibleSpecific(final UIPluginContext context, ResetNet net) throws Exception {
		mapInvisible(context, net);
	}

	@PluginVariant(variantLabel = "Configure visibility of transitions", requiredParameterLabels = { 0 })
	public void mapInvisibleSpecific(final UIPluginContext context, ResetInhibitorNet net) throws Exception {
		mapInvisible(context, net);
	}

	// method to map invisible transitions 
	private void mapInvisible(UIPluginContext context, PetrinetGraph net) {
		InvisibleTransMapperPanel mapPanel = new InvisibleTransMapperPanel(net);
		InteractionResult iRes = context.showConfiguration("Transitions visibility", mapPanel);

		if (iRes.equals(InteractionResult.CONTINUE)){
			// change mapping
			Set<Transition> inviTransition = mapPanel.getInviTransitions();
			
			for (Transition t : net.getTransitions()){
				t.setInvisible(inviTransition.contains(t));
			}
		}
	}
}
