/*****************************************************************************\
 * Copyright (c) 2008, 2009, 2010. Dirk Fahland. AGPL3.0
 * All rights reserved. 
 * 
 * ServiceTechnology.org - Uma, an Unfolding-based Model Analyzer
 * 
 * This program and the accompanying materials are made available under
 * the terms of the GNU Affero General Public License Version 3 or later,
 * which accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/agpl.txt
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
\*****************************************************************************/

package org.processmining.plugins.uma;

import hub.top.petrinet.PetriNet;
import hub.top.uma.DNodeSys;
import hub.top.uma.InvalidModelException;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.annotations.TestMethod;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * A helper class for converting ProM framework objects to objects of
 * Uma and its supporting Petri Net API and back
 * 
 * @author Dirk Fahland
 * @email dirk.fahland@service-technology.org
 * @version Jun 20, 2010
 *
 */
public class UmaPromUtil {
	
  /**
    * @param net
	* @param netLabel
	* @return an array of the Petrinet <code>net</code> and its initial marking
	*         in ProM format, the net has the given <code>netLabel</code>
	*/
  public static Object[] toPromFormat (hub.top.petrinet.PetriNet net, String netLabel) {
    Petrinet promNet = PetrinetFactory.newPetrinet(netLabel);
    Marking initMarking = new Marking();

    HashMap<hub.top.petrinet.Node, PetrinetNode> nodeMap = new HashMap<hub.top.petrinet.Node, PetrinetNode>();
    
    // first translate all places
    for (hub.top.petrinet.Place p : net.getPlaces()) {

      String name = p.getName();
        
      Place p2 = promNet.addPlace(name); 
      nodeMap.put(p, p2);
      
      if (p.getTokens() > 0)
        initMarking.add(p2, p.getTokens());
    }

    // then all transitions
    for (hub.top.petrinet.Transition t : net.getTransitions()) {
      
      String name = t.getName();
      Transition t2 = promNet.addTransition(name);
      nodeMap.put(t, t2);
      
      // and their incoming and outgoing arcs
      for (hub.top.petrinet.Place p : t.getPreSet()) {
        promNet.addArc((Place)nodeMap.get(p), (Transition)nodeMap.get(t));
      }
      for (hub.top.petrinet.Place p : t.getPostSet()) {
        promNet.addArc((Transition)nodeMap.get(t), (Place)nodeMap.get(p));
      }
    }
    return new Object[]{ promNet, initMarking, nodeMap };
  }
  
  public static hub.top.petrinet.PetriNet toPNAPIFormat(Petrinet net, Marking initMarking) {
	  return toPNAPIFormat(net, initMarking, new HashMap<PetrinetNode, hub.top.petrinet.Transition>());
  }
  
