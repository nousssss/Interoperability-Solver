package org.processmining.models.graphbased.directed.opennet;

import java.util.HashSet;

public class OpenNetInterface extends HashSet<OpenNetPort> {

	private static final long serialVersionUID = 5482643409002017741L;

	public OpenNetLabel findLabel(String id) {
		for (OpenNetPort port : this) {
			for (OpenNetLabel label : port) {
				if (label.getId().equals(id)) {
					return label;
				}
			}
		}
		return null;
	}

}
