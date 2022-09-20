package org.processmining.models.workshop.fbetancor.constructors;

import java.util.List;

public class OutputDefinition {
	private String EventlogName;
	private List<Output> OutputList;
	private String OutputScore;

	public OutputDefinition(String eventlogName, List<Output> outputList, String outputScore) {
		EventlogName = eventlogName;
		OutputList = outputList;
		OutputScore = outputScore;
	}

	public void setEventlogName(String eName) {
		EventlogName = eName;
	}

	public void setOutputList(List<Output> outputL) {
		OutputList = outputL;
	}

	public void setOutputScore(String score) {
		OutputScore = score;
	}

	public String getEventlogName() {
		return EventlogName;
	}

	public List<Output> getOutputList() {
		return OutputList;
	}

	public String getOutputScore() {
		return OutputScore;
	}

}
