/**
 * 
 */
package org.processmining.plugins.connectionfactories.logpetrinet;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.ArrayUtils;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * GUI to map event class (with any classifiers) to transitions of Petri net
 * 
 * @author aadrians
 * @author F. Mannhardt - deactivate approximate matches
 * 
 */
public class EvClassLogPetrinetConnectionFactoryUI extends JPanel {
	
	private static final long serialVersionUID = -699953189980632566L;

	// dummy event class (for unmapped transitions)
	public final static XEventClass DUMMY = new XEventClass("DUMMY", -1) {
	
		public boolean equals(Object o) {
			return this == o;
		}

		public int hashCode() {
			return System.identityHashCode(this);
		}
		
	};

	private Map<Transition, JComboBox> mapTrans2ComboBox = new HashMap<Transition, JComboBox>();
	private JComboBox classifierSelectionCbBox;
	private JCheckBox useApproximateMatchingChkBox;

	public EvClassLogPetrinetConnectionFactoryUI(final XLog log, final PetrinetGraph net, Object[] availableClassifier) {
		super();

		// index for row
		int rowCounter = 0;

		// swing factory
		SlickerFactory factory = SlickerFactory.instance();

		// set layout
		double size[][] = { { TableLayoutConstants.FILL, TableLayoutConstants.FILL }, { 80, 70 } };
		TableLayout layout = new TableLayout(size);
		setLayout(layout);
		setOpaque(false);

		// label
		add(factory.createLabel(
				"<html><h1>Map Transitions to Event Classes</h1><p>First, select an appropriate classifier. "
				+ "Unmapped transitions will be mapped to a dummy event class.<BR/>"
				+ "Approximate matches are highlighted in yellow and should be carefully reviewed.</p></html>"),
				"0, " + rowCounter + ", 1, " + rowCounter);
		rowCounter++;


		final String invisibleTransitionRegEx = "[a-z][0-9]+|(tr[0-9]+)|(silent)|(tau)|(skip)|(invisible)";
		final Pattern pattern = Pattern.compile(invisibleTransitionRegEx);

		//checkbox selection
		useApproximateMatchingChkBox = factory.createCheckBox("Use Approximate Matches", true);
		useApproximateMatchingChkBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				refresh(log, pattern, useApproximateMatchingChkBox.getModel().isSelected());
			}

		});
		useApproximateMatchingChkBox.setMinimumSize(new Dimension(350, 30));
		useApproximateMatchingChkBox.setPreferredSize(new Dimension(350, 30));
		
		// add classifier selection
		classifierSelectionCbBox = factory.createComboBox(availableClassifier);
		classifierSelectionCbBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh(log, pattern, useApproximateMatchingChkBox.getModel().isSelected());
			}
		});
		classifierSelectionCbBox.setSelectedIndex(0);
		classifierSelectionCbBox.setPreferredSize(new Dimension(350, 30));
		classifierSelectionCbBox.setMinimumSize(new Dimension(350, 30));

		add(factory.createLabel("Choose classifier"), "0, " + rowCounter + ", l, c");
		add(classifierSelectionCbBox, "1, " + rowCounter + ", l, c");
		rowCounter++;
		layout.insertRow(rowCounter, 30);
		add(factory.createLabel("Use approximate matching"), "0, " + rowCounter + ", l, c");
		add(useApproximateMatchingChkBox, "1, " + rowCounter + ", l, c");
		rowCounter++;

		// add mapping between transitions and selected event class 
		Object[] boxOptions = extractEventClasses(log, (XEventClassifier) classifierSelectionCbBox.getSelectedItem());
		List<Transition> listTrans = new ArrayList<Transition>(net.getTransitions());
		Collections.sort(listTrans, new Comparator<Transition>() {
			public int compare(Transition o1, Transition o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		});
		for (Transition transition : listTrans) {
			layout.insertRow(rowCounter, 30);
			JComboBox cbBox = new ProMComboBox(boxOptions);
			cbBox.setPreferredSize(new Dimension(350, 30));
			cbBox.setMinimumSize(new Dimension(350, 30));
			mapTrans2ComboBox.put(transition, cbBox);
			if (transition.isInvisible()) {
				cbBox.setSelectedItem(DUMMY);
			} else {
				cbBox.setSelectedIndex(preSelectOption(transition, boxOptions, pattern,
						useApproximateMatchingChkBox.getModel().isSelected()));
			}

			add(factory.createLabel(transition.getLabel()), "0, " + rowCounter + ", l, c");
			add(cbBox, "1, " + rowCounter + ", l, c");
			rowCounter++;
		}

	}
	
	private void refresh(final XLog log, final Pattern pattern, boolean useApproximateMatching) {
		Object[] boxOptions = extractEventClasses(log, (XEventClassifier) classifierSelectionCbBox.getSelectedItem());

		for (Transition transition : mapTrans2ComboBox.keySet()) {
			JComboBox cbBox = mapTrans2ComboBox.get(transition);
			cbBox.removeAllItems(); // remove all items

			for (Object item : boxOptions) {
				cbBox.addItem(item);
			}
			if (!transition.isInvisible()) {
				cbBox.setSelectedIndex(preSelectOption(transition, boxOptions, pattern, useApproximateMatching));
			} else {
				cbBox.setSelectedItem(DUMMY);
			}
		}
	}

	/**
	 * get all available event classes using the selected classifier, add with
	 * NONE
	 * 
	 * @param log
	 * @param selectedItem
	 * @return
	 */
	private Object[] extractEventClasses(XLog log, XEventClassifier selectedItem) {
		XLogInfo summary = XLogInfoFactory.createLogInfo(log,
				(XEventClassifier) classifierSelectionCbBox.getSelectedItem());
		XEventClasses eventClasses = summary.getEventClasses();

		// sort event class
		Collection<XEventClass> classes = eventClasses.getClasses();

		// create possible event classes
		Object[] arrEvClass = classes.toArray();
		Arrays.sort(arrEvClass);
		Object[] notMappedAct = { "NONE" };
		Object[] boxOptions = ArrayUtils.concatAll(notMappedAct, arrEvClass);

		return boxOptions;
	}

	/**
	 * Returns the Event Option Box index of the most similar event for the
	 * transition.
	 * 
	 * @param transition
	 *            Name of the transitions, assuming low cases
	 * @param events
	 *            Array with the options for this transition
	 * @param approximateMatch
	 * 			  Matching transitions and events approximately
	 * @return Index of option more similar to the transition
	 */
	private int preSelectOption(Transition transition, Object[] events, Pattern pattern, boolean approximateMatch) {
		String transitionLabel = transition.getLabel().toLowerCase();

		mapTrans2ComboBox.get(transition).setForeground(WidgetColors.COLOR_LIST_FG);
		mapTrans2ComboBox.get(transition).setBackground(WidgetColors.COLOR_LIST_BG);

		// try to find precise match
		for (int i = 1; i < events.length; i++) {
			String event = ((XEventClass) events[i]).toString().toLowerCase();
			if (event.equalsIgnoreCase(transitionLabel)) {
				return i;
			}
			;
		}

		Matcher matcher = pattern.matcher(transitionLabel);
		if (matcher.find() && matcher.start() == 0) {
			return 0;
		}

		if (approximateMatch) {
			//The metric to get the similarity between strings
			AbstractStringMetric metric = new Levenshtein();

			int index = 0;
			float simOld = Float.MIN_VALUE;
			for (int i = 1; i < events.length; i++) {
				String event = ((XEventClass) events[i]).toString().toLowerCase();

				if (transitionLabel.startsWith(event)) {
					index = i;
					break;
				}

				float sim = metric.getSimilarity(transitionLabel, event);
				if (simOld < sim) {
					simOld = sim;
					index = i;
				}

			}

			mapTrans2ComboBox.get(transition).setForeground(Color.YELLOW);

			return index;
		} else {
			return 0;
		}
	}

	/**
	 * Generate the map between Transitions and Event according to the user
	 * selection.
	 * 
	 * @return Map between Transitions and Events.
	 */
	public TransEvClassMapping getMap() {
		TransEvClassMapping map = new TransEvClassMapping(
				(XEventClassifier) this.classifierSelectionCbBox.getSelectedItem(), DUMMY);
		for (Transition trans : mapTrans2ComboBox.keySet()) {
			Object selectedValue = mapTrans2ComboBox.get(trans).getSelectedItem();
			if (selectedValue instanceof XEventClass) {
				// a real event class
				map.put(trans, (XEventClass) selectedValue);
			} else {
				// this is "NONE"
				map.put(trans, DUMMY);
			}
		}
		return map;
	}

	/**
	 * Get the selected classifier
	 * 
	 * @return
	 */
	public XEventClassifier getSelectedClassifier() {
		return (XEventClassifier) classifierSelectionCbBox.getSelectedItem();
	}

}
