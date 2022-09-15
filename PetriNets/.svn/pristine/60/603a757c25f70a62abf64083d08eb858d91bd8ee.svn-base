package org.processmining.models.graphbased.directed.petrinet.analysis;

import java.util.TreeSet;

import org.processmining.framework.util.collection.ComparablePair;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;

public abstract class AbstractNodePairSet<T extends PetrinetNode, S extends PetrinetNode> extends
		TreeSet<ComparablePair<T, S>> {

	private static final long serialVersionUID = -4003096887436890715L;
	private final String label;

	public AbstractNodePairSet(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
