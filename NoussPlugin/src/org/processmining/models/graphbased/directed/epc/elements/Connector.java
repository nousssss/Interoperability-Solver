package org.processmining.models.graphbased.directed.epc.elements;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.epc.EPCEdge;
import org.processmining.models.graphbased.directed.epc.EPCNode;
import org.processmining.models.shapes.Decorated;
import org.processmining.models.shapes.Ellipse;

public class Connector extends EPCNode implements Decorated {

	public enum ConnectorType {

		/**
		 * And connector
		 */
		AND("A", "And"),
		/**
		 * Xor connector
		 */
		XOR("X", "Xor"),
		/**
		 * Or connector
		 */
		OR("V", "OR");

		private final String shortName;
		private final String longName;

		ConnectorType(String shortName, String longName) {
			this.shortName = shortName;
			this.longName = longName;
		}

		public String getShortName() {
			return shortName;
		}

		public String getLongName() {
			return longName;
		}

	}

	private ConnectorType type = ConnectorType.AND;

	public Connector(AbstractDirectedGraph<EPCNode, EPCEdge<? extends EPCNode, ? extends EPCNode>> epc, String label,
			ConnectorType type) {
		super(epc, label);
		setType(type);
		getAttributeMap().put(AttributeMap.SHAPE, new Ellipse());
		getAttributeMap().put(AttributeMap.SQUAREBB, true);
		getAttributeMap().put(AttributeMap.RESIZABLE, false);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(25, 25));
		getAttributeMap().put(AttributeMap.SHOWLABEL, false);
	}

	public ConnectorType getType() {
		return type;
	}

	public void setType(ConnectorType type) {
		this.type = type;
	}

	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
		if (type == ConnectorType.OR) {
			GeneralPath orDecorator = new GeneralPath();
			orDecorator.moveTo((float) (x + 0.25 * width), (float) (y + 0.25 * height));
			orDecorator.lineTo((float) (x + 0.5 * width), (float) (y + 0.75 * height));
			orDecorator.lineTo((float) (x + 0.75 * width), (float) (y + 0.25 * height));
			g2d.draw(orDecorator);
		} else if (type == ConnectorType.AND) {
			GeneralPath andDecorator = new GeneralPath();
			andDecorator.moveTo((float) (x + 0.25 * width), (float) (y + 0.75 * height));
			andDecorator.lineTo((float) (x + 0.5 * width), (float) (y + 0.25 * height));
			andDecorator.lineTo((float) (x + 0.75 * width), (float) (y + 0.75 * height));
			g2d.draw(andDecorator);
		} else if (type == ConnectorType.XOR) {
			GeneralPath xorDecorator = new GeneralPath();
			xorDecorator.moveTo((float) (x + 0.25 * width), (float) (y + 0.25 * height));
			xorDecorator.lineTo((float) (x + 0.75 * width), (float) (y + 0.75 * height));
			xorDecorator.moveTo((float) (x + 0.25 * width), (float) (y + 0.75 * height));
			xorDecorator.lineTo((float) (x + 0.75 * width), (float) (y + 0.25 * height));
			g2d.draw(xorDecorator);
		}
	}

}
