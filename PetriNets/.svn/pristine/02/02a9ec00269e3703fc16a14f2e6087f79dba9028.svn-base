package org.processmining.models.graphbased.directed.petrinet.configurable;

import java.util.List;

import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;

/**
 * A configurable feature of a graph element has input parameters and can be
 * executed based on the input parameters. You can use {@link ConfigurableParameter}s
 * to link input parameters to the execution of the feature. 
 * 
 * @author dfahland
 * 
 * @param <T>
 * @param <F>
 */
public interface ConfigurableFeature<T extends DirectedGraphElement, F> extends ParameterizedFeature<ConfigurableParameter<F>> {
	
	/**
	 * @return id of the configurable feature
	 */
	public String getId();
	
	/**
	 * Graph element being configured
	 * @return
	 */
	public T getConfiguredElement();
	
	/**
	 * @param parent
	 * @return true iff this configuration can be executed on the parent, e.g.
	 *         if {@link #getConfiguredElement()} is still an element of parent
	 */
	public boolean isStillExecutable(ConfigurablePetrinet<? extends PetrinetGraph> parent);
	
	/**
	 * Executes the configuration on {@link #getConfiguredElement()}. The
	 * configuration itself may not be completely executable on
	 * {@link #getConfiguredElement(), but may require to change the parent
	 * object of #getConfiguredElement() as well.
	 */
	public void executeConfiguration(ConfigurablePetrinet<? extends PetrinetGraph> parent);
	
	/**
	 * Create a clone of this feature using newElement as {@link #getConfiguredElement()}.
	 * @param newElement
	 */
	public ConfigurableFeature<T, F> cloneFor(T newElement) throws InvalidConfigurationException;

	/**
	 * @return list of input parameters of this feature
	 */
	public List<ConfigurableParameter<F>> getInputParameters();
	
	/**
	 * Update the value of this configurable feature. Has to be invoked whenever the value of this
	 * feature depends on several input parameters
	 */
	public void updateValue();
	
	/**
	 * Change visual representation of {@link #getConfiguredElement()}
	 */
	public void updateElementVisualization();

	/**
	 * Clear visual representation of {@link #getConfiguredElement()} back to normal
	 */
	public void clearElementVisualization();
}
