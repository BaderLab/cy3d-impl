package org.cytoscape.paperwing.internal.rendering;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.GeometryToolkit;
import org.cytoscape.paperwing.internal.tools.RenderColor;
import org.cytoscape.paperwing.internal.tools.RenderToolkit;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class RenderArcEdgesProcedure implements ReadOnlyGraphicsProcedure {

	private static final double SEGMENT_RADIUS = 0.009;
	private static final int SEGMENT_SLICES = 4;
	private static final int SEGMENT_STACKS = 1;
	
	private static final double MIN_LENGTH = Double.MIN_NORMAL;
	
	private static final RenderColor DEFAULT_COLOR = 
		new RenderColor(0.67, 0.67, 0.67);
	private static final RenderColor DEFAULT_SELECTED_COLOR = 
		new RenderColor(0.73, 0.73, 0.6);
	private static final RenderColor DEFAULT_HOVER_COLOR = 
		new RenderColor(0.5, 0.5, 0.7);
	
	private int segmentListIndex;
	
	private class EdgeViewContainer {
		private View<CyEdge> edgeView;
		
		private int edgeNumber;
		private int totalCoincidentEdges;
	}
	
	// Analyze the network to obtain whether each edge is connecting 2 nodes that
	// are already connected by other nodes
	// Maybe add an optimization so we only have to re-analyze the network each time it changes?
	private Set<EdgeViewContainer> analyzeEdges(CyNetworkView networkView) {
		
		// A pair of nodes will be identified by index1 * index2 + index1, where index1
		// is the higher of the 2 indices
		Map<Long, Integer> pairs = new HashMap<Long, Integer>(
				networkView.getModel().getNodeCount());
		long identifier;
		int sourceIndex, targetIndex;
		int nodeCount = networkView.getModel().getNodeCount();
		
		for (CyEdge edge : networkView.getModel().getEdgeList()) {			
			sourceIndex = edge.getSource().getIndex();
			targetIndex = edge.getTarget().getIndex();
			
			if (sourceIndex >= targetIndex) {
				identifier = (long) nodeCount * sourceIndex + targetIndex;
			} else {
				identifier = (long) nodeCount * targetIndex + sourceIndex;
			}
			
			if (!pairs.containsKey(identifier)) {
				pairs.put(identifier, 1);
			} else {
				pairs.put(identifier, pairs.get(identifier) + 1);
			}
		}
		
		Set<EdgeViewContainer> edgeViewContainers = new HashSet<EdgeViewContainer>(
				networkView.getModel().getEdgeCount());
		
		EdgeViewContainer container;
		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {
			// container = new
		}
		
		return null;
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();

		segmentListIndex = gl.glGenLists(1);

		GLU glu = GLU.createGLU(gl);
		
		GLUquadric segmentQuadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(segmentQuadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(segmentQuadric, GLU.GLU_SMOOTH); // TODO: Experiment
															// with GLU_FLAT for
															// efficiency

		gl.glNewList(segmentListIndex, GL2.GL_COMPILE);
		glu.gluCylinder(segmentQuadric, SEGMENT_RADIUS, SEGMENT_RADIUS, 1.0,
				SEGMENT_SLICES, SEGMENT_STACKS);
		gl.glEndList();
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		GL2 gl = graphicsData.getGlContext();
		
		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {
			
			chooseColor(gl, edgeView, graphicsData);
			
			CyEdge edge = edgeView.getModel();
			Vector3 start = obtainCoordinates(edge.getSource(), networkView, graphicsData.getDistanceScale());
			Vector3 end = obtainCoordinates(edge.getTarget(), networkView, graphicsData.getDistanceScale());
			
			// TODO: Check for case where source() == target(), ie. self-edge
			
			if (start != null && end != null) {
				// Load name for edge picking
				gl.glLoadName(edge.getIndex());
				
				// drawSegment(gl, start, end);
				drawArcSegments(gl, start, end, 0.2, Math.PI, 4);
			} else { 
//				System.out.println("Null coordinates: " + start + ", " + end);
			}
		}
	}
	
	// Picks a color according to the edgeView and passes it to OpenGL
	private void chooseColor(GL2 gl, View<CyEdge> edgeView, GraphicsData graphicsData) {
		Color visualPropertyColor = null;
		visualPropertyColor = (Color) edgeView.getVisualProperty(RichVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT);
		
		RenderColor color = new RenderColor(DEFAULT_COLOR);
		
		if (visualPropertyColor != null) {
			color.set((double) visualPropertyColor.getRed() / 255, 
					(double) visualPropertyColor.getGreen() / 255, 
					(double) visualPropertyColor.getBlue() / 255);
		}
		
		if (edgeView.getVisualProperty(MinimalVisualLexicon.EDGE_SELECTED)) {
			
			// Make selected edges appear greener
			color.multiplyRed(0.7, 0, 1);
			color.multiplyGreen(1.5, 0.5, 1);
			color.multiplyBlue(0.7, 0, 1);
		} else if (edgeView.getModel().getIndex() == graphicsData.getSelectionData().getHoverEdgeIndex()) {
			// Make hovered edges appear bluer
			color.multiplyRed(0.7, 0, 1);
			color.multiplyGreen(0.7, 0, 1);
			color.multiplyBlue(1.5, 0.5, 1);
		}
		
		RenderColor.setNonAlphaColors(gl, color);
	}
	
	// Obtain the coordinates of a given node, eg. source or target node
	// Returns null if failed to find coordinates
	private Vector3 obtainCoordinates(CyNode node, CyNetworkView networkView, double distanceScale) {
		Vector3 coordinates = null;
		
		View<CyNode> nodeView = networkView.getNodeView(node);
		
		if (nodeView != null) {
			double x = nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION) / distanceScale;
			double y = nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION) / distanceScale;
			double z = nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION) / distanceScale;
			
			// TODO: Perform a check to ensure none of x, y, z are null?
			coordinates = new Vector3(x, y, z);
		}
		
		return coordinates;
	}
	
	private void drawArcSegments(GL2 gl, Vector3 start, Vector3 end, double radius, double angle, int segments) {
		Vector3 displacement = end.subtract(start);
		double displacementLength = displacement.magnitude();
		
		// Radius adjustment (can't draw an arc from start to end if the radius of the arc is less than half that
		// distance)
		radius = Math.max(displacementLength * 2, radius);
		
		// Use cosine law
		double arcAngle;
		arcAngle = GeometryToolkit.saferArcCos(
				(2 * radius * radius - displacementLength * displacementLength) 
				/ (2 * radius * radius));
		
		double nearCornerAngle = Math.PI / 2 - (arcAngle / 2);
	
		Vector3 targetDirection = new Vector3(0, 0, 1);
		targetDirection = targetDirection.rotate(displacement, angle);
		
		Vector3 circleCenterOffset = displacement.rotate(targetDirection.cross(displacement), nearCornerAngle);
		circleCenterOffset.normalizeLocal();
		circleCenterOffset.multiplyLocal(radius);
	
		Vector3 circleCenter = start.plus(circleCenterOffset);
		
//		drawSegment(gl, start, circleCenter);
//		drawSegment(gl, circleCenter, end);
		
		Vector3 rotationNormal = displacement.cross(circleCenterOffset);
		
		if (segments <= 1) {
			drawSegment(gl, start, end);
		} else {
			// Draw segments along the length of the arc
			double angleIncrement = arcAngle / segments;
			// Displacement vector from circle center to current point
			Vector3 currentPointOffset = start.subtract(circleCenter);
			Vector3 currentPoint = circleCenter.plus(currentPointOffset);
			Vector3 previousPoint = currentPoint.copy();
			
			for (int i = 1; i <= segments; i ++) {
				currentPointOffset = currentPointOffset.rotate(rotationNormal, angleIncrement);
				currentPoint = circleCenter.plus(currentPointOffset);
				drawSegment(gl, previousPoint, currentPoint);
				previousPoint.set(currentPoint);
			}	
		}
		
		// Need to obtain circle's center, and use a parametric equation to obtain points along the arc between
		// start and end
	}
	
	private void drawSegment(GL2 gl, Vector3 start, Vector3 end) {
		Vector3 displacement = end.subtract(start);
		double length = displacement.magnitude();
		
		if (length > MIN_LENGTH) {
		
			gl.glPushMatrix();
			RenderToolkit.setUpFacingTransformation(gl, start, displacement);
			gl.glScaled(1, 1, length);
			
			// need a scale transformation for segment radius?
			
			gl.glCallList(segmentListIndex);
			gl.glPopMatrix();
		} else {
//			System.out.println("Edge length too small: " + length);
		}
	}

}
