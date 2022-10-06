package org.processmining.models.graphbased;

import org.jgraph.graph.AttributeMap;

public class AnnotatedAttributeMap extends AttributeMap {
	
	/*
	 * HV: Specific attributes used by this package. Used in renderer.
	 */
	private final static String ANNOTATEDPREFIX = "ProM_Vis_anno_attr_";

	public final static String LANEHEIGHTS = ANNOTATEDPREFIX + "laneHeights";
	public final static String LANECOLORS = ANNOTATEDPREFIX + "laneColors";
	public final static String LANECHART = ANNOTATEDPREFIX + "laneChart";
	public final static String COLORMAP = ANNOTATEDPREFIX + "colorMap";

}
