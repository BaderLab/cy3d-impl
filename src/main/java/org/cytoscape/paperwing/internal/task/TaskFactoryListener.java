package org.cytoscape.paperwing.internal.task;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.event.CyListener;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.work.TaskFactory;

/**
 * This class is responsible for keeping track of all the current view {@link TaskFactory} objects,
 * which are used in situations such as needing to execute a certain task via the right-click menu.
 */
public class TaskFactoryListener implements CyListener {
	private Map<NodeViewTaskFactory, Map<String, Object>> nodeViewTaskFactories;
	private Map<EdgeViewTaskFactory, Map<String, Object>> edgeViewTaskFactories;
	
	private Map<NetworkViewTaskFactory, Map<String, Object>> networkViewTaskFactories;
	
	public TaskFactoryListener() {
		nodeViewTaskFactories = new HashMap<NodeViewTaskFactory, Map<String, Object>>();
		edgeViewTaskFactories = new HashMap<EdgeViewTaskFactory, Map<String, Object>>();
		networkViewTaskFactories = new HashMap<NetworkViewTaskFactory, Map<String, Object>>();
	}
	
	public void addNodeViewTaskFactory(NodeViewTaskFactory taskFactory, Map<String, Object> properties) {
		nodeViewTaskFactories.put(taskFactory, properties);
		
		// printTaskFactoryDetails(taskFactory, properties);
	}
	
	public void addEdgeViewTaskFactory(EdgeViewTaskFactory taskFactory, Map<String, Object> properties) {
		edgeViewTaskFactories.put(taskFactory, properties);
		
		// printTaskFactoryDetails(taskFactory, properties);
	}
	
	public void addNetworkViewTaskFactory(NetworkViewTaskFactory taskFactory, Map<String, Object> properties) {
		networkViewTaskFactories.put(taskFactory, properties);
		
		// printTaskFactoryDetails(taskFactory, properties);
	}
	
	public Map<NodeViewTaskFactory, Map<String, Object>> getNodeViewTaskFactories() {
		return nodeViewTaskFactories;
	}
	
	public Map<EdgeViewTaskFactory, Map<String, Object>> getEdgeViewTaskFactories() {
		return edgeViewTaskFactories;
	}
	
	public Map<NetworkViewTaskFactory, Map<String, Object>> getNetworkViewTaskFactories() {
		return networkViewTaskFactories;
	}
	
	public void removeNodeViewTaskFactory(NodeViewTaskFactory taskFactory, Map<String, Object> properties) {
		nodeViewTaskFactories.put(taskFactory, properties);
	}
	
	public void removeEdgeViewTaskFactory(EdgeViewTaskFactory taskFactory, Map<String, Object> properties) {
		edgeViewTaskFactories.put(taskFactory, properties);
	}
	
	public void removeNetworkViewTaskFactory(NetworkViewTaskFactory taskFactory, Map<String, Object> properties) {
		networkViewTaskFactories.put(taskFactory, properties);
	}
	
	private void printTaskFactoryDetails(TaskFactory taskFactory, Map<String, String> properties) {
		System.out.println("TaskFactory added: " + taskFactory);
		System.out.println("Properties: " + properties);
	}
}
