/**
 * 
 */
package org.processmining.plugins.petrinet.manifestreplayer;

import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * This class represent patterns in form of petri net.
 * @author aadrians
 * May 17, 2012
 *
 */
public class PNetPattern {
	private PetrinetGraph net; // index of the graph indicate patterns
	private Marking initMarking; // initial marking of net patterns
	private Marking[] finalMarkings; // final marking of net patterns
	private Map<Transition, XEventClass> trans2ecC; // mapping from transition to event class

	public PNetPattern(PetrinetGraph net, Map<Transition, XEventClass> mapping, Marking initMarking, Marking... finalMarkings) {
		this.net = net;
		this.trans2ecC = mapping;
		this.initMarking = initMarking;
		this.finalMarkings = finalMarkings;
	}
	

	/**
	 * @return the net
	 */
	public PetrinetGraph getNet() {
		return net;
	}

	/**
	 * @param net the net to set
	 */
	public void setNet(PetrinetGraph net) {
		this.net = net;
	}

	/**
	 * @return the initMarking
	 */
	public Marking getInitMarking() {
		return initMarking;
	}

	/**
	 * @param initMarking the initMarking to set
	 */
	public void setInitMarking(Marking initMarking) {
		this.initMarking = initMarking;
	}

	/**
	 * @return the finalMarkings
	 */
	public Marking[] getFinalMarkings() {
		return finalMarkings;
	}

	/**
	 * @param finalMarkings the finalMarkings to set
	 */
	public void setFinalMarkings(Marking[] finalMarkings) {
		this.finalMarkings = finalMarkings;
	}

	/**
	 * @return the trans2ecC
	 */
	public Map<Transition, XEventClass> getTrans2ecC() {
		return trans2ecC;
	}

	/**
	 * @param trans2ecC the trans2ecC to set
	 */
	public void setTrans2ecC(Map<Transition, XEventClass> trans2ecC) {
		this.trans2ecC = trans2ecC;
	}

}
