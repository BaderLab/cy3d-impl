package org.baderlab.cy3d.internal.rendering.shapes;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.tools.RenderToolkit;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.jogamp.opengl.util.gl2.GLUT;

public class ScalableShapeDrawer {
	
	public static enum Shape {
		SHAPE_SPHERE,
		SHAPE_CUBE, 
		SHAPE_TETRAHEDRON,
	}
	
	public static enum Detail {
		DETAIL_LOW(6),
		DETAIL_MED(12),
		DETAIL_HIGH(24);
		
		private final int sphereDetail;
		
		private Detail(int sphereDetail) {
			this.sphereDetail = sphereDetail;
		}
	}
	
	private Table<Shape, Detail, Integer> shapeLists = ImmutableTable.of();
	
	
	public void initialize(GL2 gl) {
		ImmutableTable.Builder<Shape, Detail, Integer> builder = ImmutableTable.builder();
		
		builder.put(Shape.SHAPE_SPHERE, Detail.DETAIL_LOW,  initializeSphere(gl, Detail.DETAIL_LOW));
		builder.put(Shape.SHAPE_SPHERE, Detail.DETAIL_MED,  initializeSphere(gl, Detail.DETAIL_MED));
		builder.put(Shape.SHAPE_SPHERE, Detail.DETAIL_HIGH, initializeSphere(gl, Detail.DETAIL_HIGH));
		
		int cubeIndex = initializeCube(gl);
		builder.put(Shape.SHAPE_CUBE, Detail.DETAIL_LOW, cubeIndex);
		builder.put(Shape.SHAPE_CUBE, Detail.DETAIL_MED, cubeIndex);
		builder.put(Shape.SHAPE_CUBE, Detail.DETAIL_HIGH, cubeIndex);
		
		int tetIndex = initializeTetrahedron(gl);
		builder.put(Shape.SHAPE_TETRAHEDRON, Detail.DETAIL_LOW, tetIndex);
		builder.put(Shape.SHAPE_TETRAHEDRON, Detail.DETAIL_MED, tetIndex);
		builder.put(Shape.SHAPE_TETRAHEDRON, Detail.DETAIL_HIGH, tetIndex);
		
		shapeLists = builder.build();
	}
	
	// Diameter 1 sphere
	private int initializeSphere(GL2 gl, Detail detailLevel) {
		int shapeListIndex = gl.glGenLists(1);

		GLU glu = GLU.createGLU(gl);

		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		
		gl.glNewList(shapeListIndex, GL2.GL_COMPILE);
		glu.gluSphere(quadric, 0.5, detailLevel.sphereDetail, detailLevel.sphereDetail); 
		gl.glEndList();
		
		return shapeListIndex;
	}
	
	// Cube inscribed in a radius 0.5 sphere
	private int initializeCube(GL2 gl) {
		int shapeListIndex = gl.glGenLists(1);

		GLU glu = GLU.createGLU(gl);
		GLUT glut = new GLUT();
		
		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		
		float halfLength = (float) (1.0 / Math.sqrt(2) / 2);
		
		gl.glNewList(shapeListIndex, GL2.GL_COMPILE);
		// gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		
		gl.glPushMatrix();
		//gl.glScalef(halfLength, halfLength, halfLength);
		//gl.glTranslatef(0.0f, 0.0f, -0.5f);
		
		//glu.gluCylinder(quadric, 1.0, 1.0, 1.0, 4, 1);
		
		glut.glutSolidCube(0.5f);
		// gl.glColor3f(0.0f, 0.0f, 0.0f);
		// glut.glutWireCube(0.5f);
		
		gl.glPopMatrix();
		
		gl.glEndList();
		
		return shapeListIndex;
	}
	
	
	// Tetrahedron inscribed in circle with radius 0.5
	private int initializeTetrahedron(GL2 gl) {
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
		
		return shapeListIndex;
	}
	
	
	public void drawShape(GL2 gl, Shape shapeType, Detail detailLevel) {
		Integer listIndex = shapeLists.get(shapeType, detailLevel);
		
		if (listIndex != null) {
			gl.glCallList(listIndex);
		}
	}
}
