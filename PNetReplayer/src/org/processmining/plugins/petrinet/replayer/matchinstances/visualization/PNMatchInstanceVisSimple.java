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
 * @author aadrians
 * Mar 1, 2013
 *
 */

@Plugin(name = "Default Simple Visualization", returnLabels = { "Visualized All Log-Model Alignments Projected to Log" }, returnTypes = { JComponent.class }, parameterLabels = { "All Log-Model alignment" }, userAccessible = true)
@Visualizer
public class PNMatchInstanceVisSimple {
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

		return new PNMatchInstancesRepResultSimpleVisPanel(net, log, logReplayResult, context.getProgress());
	}
}