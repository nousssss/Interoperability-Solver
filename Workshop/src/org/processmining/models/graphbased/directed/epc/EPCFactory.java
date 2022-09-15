package org.processmining.models.graphbased.directed.epc;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

public class EPCFactory {

	private EPCFactory() {
	}

	public static EPC newEPC(String label) {
		return new EPCImpl(label);
	}

	public static ConfigurableEPC newConfigurableEPC(String label) {
		return new ConfigurableEPCImpl(label);
	}

	public static InstanceEPC newInstanceEPC(String label) {
		return new InstanceEPCImpl(label);
	}

	public static EPC cloneEPC(EPC epc) {
		EPCImpl newEPC = new EPCImpl(epc.getLabel());
		newEPC.cloneFrom(epc);
		return newEPC;
	}

	public static ConfigurableEPC cloneConfigurableEPC(ConfigurableEPC epc) {
		ConfigurableEPCImpl newEPC = new ConfigurableEPCImpl(epc.getLabel());
		newEPC.cloneFrom(epc);
		return newEPC;
	}

	public static InstanceEPC cloneInstanceEPC(InstanceEPC epc) {
		InstanceEPCImpl newEPC = new InstanceEPCImpl(epc.getLabel());
		newEPC.cloneFrom(epc);
		return newEPC;
	}

	@Plugin(name = "EPC to Configurable EPC", returnLabels = { "Configurable EPC" }, returnTypes = { ConfigurableEPC.class }, parameterLabels = { "EPC" }, help = "Converts an EPC into a Configurable EPC without configurable objects.", userAccessible = true)
	public static ConfigurableEPC toConfigurableEPC(PluginContext context, EPC epc) {
		ConfigurableEPCImpl newEPC = new ConfigurableEPCImpl(epc.getLabel());
		newEPC.cloneFrom(epc);
		context.getFutureResult(0).setLabel(newEPC.getLabel());
		return newEPC;
	}

	@Plugin(name = "instance EPC to Configurable EPC", returnLabels = { "Configurable EPC" }, returnTypes = { ConfigurableEPC.class }, parameterLabels = { "instance EPC" }, help = "Converts an instance EPC into a loop-free Configurable EPC without configurable objects and without OR and XOR connectors.", userAccessible = true)
	public static ConfigurableEPC toConfigurableEPC(PluginContext context, InstanceEPC instanceEpc) {
		ConfigurableEPCImpl newEPC = new ConfigurableEPCImpl(instanceEpc.getLabel());
		newEPC.cloneFrom(instanceEpc);
		context.getFutureResult(0).setLabel(newEPC.getLabel());
		return newEPC;
	}

	@Plugin(name = "instance EPC to EPC", returnLabels = { "EPC" }, returnTypes = { EPC.class }, parameterLabels = { "instance EPC" }, help = "Converts an instance EPC into a loop-free EPC without OR and XOR connectors.", userAccessible = true)
	public static EPC toEPC(PluginContext context, InstanceEPC instanceEpc) {
		EPCImpl newEPC = new EPCImpl(instanceEpc.getLabel());
		newEPC.cloneFrom(instanceEpc);
		context.getFutureResult(0).setLabel(newEPC.getLabel());
		return newEPC;
	}

}
