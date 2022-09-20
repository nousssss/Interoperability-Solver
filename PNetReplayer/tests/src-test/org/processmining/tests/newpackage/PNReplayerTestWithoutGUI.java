/**
 * 
 */
package org.processmining.tests.newpackage;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.Test;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerSSD;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithoutILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * @author aadrians Feb 8, 2012
 * 
 */
public class PNReplayerTestWithoutGUI {
	@Test
	public void replayerTest() throws Throwable {
		Petrinet net = PetrinetFactory.newPetrinet("net");
		Place pStart = net.addPlace("start");
		Place p1 = net.addPlace("p1");
		Place p2 = net.addPlace("p2");
		Place pEnd = net.addPlace("end");
		Transition tA = net.addTransition("A");
		Transition tB = net.addTransition("B");
		Transition tC = net.addTransition("C");
		net.addArc(pStart, tA);
		net.addArc(tA, p1);
		net.addArc(p1, tB);
		net.addArc(tB, p2);
		net.addArc(p2, tC);
		net.addArc(tC, pEnd);

		// create log of 3 events: AAB
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XConceptExtension conceptExtension = XConceptExtension.instance();
		XLog log = factory.createLog();

		XTrace newTrace = factory.createTrace();
		XAttribute traceNameAttr = factory.createAttributeLiteral("concept:name", "test trace", conceptExtension);
		newTrace.getAttributes().put("concept:name", traceNameAttr);

		// create events
		XEventClass evClassDummy = new XEventClass("DUMMY", -1);

		XAttribute nameA = factory.createAttributeLiteral("concept:name", "A", conceptExtension);
		XAttribute nameB = factory.createAttributeLiteral("concept:name", "B", conceptExtension);
		XAttribute transitionComplete = factory.createAttributeLiteral("lifecycle:transition", "complete",
				XConceptExtension.instance());

		XEvent eventA = factory.createEvent();
		eventA.getAttributes().put("concept:name", nameA);
		eventA.getAttributes().put("lifecycle:transition", transitionComplete);
		newTrace.add(eventA);

		XEvent eventA2 = factory.createEvent();
		eventA2.getAttributes().put("concept:name", nameA);
		eventA2.getAttributes().put("lifecycle:transition", transitionComplete);
		newTrace.add(eventA2);

		XEvent eventB = factory.createEvent();
		eventB.getAttributes().put("concept:name", nameB);
		eventB.getAttributes().put("lifecycle:transition", transitionComplete);
		newTrace.add(eventB);

		log.add(newTrace);

		// create mapping
		TransEvClassMapping mapping = new TransEvClassMapping(XLogInfoImpl.STANDARD_CLASSIFIER, evClassDummy);
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);
		for (XEventClass ec : logInfo.getEventClasses().getClasses()) {
			if (ec.toString().equals("A+complete")) {
				mapping.put(tA, ec);
			} else if (ec.toString().equals("B+complete")) {
				mapping.put(tB, ec);
			}
		}
		mapping.put(tC, evClassDummy);

		// create parameter
		CostBasedCompleteParam parameter = new CostBasedCompleteParam(logInfo.getEventClasses().getClasses(),
				evClassDummy, net.getTransitions(), 2, 5);
		parameter.setGUIMode(false);
		parameter.setCreateConn(false);
		
		Marking initMarking = new Marking();
		initMarking.add(pStart);
		parameter.setInitialMarking(initMarking);
		
		Marking finalMarking = new Marking();
		finalMarking.add(pEnd);
		parameter.setFinalMarkings(new Marking[] {finalMarking});
		parameter.setMaxNumOfStates(200000);
		
		// instantiate replayer
		PNLogReplayer replayer = new PNLogReplayer();
		
		// select algorithm without ILP
		PetrinetReplayerWithoutILP replWithoutILP = new PetrinetReplayerWithoutILP();
		PNRepResult pnRepResult = replayer.replayLog(null, net, log, mapping, replWithoutILP, parameter);
		validateResult(pnRepResult);
		
		// select algorithm SSD
		PetrinetReplayerSSD replSSD = new PetrinetReplayerSSD();
		PNRepResult pnRepResult2 = replayer.replayLog(null, net, log, mapping, replSSD, parameter);
		validateResult(pnRepResult2);
		
	}

	private void validateResult(PNRepResult pnRepResult) {
		for (SyncReplayResult syncRepResult : pnRepResult){
			assert(syncRepResult.isReliable()); // result should be reliable
			Map<String, Double> info = syncRepResult.getInfo();			
			assert(Double.compare(7.000, info.get(PNRepResult.RAWFITNESSCOST)) == 0); // one move log, one move model
			
			List<Object> nodeInstance = syncRepResult.getNodeInstance();
			List<StepTypes> stepTypes = syncRepResult.getStepTypes();
			
			Iterator<Object> it = nodeInstance.iterator();
			for (StepTypes st : stepTypes){
				
				switch(st){
					case LMGOOD:
						it.next();
//						System.out.print(((Transition)it.next()).getLabel());
//						System.out.print("(L/M)");
						break;
					case L:
						assert(it.next().toString().equals("A+complete"));
//						System.out.print(it.next());
//						System.out.print("(L)");
						break;
					case MREAL:
						assert(((Transition)it.next()).getLabel().equals("C"));
//						System.out.print(((Transition)it.next()).getLabel());
//						System.out.print("(M)");
						break;
					default:
						assert(false);
				}
			}
		}
	}
}
