package org.baderlab.cy3d.internal.rendering;

import java.awt.Color;
import java.awt.Paint;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.tools.SimpleCamera;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class ResetSceneProcedure implements ReadOnlyGraphicsProcedure {

	@Override
	public void initialize(GraphicsData graphicsData) {
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		SimpleCamera camera = graphicsData.getCamera();
		
		// Obtain the desired network background color
		Color backgroundColor = (Color) 
			graphicsData.getNetworkView().getVisualProperty(
					BasicVisualLexicon.NETWORK_BACKGROUND_PAINT);
		
		if (backgroundColor != null) {
			gl.glClearColor(backgroundColor.getRed() / 255f, 
					backgroundColor.getGreen() / 255f, 
					backgroundColor.getBlue() / 255f, 1.0f);
		}
		
		// Reset scene
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
	}

}
