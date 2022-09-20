/**
 * 
 */
package org.processmining.plugins.petrinet.replayresult;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * @author aadrians
 * Sep 14, 2012
 *
 */
public class ViolatingSyncMove {
	private Transition transition = null;
	private XEventClass eventClass = null;
	private String info = null;

	@SuppressWarnings("unused")
	private ViolatingSyncMove(){}
	
	public ViolatingSyncMove(Transition t){
		this(t, null, null);
	}
	
	public ViolatingSyncMove(XEventClass ec){
		this(null, ec, null);
	}
	
	public ViolatingSyncMove(Transition t, XEventClass ec, String info){
		this.transition = t;
		this.eventClass = ec;
		this.info = info;
	}
	
	/**
	 * @return the transition
	 */
	public Transition getTransition() {
		return transition;
	}

	/**
	 * @param transition the transition to set
	 */
	public void setTransition(Transition transition) {
		this.transition = transition;
	}

	/**
	 * @return the eventClass
	 */
	public XEventClass getEventClass() {
		return eventClass;
	}

	/**
	 * @param eventClass the eventClass to set
	 */
	public void setEventClass(XEventClass eventClass) {
		this.eventClass = eventClass;
	}
	
	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	public String toString(){
		if (info != null){
			return info;
		} else {
			String str = ""; 
			if (eventClass != null){
				str += "[" + eventClass.getId() + "]";
			} else {
				str += "[no event class]";
			}
			str += "-sync-with-";
			if (transition != null){
				str += "[" + transition.getLabel() + "]";
			} else {
				str += "[no trans]";
			}
			return str;
		}
	}
}
