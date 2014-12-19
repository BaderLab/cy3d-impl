package org.baderlab.cy3d.internal;

import javax.swing.JInternalFrame;

import org.baderlab.cy3d.internal.graphics.Graphics;
import org.baderlab.cy3d.internal.graphics.MainGraphicsHandler;
import org.cytoscape.application.events.SetCurrentRenderingEngineEvent;
import org.cytoscape.application.events.SetCurrentRenderingEngineListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngine;

import com.jogamp.opengl.util.FPSAnimator;

/** This class represents a Cy3DRenderingEngine, responsible for
 * creating a rendering of a {@link CyNetwork}
 * 
 * @author Paperwing (Yue Dong)
 */
public class Cy3DMainRenderingEngine extends Cy3DRenderingEngine {

	private Graphics graphics;
	
	public Cy3DMainRenderingEngine(Object container, View<CyNetwork> viewModel, VisualLexicon visualLexicon) {
		super(container, viewModel, visualLexicon);
	}

	@Override
	protected Graphics getGraphicsInstance(CyNetworkView networkView, VisualLexicon visualLexicon) {
		return graphics = new Graphics(networkView, visualLexicon, new MainGraphicsHandler());
	}

	@Override
	protected SetCurrentRenderingEngineListener getSetCurrentRenderingEngineListener(final FPSAnimator animator) {
		final RenderingEngine<CyNetwork> renderingEngine = this;

		return new SetCurrentRenderingEngineListener() {
			
			@Override
			public void handleEvent(SetCurrentRenderingEngineEvent e) {
				if (e.getRenderingEngine() == renderingEngine) {
					animator.start();
				} else {
					animator.stop();
				}
			}
		};
	}
	
	@Override
	public void setUpCanvas(Object container) {
		super.setUpCanvas(container);
		
		if(container instanceof JInternalFrame) {
			JInternalFrame frame = (JInternalFrame) container;
			ToolPanel toolPanel = ToolPanel.createFor(frame);
			graphics.trackSettings(toolPanel.getSettingsData());
		}
	}
	
}
