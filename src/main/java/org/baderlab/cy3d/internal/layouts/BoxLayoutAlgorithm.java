package org.baderlab.cy3d.internal.layouts;

import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class BoxLayoutAlgorithm extends AbstractLayoutAlgorithm {

	public BoxLayoutAlgorithm(UndoSupport undo) {
		super("box", "3D Box Layout", undo);
	}

	@Override
	public BoxLayoutContext createLayoutContext() {
		return new BoxLayoutContext();
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView, Object context, Set<View<CyNode>> nodesToLayOut, String layoutAttribute) {
		return new TaskIterator(new BoxLayoutAlgorithmTask(getName(), (BoxLayoutContext)context, networkView, nodesToLayOut, layoutAttribute, undoSupport));
	}
}
