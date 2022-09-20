/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.util.codec;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.InhibitorArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.ResetArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * @author aadrians
 * 
 */
public class PNCodec {
	private Map<Short, Transition> mapShortTrans;
	private Map<Transition, Short> mapTransShort;

	private Map<Short, Place> mapShortPlace;
	private Map<Place, Short> mapPlaceShort;

	private Map<Short, Set<Short>> successors;
	private Map<Short, Set<Short>> predecessors;
	private Map<Short, Set<Short>> inhibitorpredecessors;
	private Map<Short, Set<Short>> resetpredecessors;

	// create PNCodec from scratch
	public PNCodec(){
		// initialize all
		mapShortTrans = new HashMap<Short, Transition>();
		mapTransShort = new HashMap<Transition, Short>();
		
		mapShortPlace = new HashMap<Short, Place>();
		mapPlaceShort = new HashMap<Place, Short>();
		
		successors = new HashMap<Short, Set<Short>>();
		predecessors = new HashMap<Short, Set<Short>>();
		
		inhibitorpredecessors = new HashMap<Short, Set<Short>>();
		resetpredecessors = new HashMap<Short, Set<Short>>();
	}
	
	public PNCodec(PetrinetGraph net) {
		short count = Short.MIN_VALUE;

		// create setplaces
		Collection<Place> setPlaces = net.getPlaces();
		int placeSize = setPlaces.size();
		mapShortPlace = new HashMap<Short, Place>(placeSize);
		mapPlaceShort = new HashMap<Place, Short>(placeSize);
		for (Place place : setPlaces) {
			mapPlaceShort.put(place, count);
			mapShortPlace.put(count, place);
			count++;
		}

		// create set transitions, successors, predecessors
		Collection<Transition> setTrans = net.getTransitions();
		successors = new HashMap<Short, Set<Short>>();
		predecessors = new HashMap<Short, Set<Short>>();
		inhibitorpredecessors = new HashMap<Short, Set<Short>>();
		resetpredecessors = new HashMap<Short, Set<Short>>();

		int size = setTrans.size();
		mapShortTrans = new HashMap<Short, Transition>(size);
		mapTransShort = new HashMap<Transition, Short>(size);

		for (Transition trans : setTrans) {
			mapTransShort.put(trans, count);
			mapShortTrans.put(count, trans);

			// check predecessors
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inEdges = net
					.getInEdges(trans);
			Set<Short> predShortA = new HashSet<Short>(inEdges.size());
			Set<Short> predShortR = new HashSet<Short>(0);
			Set<Short> predShortI = new HashSet<Short>(0);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : inEdges) {
				if (edge instanceof Arc) {
					predShortA.add(mapPlaceShort.get(edge.getSource()));
				} else if (edge instanceof ResetArc) {
					predShortR.add(mapPlaceShort.get(edge.getSource()));
				} else if (edge instanceof InhibitorArc) {
					predShortI.add(mapPlaceShort.get(edge.getSource()));
				}
			}
			predecessors.put(count, predShortA);
			inhibitorpredecessors.put(count, predShortI);
			resetpredecessors.put(count, predShortR);

			// check successors
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outEdges = net
					.getOutEdges(trans);
			Set<Short> succShort = new HashSet<Short>(outEdges.size());
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : outEdges) {
				succShort.add(mapPlaceShort.get(edge.getTarget()));
			}
			successors.put(count, succShort);

