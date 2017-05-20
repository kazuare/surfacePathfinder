package org.educationalProject.surfacePathfinder.visualization;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;

public class ColorizedMapVisualizer extends MapVisualizer{
	
	protected void drawContent( GL2 gl2 ){
		gl2.glBegin(GL.GL_TRIANGLES);	
		for(Triangle2D triangle : triangles){

			DrawingUtils.drawColoredPoint(gl2, sceneParams, (Point)triangle.a);
			DrawingUtils.drawColoredPoint(gl2, sceneParams, (Point)triangle.b);
			DrawingUtils.drawColoredPoint(gl2, sceneParams, (Point)triangle.c);
			
		}
		gl2.glEnd();

		gl2.glLineWidth(1);
		gl2.glBegin(GL.GL_LINES);
		gl2.glColor3f(0.5f, 0, 0.5f);
		for(DefaultWeightedEdge edge : graph.edgeSet()){			
			DrawingUtils.drawPoint(gl2, sceneParams, graph.getEdgeSource(edge));
			DrawingUtils.drawPoint(gl2, sceneParams, graph.getEdgeTarget(edge));	      	        
		}
		
		gl2.glEnd();
	}
}
