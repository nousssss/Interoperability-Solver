package org.processmining.plugins.workshop.Khanhlv;
/**
 * 
 * @author luu
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class VectorFrequence {
	private TraceLogs tracelogs;
	private Map<Integer,Trace> traceList;
	private Set<String> baseVector;
	private Map<Integer,List<Float>> vectorList;
	
	/**
	 * Constructor with parameter is a TraceLogs object,
	 * it will calculate binary vector for each trace. 
	 * 
	 * @param traceLogInput
	 */
	public VectorFrequence(TraceLogs traceLogInput) {
		this.tracelogs = traceLogInput;
		traceList = tracelogs.getListTrace();
		baseVector = tracelogs.getBaseVecto();
		vectorList = new TreeMap<Integer,List<Float>>();
		for(int i = 1; i <= traceList.size(); i++) {
			vectorList.put(i,createFrequence(traceList.get(i)));
		}
	}
	
	
	private int count(String event, Trace trace) {
		int count = 0;
		for(int index = 0; index < trace.listEvent().size(); index++) {
			if(trace.listEvent().get(index).equals(event))
				count++;
		}
		return count;
	}
	
	/*
	 * This function is private, its use to calculate frequency vector
	 * for a trace. This function will call by constructor.
	 * 
	 */
	private List<Float> createFrequence(Trace trace){
		List<Float> frequence = new ArrayList<Float>();
		Iterator<String> eventBase = baseVector.iterator();
		while(eventBase.hasNext()) {
			String e = eventBase.next();
			if(trace.listEvent().contains(e))
				frequence.add((float)count(e,trace));
//				frequence.add(1f);
			else
				frequence.add(0f);
		}
		return frequence;
	}
	
	/**
	 * 
	 * @return vectorList
	 */
	public Map<Integer,List<Float>> getVectorList(){
		return this.vectorList;
	}
}
