package org.processmining.plugins.multietc.automaton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JComponent;

import org.apache.commons.collections15.Transformer;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 * Visualization of Precision Automaton.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
@Plugin(name = "Automaton Visualizer", 
		returnLabels = { "Visualization Automaton" }, 
		returnTypes = { JComponent.class }, 
		parameterLabels = "Automaton")
@Visualizer
public class AutomatonVis {
	
	@PluginVariant(	requiredParameterLabels = { 0 }, 
			variantLabel = "Default Visualization")
	public JComponent open(PluginContext context, Automaton auto) {
		
		//Get the Layout and the View
		Layout<AutomatonNode, AutomatonEdge> layout = new KKLayout<AutomatonNode, AutomatonEdge>(auto.getJUNG());
		VisualizationViewer<AutomatonNode,AutomatonEdge> vv =
		new VisualizationViewer<AutomatonNode,AutomatonEdge>(layout, new Dimension(600,
                600));
		
		//Turn the automaton in horizontal
		//vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).rotate(Math.toRadians(-90), vv.getCenter());
		
		//Tool tips
		vv.setVertexToolTipTransformer(new VertexToolTipRepresentation());
		
		//Vertex Transformers
		vv.getRenderContext().setVertexLabelTransformer(new VertexWeightLabel());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		vv.getRenderContext().setVertexShapeTransformer(new VertexRectangularShape());
		vv.getRenderContext().setVertexFillPaintTransformer(new VertexSemPaint());
		
		//Edge Transformers
		vv.getRenderContext().setEdgeLabelTransformer(new EdgeTransitionLabel());
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<AutomatonNode,AutomatonEdge>());
		
		//Mouse Actions
		PluggableGraphMouse gm = new PluggableGraphMouse();
		gm.add(new TranslatingGraphMousePlugin(MouseEvent.BUTTON3_MASK));
		gm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1.1f, 0.9f));
		gm.add(new RotatingGraphMousePlugin(MouseEvent.BUTTON2_MASK));
		gm.add(new PickingGraphMousePlugin<Object, Object>());
		vv.setGraphMouse(gm);
		
		
		//Return the view
		return vv;
	}
	
	// Transformer to show Vertex representation as ToolTip 
    private class VertexToolTipRepresentation implements Transformer<AutomatonNode, String> {
    	public VertexToolTipRepresentation(){
    	}

		@SuppressWarnings("unchecked")
		public String transform(AutomatonNode node) {
			
			Integer non_escaping = null;
			if(node.getAttribute(AutomatonNode.NUM_NON_ESCAPING_TASKS) != null){
				non_escaping = (Integer) node.getAttribute(AutomatonNode.NUM_NON_ESCAPING_TASKS);
			}
			
			Integer available = null;
			if(node.getAttribute(AutomatonNode.NUM_AVAIL_TASKS) != null){
				available = (Integer) node.getAttribute(AutomatonNode.NUM_AVAIL_TASKS);
			}
			
			Set<Transition> escapingSet = null;
			if(node.getAttribute(AutomatonNode.ESCAPING_TASKS) != null){
				escapingSet = (Set<Transition> ) node.getAttribute(AutomatonNode.ESCAPING_TASKS);
			}
			
			String tip = node.toString();
			if(non_escaping != null && available != null && escapingSet != null){
				tip += " ->("+non_escaping+"/"+available+"):"+escapingSet.toString();
			}
			
			
			return tip;
		}
    }
    

    // Transformer to show Vertex representation as ToolTip 
    private class VertexRectangularShape implements Transformer<AutomatonNode, Shape> {
    	public VertexRectangularShape(){
    	}

		public Shape transform(AutomatonNode node) {
			return new Rectangle(-20, -10, 40, 20);
		}	
    }
    
    
    // Transformer to show Vertex representation as ToolTip 
    private class VertexWeightLabel implements Transformer<AutomatonNode, String> {
    	public VertexWeightLabel(){
    	}

		public String transform(AutomatonNode node) {
			return Double.toString(rond(node.getWeight(),1));
		}	
    }
    
    // Transformer to paint the vertex from Green to Red according to the escaping points 
    private class VertexSemPaint implements Transformer<AutomatonNode, Paint> {
    	public VertexSemPaint(){
    	}

		public Paint transform(AutomatonNode node) {
			if( node.getMarking() == null) return Color.WHITE;
			else return computeColor(node);
			
		}	
		public Paint computeColor(AutomatonNode node){
			int escaping =  (Integer) node.getAttribute(AutomatonNode.NUM_ESCAPING_TASKS);
			int available = (Integer) node.getAttribute(AutomatonNode.NUM_AVAIL_TASKS);
			
			double power = 1.0;
			if( available == 0) power = 1.0;
			else{
				power = 1.0 - ((double) escaping / (double) available);
			}			
			double H = power * 0.3; // Hue (note 0.3 = Green)
		    double S = 1.0; // Saturation
		    double B = 1.0; // Brightness

		    return Color.getHSBColor((float)H, (float)S, (float)B);
		}
    }
    
	
    // Transformer to show Label on the Vertexes 
    private class EdgeTransitionLabel implements Transformer<AutomatonEdge, String> {
    	public EdgeTransitionLabel(){
    	}

		public String transform(AutomatonEdge edge) {
			return edge.getTransition().getLabel();
		}
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
