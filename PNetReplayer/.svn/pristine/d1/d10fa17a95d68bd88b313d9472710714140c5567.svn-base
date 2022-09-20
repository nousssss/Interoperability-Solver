/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.swapping;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.util.Pair;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteUI;

import com.fluxicon.slickerbox.components.SlickerTabbedPane;
import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;

/**
 * @author aadrians Sep 13, 2012
 * 
 */
public class CostBasedCompleteSwapUI extends JComponent {
	private static final long serialVersionUID = 4834937564806238212L;

	protected CostBasedCompleteUI costBasedCompleteUI;
	protected CostSwapReplaceUI replaceUI;
	protected CostSwapReplaceUI swapUI;

	protected Color bgCol = new Color(200, 200, 200);

	public CostBasedCompleteSwapUI(Collection<Transition> transCol, Collection<XEventClass> evClassCol,
			Collection<XEventClass> evClassColWODummy, CostBasedSwapParam param) {
		// utilities
		SlickerFactory factory = SlickerFactory.instance();

		// cost based UI
		if (param != null) {
			costBasedCompleteUI = new CostBasedCompleteUI(transCol, evClassCol, param.getMapTrans2Cost(),
					param.getMapSync2Cost(), param.getMapEvClass2Cost());
		} else {
			costBasedCompleteUI = new CostBasedCompleteUI(transCol, evClassCol);
		}
		replaceUI = new CostSwapReplaceUI(
				evClassColWODummy,
				param != null ? param.getReplacementCostMap() : null,
				"Replacement Cost",
				"Set the cost of replacing one event class with another in synchronous moves. Use only non-negative integers.",
				"Event Class", "Replaced With");
		swapUI = new CostSwapReplaceUI(
				evClassColWODummy,
				param != null ? param.getSwapCostMap() : null,
				"Swapping Cost",
				"If swapping cost between two event classes is not defined, swapping is not allowed. Use only non-negative integers.",
				"Event Class", "Swapped with");

		// use tabbing
		SlickerTabbedPane tabPane = factory.createTabbedPane("Replay Parameter", new Color(200, 200, 200), new Color(0,
				0, 0, 230), Color.BLACK);
		tabPane.addTab("General Settings", costBasedCompleteUI);
		tabPane.addTab("Replacement Cost", replaceUI);
		tabPane.addTab("Swapping Cost", swapUI);

		// set coloring
		costBasedCompleteUI.setBackground(bgCol);
		replaceUI.setBackground(bgCol);
		swapUI.setBackground(bgCol);

		setLayout(new BorderLayout());
		add(tabPane, BorderLayout.CENTER);
	}

	public Map<XEventClass, Integer> getMapEvClassToCost() {
		return costBasedCompleteUI.getMapEvClassToCost();
	}

	public Map<Transition, Integer> getMapTransToCost() {
		return costBasedCompleteUI.getTransitionWeight();
	}

	public Map<XEventClass, List<Pair<XEventClass, Integer>>> getSwapCost() {
		return swapUI.getCost();
	}

	public Map<XEventClass, List<Pair<XEventClass, Integer>>> getReplacementCost() {
		return replaceUI.getCost();
	}

	public int getMaxNumOfStates() {
		return costBasedCompleteUI.getMaxNumOfStates();
	}

	public Map<Transition, Integer> getMapSyncToCost() {
		return costBasedCompleteUI.getSyncCost();
	}

}

class CostSwapReplaceUI extends JPanel {
	private static final long serialVersionUID = 3371242042809099939L;
	private Object[] selections; // index 0 is null
	private List<PairEventClassPanel> lstPanel = new LinkedList<PairEventClassPanel>();

	private double[][] rowSize = new double[][] { { 270, 5, 270, 5, 100, 40, 40, 10 }, { 30 } };

