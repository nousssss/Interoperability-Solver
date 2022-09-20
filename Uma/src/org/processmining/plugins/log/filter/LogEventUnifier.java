package org.processmining.plugins.log.filter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.serialize.XStreamObject;
import org.processmining.plugins.utils.HammingDistance;

public class LogEventUnifier {
	
	public static class LogEventUnifierMapping implements XStreamObject {
		
		protected final Map<String, Set<String>> e2eMap = new HashMap<String, Set<String>>();
		protected final Set<String> unassigned = new TreeSet<String>();
		
		public LogEventUnifierMapping () {
		}
		
		public LogEventUnifierMapping(LogEventUnifierMapping map) {
			this.e2eMap.putAll(map.e2eMap);
			this.unassigned.addAll(map.unassigned);
		}
		
		public String getXStreamAlias() {
			return "unifier";
		}

	}
	
	protected final LogEventUnifierMapping mapping;
	
	public LogEventUnifier(Collection<String> knownEvents, XLog log) {
		
		mapping = new LogEventUnifierMapping();
		
		for (String s : knownEvents) {
			mapping.e2eMap.put(s, new TreeSet<String>());
		}
		
		addEventsFromLogToUnassigned(log);
		distributeUnassignedToEventClasses();
	}
	
	public LogEventUnifier(LogEventUnifierMapping mapping) {
		this.mapping = new LogEventUnifierMapping(mapping);
	}
	
	public static Collection<String> getNamesFromModel(Petrinet net) {
		Set<String> eventNames = new HashSet<String>();

		for (Transition t : net.getTransitions()) {
			eventNames.add(t.getLabel());
		}
		
		return eventNames;
	}
	
	private void addEventsFromLogToUnassigned(XLog log) {
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				String eventName = event.getAttributes().get("concept:name").toString();
				mapping.unassigned.add(eventName);
			}
		}
				
	}
	
	public static boolean isExactlyContainedIn(String s1, String s2) {
		return s2.toLowerCase().indexOf(s1) >= 0;
	}
	
	public static boolean isRoughlyContainedIn(String s1, String s2) {
		s2 = s2.replace('_', ' ');
		s2 = s2.replace('-', ' ');
		String words[] = s2.split(" ");

		double best = Double.MAX_VALUE;
		String bestMatch = null;
		for (String w : words) {
			double sim = ((double)w.length() - HammingDistance.hammingDistance(s1, w))/w.length();
			if (sim < .3) {
				if (sim < best) {
					best = sim;
					bestMatch = w;
				}
			}
		}
		return bestMatch != null;
	}
		
	public void distributeUnassignedToEventClasses() {
		
		Set<String> noMatch = new TreeSet<String>();
		
		for (String eventName : mapping.unassigned) {
		
			eventName = eventName.toLowerCase();
		
			String bestMatch = null;
			int bestMatchCount = 0;
			
			for (String knownEvent : mapping.e2eMap.keySet()) {
				String keywords[] = knownEvent.split(" ");
				
				int matchCount = 0;
				int exactCount = 0;
				for (String keyword : keywords) {
					if (isExactlyContainedIn(keyword, eventName)) {
						exactCount++;
						matchCount++;
					} else if (isRoughlyContainedIn(keyword, eventName)) {
						matchCount++;
					}
				}
				if (matchCount > 0) {
					if (exactCount == keywords.length) {
						bestMatchCount = exactCount;
						bestMatch = knownEvent;
					}
					if (matchCount > bestMatchCount) {
						bestMatchCount = matchCount;
						bestMatch = knownEvent;
					}
				}
			}
			
			if (bestMatch == null) {
				noMatch.add(eventName);
			} else {
				mapping.e2eMap.get(bestMatch).add(eventName);
			}
		}
		mapping.unassigned.clear();
		mapping.unassigned.addAll(noMatch);
	}
	
	// the inverse of e2eMap
	private Map<String, String> classification = null;
	
	/**
	 * compute inverse of e2eMap, i.e. give each event classified in some class
	 * its corresponding event class
	 */
	private void computeInverseMapping() {
		classification = new HashMap<String, String>();
		for (String eventClass : mapping.e2eMap.keySet()) {
			
			System.out.println(eventClass+" "+mapping.e2eMap.get(eventClass));
			
			for (String event : mapping.e2eMap.get(eventClass)) {
				classification.put(event, eventClass);
			}
		}
		
		System.out.println(mapping.unassigned);
		
		for (String event : mapping.unassigned) {
			classification.put(event, "unassigned");
		}
	}
	
	/**
	 * @return map that assigns each event the name of the unified event class
	 */
	public Map<String, String> getEventClassification() {
		if (classification == null) computeInverseMapping();
		return classification;
	}

}
