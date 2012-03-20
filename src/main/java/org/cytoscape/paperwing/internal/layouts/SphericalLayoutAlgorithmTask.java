package org.cytoscape.paperwing.internal.layouts;

import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractBasicLayoutTask;
import org.cytoscape.view.layout.AbstractLayoutAlgorithmContext;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;

public class SphericalLayoutAlgorithmTask extends AbstractBasicLayoutTask {

	private SphericalLayoutContext context;
	
	public SphericalLayoutAlgorithmTask(String name,
			SphericalLayoutContext context) {
		super(name, context);
		this.context = context;
	}

	@Override
	protected void doLayout(TaskMonitor taskMonitor) {

		taskMonitor.setProgress(0.2);
		
		int nodeCount = networkView.getNodeViews().size();
		int current = 0;
		
		double sphereRadius = 500 + Math.sqrt(nodeCount);
		double x, y, z;
		
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			
			double phi = Math.random() * Math.PI * 2;
			
			x = Math.cos((double) current / nodeCount * Math.PI * 2) * Math.sin(phi);
			y = Math.sin((double) current / nodeCount * Math.PI * 2) * Math.sin(phi);
			z = Math.cos(phi);
			
			x *= sphereRadius;
			y *= sphereRadius;
			z *= sphereRadius;
			
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION, z);	
			
			current++;
		}
		
		taskMonitor.setProgress(0.8);
	}

}
