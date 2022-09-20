/**
 * 
 */
package org.processmining.plugins.petrinet.finalmarkingprovider;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.collection.AlphanumComparator;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * @author aadrians Nov 24, 2011
 * 
 */
public class MarkingEditorPanel extends JPanel {
	private static final long serialVersionUID = 8759205978114685628L;

	// GUI
	private ProMList placeList;
	private DefaultListModel placeListMdl;

	private ProMList candidateMarkings;
	private DefaultListModel candidateMarkingsMdl;

	private JButton addPlacesBtn;
	private JButton removePlacesBtn;

	private String EMPTYMARKING = "<Empty Marking>";
	private String type = "Marking";

	public MarkingEditorPanel(String type) {
		this.type = type;
	}

	public Marking getMarking(UIPluginContext context, PetrinetGraph net) {
		init(net);

		// init result variable
		InteractionResult result = context.showWizard("Select mapping", true, true, this);

		// configure interaction with user
		if (result == InteractionResult.FINISHED) {
			Marking newMarking = new Marking();
			if (candidateMarkingsMdl.size() > 1) {
				Enumeration<?> elements = candidateMarkingsMdl.elements();
				while (elements.hasMoreElements()) {
					newMarking.add((Place) elements.nextElement());
				}
			} else {
				if (!EMPTYMARKING.equals(candidateMarkingsMdl.elementAt(0))) {
					newMarking.add((Place) candidateMarkingsMdl.elements().nextElement());
				}
			}
			return newMarking;
		}
		return null;
	}

	private void init(PetrinetGraph net) {
		// factory 
		SlickerFactory factory = SlickerFactory.instance();

		// place selection
		placeListMdl = new DefaultListModel();
		Set<Place> places = new TreeSet<Place>(new Comparator<Place>() {
			private AlphanumComparator comp = new AlphanumComparator();

			public int compare(Place o1, Place o2) {
				return comp.compare(o1.getLabel(), o2.getLabel());
			}
		});
		places.addAll(net.getPlaces());
		for (Place p : places) {
			placeListMdl.addElement(p);
		}
		placeList = new ProMList("List of Places", placeListMdl);
		placeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// add places
		candidateMarkingsMdl = new DefaultListModel();
		candidateMarkingsMdl.addElement(EMPTYMARKING);
		candidateMarkings = new ProMList("Candidate " + type, candidateMarkingsMdl);

		addPlacesBtn = factory.createButton("Add Place >>");
		addPlacesBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!placeList.getSelectedValuesList().isEmpty()) {
					if ((candidateMarkingsMdl.size() == 1) && (candidateMarkingsMdl.elementAt(0).equals(EMPTYMARKING))) {
						candidateMarkingsMdl.removeAllElements();
					}
					candidateMarkingsMdl.addElement(placeList.getSelectedValuesList().get(0));
				}
			}
		});

		removePlacesBtn = factory.createButton("<< Remove Place");
		removePlacesBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (Object obj : candidateMarkings.getSelectedValuesList()) {
					candidateMarkingsMdl.removeElement(obj);
					if (candidateMarkingsMdl.size() == 0) {
						candidateMarkingsMdl.addElement(EMPTYMARKING);
					}
				}
			}
		});

		// commit marking
		//		commitedMarkingsMdl = new DefaultListModel();
		//		commitedMarkings = new ProMList("Committed Final Markings", commitedMarkingsMdl);

		//		commitMarkingBtn = factory.createButton("Commit Final Marking");
		//		commitMarkingBtn.addActionListener(new ActionListener() {
		//			
		//			public void actionPerformed(ActionEvent e) {
		//				if (!candidateMarkingsMdl.isEmpty()){
		//					Marking newMarking = new Marking();
		//					Enumeration<?> elements = candidateMarkingsMdl.elements();
		//					while (elements.hasMoreElements()){
		//						newMarking.add((Place) elements.nextElement());
		//					}
		//					// add the marking to committed marking
		//					commitedMarkingsMdl.addElement(newMarking);
		//				}
		//				// reset all candidate
		//				candidateMarkingsMdl.removeAllElements();
		//			}
		//		});

		//		removeMarkingBtn = factory.createButton("Remove selected final marking(s)");
		//		removeMarkingBtn.addActionListener(new ActionListener(){
		//			public void actionPerformed(ActionEvent e) {
		//				for (Object obj : commitedMarkings.getSelectedValues()){
		//					commitedMarkingsMdl.removeElement(obj);
		//				};
		//			}
		//		});

		// now add the elements
		//		double[][] size = new double[][]{ {250,10,200,10,250},{ 125, 45, 125, 25, 125, 25} };
		double[][] size = new double[][] { { 250, 10, 200, 10, 250 }, { TableLayout.FILL, 30, 5, 30, TableLayout.FILL } };
		TableLayout layout = new TableLayout(size);
		setLayout(layout);
		add(placeList, "0,0,0,4");
		add(addPlacesBtn, "2,1");
		add(removePlacesBtn, "2,3");
		add(candidateMarkings, "4,0,4,4");

		//		add(commitMarkingBtn, "4,3");
		//		add(commitedMarkings, "0,4,4,4");
		//		add(removeMarkingBtn, "4,5");

	}

}
