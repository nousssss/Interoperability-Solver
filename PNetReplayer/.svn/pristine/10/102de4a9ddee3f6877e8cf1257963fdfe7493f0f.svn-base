/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.util;

import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;

/**
 * @author aadrians
 *
 */
public class LogAutomatonNode {
	private int id;
	private XEventClass eventClass;
	private int frequency = 0;
	private Set<LogAutomatonNode> children = null;
	
	@SuppressWarnings("unused")
	private LogAutomatonNode(){}
	
	public LogAutomatonNode(int id, XEventClass eventClass, int frequency){
		this.id = id;
		this.eventClass = eventClass;
		this.frequency = frequency;
		this.children = new HashSet<LogAutomatonNode>(1); 
	}
	
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @param child
	 * @return
	 */
	public boolean addChild(LogAutomatonNode child){
		return this.children.add(child);
	}
	
	/**
	 * 
	 * @param evClass
	 * @return the children that represents the ev class
	 */
	public LogAutomatonNode isParentOfClass(XEventClass evClass){
		for (LogAutomatonNode node : children){
			if (node.getEventClass().equals(evClass)){
				return node;
			}
		}
		return null;
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
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the children
	 */
	public Set<LogAutomatonNode> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(Set<LogAutomatonNode> children) {
		this.children = children;
	}

	public void incFrequency() {
		this.frequency++;
	}

	
}
