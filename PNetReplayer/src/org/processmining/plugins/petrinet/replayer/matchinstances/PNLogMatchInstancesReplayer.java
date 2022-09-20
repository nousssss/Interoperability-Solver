/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances;

import java.text.NumberFormat;

import javax.swing.JLabel;

import nl.tue.astar.AStarException;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.PNMatchInstancesRepResultConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.IPNMatchInstancesLogReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.matchinstances.ui.PNMatchInstancesReplayerUI;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;

/**
 * This class replay a log on a model and return the set of all best matching
 * alignments for all traces in the log.
 * 
 * NOTE: Some algorithms discard final markings, some are not.
 * 
 * @author aadrians
 * 
 */
@Plugin(name = "Replay a Log on Petri Net for All Optimal Alignments", returnLabels = { "All Optimal Alignments" }, returnTypes = { PNMatchInstancesRepResult.class }, parameterLabels = {
		"Petri net", "Event Log", "Mapping", "Initial Marking", "Final Marking", "Replay Algorithm", "Parameters" }, help = "Replay an event log on Petri net to obtain all optimal alignments for each trace.", userAccessible = true)
public class PNLogMatchInstancesReplayer {

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Arya Adriansyah", email = "a.adriansyah@tue.nl", pack = "PNetReplayer")
	@PluginVariant(variantLabel = "From Petri net and Event Log", requiredParameterLabels = { 0, 1 })
	public PNMatchInstancesRepResult replayLog(final UIPluginContext context, Petrinet net, XLog log)
			throws AStarException {
		return replayLogGUIPrivate(context, net, log);
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Arya Adriansyah", email = "a.adriansyah@tue.nl", pack = "PNetReplayer")
	@PluginVariant(variantLabel = "From Reset net and Event Log", requiredParameterLabels = { 0, 1 })
	public PNMatchInstancesRepResult replayLog(final UIPluginContext context, ResetNet net, XLog log) throws AStarException {
		return replayLogGUIPrivate(context, net, log);
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Arya Adriansyah", email = "a.adriansyah@tue.nl", pack = "PNetReplayer")
	@PluginVariant(variantLabel = "From Reset Inhibitor net and Event Log", requiredParameterLabels = { 0, 1 })
	public PNMatchInstancesRepResult replayLog(final UIPluginContext context, ResetInhibitorNet net, XLog log) throws AStarException {
		return replayLogGUIPrivate(context, net, log);
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Arya Adriansyah", email = "a.adriansyah@tue.nl", pack = "PNetReplayer")
	@PluginVariant(variantLabel = "From Inhibitor net and Event Log", requiredParameterLabels = { 0, 1 })
	public PNMatchInstancesRepResult replayLog(final UIPluginContext context, InhibitorNet net, XLog log) throws AStarException {
		return replayLogGUIPrivate(context, net, log);
	}

	@Deprecated
	public PNMatchInstancesRepResult replayLog(final UIPluginContext context, PetrinetGraph net, XLog log) throws AStarException {
		return replayLogGUIPrivate(context, net, log);
	}

	private PNMatchInstancesRepResult replayLogGUIPrivate(final UIPluginContext context, PetrinetGraph net, XLog log) throws AStarException {
		if (net.getTransitions().isEmpty()) {
			context.showConfiguration("Error", new JLabel("Cannot replay on a Petri net that does not contain transitions. Select Cancel or Continue to continue."));
			context.getFutureResult(0).cancel(true);
			return null;
		}
		PNMatchInstancesReplayerUI pnReplayerUI = new PNMatchInstancesReplayerUI(context);
		Object[] resultConfiguration = pnReplayerUI.getConfiguration(net, log);
		if (resultConfiguration == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		}

		// check connection between petri net and marking
		Marking initMarking = null;
		try {
			initMarking = context.getConnectionManager()
					.getFirstConnection(InitialMarkingConnection.class, context, net)
					.getObjectWithRole(InitialMarkingConnection.MARKING);
		} catch (Exception exc) {
			initMarking = new Marking();
		}

		Marking finalMarking = null;
		try {
			finalMarking = context.getConnectionManager()
					.getFirstConnection(FinalMarkingConnection.class, context, net)
					.getObjectWithRole(FinalMarkingConnection.MARKING);
		} catch (Exception exc) {
			finalMarking = new Marking();
		}

		// if all parameters are set, replay log
		if (resultConfiguration[PNMatchInstancesReplayerUI.MAPPING] != null) {
			context.log("replay is performed. All parameters are set.");

			// get all parameters
			IPNMatchInstancesLogReplayAlgorithm selectedAlg = (IPNMatchInstancesLogReplayAlgorithm) resultConfiguration[PNMatchInstancesReplayerUI.ALGORITHM];

			PNMatchInstancesRepResult res = replayLogPrivate(context, net, log,
					(TransEvClassMapping) resultConfiguration[PNMatchInstancesReplayerUI.MAPPING], initMarking,
					finalMarking, selectedAlg, (Object[]) resultConfiguration[PNMatchInstancesReplayerUI.PARAMETERS]);

			// add connection
			PNMatchInstancesRepResultConnection con = context.addConnection(new PNMatchInstancesRepResultConnection(
					"All results of replaying " + XConceptExtension.instance().extractName(log) + " on "
							+ net.getLabel(), net, initMarking, log, res));
			con.setLabel("Connection between " + net.getLabel() + ", " + XConceptExtension.instance().extractName(log)
					+ ", and all optimal alignments");

			context.getFutureResult(0).setLabel(
					"All optimal alignments between log " + XConceptExtension.instance().extractName(log) + " on "
							+ net.getLabel() + " using " + selectedAlg.toString());

			return res;

		} else {
			context.log("replay is not performed because not enough parameter is submitted");
			context.getFutureResult(0).cancel(true);
			return null;
		}
	}

	@PluginVariant(variantLabel = "Replay Petri net on log, require complete parameters", requiredParameterLabels = {
			0, 1, 2, 3, 4, 5, 6 })
	public PNMatchInstancesRepResult replayLog(PluginContext context, Petrinet net, XLog log,
			TransEvClassMapping mapping, Marking initMarking, Marking finalMarking,
			IPNMatchInstancesLogReplayAlgorithm selectedAlg, Object[] parameters) throws AStarException {
		return replayLogPrivate(context, net, log, mapping, initMarking, finalMarking, selectedAlg, parameters);
	}

	@PluginVariant(variantLabel = "Replay Petri net on log, require complete parameters", requiredParameterLabels = {
			0, 1, 2, 3, 4, 5, 6 })
	public PNMatchInstancesRepResult replayLog(PluginContext context, ResetNet net, XLog log,
			TransEvClassMapping mapping, Marking initMarking, Marking finalMarking,
			IPNMatchInstancesLogReplayAlgorithm selectedAlg, Object[] parameters) throws AStarException {
		return replayLogPrivate(context, net, log, mapping, initMarking, finalMarking, selectedAlg, parameters);
	}

	@PluginVariant(variantLabel = "Replay Petri net on log, require complete parameters", requiredParameterLabels = {
			0, 1, 2, 3, 4, 5, 6 })
	public PNMatchInstancesRepResult replayLog(PluginContext context, ResetInhibitorNet net, XLog log,
			TransEvClassMapping mapping, Marking initMarking, Marking finalMarking,
			IPNMatchInstancesLogReplayAlgorithm selectedAlg, Object[] parameters) throws AStarException {
		return replayLogPrivate(context, net, log, mapping, initMarking, finalMarking, selectedAlg, parameters);
	}

	@PluginVariant(variantLabel = "Replay Petri net on log, require complete parameters", requiredParameterLabels = {
			0, 1, 2, 3, 4, 5, 6 })
	public PNMatchInstancesRepResult replayLog(PluginContext context, InhibitorNet net, XLog log,
			TransEvClassMapping mapping, Marking initMarking, Marking finalMarking,
			IPNMatchInstancesLogReplayAlgorithm selectedAlg, Object[] parameters) throws AStarException {
		return replayLogPrivate(context, net, log, mapping, initMarking, finalMarking, selectedAlg, parameters);
	}

	private PNMatchInstancesRepResult replayLogPrivate(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping, Marking initMarking, Marking finalMarking,
			IPNMatchInstancesLogReplayAlgorithm selectedAlg, Object[] parameters) throws AStarException {

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		long startTime = System.nanoTime();

		// for each trace, replay according to the algorithm. Only returns two objects
		PNMatchInstancesRepResult allReplayRes = selectedAlg.replayLog(context, net, initMarking, finalMarking, log,
				mapping, parameters);
		long duration = System.nanoTime() - startTime;

		if (context != null) {
			context.log("Replay is finished in " + nf.format(duration / 1000000000) + " seconds");
		}
		return allReplayRes;
	}
}
