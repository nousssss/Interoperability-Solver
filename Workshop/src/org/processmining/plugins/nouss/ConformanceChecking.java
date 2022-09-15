package org.processmining.plugins.nouss;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;


// Creating a map between the log and the Petri net model

public class ConformanceChecking {
	
  private static TransEvClassMapping constructMapping(PetrinetGraph net, XLog log,
			XEventClass dummyEvClass, XEventClassifier eventClassifier) 
  {
		TransEvClassMapping mapping = new TransEvClassMapping(eventClassifier,dummyEvClass);
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, eventClassifier);
		for (Transition t : net.getTransitions())
		   {
				boolean mapped = false;
				for (XEventClass evClass : summary.getEventClasses().getClasses()) 
				 {
					String id = evClass.getId();
					String label = t.getLabel();
					id = id.substring(0, id.length()-1);
					if (label.equals(id)) 
					 {
					   mapping.put(t, evClass);
					   mapped = true;
					   break;
					 }
				  }
		   }
		return mapping;
   }
	
	public static void mapping() throws Exception 
	{
		XLog log = ImportLog.readLogFromFile();
		Object[] petri = ImportPetriNets.readPNFromFile();
		Petrinet net = (Petrinet) petri[0];
		Marking initialMarking = (Marking) petri[1];
		XEventClass dummyEvClass = new XEventClass("DUMMY", 99999);
		XEventClassifier eventClassifier = XLogInfoImpl.STANDARD_CLASSIFIER;
		TransEvClassMapping mapping = constructMapping(net, log, dummyEvClass,
		eventClassifier); 
	
	}
	

	
}