  public static hub.top.petrinet.PetriNet toPNAPIFormat(Petrinet net, Marking initMarking, Map<PetrinetNode, hub.top.petrinet.Transition> transitionMap) {
    hub.top.petrinet.PetriNet umaNet = new hub.top.petrinet.PetriNet();
    
    HashMap<PetrinetNode, hub.top.petrinet.Place> placeMap = new HashMap<PetrinetNode, hub.top.petrinet.Place>();
    
    for (PetrinetNode n : net.getPlaces()) {
      hub.top.petrinet.Place p = umaNet.addPlace(n.getLabel());
      placeMap.put(n, p);
      int num = initMarking.occurrences(n);
      if (num > 0) umaNet.setTokens(p, num);
    }
    
    for (PetrinetNode n : net.getTransitions()) {
      hub.top.petrinet.Transition t = umaNet.addTransition(n.getLabel());
      
      transitionMap.put(n, t);
      
      if (n instanceof org.processmining.models.graphbased.directed.petrinet.elements.Transition) {
    	  if (((org.processmining.models.graphbased.directed.petrinet.elements.Transition) n).isInvisible()) {
    		  t.tau = true;
    	  }
      }
      
      for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> a : net.getInEdges(n)) {
        umaNet.addArc(placeMap.get(a.getSource()), t);
      }
      for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> a : net.getOutEdges(n)) {
        umaNet.addArc(t, placeMap.get(a.getTarget()));
      }
    }
    
    return umaNet;
  }
  
  public static class ProMToUmaBridge {
	  public hub.top.petrinet.PetriNet pnet;
	  public Map<PetrinetNode, hub.top.petrinet.Node> nodeMap = new HashMap<PetrinetNode, hub.top.petrinet.Node>();
  }
  
  public static ProMToUmaBridge toPNAPIFormat(PetrinetGraph net, Marking initMarking) {
	  
	  ProMToUmaBridge bridge = new ProMToUmaBridge();
	  bridge.pnet = new hub.top.petrinet.PetriNet();
	    

	    for (PetrinetNode n : net.getPlaces()) {
	      hub.top.petrinet.Place p = bridge.pnet.addPlace(n.getLabel());
	      bridge.nodeMap.put(n, p);
	      int num = initMarking.occurrences(n);
	      if (num > 0) bridge.pnet.setTokens(p, num);
	    }
	    
	    for (PetrinetNode n : net.getTransitions()) {
	      hub.top.petrinet.Transition t = bridge.pnet.addTransition(n.getLabel());
	      bridge.nodeMap.put(n, t);
	      if (((Transition)n).isInvisible()) t.setTau(true);
	      
	      for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> a : net.getInEdges(n)) {
	    	  // depending on the net type, the arc may come from a node that is not a place
	    	  // in this case the mapping will return null
	    	  hub.top.petrinet.Place p = (hub.top.petrinet.Place)bridge.nodeMap.get(a.getSource());
	    	  if (p != null) bridge.pnet.addArc(p, t);
	      }
	      for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> a : net.getOutEdges(n)) {
	    	  // depending on the net type, the arc may point to a node that is not a place
	    	  // in this case the mapping will return null
	    	  hub.top.petrinet.Place p = (hub.top.petrinet.Place)bridge.nodeMap.get(a.getTarget());
	    	  if (p != null) bridge.pnet.addArc(t, p);
	      }
	    }
	    
	    return bridge;
	  }
  
  public static DNodeSys toUmaFormat(Petrinet net, Marking initMarking) throws InvalidModelException {
    return new DNodeSys_PtNet(net, initMarking);
  }
  
  public static String[] toSimpleEventTrace(XTrace trace, XEventClassifier classifier) {
	  HashMap<String, String> nameCache = new HashMap<String, String>();
	  return toSimpleEventTrace(trace, nameCache, classifier);
  }
  
  private static String[] toSimpleEventTrace(XTrace trace, HashMap<String, String> nameCache, XEventClassifier classifier) {
      LinkedList<String> eventTrace = new LinkedList<String>();
      if (classifier == null) classifier = org.deckfour.xes.info.impl.XLogInfoImpl.STANDARD_CLASSIFIER;
      try {
        for (XEvent event : trace) {
          String eventString = classifier.getClassIdentity(event);
          if (!nameCache.containsKey(eventString)) nameCache.put(eventString, eventString);
          eventTrace.add(nameCache.get(eventString));
        }
        String[] eventTrace2 = new String[eventTrace.size()];
        eventTrace.toArray(eventTrace2);
        return eventTrace2;
      } catch (AssertionError e) {
        //System.out.print(" failed to read ");
    	  return null;
      }
  }
  
  public static LinkedList<String []> toSimpleEventLog (XLog log, XEventClassifier classifier) {
    HashMap<String, String> nameCache = new HashMap<String, String>();
    LinkedList<String []> simpleLog = new LinkedList<String[]>();
    for (XTrace trace : log) {
    	String[] eventTrace = toSimpleEventTrace(trace, nameCache, classifier); 
        if (eventTrace != null) simpleLog.addLast(eventTrace);
    }
    return simpleLog;
  }
  
  /**
   * @param simpleLog
   * @param logName
   * @return {@link XLog} representation of the simple log
   */
  public static XLog toXLog(Collection<String[]> simpleLog, String logName) {
	  
		// create aligned log
		XFactory f = XFactoryRegistry.instance().currentDefault();
		
		XLog xLog = f.createLog();
		
		// log needs a name
		String alignedLogName = logName;
		XAttributeMap logAttr = f.createAttributeMap();
		logAttr.put("concept:name",
					   f.createAttributeLiteral("concept:name", alignedLogName, XConceptExtension.instance()));
		xLog.setAttributes(logAttr);

		int caseNum = 0;
		
		// create traces in the aligned log (each trace is one trace class from the replay)
		for (String[] trace : simpleLog) {
		
			// create trace
			XTrace t = f.createTrace();
			
			// write trace attributes
			XAttributeMap traceAttr = f.createAttributeMap();
			traceAttr.put("concept:name",
						   f.createAttributeLiteral("concept:name", "case"+caseNum, XConceptExtension.instance()));
			t.setAttributes(traceAttr);
			
			// add events to trace
			for (int i=0; i<trace.length; i++) {
				
				// split name into event name and life-cycle transition
				String qualified_eventName = trace[i];
				String name;
				String life_cycle;
				int plus_pos = qualified_eventName.indexOf('+');
				if (plus_pos >= 0) {
					name = qualified_eventName.substring(0, plus_pos);
					life_cycle = qualified_eventName.substring(plus_pos+1);
				} else {
					name = qualified_eventName;
					life_cycle = "complete";
				}
				
				// write event attributes
				XEvent e = f.createEvent();
				XAttributeMap eventAttr = f.createAttributeMap();
				eventAttr.put("concept:name",
							   f.createAttributeLiteral("concept:name", name, XConceptExtension.instance())); 
				eventAttr.put("lifecycle:transition",
						   f.createAttributeLiteral("lifecycle:transition", life_cycle, XLifecycleExtension.instance()));
				e.setAttributes(eventAttr);
				
				// add event to trace
				t.add(e);
			}
		
			// add trace to log
			xLog.add(t);
		}
		return xLog;
  }
  
  public static boolean equalNets(PetriNet net1, PetriNet net2) {
	  if (net1.getTransitions().size() != net2.getTransitions().size()) return false;
	  if (net1.getPlaces().size() != net2.getPlaces().size()) return false;
	  if (net1.getArcs().size() != net2.getArcs().size()) return false;
	  
	  HashMap<hub.top.petrinet.Node, hub.top.petrinet.Node> match = new HashMap<hub.top.petrinet.Node, hub.top.petrinet.Node>();
	  
	  for (hub.top.petrinet.Place p1 : net1.getPlaces()) {
		  boolean found = false;
		  
		  for (hub.top.petrinet.Place p2 : net1.getPlaces()) {
			  if (p1.getName().equals(p2.getName())) {
				  match.put(p1,p2);
				  found = true;
			  }
		  }
		  
		  if (!found) return false;
	  }
	  
	  for (hub.top.petrinet.Transition t1 : net1.getTransitions()) {
		  boolean found = false;
		  
		  for (hub.top.petrinet.Transition t2 : net1.getTransitions()) {
			  if (t1.getName().equals(t2.getName())) {
				  match.put(t1,t2);
				  found = true;
			  }
		  }
		  
		  if (!found) return false;
	  }

	  for (hub.top.petrinet.Node n1 : match.keySet()) {
		  for (hub.top.petrinet.Arc a : n1.getIncoming()) {
			  if (!match.get(n1).getPreSet().contains(a.getSource())) return false;
		  }
		  for (hub.top.petrinet.Arc a : n1.getOutgoing()) {
			  if (!match.get(n1).getPostSet().contains(a.getTarget())) return false;
		  }
	  }
	  
	  return true;
  }
  
	@TestMethod(output="nets are equal: true")
	public static String test_UMA_PromUtil_conversion() {
		PetriNet net = new PetriNet();
		net.addPlace("p1");
		net.addPlace("p2");
		net.addPlace("p3");
		net.addTransition("t1");
		net.addTransition("t2");
		net.addTransition("t3");
		net.setTokens("p1", 1);
		net.addArc("p1", "t1"); net.addArc("t1", "p2"); net.addArc("t1", "p3");
		net.addArc("p1", "t2"); net.addArc("t2", "p3");
		net.addArc("p3", "t3"); net.addArc("t3", "p1");
		
		Object[] res = toPromFormat(net, "testNet");
		Petrinet promNet = (Petrinet)res[0];
		Marking initMarking = (Marking)res[1];
		PetriNet net2 = toPNAPIFormat(promNet, initMarking);
		
		return "nets are equal: "+equalNets(net, net2);
	}
	
	public static void main(String[] args) {
		test_UMA_PromUtil_conversion();
	}
	
	public static void printMessage(PluginContext context, String header, String message) {
		System.out.println("["+header+"]: "+message);
		context.log(message);
	}
}