	public Map<XEventClass, List<Pair<XEventClass, Integer>>> getCost() {
		Map<XEventClass, List<Pair<XEventClass, Integer>>> res = new HashMap<XEventClass, List<Pair<XEventClass, Integer>>>();
		forLoop: for (PairEventClassPanel pnl : lstPanel) {
			if (pnl.getFirstItem() != null) {
				List<Pair<XEventClass, Integer>> lst = res.get(pnl.getFirstItem());
				if (lst == null) {
					lst = new LinkedList<Pair<XEventClass, Integer>>();
					res.put(pnl.getFirstItem(), lst);
				}

				// check if similar item exists
				for (Pair<XEventClass, Integer> p : lst) {
					if (p.getFirst().equals(pnl.getSecondItem())) {
						continue forLoop;
					}
				}

				// the replacement is unique
				lst.add(new Pair<XEventClass, Integer>(pnl.getSecondItem(), pnl.getCost()));
			}
		}
		return res;
	}

	public CostSwapReplaceUI(Collection<XEventClass> evClassCol,
			Map<XEventClass, List<Pair<XEventClass, Integer>>> map, String title, String extraInfo, String firstLabel,
			String secondLabel) {
		// util
		SlickerFactory factory = SlickerFactory.instance();
		Color bgColor = new Color(200, 200, 200);

		// init selections
		List<XEventClass> list = new ArrayList<XEventClass>(evClassCol);
		Collections.sort(list, new Comparator<XEventClass>() {
			public int compare(XEventClass o1, XEventClass o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});

		selections = new Object[list.size() + 1];
		selections[0] = "[NONE]";
		Object[] arr = list.toArray();
		System.arraycopy(arr, 0, selections, 1, arr.length);

		// create cost for future reference
		final ProMTextField allCost = new ProMTextField();
		allCost.setText("1");
		allCost.setPreferredSize(new Dimension(60, 30));

		// create layout and button
		final TableLayout costPanelLayout = new TableLayout(rowSize);
		final JPanel costPanel = new JPanel();
		costPanel.setBackground(bgColor);
		costPanel.setLayout(costPanelLayout);

		final JButton addButton = factory.createButton("+");
		final JButton removeButton = factory.createButton("-");

		addButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				costPanel.invalidate();
				// create new panel
				PairEventClassPanel newPanel = new PairEventClassPanel(selections, rowSize, readCost(allCost.getText()));
				lstPanel.add(newPanel);

				// add layout
				costPanelLayout.insertRow(costPanelLayout.getNumRow(), 30.0);
				costPanel.add(newPanel, "0," + (costPanelLayout.getNumRow() - 1) + ",4,"
						+ (costPanelLayout.getNumRow() - 1));

				// add remove button if necessary
				if (lstPanel.size() == 2) {
					costPanel.add(removeButton, "6,0");
				}
				costPanel.revalidate();
			}

		});

		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				costPanel.invalidate();
				// remove layout and panel
				lstPanel.remove(lstPanel.size() - 1);
				costPanelLayout.deleteRow(costPanelLayout.getNumRow() - 1);

				// hide its existence
				if (lstPanel.size() == 1) {
					costPanelLayout.removeLayoutComponent(removeButton);
				}
				costPanel.revalidate();
			}
		});

		// add default cost
		if (map != null) {
			costPanel.invalidate();
			costPanel.add(removeButton, "6,0");
			for (Entry<XEventClass, List<Pair<XEventClass, Integer>>> entry : map.entrySet()) {
				for (Pair<XEventClass, Integer> pair : entry.getValue()) {
					// create new panel
					PairEventClassPanel newPanel = new PairEventClassPanel(selections, rowSize, pair.getSecond(),
							entry.getKey(), pair.getFirst());
					lstPanel.add(newPanel);

					// add new row if necessary
					if (lstPanel.size() > 1) {
						costPanelLayout.insertRow(costPanelLayout.getNumRow(), 30.0);
					}

					// add layout
					costPanel.add(newPanel,
							"0," + (costPanelLayout.getNumRow() - 1) + ",4," + (costPanelLayout.getNumRow() - 1));

				}
			}
			costPanel.revalidate();
		} else {
			// add first component
			PairEventClassPanel firstItem = new PairEventClassPanel(selections, rowSize, readCost(allCost.getText()));
			lstPanel.add(firstItem);

			costPanel.add(firstItem, "0," + (costPanelLayout.getNumRow() - 1) + ",4,"
					+ (costPanelLayout.getNumRow() - 1));
		}
		costPanel.add(addButton, "5,0");

		// global setting panel
		JButton genAllCombination = factory.createButton("Add all pairs of event class");
		genAllCombination.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (selections.length > 2) {
					if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(new JPanel(), "Are you sure? "
							+ (((selections.length - 1) * (selections.length - 2)))
							+ " item(s) are going to be generated.")) {
						// remove existing ones
						costPanel.invalidate();
						while (costPanelLayout.getNumRow() > 0) {
							costPanelLayout.deleteRow(0);
						}

						lstPanel.clear();

						for (int i = 1; i < selections.length - 1; i++) {
							for (int j = i + 1; j < selections.length; j++) {
								// pair the two
								PairEventClassPanel item = new PairEventClassPanel(selections, rowSize,
										readCost(allCost.getText()));
								lstPanel.add(item);
								item.setFirstItemIndex(i);
								item.setSecondItemIndex(j);

								costPanelLayout.insertRow(costPanelLayout.getNumRow(), 30.0);
								costPanel.add(item,
										"0," + (costPanelLayout.getNumRow() - 1) + ",4,"
												+ (costPanelLayout.getNumRow() - 1));
								costPanelLayout.insertRow(costPanelLayout.getNumRow(), 30.0);

								PairEventClassPanel item2 = new PairEventClassPanel(selections, rowSize,
										readCost(allCost.getText()));
								lstPanel.add(item2);
								item2.setFirstItemIndex(j);
								item2.setSecondItemIndex(i);

								costPanel.add(item2, "0," + (costPanelLayout.getNumRow() - 1) + ",4,"
										+ (costPanelLayout.getNumRow() - 1));
							}
						}
						costPanel.add(addButton, "5,0");
						if (lstPanel.size() > 1) {
							costPanel.add(removeButton, "6,0");
						}
						costPanel.revalidate();
					}
				}
			}
		});

		JButton resetAllCombination = factory.createButton("Remove all pairs");
		resetAllCombination.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				costPanel.invalidate();
				lstPanel.clear();
				while (costPanelLayout.getNumRow() > 0) {
					costPanelLayout.deleteRow(0);
				}
				PairEventClassPanel firstItem = new PairEventClassPanel(selections, rowSize,
						readCost(allCost.getText()));
				lstPanel.add(firstItem);

				costPanelLayout.insertRow(costPanelLayout.getNumRow(), 30.0);
				costPanel.add(firstItem, "0,0,4,0");
				costPanel.add(addButton, "5,0");

				costPanel.revalidate();
			}
		});

		// cost setting for all
		JButton setAllCost = factory.createButton("Set");
		setAllCost.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
					int cost = Integer.parseInt(allCost.getText());
					if (cost < 0) {
						JOptionPane.showMessageDialog(new JPanel(),
								"Invalid cost. Set cost to non-negative integer value.");
					} else {
						for (PairEventClassPanel p : lstPanel) {
							p.setCost(cost);
						}
					}
				} catch (NumberFormatException exc) {
					JOptionPane
							.showMessageDialog(new JPanel(), "Invalid cost. Set cost to non-negative integer value.");
				}

			}
		});

		JPanel globalPnl = new JPanel();
		globalPnl.setLayout(new BoxLayout(globalPnl, BoxLayout.X_AXIS));
		globalPnl.add(genAllCombination);
		globalPnl.add(resetAllCombination);
		globalPnl.add(Box.createRigidArea(new Dimension(200, 10)));
		globalPnl.add(Box.createHorizontalGlue());
		globalPnl.add(factory.createLabel("Set all costs to "));
		globalPnl.add(allCost);
		globalPnl.add(setAllCost);
		globalPnl.setBackground(bgColor);

		// set layout
		TableLayout genLayout = new TableLayout(new double[][] { { 270, 5, 270, 5, 100, 40, 60 },
				{ 80, 30, TableLayout.FILL, 30 } });
		setLayout(genLayout);

		// add components
		add(factory.createLabel("<html><h1>" + title + "</h1><p>" + extraInfo + "</p></html>"), "0,0,6,0,l,t");
		add(factory.createLabel(firstLabel), "0,1,1,1,l,c");
		add(factory.createLabel(secondLabel), "2,1,3,1,l,c");
		add(factory.createLabel("Cost"), "4,1,l,c");
		JScrollPane scrollPane = new JScrollPane(costPanel);
		scrollPane.setOpaque(true);
		scrollPane.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		scrollPane.getViewport().setOpaque(true);
		scrollPane.getViewport().setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollBar vBar = scrollPane.getVerticalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		vBar.setOpaque(true);
		vBar.setBackground(WidgetColors.PROPERTIES_BACKGROUND);

		add(scrollPane, "0,2,6,2");
		add(globalPnl, "0,3,6,3");
	}

	private int readCost(String text) {
		int cost;
		try {
			cost = Integer.parseInt(text);
			assert (cost >= 0);
		} catch (Exception exc) {
			cost = 1;
		}
		return cost;
	}
}

