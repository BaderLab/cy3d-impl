package org.baderlab.cy3d.internal.cytoscape.processing;

import org.baderlab.cy3d.internal.data.GraphicsData;

// Responsible for interacting with Cytoscape through Cytoscape API, 
// such as transferring data to Cytoscape's CyTables
public interface CytoscapeDataProcessor {
	
	public void processCytoscapeData(GraphicsData graphicsData);
	
}
