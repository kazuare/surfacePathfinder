package org.educationalProject.surfacePathfinder;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;

import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;

public class ColorizedMapVisualizer extends MapVisualizer{
	
	protected void drawContent( GL2 gl2 ){
		gl2.glBegin(gl2.GL_TRIANGLES);	
		for(Triangle2D triangle : triangles){

			drawColoredPoint(gl2, (Point)triangle.a);
			drawColoredPoint(gl2, (Point)triangle.b);
			drawColoredPoint(gl2, (Point)triangle.c);
			
		}
		gl2.glEnd();

		gl2.glLineWidth(1);
		gl2.glBegin(gl2.GL_LINES);
		gl2.glColor3f(0.5f, 0, 0.5f);
		for(DefaultWeightedEdge edge : graph.edgeSet()){			
			drawPoint(gl2, graph.getEdgeSource(edge));
			drawPoint(gl2, graph.getEdgeTarget(edge));	      	        
		}
		
		gl2.glEnd();
	}
}
