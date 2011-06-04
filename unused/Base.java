package org.cytoscape.paperwing.internal;
import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.framework.*;
import com.ardor3d.framework.lwjgl.*;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.input.*;
import com.ardor3d.input.control.FirstPersonControl;
//import com.ardor3d.input.control.OrbitCamControl;
import com.ardor3d.input.logical.*;
import com.ardor3d.input.lwjgl.*;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.light.PointLight;
import com.ardor3d.math.*;
import com.ardor3d.math.type.ReadOnlyQuaternion;
import com.ardor3d.renderer.*;
import com.ardor3d.renderer.lwjgl.LwjglTextureRendererProvider;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.scenegraph.shape.Cylinder;
import com.ardor3d.scenegraph.shape.Sphere;
import com.ardor3d.util.*;
import com.ardor3d.util.stat.StatCollector;
import com.google.common.base.*;
 
public class Base implements Runnable, Updater, Scene {
 
	final Timer timer = new Timer();
	final FrameHandler frameHandler = new FrameHandler(timer);
 
	final DisplaySettings settings;
	final LwjglCanvas canvas;
	final PhysicalLayer physicalLayer;
	final LwjglMouseManager mouseManager;
 
	Vector3 worldUp = new Vector3(0, 1, 0);
 
	final Node root = new Node();
 
	final LogicalLayer logicalLayer = new LogicalLayer();
	boolean exit;
	
	//OrbitCamControl orbitCam;
 
	public Base(DisplaySettings settings) {
		this.settings = settings;
 
		LwjglCanvasRenderer canvasRenderer = new LwjglCanvasRenderer(this);
		canvas = new LwjglCanvas(canvasRenderer, settings);
		physicalLayer = new PhysicalLayer(new LwjglKeyboardWrapper(), new LwjglMouseWrapper(), new LwjglControllerWrapper(), (LwjglCanvas) canvas);
		mouseManager = new LwjglMouseManager();
		TextureRendererFactory.INSTANCE.setProvider(new LwjglTextureRendererProvider());
 
		logicalLayer.registerInput(canvas, physicalLayer);
 
		frameHandler.addUpdater(this);
		frameHandler.addCanvas(canvas);
 
		canvas.setTitle("Test");
	}
 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
 
		DisplaySettings settings = new DisplaySettings(1024, 768, 24, 0, 0, 8, 0, 0, false, false);
 
