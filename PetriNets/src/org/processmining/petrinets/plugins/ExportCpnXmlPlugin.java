package org.processmining.petrinets.plugins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.petrinets.algorithms.ExportCpnXmlAlgorithm;
import org.processmining.plugins.utils.ProvidedObjectHelper;

@Plugin(name = "CPNXML export (Petri net)", level = PluginLevel.PeerReviewed, returnLabels = {}, returnTypes = {}, parameterLabels = { "Petri net", "File" }, userAccessible = true)
@UIExportPlugin(description = "CPNXML files", extension = "cpn")
public class ExportCpnXmlPlugin extends ExportCpnXmlAlgorithm {

	@PluginVariant(variantLabel = "PNML export (Petri net)", requiredParameterLabels = { 0, 1 })
	public void export(PluginContext context, Petrinet net, File file) throws IOException {
		Marking marking;
		try {
			marking = context.tryToFindOrConstructFirstObject(Marking.class, InitialMarkingConnection.class,
					InitialMarkingConnection.MARKING, net);
		} catch (ConnectionCannotBeObtained e) {
			// use empty marking\
			marking = new Marking();
		}

		GraphLayoutConnection layout;
		try {
			layout = context.getConnectionManager().getFirstConnection(GraphLayoutConnection.class, context, net);
		} catch (ConnectionCannotBeObtained e) {
			layout = new GraphLayoutConnection(net);
		}
		String name = ProvidedObjectHelper.getProvidedObjectLabel(context, net);
		
		String text = apply(net, layout, marking, name);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		bw.write(text);
		bw.close();	
	}

}
