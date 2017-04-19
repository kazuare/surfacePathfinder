package org.educationalProject.surfacePathfinder.visualization;

import java.util.List;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;

public class DecolorizedMapVisualizer extends MapVisualizer{

	public void setData(WeightedGraph<Point, DefaultWeightedEdge> graph, List<Point> pathCoords){
		this.graph = graph;
		this.path = pathCoords;		
		dataSet = true;
	}
	protected void findExtremes(){		
		for(Point a : graph.vertexSet()){			
			minX = Math.min(minX, a.x);			
			minY = Math.min(minY, a.y);
			minAlt = Math.min(minAlt, a.alt);			
			maxX = Math.max(maxX, a.x);			
			maxY = Math.max(maxY, a.y);			
			maxAlt = Math.max(maxAlt, a.alt);
		}		
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
