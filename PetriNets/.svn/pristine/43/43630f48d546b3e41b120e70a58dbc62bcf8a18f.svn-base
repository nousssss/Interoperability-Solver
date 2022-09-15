package org.processmining.plugins.pnml.elements.extensions.opennet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.processmining.framework.util.Pair;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.opennet.OpenNet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.base.PnmlElement;
import org.processmining.plugins.pnml.elements.PnmlNet;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         PNMLmodule element. Used by Open net PNML.
 * 
 *         Contains prots, a net, and final markings for the Open net. Note that
 *         only a single net is expected. In case of multiple nets, the last net
 *         will be used.
 */
/**
 * @author hverbeek
 * 
 */
public class PnmlModule extends PnmlElement {

	public final static String TAG = "module";

	/*
	 * The ports.
	 */
	private PnmlPorts ports;
	/*
	 * The net.
	 */
	private PnmlNet net;
	/*
	 * The final markings.
	 */
	private PnmlFinalMarkings finalMarkings;

	/**
	 * Constructs a Pnml module handler.
	 */
	protected PnmlModule() {
		super(TAG);

		ports = null;
		net = null;
		finalMarkings = null;
	}

	public String getName(String defaultName) {
		if (net != null) {
			return net.getName(defaultName);
		}
		return defaultName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.plugins.pnml.PnmlElement#importElements(org.xmlpull
	 * .v1.XmlPullParser, org.processmining.plugins.pnml.Pnml)
	 * 
	 * Imports ports, net, and/or finalmarkings elements.
	 */
	protected boolean importElements(XmlPullParser xpp, Pnml pnml) {
		if (super.importElements(xpp, pnml)) {
			/*
			 * Start tag corresponds to a known child element of a PNML
			 * annotation.
			 */
			return true;
		}
		if (xpp.getName().equals(PnmlPorts.TAG)) {
			ports = factory.createPnmlPorts();
			ports.importElement(xpp, pnml);
			return true;
		}
		if (xpp.getName().equals(PnmlNet.TAG)) {
			net = factory.createPnmlNet();
			net.importElement(xpp, pnml);
			return true;
		}
		if (xpp.getName().equals(PnmlFinalMarkings.TAG)) {
			finalMarkings = factory.createPnmlFinalMarkings();
			finalMarkings.importElement(xpp, pnml);
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.processmining.plugins.pnml.PnmlElement#exportElements()
	 * 
	 * Gets the PNML string for the module.
	 */
	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		if (ports != null) {
			s += ports.exportElement(pnml);
		}
		if (net != null) {
			s += net.exportElement(pnml);
		}
		if (finalMarkings != null) {
			s += finalMarkings.exportElement(pnml);
		}
		return s;
	}

	/**
	 * Converts the PNML module into an Open net.
	 * 
	 * @param openNet
	 *            The Open net to store the results in.
	 * @param marking
	 *            The marking to store the inital marking in.
	 * @param pnml
	 *            The Pnml handler.
	 */
	public void convertToOpenNet(OpenNet openNet, Marking marking, Pnml pnml, GraphLayoutConnection layout) {
		/*
		 * Convert all ports.
		 */
		if (ports != null) {
			ports.convertToOpenNet(openNet);
		}
		/*
		 * Convert the net.
		 */
		Map<String, Place> placeMap = new HashMap<String, Place>();
		Map<String, Transition> transitionMap = new HashMap<String, Transition>();
		Map<String, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edgeMap = new HashMap<String, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>>();
		if (net != null) {
			net.convertToNet(openNet, marking, new HashSet<Marking>(), placeMap, transitionMap, edgeMap, layout);
		}
		/*
		 * Convert all final markings.
		 */
		if (finalMarkings != null) {
			finalMarkings.convertToOpenNet(openNet, placeMap);
		}
		/*
		 * Check whether we need to do an auto-layout. If all nodes have a
		 * proper position, then we do not need an auto-layout.
		 */
		pnml.setLayout(openNet, layout);
	}

	/**
	 * Converts the given Open net with the given initial marking into a Pnml
	 * module. Typically, this conversion is done to export the Open net and
	 * marking to PNML file lateron.
	 * 
	 * @param openNet
	 *            The given Open net.
	 * @param marking
	 *            The initial marking of the given Open net.
	 * @return The Pnml module.
	 */
	public PnmlModule convertFromOpenNet(OpenNet openNet, Marking marking, Map<String, AbstractGraphElement> idMap,
			GraphLayoutConnection layout) {
		/*
		 * Create an (initial) map from some node in some subnet to a unique id.
		 */
		Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> map = new HashMap<Pair<AbstractGraphElement, ExpandableSubNet>, String>();
		/*
		 * Convert all ports. Port nodes will be added to the map.
		 */
		ports = factory.createPnmlPorts();
		ports.convertFromOpenNet(openNet.getInterface(), map);
		/*
		 * Convert the net. Petri net nodes will be added to the map. Port nodes
		 * will be checked for their id's.
		 */
		net = factory.createPnmlNet();
		net.convertFromNet(openNet, marking, new HashSet<Marking>(), map, 1, idMap, layout);
		/*
		 * Convert the final markings. Places will be checked for their id's
		 * using the map.
		 */
		finalMarkings = factory.createPnmlFinalMarkings();
		finalMarkings.convertFromOpenNet(openNet.getPlaces(), openNet.getFinalMarkings(), map);
		return this;
	}

	public void setName(String name) {
		if (net != null) {
			net.setName(name);
		}
	}
}
