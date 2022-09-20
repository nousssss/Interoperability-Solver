/**
 * 
 */
package org.processmining.plugins.petrinet.replayresult.exporting;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.collection.MultiSet;
import org.processmining.framework.util.collection.TreeMultiSet;
import org.processmining.models.connections.petrinets.PNManifestConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.manifestreplayer.PNManifestReplayerParameter;
import org.processmining.plugins.petrinet.manifestreplayer.TransClass2PatternMap;
import org.processmining.plugins.petrinet.manifestreplayresult.Manifest;

/**
 * @author aadrians May 15, 2013
 * 
 */
@Plugin(name = "Export manifests and attached info as CSV (.csv)", returnLabels = {}, returnTypes = {}, parameterLabels = {
		"PNRepResult", "File" }, userAccessible = true)
@UIExportPlugin(description = "Export alignments and attached info as CSV (.csv)", extension = "csv")
public class ExportPNMatchInstancesRepResultDetail {
	private String DELIMITER = ",";

	@PluginVariant(variantLabel = "Export result report as CSV (.csv)", requiredParameterLabels = { 0, 1 })
	public void exportRepResult2File(UIPluginContext context, Manifest manifest, File file) throws IOException {
		System.gc();

		// utilities class
		PNManifestReplayerParameter param = null;
		PetrinetGraph net = null;
		XLog log = null;
		XEventClasses ec = null;
		Marking initMarking = null;
		try {
			PNManifestConnection conn = context.getConnectionManager().getFirstConnection(PNManifestConnection.class,
					context, manifest);

			param = (PNManifestReplayerParameter) conn.getObjectWithRole(PNManifestConnection.REPLAYPARAMETERS);
			net = conn.getObjectWithRole(PNManifestConnection.PN);
			log = conn.getObjectWithRole(PNManifestConnection.LOG);
			ec = XLogInfoFactory.createLogInfo(log, manifest.getEvClassifier()).getEventClasses();
			initMarking = param.getInitMarking();
		} catch (Exception exc) {
			context.log("No net can be found for this log replay result");
		}

		// first, identify all information in the log to be written
		StringBuilder sbHeader = new StringBuilder();

		List<String> logAttrOrder = null;
		if (log.getAttributes() != null) {
			logAttrOrder = new ArrayList<String>(log.getAttributes().keySet());
			extractAttMapInfoLabel(logAttrOrder, "LOG:", sbHeader); // attribute of log
		}

		// log-level info from manifest : none

		// trace level info
		List<String> traceInfoOrder = new ArrayList<String>(log.get(0).getAttributes().keySet());
		extractAttMapInfoLabel(traceInfoOrder, "TRACE:", sbHeader);

		// trace level info from alignment
		sbHeader.append("\"TRACEAL:IsReliable\"");
		sbHeader.append(DELIMITER);
		sbHeader.append("\"TRACEAL:RawCost\"");
		sbHeader.append(DELIMITER);
		sbHeader.append("\"TRACEAL:MoveLogFitness\"");
		sbHeader.append(DELIMITER);
		sbHeader.append("\"TRACEAL:MoveModelFitness\"");
		sbHeader.append(DELIMITER);
		sbHeader.append("\"TRACEAL:TraceFitness\"");
		sbHeader.append(DELIMITER);
		sbHeader.append("\"TRACEAL:CompTime\"");
		sbHeader.append(DELIMITER);

		// event level info
		List<String> eventInfoOrder = new ArrayList<String>(log.get(0).get(0).getAttributes().keySet());
		extractAttMapInfoLabel(eventInfoOrder, "EVT:", sbHeader);

		// event level info from alignment
		List<String> repResEvtInfoOrder = new ArrayList<String>(6);
		repResEvtInfoOrder.add("Transition");
		repResEvtInfoOrder.add("TransitionID");
		repResEvtInfoOrder.add("TransPatternStr"); // pattern represented
		repResEvtInfoOrder.add("TransPatternID"); // id of the pattern
		repResEvtInfoOrder.add("Cost");
		repResEvtInfoOrder.add("Marking");
		repResEvtInfoOrder.add("ParikhVector");
		repResEvtInfoOrder.add("ActivityInstance");
		extractAttMapInfoLabel(repResEvtInfoOrder, "EVTAL:", sbHeader);

		// write labels to file 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		bw.write(sbHeader.toString());
		sbHeader = null;
		bw.newLine();

		// logAttrOrder -- traceInfoOrder -- eventInfoOrder -- repResEvtInfoOrder

		/**
		 * WRITE CONTENT
		 */
		// prepare log info
		StringBuilder sbLog = new StringBuilder();
		String logInfoStr = null;
		if (logAttrOrder != null) {
			XAttributeMap logAttMap = log.getAttributes();
			for (String logAtt : logAttrOrder) {
				sbLog.append("\"");
				sbLog.append(logAttMap.get(logAtt).toString());
				sbLog.append("\"");
				sbLog.append(DELIMITER);
			}
			logInfoStr = sbLog.toString();
			sbLog = null;
		} else {
			logInfoStr = "";
		}

		// util
		TransClass2PatternMap mapping = param.getMapping();
		PetrinetGraphPlayOut player = new PetrinetGraphPlayOut(net, new Marking(initMarking));

		int[] casePointer = manifest.getCasePointers();
		for (int i = 0; i < casePointer.length; i++) {
			try {
				int[] caseEncoded = manifest.getManifestForCase(i);
				int pointer = 0;
				StringBuilder sbTrace = new StringBuilder();

				XTrace trace = log.get(i);

				// get info about the trace to be appended
				appendAttMapInfo(trace.getAttributes(), traceInfoOrder, sbTrace);

				// write info from manifest about the trace
				sbTrace.append(manifest.isCaseReliable(i) ? "\"true\"" : "\"false\"");
				sbTrace.append(DELIMITER);
				sbTrace.append(String.valueOf(manifest.getRawCostFitness(i)));
				sbTrace.append(DELIMITER);
				sbTrace.append(String.valueOf(manifest.getMoveLogFitness(i)));
				sbTrace.append(DELIMITER);
				sbTrace.append(String.valueOf(manifest.getMoveModelFitness(i)));
				sbTrace.append(DELIMITER);
				sbTrace.append(String.valueOf(manifest.getTraceFitness(i)));
				sbTrace.append(DELIMITER);
				sbTrace.append(String.valueOf(manifest.getComputationTime(i)));
				sbTrace.append(DELIMITER);
				String traceInfoStr = sbTrace.toString();
				sbTrace = null;

				// updated data
				player.init(new Marking(initMarking));

				MultiSet<XEventClass> parikhVector = new TreeMultiSet<XEventClass>();
				for (XEvent evt : trace) {
					parikhVector.add(ec.getClassOf(evt));
				}
				TIntIntMap instanceEvtMap = new TIntIntHashMap(10, 0.5f, -1, -1);

				Iterator<XEvent> it = trace.iterator();

				while (pointer < caseEncoded.length) {
					bw.write(logInfoStr);
					bw.write(traceInfoStr);

					if (caseEncoded[pointer] == Manifest.MOVELOG) {
						// write all about the event
						XEvent event = it.next();
						writeAttMapInfo(event.getAttributes(), eventInfoOrder, bw);

						writeMoveModelInfo(null, null, null, param.getMoveLogCost(ec.getClassOf(event)),
								player.getCurrentMarking(), parikhVector, null, bw);

						// update the parikh vector
						parikhVector.remove(ec.getClassOf(event));

						pointer++;
					} else if (caseEncoded[pointer] == Manifest.MOVEMODEL) {
						writeAttMapInfo(null, eventInfoOrder, bw);

						Transition tMoveModel = manifest.getTransitionOf(caseEncoded[pointer + 1]);
						writeMoveModelInfo(tMoveModel, null, null,
								param.getMoveModelCost(manifest.getTransClassOf(tMoveModel)),
								player.getCurrentMarking(), parikhVector, null, bw);

						player.fire(tMoveModel);
						pointer += 2;
					} else if (caseEncoded[pointer] == Manifest.MOVESYNC) {
						// write all about the event					
						XEvent event = it.next();
						writeAttMapInfo(event.getAttributes(), eventInfoOrder, bw);

						// get manifest ID
						int manifestID = caseEncoded[pointer + 1];
						int patternID = manifest.getPatternIDOfManifest(manifestID);

						Transition t = manifest.getTransitionOf(manifest.getEncTransOfManifest(manifestID));

						// write things about the transitions
						writeMoveModelInfo(t, mapping.getPatternStr(patternID), patternID,
								param.getMoveSyncCost(mapping.getTransClassOf(t)), player.getCurrentMarking(),
								parikhVector, manifestID, bw);

						// update the parikh vector and state of model
						parikhVector.remove(ec.getClassOf(event));

						// only fire if this is the last in the pattern
						int val = instanceEvtMap.get(manifestID);
						if (val == -1) { // this is the first event
							if (mapping.getPatternIDNumElmts(patternID) == 1) {
								// no need to update instanceEvtMap
								player.fire(t);
							} else {
								instanceEvtMap.put(manifestID, 1);
							}
						} else {
							// some events have been identified
							val++;
							if (mapping.getPatternIDNumElmts(patternID) == val) {
								// this is the last event, update
								player.fire(t);
							} else {
								instanceEvtMap.put(manifestID, val);
							}

						}
						pointer += 2;
					}
					bw.newLine();
				}
			} catch (IllegalTransitionException tex) {
				System.out.println("Illegal transition exception");
				tex.printStackTrace();
			}
		}
		bw.close();
	}

