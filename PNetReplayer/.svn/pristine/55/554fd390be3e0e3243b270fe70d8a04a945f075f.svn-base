package test;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import nl.tue.astar.AStarException;
import nl.tue.astar.AStarThread.ASynchronousMoveSorting;
import nl.tue.astar.AStarThread.QueueingModel;
import nl.tue.astar.AStarThread.Type;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.jbpt.petri.Flow;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.io.PNMLSerializer;
import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.ConnectionManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginContextID;
import org.processmining.framework.plugin.PluginDescriptor;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.PluginManager;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.plugin.ProMFuture;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.RecursiveCallException;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.framework.plugin.events.PluginLifeCycleEventListener.List;
import org.processmining.framework.plugin.events.ProgressEventListener.ListenerList;
import org.processmining.framework.plugin.impl.FieldSetException;
import org.processmining.framework.providedobjects.ProvidedObjectManager;
import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.AbstractPetrinetReplayer;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithoutILP;
import org.processmining.plugins.astar.petrinet.impl.AbstractPILPDelegate;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class AlignmentTest {

	public static int iteration = 0;

	static {
		try {
			System.loadLibrary("lpsolve55");
			System.loadLibrary("lpsolve55j");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		test(args);
	}

	public static void test(String[] args) throws Exception {
		//		DummyUIPluginContext context = new DummyUIPluginContext(new DummyGlobalContext(), "label");
		//		AbstractPILPDelegate.setDebugMode(new File("D:\\temp\\alignmentDebugTest\\"));

		AbstractPILPDelegate.setDebugMode(null);

		PetrinetGraph net = null;
		Marking initialMarking = null;
		Marking[] finalMarkings = null; // only one marking is used so far
		XLog log = null;
		Map<Transition, Integer> costMOS = null; // movements on system
		Map<XEventClass, Integer> costMOT = null; // movements on trace
		TransEvClassMapping mapping = null;

		String name = "prDm6";
		net = constructNet("d:/temp/alignment/" + name + ".pnml");
		initialMarking = getInitialMarking(net);
		finalMarkings = getFinalMarkings(net);
		//		log = XParserRegistry.instance().currentDefault().parse(new File("d:/temp/alignment/prAm6.mxml"))
		//				.get(0);
		XMxmlParser parser = new XMxmlParser();
		log = parser.parse(new File("d:/temp/alignment/" + name + ".mxml")).get(0);

		//		log.retainAll(Arrays.asList(new XTrace[] { log.get(201) }));

		//		log.add(XFactoryRegistry.instance().currentDefault().createTrace());

		//			log = XParserRegistry.instance().currentDefault().parse(new File("d:/temp/BPI 730858110.xes.gz")).get(0);
		//			log = XFactoryRegistry.instance().currentDefault().openLog();
		costMOS = constructMOSCostFunction(net);
		XEventClass dummyEvClass = new XEventClass("DUMMY", 99999);
		XEventClassifier eventClassifier = XLogInfoImpl.STANDARD_CLASSIFIER;
		costMOT = constructMOTCostFunction(net, log, eventClassifier, dummyEvClass);
		mapping = constructMapping(net, log, dummyEvClass, eventClassifier);

		int iteration = 0;
		for (ASynchronousMoveSorting sort : new ASynchronousMoveSorting[] { ASynchronousMoveSorting.TOTAL,
				ASynchronousMoveSorting.LOGMOVEFIRST }) {
			System.out.println("start: " + iteration + " sorting: " + sort);
			long start = System.currentTimeMillis();
			int cost1 = AlignmentTest.computeCost(costMOS, costMOT, initialMarking, finalMarkings,
					new TestPluginContext(), net, log, mapping, true, sort);
			long mid = System.currentTimeMillis();
			System.out.println("   With ILP cost: " + cost1 + "  t: " + (mid - start));

			//				long mid2 = System.currentTimeMillis();
			//				int cost2 = AlignmentTest.computeCost(costMOS, costMOT, initialMarking, finalMarkings,  new TestPluginContext(), net, log,
			//						mapping, false, sort);
			//				long end = System.currentTimeMillis();
			//
			//				System.out.println("   No ILP   cost: " + cost2 + "  t: " + (end - mid2));
			//				if (cost1 != cost2) {
			//					System.err.println("ERROR");
			//				}
			System.gc();
			System.out.flush();
			iteration++;
		}
		return;
	}

	public static int computeCost(Map<Transition, Integer> costMOS, Map<XEventClass, Integer> costMOT,
			Marking initialMarking, Marking[] finalMarkings, PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping, boolean useILP, ASynchronousMoveSorting sorting) {
		AbstractPetrinetReplayer<?, ?> replayEngine;
		if (useILP) {
			replayEngine = new PetrinetReplayerWithILP();
		} else {
			replayEngine = new PetrinetReplayerWithoutILP();
		}

		IPNReplayParameter parameters = new CostBasedCompleteParam(costMOT, costMOS);
		parameters.setInitialMarking(initialMarking);
		parameters.setFinalMarkings(finalMarkings[0]);
		parameters.setAsynchronousMoveSort(sorting);
		parameters.setGUIMode(false);
		parameters.setCreateConn(false);
		parameters.setNumThreads(8);
		parameters.setType(Type.PLAIN);
		parameters.setQueueingModel(QueueingModel.DEPTHFIRSTWITHCERTAINTYPRIORITY);

		int cost = 0;
		try {
			PNRepResult result = replayEngine.replayLog(context, net, log, mapping, parameters);

			long q = 0;
			long g = 0;
			for (SyncReplayResult res : result) {
				if (res.isReliable()) {
					q += res.getInfo().get(PNRepResult.QUEUEDSTATE);
					g += res.getInfo().get(PNRepResult.NUMSTATEGENERATED);
					cost += ((int) res.getInfo().get(PNRepResult.RAWFITNESSCOST).doubleValue())
							* res.getTraceIndex().size();
				} else {
					System.err.println("Error in traces " + res.getTraceIndex());
				}
			}
			System.out.println("Queued states: " + q);
			System.out.println("Generated states: " + g);

		} catch (AStarException e) {
			e.printStackTrace();
		}

		return cost;
	}

	private static PetrinetGraph constructNet(String netFile) {
		PNMLSerializer PNML = new PNMLSerializer();
		NetSystem sys = PNML.parse(netFile);

		//System.err.println(sys.getMarkedPlaces());

		//		int pi, ti;
		//		pi = ti = 1;
		//		for (org.jbpt.petri.Place p : sys.getPlaces())
		//			p.setName("p" + pi++);
		//		for (org.jbpt.petri.Transition t : sys.getTransitions())
		//				t.setName("t" + ti++);

		PetrinetGraph net = PetrinetFactory.newPetrinet(netFile);

		// places
		Map<org.jbpt.petri.Place, Place> p2p = new HashMap<org.jbpt.petri.Place, Place>();
		for (org.jbpt.petri.Place p : sys.getPlaces()) {
			Place pp = net.addPlace(p.toString());
			p2p.put(p, pp);
		}

		// transitions
		int l = 0;
		Map<org.jbpt.petri.Transition, Transition> t2t = new HashMap<org.jbpt.petri.Transition, Transition>();
		for (org.jbpt.petri.Transition t : sys.getTransitions()) {
			Transition tt = net.addTransition(t.getLabel());
			tt.setInvisible(t.isSilent());
			t2t.put(t, tt);
		}

		// flow
		for (Flow f : sys.getFlow()) {
			if (f.getSource() instanceof org.jbpt.petri.Place) {
				net.addArc(p2p.get(f.getSource()), t2t.get(f.getTarget()));
			} else {
				net.addArc(t2t.get(f.getSource()), p2p.get(f.getTarget()));
			}
		}

		// add unique start node
		if (sys.getSourceNodes().isEmpty()) {
			Place i = net.addPlace("START_P");
			Transition t = net.addTransition("");
			t.setInvisible(true);
			net.addArc(i, t);

			for (org.jbpt.petri.Place p : sys.getMarkedPlaces()) {
				net.addArc(t, p2p.get(p));
			}

		}

		return net;
	}

	private static Marking[] getFinalMarkings(PetrinetGraph net) {
		Marking finalMarking = new Marking();

		for (Place p : net.getPlaces()) {
			if (net.getOutEdges(p).isEmpty())
				finalMarking.add(p);
		}

		Marking[] finalMarkings = new Marking[1];
		finalMarkings[0] = finalMarking;

		return finalMarkings;
	}

	private static Marking getInitialMarking(PetrinetGraph net) {
		Marking initMarking = new Marking();

		for (Place p : net.getPlaces()) {
			if (net.getInEdges(p).isEmpty())
				initMarking.add(p);
		}

		return initMarking;
	}

	private static Map<Transition, Integer> constructMOSCostFunction(PetrinetGraph net) {
		Map<Transition, Integer> costMOS = new HashMap<Transition, Integer>();

		for (Transition t : net.getTransitions())
			if (t.isInvisible() || t.getLabel().equals(""))
				costMOS.put(t, 0);
			else
				costMOS.put(t, 1);

		return costMOS;
	}

	private static Map<XEventClass, Integer> constructMOTCostFunction(PetrinetGraph net, XLog log,
			XEventClassifier eventClassifier, XEventClass dummyEvClass) {
		Map<XEventClass, Integer> costMOT = new HashMap<XEventClass, Integer>();
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, eventClassifier);

		for (XEventClass evClass : summary.getEventClasses().getClasses()) {
			costMOT.put(evClass, 1);
		}

		//		costMOT.put(dummyEvClass, 1);

		return costMOT;
	}

	private static TransEvClassMapping constructMapping(PetrinetGraph net, XLog log, XEventClass dummyEvClass,
			XEventClassifier eventClassifier) {
		TransEvClassMapping mapping = new TransEvClassMapping(eventClassifier, dummyEvClass);

		XLogInfo summary = XLogInfoFactory.createLogInfo(log, eventClassifier);

		for (Transition t : net.getTransitions()) {
			boolean mapped = false;

			for (XEventClass evClass : summary.getEventClasses().getClasses()) {
				String id = evClass.getId();

				if (t.getLabel().equals(id)) {
					mapping.put(t, evClass);
					mapped = true;
					break;
				}
			}

			//			if (!mapped && !t.isInvisible()) {
			//				mapping.put(t, dummyEvClass);
			//			}

		}

		return mapping;
	}

	private static class TestPluginContext implements PluginContext {

		private final Progress progress = new Progress() {

			public void setMinimum(int value) {
			}

			public void setMaximum(int value) {
			}

			public void setValue(int value) {
			}

			public void setCaption(String message) {
			}

			public String getCaption() {
				throw new NotImplementedException();
			}

			public int getValue() {
				throw new NotImplementedException();
			}

			public void inc() {
				System.out.print(".");
			}

			public void setIndeterminate(boolean makeIndeterminate) {
			}

			public boolean isIndeterminate() {
				throw new NotImplementedException();
			}

			public int getMinimum() {
				throw new NotImplementedException();
			}

			public int getMaximum() {
				throw new NotImplementedException();
			}

			public boolean isCancelled() {
				return false;
			}

			public void cancel() {
			}

		};

		public PluginManager getPluginManager() {
			throw new NotImplementedException();

		}

		public ProvidedObjectManager getProvidedObjectManager() {
			throw new NotImplementedException();

		}

		public ConnectionManager getConnectionManager() {
			throw new NotImplementedException();

		}

		public PluginContextID createNewPluginContextID() {
			throw new NotImplementedException();

		}

		public void invokePlugin(PluginDescriptor plugin, int index, Object... objects) {
			throw new NotImplementedException();

		}

		public void invokeBinding(PluginParameterBinding binding, Object... objects) {
			throw new NotImplementedException();

		}

		public Class<? extends PluginContext> getPluginContextType() {
			throw new NotImplementedException();

		}

		public <T, C extends Connection> Collection<T> tryToFindOrConstructAllObjects(Class<T> type,
				Class<C> connectionType, String role, Object... input) throws ConnectionCannotBeObtained {
			throw new NotImplementedException();

		}

		public <T, C extends Connection> T tryToFindOrConstructFirstObject(Class<T> type, Class<C> connectionType,
				String role, Object... input) throws ConnectionCannotBeObtained {
			throw new NotImplementedException();

		}

		public <T, C extends Connection> T tryToFindOrConstructFirstNamedObject(Class<T> type, String name,
				Class<C> connectionType, String role, Object... input) throws ConnectionCannotBeObtained {
			throw new NotImplementedException();

		}

		public PluginContext createChildContext(String label) {
			throw new NotImplementedException();

		}

		public Progress getProgress() {
			return progress;
		}

		public ListenerList getProgressEventListeners() {
			throw new NotImplementedException();

		}

		public List getPluginLifeCycleEventListeners() {
			throw new NotImplementedException();

		}

		public PluginContextID getID() {
			throw new NotImplementedException();

		}

		public String getLabel() {
			throw new NotImplementedException();

		}

		public Pair<PluginDescriptor, Integer> getPluginDescriptor() {
			throw new NotImplementedException();

		}

		public PluginContext getParentContext() {
			throw new NotImplementedException();

		}

		public java.util.List<PluginContext> getChildContexts() {
			throw new NotImplementedException();
		}

		public PluginExecutionResult getResult() {
			throw new NotImplementedException();
		}

		public ProMFuture<?> getFutureResult(int i) {
			throw new NotImplementedException();
		}

		public Executor getExecutor() {
			throw new NotImplementedException();
		}

		public boolean isDistantChildOf(PluginContext context) {
			throw new NotImplementedException();
		}

		public void setFuture(PluginExecutionResult resultToBe) {
			throw new NotImplementedException();

		}

		public void setPluginDescriptor(PluginDescriptor descriptor, int methodIndex) throws FieldSetException,
				RecursiveCallException {
			throw new NotImplementedException();

		}

		public boolean hasPluginDescriptorInPath(PluginDescriptor descriptor, int methodIndex) {
			throw new NotImplementedException();
		}

		public void log(String message, MessageLevel level) {
			System.out.println(message);
		}

		public void log(String message) {
			System.out.println(message);
		}

		public void log(Throwable exception) {
			exception.printStackTrace();
		}

		public org.processmining.framework.plugin.events.Logger.ListenerList getLoggingListeners() {
			throw new NotImplementedException();
		}

		public PluginContext getRootContext() {
			throw new NotImplementedException();
		}

		public boolean deleteChild(PluginContext child) {
			throw new NotImplementedException();
		}

		public <T extends Connection> T addConnection(T c) {
			throw new NotImplementedException();
		}

		public void clear() {
			throw new NotImplementedException();

		}

	}

}
