package org.cytoscape.paperwing.internal.graphics;

public interface CoordinatorProcessor {
	public void extractData(BirdsEyeCoordinator coordinator, GraphicsData graphicsData);

	public void unlinkCoordinator(BirdsEyeCoordinator coordinator);
}
