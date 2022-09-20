/**
 * 
 */
package org.processmining.plugins.petrinet.replayer;

import java.text.NumberFormat;

import javax.swing.JLabel;

import nl.tue.astar.AStarException;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.PNRepResultAllRequiredParamConnection;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.ui.PNReplayerUI;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

/**
 * @author aadrians
 * 
 */
@Plugin(name = "Replay a Log on Petri Net for Conformance Analysis", level = PluginLevel.PeerReviewed, categories = { PluginCategory.ConformanceChecking }, returnLabels = { "Petrinet log replay result" }, returnTypes = { PNRepResult.class }, parameterLabels = {
		"Petri net", "Event Log", "Mapping", "Replay Algorithm", "Parameters" }, help = "Replay an event log on Petri net to check conformance.", userAccessible = true)
public class PNLogReplayer {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Arya Adriansyah", email = "a.adriansyah@tue.nl", pack = "PNetReplayer")
	@PluginVariant(variantLabel = "From Petri net and Event Log", requiredParameterLabels = { 0, 1 })
	public PNRepResult replayLog(final UIPluginContext context, Petrinet net, XLog log)
			throws ConnectionCannotBeObtained, AStarException {
		return replayLogGUI(context, net, log);
	}
	
//	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Arya Adriansyah", email = "a.adriansyah@tue.nl", pack = "PNetReplayer")
	@PluginVariant(variantLabel = "From Reset net and Event Log", requiredParameterLabels = { 0, 1 })
	public PNRepResult replayLog(final UIPluginContext context, ResetNet net, XLog log) throws ConnectionCannotBeObtained, AStarException {
		return replayLogGUI(context, net, log);
	}
	
//	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Arya Adriansyah", email = "a.adriansyah@tue.nl", pack = "PNetReplayer")
	@PluginVariant(variantLabel = "From Reset Inhibitor net and Event Log", requiredParameterLabels = { 0, 1 })
	public PNRepResult replayLog(final UIPluginContext context, ResetInhibitorNet net, XLog log) throws ConnectionCannotBeObtained, AStarException {
		return replayLogGUI(context, net, log);
	}
	
//	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Arya Adriansyah", email = "a.adriansyah@tue.nl", pack = "PNetReplayer")
	@PluginVariant(variantLabel = "From Inhibitor net and Event Log", requiredParameterLabels = { 0, 1 })
	public PNRepResult replayLog(final UIPluginContext context, InhibitorNet net, XLog log) throws ConnectionCannotBeObtained, AStarException {
		return replayLogGUI(context, net, log);
	}
	
