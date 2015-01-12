// If you are visiting this class for the first time,
// consider taking a look at the following files:
//
// src/main/resources/controls.txt -- contains information about controls
// src/main/resources/overview-todo.txt -- contains information about what 
// is to be done

package org.baderlab.cy3d.internal.graphics;
import java.awt.Component;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import org.baderlab.cy3d.internal.coordinator.CoordinatorProcessor;
import org.baderlab.cy3d.internal.coordinator.ViewingCoordinator;
import org.baderlab.cy3d.internal.cytoscape.processing.CytoscapeDataProcessor;
import org.baderlab.cy3d.internal.cytoscape.view.Cy3DNetworkView;
import org.baderlab.cy3d.internal.data.FrameRateTracker;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.PixelConverter;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.picking.ShapePickingProcessor;
import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.baderlab.cy3d.internal.tools.GeometryToolkit;
import org.baderlab.cy3d.internal.tools.SimpleCamera;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.work.swing.DialogTaskManager;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * This class represents a Cy3D rendering object, directly called
 * by the display thread to update the rendered scene for every frame.
 * 
 * Its behavior is governed by its {@link GraphicsConfiguration} object, 
 * which will determine if and how it handles the following:
 * - keyboard and mouse input
 * - calculation related to rendering
 * - rendering of the network
 * - communication with other {@link RenderEventListener} objects, such as in the case
 * of a bird's eye rendering object and a main window rendering object communication
 * with each other.
 * 
 * @author Paperwing (Yue Dong)
 */
public class RenderEventListener implements GLEventListener {
	
	// Contains the "global" state used by one renderer.
	private GraphicsData graphicsData;
	private ShapePickingProcessor shapePickingProcessor;
	private ViewingCoordinator coordinator;
	private CoordinatorProcessor coordinatorProcessor;
	private CytoscapeDataProcessor cytoscapeDataProcessor;
	
	private GraphicsConfiguration handler;
	private RenderUpdateFlag renderUpdateFlag;
	
	
	/** Create a new Graphics object
	 * 
	 * @param networkView The CyNetworkView object, representing the 
	 * View<CyNetwork> object that we are rendering
	 * @param visualLexicon The visual lexicon being used
	 */
	public RenderEventListener(CyNetworkView networkView, VisualLexicon visualLexicon, GraphicsConfiguration handler) {
		this.handler = handler;
		
		// TODO: add default constant speeds for camera movement
		graphicsData = new GraphicsData();
		graphicsData.setCamera(new SimpleCamera(new Vector3(0, 0, 3), new Vector3(0, 0, 0), new Vector3(0, 1, 0), 
				                                0.04, 0.0033, 0.01, 0.01, 0.4));
		
		PixelConverter pixelConverter = new PixelConverter(null);
		graphicsData.setPixelConverter(pixelConverter);
		
		graphicsData.setNetworkView(networkView);
		graphicsData.setVisualLexicon(visualLexicon);
		
		coordinator = handler.getCoordinator(graphicsData);
		coordinatorProcessor = handler.getCoordinatorProcessor();
		coordinatorProcessor.initializeCoordinator(coordinator, graphicsData);
		
		shapePickingProcessor = handler.getShapePickingProcessor();
		
		cytoscapeDataProcessor = handler.getCytoscapeDataProcessor();
		//lightingProcessor = handler.getLightingProcessor();
		
		if (handler instanceof MainGraphicsConfiguration) {
			((Cy3DNetworkView) graphicsData.getNetworkView()).setNetworkCamera(graphicsData.getCamera());
		}
	}
	
	/** Attach the KeyboardMonitor and MouseMonitors, which are listeners,
	 * to the specified component for capturing keyboard and mouse events
	 * 
	 * @param component The component to listen to events for
	 * @param settingsData 
	 */
	public void trackInput(Component component) {
		renderUpdateFlag = handler.trackInput(graphicsData, component);
		if(renderUpdateFlag == null)
			renderUpdateFlag = RenderUpdateFlag.ALWAYS_RENDER;
		
		graphicsData.setContainer(component);
		
		if (handler instanceof MainGraphicsConfiguration) {
			((Cy3DNetworkView) graphicsData.getNetworkView()).setContainer(component);
		} 
	}
	
	
	/**
	 * Set the {@link TaskFactoryListener} object used to obtain the list of current task factories.
	 * @param listener
	 */
	public void setupTaskFactories(TaskFactoryListener taskFactoryListener, DialogTaskManager taskManager) {
		graphicsData.setTaskFactoryListener(taskFactoryListener);
		graphicsData.setTaskManager(taskManager);
//		graphicsData.setSubmenuTaskManager(submenuTaskManager);
	}

