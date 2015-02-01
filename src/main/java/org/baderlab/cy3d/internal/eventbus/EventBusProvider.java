package org.baderlab.cy3d.internal.eventbus;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.WeakHashMap;

import org.baderlab.cy3d.internal.cytoscape.view.Cy3DNetworkView;

import com.google.common.eventbus.EventBus;


/**
 * Acts as a single point for accessing the event bus for a Cy3DNetworkView.
 * 
 * @author mkucera
 */
public class EventBusProvider {

	private Map<Long,EventBus> eventBusMap = new WeakHashMap<>();
	
	
	/**
	 * Returns an event bus that can be used by all the parts of the renderer
	 * for a particular 3D network view.
	 * 
	 * Note: The parameter type is rather restrictive to ensure its not accidentally
	 * called on any CyIdentifiable.
	 */
	public synchronized EventBus getEventBus(Cy3DNetworkView identifiable) {
		Long suid = checkNotNull(identifiable).getSUID();
		EventBus eventBus = eventBusMap.get(suid);
		if(eventBus == null) {
			eventBus = new EventBus(suid.toString());
			eventBusMap.put(suid, eventBus);
		}
		return eventBus;
	}
	
}
