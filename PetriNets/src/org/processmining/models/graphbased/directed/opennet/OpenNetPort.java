package org.processmining.models.graphbased.directed.opennet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author hverbeek
 * 
 *         Open net port node. Holds inputs, outputs, and synchronous elements.
 * 
 *         We use an ExpandableSubNet for the port. Its elements will be
 *         displayed inside it.
 */
public class OpenNetPort extends HashSet<OpenNetLabel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2305942676028176926L;
	private final String label;
	private final String id;

	/**
	 * Constructs a port, given its label.
	 * 
	 * @param net
	 *            The given net.
	 * @param label
	 *            The given label.
	 */
	public OpenNetPort(String label, String id) {
		this.label = label;
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public Collection<OpenNetLabel> getLabels(OpenNetLabel.Type type) {
		ArrayList<OpenNetLabel> labels = new ArrayList<OpenNetLabel>(this);
		for (Iterator<OpenNetLabel> it = labels.iterator(); it.hasNext();) {
			if (!it.next().getType().equals(type)) {
				it.remove();
			}
		}
		return labels;
	}

	public String getId() {
		return id;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public boolean equals(Object o) {
		return (o instanceof OpenNetPort ? ((OpenNetPort) o).id.equals(id) : false);
	}
}
