package org.baderlab.cy3d.internal;

import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.work.swing.DialogTaskManager;

/** This class is capable of creating instances of the WindMapRenderingEngine
 * 
 * @author Paperwing (Yue Dong)
 */
public class WindBirdsEyeRenderingEngineFactory extends WindRenderingEngineFactory {

	public WindBirdsEyeRenderingEngineFactory(
			CyNetworkViewManager networkViewManager,
			RenderingEngineManager renderingEngineManager,
			VisualLexicon lexicon,
			TaskFactoryListener taskFactoryListener,
			DialogTaskManager taskManager,
			CyServiceRegistrar serviceRegistrar) {
		super(networkViewManager, renderingEngineManager, lexicon, taskFactoryListener,
				taskManager, serviceRegistrar);
	}

	@Override
	protected WindRenderingEngine getNewRenderingEngine(Object container,
			View<CyNetwork> viewModel, VisualLexicon visualLexicon) {
	
		return new WindBirdsEyeRenderingEngine(container, viewModel, visualLexicon);
	}
	
	

}
