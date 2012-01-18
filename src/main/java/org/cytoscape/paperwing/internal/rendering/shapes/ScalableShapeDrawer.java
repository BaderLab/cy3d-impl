package org.cytoscape.paperwing.internal.rendering.shapes;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

public class ScalableShapeDrawer {
	
	private static final int SPHERE_SLICES_DETAIL = 12;
	private static final int SPHERE_STACKS_DETAIL = 12;
	
	public static enum ShapeType {
		SHAPE_SPHERICAL,
		SHAPE_CUBIC, 
		SHAPE_TETRAHEDRAL,
		SHAPE_CONICAL,
		SHAPE_CYLINDRICAL,
		SHAPE_DOUGHNUT,
	}
	
	private Map<ShapeType, Integer> shapeLists;
	
	public ScalableShapeDrawer() {
		shapeLists = new HashMap<ShapeType, Integer>(12);
	}
	
	public void initialize(GL2 gl) {
		initializeSphere(gl);
		initializeCube(gl);
	}
	
	// Diameter 1 sphere
	private void initializeSphere(GL2 gl) {
		int shapeListIndex = gl.glGenLists(1);

		GLU glu = GLU.createGLU(gl);

		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		
		gl.glNewList(shapeListIndex, GL2.GL_COMPILE);
		glu.gluSphere(quadric, 0.5, SPHERE_SLICES_DETAIL, SPHERE_STACKS_DETAIL); 
		gl.glEndList();
		
		
		shapeLists.put(ShapeType.SHAPE_SPHERICAL, shapeListIndex);
	}
	
	// Cube inscribed in a radius 0.5 sphere
	private void initializeCube(GL2 gl) {
		int shapeListIndex = gl.glGenLists(1);

		float halfLength = (float) (1.0 / Math.sqrt(2) / 2);
		
		gl.glNewList(shapeListIndex, GL2.GL_COMPILE);
		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		
		// +y face
		gl.glNormal3f(0, 1, 0);
		gl.glVertex3f(-halfLength, halfLength, -halfLength); // -x, -z
		gl.glVertex3f(halfLength, halfLength, -halfLength); // +x, -z
		gl.glVertex3f(-halfLength, halfLength, halfLength); // -x, +z
		gl.glVertex3f(halfLength, halfLength, halfLength); // +x, +z
		
		// +z face
		gl.glNormal3f(0, 0, 1);
		gl.glVertex3f(halfLength, -halfLength, halfLength);
		
		// +x face
		gl.glNormal3f(1, 0, 0);
		gl.glVertex3f(halfLength, halfLength, -halfLength);
		gl.glVertex3f(halfLength, -halfLength, -halfLength);
		
		// -z face
		gl.glNormal3f(0, 0, -1);
		gl.glVertex3f(-halfLength, halfLength, -halfLength);
		gl.glVertex3f(-halfLength, -halfLength, -halfLength);
		
		// -x face
		gl.glNormal3f(-1, 0, 0);
		gl.glVertex3f(-halfLength, halfLength, halfLength);
		gl.glVertex3f(-halfLength, -halfLength, halfLength);
		
		// +z face
		gl.glNormal3f(0, 0, 1);
		gl.glVertex3f(halfLength, -halfLength, halfLength);
		
		// -y face
		gl.glNormal3f(0, -1, 0);
		gl.glVertex3f(-halfLength, -halfLength, -halfLength);
		gl.glVertex3f(halfLength, -halfLength, -halfLength);
		
//		// -y face
//		gl.glNormal3f(0, -1, 0);
//		gl.glVertex3f(halfLength, -halfLength, -halfLength);
//		gl.glVertex3f(-halfLength, -halfLength, -halfLength);
		
		
		
		// gl.glVertex3f(-cornerCoordinate, -cornerCoordinate, cornerCoordinate);
		// gl.glNorm
		
		gl.glEnd();
		
		gl.glEndList();
		
		shapeLists.put(ShapeType.SHAPE_CUBIC, shapeListIndex);
	}
	
	public void drawShape(GL2 gl, ShapeType shapeType) {
		Integer listIndex = shapeLists.get(shapeType);
		
		if (listIndex != null) {
			gl.glCallList(listIndex);
		}
	}
}
