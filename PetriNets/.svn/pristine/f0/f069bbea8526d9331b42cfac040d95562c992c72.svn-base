package org.processmining.models.graphbased.directed.petrinet.configurable.elements;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementOption;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;

/**
 * Configures a {@link Place} to present or absent in a {@link Petrinet}.
 * 
 * @author dfahland
 *
 */
public class ConfigurablePlace extends ConfigurableGraphElementFeature<Place> {

	public ConfigurablePlace(String id, Place element, ConfigurableGraphElementOption[] values, ConfigurableGraphElementOption defaultValue)
			throws InvalidConfigurationException {
		super(id, element, values, defaultValue);
	}

	/*
	 * (non-Javadoc)
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#isStillExecutable(org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet)
	 */
	public boolean isStillExecutable(ConfigurablePetrinet<? extends PetrinetGraph> parent) {
		return parent.getPlaces().contains(getConfiguredElement());
	}

	/**
	 * Configures the place of this configurable feature. If
	 * {@link ConfigurablePlaceOptions#REMOVE} the palce and its adjacent arcs
	 * are removed, if {@link ConfigurableGraphElementOption#USE}, the place is
	 * left unchanged.
	 * 
	 * @param parent
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#executeConfiguration(org.processmining.models.graphbased.directed.petrinet.PetrinetGraph)
	 */
	public void executeConfiguration(ConfigurablePetrinet<? extends PetrinetGraph> parent) {
		Place p = getConfiguredElement();
		switch (getValue()) {
			case ALLOW: break;
			case BLOCK: {
				parent.getConfiguredMarking().remove(p);
				parent.removePlace(p);
				break;
			}
			case SKIP: break;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#cloneFor(org.processmining.models.graphbased.AbstractGraphElement)
	 */
	public ConfigurableFeature<Place, ConfigurableGraphElementOption> cloneFor(Place newElement)
			throws InvalidConfigurationException {
		return new ConfigurablePlace(getId(), newElement, getDiscreteDomain(), getValue());
	}
}
