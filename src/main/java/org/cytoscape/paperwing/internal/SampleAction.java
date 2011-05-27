package org.cytoscape.paperwing.internal;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.session.CyApplicationManager;

import com.jogamp.opengl.util.FPSAnimator;

public class SampleAction extends AbstractCyAction {
	public SampleAction(Map<String, String> properties, CyApplicationManager applicationManager) {
		super(properties, applicationManager);
	}

	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(null, "Test");
		
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
}
