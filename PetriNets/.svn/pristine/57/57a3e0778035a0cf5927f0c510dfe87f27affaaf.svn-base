package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.NetAnalysisInformation;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;

public abstract class BehavioralAnalysisInformationConnection extends AbstractSemanticConnection {

	public final static String NETANALYSISINFORMATION = "NetAnalysisInformation";

	/**
	 * Connection with marking
	 * 
	 * @param net
	 * @param marking
	 * @param netAnalysisInformation
	 */
	public BehavioralAnalysisInformationConnection(PetrinetGraph net, Marking marking,
			Semantics<Marking, Transition> semantics, NetAnalysisInformation<?> netAnalysisInformation) {
		super("Connection between " + net.getLabel() + " and net analysis " + netAnalysisInformation.getLabel(), net, marking, semantics);
		put(NETANALYSISINFORMATION, netAnalysisInformation);
	}
}
