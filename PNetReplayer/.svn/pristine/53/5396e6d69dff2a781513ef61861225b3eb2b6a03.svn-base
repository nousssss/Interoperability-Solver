/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.util.Pair;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.IPNMatchInstancesLogReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express.BestWithFitnessBoundAlignmentsTreeAlg;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express.NBestAlignmentsAlg;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express.ParamSettingBestWithFitnessBoundAlg;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express.ParamSettingExpressAlg;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express.ParamSettingNBestAlg;

/**
 * @author aadrians
 * 
 */
public class PNMatchInstancesReplayerUI {
	// reference variable
	private final UIPluginContext context;

	public static final int MAPPING = 0;
	public static final int ALGORITHM = 1;
	public static final int PARAMETERS = 2;

	// selected algorithm
	IPNMatchInstancesLogReplayAlgorithm selectedAlgorithm;
	
	// steps
	private int nofSteps;
	private int currentStep;

	private int algorithmStep;
	private int testingParamStep;

	// gui for each steps
	private PNReplayStep[] replaySteps;

	public PNMatchInstancesReplayerUI(final UIPluginContext context) {
		this.context = context;
		this.selectedAlgorithm = null;
	}

	public PNMatchInstancesReplayerUI(final UIPluginContext context, final IPNMatchInstancesLogReplayAlgorithm selectedAlgorithm) {
		this.context = context;
		this.selectedAlgorithm = selectedAlgorithm;
	}

