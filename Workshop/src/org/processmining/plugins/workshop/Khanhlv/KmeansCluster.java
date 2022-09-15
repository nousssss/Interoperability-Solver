package org.processmining.plugins.workshop.Khanhlv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KmeansCluster {
	private Map<Integer, List<Float>> listTrace;
	private int numberCluster;
	private Centroid[] listCentroid;
	private List<List<Integer>> listClusterOld;
	private Cluster[] listCluster;
	private boolean euclid = true;
	private boolean cosine;
	private int lengthVector;
	private int numLoop = 30;

	public KmeansCluster(Map<Integer, List<Float>> data, int numClus, int numLoop) {
		listTrace = data;
		numberCluster = numClus;
		this.numLoop = numLoop;
		lengthVector = data.get(1).size();
		listCentroid = new Centroid[numberCluster];
		listClusterOld = new ArrayList<List<Integer>>();
		for(int i = 0; i < numberCluster; i++) {
			listClusterOld.add(new ArrayList<Integer>());
		}
		listCluster = new Cluster[numberCluster];
		initializeCentroid();
	}

	/**
	 * 
	 * 
	 * @param data
	 * @param num
	 */
	public KmeansCluster(Map<Integer, List<Float>> data, int numClus) {
		listTrace = data;
		numberCluster = numClus;
		lengthVector = data.get(1).size();
		listCentroid = new Centroid[numberCluster];
		listClusterOld = new ArrayList<List<Integer>>();
		for(int i = 0; i < numberCluster; i++) {
			listClusterOld.add(new ArrayList<Integer>());
		}
		listCluster = new Cluster[numberCluster];
		initializeCentroid();
	}

	/**
	 * 
	 * 
	 * @param value
	 */
	public void setCosineDistance(boolean value) {
		if (value) {
			cosine = value;
			euclid = false;
		} else {
			cosine = false;
			euclid = true;
		}
	}

	public boolean getCosine() {
		return cosine;
	}

	public boolean getEuclid() {
		return euclid;
	}

	public Centroid[] getCentroidList() {
		return listCentroid;
	}

	public Cluster[] getCluster() {
		return listCluster;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public void cluster() {
		doCluster();
	}

	/**
	 * 
	 * 
	 */
	private void doCluster() {
		int i = 0;
		boolean check[] = new boolean[numberCluster];
		boolean result;
		for (; i < numLoop; i++) {
			result = check[0];
			for (int j = 1; j < numberCluster; j++) {
				result &= check[j];
			}
			if (result)
				return;
			if (i > 0)
				for (int j = 0; j < numberCluster; j++) {
					List<Integer> old = listClusterOld.get(j);
					List<Integer> cur = listCluster[j].getListElement();
					if (old.size() != cur.size())
						break;
					boolean temp = false;
					for (int k = 0; k < old.size(); k++) {
						if (old.get(k) != cur.get(k)) {
							temp = false;
							break;
						}
						temp = true;
					}
					check[j] = temp;
				}

			for (int count = 1; count <= listTrace.size(); count++) {
				int clus = assignCluster(listTrace.get(count));
				listCluster[clus].addVector(count);
			}
			if (i < numLoop - 1)
				calculateCentroid();
		}
	}

	/**
	 * 
	 * 
	 * @param vec
	 * @return
	 */
	private int assignCluster(List<Float> vec) {
		int clus = 0;
		double distance[] = new double[numberCluster];
		for (int i = 0; i < numberCluster; i++) {
			if (euclid)
				distance[i] = euclidDistance(vec, listCentroid[i].getValue());
			else
				distance[i] = cosDistance(vec, listCentroid[i].getValue());
		}
		double min = distance[0];
		for (int i = 1; i < numberCluster; i++) {
			if (distance[i] < min) {
				clus = i;
				min = distance[i];
			}
		}
		return clus;
	}

	/**
	 * 
	 * After each assign cluster, we must recalculate centroid with this function.
	 * 
	 */
	private void calculateCentroid() {

		for (int i = 0; i < numberCluster; i++) {
			//assign value cluster to list old cluster
			Cluster temp = listCluster[i];
			List<Integer> valueClus = temp.getListElement();
			List<Integer> tempOld = new ArrayList<Integer>();
			for (int in = 0; in < valueClus.size(); in++) {
				tempOld.add(valueClus.get(in));
			}
			listClusterOld.set(i, tempOld);

			if (valueClus.isEmpty())
				continue;
			List<Float> valueTemp = listTrace.get(valueClus.get(0));
			int size = temp.getListElement().size();

			//calculate value for new centroid
			for (int j = 0; j < lengthVector; j++) {
				for (int k = 1; k < size; k++) {
					valueTemp.set(j, valueTemp.get(j) + listTrace.get(valueClus.get(k)).get(j));
				}
				float av = (valueTemp.get(j)) / size;
				valueTemp.set(j, av);

			}
			temp.setCentroid(new Centroid(valueTemp));
			temp.setListElement(new ArrayList<Integer>());
			listCluster[i] = temp;
		}
	}

	/**
	 * Calculate distance between 2 vector with cosine distance.
	 * 
	 * @param trace1
	 * @param trace2
	 * @return distance
	 */
	private double cosDistance(List<Float> trace1, List<Float> trace2) {
		double distance = 0;
		double d0 = 0, d1 = 0, d2 = 0;
		for (int i = 0; i < lengthVector; i++) {
			d0 += trace1.get(i) * trace2.get(i);
			d1 += Math.pow(trace1.get(i), 2);
			d2 += Math.pow(trace2.get(i), 2);
		}
		distance = d0 / (Math.sqrt(d1) * Math.sqrt(d2));
		return distance;
	}

	/**
	 * Calculate distance between 2 vector with euclid distance.
	 * 
	 * @param trace1
	 * @param trace2
	 * @return distance
	 */
	private double euclidDistance(List<Float> trace1, List<Float> trace2) {
		double distance = 0;
		for (int i = 0; i < lengthVector; i++) {
			distance += Math.pow((trace1.get(i) - trace2.get(i)), 2);
		}
		return Math.sqrt(distance);
	}

	/**
	 * 
	 * 
	 * 
	 */
	private void initializeCentroid() {
		List<List<Float>> listV = new ArrayList<List<Float>>();
		for (int i = 1; i <= listTrace.size(); i++) {
			listV.add(listTrace.get(i));
		}

		for (int i = 0; i < numberCluster; i++) {
			int index = 0;
			for (int j = 0; j < i; j++) {
				while (listCentroid[j].getValue().toString().equals(listV.get(index).toString())) {
					index++;
				}
			}
			listCentroid[i] = new Centroid(listV.get(index));

			List<Integer> tempValue = new ArrayList<Integer>();
			tempValue.add(index + 1);
			listCluster[i] = new Cluster(listCentroid[i], tempValue);
		}
	}
}
