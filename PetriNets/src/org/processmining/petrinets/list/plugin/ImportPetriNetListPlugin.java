package org.processmining.petrinets.list.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.io.IOUtils;
import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.petrinets.list.PetriNetList;
import org.processmining.petrinets.list.factory.PetriNetListFactory;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.importing.PnmlImportUtils;

@Plugin(name = "Import Petri Net List from .pnlist File", parameterLabels = { "Filename" }, returnLabels = {
		"Petri Net List" }, returnTypes = { PetriNetList.class })
@UIImportPlugin(description = "Petri Net List Files", extensions = { "pnlist" })
public class ImportPetriNetListPlugin extends AbstractImportPlugin {

	private static final String PNML_START_TAG = "<pnml>", PNML_END_TAG = "</pnml>";

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		PetriNetList list = PetriNetListFactory.createPetriNetList();
		String contents = IOUtils.toString(input);
		PnmlImportUtils utils = new PnmlImportUtils();
		int start = contents.indexOf(PNML_START_TAG, 0);
		while (start >= 0) {
			int end = contents.indexOf(PNML_END_TAG, start);
			String pnmlStr = contents.substring(start, end + PNML_END_TAG.length());
			File f = new File("f" + start);
			FileOutputStream outputStream = new FileOutputStream(f);
			IOUtils.write(pnmlStr, outputStream);
			outputStream.close();
			FileInputStream inputStream = new FileInputStream(f);
			Pnml pnml = utils.importPnmlFromStream(context, inputStream, f.getName(), f.length());
			if (pnml == null) {
				return null;
			}
			inputStream.close();
			/*
			 * Initialize necessary objects.
			 */
			Petrinet net = PetrinetFactory.newPetrinet(pnml.getLabel());
			Marking marking = new Marking();
			Collection<Marking> finalMarkings = new HashSet<Marking>();
			GraphLayoutConnection layout = new GraphLayoutConnection(net);
			/*
			 * Copy the imported data into these objects.
			 */
			pnml.convertToNet(net, marking, finalMarkings, layout);
			/*
			 * Create necessary connections.
			 */
			context.addConnection(new InitialMarkingConnection(net, marking));
			for (Marking finalMarking : finalMarkings) {
				context.addConnection(new FinalMarkingConnection(net, finalMarking));
			}
			context.addConnection(layout);
			
			list.add(net);
			start = contents.indexOf(PNML_START_TAG, start + 1);
			try {
				f.delete();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

		}
		return list;
	}

	public PetriNetList apply(PluginContext context, File file) throws FileNotFoundException, Exception {
		return (PetriNetList) importFromStream(context, new FileInputStream(file), file.getName(), file.length());
	}

}
