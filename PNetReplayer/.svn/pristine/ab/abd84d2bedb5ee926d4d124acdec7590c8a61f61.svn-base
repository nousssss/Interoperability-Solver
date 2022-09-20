/**
 * 
 */
package org.processmining.plugins.petrinet.manifestreplayer.transclassifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * @author aadrians
 * Feb 19, 2012
 *
 */
public class TransClasses {
	
	/**
	 * The classifier used for creating the set of event classes.
	 */
	protected ITransClassifier classifier;
	
	/**
	 * Map holding the event classes, indexed by their unique identifier string.
	 */
	protected Map<String, TransClass> classMap;
	
	public TransClasses(PetrinetGraph net){
		init(net, new DefTransClassifier());
	}
	
	public TransClasses(PetrinetGraph net, ITransClassifier classifier){
		init(net, classifier);
	}
	
	private void init(PetrinetGraph net, ITransClassifier classifier) {
		this.classifier = classifier;
		this.classMap = new HashMap<String, TransClass>();
		
		// iterate through transitions
		for (Transition t : net.getTransitions()){
			String id = this.classifier.getClassIdentity(t);
			if (classMap.get(id) == null){
				// create new class
				classMap.put(id, new TransClass(id));
			}
		}
	}
	
	/**
	 * Return ordered set of all trans classes
	 * @return
	 */
	public Collection<TransClass> getTransClasses(){
		Set<TransClass> treeClasses = new TreeSet<TransClass>(classMap.values());
		List<TransClass> list = new ArrayList<TransClass>(treeClasses);
		Collections.sort(list, new Comparator<TransClass>(){

			public int compare(TransClass o1, TransClass o2) {
				return o1.getId().compareTo(o2.getId());
			}
			
		});
		return treeClasses;
	}

	/**
	 * return the transClass of a transition
	 * @param t
	 * @return
	 */
	public TransClass getClassOf(Transition t){
		return classMap.get(classifier.getClassIdentity(t));
	}

}
