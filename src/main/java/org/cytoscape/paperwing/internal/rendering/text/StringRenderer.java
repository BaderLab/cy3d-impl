package org.cytoscape.paperwing.internal.rendering.text;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import org.cytoscape.paperwing.internal.geometric.Vector3;

public class StringRenderer {
	
	private static final float CHARACTER_SPACING = 0.1f;
	
	private static final float MAX_CHARACTER_HEIGHT = 1.0f;
	private static final float MAX_CHARACTER_SECONDARY_HEIGHT = 0.5f;
	private static final float MAX_CHARACTER_WIDTH = 0.5f;	
	
	Map<Character, RenderedCharacter> characterLists;
	
	private class RenderedCharacter {
		int characterListIndex;
		float width;
		char character;
		
		@Override
		public String toString() {
			return ("RenderedCharacter " + character);
		}
	}
	
	public StringRenderer() {
		characterLists = new HashMap(80);
	}
	
	public void initialize(GL2 gl) {
		initializeCharacterSet(gl);
		
		initializeCharacterLists(gl);
		
		// System.out.println(characterLists);
	}
	
	private void initializeCharacterSet(GL2 gl) {
		int startingIndex;
		startingIndex = gl.glGenLists(26);
		char startingCharacter = 'a';
	
		RenderedCharacter renderedCharacter;
		
		for (int i = 0; i < 26; i++) {
			renderedCharacter = new RenderedCharacter();
			renderedCharacter.character = startingCharacter++;
			renderedCharacter.characterListIndex = startingIndex++;
			
			characterLists.put(renderedCharacter.character, renderedCharacter);
		}
	}
	
	private void initializeCharacterLists(GL2 gl) {
		int listIndex;
		
		float maxHeight = 1.0f;
		float secondaryHeight = 0.5f;
		float maxWidth = 0.5f;
		
		for (RenderedCharacter renderedCharacter : characterLists.values()) {
			listIndex = renderedCharacter.characterListIndex;
			renderedCharacter.width = maxWidth;
			
			gl.glNewList(listIndex, GL2.GL_COMPILE);
			gl.glBegin(GL2.GL_LINE_STRIP);
			
			// Characters are drawn with bottom left as origin, extends to (1, 0.5)
			
			switch (renderedCharacter.character) {
			case 'a':
				gl.glVertex2f(maxWidth, secondaryHeight * 0.0f);
				gl.glVertex2f(maxWidth, secondaryHeight * 0.9f);
				gl.glVertex2f(maxWidth * 0.9f, secondaryHeight);
				gl.glVertex2f(maxWidth * 0.2f, secondaryHeight);
				gl.glVertex2f(maxWidth * 0.1f, secondaryHeight * 0.7f);
				gl.glVertex2f(maxWidth * 0.05f, secondaryHeight * 0.0f);
				gl.glVertex2f(maxWidth, secondaryHeight * 0.1f);
				break;
			case 'c':
				gl.glVertex2f(maxWidth, secondaryHeight);
				gl.glVertex2f(maxWidth * 0.1f, secondaryHeight);
				gl.glVertex2f(maxWidth * 0.1f, secondaryHeight * 0.0f);
				gl.glVertex2f(maxWidth, secondaryHeight * 0.0f);
				break;
			}
			
			gl.glEnd();
			gl.glEndList();
			
			
		}
	}
	
	private void drawString(GL2 gl, String text) {
		char current;
		RenderedCharacter renderedCharacter;
		
		for (int i = 0; i < text.length(); i++) {
			current = text.charAt(i);
			
			renderedCharacter = characterLists.get(current);
			
//			System.out.println("get result for " + current + "in " + text + ": " + renderedCharacter);
			
			if (renderedCharacter != null) {
				gl.glCallList(renderedCharacter.characterListIndex);
//				System.out.println("calling list for :" + renderedCharacter.character);
				gl.glTranslatef(renderedCharacter.width + CHARACTER_SPACING, 0, 0);
			}
		}
	}
	
	public void drawCenteredText(GL2 gl, String text) {
		float stringWidth = 0;
		
		char current;
		RenderedCharacter renderedCharacter;
		
		for (int i = 0; i < text.length(); i++) {
			current = text.charAt(i);
			
			renderedCharacter = characterLists.get(current);
			
			if (renderedCharacter != null) {
				stringWidth += renderedCharacter.width;
			}
		}
		
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glPushMatrix();
		
		gl.glTranslatef(-stringWidth / 2, 0, 0);
		drawString(gl, text);
		
		gl.glPopMatrix();
		gl.glEnable(GL2.GL_LIGHTING);
	}
}
