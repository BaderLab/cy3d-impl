package org.cytoscape.paperwing.internal.cytoscape.processing;

import org.cytoscape.paperwing.internal.data.GraphicsData;

// Responsible for interacting with Cytoscape through Cytoscape API, 
// such as transferring data to Cytoscape's CyTables
public interface CytoscapeDataProcessor {
	
	public void processCytoscapeData(GraphicsData graphicsData);
	
}
