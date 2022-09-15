package org.processmining.plugins.pnml.elements.extensions.configurations;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeatureGroup;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.base.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

public class PnmlConfiguration extends PnmlElement {
	
	public final static String TAG = "configurable";
	
	protected Collection<PnmlGroup> groups;
	
	protected PnmlConfiguration(String tag) {
		super(tag);
		groups = new HashSet<PnmlGroup>();
	}

	protected PnmlConfiguration() {
		this(TAG);
	}

	protected boolean importElements(XmlPullParser xpp, Pnml pnml) {
		if (super.importElements(xpp, pnml)) {
			return true;
		}
		if (xpp.getName().equals(PnmlGroup.TAG)) {
			PnmlGroup group = factory.createPnmlGroup();
			group.importElement(xpp, pnml);
			groups.add(group);
			return true;
		}
		return false;
	}

	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		for (PnmlGroup group : groups) {
			s += group.exportElement(pnml);
		}
		return s;
	}

	public void convertToNet(PetrinetGraph net, Map<String, Place> placeMap, Map<String, Transition> transitionMap,
			Map<String, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edgeMap) {
		for (PnmlGroup group : groups) {
			group.convertToNet(net, placeMap, transitionMap, edgeMap);
		}
	}
	
	public PnmlConfiguration convertFromNet(ConfigurableResetInhibitorNet net, Map<AbstractGraphElement, String> map) {
		for (ConfigurableFeatureGroup group : net.getConfigurableFeatureGroups()) {
			PnmlGroup pnmlGroup = factory.createPnmlGroup();
			pnmlGroup.convertFromNet(group, map);
			groups.add(pnmlGroup);
		}
		return this;
	}

}
