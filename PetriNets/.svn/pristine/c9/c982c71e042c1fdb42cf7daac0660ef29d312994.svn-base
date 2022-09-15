package org.processmining.models.graphbased.directed.opennet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * @author hverbeek
 * 
 *         Open net model.
 * 
 *         An Open net model extends a regular Petri net with a set of ports and
 *         a set of final markings.
 */
/**
 * @author hverbeek
 * 
 */
public class OpenNet extends PetrinetImpl {

	private final OpenNetInterface openNetInterface;
	private final Collection<Marking> markings = new HashSet<Marking>();

	protected final Map<Transition, Collection<OpenNetLabel>> trans2label;
	protected final Map<OpenNetLabel, Collection<Transition>> label2trans;

	public OpenNet(String label, OpenNetInterface openNetInterface) {
		super(label);
		this.openNetInterface = openNetInterface;
		trans2label = new HashMap<Transition, Collection<OpenNetLabel>>();
		label2trans = new HashMap<OpenNetLabel, Collection<Transition>>();
	}

	public OpenNet(String label) {
		this(label, new OpenNetInterface());
	}

	@Override
	public synchronized Transition addTransition(String label) {
		return addTransition(label, null);
	}

	@Override
	public synchronized Transition addTransition(String label, ExpandableSubNet parent) {
		Transition t = new Transition(label, this, parent);
		transitions.add(t);
		graphElementAdded(t);
		return t;
	}

	public OpenNetInterface getInterface() {
		return openNetInterface;

	}

	public void addFinalMarking(Marking m) {
		markings.add(m);
	}

	public Collection<Marking> getFinalMarkings() {
		return markings;
	}

	public void addConnection(Transition transition, OpenNetLabel label) {
		Collection<OpenNetLabel> labels = trans2label.get(transition);
		if (labels == null) {
			labels = new TreeSet<OpenNetLabel>();
			trans2label.put(transition, labels);
		}
		labels.add(label);

		Collection<Transition> transitions = label2trans.get(label);
		if (transitions == null) {
			transitions = new HashSet<Transition>();
			label2trans.put(label, transitions);
		}
		transitions.add(transition);
	}

	@Override
	protected OpenNet getEmptyClone() {
		return new OpenNet(getLabel());
	}

	public Collection<OpenNetLabel> getLabelsFor(Transition openNetTransition) {
		Collection<OpenNetLabel> labels = trans2label.get(openNetTransition);
		return (labels == null ? Collections.<OpenNetLabel>emptySet() : labels);

	}

	public Collection<Transition> getTransitions(OpenNetLabel label) {
		Collection<Transition> trans = label2trans.get(label);
		return trans == null ? Collections.<Transition>emptySet() : trans;
	}

}
