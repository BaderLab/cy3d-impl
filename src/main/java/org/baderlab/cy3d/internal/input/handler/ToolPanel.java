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

import javax.swing.ButtonGroup;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Tool panel that overlays the renderer window.
 * 
 * @author mkucera
 *
 */
public class ToolPanel {
	
	
	private Set<MouseModeChangeListener> listeners = new LinkedHashSet<>();
	
	public interface MouseModeChangeListener {
		void mouseModeChanged(MouseMode mouseMode);
	}
	
	
	private ToolPanel() {}
	
	
	public boolean addMouseModeChangeListener(MouseModeChangeListener listner) {
		return listeners.add(listner);
	}
	
	public boolean removeMouseModeChangeListener(MouseModeChangeListener listener) {
		return listeners.remove(listener);
	}
	
	private void fireMouseModeChangeEvent(MouseMode mouseMode) {
		for(MouseModeChangeListener listener : listeners) {
			listener.mouseModeChanged(mouseMode);
		}
	}
	
	
	public static ToolPanel createFor(JInternalFrame frame) {
		ToolPanel toolPanel = new ToolPanel();
		toolPanel.setUpGlassPane(frame);
		return toolPanel;
	}
	
	private void setUpGlassPane(JInternalFrame frame) {
		JPanel glass = (JPanel) frame.getGlassPane();
		glass.setLayout(new BorderLayout());
		glass.setVisible(true);
		
		ButtonGroup cameraGroup = new ButtonGroup();
		JPanel cameraToolBar = new JPanel();
		cameraToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		cameraToolBar.setOpaque(false);
		
		createButton("Select", MouseMode.SELECT, cameraToolBar, cameraGroup);
		createButton("Pan",    MouseMode.PAN,    cameraToolBar, cameraGroup);
		createButton("Strafe", MouseMode.STRAFE, cameraToolBar, cameraGroup);
		createButton("Orbit",  MouseMode.ORBIT,  cameraToolBar, cameraGroup);
		
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
				fireMouseModeChangeEvent(mode);
			}
		});
		
		if(mode == MouseMode.getDefault()) {
			button.setSelected(true);
		}
		
		return button;
	}
	
	
}
