package org.baderlab.cy3d.internal;

import org.cytoscape.application.NetworkViewRenderer;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.presentation.RenderingEngineFactory;

/**
 * This is the main service that is registered with Cytoscape so that it
 * picks up the 3D renderer.
 * 
 * @author mkucera
 */
public class Cy3DNetworkViewRenderer implements NetworkViewRenderer {

	public static final String ID = "org.baderlab.cy3d";
	public static final String DISPLAY_NAME = "Cytoscape 3D";
	
	private final CyNetworkViewFactory networkViewFactory;
	private final RenderingEngineFactory<CyNetwork> mainFactory;
	private final RenderingEngineFactory<CyNetwork> birdsEyeFactory;
	
	public Cy3DNetworkViewRenderer(CyNetworkViewFactory networkViewFactory, 
			                       RenderingEngineFactory<CyNetwork> mainFactory, 
			                       RenderingEngineFactory<CyNetwork> birdsEyeFactory) {
		
		this.networkViewFactory = networkViewFactory;
		this.mainFactory = mainFactory;
		this.birdsEyeFactory = birdsEyeFactory;
	}
	
	
	@Override
	public String getId() {
		return ID;
	}

	@Override
	public CyNetworkViewFactory getNetworkViewFactory() {
		return networkViewFactory;
	}

	@Override
	public RenderingEngineFactory<CyNetwork> getRenderingEngineFactory(String context) {
		switch(context) {
			default:
			case DEFAULT_CONTEXT: return mainFactory;
			case BIRDS_EYE_CONTEXT: return birdsEyeFactory;
			case VISUAL_STYLE_PREVIEW_CONTEXT: return null; // MKTODO what to return here?
		}
	}

	@Override
	public String toString() {
		return DISPLAY_NAME;
	}
}
