/**
 * 
 */
package org.processmining.plugins.petrinet.invisiblemapper;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * @author Arya Adriansyah
 * @email a.adriansyah@tue.nl
 * @version Feb 17, 2011
 */
public class InvisibleTransMapperPanel extends JPanel {
	private static final long serialVersionUID = 3406856963492945923L;

	private List<Transition> transV = new LinkedList<Transition>();

	private List<JComboBox> eBoxes = new LinkedList<JComboBox>();

	private Object[] isInvi = new Object[] { "Invisible", "Not invisible" };

	public InvisibleTransMapperPanel(PetrinetGraph net) {
		super();
		//Factory to create ProM swing components
		SlickerFactory factory = SlickerFactory.instance();

		//Setting the Layout (table of 2 columns and N rows)
		setLayout(new GridLayout(0, 2));

		//Setting the "table"
		add(factory.createLabel("Transition"));
		add(factory.createLabel("Is invisible?"));

		// regular expression for invisible transitions
		String invisibleTransitionRegEx = "[a-z][0-9]+|(tr[0-9]+)|(n[0-9]+)|(silent)|(tau)|(skip)|(invi)";
		Pattern pattern = Pattern.compile(invisibleTransitionRegEx);
		List<Transition> listTrans = new ArrayList<Transition>(net.getTransitions());
		Collections.sort(listTrans, new Comparator<Transition>() {
			public int compare(Transition o1, Transition o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		});
		for (Transition transition : listTrans) {
			//Add the transition in that position to the vector
			transV.add(transition);

			//Create a Label with the name of the Transition
			add(factory.createLabel(transition.getLabel()));

			//Create, store, and show the box of the events for that transition
			JComboBox boxE = factory.createComboBox(isInvi);
			boxE.setSelectedIndex(transition.isInvisible() ? 0 : 1);
			if (!transition.isInvisible()) {
				// by default, suggest invisible if the name of transitions:
				String lowCase = transition.getLabel().toLowerCase();
				Matcher matcher = pattern.matcher(lowCase);
				if(matcher.find() && matcher.start()==0){
					boxE.setSelectedIndex(0);
				}
			}
			eBoxes.add(boxE);
			add(boxE);
		}
	}

	public Set<Transition> getInviTransitions() {
		Set<Transition> res = new HashSet<Transition>();
		Iterator<JComboBox> it = eBoxes.iterator();
		for (Transition t : transV) {
			if (it.next().getSelectedIndex() == 0) {
				res.add(t);
			}
		}
		return res;
	}
}
