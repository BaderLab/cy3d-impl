package org.cytoscape.paperwing.internal.graphics;

import java.awt.Color;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class RenderNodeProcedure implements ReadOnlyGraphicsProcedure {

	/** The default radius of the spherical nodes */
	private static final float SMALL_SPHERE_RADIUS = 0.102f; // 0.015f

	/** The slices detail level to use for drawing spherical nodes */
	// 10, 10, 4 // 24, 24, 12 used to be default values for
	// slices/stacks/slices
	private static int NODE_SLICES_DETAIL = 10;

	/** The stacks detail level to use for drawing spherical nodes */
	private static int NODE_STACKS_DETAIL = 10;

	private int nodeListIndex;

	@Override
	public void initialize(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();

		nodeListIndex = gl.glGenLists(1);

		GLU glu = new GLU();

		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);

		// Draw Node
		// ---------

		gl.glNewList(nodeListIndex, GL2.GL_COMPILE);
		glu.gluSphere(quadric, SMALL_SPHERE_RADIUS, NODE_SLICES_DETAIL,
				NODE_STACKS_DETAIL);
		// glut.glutSolidSphere(SMALL_SPHERE_RADIUS, NODE_SLICES_DETAIL,
		// NODE_STACKS_DETAIL);
		gl.glEndList();

	}

	@Override
	public void execute(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();

		CyNetworkView networkView = graphicsData.getNetworkView();
		float distanceScale = graphicsData.getDistanceScale();
		Set<Integer> selectedNodeIndices = graphicsData.getSelectionData()
				.getSelectedNodeIndices();
		int hoverNodeIndex = graphicsData.getHoverNodeIndex();

		// Currently supporting the following visual properties

		// VisualProperty<Double> NODE_X_LOCATION
		// VisualProperty<Double> NODE_Y_LOCATION
		// VisualProperty<Double> NODE_Z_LOCATION
		// VisualProperty<Paint> NODE_PAINT
		// VisualProperty<Boolean> NODE_VISIBLE
		// VisualProperty<Boolean> NODE_SELECTED
		// VisualProperty<Double> NODE_WIDTH
		// VisualProperty<Double> NODE_HEIGHT
		// VisualProperty<Double> NODE_DEPTH

		// Uncertain about the following visual properties

		// VisualProperty<Paint> NODE_FILL_COLOR

		float x, y, z;
		int index;
		networkView.updateView();
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			x = nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION)
					.floatValue() / distanceScale;
			y = nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION)
					.floatValue() / distanceScale;
			z = nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION)
					.floatValue() / distanceScale;

			index = nodeView.getModel().getIndex();
			gl.glLoadName(index);
			// gl.glLoadName(33);

			gl.glPushMatrix();
			gl.glTranslatef(x, y, z);

			/*
			 * gl.glScalef(nodeView.getVisualProperty(
			 * MinimalVisualLexicon.NODE_WIDTH).floatValue() / DISTANCE_SCALE,
			 * nodeView.getVisualProperty(
			 * MinimalVisualLexicon.NODE_HEIGHT).floatValue() / DISTANCE_SCALE,
			 * nodeView.getVisualProperty(
			 * RichVisualLexicon.NODE_DEPTH).floatValue() / DISTANCE_SCALE);
			 */

			Color color;

			if (selectedNodeIndices.contains(index)) {

				gl.glScalef(1.1f, 1.1f, 1.1f);

				color = (Color) nodeView
						.getVisualProperty(RichVisualLexicon.NODE_SELECTED_PAINT);

				gl.glColor3f(color.getRed() / 255.0f,
						color.getGreen() / 255.0f, color.getBlue() / 255.0f);

				nodeView.setVisualProperty(RichVisualLexicon.NODE_SELECTED,
						true);

				// Default color is below
				// gl.glColor3f(0.52f, 0.70f, 0.52f);
			} else if (index == hoverNodeIndex) {
				gl.glColor3f(0.52f, 0.52f, 0.70f);

				nodeView.setVisualProperty(RichVisualLexicon.NODE_SELECTED,
						false);
			} else {
				color = (Color) nodeView
						.getVisualProperty(MinimalVisualLexicon.NODE_PAINT);

				gl.glColor3f(color.getRed() / 255.0f,
						color.getGreen() / 255.0f, color.getBlue() / 255.0f);

				nodeView.setVisualProperty(RichVisualLexicon.NODE_SELECTED,
						false);

				// Default color is below
				// gl.glColor3f(0.73f, 0.73f, 0.73f);

			}

			// Draw it only if the visual property says it is visible
			if (nodeView.getVisualProperty(MinimalVisualLexicon.NODE_VISIBLE)) {
				gl.glCallList(nodeListIndex);
			}

			gl.glPopMatrix();
		}
	}

}
