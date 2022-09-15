package org.processmining.plugins.pnml.elements.extensions.configurations;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeatureGroup;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.base.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

public class PnmlGroup extends PnmlElement {

	public final static String TAG = "group";
	
	protected String id;
	
	protected Collection<PnmlFeature> features;
	
	protected PnmlGroup(String tag) {
		super(tag);
		id = null;
		features = new HashSet<PnmlFeature>();
	}

	protected PnmlGroup() {
		this(TAG);
	}
	
	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		super.importAttributes(xpp, pnml);
		importId(xpp, pnml);
	}

	private void importId(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "id");
		if (value != null) {
			id = value;
		}
	}

	protected String exportAttributes(Pnml pnml) {
		return super.exportAttributes(pnml) + exportId(pnml);
	}

	private String exportId(Pnml pnml) {
		if (id != null) {
			return exportAttribute("id", id, pnml);
		}
		return "";
	}

	protected boolean importElements(XmlPullParser xpp, Pnml pnml) {
		if (super.importElements(xpp, pnml)) {
			return true;
		}
		if (xpp.getName().equals(PnmlFeature.TAG)) {
			PnmlFeature feature = factory.createPnmlFeature();
			feature.importElement(xpp, pnml);
			features.add(feature);
			return true;
		}
		return false;
	}

	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		for (PnmlFeature feature : features) {
			s += feature.exportElement(pnml);
		}
		return s;
	}

	public void convertToNet(PetrinetGraph net, Map<String, Place> placeMap, Map<String, Transition> transitionMap,
			Map<String, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edgeMap) {
		ConfigurableFeatureGroup group = new ConfigurableFeatureGroup(id);
		for (PnmlFeature feature : features) {
			feature.convertToNet(net, group, placeMap, transitionMap, edgeMap);
		}
		if (net instanceof ConfigurableResetInhibitorNet) {
			ConfigurableResetInhibitorNet configurableNet = (ConfigurableResetInhibitorNet) net;
			try {
				configurableNet.addConfigurableFeatures(group);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public PnmlGroup convertFromNet(ConfigurableFeatureGroup group, Map<AbstractGraphElement, String> map) {
		id = group.getId();
		for (ConfigurableFeature<?, ?> feature : group.getFeatures()) {
			PnmlFeature pnmlFeature = factory.createPnmlFeature();
			pnmlFeature.convertFromNet(feature, map);
			features.add(pnmlFeature);
		}
		return this;
	}
}
