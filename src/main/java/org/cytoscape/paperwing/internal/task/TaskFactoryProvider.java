package org.cytoscape.paperwing.internal.task;

import java.awt.datatransfer.Transferable;
import java.awt.geom.Point2D;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.NetworkViewLocationTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class TaskFactoryProvider {
	public  TaskFactory createFor(final NetworkViewTaskFactory factory, CyNetworkView networkView) {
		final Reference<CyNetworkView> reference = new WeakReference<CyNetworkView>(networkView);
		return new TaskFactory() {
			public TaskIterator createTaskIterator() {
				return factory.createTaskIterator(reference.get());
			}
			
			public boolean isReady() {
				return factory.isReady(reference.get());
			}
		};
	}
	
	public  TaskFactory createFor(final NetworkViewLocationTaskFactory factory, CyNetworkView networkView, final Point2D point, final Point2D transformedPoint) {
		final Reference<CyNetworkView> reference = new WeakReference<CyNetworkView>(networkView);
		return new TaskFactory() {
			public TaskIterator createTaskIterator() {
				return factory.createTaskIterator(reference.get(), point, transformedPoint);
			}
			
			public boolean isReady() {
				return factory.isReady(reference.get(), point, transformedPoint);
			}
		};
	}
	
	public  TaskFactory createFor(final NodeViewTaskFactory factory, View<CyNode> nodeView, CyNetworkView networkView) {
		final Reference<View<CyNode>> nodeReference = new WeakReference<View<CyNode>>(nodeView);
		final Reference<CyNetworkView> networkReference = new WeakReference<CyNetworkView>(networkView);
		return new TaskFactory() {
			public TaskIterator createTaskIterator() {
				return factory.createTaskIterator(nodeReference.get(), networkReference.get());
			}
			
			public boolean isReady() {
				return factory.isReady(nodeReference.get(), networkReference.get());
			}
		};
	}

	public  TaskFactory createFor(final EdgeViewTaskFactory factory, View<CyEdge> edgeView, CyNetworkView networkView) {
		final Reference<View<CyEdge>> edgeReference = new WeakReference<View<CyEdge>>(edgeView);
		final Reference<CyNetworkView> networkReference = new WeakReference<CyNetworkView>(networkView);
		return new TaskFactory() {
			public TaskIterator createTaskIterator() {
				return factory.createTaskIterator(edgeReference.get(), networkReference.get());
			}
			
			public boolean isReady() {
				return factory.isReady(edgeReference.get(), networkReference.get());
			}
		};
	
	}
}
