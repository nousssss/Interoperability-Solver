package org.processmining.models.graphbased.directed.petrinet.impl;

import java.util.Map;

import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;

public class PetrinetFactory {

	private PetrinetFactory() {

	}

	public static Petrinet newPetrinet(String label) {
		return new PetrinetImpl(label);
	}

	public static ResetNet newResetNet(String label) {
		return new ResetNetImpl(label);
	}

	public static InhibitorNet newInhibitorNet(String label) {
		return new InhibitorNetImpl(label);
	}

	public static ResetInhibitorNet newResetInhibitorNet(String label) {
		return new ResetInhibitorNetImpl(label);
	}

	public static Petrinet clonePetrinet(Petrinet net) {
		PetrinetImpl newNet = new PetrinetImpl(net.getLabel());
		newNet.cloneFrom(net);
		return newNet;
	}

	public static Petrinet clonePetrinet(Petrinet net, Map<DirectedGraphElement, DirectedGraphElement> map) {
		PetrinetImpl newNet = new PetrinetImpl(net.getLabel());
		map.putAll(newNet.cloneFrom(net));
		return newNet;
	}

	public static ResetNet cloneResetNet(ResetNet net) {
		ResetNetImpl newNet = new ResetNetImpl(net.getLabel());
		newNet.cloneFrom(net);
		return newNet;
	}

	public static ResetNet cloneResetNet(ResetNet net, Map<DirectedGraphElement, DirectedGraphElement> map) {
		ResetNetImpl newNet = new ResetNetImpl(net.getLabel());
		map.putAll(newNet.cloneFrom(net));
		return newNet;
	}

	public static InhibitorNet cloneInhibitorNet(InhibitorNet net) {
		InhibitorNetImpl newNet = new InhibitorNetImpl(net.getLabel());
		newNet.cloneFrom(net);
		return newNet;
	}

	public static InhibitorNet cloneInhibitorNet(InhibitorNet net, Map<DirectedGraphElement, DirectedGraphElement> map) {
		InhibitorNetImpl newNet = new InhibitorNetImpl(net.getLabel());
		map.putAll(newNet.cloneFrom(net));
		return newNet;
	}

	public static ResetInhibitorNet cloneResetInhibitorNet(ResetInhibitorNet net) {
		ResetInhibitorNetImpl newNet = new ResetInhibitorNetImpl(net.getLabel());
		newNet.cloneFrom(net);
		return newNet;
	}

	public static ResetInhibitorNet cloneResetInhibitorNet(ResetInhibitorNet net, Map<DirectedGraphElement, DirectedGraphElement> map) {
		ResetInhibitorNetImpl newNet = new ResetInhibitorNetImpl(net.getLabel());
		map.putAll(newNet.cloneFrom(net));
		return newNet;
	}

}
