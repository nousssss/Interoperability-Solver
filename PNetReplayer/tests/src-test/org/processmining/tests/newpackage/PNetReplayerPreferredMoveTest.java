package org.processmining.tests.newpackage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import org.processmining.plugins.astar.petrinet.PetrinetReplayerMovePreferenceAwareWithILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import com.google.common.collect.ImmutableSet;

public class PNetReplayerPreferredMoveTest {
	@Test
	public void replayerTest() throws Throwable {
		
		Petrinet net = PetrinetFactory.newPetrinet("net");
		Place pStart = net.addPlace("start");
		Place p1 = net.addPlace("p1");
		Transition tA = net.addTransition("A");
		Transition tB = net.addTransition("B");
		net.addArc(pStart, tA);
		net.addArc(tA, p1);
		net.addArc(p1, tB);

		// create log of 3 events: BA
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
		
		XEvent eventB = factory.createEvent();
		eventB.getAttributes().put("concept:name", nameB);
		eventB.getAttributes().put("lifecycle:transition", transitionComplete);
		newTrace.add(eventB);


		XEvent eventA = factory.createEvent();
		eventA.getAttributes().put("concept:name", nameA);
		eventA.getAttributes().put("lifecycle:transition", transitionComplete);
		newTrace.add(eventA);

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

		// create parameter
		CostBasedCompleteParam parameter = new CostBasedCompleteParam(logInfo.getEventClasses().getClasses(),
				evClassDummy, net.getTransitions(), 1, 1);
		parameter.setGUIMode(false);
		parameter.setCreateConn(false);
		
		Marking initMarking = new Marking();
		initMarking.add(pStart);
		parameter.setInitialMarking(initMarking);
		
		Marking finalMarking = new Marking(); //empty marking

		parameter.setFinalMarkings(new Marking[] {finalMarking});
		parameter.setMaxNumOfStates(200000);
		
		// instantiate replayer
		PNLogReplayer replayer = new PNLogReplayer();
		
		// select algorithm without ILP
		PetrinetReplayerMovePreferenceAwareWithILP movePreferenceReplayer = new PetrinetReplayerMovePreferenceAwareWithILP();
		movePreferenceReplayer.setPreferredTransitions(ImmutableSet.of(tB));
		PNRepResult pnRepResult = replayer.replayLog(null, net, log, mapping, movePreferenceReplayer, parameter);
		
		assertTrue(!pnRepResult.isEmpty());
		
		SyncReplayResult syncRepResult = pnRepResult.iterator().next();
		
		assertTrue(syncRepResult.isReliable()); // result should be reliable
		
		Map<String, Double> info = syncRepResult.getInfo();			
		assertTrue(Double.compare(2.000, info.get(PNRepResult.RAWFITNESSCOST)) == 0); // one move log, one move model
		
		System.out.println(syncRepResult.getNodeInstance().toString());
		System.out.println(syncRepResult.getStepTypes().toString());
		
		List<Object> nodeInstance = syncRepResult.getNodeInstance();
		List<StepTypes> stepTypes = syncRepResult.getStepTypes();
		
		assertEquals(stepTypes.get(0), StepTypes.MREAL);
		assertEquals(stepTypes.get(1), StepTypes.LMGOOD);
		assertEquals(stepTypes.get(2), StepTypes.L);

		assertEquals(nodeInstance.get(0), tA);
		assertEquals(nodeInstance.get(1), tB);
		assertEquals(logInfo.getEventClasses().getClassOf(eventA),nodeInstance.get(2));
		
	}

}
