package org.baderlab.cy3d.internal.picking;

import org.baderlab.cy3d.internal.data.GraphicsData;

// Read-only from GraphicsData and SelectionData, writes to PickingData
public interface ShapePickingProcessor {
	
	public void initialize(GraphicsData graphicsData);
	
	public void processPicking(int x, int y, GraphicsData graphicsData);

}
