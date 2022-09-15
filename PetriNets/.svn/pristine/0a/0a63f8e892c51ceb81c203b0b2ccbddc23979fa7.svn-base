package org.processmining.models.graphbased.directed.petrinet.configurable.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.configurable.ParameterizedFeature;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

/**
 * An abstract implementation of a {@link ParameterizedFeature} ranging over {@link Integer}
 * 
 * @author dfahland
 *
 * @param <T>
 */
public abstract class ParameterizedIntegerFeature<T extends DirectedGraphElement> extends ConfigurableIntegerFeature<T> {
	
	private String expressionString;
	private Calculable expression;
	private final List<ConfigurableParameter<Integer>> inputParameters = new LinkedList<ConfigurableParameter<Integer>>();

	/**
	 * Create a new parameterized feature for the given element. The value of
	 * the feature is computed from the given expression which contains the
	 * provided parameters with their default values. Values of variables may
	 * range between min and max.
	 * 
	 * @param element
	 * @param min
	 * @param max
	 * @param expression
	 * @param parameters
	 * @throws InvalidConfigurationException
	 * @throws UnknownFunctionException
	 * @throws UnparsableExpressionException
	 */
	public ParameterizedIntegerFeature(String id, T element, Integer min, Integer max, String expression, List<ConfigurableParameter<Integer>> inputParameters) throws InvalidConfigurationException, UnknownFunctionException, UnparsableExpressionException {
		super(id, element, min, max, min);
		this.inputParameters.addAll(inputParameters);
		this.expressionString = expression;
		setExpression(expression, inputParameters);
	}
	
	/**
	 * Generate expression objects for this feature to evaluate for any given parameter value.
	 * @param expression
	 * @param parameters
	 * @throws UnknownFunctionException
	 * @throws UnparsableExpressionException
	 * @throws InvalidConfigurationException
	 */
	private void setExpression(String expression, List<ConfigurableParameter<Integer>> inputParameters) throws UnknownFunctionException, UnparsableExpressionException, InvalidConfigurationException {
		Map<String, Double> dValues = generateExpressionParameters(inputParameters);
		this.expression = new ExpressionBuilder(expression).withVariables(dValues).build();
	}
	
	private static Map<String, Double> generateExpressionParameters(List<ConfigurableParameter<Integer>> inputParameters) {
		Map<String, Double> dValues = new HashMap<String, Double>();
		for (ConfigurableParameter<Integer> param : inputParameters) {
			String var = param.getId();
			int val = param.getValue();
			dValues.put(var, new Double(val));
		}
		return dValues;
	}

	/**
	 * Never set values directly for parameterized features, does nothing.
	 * 
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#setValue(java.lang.Object)
	 * @throws InvalidConfigurationException
	 */
	@Override
	public final void setValue(Object value) throws InvalidConfigurationException {
		//throw new InvalidConfigurationException("Cannot directly assign value for a parameterized feature.");
		updateValue();
	}
	
	/**
	 * @return all parameters of this feature together with their chosen value
	 */
	public List<ConfigurableParameter<Integer>> getInputParameters() {
		return Collections.unmodifiableList(inputParameters);
	}
	
	public List<ConfigurableParameter<Integer>> getInputParametersByName() {
		HashMap<String, ConfigurableParameter<Integer>> params = new HashMap<String, ConfigurableParameter<Integer>>();
		for (ConfigurableParameter<Integer> p : getInputParameters()) {
			params.put(p.getId(), p);
		}
		LinkedList<ConfigurableParameter<Integer>> parameters = new LinkedList<ConfigurableParameter<Integer>>(params.values());
		return parameters;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableIntegerFeature#updateValue()
	 */
	@Override
	public void updateValue() {
		if (inputParameters != null) {
			for (ConfigurableParameter<Integer> par : inputParameters) {
				expression.setVariable(par.getId(), par.getValue());
			}
		}
	}
	
	/**
	 * @return expression of this feature
	 */
	public String getExpression() {
		return expressionString;
	}
	
	/**
	 * @param id
	 * @return input parameter of that id
	 */
	private ConfigurableParameter<Integer> getInputParameter(String id) {
		for (ConfigurableParameter<Integer> par : inputParameters) {
			if (par.getId().equals(id)) return par;
		}
		return null;
	}
	
	/**
	 * Set value of a parameter in the expression
	 * @param par
	 * @param value
	 * @throws InvalidConfigurationException if the parameter is unknown or the value is out of range
	 */
	public void setInputParameter(String id, Object value) throws InvalidConfigurationException {
		ConfigurableParameter<Integer> par = getInputParameter(id);
		if (par == null) throw new InvalidConfigurationException("Unknown parameter "+id+" for expression "+expression.getExpression());
		if (!(value instanceof Integer)) throw new InvalidConfigurationException("Value "+value+" should be an integer.");

		par.setValue((Integer)value);
		expression.setVariable(par.getId(), (Integer)value);
	}

	/**
	 * @return value of this feature as computed by the assigned expression and
	 *         the current parameter values
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#getValue()
	 */
	@Override
	public Integer getValue() {
		return (int) expression.calculate();
	}

	/*
	 * (non-Javadoc)
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature#executeConfiguration(org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet)
	 */
	public abstract void executeConfiguration(ConfigurablePetrinet<? extends PetrinetGraph> parent);
	
	public abstract ConfigurableFeature<T, Integer> cloneFor(T newElement) throws InvalidConfigurationException;
	
}
