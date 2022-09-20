/**
 * 
 */
package org.processmining.plugins.petrinet.replayresult.exporting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.processmining.models.connections.petrinets.PNRepResultAllRequiredParamConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * @author aadrians May 12, 2013
 * 
 */
@Plugin(name = "Export alignments and attached info as CSV (.csv)", returnLabels = {}, returnTypes = {}, parameterLabels = {
		"PNRepResult", "File" }, userAccessible = true)
@UIExportPlugin(description = "Export alignments and attached info as CSV (.csv)", extension = "csv")
public class ExportPNRepResultDetail {
	private String DELIMITER = ",";

	@PluginVariant(variantLabel = "Export result report as CSV (.csv)", requiredParameterLabels = { 0, 1 })
	public void exportRepResult2File(UIPluginContext context, PNRepResult repResult, File file) throws IOException {
		System.gc();

		// utilities class
		PetrinetGraph net = null;
		XLog log = null;
		TransEvClassMapping mapping = null;
		Marking initMarking = null;
		IPNReplayParameter parameters = null;
		XEventClasses eventClasses = null;
		Map<XEventClass, Integer> moveLogCost = null;
		Map<Transition, Integer> moveModelCost = null;
		Map<Transition, Integer> moveSyncCost = null;

		try {
			PNRepResultAllRequiredParamConnection conn = context.getConnectionManager().getFirstConnection(
					PNRepResultAllRequiredParamConnection.class, context, repResult);

			net = conn.getObjectWithRole(PNRepResultAllRequiredParamConnection.PN);
			log = conn.getObjectWithRole(PNRepResultAllRequiredParamConnection.LOG);
			mapping = conn.getObjectWithRole(PNRepResultAllRequiredParamConnection.TRANS2EVCLASSMAPPING);
			eventClasses = XLogInfoFactory.createLogInfo(log, mapping.getEventClassifier()).getEventClasses();
			parameters = conn.getObjectWithRole(PNRepResultAllRequiredParamConnection.REPLAYPARAMETERS);

			// now, derive cost
			if (parameters instanceof CostBasedCompleteParam) {
				CostBasedCompleteParam par = (CostBasedCompleteParam) parameters;
				moveLogCost = par.getMapEvClass2Cost();
				moveModelCost = par.getMapTrans2Cost();
				moveSyncCost = par.getMapSync2Cost();
			}

			InitialMarkingConnection iMarkConn = context.getConnectionManager().getFirstConnection(
					InitialMarkingConnection.class, context, net);
			initMarking = iMarkConn.getObjectWithRole(InitialMarkingConnection.MARKING);

		} catch (Exception exc) {
			context.log("No net can be found for this log replay result");
		}

		// first, identify all information in the log to be written
		StringBuilder sbHeader = new StringBuilder();
		List<String> logAttrOrder = new ArrayList<String>(log.getAttributes().keySet());
		extractAttMapInfoLabel(logAttrOrder, "LOG:", sbHeader); // attribute of log

		// log-level info from alignment
		List<String> repResLogInfoOrder;
		Map<String, Object> repResultInfo = repResult.getInfo();
		if (repResultInfo != null) {
			repResLogInfoOrder = new ArrayList<String>(repResultInfo.keySet());
		} else {
			repResLogInfoOrder = new ArrayList<String>(0);
		}
		extractAttMapInfoLabel(repResLogInfoOrder, "LOG:", sbHeader);

		// trace level info
		List<String> traceInfoOrder = new ArrayList<String>(log.get(0).getAttributes().keySet());
		extractAttMapInfoLabel(traceInfoOrder, "TRACE:", sbHeader);

		// trace level info from alignment 
		List<String> repResTraceInfoOrder;
		Map<String, Double> repResInfo = repResult.iterator().next().getInfo();
		if (repResInfo != null) {
			repResTraceInfoOrder = new ArrayList<String>(repResInfo.keySet());
		} else {
			repResTraceInfoOrder = new ArrayList<String>(0);
		}
		extractAttMapInfoLabel(repResTraceInfoOrder, "TRACEAL:", sbHeader);

		// event level info
		List<String> eventInfoOrder = new ArrayList<String>(log.get(0).get(0).getAttributes().keySet());
		extractAttMapInfoLabel(eventInfoOrder, "EVT:", sbHeader);

		// event level info from alignment
		List<String> repResEvtInfoOrder = new ArrayList<String>(6);
		repResEvtInfoOrder.add("Transition");
		repResEvtInfoOrder.add("TransitionID");
		repResEvtInfoOrder.add("TransEvClass");
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

		// logAttrOrder -- repResLogInfoOrder -- traceInfoOrder -- repResTraceInfoOrder -- eventInfoOrder -- repResEvtInfoOrder

		/**
		 * WRITE CONTENT
		 */
		// prepare log info
		StringBuilder sbLog = new StringBuilder();
		XAttributeMap logAttMap = log.getAttributes();
		for (String logAtt : logAttrOrder) {
			sbLog.append("\"");
			sbLog.append(logAttMap.get(logAtt).toString());
			sbLog.append("\"");
			sbLog.append(DELIMITER);
		}
		for (String logInfoAtt : repResLogInfoOrder) {
			sbLog.append("\"");
			sbLog.append(String.valueOf(repResultInfo.get(logInfoAtt)));
			sbLog.append("\"");
			sbLog.append(DELIMITER);
		}
		String logInfoStr = sbLog.toString();
		sbLog = null;

		// write all things in order
		// log info, log info from alignment, trace info, trace info from alignment, event info, event info from alignment

		PetrinetGraphPlayOut player = new PetrinetGraphPlayOut(net, new Marking(initMarking));

		for (SyncReplayResult repRes : repResult) {
			Map<String, Double> info = repRes.getInfo();
			StringBuilder repResInfoBuilder = new StringBuilder();
			for (String key : repResTraceInfoOrder) {
				repResInfoBuilder.append("\"");
				repResInfoBuilder.append(info.get(key));
				repResInfoBuilder.append("\"");
				repResInfoBuilder.append(DELIMITER);
			}

			for (Integer ti : repRes.getTraceIndex()) {
				try {
					StringBuilder sbTrace = new StringBuilder();
					// get index
					XTrace trace = log.get(ti);

					// get info about the trace to be appended
					appendAttMapInfo(trace.getAttributes(), traceInfoOrder, sbTrace);

					// get info about trace from alignment
					sbTrace.append(repResInfoBuilder.toString());
					String traceInfoStr = sbTrace.toString();

					// get info about each movement
					Iterator<Object> niIt = repRes.getNodeInstance().iterator();
					List<StepTypes> st = repRes.getStepTypes();
					Iterator<XEvent> traceIt = trace.iterator();

					// updated data
					player.init(new Marking(initMarking));

					MultiSet<XEventClass> parikhVector = new TreeMultiSet<XEventClass>();
					for (XEvent evt : trace) {
						parikhVector.add(eventClasses.getClassOf(evt));
					}

					for (StepTypes stelmt : st) {
						bw.write(logInfoStr);
						bw.write(traceInfoStr);
						switch (stelmt) {
							case L :
								// write all about the event
								XEvent event = traceIt.next();
								writeAttMapInfo(event.getAttributes(), eventInfoOrder, bw);

								// write things about transitions
								niIt.next();
								writeMoveModelInfo(null, null, moveLogCost.get(eventClasses.getClassOf(event)),
										player.getCurrentMarking(), parikhVector, null, bw);

								// update the parikh vector
								parikhVector.remove(eventClasses.getClassOf(event));
								break;
							case LMGOOD :
								// write all about the event
								writeAttMapInfo(traceIt.next().getAttributes(), eventInfoOrder, bw);

								// write things about the transitions
								Transition t = (Transition) niIt.next();
								writeMoveModelInfo(t, mapping.get(t), moveSyncCost.get(t), player.getCurrentMarking(),
										parikhVector, null, bw);

								// update the parikh vector and state of model
								parikhVector.remove(mapping.get(t));
								player.fire(t);
								break;
							case MINVI :
							case MREAL :
								// write all about the event
								writeAttMapInfo(null, eventInfoOrder, bw);

								// write things about the transitions
								Transition tMoveModel = (Transition) niIt.next();
								XEventClass evClass = mapping.get(tMoveModel);
								writeMoveModelInfo(tMoveModel, mapping.getDummyEventClass().equals(evClass) ? null
										: evClass, moveModelCost.get(tMoveModel), player.getCurrentMarking(),
										parikhVector, null, bw);

								// update the state of model
								player.fire(tMoveModel);
								break;
							default :
								throw (new IllegalArgumentException(
										"only movements of type move model, move logs, and move synchronous are allowed"));
						}
						bw.newLine();
					}
				} catch (IllegalTransitionException tex) {
					System.out.println("Illegal transition exception");
					tex.printStackTrace();
				}
			}
		}
		bw.close();
	}

	private void writeMoveModelInfo(Transition trans, XEventClass eventClass, Integer cost, Marking m,
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
			if (eventClass != null) {
				bw.write(eventClass.getId());
			} else {
				bw.write(String.valueOf(eventClass));
			}
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
			bw.write(parikhVector.toString());
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
			bw.write(String.valueOf(eventClass));
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
			bw.write(parikhVector.toString());
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
				XAttribute attVal = attMap.get(attName);
				if (attVal != null) {
					bw.write(attVal.toString());
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
				XAttribute attVal = attMap.get(info);
				if (attVal != null) {
					sb.append(attVal.toString());
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
