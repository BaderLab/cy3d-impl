package org.cytoscape.paperwing.internal;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.FloatBuffer;
import java.util.Random;
import java.util.Set;

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
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.RenderingEngineManager;

public class TestGraphics implements GLEventListener {

	private static final int NODE_COUNT = 2;
	private static final int EDGE_COUNT = 0;
	private static final float LARGE_SPHERE_RADIUS = 1.0f; // 1.5f
	private static final float SMALL_SPHERE_RADIUS = 0.125f; // 0.015f
	private static final float EDGE_RADIUS = 0.008f;

	private static final int NODE_SLICES_DETAIL = 24;
	private static final int NODE_STACKS_DETAIL = 24;
	private static final int EDGE_SLICES_DETAIL = 3;
	private static final int EDGE_STACKS_DETAIL = 1;

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
	
	private DrawnNode[] nodes;
	private DrawnEdge[] edges;

	private int nodeListIndex;
	private int edgeListIndex;

	private long startTime;
	private long endTime;
	private int framesElapsed = 0;

	private int nodeSeed = 556;
	private int edgeSeed = 556;
	
	private KeyboardMonitor keys;
	private MouseMonitor mouse;
	private SimpleCamera camera;
	
	private CyApplicationManager applicationManager;
	private CyNetworkManager networkManager;
	private CyNetworkViewManager networkViewManager;
	private RenderingEngineManager renderingEngineManager;
	
	static {
		
	}
	
	public static void initSingleton() {
		GLProfile.initSingleton(false);
		System.out.println("initSingleton called");
	}
	
	public TestGraphics() {
		keys = new KeyboardMonitor();
		mouse = new MouseMonitor();

		// TODO: add default constant speeds for camera movement
		camera = new SimpleCamera(new Vector3(0, 0, 2), new Vector3(0, 0, 0),
				new Vector3(0, 1, 0), 0.04, 0.003, 0.01, 0.01, 0.4);
	}
	
	public void trackInput(Component component) {
		component.addMouseListener(mouse);
		component.addMouseMotionListener(mouse);
		component.addMouseWheelListener(mouse);
		component.addFocusListener(mouse);
		
		component.addKeyListener(keys);
		component.addFocusListener(keys);
	}
	
	public void setManagers(CyApplicationManager applicationManager,
			CyNetworkManager networkManager,
			CyNetworkViewManager networkViewManager,
			RenderingEngineManager renderingEngineManager) {
		this.applicationManager = applicationManager;
		this.networkManager = networkManager;
		this.networkViewManager = networkViewManager;
		this.renderingEngineManager = renderingEngineManager;
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("JOGL Test Window");
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);

		// Use the system's default version of OpenGL
		GLProfile profile = GLProfile.getDefault();
		GLProfile.initSingleton(true);
		GLCapabilities capabilities = new GLCapabilities(profile);
		GLCanvas canvas = new GLCanvas(capabilities);

		TestGraphics graphics = new TestGraphics();

