package org.cytoscape.paperwing.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.print.Printable;
import java.util.Properties;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.view.presentation.RenderingEngine;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;

/** This class represents a WindRenderingEngine, responsible for
 * creating a rendering of a {@link CyNetwork}
 * 
 * @author Paperwing (Yue Dong)
 */
public class WindMainRenderingEngine extends WindRenderingEngine {

	public WindMainRenderingEngine(Object container, View<CyNetwork> viewModel,
			VisualLexicon visualLexicon) {
		super(container, viewModel, visualLexicon);
	}

	@Override
	protected Graphics getGraphicsInstance(CyNetworkView networkView,
			VisualLexicon visualLexicon) {
		
		return new Graphics(networkView, visualLexicon, new MainGraphicsHandler());
	}
	
	
	@Override
	protected void setUpAnimatorStarting(Container container, FPSAnimator animator) {
		container.addFocusListener(getContainerFocusListener(animator));
	}
	
	private FocusListener getContainerFocusListener(final FPSAnimator animator) {

		return new FocusListener() {
			
			@Override
			public void focusGained(FocusEvent event) {
				if (!event.isTemporary()) {
					//System.out.println("Animator started for: " + this);
					animator.start();
				}
			}

			@Override
			public void focusLost(FocusEvent event) {
				if (!event.isTemporary()) {
					//System.out.println("Animator stopped for: " + this);
					animator.stop();
				}
			}
		};
	}

	
}
