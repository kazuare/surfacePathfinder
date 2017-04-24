package org.educationalProject.surfacePathfinder.visualization;

import java.util.List;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class DecolorizedMapVisualizer extends MapVisualizer{

	public void setData(WeightedGraph<Point, DefaultWeightedEdge> graph, List<Point> pathCoords){
		this.graph = graph;
		this.path = pathCoords;		
		dataSet = true;
	}
	
	protected void drawContent( GL2 gl2 ){
		gl2.glLineWidth(1f);
		gl2.glBegin(GL.GL_LINES);
        
		for(DefaultWeightedEdge edge : graph.edgeSet()){			
	        drawColoredPoint(gl2, graph.getEdgeSource(edge));
	        drawColoredPoint(gl2, graph.getEdgeTarget(edge));	      	        
		}
		
		gl2.glEnd();
	}
}
