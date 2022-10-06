package org.processmining.plugins.annotatedgraph;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.helpers.AttributeHelper;
import org.processmining.models.annotatedgraph.AnnotatedEdge;
import org.processmining.models.annotatedgraph.AnnotatedEdgeIMP;
import org.processmining.models.annotatedgraph.AnnotatedGraph;
import org.processmining.models.annotatedgraph.AnnotatedVertex;

@Plugin(name = "Provide a directed graph with annotations", parameterLabels = { "AnnotatedGraph", "Log" }, returnLabels = { "AnnotatedGraph" }, returnTypes = { AnnotatedGraph.class })
public class AnnotatedGraphAnnotater {
	protected AnnotatedGraph graph;
	public static String CLASSIFIER = "concept:name";
	public static final String DURATIONTAG = "duration";
	public static final String FREQUENCYTAG = "frequency";
	public static final String LIFECYCLECOMPLETE = "complete";
	public static final String LIFECYCLETAG = "lifecycle:transition";
	public static final String TIMESTAMPTAG = "time:timestamp";

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "J.A.J. van Mourik", email = "j.a.j.v.mourik@student.tue.nl", pack = "JeroenVanMourik")
	@PluginVariant(variantLabel = "Provide a directed graph with annotations", requiredParameterLabels = { 0, 1 })
	public AnnotatedGraph annotateGraph(PluginContext context, AnnotatedGraph inputGraph, XLog inputLog) {
		return privateAnnotater(context, inputGraph, inputLog);
	}

	private AnnotatedGraph privateAnnotater(PluginContext context, AnnotatedGraph inputGraph, XLog inputLog) {
		AnnotatedGraph localGraph = inputGraph.getClone();

		// Retrieve label; this assumes graph and model are compatible
		String modelName = XConceptExtension.instance().extractName(inputLog);
		graph = new AnnotatedGraph(modelName);

		for (AnnotatedVertex localVertex : localGraph.getVertices()) {
			graph.addVertex(localVertex.getValue());
		}
		for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> localEdge : localGraph.getEdges()) {
			AnnotatedVertex sourceVertex = graph.getVertex(localEdge.getSource().getValue());
			AnnotatedVertex targetVertex = graph.getVertex(localEdge.getTarget().getValue());

			if ((!(sourceVertex == null)) && (!(targetVertex == null))) {
				graph.addEdge(sourceVertex, targetVertex, localEdge.getModel());
			}
		}

		context.getFutureResult(0).setLabel("Annotated graph from " + modelName);

		storeAttributes(inputLog);

		return AttributeHelper.getInstance().storeMaxAttributes(graph);
	}

	@SuppressWarnings("unchecked")
	private void storeAttributes(XLog xLog) {
		String modelName = XConceptExtension.instance().extractName(xLog);
		long divTime = 1; // Time in milliseconds: 1; Time in seconds: 1000; etc...
		HashMap<XTrace, HashMap<String, Boolean>> traceCounted = new HashMap<XTrace, HashMap<String, Boolean>>();

		for (XTrace xTrace : xLog) {
			boolean completedEvent = true;
			String sourceName = null;
			Date sourceDate = null;
			Date startDate = null;
			String currentName = null;
			Date currentDate = null;
			String tempDate = null;

			if (!(traceCounted.containsKey(xTrace))) {
				traceCounted.put(xTrace, new HashMap<String, Boolean>());
			}
			for (XEvent xEvent : xTrace) {
				XAttributeMap xEventAttributes = xEvent.getAttributes();

				// Classifier and time stamps are required
				if ((!(xEventAttributes.containsKey(CLASSIFIER))) || (!(xEventAttributes.containsKey(TIMESTAMPTAG)))) {
					continue;
				}

				currentName = xEventAttributes.get(CLASSIFIER).toString();

				tempDate = (xEventAttributes.get(TIMESTAMPTAG).toString());
				if (tempDate.length() <= 25) {
					tempDate = tempDate.substring(0, 19) + ".000";
				} else {
					tempDate = tempDate.substring(0, 23);
				}
				SimpleDateFormat tempDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				try {
					currentDate = tempDateFormat.parse(tempDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				// If the current event is a new event (and therefore possible edge target) and the source is defined >>> retrieve edge (if possible)
				if (completedEvent && (sourceName != null) && (!sourceName.equals(currentName))) {
					if ((currentDate != null) && (sourceDate != null)) {
						long dateDifference = currentDate.getTime() - sourceDate.getTime();
						if (dateDifference >= 0) {

							AnnotatedEdgeIMP e = graph.getEdge(sourceName, currentName, modelName);

							if (!(e == null)) {
								HashMap<String, ArrayList<?>> eAttributes = e.getAttributes();

								if (!(traceCounted.get(xTrace).containsKey(sourceName + currentName))) {
									traceCounted.get(xTrace).put(sourceName + currentName, false);
								}
								if (!traceCounted.get(xTrace).get(sourceName + currentName)) {
									traceCounted.get(xTrace).put(sourceName + currentName, true);

									ArrayList<Integer> tempCaseFrequencyList = new ArrayList<Integer>();
									if (!eAttributes.containsKey("case_" + FREQUENCYTAG)) {
										tempCaseFrequencyList.add(1);
									} else {
										Integer tempCaseFreq = ((ArrayList<Integer>) eAttributes.get("case_"
												+ FREQUENCYTAG)).get(0);
										tempCaseFreq++;
										tempCaseFrequencyList.add(tempCaseFreq);
									}
									e.addAttribute("case_" + FREQUENCYTAG, tempCaseFrequencyList);
								}

								ArrayList<Integer> tempFrequencyList = new ArrayList<Integer>();
								if ((!(eAttributes.containsKey(FREQUENCYTAG)))) {
									tempFrequencyList.add(1);
								} else {
									Integer tempFreq = ((ArrayList<Integer>) eAttributes.get(FREQUENCYTAG)).get(0);
									tempFreq++;
									tempFrequencyList.add(tempFreq);
								}
								e.addAttribute(FREQUENCYTAG, tempFrequencyList);

								ArrayList<Long> tempDurationList = new ArrayList<Long>();
								if (eAttributes.containsKey(DURATIONTAG)) {
									tempDurationList = (ArrayList<Long>) eAttributes.get(DURATIONTAG);
								}
								tempDurationList.add(dateDifference / divTime);
								e.addAttribute(DURATIONTAG, tempDurationList);
							}
						}
					}
				}

				if (completedEvent) {
					startDate = currentDate;
					completedEvent = false;
				}

				if ((!xEventAttributes.containsKey(LIFECYCLETAG))
						|| (xEventAttributes.containsKey(LIFECYCLETAG) && xEventAttributes.get(LIFECYCLETAG).toString()
								.equals(LIFECYCLECOMPLETE))) {
					sourceName = currentName;
					sourceDate = currentDate;
					completedEvent = true;

					if ((currentDate != null) && (startDate != null)) {
						long dateDifference = currentDate.getTime() - startDate.getTime();
						if (dateDifference >= 0) {

							AnnotatedVertex v = graph.getVertex(currentName);

							if (!(v == null)) {
								HashMap<String, HashMap<String, ArrayList<?>>> vAttributes = v.getAttributes();

								if (!(traceCounted.get(xTrace).containsKey(v.getValue()))) {
									traceCounted.get(xTrace).put(v.getValue(), false);
								}
								if (!traceCounted.get(xTrace).get(v.getValue())) {
									traceCounted.get(xTrace).put(v.getValue(), true);

									ArrayList<Integer> tempCaseFrequencyList = new ArrayList<Integer>();
									if (!vAttributes.containsKey("case_" + FREQUENCYTAG)
											|| (!(vAttributes.get("case_" + FREQUENCYTAG).containsKey(modelName)))) {
										tempCaseFrequencyList.add(1);
									} else {
										Integer tempCaseFreq = ((ArrayList<Integer>) vAttributes.get(
												"case_" + FREQUENCYTAG).get(modelName)).get(0);
										tempCaseFreq++;
										tempCaseFrequencyList.add(tempCaseFreq);
									}
									v.addAttribute("case_" + FREQUENCYTAG, modelName, tempCaseFrequencyList);
								}

								ArrayList<Integer> tempFrequencyList = new ArrayList<Integer>();
								if ((!(vAttributes.containsKey(FREQUENCYTAG)))
										|| (!(vAttributes.get(FREQUENCYTAG).containsKey(modelName)))) {
									tempFrequencyList.add(1);
								} else {
									Integer oldfreq = ((ArrayList<Integer>) vAttributes.get(FREQUENCYTAG)
											.get(modelName)).get(0);
									oldfreq++;
									tempFrequencyList.add(oldfreq);
								}
								v.addAttribute(FREQUENCYTAG, modelName, tempFrequencyList);

								ArrayList<Long> tempDurationList = new ArrayList<Long>();
								if (!((!(vAttributes.containsKey(DURATIONTAG))) || (!(vAttributes.get(DURATIONTAG)
										.containsKey(modelName))))) {
									tempDurationList = (ArrayList<Long>) vAttributes.get(DURATIONTAG).get(modelName);
								}
								tempDurationList.add(dateDifference / divTime);
								v.addAttribute(DURATIONTAG, modelName, tempDurationList);

								for (String attributeName : xEventAttributes.keySet()) {
									if (!(attributeName.equals(LIFECYCLETAG) || attributeName.equals(TIMESTAMPTAG))) {

										ArrayList<Object> vAttributeValue = new ArrayList<Object>();
										if (!((!(vAttributes.containsKey(attributeName))) || (!(vAttributes
												.get(attributeName).containsKey(modelName))))) {
											vAttributeValue = (ArrayList<Object>) v.getAttributes(attributeName).get(
													modelName);
										}
										vAttributeValue.add(xEventAttributes.get(attributeName).toString());
										v.addAttribute(attributeName, modelName, vAttributeValue);
									}
								}
							}
						}
					}
				}
			}
		}

		for (AnnotatedVertex v : graph.getVertices()) {
			HashMap<String, HashMap<String, ArrayList<?>>> vAttributes = v.getAttributes();
			Integer vCaseFrequencyValue = 0;

			if (vAttributes.containsKey(FREQUENCYTAG)) {
				Integer vFrequencyValue = ((ArrayList<Integer>) vAttributes.get(FREQUENCYTAG).get(modelName)).get(0);
				ArrayList<Double> tempRelFrequencyList = new ArrayList<Double>();
				tempRelFrequencyList.add(vFrequencyValue / (double) xLog.size());
				v.addAttribute("rel_" + FREQUENCYTAG, modelName, tempRelFrequencyList);

				if (vAttributes.containsKey("case_" + FREQUENCYTAG)) {
					vCaseFrequencyValue = ((ArrayList<Integer>) vAttributes.get("case_" + FREQUENCYTAG).get(modelName))
							.get(0);
					ArrayList<Double> tempRelCaseFrequencyList = new ArrayList<Double>();
					tempRelCaseFrequencyList.add(vCaseFrequencyValue / (double) xLog.size());
					v.addAttribute("rel_case_" + FREQUENCYTAG, modelName, tempRelCaseFrequencyList);

					ArrayList<Double> tempFreqPerCaseFrequencyList = new ArrayList<Double>();
					tempFreqPerCaseFrequencyList.add((double) vFrequencyValue / (double) vCaseFrequencyValue);
					v.addAttribute(FREQUENCYTAG + "_per_case", modelName, tempFreqPerCaseFrequencyList);
				}
			}

			HashSet<String> attributeKeys = new HashSet<String>(vAttributes.keySet());
			for (String attributeName : attributeKeys) {
				if (attributeName.contains("duration") && (!(vCaseFrequencyValue == 0))) {
					ArrayList<Object> durationValue = ((ArrayList<Object>) vAttributes.get(attributeName)
							.get(modelName));

					Double durationSum = 0.0;
					for (Object singleDuration : durationValue) {
						try {
							durationSum += Double.parseDouble(singleDuration.toString());
						} catch (NumberFormatException ex) {
							//
						}
					}
					ArrayList<Double> tempDurationCase = new ArrayList<Double>();
					tempDurationCase.add(durationSum / vCaseFrequencyValue);
					v.addAttribute(attributeName + "_per_case", modelName, tempDurationCase);
				}
			}
		}

		for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : graph.getEdges()) {
			HashMap<String, ArrayList<?>> eAttributes = e.getAttributes();
			Integer eCaseFrequencyValue = 0;

			if (eAttributes.containsKey(FREQUENCYTAG)) {
				Integer eFrequencyValue = ((ArrayList<Integer>) eAttributes.get(FREQUENCYTAG)).get(0);
				ArrayList<Double> tempRelFrequencyList = new ArrayList<Double>();
				tempRelFrequencyList.add(eFrequencyValue / (double) xLog.size());
				e.addAttribute("rel_" + FREQUENCYTAG, tempRelFrequencyList);

				if (eAttributes.containsKey("case_" + FREQUENCYTAG)) {
					eCaseFrequencyValue = ((ArrayList<Integer>) eAttributes.get("case_" + FREQUENCYTAG)).get(0);
					ArrayList<Double> tempRelCaseFrequencyList = new ArrayList<Double>();
					tempRelCaseFrequencyList.add(eCaseFrequencyValue / (double) xLog.size());
					e.addAttribute("rel_case_" + FREQUENCYTAG, tempRelCaseFrequencyList);

					ArrayList<Double> tempFreqPerCaseFrequencyList = new ArrayList<Double>();
					tempFreqPerCaseFrequencyList.add((double) eFrequencyValue / (double) eCaseFrequencyValue);
					e.addAttribute(FREQUENCYTAG + "_per_case", tempFreqPerCaseFrequencyList);
				}
			}

			HashSet<String> attributeKeys = new HashSet<String>(eAttributes.keySet());
			for (String attributeName : attributeKeys) {
				if (attributeName.contains("duration") && (!(eCaseFrequencyValue == 0))) {
					ArrayList<Object> durationValue = ((ArrayList<Object>) eAttributes.get(attributeName));

					Double durationSum = 0.0;
					for (Object singleDuration : durationValue) {
						try {
							durationSum += Double.parseDouble(singleDuration.toString());
						} catch (NumberFormatException ex) {
							//
						}
					}
					ArrayList<Double> tempDurationCase = new ArrayList<Double>();
					tempDurationCase.add(durationSum / eCaseFrequencyValue);
					e.addAttribute(attributeName + "_per_case", tempDurationCase);
				}
			}
		}

	}
}