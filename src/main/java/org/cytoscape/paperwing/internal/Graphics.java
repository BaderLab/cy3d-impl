// If you are visiting this class for the first time,
// consider taking a look at the following files:
//
// src/main/resources/controls.txt -- contains information about controls
// src/main/resources/overview-todo.txt -- contains information about what 
// is to be done

package org.cytoscape.paperwing.internal;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;
import javax.swing.JInternalFrame;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;

import org.cytoscape.paperwing.internal.coordinator.CoordinatorProcessor;
import org.cytoscape.paperwing.internal.coordinator.ViewingCoordinator;
import org.cytoscape.paperwing.internal.cytoscape.processing.CytoscapeDataProcessor;
import org.cytoscape.paperwing.internal.cytoscape.view.WindNetworkView;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.input.InputProcessor;
import org.cytoscape.paperwing.internal.input.KeyboardMonitor;
import org.cytoscape.paperwing.internal.input.MouseMonitor;
import org.cytoscape.paperwing.internal.lighting.LightingProcessor;
import org.cytoscape.paperwing.internal.picking.ShapePickingProcessor;
import org.cytoscape.paperwing.internal.task.TaskFactoryListener;
import org.cytoscape.paperwing.internal.tools.GeometryToolkit;
import org.cytoscape.paperwing.internal.tools.SimpleCamera;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.swing.SubmenuTaskManager;

/**
 * This class represents a Wind rendering object, directly called
 * by the display thread to update the rendered scene for every frame.
 * 
 * Its behavior is governed by its {@link GraphicsHandler} object, 
 * which will determine if and how it handles the following:
 * - keyboard and mouse input
 * - calculation related to rendering
 * - rendering of the network
 * - communication with other {@link Graphics} objects, such as in the case
 * of a bird's eye rendering object and a main window rendering object communication
 * with each other.
 * 
 * @author Paperwing (Yue Dong)
 */
public class Graphics implements GLEventListener {
	
	/** A monitor to keep track of keyboard events */
	private KeyboardMonitor keys;
	
	/** A monitor to keep track of mouse events */
	private MouseMonitor mouse;
	
	/** A boolean to use lower quality 3D shapes to improve framerate */
	private boolean lowerQuality = false;
	
	private GraphicsData graphicsData;
	
	private InputProcessor inputProcessor;
	// private ShapePicker shapePicker;
	private ShapePickingProcessor shapePickingProcessor;
	private ViewingCoordinator coordinator;
	private CoordinatorProcessor coordinatorProcessor;
	private CytoscapeDataProcessor cytoscapeDataProcessor;
	
	/**
	 * The {@link LightingProcessor} object responsible for setting up and maintaining lighting
	 */
	private LightingProcessor lightingProcessor;
	
	private GraphicsHandler handler;
	
	/** Create a new Graphics object
	 * 
	 * @param networkView The CyNetworkView object, representing the 
	 * View<CyNetwork> object that we are rendering
	 * @param visualLexicon The visual lexicon being used
	 */
	public Graphics(CyNetworkView networkView, VisualLexicon visualLexicon, GraphicsHandler handler) {
		
		keys = new KeyboardMonitor();
		mouse = new MouseMonitor();

//		renderProcedures.put("nodes", new RenderNodesProcedure());
//		renderProcedures.put("edges", new RenderEdgesProcedure());
//		renderProcedures.put("selectionBox", new RenderSelectionBoxProcedure());
		
		this.handler = handler;
		
		// TODO: add default constant speeds for camera movement
		graphicsData = new GraphicsData();
		graphicsData.setCamera(new SimpleCamera(new Vector3(0, 0, 3), new Vector3(0, 0, 0),
				new Vector3(0, 1, 0), 0.04, 0.0033, 0.01, 0.01, 0.4));
		
		graphicsData.setNetworkView(networkView);
		graphicsData.setVisualLexicon(visualLexicon);
		
		coordinator = handler.getCoordinator(graphicsData);
		coordinatorProcessor = handler.getCoordinatorProcessor();
		coordinatorProcessor.initializeCoordinator(coordinator, graphicsData);
		
		shapePickingProcessor = handler.getShapePickingProcessor();
		inputProcessor = handler.getInputProcessor();
		
		cytoscapeDataProcessor = handler.getCytoscapeDataProcessor();
		lightingProcessor = handler.getLightingProcessor();
		
		if (handler instanceof MainGraphicsHandler) {
			((WindNetworkView) graphicsData.getNetworkView()).setNetworkCamera(graphicsData.getCamera());
		}
	}
	
