package org.baderlab.cy3d.internal.input.handler;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

/**
 * Tool panel that overlays the renderer window.
 * 
 * Responds to ALT and SHIFT as well.
 * 
 * @author mkucera
 *
 */
public class ToolPanel {
	
	private boolean forceSelect = false;
	private boolean forceCamera = false;
	private MouseMode defaultToolbarMode = MouseMode.getDefault();
	
	private JToggleButton selectButton;
	private JToggleButton cameraButton;
	
	private Set<MouseModeChangeListener> listeners = new LinkedHashSet<>();
	
	
	public interface MouseModeChangeListener {
		void mouseModeChanged(MouseMode mouseMode);
	}
	
	
	public ToolPanel(JInternalFrame frame) {
		setUpGlassPane(frame);
		setUpKeyboardInput(frame);
	}
	
	
	public boolean addMouseModeChangeListener(MouseModeChangeListener listner) {
		return listeners.add(listner);
	}
	
	public boolean removeMouseModeChangeListener(MouseModeChangeListener listener) {
		return listeners.remove(listener);
	}
	
	private void fireMouseModeChangeEvent() {
		MouseMode mouseMode = getCurrentMouseMode();
		for(MouseModeChangeListener listener : listeners) {
			listener.mouseModeChanged(mouseMode);
		}
	}
	
	
	private void setUpGlassPane(JInternalFrame frame) {
		JPanel glass = (JPanel) frame.getGlassPane();
		glass.setLayout(new BorderLayout());
		glass.setVisible(true);
		
		ButtonGroup cameraGroup = new ButtonGroup();
		JPanel cameraToolBar = new JPanel();
		cameraToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		cameraToolBar.setOpaque(false);
		
		selectButton = createButton("Select", MouseMode.SELECT, cameraToolBar, cameraGroup);
		cameraButton = createButton("Camera", MouseMode.CAMERA, cameraToolBar, cameraGroup);
		
		glass.add(cameraToolBar, BorderLayout.WEST);
	}
	
	private JToggleButton createButton(String text, final MouseMode mode, Container parent, ButtonGroup group) {
		JToggleButton button = new JToggleButton(text);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setPreferredSize(new Dimension(40, 20));
		button.setFont(new Font("Arial", Font.PLAIN, 8));
		button.setFocusable(false);
		group.add(button);
		parent.add(button);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				defaultToolbarMode = mode;
				fireMouseModeChangeEvent();
			}
		});
		
		if(mode == MouseMode.getDefault()) { // initial selection
			button.setSelected(true);
		}
		
		return button;
	}

	
	@SuppressWarnings("serial")
	private void setUpKeyboardInput(JInternalFrame frame) {
		String FORCE_CAMERA = "force_camera", UNFORCE_CAMERA = "unforce_camera";
		String FORCE_SELECT = "force_select", UNFORCE_SELECT = "unforce_select";
		
		InputMap inputMap = frame.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		
		inputMap.put(KeyStroke.getKeyStroke("alt pressed ALT"), FORCE_CAMERA);
		inputMap.put(KeyStroke.getKeyStroke("released ALT"), UNFORCE_CAMERA);
		inputMap.put(KeyStroke.getKeyStroke("shift pressed SHIFT"), FORCE_SELECT);
		inputMap.put(KeyStroke.getKeyStroke("released SHIFT"), UNFORCE_SELECT);
		inputMap.put(KeyStroke.getKeyStroke("shift alt pressed ALT"), FORCE_CAMERA);
		inputMap.put(KeyStroke.getKeyStroke("shift released ALT"), UNFORCE_CAMERA);
		inputMap.put(KeyStroke.getKeyStroke("shift alt pressed SHIFT"), FORCE_SELECT);
		inputMap.put(KeyStroke.getKeyStroke("alt released SHIFT"), UNFORCE_SELECT);
		
		frame.getActionMap().put(FORCE_CAMERA, new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				forceCamera = true;
				showCurrentMode();
				fireMouseModeChangeEvent();
			}
		});
		
		frame.getActionMap().put(UNFORCE_CAMERA, new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				forceCamera = false;
				showCurrentMode();
				fireMouseModeChangeEvent();
			}
		});
		
		frame.getActionMap().put(FORCE_SELECT, new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				forceSelect = true;
				showCurrentMode();
				fireMouseModeChangeEvent();
			}
		});
		
		frame.getActionMap().put(UNFORCE_SELECT, new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				forceSelect = false;
				showCurrentMode();
				fireMouseModeChangeEvent();
			}
		});
	}
	
	
	private MouseMode getCurrentMouseMode() {
		if(forceSelect)
			return MouseMode.SELECT;
		if(forceCamera)
			return MouseMode.CAMERA;
		return defaultToolbarMode;
	}
	
	private void showCurrentMode() {
		if(forceSelect)
			selectButton.setSelected(true);
		else if(forceCamera)
			cameraButton.setSelected(true);
		else {
			if(defaultToolbarMode == MouseMode.CAMERA)
				cameraButton.setSelected(true);
			else
				selectButton.setSelected(true);
		}
	}


	@Override
	public String toString() {
		return "ToolPanel [forceSelect=" + forceSelect + ", forceCamera="
				+ forceCamera + ", currentToolbarMode=" + defaultToolbarMode + "]";
	}
	
	
}