	private void writeMoveModelInfo(Transition trans, String patternString, Integer patternID, Integer cost, Marking m,
			MultiSet<XEventClass> parikhVector, Integer instanceID, BufferedWriter bw) throws IOException {
		if (trans != null) {
			bw.write("\"");
			bw.write(trans.getLabel());
			bw.write("\"");
			bw.write(DELIMITER);
			bw.write("\"");
			bw.write(trans.getId().toString());
			bw.write("\"");
			bw.write(DELIMITER);
			bw.write("\"");
			bw.write(String.valueOf(patternString));
			bw.write("\"");
			bw.write(DELIMITER);
			bw.write("\"");
			bw.write(String.valueOf(patternID));
			bw.write("\"");
			bw.write(DELIMITER);
			bw.write("\"");
			bw.write(String.valueOf(cost));
			bw.write("\"");
			bw.write(DELIMITER);
			bw.write("\"");
			bw.write(m.toString());
			bw.write("\"");
			bw.write(DELIMITER);
			bw.write("\"");
			if (parikhVector != null) {
				bw.write(parikhVector.toString());
			} else {
				bw.write("NULL");
			}
			bw.write("\"");
			bw.write(DELIMITER);
			bw.write("\"");
			bw.write(String.valueOf(instanceID));
			bw.write("\"");
			bw.write(DELIMITER);
		} else {
			bw.write("\"");
			bw.write(String.valueOf(trans));
			bw.write("\"");
			bw.write(DELIMITER);
			bw.write("\"");
			bw.write(String.valueOf(trans));
			bw.write("\"");
			bw.write(DELIMITER);
			bw.write("\"");
			bw.write(String.valueOf(patternString));
			bw.write("\"");
			bw.write(DELIMITER);
			bw.write("\"");
			bw.write(String.valueOf(patternID));
			bw.write("\"");
			bw.write(DELIMITER);
			bw.write("\"");
			bw.write(String.valueOf(cost));
			bw.write("\"");
			bw.write(DELIMITER);
			bw.write("\"");
			bw.write(m.toString());
			bw.write("\"");
			bw.write(DELIMITER);
			bw.write("\"");
			if (parikhVector != null) {
				bw.write(parikhVector.toString());
			} else {
				bw.write("NULL");
			}
			bw.write("\"");
			bw.write(DELIMITER);
			bw.write("\"");
			bw.write(String.valueOf(instanceID));
			bw.write("\"");
			bw.write(DELIMITER);
		}
	}

