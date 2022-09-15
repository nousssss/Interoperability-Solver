package org.processmining.plugins.pnml.elements;

import java.util.ArrayList;
import java.util.List;

import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.base.PnmlElement;
import org.processmining.plugins.pnml.toolspecific.PnmlToolSpecific;
import org.xmlpull.v1.XmlPullParser;

/**
 * Basic PNML object.
 * 
 * @author hverbeek
 * 
 */
public abstract class PnmlBasicObject extends PnmlElement {

	/**
	 * Name element.
	 */
	protected PnmlName name;
	/**
	 * ToolSpecifics elements (there may be multiple).
	 */
	protected List<PnmlToolSpecific> toolSpecificList;

	/**
	 * Creates a fresh basic PNML object.
	 * 
	 * @param tag
	 */
	protected PnmlBasicObject(String tag) {
		super(tag);
		name = null;
		toolSpecificList = new ArrayList<PnmlToolSpecific>();
	}

	public String getName(String defaultName) {
		if (name != null) {
			return name.getName(defaultName);
		}
		return defaultName;
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
			return true;
		}
		if (xpp.getName().equals(PnmlName.TAG)) {
			/*
			 * Name element. Create name object and import name element.
			 */
			name = factory.createPnmlName();
			name.importElement(xpp, pnml);
			return true;
		}
		if (xpp.getName().equals(PnmlToolSpecific.TAG)) {
			/*
			 * Tool specifics element. Create tool specifics object and import
			 * tool specifics element.
			 */
			PnmlToolSpecific toolSpecific = factory.createPnmlToolSpecific();
			toolSpecific.importElement(xpp, pnml);
			toolSpecificList.add(toolSpecific);
			return true;
		}
		/*
		 * Unknown start tag.
		 */
		return false;
	}

	/**
	 * Exports all elements.
	 */
	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		if ((name != null) && (name.text != null)) {
			s += name.exportElement(pnml);
		}
		for (PnmlToolSpecific toolSpecific : toolSpecificList) {
			s += toolSpecific.exportElement(pnml);
		}
		return s;
	}

	public void convertToNet(PetrinetNode node) {
		if (name != null) {
			name.convertToNet(node);
		}
	}

	public void convertToNet(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge) {
		if (name != null) {
			name.convertToNet(edge);
		}
	}

	public PnmlBasicObject convertFromNet(String label) {
		PnmlBasicObject result = null;
		try {
			name = factory.createPnmlName(label);
			//toolSpecifics = new ArrayList<PnmlToolSpecific> ();
			result = this;
		} catch (Exception ex) {
		}
		return result;
	}
}
