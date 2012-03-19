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

import org.cytoscape.application.events.SetCurrentRenderingEngineEvent;
import org.cytoscape.application.events.SetCurrentRenderingEngineListener;
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
		
		System.out.println("Main rendering engine created: " + this);
	}

	@Override
	protected Graphics getGraphicsInstance(CyNetworkView networkView,
			VisualLexicon visualLexicon) {
		
		return new Graphics(networkView, visualLexicon, new MainGraphicsHandler());
	}

	@Override
	protected SetCurrentRenderingEngineListener getSetCurrentRenderingEngineListener(
			final FPSAnimator animator) {
		final RenderingEngine<CyNetwork> renderingEngine = this;

		return new SetCurrentRenderingEngineListener() {
			
			@Override
			public void handleEvent(SetCurrentRenderingEngineEvent e) {
				if (e.getRenderingEngine() == renderingEngine) {
					System.out.println("Current network view changed, starting animator for " + this + ".");
					animator.start();
				} else {
					System.out.println("Current network view changed, stopping animator for " + this + ".");
					animator.stop();
				}
			}
		};
	}
}
