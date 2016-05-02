package org.baderlab.cy3d.internal.graphics;

import javax.swing.JComponent;

import org.baderlab.cy3d.internal.data.GraphicsData;

import com.google.common.eventbus.EventBus;

/**
 * The {@link RenderEventListener} uses an instance of GraphicsConfiguration to perform
 * the actual drawing of the scene. <br><br>
 * 
 * Contains life-cycle methods for initializing, updating, drawing the scene, and for 
 * cleaning up when the renderer shuts down. <br><br>
 * 
 * The GraphicsConfiguration may also register with the {@link EventBus} in order to respond
 * to events that may change how the scene is rendered.
 * 
 * For example:
 * 
 * <code>
 * <pre>
 * &#64;Override 
 * public void initialize(GraphicsData graphicsData) {
 *     // register with event bus
 *     graphicsData.getEventBus().register(this);
 * }
 * 
 * &#64;Subscribe 
 * public void handleShowLabelsEvent(ShowLabelsEvent e) {
 *     // handle event
 * }
 * </pre>
 * </code>
 */
public interface GraphicsConfiguration {
		
	
	/**
	 * Allows the configuration to add anything it needs to the outermost swing container.
	 * Called once when the JInternalFrame is created.
	 * Called first, before initialize(GraphicsData) is called.
	 */
	public void initializeFrame(JComponent container, JComponent inputComponent);
	
	
	/**
	 * Gives the GraphicsConfiguration access to the GraphicsData for the current renderer.
	 * Called once before rendering begins.
	 */
	public void initialize(GraphicsData graphicsData);
	
	
	/**
	 * This method is called to do any processing based on changes to the grapicsData
	 * as needed. It will be called every frame before drawScene() is called.
	 */
	public void update();
	
	/**
	 * This method is called to graphically render the current scene. It will be
	 * called every frame after update() is called.
	 */
	public void drawScene();
	
	/**
	 * Called when the GraphicsHandler is about to be disposed, to perform any necessary cleanup.
	 */
	public void dispose();
	
}


