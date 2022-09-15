package org.processmining.models.graphbased.directed.petrinet.analysis;

import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;

public class SComponentSet extends AbstractComponentSet<PetrinetNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 907138142386125250L;

	public SComponentSet() {
		super("S-Components");
	}

	public boolean equals(Object o) {
		if (o instanceof SComponentSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