	public PNRepResult replayLogGUI(final UIPluginContext context, PetrinetGraph net, XLog log)
			throws ConnectionCannotBeObtained, AStarException {
		if (net.getTransitions().isEmpty()) {
			context.showConfiguration("Error", new JLabel("Cannot replay on a Petri net that does not contain transitions. Select Cancel or Continue to continue."));
			context.getFutureResult(0).cancel(true);
			return null;
		}
		PNReplayerUI pnReplayerUI = new PNReplayerUI();
		Object[] resultConfiguration = pnReplayerUI.getConfiguration(context, net, log);
		if (resultConfiguration == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		}

		// if all parameters are set, replay log
		if (resultConfiguration[PNReplayerUI.MAPPING] != null) {
			context.log("replay is performed. All parameters are set.");

			// This connection MUST exists, as it is constructed by the configuration if necessary
			context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net, log);

			// get all parameters
			IPNReplayAlgorithm selectedAlg = (IPNReplayAlgorithm) resultConfiguration[PNReplayerUI.ALGORITHM];
			IPNReplayParameter algParameters = (IPNReplayParameter) resultConfiguration[PNReplayerUI.PARAMETERS];

			// since based on GUI, create connection
			algParameters.setCreateConn(true);
			algParameters.setGUIMode(true);

			PNRepResult res = replayLogPrivate(context, net, log,
					(TransEvClassMapping) resultConfiguration[PNReplayerUI.MAPPING], selectedAlg, algParameters);

			context.getFutureResult(0).setLabel(
					"Replay result - log " + XConceptExtension.instance().extractName(log) + " on " + net.getLabel()
							+ " using " + selectedAlg.toString());

			return res;

		} else {
			context.log("replay is not performed because not enough parameter is submitted");
			context.getFutureResult(0).cancel(true);
			return null;
		}
	}
	
	@PluginVariant(variantLabel = "Complete parameters", requiredParameterLabels = { 0, 1, 2, 3, 4 })
	public PNRepResult replayLog(PluginContext context, Petrinet net, XLog log, TransEvClassMapping mapping,
			IPNReplayAlgorithm selectedAlg, IPNReplayParameter parameters) throws AStarException {
		return replayLogPrivate(context, net, log, mapping, selectedAlg, parameters);
	}
	@PluginVariant(variantLabel = "Complete parameters", requiredParameterLabels = { 0, 1, 2, 3, 4 })
	public PNRepResult replayLog(PluginContext context, ResetNet net, XLog log, TransEvClassMapping mapping,
			IPNReplayAlgorithm selectedAlg, IPNReplayParameter parameters) throws AStarException {
		return replayLogPrivate(context, net, log, mapping, selectedAlg, parameters);
	}
	@PluginVariant(variantLabel = "Complete parameters", requiredParameterLabels = { 0, 1, 2, 3, 4 })
	public PNRepResult replayLog(PluginContext context, ResetInhibitorNet net, XLog log, TransEvClassMapping mapping,
			IPNReplayAlgorithm selectedAlg, IPNReplayParameter parameters) throws AStarException {
		return replayLogPrivate(context, net, log, mapping, selectedAlg, parameters);
	}
	@PluginVariant(variantLabel = "Complete parameters", requiredParameterLabels = { 0, 1, 2, 3, 4 })
	public PNRepResult replayLog(PluginContext context, InhibitorNet  net, XLog log, TransEvClassMapping mapping,
			IPNReplayAlgorithm selectedAlg, IPNReplayParameter parameters) throws AStarException {
		return replayLogPrivate(context, net, log, mapping, selectedAlg, parameters);
	}
	
	public PNRepResult replayLog(PluginContext context, PetrinetGraph  net, XLog log, TransEvClassMapping mapping,
			IPNReplayAlgorithm selectedAlg, IPNReplayParameter parameters) throws AStarException {
		return replayLogPrivate(context, net, log, mapping, selectedAlg, parameters);
	}

	/**
	 * Main method to replay log.
	 * 
	 * @param context
	 * @param net
	 * @param log
	 * @param mapping
	 * @param selectedAlg
	 * @param parameters
	 * @return
	 * @throws AStarException
	 */
	private PNRepResult replayLogPrivate(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayAlgorithm selectedAlg, IPNReplayParameter parameters) throws AStarException {
		if (selectedAlg.isAllReqSatisfied(context, net, log, mapping, parameters)) {
			// for each trace, replay according to the algorithm. Only returns two objects
			PNRepResult replayRes = null;

			if (parameters.isGUIMode()) {
				long start = System.nanoTime();

				replayRes = selectedAlg.replayLog(context, net, log, mapping, parameters);

				long period = System.nanoTime() - start;
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMinimumFractionDigits(2);
				nf.setMaximumFractionDigits(2);

				context.log("Replay is finished in " + nf.format(period / 1000000000) + " seconds");
			} else {
				replayRes = selectedAlg.replayLog(context, net, log, mapping, parameters);
			}

			// add connection
			if (replayRes != null) {
				if (parameters.isCreatingConn()) {
					createConnections(context, net, log, mapping, selectedAlg, parameters, replayRes);
				}
			}

			return replayRes;
		} else {
			if (context != null) {
				context.log("The provided parameters is not valid for the selected algorithm.");
				context.getFutureResult(0).cancel(true);
			}
			return null;
		}
	}

	protected void createConnections(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayAlgorithm selectedAlg, IPNReplayParameter parameters, PNRepResult replayRes) {
		context.addConnection(new PNRepResultAllRequiredParamConnection(
				"Connection between replay result, " + XConceptExtension.instance().extractName(log)
						+ ", and " + net.getLabel(), net, log, mapping, selectedAlg, parameters, replayRes));
	}

