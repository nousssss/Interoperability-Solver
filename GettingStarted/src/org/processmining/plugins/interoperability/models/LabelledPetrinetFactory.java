package org.processmining.plugins.interoperability.models;

import java.util.Map;

import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

public class LabelledPetrinetFactory {
	private LabelledPetrinetFactory() {

	}

	public static LabelledPetrinet newPetrinet(String label) {
		return new LabelledPetrinetImpl(label);
	}


	public static LabelledPetrinet clonePetrinet(Petrinet net) {
		LabelledPetrinetImpl newNet = new LabelledPetrinetImpl(net.getLabel());
		newNet.cloneFrom(net);
		return newNet;
	}

	public static LabelledPetrinet clonePetrinet(Petrinet net, Map<DirectedGraphElement, DirectedGraphElement> map) {
		LabelledPetrinetImpl newNet = new LabelledPetrinetImpl(net.getLabel());
		map.putAll(newNet.cloneFrom(net));
		return newNet;
	}

	


}
