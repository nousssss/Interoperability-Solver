package org.processmining.plugins.pnml.elements.extensions.configurations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeatureGroup;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableArc;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableArcWeight;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurablePlace;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurablePlaceMarking;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableTransition;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ParameterizedArc;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ParameterizedPlaceMarking;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementOption;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableIntegerFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ParameterizedIntegerFeature;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.pnml.base.Pnml;
import org.xmlpull.v1.XmlPullParser;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class PnmlFeature extends PnmlParameter {

	public final static String TAG = "feature";

	protected String netElement;
	protected String type;
	protected String expression;

	protected Collection<PnmlParameter> parameters;

	protected PnmlFeature(String tag) {
		super(tag);
		parameters = new HashSet<PnmlParameter>();
	}

	protected PnmlFeature() {
		this(TAG);
	}

	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		super.importAttributes(xpp, pnml);
		importNetElement(xpp, pnml);
		importType(xpp, pnml);
		importExpression(xpp, pnml);

		if (name == null) {
			name = type + "_" + netElement;
		}
	}

	private void importNetElement(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "netElement");
		if (value != null) {
			netElement = value;
		}
	}

	private void importType(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "type");
		if (value != null) {
			type = value;
		}
	}

	private void importExpression(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "expression");
		if (value != null) {
			expression = value;
		}
	}

	protected String exportAttributes(Pnml pnml) {
		return super.exportAttributes(pnml) + exportNetElement(pnml) + exportType(pnml) + exportExpression(pnml);
	}

	private String exportNetElement(Pnml pnml) {
		if (netElement != null) {
			return exportAttribute("netElement", netElement, pnml);
		}
		return "";
	}

	private String exportType(Pnml pnml) {
		if (type != null) {
			return exportAttribute("type", type, pnml);
		}
		return "";
	}

	private String exportExpression(Pnml pnml) {
		if (expression != null) {
			return exportAttribute("expression", expression, pnml);
		}
		return "";
	}

	protected boolean importElements(XmlPullParser xpp, Pnml pnml) {
		if (super.importElements(xpp, pnml)) {
			return true;
		}
		if (xpp.getName().equals(PnmlParameter.TAG)) {
			PnmlParameter parameter = factory.createPnmlParameter();
			parameter.importElement(xpp, pnml);
			parameters.add(parameter);
			return true;
		}
		return false;
	}

	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		for (PnmlParameter parameter : parameters) {
			s += parameter.exportElement(pnml);
		}
		return s;
	}

	public void convertToNet(PetrinetGraph net, ConfigurableFeatureGroup group, Map<String, Place> placeMap,
			Map<String, Transition> transitionMap,
			Map<String, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edgeMap) {
		if (type.equalsIgnoreCase("presence")) {
			try {
				ConfigurableGraphElementOption[] options = getOptionValues(allowedVals.split(" "));
				ConfigurableGraphElementOption defaultOption = getOptionValue(defaultVal);
				if (placeMap.containsKey(netElement)) {
					/*
					 * Refers to place
					 */
					group.addFeature(new ConfigurablePlace(name, placeMap.get(netElement), options, defaultOption));
				} else if (transitionMap.containsKey(netElement)) {
					/*
					 * Refers to transition
					 */
					group.addFeature(new ConfigurableTransition(name, transitionMap.get(netElement), options,
							defaultOption));
				} else if (edgeMap.containsKey(netElement)) {
					/*
					 * Refers to edge.
					 */
					PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge = edgeMap.get(netElement);
					group.addFeature(new ConfigurableArc(name, edge, options, defaultOption));
				}
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		} else if (type.equalsIgnoreCase("weight")) {
			if (expression == null) {
				try {
					int min = Integer.parseInt(minVal);
					int max = Integer.parseInt(maxVal);
					int def = Integer.parseInt(defaultVal);
					if (edgeMap.containsKey(netElement)) {
						/*
						 * Refers to edge.
						 */
						if (edgeMap.get(netElement) instanceof Arc) {
							Arc arc = (Arc) edgeMap.get(netElement);
							group.addFeature(new ConfigurableArcWeight(name, arc, min, max, def));
						}
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				}
			} else {
				try {
					int min = Integer.parseInt(minVal);
					int max = Integer.parseInt(maxVal);
					List<ConfigurableParameter<Integer>> parameterList = new ArrayList<ConfigurableParameter<Integer>>();
					for (PnmlParameter parameter : parameters) {
						parameter.convertToNet(parameterList);
					}
					if (edgeMap.containsKey(netElement)) {
						/*
						 * Refers to arc.
						 */
						if (edgeMap.get(netElement) instanceof Arc) {
							Arc arc = (Arc) edgeMap.get(netElement);
							group.addFeature(new ParameterizedArc(name, arc, min, max, expression, parameterList));
						}
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				} catch (UnknownFunctionException e) {
					e.printStackTrace();
				} catch (UnparsableExpressionException e) {
					e.printStackTrace();
				}
			}
		} else if (type.equalsIgnoreCase("tokens")) {
			if (expression == null) {
				try {
					int min = Integer.parseInt(minVal);
					int max = Integer.parseInt(maxVal);
					int def = Integer.parseInt(defaultVal);
					if (placeMap.containsKey(netElement)) {
						/*
						 * Refers to place.
						 */
						group.addFeature(new ConfigurablePlaceMarking(name, placeMap.get(netElement), min, max, def));
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				}
			} else {
				try {
					int min = Integer.parseInt(minVal);
					int max = Integer.parseInt(maxVal);
					List<ConfigurableParameter<Integer>> parameterList = new ArrayList<ConfigurableParameter<Integer>>();
					for (PnmlParameter parameter : parameters) {
						parameter.convertToNet(parameterList);
					}
					if (placeMap.containsKey(netElement)) {
						/*
						 * Refers to place.
						 */
						group.addFeature(new ParameterizedPlaceMarking(name, placeMap.get(netElement), min, max,
								expression, parameterList));
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				} catch (UnknownFunctionException e) {
					e.printStackTrace();
				} catch (UnparsableExpressionException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private ConfigurableGraphElementOption[] getOptionValues(String[] values) {
		ConfigurableGraphElementOption[] options;
		int n = 0;
		for (int i = 0; i < values.length; i++) {
			if (getOptionValue(values[i]) != null) {
				n++;
			}
		}
		options = new ConfigurableGraphElementOption[n];
		n = 0;
		for (int i = 0; i < values.length; i++) {
			if (n < options.length) {
				options[n] = getOptionValue(values[i]);
				if (options[n] != null) {
					n++;
				}
			}
		}
		return options;
	}

	private ConfigurableGraphElementOption getOptionValue(String value) {
		ConfigurableGraphElementOption option = null;
		if (value.equalsIgnoreCase("allow")) {
			option = ConfigurableGraphElementOption.ALLOW;
		} else if (value.equalsIgnoreCase("block")) {
			option = ConfigurableGraphElementOption.BLOCK;
		} else if (value.equalsIgnoreCase("skip")) {
			option = ConfigurableGraphElementOption.SKIP;
		}
		return option;
	}

	public PnmlFeature convertFromNet(ConfigurableFeature<?, ?> feature, Map<AbstractGraphElement, String> map) {
		if (feature instanceof ConfigurablePlace || feature instanceof ConfigurableTransition
				|| feature instanceof ConfigurableArc) {
			ConfigurableGraphElementFeature<?> graphElement = (ConfigurableGraphElementFeature<?>) feature;
			netElement = map.get(feature.getConfiguredElement());
			type = "presence";
			allowedVals = getOptionValues(graphElement.getDiscreteDomain());
			defaultVal = getOptionValue(graphElement.getValue());
		} else if (feature instanceof ConfigurableArcWeight || feature instanceof ParameterizedArc) {
			ConfigurableIntegerFeature<?> integerFeature = (ConfigurableIntegerFeature<?>) feature;
			netElement = map.get(integerFeature.getConfiguredElement());
			type = "weight";
			minVal = String.valueOf(integerFeature.getIntervalMin());
			maxVal = String.valueOf(integerFeature.getIntervalMax());
			defaultVal = String.valueOf(integerFeature.getValue());
		} else if (feature instanceof ConfigurablePlaceMarking || feature instanceof ParameterizedPlaceMarking) {
			ConfigurableIntegerFeature<?> integerFeature = (ConfigurableIntegerFeature<?>) feature;
			netElement = map.get(integerFeature.getConfiguredElement());
			type = "tokens";
			minVal = String.valueOf(integerFeature.getIntervalMin());
			maxVal = String.valueOf(integerFeature.getIntervalMax());
			defaultVal = String.valueOf(integerFeature.getValue());
		}
		name = feature.getId();
		if (feature instanceof ParameterizedArc || feature instanceof ParameterizedPlaceMarking) {
			ParameterizedIntegerFeature<?> integerFeature = (ParameterizedIntegerFeature<?>) feature;
			expression = integerFeature.getExpression();
			for (ConfigurableParameter<Integer> parameter : integerFeature.getInputParameters()) {
				PnmlParameter pnmlParameter = factory.createPnmlParameter();
				pnmlParameter.convertFromNet(parameter);
				parameters.add(pnmlParameter);
			}
		}
		return this;
	}

	private String getOptionValues(ConfigurableGraphElementOption[] options) {
		String values = "";
		String space = "";
		for (ConfigurableGraphElementOption option : options) {
			values += space + getOptionValue(option);
			space = " ";
		}
		return values;
	}

	private String getOptionValue(ConfigurableGraphElementOption option) {
		switch (option) {
			case ALLOW : {
				return "allow";
			}
			case BLOCK : {
				return "block";
			}
			case SKIP : {
				return "skip";
			}
		}
		return "";
	}

}
