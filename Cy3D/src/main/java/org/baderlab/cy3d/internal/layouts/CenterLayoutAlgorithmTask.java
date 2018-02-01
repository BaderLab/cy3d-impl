package org.baderlab.cy3d.internal.layouts;

import java.util.Collection;
import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.UndoSupport;

public class CenterLayoutAlgorithmTask extends AbstractLayoutTask {

	public CenterLayoutAlgorithmTask(String displayName, CyNetworkView networkView, Set<View<CyNode>> nodesToLayOut, String layoutAttribute, UndoSupport undo) {
		super(displayName, networkView, nodesToLayOut, layoutAttribute, undo);
	}

	@Override
	protected void doLayout(TaskMonitor taskMonitor) {
		Collection<View<CyNode>> nodeViews = networkView.getNodeViews();
		int n = nodeViews.size();
		
		// MKTODO This duplicates the logic found in LayoutToolkit.findCenter()
		
		double sumX = 0, sumY = 0, sumZ = 0;
		
		for(View<CyNode> nodeView : nodeViews) {
			sumX += nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
			sumY += nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
			sumZ += nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION);
		}
		
		double originX = sumX / n;
		double originY = sumY / n;
		double originZ = sumZ / n;
		
		// Translate graph to new origin
		for(View<CyNode> nodeView : nodeViews) {
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION) - originX);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION) - originY);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION, nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION) - originZ);
		}
		
	}

}