			count++;
		}
	}

	public Short getEncodeOfTransition(Transition trans) {
		return mapTransShort.get(trans);
	}

	public Short getEncodeOfPlace(Place place) {
		return mapPlaceShort.get(place);
	}

	public Transition getTransitionOfEncode(Short val) {
		return mapShortTrans.get(val);
	}

	public Place getPlaceOfEncode(Short val) {
		return mapShortPlace.get(val);
	}

	/**
	 * Successors
	 * 
	 * @param trans
	 * @return
	 */
	public Set<Short> getSuccessors(Short trans) {
		return successors.get(trans);
	}

	/**
	 * Predecessors
	 * 
	 * @param trans
	 * @return
	 */
	public Set<Short> getPredecessors(Short trans) {
		return predecessors.get(trans);
	}

	/**
	 * @return the mapShortPlace
	 */
	public Map<Short, Place> getMapShortPlace() {
		return mapShortPlace;
	}

	/**
	 * @param mapShortPlace
	 *            the mapShortPlace to set
	 */
	public void setMapShortPlace(Map<Short, Place> mapShortPlace) {
		this.mapShortPlace = mapShortPlace;
	}

	/**
	 * @return the mapPlaceShort
	 */
	public Map<Place, Short> getMapPlaceShort() {
		return mapPlaceShort;
	}

	/**
	 * @param mapPlaceShort
	 *            the mapPlaceShort to set
	 */
	public void setMapPlaceShort(Map<Place, Short> mapPlaceShort) {
		this.mapPlaceShort = mapPlaceShort;
	}

	/**
	 * @return the successors
	 */
	public Map<Short, Set<Short>> getSuccessors() {
		return successors;
	}

	/**
	 * @param successors
	 *            the successors to set
	 */
	public void setSuccessors(Map<Short, Set<Short>> successors) {
		this.successors = successors;
	}

	/**
	 * @return the predecessors
	 */
	public Map<Short, Set<Short>> getPredecessors() {
		return predecessors;
	}

	/**
	 * @param predecessors
	 *            the predecessors to set
	 */
	public void setPredecessors(Map<Short, Set<Short>> predecessors) {
		this.predecessors = predecessors;
	}

	/**
	 * @return the mapShortTrans
	 */
	public Map<Short, Transition> getMapShortTrans() {
		return mapShortTrans;
	}

	/**
	 * @param mapShortTrans
	 *            the mapShortTrans to set
	 */
	public void setMapShortTrans(Map<Short, Transition> mapShortTrans) {
		this.mapShortTrans = mapShortTrans;
	}

	/**
	 * @return the mapTransShort
	 */
	public Map<Transition, Short> getMapTransShort() {
		return mapTransShort;
	}

	/**
	 * @param mapTransShort
	 *            the mapTransShort to set
	 */
	public void setMapTransShort(Map<Transition, Short> mapTransShort) {
		this.mapTransShort = mapTransShort;
	}

	/**
	 * Encode the transition
	 * 
	 * @param node
	 * @return
	 */
	public short encode(Transition transition) {
		return this.mapTransShort.get(transition);
	}

	/**
	 * Decode the transition
	 * 
	 * @param value
	 * @return
	 */
	public Transition decode(short value) {
		return mapShortTrans.get(value);
	}

	/**
	 * print
	 */
	public String toString() {
		String res = "";
		res += "Transition  --> Short \n";
		for (Transition ev : mapTransShort.keySet()) {
			res += ev.toString() + " --> " + mapTransShort.get(ev).toString()
					+ "\n";
		}
		res += "-------------------------------------------";
		res += "Short --> Transition \n";
		for (Short sh : mapShortTrans.keySet()) {
			res += sh + " --> " + mapShortTrans.get(sh).toString() + "\n";
		}
		res += "-------------------------------------------";
		return res;
	}

	public Set<Short> getInhibitors(Short selectedNode) {
		return inhibitorpredecessors.get(selectedNode);
	}

	public Set<Short> getResets(Short selectedNode) {
		return resetpredecessors.get(selectedNode);
	}

	/**
	 * @return the inhibitorpredecessors
	 */
	public Map<Short, Set<Short>> getInhibitorpredecessors() {
		return inhibitorpredecessors;
	}

	/**
	 * @param inhibitorpredecessors the inhibitorpredecessors to set
	 */
	public void setInhibitorpredecessors(Map<Short, Set<Short>> inhibitorpredecessors) {
		this.inhibitorpredecessors = inhibitorpredecessors;
	}

	/**
	 * @return the resetpredecessors
	 */
	public Map<Short, Set<Short>> getResetpredecessors() {
		return resetpredecessors;
	}

	/**
	 * @param resetpredecessors the resetpredecessors to set
	 */
	public void setResetpredecessors(Map<Short, Set<Short>> resetpredecessors) {
		this.resetpredecessors = resetpredecessors;
	}
	
	public void addPlace(Place place, Short encodedPlace){
		this.mapPlaceShort.put(place, encodedPlace);
		this.mapShortPlace.put(encodedPlace, place);
	}
	
	public void addTransition(Transition transition, Short encodedTransition){
		this.mapTransShort.put(transition, encodedTransition);
		this.mapShortTrans.put(encodedTransition, transition);
	}
	
	public void addSuccessors(Short encodedTransition, Set<Short> encodedSuccessors){
		this.successors.put(encodedTransition, encodedSuccessors);
	}
	
	public void addPredecessors(Short encodedTransition, Set<Short> encodedPredecessors){
		this.predecessors.put(encodedTransition, encodedPredecessors);
	}
	
	public void addInhibitorPredecessors(Short encodedTransition, Set<Short> encodedPredecessors){
		this.inhibitorpredecessors.put(encodedTransition, encodedPredecessors);
	}
	
	public void addResetPredecessors(Short encodedTransition, Set<Short> encodedPredecessors){
		this.resetpredecessors.put(encodedTransition, encodedPredecessors);
	}
	
	public void removeTransition(Short transition){
		this.mapTransShort.remove(this.mapShortTrans.get(transition));
		this.mapShortTrans.remove(transition);
		this.predecessors.remove(transition);
		this.successors.remove(transition);
		this.inhibitorpredecessors.remove(transition);
		this.resetpredecessors.remove(transition);
	}
}
