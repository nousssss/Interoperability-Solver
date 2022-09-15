package org.processmining.models.graphbased.directed.petrinet.configurable;

import java.util.LinkedList;
import java.util.List;

import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableArc;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableArcWeight;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurablePlace;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurablePlaceMarking;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableTransition;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ParameterizedArc;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ParameterizedPlaceMarking;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementOption;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableParameterInteger;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public class ConfigurableFeatureFactory {
	
	public static final String PARAM_FEATURE_PRESENCE = "presence";
	public static final String PARAM_FEATURE_MARKING = "initial marking";
	public static final String PARAM_FEATURE_ARC_WEIGHT = "arc weight";
	
	public static final String[] PARAM_OPTIONS_TRANSITION = new String[] { PARAM_FEATURE_PRESENCE };
	public static final String[] PARAM_OPTIONS_PLACE = new String[] { PARAM_FEATURE_PRESENCE, PARAM_FEATURE_MARKING };
	public static final String[] PARAM_OPTIONS_ARC = new String[] { PARAM_FEATURE_PRESENCE, PARAM_FEATURE_ARC_WEIGHT };
	public static final String[] PARAM_OPTIONS_RIARC = new String[] { PARAM_FEATURE_PRESENCE };

	public static ConfigurableFeature<?, ?> createDefaultFeature(DirectedGraphElement netElement, String featureName, boolean parameterized) {

		String id;
		ConfigurableFeature<?, ?> feature = null;

		try {
			if (netElement instanceof Transition) {
				
				if (featureName == ConfigurableFeatureFactory.PARAM_FEATURE_PRESENCE) {
					id = ConfigurableGraphElementFeature.generateID(netElement);
					feature = new ConfigurableTransition(id, (Transition)netElement, ConfigurableGraphElementFeature.ALL, ConfigurableGraphElementOption.ALLOW);
				}
						
			} else if (netElement instanceof Place){
				
				if (featureName == ConfigurableFeatureFactory.PARAM_FEATURE_PRESENCE) {
					id = ConfigurableGraphElementFeature.generateID(netElement);
					feature = new ConfigurablePlace(id, (Place)netElement, ConfigurableGraphElementFeature.ALL, ConfigurableGraphElementOption.ALLOW);
				} else if (featureName == ConfigurableFeatureFactory.PARAM_FEATURE_MARKING) {
					id = ConfigurablePlaceMarking.generateID(netElement);
					if (parameterized) {
						try {
							ConfigurableParameterInteger p = new ConfigurableParameterInteger("k", 0, 5, 1);
							List<ConfigurableParameter<Integer>> params = new LinkedList<ConfigurableParameter<Integer>>();
							params.add(p);
							feature = new ParameterizedPlaceMarking((Place)netElement, 0, 5, "k", params);
						} catch (Exception e) {	}
					} else {
						feature = new ConfigurablePlaceMarking(id, (Place)netElement, 0, 10, 0);
					}
				}
				
			} else if (netElement instanceof PetrinetEdge<?, ?>) {

				if (featureName == ConfigurableFeatureFactory.PARAM_FEATURE_PRESENCE) {
					id = ConfigurableGraphElementFeature.generateID(netElement);
					feature = new ConfigurableArc(id, (PetrinetEdge<?, ?>)netElement, ConfigurableGraphElementFeature.ALL, ConfigurableGraphElementOption.ALLOW);
				} else if ((netElement instanceof Arc) && featureName == ConfigurableFeatureFactory.PARAM_FEATURE_ARC_WEIGHT) {
					id = ConfigurablePlaceMarking.generateID(netElement);
					if (parameterized) {
						try {
							ConfigurableParameterInteger p = new ConfigurableParameterInteger("k", 0, 5, 1);
							List<ConfigurableParameter<Integer>> params = new LinkedList<ConfigurableParameter<Integer>>();
							params.add(p);
							feature = new ParameterizedArc((Arc)netElement, 0, 5, "k", params);
						} catch (Exception e) {	}
					} else {
						feature = new ConfigurableArcWeight(id, (Arc)netElement, 0, 10, 1);
					}
				}
				
			}
			
		} catch (InvalidConfigurationException e) {
			System.err.println(e);
			e.printStackTrace();
		}

		return feature;
	}

}
