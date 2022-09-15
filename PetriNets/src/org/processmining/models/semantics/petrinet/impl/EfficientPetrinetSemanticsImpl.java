package org.processmining.models.semantics.petrinet.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.ExecutionInformation;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.EfficientPetrinetSemantics;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetExecutionInformation;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;

/**
 * More efficient implementation than {@link PetrinetSemanticsImpl} avoiding
 * object creation.
 * 
 * @author F. Mannhardt
 * @author S.J. van Zelst
 *
 */
public final class EfficientPetrinetSemanticsImpl implements EfficientPetrinetSemantics {

	private static final long serialVersionUID = 1L;

	private final IdentityHashMap<Transition, Integer> transitionMap;
	private final Transition[] orderedTransitions;
	private final IdentityHashMap<Place, Integer> placeMap;
	private final Place[] orderedPlaces;

	private final byte[][] consuming;
	private final byte[][] producing;
	private final byte[][] effect;

	private byte[] state;

	/**
	 * Creates a copy of the supplied semantics with an independent state. All
	 * other data structures are shared to minimize copying of data.
	 * 
	 * @param semantics
	 */
	public EfficientPetrinetSemanticsImpl(EfficientPetrinetSemanticsImpl semantics) {
		this.consuming = semantics.consuming;
		this.producing = semantics.producing;
		this.effect = semantics.effect;
		this.transitionMap = semantics.transitionMap;
		this.orderedTransitions = semantics.orderedTransitions;
		this.placeMap = semantics.placeMap;
		this.orderedPlaces = semantics.orderedPlaces;
		this.state = Arrays.copyOf(semantics.state, semantics.state.length);
	}

	/**
	 * Creates the semantics for the supplied {@link PetrinetGraph} and an empty
	 * initial marking. This semantics class treat all {@link PetrinetGraph}s as
	 * a {@link Petrinet} (so do not honor the semantics of reset or inhibitor
	 * nets).
	 * 
	 * @param net
	 *            with the graph structure
	 */
	public EfficientPetrinetSemanticsImpl(PetrinetGraph net) {
		this(net, new Marking());
	}

