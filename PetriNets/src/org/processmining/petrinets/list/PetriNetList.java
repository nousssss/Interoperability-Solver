package org.processmining.petrinets.list;

import java.util.List;

import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

/**
 * The PetriNetList interface is just a wrapper for using a list of petri-nets
 * as an input for some other plugin. The main reason for this interfaces is the
 * fact that the {@link Plugin} annotation can simply not handle a List
 * <Petrinet> as a return type.
 * 
 * @author svzelst
 *
 */
public interface PetriNetList extends List<Petrinet> {

}
