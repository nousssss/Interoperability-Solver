package org.processmining.models.graphbased.directed.petrinetwithdata.unfolding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.graphbased.directed.petrinetwithdata.DataElement;
import org.processmining.models.graphbased.directed.petrinetwithdata.Literal;
import org.processmining.models.graphbased.directed.petrinetwithdata.PetriNetWithData;
import org.processmining.models.graphbased.directed.petrinetwithdata.Predicate;

public class Unfolder {
	PetriNetWithData pnd;
	Petrinet pn; //unfolded net

	//Maps that connect the PND with its unfolding
	Map<Transition, Transition> tran_src_map;
	Map<Place, Place> place_map;
	Map<DataElement, Place> data_def_place_map;
	Map<DataElement, Place> data_ndef_place_map;
	Map<Predicate, Place> pred_true_place_map;
	Map<Predicate, Place> pred_false_place_map;

	public Unfolder(PetriNetWithData pnd) {
		this.pnd = pnd;
		pn = null;
		tran_src_map = new HashMap<Transition, Transition>();
		place_map = new HashMap<Place, Place>();
		data_def_place_map = new HashMap<DataElement, Place>();
		data_ndef_place_map = new HashMap<DataElement, Place>();
		pred_true_place_map = new HashMap<Predicate, Place>();
		pred_false_place_map = new HashMap<Predicate, Place>();
	}

	Set<DataEffect> getDataEffects(Transition t, DataElement d) {
		Set<DataEffect> effects = new HashSet<DataEffect>();

		if (pnd.isReading(t, d) && pnd.isDestroying(t, d)) {
			effects.add(new DataEffect(d, "TB"));
		} else if (pnd.isReading(t, d) && !pnd.isDestroying(t, d)) {
			effects.add(new DataEffect(d, "TT"));
		} else if (!pnd.isReading(t, d) && pnd.isDestroying(t, d)) {
			effects.add(new DataEffect(d, "TB"));
			effects.add(new DataEffect(d, "BB"));
		} else if (pnd.isWriting(t, d) && !(pnd.isDestroying(t, d) || pnd.isReading(t, d))) {
			effects.add(new DataEffect(d, "TT"));
			effects.add(new DataEffect(d, "BT"));
		} else {
			effects.add(new DataEffect(d, "-"));
		}

		return effects;
	}

	Set<PredicateEffect> getPredicateEffects(Transition t, Predicate p, Set<Literal> conj) {
		Set<PredicateEffect> effects = new HashSet<PredicateEffect>();

		Set<DataElement> w = new HashSet<DataElement>(pnd.writing_data.get(t));
		Set<DataElement> d = new HashSet<DataElement>(pnd.destroying_data.get(t));
		Set<DataElement> lp = new HashSet<DataElement>(p.getDepData());

		Set<DataElement> w_or_d_cap_lp = new HashSet<DataElement>(w);
		w_or_d_cap_lp.addAll(d);
		w_or_d_cap_lp.retainAll(lp);

		Set<DataElement> w_minus_d_cap_lp = new HashSet<DataElement>(w);
		w_minus_d_cap_lp.removeAll(d);
		w_minus_d_cap_lp.retainAll(lp);

		Set<DataElement> d_cap_lp = new HashSet<DataElement>(d);
		d_cap_lp.retainAll(lp);

		boolean p_in_conj = false;
		boolean neg_p_in_conj = false;

		for (Literal l : conj) {
			if (l.getPred().equals(p)) {
				if (!l.isNegated()) {
					p_in_conj = true;
				} else {
					neg_p_in_conj = true;
				}
			}
		}

		if (w_or_d_cap_lp.isEmpty() && !p_in_conj && !neg_p_in_conj) {
			effects.add(new PredicateEffect(p, conj, "-"));
		} else if (w_or_d_cap_lp.isEmpty() && p_in_conj) {
			effects.add(new PredicateEffect(p, conj, "TT"));
		} else if (w_or_d_cap_lp.isEmpty() && neg_p_in_conj) {
			effects.add(new PredicateEffect(p, conj, "FF"));
		} else if (!w_minus_d_cap_lp.isEmpty() && !p_in_conj && !neg_p_in_conj) {
			effects.add(new PredicateEffect(p, conj, "TF"));
			effects.add(new PredicateEffect(p, conj, "FT"));
			effects.add(new PredicateEffect(p, conj, "-"));
		} else if (!w_minus_d_cap_lp.isEmpty() && p_in_conj) {
			effects.add(new PredicateEffect(p, conj, "TT"));
			effects.add(new PredicateEffect(p, conj, "TF"));
		} else if (!w_minus_d_cap_lp.isEmpty() && neg_p_in_conj) {
			effects.add(new PredicateEffect(p, conj, "FT"));
			effects.add(new PredicateEffect(p, conj, "FF"));
		} else if (!d_cap_lp.isEmpty() && !p_in_conj && !neg_p_in_conj) {
			effects.add(new PredicateEffect(p, conj, "TF"));
			effects.add(new PredicateEffect(p, conj, "FF"));
		} else if (!d_cap_lp.isEmpty() && p_in_conj) {
			effects.add(new PredicateEffect(p, conj, "TF"));
		} else if (!d_cap_lp.isEmpty() && neg_p_in_conj) {
			effects.add(new PredicateEffect(p, conj, "FF"));
		} else {
			System.out.println("Something is wrong");
		}

		return effects;
	}

	public Set<Set<Effect>> getOverallEffects(Transition t) {

		Set<Set<Effect>> result = new HashSet<Set<Effect>>();
		result.add(new HashSet<Effect>());

		for (DataElement d : pnd.getDataElements()) {
			Set<DataEffect> eff_t_d = getDataEffects(t, d);
			for (Set<Effect> r : new HashSet<Set<Effect>>(result)) {
				for (Effect de : eff_t_d) {
					Set<Effect> temp = new HashSet<Effect>(r);
					temp.add(de);
					result.add(temp);
				}
				result.remove(r);
			}
		}

		for (Set<Literal> conj : pnd.getGuards().get(t).getConjuncts()) {
			for (Predicate p : pnd.getPredicates()) {
				Set<PredicateEffect> eff_t_p = getPredicateEffects(t, p, conj);
				for (Set<Effect> r : new HashSet<Set<Effect>>(result)) {
					for (Effect pe : eff_t_p) {
						Set<Effect> temp = new HashSet<Effect>(r);
						temp.add(pe);
						result.add(temp);
					}
					result.remove(r);
				}
			}
		}
		return result;
	}

	public void unfold() {
		Petrinet pn = PetrinetFactory.newPetrinet("Unfolded_" + pnd.getLabel());

		//Add normal places
		for (Place p : pnd.getPlaces()) {
			Place new_p = pn.addPlace(p.getLabel());
			place_map.put(p, new_p);
		}

		//Add data places
		for (DataElement d : pnd.getDataElements()) {
			Place p_d_def = pn.addPlace("p_" + d.getValue() + "T");
			data_def_place_map.put(d, p_d_def);
			Place p_d_ndef = pn.addPlace("p_" + d.getValue() + "B");
			data_ndef_place_map.put(d, p_d_ndef);
		}

		//Add predicate places
		for (Predicate p : pnd.getPredicates()) {
			Place p_true = pn.addPlace("p_" + p.getName() + "T");
			pred_true_place_map.put(p, p_true);
			Place p_false = pn.addPlace("p_" + p.getName() + "F");
			pred_false_place_map.put(p, p_false);
		}

	}

}
