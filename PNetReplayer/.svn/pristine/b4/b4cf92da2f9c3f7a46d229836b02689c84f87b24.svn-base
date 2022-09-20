/**
 * 
 */
package org.processmining.tests.newpackage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
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
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerILPRestrictedMoveModel;
import org.processmining.plugins.astar.petrinet.manifestreplay.CostBasedCompleteManifestParam;
import org.processmining.plugins.astar.petrinet.manifestreplay.PNManifestFlattener;
import org.processmining.plugins.petrinet.manifestreplayer.EvClassPattern;
import org.processmining.plugins.petrinet.manifestreplayer.PNManifestReplayerParameter;
import org.processmining.plugins.petrinet.manifestreplayer.TransClass2PatternMap;
import org.processmining.plugins.petrinet.manifestreplayer.algorithms.PNManifestReplayerILPAlgorithm;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.DefTransClassifier;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.ITransClassifier;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClass;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClasses;
import org.processmining.plugins.petrinet.manifestreplayresult.Manifest;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * @author aadrians Feb 19, 2012
 * 
 */
public class PNManifestReplayerTestWithoutGUI {
//	@Test
	public void transActMappingTest() throws Throwable {
		/**
		 * SEQUENTIAL NET : A --o--> B --o--> C
		 */
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

		// create log of 6 events: [A-start][A-complete][B-complete][C-complete][C-start][C-complete]
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XConceptExtension conceptExtension = XConceptExtension.instance();
		XLog log = factory.createLog();

		// TRACE 1 : Astart - BComplete - CComplete - CStart - CComplete
		XTrace trace1 = factory.createTrace();
		XAttribute traceNameAttr = factory.createAttributeLiteral("concept:name", "trace 1", conceptExtension);
		trace1.getAttributes().put("concept:name", traceNameAttr);

		// create events
		XAttribute nameA = factory.createAttributeLiteral("concept:name", "A", conceptExtension);
		XAttribute nameB = factory.createAttributeLiteral("concept:name", "B", conceptExtension);
		XAttribute nameC = factory.createAttributeLiteral("concept:name", "C", conceptExtension);

		XAttribute transitionComplete = factory.createAttributeLiteral("lifecycle:transition", "complete",
				XConceptExtension.instance());
		XAttribute transitionStart = factory.createAttributeLiteral("lifecycle:transition", "start",
				XConceptExtension.instance());

		XEvent eventAStart = factory.createEvent();
		eventAStart.getAttributes().put("concept:name", nameA);
		eventAStart.getAttributes().put("lifecycle:transition", transitionStart);
		trace1.add(eventAStart);

		XEvent eventAComplete = factory.createEvent();
		eventAComplete.getAttributes().put("concept:name", nameA);
		eventAComplete.getAttributes().put("lifecycle:transition", transitionComplete);
		//		trace1.add(eventAComplete);

		XEvent eventBComplete = factory.createEvent();
		eventBComplete.getAttributes().put("concept:name", nameB);
		eventBComplete.getAttributes().put("lifecycle:transition", transitionComplete);
		trace1.add(eventBComplete);

		XEvent eventCComplete = factory.createEvent();
		eventCComplete.getAttributes().put("concept:name", nameC);
		eventCComplete.getAttributes().put("lifecycle:transition", transitionComplete);
		trace1.add(eventCComplete);

		XEvent eventCStart = factory.createEvent();
		eventCStart.getAttributes().put("concept:name", nameC);
		eventCStart.getAttributes().put("lifecycle:transition", transitionStart);
		trace1.add(eventCStart);

		XEvent eventCComplete2 = factory.createEvent();
		eventCComplete2.getAttributes().put("concept:name", nameC);
		eventCComplete2.getAttributes().put("lifecycle:transition", transitionComplete);
		trace1.add(eventCComplete2);

		log.add(trace1);

		/**
		 * add trace 2: Acomplete
		 */
		XTrace trace2 = factory.createTrace();
		XAttribute traceNameAttr2 = factory.createAttributeLiteral("concept:name", "trace 2", conceptExtension);
		trace2.getAttributes().put("concept:name", traceNameAttr2);
		trace2.add(eventAComplete);
		//		trace2.add(eventCComplete2);
		log.add(trace2);

		// create mapping
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, XLogInfoImpl.STANDARD_CLASSIFIER);
		XEventClasses eventClasses = logInfo.getEventClasses();

		// create A start-complete
		EvClassPattern ALC = new EvClassPattern();
		ALC.add(eventClasses.getClassOf(eventAStart));
		ALC.add(eventClasses.getClassOf(eventAComplete));
		Set<EvClassPattern> setSeqEvClassA = new HashSet<EvClassPattern>(1);
		setSeqEvClassA.add(ALC);

