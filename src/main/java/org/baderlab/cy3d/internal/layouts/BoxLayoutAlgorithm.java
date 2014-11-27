package org.baderlab.cy3d.internal.layouts;

import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutContext;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class BoxLayoutAlgorithm extends AbstractLayoutAlgorithm<BoxLayoutContext> {

	public BoxLayoutAlgorithm() {
		super("box", "Box Layout", false);
	}

	@Override
	public TaskIterator createTaskIterator(BoxLayoutContext context) {
		return new TaskIterator(
				new BoxLayoutAlgorithmTask(getName(), context));
	}
	
	@Override
	public BoxLayoutContext createLayoutContext() {
		return new BoxLayoutContext(supportsSelectedOnly(), supportsNodeAttributes(), supportsEdgeAttributes());
	}
}
