package org.processmining.plugins.pnml.elements.extensions.configurations;

import java.util.List;

import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableParameterInteger;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.base.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

public class PnmlParameter extends PnmlElement {

	public final static String TAG = "parameter";

	protected String name;
	protected String minVal;
	protected String maxVal;
	protected String allowedVals;
	protected String defaultVal;

	protected PnmlParameter(String tag) {
		super(tag);
		name = null;
		minVal = null;
		maxVal = null;
		allowedVals = null;
		defaultVal = null;
	}

	protected PnmlParameter() {
		this(TAG);
	}

	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		super.importAttributes(xpp, pnml);
		importName(xpp, pnml);
		importMinVal(xpp, pnml);
		importMaxVal(xpp, pnml);
		importAllowed(xpp, pnml);
		importDefault(xpp, pnml);
	}

	private void importName(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "name");
		if (value != null) {
			name = value;
		}
	}

	private void importMinVal(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "min");
		if (value != null) {
			minVal = value;
		}
	}

	private void importMaxVal(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "max");
		if (value != null) {
			maxVal = value;
		}
	}

	private void importAllowed(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "allowed");
		if (value != null) {
			allowedVals = value;
		}
	}

	private void importDefault(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "default");
		if (value != null) {
			defaultVal = value;
		}
	}

	protected String exportAttributes(Pnml pnml) {
		return super.exportAttributes(pnml) + exportName(pnml) + exportMinVal(pnml) + exportMaxVal(pnml)
				+ exportAllowed(pnml) + exportDefault(pnml);
	}

	private String exportName(Pnml pnml) {
		if (name != null) {
			return exportAttribute("name", name, pnml);
		}
		return "";
	}

	private String exportMinVal(Pnml pnml) {
		if (minVal != null) {
			return exportAttribute("min", minVal, pnml);
		}
		return "";
	}

	private String exportMaxVal(Pnml pnml) {
		if (maxVal != null) {
			return exportAttribute("max", maxVal, pnml);
		}
		return "";
	}

	private String exportAllowed(Pnml pnml) {
		if (allowedVals != null) {
			return exportAttribute("allowed", allowedVals, pnml);
		}
		return "";
	}

	private String exportDefault(Pnml pnml) {
		if (defaultVal != null) {
			return exportAttribute("default", defaultVal, pnml);
		}
		return "";
	}

	public void convertToNet(List<ConfigurableParameter<Integer>> parameterList) {
		try {
			int min = Integer.parseInt(minVal);
			int max = Integer.parseInt(maxVal);
			int def = Integer.parseInt(defaultVal);
			ConfigurableParameterInteger parameter = new ConfigurableParameterInteger(name, min, max, def);
			parameterList.add(parameter);
			// TODO Connect parameter to arc
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PnmlParameter convertFromNet(ConfigurableParameter<Integer> parameter) {
		name = new String(parameter.getId());
		minVal = String.valueOf(parameter.getIntervalMin());
		maxVal = String.valueOf(parameter.getIntervalMax());
		defaultVal = String.valueOf(parameter.getValue());
		return this;
	}
}
