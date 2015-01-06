package org.baderlab.cy3d.internal.rendering;

import java.util.List;

import javax.media.opengl.GL2;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.graphics.GraphicsEventHandler;

/**
 * This class represents a rendering procedure. Rendering procedures
 * are meant to only read provided data and not write any data. They
 * are usually freely allowed to read the given data to carry out its
 * rendering task (such as drawing all the nodes of a network, or 
 * the drag selection box).
 * 
 * @author Yue Dong (Paperwing)
 */
public interface ReadOnlyGraphicsProcedure {
	
	/**
	 * Initialize the graphics procedure, if any intialization is needed.
	 * 
	 * @param graphicsData The {@link GraphicsData} object containing 
	 * relevant information about the current state of rendering. This class
	 * is permitted generally only to read from this data object and not do
	 * any writing.
	 */
	public void initialize(GraphicsData graphicsData);
	
	/** 
	 * Execute the graphics procedure.
	 * 
	 * @param graphicsData The {@link GraphicsData} object containing information
	 * about the current state of rendering. The {@link ReadOnlyGraphicsProcedure}
	 * is allowed to freely read from this data object to carry out its task, but it 
	 * is generally not permitted to write to it.
	 */
	public void execute(GraphicsData graphicsData);
}
