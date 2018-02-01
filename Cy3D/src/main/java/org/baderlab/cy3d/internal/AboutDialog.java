package org.baderlab.cy3d.internal;


import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.util.swing.OpenBrowser;



/**
 * This class handles about WordCloud popup from the WordCloud menu.
 */
@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

	private static final String BUILD_PROPS_FILE = "/buildinfo.props";
	private static final String BUILD_VERSION = "build.version";
	private static final String BUILD_TIMESTAMP = "build.timestamp";
	private static final String URL = "http://baderlab.org/Software";
	
	private final OpenBrowser openBrowser;

	
	public AboutDialog(CySwingApplication application, OpenBrowser openBrowser) {
		super(application.getJFrame(), "About Cy3D", ModalityType.MODELESS);
		this.openBrowser = openBrowser;
		
		setResizable(false);

		Properties buildProps = getBuildProperties();
		String version = buildProps.getProperty(BUILD_VERSION);
		String timestamp = buildProps.getProperty(BUILD_TIMESTAMP);
		
		// main panel for dialog box
		JEditorPane editorPane = new JEditorPane();
		editorPane.setMargin(new Insets(10, 10, 10, 10));
		editorPane.setEditable(false);
		editorPane.setEditorKit(new HTMLEditorKit());
		editorPane.addHyperlinkListener(new HyperlinkAction(editorPane));

		editorPane
				.setText("<html><body>"
						+ "<table border='0'><tr>"
						+ "<td width='125'></td>"
						+ "<td width='200'>"
						+ "<p align=center><b>Cy3D</b><BR>A Cytoscape App<BR><BR></p>"
						+ "<p align=center>Version: " + version + "<br>Build: " + timestamp + "</p>"
						+ "</td>"
						+ "<td width='125'><div align='right'></td>"
						+ "</tr></table>"
						+ "<p align=center>"
						+ "3D Network Renderer for Cytoscape."
						+ "<BR><BR>"
						+ "by Yue Dong, Mike Kucera and Gary Bader<BR>"
						+ "(<a href='http://www.baderlab.org/'>Bader Lab</a>, University of Toronto)<BR>"
						+ "<BR>" + "App Homepage:<BR>" + "<a href='"
						+ URL + "'>" + URL + "</a><BR>" + "<BR>"
						+ "<font size='-1'>" + "</font>" + "</p></body></html>");
		
		setContentPane(editorPane);
	}
	
	
	private Properties getBuildProperties() {
		InputStream in = CyActivator.class.getResourceAsStream(BUILD_PROPS_FILE);
		if(in == null)
			return new Properties();
		try {
			Properties buildProps = new Properties();
			buildProps.load(in);
			return buildProps;
		} catch(IOException e) {
			return new Properties();
		} finally {
			try {
				in.close();
			} catch (IOException e) { }
		}
	}

	private class HyperlinkAction implements HyperlinkListener {
		@SuppressWarnings("unused")
		JEditorPane pane;

		public HyperlinkAction(JEditorPane pane) {
			this.pane = pane;
		}

		public void hyperlinkUpdate(HyperlinkEvent event) {
			if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				openBrowser.openURL(event.getURL().toString());
			}
		}
	}
}
