package org.processmining.plugins.discoprocessmap;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.annotatedgraph.AnnotatedEdgeIMP;
import org.processmining.models.annotatedgraph.AnnotatedGraph;
import org.processmining.models.annotatedgraph.AnnotatedVertex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Plugin(name = "Disco process map", parameterLabels = { "Filename" }, returnLabels = { "DiscoProcessMap" }, returnTypes = { AnnotatedGraph.class })
@UIImportPlugin(description = "Disco process map", extensions = { "xml" })
public class DiscoProcessMapImporter extends AbstractImportPlugin {

	protected Object importFromStream(PluginContext context, InputStream input, String fileName, long fileSizeInBytes)
			throws Exception {
		HashMap<String, String> nodeIDMap = new HashMap<String, String>();

		String processMapName = fileName.replaceAll(".xml", "");
		AnnotatedGraph importedGraph = new AnnotatedGraph(processMapName);

		try {
			context.getFutureResult(0).setLabel(processMapName);

			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document parsedDocument = documentBuilder.parse(input);
			parsedDocument.getDocumentElement().normalize();

			NodeList nodeList = parsedDocument.getElementsByTagName("Node");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node documentNode = nodeList.item(i);
				if (documentNode.getNodeType() == Node.ELEMENT_NODE) {
					Element nodeElement = (Element) documentNode;

					AnnotatedVertex v = importedGraph.addVertex(nodeElement.getAttribute("activity"));
					if (!(v == null)) {
						nodeIDMap.put(nodeElement.getAttribute("index"), nodeElement.getAttribute("activity"));

						NodeList frequencyList = nodeElement.getElementsByTagName("Frequency");
						for (int j = 0; j < frequencyList.getLength(); j++) {
							Node frequencyNode = frequencyList.item(j);
							if (frequencyNode.getNodeType() == Node.ELEMENT_NODE) {
								Element frequencyElement = (Element) frequencyNode;

								ArrayList<Double> tempList;

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(frequencyElement.getAttribute("total")));
								v.addAttribute("frequency", processMapName, tempList);

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(frequencyElement.getAttribute("case")));
								v.addAttribute("frequency_case", processMapName, tempList);

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(frequencyElement.getAttribute("start")));
								v.addAttribute("frequency_start", processMapName, tempList);

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(frequencyElement.getAttribute("end")));
								v.addAttribute("frequency_end", processMapName, tempList);

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(frequencyElement.getAttribute("maxRepetitions")));
								v.addAttribute("frequency_maxRepetitions", processMapName, tempList);
							}
						}

						NodeList durationList = nodeElement.getElementsByTagName("Duration");
						for (int j = 0; j < durationList.getLength(); j++) {
							Node durationNode = durationList.item(j);
							if (durationNode.getNodeType() == Node.ELEMENT_NODE) {
								Element durationElement = (Element) durationNode;

								ArrayList<Double> tempList;

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(durationElement.getAttribute("total")));
								v.addAttribute("duration", processMapName, tempList);

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(durationElement.getAttribute("max")));
								v.addAttribute("duration_max", processMapName, tempList);

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(durationElement.getAttribute("mean")));
								v.addAttribute("duration_mean", processMapName, tempList);

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(durationElement.getAttribute("median")));
								v.addAttribute("duration_median", processMapName, tempList);
							}
						}

					} else {
						System.out.println("[DiscoGraphImporter] Vertex " + nodeElement.getAttribute("activity")
								+ " is NULL and could therefore not be added");
					}
				}
			}

			NodeList eList = parsedDocument.getElementsByTagName("Edge");

			for (int i = 0; i < eList.getLength(); i++) {
				Node documentNode = eList.item(i);
				if (documentNode.getNodeType() == Node.ELEMENT_NODE) {
					Element edgeElement = (Element) documentNode;

					AnnotatedVertex potentialSource = importedGraph.getVertex(nodeIDMap.get(edgeElement
							.getAttribute("sourceIndex")));
					AnnotatedVertex potentialTarget = importedGraph.getVertex(nodeIDMap.get(edgeElement
							.getAttribute("targetIndex")));
					if ((potentialSource == null) || (potentialTarget == null)) {
						continue;
					}

					AnnotatedEdgeIMP e = importedGraph.addEdge(potentialSource, potentialTarget, processMapName);
					if (!(e == null)) {
						NodeList frequencyList = edgeElement.getElementsByTagName("Frequency");
						for (int j = 0; j < frequencyList.getLength(); j++) {
							Node frequencyNode = frequencyList.item(j);
							if (frequencyNode.getNodeType() == Node.ELEMENT_NODE) {
								Element frequencyElement = (Element) frequencyNode;

								ArrayList<Double> tempList;

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(frequencyElement.getAttribute("total")));
								e.addAttribute("frequency", tempList);

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(frequencyElement.getAttribute("case")));
								e.addAttribute("frequency_case", tempList);

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(frequencyElement.getAttribute("maxRepetitions")));
								e.addAttribute("frequency_maxRepetitions", tempList);
							}
						}

						NodeList durationList = edgeElement.getElementsByTagName("Duration");
						for (int j = 0; j < durationList.getLength(); j++) {
							Node durationNode = durationList.item(j);
							if (durationNode.getNodeType() == Node.ELEMENT_NODE) {
								Element durationElement = (Element) durationNode;

								ArrayList<Double> tempList;

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(durationElement.getAttribute("total")));
								e.addAttribute("duration", tempList);

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(durationElement.getAttribute("max")));
								e.addAttribute("duration_max", tempList);

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(durationElement.getAttribute("mean")));
								e.addAttribute("duration_mean", tempList);

								tempList = new ArrayList<Double>();
								tempList.add(Double.parseDouble(durationElement.getAttribute("median")));
								e.addAttribute("duration_median", tempList);
							}
						}

					} else {
						System.out.println("[DiscoGraphImporter] Edge " + potentialSource.getValue() + "_"
								+ potentialTarget.getValue() + " is NULL and could therefore not be added");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return importedGraph;
	}

}