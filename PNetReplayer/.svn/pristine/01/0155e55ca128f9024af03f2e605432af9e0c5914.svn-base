package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import com.fluxicon.slickerbox.components.NiceDoubleSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * Adds a slider to adjust the parameter "lowerFitnessBound" to the standard parameter settings dialog
 * 
 * @author fmannhardt
 *
 */
public class ParamSettingBestWithFitnessBoundAlg extends ParamSettingExpressAlg {

	private static final long serialVersionUID = 7597214109646340219L;

	private static final double MAX_LOWER_BOUND = 1.0d;

	protected NiceDoubleSlider lowerBoundSlider;
	
	public ParamSettingBestWithFitnessBoundAlg(){
		super();
		
		// add setting of N before any other parameters
		SlickerFactory factory = SlickerFactory.instance();
		lowerBoundSlider = factory.createNiceDoubleSlider("<html><h4>Lower bound for fitness.</h4></html>",0.0d,MAX_LOWER_BOUND,0.5d,Orientation.HORIZONTAL);
		add(lowerBoundSlider);		
	}
	
	public double getLowerFitnessBound(){
		return lowerBoundSlider.getValue();
	}
	
	/**
	 * Get all parameters for this algorithm
	 */
	@Override
	public Object[] getAllParameters() {
		Object[] res = new Object[4];

		// create map trans to cost
		res[AllOptAlignmentsTreeAlg.MAPTRANSTOCOST] = getTransitionWeight();
		res[AllOptAlignmentsTreeAlg.MAXEXPLOREDINSTANCES] = limExpInstances.getValue() == MAXLIMMAXNUMINSTANCES ? Integer.MAX_VALUE
				: limExpInstances.getValue() * 100;
		res[AllOptAlignmentsTreeAlg.MAPXEVENTCLASSTOCOST] = getMapEvClassToCost();
		res[3] = getLowerFitnessBound();
		return res;
	}
}
