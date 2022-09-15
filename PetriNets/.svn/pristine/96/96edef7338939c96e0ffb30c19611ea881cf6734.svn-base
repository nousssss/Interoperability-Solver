package org.processmining.models.graphbased.directed.petrinet.configurable.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingConstants;

import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeatureGroup;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.Configuration;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.InhibitorArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.ResetArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.ResetInhibitorNetImpl;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * A configurable {@link ResetInhibitorNet}.
 * 
 * @author dfahland
 *
 */
public class ConfigurableResetInhibitorNet extends ResetInhibitorNetImpl implements ConfigurablePetrinet<ResetInhibitorNet> {

	private Marking configuredMarking = new Marking();
	
	/**
	 * Create an empty configurable reset inhibitor net. 
	 * @param label
	 * @param net
	 */	
	public ConfigurableResetInhibitorNet(String label) {
		super(label);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
	}

	/**
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.old.ConfigurablePetrinet#cloneFrom(org.processmining.models.graphbased.directed.petrinet.PetrinetGraph, java.util.List, org.processmining.models.connections.GraphLayoutConnection, org.processmining.models.connections.GraphLayoutConnection)
	 */
	public void cloneFrom(ResetInhibitorNet net, Marking netMarking, List<ConfigurableFeatureGroup> configurations, GraphLayoutConnection oldLayout, GraphLayoutConnection newLayout) throws Exception {
		HashMap<DirectedGraphElement, DirectedGraphElement> mapping = new HashMap<DirectedGraphElement, DirectedGraphElement>();

		for (Transition t : net.getTransitions()) {
			Transition copy = addTransition(t.getLabel()); 
			for (String key : t.getAttributeMap().keySet()) {
				copy.getAttributeMap().put(key, t.getAttributeMap().get(key));
			}
			copy.setInvisible(t.isInvisible());
			LayoutUtils.copyLayout(t, oldLayout, copy, newLayout);
			mapping.put(t, copy);
		}
		for (Place p : net.getPlaces()) {
			Place copy = addPlace(p.getLabel());
			for (String key : p.getAttributeMap().keySet()) {
				copy.getAttributeMap().put(key, p.getAttributeMap().get(key));
			}
			LayoutUtils.copyLayout(p, oldLayout, copy, newLayout);
			mapping.put(p, copy);
			
			configuredMarking.add(copy, netMarking.occurrences(p)); // copy marking
		}
		
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> a : net.getEdges()) {
			PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> copy = null;
			if (a instanceof InhibitorArc) {
				copy = addInhibitorArc((Place) mapping.get(a.getSource()), (Transition) mapping.get(a.getTarget()), a.getLabel());

			} else if (a instanceof ResetArc) {
				copy = addResetArc((Place) mapping.get(a.getSource()), (Transition) mapping.get(a.getTarget()),
						a.getLabel());
			} else if (a instanceof Arc){
				copy = addArcPrivate((PetrinetNode) mapping.get(a.getSource()), (PetrinetNode) mapping.get(a
						.getTarget()), ((Arc)a).getWeight(), a.getParent());	
			}
			if (copy != null) {
				LayoutUtils.copyLayout(a, oldLayout, copy, newLayout);
				for (String key : a.getAttributeMap().keySet()) {
					copy.getAttributeMap().put(key, a.getAttributeMap().get(key));
				}
				mapping.put(a, copy);
			}
		}
		
		for (ConfigurableFeatureGroup f : configurations) {
			addConfigurableFeatures(f.cloneFor(mapping));
		}
		
		getAttributeMap().clear();
		AttributeMap map = net.getAttributeMap();
		for (String key : map.keySet()) {
			getAttributeMap().put(key, map.get(key));
		}
	}
	
	/**
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.old.ConfigurablePetrinet#cloneFrom(org.processmining.models.graphbased.directed.petrinet.configurable.old.ConfigurablePetrinet, org.processmining.models.connections.GraphLayoutConnection, org.processmining.models.connections.GraphLayoutConnection)
	 */
	public void cloneFrom(ConfigurablePetrinet<ResetInhibitorNet> net, GraphLayoutConnection oldLayout, GraphLayoutConnection newLayout) throws Exception {
		cloneFrom((ResetInhibitorNet)net, net.getConfiguredMarking(), net.getConfigurableFeatureGroups(), oldLayout, newLayout);
	}
	
	/**
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet#configure(List))
	 */
	public synchronized void configure(List<Configuration> configurations) throws InvalidConfigurationException {
		
		for (Configuration config : configurations) {
			
			System.out.println("configuring: "+config);
			
			try {
				ConfigurableFeatureGroup group = getConfigurableFeatureGroup(config.getFeatureGroupId());
				for (String key : config.keySet()) {
					group.setInputParameter(key, config.get(key));	
				}
				
				group.executeConfiguration(this);
				configurableFeatures.remove(group);
				
			} catch (Exception e) {
				throw new InvalidConfigurationException("Invalid configuration for "+config.getFeatureGroupId(), e);
			}
		}
	}
	
	/**
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet#configureDefault()
	 */
	public synchronized void configureDefault() throws InvalidConfigurationException {
		
		List<Configuration> allConfigurations = new LinkedList<Configuration>();
		for (ConfigurableFeatureGroup group : getConfigurableFeatureGroups()) {
			allConfigurations.add(new Configuration(group.getId()));
		}

		configure(allConfigurations);
	}
	
		

	// all configuration features of this configurable Petri net 
	private final List<ConfigurableFeatureGroup> configurableFeatures = new LinkedList<ConfigurableFeatureGroup>();

	/**
	 * @see org.processmining.models.graphbased.directed.petrinet.configurable.old.ConfigurablePetrinet#getConfigurationFeatures()
	 */
	public List<ConfigurableFeatureGroup> getConfigurableFeatureGroups() {
		return configurableFeatures;
	}
	
	/**
	 * @param id
	 * @return configuration feature of the given id
	 */
	private ConfigurableFeatureGroup getConfigurableFeatureGroup(String id) {
		for (ConfigurableFeatureGroup feature : configurableFeatures) {
			if (feature.getId().equals(id)) return feature;
		}
		return null;
	}

	public void addConfigurableFeature(String id, ConfigurableFeature<? extends DirectedGraphElement, ?> feature) throws InvalidConfigurationException {
		ConfigurableFeatureGroup group = new ConfigurableFeatureGroup(id);
		group.addFeature(feature);
		addConfigurableFeatures(group);
	}

	public void addConfigurableFeatures(ConfigurableFeatureGroup group) throws InvalidConfigurationException {
		if (getConfigurableFeatureGroup(group.getId()) != null) throw new InvalidConfigurationException("Feature group with "+group.getId()+" already exists");
		configurableFeatures.add(group);
		for (ConfigurableFeature<?, ?> feature : group.getFeatures()) {
			feature.updateElementVisualization();
		}
	}

	public ResetInhibitorNet getConfiguredNet() throws InvalidConfigurationException {
		configureDefault();
		return this;
	}

	public Marking getConfiguredMarking() {
		return configuredMarking;
	}

	
}
