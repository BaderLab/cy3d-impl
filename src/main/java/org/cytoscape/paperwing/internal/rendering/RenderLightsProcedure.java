package org.cytoscape.paperwing.internal.rendering;

import javax.media.opengl.GL2;

import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.data.LightingData;
import org.cytoscape.paperwing.internal.lighting.Light;
import org.cytoscape.paperwing.internal.rendering.shapes.ScalableShapeDrawer;
import org.cytoscape.paperwing.internal.rendering.shapes.ScalableShapeDrawer.ShapeType;

/**
 * This {@link ReadOnlyGraphicsProcedure} object is responsible for drawing the representations of scene lights
 * to help with visualizing their locations.
 */
public class RenderLightsProcedure implements ReadOnlyGraphicsProcedure {

	private ScalableShapeDrawer shapeDrawer;
	
	public RenderLightsProcedure() {
		shapeDrawer = new ScalableShapeDrawer();
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		
		shapeDrawer.initialize(gl);
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		
		GL2 gl = graphicsData.getGlContext();
		
		LightingData lightingData = graphicsData.getLightingData();
		
		// Draw the light at index 0
		Light light = lightingData.getLight(0);
		
		if (lightingData.displayLight(0)) {
			float[] lightPosition = light.getPosition();
			
			gl.glPushMatrix();
			gl.glTranslatef(lightPosition[0], lightPosition[1], lightPosition[2]);
			
			gl.glColor3f(1.0f, 1.0f, 0.6f);
			shapeDrawer.drawShape(gl, ShapeType.SHAPE_SPHERE);
					
			gl.glPopMatrix();
		}
	}

}
