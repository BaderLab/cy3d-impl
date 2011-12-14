package org.cytoscape.paperwing.internal.graphics;

import java.util.List;

import javax.media.opengl.GL2;

import org.cytoscape.paperwing.internal.Graphics;

public interface ReadOnlyGraphicsProcedure {
	public void initialize(GraphicsData graphicsData);
	
	public void execute(GraphicsData graphicsData);
}
