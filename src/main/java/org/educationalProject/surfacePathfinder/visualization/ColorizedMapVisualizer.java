package org.educationalProject.surfacePathfinder.visualization;

import org.jgrapht.graph.DefaultWeightedEdge;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class ColorizedMapVisualizer extends MapVisualizer{
	
	protected void drawContent( GL2 gl2 ){
		DrawingUtils.drawSurface(gl2, sceneParams, triangles);
		DrawingUtils.drawSurfaceEdges(gl2, sceneParams, triangles, 1);		
		gl2.glEnd();
	}
}
