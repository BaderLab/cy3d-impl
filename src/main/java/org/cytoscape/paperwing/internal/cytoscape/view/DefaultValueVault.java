package org.cytoscape.paperwing.internal.cytoscape.view;

import java.util.LinkedHashMap;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;

public class DefaultValueVault {
	
	// Assumes VisualProperty ID names are unique
	private LinkedHashMap<String, VisualPropertyValueHolder<?>> nodeDefaultValues;
	private LinkedHashMap<String, VisualPropertyValueHolder<?>> edgeDefaultValues;
	private LinkedHashMap<String, VisualPropertyValueHolder<?>> networkDefaultValues;
	
	private VisualLexicon visualLexicon;
	
	public DefaultValueVault(VisualLexicon visualLexicon) {
		this.visualLexicon = visualLexicon;
		
		nodeDefaultValues = new LinkedHashMap<String, VisualPropertyValueHolder<?>>();
		edgeDefaultValues = new LinkedHashMap<String, VisualPropertyValueHolder<?>>();
		networkDefaultValues = new LinkedHashMap<String, VisualPropertyValueHolder<?>>();
	
		populateDefaultValues();
	}
	
	// Sets initial default values
	private void populateDefaultValues() {
		VisualPropertyValueHolder<?> valueHolder;
		Class<?> targetDataType;
		
		for (VisualProperty<?> visualProperty : visualLexicon.getAllVisualProperties()) {
			valueHolder = new VisualPropertyValueHolder(visualProperty.getDefault());
			targetDataType = visualProperty.getTargetDataType();
			
			if (targetDataType == CyNode.class) {
				nodeDefaultValues.put(visualProperty.getIdString(), valueHolder);
			} else if (targetDataType == CyEdge.class) {
				edgeDefaultValues.put(visualProperty.getIdString(), valueHolder);
			} else if (targetDataType == CyNetwork.class) {
				networkDefaultValues.put(visualProperty.getIdString(), valueHolder);
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
		
		if (targetDataType == CyNode.class) {
			nodeDefaultValues.put(visualProperty.getIdString(), valueHolder);
		} else if (targetDataType == CyEdge.class) {
			edgeDefaultValues.put(visualProperty.getIdString(), valueHolder);
		} else if (targetDataType == CyNetwork.class) {
			networkDefaultValues.put(visualProperty.getIdString(), valueHolder);
		}
	}
	
	public void initializeNode(VisualPropertyKeeper<CyNode> keeper) {
		VisualPropertyValueHolder<?> valueHolder;
		
		for (String key : nodeDefaultValues.keySet()) {
			valueHolder = nodeDefaultValues.get(key);	
			keeper.setVisualProperty(key, valueHolder);
		}
	}
	
	public void initializeEdge(VisualPropertyKeeper<CyEdge> keeper) {
		VisualPropertyValueHolder<?> valueHolder;
		
		for (String key : edgeDefaultValues.keySet()) {
			valueHolder = edgeDefaultValues.get(key);	
			keeper.setVisualProperty(key, valueHolder);
		}
	}
	
	public void initializeNetwork(VisualPropertyKeeper<CyNetwork> keeper) {
		VisualPropertyValueHolder<?> valueHolder;
		
		for (String key : networkDefaultValues.keySet()) {
			valueHolder = networkDefaultValues.get(key);	
			keeper.setVisualProperty(key, valueHolder);
		}
	}
}


