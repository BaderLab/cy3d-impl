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

	private static final double SEGMENT_RADIUS = 0.007; // 0.007 default
	private static final int SEGMENT_SLICES = 4;
	private static final int SEGMENT_STACKS = 1;
	
	private static final double MIN_LENGTH = Double.MIN_NORMAL;
	
	private static final RenderColor DEFAULT_COLOR = 
		new RenderColor(0.67, 0.67, 0.67);
	private static final RenderColor DEFAULT_SELECTED_COLOR = 
		new RenderColor(0.73, 0.73, 0.6);
	private static final RenderColor DEFAULT_HOVER_COLOR = 
		new RenderColor(0.5, 0.5, 0.7);
	
	private static final int NUM_SEGMENTS = 11;
	
	private int segmentListIndex;
	
	// Container for EdgeView objects that also adds information about whether the
	// edge is part of a series of edges that connect the same pair of nodes
	private class EdgeViewContainer {
		protected View<CyEdge> edgeView;
		
		// Identifies the pair of nodes that the edge connects
		protected long pairIdentifier;
		
		// The index of this edge compared to all the other edges that connect the same pair
		// of nodes. If this is the first of 7 edges that connect the same pair of nodes, its
		// edgeNumber would be set to 1.
		protected int edgeNumber;
		
		// The total number of edges that connect the pair of nodes.
		protected int totalCoincidentEdges;
	}
	
	// Analyze the network to obtain whether each edge is connecting 2 nodes that
	// are already connected by other nodes
	// Maybe add an optimization so we only have to re-analyze the network each time it changes?
	private Set<EdgeViewContainer> analyzeEdges(CyNetworkView networkView) {
		
		// Create the set of containers to be returned
		Set<EdgeViewContainer> edgeViewContainers = new HashSet<EdgeViewContainer>(
				networkView.getModel().getEdgeCount());
		
		// This map maps each node-pair identifier to the number of edges between that pair of nodes
		// The identifier is: max(sourceIndex, targetIndex) * nodeCount + min(sourceIndex, targetIndex)
		Map<Long, Integer> pairCoincidenceCount = new HashMap<Long, Integer>(
				networkView.getModel().getNodeCount());
		long identifier;
		int sourceIndex, targetIndex;
		int nodeCount = networkView.getModel().getNodeCount();
		CyEdge edge;
		int edgeNumber;
		
		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {			
			edge = edgeView.getModel();
			
			// Assign an identifier to each pair of nodes
			sourceIndex = edge.getSource().getIndex();
			targetIndex = edge.getTarget().getIndex();
			
			if (sourceIndex >= targetIndex) {
				identifier = (long) nodeCount * sourceIndex + targetIndex;
			} else {
				identifier = (long) nodeCount * targetIndex + sourceIndex;
			}
			
			// Assign a value that represents how many edges have been found between this pair
			if (!pairCoincidenceCount.containsKey(identifier)) {
				edgeNumber = 1;
			} else {
				edgeNumber = pairCoincidenceCount.get(identifier) + 1;
			}
			
			pairCoincidenceCount.put(identifier, edgeNumber);
			
			EdgeViewContainer container = new EdgeViewContainer();
			container.edgeView = edgeView;
			container.pairIdentifier = identifier;
			container.edgeNumber = edgeNumber;
			
			edgeViewContainers.add(container);
		}
		
		// Update the value for the total number of edges between this pair of nodes
		for (EdgeViewContainer container : edgeViewContainers) {
			container.totalCoincidentEdges = pairCoincidenceCount.get(container.pairIdentifier);
		}
		
		return edgeViewContainers;
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
		
//		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {
//			
//			chooseColor(gl, edgeView, graphicsData);
//			
//			CyEdge edge = edgeView.getModel();
//			Vector3 start = obtainCoordinates(edge.getSource(), networkView, graphicsData.getDistanceScale());
//			Vector3 end = obtainCoordinates(edge.getTarget(), networkView, graphicsData.getDistanceScale());
//			
//			// TODO: Check for case where source() == target(), ie. self-edge
//			
//			if (start != null && end != null) {
//				// Load name for edge picking
//				gl.glLoadName(edge.getIndex());
//				
//				// drawSegment(gl, start, end);
//				drawArcSegments(gl, start, end, 0.2, Math.PI, 4);
//			}
//		}
		
		double distanceScale = graphicsData.getDistanceScale();
		
		Set<EdgeViewContainer> edgeViewContainers = analyzeEdges(networkView);
		View<CyEdge> edgeView;
		CyEdge edge;
		Vector3 start, end;
		
		double curvedEdgeRadius, edgeRadialAngle;
		for (EdgeViewContainer container : edgeViewContainers) {
			edgeView = container.edgeView;
			
			chooseColor(gl, edgeView, graphicsData);
			
			edge = edgeView.getModel();
			start = obtainCoordinates(edge.getSource(), networkView, distanceScale);
			end = obtainCoordinates(edge.getTarget(), networkView, distanceScale);
			
			// TODO: Check for cases where the edge goes from a node to itself
			
			if (start != null && end != null) {
				// Load name for edge pikcking
				gl.glLoadName(edge.getIndex());
				
				// If there's only 1 edge between this pair of nodes, draw it straight
				if (container.totalCoincidentEdges <= 1) {
					drawSegment(gl, start, end);
				// Otherwise, distribute the edges radially and make them curved
				} else {
					
					// Level 1 has 2^2 - 1^1 = 3 edges, level 2 has 3^3 - 2^2 = 5, level 3 has 7
					int edgeLevel = (int) (Math.sqrt((double) container.edgeNumber));
					int maxLevel = (int) (Math.sqrt((double) container.totalCoincidentEdges));
					
					int edgesInLevel = edgeLevel * 2 + 1;
					
					// Smaller edge level -> greater radius
					curvedEdgeRadius = start.distance(end) * (0.5 + (double) 1.5 / Math.pow(edgeLevel, 2));
					
					// The outmost level is usually not completed
					if (edgeLevel == maxLevel) {
						edgesInLevel = (int) (container.totalCoincidentEdges - Math.pow(maxLevel, 2) + 1);
					}
					
					edgeRadialAngle = (double) (container.edgeNumber - Math.pow(edgeLevel, 2)) / edgesInLevel * Math.PI * 2;
					
//					if (graphicsData.getFramesElapsed() == 500) {
//						System.out.println("edgeNumber: " + container.edgeNumber);
//						System.out.println("totalCoincidentEdges: " + container.totalCoincidentEdges);
//						System.out.println("edgeLevel: " + edgeLevel);
//						System.out.println("maxLevel: " + maxLevel);
//						System.out.println("edgesInLevel: " + edgesInLevel);
//						System.out.println("curvedEdgeRadius: " + curvedEdgeRadius);
//						System.out.println("edgeRadialAngle: " + edgeRadialAngle);
//						System.out.println("==================================");
//					}

					drawArcSegments(gl, start, end, curvedEdgeRadius, edgeRadialAngle, NUM_SEGMENTS);
				}
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
		// radius = Math.max(displacementLength * 2, radius);
		
		// Use cosine law
		double arcAngle;
		arcAngle = GeometryToolkit.saferArcCos(
				(2 * radius * radius - displacementLength * displacementLength) 
				/ (2 * radius * radius));
		
		double nearCornerAngle = Math.PI / 2 - (arcAngle / 2);
	
		// Set the angle of rotation along the node-to-node displacement axis
		Vector3 targetDirection = new Vector3(0, 0, 1);
		targetDirection = targetDirection.rotate(displacement, angle);
		
		// Offset vector that points from first node to the circle's center
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
