/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/
package org.processmining.plugins.etconformance.ui;

import java.awt.Color;
import java.awt.Paint;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextPane;

import org.apache.commons.collections15.Transformer;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.etconformance.ETCResults;
import org.processmining.plugins.etconformance.data.PrefixAutomatonEdge;
import org.processmining.plugins.etconformance.data.PrefixAutomatonNode;

import com.fluxicon.slickerbox.colors.SlickerColors;
import com.fluxicon.slickerbox.components.SlickerTabbedPane;
import com.fluxicon.slickerbox.factory.SlickerFactory;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;


/**
 * Visualization of ETCAnalysis Results.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
@Plugin(name = "ETCResults Visualizer", 
		returnLabels = { "Visualized ETCAnalysisResults" }, 
		returnTypes = { JComponent.class }, 
		parameterLabels = "ETCResults")
@Visualizer
public class ETCResultsVisualization {
		
	@PluginVariant(	requiredParameterLabels = { 0 }, 
					variantLabel = "Default Visualization")
	public JComponent open(PluginContext context, ETCResults res) {
		
		//Create the General Pane
		SlickerTabbedPane pane = SlickerFactory.instance()
		.createTabbedPane("ETConformance Results", SlickerColors.COLOR_BG_1,
				SlickerColors.COLOR_FG,SlickerColors.COLOR_FG);
		
		//Results Tab
		pane.addTab("Results", createResults(res));
		writeResults(res);
		
		//Automaton Tab
		if(res.isAutomaton()) pane.addTab("Prefix Automaton", createPA(res));
		
		return pane;
	}
	


	private void writeResults(ETCResults res) {
		// Print all the results data
		System.out.println();
		System.out.println("-------------------------------------------------");
		System.out.println("Log:"+res.getLogName()+" | Model:"+res.getModelName());
		System.out.println("|Traces|:"+res.getNTraces()+" - |Non Fit Traces|:"+res.getnNonFitTraces());
		System.out.println("Average Trace Size: "+res.getAveSizeTraces());
		System.out.println("ETCp:"+rond(res.getEtcp(), 4)+" | Gamma:"+rond(res.getEscTh(),4));
		if(res.isConfidence()){
			System.out.println("["+rond(res.getLowerBound(),4)+" , "+rond(res.getUpperBound(),4)+"] | k:"+res.getkConfidence()+" | Trace Size: "+res.getAveSizeTraces());
		}
		System.out.print("Heuristics: ");
		if(res.isLazyInv()) System.out.print("Lazy Invisible");
		if(res.isRandomIndet()) System.out.print("  First Indeterminsm Solving");
		System.out.println();
		
		System.out.println("IN:"+res.getInStates()+" | 0-ESC:"+res.getEsc0States()+" | G-ESC:"+res.getEscGStates()+" | OUT:"+res.getOuterStates()+" | NON_FIT:"+res.getNonFitStates()+" | TOTAL:"+res.getTotalStates());
		System.out.println("-------------------------------------------------");
		System.out.println();
	}

	/**
	 * Create the visualization for the Results tab.
	 * @param res Results to be visualized.
	 * @return Pane with the result information.
	 */
	public JTextPane createResults(ETCResults res){	
		//Content
		JTextPane body = new JTextPane();
		body.setBorder(BorderFactory.createEmptyBorder());
		body.setContentType("text/html");
		body.setEditable(false);
		body.setCaretPosition(0);

		//Text
		StringBuffer text = new StringBuffer();
		text.append("<html><body bgcolor=\"#888888\" text=\"#111111\">");
		
		//METRIC
		text.append("<center><br><table width=\"95%\" border=\"0\" cellspacing=\"10\" cellpadding=\"10\">");
		text.append("<tr><td width=\"100%\" align=\"left\" bgcolor=\"#AAAAAA\">");
		text.append("<font face=\"arial,helvetica,sans-serif\" size=\"+2\" color=\"#111111\">");
		text.append("Metric");
		text.append("</font><hr noshade width=\"100%\" size=\"1\"><br>");

		String fontText = "<font face=\"helvetica,arial,sans-serif\" size=\"4\">";
		text.append(fontText + "ETC Precision Metric (<i>ETCp</i>): <b>" + rond(res.getEtcp(),4) + "</b><br>");
		text.append(fontText + "<i>Gamma</i>: <b>" + rond(res.getEscTh(),4) + "</b><br>");
		if(res.isConfidence()){
			text.append(fontText + "Confidence Interval: <b>[" + rond(res.getLowerBound(),4) +","+ rond(res.getUpperBound(),4) +"]</b><br>");
			text.append(fontText + "<i>k</i>: <b>" + res.getkConfidence() + "</b><br>");
		}
		text.append(fontText + "Heuristics: <b>");
		if(res.isLazyInv()) text.append("Lazy Invisible");
		if(res.isRandomIndet()) text.append("  First Indeterminism Solver");
		text.append("</b><br>");
		text.append("</td></tr>");
		
		
		//LOG AND MODEL
		text.append("<tr><td width=\"100%\" align=\"left\" bgcolor=\"#AAAAAA\">");
		text.append("<font face=\"arial,helvetica,sans-serif\" size=\"+2\" color=\"#111111\">");
		text.append("Log and Model");
		text.append("</font><hr noshade width=\"100%\" size=\"1\"><br>");

		text.append(fontText + "Model: <b>" + res.getModelName() + "</b><br>");
		text.append(fontText + "Log: <b>" + res.getLogName() + "</b><br>");
		text.append(fontText + "|Traces|: <b>" + res.getNTraces() + "</b><br>");
		String fitnessAlert ="";
		if(res.getnNonFitTraces() >= (res.getNTraces() * 0.9)) fitnessAlert="<b><font color=\"red\"> --- The fitness is really low! Check the correctness of the Model-Log tasks relation --- </font></b>";
		text.append(fontText + "|Non Fit Traces|: <b>" + res.getnNonFitTraces() + "</b> of <b>"+res.getNTraces()+"</b>"+fitnessAlert+"<br>");
		text.append(fontText + "Average Trace Size: <b>" + res.getAveSizeTraces() + "</b><br>");
		text.append("</td></tr>");
		
		//PREFIX AUTOMATON
		text.append("<tr><td width=\"100%\" align=\"left\" bgcolor=\"#AAAAAA\">");
		text.append("<font face=\"arial,helvetica,sans-serif\" size=\"+2\" color=\"#111111\">");
		text.append("Prefix Automaton");
		text.append("</font><hr noshade width=\"100%\" size=\"1\"><br>");

		text.append(fontText + "IN: <b>" + res.getInStates() + "</b><br>");
		text.append(fontText + "0-ESCAPING: <b>" + res.getEsc0States() + "</b><br>");
		text.append(fontText + "Gamma-ESCAPING: <b>" + res.getEscGStates() + "</b><br>");
		text.append(fontText + "OUTER: <b>" + res.getOuterStates() + "</b><br>");
		text.append(fontText + "NON FIT: <b>" + res.getNonFitStates() + "</b><br>");
		text.append(fontText + "NON DET: <b>" + res.getNonDetStates() + "</b><br>");
		text.append(fontText + "TOTAL: <b>" + res.getTotalStates() + "</b><br>");
		text.append("</td></tr>");

		//Return
		body.setText(text.toString());
		return body;

	}
	
	
	
	/**
	 * Create the visualization for the Prefix Automaton.
	 * @param res Results that contain the Prefix Automaton to be visualized.
	 * @return A Pane with the visualization of the Prefix Automaton.
	 */
	public JComponent createPA(ETCResults res){
		
		/**
		 * Transformer that paints each type of node with a different color.
		 */
		Transformer<PrefixAutomatonNode, Paint> vertexPaint = new Transformer<PrefixAutomatonNode, Paint>() {
		    public Paint transform(PrefixAutomatonNode node) {
		    	switch (node.getType()) {
					case IN:  return Color.white;
					case ESCAPING: return Color.red;
					case OUT: return Color.lightGray;
					case NON_FIT: return new Color(119,136,153);
					case NON_DET: return Color.cyan;
					default: return Color.green;
		    	}
		    }
		};
		
		Layout<PrefixAutomatonNode, PrefixAutomatonEdge> layout = new TreeLayout<PrefixAutomatonNode, PrefixAutomatonEdge>(res.getPA().getTree());
		//layout.setSize(new Dimension(300,300)); // sets the initial size of the space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		VisualizationViewer<PrefixAutomatonNode,PrefixAutomatonEdge> vv =
		new VisualizationViewer<PrefixAutomatonNode,PrefixAutomatonEdge>(layout);
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<PrefixAutomatonEdge>());
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<PrefixAutomatonNode>());
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<PrefixAutomatonNode, PrefixAutomatonEdge>());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).rotate(Math.toRadians(-90), vv.getCenter());
		
//		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
//		gm.setMode(Mode.PICKING);
//		gm.setMode(Mode.TRANSFORMING);
		
		PluggableGraphMouse gm = new PluggableGraphMouse();
		gm.add(new TranslatingGraphMousePlugin(MouseEvent.BUTTON3_MASK));
		gm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1.1f, 0.9f));
		gm.add(new RotatingGraphMousePlugin(MouseEvent.BUTTON2_MASK));
		gm.add(new PickingGraphMousePlugin<Object, Object>());
		
		vv.setGraphMouse(gm);
		return vv;
		

	}

	/**
	 * Function to round a double with the given number of decimals.
	 * @param n Number to be rounded.
	 * @param decimals Number of decimals wanted.
	 * @return The rounded number with the given number of decimals.
	 */
	private double rond(double n, int decimals){
		return Math.rint(n*(Math.pow(10,decimals)))/Math.pow(10,decimals);
	}


}

