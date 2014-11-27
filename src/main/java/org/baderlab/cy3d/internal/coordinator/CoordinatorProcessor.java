package org.baderlab.cy3d.internal.coordinator;

import org.baderlab.cy3d.internal.data.GraphicsData;

public interface CoordinatorProcessor {

	// Read-only for GraphicsData
	public void initializeCoordinator(ViewingCoordinator coordinator,
			GraphicsData graphicsData);
	
	public void extractData(ViewingCoordinator coordinator, GraphicsData graphicsData);

	public void unlinkCoordinator(ViewingCoordinator coordinator);
}