	private void writeAttMapInfo(XAttributeMap attMap, Collection<String> attMapOrder, BufferedWriter bw)
			throws IOException {
		if (attMap != null) {
			for (String attName : attMapOrder) {
				bw.write("\"");
				XAttribute name = attMap.get(attName);
				if (name != null){
					bw.write(name.toString());
				} else {
					bw.write("NULL");
				}
				bw.write("\"");
				bw.write(DELIMITER);
			}
		} else {
			for (int i = 0; i < attMapOrder.size(); i++) {
				bw.write("\"null\"");
				bw.write(DELIMITER);
			}
		}
	}

	private void extractAttMapInfoLabel(Collection<String> keySet, String additionalHeader, StringBuilder sb) {
		for (String key : keySet) {
			sb.append("\"");
			if (additionalHeader != null) {
				sb.append(additionalHeader);
			}
			sb.append(key);
			sb.append("\"");
			sb.append(DELIMITER);
		}
	}

	private void appendAttMapInfo(XAttributeMap attMap, Collection<String> attMapOrder, StringBuilder sb) {
		if (attMap != null) {
			for (String info : attMapOrder) {
				sb.append("\"");
				XAttribute name = attMap.get(info);
				if (name != null){
					sb.append(name.toString());
				} else {
					sb.append("NULL");
				}
				sb.append("\"");
				sb.append(DELIMITER);
			}
		} else {
			for (int i = 0; i < attMapOrder.size(); i++) {
				sb.append("\"");
				sb.append("\"");
				sb.append(DELIMITER);
			}
		}
	}
}
