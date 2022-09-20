/**
 * 
 */
package org.processmining.plugins.petrinet.manifestreplayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import nl.tue.astar.AStarException;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.ConnectionManager;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.Pair;
import org.processmining.framework.util.ui.wizard.ListWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.models.connections.petrinets.PNManifestConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.manifestreplay.ui.ChooseAlgorithmStep;
import org.processmining.plugins.astar.petrinet.manifestreplay.ui.CreatePatternPanel;
import org.processmining.plugins.astar.petrinet.manifestreplay.ui.CreatePatternStep;
import org.processmining.plugins.astar.petrinet.manifestreplay.ui.MapCostStep;
import org.processmining.plugins.astar.petrinet.manifestreplay.ui.MapPattern2TransStep;
import org.processmining.plugins.petrinet.manifestreplayer.algorithms.IPNManifestReplayAlgorithm;
import org.processmining.plugins.petrinet.manifestreplayresult.Manifest;

/**
 * @author aadrians Feb 13, 2012
 * 
 */
@Plugin(name = "Replay a Log on Petri Net for Performance/Conformance Analysis", level = PluginLevel.PeerReviewed, categories = { PluginCategory.ConformanceChecking }, returnLabels = { "Manifest" }, returnTypes = { Manifest.class }, parameterLabels = {
		"Petri net", "Event Log", "Algorithm", "Parameters" }, help = "Replay an event log on Petri net to get all manifest of patterns.", userAccessible = true)
public class PNManifestReplayer {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Arya Adriansyah", email = "a.adriansyah@tue.nl", pack = "PNetReplayer")
	@PluginVariant(variantLabel = "From Petri net and Event Log", requiredParameterLabels = { 0, 1 })
	public Manifest replayLogPetrinet(UIPluginContext context, Petrinet net, XLog log) throws AStarException {
		return replayLog(context, net, log);
	}
	
	public Manifest replayLog(UIPluginContext context, PetrinetGraph net, XLog log) throws AStarException {
		if (net.getTransitions().isEmpty()) {
			context.showConfiguration(
					"Error",
					new JLabel(
							"Cannot replay on a Petri net that does not contain transitions. Select Cancel or Continue to continue."));
			context.getFutureResult(0).cancel(true);
			return null;
		}
		Object[] obj = chooseAlgorithmAndParam(context, net, log);

		if (obj == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		}
		IPNManifestReplayAlgorithm alg = (IPNManifestReplayAlgorithm) obj[0];
		PNManifestReplayerParameter parameter = (PNManifestReplayerParameter) obj[1];

		return replayLogParameter(context, net, log, alg, parameter);
	}

	@PluginVariant(variantLabel = "From Petri net and Event Log, given algorithm and parameters", requiredParameterLabels = {
			0, 1, 2, 3 })
	public Manifest replayLogParameter(PluginContext context, PetrinetGraph net, XLog log,
			IPNManifestReplayAlgorithm alg, PNManifestReplayerParameter parameter) throws AStarException {
		Manifest manifest = replayLog(context, net, log, alg, parameter);
		if (parameter.isBuildConnection()) {
			createConnections(context, net, log, alg, parameter, manifest);
		}
		return manifest;
	}

	protected void createConnections(PluginContext context, PetrinetGraph net, XLog log,
			IPNManifestReplayAlgorithm alg, PNManifestReplayerParameter parameter, Manifest manifest) {
		context.addConnection(new PNManifestConnection("Manifest connection", net, log, alg, parameter, manifest));
	}

