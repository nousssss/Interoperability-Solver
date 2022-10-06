package org.processmining.plugins.interoperability;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.modelrepair.plugins.Uma_RepairModel_Subprocess_Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

public class Repair {

	public static Petrinet repair(UIPluginContext context, XLog log, Petrinet net, PNRepResult aligned) {
	//Reparation
    Uma_RepairModel_Subprocess_Plugin repairPlugin = new Uma_RepairModel_Subprocess_Plugin();
    Object[] repRes = repairPlugin.repairModel(context, log, net,aligned) ;
    Petrinet repairedNet = (Petrinet) repRes[0];
    return repairedNet;
	}
    
}