	/**
	 * Creates the semantics for the supplied {@link PetrinetGraph} and initial
	 * {@link Marking}. This semantics class treat all {@link PetrinetGraph}s as
	 * a {@link Petrinet} (so do not honor the semantics of reset or inhibitor
	 * nets).
	 * 
	 * @param net
	 *            with the graph structure
	 * @param initialMarking
	 */
	public EfficientPetrinetSemanticsImpl(PetrinetGraph net, Marking initialMarking) {

		Collection<Place> places = net.getPlaces();
		placeMap = new IdentityHashMap<Place, Integer>(places.size());
		orderedPlaces = new Place[places.size()];
		state = new byte[places.size()];

		int currentPlaceIndex = 0;
		for (Place p : places) {
			placeMap.put(p, currentPlaceIndex);
			orderedPlaces[currentPlaceIndex] = p;
			Integer tokens = initialMarking.occurrences(p);
			state[currentPlaceIndex] = tokens.byteValue();
			currentPlaceIndex++;
		}

		Collection<Transition> transitions = net.getTransitions();
		orderedTransitions = new Transition[transitions.size()];
		transitionMap = new IdentityHashMap<Transition, Integer>(transitions.size());
		consuming = new byte[transitions.size()][];
		producing = new byte[transitions.size()][];
		effect = new byte[transitions.size()][];

		int currentTransitionIndex = 0;
		for (Transition t : transitions) {
			transitionMap.put(t, currentTransitionIndex);
			orderedTransitions[currentTransitionIndex] = t;

			effect[currentTransitionIndex] = new byte[places.size()];

			consuming[currentTransitionIndex] = new byte[places.size()];
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inEdges = net.getInEdges(t);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : inEdges) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;

					PetrinetNode sourceNode = edge.getSource();
					Place sourcePlace = (Place) sourceNode;
					int placeIndex = placeMap.get(sourcePlace);
					// consumes n tokens from this place 
					consuming[currentTransitionIndex][placeIndex] = (byte) arc.getWeight();
					effect[currentTransitionIndex][placeIndex] -= (byte) arc.getWeight();
				}
			}

			producing[currentTransitionIndex] = new byte[places.size()];
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outEdges = net.getOutEdges(t);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : outEdges) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;

					PetrinetNode targetNode = edge.getTarget();
					Place targetPlace = (Place) targetNode;
					int placeIndex = placeMap.get(targetPlace);
					// produces n tokens to this place 
					producing[currentTransitionIndex][placeIndex] = (byte) arc.getWeight();
					effect[currentTransitionIndex][placeIndex] += (byte) arc.getWeight();
				}
			}

			currentTransitionIndex++;
		}
	}

	@Override
	public byte[] getState() {
		return state.clone();
	}

	@Override
	public void setState(byte[] marking) {
		state = marking.clone();
	}

	@Override
	public void directExecuteExecutableTransition(Transition transition) {
		Integer transitionIndex = transitionMap.get(transition);
		if (transitionIndex == null) {
			throw new IllegalArgumentException("Transition " + transition + " is unknown!");
		}
		assert isEnabled(transitionIndex) : "Transition " + transition + " is not enabled at marking "
				+ getCurrentState();
		byte[] effectOnTokens = effect[transitionIndex];
		for (int i = 0; i < effectOnTokens.length; i++) {
			state[i] = (byte) (state[i] + effectOnTokens[i]);
		}
	}

	@Override
	public boolean isEnabled(final Transition transition) {
		Integer transitionIndex = transitionMap.get(transition);
		if (transitionIndex == null) {
			throw new IllegalArgumentException("Transition " + transition + " is unknown!");
		}
		return isEnabled(transitionIndex);
	}

	public boolean isEnabled(final int transitionIndex) {
		byte[] neededTokens = consuming[transitionIndex];
		for (int i = 0; i < neededTokens.length; i++) {
			byte tokens = neededTokens[i];
			if ((tokens > 0) && (state[i] < tokens)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isMarked(byte[] marking, Place place) {
		Integer placeIndex = placeMap.get(place);
		if (placeIndex == null) {
			throw new IllegalArgumentException("Place " + place + " is unknown!");
		}
		return marking[placeIndex] > 0;
	}

	@Override
	public void forEachMarkedPlace(PlaceVisitor placeVisitor) {
		for (int i = 0; i < state.length; i++) {
			int tokens = state[i];
			if (tokens > 0) {
				placeVisitor.accept(orderedPlaces[i], tokens);
			}
		}
	}

	@Override
	public void forEachPlace(PlaceVisitor placeVisitor) {
		for (int i = 0; i < state.length; i++) {
			placeVisitor.accept(orderedPlaces[i], state[i]);
		}
	}

	@Override
	public boolean equalMarking(byte[] marking1, Marking marking2) {
		for (int i = 0; i < marking1.length; i++) {
			Place place = orderedPlaces[i];
			int mutiplicity = marking1[i];
			if (marking2.occurrences(place) != mutiplicity) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean equalOrLessMarking(byte[] marking1, Marking marking2) {
		boolean equalOrLessMarking = true;
		for (int i = 0; i < marking1.length; i++) {
			Place place = orderedPlaces[i];
			int mutiplicity = marking1[i];
			Integer occurrences = marking2.occurrences(place);
			if (occurrences >= 1) {
				equalOrLessMarking = occurrences <= mutiplicity;
			}
		}
		return equalOrLessMarking;
	}

	/**
	 * Convenience method of the original {@link PetrinetSemantics} interface.
	 * Use {@link #setState(int[])} instead for a more efficient implementation.
	 */
	@Override
	public void setCurrentState(Marking marking) {
		for (Place p : orderedPlaces) {
			Integer tokens = marking.occurrences(p);
			state[placeMap.get(p)] = tokens.byteValue();
		}
	}

	/**
	 * Convenience method of the original {@link PetrinetSemantics} interface.
	 * Use {@link #getState()} instead for a more efficient implementation.
	 */
	@Override
	public Marking getCurrentState() {
		return convert(state);
	}

	@Override
	public Collection<Transition> getExecutableTransitions() {
		ArrayList<Transition> enabledTransitions = new ArrayList<Transition>();
		for (int i = 0; i < orderedTransitions.length; i++) {
			if (isEnabled(i)) {
				enabledTransitions.add(orderedTransitions[i]);
			}
		}
		return enabledTransitions;
	}

	/**
	 * For a more efficient implementation use
	 * {@link #directExecuteExecutableTransition(Transition)}, which does not
	 * return any information about the execution.
	 */
	@Override
	public ExecutionInformation executeExecutableTransition(Transition transition) throws IllegalTransitionException {
		if (!isEnabled(transition)) {
			throw new IllegalTransitionException(transition, getStateAsMarking());
		}
		Integer transitionIndex = transitionMap.get(transition);
		if (transitionIndex == null) {
			throw new IllegalArgumentException("Transition " + transition + " is unknown!");
		}
		byte[] consumedTokens = consuming[transitionIndex];
		byte[] producedTokens = producing[transitionIndex];
		directExecuteExecutableTransition(transition);
		Marking necessary = convert(consumedTokens);
		return new PetrinetExecutionInformation(necessary, necessary, convert(producedTokens), transition);
	}

	/**
	 * This method cannot be used in this implementation. Please use the normal
	 * way to initialize an instance (the constructor).
	 */
	@Override
	public void initialize(Collection<Transition> transitions, Marking initialState) {
		throw new UnsupportedOperationException(
				"Initialize is not possible on this PetrinetSemantic, use the constructor!");
	}

	/**
	 * Use {@link #getCurrentState()} instead.
	 */
	public Marking getStateAsMarking() {
		return getCurrentState();
	}

	/**
	 * Use {@link #setCurrentState(Marking)} instead.
	 */
	public void setStateAsMarking(Marking marking) {
		setCurrentState(marking);
	}

	/**
	 * @return a map from {@link Place} to its index in the marking returned by
	 *         {@link #getState()}.
	 */
	public Map<Place, Integer> getPlaceMap() {
		return placeMap;
	}

	public int getIndex(Transition t) {
		return transitionMap.get(t);
	}

	public int getIndex(Place p) {
		return placeMap.get(p);
	}

	public byte[] getMissingTokensToEnable(Transition t) {
		Integer transitionIndex = transitionMap.get(t);
		if (transitionIndex == null) {
			throw new IllegalArgumentException("Transition " + t.toString() + " is unknown!");
		}
		return getMissingTokensToEnable(transitionIndex);
	}

	public Place getPlace(int index) {
		return orderedPlaces[index];
	}

	public Transition getTransition(int index) {
		return orderedTransitions[index];
	}

	public byte[] getMissingTokensToEnable(int transitionIndex) {
		byte[] neededTokens = consuming[transitionIndex];
		byte[] result = new byte[consuming.length];
		for (int i = 0; i < neededTokens.length; i++) {
			byte tokens = neededTokens[i];
			byte curMark = state[i];
			if ((tokens > 0) && (curMark < tokens)) {
				result[i] = (byte) (tokens - curMark);
			}
		}
		return result;
	}

	public byte[] convert(Marking marking) {
		byte[] tokens = new byte[orderedPlaces.length];
		for (Place p : marking) { //iterator is multiset aware!
			tokens[getIndex(p)]++;
		}
		return tokens;
	}

	public Marking convert(byte[] marking) {
		Marking obj = new Marking();
		for (int i = 0; i < marking.length; i++) {
			int tokens = marking[i];
			if (tokens > 0) {
				obj.add(getPlace(i), tokens);
			}
		}
		return obj;
	}

}