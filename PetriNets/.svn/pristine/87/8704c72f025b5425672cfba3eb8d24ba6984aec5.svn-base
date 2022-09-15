package org.processmining.models.graphbased.directed.petrinetwithdata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;

public class PetriNetWithData extends PetrinetImpl {

	Set<DataElement> data_elements;
	Set<Predicate> pred_set;

	Map<Transition, Set<DataElement>> reading_data;
	public Map<Transition, Set<DataElement>> writing_data;
	public Map<Transition, Set<DataElement>> destroying_data;
	Map<Transition, Guard> guards;

	void initializeDataOperations() {
		reading_data = new HashMap<Transition, Set<DataElement>>();
		writing_data = new HashMap<Transition, Set<DataElement>>();
		destroying_data = new HashMap<Transition, Set<DataElement>>();

		//Assign empty set of data operations for every transition
		for (Transition t : getTransitions()) {
			assignDataOperation(t, new HashSet<DataElement>(), "R");
			assignDataOperation(t, new HashSet<DataElement>(), "W");
			assignDataOperation(t, new HashSet<DataElement>(), "D");
		}
	}

	void initializeGuards() {
		guards = new HashMap<Transition, Guard>();

		for (Transition t : getTransitions()) {
			assignGuard(t, new Guard());
		}
	}

	public PetriNetWithData(String label, Set<DataElement> data_elements, Set<Predicate> pred_set) {
		super(label);

		this.data_elements = new HashSet<DataElement>(data_elements);

		//for predicate data take only what is in the set of data elements
		for (Predicate p : pred_set) {
			p.getDepData().retainAll(data_elements);
		}

		this.pred_set = new HashSet<Predicate>(pred_set);

		initializeDataOperations();
		initializeGuards();

	}

	public void assignDataOperation(Transition t, Set<DataElement> data_elements, String type) {
		//for data operations take only what is in the set of data elements
		data_elements.retainAll(this.data_elements);

		if (type.equals("R")) {
			reading_data.put(t, data_elements);
		} else if (type.equals("W")) {
			writing_data.put(t, data_elements);
		} else if (type.equals("D")) {
			destroying_data.put(t, data_elements);
		}
	}

	public void setVisualizationLabels() {

		for (Transition t : getTransitions()) {
			t.getAttributeMap().put(
					AttributeMap.TOOLTIP,
					"<html>" + t.getLabel() + "<br>" + "GRD: " + guards.get(t).toString() + "<br>" + "R: "
							+ reading_data.get(t) + "<br>" + "W: " + writing_data.get(t) + "<br>" + "D: "
							+ destroying_data.get(t) + "<html>");
		}
	}

	public boolean isReading(Transition t, DataElement d) {
		return (reading_data.get(t).contains(d) || guards.get(t).dataOf().contains(d))

		;
	}

	public boolean isWriting(Transition t, DataElement d) {
		return writing_data.get(t).contains(d);
	}

	public boolean isDestroying(Transition t, DataElement d) {
		return destroying_data.get(t).contains(d);
	}

	public void assignGuard(Transition t, Guard grd) {
		guards.put(t, grd);
	}

	public Set<DataElement> getDataElements() {
		return data_elements;
	}

	public void showDataElements() {
		System.out.println(data_elements);
	}

	public void showDataOperations() {
		for (Transition t : getTransitions()) {
			System.out.println("R" + "(" + t.getLabel() + ") = " + reading_data.get(t));
			System.out.println("W" + "(" + t.getLabel() + ") = " + writing_data.get(t));
			System.out.println("D" + "(" + t.getLabel() + ") = " + destroying_data.get(t));
		}

	}

	public void showGuards() {
		System.out.println(guards);
	}

	public Map<Transition, Guard> getGuards() {
		return guards;
	}

	public Set<Predicate> getPredicates() {

		return pred_set;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataElement a = new DataElement("a");
		DataElement b = new DataElement("b");
		DataElement c = new DataElement("c");

		Set<DataElement> data_elements = new HashSet<DataElement>();
		data_elements.add(a);
		data_elements.add(b);
		data_elements.add(c);

		Set<DataElement> data_of_p = new HashSet<DataElement>();
		data_of_p.add(a);
		Predicate p = new Predicate("p", data_of_p);

		Set<DataElement> data_of_q = new HashSet<DataElement>();
		data_of_q.add(c);
		Predicate q = new Predicate("q", data_of_q);

		Set<Predicate> pred_set = new HashSet<Predicate>();
		pred_set.add(p);
		pred_set.add(q);

		//Set-up net
		PetriNetWithData pnd = new PetriNetWithData("PND1", data_elements, pred_set);

		Place start = pnd.addPlace("start");
		Place end = pnd.addPlace("end");
		Transition t = pnd.addTransition("t");
		pnd.addArc(start, t);
		pnd.addArc(t, end);

		//Set-up data operations
		Set<DataElement> r_data_t = new HashSet<DataElement>();
		r_data_t.add(a);
		pnd.assignDataOperation(t, r_data_t, "R");

		Set<DataElement> w_data_t = new HashSet<DataElement>();
		w_data_t.add(b);
		pnd.assignDataOperation(t, w_data_t, "W");

		Set<DataElement> d_data_t = new HashSet<DataElement>();
		d_data_t.add(c);
		pnd.assignDataOperation(t, d_data_t, "D");

		//Set-up guards
		Literal p_lit = new Literal(p, false);
		Literal not_q_lit = new Literal(q, true);

		Set<Literal> c1 = new HashSet<Literal>();
		c1.add(p_lit);
		Set<Literal> c2 = new HashSet<Literal>();
		c2.add(not_q_lit);

		Guard grd_t = new Guard();
		grd_t.addConjunct(c1);
		grd_t.addConjunct(c2);

		pnd.assignGuard(t, grd_t);

		pnd.showDataElements();
		pnd.showDataOperations();
		pnd.showGuards();

		System.out.println(t.getId());

	}
}
