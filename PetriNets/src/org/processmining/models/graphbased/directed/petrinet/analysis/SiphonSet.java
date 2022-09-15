package org.processmining.models.graphbased.directed.petrinet.analysis;

import org.processmining.models.graphbased.directed.petrinet.elements.Place;

public class SiphonSet extends AbstractComponentSet<Place> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8416543069625187327L;

	public SiphonSet() {
		super("Siphons");
	}

	public boolean equals(Object o) {
		if (o instanceof SiphonSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
