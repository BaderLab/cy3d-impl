package org.baderlab.cy3d.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.print.Printable;
import java.util.Properties;

import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import org.cytoscape.application.events.SetCurrentRenderingEngineListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.view.presentation.RenderingEngine;

import com.jogamp.opengl.util.FPSAnimator;

/** This class represents the RenderingEngine object for the smaller
 * navigation window in Cytoscape.
 * 
 * It is expected that this class will be expanded on in the near future
 * 
 * @author Paperwing (Yue Dong)
 */
public class WindBirdsEyeRenderingEngine extends WindRenderingEngine {

	public WindBirdsEyeRenderingEngine(Object container, View<CyNetwork> viewModel,
			VisualLexicon visualLexicon) {
		super(container, viewModel, visualLexicon);
	}

	@Override
	protected Graphics getGraphicsInstance(CyNetworkView networkView,
			VisualLexicon visualLexicon) {
		
		return new Graphics(networkView, visualLexicon, new BirdsEyeGraphicsHandler());
	}

	@Override
	protected SetCurrentRenderingEngineListener getSetCurrentRenderingEngineListener(
			FPSAnimator animator) {
		
		// Cytoscape creates a new RenderingEngine instance every time the current RenderingEngine changes
		return null;
	}
}