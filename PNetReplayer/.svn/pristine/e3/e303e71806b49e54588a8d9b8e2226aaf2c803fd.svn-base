package org.processmining.plugins.petrinet.replayer.algorithms.behavapp;

import info.clearthought.layout.TableLayout;

import java.awt.Dimension;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.util.ui.widgets.ProMTable;

import com.fluxicon.slickerbox.components.NiceIntegerSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class BehavAppUI extends JComponent {

	private static final long serialVersionUID = -6104289998585264084L;

	// default constants
	private static int MAXLIMMAXNUMINSTANCES = 10001;
	private static int DEFLIMMAXNUMINSTANCES = 2000;

	// GUI settings
	private NiceIntegerSlider maxNumStates;
	private JCheckBox useLogWeight;
	private JCheckBox useTransitionWeight;

	// Pointers to transition weighting
	private Map<XEventClass, Integer> mapXEventClass2RowIndex = null;
	private DefaultTableModel tableModel = null;

	public BehavAppUI(Collection<XEventClass> evClassCol) {
		initComponentsWithoutTransMapping();
		populateActivityWeight(evClassCol);
	}

	private void initComponentsWithoutTransMapping() {
		SlickerFactory factory = SlickerFactory.instance();

		double size[][];
		size = new double[][] { { 750 }, { 60, 30, 30, 30, TableLayout.FILL } };

		setLayout(new TableLayout(size));

		add(factory.createLabel("<html><h1>Configure replay</h1></html>"), "0,0,l,t");

		maxNumStates = factory.createNiceIntegerSlider(
				"<html><h4># Maximum explored states (in hundreds). Set max for unlimited.</h4></html>", 1,
				MAXLIMMAXNUMINSTANCES, DEFLIMMAXNUMINSTANCES, Orientation.HORIZONTAL);
		maxNumStates.setPreferredSize(new Dimension(700, 20));
		maxNumStates.setMaximumSize(new Dimension(750, 20));
		maxNumStates.setMinimumSize(new Dimension(750, 20));
		add(maxNumStates, "0,1,l,t");

		useLogWeight = factory.createCheckBox("Weigh event class discrepancies in log based on frequency", true);
		add(useLogWeight, "0,2,l,t");

		useTransitionWeight = factory.createCheckBox("Weigh event class discrepancies in process model according to table", true);
		add(useTransitionWeight, "0,3,l,t");
	}
	
//	public Object[] getAllParameters() {
//		return new Object[] {
//				maxNumStates.getValue() == MAXLIMMAXNUMINSTANCES ? Integer.MAX_VALUE : maxNumStates.getValue() * 100,
//				useLogWeight.isSelected(), 
//				useTransitionWeight.isSelected() ? getTransitionWeight() : null, 
//				this.log };
//	}

	private Map<XEventClass, Integer> getTransitionWeight() {
		Map<XEventClass, Integer> costs = new HashMap<XEventClass, Integer>();
		for (XEventClass evClass : mapXEventClass2RowIndex.keySet()) {
			int index = mapXEventClass2RowIndex.get(evClass);
			if (tableModel.getValueAt(index, 1) instanceof Integer) {
				costs.put(evClass, (Integer) tableModel.getValueAt(index, 1));
			} else { // instance of other
				costs.put(evClass, Integer.parseInt(tableModel.getValueAt(index, 1).toString()));
			}
		}
		return costs;
	}

	public void populateActivityWeight(Collection<XEventClass> eventClassesCol) {
		mapXEventClass2RowIndex = new HashMap<XEventClass, Integer>();

		int size = eventClassesCol.size();
		
		Object[][] tableContent = new Object[size][2];
		int rowCounter = 0;
		for (XEventClass evClass : eventClassesCol) {
			tableContent[rowCounter] = new Object[] { evClass.getId(), "1" };
			mapXEventClass2RowIndex.put(evClass, rowCounter);
			rowCounter++;
		}
		tableModel = new DefaultTableModel(tableContent, new Object[] { "Event Class", "Weight" });

		ProMTable promTable = new ProMTable(tableModel);
		promTable.setPreferredSize(new Dimension(750, 270));
		add(promTable, "0,4,c,t");
	}

	public Integer getMaxNumStates() {
		return maxNumStates.getValue() == MAXLIMMAXNUMINSTANCES ? Integer.MAX_VALUE : maxNumStates.getValue() * 100;
	}

	public Boolean isUseLogWeight() {
		return useLogWeight.isSelected();
	}

	public boolean isUseModelWeight() {
		return useTransitionWeight.isSelected();
	}

	public Map<XEventClass, Integer> getModelWeight() {
		return getTransitionWeight();
	}
	
	
}
