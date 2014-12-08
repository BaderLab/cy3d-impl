package org.baderlab.cy3d.internal.rendering;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.Collection;

import javax.media.opengl.GL2;

import org.baderlab.cy3d.internal.cytoscape.edges.AugmentedEdgeContainer;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.rendering.shapes.EdgeShapeDrawer;
import org.baderlab.cy3d.internal.rendering.shapes.EdgeShapeDrawer.EdgeShapeType;
import org.baderlab.cy3d.internal.tools.RenderColor;
import org.baderlab.cy3d.internal.tools.RenderToolkit;
import org.baderlab.cy3d.internal.tools.SUIDToolkit;
import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;

public class RenderArcEdgesProcedure implements ReadOnlyGraphicsProcedure {

	private static final float SEGMENT_RADIUS = 0.013f; // 0.007 default
	private static final int SEGMENT_SLICES = 4;
	private static final int SEGMENT_STACKS = 1;
	
	private static final double MIN_LENGTH = Double.MIN_NORMAL;
	
	private static final RenderColor DEFAULT_COLOR = 
		new RenderColor(0.67, 0.67, 0.67);
	
	private static final float DASHED_EDGE_RADIUS = 0.012f;
	private static final float DASHED_EDGE_LENGTH = 0.05f;
	
	private static final float DOTTED_EDGE_RADIUS = 0.017f;

	
	/**
	 * The number of straight segments used to approximate a curved edge
	 */
	private static final int NUM_SEGMENTS = 8;
	
	private EdgeShapeDrawer shapeDrawer;
	
	private float edgeRadiusFactor = 1.0f;
		
	public RenderArcEdgesProcedure() {
		shapeDrawer = new EdgeShapeDrawer();
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		shapeDrawer.initialize(graphicsData.getGlContext());
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		GL2 gl = graphicsData.getGlContext();
		
		float[] specularReflection = { 0.1f, 0.1f, 0.1f, 1.0f };
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR,
				FloatBuffer.wrap(specularReflection));
		gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 1);
		
		double distanceScale = graphicsData.getDistanceScale();
		
		Collection<AugmentedEdgeContainer> edgeContainers = graphicsData.getEdgeAnalyser().getAnalyzedEdges(networkView, distanceScale, graphicsData.getFramesElapsed());
		View<CyEdge> edgeView;

		Number edgeWidth;
		
		for (AugmentedEdgeContainer container : edgeContainers) {
			edgeView = container.getEdgeView();
			
			if (container.isSufficientLength() && edgeView.getVisualProperty(BasicVisualLexicon.EDGE_VISIBLE)) {
				
				edgeWidth = edgeView.getVisualProperty(BasicVisualLexicon.EDGE_WIDTH);
				
				if (edgeWidth != null) {
					edgeRadiusFactor = edgeWidth.floatValue() / 2;
				} else {
					edgeRadiusFactor = 1.0f;
				}
				
				// Set color
				chooseColor(gl, edgeView, graphicsData);
				
				// Load name for edge picking
				long suid = edgeView.getModel().getSUID();
				int upper = SUIDToolkit.upperInt(suid);
				int lower = SUIDToolkit.lowerInt(suid);
				
				gl.glLoadName(upper);
				gl.glPushName(lower);
				
				// General points along the arc
				Vector3[] points = container.getCoordinates();
				

				// Draw the correct type of edge depending on the visual property
				if (edgeView.getVisualProperty(BasicVisualLexicon.EDGE_LINE_TYPE) == LineTypeVisualProperty.EQUAL_DASH) {
					drawDashedArc(gl, points);
				} else if (edgeView.getVisualProperty(BasicVisualLexicon.EDGE_LINE_TYPE) == LineTypeVisualProperty.DOT) {
					drawDottedArc(gl, points);
				} else { // Draw regular edges for the catch-all case
					drawRegularArc(gl, points);
				}
				
				gl.glPopName();
				
			}
		}
	}
	
	// Picks a color according to the edgeView and passes it to OpenGL
	private void chooseColor(GL2 gl, View<CyEdge> edgeView, GraphicsData graphicsData) {
		Color visualPropertyColor = null;
		visualPropertyColor = (Color) edgeView.getVisualProperty(BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT);
		
		RenderColor color = new RenderColor(DEFAULT_COLOR);
		
		if (visualPropertyColor != null) {
			color.set((double) visualPropertyColor.getRed() / 255, 
					(double) visualPropertyColor.getGreen() / 255, 
					(double) visualPropertyColor.getBlue() / 255);
		}
		
		if (edgeView.getVisualProperty(BasicVisualLexicon.EDGE_SELECTED)) {
			
			// Make selected edges appear greener
			color.multiplyRed(0.7, 0, 0.7);
			color.multiplyGreen(1.5, 0.5, 1);
			color.multiplyBlue(0.7, 0, 0.7);
		} 
		// MKTODO
//		else if (edgeView.getModel().getIndex() == graphicsData.getSelectionData().getHoverEdgeIndex()) {
//			
//			// Make hovered edges appear bluer
//			color.multiplyRed(0.7, 0, 0.7);
//			color.multiplyGreen(0.7, 0, 0.7);
//			color.multiplyBlue(1.5, 0.5, 1);
//		}
//		
		RenderColor.setNonAlphaColors(gl, color);
	}
	
	private void drawRegularArc(GL2 gl, Vector3[] points) {
		Vector3 displacement;
		
		for (int i = 0; i < points.length - 1; i++) {
			displacement = points[i + 1].subtract(points[i]);
			
			gl.glPushMatrix();
			
			// Setup transformations to draw the shape
			RenderToolkit.setUpFacingTransformation(gl, points[i], displacement);
			gl.glScalef(SEGMENT_RADIUS * edgeRadiusFactor, 
					SEGMENT_RADIUS * edgeRadiusFactor, 
					(float) displacement.magnitude());
			
			// Perform drawing
			shapeDrawer.drawSegment(gl, EdgeShapeType.REGULAR);
			
			gl.glPopMatrix();
		}
	}
	
	private void drawDashedArc(GL2 gl, Vector3[] points) {
		Vector3 facing;
		
		for (int i = 1; i < points.length - 1; i++) {
			facing = points[i + 1].subtract(points[i - 1]);
			
			gl.glPushMatrix();
			
			RenderToolkit.setUpFacingTransformation(gl, points[i], facing);
			gl.glTranslatef(0, 0, -DASHED_EDGE_LENGTH / 2);
			
			gl.glScalef(DASHED_EDGE_RADIUS * edgeRadiusFactor,
					DASHED_EDGE_RADIUS * edgeRadiusFactor, 
					DASHED_EDGE_LENGTH);
			
			// Perform drawing
			shapeDrawer.drawSegment(gl, EdgeShapeType.DASHED);
			
			gl.glPopMatrix();
		}
	}
	
	private void drawDottedArc(GL2 gl, Vector3[] points) {
		Vector3 facing;
		
		for (int i = 1; i < points.length - 1; i++) {
			facing = points[i + 1].subtract(points[i - 1]);
			
			gl.glPushMatrix();
			
			RenderToolkit.setUpFacingTransformation(gl, points[i], facing);
			gl.glScalef(DOTTED_EDGE_RADIUS  * edgeRadiusFactor,
					DOTTED_EDGE_RADIUS * edgeRadiusFactor,
					DOTTED_EDGE_RADIUS * edgeRadiusFactor);
			
			// Perform drawing
			shapeDrawer.drawSegment(gl, EdgeShapeType.DOTTED);
			
			gl.glPopMatrix();
		}
	}
}
