package org.cytoscape.paperwing.internal.task;

import java.util.Collection;

import javax.swing.JPopupMenu;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.util.swing.JMenuTracker;
import org.cytoscape.view.model.View;

/**
 * This class is responsible for creating and populating pop-up menus created when right-clicking the network.
 */
public class PopupMenuCreator {

	public void createNodeMenu(View<CyNode> nodeView, Collection<NodeViewTaskFactory> taskFactories) {
	
		JPopupMenu menu = new JPopupMenu();
		JMenuTracker tracker = new JMenuTracker(menu);
		
		for (NodeViewTaskFactory taskFactory : taskFactories) {
			
		}
	}
}
