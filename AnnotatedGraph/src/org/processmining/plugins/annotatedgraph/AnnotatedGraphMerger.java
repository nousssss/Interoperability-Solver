package org.processmining.plugins.annotatedgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.annotatedgraph.AnnotatedEdge;
import org.processmining.models.annotatedgraph.AnnotatedEdgeIMP;
import org.processmining.models.annotatedgraph.AnnotatedGraph;
import org.processmining.models.annotatedgraph.AnnotatedGraph.ElementType;
import org.processmining.models.annotatedgraph.AnnotatedVertex;

@Plugin(name = "Merge multiple annotated graphs", parameterLabels = { "AnnotatedGraph" }, returnLabels = { "AnnotatedGraph" }, returnTypes = { AnnotatedGraph.class })
public class AnnotatedGraphMerger {
	protected double eMaxAggregate = 0;
	String[] gNames;
	protected double vMaxAggregate = 0;
	public static final String[] FUNCTIONTAGS = { "VARIANCE", "AVERAGE", "MAXIMUM", "MINIMUM", "RANGE", "MEDIAN",
			"DISTINCT VALUES COUNT", "MOST FREQUENT VALUE", "MISSING ELEMENTS COUNT", "MISSING VALUES COUNT" };
	// Structure: Attribute, Aggregate function, Element, Value
	protected static HashMap<String, HashMap<String, HashMap<Object, Double>>> globalAggregateValuesMap = new HashMap<String, HashMap<String, HashMap<Object, Double>>>();
	// Structure: Attribute, Aggregate function, Element, Model, Value
	protected static HashMap<String, HashMap<String, HashMap<Object, HashMap<String, Double>>>> localAggregateValuesMap = new HashMap<String, HashMap<String, HashMap<Object, HashMap<String, Double>>>>();

	private boolean checkAllNumbers(ArrayList<ArrayList<Object>> attributeValues) {
		for (ArrayList<Object> objectList : attributeValues) {
			for (Object object : objectList) {
				try {
					Double.parseDouble(object.toString());
				} catch (NumberFormatException e) {
					return false;
				}
			}
		}
		return true;
	}

