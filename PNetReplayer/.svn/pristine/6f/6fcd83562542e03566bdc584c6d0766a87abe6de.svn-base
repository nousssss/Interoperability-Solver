/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import com.fluxicon.slickerbox.components.NiceIntegerSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * @author aadrians
 * Mar 15, 2013
 *
 */
public class ParamSettingNBestAlg extends ParamSettingExpressAlg {
	
	private static final int MAX_ALIGNMENTS = 1000;

	private static final long serialVersionUID = -7283101679802274197L;
	
	protected NiceIntegerSlider numExpectedAlignments;
	
	public ParamSettingNBestAlg(){
		super();
		
		// add setting of N before any other parameters
		SlickerFactory factory = SlickerFactory.instance();
		numExpectedAlignments = factory.createNiceIntegerSlider("<html><h4>Number of Expected Alignments.</h4></html>",1,MAX_ALIGNMENTS,10,Orientation.HORIZONTAL);
		add(numExpectedAlignments);		
	}
	
	public int getNumExpectedAlignments(){
		return numExpectedAlignments.getValue();
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
		res[3] = getNumExpectedAlignments();
		return res;
	}
}
