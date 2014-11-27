package org.baderlab.cy3d.internal;

import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.swing.SubmenuTaskManager;

/** The RenderingEngineFactory for the WindRenderingEngine
 * 
 * @author paperwing (Yue Dong)
 */
public class WindMainRenderingEngineFactory extends WindRenderingEngineFactory {

	public WindMainRenderingEngineFactory(
			CyNetworkViewManager networkViewManager,
			RenderingEngineManager renderingEngineManager,
			VisualLexicon lexicon,
			TaskFactoryListener taskFactoryListener,
			DialogTaskManager taskManager,
			SubmenuTaskManager submenuTaskManager,
			CyServiceRegistrar serviceRegistrar) {
		super(networkViewManager, renderingEngineManager, lexicon, taskFactoryListener, 
				taskManager, submenuTaskManager, serviceRegistrar);
	}

	
	@Override
	protected WindRenderingEngine getNewRenderingEngine(Object container,
			View<CyNetwork> viewModel, VisualLexicon visualLexicon) {
		
		return new WindMainRenderingEngine(container, viewModel, visualLexicon);
	}
	
	
}
