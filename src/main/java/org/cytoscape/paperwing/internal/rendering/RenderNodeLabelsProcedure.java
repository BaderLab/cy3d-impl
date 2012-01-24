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
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

import com.jogamp.opengl.util.awt.TextRenderer;

public class RenderNodeLabelsProcedure implements ReadOnlyGraphicsProcedure {

	private static final float TEXT_SCALE = 0.0005f;
	private static final Vector3 TEXT_OFFSET = new Vector3 (0, 0.04, 0);
	
	private static final float TEXT_CHARACTER_WIDTH = 0.612f;
	private static final int TEXT_FONT_SIZE = 72;
	
	private TextRenderer textRenderer;
	private Font defaultFont = new Font("Trebuchet MS", Font.PLAIN, TEXT_FONT_SIZE);
	
	public RenderNodeLabelsProcedure() {
		textRenderer = new TextRenderer(defaultFont);
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

		CyNetworkView networkView = graphicsData.getNetworkView();
		float distanceScale = graphicsData.getDistanceScale();
		float x, y, z;

		String text;
		
		textRenderer.setColor(Color.BLACK);
		
		gl.glPushMatrix();
		textRenderer.begin3DRendering();
		
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			x = nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION)
					.floatValue() / distanceScale;
			y = nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION)
					.floatValue() / distanceScale;
			z = nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION)
					.floatValue() / distanceScale;

			// Draw it only if the visual property says it is visible
			if (nodeView.getVisualProperty(MinimalVisualLexicon.NODE_VISIBLE)) {
				
//				gl.glTranslatef(x, y, z);
				
				gl.glColor3f(0.2f, 0.2f, 0.2f);
				
				text = nodeView.getVisualProperty(MinimalVisualLexicon.NODE_LABEL);
				
//				gl.glTranslatef((float) TEXT_OFFSET.x(),
//						(float) TEXT_OFFSET.y(),
//						(float) TEXT_OFFSET.z());
				
				if (text != null) {
					
//					gl.glScalef(TEXT_SCALE, TEXT_SCALE, TEXT_SCALE);
					// textRenderer.drawCenteredText(gl, text, TEXT_OFFSET);
					// textRenderer.drawCenteredText(gl, "aac");
					
//					text = "test" + nodeView.getSUID();
					textRenderer.draw3D(text, 
							x + (float) TEXT_OFFSET.x() - text.length() * (TEXT_CHARACTER_WIDTH / TEXT_FONT_SIZE), 
							y + (float) TEXT_OFFSET.y(), 
							z + (float) TEXT_OFFSET.z(), 
							TEXT_SCALE);
				}
			}
		}
		
		textRenderer.flush();
		textRenderer.endRendering();
		
		gl.glPopMatrix();
		
	}
}
