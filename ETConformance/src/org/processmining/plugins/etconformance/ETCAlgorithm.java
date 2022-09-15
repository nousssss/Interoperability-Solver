/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/
package org.processmining.plugins.etconformance;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.etconformance.data.PrefixAutomaton;

/**
 * Class with the ETConformance Algorithm.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class ETCAlgorithm {
	
	/**
	 * Perform the ETConformance algorithm.
	 * @param context Context of the plug-in.
	 * @param log Log.
	 * @param net Model.
	 * @param marking Initial Marking of the model.
	 * @param mapping Connection between the log and the model.
	 * @param res Results object to store the results.
	 * @return Minimal Disconformant Traces (traces leading to an imprecision).
	 * @throws Exception
	 */
	public static XLog exec(PluginContext context, XLog log, Petrinet net, Marking marking, TransEvClassMapping mapping,
			ETCResults res) throws Exception{

long startT = System.currentTimeMillis();
		
		//Get the progress bar
		//TODO Progress bar in more detail.
//		Progress prog = context.getProgress();
//		prog.setMinimum(0);
		int numSteps = 4;
		if(res.isConfidence()) ++numSteps;
		if(res.isMdt()) ++ numSteps;
//		prog.setMaximum(numSteps);
		
		// 1- Constructing the Prefix Automaton
//		context.log("Constructing the Prefix Automaton");
		PrefixAutomaton pA = new PrefixAutomaton(log, res, mapping);
		if(res.isAutomaton()) res.setPA(pA);
//		prog.inc();
		
		// 2- Extending it with model information
//		context.log("Extending the Prefix Automaton");
		pA.enrich(context, net, marking, mapping, res);
//		prog.inc();
		
		// 3- Detecting the Escaping States
//		context.log("Computing the Escaping States");
		pA.detectEscaping(res);
//		prog.inc();
		
		// 4- Computing the metrics
//		context.log("Computing the Metrics");
		pA.computeMetrics(res);
//		prog.inc();
		
		long metricsT = System.currentTimeMillis();
		
		// 5- Compute the Confidence Interval (if it is required)
		if(res.isConfidence()){
//			context.log("Computing the Confidence Interval");
			pA.computeConfidence(res);
//			prog.inc();
		}
		
		long intervalT = System.currentTimeMillis();
		
		//6- Build the MDT and the severity (if it is required)
		XLog MDT = null;
		if(res.isMdt()){
//			context.log("Computing Minimal Disconformant Traces (MDT)");
			MDT = pA.createMDT(res);
//			prog.inc();
		}
		
		long MDTT = System.currentTimeMillis();
		
//		System.out.println("Time (only metrics): "+(metricsT-startT)+" ms.");
//		System.out.println("Time (metrics + interval): "+(intervalT-startT)+" ms.");
//		System.out.println("Time (all): "+(MDTT-startT)+" ms.");
		
		//Return
		return MDT;

	}

}
