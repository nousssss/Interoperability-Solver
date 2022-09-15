/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/
package org.processmining.plugins.etconformance.util;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XSemanticExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * Class with some utilities to create a Minimal Disconformant 
 * Traces (MDT) log.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class MDTUtils {
	
	/** Factory of ProM visual components */
	static XFactory factory = XFactoryRegistry.instance().currentDefault();
	
	/**
	 * Create a MDT (a XLog) setting and initializing all the parameters.
	 * @param name Name of the log.
	 * @param descrip Description of the log.
	 * @param source Source of the log.
	 * @return An empty log with all the parameters set.
	 */
	public static XLog createMDT(String name, String descrip, String source){
		//Create a log
		XLog MDT = factory.createLog();
		//Add all the extensions		
		MDT.getExtensions().add(XConceptExtension.instance());
		MDT.getExtensions().add(XOrganizationalExtension.instance());
		MDT.getExtensions().add(XLifecycleExtension.instance());
		MDT.getExtensions().add(XSemanticExtension.instance());
		MDT.getExtensions().add(XTimeExtension.instance());
		//Set Source
		XAttributeLiteral sourceAttribute = factory.createAttributeLiteral("source", source, null);
		MDT.getAttributes().put(sourceAttribute.getKey(), sourceAttribute);
		//Set Name
		XConceptExtension.instance().assignName(MDT, name);
		//Set life cycle
		XLifecycleExtension.instance().assignModel(MDT, XLifecycleExtension.VALUE_MODEL_STANDARD);
		//Set description
		XAttributeLiteral description = factory.createAttributeLiteral("description", descrip, null);
		MDT.getAttributes().put(description.getKey(), description);
		
		return MDT;
	}
	
	/**
	 * Create a new trace with the given parameters.
	 * @param name Name of the trace.
	 * @return A trace with no events.
	 */
	public static XTrace createTrace(String name){
		XTrace trace = factory.createTrace();
		XConceptExtension.instance().assignName(trace, name);		
		return trace;
	}
	
	/**
	 * Create an event with the given parameters.
	 * @param name Name of the activity of the event.
	 * @return An event for the given activity.
	 */
	public static XEvent createEvent(String name){
		XEvent event = factory.createEvent();
		XConceptExtension.instance().assignName(event, name);
		return event;
	}
	
	/**
	 * Add the severity fields to the given trace.
	 * @param trace Trace for adding the severity.
	 * @param freq Value of the frequency factor.
	 * @param alt Value of the alternation factor.
	 * @param stab Value of the stabing factor.
	 */
	public static void addSeverity(XTrace trace, Double freq, Double alt, Double stab){	
		XAttributeContinuous freqAttribute = factory.createAttributeContinuous("Severity:frequency", rond(freq,10), null);
		trace.getAttributes().put(freqAttribute.getKey(), freqAttribute);
		
		XAttributeContinuous altAttribute = factory.createAttributeContinuous("Severity:alternation", rond(alt,10), null);
		trace.getAttributes().put(altAttribute.getKey(), altAttribute);
		
		XAttributeContinuous stabAttribute = factory.createAttributeContinuous("Severity:stability", rond(stab,10), null);
		trace.getAttributes().put(stabAttribute.getKey(), stabAttribute);
	}
	
	/**
	 * Function to round a double with X decimals.
	 * @param n Number to be rounded.
	 * @param decimals Number of decimals wanted.
	 * @return Number rounded with the given decimals.
	 */
	private static double rond(double n, int decimals){
		return Math.rint(n*(Math.pow(10,decimals)))/Math.pow(10,decimals);
	}

}
