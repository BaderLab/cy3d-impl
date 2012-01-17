package org.cytoscape.paperwing.internal.rendering.shapes;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

public class ScalableShapeDrawer {
	public static enum ShapeType {
		SHAPE_CUBIC, 
		SHAPE_TETRAHEDRAL
	}
	
	private Map<ShapeType, Integer> shapeLists;
	
	public ScalableShapeDrawer() {
		shapeLists = new HashMap<ShapeType, Integer>(12);
	}
	
	public void initialize(GL2 gl) {
		int shapeListIndex = gl.glGenLists(1);

		GLU glu = GLU.createGLU(gl);

		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);

		// Draw Shape
		// ---------

		gl.glNewList(shapeListIndex, GL2.GL_COMPILE);
		
		
		gl.glEndList();
	}
	
	
	
	public void drawShape(ShapeType shapeType) {
		
	}
}
