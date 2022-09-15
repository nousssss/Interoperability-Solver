package org.processmining.models.workshop.fbetancor.listcreators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.HashBag;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeCollection;
import org.deckfour.xes.model.XAttributeContainer;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeTimestamp;

/**
 * Class made for filling lists and bags that require attributes as input.
 * 
 * @author R. Verhulst
 *
 */
public class AttributeLists {
	/**
	 * List for each event the values of all the event-related attributes.
	 */
	private List<ArrayList<String>> attributeValues;

	/**
	 * List of all the attribute names.
	 */
	private List<String> attributeNames;

	/**
	 * Bag for all the attribute names. This way, counting the number of
	 * occurrences per attribute name can be done efficiently.
	 */
	private Bag<String> attributeNameBag;

	/**
	 * List containing for every attribute the different types.
	 */
	private List<ArrayList<String>> attributeTypes;

	/**
	 * Initialize the bag and lists.
	 */
	public void initialize() {
		attributeValues = new ArrayList<ArrayList<String>>();
		attributeNameBag = new HashBag<String>();
		attributeNames = new ArrayList<String>();
		attributeTypes = new ArrayList<ArrayList<String>>();
	}

	/**
	 * Fill the list and bag with attribute-related values.
	 * 
	 * @param att
	 */
	public void fillList(XAttribute att) {
		ArrayList<String> current = new ArrayList<String>();
		ArrayList<String> currentType = new ArrayList<String>();
		attributeNameBag.add(att.getKey());
		/*
		 * If there's a new AttributeType
		 */
		if (!attributeNames.contains(att.getKey())) {
			attributeNames.add(att.getKey());
			current.add(att.getKey());
			currentType.add(att.getKey());

			/*
			 * If the key of the Att hasn't been added yet, but another value;
			 * Add the non-key value. You don't want to have unnecessary
			 * duplicates.
			 */

			if (!current.contains(att.toString())) {
				current.add(att.toString());
				String detType = determineType(att);
				currentType.add(detType);
			}

			attributeValues.add((ArrayList<String>) current.clone());
			attributeTypes.add((ArrayList<String>) currentType.clone());
		} else {
			/*
			 * The attribute has already been logged, so just add the element at
			 * the right place to the Attribute Column.
			 */
			int position = attributeNames.indexOf(att.getKey());
			attributeValues.get(position).add(att.toString());
			
			String type = determineType(att);
			attributeTypes.get(position).add(type);
		}
	}

	/**
	 * Determine the type in terms of a string-return value. 
	 * 
	 * @param att
	 * @return (String) Type 
	 */
	private String determineType(XAttribute att) {
		if(att instanceof XAttributeLiteral) {
			return "String";
		} else if(att instanceof XAttributeBoolean) {
			return "Boolean";
		} else if(att instanceof XAttributeContinuous) {
			return "Double";
		} else if(att instanceof XAttributeDiscrete) {
			return "Integer";
		} else if(att instanceof XAttributeTimestamp) {
			return "Timestamp";
		} else if(att instanceof XAttributeID) {
			return "ID";
		} else if(att instanceof XAttributeCollection) {
			return "Collection";
		} else if(att instanceof XAttributeList) {
			return "List";
		} else if(att instanceof XAttributeContainer) {
			return "Container";
		} 
		return "null";
	}

	/**
	 * Get method for the attribute Name Bag.
	 * 
	 * @return attributeNameBag
	 */
	public Bag<String> getAttributeNameBag() {
		return attributeNameBag;
	}

	/**
	 * Get method for the attribute value list.
	 * 
	 * @return attributeValues
	 */
	public List<ArrayList<String>> getAttributeValuesList() {
		return attributeValues;
	}
	
	/**
	 * Get method for the attribute type list.
	 * 
	 * @return attributeTypes
	 */
	public List<ArrayList<String>> getAttributeTypeList() {
		return attributeTypes;
	}

}
