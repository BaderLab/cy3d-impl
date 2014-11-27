package org.baderlab.cy3d.internal.layouts;

import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutContext;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class GridLayoutAlgorithm extends AbstractLayoutAlgorithm<GridLayoutContext> {

	public GridLayoutAlgorithm() {
		super("grid3D", "3D Grid Layout", false);
	}

	@Override
	public TaskIterator createTaskIterator(GridLayoutContext context) {
		return new TaskIterator(
				new GridLayoutAlgorithmTask(getName(), context));
	}
	
	@Override
	public GridLayoutContext createLayoutContext() {
		return new GridLayoutContext(supportsSelectedOnly(), supportsNodeAttributes(), supportsEdgeAttributes());
	}
}
