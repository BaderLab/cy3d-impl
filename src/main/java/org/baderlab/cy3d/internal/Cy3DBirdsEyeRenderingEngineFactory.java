package org.baderlab.cy3d.internal;

import org.baderlab.cy3d.internal.cytoscape.view.Cy3DNetworkView;
import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.work.swing.DialogTaskManager;

/** This class is capable of creating instances of the Cy3DRenderingEngine
 * 
 * @author Paperwing (Yue Dong)
 */
public class Cy3DBirdsEyeRenderingEngineFactory extends Cy3DRenderingEngineFactory {

	public Cy3DBirdsEyeRenderingEngineFactory(
			RenderingEngineManager renderingEngineManager,
			VisualLexicon lexicon,
			TaskFactoryListener taskFactoryListener,
			DialogTaskManager taskManager,
			CyServiceRegistrar serviceRegistrar) {
		super(renderingEngineManager, lexicon, taskFactoryListener, taskManager, serviceRegistrar);
	}

	@Override
	protected Cy3DRenderingEngine getNewRenderingEngine(Cy3DNetworkView viewModel, VisualLexicon visualLexicon) {
		return new Cy3DBirdsEyeRenderingEngine(viewModel, visualLexicon);
	}
	
}
