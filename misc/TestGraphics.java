package org.cytoscape.paperwing.internal;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

public class TestGraphics implements GLEventListener, MouseInputListener{

	private static final int NODE_COUNT = 30000;
	private static final int EDGE_COUNT = 0;
	private static final float LARGE_SPHERE_RADIUS = 2.0f;
	private static final float SMALL_SPHERE_RADIUS = 0.03f;
	private static final float EDGE_RADIUS = 0.008f;

	private static final int NODE_SLICES_DETAIL = 6;
	private static final int NODE_STACKS_DETAIL = 6;

	private static final int EDGE_SLICES_DETAIL = 3;
	private static final int EDGE_STACKS_DETAIL = 1;

	private DrawnNode[] nodes;
	private DrawnEdge[] edges;

	private class DrawnNode {
		public float x;
		public float y;
		public float z;
	}

	private class DrawnEdge {
		public float x;
		public float y;
		public float z;
		public float rotateAxisX;
		public float rotateAxisY;
		public float rotateAxisZ;
		public float rotateAngle;
		public float length;
	}

	private float yRotate = 0.0f;
	private float xRotate = 0.0f;

	private int nodeListIndex;
	private int edgeListIndex;

	private long startTime;
	private long endTime;
	private int framesElapsed = 0;

	private int nodeSeed = 556;
	private int edgeSeed = 556;

	private int lastX;
	private int lastY;

	private float xFace = 1.0f;
	private float yFace = 0.0f;
	private float zFace = 0.0f;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("JOGL Test");
		frame.setSize(650, 650);

		frame.setLocationRelativeTo(null);

		// Use the system's default version of OpenGL
		GLProfile profile = GLProfile.getDefault();

		GLProfile.initSingleton(true);

		GLCapabilities capab = new GLCapabilities(profile);
		GLCanvas canvas = new GLCanvas(capab);

		TestGraphics graphics = new TestGraphics();

		canvas.addGLEventListener(graphics);
		canvas.addMouseListener(graphics);
		canvas.addMouseMotionListener(graphics);
		frame.add(canvas);

		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		frame.setVisible(true);

		FPSAnimator animator = new FPSAnimator(60);
		animator.add(canvas);
		animator.start();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glColor3f(0.6f, 0.6f, 0.6f);

		gl.glTranslatef(0.0f, 0.0f, -6.0f);
	

		// Use dot product to find angle
		float angle = (float) Math.toDegrees(Math.acos(zFace
				/ (Math.sqrt(Math.pow(xFace, 2) + Math.pow(yFace, 2)
						+ Math.pow(zFace, 2)))));

		// Find rotation vector with cross product
		gl.glRotatef(angle, -yFace, xFace, 0);
		
		GLUT glut = new GLUT();

		float axisLength = 1.8f;
		float overhang = 0.0f;

		
		// Draw X axis gl.glTranslatef(-overhang, 0.0f, 0.0f);
		gl.glRotatef(90, 0, 1, 0); gl.glColor3f(1.0f, 0.0f, 0.0f);
		glut.glutSolidCylinder(0.005f, axisLength, 6, 3); gl.glRotatef(-90, 0, 1, 0); gl.glTranslatef(overhang, 0.0f, 0.0f);
		
		// Draw Y axis gl.glTranslatef(0.0f, -overhang, 0.0f);
		gl.glRotatef(-90, 1, 0, 0); gl.glColor3f(0.0f, 1.0f, 0.0f);
		glut.glutSolidCylinder(0.005f, axisLength, 6, 3); gl.glRotatef(90, 1, 0, 0); gl.glTranslatef(0.0f, overhang, 0.0f);
		
		// Draw Z axis gl.glTranslatef(0.0f, 0.0f, -overhang);
		gl.glColor3f(0.0f, 0.0f, 1.0f); glut.glutSolidCylinder(0.005f,
		axisLength, 6, 3); gl.glTranslatef(0.0f, 0.0f, overhang);
		 

		// gl.glRotatef(90, 0.0f, -1.0f, 0.0f);

