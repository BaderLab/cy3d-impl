package org.baderlab.cy3d.internal;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import org.baderlab.cy3d.internal.command.ToolPanel;
import org.baderlab.cy3d.internal.cytoscape.view.Cy3DNetworkView;
import org.baderlab.cy3d.internal.graphics.GraphicsEventHandler;
import org.baderlab.cy3d.internal.graphics.MainGraphicsHandler;
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

	private GraphicsEventHandler graphics;
	
	public Cy3DMainRenderingEngine(Cy3DNetworkView viewModel, VisualLexicon visualLexicon) {
		super(viewModel, visualLexicon);
	}

	@Override
	protected GraphicsEventHandler getGraphicsInstance(CyNetworkView networkView, VisualLexicon visualLexicon) {
		return graphics = new GraphicsEventHandler(networkView, visualLexicon, new MainGraphicsHandler());
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
			graphics.trackSettings(toolPanel.getSettingsData()); // MKTODO this has to change, use observer instead
		}
	}
	
}
