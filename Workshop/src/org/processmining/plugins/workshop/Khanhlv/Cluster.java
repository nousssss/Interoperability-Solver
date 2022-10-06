package org.processmining.plugins.workshop.Khanhlv;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	private Centroid centroid;
	private List<Integer> listElement;
	
	public Cluster() {
		centroid = new Centroid();
		listElement = new ArrayList<Integer>();
	}
	
	public Cluster(Centroid centroid, List<Integer> listE) {
		this.centroid = centroid;
		this.listElement = listE;
	}

	public Centroid getCentroid() {
		return centroid;
	}

	public void setCentroid(Centroid centroid) {
		this.centroid = centroid;
	}

	public List<Integer> getListElement() {
		return listElement;
	}

	public void setListElement(List<Integer> listElement) {
		this.listElement = listElement;
	}
	
	public void addVector(Integer v) {
		listElement.add(v);
	}
}
