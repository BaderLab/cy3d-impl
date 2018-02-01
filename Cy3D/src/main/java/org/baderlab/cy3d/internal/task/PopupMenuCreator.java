package org.baderlab.cy3d.internal.task;

import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.util.swing.GravityTracker;
import org.cytoscape.util.swing.JMenuTracker;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.work.TaskFactory;
//import org.cytoscape.work.TaskFactoryPredicate;
import org.cytoscape.work.swing.DialogTaskManager;

/**
 * This class is responsible for creating and populating pop-up menus when right-clicking the network.
 */
public class PopupMenuCreator {
	
	/** For creating popup menu items for the network, only use task factories that match the preferred action*/
	private static final String NETWORK_PREFFERED_ACTION = "NEW";
	
	private DialogTaskManager taskManager;
	private TaskFactoryProvider taskFactoryProvider;
	
	// Large value to be used for the gravity value of org.cytoscape.util.swing.GravityTracker
	private double largeValue = Double.MAX_VALUE / 2.0;

	
	
	private static final String[][] networkFilter = {
		{"select", "all nodes and edges"},
	};
	
	private static final String[][] nodeFilter = {
		{"add", "edges connecting selected nodes"},
		{"edit", "cut"},
		{"edit", "copy"},
		{"edit", "paste"},
		{"select", "select first neighbors"},
	};
	
