package org.cytoscape.paperwing.internal.rendering;

import java.util.List;

import javax.media.opengl.GL2;

import org.cytoscape.paperwing.internal.Graphics;
import org.cytoscape.paperwing.internal.data.GraphicsData;

public interface ReadOnlyGraphicsProcedure {
	public void initialize(GraphicsData graphicsData);
	
	public void execute(GraphicsData graphicsData);
}
