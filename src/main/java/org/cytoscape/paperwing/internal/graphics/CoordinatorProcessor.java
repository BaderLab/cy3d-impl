package org.cytoscape.paperwing.internal.graphics;

public interface CoordinatorProcessor {
	public void extractData(ViewingCoordinator coordinator, GraphicsData graphicsData);

	public void unlinkCoordinator(ViewingCoordinator coordinator);
}
