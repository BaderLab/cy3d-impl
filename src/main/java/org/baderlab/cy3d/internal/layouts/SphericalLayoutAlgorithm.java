package org.baderlab.cy3d.internal.layouts;

import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutContext;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class SphericalLayoutAlgorithm extends AbstractLayoutAlgorithm<SphericalLayoutContext> {

	public SphericalLayoutAlgorithm() {
		super("spherical", "Spherical Layout", false);
	}

	@Override
	public TaskIterator createTaskIterator(SphericalLayoutContext context) {
		return new TaskIterator(
				new SphericalLayoutAlgorithmTask(getName(), context));
	}
	
	@Override
	public SphericalLayoutContext createLayoutContext() {
		return new SphericalLayoutContext(supportsSelectedOnly(), supportsNodeAttributes(), supportsEdgeAttributes());
	}
}
