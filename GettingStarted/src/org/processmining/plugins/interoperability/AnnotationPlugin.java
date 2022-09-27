package org.processmining.plugins.interoperability;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;


@Plugin
(
	name = "Annotation Plugin", 
	parameterLabels = {"Petri net" , "Event Log"},
    returnLabels = { "Annotated petrinet" }, 
    returnTypes = { LabelledPetrinet.class },
    userAccessible = true
 )

public class AnnotationPlugin {
	
	@UITopiaVariant
	  (
	     affiliation = "CDTA", 
	     author = "Bachiri Inas", 
	     email = "ji_bachiri@esi.dz"
	  )
	@PluginVariant 
	  (
		 variantLabel = "Annotation Plugin", 
		 requiredParameterLabels = {0,1}
	  )
	
	public static LabelledPetrinet annotate(UIPluginContext context,Petrinet net, XLog log) {
		LabelledPetrinet labeledNet = LabelledPetrinetFactory.clonePetrinet(net);

		for (LabelledTransition trans : labeledNet.getTransitions()) {
			for (XTrace trace : log) 
			 {
				for (XEvent event : trace) {
					String id = event.getAttributes().get("concept:name").toString();
				    String name = trans.getLabel();
				    if (name.equals(id) && event.getAttributes().containsKey("RECEIVE_MESSAGE") ) 
				     {
				    	trans.setMsgName(event.getAttributes().get("RECEIVE_MESSAGE").toString());
				    	trans.setMsgType(event.getAttributes().get("KIND").toString());
				   
				        break;
				     }
				    if (name.equals(id) && event.getAttributes().containsKey("SEND_MESSAGE") ) 
				     {
				    	trans.setMsgName(event.getAttributes().get("SEND_MESSAGE").toString());
				    	trans.setMsgType(event.getAttributes().get("KIND").toString());
				   
				        break;
				     }
			  }
		}
	}
		return labeledNet;

}
}
