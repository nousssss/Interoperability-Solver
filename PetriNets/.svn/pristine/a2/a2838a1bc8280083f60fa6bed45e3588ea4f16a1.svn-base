package org.processmining.models.connections.petrinets;

import java.util.Map;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.framework.util.collection.WeakKeyValueMap;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public class PetrinetGraphConnection extends AbstractConnection {

	public final static String SOURCE = "Source net";
	public final static String TARGET = "Target net";

	private static final long serialVersionUID = 3134438310240127451L;
	private final Map<Transition, Transition> transitionMap;
	private final Map<Place, Place> placeMap;

	/**
	 * Connects the two Petri nets through the mappings described in the
	 * transitions and place map. The following assumptions are made:
	 * 
	 * assert(source.getTransitions().containsAll(transitionMap.keySet()));
	 * assert(target.getTransitions().containsAll(transitionMap.values()));
	 * assert(source.getPlaces().containsAll(placeMap.keySet()));
	 * assert(target.getPlaces().containsAll(placeMap.values()));
	 * 
	 * @param source
	 *            the source Petri net
	 * @param target
	 *            the target Petri net
	 * @param transitionMap
	 *            the mapping between transitions. This mapping is copied as a
	 *            WeakKeyValueMap to avoid keeping the net alive for the sake of
	 *            storing the connection. This is necessary since Transitions
	 *            keep pointers to the Petrinet in which they are contained and
	 *            these nets are used in the connection.
	 * @param placeMap
	 *            the mapping between places. This mapping is copied as a
	 *            WeakKeyValueMap to avoid keeping the net alive for the sake of
	 *            storing the connection.
	 */
	public PetrinetGraphConnection(PetrinetGraph source, PetrinetGraph target,
			Map<Transition, Transition> transitionMap, Map<Place, Place> placeMap) {
		super("Place/Transition mapping from " + source.getLabel() + " to " + target.getLabel());

		assert (source.getTransitions().containsAll(transitionMap.keySet()));
		assert (target.getTransitions().containsAll(transitionMap.values()));
		assert (source.getPlaces().containsAll(placeMap.keySet()));
		assert (target.getPlaces().containsAll(placeMap.values()));

		this.transitionMap = new WeakKeyValueMap<Transition, Transition>(transitionMap);
		this.placeMap = new WeakKeyValueMap<Place, Place>(placeMap);
		put(SOURCE, source);
		put(TARGET, target);

	}

	/**
	 * return true if this net is the source of the connection. Note that any
	 * user has a reference to the net available when the connection is
	 * retrieved from the framework. No equals method is used, i.e. pointers are
	 * compared.
	 * 
	 * @param net
	 * @return
	 */
	public boolean isSourceNet(PetrinetGraph net) {
		return getObjectWithRole(SOURCE) == net;
	}

	/**
	 * return true if this net is the source of the connection. Note that any
	 * user has a reference to the net available when the connection is
	 * retrieved from the framework. No equals method is used, i.e. pointers are
	 * compared.
	 * 
	 * @param net
	 * @return
	 */
	public boolean isTargetNet(PetrinetGraph net) {
		return getObjectWithRole(TARGET) == net;
	}

	/**
	 * Returns the mapping from transitions in source to transitions in target
	 * 
	 * @return
	 */
	public Map<Transition, Transition> getTransitionMapping() {
		return transitionMap;
	}

	/**
	 * Returns the mapping from places in source to places in target
	 * 
	 * @return
	 */
	public Map<Place, Place> getPlaceMapping() {
		return placeMap;
	}
}