package org.processmining.plugins.alignetc.result;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;

public class ResultsPaneUI extends JTextPane{

	private static final long serialVersionUID = 8443848579651279289L;

	public ResultsPaneUI(AlignETCResult res){
		
		super();
		
		//Content
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setContentType("text/html");
		this.setEditable(false);
		this.setCaretPosition(0);

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
		text.append(fontText + "Align ETC Precision Metric (<i>ap</i>): <b>" + rond(res.ap,4) + "</b><br>");
		text.append("</td></tr>");

		//Return
		this.setText(text.toString());
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
