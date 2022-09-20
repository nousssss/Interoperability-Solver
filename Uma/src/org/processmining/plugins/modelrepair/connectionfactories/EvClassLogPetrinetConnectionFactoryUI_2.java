package org.processmining.plugins.modelrepair.connectionfactories;

/**
 * 
 */
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.ArrayUtils;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.connectionfactories.logpetrinet.EvClassLogPetrinetConnectionFactoryUI;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

/**
 * GUI to map event class (with any classifiers) to transitions of Petri net
 *  
 * @author aadrians
 * 
 */
public class EvClassLogPetrinetConnectionFactoryUI_2  {
	private static final long serialVersionUID = 1L;

	// dummy event class (for unmapped transitions)
	public final static XEventClass DUMMY = EvClassLogPetrinetConnectionFactoryUI.DUMMY;

	// internal attributes
	@SuppressWarnings("unused")
	private final XLog log;
	private Map<Transition, Object> mapTrans2ComboBox = new HashMap<Transition, Object>();
	
	private Object[] boxOptions;
	private Object[] availableClassifier;
	
	public EvClassLogPetrinetConnectionFactoryUI_2(final XLog log, final PetrinetGraph net, Object[] availableClassifier) {
		super();
		
		// import variable
		this.log = log;
		this.availableClassifier = availableClassifier;

		// add mapping between transitions and selected event class 
		boxOptions = extractEventClasses(log);
		
		for (Transition transition : net.getTransitions()) {
			// by default, suggest invisible if the name of transitions:
			// 1. started with "tr"
			// 2. started with "SILENT"
			// 3. started with "tau"
			// 4. started with "invi"
			String lowCase = transition.getLabel().toLowerCase();
			if ((lowCase.startsWith("tr")) || (lowCase.startsWith("silent")) || (lowCase.startsWith("tau"))
					|| (lowCase.startsWith("invi"))) {
				transition.setInvisible(true);
			}
		}
		
		TreeSet<Transition> transitions = new TreeSet<Transition>(new Comparator<Transition>() {
			public int compare(Transition o1, Transition o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		});
		transitions.addAll(net.getTransitions());
		
		for (Transition transition : transitions){
			int choice = preSelectOption(transition.getLabel(), boxOptions);
			mapTrans2ComboBox.put(transition, boxOptions[choice]);
		}
	
	}

	
	/**
	 * get all available event classes using the selected classifier, add with NONE
	 * 
	 * @param log
	 * @return
	 */
	private Object[] extractEventClasses(XLog log) {
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, getSelectedClassifier());
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
	 *            Name of the transitions
	 * @param events
	 *            Array with the options for this transition
	 * @return Index of option more similar to the transition
	 */
	private int preSelectOption(String transition, Object[] events) {
		//The metric to get the similarity between strings
		AbstractStringMetric metric = new Levenshtein();

		int index = 0;
		float simOld = metric.getSimilarity(transition, "none");
		simOld = Math.max(simOld, metric.getSimilarity(transition, "invisible"));
		simOld = Math.max(simOld, metric.getSimilarity(transition, "skip"));
		simOld = Math.max(simOld, metric.getSimilarity(transition, "tau"));

		for (int i = 1; i < events.length; i++) {
			String event = ((XEventClass) events[i]).toString();
			float sim = metric.getSimilarity(transition, event);

			if (simOld < sim) {
				simOld = sim;
				index = i;
			}
		}

		return index;
	}
	
	/**
	 * Generate the map between Transitions and Event according to the user
	 * selection.
	 * 
	 * @return Map between Transitions and Events.
	 */
	public TransEvClassMapping getMap() {
		TransEvClassMapping map = new TransEvClassMapping((XEventClassifier) availableClassifier[0], DUMMY);
		for (Transition trans : mapTrans2ComboBox.keySet()){
			Object selectedValue = mapTrans2ComboBox.get(trans);
			if (selectedValue instanceof XEventClass){
				// a real event class
				map.put(trans, (XEventClass) selectedValue);
			} else {
				// this is "NONE"
				map.put(trans, DUMMY);
			}
			//System.out.println(trans+" --> "+map.get(trans));
		}
		return map;
	}

	/**
	 * Get the selected classifier
	 * @return
	 */
	public XEventClassifier getSelectedClassifier() {
		return (XEventClassifier) availableClassifier[0];
	}

}
