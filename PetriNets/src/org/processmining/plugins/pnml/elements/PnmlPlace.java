package org.processmining.plugins.pnml.elements;

import java.awt.geom.Point2D;
import java.util.Map;

import org.processmining.framework.util.Pair;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.elements.extensions.PnmlInitialMarking;
import org.processmining.plugins.pnml.toolspecific.PnmlToolSpecific;
import org.xmlpull.v1.XmlPullParser;

/**
 * Basic PNML place object.
 * 
 * @author hverbeek
 */
public class PnmlPlace extends PnmlNode {

	/**
	 * PNML place tag.
	 */
	public final static String TAG = "place";

	/**
	 * Initial marking of place.
	 */
	private PnmlInitialMarking initialMarking;

	/**
	 * Creates a fresh PNML place.
	 */
	protected PnmlPlace() {
		super(TAG);
		initialMarking = null;
	}

	/**
	 * Checks whether the current start tag is known. If known, it imports the
	 * corresponding child element and returns true. Otherwise, it returns
	 * false.
	 * 
	 * @return Whether the start tag was known.
	 */
	protected boolean importElements(XmlPullParser xpp, Pnml pnml) {
		if (super.importElements(xpp, pnml)) {
			/*
			 * Start tag corresponds to a known child element of a PNML node.
			 */
			return true;
		}
		if (xpp.getName().equals(PnmlInitialMarking.TAG)) {
			/*
			 * Initial marking element. Create an initial marking object and
			 * import initial marking element.
			 */
			initialMarking = factory.createPnmlInitialMarking();
			initialMarking.importElement(xpp, pnml);
			return true;
		}
		/*
		 * Unknown tag.
		 */
		return false;
	}

	/**
	 * Exports all child elements.
	 */
	protected String exportElements(Pnml pnml) {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements(pnml);
		/*
		 * Export initial marking.
		 */
		if (initialMarking != null) {
			s += initialMarking.exportElement(pnml);
		}
		return s;
	}

	/**
	 * Converts this place to a Petri net place.
	 * 
	 * @param net
	 *            Net to add this place to.
	 * @param subNet
	 *            Sub net to add this place to.
	 * @param marking
	 *            Marking to add the initial marking of this place to.
	 * @param map
	 *            Places found so far.
	 */
	public void convertToNet(PetrinetGraph net, ExpandableSubNet subNet, Marking marking, Map<String, Place> map,
			Point2D.Double displacement, GraphLayoutConnection layout) {
		/*
		 * Add the place to the net.
		 */
		Place place = net.addPlace((((name != null) && (name.text != null)) ? name.text.getText() : id), subNet);
		super.convertToNet(subNet, place, displacement, layout);
		/*
		 * Add the initial marking of this place to the marking.
		 */
		int weight = 0;
		if (initialMarking != null) {
			weight = initialMarking.getInitialMarking();
		}
		if (weight != 0) {
			marking.add(place, weight);
		}
		/*
		 * Register new place found.
		 */
		map.put(id, place);

		for (PnmlToolSpecific toolSpecific : toolSpecificList) {
			toolSpecific.convertToNet(place);
		}
	}

	public PnmlPlace convertFromNet(Marking marking, ExpandableSubNet parent, Place place,
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap, GraphLayoutConnection layout) {
		super.convertFromNet(parent, place, idMap, layout);
		initialMarking = factory.createPnmlInitialMarking().convertFromNet(marking, place);
		PnmlToolSpecific toolSpecific = factory.createPnmlToolSpecific();
		toolSpecific.convertFromNet(place);
		toolSpecificList.add(toolSpecific);
		return this;
	}

}
