package org.processmining.models.workshop.sjjleemans.ProcessTree.mining;

import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.models.workshop.sjjleemans.ProcessTree.model.ProcessTreeModel.Operator;

public class Possibility {
	private Operator operator;
	
	private Set<XEventClass> activitiesLeft;
	private Filteredlog logLeft;
	
	private Set<XEventClass> activitiesRight;
	private Filteredlog logRight;
	
	private Filteredlog log;
	
	public Possibility(
			Operator operator, 
			Set<XEventClass> activitiesLeft, 
			Set<XEventClass> activitiesRight, 
			Filteredlog log) {
		this.operator = operator;
		
		//compute the filtered log for the left branch
		this.activitiesLeft = activitiesLeft;
		
		this.activitiesRight = activitiesRight;
		
		this.log = log;
		
		System.out.println(toString());
	}
	
	public String toString() {
		//output debug information
		String x;
		if (getActivitiesRight() != null) {
			x = "{" + implode(getActivitiesLeft(), ", ") + "} {" + implode(getActivitiesRight(), ", ") + "}";
		} else {
			x = "{" + implode(getActivitiesLeft(), ", ") + "}";
		}
		switch (operator) {
			case ACTIVITY :
				return "possibility: activity " + x;
			case EXCLUSIVE_CHOICE :
				return "possibility: exclusive choice " + x;
			case LOOP :
				return "possibility: loop " + x;
			case LOOP_FLOWER :
				return "possibility: flower loop " + x;
			case PARALLEL :
				return "possibility: parallel " + x;
			case SEQUENTIAL :
				return "possibility: sequential " + x;
			default :
				return "not implemented";
		}
	}

	public Operator getOperator() {
		return operator;
	}

	public Set<XEventClass> getActivitiesLeft() {
		return activitiesLeft;
	}

	public Filteredlog getLogLeft() {
		if (logLeft == null) {
			logLeft = log.applyFilter(operator, activitiesLeft);
		}
		return logLeft;
	}

	public Set<XEventClass> getActivitiesRight() {
		return activitiesRight;
	}

	public Filteredlog getLogRight() {
		if (logRight == null) {
			logRight = log.applyFilter(operator, activitiesRight);
		}
		return logRight;
	}
	
	public static String implode(Set<XEventClass> input, String glueString) {
		String output = "";
		boolean first = true;
		if (input.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (XEventClass e : input) {
				if (first) {
					first = false;
				} else {
					sb.append(glueString);
				}
				sb.append(e.toString());
			}
			output = sb.toString();
		}
		return output;
	}
}
