package org.processmining.plugins.annotatedgraph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.collection.AlphanumComparator;
import org.processmining.helpers.AttributeHelper;
import org.processmining.helpers.Triple;
import org.processmining.models.EventLogArray;
import org.processmining.models.annotatedgraph.AnnotatedEdge;
import org.processmining.models.annotatedgraph.AnnotatedEdgeIMP;
import org.processmining.models.annotatedgraph.AnnotatedGraph;
import org.processmining.models.annotatedgraph.AnnotatedVertex;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.replay.ReplayProblem;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import com.fluxicon.slickerbox.factory.SlickerFactory;

@Plugin(name = "Compare process models", parameterLabels = { "EventLogArray",
		"DiscoProcessMap" }, returnLabels = { "AnnotatedGraph" }, returnTypes = { AnnotatedGraph.class })
public class AnnotateGraphMinerConverterAnnotaterMerger {
	protected String minerName;
	protected boolean doAnnotation = true;	
	protected int numberTraces = 0;

	/* Alignment */
	HashMap<PetrinetNode, HashMap<Integer, HashMap<Integer, HashMap<String, String>>>> logData;
	ArrayList<String> forbiddenAttributes = new ArrayList<String>(Arrays.asList(new String[]{ "lifecycle:transition", "time:timestamp", "sourcecompletename", "sourcecompletestamp", "targetstartstamp", "targetcompletestamp" }));

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	/* Dialog that provides the choice of a miner; in case of XLog variant */
	public class CustomDialog extends JPanel {
		private int DIMHEIGHT = 50;
		private int DIMWIDTH = 400;

		private static final long serialVersionUID = -3587595452197538653L;

