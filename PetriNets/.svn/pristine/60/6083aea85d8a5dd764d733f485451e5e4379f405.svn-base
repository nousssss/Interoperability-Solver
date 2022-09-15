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
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

/**
 * Configurable arc to determine arc weights based on an expression.
 * 
 * @author dfahland
 *
 */
public class ParameterizedArc extends ParameterizedIntegerFeature<Arc> {

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
	public ParameterizedArc(String id, Arc element, Integer min, Integer max, String expression, List<ConfigurableParameter<Integer>> inputParameters)
			throws InvalidConfigurationException, UnknownFunctionException, UnparsableExpressionException
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
	public ParameterizedArc(Arc element, Integer min, Integer max, String expression, List<ConfigurableParameter<Integer>> inputParameters)
			throws InvalidConfigurationException, UnknownFunctionException, UnparsableExpressionException
	{
		super(ConfigurableArcWeight.generateID(element), element, min, max, expression, inputParameters);
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
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ParameterizedFeature#cloneFor(org.processmining.models.graphbased.AbstractGraphElement)
	 */
	public ConfigurableFeature<Arc, Integer> cloneFor(Arc newElement) throws InvalidConfigurationException {
		try {
			return new ParameterizedArc(newElement, getIntervalMin(), getIntervalMax(), getExpression(), getInputParameters());
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
			getConfiguredElement().getAttributeMap().put(AttributeMap.STROKECOLOR, Color.BLUE);
			getConfiguredElement().getAttributeMap().put(AttributeMap.BORDERWIDTH, 5);
		}
	}
	
	public void clearElementVisualization() {
		getConfiguredElement().getAttributeMap().remove(AttributeMap.STROKECOLOR);
		getConfiguredElement().getAttributeMap().remove(AttributeMap.BORDERWIDTH);
	}

}