class PairEventClassPanel extends JPanel {
	private static final long serialVersionUID = -8742435383328217213L;

	private JComboBox first;
	private JComboBox second;
	private ProMTextField cost;

	private static Dimension dimension = new Dimension(270, 30);
	private static Color bgColor = new Color(200, 200, 200);

	public PairEventClassPanel(Object[] evClassCol, double[][] size, int defaultCost) {
		this(evClassCol, size, defaultCost, null, null);
	}

	public PairEventClassPanel(Object[] evClassCol, double[][] size, int defaultCost, Object defaultLeft,
			Object defaultRight) {
		first = SlickerFactory.instance().createComboBox(evClassCol);
		if (defaultLeft != null) {
			first.setSelectedItem(defaultLeft);
		} else {
			first.setSelectedIndex(0);
		}
		first.setPreferredSize(dimension);
		first.setMinimumSize(dimension);
		second = SlickerFactory.instance().createComboBox(evClassCol);
		if (defaultRight != null) {
			second.setSelectedItem(defaultRight);
		} else {
			second.setSelectedIndex(0);
		}
		second.setPreferredSize(dimension);
		second.setMinimumSize(dimension);
		cost = new ProMTextField();
		cost.setText("" + defaultCost);

		// set layout
		setLayout(new TableLayout(size));

		add(first, "0,0,l,c");
		add(second, "2,0,l,c");
		add(cost, "4,0,l,c");

		setBackground(bgColor);
	}

	public int getCost() {
		try {
			return Integer.parseInt(cost.getText());
		} catch (Exception exc) {
			return 0;
		}
	}

	public XEventClass getFirstItem() {
		if (first.getSelectedIndex() > 0) {
			return (XEventClass) first.getSelectedItem();
		}
		return null;
	}

	public XEventClass getSecondItem() {
		if (second.getSelectedIndex() > 0) {
			return (XEventClass) second.getSelectedItem();
		}
		return null;
	}

	public void setCost(int costInt) {
		cost.setText(String.valueOf(costInt));
	}

	public void setFirstItem(XEventClass firstItem) {
		first.setSelectedItem(firstItem);
	}

	public void setSecondItem(XEventClass secondItem) {
		second.setSelectedItem(secondItem);
	}

	public void setFirstItemIndex(int index) {
		first.setSelectedIndex(index);
	}

	public void setSecondItemIndex(int index) {
		second.setSelectedIndex(index);
	}

}