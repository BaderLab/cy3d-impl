package org.baderlab.cy3d.internal.rendering;

import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NODE_LABEL_FONT_FACE;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NODE_LABEL_FONT_SIZE;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jogamp.opengl.GL2;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.tools.RenderToolkit;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkViewSnapshot;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.jogamp.opengl.util.awt.TextRenderer;

// Below will be used for JOGL 2.0-b45-20111219
/*
import com.jogamp.graph.curve.opengl.RenderState;
import com.jogamp.graph.curve.opengl.TextRenderer;
import com.jogamp.graph.curve.opengl.GLRegion;
import com.jogamp.graph.font.FontFactory;
*/

public class RenderNodeLabelsProcedure implements GraphicsProcedure {

	private static final int TEXT_FONT_SIZE = 9;
	private static final String DEFAULT_FONT_NAME = "SansSerif";
	private static final Color TEXT_DEFAULT_COLOR = Color.BLACK;
	
	private static final Font TEXT_DEFAULT_FONT = new Font(DEFAULT_FONT_NAME, Font.PLAIN, TEXT_FONT_SIZE);
	
	private final TextRendererCache textRendererCache = new TextRendererCache();
	
	
	public RenderNodeLabelsProcedure() {		
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		if(!graphicsData.getShowLabels())
			return;
		
		GL2 gl = graphicsData.getGlContext();
		
		CyNetworkViewSnapshot networkView = graphicsData.getNetworkSnapshot();
		float distanceScale = GraphicsData.DISTANCE_SCALE;

		// Store the current modelview, projection, and viewport matrices
		double modelView[] = new double[16];
		double projection[] = new double[16];
		int viewPort[] = new int[4];
		
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelView, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection, 0);
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewPort, 0);
		
		gl.glPushMatrix();
		
		
		for(View<CyNode> nodeView : networkView.getNodeViews()) {
			// Draw it only if the visual property says it is visible
			if (nodeView.getVisualProperty(BasicVisualLexicon.NODE_VISIBLE)) {
				float x = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION).floatValue() / distanceScale;
				float y = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION).floatValue() / distanceScale;
				float z = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION).floatValue() / distanceScale;
				y = -y; // Cytoscape measures Y down from the top, OpenGL measures Y up from the bottom

				String text = nodeView.getVisualProperty(BasicVisualLexicon.NODE_LABEL);
				if(text != null) {
					Vector3 text3dPosition = new Vector3(x, y, z);
					Vector3 screenCoordinates = RenderToolkit.convert3dToScreen(gl, text3dPosition, modelView, projection, viewPort);
					Vector3 offsetFromCamera = text3dPosition.subtract(graphicsData.getCamera().getPosition());
					
					// Only draw the text if the front side of the camera faces it
					if (offsetFromCamera.magnitudeSquared() > Double.MIN_NORMAL && graphicsData.getViewingVolume().inside(text3dPosition, GraphicsData.NEAR_Z / 2)) {
						Font font = getLabelFont(nodeView);
						Color color = getLabelColor(nodeView);
						
						TextRenderer textRenderer = textRendererCache.get(font);
						textRenderer.beginRendering(graphicsData.getScreenWidth(), graphicsData.getScreenHeight(), true);
						try {
							textRenderer.setColor(color);
							int width = findTextScreenWidth(textRenderer, font, text);
							textRenderer.draw(text, (int) screenCoordinates.x() - width / 2, (int) screenCoordinates.y());
						} finally {
							// textRenderer.flush();
							textRenderer.endRendering();
						}
					}
				}
			}
		}
		
		gl.glPopMatrix();
	}
	
	
	private static Color getLabelColor(View<CyNode> nodeView) {
		Paint textPaint = nodeView.getVisualProperty(BasicVisualLexicon.NODE_LABEL_COLOR);
		if(textPaint instanceof Color) {
			return (Color) textPaint;
		}
		return TEXT_DEFAULT_COLOR; // Use black as default if no node label color was found
	}
	
	private static Font getLabelFont(View<CyNode> nodeView) {
		Font font = nodeView.getVisualProperty(NODE_LABEL_FONT_FACE);
		if(font == null)
			return TEXT_DEFAULT_FONT;
		Number size = nodeView.getVisualProperty(NODE_LABEL_FONT_SIZE);
		if(size == null)
			return font;
		return font.deriveFont(size.floatValue());
	}
	
	private static int findTextScreenWidth(TextRenderer textRenderer, Font font, String text) {
		int width = 0;
		for(int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			try {
				width += fastGetWidth(textRenderer, font, c);
			} catch (InternalError e) {
				// workaround for bug in jogl, see http://forum.jogamp.org/FontRenderContext-td4035841.html
				width += slowGetWidth(textRenderer, font, c);
			}
		}
		return width;
	}
	
	private static int fastGetWidth(TextRenderer textRenderer, Font font, char c) {
		return (int) textRenderer.getCharWidth(c);
	}
	
	private static int slowGetWidth(TextRenderer textRenderer, Font font, char c) {
		FontRenderContext fontRenderContext;
		try {
			Method method = textRenderer.getClass().getDeclaredMethod("getFontRenderContext");
			method.setAccessible(true);
			fontRenderContext = (FontRenderContext) method.invoke(textRenderer);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return 10;
		}
		
		int[] singleUnicode = { c };
		GlyphVector gv = font.createGlyphVector(fontRenderContext, singleUnicode);
		return (int) gv.getGlyphMetrics(0).getAdvance();
	}
}
