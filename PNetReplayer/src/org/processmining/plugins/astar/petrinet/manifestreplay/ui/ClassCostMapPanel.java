/**
 * 
 */
package org.processmining.plugins.astar.petrinet.manifestreplay.ui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClass;

import com.fluxicon.slickerbox.components.NiceIntegerSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * @author aadrians Feb 29, 2012
 * 
 */
public class ClassCostMapPanel extends JPanel {
	private static final long serialVersionUID = 3863831601126672322L;

	private TransClass[] transClasses;
	private XEventClass[] evClasses;

	// default value 
	private static final int DEFCOSTMOVEONLOG = 1;
	private static final int DEFCOSTMOVEONMODEL = 1;
	private static final int MAXLIMMAXNUMINSTANCES = 10001;
	private static final int DEFLIMMAXNUMINSTANCES = 2000;

	// parameter-related GUI
	private NiceIntegerSlider limExpInstances;
	private DefaultTableModel tableModel = null;
	private ProMTable promTable;

	private DefaultTableModel evClassTableModel = null;
	private ProMTable promEvClassTable;

	private DefaultTableModel tableSyncModel = null;
	private ProMTable promSyncTable;

	public ClassCostMapPanel(Collection<TransClass> transClassCol, Collection<XEventClass> evClassesCol) {
		double size[][] = { { TableLayoutConstants.FILL },
				{ 40, TableLayoutConstants.FILL, 35, TableLayoutConstants.FILL, 30, TableLayoutConstants.FILL, 30 } };
		setLayout(new TableLayout(size));

		// label
		SlickerFactory slickerFactoryInstance = SlickerFactory.instance();

		// max instance
		limExpInstances = slickerFactoryInstance.createNiceIntegerSlider(
				"<html><h4># Maximum explored states (in hundreds). Set max for unlimited.</h4></html>", 1,
				MAXLIMMAXNUMINSTANCES, DEFLIMMAXNUMINSTANCES, Orientation.HORIZONTAL);
		limExpInstances.setPreferredSize(new Dimension(700, 20));
		limExpInstances.setMaximumSize(new Dimension(700, 20));
		limExpInstances.setMinimumSize(new Dimension(700, 20));

		add(limExpInstances, "0, 0, c, t");

		// store transition classess
		this.transClasses = transClassCol.toArray(new TransClass[transClassCol.size()]);

		populateMoveOnModelPanel(transClassCol);
		populateSetAllButton(1, tableModel, "0, 2, c, t");

		populateMoveOnLogPanel(evClassesCol);
		populateSetAllButton(1, evClassTableModel, "0, 4, c, t");

		populateMoveSyncPanel(transClassCol);
		populateSetAllButton(0, tableSyncModel, "0, 6, c, t");

	}

