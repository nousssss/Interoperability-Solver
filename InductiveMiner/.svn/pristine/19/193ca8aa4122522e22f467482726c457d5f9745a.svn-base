package org.processmining.plugins.inductiveminer2.plugins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.directlyfollowsgraph.DirectlyFollowsGraph;
import org.processmining.plugins.inductiveminer2.helperclasses.graphs.IntGraph;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

@Plugin(name = "DfgMsd export (minimum self-distance graph)", returnLabels = {}, returnTypes = {}, parameterLabels = {
		"Minimum self-distance graph", "File" }, userAccessible = true)
@UIExportPlugin(description = "Directly follows model files", extension = "dfgmsd")
public class DfgMsdExportPlugin {
	@PluginVariant(variantLabel = "Dfg export (Directly follows graph)", requiredParameterLabels = { 0, 1 })
	public void exportDefault(UIPluginContext context, DirectlyFollowsGraph dfg, File file) throws IOException {
		export(dfg, file);
	}

	public void exportDefault(PluginContext context, DirectlyFollowsGraph dfg, File file) throws IOException {
		export(dfg, file);
	}

	public static void export(DirectlyFollowsGraph dfg, File file) throws IOException {
		BufferedWriter result = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		result.append(dfg.getNumberOfActivities() + "\n");
		for (String e : dfg.getAllActivities()) {
			result.append(e + "\n");
		}

		result.append(dfg.getStartActivities().setSize() + "\n");
		for (int activityIndex : dfg.getStartActivities()) {
			result.append(activityIndex + "x" + dfg.getStartActivities().getCardinalityOf(activityIndex) + "\n");
		}

		result.append(dfg.getEndActivities().setSize() + "\n");
		for (int activityIndex : dfg.getEndActivities()) {
			result.append(activityIndex + "x" + dfg.getEndActivities().getCardinalityOf(activityIndex) + "\n");
		}

		//dfg-edges
		{
			IntGraph g = dfg.getDirectlyFollowsGraph();
			long edges = 0;
			for (Iterator<Long> iterator = g.getEdges().iterator(); iterator.hasNext();) {
				iterator.next();
				edges++;
			}
			result.append(edges + "\n");
			for (long edge : g.getEdges()) {
				long v = g.getEdgeWeight(edge);
				if (v > 0) {
					int source = g.getEdgeSource(edge);
					int target = g.getEdgeTarget(edge);
					result.append(source + ">");
					result.append(target + "x");
					result.append(v + "\n");
				}
			}
		}

		//msd-edges
		if (dfg instanceof DfgMsd) {
			IntGraph g = ((DfgMsd) dfg).getMinimumSelfDistanceGraph();
			long edges = 0;
			for (Iterator<Long> iterator = g.getEdges().iterator(); iterator.hasNext();) {
				iterator.next();
				edges++;
			}
			result.append(edges + "\n");
			for (long edge : g.getEdges()) {
				long v = g.getEdgeWeight(edge);
				if (v > 0) {
					int source = g.getEdgeSource(edge);
					int target = g.getEdgeTarget(edge);
					result.append(source + ">");
					result.append(target + "x");
					result.append(v + "\n");
				}
			}
		}

		result.flush();
		result.close();
	}
}
