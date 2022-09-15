package org.processmining.models.graphbased.directed.petrinet.configurable.elements;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementOption;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * Configure a transition to be allowed, blocked, or skipped (see
 * {@link ConfigurableGraphElementOption}).
 * 
 * @author dfahland
 * 
 */
public class ConfigurableTransition extends ConfigurableGraphElementFeature<Transition>{

	public ConfigurableTransition(String id, Transition element, ConfigurableGraphElementOption[] values, ConfigurableGraphElementOption defaultValue) throws InvalidConfigurationException {
		super(id, element, values, defaultValue);
	}

	/*
	 * (non-Javadoc)
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#isStillExecutable(org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet)
	 */
	public boolean isStillExecutable(ConfigurablePetrinet<? extends PetrinetGraph> parent) {
		return parent.getTransitions().contains(getConfiguredElement());
	}

	/**
	 * Configures the transition of this configurable feature. If
	 * {@link ConfigurableGraphElementOption#BLOCK} the transitions and its adjacent arcs are
	 * removed, if {@link ConfigurableGraphElementOption#SKIP}, the transition is made
	 * invisible, and if {@link ConfigurableGraphElementOption#ALLOW}, the transition is left
	 * unchanged.
	 * 
	 * @param parent
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#executeConfiguration(org.processmining.models.graphbased.directed.petrinet.PetrinetGraph)
	 */
	@Override
	public void executeConfiguration(ConfigurablePetrinet<? extends PetrinetGraph> parent) {
		Transition t = getConfiguredElement();
		switch (getValue()) {
			case ALLOW: break;
			case BLOCK: parent.removeTransition(t); break;
			case SKIP: t.setInvisible(true); break;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#cloneFor(org.processmining.models.graphbased.AbstractGraphElement)
	 */
	public ConfigurableFeature<Transition, ConfigurableGraphElementOption> cloneFor(Transition newElement) throws InvalidConfigurationException {
		return new ConfigurableTransition(getId(), newElement, getDiscreteDomain(), getValue());
	}

}
