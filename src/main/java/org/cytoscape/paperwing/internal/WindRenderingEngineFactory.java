package org.cytoscape.paperwing.internal;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JComponent;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineManager;

import com.jogamp.opengl.util.FPSAnimator;

public class WindRenderingEngineFactory implements RenderingEngineFactory<CyNetwork> {

	private final VisualLexicon lexicon;
	private RenderingEngineManager manager;
	
	public WindRenderingEngineFactory(RenderingEngineManager manager, VisualLexicon lexicon) {
		
		this.lexicon = lexicon;
		this.manager = manager;
		
	}
	
	@Override
	public RenderingEngine<CyNetwork> getInstance(
			Object container, View<CyNetwork> viewModel) {
		
		//if (container instanceof JComponent) {
		if (false) {
			System.out.println("Adding canvas");
			
			JComponent component = (JComponent) container;
			component.removeAll();
			
			// Use the system's default version of OpenGL
			GLProfile profile = GLProfile.getDefault();
			GLProfile.initSingleton(true);
			
			GLCapabilities capabilities = new GLCapabilities(profile);
			GLCanvas canvas = new GLCanvas(capabilities);
	
			TestGraphics graphics = new TestGraphics();
	
			canvas.addGLEventListener(graphics);
			graphics.getKeyListener();

			canvas.addKeyListener(graphics.getKeyListener());
			canvas.addMouseListener(graphics.getMouseListener());
			canvas.addMouseMotionListener(graphics.getMouseMotionListener());
			canvas.addMouseWheelListener(graphics.getMouseWheelListener());
			
			component.add(canvas);
			component.addKeyListener(graphics.getKeyListener());
			
			FPSAnimator animator = new FPSAnimator(60);
			animator.add(canvas);
			animator.start();
		}
		
		// TODO Auto-generated method stub
		return new WindRenderingEngine(viewModel, lexicon);
	}

	@Override
	public VisualLexicon getVisualLexicon() {
		// System.out.println("getVisualLexicon call");
		
		return lexicon;
	}

}
