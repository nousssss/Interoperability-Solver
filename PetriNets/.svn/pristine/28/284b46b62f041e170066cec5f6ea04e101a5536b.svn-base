package org.processmining.plugins.petrinet.configurable.ui.widgets;

/**
 * Abstract UI design template for UI classes, providing a standard method of
 * setting values in the UI and for obtaining configuration results from the UI.
 * 
 * @author dfahland
 * 
 * @param <INPUT>
 * @param <OUTPUT>
 */
public interface Structured_UI<INPUT,OUTPUT> {
	
	/**
	 * Fill controls of this panel based on the values in the feature.
	 * @param feature
	 */
	public void setValues(INPUT input);
	
	/**
	 * @return object configured by this panel
	 */
	public OUTPUT getConfigured() throws Exception;

}
