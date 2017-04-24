package org.educationalProject.surfacePathfinder.visualization;

import java.util.List;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;
/**
* Visualizes top down map image.
* Subclasses can draw either filled triangles or not filled
*/
public class GraphVisualizer extends MapVisualizer {
	protected WeightedGraph<Point, DefaultWeightedEdge> graph;
	
	/**
	* Sets data that needs to be visualized
	*/
	public void setData(WeightedGraph<Point, DefaultWeightedEdge> graph){
		this.graph = graph;
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
	
	public void display( GL2 gl2 ){
		gl2.glClear( GL.GL_COLOR_BUFFER_BIT );
	    gl2.glLoadIdentity();
	        
		findExtremes();				
			
		drawContent(gl2);
			
	}
}