		@SuppressWarnings("rawtypes")
		public CustomDialog(UIPluginContext context) {
			this.setLayout(new GridLayout(0, 2));

			SlickerFactory slickerFac = SlickerFactory.instance();

			JLabel minerLabel = new JLabel("Choose a miner: ");
			minerLabel.setOpaque(false);
			minerLabel.setForeground(Color.BLACK);
			minerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			minerLabel.setPreferredSize(new Dimension(DIMWIDTH, DIMHEIGHT));
			minerLabel.setMaximumSize(new Dimension(DIMWIDTH, DIMHEIGHT));
			JPanel minerLabelPanel = new JPanel();
			minerLabelPanel.setLayout(new FlowLayout());
			minerLabelPanel.add(minerLabel);
			this.add(minerLabelPanel);

			JComboBox minerBox = slickerFac.createComboBox(new String[] {
					"Mine a directed graph using causal relations", "Alpha Miner", "ILP Miner",
					"Mine Petri net with Inductive Miner" });
			minerName = "Mine a directed graph using causal relations";
			minerBox.setSelectedIndex(0);
			minerBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					JComboBox cb = (JComboBox) event.getSource();
					minerName = (String) cb.getSelectedItem();
				}
			});
			minerBox.setPreferredSize(new Dimension(DIMWIDTH, DIMHEIGHT));
			minerBox.setMaximumSize(new Dimension(DIMWIDTH, DIMHEIGHT));
			JPanel minerBoxPanel = new JPanel();
			minerBoxPanel.setLayout(new FlowLayout());
			minerBoxPanel.add(minerBox);
			this.add(minerBoxPanel);
		}
	}

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	/*           */
	/* ALIGNMENT */
	/*           */

	private void alignLogToModel(PluginContext context, XLog log, Petrinet net, PNRepResult result) {
		//Loop over alignments to compute moves per event
		for (SyncReplayResult res : result) {
			HashMap<Integer, ArrayList<Triple<String, String, String>>> tracesTimeData = new HashMap<Integer, ArrayList<Triple<String, String, String>>>();
			HashMap<Integer, ArrayList<HashMap<String, HashMap<String, String>>>> tracesLogData = new HashMap<Integer, ArrayList<HashMap<String, HashMap<String, String>>>>();
			HashMap<Integer, HashMap<PetrinetNode, HashMap<String, String>>> traceNodeStamps = new HashMap<Integer, HashMap<PetrinetNode, HashMap<String, String>>>();
			HashMap<Integer, Integer> traceCounter = new HashMap<Integer, Integer>();
			SortedSet<String> caseIDSets = new TreeSet<String>(new AlphanumComparator());
			XConceptExtension ce = XConceptExtension.instance();
			PetrinetNode currentNode = null;

			//count the number of traces in the same case
			for (int index : res.getTraceIndex()) {
				ArrayList<Triple<String, String, String>> eventsTimeData = new ArrayList<Triple<String, String, String>>();
				ArrayList<HashMap<String, HashMap<String, String>>> eventsLogData = new ArrayList<HashMap<String, HashMap<String, String>>>();
				for (XEvent event : log.get(index)) {
					XAttributeMap eventAttributes = event.getAttributes();
					String eventName = ce.extractName(event);
					if (eventAttributes.containsKey("time:timestamp")) {
						String lifecycle = "complete";
						if (eventAttributes.containsKey("lifecycle:transition")) {
							lifecycle = eventAttributes.get("lifecycle:transition").toString();
							if (lifecycle.equalsIgnoreCase("start")) {
								lifecycle = "start";
							}
							if (lifecycle.equalsIgnoreCase("complete")) {
								lifecycle = "complete";
							}
						}
						String timestamp = eventAttributes.get("time:timestamp").toString();
						if (lifecycle.equals("start") || lifecycle.equals("complete")) {
							Triple<String, String, String> eventTime = Triple.of(eventName, lifecycle,
									timestamp);
							eventsTimeData.add(eventTime);
						} else {
							Triple<String, String, String> eventTime = Triple.of(eventName, "", "");
							eventsTimeData.add(eventTime);
						}
					} else {
						Triple<String, String, String> eventTime = Triple.of(eventName, "", "");
						eventsTimeData.add(eventTime);
					}
					HashMap<String, HashMap<String, String>> eventData = new HashMap<String, HashMap<String, String>>();
					eventData.put(eventName, new HashMap<String, String>());
					for (String attributeName : eventAttributes.keySet()){
						if (!(attributeName.equals("lifecycle:transition") || attributeName.equals("time:timestamp"))){
							eventData.get(eventName).put(attributeName, eventAttributes.get(attributeName).toString());
						}
					}
					eventsLogData.add(eventData);
				}
				tracesTimeData.put(index, eventsTimeData);
				tracesLogData.put(index, eventsLogData);
				traceCounter.put(index, 0);
				String name = ce.extractName(log.get(index));
				if (name == null) {
					name = String.valueOf(index);
				}
				caseIDSets.add(name);
				HashMap<PetrinetNode, HashMap<String, String>> nodeStamps = new HashMap<PetrinetNode, HashMap<String, String>>();
				for (PetrinetNode v : net.getNodes()) {
					HashMap<String, String> stamp = new HashMap<String, String>();
					nodeStamps.put(v, stamp);
				}
				traceNodeStamps.put(index, nodeStamps);
			}

			List<StepTypes> steptypes = new LinkedList<StepTypes>(res.getStepTypes());
			List<Object> events = new LinkedList<Object>(res.getNodeInstance());

			int counter = 0;
			//pointer to event in trace
			int j = 0;
			for (int i = 0; i < steptypes.size(); i++) {
				StepTypes step = steptypes.get(i);
				switch (step) {
					case LMGOOD :
						currentNode = (PetrinetNode) events.get(i);
						for (int t : tracesTimeData.keySet()) {
							if (tracesTimeData.get(t).get(j).getMiddle().equals("start")) {
								if (j > 0) {
									HashMap<String, String> laCoSE = getLaCoSE(step, steptypes, events, tracesTimeData, i, j, t, net);
									if (!laCoSE.equals("")) {
										//store laCoSE
										HashMap<String, String> tempMap = new HashMap<String, String>(traceNodeStamps.get(t).get(
												currentNode));
										for (String key : laCoSE.keySet()){
											tempMap.put(key, laCoSE.get(key));
										}
										traceNodeStamps.get(t).put(currentNode, tempMap);
									}
								}
							}
							if (tracesTimeData.get(t).get(j).getMiddle().equals("complete")) {
								Boolean start = false;
								//store complete
								HashMap<String, String> tempMap = new HashMap<String, String>(traceNodeStamps.get(t).get(currentNode));
								tempMap.put("targetcompletestamp", tracesTimeData.get(t).get(j).getRight());
								traceNodeStamps.get(t).put(currentNode, tempMap);
								if (j > 0) {
									for (int k = (j - 1); k >= 0; k--) {
										if (tracesTimeData.get(t).get(k).getLeft().equals(tracesTimeData.get(t).get(j).getLeft())
												&& tracesTimeData.get(t).get(k).getMiddle().equals("start")) {
											start = true;
											Triple<String, String, String> tempTriple = tracesTimeData.get(t).get(k);
											tracesTimeData.get(t).set(k,
													Triple.of(tempTriple.getLeft(), "used", tempTriple.getRight()));
											//store start
											tempMap = new HashMap<String, String>(traceNodeStamps.get(t).get(currentNode));
											tempMap.put("targetstartstamp", tracesTimeData.get(t).get(k).getRight());
											traceNodeStamps.get(t).put(currentNode, tempMap);
											break;
										}
									}
								}
								if (!start) {
									HashMap<String, String> laCoSE = getLaCoSE(step, steptypes, events, tracesTimeData, i, j, t, net);
									if (!laCoSE.equals("")) {
										tempMap = new HashMap<String, String>(traceNodeStamps.get(t).get(currentNode));
										for (String key : laCoSE.keySet()){
											tempMap.put(key, laCoSE.get(key));
										}
										traceNodeStamps.get(t).put(currentNode, tempMap);
									}
								}
								HashMap<Integer, HashMap<Integer, HashMap<String, String>>> traceStamps = logData
										.get(currentNode);
								HashMap<String, String> stamp = new HashMap<String, String>(traceNodeStamps.get(t).get(currentNode));
								if (!traceStamps.containsKey(t)) {
									HashMap<Integer, HashMap<String, String>> stamps = new HashMap<Integer, HashMap<String, String>>();									
									stamps.put(traceCounter.get(t), stamp);
									traceStamps.put(t, stamps);
								} else {
									counter = traceCounter.get(t);
									traceCounter.put(t, ++counter);
									traceStamps.get(t).put(traceCounter.get(t), stamp);
								}
								for (String dataName : tracesLogData.get(t).get(j).get(currentNode.getLabel()).keySet()){
									traceStamps.get(t).get(traceCounter.get(t)).put(dataName, tracesLogData.get(t).get(j).get(currentNode.getLabel()).get(dataName));
								}
								traceStamps.get(t).get(traceCounter.get(t));
								logData.put(currentNode, traceStamps);
							}
						}
						counter = 0;
						j++;
						break;
					case MREAL :
						// Skip
						break;
					case MINVI :
						// Skip
						break;
					case L :
						j++;
						break;
					default :
						break;
				}
			}
		}
		PrintWriter writer;
		try {
			writer = new PrintWriter("C:/Users/Jeroen/Desktop/" + XConceptExtension.instance().extractName(log) + ".txt", "UTF-8");
			for (PetrinetNode n : logData.keySet()) {
				HashMap<Integer, HashMap<Integer, HashMap<String, String>>> map1 = logData.get(n);
				for (int i1 : map1.keySet()) {
					HashMap<Integer, HashMap<String, String>> map2 = map1.get(i1);
					for (int i2 : map2.keySet()) {
						HashMap<String, String> map3 = map2.get(i2);
						for (String key : map3.keySet()){
							writer.println(n.getLabel() + " --- " + i1 + " --- " + i2 + " --- " + key + " --- " + map3.get(key));
						}
					}
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	private HashMap<String, String> getLaCoSE(StepTypes step, List<StepTypes> steptypes, List<Object> events,
			HashMap<Integer, ArrayList<Triple<String, String, String>>> traces, int index, int j, int t, Petrinet net) {
		HashMap<String, String> returnMap = new HashMap<String, String>();
		if (step.equals(StepTypes.LMGOOD)) {
			PetrinetNode node1 = (PetrinetNode) events.get(index);
			Set<Transition> set = getPreSet(net, node1);
			for (int i = (index - 1); i >= 0; i--) {
				if (steptypes.get(i).equals(StepTypes.LMGOOD)) {
					j--;
					PetrinetNode node2 = (PetrinetNode) events.get(i);
					if (traces.get(t).get(j).getMiddle().equals("start")) {
						if (!(set.contains(node2))) {
							continue;
						} else {
							break;
						}
					}
					if (traces.get(t).get(j).getMiddle().equals("complete")) {
						if (!(set.contains(node2))) {
							continue;
						} else {
							returnMap.put("sourcecompletename", node2.getLabel());
							returnMap.put("sourcecompletestamp", traces.get(t).get(j).getRight());
							return returnMap;
						}
					}
				}
				if (steptypes.get(i).equals(StepTypes.L)) {
					j--;
				}
			}
		}
		return returnMap;
	}

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	private Set<Transition> getPreSet(Petrinet net, PetrinetNode node) {
		HashSet<Transition> set = new HashSet<Transition>();
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inEdges = net.getInEdges(node);

		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : inEdges) {
			if (edge.getSource() instanceof Transition) {
				set.add((Transition) edge.getSource());
			} else {
				set.addAll(getPreSet(net, edge.getSource()));
			}
		}

		return set;
	}

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	public void initializeAlignment(Petrinet net) {
		logData = new HashMap<PetrinetNode, HashMap<Integer, HashMap<Integer, HashMap<String, String>>>>();

		for (PetrinetNode n : net.getNodes()) {
			HashMap<Integer, HashMap<Integer, HashMap<String, String>>> stamps = new HashMap<Integer, HashMap<Integer, HashMap<String, String>>>();
			logData.put(n, stamps);
		}
	}

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	/**
	 * Variant that creates a merged annotated graph from an EventLogArray and
	 * process maps as input
	 * 
	 * @author J.A.J. van Mourik
	 */
	@UITopiaVariant(uiLabel = "Compare process models; DiscoProcessMap", affiliation = UITopiaVariant.EHV, author = "J.A.J. van Mourik", email = "j.a.j.v.mourik@student.tue.nl", pack = "JeroenVanMourik")
	@PluginVariant(variantLabel = "Compare process models; DiscoProcessMap", requiredParameterLabels = {
			0, 1 })
	public AnnotatedGraph mergeDiscoProcessMap(PluginContext context, EventLogArray logArray,
			AnnotatedGraph... processMaps) {
		HashMap<XLog, AnnotatedGraph> logProcessMapMap = new HashMap<XLog, AnnotatedGraph>();

		if (!(logArray.getSize() == processMaps.length)) {
			return null;
			// TODO: Return message
		}

		boolean matchFound;

		for (AnnotatedGraph processMap : processMaps) {
			matchFound = false;
			for(int i = 0; i < logArray.getSize(); i++){
				XLog inputLog = logArray.getLog(i);
				// File matching, issue?!?
				String logLabel = XConceptExtension.instance().extractName(inputLog);
				logLabel.replace("Anonymous log imported from ", "");
				if (logLabel.equals(processMap.getLabel())) {
					logProcessMapMap.put(inputLog, processMap);
					matchFound = true;
					break;
				}
			}
			if (!(matchFound)) {
				return null;
				// TODO: Return message
			}
		}

		// At this point, all process maps exist in a (log,processmap) pair in logProcessMapMap
		AnnotatedGraph[] graphsToMerge = new AnnotatedGraph[processMaps.length];
		AnnotatedGraph mergedAnnotatedGraph = null;
		int modelCounter = 0;
		String mergedLabel = "";

		for (XLog log : logProcessMapMap.keySet()) {
			mergedLabel += " " + XConceptExtension.instance().extractName(log) + ",";
			AnnotatedGraph processmap = logProcessMapMap.get(log);
			graphsToMerge[modelCounter] = processmap;
			double logsize = log.size();

			ArrayList<Double> tempList;

			for (AnnotatedVertex v : processmap.getVertices()) {
				double casefreq = (Double) v.getAttributes().get("frequency_case").get(processmap.getLabel()).get(0);

				tempList = new ArrayList<Double>();
				tempList.add(casefreq / logsize);
				v.addAttribute("frequency_case_rel", processmap.getLabel(), tempList);
			}

			for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : processmap.getEdges()) {
				double casefreq = (Double) e.getAttributes().get("frequency_case").get(0);

				tempList = new ArrayList<Double>();
				tempList.add(casefreq / logsize);
				e.addAttribute("frequency_case_rel", tempList);
			}

			processmap = AttributeHelper.getInstance().storeMaxAttributes(processmap);

			modelCounter++;
		}

		try {
			mergedAnnotatedGraph = context.tryToFindOrConstructFirstNamedObject(AnnotatedGraph.class,
					"Merge multiple annotated graphs", null, null, (Object[]) graphsToMerge);
		} catch (ConnectionCannotBeObtained e) {
			System.out
					.println("[AnnotatedGraphMinerConverterMergerPack] Merging annotated graphs throwed an exception");
			e.printStackTrace();
		}

		mergedLabel = mergedLabel.substring(0, mergedLabel.length() - 1);
		context.getFutureResult(0).setLabel("Merged annotated graph from" + mergedLabel);

		return mergedAnnotatedGraph;

	}

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	/**
	 * Variant that creates a merged annotated graph from event logs as input
	 * 
	 * In case of a Petrinet, we also use the alignment to map the log to the
	 * model to be able to couple the data
	 * 
	 * @author J.A.J. van Mourik
	 */
	@UITopiaVariant(uiLabel = "Compare process models; XLog", affiliation = UITopiaVariant.EHV, author = "J.A.J. van Mourik", email = "j.a.j.v.mourik@student.tue.nl", pack = "JeroenVanMourik")
	@PluginVariant(variantLabel = "Compare process models; XLog", requiredParameterLabels = { 0 })
	public AnnotatedGraph mergeXLog(UIPluginContext context, EventLogArray logArray) {
		String classifierName = "concept:name";

		CustomDialog dialog = new CustomDialog(context);
		InteractionResult result = context.showWizard("Choose the miner and classifier to be used", true, true, dialog);
		if (!(result == InteractionResult.FINISHED)) {
			return null;
		}

		AnnotatedGraph[] graphsToMerge = new AnnotatedGraph[logArray.getSize()];
		AnnotatedGraph mergedAnnotatedGraph = null;
		int modelCounter = 0;

		for(int i = 0; i < logArray.getSize(); i++){
			XLog inputLog = logArray.getLog(i);
			
			numberTraces = inputLog.size();
			doAnnotation = true;
			Petrinet minedNet = null;
			AnnotatedGraph minedDirectedGraph = null;
			AnnotatedGraph convertedDirectedGraph = null;
			String modelName = XConceptExtension.instance().extractName(inputLog);
			String[] classifierKeys = { classifierName };

			try {
				if (minerName.contains("causal")) {
					XEventClassifier customClassifier = new XEventAttributeClassifier(classifierName, classifierKeys);
					inputLog.getClassifiers().clear();
					inputLog.getClassifiers().add(customClassifier);
					minedDirectedGraph = ((PluginContext) context).tryToFindOrConstructFirstNamedObject(
							AnnotatedGraph.class, minerName, null, null, inputLog);
				}
				if (minerName.equals("Alpha Miner")) {
					XEventNameClassifier customClassifier = new XEventNameClassifier();
					inputLog.getClassifiers().clear();
					inputLog.getClassifiers().add(customClassifier);
					minedNet = ((PluginContext) context).tryToFindOrConstructFirstNamedObject(Petrinet.class,
							minerName, null, null, inputLog);
				}
				if (minerName.equals("ILP Miner")) {
					XEventNameClassifier customClassifier = new XEventNameClassifier();
					inputLog.getClassifiers().clear();
					inputLog.getClassifiers().add(customClassifier);
					minedNet = context.tryToFindOrConstructFirstNamedObject(Petrinet.class, minerName, null, null,
							inputLog);
				}
				if (minerName.equals("Mine Petri net with Inductive Miner")) {
					XEventNameClassifier customClassifier = new XEventNameClassifier();
					inputLog.getClassifiers().clear();
					inputLog.getClassifiers().add(customClassifier);
					minedNet = context.tryToFindOrConstructFirstNamedObject(Petrinet.class, minerName, null, null,
							inputLog);
				}
				if (!(minedNet == null)) {
					try {
						minedDirectedGraph = context.tryToFindOrConstructFirstNamedObject(AnnotatedGraph.class,
								"Convert a Petri net to a directed graph", null, null, minedNet, modelName);
					} catch (ConnectionCannotBeObtained e) {
						System.out
								.println("[AnnotatedGraphMinerConverterMergerPack] Converting a Petri net to a directed graph from "
										+ modelName + " throwed an exception");
						e.printStackTrace();
					}

					if (!(minedDirectedGraph == null)) {
						extractLogDataUsingAlignment(context, minedNet, inputLog);
						annotateGraphUsingLogData(minedDirectedGraph);
						graphsToMerge[modelCounter] = minedDirectedGraph;
					}
					/* TODO Prevent additional annotation */
					doAnnotation = false;
				}
			} catch (ConnectionCannotBeObtained e) {
				System.out.println("[AnnotatedGraphMinerConverterMergerPack] Running \"" + minerName + "\" with "
						+ modelName + " throwed an exception");
				e.printStackTrace();
			}
			if (!(minedDirectedGraph == null) && doAnnotation) {
				try {
					convertedDirectedGraph = context.tryToFindOrConstructFirstNamedObject(AnnotatedGraph.class,
							"Provide a directed graph with annotations", null, null, minedDirectedGraph, inputLog);
					graphsToMerge[modelCounter] = convertedDirectedGraph;
				} catch (ConnectionCannotBeObtained e) {
					System.out.println("[AnnotatedGraphMinerConverterMergerPack] Providing " + modelName
							+ " with annotations throwed an exception");
					e.printStackTrace();
				}
			}
			modelCounter++;
		}

		try {
			mergedAnnotatedGraph = context.tryToFindOrConstructFirstNamedObject(AnnotatedGraph.class,
					"Merge multiple annotated graphs", null, null, (Object[]) graphsToMerge);
		} catch (ConnectionCannotBeObtained e) {
			System.out
					.println("[AnnotatedGraphMinerConverterMergerPack] Merging annotated graphs throwed an exception");
			e.printStackTrace();
		}

		context.getFutureResult(0).setLabel(mergedAnnotatedGraph.getLabel());

		return mergedAnnotatedGraph;
	}

	@SuppressWarnings("unchecked")
	private void annotateGraphUsingLogData(AnnotatedGraph minedDirectedGraph) {
		String modelName = minedDirectedGraph.getLabel();
		HashMap<AnnotatedVertex, Integer> vertexFrequencies = new HashMap<AnnotatedVertex, Integer>();
		HashMap<AnnotatedEdgeIMP, Integer> edgeFrequencies = new HashMap<AnnotatedEdgeIMP, Integer>();
		
		HashMap<AnnotatedVertex, HashSet<Integer>> vertexCases = new HashMap<AnnotatedVertex, HashSet<Integer>>();
		HashMap<AnnotatedEdgeIMP, HashSet<Integer>> edgeCases = new HashMap<AnnotatedEdgeIMP, HashSet<Integer>>();
		
		for (PetrinetNode n : logData.keySet()) {
			HashMap<Integer, HashMap<Integer, HashMap<String, String>>> map1 = logData.get(n);
			for (int i1 : map1.keySet()) {
				HashMap<Integer, HashMap<String, String>> map2 = map1.get(i1);
				for (int i2 : map2.keySet()) {
					HashMap<String, String> map3 = map2.get(i2);
					
					AnnotatedVertex vertex = minedDirectedGraph.getVertex(n.getLabel());
					
					if (!(vertex == null)){						
						/* Vertex frequency */
						if (!vertexFrequencies.containsKey(vertex)){
							vertexFrequencies.put(vertex, 1);
						} else {
							int count = vertexFrequencies.get(vertex);
							count++;
							vertexFrequencies.put(vertex, count);
						}
						if (!vertexCases.containsKey(vertex)){
							vertexCases.put(vertex, new HashSet<Integer>());
						}
						vertexCases.get(vertex).add(i1);
						
						String tempTargetDate = "";
						String tempTargetStartDate = "";
						String tempTargetCompleteDate = "";
						SimpleDateFormat tempDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
						
						if (map3.containsKey("targetstartstamp")){
							tempTargetStartDate = map3.get("targetstartstamp");
							tempTargetDate = tempTargetStartDate;
						}
						if (map3.containsKey("targetcompletestamp")) {
							tempTargetCompleteDate = map3.get("targetcompletestamp");
							if (tempTargetStartDate.equals("")){
								tempTargetDate = tempTargetCompleteDate;
							}
						}
						if (tempTargetDate.length() <= 25) {
							tempTargetDate = tempTargetDate.substring(0, 19) + ".000";
						} else {
							tempTargetDate = tempTargetDate.substring(0, 23);
						}
						
						/* Vertex duration */
						if ((!tempTargetStartDate.equals("")) && (!tempTargetCompleteDate.equals(""))){
							if (tempTargetStartDate.length() <= 25) {
								tempTargetStartDate = tempTargetStartDate.substring(0, 19) + ".000";
							} else {
								tempTargetStartDate = tempTargetStartDate.substring(0, 23);
							}
							if (tempTargetCompleteDate.length() <= 25) {
								tempTargetCompleteDate = tempTargetCompleteDate.substring(0, 19) + ".000";
							} else {
								tempTargetCompleteDate = tempTargetCompleteDate.substring(0, 23);
							}
							try {
								Date startDate = tempDateFormat.parse(tempTargetStartDate);
								Date completeDate = tempDateFormat.parse(tempTargetCompleteDate);
								
								long dateDifference = completeDate.getTime() - startDate.getTime();
								if (dateDifference >= 0) {
									HashMap<String, HashMap<String, ArrayList<?>>> targetAttributes = vertex.getAttributes();
									ArrayList<Long> tempList = new ArrayList<Long>();
									if (!targetAttributes.containsKey("duration") || (!targetAttributes.get("duration").containsKey(modelName))){
										tempList.add(dateDifference);
									} else {
										tempList = (ArrayList<Long>) targetAttributes.get("duration").get(modelName);
										tempList.add(dateDifference);
									}
									vertex.addAttribute("duration", modelName, tempList);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						
						/* Edge duration */
						if (map3.containsKey("sourcecompletestamp") && map3.containsKey("sourcecompletename")){
							AnnotatedEdgeIMP edge = minedDirectedGraph.getEdge(map3.get("sourcecompletename"), vertex.getValue(), modelName);
							if (!(edge == null)){
								/* Edge frequency */
								if (!edgeFrequencies.containsKey(edge)){
									edgeFrequencies.put(edge, 1);
								} else {
									int count = edgeFrequencies.get(edge);
									count++;
									edgeFrequencies.put(edge, count);
								}
								if (!edgeCases.containsKey(edge)){
									edgeCases.put(edge, new HashSet<Integer>());
								}
								edgeCases.get(edge).add(i1);
								
								String tempSourceDate = map3.get("sourcecompletestamp");
								if (tempSourceDate.length() <= 25) {
									tempSourceDate = tempSourceDate.substring(0, 19) + ".000";
								} else {
									tempSourceDate = tempSourceDate.substring(0, 23);
								}
								try {
									Date sourceDate = tempDateFormat.parse(tempSourceDate);
									Date targetDate = tempDateFormat.parse(tempTargetDate);
									
									long dateDifference = targetDate.getTime() - sourceDate.getTime();
									if (dateDifference >= 0) {
										HashMap<String, ArrayList<?>> edgeAttributes = edge.getAttributes();
										ArrayList<Long> tempList = new ArrayList<Long>();
										if (!edgeAttributes.containsKey("duration")){
											tempList.add(dateDifference);
										} else {
											tempList = (ArrayList<Long>) edgeAttributes.get("duration");
											tempList.add(dateDifference);
										}
										edge.addAttribute("duration", tempList);
									}
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
						}
						HashMap<String, HashMap<String, ArrayList<?>>> vAttributes = vertex.getAttributes();
						for (String attributeName : map3.keySet()){
							if (!forbiddenAttributes.contains(attributeName)){
								ArrayList<Object> tempList;
								if (!vAttributes.containsKey(attributeName)){
									tempList = new ArrayList<Object>();
								} else {
									tempList = (ArrayList<Object>) vAttributes.get(attributeName).get(modelName);
								}
								tempList.add(map3.get(attributeName));
								vertex.addAttribute(attributeName, modelName, tempList);
							}
						}
					}
				}
			}
		}
		for (AnnotatedVertex v : minedDirectedGraph.getVertices()){
			if (vertexFrequencies.containsKey(v)){
				v.addAttribute("frequency", modelName, new ArrayList<Integer>(vertexFrequencies.get(v)));
				ArrayList<Double> tempList = new ArrayList<Double>();
				tempList.add(((double) vertexCases.get(v).size()) / numberTraces);
				v.addAttribute("frequency_case_rel", modelName, tempList); 
			}
		}
		for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : minedDirectedGraph.getEdges()){
			if (edgeFrequencies.containsKey(e)){
				e.addAttribute("frequency", new ArrayList<Integer>(edgeFrequencies.get(e)));
				ArrayList<Double> tempList = new ArrayList<Double>();
				tempList.add(((double) edgeCases.get(e).size()) / numberTraces);
				e.addAttribute("frequency_case_rel", tempList);
			}
		}
	}

	/*
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 * ========================================================================
	 */

	private void extractLogDataUsingAlignment(UIPluginContext context, Petrinet net, XLog log) {
		// replay log on model (or obtain existing replay result)
		ReplayProblem repproblem = new ReplayProblem(net, log);
		PNRepResult result = repproblem.getResult(context);
		initializeAlignment(net);
		alignLogToModel(context, log, net, result);
	}
}