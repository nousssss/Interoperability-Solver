package org.processmining.pnetreplayer.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.log.utils.XUtils;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.pnetreplayer.parameters.TransEvClassMappingParameter;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class TransEvClassMappingPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9222869331190598659L;

	public TransEvClassMappingPanel(Set<XEventClass> activities, final TransEvClassMappingParameter tecParameter) {

		final TransEvClassMapping map = tecParameter.getMapping();
		XEventClass invisibleActivity = XUtils.INVISIBLEACTIVITY;
		final List<Transition> transitionList = new ArrayList<Transition>();
		Set<String> labels = new HashSet<String>();
		for (Transition transition : map.keySet()) {
			if (!transition.isInvisible()) {
				if (!labels.contains(transition.getLabel())) {
					labels.add(transition.getLabel());
					transitionList.add(transition);
				}
			}
		}
//		for (Transition transition : map.keySet()) {
//			if (transition.isInvisible()) {
//				if (!labels.contains(transition.getLabel())) {
//					labels.add(transition.getLabel());
//					transitionList.add(transition);
//				}
//			}
//		}
		Collections.sort(transitionList, new Comparator<Transition>() {

			public int compare(Transition o1, Transition o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}

		});
		List<XEventClass> activityList = new ArrayList<XEventClass>(activities);
		activityList.add(invisibleActivity);
		Collections.sort(activityList);

		//		for (Transition transition : transitionList) {
		//			map.put(transition, invisibleActivity);
		//			for (XEventClass activity : activityList) {
		//				if (activity.getId().equals(transition.getLabel())) {
		//					map.put(transition, activity);
		//				}
		//			}
		//		}

		double rows[] = new double[transitionList.size() + 1];
		for (int i = 0; i <= transitionList.size(); i++) {
			rows[i] = 30;
		}
		double size[][] = { { TableLayoutConstants.FILL, TableLayoutConstants.FILL }, rows };
		setLayout(new TableLayout(size));

		setOpaque(false);

		final ProMTextField[] textFields = new ProMTextField[transitionList.size()];
		@SuppressWarnings("unchecked")
		final ProMComboBox<XEventClass>[] comboBoxes = new ProMComboBox[transitionList.size()];

		add(new JLabel("Transition"), "0, 0");
		add(new JLabel("Activity"), "1, 0");

		int i = 0;
		for (Transition transition : transitionList) {
			textFields[i] = new ProMTextField(transition.getLabel());
			textFields[i].setEditable(false);
			textFields[i].setPreferredSize(new Dimension(200, 25));
			add(textFields[i], "0, " + (i + 1));
			comboBoxes[i] = new ProMComboBox<XEventClass>(activityList);
			comboBoxes[i].setSelectedItem(map.get(transition));
			comboBoxes[i].setPreferredSize(new Dimension(200, 25));
			comboBoxes[i].addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					for (int j = 0; j < comboBoxes.length; j++) {
						Object source = e.getSource();
						ProMComboBox<XEventClass> comboBox = comboBoxes[j];
						if (comboBox.equals(source)) {
							XEventClass selected = (XEventClass) comboBoxes[j].getSelectedItem();
							Set<Transition> transitions = new HashSet<Transition>();
							for (Transition transition : map.keySet()) {
								if (!transition.isInvisible() && transition.getLabel().equals(transitionList.get(j).getLabel())) {
									transitions.add(transition);
								}
							}
							for (Transition transition : transitions) {
								map.put(transition, selected);
							}
						}
					}

				}

			});
			add(comboBoxes[i], "1, " + (i + 1));
			i++;
		}
	}
}
