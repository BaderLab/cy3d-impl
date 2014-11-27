package org.baderlab.cy3d.internal.cytoscape.processing;

import org.baderlab.cy3d.internal.data.GraphicsData;

/**
 * This interface represents a Cytoscape data sub-processor, which provides more specific functionalities relating
 * to obtaining and transferring data to Cytoscape via its API.
 */
public interface CytoscapeDataSubprocessor {
	
	/**
	 * Transfer data to or from Cytoscape given the graphics data object. This could be data relating to CyTable objects
	 * or visual properties.
	 * @param graphicsData
	 */
	public void processCytoscapeData(GraphicsData graphicsData);
}
