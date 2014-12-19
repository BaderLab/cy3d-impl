package org.baderlab.cy3d.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.baderlab.cy3d.internal.data.SettingsData;
import org.baderlab.cy3d.internal.data.SettingsData.CameraDragMode;

/**
 * Tool panel that overlays the renderer window.
 * 
 * @author mkucera
 *
 */
public class ToolPanel {
	
	
	private CameraDragMode currentCameraMode;
	
	
	private ToolPanel() {}
	
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
		
		createButton("Select", CameraDragMode.OFF,    cameraToolBar, cameraGroup).setSelected(true);
		createButton("Pan",    CameraDragMode.PAN,    cameraToolBar, cameraGroup);
		createButton("Strafe", CameraDragMode.STRAFE, cameraToolBar, cameraGroup);
		createButton("Orbit",  CameraDragMode.ORBIT,  cameraToolBar, cameraGroup);
		
		currentCameraMode = CameraDragMode.OFF;
		
		glass.add(cameraToolBar, BorderLayout.WEST);
	}
	
	private JToggleButton createButton(String text, final CameraDragMode mode, Container parent, ButtonGroup group) {
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
				currentCameraMode = mode;
			}
		});
		return button;
	}
	
	
	public SettingsData getSettingsData() {
		return new ExposedSettingsData();
	}
	
	
	private class ExposedSettingsData implements SettingsData {

		@Override
		public CameraDragMode getCameraDragMode() {
			return currentCameraMode;
		}

		@Override
		public boolean isSelectMode() {
			return currentCameraMode == CameraDragMode.OFF;
		}

		@Override
		public boolean resetCamera() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	
}
