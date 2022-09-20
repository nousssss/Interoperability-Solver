package org.processmining.modelrepair.plugins.align;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.collection.AlphanumComparator;
import org.processmining.models.connections.petrinets.PNRepResultAllRequiredParamConnection;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

@Plugin(name = "Export simple control-flow alignment as CSV", returnLabels = {}, returnTypes = {}, parameterLabels = {
		"Control-Flow Alignment", "File" }, userAccessible = true)
@UIExportPlugin(description = "Export simple control-flow alignment as CSV (.csv)", extension = "csv")
public class Uma_ExportAlignmentToCSV {

	@PluginVariant(variantLabel = "Export simple control-flow alignment as CSV (.csv)", requiredParameterLabels = { 0,
			1 })
	public void exportPetriNetToEPNMLFile(UIPluginContext context, PNRepResult alignment, File file) throws IOException {
		exportAlignmentToCSV_simple(context, alignment, file);
	}

	protected void exportAlignmentToCSV_simple(UIPluginContext context, PNRepResult alignment, File file)
			throws IOException {
		
		XLog log;
		TransEvClassMapping mapping;
		try {
			PNRepResultAllRequiredParamConnection c = context.getConnectionManager().getFirstConnection(PNRepResultAllRequiredParamConnection.class, context, alignment);
			log = c.getObjectWithRole(PNRepResultAllRequiredParamConnection.LOG);
			mapping = c.getObjectWithRole(PNRepResultAllRequiredParamConnection.TRANS2EVCLASSMAPPING);
		} catch (ConnectionCannotBeObtained e) {
			cancel(context, "No log related to alignment found.");
			return;
		}
		XLogInfo info = log.getInfo(mapping.getEventClassifier());

		boolean interrupted = false;
		
		StringBuilder sb = new StringBuilder();
		sb.append("case id;activity;move type\n");

		for (SyncReplayResult res : alignment) {

			// collect event order as determined by replayer
			ArrayList<String> alignedEvents = new ArrayList<String>();
			ArrayList<String> moveType = new ArrayList<String>();
			
			int alignmentLength = res.getNodeInstance().size();
			for (int i=0; i<alignmentLength; i++) {
				Object event = res.getNodeInstance().get(i);
				if (event instanceof Transition) {
					alignedEvents.add(((Transition) event).getLabel());
				} else if (event instanceof String) {
					alignedEvents.add((String)event);
				} else if (event instanceof XEvent) {
					alignedEvents.add(info.getEventClasses().getClassOf((XEvent)event).toString());
				} else {
					alignedEvents.add(event.toString());
				}
				
				switch (res.getStepTypes().get(i)) {
					case L: moveType.add("log"); break;
					case LMGOOD: moveType.add("sync"); break;
					case MREAL: moveType.add("model"); break;
					case MINVI: moveType.add("invisible"); break;
					case LMNOGOOD: moveType.add("LMNOGOOD"); break;
					case LMREPLACED: moveType.add("LMREPLACED"); break;
					case LMSWAPPED: moveType.add("LMSWAPPED"); break;
				}

			}

			// to preserve frequencies of the original log, create a separate copy
			// for each trace in the trace class: collect all caseIDs in the class
			SortedSet<String> caseIDs = new TreeSet<String>(new AlphanumComparator());
			
			XConceptExtension ce = XConceptExtension.instance();
			for (int index : res.getTraceIndex()) {
				String caseID = ce.extractName(log.get(index));
				if (caseID == null)
					caseID = "id_" + index;
				caseIDs.add(caseID);
			}
			
			for (String caseID : caseIDs) {
				for (int i=0; i<alignedEvents.size(); i++) {
					sb.append(caseID+";"+alignedEvents.get(i)+";"+moveType.get(i)+"\n");
				}
				
				if (context.getProgress().isCancelled()) {
					interrupted = true;
					break;
				}
			}
			if (interrupted) break;
		}

		Writer out = new OutputStreamWriter(new FileOutputStream(file));
		out.append(sb);
		out.close();
	}
	
	private static void cancel(PluginContext context, String message) {
		System.out.println("[ModelRepair/csv export]: "+message);
		context.log(message);
	}
}
