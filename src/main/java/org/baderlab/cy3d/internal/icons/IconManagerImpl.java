package org.baderlab.cy3d.internal.icons;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JLabel;


public class IconManagerImpl implements IconManager {

	private Font iconFont;

	public IconManagerImpl() {
		try {
			iconFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/fontawesome-webfont.ttf"));
		} catch (FontFormatException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Font getIconFont(float size) {
		return iconFont.deriveFont(size);
	}

	@Override
	public Cursor getIconCursor(String icon) {
		Dimension size = Toolkit.getDefaultToolkit().getBestCursorSize(24, 24);
		Image image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();
		
		JLabel label = new JLabel();
		label.setBounds(0 , 0, size.width, size.height);
		label.setText(icon);
		label.setFont(getIconFont(14));
		label.paint(graphics);
		graphics.dispose();
		
		Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0,0), "custom:" + (int)icon.charAt(0));
		return cursor;
	}
	
}
