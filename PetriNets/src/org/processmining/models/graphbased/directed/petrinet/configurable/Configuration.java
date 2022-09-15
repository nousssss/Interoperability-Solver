package org.processmining.models.graphbased.directed.petrinet.configurable;

/**
 * Configuration for features of a {@link ConfigurableFeatureGroup}. Sets new values
 * for features of a {@link ConfigurableFeatureGroup}. A key of a {@link Configuration}
 * refers to a {@link ConfigurableParameter#getId()}, the value is the value of
 * this feature.
 * 
 * @author dfahland
 * 
 */
public class Configuration extends java.util.HashMap<String, Object> {

	private static final long serialVersionUID = 1L;
	
	private String groupId;
	
	public Configuration(String groupId) {
		this.groupId = groupId;
	}
	
	public String getFeatureGroupId() {
		return groupId;
	}
}