		float[] specularReflection = { 1.0f, 1.0f, 1.0f, 1.0f };
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR,
				FloatBuffer.wrap(specularReflection));
		gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 20);

		gl.glColor3f(0.73f, 0.73f, 0.75f);
		drawNodes(gl);
		gl.glColor3f(0.53f, 0.53f, 0.55f);
		drawEdges(gl);
		// drawNodesEdges(gl);

		framesElapsed++;
	}

	private void drawNodes(GL2 gl) {
		float x, y, z;

		// gl.glColor3f(0.5f, 0.5f, 0.5f);
		for (int i = 0; i < NODE_COUNT; i++) {
			x = nodes[i].x;
			y = nodes[i].y;
			z = nodes[i].z;

			gl.glTranslatef(x, y, z);
			// glut.glutSolidSphere(SMALL_SPHERE_RADIUS, 5, 5);
			gl.glCallList(nodeListIndex);
			gl.glTranslatef(-x, -y, -z);
		}
	}

	private void drawEdges(GL2 gl) {

		// gl.glColor3f(0.9f, 0.1f, 0.1f);
		for (int i = 0; i < EDGE_COUNT; i++) {
			gl.glTranslatef(edges[i].x, edges[i].y, edges[i].z);
			gl.glRotatef(edges[i].rotateAngle, edges[i].rotateAxisX,
					edges[i].rotateAxisY, edges[i].rotateAxisZ);
			gl.glScalef(1.0f, 1.0f, edges[i].length);
			gl.glCallList(edgeListIndex);
			gl.glScalef(1.0f, 1.0f, 1.0f / edges[i].length);
			// glut.glutSolidCylinder(EDGE_RADIUS, edges[i].length,
			// EDGE_SLICES_DETAIL, EDGE_STACKS_DETAIL);

			// gl.glCallList(nodeListIndex);
			// Undo the transformation operations we performed above
			gl.glRotatef(-edges[i].rotateAngle, edges[i].rotateAxisX,
					edges[i].rotateAxisY, edges[i].rotateAxisZ);
			gl.glTranslatef(-edges[i].x, -edges[i].y, -edges[i].z);
		}
	}

	private void drawNodesEdges(GL2 gl) {
		gl.glCallList(edgeListIndex);
		gl.glCallList(nodeListIndex);
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		initLighting(drawable);

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_DEPTH_TEST);

		gl.glDepthFunc(GL.GL_LEQUAL);
		// gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
		// gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);

		gl.glViewport(0, 0, drawable.getWidth(), drawable.getHeight());

		generateNodes();
		generateEdges();
		startTime = System.nanoTime();
		// createDisplayListsIndividual(gl);
		createDisplayLists(gl);
	}

	private void createDisplayListsIndividual(GL2 gl) {
		nodeListIndex = gl.glGenLists(1);
		edgeListIndex = gl.glGenLists(1);

		GLU glu = new GLU();

		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);

		gl.glNewList(nodeListIndex, GL2.GL_COMPILE);
		float x, y, z;
		for (int i = 0; i < NODE_COUNT; i++) {
			x = nodes[i].x;
			y = nodes[i].y;
			z = nodes[i].z;

			gl.glTranslatef(x, y, z);
			glu.gluSphere(quadric, SMALL_SPHERE_RADIUS, NODE_SLICES_DETAIL,
					NODE_STACKS_DETAIL);
			gl.glTranslatef(-x, -y, -z);
		}
		gl.glEndList();

		gl.glNewList(edgeListIndex, GL2.GL_COMPILE);
		for (int i = 0; i < EDGE_COUNT; i++) {
			gl.glTranslatef(edges[i].x, edges[i].y, edges[i].z);
			gl.glRotatef(edges[i].rotateAngle, edges[i].rotateAxisX,
					edges[i].rotateAxisY, edges[i].rotateAxisZ);
			gl.glScalef(1.0f, 1.0f, edges[i].length);
			glu.gluCylinder(quadric, EDGE_RADIUS, EDGE_RADIUS, 1.0,
					EDGE_SLICES_DETAIL, EDGE_STACKS_DETAIL);
			gl.glScalef(1.0f, 1.0f, 1.0f / edges[i].length);

			// Undo the transformation operations we performed above
			gl.glRotatef(-edges[i].rotateAngle, edges[i].rotateAxisX,
					edges[i].rotateAxisY, edges[i].rotateAxisZ);
			gl.glTranslatef(-edges[i].x, -edges[i].y, -edges[i].z);
		}
		gl.glEndList();
	}

	private void createDisplayLists(GL2 gl) {
		nodeListIndex = gl.glGenLists(1);
		edgeListIndex = gl.glGenLists(1);

		GLUT glut = new GLUT();
		GLU glu = new GLU();

		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);

		gl.glNewList(nodeListIndex, GL2.GL_COMPILE);
		glu.gluSphere(quadric, SMALL_SPHERE_RADIUS, NODE_SLICES_DETAIL,
				NODE_STACKS_DETAIL);
		// glut.glutSolidSphere(SMALL_SPHERE_RADIUS, NODE_SLICES_DETAIL,
		// NODE_STACKS_DETAIL);
		gl.glEndList();

		gl.glNewList(edgeListIndex, GL2.GL_COMPILE);
		glu.gluCylinder(quadric, EDGE_RADIUS, EDGE_RADIUS, 1.0,
				EDGE_SLICES_DETAIL, EDGE_STACKS_DETAIL);
		// glut.glutSolidCylinder(EDGE_RADIUS, 1.0f, EDGE_SLICES_DETAIL,
		// EDGE_STACKS_DETAIL);
		gl.glEndList();
	}

	private void generateNodes() {
		Random random = new Random();
		// random.setSeed(nodeSeed);
		nodeSeed++;
		// 500 should be the default seed

		nodes = new DrawnNode[NODE_COUNT];

		float x, y, z;
		float radius = LARGE_SPHERE_RADIUS;

		for (int i = 0; i < NODE_COUNT; i++) {
			nodes[i] = new DrawnNode();

			do {
				x = (float) (radius * 2 * random.nextFloat() - radius);
				y = (float) (radius * 2 * random.nextFloat() - radius);
				z = (float) (radius * 2 * random.nextFloat() - radius);
			} while (Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2) > Math
					.pow(radius, 2));

			nodes[i].x = x;
			nodes[i].y = y;
			nodes[i].z = z;
		}

		// System.out.println("Last node float: " + random.nextFloat());
	}

	private void generateEdges() {
		Random random = new Random();
		// random.setSeed(edgeSeed);
		edgeSeed++;

		edges = new DrawnEdge[EDGE_COUNT];

		int firstNode, secondNode;
		DrawnNode first, second;

		for (int i = 0; i < EDGE_COUNT; i++) {
			firstNode = random.nextInt(NODE_COUNT);
			secondNode = random.nextInt(NODE_COUNT);

			// System.out.println("Edge from, " + firstNode + " to, " +
			// secondNode);

			first = nodes[firstNode];
			second = nodes[secondNode];

			// System.out.println("Edge runs from (" + first.x + ",");

			edges[i] = new DrawnEdge();

			edges[i].x = first.x;
			edges[i].y = first.y;
			edges[i].z = first.z;
			edges[i].length = (float) Math.sqrt(Math.pow(first.x - second.x, 2)
					+ Math.pow(first.y - second.y, 2)
					+ Math.pow(first.z - second.z, 2));

			// System.out.println("Edge has length " + edges[i].length);

			edges[i].rotateAxisX = first.y - second.y;
			edges[i].rotateAxisY = second.x - first.x;
			edges[i].rotateAxisZ = 0;

			if (edges[i].length < (EDGE_RADIUS * 2)) {
				edges[i].length = EDGE_RADIUS * 2;
			}

			// Convert radians to degrees as well
			edges[i].rotateAngle = (float) (Math.acos((second.z - first.z)
					/ edges[i].length) * 180 / Math.PI);
		}

		// System.out.println("Last edge int: " + random.nextInt(NODE_COUNT));
	}

	private void initLighting(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		float[] global = { 0.2f, 0.2f, 0.2f, 1.0f };

		gl.glEnable(GL2.GL_LIGHTING);
		gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(global));
		gl.glShadeModel(GL2.GL_SMOOTH);

		float[] ambient = { 0.0f, 0.0f, 0.0f, 1.0f };
		float[] diffuse = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] specular = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] position = { -4.0f, 4.0f, 6.0f, 1.0f };

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, FloatBuffer.wrap(ambient));
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, FloatBuffer.wrap(diffuse));
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, FloatBuffer.wrap(specular));
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, FloatBuffer.wrap(position));

		gl.glEnable(GL2.GL_LIGHT0);

		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {

		if (height <= 0) {
			height = 1;
		}

		GL2 gl = drawable.getGL().getGL2();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU glu = new GLU();
		glu.gluPerspective(45.0f, (float) width / height, 0.2f, 50.0f);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		lastX = e.getX();
		lastY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		endTime = System.nanoTime();

		double duration = (endTime - startTime) / Math.pow(10, 9);
		double frameRate = framesElapsed / duration;
		System.out.println("Average fps over " + duration + " seconds: "
				+ frameRate);
		
		rotateY(1);
		System.out.println("New facing: (" + xFace + ", " + yFace + ", " + zFace + ")");
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		float xDelta = e.getX() - lastX;
		float yDelta = e.getY() - lastY;

		xRotate += xDelta / 8.0f;
		yRotate += yDelta / 8.0f;

		// yRotate = yRotate % 360;
		// xRotate = xRotate % 360;
		// yFace += yRotate;
		
		//rotateY(yDelta / 10);
		//rotateX(xDelta / 10);
		
		lastX = e.getX();
		lastY = e.getY();
	}

	// Rotation about the absolute y-axis, by the right-hand rule
	private void rotateY(float degrees) {
		// Calculate radius of the rotation circle that is perpendicular to the
		// y-axis
		float radius = (float) Math.sqrt(Math.pow(xFace, 2)
				+ Math.pow(zFace, 2));
		
		System.out.println("Radius: " + radius);

		// Determine current angle between projection of facing vector on the xz
		// plane and z-axis, taking into account the right-hand rule about the 
		// y-axis
		float angle = (float) Math.toDegrees(Math.acos(Math.min(1, zFace)));
		
		System.out.println("Angle: " + angle);
		
		// Check if the angle was supposed to lie in the (-, -) or the (-, +)
		// quadrants of the xz plane
		if (xFace < 0) {
			angle = 360 - angle;
		}
		
		// Rotate in the direction of the right-hand rule
		float newAngle = angle + degrees;
		
		System.out.println("New angle: " + newAngle);
		
		// Update coordinates according to new angle
		zFace = (float) Math.cos(Math.toRadians(newAngle)) * radius;
		xFace = (float) Math.sin(Math.toRadians(newAngle)) * radius;

		// System.out.println("Facing before correction: (" + xFace + ", " + yFace + ", " + zFace + ")");
		
		// Ensure that the new facing vector has magnitude 1
		float magnitudeY = (float) Math.sqrt(1 - Math.pow(xFace, 2) - Math.pow(zFace, 2));
		
		// System.out.println("MagnitudeY: " + magnitudeY);
		
		// yFace = (yFace > 0) ? magnitudeY : -magnitudeY;
		
		// System.out.println("Facing after correction: (" + xFace + ", " + yFace + ", " + zFace + ")");
	}
	
	// Rotation about the absolute x-axis, by the right-hand rule
	private void rotateX(float degrees) {
		float radius = (float) Math.sqrt(Math.pow(yFace, 2)
				+ Math.pow(zFace, 2));

		float angle = (float) Math.toDegrees(Math.acos(Math.min(1, zFace)));
		
		// Check if the angle was supposed to lie in the (+, -) or the (+, +)
		// quadrants of the yz plane
		if (yFace > 0) {
			angle = 360 - angle;
		}
		
		// Rotate in the direction of the right-hand rule
		float newAngle = angle + degrees;
		
		// Update coordinates according to new angle
		zFace = (float) Math.cos(Math.toRadians(newAngle)) * radius;
		yFace = (float) -Math.sin(Math.toRadians(newAngle)) * radius;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
	}
}