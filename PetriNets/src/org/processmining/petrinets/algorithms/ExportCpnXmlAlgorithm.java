package org.processmining.petrinets.algorithms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

import com.google.common.collect.Lists;

public class ExportCpnXmlAlgorithm {

	private Petrinet net;
	private Marking marking;
	private GraphLayoutConnection layout;
	private String name;
	private StringBuffer buf;

	private String tool;
	private String version;
	private Map<PetrinetNode, Integer> nodeIds;
	private int nextId;
	private double scale;
	private double panx = -470.0;
	private double pany = -260.0;

	private Set<String> labels;
	private Map<String, Integer> duplicateLabels;
	
	private Locale locale;

	public ExportCpnXmlAlgorithm() {
		tool = "ProM.PetriNets"; //"CPN Tools";
		version = "6.10.149"; //"4.0.1";
		nodeIds = new HashMap<PetrinetNode, Integer>();
		nextId = 100;
		scale = 1.0;
		labels = new HashSet<String>();
		duplicateLabels = new HashMap<String, Integer>();
		locale = Locale.US;
	}

	public String apply(Petrinet net, GraphLayoutConnection layout, Marking marking, String name) {
		this.net = net;
		this.layout = layout;
		this.marking = marking;
		this.name = name;
		this.buf = new StringBuffer();
		appendHeader(0);
		appendWorkspaceElements(0);
		return this.buf.toString();
	}

	private void appendIndent(int indent) {
		for (int i = 0; i < indent; i++) {
			buf.append("  ");
		}
	}

	private void appendHeader(int indent) {
		appendIndent(indent);
		buf.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n");
		appendIndent(indent);
		buf.append(
				"<!DOCTYPE workspaceElements PUBLIC \"-//CPN//DTD CPNXML 1.0//EN\" \"http://cpntools.org/DTD/6/cpn.dtd\">\n\n");
	}

	private void appendWorkspaceElements(int indent) {
		appendIndent(indent);
		buf.append("<workspaceElements>\n");
		appendGenerator(indent + 1);
		appendCpNet(indent + 1);
		appendIndent(indent);
		buf.append("</workspaceElements>");
	}

	private void appendGenerator(int indent) {
		appendIndent(indent);
		buf.append("<generator tool=\"" + tool + "\"\n");
		appendIndent(indent);
		buf.append("           version=\"" + version + "\"\n");
		appendIndent(indent);
		buf.append("           format=\"6\"/>\n");
	}

	private void appendCpNet(int indent) {
		appendIndent(indent);
		buf.append("<cpnet>\n");
		appendGlobBox(indent + 1);
		appendPage(indent + 1);
		appendInstances(indent + 1);
		appendOptions(indent + 1);
		appendBinders(indent + 1);
		appendMonitorBlock(indent + 1);
		appendIndexNode(indent + 1);
		appendIndent(indent);
		buf.append("</cpnet>\n");
	}

	private void appendGlobBox(int indent) {
		appendIndent(indent);
		buf.append("<globbox>\n");
		appendStandardDeclarationBlock(indent + 1);
		appendIndent(indent);
		buf.append("</globbox>\n");
	}

	private void appendStandardDeclarationBlock(int indent) {
		appendIndent(indent);
		buf.append("<block id=\"Block1\">\n");
		appendIndent(indent + 1);
		buf.append("<id>Standard declarations</id>\n");
		appendIndent(indent + 1);
		buf.append("<color id=\"Color1\">\n");
		appendIndent(indent + 2);
		buf.append("<id>UNIT</id>\n");
		appendIndent(indent + 2);
		buf.append("<unit/>\n");
		appendIndent(indent + 2);
		buf.append("<layout>colset UNIT = unit;</layout>\n");
		appendIndent(indent + 1);
		buf.append("</color>\n");
		appendIndent(indent);
		buf.append("</block>\n");
		appendIndent(indent);
//		buf.append("<block id=\"Block2\">\n");
//		appendIndent(indent + 1);
//		buf.append("<id>Petri net color sets</id>\n");
//		appendIndent(indent + 1);
//		buf.append("<color id=\"Color2\">\n");
//		appendIndent(indent + 2);
//		buf.append("<id>Token</id>\n");
//		appendIndent(indent + 2);
//		buf.append("<alias>\n");
//		appendIndent(indent + 3);
//		buf.append("<id>INT</id>\n");
//		appendIndent(indent + 2);
//		buf.append("</alias>\n");
//		appendIndent(indent + 2);
//		buf.append("<layout>colset Token = INT;</layout>\n");
//		appendIndent(indent + 1);
//		buf.append("</color>\n");
//		appendIndent(indent);
//		buf.append("</block>\n");
//		appendIndent(indent);
//		buf.append("<block id=\"Block3\">\n");
//		appendIndent(indent + 1);
//		buf.append("<id>Petri net declarations</id>\n");
//		appendIndent(indent + 1);
//		buf.append("<var id=\"Var1\">\n");
//		appendIndent(indent + 2);
//		buf.append("<type>\n");
//		appendIndent(indent + 3);
//		buf.append("<id>Token</id>\n");
//		appendIndent(indent + 2);
//		buf.append("</type>\n");
//		appendIndent(indent + 2);
//		buf.append("<id>token</id>\n");
//		appendIndent(indent + 2);
//		buf.append("<layout>var token : Token;</layout>\n");
//		appendIndent(indent + 1);
//		buf.append("</var>\n");
//		appendIndent(indent);
//		buf.append("</block>\n");
	}

