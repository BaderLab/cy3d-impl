package org.cytoscape.paperwing.internal.graphics;

import javax.media.opengl.GL2;

import org.cytoscape.paperwing.internal.Graphics;

public interface ReadOnlyGraphicsProcedure {
	public void initialize(GL2 gl, GraphicsData graphicsData);
	
	public void execute(GL2 gl, GraphicsData graphicsData);
}
