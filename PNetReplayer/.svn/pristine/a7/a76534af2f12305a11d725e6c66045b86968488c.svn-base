/**
 * 
 */
package org.processmining.plugins.petrinet.manifestreplayresult.visualization;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.petrinet.manifestreplayer.conversion.Manifest2PNRepResult;
import org.processmining.plugins.petrinet.manifestreplayresult.Manifest;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.visualization.PNLogReplayResultVisPanel;

/**
 * @author aadrians
 * Jun 1, 2012
 *
 */
@Plugin(name = "Project Manifest to Log", level = PluginLevel.PeerReviewed, returnLabels = { "Projected Manifest-based-conformance onto Log" }, returnTypes = { JComponent.class }, parameterLabels = { "Pattern Manifestation" }, userAccessible = true)
@Visualizer
public class ManifestLogProjectionVisualization {
	@PluginVariant(requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, Manifest manifest) {
		System.gc();
		
		// create pnRep result from manifest
		PNRepResult pnRepResult = Manifest2PNRepResult.convert(manifest);
		
		// visualize it
		return new PNLogReplayResultVisPanel(manifest.getNet(), manifest.getLog(), pnRepResult, context.getProgress());
	}

}
