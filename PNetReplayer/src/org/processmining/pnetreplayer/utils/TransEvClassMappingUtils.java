package org.processmining.pnetreplayer.utils;

import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.log.utils.XUtils;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;

public class TransEvClassMappingUtils {

	private static double BESTTHRESHOLD = 0.9;
	private static double SECONDBESTTHRESHOLD = 0.05;
	private XEventClass invisibleActivity;
	private static TransEvClassMappingUtils instance = null;

	private TransEvClassMappingUtils() {
	}
	
	public static TransEvClassMappingUtils getInstance() {
		if (instance == null) {
			instance = new TransEvClassMappingUtils();
		}
		return instance;
	}
	
	public void setInvisibleActivity(XEventClass invisibleActivity) {
		this.invisibleActivity = invisibleActivity;
	}

	public XEventClass getInvisibleActivity() {
		return invisibleActivity;
	}

	public TransEvClassMapping getMapping(Petrinet net,
			Set<XEventClass> activities, XEventClassifier classifier) {
		invisibleActivity = XUtils.INVISIBLEACTIVITY;
		TransEvClassMapping mapping = new TransEvClassMapping(classifier, invisibleActivity);
		for (Transition transition : net.getTransitions()) {
			mapping.put(transition, invisibleActivity);
			for (XEventClass activity : activities) {
				if (activity.getId().equals(transition.getLabel())) {
					mapping.put(transition, activity);
				}
			}
		}
		for (Transition transition : mapping.keySet()) {
			if (mapping.get(transition).equals(invisibleActivity)) {
				if (activities.size() == 1) {
					XEventClass activity = activities.iterator().next();
					if (match(transition.getLabel(), activity.getId()) > BESTTHRESHOLD) {
						mapping.put(transition, activity);
					}
				} else if (activities.size() > 1){
					double bestMatch = 0.0;
					double secondBestMatch = 0.0;
					XEventClass bestActivity = activities.iterator().next();
					for (XEventClass activity : activities) {
						double match = match(transition.getLabel(), activity.getId());
//						System.out.println(transition.getLabel() + " " + activity.getId() + " " + match);
						if (match > bestMatch) {
							secondBestMatch = bestMatch;
							bestMatch = match;
							bestActivity = activity;
						} else if (match > secondBestMatch) {
							secondBestMatch = match;
						}
					}
//					System.out.println(transition.getLabel() + " " + bestMatch + " " + secondBestMatch);
					if (bestMatch > BESTTHRESHOLD && bestMatch - secondBestMatch > SECONDBESTTHRESHOLD) {
						mapping.put(transition, bestActivity);
					}
				}
			}
		}
		return mapping;
	}

	private int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	private int computeLevenshteinDistance(CharSequence str1, CharSequence str2) {
		int[][] distance = new int[str1.length() + 1][str2.length() + 1];

		for (int i = 0; i <= str1.length(); i++)
			distance[i][0] = i;
		for (int j = 1; j <= str2.length(); j++)
			distance[0][j] = j;

		for (int i = 1; i <= str1.length(); i++)
			for (int j = 1; j <= str2.length(); j++)
				distance[i][j] = minimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1, distance[i - 1][j - 1]
						+ ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));

		return distance[str1.length()][str2.length()];
	}

	private double match(CharSequence str1, CharSequence str2) {
		return 1.0 - ((1.0 * computeLevenshteinDistance(str1, str2)) / (str1.length() + str2.length()));
	}

}
