package org.processmining.models.graphbased.directed.petrinet.configurable.elements;

import java.awt.Color;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurationUtils;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableIntegerFeature;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;

/**
 * Configures the number of tokens on a {@link Place} on the initial marking of
 * a {@link Petrinet}.
 * 
 * @author dfahland
 * 
 */
public class ConfigurablePlaceMarking extends ConfigurableIntegerFeature<Place> {

	public ConfigurablePlaceMarking(String id, Place element, Integer min, Integer max,
			Integer defaultValue) throws InvalidConfigurationException {
		super(id, element, min, max, defaultValue);
	}

	/*
	 * (non-Javadoc)
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#isStillExecutable(org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet)
	 */
	public boolean isStillExecutable(ConfigurablePetrinet<? extends PetrinetGraph> parent) {
		return parent.getPlaces().contains(getConfiguredElement());
	}

	/**
	 * Set initial marking of configurable place in its parent net to the
	 * configured value.
	 * 
	 * @param parent
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#executeConfiguration(org.processmining.models.graphbased.directed.petrinet.PetrinetGraph)
	 */
	@Override
	public void executeConfiguration(ConfigurablePetrinet<? extends PetrinetGraph> parent) {
		parent.getConfiguredMarking().remove(getConfiguredElement());
		parent.getConfiguredMarking().add(getConfiguredElement(), getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#cloneFor(org.processmining.models.graphbased.AbstractGraphElement)
	 */
	public ConfigurableFeature<Place, Integer> cloneFor(Place newElement) throws InvalidConfigurationException {
		return new ConfigurablePlaceMarking(getId(), newElement, getIntervalMin(), getIntervalMax(), getValue());
	}

	public void updateElementVisualization() {
		if (getIntervalMin() == getIntervalMax()) {
			clearElementVisualization();
		} else {
			getConfiguredElement().getAttributeMap().put(AttributeMap.FILLCOLOR, Color.BLUE);
		}
	}
	
	public void clearElementVisualization() {
		getConfiguredElement().getAttributeMap().remove(AttributeMap.FILLCOLOR);
	}
	
	public static String generateID(DirectedGraphElement el) {
		return ConfigurationUtils.generateElementIDforFeature(el)+"_marking";
	}
}
