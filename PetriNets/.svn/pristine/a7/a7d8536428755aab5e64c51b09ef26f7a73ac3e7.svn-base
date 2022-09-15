package org.processmining.plugins.pnml.elements.extensions.opennet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.opennet.OpenNet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.base.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

public class PnmlFinalMarking extends PnmlElement {

	public final static String TAG = "marking";

	private final List<PnmlMarkedPlace> markedPlaceList;

	protected PnmlFinalMarking() {
		super(TAG);

		markedPlaceList = new ArrayList<PnmlMarkedPlace>();
	}

	protected boolean importElements(XmlPullParser xpp, Pnml pnml) {
		if (super.importElements(xpp, pnml)) {
			/*
			 * Start tag corresponds to a known child element of a PNML
			 * annotation.
			 */
			return true;
		}
		if (xpp.getName().equals(PnmlMarkedPlace.TAG)) {
			PnmlMarkedPlace markedPlace = factory.createPnmlMarkedPlace();
			markedPlace.importElement(xpp, pnml);
			markedPlaceList.add(markedPlace);
			return true;
		}
		return false;
	}

	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		for (PnmlMarkedPlace markedPlace : markedPlaceList) {
			s += markedPlace.exportElement(pnml);
		}
		return s;
	}

	public void convertToOpenNet(OpenNet openNet, Map<String, Place> placeMap) {
		Marking finalMarking = new Marking();

		for (PnmlMarkedPlace markedPlace : markedPlaceList) {
			markedPlace.convertToOpenNet(finalMarking, placeMap);
		}
		openNet.addFinalMarking(finalMarking);
	}

	public void convertToNet(PetrinetGraph net, Map<String, Place> placeMap, Collection<Marking> finalMarkings) {
		Marking finalMarking = new Marking();

		for (PnmlMarkedPlace markedPlace : markedPlaceList) {
			markedPlace.convertToOpenNet(finalMarking, placeMap);
		}
		finalMarkings.add(finalMarking);
	}

	public PnmlFinalMarking convertFromOpenNet(Collection<? extends Place> places, Marking marking,
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> map) {
		for (Place place : places) {
			PnmlMarkedPlace markedPlace = factory.createPnmlMarkedPlace();
			ExpandableSubNet subnet = place.getParent();
			Pair<AbstractGraphElement, ExpandableSubNet> key = new Pair<AbstractGraphElement, ExpandableSubNet>(place,
					subnet);
			String id = map.get(key);
			markedPlace.convertFromOpenNet(id, marking.occurrences(place));
			markedPlaceList.add(markedPlace);
		}
		return this;
	}
}