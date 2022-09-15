package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.NetAnalysisInformation;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;

public class BoundednessInfoConnection extends BehavioralAnalysisInformationConnection {

	public BoundednessInfoConnection(PetrinetGraph net, Marking marking, Semantics<Marking, Transition> semantics,
			NetAnalysisInformation.BOUNDEDNESS netAnalysisInformation) {
		super(net, marking, semantics, netAnalysisInformation);
		// TODO Auto-generated constructor stub
	}

}