	/** Main drawing method; can be called by an {@link Animator} such as
	 * {@link FPSAnimator}, responsible for drawing the scene and advancing
	 * the frame
	 * 
	 * @param drawable The GLAutoDrawable object used for rendering
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		if(!renderUpdateFlag.needToRender())
			return;
		
		System.out.println("display: " + handler);
		
		GL2 gl = drawable.getGL().getGL2();
		graphicsData.setGlContext(gl);
		graphicsData.getPixelConverter().setNativeSurface(drawable.getNativeSurface());
		
		// Re-calculate the viewing volume
		SimpleCamera camera = graphicsData.getCamera();
		graphicsData.getViewingVolume().calculateViewingVolume(
				camera.getPosition(), 
				camera.getDirection(), 
				camera.getUp(), 
				graphicsData.getNearZ(), 
				graphicsData.getFarZ(), 
				graphicsData.getVerticalFov(), 
				GeometryToolkit.findHorizontalFieldOfView(graphicsData.getDistanceScale(), 
						graphicsData.getScreenWidth(), graphicsData.getScreenHeight()));
		
		// Perform picking
		shapePickingProcessor.processPicking(graphicsData);
		
		// Update data for bird's eye view camera movement
		coordinatorProcessor.extractData(coordinator, graphicsData);
		
		// Process Cytoscape data
		cytoscapeDataProcessor.processCytoscapeData(graphicsData);
		
		// Update lighting
		//lightingProcessor.updateLighting(gl, graphicsData.getLightingData());
		
		// Draw the scene
		handler.drawScene(graphicsData);
		
		int errorCode = gl.glGetError();
		if(errorCode != GL2.GL_NO_ERROR) {
			System.out.println("Error Code: " + errorCode);
		}
		
		renderUpdateFlag.reset();
	}

	@Override
	public void dispose(GLAutoDrawable autoDrawable) {
		coordinatorProcessor.unlinkCoordinator(coordinator);
		handler.dispose(graphicsData);
	}

	/** Initialize the Graphics object, performing certain
	 * OpenGL initializations
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		System.out.println("GL_VENDOR: "   + gl.glGetString(GL2.GL_VENDOR));
		System.out.println("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER));
		System.out.println("GL_VERSION: "  + gl.glGetString(GL2.GL_VERSION));
		
		graphicsData.getPixelConverter().setNativeSurface(drawable.getNativeSurface());
		
		initLighting(drawable);

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glCullFace(GL2.GL_BACK);
//		gl.glFrontFace(GL2.GL_CW);

		gl.glDepthFunc(GL.GL_LEQUAL);
		// gl.glDepthFunc(GL2.GL_LESS);
		// gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
		// gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);

		gl.glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());

		// Correct lightning for scaling certain models
		gl.glEnable(GL2.GL_NORMALIZE);
		
		// Enable blending
		// ---------------
		
		// gl.glEnable(GL2.GL_BLEND);
		// gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		graphicsData.setGlContext(gl);
		graphicsData.setFrameRateTracker(new FrameRateTracker(drawable.getAnimator()));
		
		handler.initializeGraphicsProcedures(graphicsData);
		//handler.setupLighting(graphicsData);
		
		shapePickingProcessor.initialize(graphicsData);
		
	}

	
	private void initLighting(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		float[] global = { 0.4f, 0.4f, 0.4f, 1.0f };

		gl.glEnable(GL2.GL_LIGHTING);
		gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(global));
		gl.glShadeModel(GL2.GL_SMOOTH);
		/*
		
		float[] ambient = { 0.4f, 0.4f, 0.4f, 1.0f };
		float[] diffuse = { 0.57f, 0.57f, 0.57f, 1.0f };
		float[] specular = { 0.79f, 0.79f, 0.79f, 1.0f };
		float[] position = { -4.0f, 4.0f, 6.0f, 1.0f };
		
//		float[] ambient = { 0.8f, 0.8f, 0.8f, 0.8f };
//		float[] diffuse = { 0.7f, 0.7f, 0.7f, 0.7f };
//		float[] specular = { 0.8f, 0.8f, 0.8f, 0.8f };
//		float[] position = { -4.0f, 4.0f, 6.0f, 1.0f };

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, FloatBuffer.wrap(ambient));
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, FloatBuffer.wrap(diffuse));
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, FloatBuffer.wrap(specular));
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, FloatBuffer.wrap(position));

		gl.glEnable(GL2.GL_LIGHT0);

		*/

		float[] diffuse = { 0.7f, 0.7f, 0.7f, 0.7f };
		float[] position = { -4.0f, 4.0f, 6.0f, 0.0f };
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, FloatBuffer.wrap(diffuse));
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, FloatBuffer.wrap(position));
		gl.glEnable(GL2.GL_LIGHT0);
		
		
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
		
		// Older default values
		// float[] specularReflection = { 0.5f, 0.5f, 0.5f, 1.0f };
		// gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR,
		// FloatBuffer.wrap(specularReflection));
		// gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 40);
		
//		float[] specularReflection = { 0.46f, 0.46f, 0.46f, 1.0f };
//		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, FloatBuffer.wrap(specularReflection));
//		gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 16); // Default shininess 31
//		
//		gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 0);
//		
//		lightingProcessor.setupLighting(gl, graphicsData.getLightingData());
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		if (height <= 0) {
			height = 1;
		}

		GL2 gl = drawable.getGL().getGL2();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU glu = new GLU();
		glu.gluPerspective(graphicsData.getVerticalFov(), (float) width / height, graphicsData.getNearZ(), graphicsData.getFarZ());

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		graphicsData.setScreenHeight(height);
		graphicsData.setScreenWidth(width);
	}
	
	@Override
	public String toString() {
		return "Graphics(" + handler + ")";
	}
}