//	public Object[] replayLog(PluginContext context) throws AStarException {
//		Petrinet net = null;
//		XLog log = null;
//		PNRepResult res = null;
//
//		Random r = new Random();
//		for (int n = 3; n < 50; n++) {
//			System.out.println("Test " + n);
//			net = new PetrinetImpl("Test" + n);
//			//int n = 10;
//			int m = r.nextInt(n);
//			final Place[] places = new Place[n + 1];
//			final Transition[] vis = new Transition[n];
//			final Transition[] invis = new Transition[n];
//
//			for (int i = 0; i <= n; i++) {
//				places[i] = net.addPlace("p" + i);
//			}
//
//			for (int i = 0; i < n; i++) {
//				vis[i] = net.addTransition("A");
//				invis[i] = net.addTransition("tau");
//				invis[i].setInvisible(true);
//				net.addArc(places[i], vis[i]);
//				net.addArc(places[i], invis[i]);
//				net.addArc(vis[i], places[i + 1]);
//				net.addArc(invis[i], places[i + 1]);
//			}
//
//			// net is done.
//
//			log = XFactoryRegistry.instance().currentDefault().createLog();
//			XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace();
//			XConceptExtension.instance().assignName(trace, "trace");
//			log.getGlobalEventAttributes().addAll(XConceptExtension.instance().getEventAttributes());
//			log.add(trace);
//			for (int i = 0; i < n - m; i++) {
//				XEvent evt = XFactoryRegistry.instance().currentDefault().createEvent();
//				XConceptExtension.instance().assignName(evt, "A");
//				trace.add(evt);
//			}
//			log.getClassifiers().add(new XEventAttributeClassifier("Event name", XConceptExtension.KEY_NAME));
//
//			// log is done.
//
//			XLogInfo info = XLogInfoFactory.createLogInfo(log, log.getClassifiers().get(0));
//			XEventClass clazz = info.getEventClasses().getByIndex(0);
//			XEventClass dummy = new XEventClass("", 1);
//			TransEvClassMapping mapping = new TransEvClassMapping(log.getClassifiers().get(0), dummy);
//
//			Map<XEventClass, Integer> mapEvClass2Cost = new HashMap<XEventClass, Integer>();
//			Map<Transition, Integer> mapTrans2Cost = new HashMap<Transition, Integer>();
//
//			mapEvClass2Cost.put(clazz, 10);
//			mapEvClass2Cost.put(dummy, 0);
//
//			for (int i = 0; i < vis.length; i++) {
//				mapping.put(vis[i], clazz);
//				mapTrans2Cost.put(vis[i], 10);
//				mapTrans2Cost.put(invis[i], r.nextInt(11));
//			}
//
//			// mapping is done, mapping all visible transitions to the only event class
//			IPNReplayParameter parameters = new CostBasedCompleteParam(mapEvClass2Cost, mapTrans2Cost);
//			Marking mi = new Marking();
//			mi.add(places[0]);
//			Marking mf = new Marking();
//			mf.add(places[n]);
//			parameters.setFinalMarkings(mf);
//			parameters.setInitialMarking(mi);
//
//			res = replayLog(context, net, log, mapping, new CostBasedCompleteMarkEquationPrune(), parameters);
//
//			SyncReplayResult replayedTrace = res.first();
//			PetrinetSemantics sem = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);
//
//			sem.setCurrentState(mi);
//
//			System.out.println(replayedTrace.getStepTypes());
//			for (int i = 0; i < replayedTrace.getNodeInstance().size(); i++) {
//				Transition trans = (Transition) replayedTrace.getNodeInstance().get(i);
//				try {
//					sem.executeExecutableTransition(trans);
//				} catch (IllegalTransitionException e) {
//					System.err.println("OOPS");
//				}
//			}
//			System.out.println();
//			System.out.println(replayedTrace.getNodeInstance());
//			System.out.println(replayedTrace.getInfo());
//		}
//		return new Object[] { net, log, res };
//	}
}
