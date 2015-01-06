package org.baderlab.cy3d.internal;

import org.baderlab.cy3d.internal.cytoscape.view.Cy3DNetworkView;
import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.work.swing.DialogTaskManager;

/** The RenderingEngineFactory for the Cy3DRenderingEngine
 * 
 * @author paperwing (Yue Dong)
 */
public class Cy3DMainRenderingEngineFactory extends Cy3DRenderingEngineFactory {

	public Cy3DMainRenderingEngineFactory(
			RenderingEngineManager renderingEngineManager,
			VisualLexicon lexicon,
			TaskFactoryListener taskFactoryListener,
			DialogTaskManager taskManager,
			CyServiceRegistrar serviceRegistrar) {
		super(renderingEngineManager, lexicon, taskFactoryListener, taskManager, serviceRegistrar);
	}

	
	@Override
	protected Cy3DRenderingEngine getNewRenderingEngine(Cy3DNetworkView viewModel, VisualLexicon visualLexicon) {
		return new Cy3DMainRenderingEngine(viewModel, visualLexicon);
	}
	
}
