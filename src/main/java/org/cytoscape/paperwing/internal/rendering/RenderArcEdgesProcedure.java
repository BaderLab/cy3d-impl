package org.cytoscape.paperwing.internal.rendering;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.GeometryToolkit;
import org.cytoscape.paperwing.internal.tools.RenderToolkit;

public class RenderArcEdgesProcedure implements ReadOnlyGraphicsProcedure {

	private static final double SEGMENT_RADIUS = 0.05;
	private static final int SEGMENT_SLICES = 4;
	private static final int SEGMENT_STACKS = 1;
	
	private int segmentListIndex;
	
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
		// TODO Auto-generated method stub
		
	}
	
	private void drawArcSegments(GL2 gl, Vector3 start, Vector3 end, double radius, double angle, int segments) {
		Vector3 displacement = end.subtract(start);
		double displacementLength = displacement.magnitude();
		
		// Use cosine law
		double arcAngle;
		arcAngle = GeometryToolkit.saferArcCos(
				(2 * radius * radius - displacementLength * displacementLength) 
				/ (2 * radius * radius));
		
		double nearCornerAngle = Math.PI / 2 - (arcAngle / 2);
		
		
	}
	
	private void drawSegment(GL2 gl, Vector3 start, Vector3 end) {
		Vector3 displacement = end.subtract(start);
		
		RenderToolkit.setUpFacingTransformation(gl, start, displacement);
		
		gl.glScaled(1, 1, displacement.magnitude());
		
		// need a scale transformation for segment radius?
		
		gl.glCallList(segmentListIndex);
	}

}
