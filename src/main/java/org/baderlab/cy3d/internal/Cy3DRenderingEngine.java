package org.baderlab.cy3d.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.util.Properties;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import org.baderlab.cy3d.internal.cytoscape.view.Cy3DNetworkView;
import org.baderlab.cy3d.internal.graphics.GraphicsConfiguration;
import org.baderlab.cy3d.internal.graphics.RenderEventListener;
import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.work.swing.DialogTaskManager;

/** 
 * This class represents a Cy3DRenderingEngine, responsible for
 * creating a rendering of a {@link CyNetwork}.
 */
class Cy3DRenderingEngine implements RenderingEngine<CyNetwork> {
	
	private final Cy3DNetworkView networkView;
	private final VisualLexicon visualLexicon;
	private final GraphicsConfiguration configuration;
	
	private RenderEventListener renderEventListener;
	private GLJPanel panel;
	
	
	public Cy3DRenderingEngine(Cy3DNetworkView viewModel, VisualLexicon visualLexicon, GraphicsConfiguration configuration) {
		this.networkView = viewModel;
		this.visualLexicon = visualLexicon;
		this.configuration = configuration;
	}
	
	
	/** Set up the canvas by creating and placing it, along with a Graphics
	 * object, into the container
	 * 
	 * @param container A container in the GUI window used to contain
	 * the rendered results
	 */
	public void setUpCanvas(JComponent container) {
		GLProfile profile = GLProfile.getDefault(); // Use the system's default version of OpenGL
		GLCapabilities capabilities = new GLCapabilities(profile);
		capabilities.setHardwareAccelerated(true);
		capabilities.setDoubleBuffered(true);
		
		panel = new GLJPanel(capabilities); // GLJPanel is meant to be used with JInternalFrame
		panel.setIgnoreRepaint(true); // TODO: check if negative effects produced by this
		//panel.setDoubleBuffered(true);
		
		renderEventListener = new RenderEventListener(networkView, visualLexicon, configuration);
		renderEventListener.trackInput(panel);
		
		configuration.setUpContainer(container);

		panel.addGLEventListener(renderEventListener);
		
		networkView.addContainer(panel); // When networkView.updateView() is called it will repaint all containers it owns

		if (container instanceof JInternalFrame) {
			JInternalFrame frame = (JInternalFrame) container;
			Container pane = frame.getContentPane();
			pane.setLayout(new BorderLayout());
			pane.add(panel, BorderLayout.CENTER);
		} 
		else {
			container.setLayout(new BorderLayout());
			container.add(panel, BorderLayout.CENTER);
		}
	}
	
	
	public void setUpTaskFactories(TaskFactoryListener taskFactoryListener, DialogTaskManager taskManager) {
		renderEventListener.setupTaskFactories(taskFactoryListener, taskManager);
	}
	
	
	
	@Override
	public View<CyNetwork> getViewModel() {
		return networkView;
	}

	@Override
	public VisualLexicon getVisualLexicon() {
		return visualLexicon;
	}

	@Override
	public Properties getProperties() {
		return null;
	}
	
	@Override
	public Printable createPrintable() {
		return null;
	}

	@Override
	public Image createImage(int width, int height) {
		Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);		

		Dimension panelSize = panel.getSize();
		
		panel.setSize(width, height);
		panel.paint(image.getGraphics());
		panel.setSize(panelSize);
		
		return image;
	}

	@Override
	public <V> Icon createIcon(VisualProperty<V> vp, V value, int width, int height) {
		return null;
	}

	@Override
	public void printCanvas(java.awt.Graphics printCanvas) {
	}
	
	@Override
	public String getRendererId() {
		return Cy3DNetworkViewRenderer.ID;
	}
	
	@Override
	public void dispose() {
	}
}
