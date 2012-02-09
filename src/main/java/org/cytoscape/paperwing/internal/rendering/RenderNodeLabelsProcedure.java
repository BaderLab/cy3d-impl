package org.cytoscape.paperwing.internal.rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.util.Set;

import javax.media.opengl.GL2;

import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.rendering.shapes.ScalableShapeDrawer.ShapeType;
import org.cytoscape.paperwing.internal.rendering.text.StringRenderer;
import org.cytoscape.paperwing.internal.tools.RenderColor;
import org.cytoscape.paperwing.internal.tools.RenderToolkit;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

import com.jogamp.opengl.util.awt.TextRenderer;

public class RenderNodeLabelsProcedure implements ReadOnlyGraphicsProcedure {

	private static final float TEXT_SCALE = 0.0005f;
	private static final Vector3 TEXT_OFFSET = new Vector3 (0, 0.04, 0);
	
	private static final float TEXT_CHARACTER_WIDTH = 0.612f;
	private static final int TEXT_FONT_SIZE = 9;
	
	private static final Color TEXT_DEFAULT_COLOR = Color.BLACK;
	
	private TextRenderer textRenderer;
	private Font defaultFont = new Font("SansSerif", Font.PLAIN, TEXT_FONT_SIZE);
	
	public RenderNodeLabelsProcedure() {
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		
		// Increase rendering efficiency; can set to true if desired
		textRenderer.setSmoothing(false);
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		
		textRenderer = new TextRenderer(defaultFont);

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
		
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			x = nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION)
					.floatValue() / distanceScale;
			y = nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION)
					.floatValue() / distanceScale;
			z = nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION)
					.floatValue() / distanceScale;

			// Draw it only if the visual property says it is visible
			if (nodeView.getVisualProperty(MinimalVisualLexicon.NODE_VISIBLE)) {
				
				gl.glColor3f(0.2f, 0.2f, 0.2f);
				
				text = nodeView.getVisualProperty(MinimalVisualLexicon.NODE_LABEL);
				
				if (text != null) {
					
					Vector3 text3dPosition = new Vector3(x, y, z);
					Vector3 screenCoordinates = RenderToolkit.convert3dToScreen(gl, text3dPosition, modelView, projection, viewPort);
//					System.out.println("Node label " + (new Vector3(x, y, z)) + " mapped to: " + screenCoordinates);
					
					Vector3 offsetFromCamera = text3dPosition.subtract(graphicsData.getCamera().getPosition());
					
					// Only draw the text if the front side of the camera faces it
					if (offsetFromCamera.magnitudeSquared() > Double.MIN_NORMAL 
							&& graphicsData.getCamera().getDirection().angle(offsetFromCamera) <= Math.PI / 2) {
						
						// TODO: Check if there is a way around this cast
						textColor = (Color) nodeView.getVisualProperty(MinimalVisualLexicon.NODE_LABEL_COLOR);
						if (textColor == null) {
							
							// Use black as default if no node label color was found
							textColor = TEXT_DEFAULT_COLOR;
						}
						
						textRenderer.setColor(textColor);
						textRenderer.draw(text, (int) screenCoordinates.x() - findTextScreenWidth(text) / 2, (int) screenCoordinates.y());		
					}
				}
			}
		}
		
		textRenderer.flush();
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
