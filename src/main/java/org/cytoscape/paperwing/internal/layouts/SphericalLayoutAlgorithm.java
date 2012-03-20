package org.cytoscape.paperwing.internal.layouts;

import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class SphericalLayoutAlgorithm extends AbstractLayoutAlgorithm {

	public SphericalLayoutAlgorithm(UndoSupport undo) {
		
		super(undo, "spherical", "Spherical Layout", false);
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(
				new SphericalLayoutAlgorithmTask(
						networkView, getName(), selectedOnly, staticNodes));
	}
	
}
