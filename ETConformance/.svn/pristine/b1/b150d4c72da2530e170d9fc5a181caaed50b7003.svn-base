package org.processmining.plugins.multietc.reflected;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public class ReflectedTrace extends ArrayList<Transition>{

	/** */
	private static final long serialVersionUID = 7172196065685062195L;

	public static final String WEIGHT = "weight";

	Map<Object,Object> attr;
	
	public ReflectedTrace(){
		super();
		attr = new HashMap<Object,Object>();
	}
	
 
	public Object putAttribute(Object key, Object value){
		return attr.put(key, value);
	}
	
	public Object getAttribute(Object key){
		return attr.get(key);
	}
	
	public double getWeight(){
		return (Double) attr.get(WEIGHT);
	}
	
	public Object putWeight(double w){
		return attr.put(WEIGHT,w);
	}

}
