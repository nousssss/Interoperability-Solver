package org.processmining.plugins.workshop.Khanhlv;

import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

public class ClusteringEventLog {

	@Plugin(
			name = "Clustering Event log",
			parameterLabels = {"Event log"},
			returnLabels = { "output"},
			returnTypes = {String.class},
			userAccessible = true,
			help = "Plugin for cluster event log with distance graph by Kmeans")

	@UITopiaVariant(affiliation = "UET-KTLAB", author = "Khanhlv", email = "0915809219a@gmail.com")

	public static String Hello(PluginContext context, XLog log) {
		TraceLogs traceLogs1 = new TraceLogs(log, 1);
		TraceLogs traceLogs2 = new TraceLogs(log, 2);
		TraceLogs traceLogs3 = new TraceLogs(log, 3);
		
		int clus = 3;
		String result = "<html><body>";
		
		VectorFrequence vectorFrequence = new VectorFrequence(traceLogs1);
		Map<Integer,List<Float>> v = vectorFrequence.getVectorList();
		
		KmeansCluster k = new KmeansCluster(v,clus);
//		k.setCosineDistance(true);
		k.cluster();
		result += "Distance graph 1:<br>";
		Cluster[] ex = k.getCluster();
		for(int i = 0; i < ex.length; i++) {
			result += "Cluster "+ (i) + ": " + ex[i].getListElement().size() + "<br>";
			System.out.println(ex[i].getListElement().toString());
			System.out.println(ex[i].getListElement().size());
			String filename = "cluster_1_" + (new Integer((i+1))).toString() + ".mxml";
			ExportToFile exp = new ExportToFile(filename,ex[i],log);
			exp.export();
		}
		System.out.println("=====================================================================");
		
		vectorFrequence = new VectorFrequence(traceLogs2);
		v = vectorFrequence.getVectorList();
		KmeansCluster k2 = new KmeansCluster(v,clus);
		k2.cluster();
		result += "Distance graph 2:<br>";
		Cluster[] ex2 = k2.getCluster();
		for(int i = 0; i < ex2.length; i++) {
			result += "Cluster "+ (i+1) + ": " + ex2[i].getListElement().size() + "<br>";
			System.out.println(ex2[i].getListElement().toString());
			System.out.println(ex2[i].getListElement().size());
			String filename = "cluster_2_" + (new Integer((i+1))).toString() + ".mxml";
			ExportToFile exp = new ExportToFile(filename,ex2[i],log);
			exp.export();
		}
		System.out.println("=====================================================================");
		
		
		vectorFrequence = new VectorFrequence(traceLogs3);
		v = vectorFrequence.getVectorList();
		KmeansCluster k3 = new KmeansCluster(v,clus);
		k3.cluster();
		result += "Distance graph 3:<br>";
		Cluster[] ex3 = k3.getCluster();
		for(int i = 0; i < ex3.length; i++) {
			result += "Cluster "+ (i) + ": " + ex3[i].getListElement().size() + "<br>";
			System.out.println(ex3[i].getListElement().toString());
			System.out.println(ex3[i].getListElement().size());
			String filename = "cluster_3_" + (new Integer((i+1))).toString() + ".mxml";
			ExportToFile exp = new ExportToFile(filename,ex3[i],log);
			exp.export();
		}
		System.out.println("=====================================================================");
		
		
		return result;
	}
}
