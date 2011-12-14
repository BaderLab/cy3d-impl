package org.cytoscape.paperwing.internal.graphics;

import java.awt.Color;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
// import org.cytoscape.paperwing.internal.Graphics.DrawStateModifier;
import org.cytoscape.paperwing.internal.Vector3;
import org.cytoscape.paperwing.internal.Graphics.DrawStateModifier;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

import com.jogamp.opengl.util.gl2.GLUT;

public class RenderEdgeProcedure implements ReadOnlyGraphicsProcedure {

	/** The default radius of the semi-cylindrical edges */
	private static final float EDGE_RADIUS = 0.018f;

	/**
	 * A multiplicative factor for the width of the edges when reading from the
	 * visual property mappings
	 */
	private static final float EDGE_WIDTH_FACTOR = 0.17f;

	/** A multiplicative curve factor for the edges */
	private static final float EDGE_CURVE_FACTOR = 0.43f; // 0.31f

	/** The minimum distance between nodes required for an edge to be drawn */
	private static final float MINIMUM_EDGE_DRAW_DISTANCE_SQUARED = Float.MIN_NORMAL; // 0.015f

	/**
	 * How many straight edge segments to use for approximating a curved edge,
	 * this value does not have to be static
	 */
	private static int QUADRATIC_EDGE_SEGMENTS = 5;

	/** The slices detail level to use for drawing edges */
	private static int EDGE_SLICES_DETAIL = 4;

	/** The stacks detail level to use for drawing edges */
	private static int EDGE_STACKS_DETAIL = 1;

	/**
	 * A draw state modifier which can be used to modify the appearance of
	 * certain objects
	 */
	public static enum DrawStateModifier {
		HOVERED, SELECTED, NORMAL, ENLARGED, SELECT_BORDER
	}

	private int edgeListIndex;

	@Override
	public void initialize(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();

		edgeListIndex = gl.glGenLists(1);

		GLU glu = new GLU();

		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);

		// Draw Standard-Length Edge
		// -------------------------

		GLUquadric edgeQuadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(edgeQuadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(edgeQuadric, GLU.GLU_SMOOTH); // TODO: Experiment
															// with GLU_FLAT for
															// efficiency

		gl.glNewList(edgeListIndex, GL2.GL_COMPILE);
		glu.gluCylinder(edgeQuadric, EDGE_RADIUS, EDGE_RADIUS, 1.0,
				EDGE_SLICES_DETAIL, EDGE_STACKS_DETAIL);
		gl.glEndList();
	}

