package org.processmining.plugins.workshop.Khanhlv;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExportToFile {

	private String fileName;
	private List<Integer> listIndex;
	private XLog log;
	private Document doc;
	private Element rootElement;

	public ExportToFile(String fileName, Cluster cluster, XLog log) {
		
		this.fileName = fileName;
		this.log      = log;
		listIndex     = cluster.getListElement();
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder        = docFactory.newDocumentBuilder();
			
			//root elements
            doc         = docBuilder.newDocument();
            rootElement = doc.createElement("Process");
            rootElement.setAttribute("id", this.fileName);
            doc.appendChild(rootElement);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		readData();
	}

//	private void createFolder() {
//		if (!Files.exists(pathFile)) {
//			try {
//				Files.createDirectories(pathFile);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

	private void readData() {
		Iterator<XTrace> traceIter = log.iterator();
		List<XTrace> listTrace = new ArrayList<XTrace>();
		while (traceIter.hasNext()) {
			listTrace.add(traceIter.next());
		}
		for (int i = 0; i < listIndex.size(); i++) {
			XTrace temp = listTrace.get(listIndex.get(i) - 1);
			String idTrace = temp.getAttributes().get("concept:name").toString();

			//create note for trace
			Element trace = doc.createElement("ProcessInstance");
			trace.setAttribute("id", idTrace);
			rootElement.appendChild(trace);
			
			Iterator<XEvent> eventIter = temp.iterator();
			while (eventIter.hasNext()) {
				XEvent e = eventIter.next();
				
				//create node for a event
				Element eventElement = doc.createElement("AuditTrailEntry");
				trace.appendChild(eventElement);
				
				//create element WorkflowModeElement
				String workFlow = e.getAttributes().get("concept:name").toString();
				Element work    = doc.createElement("WorkflowModelElement");
				work.appendChild(doc.createTextNode(workFlow));
				eventElement.appendChild(work);
				
				//create element EventType
				String eventType = e.getAttributes().get("lifecycle:transition").toString();
				Element type     = doc.createElement("EventType");
				type.appendChild(doc.createTextNode(eventType));
				eventElement.appendChild(type);
				
				//create element Timestamp
				String timeStamp = e.getAttributes().get("time:timestamp").toString();
				Element time     = doc.createElement("Timestamp");
				time.appendChild(doc.createTextNode(timeStamp));
				eventElement.appendChild(time);
				
				//create element Originator
				String originator = e.getAttributes().get("org:resource").toString();
				Element ori       = doc.createElement("Originator");
				ori.appendChild(doc.createTextNode(originator));
				eventElement.appendChild(ori);
				
			}
		}
	}

	public void export() {
		try {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(fileName));
        
        transformer.transform(source, result);
        
        System.out.println("File saved.");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
