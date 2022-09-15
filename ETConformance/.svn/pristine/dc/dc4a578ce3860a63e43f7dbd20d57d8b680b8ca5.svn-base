package org.processmining.plugins.multietc.res;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextPane;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.multietc.sett.MultiETCSettings;

/**
 * Visualization of MultiETC Results.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
@Plugin(name = "MultiETCResult Visualizer", 
		returnLabels = { "Visualization MultiETC Result" }, 
		returnTypes = { JComponent.class }, 
		parameterLabels = "MultiETCResult")
@Visualizer
public class MultiETCResultVis {
	
	@PluginVariant(	requiredParameterLabels = { 0 }, 
			variantLabel = "Default Visualization")
	public JComponent open(PluginContext context, MultiETCResult res) {
		
		JTextPane pane = new JTextPane();
		
		//Content
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setContentType("text/html");
		pane.setEditable(false);
		pane.setCaretPosition(0);

		//Text
		StringBuffer text = new StringBuffer();
		text.append("<html><body bgcolor=\"#888888\" text=\"#111111\">");
		
		
		
		//Conformance
		text.append("<center><br><table width=\"95%\" border=\"0\" cellspacing=\"10\" cellpadding=\"10\">");
		text.append("<tr><td width=\"100%\" align=\"left\" bgcolor=\"#AAAAAA\">");
		text.append("<font face=\"arial,helvetica,sans-serif\" size=\"+2\" color=\"#111111\">");
		text.append("Conformance");
		text.append("</font><hr noshade width=\"100%\" size=\"1\"><br>");
		String fontText = "<font face=\"helvetica,arial,sans-serif\" size=\"4\">";
		
		if(res.getAttribute(MultiETCResult.PRECISION) != null)
			text.append(fontText + "Precision: <b>" + rond((Double) res.getAttribute(MultiETCResult.PRECISION),4) + "</b><br>");
		if(res.getAttribute(MultiETCResult.PRECISION) != null)
			text.append(fontText + "Backwards Precision: <b>" + rond((Double) res.getAttribute(MultiETCResult.BACK_PRECISION),4) + "</b><br>");
		if(res.getAttribute(MultiETCResult.PRECISION) != null)
			text.append(fontText + "Balanced Precision: <b>" + rond((Double) res.getAttribute(MultiETCResult.BALANCED_PRECISION),4) + "</b><br>");
		if(res.getAttribute(MultiETCResult.ETC_SETT) != null){
			MultiETCSettings sett = (MultiETCSettings) res.getAttribute(MultiETCResult.ETC_SETT);
			if(sett.getAlgorithm() != null)
				text.append(fontText + "Algortihm: <b>" + sett.getAlgorithm() + "</b><br>");
			if(sett.getAlgorithm() != null)
				text.append(fontText + "Representation: <b>" + sett.getRepresentation() + "</b><br>");
		}
		
		
		text.append("</td></tr>");
		
		//Conformance
		text.append("<tr><td width=\"100%\" align=\"left\" bgcolor=\"#AAAAAA\">");
		text.append("<font face=\"arial,helvetica,sans-serif\" size=\"+2\" color=\"#111111\">");
		text.append("Automaton");
		text.append("</font><hr noshade width=\"100%\" size=\"1\"><br>");
		fontText = "<font face=\"helvetica,arial,sans-serif\" size=\"4\">";
		
		if(res.getAttribute(MultiETCResult.AUTO_STATES) != null)
			text.append(fontText + "States Total: <b>" + res.getAttribute(MultiETCResult.AUTO_STATES) + "</b><br>");
		if(res.getAttribute(MultiETCResult.AUTO_STATES_IN) != null)
			text.append(fontText + "States IN: <b>" + res.getAttribute(MultiETCResult.AUTO_STATES_IN) + "</b><br>");
		if(res.getAttribute(MultiETCResult.AUTO_STATES_OUT) != null)
			text.append(fontText + "States OUT: <b>" + res.getAttribute(MultiETCResult.AUTO_STATES_OUT) + "</b><br>");
		
		
		text.append("</td></tr>");
		
		//Conformance
		text.append("<tr><td width=\"100%\" align=\"left\" bgcolor=\"#AAAAAA\">");
		text.append("<font face=\"arial,helvetica,sans-serif\" size=\"+2\" color=\"#111111\">");
		text.append("Backwards Automaton");
		text.append("</font><hr noshade width=\"100%\" size=\"1\"><br>");
		fontText = "<font face=\"helvetica,arial,sans-serif\" size=\"4\">";
		
		if(res.getAttribute(MultiETCResult.AUTO_STATES_BACK) != null)
			text.append(fontText + "States Total: <b>" + res.getAttribute(MultiETCResult.AUTO_STATES_BACK) + "</b><br>");
		if(res.getAttribute(MultiETCResult.AUTO_STATES_IN_BACK) != null)
			text.append(fontText + "States IN: <b>" + res.getAttribute(MultiETCResult.AUTO_STATES_IN_BACK) + "</b><br>");
		if(res.getAttribute(MultiETCResult.AUTO_STATES_OUT_BACK) != null)
			text.append(fontText + "States OUT: <b>" + res.getAttribute(MultiETCResult.AUTO_STATES_OUT_BACK) + "</b><br>");
		
		
		text.append("</td></tr>");

		//Return
		pane.setText(text.toString());
		return pane;
		
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
