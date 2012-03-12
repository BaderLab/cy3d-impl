package org.cytoscape.paperwing.internal.task;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPopupMenu;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.util.swing.JMenuTracker;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.swing.DialogTaskManager;

/**
 * This class is responsible for creating and populating pop-up menus created when right-clicking the network.
 */
public class PopupMenuCreator {

	public void createNodeMenu(View<CyNode> nodeView, 
			CyNetworkView networkView, 
			Map<NodeViewTaskFactory, Map<String, String>> taskFactories,
			DialogTaskManager taskManager) {
	
		JPopupMenu menu = new JPopupMenu();
		JMenuTracker tracker = new JMenuTracker(menu);
		
		if (taskFactories.size() == 1) {
			NodeViewTaskFactory nodeViewTaskFactory = taskFactories.keySet().iterator().next();
			
			nodeViewTaskFactory.setNodeView(nodeView, networkView);
			taskManager.execute(nodeViewTaskFactory);
			
		} else if (taskFactories.size() > 1) {
			for (Entry<NodeViewTaskFactory, Map<String, String>> entry : taskFactories.entrySet()) {

				entry.getKey().setNodeView(nodeView, networkView);
				
		
			}
		}
		
		
		/*
		
		// build a menu of actions if more than factory exists
		if ( usableTFs.size() > 1) {
			String nodeLabel = network.getRow(nv.getModel()).get("name",String.class);
			JPopupMenu menu = new JPopupMenu(nodeLabel);
			JMenuTracker tracker = new JMenuTracker(menu);

			for ( NodeViewTaskFactory nvtf : usableTFs ) {
				nvtf.setNodeView(nv, m_view);
				createMenuItem(nv, menu, nvtf, tracker, m_view.nodeViewTFs.get( nvtf ));
			}

			menu.show(invoker, x, y);

		// execute the task directly if only one factory exists
		} else if ( usableTFs.size() == 1) {
			NodeViewTaskFactory tf  = usableTFs.iterator().next();
			tf.setNodeView(nv, m_view);
			executeTask(tf);
		}
		
		*/
	}
}
