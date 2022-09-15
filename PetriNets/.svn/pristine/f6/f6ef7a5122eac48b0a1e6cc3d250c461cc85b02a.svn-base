package org.processmining.plugins.petrinet.configurable.ui;

import javax.swing.JComponent;

import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableArc;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableArcWeight;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurablePlace;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurablePlaceMarking;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableTransition;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ParameterizedArc;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ParameterizedPlaceMarking;
import org.processmining.plugins.petrinet.configurable.ui.impl.ConfigurableGraphElement_UI.ConfigurableArc_UI;
import org.processmining.plugins.petrinet.configurable.ui.impl.ConfigurableGraphElement_UI.ConfigurablePlace_UI;
import org.processmining.plugins.petrinet.configurable.ui.impl.ConfigurableGraphElement_UI.ConfigurableTransition_UI;
import org.processmining.plugins.petrinet.configurable.ui.impl.ConfigurableIntegerFeature_UI.ConfigurableArcWeightFeature_UI;
import org.processmining.plugins.petrinet.configurable.ui.impl.ConfigurableIntegerFeature_UI.ConfigurablePlaceMarkingFeature_UI;
import org.processmining.plugins.petrinet.configurable.ui.impl.ParameterizedIntegerFeature_UI.ParameterizedArcWeightFeature_UI;
import org.processmining.plugins.petrinet.configurable.ui.impl.ParameterizedIntegerFeature_UI.ParameterizedPlaceMarkingFeature_UI;

/**
 * Factory to create specific {@link ConfigurableFeature_UI} objects for a given
 * {@link ConfigurableFeature}
 * 
 * @author dfahland
 */
public class ConfigurableFeature_UI_Factory {
	
	/**
	 * @param label
	 * @param feature
	 * @param line_height
	 * @return Panel containing configuration options for the given configurable feature
	 */
	public static ConfigurableFeature_UI<?, ?> 
		getUIforFeature(JComponent root, ConfigurableFeature<?,?> feature)
	{
		ConfigurableFeature_UI<?,?> feature_ui = null;
		int line_height = 40;
		if (feature instanceof ConfigurableTransition) {
			feature_ui =  new ConfigurableTransition_UI(root, (ConfigurableTransition)feature, line_height);
		} else if (feature instanceof ConfigurablePlace) {
			feature_ui =  new ConfigurablePlace_UI(root, (ConfigurablePlace)feature, line_height);
		} else if (feature instanceof ConfigurableArc) {
			feature_ui =  new ConfigurableArc_UI(root, (ConfigurableArc)feature, line_height);
		} else if (feature instanceof ConfigurablePlaceMarking) {
			feature_ui =  new ConfigurablePlaceMarkingFeature_UI(root, (ConfigurablePlaceMarking)feature, line_height);
		} else if (feature instanceof ConfigurableArcWeight) {
			feature_ui =  new ConfigurableArcWeightFeature_UI(root, (ConfigurableArcWeight)feature, line_height);
		} else if (feature instanceof ParameterizedPlaceMarking) {
			feature_ui =  new ParameterizedPlaceMarkingFeature_UI(root, (ParameterizedPlaceMarking)feature, line_height);
		} else if (feature instanceof ParameterizedArc) {
			feature_ui =  new ParameterizedArcWeightFeature_UI(root, (ParameterizedArc)feature, line_height);
		} else {
			return null;
		}
		
		feature_ui.setValues(feature);
		return feature_ui;
	}

}
