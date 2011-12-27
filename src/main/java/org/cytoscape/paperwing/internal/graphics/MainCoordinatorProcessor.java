package org.cytoscape.paperwing.internal.graphics;

public class MainCoordinatorProcessor implements CoordinatorProcessor {

	@Override
	public void extractData(ViewingCoordinator coordinator,
			GraphicsData graphicsData) {
	}

	@Override
	public void unlinkCoordinator(ViewingCoordinator coordinator) {
		coordinator.unlinkMain();
	}

}
