package org.processmining.models.graphbased.directed.petrinet.configurable.impl;

import java.awt.geom.Point2D;

import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AttributeMapOwner;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;

/**
 * Utils to copy the layout from one net to another net
 * 
 * @author dfahland
 *
 */
public class LayoutUtils {

	/**
	 * Copy layout attributes of a node/edge to a new node.
	 * 
	 * @param oldOwner
	 * @param oldLayout old layout connection containing layout information of oldOwner
	 * @param newOwner
	 * @param newLayout new layout connection containing layout information of newOwner
	 */
	public static void copyLayout(AttributeMapOwner oldOwner, GraphLayoutConnection oldLayout, AttributeMapOwner newOwner, GraphLayoutConnection newLayout) {
		if (oldLayout == null) return;
		
		newLayout.setPosition(newOwner, oldLayout.getPosition(oldOwner));
		newLayout.setPortOffset(newOwner, oldLayout.getPortOffset(oldOwner));
		newLayout.setSize(newOwner, oldLayout.getSize(oldOwner));
		newLayout.setEdgePoints(newOwner, oldLayout.getEdgePoints(oldOwner));
	}

	/**
	 * {@link GraphLayoutConnection#setLayedOut(boolean)} based on layout information in the net,
	 * i.e., whether all nodes have a proper position.
	 * 
	 * @param net
	 * @param layout
	 */
	public static void setLayout(PetrinetGraph net, GraphLayoutConnection layout) {
		boolean doLayout = false;
		/*
		 * If any node has no position, then we need to layout the graph.
		 */
		for (PetrinetNode node : net.getNodes()) {
			if (layout.getPosition(node) == null) {
				doLayout = true;
			}
		}
		if (!doLayout) {
			/*
			 * All nodes have position (10.0,10.0) (which is the default
			 * position) we need to layout as well.
			 */
			doLayout = true;
			for (PetrinetNode node : net.getNodes()) {
				Point2D position = layout.getPosition(node);
				if ((position.getX() != 10.0) || (position.getY() != 10.0)) {
					doLayout = false;
				}
			}
		}
		layout.setLayedOut(!doLayout);
	}
	
}
