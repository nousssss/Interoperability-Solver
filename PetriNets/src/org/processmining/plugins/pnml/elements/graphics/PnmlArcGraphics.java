package org.processmining.plugins.pnml.elements.graphics;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.base.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * PNML arc graphics.
 * 
 * @author hverbeek
 */
public class PnmlArcGraphics extends PnmlElement {

	/**
	 * PNML annotation graphics tag.
	 */
	public final static String TAG = "graphics";

	/**
	 * Positions elements (may be multiple).
	 */
	protected final List<PnmlPosition> positionList;
	/**
	 * Line element.
	 */
	protected PnmlLine line;

	/**
	 * Creates a fresh PNML arc graphics.
	 */
	protected PnmlArcGraphics() {
		this(TAG);
	}

	/**
	 * Creates a fresh PNML arc graphics.
	 */
	protected PnmlArcGraphics(String tag) {
		super(tag);
		positionList = new ArrayList<PnmlPosition>();
		line = null;
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
			PnmlPosition position = factory.createPnmlPosition();
			position.importElement(xpp, pnml);
			positionList.add(position);
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
	 * Exports the arc graphics.
	 */
	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		for (PnmlPosition position : positionList) {
			s += position.exportElement(pnml);
		}
		if (line != null) {
			s += line.exportElement(pnml);
		}
		return s;
	}

	/**
	 * Sets the graphics for the given graph element.
	 * 
	 * @param subNet
	 *            The given sub net.
	 * @param element
	 *            The given element.
	 * @param displacement
	 *            The displacement for this sub net.
	 */
	public void convertToNet(ExpandableSubNet subNet, AbstractGraphElement element, Point2D.Double displacement,
			GraphLayoutConnection layout) {
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		layout.setEdgePoints(element, points);
		for (PnmlPosition position : positionList) {
			position.convertToNet(subNet, element, points, displacement);
		}
		if (line != null) {
			line.convertToNet(element);
		}
	}

	public PnmlArcGraphics convertFromNet(ExpandableSubNet parent, AbstractGraphElement element,
			GraphLayoutConnection layout) {
		PnmlArcGraphics result = null;
		try {
			List<Point2D> points = layout.getEdgePoints(element);
			for (Point2D point : points) {
				positionList.add(factory.createPnmlPosition().convertFromNet(parent, point));
			}
			line = factory.createPnmlLine().convertFromNet(element);
			if (!positionList.isEmpty() || (line != null)) {
				result = this;
			}
		} catch (Exception ex) {
		}
		return result;
	}

}
