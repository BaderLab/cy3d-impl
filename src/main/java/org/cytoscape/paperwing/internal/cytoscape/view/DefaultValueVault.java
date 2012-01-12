package org.cytoscape.paperwing.internal.cytoscape.view;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;

public class DefaultValueVault {
	
	// Assumes VisualProperty ID names are unique
	private Map<String, VisualPropertyValueHolder<?>> nodeDefaultValues;
	private Map<String, VisualPropertyValueHolder<?>> edgeDefaultValues;
	private Map<String, VisualPropertyValueHolder<?>> networkDefaultValues;
	
	private Map<Class<? extends CyTableEntry>,
		Map<String, VisualPropertyValueHolder<?>>> defaultValueSets;
	
	private VisualLexicon visualLexicon;
	
	public DefaultValueVault(VisualLexicon visualLexicon) {
		this.visualLexicon = visualLexicon;
		
		nodeDefaultValues = new HashMap<String, VisualPropertyValueHolder<?>>();
		edgeDefaultValues = new HashMap<String, VisualPropertyValueHolder<?>>();
		networkDefaultValues = new HashMap<String, VisualPropertyValueHolder<?>>();
		
		defaultValueSets = new HashMap<Class<? extends CyTableEntry>, Map<String, VisualPropertyValueHolder<?>>>();
		defaultValueSets.put(CyNode.class, nodeDefaultValues);
		defaultValueSets.put(CyEdge.class, edgeDefaultValues);
		defaultValueSets.put(CyNetwork.class, networkDefaultValues);
	
		populateDefaultValues();
	}
	
	// Sets initial default values
	private void populateDefaultValues() {
		VisualPropertyValueHolder<?> valueHolder;
		Class<?> targetDataType;
		
		for (VisualProperty<?> visualProperty : visualLexicon.getAllVisualProperties()) {
			valueHolder = new VisualPropertyValueHolder<Object>(visualProperty.getDefault());
			targetDataType = visualProperty.getTargetDataType();
			
			if (defaultValueSets.get(targetDataType) != null) {
				defaultValueSets.get(targetDataType).put(visualProperty.getIdString(), valueHolder);
			}
		}
	}
	
//	@Override
//	public <T, V extends T> void setViewDefault(VisualProperty<? extends T> visualProperty,
//			V defaultValue) {
//		
//	}
	
	public <T, V extends T> void modifyDefaultValue(VisualProperty<? extends T> visualProperty, V value) {
		Class<?> targetDataType = visualProperty.getTargetDataType();
		
		VisualPropertyValueHolder<V> valueHolder = new VisualPropertyValueHolder<V>(value);
		
		if (defaultValueSets.get(targetDataType) != null) {
			defaultValueSets.get(targetDataType).put(visualProperty.getIdString(), valueHolder);
		}
	}
	
	public void initializeNode(VisualPropertyKeeper<CyNode> keeper) {
		for (Entry<String, VisualPropertyValueHolder<?>> entry: nodeDefaultValues.entrySet()) {	
			keeper.setVisualProperty(entry.getKey(), entry.getValue().getValue());
		}
	}
	
	public void initializeEdge(VisualPropertyKeeper<CyEdge> keeper) {
		for (Entry<String, VisualPropertyValueHolder<?>> entry: edgeDefaultValues.entrySet()) {	
			keeper.setVisualProperty(entry.getKey(), entry.getValue().getValue());
		}
	}
	
	public void initializeNetwork(VisualPropertyKeeper<CyNetwork> keeper) {
		for (Entry<String, VisualPropertyValueHolder<?>> entry: networkDefaultValues.entrySet()) {	
			keeper.setVisualProperty(entry.getKey(), entry.getValue().getValue());
		}
	}
}


