package org.processmining.models.jgraph.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import org.processmining.framework.util.Cast;
import org.processmining.models.graphbased.AnnotatedAttributeMap;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.jgraph.views.JGraphShapeView;
import org.processmining.models.shapes.Decorated;
import org.processmining.models.shapes.Shape;

public class AnnotatedShapeRenderer extends ProMGroupShapeRenderer {

	/*
	 * HV: Specific renderer for vertices used by this package.
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1424933579349719029L;

	
	public void paint(Graphics g) {
		ViewSpecificAttributeMap map = ((JGraphShapeView) view).getViewSpecificAttributeMap();

		int[] laneHeights = (int[]) map.get(((JGraphShapeView) view).getNode(), AnnotatedAttributeMap.LANEHEIGHTS);
		Color[] laneColors = (Color[]) map.get(((JGraphShapeView) view).getNode(), AnnotatedAttributeMap.LANECOLORS);
		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<Double, Integer>> laneChart = (HashMap<String, HashMap<Double, Integer>>) map.get(((JGraphShapeView) view).getNode(), AnnotatedAttributeMap.LANECHART);
		@SuppressWarnings("unchecked")
		HashMap<String, Color> colorMap = (HashMap<String, Color>) map.get(((JGraphShapeView) view).getNode(), AnnotatedAttributeMap.COLORMAP);

		highlightColor = Color.ORANGE;
		lockedHandleColor = Color.RED;
		DirectedGraphNode node = ((JGraphShapeView) view).getNode();
		//		Dimension d = (Dimension) map.get(node,AttributeMap.SIZE);
		//		d.setSize(d.getWidth() * 1.4, d.getHeight() * 1.4);

		Dimension d = (Dimension) map.get(node, AttributeMap.SIZE);
		if (d == null) {
			d = getSize();
		} else {
			Rectangle2D bounds = view.getBounds();
			view.setBounds(new Rectangle2D.Double(bounds.getX(), bounds.getY(), d.getWidth(), d.getHeight()));
			setSize(d);
		}

		//assert(d.equals(node.getAttributeMap().get(AttributeMap.SIZE)));

		if (!map.get(node, AttributeMap.SHOWLABEL, true) || ((JGraphShapeView) view).isPIP()) {
			setText(null);
		} else {
			setVerticalAlignment(map.get(node, AttributeMap.LABELVERTICALALIGNMENT, SwingConstants.TOP));
			setHorizontalAlignment(map.get(node, AttributeMap.LABELHORIZONTALALIGNMENT, SwingConstants.CENTER));
			String text = map.get(node, AttributeMap.LABEL, getText());
			if (!text.toLowerCase().startsWith("<html>")) {
				text = "<html>" + text + "</html>";
			}
			setText(text);
		}

		Shape shape = map.get(node, AttributeMap.SHAPE, JGraphShapeView.RECTANGLE);

		Icon icon = (Icon) map.get(node, AttributeMap.ICON);
		if ((icon != null) && (icon instanceof ImageIcon)) {
			Image image = ((ImageIcon) icon).getImage();
			if ((icon.getIconHeight() > d.height) || (icon.getIconWidth() > d.width)) {
				image = image.getScaledInstance(d.height, d.width, Image.SCALE_SMOOTH);
			}
			icon = new ImageIcon(image);
		}
		if (!((JGraphShapeView) view).isPIP()) {
			setIcon(icon);
		} else {
			setIcon(null);
		}

		borderWidth = map.get(node, AttributeMap.BORDERWIDTH, borderWidth);
		int b = borderWidth - 1;
		Graphics2D g2 = (Graphics2D) g;

		boolean tmp = selected;

		GeneralPath path = shape.getPath(b, b, d.width - 2 * b, d.height - 2 * b);

		Color fill = (Color) map.get(((JGraphShapeView) view).getNode(), AttributeMap.FILLCOLOR);
		g.setColor(fill);
		setOpaque(fill != null);
		if (fill != null) {
			g2.fill(path);
		}
		if ((!(colorMap == null))&&((!(laneHeights == null))||(!(laneColors == null)))) {
			// Code Jeroen van Mourik
			
			double planeHeight = (((double) d.height) / colorMap.keySet().size());

			double startX = 0;
			double startY = 0;

			int index = 0;
			for (String modelName : colorMap.keySet()) {
				double height = 0;
				if ((laneHeights == null)){
					height = planeHeight;
				} else {
					height = laneHeights[index];
				}
				if ((laneColors == null) || (laneColors[index] == null)){
					g2.setPaint(colorMap.get(modelName));
				} else {
					g2.setPaint(laneColors[index]);
				}
				g2.fill(new Rectangle2D.Double(startX, startY, d.width, height));

				// BEGIN Chart
				if ((!(laneChart == null)) && (height >= 42)){
					double maxRelativeBarLength = 0;
					double barThickness = (height - 22) / 10.0;
					double localStartY = startY + 2;
					
					for (Double range : laneChart.get(modelName).keySet()){
						maxRelativeBarLength = Math.max(maxRelativeBarLength, laneChart.get(modelName).get(range));
					}
					Double[] rangeValues = { 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9 };
					for (double range : rangeValues){
						double barLength = (laneChart.get(modelName).get(range) / maxRelativeBarLength) * (d.width - 2);
						g2.setPaint(new Color(50,50,50));
						g2.fill(new Rectangle2D.Double(startX, localStartY, barLength, barThickness));
						localStartY += 4;
					}
					// relative to node height and width, relative to max value
				}
				// END Chart
				
				startY += height;
				startX = 0;
				index++;
			}
		} else {
			// Original code
			
			//g.setColor(Color.BLACK);
			g.setColor(map.get(node, AttributeMap.STROKECOLOR, Color.BLACK));
			//		g2.setStroke(new BasicStroke(borderWidth));
			float[] pattern = map.get(node, AttributeMap.DASHPATTERN, new float[0]);
			if (pattern.length > 0f) {
	
				float offset = map.get(node, AttributeMap.DASHOFFSET, 0f);
				g2.setStroke(new BasicStroke(borderWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, pattern,
						offset));
			} else {
				g2.setStroke(new BasicStroke(borderWidth));
			}
	
			g2.draw(path);
		}

		try {
			setBorder(null);
			setOpaque(false);
			// selected = false;
			super.paint(g);
			if (node instanceof Decorated) {
				Cast.<Decorated>cast(node).decorate(g2, b, b, d.width - 2 * b, d.height - 2 * b);
			}

			//			if (isGroup) {
			//				g.setColor(handleColor);
			//				g.fill3DRect(handle.x, handle.y, handle.width, handle.height, true);
			//				g.setColor(graphForeground);
			//				g.drawRect(handle.x, handle.y, handle.width, handle.height);
			//				g.drawLine(handle.x + 1, handle.y + handle.height / 2, handle.x + handle.width - 2, handle.y
			//						+ handle.height / 2);
			//				if (view.isLeaf()) {
			//					g.drawLine(handle.x + handle.width / 2, handle.y + 1, handle.x + handle.width / 2, handle.y
			//							+ handle.height - 2);
			//				}
			//			}

		} finally {
			selected = tmp;
		}
	}
}
