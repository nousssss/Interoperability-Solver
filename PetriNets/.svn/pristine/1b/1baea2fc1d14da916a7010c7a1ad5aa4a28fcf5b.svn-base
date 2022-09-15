package org.processmining.models.graphbased.directed.petrinet.configurable;

import java.util.List;

import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * Configurable Petri net that builds on top of an existing class of Petri nets specified
 * by parameter NetType. The contract with this interface is that any class that implements
 * this interface als implements or extends NetType.
 * 
 * @author dfahland
 *
 * @param <NetType> the net base type made configurable by this net
 */
public interface ConfigurablePetrinet<NetType extends PetrinetGraph> extends Petrinet {
	
	/**
	 * Add a configurable feature to this net
	 * @param id to uniquely reference the feature
	 * @param feature
	 * @throws InvalidConfigurationException if the feature ranges over an element not part of this net
	 */
	public void addConfigurableFeature(String id, ConfigurableFeature<? extends DirectedGraphElement, ?> feature) throws InvalidConfigurationException;

	/**
	 * Add a configurable feature group to this net,
	 * @param group
	 * @throws InvalidConfigurationException if any feature in the group ranges over an element not part of this net
	 */
	public void addConfigurableFeatures(ConfigurableFeatureGroup group) throws InvalidConfigurationException;

	/**
	 * Clone a configurable reset inhibitor net from an existing reset inhibitor
	 * net. The configurations specified for the existing net will be
	 * instantiated for the new configurable net.
	 * 
	 * @param net
	 * @param netMarking initial marking of the net, can be {@code null} or empty
	 * @param configurations
	 * @param oldLayout
	 * @param newLayout
	 * @throws Exception in case the cloned net has an invalid set of configuration features
	 */
	public void cloneFrom(NetType net, Marking netMarking, List<ConfigurableFeatureGroup> configurationOptions, GraphLayoutConnection oldLayout, GraphLayoutConnection newLayout) throws Exception;
	
	/**
	 * @return a clone of the given {@link ConfigurablePetrinet}
	 * @throws Exception in case the cloned net has an invalid set of configuration features
	 */
	public void cloneFrom(ConfigurablePetrinet<NetType> net, GraphLayoutConnection oldLayout, GraphLayoutConnection newLayout) throws Exception;

	/**
	 * @return list of all configuration features of this Petri net (individual
	 *         transitions and groups of transitions)
	 */
	public List<ConfigurableFeatureGroup> getConfigurableFeatureGroups();
	
	/**
	 * Configure the provided configuration features with the provided parameter values,
	 * and execute the configurations.
	 * 
	 * @param configurations
	 */
	public void configure(List<Configuration> configurations) throws InvalidConfigurationException;

	/**
	 * Configure this Petri net by executing all configurations of all
	 * configuration features. An implementation should iterate over each
	 * {@link ConfigurableFeatureGroup} in {@link #getConfigurableFeatureGroups()}
	 * and execute
	 * {@link ConfigurableFeatureGroup#executeConfiguration(ConfigurablePetrinet)}
	 */
	public void configureDefault() throws InvalidConfigurationException;
	
	/**
	 * @return non-configurable Petri net from this net after executing {@link #configure()}
	 */
	public NetType getConfiguredNet() throws InvalidConfigurationException;

	/**
	 * @return initial marking of the configured net, must not be {@code null}
	 */
	public Marking getConfiguredMarking();
	
	
}