		// create B complete only
		EvClassPattern BLC = new EvClassPattern();
		BLC.add(eventClasses.getClassOf(eventBComplete));
		Set<EvClassPattern> setSeqEvClassB = new HashSet<EvClassPattern>(1);
		setSeqEvClassB.add(BLC);

		// create C start-complete and C complete only
		EvClassPattern CLC1 = new EvClassPattern();
		EvClassPattern CLC2 = new EvClassPattern();

		CLC1.add(eventClasses.getClassOf(eventCStart));
		CLC1.add(eventClasses.getClassOf(eventCComplete));
		CLC2.add(eventClasses.getClassOf(eventCComplete));
		Set<EvClassPattern> setSeqEvClassC = new HashSet<EvClassPattern>(2);
		setSeqEvClassC.add(CLC1);
		setSeqEvClassC.add(CLC2);

		Map<TransClass, Set<EvClassPattern>> mapTrans2List = new HashMap<TransClass, Set<EvClassPattern>>(4);

		ITransClassifier transClassifier = new DefTransClassifier();
		TransClasses transClasses = new TransClasses(net, transClassifier);
		mapTrans2List.put(transClasses.getClassOf(tA), setSeqEvClassA);
		mapTrans2List.put(transClasses.getClassOf(tB), setSeqEvClassB);
		mapTrans2List.put(transClasses.getClassOf(tC), setSeqEvClassC);

		TransClass2PatternMap mapping = new TransClass2PatternMap(log, net, XLogInfoImpl.STANDARD_CLASSIFIER,
				transClasses, mapTrans2List);

		// create parameter
		Map<TransClass, Integer> mapTrans2Cost = new HashMap<TransClass, Integer>();
		mapTrans2Cost.put(transClasses.getClassOf(tA), 2);
		mapTrans2Cost.put(transClasses.getClassOf(tC), 2);

		Map<XEventClass, Integer> mapEvClass2Cost = new HashMap<XEventClass, Integer>(5);
		for (XEventClass ec : eventClasses.getClasses()) {
			//			if (ec.toString().startsWith("A")) {
			//				mapEvClass2Cost.put(ec, 5);
			//			} else {
			mapEvClass2Cost.put(ec, 5);
			//			}
		}

		Marking initMarking = new Marking();
		initMarking.add(pStart);

		Marking finalMarking = new Marking();
		finalMarking.add(pEnd);
		Marking[] finalMarkings = new Marking[1];
		finalMarkings[0] = finalMarking;

		PNManifestReplayerParameter parameter = new PNManifestReplayerParameter(mapTrans2Cost, mapEvClass2Cost, mapping,
				1000, initMarking, finalMarkings);

		for (List<XEventClass> pattern : parameter.getAllPatternsFor(tA)) {
			Iterator<XEventClass> it = pattern.iterator();
			assert (it.next().toString().equals("A+start"));
			assert (it.next().toString().equals("A+complete"));
			assert (!it.hasNext());
		}
		;
		for (List<XEventClass> pattern : parameter.getAllPatternsFor(tB)) {
			Iterator<XEventClass> it = pattern.iterator();
			assert (it.next().toString().equals("B+complete"));
			assert (!it.hasNext());
		}
		;
		Set<EvClassPattern> patterns = parameter.getAllPatternsFor(tC);
		assert (patterns.size() == 2);
		for (EvClassPattern pattern : patterns) {
			if (pattern.size() == 2) {
				Iterator<XEventClass> it = pattern.iterator();
				assert (it.next().toString().equals("C+start"));
				assert (it.next().toString().equals("C+complete"));
				assert (!it.hasNext());
			} else {
				Iterator<XEventClass> it = pattern.iterator();
				assert (it.next().toString().equals("C+complete"));
				assert (!it.hasNext());
			}
		}
		;

		// test flattened petri net
		PNManifestFlattener flattener = new PNManifestFlattener(net, parameter);

		PetrinetGraph flattenedNet = flattener.getNet();
		Collection<Transition> transCol = flattenedNet.getTransitions();
		for (Transition t : transCol) {
			assert (flattener.getMapTrans2Cost().get(t) != null);

			//			System.out.print("Transition " + t.getLabel() + ":");
			//			System.out.println(flattener.getMapTrans2Cost().get(t));
			//			System.out.print("pre : ");
			//			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : flattenedNet.getInEdges(t)){
			//				System.out.print(edge.getSource().getLabel());
			//				System.out.print("[");
			//				System.out.print(flattenedNet.getArc(edge.getSource(), edge.getTarget()).getWeight());
			//				System.out.print("],");
			//			}
			//			System.out.println();
			//			System.out.print("out : ");
			//			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : flattenedNet.getOutEdges(t)){
			//				System.out.print(edge.getTarget().getLabel());
			//				System.out.print("[");
			//				System.out.print(flattenedNet.getArc(edge.getSource(), edge.getTarget()).getWeight());
			//				System.out.print("],");
			//			}
			//			System.out.println();
			//			System.out.println();
		}
		assert (transCol.size() == 8);
		assert (flattener.getMapTrans2Cost().keySet().size() == 8);
		//assert (flattener.getMoveModelTrans().size() == 2);

