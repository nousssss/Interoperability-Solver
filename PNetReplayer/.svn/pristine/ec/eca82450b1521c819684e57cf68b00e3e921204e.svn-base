/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.visualization;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.PNMatchInstancesRepResultConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;

/**
 * @author aadrians Feb 27, 2013
 * 
 */
@Plugin(name = "Project All Alignments to Log", returnLabels = { "Visualized All Log-Model Alignments Projected to Log" }, 
returnTypes = { JComponent.class }, parameterLabels = { "All Log-Model alignment" }, userAccessible = true)
@Visualizer
public class PNMatchInstancesVis {
	
	@PluginVariant(requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, PNMatchInstancesRepResult logReplayResult) {
		System.gc();
		PetrinetGraph net = null;
		XLog log = null;

		try {
			PNMatchInstancesRepResultConnection conn = context.getConnectionManager().getFirstConnection(
					PNMatchInstancesRepResultConnection.class, context, logReplayResult);
			net = conn.getObjectWithRole(PNMatchInstancesRepResultConnection.PN);
			log = conn.getObjectWithRole(PNMatchInstancesRepResultConnection.LOG);
		} catch (Exception exc) {
			context.log("No net can be found for this log replay result");
			return null;
		}

		return new PNMatchInstancesRepResultVisPanel(net, log, logReplayResult, context.getProgress());
	}
}
