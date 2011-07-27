package org.cytoscape.paperwing.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.events.RenderingEngineAboutToBeRemovedListener;

import com.jogamp.opengl.util.FPSAnimator;

public class WindMapRenderingEngineFactory implements RenderingEngineFactory<CyNetwork> {

	private CyNetworkViewManager networkViewManager;
	private RenderingEngineManager renderingEngineManager;
	private final VisualLexicon visualLexicon;
	
	private CyServiceRegistrar serviceRegistrar;
	
	public WindMapRenderingEngineFactory(CyNetworkViewManager networkViewManager,
			RenderingEngineManager renderingEngineManager, VisualLexicon lexicon,
			CyServiceRegistrar serviceRegistrar) {	
		this.networkViewManager = networkViewManager;
		this.renderingEngineManager = renderingEngineManager;
		this.visualLexicon = lexicon;
		
		this.serviceRegistrar = serviceRegistrar;
		
		// TestGraphics.initSingleton();
		Graphics.initSingleton();
		WindMapRenderingEngine.setNetworkViewManager(networkViewManager);
	}
	
	@Override
	public RenderingEngine<CyNetwork> getInstance(
			Object container, View<CyNetwork> viewModel) {
		
		/* For code below, seems that NetworkViewManager does not contain references to all available NetworkViews
		 */
		System.out.println("map given model: " + viewModel.getModel());
		System.out.println("map given model suid: " + viewModel.getModel().getSUID());
		System.out.println("map given suid: " + viewModel.getSUID());
		System.out.println("map networkViewSet: " + networkViewManager.getNetworkViewSet());
		
		/*
		// CyNetworkView networkView = networkViewManager.getNetworkView(viewModel.getSUID());
		CyNetworkView networkView = null;
		for (CyNetworkView view : networkViewManager.getNetworkViewSet()) {
			if (view.getModel() == viewModel.getModel()) {
				networkView = view;
			}
			System.out.println("current model: " + view.getModel());
			System.out.println("current model suid: " + view.getModel().getSUID());
			System.out.println("current suid: " + view.getSUID());
				
		}
		*/
		
		//TODO: NetworkViewManager does not contain all instances of CyNetworkView, so wait 
		WindMapRenderingEngine engine = new WindMapRenderingEngine(container, viewModel, visualLexicon);
		System.out.println("map returning engine: " + engine);
		renderingEngineManager.addRenderingEngine(engine);
		
		System.out.println("map engine active?: " + engine.isActive());

		serviceRegistrar.registerService(engine.getEngineRemovedListener(), 
				RenderingEngineAboutToBeRemovedListener.class, new Properties());
		
		return engine;
	}

	@Override
	public VisualLexicon getVisualLexicon() {
		// System.out.println("getVisualLexicon call");
		
		return visualLexicon;
	}

}
