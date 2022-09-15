/**
 * 
 */
package org.processmining.plugins.multietc.sett;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jmunoz
 *
 */
public class MultiETCSettings {
	
	public enum Algorithm {ETC, ALIGN_1, ALIGN_REPRE, ALIGN_ALL}
	public enum Representation {ORDERED, UNORDERED}
	public enum Window {BACKWARDS, FORWARDS}
	
	public static final String ALGORITHM = "Algorithm";
	public static final String REPRESENTATION = "Representation";
	public static final String WINDOW = "Window";
	
	Map<Object,Object> sett;
	
	public MultiETCSettings(){
		sett = new HashMap<Object,Object>();
	}
	
	public Object put(Object key, Object value){
		return sett.put(key, value);
	}
	
	public Object get(Object key){
		return sett.get(key);
	}
	
	public Algorithm getAlgorithm(){
		return (Algorithm) sett.get(ALGORITHM);
	}
	
	public Representation getRepresentation(){
		return (Representation) sett.get(REPRESENTATION);
	}
	
	public Window getWindow(){
		return (Window) sett.get(WINDOW);
	}
	
	public void setWindow(Window w){
		sett.put(WINDOW,w);
	}
	

}