	/**
	 * Draw all edges onto the screen, taking into account certain visual
	 * properties such as color
	 * 
	 * @param gl
	 *            The {@link GL2} object used for rendering
	 * @param generalModifier
	 *            A modifier to be applied to the drawn results.
	 *            {@link DrawStateModifier}.ENLARGED is used for picking while
	 *            using OpenGL's GL_SELECT rendering mode.
	 */
	@Override
	public void execute(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();

		CyNetworkView networkView = graphicsData.getNetworkView();
		float distanceScale = graphicsData.getDistanceScale();
		int hoverEdgeIndex = graphicsData.getHoverEdgeIndex();
		Set<Integer> selectedEdgeIndices = graphicsData.getSelectionData()
				.getSelectedEdgeIndices();

		// Indirectly supporting the following visual properties
		// VisualProperty<Paint> EDGE_PAINT
		// VisualProperty<Double> EDGE_WIDTH
		// VisualProperty<Paint> EDGE_SELECTED_PAINT

		// Directly supporting the following visual properties
		// VisualProperty<Boolean> EDGE_VISIBLE
		// VisualProperty<Boolean> EDGE_SELECTED

		View<CyNode> sourceView;
		View<CyNode> targetView;

		int nodeCount = networkView.getModel().getNodeCount();

		int sourceIndex;
		int targetIndex;

		// A unique identifier (as far as this method is concerned) for each
		// pair of nodes
		long pairIdentifier;

		TreeMap<Long, Integer> pairs = new TreeMap<Long, Integer>();

		// Points 0 and 2 represent endpoints of the quadratic Bezier curve,
		// while
		// point 1 represents the approach point
		Vector3 p0 = new Vector3();
		Vector3 p1 = new Vector3();
		Vector3 p2 = new Vector3();

		Vector3 p1Offset;
		Vector3 direction;

		int edgeIndex;

		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {

			sourceView = networkView.getNodeView(edgeView.getModel()
					.getSource());
			targetView = networkView.getNodeView(edgeView.getModel()
					.getTarget());
			sourceIndex = sourceView.getModel().getIndex();
			targetIndex = targetView.getModel().getIndex();

			edgeIndex = edgeView.getModel().getIndex();

			// These indices rely on CyNode's guarantee that NodeIndex <
			// NumOfNodes
			assert sourceIndex < nodeCount;
			assert targetIndex < nodeCount;

			// Identify this pair of nodes so we'll know if we've drawn an
			// edge between them before
			// TODO: Check if this is a safe calculation
			pairIdentifier = ((long) Integer.MAX_VALUE + 1) * sourceIndex
					+ targetIndex;

			// Commenting the below will remove distinguishment between source
			// and
			// target nodes
			if (sourceIndex > targetIndex) {
				pairIdentifier = ((long) Integer.MAX_VALUE + 1) * targetIndex
						+ sourceIndex;
			} else {
				pairIdentifier = ((long) Integer.MAX_VALUE + 1) * sourceIndex
						+ targetIndex;
			}

			// Have we visited an edge between these nodes before?
			if (pairs.containsKey(pairIdentifier)) {
				pairs.put(pairIdentifier, pairs.get(pairIdentifier) + 1);
			} else {
				pairs.put(pairIdentifier, 0);
			}

			// Find p0, p1, p2 for the Bezier curve
			p0.set(sourceView
					.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION),
					sourceView
							.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION),
					sourceView
							.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION));
			p0.divideLocal(distanceScale);

			p2.set(targetView
					.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION),
					targetView
							.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION),
					targetView
							.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION));
			p2.divideLocal(distanceScale);

			p1 = p0.add(p2);
			p1.divideLocal(2);

			direction = p2.subtract(p0);
			p1Offset = direction.cross(0, 1, 0);
			p1Offset.normalizeLocal();

			// Multiplier controlling distance between curve point p1 and the
			// straight line between the nodes p0 and p2
			int distanceMultiplier = (int) Math.sqrt(pairs.get(pairIdentifier));

			int radiusEdgeCount = distanceMultiplier * 2 + 1;

			// Multiplier controlling rotation about the p0p2 vector axis
			int rotationMultiplier = pairs.get(pairIdentifier);

			// Shift the square root graph one to the left and one down
			// to get smoother curves
			// TODO: Check if sqrt is needed
			p1Offset.multiplyLocal(distanceMultiplier * EDGE_CURVE_FACTOR
					* (Math.sqrt(direction.magnitude() + 1) - 1));

			if (distanceMultiplier % 2 == 1) {
				p1Offset = p1Offset.rotate(direction, 2 * Math.PI
						* rotationMultiplier / radiusEdgeCount);
			} else {
				p1Offset = p1Offset.rotate(direction, 2 * Math.PI
						* rotationMultiplier / radiusEdgeCount + Math.PI);
			}

			p1.addLocal(p1Offset);

			// if (latch_1) {
			// System.out.println("Source index: " + sourceIndex);
			// System.out.println("Source target: " + targetIndex);
			// System.out.println("pairs.get(pairIdentifier): " +
			// pairs.get(pairIdentifier));
			// System.out.println("pairIdentifier: " + pairIdentifier);
			// }

			// Load name for edge picking
			gl.glLoadName(edgeIndex);

			edgeView.setVisualProperty(RichVisualLexicon.NODE_SELECTED, false);

			DrawStateModifier modifier;
			// if (generalModifier == DrawStateModifier.ENLARGED) {
			// modifier = DrawStateModifier.ENLARGED;
			// } else if (selectedEdgeIndices.contains(edgeIndex)) {
			if (selectedEdgeIndices.contains(edgeIndex)) {
				modifier = DrawStateModifier.SELECTED;

				edgeView.setVisualProperty(RichVisualLexicon.NODE_SELECTED,
						true);
			} else if (edgeIndex == hoverEdgeIndex) {
				modifier = DrawStateModifier.HOVERED;
			} else {
				modifier = DrawStateModifier.NORMAL;
			}

			// Draw it only if the visual property says it is visible
			if (edgeView.getVisualProperty(MinimalVisualLexicon.EDGE_VISIBLE)) {

				if (distanceMultiplier == 0) {
					drawQuadraticEdge(gl, p0, p1, p2, 1, modifier, edgeView);
				} else {
					drawQuadraticEdge(gl, p0, p1, p2, QUADRATIC_EDGE_SEGMENTS,
							modifier, edgeView);
				}
			}
		}

		// latch_1 = false;
	}

	/**
	 * Draws an edge shaped around a quadratic Bezier curve
	 * 
	 * @param gl
	 *            {@link GL2} rendering object
	 * @param p0
	 *            The starting point for the Bezier curve, p0
	 * @param p1
	 *            The approach point, p1
	 * @param p2
	 *            The end point, p2
	 * @param numSegments
	 *            The number of straight-line segments used to approximate the
	 *            Bezier curve
	 * @param modifier
	 *            A modifier to change the appearance of the edge object
	 */
	private void drawQuadraticEdge(GL2 gl, Vector3 p0, Vector3 p1, Vector3 p2,
			int numSegments, DrawStateModifier modifier, View<CyEdge> edgeView) {
		// TODO: Allow the minimum distance to be changed
		if (p0.distanceSquared(p2) < MINIMUM_EDGE_DRAW_DISTANCE_SQUARED) {
			return;
		}

		// Equation for Quadratic Bezier curve:
		// B(t) = (1 - t)^2P0 + 2(1 - t)tP1 + t^2P2, t in [0, 1]

		double parameter;

		Vector3 current;
		Vector3[] points = new Vector3[numSegments + 1];

		points[0] = new Vector3(p0);
		for (int i = 1; i < numSegments; i++) {
			// Obtain points along the Bezier curve
			parameter = (double) i / numSegments;

			current = p0.multiply(Math.pow(1 - parameter, 2));
			current.addLocal(p1.multiply(2 * (1 - parameter) * parameter));
			current.addLocal(p2.multiply(parameter * parameter));

			points[i] = new Vector3(current);
		}

		points[numSegments] = new Vector3(p2);

		for (int i = 0; i < numSegments; i++) {

			drawSingleEdge(gl, points[i], points[i + 1], modifier, edgeView);
		}
	}

	/**
	 * Draws a single edge-like object
	 * 
	 * @param gl
	 *            The {@link GL2} rendering object
	 * @param start
	 *            The start location
	 * @param end
	 *            The end location
	 * @param modifier
	 *            A modifier to vary the appearance of the output
	 */
	private void drawSingleEdge(GL2 gl, Vector3 start, Vector3 end,
			DrawStateModifier modifier, View<CyEdge> edgeView) {
		// Directly supporting th following visual properties
		// VisualProperty<Paint> EDGE_PAINT
		// VisualProperty<Double> EDGE_WIDTH
		// VisualProperty<Paint> EDGE_SELECTED_PAINT

		gl.glPushMatrix();

		Vector3 direction = end.subtract(start);

		GraphicsUtility.setUpFacingTransformation(gl, start, direction);

		// Perform a transformation to adjust length
		gl.glScalef(1.0f, 1.0f, (float) direction.magnitude());

		float width = edgeView.getVisualProperty(RichVisualLexicon.EDGE_WIDTH)
				.floatValue() * EDGE_WIDTH_FACTOR;

		// Perform a transformation to adjust width
		gl.glScalef(width, width, 1.0f);

		Color color;

		if (modifier == DrawStateModifier.NORMAL) {
			color = (Color) edgeView
					.getVisualProperty(RichVisualLexicon.EDGE_PAINT);

			gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f,
					color.getBlue() / 255.0f);

			// Default color is below
			// gl.glColor3f(0.73f, 0.73f, 0.73f);
			gl.glCallList(edgeListIndex);
		} else if (modifier == DrawStateModifier.ENLARGED) {
			gl.glScalef(1.6f, 1.6f, 1.0f);
			gl.glCallList(edgeListIndex);
		} else if (modifier == DrawStateModifier.SELECTED) {
			color = (Color) edgeView
					.getVisualProperty(RichVisualLexicon.EDGE_SELECTED_PAINT);

			gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f,
					color.getBlue() / 255.0f);

			// Default color below
			// gl.glColor3f(0.48f, 0.65f, 0.48f);
			gl.glScalef(1.1f, 1.1f, 1.0f);
			gl.glCallList(edgeListIndex);
		} else if (modifier == DrawStateModifier.HOVERED) {
			gl.glColor3f(0.45f, 0.45f, 0.70f);
			gl.glCallList(edgeListIndex);
			// } else if (modifier == DrawStateModifier.SELECT_BORDER) {
			// gl.glColor3f(0.72f, 0.31f, 0.40f);
			// gl.glCallList(selectBorderListIndex);
		} else {
			// Invalid modifier found
		}

		gl.glPopMatrix();
	}
}
