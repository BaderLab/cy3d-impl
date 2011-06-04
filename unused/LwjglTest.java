package org.cytoscape.paperwing.internal;
import java.awt.Canvas;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.swing.JFrame;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.glu.GLU;

public class LwjglTest {
	
	private static final int NODE_COUNT = 30000;
	private static final float LARGE_SPHERE_RADIUS = 2.0f;
	private static final float SMALL_SPHERE_RADIUS = 0.02f;
	private float[] x;
	private float[] y;
	private float[] z;
	
	private float yRotate;
	
	private int nodeListIndex;
	
	private long startTime;
	private long endTime;
	private int framesElapsed = 0;
	
	public LwjglTest() {
		JFrame frame = new JFrame("Lwjgl Test");
        frame.setSize(650, 650);
        frame.setLocationRelativeTo(null);
       
        Canvas canvas = new Canvas();
        
        frame.add(canvas);
        frame.addWindowListener(new WindowAdapter() {
        	
        	@Override
        	public void windowClosing(WindowEvent e) {
        		System.exit(0);
        	}
        });
        
        frame.setVisible(true);
        
        try {
        	Display.create();
			Display.setParent(canvas);
		} catch (LWJGLException e) {
			// TODO Produce proper response to error
			e.printStackTrace();
		}
		
		init();
		computeCoordinates();
		drawScene();
		
		startTime = System.nanoTime();
		beginRenderLoop();
	}
	
	private void beginRenderLoop() {
		while (!Display.isCloseRequested()) {
			drawScene();
			
			framesElapsed++;
			
			if (Mouse.isButtonDown(1)) {
				endTime = System.nanoTime();
				
				double duration = (endTime - startTime) / Math.pow(10, 9);
				double frameRate = framesElapsed / duration;
				System.out.println("Average fps over " + duration + " seconds: " + frameRate);
			}
			
			Display.sync(60);
		}
	}
	
	private void createNodeDisplayList() {
		nodeListIndex = GL11.glGenLists(1);
		
		Sphere sphere = new Sphere();
		
		GL11.glNewList(nodeListIndex, GL11.GL_COMPILE);
		sphere.draw(SMALL_SPHERE_RADIUS, 6, 6);
		GL11.glEndList();
	}
	
	public void init() {
		initLighting();
		
		int width = Display.getParent().getWidth();
		int height = Display.getParent().getHeight();
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_FASTEST);
		
		GL11.glViewport(0, 0, width, height);
	
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		GLU.gluPerspective(45.0f, (float) width/height, 0.2f, 50.0f);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		createNodeDisplayList();
	}
	
	private void computeCoordinates() {
		x = new float[NODE_COUNT];
		y = new float[NODE_COUNT];
		z = new float[NODE_COUNT];
		
		float radius = LARGE_SPHERE_RADIUS;
		
		for (int i = 0; i < NODE_COUNT; i++) {
			
			do {
				x[i] = (float)(radius * 2 * Math.random() - radius);
				y[i] = (float)(radius * 2 * Math.random() - radius);
				z[i] = (float)(radius * 2 * Math.random() - radius);
			} while (Math.pow(x[i], 2) + Math.pow(y[i], 2) + Math.pow(z[i], 2) > Math.pow(radius, 2));
		}
	}
	
	private void drawNodes(float zTranslate) {
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0f, 0.0f, zTranslate);
		
		GL11.glColor3f(0.5f, 0.5f, 0.5f);
		
		GL11.glRotatef(yRotate, 0.5f, 0.5f, 0.0f);
		// yRotate--;
		
		for (int i = 0; i < NODE_COUNT; i++) {
			GL11.glTranslatef(x[i], y[i], z[i]);
			GL11.glCallList(nodeListIndex);
			GL11.glTranslatef(-x[i], -y[i], -z[i]);
		}
	}
	
	public void initLighting() {
		ByteBuffer buffer = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder());
		
		// buffer = FloatBuffer.allocate(4);
		float[] global = {0.5f, 0.5f, 0.5f, 1.0f};
		// buffer = buffer.put(global);
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, (FloatBuffer) buffer.asFloatBuffer().put(global));
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		float[] ambient = {0.2f, 0.2f, 0.2f, 1.0f};
		float[] diffuse = {0.8f, 0.8f, 0.8f, 1.0f};
		float[] specular = {0.5f, 0.5f, 0.5f, 1.0f};
		float[] position = {8.5f, 5.5f, -1.0f, 1.0f};
		
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, (FloatBuffer) buffer.asFloatBuffer().put(ambient).flip());
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, (FloatBuffer) buffer.asFloatBuffer().put(diffuse).flip());
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, (FloatBuffer) buffer.asFloatBuffer().put(specular).flip());
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, (FloatBuffer) buffer.asFloatBuffer().put(position).flip());

		GL11.glEnable(GL11.GL_LIGHT0);
	}
	
	public void drawScene() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glLoadIdentity();
		
		drawNodes(-6.0f);
		Display.update();
	}
	
	public static void main(String [] argv) {
		LwjglTest test = new LwjglTest();
	}
}