		// check all move model transitions 
		//Set<Transition> moveModelTrans = flattener.getMoveModelTrans();
		Set<Transition> origMoveModelTrans = new HashSet<Transition>(3);
		origMoveModelTrans.add(tA);
		origMoveModelTrans.add(tC);
//		for (Transition flatT : moveModelTrans) {
//			assert (origMoveModelTrans.contains(flattener.getOrigTransFor(flatT)));
//		}

		//		Map<Transition, Transition> mapTrans2Orig = map;
		//
		//		for (Transition t : mapTrans2Orig.values()) {
		//			assert (net.getTransitions().contains(t));
		//		}

		//		PNManifestReplayerILPAlgorithm replayer = new PNManifestReplayerILPAlgorithm();
		//		replayer.replayLog(null, net, log, parameter);

		// detailed checking
		// create parameter
		IPNReplayParameter parametermanifest = new CostBasedCompleteManifestParam(flattener.getMapEvClass2Cost(),
				flattener.getMapTrans2Cost(), flattener.getInitMarking(), flattener.getFinalMarkings(), 200000,
				flattener.getFragmentTrans());

		IPNReplayAlgorithm alg = new PetrinetReplayerILPRestrictedMoveModel();

		// call petri net replayer with ILP
		PNLogReplayer lowlevelreplayer = new PNLogReplayer();

		// select algorithm with ILP
		PNRepResult pnRepResult = lowlevelreplayer.replayLog(null, flattener.getNet(), log, flattener.getMap(), alg,
				parametermanifest);
		assert (pnRepResult != null);

		Iterator<SyncReplayResult> it = pnRepResult.iterator();
		SyncReplayResult syncRepRes1 = it.next();

		List<Object> nodeInstance1 = syncRepRes1.getNodeInstance();
		List<StepTypes> stepTypes1 = syncRepRes1.getStepTypes();

		assert (stepTypes1.iterator().next().equals(StepTypes.MREAL));
		assert (syncRepRes1.getInfo().get(PNRepResult.RAWFITNESSCOST).intValue() == (12 * flattener.getCostFactor()));

