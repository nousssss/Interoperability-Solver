package org.processmining.plugins.workshop.Khanhlv;

import java.util.List;

public class Centroid {
	
	
	private List<Float> value;
	
	public Centroid() {
		
	}
	
	public Centroid(List<Float> value) {
		this.value = value;
		
	}
	
	public List<Float> getValue(){
		return value;
	}
	
	public void setValue(List<Float> value) {
		this.value = value;
	}
	
}
