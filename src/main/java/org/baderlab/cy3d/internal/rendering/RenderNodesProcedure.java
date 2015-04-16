package org.baderlab.cy3d.internal.rendering;

import java.awt.Color;
import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import org.baderlab.cy3d.internal.cytoscape.view.Cy3DVisualLexicon;
import org.baderlab.cy3d.internal.cytoscape.view.DetailLevel;
import org.baderlab.cy3d.internal.cytoscape.view.DetailLevelVisualProperty;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.rendering.shapes.ScalableShapeDrawer;
import org.baderlab.cy3d.internal.rendering.shapes.ScalableShapeDrawer.Detail;
import org.baderlab.cy3d.internal.rendering.shapes.ScalableShapeDrawer.Shape;
import org.baderlab.cy3d.internal.tools.RenderColor;
import org.baderlab.cy3d.internal.tools.SUIDToolkit;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.NodeShape;

public class RenderNodesProcedure implements GraphicsProcedure {

	private static final RenderColor DEFAULT_COLOR = new RenderColor(0.67, 0.67, 0.67);
	private static final RenderColor DEFAULT_SELECTED_COLOR = new RenderColor(0.73, 0.73, 0.6);
	private static final RenderColor DEFAULT_HOVER_COLOR = new RenderColor(0.5, 0.5, 0.7);
	
	/** The default radius of the spherical nodes */
	private static final float NODE_SIZE_RADIUS = 0.322f; // 0.015f
	
	private ScalableShapeDrawer shapeDrawer = new ScalableShapeDrawer();
	
	
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		shapeDrawer.initialize(gl);
	}
	
	
	private static Shape mapNodeShape(NodeShape nodeShape) {
		if(NodeShapeVisualProperty.TRIANGLE.equals(nodeShape))
			return Shape.SHAPE_TETRAHEDRON;
		if(NodeShapeVisualProperty.ELLIPSE.equals(nodeShape))
			return Shape.SHAPE_SPHERE;
		return Shape.SHAPE_CUBE;
	}

	private static Detail mapDetailLevel(DetailLevel detailLevel) {
		if(DetailLevelVisualProperty.DETAIL_MED.equals(detailLevel))
			return Detail.DETAIL_MED;
		if(DetailLevelVisualProperty.DETAIL_HIGH.equals(detailLevel))
			return Detail.DETAIL_HIGH;
		return Detail.DETAIL_LOW;
	}
	
	@Override
	public void execute(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();

		float[] specularReflection = { 0.46f, 0.46f, 0.46f, 1.0f };
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, FloatBuffer.wrap(specularReflection));
		gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 13);
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		float distanceScale = GraphicsData.DISTANCE_SCALE;
		float nodeSizeScale = 60;

		// networkView.updateView();
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			if(nodeView == null) {
				// MKTODO why does this happen?
				System.err.println("nodeView is null: networkView.getNodeViews() returns: " + networkView.getNodeViews());
				continue;
			}
			
			float x = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION).floatValue() / distanceScale;
			float y = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION).floatValue() / distanceScale;
			float z = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION).floatValue() / distanceScale;
			
			y = -y; // Cytoscape measures Y down from the top, OpenGL measures Y up from the bottom

			Double width  = nodeView.getVisualProperty(BasicVisualLexicon.NODE_WIDTH);
			Double height = nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT);
			Double depth  = nodeView.getVisualProperty(BasicVisualLexicon.NODE_DEPTH);
			
			// Draw it only if the visual property says it is visible
			if (nodeView.getVisualProperty(BasicVisualLexicon.NODE_VISIBLE) && graphicsData .getViewingVolume().inside(new Vector3(x, y, z), 1)) {
				
				long suid = nodeView.getModel().getSUID();
				
				// glLoadName() and glPushName() only support int, so we need to break the long SUID into two parts
				int upper = SUIDToolkit.upperInt(suid);
				int lower = SUIDToolkit.lowerInt(suid);
				
				gl.glPushMatrix();
				gl.glTranslatef(x, y, z);
				gl.glLoadName(upper);
				gl.glPushName(lower);
				
				chooseColor(gl, nodeView, graphicsData);
				//gl.glCallList(nodeListIndex);
				
				gl.glScalef(NODE_SIZE_RADIUS, NODE_SIZE_RADIUS, NODE_SIZE_RADIUS);
				
				
				
				if (width != null && height != null && depth != null) {
					gl.glScalef(width.floatValue() / nodeSizeScale, 
							height.floatValue() / nodeSizeScale, 
							depth.floatValue() / nodeSizeScale);
				}
				
				Shape shapeType = mapNodeShape(nodeView.getVisualProperty(BasicVisualLexicon.NODE_SHAPE));
				Detail detail = mapDetailLevel(networkView.getVisualProperty(Cy3DVisualLexicon.DETAIL_LEVEL)); 
				
				shapeDrawer.drawShape(gl, shapeType, detail);
				
				gl.glPopName();
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
		
		Long suid = nodeView.getModel().getSUID();
		
		if (nodeView.getVisualProperty(BasicVisualLexicon.NODE_SELECTED)) {
			// Make selected nodes appear greener
			color.multiplyRed(0.7, 0, 0.3);
			color.multiplyGreen(1.5, 0.5, 1);
			color.multiplyBlue(0.7, 0, 0.3);
		} 
		else if (suid.equals(graphicsData.getSelectionData().getHoverNodeIndex()) || graphicsData.getPickingData().getPickedNodeIndices().contains(suid)) {
			// Make hovered nodes appear bluer
			color.multiplyRed(0.7, 0, 0.7);
			color.multiplyGreen(0.7, 0, 0.7);
			color.multiplyBlue(1.5, 0.5, 1);
		}
		
		RenderColor.setNonAlphaColors(gl, color);
	}

}
