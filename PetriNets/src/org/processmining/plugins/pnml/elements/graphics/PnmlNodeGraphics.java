package org.processmining.plugins.pnml.elements.graphics;

import java.awt.geom.Point2D;

import org.processmining.framework.util.Pair;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.base.Pnml.PnmlType;
import org.processmining.plugins.pnml.base.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * PNML node graphics.
 * 
 * @author hverbeek
 */
public class PnmlNodeGraphics extends PnmlElement {

	/**
	 * PNML node graphics tag.
	 */
	public final static String TAG = "graphics";

	/**
	 * Position element.
	 */
	protected PnmlPosition position;
	/**
	 * Fill element.
	 */
	protected PnmlFill fill;
	/**
	 * Line element.
	 */
	protected PnmlLine line;
	/**
	 * Dimension element.
	 */
	protected PnmlDimension dimension;

	/**
	 * Creates a fresh PNML node graphics.
	 */
	protected PnmlNodeGraphics() {
		this(TAG);
	}

	/**
	 * Creates a fresh PNML node graphics.
	 */
	protected PnmlNodeGraphics(String tag) {
		super(tag);
		position = null;
		fill = null;
		line = null;
		dimension = null;
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
		if (xpp.getName().equals(PnmlPosition.TAG)) {
			/*
			 * Position element.
			 */
			position = factory.createPnmlPosition();
			position.importElement(xpp, pnml);
			return true;
		}
		if (xpp.getName().equals(PnmlDimension.TAG)) {
			/*
			 * Dimension element.
			 */
			dimension = factory.createPnmlDimension();
			dimension.importElement(xpp, pnml);
			return true;
		}
		if (xpp.getName().equals(PnmlFill.TAG)) {
			/*
			 * Fill element.
			 */
			fill = factory.createPnmlFill();
			fill.importElement(xpp, pnml);
			return true;
		}
		if (xpp.getName().equals(PnmlLine.TAG)) {
			/*
			 * Line element.
			 */
			line = factory.createPnmlLine();
			line.importElement(xpp, pnml);
			return true;
		}
		/*
		 * Unknown start tag.
		 */
		return false;
	}

	/**
	 * Exports the annotation graphics.
	 */
	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		if (position == null) {
			/*
			 * No position known. However, position may be mandatory in PNML, so
			 * create one.
			 */
			if (pnml.getType() == PnmlType.PNML || pnml.getType() == PnmlType.LOLA) {
				position = factory.createPnmlPosition();
				position.setX(40.0);
				position.setY(40.0);
			}
		}
		if (position != null) {
			s += position.exportElement(pnml);
		}
		if (dimension != null) {
			s += dimension.exportElement(pnml);
		}
		if (fill != null) {
			s += fill.exportElement(pnml);
		}
		if (line != null) {
			s += line.exportElement(pnml);
		}
		return s;
	}

	/**
	 * Checks validity. Should have a position element.
	 */
	protected void checkValidity(Pnml pnml) {
		super.checkValidity(pnml);
		switch (pnml.getType()) {
			case PNML :
				// Fall through
			case LOLA :
				if (position == null) {
					pnml.log(tag, lineNumber, "Expected position");
				}
				break;
			case EPNML :
				// Position not mandatory.
				break;
		}
	}

	/**
	 * Gets the bounding box for this node.
	 * 
	 * @return The bounding box of this object.
	 */
	public Pair<Point2D.Double, Point2D.Double> getBoundingBox() {
		Point2D.Double luc = new Point2D.Double(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		Point2D.Double rbc = new Point2D.Double(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		try {
			luc.x = position.getX() - (dimension == null ? 40.0 : dimension.getX()) / 2.0;
			luc.y = position.getY() - (dimension == null ? 40.0 : dimension.getY()) / 2.0;
			rbc.x = position.getX() + (dimension == null ? 40.0 : dimension.getX()) / 2.0;
			rbc.y = position.getY() + (dimension == null ? 40.0 : dimension.getY()) / 2.0;
		} catch (Exception ex) {
		}
		return new Pair<Point2D.Double, Point2D.Double>(luc, rbc);
	}

	/**
	 * Sets the graphics for the given graph element.
	 * 
	 * @param net
	 *            The given net.
	 * @param subNet
	 *            The given sub net.
	 * @param element
	 *            The given element.
	 */
	public void convertToNet(ExpandableSubNet subNet, AbstractGraphElement element, Point2D.Double displacement,
			GraphLayoutConnection layout) {
		if (position != null) {
			position.convertToNet(subNet, element, displacement, getBoundingBox(), layout);
		}
		if (fill != null) {
			fill.convertToNet(element);
		}
		if (line != null) {
			line.convertToNet(element);
		}
		if (dimension != null) {
			dimension.convertToNet(element, getBoundingBox());
		}
	}

	public PnmlNodeGraphics convertFromNet(ExpandableSubNet parent, AbstractGraphElement element,
			GraphLayoutConnection layout) {
		PnmlNodeGraphics graphics = null;
		position = factory.createPnmlPosition().convertFromNet(parent, element, layout);
		fill = factory.createPnmlFill().convertFromNet(element);
		line = factory.createPnmlLine().convertFromNet(element);
		dimension = factory.createPnmlDimension().convertFromNet(element);
		if ((position != null) || (fill != null) || (line != null) || (dimension != null)) {
			graphics = this;
		}
		return graphics;
	}
}
