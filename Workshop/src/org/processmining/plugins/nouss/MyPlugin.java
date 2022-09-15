package org.processmining.plugins.nouss;

import org.apache.tools.ant.types.resources.First;
import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.jfree.data.time.Second;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.python.bouncycastle.math.ec.ECCurve.Config;

@Plugin
      (
		name = "Your plug-in name", 
		parameterLabels = { "Name of your first input", "Name of your second input", "Name of your configuration" }, 
        returnLabels = { "Name of your output" }, 
        returnTypes = { Output.class }
	   )

public class MyPlugin {
	
	private Output yourPrivatePlugin(PluginContext context, First input1, Second input2, Config config) {
	    //TODO: The body of your plug-in.
	}
	
	@UITopiaVariant
	  (
	     affiliation = "Your affiliation", 
	     author = "Your name", 
	     email = "Your e-mail address"
	  )
	@PluginVariant 
	  (
		 variantLabel = "Your plug-in name, parameters", 
		 requiredParameterLabels = { 0, 1, 2 }
	  )
	public Output yourConfiguredPlugin(PluginContext context, First input1, Second input2, Config config) {
	    return yourPrivatePlugin(context, input1, input2, config);
	}
	
	@UITopiaVariant
	  (
		 affiliation = "Your affiliation", 
		 author = "Your name", 
		 email = "Your e-mail address"
	  )
	@PluginVariant
	  (
		 variantLabel = "Your plug-in name, dialog", 
		 requiredParameterLabels = { 0, 1 }
	  )
	public Output yourDefaultPlugin(UIPluginContext context, First input1, Second input2) {
	    Config configuration = new Config(input1, input2);
	    PluginDialog dialog = new PluginDialog(context, input1, input2, configuration);
	    InteractionResult result = context.showWizard("Your dialog title", true, true, dialog);
	    if (result == InteractionResult.FINISHED) {
	        return yourPrivatePlugin(context, input1, input2, configuration);
	    }
	    return null;
	}


}