		SyncReplayResult syncRepRes2 = it.next();
		assert (syncRepRes2.getInfo().get(PNRepResult.RAWFITNESSCOST).intValue() == (9 * flattener.getCostFactor()));
	}

	@Test
	public void transManifestConstructTest() throws Throwable {
		/**
		 * SEQUENTIAL NET : A --o--> B --o--> C
		 */
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

		// create log of 6 events: [A-start][A-complete][B-complete][C-complete][C-start][C-complete]
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XConceptExtension conceptExtension = XConceptExtension.instance();
		XLog log = factory.createLog();

		// TRACE 1 : Astart - BComplete - CComplete - CStart - CComplete
		XTrace trace1 = factory.createTrace();
		XAttribute traceNameAttr = factory.createAttributeLiteral("concept:name", "trace 1", conceptExtension);
		trace1.getAttributes().put("concept:name", traceNameAttr);

		// create events
		XAttribute nameA = factory.createAttributeLiteral("concept:name", "A", conceptExtension);
		XAttribute nameB = factory.createAttributeLiteral("concept:name", "B", conceptExtension);
		XAttribute nameC = factory.createAttributeLiteral("concept:name", "C", conceptExtension);

		XAttribute transitionComplete = factory.createAttributeLiteral("lifecycle:transition", "complete",
				XConceptExtension.instance());
		XAttribute transitionStart = factory.createAttributeLiteral("lifecycle:transition", "start",
				XConceptExtension.instance());

		XEvent eventAStart = factory.createEvent();
		eventAStart.getAttributes().put("concept:name", nameA);
		eventAStart.getAttributes().put("lifecycle:transition", transitionStart);
		trace1.add(eventAStart);

		XEvent eventAComplete = factory.createEvent();
		eventAComplete.getAttributes().put("concept:name", nameA);
		eventAComplete.getAttributes().put("lifecycle:transition", transitionComplete);
		//		trace1.add(eventAComplete);

		XEvent eventBComplete = factory.createEvent();
		eventBComplete.getAttributes().put("concept:name", nameB);
		eventBComplete.getAttributes().put("lifecycle:transition", transitionComplete);
		trace1.add(eventBComplete);

		XEvent eventCComplete = factory.createEvent();
		eventCComplete.getAttributes().put("concept:name", nameC);
		eventCComplete.getAttributes().put("lifecycle:transition", transitionComplete);
		trace1.add(eventCComplete);

		XEvent eventCStart = factory.createEvent();
		eventCStart.getAttributes().put("concept:name", nameC);
		eventCStart.getAttributes().put("lifecycle:transition", transitionStart);
		trace1.add(eventCStart);

		XEvent eventCComplete2 = factory.createEvent();
		eventCComplete2.getAttributes().put("concept:name", nameC);
		eventCComplete2.getAttributes().put("lifecycle:transition", transitionComplete);
		trace1.add(eventCComplete2);

		log.add(trace1);

		/**
		 * add trace 2: Acomplete
		 */
		XTrace trace2 = factory.createTrace();
		XAttribute traceNameAttr2 = factory.createAttributeLiteral("concept:name", "trace 2", conceptExtension);
		trace2.getAttributes().put("concept:name", traceNameAttr2);
		trace2.add(eventAComplete);
		//		trace2.add(eventCComplete2);
		log.add(trace2);

		// create mapping
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, XLogInfoImpl.STANDARD_CLASSIFIER);
		XEventClasses eventClasses = logInfo.getEventClasses();

		// create A start-complete
		EvClassPattern ALC = new EvClassPattern();
		ALC.add(eventClasses.getClassOf(eventAStart));
		ALC.add(eventClasses.getClassOf(eventAComplete));
		Set<EvClassPattern> setSeqEvClassA = new HashSet<EvClassPattern>(1);
		setSeqEvClassA.add(ALC);

		// create B complete only
		EvClassPattern BLC = new EvClassPattern();
		BLC.add(eventClasses.getClassOf(eventBComplete));
		Set<EvClassPattern> setSeqEvClassB = new HashSet<EvClassPattern>(1);
		setSeqEvClassB.add(BLC);

		// create C start-complete and C complete only
		EvClassPattern CLC1 = new EvClassPattern();
		EvClassPattern CLC2 = new EvClassPattern();

		CLC1.add(eventClasses.getClassOf(eventCStart));
		CLC1.add(eventClasses.getClassOf(eventCComplete));
		CLC2.add(eventClasses.getClassOf(eventCComplete));
		Set<EvClassPattern> setSeqEvClassC = new HashSet<EvClassPattern>(2);
		setSeqEvClassC.add(CLC1);
		setSeqEvClassC.add(CLC2);

		Map<TransClass, Set<EvClassPattern>> mapTrans2List = new HashMap<TransClass, Set<EvClassPattern>>(4);

		ITransClassifier transClassifier = new DefTransClassifier();
		TransClasses transClasses = new TransClasses(net, transClassifier);
		mapTrans2List.put(transClasses.getClassOf(tA), setSeqEvClassA);
		mapTrans2List.put(transClasses.getClassOf(tB), setSeqEvClassB);
		mapTrans2List.put(transClasses.getClassOf(tC), setSeqEvClassC);

		TransClass2PatternMap mapping = new TransClass2PatternMap(log, net, XLogInfoImpl.STANDARD_CLASSIFIER,
				transClasses, mapTrans2List);

		// create parameter
		Map<TransClass, Integer> mapTrans2Cost = new HashMap<TransClass, Integer>();
		mapTrans2Cost.put(transClasses.getClassOf(tA), 2);
		mapTrans2Cost.put(transClasses.getClassOf(tC), 2);

		Map<XEventClass, Integer> mapEvClass2Cost = new HashMap<XEventClass, Integer>(5);
		for (XEventClass ec : eventClasses.getClasses()) {
			mapEvClass2Cost.put(ec, 5);
		}

		Marking initMarking = new Marking();
		initMarking.add(pStart);

		Marking finalMarking = new Marking();
		finalMarking.add(pEnd);
		Marking[] finalMarkings = new Marking[1];
		finalMarkings[0] = finalMarking;

		PNManifestReplayerParameter parameter = new PNManifestReplayerParameter(mapTrans2Cost, mapEvClass2Cost, mapping,
				1000, initMarking, finalMarkings);
		
		PNManifestReplayerILPAlgorithm alg = new PNManifestReplayerILPAlgorithm();
		Manifest manifest = alg.replayLog(null, net, log, parameter);
		System.out.println("Case 0");
		manifest.printManifestForCase(0);
		System.out.println("Case 1");
		manifest.printManifestForCase(1);
	}

	public void printNet(PetrinetGraph net) {
		for (Transition t : net.getTransitions()) {

			System.out.print("Transition " + t.getLabel() + ":");
			System.out.print("pre : ");
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getInEdges(t)) {
				System.out.print(edge.getSource().getLabel());
				System.out.print(",");
			}
			System.out.println();
			System.out.print("out : ");
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getOutEdges(t)) {
				System.out.print(edge.getTarget().getLabel());
				System.out.print(",");
			}
			System.out.println();
			System.out.println();
		}
	}
}