		Base main = new Base(settings);
		//new Thread(main).start();
		main.run();
	}
 
	public void run() {
		try {
			frameHandler.init();
 
			while (!exit) {
				frameHandler.updateFrame();
				Thread.yield();
			}
			// grab the graphics context so cleanup will work out.
			canvas.getCanvasRenderer().makeCurrentContext();
			ContextGarbageCollector.doFinalCleanup(canvas.getCanvasRenderer().getRenderer());
			canvas.close();
		} catch (final Throwable t) {
			System.err.println("Throwable caught in MainThread - exiting");
			t.printStackTrace(System.err);
		}
	}
 
	public void init() {
		registerInputTriggers();

		AWTImageLoader.registerLoader();

		final ZBufferState buf = new ZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		root.setRenderState(buf);

		final PointLight light = new PointLight();
        light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
        light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        light.setLocation(new Vector3(100, 100, 100));
        light.setEnabled(true);
        
        /** Attach the light to a lightState and the lightState to rootNode. */
        LightState lightState = new LightState();
        lightState.setEnabled(true);
        lightState.attach(light);
        root.setRenderState(lightState);
		
        
        Sphere first = new Sphere("first", 12, 12, 0.3);
        first.setTranslation(new Vector3(-2, 0, -15));
        root.attachChild(first);
        
        Sphere second = new Sphere("second", 12, 12, 0.3);
        second.setTranslation(new Vector3(2, 0, -15));
        root.attachChild(second);
        
        Sphere third = new Sphere("third", 12, 12, 0.3);
        third.setTranslation(new Vector3(0, -1, -15));
        root.attachChild(third);
        
        Quaternion rotation = new Quaternion();
        rotation = rotation.fromAngleNormalAxis(MathUtils.HALF_PI, new Vector3(0, 1, 0));
        Cylinder edge = new Cylinder("Cylinder", 8, 8, 0.1, 4);
        edge.setTranslation(new Vector3(0, 0, -15));
        edge.setRotation(rotation);
        root.attachChild(edge);
	}
 
	public void update(ReadOnlyTimer timer) {
		if (canvas.isClosing()) {
			exit = true;
		}
 
		/** update stats, if enabled. */
		if (Constants.stats) {
			StatCollector.update();
		}
 
		logicalLayer.checkTriggers(timer.getTimePerFrame());

		// canvas.getCanvasRenderer().getCamera().lookAt(new Vector3(0, 0, -15), (new Vector3(1, 0, 0)).normalizeLocal());
		// orbitCam.update(timer.getTimePerFrame());
		
		// Execute updateQueue item
		GameTaskQueueManager.getManager(canvas.getCanvasRenderer().getRenderContext()).getQueue(GameTaskQueue.UPDATE).execute();
 
		/** Call simpleUpdate in any derived classes of ExampleBase. */
		//updateExample(timer);
		
		/** Update controllers/render states/transforms/bounds for rootNode. */
		root.updateGeometricState(timer.getTimePerFrame(), true);
	}
 
	public PickResults doPick(Ray3 pickRay) {
		// TODO Auto-generated method stub
		return null;
	}
 
	public boolean renderUnto(Renderer renderer) {
		GameTaskQueueManager.getManager(canvas.getCanvasRenderer().getRenderContext()).getQueue(GameTaskQueue.RENDER).execute(renderer);
 
		// Clean up card garbage such as textures, vbos, etc.
		ContextGarbageCollector.doRuntimeCleanup(renderer);
 
		/** Draw the rootNode and all its children. */
		if (!canvas.isClosing()) {
			/** Call renderExample in any derived classes. */
			renderer.draw(root);
 
			return true;
		} else {
			return false;
		}
	}
 
	protected void registerInputTriggers() {
		//controlHandle = 
		// FirstPersonControl.setupTriggers(logicalLayer, worldUp, true);
		
		//orbitCam = new OrbitCamControl(canvas.getCanvasRenderer().getCamera(), new Vector3(0, 0, -15), worldUp);
		//orbitCam.setupInputTriggers(logicalLayer);
		
		// orbitCam.setTarget(new Vector3(0, 0, -10));
		/*
		orbitCam.setBaseDistance(5);
		orbitCam.setupMouseTriggers(logicalLayer, true);
		orbitCam.setSphereCoords(15, 0, 0);
		*/
 
		logicalLayer.registerTrigger(new InputTrigger(new MouseButtonClickedCondition(MouseButton.RIGHT), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
 
				final Vector2 pos = Vector2.fetchTempInstance().set(inputStates.getCurrent().getMouseState().getX(), inputStates.getCurrent().getMouseState().getY());
				final Ray3 pickRay = new Ray3();
				canvas.getCanvasRenderer().getCamera().getPickRay(pos, false, pickRay);
				Vector2.releaseTempInstance(pos);
				doPick(pickRay);
			}
		}));
 
		logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.ESCAPE), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
				exit = true;
			}
		}));
 
		final Predicate<TwoInputStates> clickLeftOrRight = Predicates.or(new MouseButtonClickedCondition(MouseButton.LEFT), new MouseButtonClickedCondition(MouseButton.RIGHT));
 
		logicalLayer.registerTrigger(new InputTrigger(clickLeftOrRight, new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
				System.err.println("clicked: " + inputStates.getCurrent().getMouseState().getClickCounts());
			}
		}));
 
		logicalLayer.registerTrigger(new InputTrigger(new MouseButtonPressedCondition(MouseButton.LEFT), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
				if (mouseManager.isSetGrabbedSupported()) {
					// mouseManager.setGrabbed(GrabbedState.GRABBED);
				}
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new MouseButtonReleasedCondition(MouseButton.LEFT), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
				if (mouseManager.isSetGrabbedSupported()) {
					// mouseManager.setGrabbed(GrabbedState.NOT_GRABBED);
				}
			}
		}));
 
		logicalLayer.registerTrigger(new InputTrigger(new AnyKeyCondition(), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
				// System.out.println("Key character pressed: " + inputState.getCurrent().getKeyboardState().getKeyEvent().getKeyChar());
			}
		}));
		
	}
 
}