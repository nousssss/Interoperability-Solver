package org.processmining.models.graphbased.directed.petrinet.configurable.impl;

import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * Valid configuration options for {@link Transition}, {@link Place}, and {@link Arc}
 * 
 * @author dfahland
 */
public enum ConfigurableGraphElementOption {
	/**
	 * The transition remains in the Petri net unchanged.
	 */
	ALLOW,
	
	/**
	 * The transition is removed from the Petri net.
	 */
	BLOCK,
	
	/**
	 * The transition is made invisible in the Petri net. Only used for {@link Transition}.
	 */
	SKIP
}
