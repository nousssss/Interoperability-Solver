package org.processmining.models.semantics.petrinet;

import java.util.Collection;

import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.ExecutionInformation;
import org.processmining.models.semantics.IllegalTransitionException;

/**
 * Petrinet semantics designed with good performance characteristics avoiding
 * object creation overhead. Therefore, multiple additional methods are added,
 * to allow for direct access and manipulation of the data structures.
 * <p>
 * The original
 * {@link org.processmining.models.semantics.petrinet.PetrinetSemantics}
 * interface is also supported, but using methods of this interface might not
 * lead to the best performance.
 * 
 * @author F. Mannhardt
 * @author S.J. van Zelst
 *
 */
public interface EfficientPetrinetSemantics extends PetrinetSemantics {

	/**
	 * Visitor style interface to iterate through places
	 */
	public interface PlaceVisitor {
		void accept(Place place, int numTokens);
	}

	/**
	 * Executes (fires) a transition. For performance reasons, this method does
	 * not check whether the transition is actually enabled and does not return
	 * information on what changed.
	 * 
	 * @param transition
	 */
	void directExecuteExecutableTransition(Transition transition);

	/**
	 * @param marking1
	 * @param marking2
	 * @return whether marking2 is equal to marking1
	 */
	boolean equalMarking(byte[] marking1, Marking marking2);

	/**
	 * @param marking1
	 * @param marking2
	 * @return whether marking2 has equal or less tokens on each place than
	 *         marking1
	 */
	boolean equalOrLessMarking(byte[] marking1, Marking marking2);

	/**
	 * For better performance, use method
	 * {@link #directExecuteExecutableTransition(Transition)} instead.
	 * 
	 * @param toExecute
	 * @return
	 */
	ExecutionInformation executeExecutableTransition(Transition toExecute) throws IllegalTransitionException;

	/**
	 * Visits each place that is marked with one or more tokens.
	 * 
	 * @param placeVisitor
	 */
	void forEachMarkedPlace(PlaceVisitor placeVisitor);

	/**
	 * Visits each place.
	 * 
	 * @param placeVisitor
	 */
	void forEachPlace(PlaceVisitor placeVisitor);

	/**
	 * For better performance, use method {@link #isEnabled(Transition)}
	 * instead.
	 * 
	 * @return a collection of all enabled transitions
	 */
	Collection<Transition> getExecutableTransitions();

	/**
	 * 
	 * @param place
	 * @return the index used for this place, corresponds to an index in
	 *         {@link #getState()}
	 */
	int getIndex(Place place);

	/**
	 *
	 * @param transition
	 * @return the index used (internally) for this transition.
	 */
	int getIndex(Transition transition);

	/**
	 * figures out whether a certain transition t is missing any tokens to be
	 * fired.
	 * 
	 * @param t
	 *            transition to fire
	 * @return array of the number of tokens missing for each indexed place to
	 *         enable t
	 */
	byte[] getMissingTokensToEnable(Transition t);

	byte[] getMissingTokensToEnable(int transitionIndex);

	/**
	 * 
	 * @param placeIndex
	 *            of the place
	 * @return the Place object corresponding to the index
	 */
	Place getPlace(int placeIndex);

	/**
	 * @return a copy of the underlying array of tokens in the current state
	 *         (marking).
	 */
	byte[] getState();

	/**
	 * Creates a new {@link Marking} object with the current marking.
	 * 
	 * @return the current state as a {@link Marking}
	 */
	Marking getStateAsMarking();

	/**
	 * 
	 * @param index
	 *            of the transition
	 * @return the Transition Object corresponding to the index
	 */
	Transition getTransition(int index);

	/**
	 * @param transition
	 * @return whether the transition is enabled
	 */
	boolean isEnabled(Transition transition);

	boolean isEnabled(int transitionIndex);

	/**
	 * @param marking
	 *            obtained with {@link #getState()}
	 * @param place
	 *            of the net
	 * @return where place p is marked
	 */
	boolean isMarked(byte[] marking, Place place);

	/**
	 * Sets the state to the supplied state (marking). The state array is copied
	 * into the internal data structure.
	 * 
	 * @param state
	 */
	void setState(byte[] state);

	/**
	 * Sets the state to the supplied marking.
	 * 
	 * @param marking
	 */
	void setStateAsMarking(Marking marking);

	/**
	 * allows to convert a given marking to an equivalent int array based on the
	 * internal index representation of the petri net's places.
	 * 
	 * @param marking
	 *            of the petri net used wihtin semantics
	 * @return corresponding int array
	 */
	byte[] convert(Marking marking);

	/**
	 * convert a primative int array to a marking object based on the internal
	 * index representation of the petri net's places.
	 * 
	 * @param marking
	 *            to transform (int arr)
	 * @return fresh marking object
	 */
	Marking convert(byte[] marking);

}