package org.processmining.plugins.nouss;

import org.apache.tools.ant.types.resources.First;
import org.deckfour.xes.model.XLog;
import org.jfree.data.time.Second;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

@Plugin
      (
		name = "Interoperability Solver", 
		parameterLabels = { "Event Log", "Process Model"}, 
        returnLabels = { "Replay results" }, 
        returnTypes = { PNRepResult.class }
	   )

public class InteroperabilitySolver {
	
	private PNRepResult interoperabilitySolver(PluginContext context, PetrinetGraph net, XLog log) {
	    //TODO: The body of your plug-in.
		return null;
	}
	
	@UITopiaVariant
	  (
	     affiliation = "CDTA", 
	     author = "Bachiri Inas", 
	     email = "ji_bachiri@esi.dz"
	  )
	@PluginVariant 
	  (
		 variantLabel = "Your plug-in name, parameters", 
		 requiredParameterLabels = { 0, 1}
	  )
	public String yourConfiguredPlugin(PluginContext context, String input1,  String input2, String config) {
	    return null;
	}
	
	@UITopiaVariant
	  (
		  affiliation = "CDTA", 
		  author = "Bachiri Inas", 
		  email = "ji_bachiri@esi.dz"
	  )
	@PluginVariant
	  (
		 variantLabel = "Your plug-in name, dialog", 
		 requiredParameterLabels = {}
	  )
	public String yourDefaultPlugin(UIPluginContext context, First input1, Second input2) throws Exception {
		 //import the log 
		XLog log = ImportLog.readLogFromFile();
		   // import the petrinet
		Object[] petri = ImportPetriNets.readPNFromFile();
		
	    return null;
	}


}
