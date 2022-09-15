package org.processmining.models.graphbased.directed.petrinet.configurable;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableArcWeight;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurablePlace;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurablePlaceMarking;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableTransition;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementOption;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * A group of features that shall be configured together. *  
 * 
 * @author dfahland
 */
public class ConfigurableFeatureGroup implements ParameterizedFeature<ConfigurableParameter<?>> {
	
	public ConfigurableFeatureGroup(String id) {
		this.id = id;
	}
	
	private final String id;
	private final List<ConfigurableFeature<? extends DirectedGraphElement, ? extends Object>> features = new LinkedList<ConfigurableFeature<? extends DirectedGraphElement,? extends Object>>();

	/**
	 * Add feature to this group.
	 * 
	 * @param feature
	 * @throws InvalidConfigurationException if 
	 */
	public void addFeature(ConfigurableFeature<? extends DirectedGraphElement, ? extends Object> feature) throws InvalidConfigurationException {
		features.add(feature);
	}
	
	/**
	 * @return all features of this group
	 */
	public List<ConfigurableFeature<? extends DirectedGraphElement, ? extends Object>> getFeatures() {
		return features;
	}
	
	/**
	 * @param element
	 * @return list of features configuring the given element
	 */
	public List<ConfigurableFeature<? extends DirectedGraphElement, ? extends Object>> getFeatures(DirectedGraphElement element) {
		List<ConfigurableFeature<? extends DirectedGraphElement, ? extends Object>> elementFeatures = new LinkedList<ConfigurableFeature<? extends DirectedGraphElement,? extends Object>>();
		for (ConfigurableFeature<? extends DirectedGraphElement, ? extends Object> f : features) {
			if (f.getConfiguredElement() == element) {
				elementFeatures.add(f);
			}
		}
		return elementFeatures;
	}
	
	/**
	 * Executes the configuration for this feature group. The configuration may
	 * require to change the parent object.
	 * 
	 * @param parent
	 */
	public void executeConfiguration(ConfigurablePetrinet<? extends PetrinetGraph> parent) {
		for (ConfigurableFeature<? extends DirectedGraphElement, ? extends Object> feature : getFeatures()) {
			if (feature.isStillExecutable(parent)) {
				feature.clearElementVisualization();
				feature.executeConfiguration(parent);
			}
		}
	}
	
	/**
	 * @return ID of this feature
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Clone this feature group and create a new {@link ConfigurableFeature} for each feature in this
	 * group where the {@link ConfigurableFeature#getConfiguredElement()} is updated according to
	 * copies.
	 * 
	 * Every subclass must override this method for proper cloning behavior.
	 * 
	 * @param copies
	 * @return the clone
	 */
	@SuppressWarnings("unchecked")
	public ConfigurableFeatureGroup cloneFor(Map<DirectedGraphElement, DirectedGraphElement> copies) throws InvalidConfigurationException {
		ConfigurableFeatureGroup g = new ConfigurableFeatureGroup(getId());
		for (@SuppressWarnings("rawtypes") ConfigurableFeature f : getFeatures()) {
			g.addFeature(f.cloneFor(copies.get(f.getConfiguredElement())));
		}
		return g;
	}
	
