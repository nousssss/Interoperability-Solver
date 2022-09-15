package org.processmining.models.graphbased.directed.petrinet.configurable.elements;

import java.awt.Color;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurationUtils;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableIntegerFeature;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;

/**
 * Configurable arc to determine arc weights. An arc weight of 0 will remove the arc.
 * 
 * @author dfahland
 *
 */
public class ConfigurableArcWeight extends ConfigurableIntegerFeature<Arc> {

	public ConfigurableArcWeight(String id, Arc element, Integer min, Integer max, Integer defaultValue)
			throws InvalidConfigurationException {
		super(id, element, min, max, defaultValue);
	}

	/*
	 * (non-Javadoc)
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#isStillExecutable(org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet)
	 */
	public boolean isStillExecutable(ConfigurablePetrinet<? extends PetrinetGraph> parent) {
		return parent.getEdges().contains(getConfiguredElement());
	}

	/**
	 * Set the weight of the configurable arc to {@link #getValue()} (if > 0) and
	 * remove arc from parent (if == 0)
	 * 
	 * @param parent
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#executeConfiguration(org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet)
	 */
	public void executeConfiguration(ConfigurablePetrinet<? extends PetrinetGraph> parent) {
		if (getValue() == 0) parent.removeEdge(getConfiguredElement());
		else {
			getConfiguredElement().setWeight(getValue());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#cloneFor(org.processmining.models.graphbased.AbstractGraphElement)
	 */
	public ConfigurableFeature<Arc, Integer> cloneFor(Arc newElement) throws InvalidConfigurationException {
		return new ConfigurableArcWeight(getId(), newElement, getIntervalMin(), getIntervalMax(), getValue());
	}
	
	public void updateElementVisualization() {
		if (getIntervalMin() == getIntervalMax()) {
			clearElementVisualization();
		} else {
			getConfiguredElement().getAttributeMap().put(AttributeMap.STROKECOLOR, Color.BLUE);
			getConfiguredElement().getAttributeMap().put(AttributeMap.BORDERWIDTH, 5);
		}
	}
	
	public void clearElementVisualization() {
		getConfiguredElement().getAttributeMap().remove(AttributeMap.STROKECOLOR);
		getConfiguredElement().getAttributeMap().remove(AttributeMap.BORDERWIDTH);
	}
	
	public static String generateID(DirectedGraphElement el) {
		return ConfigurationUtils.generateElementIDforFeature(el)+"_weight";
	}
}
