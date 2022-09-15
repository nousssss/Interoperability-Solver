package org.processmining.models.graphbased.directed.petrinet.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingConstants;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.LocalNodeID;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.shapes.Rectangle;

public class ExpandableSubNet extends PetrinetNode implements ContainingDirectedGraphNode {

	private final Set<ContainableDirectedGraphElement> children;

	public ExpandableSubNet(String label,
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net) {
		this(label, net, null);
	}

	public ExpandableSubNet(String label,
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net,
			ExpandableSubNet parent) {
		this(label, net, parent, new LocalNodeID());
	}
	
	public ExpandableSubNet(String label,
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net,
			ExpandableSubNet parent, LocalNodeID id) {
		super(net, parent, label, id);
		children = new HashSet<ContainableDirectedGraphElement>();
		getAttributeMap().put(AttributeMap.SHAPE, new Rectangle());
		getAttributeMap().put(AttributeMap.RESIZABLE, false);
		getAttributeMap().put(AttributeMap.LABELVERTICALALIGNMENT, SwingConstants.TOP);
		getAttributeMap().put(AttributeMap.FILLCOLOR, new Color(.95F, .95F, .95F, .95F));
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
	}

	public void addChild(ContainableDirectedGraphElement child) {
		children.add(child);
	}

	public Set<? extends ContainableDirectedGraphElement> getChildren() {
		return children;
	}

	public Dimension getCollapsedSize() {
		return new Dimension(stdWidth, stdHeight);
	}

}
