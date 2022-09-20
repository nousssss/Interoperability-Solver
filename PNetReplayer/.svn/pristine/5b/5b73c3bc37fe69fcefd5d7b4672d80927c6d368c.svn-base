/**
 * 
 */
package org.processmining.plugins.petrinet.manifestreplayer;

import java.util.ArrayList;

import org.deckfour.xes.classification.XEventClass;

/**
 * @author aadrians Feb 26, 2012
 * 
 */
public class EvClassPattern extends ArrayList<XEventClass> {
	private static final long serialVersionUID = 1605098098617975968L;

	public EvClassPattern() {
		super();
	}

	public EvClassPattern(int capacity) {
		super(capacity);
	}

	public String toString() {
		if (size() == 0) {
			return "Empty Pattern";
		} else {
			String outString = ""; // result
			String lifecycle = ""; // identified lifecycle
			String className = ""; // class name
			String prevClassName = ""; // previous event class

			String lifecycleSeparator = ""; // lifecycle separator

			// collect similar event classes
			for (XEventClass ec : this) {
				int plusPos = ec.getId().lastIndexOf("+");
				if ((plusPos > 0) && (plusPos < ec.getId().length())) {
					// there is a plus
					className = ec.getId().substring(0, plusPos);
					lifecycle = ec.getId().substring(plusPos + 1);
				} else {
					className = ec.getId();
					lifecycle = "";
				}

				// insert to mapping
				if (!prevClassName.equals(className)){
					// close previous and print new event class name
					if (prevClassName.equals("")){
						// first case
						outString += className + "(";
					} else {
						// next case
						outString += ")," + className + "(";
					}
					prevClassName = className;
					lifecycleSeparator = "";
				} 

				outString += lifecycleSeparator;
				if (lifecycle.equals("")){
					outString += "+";
				} else {
					outString += lifecycle; 
				}
				lifecycleSeparator = ",";

			}
			outString += ")";
			return outString;
		}
	}
}
