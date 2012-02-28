package org.cytoscape.paperwing.internal;

import java.util.Properties;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineManager;

/** This class is capable of creating instances of the WindMapRenderingEngine
 * 
 * @author Paperwing (Yue Dong)
 */
public class WindBirdsEyeRenderingEngineFactory extends WindRenderingEngineFactory {

	public WindBirdsEyeRenderingEngineFactory(
			CyNetworkViewManager networkViewManager,
			RenderingEngineManager renderingEngineManager,
			VisualLexicon lexicon, CyServiceRegistrar serviceRegistrar) {
		super(networkViewManager, renderingEngineManager, lexicon, serviceRegistrar);
	}

	@Override
	protected WindRenderingEngine getNewRenderingEngine(Object container,
			View<CyNetwork> viewModel, VisualLexicon visualLexicon) {
	
		System.out.println("WindBirdsEyeRenderingEngineFactory.getNewRenderingEngine() called");
		return new WindBirdsEyeRenderingEngine(container, viewModel, visualLexicon);
	}
	
	

}
