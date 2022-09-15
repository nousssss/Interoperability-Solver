package org.processmining.plugins.petrinet.configurable;

import java.util.LinkedList;
import java.util.List;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.directed.petrinet.configurable.Configuration;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.LayoutUtils;

/**
 * 
 * @author dfahland
 */
@Plugin(name = "Configure Petri net partially",
	returnLabels = { "Configured net" },
	returnTypes = { ConfigurableResetInhibitorNet.class },
	parameterLabels = { "Configurable net", "Configuration" }, 
	help = "Configure a configurable Petri net..", userAccessible = true)
public class ConfigurePetriNet_Plugin {
	
	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland",
			email = "d.fahland@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Configure Petri net partially", requiredParameterLabels = { 0 })
	public ConfigurableResetInhibitorNet configureNet_withUI(UIPluginContext context, ConfigurableResetInhibitorNet net) {
		
		ConfigurePetriNet_UI ui_config_options = new ConfigurePetriNet_UI(net, false);
		List<Configuration> configurations = new LinkedList<Configuration>();
		try {
			if (ui_config_options.setParameters(context, configurations) == InteractionResult.CANCEL) {
				return cancel(context, "Cancelled by user.");
			}
		} catch (Exception e) {
			context.log(e);
			return cancel(context, "Invalid configuration selected");
		}
		return configureNet(context, net, configurations);
	}
	
	@PluginVariant(variantLabel = "Configure Petri net partially", requiredParameterLabels = { 0, 1 })
	public ConfigurableResetInhibitorNet configureNet(PluginContext context, ConfigurableResetInhibitorNet net, List<Configuration> configuration) {
		
		GraphLayoutConnection oldLayout = null;
		try {
			oldLayout = context.getConnectionManager().getFirstConnection(GraphLayoutConnection.class, context, net);
		} catch (ConnectionCannotBeObtained e) {
		}
		
		ConfigurableResetInhibitorNet configured = new ConfigurableResetInhibitorNet(net.getLabel());
		GraphLayoutConnection newLayout = new GraphLayoutConnection(configured);
		try {
			configured.cloneFrom(net, oldLayout, newLayout);
		} catch (Exception e) {
			context.log(e);
			return cancel(context, "Failed to configure net.");
		}
		
		try {
			System.out.println("Configuration "+configuration);
			LayoutUtils.setLayout(configured, newLayout);
			configured.configure(configuration);
			context.addConnection(newLayout);

		} catch (InvalidConfigurationException e) {
			return cancel(context, "Failed to configure net.");
		}
		
		return configured;
	}
	
	protected static ConfigurableResetInhibitorNet cancel(PluginContext context, String message) {
		System.out.println("[Configurable Nets]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}

}
