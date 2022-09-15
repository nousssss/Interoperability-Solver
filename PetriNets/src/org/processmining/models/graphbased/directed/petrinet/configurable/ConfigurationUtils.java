package org.processmining.models.graphbased.directed.petrinet.configurable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurablePlace;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurablePlaceMarking;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableTransition;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ParameterizedPlaceMarking;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementOption;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableParameterInteger;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

/**
 * Helper class for configurations.
 * 
 * @author dfahland
 *
 */
public class ConfigurationUtils {
	
	/**
	 * @param el
	 * @return unique id of a node to be used as id for a {@link ConfigurableFeature}
	 */
	public static String generateElementIDforFeature(DirectedGraphElement el) {
		String nodeID;
		if (el instanceof PetrinetNode) {
			nodeID = ConfigurationUtils.getNodeId((PetrinetNode)el);
		} else if (el instanceof PetrinetEdge<?, ?>) {
			PetrinetEdge<?, ?> a = (PetrinetEdge<?, ?>)el;
			nodeID = ConfigurationUtils.getNodeId(a.getSource())+" --> "+ConfigurationUtils.getNodeId(a.getTarget());
		} else {
			nodeID = Integer.toString(el.hashCode());
		}
		return nodeID;
	}

	public static String getNodeId(PetrinetNode n) {
		boolean duplicate = false;
		for (PetrinetNode m : n.getGraph().getNodes()) {
			if (m != n && m.getLabel().equals(n.getLabel())) {
				duplicate = true;
				break;
			}
		}
		if (!duplicate) return n.getLabel();
		else return n.getLabel()+"_"+n.getId();
	}
	
	public static List<ConfigurableFeatureGroup> stripFeaturesFromLabels(PetrinetGraph net) {
		List<ConfigurableFeatureGroup> groups = new LinkedList<ConfigurableFeatureGroup>();
		Map<String, List<ConfigurableFeature<?,?>>> group_features = new HashMap<String, List<ConfigurableFeature<?,?>>>();
		
		for (PetrinetNode node : net.getNodes()) {
			stripFeaturesFromLabel(node, group_features);
		}
		
		for (String groupID : group_features.keySet()) {
			try {
				ConfigurableFeatureGroup group = new ConfigurableFeatureGroup(groupID);
				for (ConfigurableFeature<?,?> feature : group_features.get(groupID)) {
					group.addFeature(feature);
				}
				groups.add(group);
			} catch (InvalidConfigurationException e) {
				System.err.println("Could not create feature group "+groupID);
				e.printStackTrace();
			}
		}
		return groups;
	}
	
	public static void stripFeaturesFromLabel(PetrinetNode node, Map<String, List<ConfigurableFeature<?,?>>> group_features) {
		String label = node.getLabel();
		String expressions[] = label.split("#");
		if (expressions.length <= 1) return;
		for (int i=1; i<expressions.length; i++) {
			
			try {
				String subs[] = expressions[i].split(";");
				
				String groupID = null;
				String featureID = null;
				String presenceValues = null;
				String countValue = null;
				String defaultValue = null;
				String expressionValue = null;
				String parametersValue = null;
				for (String sub : subs) {
					String attr[] = sub.split("=");
					if (attr[0].contains("groupID")) groupID = attr[1];
					if (attr[0].contains("featureID")) featureID = attr[1];
					if (attr[0].contains("presence")) presenceValues = attr[1];
					if (attr[0].contains("token")) countValue = attr[1];
					if (attr[0].contains("default")) defaultValue = attr[1];
					if (attr[0].contains("exp")) expressionValue = attr[1];
					if (attr[0].contains("var")) parametersValue = attr[1];
				}
				
				ConfigurableFeature<?,?> feature = null;
				if (presenceValues != null) {
					if (featureID == null) featureID = ConfigurableGraphElementFeature.generateID(node);
	
					List<ConfigurableGraphElementOption> allowed = new LinkedList<ConfigurableGraphElementOption>();
					if (presenceValues.contains("A")) allowed.add(ConfigurableGraphElementOption.ALLOW);
					if (presenceValues.contains("B")) allowed.add(ConfigurableGraphElementOption.BLOCK);
					if (presenceValues.contains("S")) allowed.add(ConfigurableGraphElementOption.SKIP);
					ConfigurableGraphElementOption allowedArr[] = allowed.toArray(new ConfigurableGraphElementOption[allowed.size()]);
					
					ConfigurableGraphElementOption defaultPresence = allowedArr[0];
					if (defaultValue != null) {
						if (defaultValue.equals("A")) defaultPresence = ConfigurableGraphElementOption.ALLOW;
						if (defaultValue.equals("B")) defaultPresence = ConfigurableGraphElementOption.BLOCK;
						if (defaultValue.equals("S")) defaultPresence = ConfigurableGraphElementOption.SKIP;
					}
					
					if (node instanceof Transition) {
						feature = new ConfigurableTransition(featureID, (Transition)node, allowedArr, defaultPresence);
					} else if (node instanceof Place) {
						feature = new ConfigurablePlace(featureID, (Place)node, allowedArr, defaultPresence);
					}
				} else if (countValue != null) {
					int minStart = countValue.indexOf('[');
					int minEnd = countValue.indexOf(',');
					int maxEnd = countValue.indexOf(']');
					if (minStart >= 0 && minEnd > minStart && maxEnd > minEnd) {
						int minVal = Integer.parseInt(countValue.substring(minStart+1, minEnd));
						int maxVal = Integer.parseInt(countValue.substring(minEnd+1, maxEnd));
						int defaultVal = Integer.parseInt(defaultValue);
						
						if (node instanceof Place) {
							
							if (featureID == null) featureID = ConfigurablePlaceMarking.generateID(node);
							
							if (expressionValue == null) {
								feature = new ConfigurablePlaceMarking(featureID, (Place)node, minVal, maxVal, defaultVal);
							} else {
								String params[] = parametersValue.split(",");
								List<ConfigurableParameter<Integer>> inputParameters = new LinkedList<ConfigurableParameter<Integer>>();
								for (String param : params) {
									inputParameters.add(new ConfigurableParameterInteger(param, minVal, maxVal, defaultVal));
								}
								feature = new ParameterizedPlaceMarking((Place)node, minVal, maxVal, expressionValue, inputParameters);
							}
						}
					}
				}
				if (groupID == null) groupID = featureID;
				
				if (feature != null) {
					if (!group_features.containsKey(groupID)) group_features.put(groupID, new LinkedList<ConfigurableFeature<?,?>>());
					group_features.get(groupID).add(feature);
				}
			} catch (UnknownFunctionException e) {
				System.err.println(e);
				e.printStackTrace();
			} catch (UnparsableExpressionException e) {
				System.err.println(e);
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				System.err.println(e);
				e.printStackTrace();
			}
		}
		node.getAttributeMap().put(AttributeMap.LABEL, expressions[0]);
	}
	
}
