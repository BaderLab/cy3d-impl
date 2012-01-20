package org.cytoscape.paperwing.internal.rendering;

import java.util.Set;

import javax.media.opengl.GL2;

import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.rendering.shapes.ScalableShapeDrawer.ShapeType;
import org.cytoscape.paperwing.internal.rendering.text.TextRenderer;
import org.cytoscape.paperwing.internal.tools.RenderColor;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class RenderLabelsProcedure implements ReadOnlyGraphicsProcedure {

	private static final float TEXT_SCALE = 0.045f;
	private static final Vector3 TEXT_OFFSET = new Vector3 (0, 0.04, 0);
	
	private TextRenderer textRenderer;
	
	public RenderLabelsProcedure() {
		textRenderer = new TextRenderer();
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		
		textRenderer.initialize(gl);
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();

		CyNetworkView networkView = graphicsData.getNetworkView();
		float distanceScale = graphicsData.getDistanceScale();
		float x, y, z;

		String text;
		
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			x = nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION)
					.floatValue() / distanceScale;
			y = nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION)
					.floatValue() / distanceScale;
			z = nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION)
					.floatValue() / distanceScale;

			// Draw it only if the visual property says it is visible
			if (nodeView.getVisualProperty(MinimalVisualLexicon.NODE_VISIBLE)) {
				gl.glPushMatrix();
				gl.glTranslatef(x, y, z);
				
				gl.glColor3f(0.2f, 0.2f, 0.2f);
				
				text = nodeView.getVisualProperty(MinimalVisualLexicon.NODE_LABEL);
				
				gl.glTranslatef((float) TEXT_OFFSET.x(),
						(float) TEXT_OFFSET.y(),
						(float) TEXT_OFFSET.z());
				
				if (text != null) {
					gl.glScalef(TEXT_SCALE, TEXT_SCALE, TEXT_SCALE);
					// textRenderer.drawCenteredText(gl, text, TEXT_OFFSET);
					// textRenderer.drawCenteredText(gl, "aac");
				}
				
				gl.glPopMatrix();
			}
		}
	}

}