	/**
	 * Create a configurable feature group for the presence of a transition and the weights of
	 * its adjacent arcs.
	 * 
	 * @param t
	 * @return
	 * @throws InvalidConfigurationException
	 */
	public static ConfigurableFeatureGroup createDefaultFeatureGroup(Transition t) throws InvalidConfigurationException {
		ConfigurableFeatureGroup g = new ConfigurableFeatureGroup(ConfigurationUtils.getNodeId(t));
		g.addFeature(new ConfigurableTransition(ConfigurableGraphElementFeature.generateID(t), t, ConfigurableGraphElementFeature.ALL , ConfigurableGraphElementOption.ALLOW));
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : t.getGraph().getInEdges(t)) {
			if (edge instanceof Arc) {
				Arc a = (Arc)edge;
				g.addFeature(new ConfigurableArcWeight(ConfigurableArcWeight.generateID(a), a, 0, a.getWeight()*10, a.getWeight()));
			}
		}
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : t.getGraph().getOutEdges(t)) {
			if (edge instanceof Arc) {
				Arc a = (Arc)edge;
				g.addFeature(new ConfigurableArcWeight(ConfigurableArcWeight.generateID(a), a, 0, a.getWeight()*10, a.getWeight()));
			}
		}
		return g;
	}
	
	/**
	 * Create a configurable feature group for the presence of a place and its initial marking.
	 * 
	 * @param p
	 * @param m
	 * @return
	 * @throws InvalidConfigurationException
	 */
	public static ConfigurableFeatureGroup createDefaultFeatureGroup(Place p, Marking m) throws InvalidConfigurationException {
		ConfigurableFeatureGroup g = new ConfigurableFeatureGroup(ConfigurationUtils.getNodeId(p));
		g.addFeature(new ConfigurablePlace(ConfigurableGraphElementFeature.generateID(p), p, ConfigurableGraphElementFeature.ALL , ConfigurableGraphElementOption.ALLOW));
		int markingValue = (m != null) ? m.occurrences(p) : 0;
		g.addFeature(new ConfigurablePlaceMarking(ConfigurablePlaceMarking.generateID(p), p, 0, (markingValue+1)*10, markingValue));
		return g;
	}
	
	/**
	 * Create default configurable feature groups for all nodes in the net. Each
	 * node gets its own feature group. Arcs are added to the feature groups of
	 * their adjacent transitions.
	 * 
	 * @param net
	 * @param m
	 * @return list of all default configurable feature groups, first the
	 *         transition features, then the place features
	 * @throws InvalidConfigurationException
	 */
	public static List<ConfigurableFeatureGroup> createDefaultFeatureGroups(PetrinetGraph net, Marking m) throws InvalidConfigurationException {
		LinkedList<ConfigurableFeatureGroup> transitionFeatureGroups = new LinkedList<ConfigurableFeatureGroup>();
		for (Transition t : net.getTransitions()) {
			if (t.isInvisible()) continue;
			transitionFeatureGroups.add(createDefaultFeatureGroup(t));
		}
		
		LinkedList<ConfigurableFeatureGroup> placeFeatureGroups = new LinkedList<ConfigurableFeatureGroup>();
		for (Place p : net.getPlaces()) {
			placeFeatureGroups.add(createDefaultFeatureGroup(p, m));
		}
		
		// sort transitions and places individually
		Collections.sort(transitionFeatureGroups, comparator);
		Collections.sort(placeFeatureGroups, comparator);
		
		// then return transitions followed by places
		transitionFeatureGroups.addAll(placeFeatureGroups);
		
		return transitionFeatureGroups;
	}
	
	/**
	 * Default comparator comparing groups by their ids
	 */
	public static Comparator<ConfigurableFeatureGroup> comparator = new Comparator<ConfigurableFeatureGroup>() {
		public int compare(ConfigurableFeatureGroup o1, ConfigurableFeatureGroup o2) {
			return o1.getId().compareTo(o2.getId());
		}
	};
	
	public List<ConfigurableParameter<?>> getInputParameters() {
		LinkedList<ConfigurableParameter<?>> params = new LinkedList<ConfigurableParameter<?>>();
		for (ConfigurableFeature<?, ?> feat : getFeatures()) {
			params.addAll(feat.getInputParameters());
		}
		return params;
	}
	
	public List<ConfigurableParameter<?>> getInputParametersByName() {
		HashMap<String, ConfigurableParameter<?>> params = new HashMap<String, ConfigurableParameter<?>>();
		for (ConfigurableFeature<?, ?> feat : getFeatures()) {
			for (ConfigurableParameter<?> p : feat.getInputParameters()) {
				params.put(p.getId(), p);
			}
		}
		LinkedList<ConfigurableParameter<?>> parameters = new LinkedList<ConfigurableParameter<?>>(params.values());
		return parameters;
	}

	public void setInputParameter(String key, Object value) throws InvalidConfigurationException {
		System.out.println("set "+key+" to "+value);
		List<ConfigurableParameter<? extends Object>> parameters = getInputParameters();
		for (ConfigurableParameter<? extends Object> par : parameters) {
			if (par.getId().equals(key)) {
				System.out.println("setting "+par+" to "+value);
				par.setValue(value);
			}
		}
		for (ConfigurableFeature<?, ?> feature : getFeatures()) {
			feature.updateValue(); // update values of all parameterized features
		}
	}
}
