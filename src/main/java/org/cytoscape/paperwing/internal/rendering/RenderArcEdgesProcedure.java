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
import org.cytoscape.paperwing.internal.rendering.shapes.EdgeShapeDrawer;
import org.cytoscape.paperwing.internal.rendering.shapes.EdgeShapeDrawer.EdgeShapeType;
import org.cytoscape.paperwing.internal.tools.GeometryToolkit;
import org.cytoscape.paperwing.internal.tools.RenderColor;
import org.cytoscape.paperwing.internal.tools.RenderToolkit;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class RenderArcEdgesProcedure implements ReadOnlyGraphicsProcedure {

	private static final float SEGMENT_RADIUS = 0.007f; // 0.007 default
	private static final int SEGMENT_SLICES = 4;
	private static final int SEGMENT_STACKS = 1;
	
	private static final double MIN_LENGTH = Double.MIN_NORMAL;
	
	private static final RenderColor DEFAULT_COLOR = 
		new RenderColor(0.67, 0.67, 0.67);
	private static final RenderColor DEFAULT_SELECTED_COLOR = 
		new RenderColor(0.73, 0.73, 0.6);
	private static final RenderColor DEFAULT_HOVER_COLOR = 
		new RenderColor(0.5, 0.5, 0.7);
	
	private static final float DASHED_EDGE_RADIUS = 0.007f;
	private static final float DASHED_EDGE_LENGTH = 0.04f;
	private static final float DASHED_EDGE_SPACING = 0.10f;
	
	private static final float DOTTED_EDGE_RADIUS = 0.012f;
	private static final float DOTTED_EDGE_SPACING = 0.08f;
	
	private static final double ARC_EDGE_MINIMUM_RADIUS = 0.1;
	
	private static final Vector3 X_UNIT_VECTOR = new Vector3(1, 0, 0);
	private static final Vector3 Y_UNIT_VECTOR = new Vector3(0, 1, 0);
	private static final Vector3 Z_UNIT_VECTOR = new Vector3(0, 0, 1);
	
	/**
	 * The number of straight segments used to approximate a curved edge
	 */
	private static final int NUM_SEGMENTS = 11;
	
	private EdgeShapeDrawer shapeDrawer;
	
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
		
		// Does this edge direct from a node to itself?
		protected boolean selfEdge;
		
		Vector3 start;
		Vector3 end;
	}
	
	public RenderArcEdgesProcedure() {
		shapeDrawer = new EdgeShapeDrawer();
	}
	
	// Analyze the network to obtain whether each edge is connecting 2 nodes that
	// are already connected by other nodes
	// Maybe add an optimization so we only have to re-analyze the network each time it changes?
	private Set<EdgeViewContainer> analyzeEdges(CyNetworkView networkView, double distanceScale) {
		
		// Create the set of containers to be returned
		Set<EdgeViewContainer> edgeViewContainers = new HashSet<EdgeViewContainer>(
				networkView.getModel().getEdgeCount());
		
		// This map maps each node-pair identifier to the number of edges between that pair of nodes
		// The identifier is: max(sourceIndex, targetIndex) * nodeCount + min(sourceIndex, targetIndex)
		Map<Long, Integer> pairCoincidenceCount = new HashMap<Long, Integer>(
				networkView.getModel().getNodeCount());
		long identifier;
		int sourceIndex, targetIndex, edgeNumber;
		int nodeCount = networkView.getModel().getNodeCount();
		CyEdge edge;
		
		boolean selfEdge;
		
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
			
			// Check if the edge leads from a node to itself
			if (sourceIndex == targetIndex) {
				selfEdge = true;
			} else {
				selfEdge = false;
			}
			
			EdgeViewContainer container = new EdgeViewContainer();
			container.edgeView = edgeView;
			container.pairIdentifier = identifier;
			container.edgeNumber = edgeNumber;
			container.selfEdge = selfEdge;
			
			container.start = obtainCoordinates(edge.getSource(), networkView, distanceScale);
			container.end = obtainCoordinates(edge.getTarget(), networkView, distanceScale);
			
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
		shapeDrawer.initialize(graphicsData.getGlContext());
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		GL2 gl = graphicsData.getGlContext();
		
		double distanceScale = graphicsData.getDistanceScale();
		
		Set<EdgeViewContainer> edgeViewContainers = analyzeEdges(networkView, distanceScale);
		View<CyEdge> edgeView;
		CyEdge edge;
		Vector3 circleCenter;
		
		for (EdgeViewContainer container : edgeViewContainers) {
			edgeView = container.edgeView;
			edge = edgeView.getModel();
			
			if (container.start != null && container.end != null && 
					(container.end.distance(container.start) >= MIN_LENGTH || container.selfEdge)) {
				
				// Load name for edge picking
				gl.glLoadName(edge.getIndex());
				
				// Set color
				chooseColor(gl, edgeView, graphicsData);
				
				// Find the arc circle's center
				circleCenter = findCircleCenter(container);
				
				// General points along the arc
				Vector3[] points;
				
				// Draw the correct type of edge depending on the visual property
				if (edgeView.getVisualProperty(RichVisualLexicon.EDGE_LINE_TYPE)
						== LineTypeVisualProperty.EQUAL_DASH) {
					points = generateSparseArcCoordinates(
							container.start, container.end, circleCenter, DASHED_EDGE_SPACING, container.selfEdge);
				
					drawDashedArc(gl, points);
				} else if (edgeView.getVisualProperty(RichVisualLexicon.EDGE_LINE_TYPE)
						== LineTypeVisualProperty.DOT) {
					points = generateSparseArcCoordinates(
							container.start, container.end, circleCenter, DOTTED_EDGE_SPACING, container.selfEdge);
				
					drawDottedArc(gl, points);
					
				// Draw regular edges for the catch-all case
				} else {
					points = generateArcCoordinates(
							container.start, container.end, circleCenter, NUM_SEGMENTS, container.selfEdge);
					
					drawRegularArc(gl, points);
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
	
	/**
	 * Generates points along the arc of a circle connecting 2 positions in 3D space, given
	 * the desired arc of the circle, the angle to rotate the arc about the displacement axis
	 * by the right-hand rule from the positive z-axis, and the number of points desired.
	 * 
	 * @param start The starting position of the arc
	 * @param end The terminal position of the arc
	 * @param circleCenter The center of the circle used to generate the arc
	 * @param segments The number of straight segments to divide the arc into. The number
	 * of points returned is equal to the number of segments + 1. Must be at least 1.
	 * @param invert When set to true, generates the coordinates of the arc spanning greater
	 * than 180 degrees of the circle, instead of the smaller obtuse or acute arc. This
	 * can be useful for drawing edges from an edge to itself.
	 * @return An array of position vectors representing the arc, where the first point
	 * is equal to the start of the arc, and the last point is equal to the end of the arc.
	 */
	private Vector3[] generateArcCoordinates(Vector3 start, Vector3 end, 
			Vector3 circleCenter, int segments, boolean invert) {
		
		Vector3[] arcCoordinates = new Vector3[segments + 1];
		
		Vector3 startOffset = start.subtract(circleCenter);
		Vector3 endOffset = end.subtract(circleCenter);
		Vector3 rotationNormal = startOffset.cross(endOffset);
		
		// TODO: Check if this is a good place to put this
		if (rotationNormal.magnitude() < MIN_LENGTH) {
			rotationNormal = startOffset.cross(Y_UNIT_VECTOR);
		}
		
		double arcAngle = startOffset.angle(endOffset);
		
		// Invert the angle if needed
		if (invert) {
			arcAngle = -2 * Math.PI + arcAngle;
		}
		
		double rotation = arcAngle / segments;
		
		for (int i = 0; i < segments; i++) {
			arcCoordinates[i] = circleCenter.plus(startOffset.rotate(rotationNormal, rotation * i));
		}
		
		arcCoordinates[arcCoordinates.length - 1] = end.copy();

		
		return arcCoordinates;
	}
	
	/**
	 * Similar to generateArcCoordinates(), but the result is governed by the distance
	 * between points rather than the number of points. Also, the result is calculated
	 * such that the points are distributed symmetrically between the starting
	 * and end positions.
	 * 
	 * @param start The starting point
	 * @param end The ending point
	 * @param circleCenter The center of the circle used to generate the arc
	 * @param distance The desired distance between the points
	 * @param invert Whether to invert the arc on the circle such that instead of generating
	 * points along the smaller, less than or equal to 180 degrees arc, the arc spanning
	 * more than 180 degrees is used. This is useful for drawing edges from a node to itself.
	 * @return An array of points along the arc, where the first and last points correspond
	 * to the given starting and ending positions.
	 */
	private Vector3[] generateSparseArcCoordinates(Vector3 start, Vector3 end,
			Vector3 circleCenter, double distance, boolean invert) {
		
		Vector3 startOffset = start.subtract(circleCenter);
		Vector3 endOffset = end.subtract(circleCenter);
		double offsetLength = startOffset.magnitude();
		
		// The angular increment to achieve the desired distance between points on the
		// arc. This increment is found by applying the cosine law
		double segmentAngle = GeometryToolkit.saferArcCos(
				(2 * offsetLength * offsetLength - distance * distance) / (2 * offsetLength * offsetLength));
		
		double arcAngle = startOffset.angle(endOffset);
		
		// Check if it is necessary to invert the arc in the circle, going from an angle
		// less than or equal to 180 degrees to a reflex angle
		if (invert) {
			arcAngle = 2 * Math.PI - arcAngle;
			segmentAngle = -segmentAngle;
		}
		
		int halfIncrements = Math.abs((int) (arcAngle / segmentAngle / 2));
		
		// Add 2 to include the start and end points
		Vector3[] arcCoordinates = new Vector3[2 * halfIncrements + 2]; 
		Vector3 rotationNormal = startOffset.cross(endOffset);

		Vector3 centerCurvePointOffset = startOffset.rotate(
				rotationNormal, arcAngle / 2);	
		
		// Manually add the first point
		arcCoordinates[0] = start.copy();
		
		// TODO: Current implementation includes the middle point twice, though it is not
		// noticeable
		
		// Points between the first node and the center point
		for (int i = 0; i < halfIncrements; i++) {
			arcCoordinates[(halfIncrements - i)] = circleCenter.plus(
					centerCurvePointOffset.rotate(rotationNormal, -segmentAngle * i));
		}
		
		// Points between the center point and the second node
		for (int i = 0; i < halfIncrements; i++) {
			arcCoordinates[(halfIncrements + i) + 1] = circleCenter.plus(
					centerCurvePointOffset.rotate(rotationNormal, segmentAngle * i));
		}
		
		// Include the end point
		arcCoordinates[arcCoordinates.length - 1] = end.copy();
		
		return arcCoordinates;
	}
	
	
	/**
	 * Return a 2-tuple containing the appropriate radius for the circular edge arc, as well
	 * as how much it should be rotated in the node-to-node displacement axis.
	 * 
	 * @param container The EdgeViewContainer object holding additional information about the
	 * edge, including its index amongst the other edges that connect the same pair of nodes
	 * @param selfEdge Whether or not the edge leads from a node to itself
	 */
	private double[] findArcEdgeMetrics(EdgeViewContainer container) {
		
		// Level 1 has 2^2 - 1^1 = 3 edges, level 2 has 3^3 - 2^2 = 5, level 3 has 7
		int edgeLevel = (int) (Math.sqrt((double) container.edgeNumber));
		int maxLevel = (int) (Math.sqrt((double) container.totalCoincidentEdges));
		
		int edgesInLevel = edgeLevel * 2 + 1;
		
		
		double curvedEdgeRadius;
		
		if (container.selfEdge) {
			// For self-edges, want greater edge level -> greater radius
			curvedEdgeRadius = ARC_EDGE_MINIMUM_RADIUS / (0.5 + (double) 1.5 / Math.pow(edgeLevel, 2));
		} else {
			// For regular edges, want greater edge level -> smaller radius (more curvature)
			curvedEdgeRadius = container.start.distance(container.end) * (0.5 + (double) 1.5 / Math.pow(edgeLevel, 2));
		}
		
		// The outmost level is usually not completed
		if (edgeLevel == maxLevel) {
			edgesInLevel = (int) (container.totalCoincidentEdges - Math.pow(maxLevel, 2) + 1);
		}
		
		double edgeRadialAngle = (double) (container.edgeNumber - Math.pow(edgeLevel, 2)) / edgesInLevel * Math.PI * 2;

		return new double[]{curvedEdgeRadius, edgeRadialAngle};
	}
	
	/**
	 * Finds the center of a circle passing through 2 points, rotated about the displacement axis
	 * by a certain angle.
	 * 
	 * @param container The EdgeViewContainer object holding additional information about the
	 * edge, including its index amongst the other edges that connect the same pair of nodes
	 * @return A position vector representing the center of the circle
	 */
	private Vector3 findCircleCenter(EdgeViewContainer container) {
		
		double[] edgeMetrics = findArcEdgeMetrics(container);
		
		double radius = edgeMetrics[0];
		double angle = edgeMetrics[1];
		
		if (container.selfEdge) {
			Vector3 offset = Z_UNIT_VECTOR.multiply(radius).rotate(Y_UNIT_VECTOR, angle);
		
			return container.start.plus(offset);
		} else {
			
			Vector3 displacement = container.end.subtract(container.start);
			
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
		
			return container.start.plus(circleCenterOffset);
		}
	}
	
	private void drawRegularArc(GL2 gl, Vector3[] points) {
		Vector3 displacement;
		
		for (int i = 0; i < points.length - 1; i++) {
			displacement = points[i + 1].subtract(points[i]);
			
			gl.glPushMatrix();
			
			// Setup transformations to draw the shape
			RenderToolkit.setUpFacingTransformation(gl, points[i], displacement);
			gl.glScalef(SEGMENT_RADIUS, SEGMENT_RADIUS, (float) displacement.magnitude());
			
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
			
			gl.glScalef(DASHED_EDGE_RADIUS, DASHED_EDGE_RADIUS, DASHED_EDGE_LENGTH);
			
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
			gl.glScalef(DOTTED_EDGE_RADIUS, DOTTED_EDGE_RADIUS, DOTTED_EDGE_RADIUS);
			
			// Perform drawing
			shapeDrawer.drawSegment(gl, EdgeShapeType.DOTTED);
			
			gl.glPopMatrix();
		}
	}
}
