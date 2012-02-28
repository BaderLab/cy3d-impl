package org.cytoscape.paperwing.internal.rendering;

import java.awt.Color;
import java.awt.Font;

import javax.media.opengl.GL2;

import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.RenderToolkit;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.jogamp.opengl.util.awt.TextRenderer;

// Below will be used for JOGL 2.0-b45-20111219
/*
import com.jogamp.graph.curve.opengl.RenderState;
import com.jogamp.graph.curve.opengl.TextRenderer;
import com.jogamp.graph.curve.opengl.GLRegion;
import com.jogamp.graph.font.FontFactory;
*/

public class RenderNodeLabelsProcedure implements ReadOnlyGraphicsProcedure {

	private static final int TEXT_FONT_SIZE = 9;
	private static final String DEFAULT_FONT_NAME = "SansSerif";
	private static final Color TEXT_DEFAULT_COLOR = Color.BLACK;
	
	private static final Font TEXT_DEFAULT_FONT = new Font(DEFAULT_FONT_NAME, Font.PLAIN, TEXT_FONT_SIZE);
	
	private TextRenderer textRenderer;
	
	public RenderNodeLabelsProcedure() {		
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		
		textRenderer = new TextRenderer(TEXT_DEFAULT_FONT);
		
		// Increase rendering efficiency; can set to true if desired
		// textRenderer.setSmoothing(false);
		
		// Temporarily removed -- pausing JOGL update to 2.0-b45-20111219
//		textRenderer = TextRenderer.create(RenderState.getRenderState(gl), GLRegion.TWO_PASS_DEFAULT_TEXTURE_UNIT);
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		
		
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		float distanceScale = graphicsData.getDistanceScale();
		float x, y, z;

		// Store the current modelview, projection, and viewport matrices
		double modelView[] = new double[16];
		double projection[] = new double[16];
		int viewPort[] = new int[4];
		
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelView, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection, 0);
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewPort, 0);
		
		String text;
		Color textColor;
		
		gl.glPushMatrix();
		textRenderer.beginRendering(graphicsData.getScreenWidth(), graphicsData.getScreenHeight(), true);
		// textRenderer.drawString3D(arg0, arg1, arg2, arg3, arg4, arg5)
		
		// textRenderer.beginRendering(graphicsData.getScreenWidth(), graphicsData.getScreenHeight(), true);
		// textRenderer.createString(gl, null, 0, "test").
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			x = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION)
					.floatValue() / distanceScale;
			y = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION)
					.floatValue() / distanceScale;
			z = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION)
					.floatValue() / distanceScale;

			// Draw it only if the visual property says it is visible
			if (nodeView.getVisualProperty(BasicVisualLexicon.NODE_VISIBLE)) {
				
				text = nodeView.getVisualProperty(BasicVisualLexicon.NODE_LABEL);
				
				if (text != null) {
					
					Vector3 text3dPosition = new Vector3(x, y, z);
					Vector3 screenCoordinates = RenderToolkit.convert3dToScreen(gl, text3dPosition, modelView, projection, viewPort);

					Vector3 offsetFromCamera = text3dPosition.subtract(graphicsData.getCamera().getPosition());
					
					// Only draw the text if the front side of the camera faces it
					if (offsetFromCamera.magnitudeSquared() > Double.MIN_NORMAL 
							&& graphicsData.getCamera().getDirection().angle(offsetFromCamera) <= Math.PI / 2) {
						
						// TODO: Check if there is a way around this cast
						textColor = (Color) nodeView.getVisualProperty(BasicVisualLexicon.NODE_LABEL_COLOR);
						if (textColor == null) {
							
							// Use black as default if no node label color was found
							textColor = TEXT_DEFAULT_COLOR;
						}
			
						// Below to be used for JOGL 2.0-b45-20111219's new TextRenderer
//						gl.glColor3f((float) textColor.getRed() / 255, 
//								(float) textColor.getGreen() / 255, 
//								(float) textColor.getBlue() / 255);
						
						
//						textRenderer.drawString3D(gl, FontFactory.getDefault().getDefault(), 
//								text, new float[]{1.0f, 1.0f, 2.0f}, TEXT_FONT_SIZE, 1024);
								
						textRenderer.setColor(textColor);
						textRenderer.draw(text, (int) screenCoordinates.x() - findTextScreenWidth(text) / 2, (int) screenCoordinates.y());		
						textRenderer.flush();
					}
				}
			}
		}
		
		textRenderer.endRendering();
		
		gl.glPopMatrix();
		
	}
	
	private int findTextScreenWidth(String text) {
		int width = 0;
		
		for (int i = 0; i < text.length(); i++) {
			width += textRenderer.getCharWidth(text.charAt(i));
		}
		
		return width;
	}
}