	public void computeAggregateFunctions(AnnotatedGraph m, int modelCount) {

		for (AnnotatedVertex v : m.getVertices()) {
			HashMap<String, HashMap<String, ArrayList<?>>> vAttributes = new HashMap<String, HashMap<String, ArrayList<?>>>(
					v.getAttributes());

			for (String attributeName : vAttributes.keySet()) {
				ArrayList<ArrayList<Object>> globalAttributeValues = new ArrayList<ArrayList<Object>>();
				for (String modelName : gNames) {
					ArrayList<Object> attributeValue;
					if (vAttributes.get(attributeName).containsKey(modelName)) {
						if (vAttributes.get(attributeName).get(modelName) != null) {
							attributeValue = new ArrayList<Object>(vAttributes.get(attributeName).get(modelName));
						} else {
							attributeValue = new ArrayList<Object>();
							attributeValue.add(vAttributes.get(attributeName).get(modelName));
						}
						v.addAttribute(attributeName, modelName, attributeValue);
					} else {
						attributeValue = new ArrayList<Object>();
					}
					globalAttributeValues.add(attributeValue);

					// [BEGIN Local Aggregate calculations]
					ArrayList<ArrayList<Object>> localAttributeValues = new ArrayList<ArrayList<Object>>();
					localAttributeValues.add(attributeValue);

					computeMissingValuesCount(localAttributeValues, 1, v, attributeName, modelName, true);
					computeAverage(localAttributeValues, v, attributeName, modelName, true);
					computeVariance(localAttributeValues, v, attributeName, modelName, true);
					computeMaximum(localAttributeValues, v, attributeName, modelName, true);
					computeMinimum(localAttributeValues, v, attributeName, modelName, true);
					computeDistinctValuesCount(localAttributeValues, v, attributeName, modelName, true);
					computeRange(localAttributeValues, v, attributeName, modelName, true);
					computeMedian(localAttributeValues, v, attributeName, modelName, true);
					computeMostFrequentValue(localAttributeValues, v, attributeName, modelName, true);
					computeMissingElementsCount(localAttributeValues, 1, v, attributeName, modelName, true);
					// [END Local Aggregate calculations]					
				}

				// [BEGIN Aggregate calculations]
				computeMissingValuesCount(globalAttributeValues, modelCount, v, attributeName, "", false);
				computeAverage(globalAttributeValues, v, attributeName, "", false);
				computeVariance(globalAttributeValues, v, attributeName, "", false);
				computeMaximum(globalAttributeValues, v, attributeName, "", false);
				computeMinimum(globalAttributeValues, v, attributeName, "", false);
				computeDistinctValuesCount(globalAttributeValues, v, attributeName, "", false);
				computeRange(globalAttributeValues, v, attributeName, "", false);
				computeMedian(globalAttributeValues, v, attributeName, "", false);
				computeMostFrequentValue(globalAttributeValues, v, attributeName, "", false);
				computeMissingElementsCount(globalAttributeValues, modelCount, v, attributeName, "", false);
				// [END Aggregate calculations]
			}
		}

		for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : m.getEdges()) {
			HashMap<String, ArrayList<?>> eAttributes = new HashMap<String, ArrayList<?>>(e.getAttributes());

			for (String attributeName : eAttributes.keySet()) {
				ArrayList<ArrayList<Object>> globalAttributeValues = new ArrayList<ArrayList<Object>>();

				ArrayList<Object> attributeValue;
				if (eAttributes.get(attributeName) != null) {
					attributeValue = new ArrayList<Object>(eAttributes.get(attributeName));
				} else {
					attributeValue = new ArrayList<Object>();
					attributeValue.add(eAttributes.get(attributeName));
				}
				e.addAttribute(attributeName, attributeValue);
				globalAttributeValues.add(attributeValue);

				// [BEGIN Global Aggregate calculations]
				computeMissingValuesCount(globalAttributeValues, modelCount, e, attributeName, "", false);
				computeAverage(globalAttributeValues, e, attributeName, "", false);
				computeVariance(globalAttributeValues, e, attributeName, "", false);
				computeMaximum(globalAttributeValues, e, attributeName, "", false);
				computeMinimum(globalAttributeValues, e, attributeName, "", false);
				computeDistinctValuesCount(globalAttributeValues, e, attributeName, "", false);
				computeRange(globalAttributeValues, e, attributeName, "", false);
				computeMedian(globalAttributeValues, e, attributeName, "", false);
				computeMostFrequentValue(globalAttributeValues, e, attributeName, "", false);
				computeMissingElementsCount(globalAttributeValues, modelCount, e, attributeName, "", false);
				// [END Global Aggregate calculations]
			}
		}

		// [BEGIN] 'Normalize' all aggregate values --> Max(new) = 1, Min(new) = Min(old) / Max(old)

		// Structure: Attribute, Aggregate function, Element, Model, Value
		for (String attributeName : localAggregateValuesMap.keySet()) {
			for (String functionName : localAggregateValuesMap.get(attributeName).keySet()) {
				for (Object graphElement : localAggregateValuesMap.get(attributeName).get(functionName).keySet()) {

					// Element, Value
					HashMap<String, Double> aggregateValuesMap = new HashMap<String, Double>();
					double maxAggregateValue = Integer.MIN_VALUE;
					double minAggregateValue = Integer.MAX_VALUE;

					for (String modelName : localAggregateValuesMap.get(attributeName).get(functionName)
							.get(graphElement).keySet()) {
						aggregateValuesMap.put(modelName, localAggregateValuesMap.get(attributeName).get(functionName)
								.get(graphElement).get(modelName));
						maxAggregateValue = Math.max(maxAggregateValue, aggregateValuesMap.get(modelName));
						minAggregateValue = Math.min(minAggregateValue, aggregateValuesMap.get(modelName));
					}
					if ((maxAggregateValue == 0) && (minAggregateValue == 0)) {
						maxAggregateValue = Integer.MIN_VALUE;
					} else if (maxAggregateValue == 0) {
						maxAggregateValue = 1;
					}
					for (String modelName : aggregateValuesMap.keySet()) {
						double aggregateValue;
						if ((maxAggregateValue == Integer.MIN_VALUE)) {
							aggregateValue = 1.0 / localAggregateValuesMap.get(attributeName).get(functionName)
									.get(graphElement).keySet().size();
						} else {
							aggregateValue = aggregateValuesMap.get(modelName) / maxAggregateValue;
						}
						if (graphElement instanceof AnnotatedVertex) {
							((AnnotatedVertex) graphElement).addLocalAggregate(attributeName, modelName, functionName,
									aggregateValue);
						}
					}
				}
			}
		}

