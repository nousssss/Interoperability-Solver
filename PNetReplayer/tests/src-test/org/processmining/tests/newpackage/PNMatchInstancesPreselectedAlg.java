/**
 * 
 */
package org.processmining.tests.newpackage;

import java.text.NumberFormat;

import javax.swing.JLabel;

import nl.tue.astar.AStarException;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.IPNMatchInstancesLogReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express.AllOptAlignmentsGraphAlg;
import org.processmining.plugins.petrinet.replayer.matchinstances.ui.PNMatchInstancesReplayerUI;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;

/**
 * @author aadrians
 * Apr 15, 2013
 *
 */
@Plugin(name = "Replay to Obtain All Optimal Alignments with Preselected Algs", returnLabels = { "All Optimal Alignments" }, returnTypes = { PNMatchInstancesRepResult.class }, parameterLabels = {
		"Petri net", "Event Log" }, help = "Replay an event log on Petri net to obtain all optimal alignments for each trace.", userAccessible = true)
public class PNMatchInstancesPreselectedAlg {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Arya Adriansyah", email = "a.adriansyah@tue.nl", pack="PNetReplayer")
	@PluginVariant(variantLabel = "From Reset/Inhibitor net and Event Log", requiredParameterLabels = { 0, 1 })
	public PNMatchInstancesRepResult replayLogGUI(final UIPluginContext context, PetrinetGraph net, XLog log)
			throws AStarException {
		if (net.getTransitions().isEmpty()) {
			context.showConfiguration("Error", new JLabel("Cannot replay on a Petri net that does not contain transitions. Select Cancel or Continue to continue."));
			context.getFutureResult(0).cancel(true);
			return null;
		}
		// to use specific algorithm
//		selectedAlg = new AllOptAlignmentsTreeAlg(); // benchmarking method to compute all optimal alignments, used in BPI 2012 paper
//		selectedAlg = new AllOptAlignmentsGraphSamplingAlg(); // find representatives of optimal alignments
//		selectedAlg = new AllOptAlignmentsGraphAlg(); // all optimal alignments, using graph optimization

		PNMatchInstancesReplayerUI pnReplayerUI = new PNMatchInstancesReplayerUI(context, new AllOptAlignmentsGraphAlg());
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
			// change the selected algorithm with your choice.
			// example

			IPNMatchInstancesLogReplayAlgorithm selectedAlg = (IPNMatchInstancesLogReplayAlgorithm) resultConfiguration[PNMatchInstancesReplayerUI.ALGORITHM];

			
			// repeat replay for all logs here...
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			nf.setMinimumFractionDigits(2);

			// for each trace, replay according to the algorithm. Only returns two objects
			
			long startTime = System.nanoTime();

			// for each trace, replay according to the algorithm. Only returns two objects
			PNMatchInstancesRepResult allReplayRes = selectedAlg.replayLog(context, net, initMarking, finalMarking, log,
					(TransEvClassMapping) resultConfiguration[PNMatchInstancesReplayerUI.MAPPING], (Object[]) resultConfiguration[PNMatchInstancesReplayerUI.PARAMETERS]);
			long duration = System.nanoTime() - startTime;

			return allReplayRes;
		} else {
			context.log("replay is not performed because not enough parameter is submitted");
			context.getFutureResult(0).cancel(true);
			return null;
		}
	}
	
	
}
