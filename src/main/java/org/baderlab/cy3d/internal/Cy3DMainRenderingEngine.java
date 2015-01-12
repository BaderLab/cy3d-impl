package org.baderlab.cy3d.internal;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import org.baderlab.cy3d.internal.cytoscape.view.Cy3DNetworkView;
import org.baderlab.cy3d.internal.graphics.RenderEventListener;
import org.baderlab.cy3d.internal.graphics.MainGraphicsConfiguration;
import org.baderlab.cy3d.internal.input.handler.ToolPanel;
import org.cytoscape.application.events.SetCurrentRenderingEngineEvent;
import org.cytoscape.application.events.SetCurrentRenderingEngineListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;

import com.jogamp.opengl.util.FPSAnimator;

/** This class represents a Cy3DRenderingEngine, responsible for
 * creating a rendering of a {@link CyNetwork}
 * 
 * @author Paperwing (Yue Dong)
 */
public class Cy3DMainRenderingEngine extends Cy3DRenderingEngine {

	private MainGraphicsConfiguration mainGraphicsHandler = new MainGraphicsConfiguration();
	
	public Cy3DMainRenderingEngine(Cy3DNetworkView viewModel, VisualLexicon visualLexicon) {
		super(viewModel, visualLexicon);
	}

	@Override
	protected RenderEventListener getRenderEventListener(CyNetworkView networkView, VisualLexicon visualLexicon) {
		return new RenderEventListener(networkView, visualLexicon, mainGraphicsHandler);
	}

	@Override
	protected SetCurrentRenderingEngineListener getSetCurrentRenderingEngineListener(final FPSAnimator animator) {
		return new SetCurrentRenderingEngineListener() {
			
			@Override
			public void handleEvent(SetCurrentRenderingEngineEvent e) {
				if (e.getRenderingEngine() == Cy3DMainRenderingEngine.this) {
					animator.start();
				} else {
					animator.stop();
				}
			}
		};
	}
	
	@Override
	public void setUpCanvas(JComponent container) {
		super.setUpCanvas(container);
		
		if(container instanceof JInternalFrame) {
			JInternalFrame frame = (JInternalFrame) container;
			ToolPanel toolPanel = ToolPanel.createFor(frame);
			toolPanel.addMouseModeChangeListener(mainGraphicsHandler.getMouseModeChangeListener());
		}
		
	}
	
	
}
