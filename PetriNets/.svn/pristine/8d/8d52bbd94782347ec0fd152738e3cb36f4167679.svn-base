package org.processmining.plugins.pnml.elements;

import java.util.ArrayList;
import java.util.List;

import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.base.PnmlElement;
import org.processmining.plugins.pnml.elements.graphics.PnmlAnnotationGraphics;
import org.processmining.plugins.pnml.toolspecific.PnmlToolSpecific;
import org.xmlpull.v1.XmlPullParser;

/**
 * Basic PNML annotation object.
 * 
 * @author hverbeek
 */
public abstract class PnmlAnnotation extends PnmlElement {

	/**
	 * Text element.
	 */
	public PnmlText text;
	/**
	 * Graphics element.
	 */
	protected PnmlAnnotationGraphics graphics;
	/**
	 * ToolSpecifics element.
	 */
	protected List<PnmlToolSpecific> toolSpecificList;

	/**
	 * Creates a fresh annotation object.
	 * 
	 * @param tag
	 *            The tag for the annotation.
	 */
	protected PnmlAnnotation(String tag) {
		super(tag);
		text = null;
		graphics = null;
		toolSpecificList = new ArrayList<PnmlToolSpecific>();
	}

	protected PnmlAnnotation(String text, String tag) {
		super(tag);
		this.text = factory.createPnmlText(text);
		graphics = null;
		toolSpecificList = new ArrayList<PnmlToolSpecific>();
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
		if (xpp.getName().equals(PnmlText.TAG)) {
			/*
			 * Text element. Create text object and import text element.
			 */
			text = factory.createPnmlText();
			text.importElement(xpp, pnml);
			return true;
		}
		if (xpp.getName().equals(PnmlAnnotationGraphics.TAG)) {
			/*
			 * Graphics element. Create graphics object and import graphics
			 * element.
			 */
			graphics = factory.createPnmlAnnotationGraphics();
			graphics.importElement(xpp, pnml);
			return true;
		}
		if (xpp.getName().equals(PnmlToolSpecific.TAG)) {
			/*
			 * Tool specific element. Create tool specific object and import
			 * tool specific element.
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

	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		if (text != null) {
			s += text.exportElement(pnml);
		}
		if (graphics != null) {
			s += graphics.exportElement(pnml);
		}
		for (PnmlToolSpecific toolSpecific : toolSpecificList) {
			s += toolSpecific.exportElement(pnml);
		}
		return s;
	}

	public void convertToNet(PetrinetNode node) {
		/*
		 * Set the name.
		 */
		if (text != null) {
			node.getAttributeMap().put(AttributeMap.LABEL, text.getText());
		}
		/*
		 * Set optional name graphics.
		 */
		if (graphics != null) {
			graphics.convertToNet(node);
		}
	}

	public void convertToNet(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge) {
		/*
		 * Set the name.
		 */
		//edge.getAttributeMap().put(AttributeMap.LABELALONGEDGE, true);
		if (text != null) {
			edge.getAttributeMap().put(AttributeMap.LABEL, text.getText());
		}
		/*
		 * Set optional name graphics.
		 */
		if (graphics != null) {
			graphics.convertToNet(edge);
		}
	}

	public PnmlAnnotation convertFromNet(AbstractGraphElement element) {
		PnmlAnnotation result = null;
		try {
			graphics = factory.createPnmlAnnotationGraphics().convertFromNet(element);
			if ((text.getText().length() > 0) || (graphics != null)) {
				result = this;
			}
		} catch (Exception ex) {
		}
		return result;
	}

}
