package org.processmining.plugins.workshop.Khanhlv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

public class Trace {
	private List<String> listEventGraph;
	private int distance;

	//each event will have format:
	//{org:resource=UNDEFINED, concept:name=g, lifecycle:transition=complete, time:timestamp=2010-11-09T19:07:41.903+07:00}
	/**
	 * 
	 * Constructor to initial a Trace rendered by distance graph. Two parameter is a
	 * trace (XTrace type) and dis(int type) is distance which we want to render.
	 * 
	 * @param trace
	 * @param dis
	 */
	public Trace(XTrace trace, int dis) {
		distance = dis;
		listEventGraph = new ArrayList<String>();
		renderGraph(renderTrace(trace));
	}

	/**
	 * Function to render a trace distance graph order 0.
	 * 
	 * @param trace
	 * @return trace
	 */
	private List<String> renderTrace(XTrace trace) {
		List<String> traceGraph0 = new ArrayList<String>();
		Iterator<XEvent> listEvent = trace.iterator();
		while (listEvent.hasNext()) {
			traceGraph0.add(listEvent.next().getAttributes().get("concept:name").toString());
		}
		return traceGraph0;
	}

	/**
	 * 
	 * Function to render a trace to distance graph.
	 * 
	 * @param traceGraph0
	 */
	private void renderGraph(List<String> traceGraph0) {
		if (distance < 0)
			return;
		for (int index = 0; index < traceGraph0.size() - distance; index++) {
			if (distance > 0) {
				String event1 = traceGraph0.get(index);
				String event2 = traceGraph0.get(index + distance);
				listEventGraph.add(event1 + event2);
				}
			else
				listEventGraph.add(traceGraph0.get(index));
		}
		distance--;
		renderGraph(traceGraph0);
	}

	public List<String> listEvent() {
		return listEventGraph;
	}
}
