package org.cytoscape.paperwing.internal.rendering;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.rendering.shapes.ScalableShapeDrawer;
import org.cytoscape.paperwing.internal.rendering.shapes.ScalableShapeDrawer.ShapeType;
import org.cytoscape.paperwing.internal.tools.NetworkToolkit;
import org.cytoscape.paperwing.internal.tools.RenderColor;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.values.NodeShape;

public class RenderNodesProcedure implements ReadOnlyGraphicsProcedure {

	private static final RenderColor DEFAULT_COLOR = 
		new RenderColor(0.67, 0.67, 0.67);
	private static final RenderColor DEFAULT_SELECTED_COLOR = 
		new RenderColor(0.73, 0.73, 0.6);
	private static final RenderColor DEFAULT_HOVER_COLOR = 
		new RenderColor(0.5, 0.5, 0.7);
	
	/** The default radius of the spherical nodes */
	private static final float NODE_SIZE_RADIUS = 0.322f; // 0.015f
	
	private Map<NodeShape, ShapeType> cytoscapeShapeMap;
	private ScalableShapeDrawer shapeDrawer;
	
	public RenderNodesProcedure() {
		shapeDrawer = new ScalableShapeDrawer();
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		
		shapeDrawer.initialize(gl);
		
		cytoscapeShapeMap = new HashMap<NodeShape, ShapeType>(8);
		cytoscapeShapeMap.put(NodeShapeVisualProperty.TRIANGLE, ShapeType.SHAPE_TETRAHEDRON);
		cytoscapeShapeMap.put(NodeShapeVisualProperty.DIAMOND, ShapeType.SHAPE_CUBE);
		cytoscapeShapeMap.put(NodeShapeVisualProperty.ELLIPSE, ShapeType.SHAPE_SPHERE);
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();

		float[] specularReflection = { 0.46f, 0.46f, 0.46f, 1.0f };
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR,
				FloatBuffer.wrap(specularReflection));
		gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 21);
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		float distanceScale = graphicsData.getDistanceScale();
		Set<Integer> selectedNodeIndices = graphicsData.getSelectionData()
				.getSelectedNodeIndices();
		int hoverNodeIndex = graphicsData.getSelectionData().getHoverNodeIndex();

		float x, y, z;
		Number width, height, depth;
		int index;
		ShapeType shapeType;
		
		// networkView.updateView();
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			x = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION)
					.floatValue() / distanceScale;
			y = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION)
					.floatValue() / distanceScale;
			z = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION)
					.floatValue() / distanceScale;

			width = nodeView.getVisualProperty(BasicVisualLexicon.NODE_WIDTH);
			height = nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT);
			depth = nodeView.getVisualProperty(BasicVisualLexicon.NODE_DEPTH);
			
			index = nodeView.getModel().getIndex();
			
			// gl.glLoadName(33);

			// Draw it only if the visual property says it is visible
			if (nodeView.getVisualProperty(BasicVisualLexicon.NODE_VISIBLE)) {
				gl.glPushMatrix();
				gl.glTranslatef(x, y, z);
				gl.glLoadName(index);
				
				chooseColor(gl, nodeView, graphicsData);
				//gl.glCallList(nodeListIndex);
				
				gl.glScalef(NODE_SIZE_RADIUS, 
						NODE_SIZE_RADIUS,
						NODE_SIZE_RADIUS);
				
				if (width != null && height != null && depth != null) {
					gl.glScalef(width.floatValue() / distanceScale, 
							height.floatValue() / distanceScale, 
							depth.floatValue() / distanceScale);
				}
				
				shapeType = cytoscapeShapeMap.get(nodeView.getVisualProperty(BasicVisualLexicon.NODE_SHAPE));
				
				if (shapeType != null) {
					shapeDrawer.drawShape(gl, shapeType);
				} else {
					shapeDrawer.drawShape(gl, ShapeType.SHAPE_CUBE);
				}
				
				gl.glPopMatrix();
			}
		}
	}
	
	private void chooseColor(GL2 gl, View<CyNode> nodeView, GraphicsData graphicsData) {
		Color visualPropertyColor = null;
		visualPropertyColor = (Color) nodeView.getVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR);
		
		RenderColor color = new RenderColor(DEFAULT_COLOR);
		
		if (visualPropertyColor != null) {
			color.set((double) visualPropertyColor.getRed() / 255, 
					(double) visualPropertyColor.getGreen() / 255, 
					(double) visualPropertyColor.getBlue() / 255);
		}
		
		if (nodeView.getVisualProperty(BasicVisualLexicon.NODE_SELECTED)) {
			
			// Make selected nodes appear greener
			color.multiplyRed(0.7, 0, 0.7);
			color.multiplyGreen(1.5, 0.5, 1);
			color.multiplyBlue(0.7, 0, 0.7);
		} else if (nodeView.getModel().getIndex() == graphicsData.getSelectionData().getHoverNodeIndex()) {
			// Make hovered nodes appear bluer
			color.multiplyRed(0.7, 0, 0.7);
			color.multiplyGreen(0.7, 0, 0.7);
			color.multiplyBlue(1.5, 0.5, 1);
		}
		
		RenderColor.setNonAlphaColors(gl, color);
	}

}
