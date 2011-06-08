package org.cytoscape.paperwing.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineManager;

import com.jogamp.opengl.util.FPSAnimator;

public class WindMapRenderingEngineFactory implements RenderingEngineFactory<CyNetwork> {

	private CyNetworkViewManager networkViewManager;
	private RenderingEngineManager renderingEngineManager;
	private final VisualLexicon visualLexicon;
	
	public WindMapRenderingEngineFactory(CyNetworkViewManager networkViewManager, RenderingEngineManager renderingEngineManager, VisualLexicon lexicon) {	
		this.networkViewManager = networkViewManager;
		this.renderingEngineManager = renderingEngineManager;
		this.visualLexicon = lexicon;
	}
	
	@Override
	public RenderingEngine<CyNetwork> getInstance(
			Object container, View<CyNetwork> viewModel) {

		if (container instanceof JComponent) {
			System.out.println("map getInstance called");
			
			JComponent component = (JComponent) container;
			// component.removeAll();
			// System.out.println("number of components in container after remove: " + component.getComponents().length);
			
			// Use the system's default version of OpenGL
			GLProfile profile = GLProfile.getDefault();
			
			// TODO: changed true to false, check for performance difference
			// GLProfile.initSingleton(false);

			GLCapabilities capabilities = new GLCapabilities(profile);

			// TODO: check whether to use GLCanvas or GLJPanel
			// GLCanvas canvas = new GLCanvas(capabilities);
			GLJPanel canvas = new GLJPanel(capabilities);
			// canvas.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			TestGraphics graphics = new TestGraphics();

			canvas.addGLEventListener(graphics);
	
			if (container instanceof JInternalFrame) {
				JInternalFrame frame = (JInternalFrame) container;
				Container pane = frame.getContentPane();
				
				graphics.trackInput(pane);
				
				/*
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				panel.add(canvas, BorderLayout.CENTER);
				component.add(panel, BorderLayout.CENTER);
				*/
				
				pane.setLayout(new BorderLayout());
				pane.add(canvas, BorderLayout.CENTER);
				
				// TODO: check if this line needed
				pane.setVisible(true);
			} else {
				graphics.trackInput(component);
				// graphics.trackInput(canvas);
				
				component.setLayout(new BorderLayout());
				// component.add(canvas, BorderLayout.CENTER);
			}
			
			graphics.setManagers(null, null, networkViewManager, renderingEngineManager);
			
			//FPSAnimator animator = new FPSAnimator(60);
			//animator.add(canvas);
			//animator.start();
			
			// Animator animator = new Animator(canvas);
			// animator.setRunAsFastAsPossible(true);
			// animator.start();
		}
		
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
		// viewModel.getModel().
		
		System.out.println("Returning networkView: " + networkView);
		*/
		
		//TODO: NetworkViewManager does not contain all instances of CyNetworkView, so wait 
		WindRenderingEngine engine = new WindRenderingEngine(networkViewManager, viewModel, visualLexicon);
		System.out.println("map returning engine: " + engine);
		renderingEngineManager.addRenderingEngine(engine);
		
		
		// TODO Auto-generated method stub
		return engine;
	}

	@Override
	public VisualLexicon getVisualLexicon() {
		// System.out.println("getVisualLexicon call");
		
		return visualLexicon;
	}

}
