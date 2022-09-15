package org.processmining.models.graphbased.directed.petrinet.configurable.elements;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementOption;

/**
 * Configures a {@link PetrinetEdge} to be present or absent in a {@link Petrinet}.
 * 
 * @author dfahland
 *
 */
public class ConfigurableArc extends ConfigurableGraphElementFeature<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> {

	public ConfigurableArc(String id, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> element, ConfigurableGraphElementOption[] values, ConfigurableGraphElementOption defaultValue)
			throws InvalidConfigurationException {
		super(id, element, values, defaultValue);
	}

	/*
	 * (non-Javadoc)
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#isStillExecutable(org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet)
	 */
	@Override
	public boolean isStillExecutable(ConfigurablePetrinet<? extends PetrinetGraph> parent) {
		return parent.getEdges().contains(getConfiguredElement());
	}

	/**
	 * Configures the arc of this configurable feature. If
	 * {@link ConfigurableGraphElementOption#BLOCK}, the arcs is removed,
	 * otherwise the arc remains unchanged.
	 * 
	 * @param parent
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#executeConfiguration(org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet)
	 */
	@Override
	public void executeConfiguration(ConfigurablePetrinet<? extends PetrinetGraph> parent) {
		PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> a = getConfiguredElement();
		switch (getValue()) {
			case ALLOW: break;
			case BLOCK: {
				parent.removeEdge(a);
				break;
			}
			case SKIP: break;
		}	}

	/*
	 * (non-Javadoc)
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#cloneFor(org.processmining.models.graphbased.directed.DirectedGraphElement)
	 */
	@Override
	public ConfigurableFeature<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>, ConfigurableGraphElementOption> cloneFor(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> newElement)
			throws InvalidConfigurationException {
		return new ConfigurableArc(getId(), newElement, getDiscreteDomain(), getValue());
	}

}
