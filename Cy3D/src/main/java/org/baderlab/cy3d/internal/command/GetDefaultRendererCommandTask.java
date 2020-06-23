package org.baderlab.cy3d.internal.command;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.NetworkViewRenderer;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;

public class GetDefaultRendererCommandTask extends AbstractTask implements ObservableTask {
	
	private final CyApplicationManager applicationManager;
	
	private String result;
	
	
	public GetDefaultRendererCommandTask(CyApplicationManager applicationManager) {
		this.applicationManager = applicationManager;
	}
	

	@Override
	public void run(TaskMonitor tm) {
		NetworkViewRenderer renderer = applicationManager.getDefaultNetworkViewRenderer();
		result = renderer.getId();
		System.out.println(result);
	}


	@Override
	public <R> R getResults(Class<? extends R> type) {
		if(String.class.equals(type)) {
			return type.cast(result);
		}
		return null;
	}
	

}
