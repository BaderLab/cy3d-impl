package org.cytoscape.paperwing.internal;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkViewManager;

import com.jogamp.opengl.util.FPSAnimator;

public class SampleAction extends AbstractCyAction {
	private CyApplicationManager applicationManager;
	private CyNetworkManager networkManager;
	private CyNetworkViewManager networkViewManager;
	
	private KeyboardMonitor keyboard;
	private MouseMonitor mouse;
	
	public SampleAction(Map<String, String> properties, CyApplicationManager applicationManager,
			CyNetworkManager networkManager, CyNetworkViewManager networkViewManager) {
		super(properties, applicationManager);
		
		this.applicationManager = applicationManager;
		this.networkManager = networkManager;
		this.networkViewManager = networkViewManager;
		
		//System.out.println("number of networks: " + networkManager.getNetworkSet().size());
		//System.out.println("current network: " + applicationManager.getCurrentNetwork());
		//System.out.println("current network view: " + applicationManager.getCurrentNetworkView());
	}

	public void actionPerformed(ActionEvent e) {
		final JFrame frame = new JFrame("JOGL Test Window");
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);

		// Use the system's default version of OpenGL
		GLProfile profile = GLProfile.getDefault();
		GLProfile.initSingleton(true);
		
		GLCapabilities capabilities = new GLCapabilities(profile);
		GLCanvas canvas = new GLCanvas(capabilities);

		TestGraphics graphics = new TestGraphics();
		graphics.setManagers(applicationManager, networkManager, networkViewManager);

		canvas.addGLEventListener(graphics);
		graphics.getKeyListener();
		frame.add(canvas);

		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
		});

		frame.setVisible(true);
		
		frame.addKeyListener(graphics.getKeyListener());
		canvas.addKeyListener(graphics.getKeyListener());
		canvas.addMouseListener(graphics.getMouseListener());
		canvas.addMouseMotionListener(graphics.getMouseMotionListener());
		canvas.addMouseWheelListener(graphics.getMouseWheelListener());
		
		FPSAnimator animator = new FPSAnimator(60);
		animator.add(canvas);
		animator.start();
	}
}