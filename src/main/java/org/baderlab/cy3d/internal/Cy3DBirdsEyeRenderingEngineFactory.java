package org.baderlab.cy3d.internal;

import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.work.swing.DialogTaskManager;

/** This class is capable of creating instances of the Cy3DRenderingEngine
 * 
 * @author Paperwing (Yue Dong)
 */
public class Cy3DBirdsEyeRenderingEngineFactory extends Cy3DRenderingEngineFactory {

	public Cy3DBirdsEyeRenderingEngineFactory(
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
	protected Cy3DRenderingEngine getNewRenderingEngine(Object container,
			View<CyNetwork> viewModel, VisualLexicon visualLexicon) {
	
		return new Cy3DBirdsEyeRenderingEngine(container, viewModel, visualLexicon);
	}
	
	

}
