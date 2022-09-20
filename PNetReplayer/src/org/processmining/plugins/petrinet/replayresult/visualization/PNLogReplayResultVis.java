/**
 * 
 */
package org.processmining.plugins.petrinet.replayresult.visualization;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.PNRepResultAllRequiredParamConnection;
import org.processmining.models.connections.petrinets.PNRepResultConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

/**
 * 
 * @author aadrians
 * @mail a.adriansyah@tue.nl
 * @since Dec 30, 2010
 */
@Plugin(name = "Project Alignment to Log", level = PluginLevel.PeerReviewed, returnLabels = { "Visualized Log-Model Alignment Projection to Log" }, returnTypes = { JComponent.class }, parameterLabels = { "Log-Model alignment" }, userAccessible = true)
@Visualizer
public class PNLogReplayResultVis {
	@PluginVariant(requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, PNRepResult logReplayResult) {
		//System.gc();
		PetrinetGraph net = null;
		XLog log = null;
		try {
			PNRepResultAllRequiredParamConnection conn = context.getConnectionManager().getFirstConnection(
					PNRepResultAllRequiredParamConnection.class, context, logReplayResult);

			net = conn.getObjectWithRole(PNRepResultConnection.PN);
			log = conn.getObjectWithRole(PNRepResultConnection.LOG);
		} catch (Exception exc) {
			context.log("No net can be found for this log replay result");
			return null;
		}

		return new PNLogReplayResultVisPanel(log, logReplayResult, context.getProgress());
	}
}
