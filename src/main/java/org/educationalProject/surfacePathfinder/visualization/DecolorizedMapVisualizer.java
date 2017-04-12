package org.educationalProject.surfacePathfinder.visualization;

import java.util.List;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;

public class DecolorizedMapVisualizer extends MapVisualizer{

	public void setData(List<Triangle2D> triangles, List<Point> pathCoords){
		this.triangles = triangles;
		this.path = pathCoords;		
		dataSet = true;
	}
	
	protected void drawContent( GL2 gl2 ){
		gl2.glLineWidth(2f);
		gl2.glBegin(GL.GL_LINES);
        
		for(DefaultWeightedEdge edge : graph.edgeSet()){			
	        drawColoredPoint(gl2, graph.getEdgeSource(edge));
	        drawColoredPoint(gl2, graph.getEdgeTarget(edge));	      	        
		}
		
		gl2.glEnd();
	}
}
