package org.cytoscape.paperwing.internal.rendering.shapes;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.RenderToolkit;

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
		initializeTetrahedron(gl);
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
		// gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		
		gl.glPushMatrix();
		gl.glScalef(halfLength, halfLength, halfLength);
		
		gl.glBegin(GL2.GL_QUADS);
		
		// +y face
		gl.glNormal3i(0, 1, 0);
		gl.glVertex3i(-1, 1, -1); // -x, -z
		gl.glVertex3i(1, 1, -1); // +x, -z
		gl.glVertex3i(1, 1, 1); // +x, +z
		gl.glVertex3i(-1, 1, 1); // -x, +z
		
		// +z face
		gl.glNormal3i(0, 0, 1);
		gl.glVertex3i(-1, 1, 1);
		gl.glVertex3i(1, 1, 1);
		gl.glVertex3i(1, -1, 1);
		gl.glVertex3i(-1, -1, 1);
		
		// +x face
		gl.glNormal3i(1, 0, 0);
		gl.glVertex3i(1, 1, 1);
		gl.glVertex3i(1, 1, -1);
		gl.glVertex3i(1, -1, -1);
		gl.glVertex3i(1, -1, 1);
		
		// -y face
		gl.glNormal3i(0, -1, 0);
		gl.glVertex3i(-1, -1, -1);
		gl.glVertex3i(1, -1, -1);
		gl.glVertex3i(1, -1, 1);
		gl.glVertex3i(-1, -1, 1);
		
		// -z face
		gl.glNormal3i(0, 0, -1);
		gl.glVertex3i(-1, 1, -1);
		gl.glVertex3i(1, 1, -1);
		gl.glVertex3i(1, -1, -1);
		gl.glVertex3i(-1, -1, -1);
		
		// -x face
		gl.glNormal3i(-1, 0, 0);
		gl.glVertex3i(-1, 1, 1);
		gl.glVertex3i(-1, 1, -1);
		gl.glVertex3i(-1, -1, -1);
		gl.glVertex3i(-1, -1, 1);
		
		gl.glEnd();
		gl.glPopMatrix();
		
		gl.glEndList();
		
		shapeLists.put(ShapeType.SHAPE_CUBIC, shapeListIndex);
	}
	
	// Tetrahedron inscribed in circle with radius 0.5
	private void initializeTetrahedron(GL2 gl) {
		int shapeListIndex = gl.glGenLists(1);

		double radius = 0.5;
		Vector3 yAxisDirection = new Vector3(0, 1, 0);
		Vector3 zAxisDirection = new Vector3(0, 0, 1);
		
		// Points' positions are relative to the center of the shape
		Vector3 topPoint = new Vector3(0, radius, 0);
		Vector3 nearLeftPoint = topPoint.rotate(zAxisDirection, Math.toRadians(120));
		nearLeftPoint = nearLeftPoint.rotate(yAxisDirection, Math.toRadians(30));
		
		Vector3 farPoint = nearLeftPoint.rotate(yAxisDirection, Math.toRadians(240));
		Vector3 nearRightPoint = nearLeftPoint.rotate(yAxisDirection, Math.toRadians(120));
		
		Vector3 frontNormal = topPoint.plus(nearLeftPoint).plus(nearRightPoint);
		frontNormal.normalizeLocal();
		
		Vector3 leftBackNormal = frontNormal.rotate(yAxisDirection, Math.toRadians(240));
		Vector3 rightBackNormal = frontNormal.rotate(yAxisDirection, Math.toRadians(120));
		Vector3 bottomNormal = new Vector3(0, -1, 0);
		
//		System.out.println("Tetrahedron coordinates: ");
//		System.out.println("top: " + topPoint);
//		System.out.println("nearLeft: " + nearLeftPoint);
//		System.out.println("nearLeft distance: " + nearLeftPoint.magnitude());
//		System.out.println("nearRight: " + nearRightPoint);
//		System.out.println("nearRight distance: " + nearRightPoint.magnitude());
//		System.out.println("far: " + farPoint);
//		System.out.println("far distance: " + farPoint.magnitude());
		
		gl.glNewList(shapeListIndex, GL2.GL_COMPILE);
		
		gl.glBegin(GL2.GL_TRIANGLES);
		
		RenderToolkit.setNormal(gl, frontNormal);
		RenderToolkit.drawPoint(gl, topPoint);
		RenderToolkit.drawPoint(gl, nearRightPoint);
		RenderToolkit.drawPoint(gl, nearLeftPoint);
		
		RenderToolkit.setNormal(gl, leftBackNormal);
		RenderToolkit.drawPoint(gl, topPoint);
		RenderToolkit.drawPoint(gl, farPoint);
		RenderToolkit.drawPoint(gl, nearLeftPoint);
		
		RenderToolkit.setNormal(gl, rightBackNormal);
		RenderToolkit.drawPoint(gl, topPoint);
		RenderToolkit.drawPoint(gl, nearRightPoint);
		RenderToolkit.drawPoint(gl, farPoint);
		
		RenderToolkit.setNormal(gl, bottomNormal);
		RenderToolkit.drawPoint(gl, nearRightPoint);
		RenderToolkit.drawPoint(gl, farPoint);
		RenderToolkit.drawPoint(gl, nearLeftPoint);

		gl.glEnd();
		
		gl.glEndList();
		
		shapeLists.put(ShapeType.SHAPE_TETRAHEDRAL, shapeListIndex);
	}
	
	public void drawShape(GL2 gl, ShapeType shapeType) {
		Integer listIndex = shapeLists.get(shapeType);
		
		if (listIndex != null) {
			gl.glCallList(listIndex);
		}
	}
}
