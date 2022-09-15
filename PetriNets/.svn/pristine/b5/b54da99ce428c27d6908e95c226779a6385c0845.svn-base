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
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeatureGroup;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.LayoutUtils;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * 
 * @author dfahland
 */
@Plugin(name = "Create/Edit Configurable Petri net",
	returnLabels = { "Configurable net" },
	returnTypes = { ConfigurableResetInhibitorNet.class },
	parameterLabels = { "Petri net", "Initial marking", "Allowed Configurations" }, 
	help = "Create a configurable Petri net from a given net.", userAccessible = true)
public class CreateConfigurableNet_Plugin {
	
	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland",
			email = "d.fahland@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Create/Edit Configurable Petri net", requiredParameterLabels = { 0 })
	public ConfigurableResetInhibitorNet createConfigurableNet(UIPluginContext context, ResetInhibitorNet net) {
		
		Marking m = null;
		try {
			InitialMarkingConnection m_conn = context.getConnectionManager().getFirstConnection(InitialMarkingConnection.class, context, net);
			m = m_conn.getObjectWithRole(InitialMarkingConnection.MARKING);
		} catch (ConnectionCannotBeObtained e) {
		}
		
		List<ConfigurableFeatureGroup> configs = new LinkedList<ConfigurableFeatureGroup>();
		if (net instanceof ConfigurablePetrinet<?>) {
			configs = ((ConfigurablePetrinet<?>)net).getConfigurableFeatureGroups();
			m = ((ConfigurablePetrinet<?>)net).getConfiguredMarking();
		}
//		try {
//			configs = ConfigurableFeatureGroup.createDefaultFeatureGroups(net, m);
//		} catch (InvalidConfigurationException e) {
//			context.log(e);
//			return cancel(context, "The net provides an invalid configuration.");
//		}
		
		try {
			CreateConfigurableNet_UI ui_config_options = new CreateConfigurableNet_UI(net, configs);
			if (ui_config_options.setParameters(context, configs) == InteractionResult.CANCEL) {
				return cancel(context, "Cancelled by user.");
			}
		} catch (Exception e) {
			context.log(e);
			return cancel(context, "Invalid configuration.");
		}

		return createConfigurableNet(context, net, m, configs);
	}
	
	@PluginVariant(variantLabel = "Create Configurable Petri net", requiredParameterLabels = { 0, 1, 2 })
	public ConfigurableResetInhibitorNet createConfigurableNet(PluginContext context, ResetInhibitorNet net, Marking m, List<ConfigurableFeatureGroup> config) {

		GraphLayoutConnection netLayout = null;
		try {
			netLayout = context.getConnectionManager().getFirstConnection(GraphLayoutConnection.class, context, net);
		} catch (ConnectionCannotBeObtained e) {
		}
		
		try {
			Object[] net_and_layout = createConfigurableNet(net, m, netLayout, config);
			ConfigurableResetInhibitorNet configurable = (ConfigurableResetInhibitorNet)net_and_layout[0];
			GraphLayoutConnection configurableLayout = (GraphLayoutConnection)net_and_layout[1];
			context.addConnection(configurableLayout);
			return configurable;
		} catch (Exception e) {
			context.log(e);
			return cancel(context, "Invalid configuration features. Could not create configurable net.");
		}
	}
	
	
	/**
	 * Clone net into a {@link ConfigurableResetInhibitorNet} and clone the
	 * netLayout of net into a new layout of the configurable net. The provided
	 * configuration parameters will be given to the configurable net.
	 * 
	 * @param net
	 * @param m
	 * @param netLayout
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public static Object[] createConfigurableNet(ResetInhibitorNet net, Marking m, GraphLayoutConnection netLayout, List<ConfigurableFeatureGroup> config) throws Exception {
		ConfigurableResetInhibitorNet configurableNet = new ConfigurableResetInhibitorNet(net.getLabel());
		GraphLayoutConnection layout = new GraphLayoutConnection(configurableNet);
		configurableNet.cloneFrom(net, m, config, netLayout, layout);
		LayoutUtils.setLayout(configurableNet, layout);
		return new Object[] { configurableNet, layout };
	}
	
	protected static ConfigurableResetInhibitorNet cancel(PluginContext context, String message) {
		System.out.println("[Create Configurable Net]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}

}