	public Object[] getConfiguration(PetrinetGraph net, XLog log) {
		// init local parameter
		EvClassLogPetrinetConnection conn = null;

		// check existence of initial marking
		try {
			InitialMarkingConnection initCon = context.getConnectionManager().getFirstConnection(
					InitialMarkingConnection.class, context, net);

			if (((Marking) initCon.getObjectWithRole(InitialMarkingConnection.MARKING)).isEmpty()) {
				JOptionPane
						.showMessageDialog(
								new JPanel(),
								"The initial marking is an empty marking. If this is not intended, remove the currently existing InitialMarkingConnection object and then use \"Create Initial Marking\" plugin to create a non-empty initial marking.",
								"Empty Initial Marking", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (ConnectionCannotBeObtained exc) {
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(new JPanel(),
					"No initial marking is found for this model. Create one? (otherwise, use empty marking)",
					"No Initial Marking", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
				createMarking(context, net, InitialMarkingConnection.class);
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}

		// check existence of final marking
		try {
			context.getConnectionManager().getFirstConnection(FinalMarkingConnection.class, context, net);
		} catch (ConnectionCannotBeObtained exc) {
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(new JPanel(),
					"No final marking is found for this model. Create one? (otherwise, use empty marking)",
					"No Final Marking", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
				createMarking(context, net, FinalMarkingConnection.class);
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}

		// check connection in order to determine whether mapping step is needed
		// of not
		try {
			// connection is found, no need for mapping step
			// connection is not found, another plugin to create such connection
			// is automatically
			// executed
			conn = context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net,
					log);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JPanel(), "No mapping can be constructed between the net and the log");
			context.getFutureResult(0).cancel(true);
			return null;
		}

		// init gui for each step
		TransEvClassMapping mapping = (TransEvClassMapping) conn
				.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);

		// check invisible transitions
		Set<Transition> unmappedTrans = new HashSet<Transition>();
		for (Entry<Transition, XEventClass> entry : mapping.entrySet()) {
			if (entry.getValue().equals(mapping.getDummyEventClass())) {
				if (!entry.getKey().isInvisible()) {
					unmappedTrans.add(entry.getKey());
				}
			}
		}
		if (!unmappedTrans.isEmpty()) {
			JList list = new JList(unmappedTrans.toArray());
			JPanel panel = new JPanel();
			BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
			panel.setLayout(layout);
			panel.add(new JLabel("The following transitions are not mapped to any event class:"));

			JScrollPane sp = new JScrollPane(list);
			panel.add(sp);
			panel.add(new JLabel("Do you want to consider these transitions as invisible (unlogged activities)?"));

			Object[] options = { "Yes, set them to invisible", "No, keep them as they are" };

			if (0 == JOptionPane.showOptionDialog(null, panel, "Configure transition visibility",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0])) {
				for (Transition t : unmappedTrans) {
					t.setInvisible(true);
				}
			}
			;
		}

		// init steps and gui
		nofSteps = 0;

		// other steps
		algorithmStep = nofSteps++;
		testingParamStep = nofSteps++;

		// init gui for each step
		replaySteps = new PNReplayStep[nofSteps];
		replaySteps[algorithmStep] = new PNRepMatchInstancesAlgorithmStep(context);

		// set current step
		currentStep = algorithmStep;

		// how many configuration indexes?
		int[] configIndexes = new int[1];
		configIndexes[0] = testingParamStep;

		return showConfiguration(log, net, conn, configIndexes);
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

	private Object[] showConfiguration(XLog log, PetrinetGraph net, EvClassLogPetrinetConnection conn,
			int[] configIndexes) {
		// init result variable
		InteractionResult result = InteractionResult.NEXT;
		TransEvClassMapping mapping = (TransEvClassMapping) conn
				.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);

		// configure interaction with user
		while (true) {
			if (currentStep <= 0) {
				currentStep = selectedAlgorithm == null ? 0 : 1;
			}
			if (currentStep >= nofSteps) {
				currentStep = nofSteps - 1;
			}
			init(log, net, mapping);

			result = context.showWizard("Replay in Petri net", currentStep == 0, currentStep == nofSteps - 1,
					replaySteps[currentStep]);
			switch (result) {
				case NEXT :
					go(1, log, net, mapping);
					break;
				case PREV :
					go(-1, log, net, mapping);
					break;
				case FINISHED :
					// collect all parameters
					List<Object> allParameters = new LinkedList<Object>();
					for (int i = 0; i < configIndexes.length; i++) {
						PNParamSettingStep testParamGUI = ((PNParamSettingStep) replaySteps[configIndexes[i]]);
						Object[] params = testParamGUI.getAllParameters();
						for (Object o : params) {
							allParameters.add(o);
						}
					}

					return new Object[] { mapping,
							selectedAlgorithm == null ? 
							((PNRepMatchInstancesAlgorithmStep) replaySteps[algorithmStep]).getAlgorithm() : selectedAlgorithm,
							allParameters.toArray() };
				default :
					return null;
			}
		}
	}

	private void init(XLog log, PetrinetGraph net, TransEvClassMapping mapping) {
		if (selectedAlgorithm != null){
			if (selectedAlgorithm instanceof NBestAlignmentsAlg){
				if (selectedAlgorithm instanceof BestWithFitnessBoundAlignmentsTreeAlg) {
					ParamSettingBestWithFitnessBoundAlg paramSetting = new ParamSettingBestWithFitnessBoundAlg();
					paramSetting.populateCostPanel(net, log, mapping);
					replaySteps[testingParamStep] = paramSetting;						
				} else {
					ParamSettingNBestAlg paramSetting = new ParamSettingNBestAlg();
					paramSetting.populateCostPanel(net, log, mapping);
					replaySteps[testingParamStep] = paramSetting;	
				}
			} else {
				ParamSettingExpressAlg paramSetting = new ParamSettingExpressAlg();
				paramSetting.populateCostPanel(net, log, mapping);
				replaySteps[testingParamStep] = paramSetting;
			}
		}
	}
	
	private int go(int direction, XLog log, PetrinetGraph net, TransEvClassMapping mapping) {
		currentStep += direction;

		if ((currentStep == algorithmStep)&&(selectedAlgorithm != null)){
			// skip selection of algorithm, proceed with algorithm so far
			currentStep += direction;
		}
		
		// check which algorithm is selected and adjust parameter as necessary
		if ((currentStep == testingParamStep)&&(selectedAlgorithm == null)) {
			// special checking for N-best
			if (replaySteps[algorithmStep] instanceof PNRepMatchInstancesAlgorithmStep) {
				// which algorithm is it?
				PNRepMatchInstancesAlgorithmStep step = (PNRepMatchInstancesAlgorithmStep) replaySteps[algorithmStep];
				if ((step.getAlgorithm() instanceof NBestAlignmentsAlg)) {
					if (step.getAlgorithm() instanceof BestWithFitnessBoundAlignmentsTreeAlg) {
						ParamSettingBestWithFitnessBoundAlg paramSetting = new ParamSettingBestWithFitnessBoundAlg();
						paramSetting.populateCostPanel(net, log, mapping);
						replaySteps[testingParamStep] = paramSetting;
					} else {
						ParamSettingNBestAlg paramSetting = new ParamSettingNBestAlg();
						paramSetting.populateCostPanel(net, log, mapping);
						replaySteps[testingParamStep] = paramSetting;	
					}
				} else {
					ParamSettingExpressAlg paramSetting = new ParamSettingExpressAlg();
					paramSetting.populateCostPanel(net, log, mapping);
					replaySteps[testingParamStep] = paramSetting;
				}
			}
		}

		if ((currentStep >= 0) && (currentStep < nofSteps)) {
			return currentStep;
		}
		return currentStep;
	}

}
