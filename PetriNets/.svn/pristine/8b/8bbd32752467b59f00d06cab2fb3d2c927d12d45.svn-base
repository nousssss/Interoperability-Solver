package org.processmining.models.graphbased.directed.petrinet.configurable.elements;

import java.awt.Color;
import java.util.List;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ParameterizedIntegerFeature;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

/**
 * Configures the number of tokens on a {@link Place} in the initial marking
 * based on an expression.
 * 
 * @author dfahland
 * 
 */
public class ParameterizedPlaceMarking extends ParameterizedIntegerFeature<Place> {

	/**
	 * Create a new parameterized marking of this place. The number of tokens on
	 * the place in the initial marking is computed from the given expression
	 * which contains the provided parameters with their default values. Values
	 * of variables may range between min and max.
	 * 
	 * @param id
	 * @param element
	 * @param min
	 * @param max
	 * @param expression
	 * @param inputParameters
	 * @throws InvalidConfigurationException
	 * @throws UnknownFunctionException
	 * @throws UnparsableExpressionException
	 */
	public ParameterizedPlaceMarking(String id, Place element, Integer min, Integer max, String expression, List<ConfigurableParameter<Integer>> inputParameters)
		throws InvalidConfigurationException, UnknownFunctionException,	UnparsableExpressionException
	{
		super(id, element, min, max, expression, inputParameters);
	}
	
	/**
	 * Create a new parameterized marking of this place. The number of tokens on
	 * the place in the initial marking is computed from the given expression
	 * which contains the provided parameters with their default values. Values
	 * of variables may range between min and max.
	 * 
	 * @param element
	 * @param min
	 * @param max
	 * @param expression
	 * @param inputParameters
	 * @throws InvalidConfigurationException
	 * @throws UnknownFunctionException
	 * @throws UnparsableExpressionException
	 */
	public ParameterizedPlaceMarking(Place element, Integer min, Integer max, String expression, List<ConfigurableParameter<Integer>> inputParameters)
		throws InvalidConfigurationException, UnknownFunctionException,	UnparsableExpressionException
	{
		super(ConfigurablePlaceMarking.generateID(element), element, min, max, expression, inputParameters);
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
		System.out.println(getId()+" has value "+getValue());
		parent.getConfiguredMarking().remove(getConfiguredElement());
		parent.getConfiguredMarking().add(getConfiguredElement(), getValue());
	}

	/*
	 * (non-Javadoc)
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ParameterizedFeature#cloneFor(org.processmining.models.graphbased.AbstractGraphElement)
	 */
	public ConfigurableFeature<Place, Integer> cloneFor(Place newElement) throws InvalidConfigurationException {
		try {
			return new ParameterizedPlaceMarking(newElement, getIntervalMin(), getIntervalMax(), getExpression(), getInputParameters());
		} catch (UnparsableExpressionException e) {
			throw new InvalidConfigurationException(e);
		} catch (UnknownFunctionException e) {
			throw new InvalidConfigurationException(e);
		}
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


}