		// Structure: Attribute, Aggregate function, Element, Value
		for (String attributeName : globalAggregateValuesMap.keySet()) {
			for (String functionName : globalAggregateValuesMap.get(attributeName).keySet()) {

				// Element, Value
				HashMap<Object, Double> aggregateValuesMap = new HashMap<Object, Double>();
				HashMap<AnnotatedGraph.ElementType, Double> maxAggregateValuesMap = new HashMap<AnnotatedGraph.ElementType, Double>();
				maxAggregateValuesMap.put(AnnotatedGraph.ElementType.AnnotatedVertex, (double) Integer.MIN_VALUE);
				maxAggregateValuesMap.put(AnnotatedGraph.ElementType.AnnotatedEdge, (double) Integer.MIN_VALUE);

				for (Object graphElement : globalAggregateValuesMap.get(attributeName).get(functionName).keySet()) {
					aggregateValuesMap.put(graphElement, globalAggregateValuesMap.get(attributeName).get(functionName)
							.get(graphElement));
					if (graphElement instanceof AnnotatedVertex) {
						maxAggregateValuesMap.put(AnnotatedGraph.ElementType.AnnotatedVertex, Math.max(
								maxAggregateValuesMap.get(AnnotatedGraph.ElementType.AnnotatedVertex),
								aggregateValuesMap.get(graphElement)));
					} else if (graphElement instanceof AnnotatedEdge) {
						maxAggregateValuesMap.put(AnnotatedGraph.ElementType.AnnotatedEdge, Math.max(
								maxAggregateValuesMap.get(AnnotatedGraph.ElementType.AnnotatedEdge),
								aggregateValuesMap.get(graphElement)));
					}
				}
				if ((maxAggregateValuesMap.get(AnnotatedGraph.ElementType.AnnotatedVertex) == Integer.MIN_VALUE)
						|| (maxAggregateValuesMap.get(AnnotatedGraph.ElementType.AnnotatedVertex) == 0)) {
					maxAggregateValuesMap.put(AnnotatedGraph.ElementType.AnnotatedVertex, (double) 1);
				}
				if ((maxAggregateValuesMap.get(AnnotatedGraph.ElementType.AnnotatedEdge) == Integer.MIN_VALUE)
						|| (maxAggregateValuesMap.get(AnnotatedGraph.ElementType.AnnotatedEdge) == 0)) {
					maxAggregateValuesMap.put(AnnotatedGraph.ElementType.AnnotatedEdge, (double) 1);
				}
				for (Object graphElement : globalAggregateValuesMap.get(attributeName).get(functionName).keySet()) {
					if (graphElement instanceof AnnotatedVertex) {
						((AnnotatedVertex) graphElement).addGlobalAggregate(attributeName, functionName,
								(aggregateValuesMap.get(graphElement) / maxAggregateValuesMap
										.get(AnnotatedGraph.ElementType.AnnotatedVertex)));
					} else if (graphElement instanceof AnnotatedEdge) {
						((AnnotatedEdgeIMP) graphElement).addGlobalAggregate(attributeName, functionName,
								(aggregateValuesMap.get(graphElement) / maxAggregateValuesMap
										.get(AnnotatedGraph.ElementType.AnnotatedEdge)));
					}
				}
			}
		}
		// [END] 'Normalize' all aggregate values --> Max(new) = 1, Min(new) = Min(old) / Max(old)		
	}

	public void computeAverage(ArrayList<ArrayList<Object>> attributeValues, Object graphElement, String attributeName,
			String modelName, boolean computeLocalAggregate) {
		double functionResult = 0;
		ArrayList<ArrayList<Object>> attributeValuesCopy = new ArrayList<ArrayList<Object>>();

		for (ArrayList<Object> list : attributeValues) {
			ArrayList<Object> listCopy = new ArrayList<Object>();
			listCopy.addAll(list);
			if (!(listCopy.isEmpty())) {
				attributeValuesCopy.add(listCopy);
			}
		}

		if (checkAllNumbers(attributeValuesCopy)) {
			if ((attributeValuesCopy.size() == 1)) {
				for (Object number : attributeValuesCopy.get(0)) {
					functionResult += Double.parseDouble(number.toString());
				}
				functionResult = functionResult / attributeValuesCopy.get(0).size();
			} else {
				for (ArrayList<Object> list : attributeValuesCopy) {
					double tempFunctionResult = 0;
					for (Object number : list) {
						tempFunctionResult += Double.parseDouble(number.toString());
					}
					functionResult += tempFunctionResult / list.size();
				}
				functionResult = functionResult / attributeValuesCopy.size();
			}
		}
		storeResultInMap(graphElement, attributeName, modelName, computeLocalAggregate, functionResult, FUNCTIONTAGS[1]);
	}

	////////////////////////////////////////////
	// AGGREGATE FUNCTIONS /////////////////////
	////////////////////////////////////////////

	public void computeDistinctValuesCount(ArrayList<ArrayList<Object>> attributeValues, Object graphElement,
			String attributeName, String modelName, boolean computeLocalAggregate) {
		double functionResult = 0;
		HashSet<String> distinctValues = new HashSet<String>();

		for (ArrayList<Object> list : attributeValues) {
			for (Object object : list) {
				distinctValues.add(object.toString());
			}
		}
		functionResult = distinctValues.size();

		storeResultInMap(graphElement, attributeName, modelName, computeLocalAggregate, functionResult, FUNCTIONTAGS[6]);
	}

	public void computeMaximum(ArrayList<ArrayList<Object>> attributeValues, Object graphElement, String attributeName,
			String modelName, boolean computeLocalAggregate) {
		double functionResult = Integer.MIN_VALUE;

		if (checkAllNumbers(attributeValues)) {
			for (ArrayList<Object> list : attributeValues) {
				for (Object number : list) {
					functionResult = Math.max(functionResult, Double.parseDouble(number.toString()));
				}
			}
		}

		if (functionResult == Integer.MIN_VALUE) {
			functionResult = 0;
		}
		storeResultInMap(graphElement, attributeName, modelName, computeLocalAggregate, functionResult, FUNCTIONTAGS[2]);
	}

	public void computeMedian(ArrayList<ArrayList<Object>> attributeValues, Object graphElement, String attributeName,
			String modelName, boolean computeLocalAggregate) {
		double functionResult = 0;
		int numberOfObjects = 0;
		ArrayList<ArrayList<Object>> attributeValuesCopy = new ArrayList<ArrayList<Object>>();

		for (ArrayList<Object> list : attributeValues) {
			ArrayList<Object> listCopy = new ArrayList<Object>();
			listCopy.addAll(list);
			if (listCopy.isEmpty()) {
				listCopy.add(0);
			}
			attributeValuesCopy.add(listCopy);
		}

		if (checkAllNumbers(attributeValuesCopy)) {
			for (ArrayList<Object> list : attributeValuesCopy) {
				numberOfObjects += list.size();
			}
			double[] allNumbers = new double[numberOfObjects];
			int i = 0;
			for (ArrayList<Object> list : attributeValuesCopy) {
				for (Object number : list) {
					allNumbers[i] = Double.parseDouble(number.toString());
					i++;
				}
			}
			Arrays.sort(allNumbers);
			if ((numberOfObjects % 2) == 0) {
				functionResult = (allNumbers[(int) ((numberOfObjects / 2.0) - 1)] + allNumbers[(int) (numberOfObjects / 2.0)]) / 2.0;
			} else {
				functionResult = allNumbers[(int) ((numberOfObjects - 1) / 2.0)];
			}
		}
		storeResultInMap(graphElement, attributeName, modelName, computeLocalAggregate, functionResult, FUNCTIONTAGS[5]);
	}

	public void computeMinimum(ArrayList<ArrayList<Object>> attributeValues, Object graphElement, String attributeName,
			String modelName, boolean computeLocalAggregate) {
		double functionResult = Integer.MAX_VALUE;

		if (checkAllNumbers(attributeValues)) {
			for (ArrayList<Object> list : attributeValues) {
				for (Object number : list) {
					functionResult = Math.min(functionResult, Double.parseDouble(number.toString()));
				}
			}
		}

		if (functionResult == Integer.MAX_VALUE) {
			functionResult = 0;
		}
		storeResultInMap(graphElement, attributeName, modelName, computeLocalAggregate, functionResult, FUNCTIONTAGS[3]);
	}

	public void computeMissingElementsCount(ArrayList<ArrayList<Object>> attributeValues, int modelCount,
			Object graphElement, String attributeName, String modelName, boolean computeLocalAggregate) {
		HashMap<String, HashMap<String, ArrayList<?>>> elementAttributes = null;
		if (graphElement instanceof AnnotatedVertex) {
			elementAttributes = ((AnnotatedVertex) graphElement).getAttributes();
		} else if (graphElement instanceof AnnotatedEdge) {
			return;
		}

		double functionResult = modelCount - elementAttributes.get(attributeName).keySet().size();
		storeResultInMap(graphElement, attributeName, modelName, computeLocalAggregate, functionResult, FUNCTIONTAGS[8]);
	}

	public void computeMissingValuesCount(ArrayList<ArrayList<Object>> attributeValues, int modelCount,
			Object graphElement, String attributeName, String modelName, boolean computeLocalAggregate) {
		double functionResult = modelCount;

		HashMap<String, HashMap<String, ArrayList<?>>> elementAttributes = null;
		if (graphElement instanceof AnnotatedVertex) {
			elementAttributes = ((AnnotatedVertex) graphElement).getAttributes();
		} else if (graphElement instanceof AnnotatedEdge) {
			return;
		}

		if (elementAttributes.containsKey(attributeName)) {
			if (elementAttributes.get(attributeName).keySet().size() == gNames.length) {
				functionResult = 0;
			} else {
				for (String gName : gNames) {
					if (elementAttributes.get(attributeName).containsKey(gName)
							&& (!elementAttributes.get(attributeName).containsKey(modelName))) {
						for (String aName : elementAttributes.keySet()) {
							if (elementAttributes.get(aName).containsKey(modelName)) {
								functionResult -= 1;
								break;
							}
						}
					} else {
						functionResult = 0;
					}
				}
			}
		} else {
			functionResult = 0;
		}

		storeResultInMap(graphElement, attributeName, modelName, computeLocalAggregate, functionResult, FUNCTIONTAGS[9]);
	}

	public void computeMostFrequentValue(ArrayList<ArrayList<Object>> attributeValues, Object graphElement,
			String attributeName, String modelName, boolean computeLocalAggregate) {
		double functionResult = 0;
		int max = Integer.MIN_VALUE;
		HashMap<Double, Integer> valueCounts = new HashMap<Double, Integer>();

		if (checkAllNumbers(attributeValues)) {
			for (ArrayList<Object> list : attributeValues) {
				for (Object number : list) {
					if (valueCounts.containsKey(Double.parseDouble(number.toString()))) {
						int newCount = valueCounts.get(Double.parseDouble(number.toString())) + 1;
						valueCounts.put(Double.parseDouble(number.toString()), newCount);
						max = Math.max(max, newCount);
					} else {
						valueCounts.put(Double.parseDouble(number.toString()), 1);
						max = Math.max(max, 1);
					}
				}
			}
		}

		for (Double number : valueCounts.keySet()) {
			if (valueCounts.get(number) == max) {
				functionResult = number;
				break;
			}
		}
		storeResultInMap(graphElement, attributeName, modelName, computeLocalAggregate, functionResult, FUNCTIONTAGS[7]);
	}

	public void computeRange(ArrayList<ArrayList<Object>> attributeValues, Object graphElement, String attributeName,
			String modelName, boolean computeLocalAggregate) {
		double functionResult = 0;
		double max = Integer.MIN_VALUE;
		double min = Integer.MAX_VALUE;
		ArrayList<ArrayList<Object>> attributeValuesCopy = new ArrayList<ArrayList<Object>>();

		for (ArrayList<Object> list : attributeValues) {
			ArrayList<Object> listCopy = new ArrayList<Object>();
			listCopy.addAll(list);
			if (listCopy.isEmpty()) {
				listCopy.add(0);
			}
			attributeValuesCopy.add(listCopy);
		}

		if (checkAllNumbers(attributeValuesCopy)) {
			for (ArrayList<Object> list : attributeValuesCopy) {
				for (Object number : list) {
					max = Math.max(max, Double.parseDouble(number.toString()));
					min = Math.min(min, Double.parseDouble(number.toString()));
				}
			}
		}

		if (!((max == Integer.MIN_VALUE) || (min == Integer.MAX_VALUE))) {
			functionResult = max - min;
		}
		storeResultInMap(graphElement, attributeName, modelName, computeLocalAggregate, functionResult, FUNCTIONTAGS[4]);
	}

	private double computeSampleVariance(ArrayList<Object> attributeValue) {
		double functionResult = 0;
		double sum = 0;
		double mean = 0;
		ArrayList<Double> numberList = new ArrayList<Double>();

		if (attributeValue.size() == 0) {
			return functionResult;
		}
		for (Object number : attributeValue) {
			double value = Double.parseDouble(number.toString());
			numberList.add(value);
			sum += value;
		}
		mean = sum / numberList.size();
		for (Double value : numberList) {
			functionResult += (mean - value) * (mean - value);
		}
		return (functionResult / numberList.size());
	}

	private void computeVariance(ArrayList<ArrayList<Object>> attributeValues, Object graphElement,
			String attributeName, String modelName, boolean computeLocalAggregate) {
		double functionResult = 0;
		double sampleVariance;
		double denominator = 0;
		boolean allListsSingles = true;
		ArrayList<Object> singlesList = new ArrayList<Object>();
		ArrayList<ArrayList<Object>> attributeValuesCopy = new ArrayList<ArrayList<Object>>();

		for (ArrayList<Object> list : attributeValues) {
			ArrayList<Object> listCopy = new ArrayList<Object>();
			listCopy.addAll(list);
			if (listCopy.isEmpty()) {
				listCopy.add(0);
			}
			attributeValuesCopy.add(listCopy);
		}

		if (checkAllNumbers(attributeValuesCopy)) {
			for (ArrayList<Object> list : attributeValuesCopy) {
				if (list.size() == 1) {
					singlesList.add(list.get(0));
				} else {
					allListsSingles = false;
				}
				if (!allListsSingles) {
					break;
				}
			}
			if ((attributeValuesCopy.size() == 1)) {
				functionResult = computeSampleVariance(attributeValuesCopy.get(0));
			} else if (allListsSingles) {
				functionResult = computeSampleVariance(singlesList);
			} else {
				for (ArrayList<Object> attributeValue : attributeValuesCopy) {
					sampleVariance = computeSampleVariance(attributeValue);
					functionResult += ((attributeValue.size() - 1) * sampleVariance);
					denominator += attributeValue.size() - 1;
				}
				functionResult = functionResult / (Math.max(denominator, 1));
			}
		} else {
			ArrayList<ArrayList<Object>> copyattributeValues = new ArrayList<ArrayList<Object>>(attributeValuesCopy);

			for (ArrayList<Object> al1 : attributeValuesCopy) {
				copyattributeValues.remove(al1);
				for (ArrayList<Object> al2 : copyattributeValues) {
					// "List"-Jaccard (al1,al2): Respects duplicates
					ArrayList<Object> intersection = new ArrayList<Object>();
					ArrayList<Object> copyAl2 = new ArrayList<Object>(al2);
					for (Object value : al1) {
						if (copyAl2.contains(value)) {
							intersection.add(value);
							intersection.add(value);
							copyAl2.remove(value);
						}
					}
					if (intersection.isEmpty()) {
						functionResult += 1.0 - (1.0 / ((double) al1.size() + (double) al2.size()));
					} else {
						functionResult += 1.0 - (intersection.size() / ((double) al1.size() + (double) al2.size()));
					}
				}
			}
			functionResult = functionResult / attributeValuesCopy.size();
		}
		storeResultInMap(graphElement, attributeName, modelName, computeLocalAggregate, functionResult, FUNCTIONTAGS[0]);
	}

	////////////////////////////////////////////
	// HELPER FUNCTIONS / METHODS //////////////
	////////////////////////////////////////////

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "J.A.J. van Mourik", email = "j.a.j.v.mourik@student.tue.nl", pack = "JeroenVanMourik")
	@PluginVariant(variantLabel = "Merge multiple annotated graphs, Default", requiredParameterLabels = { 0 })
	public AnnotatedGraph mergeDefault(PluginContext context, AnnotatedGraph... inputGraphs) {
		return privateMerge(context, inputGraphs);
	}

	private AnnotatedGraph privateMerge(PluginContext context, AnnotatedGraph... inputGraphs) {
		AnnotatedGraph[] graphs = new AnnotatedGraph[inputGraphs.length];
		AnnotatedGraph m = new AnnotatedGraph("Merged");
		String mLabel = "Merged annotated graph from";
		gNames = new String[inputGraphs.length];

		for (int i = 0; i < inputGraphs.length; i++) {
			gNames[i] = inputGraphs[i].getLabel();
		}

		Arrays.sort(gNames);

		int i = 0;
		for (String gName : gNames) {
			mLabel += " " + gName + ",";
			for (AnnotatedGraph inputGraph : inputGraphs) {
				if (inputGraph.getLabel().equals(gName)) {
					graphs[i] = inputGraphs[i].getClone();
					break;
				}
			}
			i++;
		}
		mLabel = mLabel.substring(0, mLabel.length() - 1);
		context.getFutureResult(0).setLabel(mLabel);
		m.setLabel(mLabel);

		// [BEGIN] Retrieve data from all input graphs
		for (AnnotatedGraph g : graphs) {
			// [BEGIN] Combine elements and their attributes
			for (AnnotatedVertex v : g.getVertices()) {
				AnnotatedVertex mVertex = m.getVertex(v.getValue());
				if (mVertex == null) {
					m.addVertex(v);
				} else {
					HashMap<String, HashMap<String, ArrayList<?>>> vAttributes = v.getAttributes();
					for (String attributeName : vAttributes.keySet()) {
						for (String modelName : vAttributes.get(attributeName).keySet()) {
							mVertex.addAttribute(attributeName, modelName, vAttributes.get(attributeName)
									.get(modelName));
						}
					}
				}
			}
			for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : g.getEdges()) {
				AnnotatedEdgeIMP mEdge = m.getEdge(e.getSource().getValue(), e.getTarget().getValue(), e.getModel());
				if (mEdge == null) {
					AnnotatedVertex ePotentialSource = m.getVertex(e.getSource().getValue());
					AnnotatedVertex ePotentialTarget = m.getVertex(e.getTarget().getValue());
					if ((ePotentialSource == null) || (ePotentialTarget == null)) {
						continue;
					}
					mEdge = m.addEdge(ePotentialSource, ePotentialTarget, e.getModel());
				}
				HashMap<String, ArrayList<?>> eAttributes = e.getAttributes();
				for (String attributeName : eAttributes.keySet()) {
					mEdge.addAttribute(attributeName, eAttributes.get(attributeName));
				}
			}
			// [END] Combine elements and their attributes

			// [BEGIN] Combine max attributes
			HashMap<String, HashMap<ElementType, Double>> gMaxAttributes = g.getMaxAttributes();
			HashMap<String, HashMap<ElementType, Double>> mMaxAttributes = m.getMaxAttributes();

			for (String attributeName : gMaxAttributes.keySet()) {
				for (ElementType elementType : gMaxAttributes.get(attributeName).keySet()) {
					if (!(mMaxAttributes.containsKey(attributeName))) {
						mMaxAttributes.put(attributeName, new HashMap<ElementType, Double>());
					}
					if (!(mMaxAttributes.get(attributeName).containsKey(elementType))) {
						mMaxAttributes.get(attributeName).put(elementType,
								gMaxAttributes.get(attributeName).get(elementType));
					} else {
						mMaxAttributes.get(attributeName).put(
								elementType,
								Math.max(m.getMaxAttribute(attributeName, elementType),
										gMaxAttributes.get(attributeName).get(elementType)));
					}
				}
			}
			m.setMaxAttributes(mMaxAttributes);
			// [END] Combine max attributes			
		}
		// [END] Retrieve data from all input graphs

		computeAggregateFunctions(m, graphs.length);

		// [BEGIN] Remove vertices that do not have any edges connected
		HashSet<AnnotatedVertex> mVertices = new HashSet<AnnotatedVertex>(m.getVertices());
		for (AnnotatedVertex v : mVertices) {
			boolean hasEdges = false;
			for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : m.getEdges()) {
				hasEdges |= (e.getSource().equals(v) || e.getTarget().equals(v));
				if (hasEdges) {
					break;
				}
			}
			if (!hasEdges) {
				m.removeVertex(v);
			}
		}
		// [END] Remove vertices that do not have any edges connected

		return m;
	}

	private void storeResultInMap(Object element, String attributeName, String modelName,
			boolean computeLocalAggregate, double value, String tag) {
		if (computeLocalAggregate) {
			if (!(localAggregateValuesMap.containsKey(attributeName))) {
				localAggregateValuesMap.put(attributeName,
						new HashMap<String, HashMap<Object, HashMap<String, Double>>>());
			}
			if (!(localAggregateValuesMap.get(attributeName).containsKey(tag))) {
				localAggregateValuesMap.get(attributeName).put(tag, new HashMap<Object, HashMap<String, Double>>());
			}
			if (!(localAggregateValuesMap.get(attributeName).get(tag).containsKey(element))) {
				localAggregateValuesMap.get(attributeName).get(tag).put(element, new HashMap<String, Double>());
			}
			localAggregateValuesMap.get(attributeName).get(tag).get(element).put(modelName, value);
		} else {
			if (!(globalAggregateValuesMap.containsKey(attributeName))) {
				globalAggregateValuesMap.put(attributeName, new HashMap<String, HashMap<Object, Double>>());
			}
			if (!(globalAggregateValuesMap.get(attributeName).containsKey(tag))) {
				globalAggregateValuesMap.get(attributeName).put(tag, new HashMap<Object, Double>());
			}
			globalAggregateValuesMap.get(attributeName).get(tag).put(element, value);
			vMaxAggregate = Math.max(vMaxAggregate, value);
		}
	}
}