package org.cytoscape.paperwing.internal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.util.Properties;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;

import com.jogamp.opengl.util.FPSAnimator;

public class WindRenderingEngine implements RenderingEngine<CyNetwork> {

	private static CyNetworkViewManager networkViewManager;
	
	private CyNetworkView networkView;
	private View<CyNetwork> viewModel;
	private VisualLexicon visualLexicon;
	
	private FPSAnimator animator;
	
	private boolean active;
	
	public WindRenderingEngine(Object container, View<CyNetwork> viewModel, VisualLexicon visualLexicon) {
		// this.networkView = networkView;
	
		this.viewModel = viewModel;
		this.visualLexicon = visualLexicon;
		this.active = false;
		
		if (networkViewManager != null) {
			this.networkView = networkViewManager.getNetworkView(viewModel.getModel().getSUID());
		}
		
		if (this.networkView != null) {
			System.out.println("Setting up canvas..");
			setUpCanvas(container);
		}
		
		System.out.println("Provided visualLexicon: " + visualLexicon);
	}
	
	public static void setNetworkViewManager(CyNetworkViewManager networkViewManager) {
		WindRenderingEngine.networkViewManager = networkViewManager;
	}
	
	public boolean isActive() {
		return active;
	}
	
	private void setUpCanvas(Object container) {
		if (container instanceof JComponent) {			
			JComponent component = (JComponent) container;
			Container focus = component;
			
			// Use the system's default version of OpenGL
			GLProfile profile = GLProfile.getDefault();
			GLCapabilities capabilities = new GLCapabilities(profile);

			// TODO: check if this line should be moved to graphics object
			// capabilities.setDoubleBuffered(true);
			
			// TODO: check whether to use GLCanvas or GLJPanel
			GLJPanel panel = new GLJPanel(capabilities);
			
			Graphics graphics = new Graphics(networkView, visualLexicon);

			panel.addGLEventListener(graphics);
	
			if (container instanceof JInternalFrame) {
				JInternalFrame frame = (JInternalFrame) container;
				Container pane = frame.getContentPane();
				
				focus = pane;
				graphics.trackInput(pane);
				
				pane.setLayout(new BorderLayout());
				pane.add(panel, BorderLayout.CENTER);
			} else {
				graphics.trackInput(component);
				
				component.setLayout(new BorderLayout());
				component.add(panel, BorderLayout.CENTER);
			}
			
			animator = new FPSAnimator(60);
			animator.add(panel);

			focus.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent event) {
					if (!event.isTemporary()) {
						System.out.println("Animator started for: " + this);
						animator.start();
					}
				}

				@Override
				public void focusLost(FocusEvent event) {
					if (!event.isTemporary()) {
						System.out.println("Animator stopped for: " + this);
						animator.stop();
					}
				}
			});
			
			active = true;
		}
	}
	
	@Override
	public View<CyNetwork> getViewModel() {
		return viewModel;
	}

	@Override
	public VisualLexicon getVisualLexicon() {
		return visualLexicon;
	}

	@Override
	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Printable createPrintable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image createImage(int width, int height) {
		// TODO Auto-generated method stub
		return null;
		//return new BufferedImage(0, width, height);
	}

	@Override
	public <V> Icon createIcon(VisualProperty<V> vp, V value, int width,
			int height) {
		// TODO Auto-generated method stub
		return null;
		//return new ImageIcon();
	}

	public void printCanvas(java.awt.Graphics graphics) {
		// TODO Auto-generated method stub
		
	}

}