	/**
	 * getParameter, using the provided GUI context This method may create
	 * initial and final marking connection if not exists before.
	 * 
	 * @param context
	 * @param net
	 * @param log
	 * @return
	 */
	public Object[] chooseAlgorithmAndParam(UIPluginContext context, PetrinetGraph net, XLog log) {
		/**
		 * Utilities
		 */
		// generate create pattern GUI
		// list possible classifiers
		List<XEventClassifier> classList = new ArrayList<XEventClassifier>(log.getClassifiers());
		// add default classifiers
		if (!classList.contains(XLogInfoImpl.RESOURCE_CLASSIFIER)) {
			classList.add(0, XLogInfoImpl.RESOURCE_CLASSIFIER);
		}
		if (!classList.contains(XLogInfoImpl.NAME_CLASSIFIER)) {
			classList.add(0, XLogInfoImpl.NAME_CLASSIFIER);
		}
		if (!classList.contains(XLogInfoImpl.STANDARD_CLASSIFIER)) {
			classList.add(0, XLogInfoImpl.STANDARD_CLASSIFIER);
		}

		XEventClassifier[] availableClassifiers = classList.toArray(new XEventClassifier[classList.size()]);
		CreatePatternStep createPatternStep = new CreatePatternStep(log, availableClassifiers);

		// results, required earlier for wizard
		PNManifestReplayerParameter parameter = new PNManifestReplayerParameter();

		// generate pattern mapping GUI
		MapPattern2TransStep mapPatternStep = new MapPattern2TransStep(net, log,
				(CreatePatternPanel) createPatternStep.getComponent(parameter));

		// generate algorithm selection GUI, look for initial marking and final markings
		Marking initialMarking;
		ConnectionManager connManager = context.getConnectionManager();
		// check existence of initial marking
		try {
			InitialMarkingConnection initCon = connManager.getFirstConnection(InitialMarkingConnection.class, context,
					net);

			initialMarking = (Marking) initCon.getObjectWithRole(InitialMarkingConnection.MARKING);
			if (initialMarking.isEmpty()) {
				JOptionPane
						.showMessageDialog(
								new JPanel(),
								"The initial marking is an empty marking. If this is not intended, remove the currently existing InitialMarkingConnection object and then use \"Create Initial Marking\" plugin to create a non-empty initial marking.",
								"Empty Initial Marking", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (ConnectionCannotBeObtained exc) {
			if (0 == JOptionPane.showConfirmDialog(new JPanel(),
					"No initial marking is found for this model. Do you want to create one?", "No Initial Marking",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
				createMarking(context, net, InitialMarkingConnection.class);
				try {
					initialMarking = connManager.getFirstConnection(InitialMarkingConnection.class, context, net)
							.getObjectWithRole(InitialMarkingConnection.MARKING);
				} catch (ConnectionCannotBeObtained e) {
					e.printStackTrace();
					initialMarking = new Marking();
				}
			} else {
				initialMarking = new Marking();
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
			initialMarking = new Marking();
		}

		Marking[] finalMarkings;
		try {
			Collection<FinalMarkingConnection> conns = connManager.getConnections(FinalMarkingConnection.class,
					context, net);
			finalMarkings = new Marking[conns.size()];
			if (conns != null) {
				int i = 0;
				for (FinalMarkingConnection fmConn : conns) {
					finalMarkings[i] = fmConn.getObjectWithRole(FinalMarkingConnection.MARKING);
					i++;
				}
			}
		} catch (ConnectionCannotBeObtained excCon) {
			if (0 == JOptionPane
					.showConfirmDialog(
							new JPanel(),
							"No final marking is found for this model. Current manifest replay require final marking. Do you want to create one?",
							"No Final Marking", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
				if (!createMarking(context, net, FinalMarkingConnection.class)) {
					return null;
				}
				;
				try {
					finalMarkings = new Marking[1];
					finalMarkings[0] = connManager.getFirstConnection(FinalMarkingConnection.class, context, net)
							.getObjectWithRole(FinalMarkingConnection.MARKING);
					if (finalMarkings[0] != null) {
						JOptionPane.showMessageDialog(new JPanel(), "A final marking (" + finalMarkings[0]
								+ ") is created. Please re-run the plugin.");
					}
					return null;
				} catch (ConnectionCannotBeObtained e) {
					e.printStackTrace();
					finalMarkings = new Marking[0];
				}
			} else {
				return null;
			}
			;
		} catch (Exception exc) {
			finalMarkings = new Marking[0];
		}
		ChooseAlgorithmStep chooseAlgorithmStep = new ChooseAlgorithmStep(net, log, initialMarking, finalMarkings);

		// generate cost setting GUI
		MapCostStep mapCostStep = new MapCostStep(createPatternStep.getPatternCreatorPanel(),
				mapPatternStep.getPatternMappingPanel());

		// construct dialog wizard
		ArrayList<ProMWizardStep<PNManifestReplayerParameter>> listSteps = new ArrayList<ProMWizardStep<PNManifestReplayerParameter>>(
				4);
		listSteps.add(createPatternStep);
		listSteps.add(mapPatternStep);
		listSteps.add(chooseAlgorithmStep);
		listSteps.add(mapCostStep);

		ListWizard<PNManifestReplayerParameter> wizard = new ListWizard<PNManifestReplayerParameter>(listSteps);

		// show wizard
		parameter = ProMWizardDisplay.show(context, wizard, parameter);

		if (parameter == null) {
			return null;
		}

		// show message: GUI mode
		parameter.setGUIMode(true);

		IPNManifestReplayAlgorithm alg = chooseAlgorithmStep.getSelectedAlgorithm();
		return new Object[] { alg, parameter };
	}

	private boolean createMarking(UIPluginContext context, PetrinetGraph net, Class<? extends Connection> classType) {
		boolean result = false;
		Collection<Pair<Integer, PluginParameterBinding>> plugins = context.getPluginManager().find(
				ConnectionObjectFactory.class, classType, context.getClass(), true, false, false, net.getClass());
		PluginContext c2 = context.createChildContext("Creating connection of Type " + classType);
		Pair<Integer, PluginParameterBinding> pair = plugins.iterator().next();
		PluginParameterBinding binding = pair.getSecond();
		try {
			PluginExecutionResult pluginResult = binding.invoke(c2, net);
			pluginResult.synchronize();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c2.getParentContext().deleteChild(c2);
		}
		return result;
	}

	public Manifest replayLog(PluginContext context, PetrinetGraph net, XLog log,
			IPNManifestReplayAlgorithm selectedAlg, PNManifestReplayerParameter parameters) throws AStarException {
		// checking preconditions here
		if (selectedAlg.isAllReqSatisfied(net, log, parameters)) {
			return selectedAlg.replayLog(context, net, log, parameters);
		} else {
			throw new IllegalArgumentException();
		}
	}
}
