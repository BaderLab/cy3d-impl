package org.cytoscape.paperwing.internal.rendering.shapes;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.cytoscape.paperwing.internal.rendering.shapes.ScalableShapeDrawer.ShapeType;

public class EdgeShapeDrawer {
	
	private static int REGULAR_CYLINDER_SLICES = 3;
	private static int REGULAR_CYLINDER_STACKS = 1;
	
	private static int DASHED_CYLINDER_SLICES = 3;
	private static int DASHED_CYLINDER_STACKS = 1;
	
	public static enum EdgeShapeType {
		REGULAR,
		DASHED,
		DOTTED,
		REGULAR_LINE_BASED // Test drawing edges using OpenGL lines instead of polygons
	}
	
	private Map<EdgeShapeType, Integer> segmentLists;
	
	public EdgeShapeDrawer() {
		segmentLists = new HashMap<EdgeShapeType, Integer>();
	}

	public void initialize(GL2 gl) {
		initializeCylinder(gl);
		initializeDashedCylinder(gl);
		initializeDottedShape(gl);
		initializeLineBasedSegment(gl);
	}
	
	/**
	 * Initializes drawing for a radius 0.5, height 1 cylinder extending from the origin
	 * towards the positive z-axis direction.
	 * 
	 * @param gl The current GL context
	 */
	private void initializeCylinder(GL2 gl) {
		int listIndex = gl.glGenLists(1);

		GLU glu = GLU.createGLU(gl);

		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		
		gl.glNewList(listIndex, GL2.GL_COMPILE);
		glu.gluCylinder(quadric, 0.5, 0.5, 1.0,
				REGULAR_CYLINDER_SLICES, REGULAR_CYLINDER_STACKS);
		gl.glEndList();
		
		segmentLists.put(EdgeShapeType.REGULAR, listIndex);
	}
	
	/**
	 * Initializes drawing for a radius 0.5, height 1 cylinder to be used for dashes
	 * in the positive z-axis direction.
	 * 
	 * @param gl The current GL context
	 */
	private void initializeDashedCylinder(GL2 gl) {
		int listIndex = gl.glGenLists(1);

		GLU glu = GLU.createGLU(gl);

		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		
		gl.glNewList(listIndex, GL2.GL_COMPILE);
		glu.gluCylinder(quadric, 0.5, 0.5, 1.0,
				DASHED_CYLINDER_SLICES, DASHED_CYLINDER_STACKS);
		gl.glEndList();
		
		segmentLists.put(EdgeShapeType.DASHED, listIndex);
	}

	/**
	 * Initializes drawing for a shape not exceeding the boundaries of a radius
	 * 0.5 sphere to be used for dotting edges. The boundaries should extend
	 * from the origin towards the positive z-axis.
	 * 
	 * @param gl
	 */
	private void initializeDottedShape(GL2 gl) {
		int listIndex = gl.glGenLists(1);

		GLU glu = GLU.createGLU(gl);

		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		
		double length = 1.0 / Math.sqrt(2);
		
		gl.glNewList(listIndex, GL2.GL_COMPILE);
		glu.gluSphere(quadric, length / 2, 4, 4);
		gl.glEndList();
		
		segmentLists.put(EdgeShapeType.DOTTED, listIndex);
	}
	
	/**
	 * Performs initialization for drawing a segment of a line, but uses OpenGL lines instead of polygons to perform the drawing.
	 * The segment has length 1, and extends from the origin towards the positive z-axis.
	 */
	private void initializeLineBasedSegment(GL2 gl) {
		int listIndex = gl.glGenLists(1);
		
		gl.glNewList(listIndex, GL2.GL_COMPILE);
		gl.glBegin(GL2.GL_LINES);
		
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glVertex3f(0.0f, 0.0f, 1.0f);
		
		gl.glEnd();
		gl.glEndList();

		segmentLists.put(EdgeShapeType.REGULAR_LINE_BASED, listIndex);
	}
	
	public void drawSegment (GL2 gl, EdgeShapeType segmentType) {
		Integer segmentList = segmentLists.get(segmentType);
		
		if (segmentList != null) {
			gl.glCallList(segmentList);
		}
	}
	

}
