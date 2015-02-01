package org.baderlab.cy3d.internal.graphics;

import javax.swing.JInternalFrame;

import org.baderlab.cy3d.internal.data.GraphicsData;

/**
 * A GraphicsConfiguration object that is responsible for specifying how input, calculation, and drawing are
 * handled in a {@link RenderEventListener} object. 
 */
public interface GraphicsConfiguration {
		
	
	/**
	 * Allows the configuration to add anything it needs to the outermost swing container.
	 * Called once when the JInternalFrame is created.
	 * Called first, before initialize(GraphicsData) is called.
	 */
	public void initializeFrame(JInternalFrame container);
	
	
	/**
	 * Gives the GraphicsConfiguration access to the GraphicsData for the current renderer.
	 * Called once before rendering.
	 */
	public void initialize(GraphicsData graphicsData);
	
	
	/**
	 * This method is called to do any processing based on changes to the grapicsData
	 * as needed. It will be called every frame before drawScene() is called.
	 */
	public void update();
	
	/**
	 * This method is called to graphically render the current scene. It will be
	 * called every frame if the state of the network during each frame is desired
	 * to for visualization.
	 */
	public void drawScene();
	
	/**
	 * Called when the GraphicsHandler is about to be disposed, to perform any necessary cleanup
	 */
	public void dispose();
	
}