	private void populateMoveOnModelPanel(Collection<TransClass> transClassCol) {
		// create table to map move on model cost
		// some transitions are by default invisible
		String invisibleTransitionRegEx = "[a-z][0-9]+|(tr[0-9]+)|(silent)|(tau)|(skip)|(invi)";
		Pattern pattern = Pattern.compile(invisibleTransitionRegEx);

		Object[][] tableContent = new Object[transClasses.length][2];
		for (int i = 0; i < transClasses.length; i++) {
			String transClass = transClasses[i].getId();

			Matcher matcher = pattern.matcher(transClass.toLowerCase());
			if (matcher.find() && matcher.start() == 0) {
				tableContent[i] = new Object[] { transClasses[i].getId(), 0 };
			} else {
				tableContent[i] = new Object[] { transClasses[i].getId(), DEFCOSTMOVEONMODEL };
			}

		}
		tableModel = new DefaultTableModel(tableContent, new Object[] { "Transition Class", "Move on Model Cost" }) {
			private static final long serialVersionUID = -6019224467802441949L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return (column != 0);
			};
		};
		promTable = new ProMTable(tableModel);
		promTable.setPreferredSize(new Dimension(700, 200));
		promTable.setMinimumSize(new Dimension(700, 200));
		add(promTable, "0, 1, c, t");
	}
	

	private void populateMoveSyncPanel(Collection<TransClass> transClassCol) {
		// create table to map move on model cost
		Object[][] tableContent = new Object[transClasses.length][2];
		for (int i = 0; i < transClasses.length; i++) {
			tableContent[i] = new Object[] { transClasses[i].getId(), 0 };
		}
		tableSyncModel = new DefaultTableModel(tableContent, new Object[] { "Transition Class", "Move Synchronous Cost" }) {
			private static final long serialVersionUID = -6019224467802441949L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return (column != 0);
			};
		};
		promSyncTable = new ProMTable(tableSyncModel);
		promSyncTable.setPreferredSize(new Dimension(700, 200));
		promSyncTable.setMinimumSize(new Dimension(700, 200));
		add(promSyncTable, "0, 5, c, t");
	}

	private void populateSetAllButton(final int defaultCost, final DefaultTableModel tableModel, String addLocation) {
		SlickerFactory factory = SlickerFactory.instance();

		final ProMTextField textField = new ProMTextField();
		textField.setText(String.valueOf(defaultCost));
		textField.setMaximumSize(new Dimension(70, 20));
		//		textField.setMinimumSize(new Dimension(70,25));
		textField.setPreferredSize(new Dimension(70, 20));
		JButton setButton = factory.createButton("Set");
		setButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					int cost = Integer.valueOf(textField.getText());
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
		add(bgPanel, addLocation);
	}

	/**
	 * Generate event class move on log panel
	 * 
	 * @param eventClassesName
	 */
	private void populateMoveOnLogPanel(Collection<XEventClass> eventClassesName) {
		evClasses = eventClassesName.toArray(new XEventClass[eventClassesName.size()]);

		// move on log cost (determined by the selection of event class in mapping)
		evClassTableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 5238526181467190856L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return (column != 0);
			};
		};

		// move on log cost
		Object[][] evClassTableContent = new Object[evClasses.length][2];
		for (int i = 0; i < evClasses.length; i++) {
			evClassTableContent[i] = new Object[] { evClasses[i], DEFCOSTMOVEONLOG };
		}

		evClassTableModel.setDataVector(evClassTableContent, new Object[] { "Event Class", "Move on Log Cost" });

		promEvClassTable = new ProMTable(evClassTableModel);
		promEvClassTable.setPreferredSize(new Dimension(700, 200));
		promEvClassTable.setMinimumSize(new Dimension(700, 200));
		add(promEvClassTable, "0, 3, c, t");
	}

	public Map<XEventClass, Integer> getMapEvClassToCost() {
		Map<XEventClass, Integer> mapEvClass2Cost = new HashMap<XEventClass, Integer>();
		for (int index = 0; index < this.evClasses.length; index++) {
			if (evClassTableModel.getValueAt(index, 1) instanceof Integer) {
				mapEvClass2Cost.put(evClasses[index], (Integer) evClassTableModel.getValueAt(index, 1));
			} else {
				try {
					mapEvClass2Cost.put(evClasses[index],
							Integer.parseInt(evClassTableModel.getValueAt(index, 1).toString().trim()));
				} catch (Exception exc) {
					mapEvClass2Cost.put(evClasses[index], DEFCOSTMOVEONLOG);
				}

			}
		}
		return mapEvClass2Cost;
	}

	public Map<TransClass, Integer> getMapTransClassToCost() {
		Map<TransClass, Integer> costs = new HashMap<TransClass, Integer>();
		for (int index = 0; index < transClasses.length; index++) {
			if (tableModel.getValueAt(index, 1) instanceof Integer) {
				costs.put(transClasses[index], (Integer) tableModel.getValueAt(index, 1));
			} else { // instance of other
				try {
					costs.put(transClasses[index], Integer.parseInt(tableModel.getValueAt(index, 1).toString().trim()));
				} catch (Exception exc) {
					costs.put(transClasses[index], DEFCOSTMOVEONMODEL);
				}
			}
		}
		return costs;
	}

	public int getMaxNumOfStates() {
		return limExpInstances.getValue() == MAXLIMMAXNUMINSTANCES ? Integer.MAX_VALUE
				: limExpInstances.getValue() * 100;
	}

	public Map<TransClass, Integer> getMapTransClassSyncToCost() {
		Map<TransClass, Integer> costs = new HashMap<TransClass, Integer>();
		for (int index = 0; index < transClasses.length; index++) {
			if (tableSyncModel.getValueAt(index, 1) instanceof Integer) {
				costs.put(transClasses[index], (Integer) tableSyncModel.getValueAt(index, 1));
			} else { // instance of other
				try {
					costs.put(transClasses[index],
							Integer.parseInt(tableSyncModel.getValueAt(index, 1).toString().trim()));
				} catch (Exception exc) {
					costs.put(transClasses[index], 0);
				}
			}
		}
		return costs;
	}

}