	/** Attach the KeyboardMonitor and MouseMonitors, which are listeners,
	 * to the specified component for capturing keyboard and mouse events
	 * 
	 * @param component The component to listen to events for
	 */
	public void trackInput(Component component) {
		component.addMouseListener(mouse);
		component.addMouseMotionListener(mouse);
		component.addMouseWheelListener(mouse);
		component.addFocusListener(mouse);
		
		component.addKeyListener(keys);
		component.addFocusListener(keys);
		
		graphicsData.setContainer(component);
		
		if (handler instanceof MainGraphicsHandler) {
			((WindNetworkView) graphicsData.getNetworkView()).setContainer(component);
		} else if (handler instanceof BirdsEyeGraphicsHandler) {
			
			// Add mouse listeners to render the updated scene when the Bird's eye view
			// is clicked or encounters mouse drag movement
			component.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (coordinator != null 
							&& coordinator.getMainAnimatorController() != null) {
						coordinator.getMainAnimatorController().startAnimator();
					}
				}
			});
			
			component.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					if (coordinator != null 
							&& coordinator.getMainAnimatorController() != null) {
						coordinator.getMainAnimatorController().startAnimator();
					}
				}
			});
		}
	}
	
	public void setAnimatorControl(GLAnimatorControl animatorControl) {
		graphicsData.setAnimatorControl(animatorControl);
	}
	
	/**
	 * Set the {@link TaskFactoryListener} object used to obtain the list of current task factories.
	 * @param listener
	 */
	public void setupTaskFactories(TaskFactoryListener taskFactoryListener, 
			DialogTaskManager taskManager, SubmenuTaskManager submenuTaskManager) {
		graphicsData.setTaskFactoryListener(taskFactoryListener);
		graphicsData.setTaskManager(taskManager);
		graphicsData.setSubmenuTaskManager(submenuTaskManager);
	}

	/** Main drawing method; can be called by an {@link Animator} such as
	 * {@link FPSAnimator}, responsible for drawing the scene and advancing
	 * the frame
	 * 
	 * @param drawable The GLAutoDrawable object used for rendering
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		graphicsData.setGlContext(gl);
		
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
		shapePickingProcessor.processPicking(mouse, keys, graphicsData);
		
		// Check input
		inputProcessor.processInput(keys, mouse, graphicsData);
		
		// Update data for bird's eye view camera movement
		coordinatorProcessor.extractData(coordinator, graphicsData);
		
		// Process Cytoscape data
		cytoscapeDataProcessor.processCytoscapeData(graphicsData);
		
		// Update lighting
		lightingProcessor.updateLighting(gl, graphicsData.getLightingData());
		
		// Draw the scene
		handler.drawScene(graphicsData);
		
		graphicsData.setFramesElapsed(graphicsData.getFramesElapsed() + 1);
		graphicsData.getFrameRateTracker().advanceFrame();
		
		// Pause rendering unless a keyboard or mouse button is held down to conserve CPU/GPU/power resources
		if (handler instanceof MainGraphicsHandler) {
			if (!graphicsData.getAnimatorController().hasKeysDown()) {
				graphicsData.getAnimatorControl().stop();
			}
		} else if (handler instanceof BirdsEyeGraphicsHandler) {
			if (coordinator.getMainAnimatorController() != null 
					&& !coordinator.getMainAnimatorController().hasKeysDown()) {
				graphicsData.getAnimatorControl().stop();
			}
		}
		
	}

	@Override
	public void dispose(GLAutoDrawable autoDrawable) {
		coordinatorProcessor.unlinkCoordinator(coordinator);
	}

	/** Initialize the Graphics object, performing certain
	 * OpenGL initializations
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		initLighting(drawable);

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_DEPTH_TEST);

		gl.glDepthFunc(GL.GL_LEQUAL);
		// gl.glDepthFunc(GL2.GL_LESS);
		// gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
		// gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);

		gl.glViewport(0, 0, drawable.getWidth(), drawable.getHeight());

		// Correct lightning for scaling certain models
		gl.glEnable(GL2.GL_NORMALIZE);
		
		// Enable blending
		// ---------------
		
		// gl.glEnable(GL2.GL_BLEND);
		// gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		graphicsData.setStartTime(System.nanoTime());
		graphicsData.setGlContext(gl);
		
		handler.initializeGraphicsProcedures(graphicsData);
		handler.setupLighting(graphicsData);
		
		shapePickingProcessor.initialize(graphicsData);
		
		if (handler instanceof MainGraphicsHandler) {

			// Add an AnimatorController as a listener that keeps the animator running only when at least 1 button is pressed
			AnimatorController controller = new AnimatorController(graphicsData.getAnimatorControl());
			controller.setCoordinator(coordinator);
			
			Component component = graphicsData.getContainer();
			component.addKeyListener(controller);
			component.addMouseListener(controller);
			component.addMouseMotionListener(controller);
			component.addMouseWheelListener(controller);
			
			graphicsData.setAnimatorController(controller);
			((WindNetworkView) graphicsData.getNetworkView()).setAnimatorController(controller);
		}
	}


	private void initLighting(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		float[] global = { 0.2f, 0.2f, 0.2f, 1.0f };

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

		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
		
		// Older default values
		// float[] specularReflection = { 0.5f, 0.5f, 0.5f, 1.0f };
		// gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR,
		// FloatBuffer.wrap(specularReflection));
		// gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 40);
		
		float[] specularReflection = { 0.46f, 0.46f, 0.46f, 1.0f };
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR,
				FloatBuffer.wrap(specularReflection));
		gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 16); // Default shininess 31
		
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 0);
		
		lightingProcessor.setupLighting(gl, graphicsData.getLightingData());
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
		glu.gluPerspective(graphicsData.getVerticalFov(), (float) width / height, graphicsData.getNearZ(), graphicsData.getFarZ());

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		graphicsData.setScreenHeight(height);
		graphicsData.setScreenWidth(width);
	}
}
