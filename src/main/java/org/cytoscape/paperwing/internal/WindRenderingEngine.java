package org.cytoscape.paperwing.internal;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;

public class WindRenderingEngine implements RenderingEngine<CyNetwork> {

	private View<CyNetwork> viewModel;
	private VisualLexicon lexicon;
	
	public WindRenderingEngine(View<CyNetwork> viewModel, VisualLexicon lexicon) {
		this.viewModel = viewModel;
		this.lexicon = lexicon;
		
		//System.out.println("renderingengine viewMondel: " + viewModel);
		//System.out.println("renderingengine visualLexicon: " + lexicon);
		//System.out.println("");
	}
	
	@Override
	public View<CyNetwork> getViewModel() {
		return viewModel;
	}

	@Override
	public VisualLexicon getVisualLexicon() {
		return lexicon;
	}

	@Override
	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Printable createPrintable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image createImage(int width, int height) {
		// TODO Auto-generated method stub
		return new BufferedImage(0, width, height);
	}

	@Override
	public <V> Icon createIcon(VisualProperty<V> vp, V value, int width,
			int height) {
		// TODO Auto-generated method stub
		return new ImageIcon();
	}

}
