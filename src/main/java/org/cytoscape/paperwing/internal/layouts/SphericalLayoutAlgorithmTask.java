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
			
			/*
			int nodesPerLevel = (int) Math.max(Math.sqrt(nodeCount), 1);
			
			// The fraction should range from 0 to 1
			double levelFraction = Math.floor(current / nodesPerLevel) * nodesPerLevel / nodeCount;
		
			double thetaLimit = 0.1;
			double phiLimit = 0.1;
			
			// double theta = Math.PI / 2 - (Math.PI * thetaLimit + (double) level / numLevels * Math.PI * (1 - 2 * thetaLimit));	
			double theta = Math.PI / 2 - (Math.PI * thetaLimit + levelFraction * Math.PI * (1 - 2 * thetaLimit));
			double phi = Math.PI * phiLimit + (double) (current % nodesPerLevel) / nodesPerLevel * Math.PI * (2 - 2 * phiLimit);
			
			phi = 2;
			*/
			
			int numLevels = (int) Math.sqrt(nodeCount);
			
			int level = (current / numLevels) * numLevels;
			
			double thetaLimit = 0.1;
			double phiLimit = 0.1;
				
			double theta = Math.PI / 2 - (Math.PI * thetaLimit + (double) level / nodeCount * Math.PI * (1 - 2 * thetaLimit));
			double phi = Math.PI * phiLimit + (double) (current % numLevels) / numLevels * Math.PI * (2 - 2 * phiLimit);			
			
			x = Math.cos(theta) * Math.sin(phi);
			y = Math.sin(theta) * Math.sin(phi);
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
