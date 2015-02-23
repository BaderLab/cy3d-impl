package org.baderlab.cy3d.internal.layouts;

import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.UndoSupport;

public class FlattenLayoutAlgorithmTask extends AbstractLayoutTask {

	public FlattenLayoutAlgorithmTask(String displayName, CyNetworkView networkView, Set<View<CyNode>> nodesToLayOut, String layoutAttribute, UndoSupport undo) {
		super(displayName, networkView, nodesToLayOut, layoutAttribute, undo);
	}

	@Override
	protected void doLayout(TaskMonitor taskMonitor) {
		for(View<CyNode> nodeView : networkView.getNodeViews()) {
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION, 0.0d);
		}
	}

}
