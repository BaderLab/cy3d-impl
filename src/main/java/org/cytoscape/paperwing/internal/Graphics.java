package org.cytoscape.paperwing.internal;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Set;
import java.util.TreeMap;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;

import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class Graphics implements GLEventListener {

	private static final float LARGE_SPHERE_RADIUS = 1.0f; // 1.5f
	private static final float SMALL_SPHERE_RADIUS = 0.102f; // 0.015f
	private static final float EDGE_RADIUS = 0.018f;
	
	private static final float EDGE_CURVE_DISTANCE = 0.7f;
	private static final float EDGE_CURVE_FACTOR = 0.31f;
	private static final int EDGES_PER_RADIUS = 3;
	
	/*
	 * This value controls distance scaling when converting from node
	 * coordinates to drawing coordinates
	 */
	private static final float DISTANCE_SCALE = 178.0f; 
	
	private static final int NODE_SLICES_DETAIL = 24;
	private static final int NODE_STACKS_DETAIL = 24;
	private static final int EDGE_SLICES_DETAIL = 12;
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
	private DrawnNode testNode = new DrawnNode();
	private DrawnEdge[] edges;

	private int nodeListIndex;
	private int edgeListIndex;
	
	private long startTime;
	private long endTime;
	private int framesElapsed = 0;
	private int screenHeight;
	private int screenWidth;

	private int nodeSeed = 556;
	private int edgeSeed = 556;
	private int selected = -1;
	private int hovered = -1;
	
	private KeyboardMonitor keys;
	private MouseMonitor mouse;
	private SimpleCamera camera;
	
	private CyApplicationManager applicationManager;
	private CyNetworkManager networkManager;
	private CyNetworkViewManager networkViewManager;
	private RenderingEngineManager renderingEngineManager;
	
	private CyNetworkView networkView;
	private VisualLexicon visualLexicon;
	
	public static void initSingleton() {
		GLProfile.initSingleton(false);
		System.out.println("initSingleton called");
	}
	
	public Graphics(CyNetworkView networkView, VisualLexicon visualLexicon) {
		keys = new KeyboardMonitor();
		mouse = new MouseMonitor();

		// TODO: add default constant speeds for camera movement
		camera = new SimpleCamera(new Vector3(0, 0, 2), new Vector3(0, 0, 0),
				new Vector3(0, 1, 0), 0.04, 0.0033, 0.01, 0.01, 0.4);
		
		this.networkView = networkView;
		this.visualLexicon = visualLexicon;
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

	@Override
	public void display(GLAutoDrawable drawable) {
		
		//drawable.swapBuffers();
		GL2 gl = drawable.getGL().getGL2();
		
		checkInput(gl);

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
		// gl.glTranslatef(0.0f, 0.0f, -6.0f);

		gl.glColor3f(0.73f, 0.73f, 0.73f);
		drawNodes(gl);
		gl.glColor3f(0.53f, 0.53f, 0.55f);
		drawEdges(gl);

		framesElapsed++;
	}
	
	private void checkInput(GL2 gl) {
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
				
				startTime = System.nanoTime();
				framesElapsed = 0;
			}
			
			if (pressed.contains(KeyEvent.VK_C)) {
				camera = new SimpleCamera(new Vector3(0, 0, 2), new Vector3(0, 0, 0),
						new Vector3(0, 1, 0), 0.04, 0.002, 0.01, 0.01, 0.4);
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
			
				if (held.contains(KeyEvent.VK_LEFT)) {
					camera.turnLeft(4);
				}
				
				if (held.contains(KeyEvent.VK_RIGHT)) {
					camera.turnRight(4);
				}
				
				if (held.contains(KeyEvent.VK_UP)) {
					camera.turnUp(4);
				}
				
				if (held.contains(KeyEvent.VK_DOWN)) {
					camera.turnDown(4);
				}
			
			}
			
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

			if (pressed.contains(KeyEvent.VK_H)) {
				System.out.println("visualLexicon: " + visualLexicon);
				
				float x, y, z;
				if (visualLexicon != null) {
					
					for (View<CyNode> nodeView : networkView.getNodeViews()) {
						x = ((Double) nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION)).floatValue();
						y = ((Double) nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION)).floatValue();
						z = ((Double) nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION)).floatValue();
						
						System.out.println("Node found at " + x + ", " + y + ", " + z);
					}
				
				}
			}
			
			if (pressed.contains(KeyEvent.VK_J)) {
				System.out.println("number of nodes: " + networkView.getNodeViews().size());
			}
			
			if (held.contains(KeyEvent.VK_W)) {
				camera.moveUp();
			}
			
			if (held.contains(KeyEvent.VK_S)) {
				camera.moveDown();
			}
			
			if (held.contains(KeyEvent.VK_A)) {
				camera.moveLeft();
			}
			
			if (held.contains(KeyEvent.VK_D)) {
				camera.moveRight();
			}
			
			if (held.contains(KeyEvent.VK_Q)) {
				camera.moveBackward();
			}
			
			if (held.contains(KeyEvent.VK_E)) {
				camera.moveForward();
			}
			
			keys.update();
		}
		
		if (mouse.hasMoved() || mouse.hasNew()) {
			if (keys.getHeld().contains(KeyEvent.VK_SHIFT)) {
				camera.turnRight(mouse.dX());
				camera.turnDown(mouse.dY());
			}
			
			if (mouse.dWheel() != 0) {
				camera.zoomOut((double) mouse.dWheel());
			}
			
			if (mouse.getPressed().contains(MouseEvent.BUTTON1) || true) {
				// Hnear = 2 * tan(fov / 2) * nearDist
				// in our case: 
				//   fov = 45 deg
				//   nearDist = 0.2
				
				double fieldOfView = Math.PI / 4;
				double nearDistance = 0.2;
				
				
				double nearPlaneHeight = 2 * Math.tan(fieldOfView / 2) * nearDistance;
				double nearPlaneWidth = nearPlaneHeight * screenWidth / screenHeight;
				
				double percentMouseOffsetX = (double) (mouse.x() - screenWidth) / screenWidth + 0.5;
				double percentMouseOffsetY = (double) (mouse.y() - screenHeight) / screenHeight + 0.5;
				
				// OpenGL has up as the positive y direction, whereas the mouse is at (0, 0) in the top left
				percentMouseOffsetY = -percentMouseOffsetY;
				
				double nearX = percentMouseOffsetX * nearPlaneWidth;
				double nearY = percentMouseOffsetY * nearPlaneHeight;
				
				// Obtain the near plane position vector
				Vector3 nearPosition;
				nearPosition = new Vector3(camera.getDirection());
				nearPosition.multiplyLocal(nearDistance);
				
				nearPosition.addLocal(camera.getPosition());
				nearPosition.addLocal(camera.getUp().multiply(nearY));
				nearPosition.addLocal(camera.getLeft().multiply(-nearX)); // Note that nearX is positive to the right
				
				// Obtain the projection direction vector
				Vector3 projectionDirection = nearPosition.subtract(camera.getPosition());
				projectionDirection.normalizeLocal();
				
				double angle = projectionDirection.angle(camera.getDirection());
				double projectionDistance = (camera.getDistance()) / Math.cos(angle);
				
				Vector3 projection = projectionDirection.multiply(projectionDistance);
				// projection.addLocal(camera.getPosition());
				// projection.addLocal(camera.getPosition().subtract(eye));
				projection.addLocal(camera.getPosition());
				
				if (mouse.getPressed().contains(MouseEvent.BUTTON1)) {
					/*
					testNode.x = (float) projection.x();
					testNode.y = (float) projection.y();
					testNode.z = (float) projection.z();
					*/
				}
				
				// testNode.x = (float) nearPosition.x();
				// testNode.y = (float) nearPosition.y();
				// testNode.z = (float) nearPosition.z();
				
				// testNode.x = (float) eye.x();
				// testNode.y = (float) eye.y();
				// testNode.z = (float) eye.z();
				
				/*
				System.out.println("percentMouseOffsetX: " + percentMouseOffsetX);
				System.out.println("percentMouseOffsetY: " + percentMouseOffsetY);
				System.out.println("nearX: " + nearX);
				System.out.println("nearY: " + nearY);
				*/
				
				// System.out.println("Mouse is at: (" + mouse.x() + ", " + mouse.y() + ")");
				
				int result = performPick(gl, mouse.x(), mouse.y());

				hovered = result;
				
				if (mouse.getPressed().contains(MouseEvent.BUTTON1)) {
					selected = result;
					
					System.out.println("Selected: " + selected);
				}
				
			}
			
			mouse.update();
		}
	}
	
	private int performPick(GL2 gl, double x, double y) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(256);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		// byteBuffer.
		IntBuffer buffer = byteBuffer.asIntBuffer();
		
		// int buffer[] = new int[256];
		IntBuffer viewport = IntBuffer.allocate(4);
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport);
		
		gl.glSelectBuffer(256, buffer);
	    gl.glRenderMode(GL2.GL_SELECT);
	    gl.glInitNames();
	    
	    gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glPushMatrix();
	    
	    GLU glu = new GLU();
	    gl.glLoadIdentity();
	
	    // System.out.println("viewport: " + viewport.get(0) + ", " + viewport.get(1) + 
	    //		", " + viewport.get(2) + ", " + viewport.get(3));
	    glu.gluPickMatrix(x, screenHeight - y, 2, 2, viewport);
	    glu.gluPerspective(45.0f, (float) screenWidth / screenHeight, 0.2f, 50.0f);
	    
	    // don't think this ortho call is needed
	    // gl.glOrtho(0.0, 8.0, 0.0, 8.0, -0.5, 2.5);
	    
	    //draw start
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	    gl.glLoadIdentity();
	    
	    //gl.glPushMatrix();
		Vector3 position = camera.getPosition();
		Vector3 target = camera.getTarget();
		Vector3 up = camera.getUp();

		glu.gluLookAt(position.x(), position.y(), position.z(), target.x(),
				target.y(), target.z(), up.x(), up.y(), up.z());

		gl.glPushName(-1);
		drawNodes(gl);
		//drawEdges(gl);
		
		//gl.glPopMatrix();
	    //draw end
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glPopMatrix();
	    
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    
	    // not sure if this is needed
	    // gl.glFlush();

	    int hits = gl.glRenderMode(GL2.GL_RENDER);
	    
	    // System.out.println("Number of hits: " + hits);
	    int selected;
	    
		if (hits > 0) {
			// The variable max helps keep track of the polygon that is closest
			// to the front of the screen
	    	int max = buffer.get(2);
	    	selected = buffer.get(3);
	    	
	    	for (int i = 0; i < hits; i++) {
	    		
	    		if (buffer.get(i * 4 + 2) < max) {
	    			max = buffer.get(i * 4 + 2);
	    	    	selected = buffer.get(i * 4 + 3);
	    		}
	    	}
	    } else {
	    	selected = -1;
	    }
	    
	    return selected;
    	
	}

	private void drawNodes(GL2 gl) {
		float x, y, z;
		int index;
		
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			x = nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION).floatValue() / DISTANCE_SCALE;
			y = nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION).floatValue() / DISTANCE_SCALE;
			z = nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION).floatValue() / DISTANCE_SCALE;
			
			index = nodeView.getModel().getIndex();
			gl.glLoadName(index);
			// gl.glLoadName(33);
			
			gl.glTranslatef(x, y, z);
			
			if (index == selected) {
				gl.glColor3f(0.52f, 0.70f, 0.52f);
				gl.glScalef(1.1f, 1.1f, 1.1f);
				gl.glCallList(nodeListIndex);
				gl.glScalef(1/1.1f, 1/1.1f, 1/1.1f);
				gl.glColor3f(0.73f, 0.73f, 0.73f);
			} else if (index == hovered) {
				gl.glColor3f(0.52f, 0.52f, 0.70f);
				gl.glCallList(nodeListIndex);
				gl.glColor3f(0.73f, 0.73f, 0.73f);
			} else {
				gl.glCallList(nodeListIndex);
			}
			
			gl.glTranslatef(-x, -y, -z);
		}
		
		// Draw the testNode
		gl.glTranslatef(testNode.x, testNode.y, testNode.z);
		// gl.glCallList(nodeListIndex);
		// GLUT glut = new GLUT();
		// glut.glutSolidCylinder(EDGE_RADIUS, 1,
		//		EDGE_SLICES_DETAIL, EDGE_STACKS_DETAIL);
		gl.glTranslatef(-testNode.x, -testNode.y, -testNode.z);
	}

	private void drawEdges(GL2 gl) {
		View<CyNode> sourceView;
		View<CyNode> targetView;
		
		int nodeCount = networkView.getModel().getNodeCount();
		
		int sourceIndex;
		int targetIndex;
		
		// A unique identifier (as far as this method is concerned) for each pair of nodes
		long pairIdentifier;
		
		TreeMap<Long, Integer> pairs = new TreeMap<Long, Integer>();
		
		// Points 0 and 2 represent endpoints of the quadratic Bezier curve, while
		// point 1 represents the approach point
		Vector3 p0 = new Vector3();
		Vector3 p1 = new Vector3();
		Vector3 p2 = new Vector3();
		
		Vector3 p1Offset;
		Vector3 direction;
		
		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {

			sourceView = networkView.getNodeView(edgeView.getModel().getSource());
			targetView = networkView.getNodeView(edgeView.getModel().getTarget());
			sourceIndex = sourceView.getModel().getIndex();
			targetIndex = targetView.getModel().getIndex();
			
			// These indices rely on CyNode's guarantee that NodeIndex < NumOfNodes
			assert sourceIndex < nodeCount;
			assert targetIndex < nodeCount;
			
			// Identify this pair of nodes so we'll know if we've drawn an edge between them before
			if (sourceIndex > targetIndex) {
				pairIdentifier = nodeCount * targetIndex + targetIndex;
			} else {
				pairIdentifier = nodeCount * sourceIndex + sourceIndex;
			}
			
			// Have we visited an edge between these nodes before?
			if (pairs.containsKey(pairIdentifier)) {
				pairs.put(pairIdentifier, pairs.get(pairIdentifier) + 1);
			} else {
				pairs.put(pairIdentifier, 1);
			}
		
			// Find p0, p1, p2 for the Bezier curve
			p0.set(sourceView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION),
					sourceView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION),
					sourceView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION));
			p0.divideLocal(DISTANCE_SCALE);
			
			p2.set(targetView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION),
					targetView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION),
					targetView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION));
			p2.divideLocal(DISTANCE_SCALE);
			
			p1 = p0.add(p2);
			p1.divideLocal(2);
			
			direction = p2.subtract(p0);
			p1Offset = direction.cross(0, 1, 0);
			p1Offset.normalizeLocal();
			p1Offset.multiplyLocal(EDGE_CURVE_FACTOR * Math.sqrt(direction.magnitude()));
			
			p1.addLocal(p1Offset);
			p1.rotate(direction, 2 * Math.PI * (pairs.get(pairIdentifier) - 1) / EDGES_PER_RADIUS);
	
			drawQuadraticEdge(gl, p0, p1, p2, 5);
		}
	}
	
	private void drawQuadraticEdge(GL2 gl, Vector3 p0, Vector3 p1, Vector3 p2, int numSegments) {
		// Equation for Quadratic Bezier curve:
		// B(t) = (1 - t)^2P0 + 2(1 - t)tP1 + t^2P2, t in [0, 1]
		
		double parameter;
		
		Vector3 current;
		Vector3[] points = new Vector3[numSegments + 1];
		double[] pointAngle = new double[numSegments + 1];
		
		Vector3 lastDirection = null;
		Vector3 currentDirection;
		
		points[0] = new Vector3(p0);
		for (int i = 1; i < numSegments; i++) {
			// Obtain points along the Bezier curve
			parameter = (double) i / numSegments;
			
			current = p0.multiply(Math.pow(1 - parameter, 2));
			current.addLocal(p1.multiply(2 * (1 - parameter) * parameter));
			current.addLocal(p2.multiply(parameter * parameter));
			
			points[i] = new Vector3(current);
			
			// Obtain the angle between the ith and (i - 1)th segment, for i in the open interval (1, numSegments)
			currentDirection = points[i].subtract(points[i - 1]);
			if (lastDirection != null) {
				// Note that this loop can only find the angle at the (i - 1)th point
				pointAngle[i - 1] = lastDirection.angle(currentDirection);
			}
			lastDirection = currentDirection;
		}
		
		// Obtain the angle between the last 2 segments, if there is more than 1 segment
		if (numSegments > 1) {
			pointAngle[numSegments - 1] = p2.subtract(points[numSegments - 1]).angle(lastDirection);
		}
		points[numSegments] = new Vector3(p2);
		
		double currentAngle;
		double extend1 = 0;
		double extend2;
		Vector3 direction;
		for (int i = 0; i < numSegments; i++) {
			currentAngle = pointAngle[i + 1];
			
			// currentAngle is likely calculated from an acos operation
			assert (!Double.isNaN(currentAngle));
			
			// Extend by c * tan(theta/2)
			extend2 = EDGE_RADIUS * Math.tan(currentAngle / 2);
	
			// TODO: Find alternative
			if (Double.isNaN(extend2)) {
				extend2 = 0;
			}
			
//			if (framesElapsed == 2) {
//				System.out.println("Segment: " + i);
//				System.out.println("Current segment angle: " + currentAngle);
//				System.out.println("Current extend1: " + extend1);
//				System.out.println("Current extend2: " + extend2);
//			}
			
			direction = points[i + 1].subtract(points[i]);
			direction.normalizeLocal();
			drawSingleEdge(gl, 
					points[i].subtract(direction.multiply(extend1)),
			 		points[i + 1].add(direction.multiply(extend2)));
			
//			drawSingleEdge(gl, 
//					points[i],
//					points[i + 1]);
			
			extend1 = extend2;
		}
		
		
	}
	
	private void drawQuadraticEdgeOld(GL2 gl, Vector3 p0, Vector3 p1, Vector3 p2, int numSegments) {
		// Equation for Quadratic Bezier curve:
		// B(t) = (1 - t)^2P0 + 2(1 - t)tP1 + t^2P2, t in [0, 1]
		
		double parameter;
		
		Vector3 current = p0;
		Vector3 next;
		
		for (int i = 1; i < numSegments; i++) {
			parameter = (double) i / numSegments;
			
			next = p0.multiply(Math.pow(1 - parameter, 2));
			next.addLocal(p1.multiply(2 * (1 - parameter) * parameter));
			next.addLocal(p2.multiply(parameter * parameter));
			
			drawSingleEdge(gl, current, next);
			
			current = next;
		}
	}
	
	private void drawEdgesOld(GL2 gl) {
		View<CyNode> sourceView;
		View<CyNode> targetView;
		
		float x1, x2, y1, y2, z1, z2;
		
		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {

			sourceView = networkView.getNodeView(edgeView.getModel().getSource());
			targetView = networkView.getNodeView(edgeView.getModel().getTarget());
		
			x1 = sourceView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION).floatValue() / DISTANCE_SCALE;
			y1 = sourceView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION).floatValue() / DISTANCE_SCALE;
			z1 = sourceView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION).floatValue() / DISTANCE_SCALE;
		
			x2 = targetView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION).floatValue() / DISTANCE_SCALE;
			y2 = targetView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION).floatValue() / DISTANCE_SCALE;
			z2 = targetView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION).floatValue() / DISTANCE_SCALE;
		
			// drawSingleEdge(gl, x1, y1, z1, x2, y2, z2);
		}
	}
	
	private void drawSingleEdge(GL2 gl, Vector3 start, Vector3 end) {
		gl.glPushMatrix();
		
		// TODO: Consider using a Vector3f object for just floats, to use translatef instead of translated
		gl.glTranslated(start.x(), start.y(), start.z());
		
		Vector3 current = new Vector3(0, 0, 1);
		Vector3 direction = end.subtract(start);
		
		Vector3 rotateAxis = current.cross(direction);

		gl.glRotated(Math.toDegrees(direction.angle(current)), rotateAxis.x(), rotateAxis.y(),
				rotateAxis.z());
		
		gl.glScalef(1.0f, 1.0f, (float) direction.magnitude());
		gl.glCallList(edgeListIndex);
	
		gl.glPopMatrix();
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

		//generateNodes();
		//generateEdges();
		startTime = System.nanoTime();
		createDisplayLists(gl);
		
		// Correct lightning for scaling certain models
		gl.glEnable(GL2.GL_NORMALIZE);
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
		gl.glEndList();
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
		
		screenHeight = height;
		screenWidth = width;
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
