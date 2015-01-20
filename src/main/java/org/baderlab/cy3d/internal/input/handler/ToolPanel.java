package org.baderlab.cy3d.internal.input.handler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

import org.baderlab.cy3d.internal.icons.IconManager;
import org.baderlab.cy3d.internal.icons.IconManagerImpl;

/**
 * Toolbar panel that overlays the renderer window.
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
	
	
	/**
	 * The input event listeners should use this to register
	 * for events that indicate changes to the toolbar mode.
	 */
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
		
		JPanel toolPanel = new JPanel();
		toolPanel.setLayout(new GridBagLayout());
		toolPanel.setOpaque(false);
		
		// Camera mode buttons
		JPanel cameraToolBar = new JPanel();
		ButtonGroup cameraGroup = new ButtonGroup();
		cameraToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		cameraToolBar.setOpaque(false);
		
		selectButton = createModeButton("Select", MouseMode.SELECT, cameraGroup);
		cameraButton = createModeButton("Camera", MouseMode.CAMERA, cameraGroup);
		cameraToolBar.add(selectButton);
		cameraToolBar.add(cameraButton);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		toolPanel.add(cameraToolBar, gbc);
		
		// Help button
		JLabel helpLabel = createHelpButton();
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weighty = 0.5;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		toolPanel.add(helpLabel, gbc);
		
		glass.add(toolPanel, BorderLayout.NORTH);
	}
	
	
	private JToggleButton createModeButton(String text, final MouseMode mode, ButtonGroup group) {
		JToggleButton button = new JToggleButton(text);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setPreferredSize(new Dimension(40, 20));
		button.setFont(new Font("Arial", Font.PLAIN, 8));
		button.setFocusable(false);
		group.add(button);
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

	
	private JLabel createHelpButton() {
		String helpText;
		try {
			helpText = getHelpContents();
		} catch(IOException e) {
			e.printStackTrace();
			return new JLabel();
		}
		
		IconManager iconManager = new IconManagerImpl();
		final JLabel questionLabel = new JLabel();
		questionLabel.setText(IconManager.ICON_QUESTION_SIGN);
		questionLabel.setFont(iconManager.getIconFont(14));
		questionLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		questionLabel.setToolTipText(helpText);
		
		questionLabel.addMouseListener(new MouseAdapter() {
			int restoreInitialDelay;
			int restoreDismissDelay;
			
			@Override
			public void mouseEntered(MouseEvent e) {
				ToolTipManager ttManager = ToolTipManager.sharedInstance();
				restoreInitialDelay = ttManager.getInitialDelay();
				restoreDismissDelay = ttManager.getDismissDelay();
				ttManager.setInitialDelay(0);
				ttManager.setDismissDelay(60000);
				MouseEvent me = new MouseEvent(questionLabel, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 0, 0, 0, false);
				ttManager.mouseMoved(me);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				ToolTipManager ttManager = ToolTipManager.sharedInstance();
				ttManager.setInitialDelay(restoreInitialDelay);
				ttManager.setDismissDelay(restoreDismissDelay);
			}
		});
		
		return questionLabel;
	}
	
	
	private String getHelpContents() throws IOException {
		InputStream in = getClass().getResourceAsStream("/controls.html");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[512];
		int length = 0;
	    while ((length = in.read(buffer)) != -1) {
	        out.write(buffer, 0, length);
	    }
	   
	    out.close();
	    in.close();
		return new String(out.toByteArray(), "UTF-8");
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
