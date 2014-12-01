package org.baderlab.cy3d.internal.rendering;

import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL2;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.rendering.shapes.EdgeShapeDrawer;
import org.baderlab.cy3d.internal.rendering.shapes.EdgeShapeDrawer.EdgeShapeType;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.baderlab.cy3d.internal.tools.PairIdentifier;
import org.baderlab.cy3d.internal.tools.RenderToolkit;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

/**
 * This procedure renders edges in a simplified form for the bird's eye view.
 */
public class RenderSimplifiedEdgesProcedure implements ReadOnlyGraphicsProcedure {

	private EdgeShapeDrawer shapeDrawer;
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		shapeDrawer = new EdgeShapeDrawer();
		shapeDrawer.initialize(graphicsData.getGlContext());
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		GL2 gl = graphicsData.getGlContext();
		
		float[] specularReflection = { 0.1f, 0.1f, 0.1f, 1.0f };
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR,
				FloatBuffer.wrap(specularReflection));
		gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 1);
		
		double distanceScale = graphicsData.getDistanceScale();
		
		// A set containing all pairs of nodes that have had an edge drawn between them
		Set<PairIdentifier> drawnPairs = new HashSet<PairIdentifier>();
		CyNode source, target;
		
		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {
			source = edgeView.getModel().getSource();
			target = edgeView.getModel().getTarget();
			
			PairIdentifier pairIdentifier = NetworkToolkit.obtainPairIdentifier(source, target, networkView.getModel().getNodeList().size());
			
			// Only draw an edge between this source-target pair if one has not been drawn already
			if (!drawnPairs.contains(pairIdentifier)) {
			
				Vector3 start = NetworkToolkit.obtainNodeCoordinates(source, networkView, distanceScale);
				Vector3 end = NetworkToolkit.obtainNodeCoordinates(target, networkView, distanceScale);
				Vector3 displacement = end.subtract(start);
				
				gl.glPushMatrix();
				
				// Setup transformations to draw the shape
				RenderToolkit.setUpFacingTransformation(gl, start, displacement);
				gl.glScalef(1.0f, 1.0f, (float) displacement.magnitude());
				
				// Perform drawing
				shapeDrawer.drawSegment(gl, EdgeShapeType.REGULAR_LINE_BASED);
				
				gl.glPopMatrix();
				
				drawnPairs.add(pairIdentifier);
			}
		}
	}

}
