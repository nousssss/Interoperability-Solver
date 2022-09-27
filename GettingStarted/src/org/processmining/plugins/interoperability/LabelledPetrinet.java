package org.processmining.plugins.interoperability;


import org.processmining.framework.annotations.AuthoredType;
import org.processmining.framework.annotations.Icon;

@AuthoredType(
		typeName = "Petri net with labelled transitions", 
        affiliation = "CDTA",
        author = "Bachiri Inas", 
        email = "ji_bachiri@esi.dz")
@Icon(
		icon = "resourcetype_petrinet_30x35.png")

public interface LabelledPetrinet extends LabelledPetrinetGraph {

	LabelledPetrinetImpl getEmptyClone();

	
}