	private void appendPage(int indent) {
		appendIndent(indent);
		buf.append("<page id=\"Page1\">\n");
		appendIndent(indent + 1);
		buf.append("<pageattr name=\"" + name + "\"/>\n");
		for (Place place : net.getPlaces()) {
			nodeIds.put(place, nextId++);
			appendPlace(place, indent + 1);
		}
		for (Transition transition : net.getTransitions()) {
			nodeIds.put(transition, nextId++);
			appendTransition(transition, indent + 1);
		}
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getEdges()) {
			if (edge instanceof Arc) {
				Arc arc = (Arc) edge;
				appendArc(arc, indent + 1);
			}
		}
		appendConstraints(indent + 1);
		appendAuxs(indent + 1);
		appendIndent(indent);
		buf.append("</page>\n");
	}

	private void appendPlace(Place place, int indent) {
		appendIndent(indent);
		buf.append("<place id=\"Place" + nodeIds.get(place) + "\">\n");
		appendPosAttr(place, 0.0, 0.0, indent + 1);
		appendFillAttr(place, indent + 1);
		appendLineAttr(place, indent + 1);
		appendTextAttr(place, indent + 1);
		appendText(place, getLabel(place.getLabel()), indent + 1, false);
		appendShape(place, indent + 1);
		appendToken(place, indent + 1);
		appendMarking(place, indent + 1);
		appendType(place, indent + 1);
		appendInitMark(place, indent + 1);
		appendIndent(indent);
		buf.append("</place>\n");
	}

	private String getLabel(String label) {
		if (label.isEmpty()) {
			label="(empty)";
		}
		while (labels.contains(label)) {
			if (duplicateLabels.containsKey(label)) {
				duplicateLabels.put(label, duplicateLabels.get(label) + 1);
			} else {
				duplicateLabels.put(label, 2);
			}
			label = label + " " + duplicateLabels.get(label);
		}
		labels.add(label);
		return label;
	}

	private void appendPosAttr(PetrinetNode node, double x, double y, int indent) {
		Point2D pos = getPosition(node);
		if (marking.occurrences(node) > 0) {
			/*
			 * Has initial marking, must be a place.
			 * Store the position to be able to center on it later.
			 */
			panx = pos.getX();
			pany = pos.getY();
		}
		appendIndent(indent);
		buf.append("<posattr x=\"" + String.format(locale, "%.6f", scale * pos.getX() + x) + "\"\n");
		appendIndent(indent);
		buf.append("         y=\"" + String.format(locale, "%.6f", -scale * pos.getY() + y) + "\"/>\n");
	}

	/*
	 * Maps colors to CPN tools color names.
	 */
	private String getColorName(Color color, String defaultColor) {
		if (color == null) {
			return defaultColor;
		} else if (color.equals(Color.decode("#ff00ff"))) {
			return "Fucia";
		} else if (color.equals(Color.decode("#800000"))) {
			return "Maroon";
		} else if (color.equals(Color.decode("#ffff00"))) {
			return "Yellow";
		} else if (color.equals(Color.decode("#ffffff"))) {
			return "White";
		} else if (color.equals(Color.decode("#ff0000"))) {
			return "Red";
		} else if (color.equals(Color.decode("#c0c0c0"))) {
			return "Silver";
		} else if (color.equals(Color.decode("#008080"))) {
			return "Teal";
		} else if (color.equals(Color.decode("#000080"))) {
			return "Navy";
		} else if (color.equals(Color.decode("#00ffff"))) {
			return "Aqua";
		} else if (color.equals(Color.decode("#000000"))) {
			return "Black";
		} else if (color.equals(Color.decode("#808000"))) {
			return "Olive";
		} else if (color.equals(Color.decode("#00ff00"))) {
			return "Lime";
		} else if (color.equals(Color.decode("#808080"))) {
			return "Gray";
		} else if (color.equals(Color.decode("#800080"))) {
			return "Purple";
		} else if (color.equals(Color.decode("#008000"))) {
			return "Green";
		} else if (color.equals(Color.decode("#0000ff"))) {
			return "Blue";
		}
		return defaultColor;
	}

	private void appendFillAttr(PetrinetNode node, int indent) {
		appendFillAttr(node, false, indent);
	}

	private void appendFillAttr(PetrinetNode node, boolean isInvisible, int indent) {
		String color = getColorName((Color) node.getAttributeMap().get(AttributeMap.FILLCOLOR), "White");
		if (isInvisible) {
			color = "Black";
		}
		appendIndent(indent);
		buf.append("<fillattr colour=\"" + color + "\"\n");
		appendIndent(indent);
		buf.append("          pattern=\"\"\n");
		appendIndent(indent);
		buf.append("          filled=\"" + isInvisible + "\"/>\n");
	}

	private void appendLineAttr(PetrinetNode node, int indent) {
		String color = getColorName((Color) node.getAttributeMap().get(AttributeMap.STROKECOLOR), "Black");
		appendIndent(indent);
		buf.append("<lineattr colour=\"" + color + "\"\n");
		appendIndent(indent);
		buf.append("          thick=\"1\"\n");
		appendIndent(indent);
		buf.append("          type=\"solid\"/>\n");
	}

	private void appendTextAttr(PetrinetNode node, int indent) {
		appendIndent(indent);
		buf.append("<textattr colour=\"Black\"\n");
		appendIndent(indent);
		buf.append("          bold=\"false\"/>\n");
	}

	private void appendText(PetrinetNode node, String text, int indent, boolean attr) {
		if (attr) {
			if (text.isEmpty()) {
				appendIndent(indent);
				buf.append("<text tool=\"" + tool + "\"\n");
				appendIndent(indent);
				buf.append("      version=\"" + version + "\"/>\n");
			} else {
				appendIndent(indent);
				buf.append("<text tool=\"" + tool + "\"\n");
				appendIndent(indent);
				buf.append("      version=\"" + version + "\">" + text + "</text>\n");
			}
		} else {
			if (text.isEmpty()) {
				appendIndent(indent);
				buf.append("<text/>\n");
			} else {
				appendIndent(indent);
				buf.append("<text>" + text + "</text>\n");
			}
		}
	}

	private void appendShape(Place place, int indent) {
		Dimension size = getSize(place);
		appendIndent(indent);
		buf.append("<ellipse w=\"" + String.format(locale, "%.6f", scale * size.getWidth()) + "\"\n");
		appendIndent(indent);
		buf.append("         h=\"" + String.format(locale, "%.6f", scale * size.getHeight()) + "\"/>\n");
	}

	private void appendShape(Transition transition, int indent) {
		Dimension size = getSize(transition);
		appendIndent(indent);
		buf.append("<box w=\"" + String.format(locale, "%.6f", scale * size.getWidth()) + "\"\n");
		appendIndent(indent);
		buf.append("     h=\"" + String.format(locale, "%.6f", scale * size.getHeight()) + "\"/>\n");
	}

	private void appendToken(Place place, int indent) {
		appendIndent(indent);
		buf.append("<token x=\"-10.000000\"\n");
		appendIndent(indent);
		buf.append("       y=\"0.000000\"/>\n");
	}

	private void appendMarking(Place place, int indent) {
		appendIndent(indent);
		buf.append("<marking x=\"0.000000\"\n");
		appendIndent(indent);
		buf.append("         y=\"0.000000\"\n");
		appendIndent(indent);
		buf.append("         hidden=\"false\">\n");
		appendIndent(indent + 1);
		buf.append("<snap snap_id=\"0\"\n");
		appendIndent(indent + 1);
		buf.append("      anchor.horizontal=\"0\"\n");
		appendIndent(indent + 1);
		buf.append("      anchor.vertical=\"0\"/>\n");
		appendIndent(indent);
		buf.append("</marking>\n");
	}

	private void appendType(Place place, int indent) {
		appendIndent(indent);
		buf.append("<type id=\"PlaceType" + nodeIds.get(place) + "\">\n");
		appendPosAttr(place, 20.0, -20.0, indent + 1);
		appendFillAttr(place, indent + 1);
		appendLineAttr(place, indent + 1);
		appendTextAttr(place, indent + 1);
		appendText(place, "UNIT", indent + 1, true);
		appendIndent(indent);
		buf.append("</type>\n");
	}

	private void appendInitMark(Place place, int indent) {
		appendIndent(indent);
		buf.append("<initmark id=\"PlaceMark" + nodeIds.get(place) + "\">\n");
		appendPosAttr(place, 20.0, 20.0, indent + 1);
		appendFillAttr(place, indent + 1);
		appendLineAttr(place, indent + 1);
		appendTextAttr(place, indent + 1);
		int tokens = marking.occurrences(place);
		String text = (tokens == 0 ? "" : "" + tokens + "`()");
//		String separator = "";
//		for (int i = 0; i < tokens; i++) {
//			text = text + separator + "1`" + i;
//			separator = "++";
//		}
		appendText(place, text, indent + 1, true);
		appendIndent(indent);
		buf.append("</initmark>\n");
	}

	private void appendTransition(Transition transition, int indent) {
		appendIndent(indent);
		buf.append("<trans id=\"Trans" + nodeIds.get(transition) + "\"\n");
		appendIndent(indent);
		buf.append("       explicit=\"false\">\n");
		appendPosAttr(transition, 0.0, 0.0, indent + 1);
		appendFillAttr(transition, transition.isInvisible(), indent + 1);
		appendLineAttr(transition, indent + 1);
		appendTextAttr(transition, indent + 1);
		appendText(transition, getLabel(transition.getLabel()), indent + 1, false);
		appendShape(transition, indent + 1);
		appendSubst(transition, indent + 1);
		appendBinding(transition, indent + 1);
		appendCond(transition, indent + 1);
		appendTime(transition, indent + 1);
		appendCode(transition, indent + 1);
		appendPriority(transition, indent + 1);
		appendIndent(indent);
		buf.append("</trans>\n");
	}

	private void appendCond(Transition transition, int indent) {
		appendIndent(indent);
		buf.append("<cond id=\"TransCond" + nodeIds.get(transition) + "\">\n");
		appendPosAttr(transition, -40.0, 30.0, indent + 1);
		appendFillAttr(transition, indent + 1);
		appendLineAttr(transition, indent + 1);
		appendTextAttr(transition, indent + 1);
		appendText(transition, "", indent + 1, true);
		appendIndent(indent);
		buf.append("</cond>\n");
	}

	private void appendTime(Transition transition, int indent) {
		appendIndent(indent);
		buf.append("<time id=\"TransTime" + nodeIds.get(transition) + "\">\n");
		appendPosAttr(transition, 40.0, 30.0, indent + 1);
		appendFillAttr(transition, indent + 1);
		appendLineAttr(transition, indent + 1);
		appendTextAttr(transition, indent + 1);
		appendText(transition, "", indent + 1, true);
		appendIndent(indent);
		buf.append("</time>\n");
	}

	private void appendCode(Transition transition, int indent) {
		appendIndent(indent);
		buf.append("<code id=\"TransCode" + nodeIds.get(transition) + "\">\n");
		appendPosAttr(transition, 40.0, -30.0, indent + 1);
		appendFillAttr(transition, indent + 1);
		appendLineAttr(transition, indent + 1);
		appendTextAttr(transition, indent + 1);
		appendText(transition, "", indent + 1, true);
		appendIndent(indent);
		buf.append("</code>\n");
	}

	private void appendPriority(Transition transition, int indent) {
		appendIndent(indent);
		buf.append("<priority id=\"TransPrio" + nodeIds.get(transition) + "\">\n");
		appendPosAttr(transition, -40.0, -30.0, indent + 1);
		appendFillAttr(transition, indent + 1);
		appendLineAttr(transition, indent + 1);
		appendTextAttr(transition, indent + 1);
		appendText(transition, "", indent + 1, true);
		appendIndent(indent);
		buf.append("</priority>\n");
	}

	private void appendSubst(Transition transition, int indent) {
		return; // Do nothing
	}

	private void appendBinding(Transition transition, int indent) {
		appendIndent(indent);
		buf.append("<binding x=\"7.200000\"\n");
		appendIndent(indent);
		buf.append("         y=\"-3.000000\"/>\n");
	}

	private void appendArc(Arc arc, int indent) {
		PetrinetNode sourceNode = arc.getSource();
		PetrinetNode targetNode = arc.getTarget();
		String orientation = "PtoT";
		if (sourceNode instanceof Transition) {
			orientation = "TtoP";
		}
		appendIndent(indent);
		buf.append("<arc id=\"Arc" + nodeIds.get(sourceNode) + "x" + nodeIds.get(targetNode) + "\"\n");
		appendIndent(indent);
		buf.append("     orientation=\"" + orientation + "\"\n");
		appendIndent(indent);
		buf.append("     order=\"1\">\n");
		appendPosAttr(arc, 0, 0, indent + 1);
		appendFillAttr(arc, indent + 1);
		appendLineAttr(arc, indent + 1);
		appendTextAttr(arc, indent + 1);
		appendArrowAttr(arc, indent + 1);
		appendTransEnd(arc, indent + 1);
		appendPlaceEnd(arc, indent + 1);
		Point2D annotPoint;
		if (layout.getEdgePoints(arc).isEmpty()) {
			Point2D sourcePoint = getPosition(sourceNode);
			Point2D targetPoint = getPosition(targetNode);
			annotPoint = new Point2D.Double((sourcePoint.getX() + targetPoint.getX()) / 2.0,
					(sourcePoint.getY() + targetPoint.getY()) / 2.0);
		} else {
			int index = layout.getEdgePoints(arc).size() / 2;
			annotPoint = layout.getEdgePoints(arc).get(index);
		}
		List<Point2D> points = layout.getEdgePoints(arc);
		if (orientation.equals("PtoT")) {
			points = Lists.reverse(points);
		}
		int index = 1;
		for (Point2D point : points) {
			appendBendPoint(arc, point, index++, indent + 1);
		}
		appendAnnot(arc, annotPoint, indent + 1);
		appendIndent(indent);
		buf.append("</arc>\n");
	}

	private Point2D getPosition(PetrinetNode node) {
		Point2D position = layout.getPosition(node);
		if (position == null) {
			position = new Point2D.Double(10.0, 10.0);
		}
		Dimension size = getSize(node);
		return new Point2D.Double(position.getX() + size.getWidth() / 2.0, position.getY() + size.getHeight() / 2.0);
	}

	private Dimension getSize(PetrinetNode node) {
		Dimension size = layout.getSize(node);
		if (size == null) {
			if (node instanceof Place) {
				size = new Dimension(25, 25);
			} else if (node instanceof Transition) {
				if (((Transition) node).isInvisible()) {
					size = new Dimension(25, 25);
				} else {
					size = new Dimension(50, 40);
				}
			} else {
				size = new Dimension(10, 10);
			}
		}
		return size;
	}

	private void appendPosAttr(Arc arc, double x, double y, int indent) {
		appendIndent(indent);
		buf.append("<posattr x=\"" + String.format(locale, "%.6f", (x == 0.0 ? x : scale * x)) + "\"\n");
		appendIndent(indent);
		buf.append("         y=\"" + String.format(locale, "%.6f", (y == 0.0 ? y : -scale * y)) + "\"/>\n");
	}

	private void appendFillAttr(Arc arc, int indent) {
		String color = getColorName((Color) arc.getAttributeMap().get(AttributeMap.FILLCOLOR), "White");
		appendIndent(indent);
		buf.append("<fillattr colour=\"" + color + "\"\n");
		appendIndent(indent);
		buf.append("          pattern=\"\"\n");
		appendIndent(indent);
		buf.append("          filled=\"false\"/>\n");
	}

	private void appendLineAttr(Arc arc, int indent) {
		String color = getColorName((Color) arc.getAttributeMap().get(AttributeMap.EDGECOLOR), "Black");
		appendIndent(indent);
		buf.append("<lineattr colour=\"" + color + "\"\n");
		appendIndent(indent);
		buf.append("          thick=\"1\"\n");
		appendIndent(indent);
		buf.append("          type=\"solid\"/>\n");
	}

	private void appendTextAttr(Arc arc, int indent) {
		appendIndent(indent);
		buf.append("<textattr colour=\"Black\"\n");
		appendIndent(indent);
		buf.append("          bold=\"false\"/>\n");
	}

	private void appendArrowAttr(Arc arc, int indent) {
		appendIndent(indent);
		buf.append("<arrowattr headsize=\"1.200000\"\n");
		appendIndent(indent);
		buf.append("           currentcyckle=\"2\"/>\n");
	}

	private void appendTransEnd(Arc arc, int indent) {
		Transition transition = null;
		if (arc.getSource() instanceof Transition) {
			transition = (Transition) arc.getSource();
		} else if (arc.getTarget() instanceof Transition) {
			transition = (Transition) arc.getTarget();
		}
		if (transition != null) {
			appendIndent(indent);
			buf.append("<transend idref=\"Trans" + nodeIds.get(transition) + "\"/>\n");
		}
	}

	private void appendPlaceEnd(Arc arc, int indent) {
		Place place = null;
		if (arc.getSource() instanceof Place) {
			place = (Place) arc.getSource();
		} else if (arc.getTarget() instanceof Place) {
			place = (Place) arc.getTarget();
		}
		if (place != null) {
			appendIndent(indent);
			buf.append("<placeend idref=\"Place" + nodeIds.get(place) + "\"/>\n");
		}
	}

	private void appendAnnot(Arc arc, Point2D point, int indent) {
		PetrinetNode sourceNode = arc.getSource();
		PetrinetNode targetNode = arc.getTarget();
		appendIndent(indent);
		buf.append("<annot id=\"ArcAnnot" + nodeIds.get(sourceNode) + "x" + nodeIds.get(targetNode) + "\">\n");
		appendPosAttr(arc, point.getX(), point.getY(), indent + 1);
		appendFillAttr(arc, indent + 1);
		appendLineAttr(arc, indent + 1);
		appendTextAttr(arc, indent + 1);
		appendText(arc, indent + 1);
		appendIndent(indent);
		buf.append("</annot>\n");
	}

	private void appendText(Arc arc, int indent) {
		appendText(arc, "" + arc.getWeight() + "`()", indent);
	}

	private void appendText(Arc arc, String text, int indent) {
		appendIndent(indent);
		buf.append("<text tool=\"" + tool + "\"\n");
		appendIndent(indent);
		buf.append("      version=\"" + version + "\">" + text + "</text>\n");
	}

	private void appendBendPoint(Arc arc, Point2D point, int index, int indent) {
		PetrinetNode sourceNode = arc.getSource();
		PetrinetNode targetNode = arc.getTarget();
		appendIndent(indent);
		buf.append("<bendpoint id=\"ArcPoint" + index + "x" + nodeIds.get(sourceNode) + "x" + nodeIds.get(targetNode)
				+ "\"\n");
		appendIndent(indent);
		buf.append("           serial=\"" + index + "\">\n");
		appendPosAttr(arc, point.getX(), point.getY(), indent + 1);
		appendFillAttr(arc, indent + 1);
		appendLineAttr(arc, indent + 1);
		appendTextAttr(arc, indent + 1);
		appendIndent(indent);
		buf.append("</bendpoint>\n");
	}

	private void appendConstraints(int indent) {
		appendIndent(indent);
		buf.append("<constraints/>\n");
	}

	private void appendAuxs(int indent) {
		return; //Do nothing.
	}

	private void appendInstances(int indent) {
		appendIndent(indent);
		buf.append("<instances>\n");
		appendIndent(indent + 1);
		buf.append("<instance id=\"Instance1\"\n");
		appendIndent(indent + 1);
		buf.append("          page=\"Page1\"/>\n");
		appendIndent(indent);
		buf.append("</instances>\n");
	}

	private void appendOptions(int indent) {
		appendIndent(indent);
		buf.append("<options>\n");
		appendBooleanOption("realtimestamp", "false", indent + 1);
		appendBooleanOption("fair_be", "false", indent + 1);
		appendBooleanOption("global_fairness", "false", indent + 1);
		appendTextOption("outputdirectory", "&lt;same as model&gt;", indent + 1);
		appendBooleanOption("repavg", "false", indent + 1);
		appendBooleanOption("repciavg", "false", indent + 1);
		appendBooleanOption("repcount", "false", indent + 1);
		appendBooleanOption("repfirstval", "false", indent + 1);
		appendBooleanOption("replastval", "false", indent + 1);
		appendBooleanOption("repmax", "false", indent + 1);
		appendBooleanOption("repmin", "false", indent + 1);
		appendBooleanOption("repssquare", "false", indent + 1);
		appendBooleanOption("repssqdev", "false", indent + 1);
		appendBooleanOption("repstddev", "false", indent + 1);
		appendBooleanOption("repsum", "false", indent + 1);
		appendBooleanOption("repvariance", "false", indent + 1);
		appendBooleanOption("avg", "false", indent + 1);
		appendBooleanOption("ciavg", "false", indent + 1);
		appendBooleanOption("count", "false", indent + 1);
		appendBooleanOption("firstval", "false", indent + 1);
		appendBooleanOption("lastval", "false", indent + 1);
		appendBooleanOption("max", "false", indent + 1);
		appendBooleanOption("min", "false", indent + 1);
		appendBooleanOption("ssquare", "false", indent + 1);
		appendBooleanOption("ssqdev", "false", indent + 1);
		appendBooleanOption("stddev", "false", indent + 1);
		appendBooleanOption("sum", "false", indent + 1);
		appendBooleanOption("variance", "false", indent + 1);
		appendBooleanOption("firstupdate", "false", indent + 1);
		appendBooleanOption("interval", "false", indent + 1);
		appendBooleanOption("lastupdate", "false", indent + 1);
		appendBooleanOption("untimedavg", "false", indent + 1);
		appendBooleanOption("untimedciavg", "false", indent + 1);
		appendBooleanOption("untimedcount", "false", indent + 1);
		appendBooleanOption("untimedfirstval", "false", indent + 1);
		appendBooleanOption("untimedlastval", "false", indent + 1);
		appendBooleanOption("untimedmax", "false", indent + 1);
		appendBooleanOption("untimedmin", "false", indent + 1);
		appendBooleanOption("untimedssquare", "false", indent + 1);
		appendBooleanOption("untimedssqdev", "false", indent + 1);
		appendBooleanOption("untimedstddev", "false", indent + 1);
		appendBooleanOption("untimedsum", "false", indent + 1);
		appendBooleanOption("untimedvariance", "false", indent + 1);
		appendIndent(indent);
		buf.append("</options>\n");
	}

	private void appendBooleanOption(String option, String value, int indent) {
		appendIndent(indent);
		buf.append("<option name=\"" + option + "\">\n");
		appendIndent(indent + 1);
		buf.append("<value>\n");
		appendIndent(indent + 2);
		buf.append("<boolean>" + value + "</boolean>\n");
		appendIndent(indent + 1);
		buf.append("</value>\n");
		appendIndent(indent);
		buf.append("</option>\n");
	}

	private void appendTextOption(String option, String value, int indent) {
		appendIndent(indent);
		buf.append("<option name=\"" + option + "\">\n");
		appendIndent(indent + 1);
		buf.append("<value>\n");
		appendIndent(indent + 2);
		buf.append("<text>" + value + "</text>\n");
		appendIndent(indent + 1);
		buf.append("</value>\n");
		appendIndent(indent);
		buf.append("</option>\n");
	}

	private void appendBinders(int indent) {
		appendIndent(indent);
		buf.append("<binders>\n");
		appendCpnBinder(indent + 1);
		appendIndent(indent);
		buf.append("</binders>\n");
	}

	private void appendCpnBinder(int indent) {
		appendIndent(indent);
		buf.append("<cpnbinder id=\"Binder1\"\n");
		appendIndent(indent);
		buf.append("           x=\"300\"\n");
		appendIndent(indent);
		buf.append("           y=\"100\"\n");
		appendIndent(indent);
		buf.append("           width=\"1000\"\n");
		appendIndent(indent);
		buf.append("           height=\"600\">\n");
		appendSheets(indent + 1);
		appendZOrder(indent + 1);
		appendIndent(indent);
		buf.append("</cpnbinder>\n");
	}

	private void appendSheets(int indent) {
		appendIndent(indent);
		buf.append("<sheets>\n");
		appendCpnSheet(indent + 1);
		appendIndent(indent);
		buf.append("</sheets>\n");
	}

	private void appendCpnSheet(int indent) {
		appendIndent(indent);
		buf.append("<cpnsheet id=\"Sheet1\"\n");
		/*
		 * Have a place with an initial marking appear at the left in the middle.
		 */
		appendIndent(indent);
		buf.append("          panx=\"" + String.format(locale, "%.6f", -panx - 450.0) + "\"\n");
		appendIndent(indent);
		buf.append("          pany=\"" + String.format(locale, "%.6f", -pany) + "\"\n");
		appendIndent(indent);
		buf.append("          zoom=\"1.000000\"\n");
		appendIndent(indent);
		buf.append("          instance=\"Instance1\">\n");
		appendZOrder(indent + 1);
		appendIndent(indent);
		buf.append("</cpnsheet>\n");
	}

	private void appendZOrder(int indent) {
		appendIndent(indent);
		buf.append("<zorder>\n");
		appendIndent(indent + 1);
		buf.append("<position value=\"0\"/>\n");
		appendIndent(indent);
		buf.append("</zorder>\n");
	}

	private void appendMonitorBlock(int indent) {
		appendIndent(indent);
		buf.append("<monitorblock name=\"Monitors\"/>\n");
	}

	private void appendIndexNode(int indent) {
		appendIndent(indent);
		buf.append("<IndexNode expanded=\"true\">\n"); // CPN file
		appendIndent(indent + 1);
		buf.append("<IndexNode expanded=\"false\"/>\n"); // Step
		appendIndent(indent + 1);
		buf.append("<IndexNode expanded=\"false\"/>\n"); // Time
		appendIndent(indent + 1);
		buf.append("<IndexNode expanded=\"false\">\n"); // Options
		appendIndent(indent + 2);
		buf.append("<IndexNode expanded=\"false\"/>\n"); // Real timestamp 
		appendIndent(indent + 2);
		buf.append("<IndexNode expanded=\"false\"/>\n"); // Binding element fairness
		appendIndent(indent + 2);
		buf.append("<IndexNode expanded=\"false\"/>\n"); // Global BE fairness
		appendIndent(indent + 2);
		buf.append("<IndexNode expanded=\"false\"/>\n"); // Output directory
		appendIndent(indent + 2);
		buf.append("<IndexNode expanded=\"false\">\n"); // Performance report statistics
		appendIndent(indent + 3);
		buf.append("<IndexNode expanded=\"false\">\n"); // Simulation performance report
		appendIndent(indent + 4);
		buf.append("<IndexNode expanded=\"false\">\n"); // Timed
		for (int i = 0; i < 15; i++) {
			appendIndent(indent + 5);
			buf.append("<IndexNode expanded=\"false\"/>\n");
		}
		appendIndent(indent + 4);
		buf.append("</IndexNode>\n"); // Timed
		appendIndent(indent + 4);
		buf.append("<IndexNode expanded=\"false\">\n"); // Untimed
		for (int i = 0; i < 12; i++) {
			appendIndent(indent + 5);
			buf.append("<IndexNode expanded=\"false\"/>\n");
		}
		appendIndent(indent + 4);
		buf.append("</IndexNode>\n"); // Untimed
		appendIndent(indent + 3);
		buf.append("</IndexNode>\n"); // Simulation performance report
		appendIndent(indent + 3);
		buf.append("<IndexNode expanded=\"false\">\n"); // Replication performance report
		for (int i = 0; i < 12; i++) {
			appendIndent(indent + 4);
			buf.append("<IndexNode expanded=\"false\"/>\n");
		}
		appendIndent(indent + 3);
		buf.append("</IndexNode>\n"); // Replication performance report
		appendIndent(indent + 2);
		buf.append("</IndexNode>\n"); // Performance report statistics
		appendIndent(indent + 1);
		buf.append("</IndexNode>\n"); // Options
		appendIndent(indent + 1);
		buf.append("<IndexNode expanded=\"false\"/>\n"); // History
		appendIndent(indent + 1);
		buf.append("<IndexNode expanded=\"true\">\n"); // Declarations
		appendIndent(indent + 2);
		buf.append("<IndexNode expanded=\"false\">\n"); // Standard declarations
		appendIndent(indent + 3);
		buf.append("<IndexNode expanded=\"false\"/>\n"); // colset UNIT
		appendIndent(indent + 2);
		buf.append("</IndexNode>\n"); // Standard declarations
//		appendIndent(indent + 2);
//		buf.append("<IndexNode expanded=\"false\">\n"); // Petri net color sets
//		appendIndent(indent + 3);
//		buf.append("<IndexNode expanded=\"false\"/>\n"); // colset Token
//		appendIndent(indent + 2);
//		buf.append("</IndexNode>\n"); // Petri net color sets
//		appendIndent(indent + 2);
//		buf.append("<IndexNode expanded=\"false\">\n"); // Petri net declarations
//		appendIndent(indent + 3);
//		buf.append("<IndexNode expanded=\"false\"/>\n"); // var token
//		appendIndent(indent + 2);
//		buf.append("</IndexNode>\n"); // Petri net declarations
		appendIndent(indent + 1);
		buf.append("</IndexNode>\n"); // Declarations
		appendIndent(indent + 1);
		buf.append("<IndexNode expanded=\"false\"/>\n"); // Monitors
		appendIndent(indent + 1);
		buf.append("<IndexNode expanded=\"true\"/>\n"); // Net
		appendIndent(indent);
		buf.append("</IndexNode>\n"); // CPN file
	}
}
