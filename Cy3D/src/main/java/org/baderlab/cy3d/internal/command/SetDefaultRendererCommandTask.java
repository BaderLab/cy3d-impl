package org.baderlab.cy3d.internal.command;

import java.util.Set;

import org.baderlab.cy3d.internal.Cy3DNetworkViewRenderer;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.NetworkViewRenderer;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class SetDefaultRendererCommandTask extends AbstractTask {

	@Tunable(description="The renderer ID. The ID for Cy3D is '" + Cy3DNetworkViewRenderer.ID + "', "
			+ "the ID for the default 2D renderer is 'org.cytoscape.ding'.")
	public String rendererID = Cy3DNetworkViewRenderer.ID;
	
	
	private final CyApplicationManager applicationManager;
	
	public SetDefaultRendererCommandTask(CyApplicationManager applicationManager) {
		this.applicationManager = applicationManager;
	}
	

	@Override
	public void run(TaskMonitor tm) {
		if(rendererID == null)
			throw new IllegalArgumentException("A value for 'rendererID' must be provided.");
		
		NetworkViewRenderer renderer = getRenderer(rendererID);
		if(renderer == null)
			throw new IllegalArgumentException("A renderer with ID '" + rendererID + "' cannot be found.");
		
		applicationManager.setDefaultNetworkViewRenderer(renderer);
	}
	
	
	private NetworkViewRenderer getRenderer(String id) {
		Set<NetworkViewRenderer> rendererSet = applicationManager.getNetworkViewRendererSet();
		for(NetworkViewRenderer renderer : rendererSet) {
			if(renderer.getId() != null && renderer.getId().equals(id)) {
				return renderer;
			}
		}
		return null;
	}
	

}
