package org.cytoscape.paperwing.internal.graphics;

import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

public class RenderCursorProcedure implements ReadOnlyGraphicsProcedure {
	/** The length to draw each segment of the reticle */
	private static final double RETICLE_LENGTH = 0.03;
	
	int reticleListIndex;
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		
		reticleListIndex = gl.glGenLists(1);
		
		GLU glu = GLU.createGLU(gl);

		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		
		// Draw a line for the reticle
		// -----------------------------
		
		GLUquadric reticleQuadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(reticleQuadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(reticleQuadric, GLU.GLU_SMOOTH);
		
		float axisLength = 0.056f;
		float overHang = 0.028f;
		float radius = 0.002f;
		
		gl.glNewList(reticleListIndex, GL2.GL_COMPILE);
		// glu.gluSphere(pointerQuadric, SMALL_SPHERE_RADIUS / 4, 4, 4);
		
		// This is the cylinder
		// glu.gluCylinder(reticleQuadric, RETICLE_RADIUS, RETICLE_RADIUS, RETICLE_LENGTH,
		//		SELECT_BORDER_SLICES_DETAIL, SELECT_BORDER_STACKS_DETAIL);
		
		// This is the line approach
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(0, 0, RETICLE_LENGTH);
		gl.glEnd();
		
		gl.glEndList();	
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		// TODO Auto-generated method stub

	}

}