		canvas.addGLEventListener(graphics);
		frame.add(canvas);

		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
			}
		});

		frame.setVisible(true);
		
		graphics.trackInput(frame.getContentPane());
		
		FPSAnimator animator = new FPSAnimator(60);
		animator.add(canvas);
		animator.start();
		// animator.pause()
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		checkInput();

		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		Vector3 position = camera.getPosition();
		Vector3 target = camera.getTarget();
		Vector3 up = camera.getUp();

		// System.out.println(position + " " + target + " " + up);

		GLU glu = new GLU();
		glu.gluLookAt(position.x(), position.y(), position.z(), target.x(),
				target.y(), target.z(), up.x(), up.y(), up.z());

		// gl.glRotated(direction.angle(current) * 180 / Math.PI, normal.x(),
		// normal.y(), normal.z());
		// gl.glTranslated(-camera.x(), -camera.y(), -camera.z());

		float[] lightPosition = { -4.0f, 4.0f, 6.0f, 1.0f };
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,
				FloatBuffer.wrap(lightPosition));

		gl.glColor3f(0.6f, 0.6f, 0.6f);
		gl.glTranslatef(0.0f, 0.0f, -6.0f);

		gl.glColor3f(0.73f, 0.73f, 0.73f);
		drawNodes(gl);
		gl.glColor3f(0.53f, 0.53f, 0.55f);
		drawEdges(gl);

		framesElapsed++;
	}
	
	private void checkInput() {
		if (keys.hasHeld() || keys.hasNew()) {
			Set<Integer> pressed = keys.getPressed();
			Set<Integer> held = keys.getHeld();
			Set<Integer> released = keys.getReleased();
			
			if (pressed.contains(KeyEvent.VK_SPACE)) {
				endTime = System.nanoTime();
	
				double duration = (endTime - startTime) / Math.pow(10, 9);
				double frameRate = framesElapsed / duration;
				System.out.println("Average fps over " + duration + " seconds: "
						+ frameRate);
			}
			
			if (pressed.contains(KeyEvent.VK_C)) {
				camera.setSpeed(0.04, 0.003, 0.01, 0.1, 0.4);
				camera.moveTo(0, 0, 2);
			}
			
			if (pressed.contains(KeyEvent.VK_SPACE)) {
				System.out.println("===");
				System.out.print("direction: " + camera.getDirection());
				System.out.print(", left: " + camera.getLeft());
				System.out.println(", up: " + camera.getUp());
				System.out.print("position: " + camera.getPosition());
				System.out.println(", target: " + camera.getTarget());
				System.out.println("===");
			}
			
			if (held.contains(KeyEvent.VK_Z)) {
				camera.rollClockwise();
			}
			
			if (held.contains(KeyEvent.VK_X)) {
				camera.rollCounterClockwise();
			}
			
			if (held.contains(KeyEvent.VK_SHIFT)) {
			
				if (held.contains(KeyEvent.VK_LEFT)) {
					camera.orbitLeft();
				}
				
				if (held.contains(KeyEvent.VK_RIGHT)) {
					camera.orbitRight();
				}
				
				if (held.contains(KeyEvent.VK_UP)) {
					camera.orbitUp();
				}
				
				if (held.contains(KeyEvent.VK_DOWN)) {
					camera.orbitDown();
				}
				
			} else {
			
				if (pressed.contains(KeyEvent.VK_B)) {
					System.out.println("number of networks: "
							+ networkManager.getNetworkSet().size());
					System.out.println("current network: "
							+ applicationManager.getCurrentNetwork());
					if (applicationManager.getCurrentNetwork() != null) {
						System.out
								.println("number of nodes in current network: "
										+ applicationManager
												.getCurrentNetwork()
												.getNodeList().size());
					}
					System.out.println("current network view: "
							+ applicationManager.getCurrentNetworkView());
					if (applicationManager.getCurrentNetworkView() != null) {
						System.out
								.println("number of views in current network: "
										+ applicationManager
												.getCurrentNetworkView()
												.getNodeViews().size());
					}

					// System.out.println("supported visual properties: "
					//		+ applicationManager.getCurrentRenderingEngine()
					//				.getVisualLexicon()
					//				.getAllVisualProperties());
				}
				
				if (pressed.contains(KeyEvent.VK_N)) {
					System.out.println("current rendering engine: "
							+ applicationManager.getCurrentRenderingEngine().getClass().getName());
					
					System.out.println("number of rendering engines: "
							+ renderingEngineManager.getAllRenderingEngines().size());
					
					
				}
				
				if (pressed.contains(KeyEvent.VK_M)) {
					System.out.println("Old rendering engine: " + applicationManager.getCurrentRenderingEngine());
					
					renderingEngineManager.removeRenderingEngine(applicationManager.getCurrentRenderingEngine());
					
					System.out.println("New rendering engine: " + applicationManager.getCurrentRenderingEngine());
				}
				
				if (pressed.contains(KeyEvent.VK_COMMA)) {
					System.out.println("networkViewSet: " + networkViewManager.getNetworkViewSet());
					
					for (CyNetworkView view : networkViewManager.getNetworkViewSet()) {
						System.out.println("current model: " + view.getModel());
						System.out.println("current model suid: " + view.getModel().getSUID());
						System.out.println("current suid: " + view.getSUID());	
					}
				}

			
			}
			
			if (held.contains(KeyEvent.VK_W)) {
				camera.moveForward();
			}
			
			if (held.contains(KeyEvent.VK_S)) {
				camera.moveBackward();
			}
			
			if (held.contains(KeyEvent.VK_A)) {
				camera.moveLeft();
			}
			
			if (held.contains(KeyEvent.VK_D)) {
				camera.moveRight();
			}
			
			if (held.contains(KeyEvent.VK_Q)) {
				camera.moveDown();
			}
			
			if (held.contains(KeyEvent.VK_E)) {
				camera.moveUp();
			}
			
			keys.update();
		}
		
		if (mouse.hasMoved() || mouse.hasNew()) {
			if (keys.getHeld().contains(KeyEvent.VK_SHIFT)) {
				camera.turnRight(mouse.dX());
				camera.turnUp(mouse.dY());
			}
			
			if (mouse.dWheel() != 0) {
				camera.zoomOut((double) mouse.dWheel());
			}
			
			mouse.update();
		}
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
		createDisplayLists(gl);
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
		random.setSeed(500);
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
		
		
		float[] specularReflection = { 0.5f, 0.5f, 0.5f, 1.0f };
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR,
				FloatBuffer.wrap(specularReflection));
		gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 40);
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
	
	/*
	// Draw X axis gl.glTranslatef(-overhang, 0.0f, 0.0f);
	gl.glRotatef(90, 0, 1, 0); gl.glColor3f(1.0f, 0.0f, 0.0f);
	glut.glutSolidCylinder(0.005f, axisLength, 6, 3); gl.glRotatef(-90, 0, 1, 0); gl.glTranslatef(overhang, 0.0f, 0.0f);
	
	// Draw Y axis gl.glTranslatef(0.0f, -overhang, 0.0f);
	gl.glRotatef(-90, 1, 0, 0); gl.glColor3f(0.0f, 1.0f, 0.0f);
	glut.glutSolidCylinder(0.005f, axisLength, 6, 3); gl.glRotatef(90, 1, 0, 0); gl.glTranslatef(0.0f, overhang, 0.0f);
	
	// Draw Z axis gl.glTranslatef(0.0f, 0.0f, -overhang);
	gl.glColor3f(0.0f, 0.0f, 1.0f); glut.glutSolidCylinder(0.005f,
	axisLength, 6, 3); gl.glTranslatef(0.0f, 0.0f, overhang);
	*/
}
