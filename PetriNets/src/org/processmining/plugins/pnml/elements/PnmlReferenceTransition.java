package org.processmining.plugins.pnml.elements;

import java.util.Map;

import org.processmining.framework.util.Pair;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.pnml.base.Pnml;
import org.xmlpull.v1.XmlPullParser;

/**
 * Basic PNML referenceTransition object.
 * 
 * @author hverbeek
 */
public class PnmlReferenceTransition extends PnmlNode {

	/**
	 * PNML referenceTransition tag.
	 */
	public final static String TAG = "referenceTransition";

	/**
	 * Ref attribute.
	 */
	private String idRef;

	/**
	 * Creates a fresh reference transition object.
	 */
	protected PnmlReferenceTransition() {
		super(TAG);
		idRef = null;
	}

	/**
	 * Imports all known attributes.
	 */
	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		/*
		 * Import all known node attributes.
		 */
		super.importAttributes(xpp, pnml);
		/*
		 * Import ref attribute.
		 */
		importIdRef(xpp, pnml);
	}

	/**
	 * Exports all known attributes.
	 */
	protected String exportAttributes(Pnml pnml) {
		return super.exportAttributes(pnml) + exportIdRef(pnml);
	}

	/**
	 * Imports ref attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importIdRef(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "ref");
		if (value != null) {
			idRef = value;
		}
	}

	/**
	 * Exports ref attribute.
	 * 
	 * @return
	 */
	private String exportIdRef(Pnml pnml) {
		if (idRef != null) {
			return exportAttribute("ref", idRef, pnml);
		}
		return "";
	}

	/**
	 * Check validity. Should have a ref attribute.
	 */
	protected void checkValidity(Pnml pnml) {
		super.checkValidity(pnml);
		if (idRef == null) {
			pnml.log(tag, lineNumber, "Expected ref");
		}
	}

	/**
	 * Converts this reference transition object to a regular Petri net
	 * transition.
	 * 
	 * @param net
	 *            Net to add transition to.
	 * @param subNet
	 *            Sub net to add transition to.
	 * @param map
	 *            Transitions found so far.
	 */
	public void convertToNet(PetrinetGraph net, ExpandableSubNet subNet, Map<String, Transition> map) {
		if (!map.containsKey(id) && map.containsKey(idRef)) {
			map.put(id, map.get(idRef));
		}
	}

	public PnmlReferenceTransition convertFromNet(ExpandableSubNet parent, Transition transition,
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap, GraphLayoutConnection layout) {
		super.convertFromNet(parent, transition, idMap, layout);
		idRef = idMap.get(new Pair<AbstractGraphElement, ExpandableSubNet>(transition, transition.getParent()));
		return this;
	}
}