	private static final String[][] edgeFilter = {
		{"edit", "cut"},
		{"edit", "copy"},
	};
	
	
	/**
	 * Filter out menu items that currently cause problems.
	 */
	private static boolean filter(String[][] filter, Map<String,Object> properties) {
		String preferredMenu = (String) properties.get("preferredMenu");
		String title = (String) properties.get("title");
		
		if(preferredMenu == null || title == null) 
			return false;
		
		for(String[] allowed : filter) {
			String allowedMenu = allowed[0];
			String allowedTitle = allowed[1];
			
			if(preferredMenu.toLowerCase().startsWith(allowedMenu) && title.toLowerCase().startsWith(allowedTitle)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	
	
	public PopupMenuCreator(DialogTaskManager taskManager) {
		this.taskManager = taskManager;
		this.taskFactoryProvider = new TaskFactoryProvider();
	}
	
	public JPopupMenu createEdgeMenu(View<CyEdge> edgeView, CyNetworkView networkView, VisualLexicon visualLexicon, 
			                         Map<EdgeViewTaskFactory, Map<String, Object>> taskFactories) {
		
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuTracker tracker = new JMenuTracker(popupMenu);
		
		if (taskFactories.size() >= 1) {
			for (Entry<EdgeViewTaskFactory, Map<String, Object>> entry : taskFactories.entrySet()) {
				EdgeViewTaskFactory edgeViewTaskFactory = entry.getKey();
				Map<String, Object> properties = entry.getValue();
				
				if(!filter(edgeFilter, properties)) continue;
				
				TaskFactory taskFactory = taskFactoryProvider.createFor(edgeViewTaskFactory, edgeView, networkView);
				createMenuItem(edgeView, visualLexicon, popupMenu, taskFactory, tracker, properties);
			}
		}
		
		return popupMenu;
	}
	
	
	public JPopupMenu createNodeMenu(View<CyNode> nodeView, CyNetworkView networkView, VisualLexicon visualLexicon,
			                         Map<NodeViewTaskFactory, Map<String, Object>> taskFactories) {
		
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuTracker tracker = new JMenuTracker(popupMenu);
		
		if (taskFactories.size() >= 1) {
			for (Entry<NodeViewTaskFactory, Map<String, Object>> entry : taskFactories.entrySet()) {
				NodeViewTaskFactory nodeViewTaskFactory = entry.getKey();
				Map<String, Object> properties = entry.getValue();
				
				if(!filter(nodeFilter, properties)) continue;
				
				TaskFactory taskFactory = taskFactoryProvider.createFor(nodeViewTaskFactory, nodeView, networkView);
				createMenuItem(nodeView, visualLexicon, popupMenu, taskFactory, tracker, properties);
			}
		}
		
		return popupMenu;
	}
	
	
	public JPopupMenu createNetworkMenu(CyNetworkView networkView, VisualLexicon visualLexicon,
			                            Map<NetworkViewTaskFactory, Map<String, Object>> taskFactories) {
		
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuTracker tracker = new JMenuTracker(popupMenu);
		
		if (taskFactories.size() >= 1) {
			for (Entry<NetworkViewTaskFactory, Map<String, Object>> entry : taskFactories.entrySet()) {
				NetworkViewTaskFactory networkViewTaskFactory = entry.getKey();
				Map<String, Object> properties = entry.getValue();
				
				if(!filter(networkFilter, properties)) continue;
				
				Object preferredAction = properties.get("preferredAction");
				
				if (preferredAction != null && preferredAction.toString().equals(NETWORK_PREFFERED_ACTION)) {
					
					TaskFactory taskFactory = taskFactoryProvider.createFor(networkViewTaskFactory, networkView);
					createMenuItem(null, visualLexicon, popupMenu, taskFactory, tracker, properties);
				}
			}
		}
		
		return popupMenu;
	}

	
	private void createMenuItem(View<?> view, VisualLexicon visualLexicon, JPopupMenu popupMenu, TaskFactory taskFactory,
	                            JMenuTracker tracker, Map<String, Object> properties) {
		
		String title = null;
		if (properties.get("title") != null) {
			title = properties.get("title").toString();
		}
		
		String tooltip = null;
		if (properties.get("tooltip") != null) {
			tooltip = properties.get("tooltip").toString();
		}
		
		String preferredMenu = null;
		if (properties.get("preferredMenu") != null) {
			preferredMenu = properties.get("preferredMenu").toString();
		}


		
		Boolean useCheckBoxMenuItem = Boolean.parseBoolean(String.valueOf(properties.get("useCheckBoxMenuItem")));
		Object targetVisualProperty = properties.get("targetVP");
		boolean isSelected = false;
		
		// Update value for isSelected
		if(view != null) {
			if (targetVisualProperty != null && targetVisualProperty instanceof String) {

				Class<?> targetClass = CyNetwork.class;
				
				if (view.getModel() instanceof CyNode)
					targetClass = CyNode.class;
				else if (view.getModel() instanceof CyEdge)
					targetClass = CyEdge.class;
				
				VisualProperty<?> visualProperty = visualLexicon.lookup(CyNode.class, targetVisualProperty.toString());
				if (visualProperty == null)
					isSelected = false;
				else
					isSelected = view.isValueLocked(visualProperty);
			} else if ( targetVisualProperty instanceof VisualProperty)
				isSelected = view.isValueLocked((VisualProperty<?>) targetVisualProperty);
		}
		

		// no title and no preferred menu
		if (title == null && preferredMenu == null) {
			title = "Unidentified Task: " + Integer.toString(taskFactory.hashCode());
			popupMenu.add(createMenuItem(taskFactory, title, useCheckBoxMenuItem, tooltip));

		// title, but no preferred menu
		} else if (title != null && preferredMenu == null) {
			popupMenu.add(createMenuItem(taskFactory, title, useCheckBoxMenuItem, tooltip));

		// no title, but preferred menu
		} else if (title == null && preferredMenu != null) {
			int last = preferredMenu.lastIndexOf(".");

			// if the preferred menu is delimited
			if (last > 0) {
				title = preferredMenu.substring(last + 1);
				preferredMenu = preferredMenu.substring(0, last);
				final GravityTracker gravityTracker = tracker.getGravityTracker(preferredMenu);
				final JMenuItem item = createMenuItem(taskFactory, title,useCheckBoxMenuItem, tooltip);
				if (useCheckBoxMenuItem) {
					final JCheckBoxMenuItem checkBox = (JCheckBoxMenuItem)item; 
					checkBox.setSelected(isSelected);
				}
				gravityTracker.addMenuItem(item, ++largeValue);
			// otherwise just use the preferred menu as the menuitem name
			} else {
				title = preferredMenu;
				popupMenu.add(createMenuItem(taskFactory, title, useCheckBoxMenuItem, tooltip));
			}

		// title and preferred menu
		} else {
			final GravityTracker gravityTracker = tracker.getGravityTracker(preferredMenu);
			gravityTracker.addMenuItem(createMenuItem(taskFactory, title,useCheckBoxMenuItem, tooltip), ++largeValue);
		}
	}
		
	private JMenuItem createMenuItem(final TaskFactory taskFactory, String title, boolean useCheckBoxMenuItem, String toolTipText) {
		JMenuItem item;
		AbstractAction action = new AbstractAction(title){

			@Override
			public void actionPerformed(ActionEvent event) {
				taskManager.execute(taskFactory.createTaskIterator());
			}
			
		};
		
		if (useCheckBoxMenuItem)
			item = new JCheckBoxMenuItem(action);
		else
			item = new JMenuItem(action);

//		if (taskFactory instanceof TaskFactoryPredicate)
//			item.setEnabled(((TaskFactoryPredicate) taskFactory).isReady());

		item.setEnabled(taskFactory.isReady());
		
		item.setToolTipText(toolTipText);
		return item;
	}
}
