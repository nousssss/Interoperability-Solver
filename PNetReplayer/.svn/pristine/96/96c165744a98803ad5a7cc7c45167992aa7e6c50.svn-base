/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.matchinstances.ui.PNParamSettingStep;

import com.fluxicon.slickerbox.components.NiceIntegerSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * @author aadrians
 * 
 */
public class ParamSettingExpressAlg extends PNParamSettingStep {
	private static final long serialVersionUID = -441770156733336644L;

	// default value 
	protected static final int DEFCOSTMOVEONLOG = 1;
	protected static final int DEFCOSTMOVEONMODEL = 1;
	protected static final int MAXLIMMAXNUMINSTANCES = 10001;
	protected static final int DEFLIMMAXNUMINSTANCES = 2000;

	// parameter-related GUI
	protected NiceIntegerSlider limExpInstances;
	protected Map<Transition, Integer> mapTrans2RowIndex = new HashMap<Transition, Integer>();
	protected DefaultTableModel tableModel = null;
	protected ProMTable promTable;

	protected Map<XEventClass, Integer> mapXEvClass2RowIndex = new HashMap<XEventClass, Integer>();
	protected DefaultTableModel evClassTableModel = null;
	protected ProMTable promEvClassTable;

	public ParamSettingExpressAlg() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		setMaximumSize(new Dimension(400, 300));
		setPreferredSize(new Dimension(400, 300));

	}

	protected void generateStandardGUI() {
		SlickerFactory slickerFactoryInstance = SlickerFactory.instance();

		// max instance
		limExpInstances = slickerFactoryInstance.createNiceIntegerSlider(
				"<html><h4># Maximum explored states (in hundreds). Set max for unlimited.</h4></html>", 1,
				MAXLIMMAXNUMINSTANCES, DEFLIMMAXNUMINSTANCES, Orientation.HORIZONTAL);

		add(limExpInstances);
		JLabel label = new JLabel(
				"<html><h4>Double click costs on table to change their values. Use only non-negative integers.</h4></html>");
		label.setAlignmentX(SwingConstants.LEFT);
		label.setMinimumSize(new Dimension(300,20));
		add(label);
	}

	public void populateCostPanel(PetrinetGraph net, XLog log, TransEvClassMapping mapping) {
		generateStandardGUI();
		
		populateMoveOnModelPanel(net, log);
		populateSetAllButton(String.valueOf(DEFCOSTMOVEONMODEL), tableModel);

		populateMoveOnLogPanel(log, mapping);
		populateSetAllButton(String.valueOf(DEFCOSTMOVEONLOG), evClassTableModel);
	}

	protected void populateSetAllButton(String defaultCost, final DefaultTableModel tableModel) {
		SlickerFactory factory = SlickerFactory.instance();

		final ProMTextField textField = new ProMTextField(defaultCost);
		textField.setMaximumSize(new Dimension(70, 20));
		textField.setPreferredSize(new Dimension(70, 20));
		JButton setButton = factory.createButton("Set");
		setButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					int cost = Integer.parseInt(textField.getText().trim());
					if (cost >= 0) {
						for (int i = 0; i < tableModel.getRowCount(); i++) {
							tableModel.setValueAt(cost, i, 1);
						}
					}
				} catch (Exception exc) {
					// no action is performed
				}
			}
		});

		JPanel bgPanel = new JPanel();
		bgPanel.setBackground(new Color(150, 150, 150));
		bgPanel.add(factory.createLabel("Set all costs above to "));
		bgPanel.add(textField);
		bgPanel.add(setButton);
		add(bgPanel);
	}

	/**
	 * Generate move on model panel
	 * 
	 * @param net
	 * @param log
	 */
	protected void populateMoveOnModelPanel(PetrinetGraph net, final XLog log) {
		// create table to map move on model cost
		Collection<Transition> transitions = net.getTransitions();
		Object[][] tableContent = new Object[transitions.size()][2];
		int rowCounter = 0;
		for (Transition trans : transitions) {
			if (trans.isInvisible()) {
				tableContent[rowCounter] = new Object[] { trans.getLabel(), 0 };
			} else {
				tableContent[rowCounter] = new Object[] { trans.getLabel(), DEFCOSTMOVEONMODEL };
			}
			mapTrans2RowIndex.put(trans, rowCounter);
			rowCounter++;
		}
		tableModel = new DefaultTableModel(tableContent, new Object[] { "Transition", "Move on Model Cost" }) {
			private static final long serialVersionUID = -3870068318560745604L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return (column != 0);
			}
		};
		promTable = new ProMTable(tableModel);
		add(promTable);
	}

	/**
	 * Generate move on log panel
	 * 
	 * @param log
	 * @param conn
	 */
	protected void populateMoveOnLogPanel(XLog log, TransEvClassMapping mapping) {
		// move on log cost (determined by the selection of event class in mapping)
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, mapping.getEventClassifier());
		XEventClasses eventClassesName = summary.getEventClasses();
		evClassTableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 5656621614933096102L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return (column != 0);
			}
		};
		;

		mapXEvClass2RowIndex.clear();

		// move on log cost
		Object[][] evClassTableContent = new Object[eventClassesName.size()][2];
		int evClassRowCounter = 0;
		for (XEventClass evClass : eventClassesName.getClasses()) {
			evClassTableContent[evClassRowCounter] = new Object[] { evClass, DEFCOSTMOVEONLOG };
			mapXEvClass2RowIndex.put(evClass, evClassRowCounter);
			evClassRowCounter++;
		}
		evClassTableModel.setDataVector(evClassTableContent, new Object[] { "Event Class", "Move on Log Cost" });

		promEvClassTable = new ProMTable(evClassTableModel);
		//add(promEvClassTable, "0, 4, c, t");
		add(promEvClassTable);
	}

	/**
	 * This method is not necessary, but require as we extend PNParamSettingStep
	 */
	public Object getParameterValue(int paramVariableValIndex) {
		return null;
	}

	/**
	 * Get all parameters for this algorithm
	 */
	public Object[] getAllParameters() {
		Object[] res = new Object[3];

		// create map trans to cost
		res[AllOptAlignmentsTreeAlg.MAPTRANSTOCOST] = getTransitionWeight();
		res[AllOptAlignmentsTreeAlg.MAXEXPLOREDINSTANCES] = getMaxExploredInstances();
		res[AllOptAlignmentsTreeAlg.MAPXEVENTCLASSTOCOST] = getMapEvClassToCost();
		return res;
	}

	public int getMaxExploredInstances(){
		return limExpInstances.getValue() == MAXLIMMAXNUMINSTANCES ? Integer.MAX_VALUE
				: limExpInstances.getValue() * 100;
	}
	
	/**
	 * Get map from event class to cost of move on log
	 * 
	 * @return
	 */
	public Map<XEventClass, Integer> getMapEvClassToCost() {
		Map<XEventClass, Integer> mapEvClass2Cost = new HashMap<XEventClass, Integer>();
		for (XEventClass evClass : mapXEvClass2RowIndex.keySet()) {
			int index = mapXEvClass2RowIndex.get(evClass);
			if (evClassTableModel.getValueAt(index, 1) instanceof Integer) {
				mapEvClass2Cost.put(evClass, (Integer) evClassTableModel.getValueAt(index, 1));
			} else {
				try {
					mapEvClass2Cost.put(evClass,
							Integer.parseInt(evClassTableModel.getValueAt(index, 1).toString().trim()));
				} catch (Exception exc) {
					mapEvClass2Cost.put(evClass, DEFCOSTMOVEONLOG);
				}

			}
		}
		return mapEvClass2Cost;
	}

	/**
	 * get penalty when move on model is performed
	 * 
	 * @return
	 */
	public Map<Transition, Integer> getTransitionWeight() {
		Map<Transition, Integer> costs = new HashMap<Transition, Integer>();
		for (Transition trans : mapTrans2RowIndex.keySet()) {
			int index = mapTrans2RowIndex.get(trans);
			if (tableModel.getValueAt(index, 1) instanceof Integer) {
				costs.put(trans, (Integer) tableModel.getValueAt(index, 1));
			} else { // instance of other
				try {
					costs.put(trans, Integer.parseInt(tableModel.getValueAt(index, 1).toString().trim()));
				} catch (Exception exc) {
					costs.put(trans, DEFCOSTMOVEONMODEL);
				}

			}
		}
		return costs;
	}
}
