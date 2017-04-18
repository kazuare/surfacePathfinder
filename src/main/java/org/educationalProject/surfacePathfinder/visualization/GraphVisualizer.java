package org.educationalProject.surfacePathfinder.visualization;

import java.util.List;

import org.educationalProject.surfacePathfinder.Point;
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
public class GraphVisualizer extends Visualizer {
	protected SimpleWeightedGraph<Point,DefaultWeightedEdge> graph;
	
	/**
	* Sets data that needs to be visualized
	*/
	public void setData(SimpleWeightedGraph<Point,DefaultWeightedEdge> graph){
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
	
	protected double maxX = Double.NEGATIVE_INFINITY;
	protected double minX = Double.POSITIVE_INFINITY;
	protected double maxY = Double.NEGATIVE_INFINITY;
	protected double minY = Double.POSITIVE_INFINITY;
	protected double maxAlt = Double.NEGATIVE_INFINITY;
	protected double minAlt = Double.POSITIVE_INFINITY;
	
	/**
	* translates map width into screen width
	*/
	protected float normalizeWidth(double data){
		return (float) (width * (data - minX)/(maxX - minX));
	}
	/**
	* translates map height into screen height
	*/
	protected float normalizeHeight(double data){
		return (float) (height * (data - minY)/(maxY - minY));
	}
	
	protected void drawPoint(GL2 gl2, Vector2D a){
		gl2.glVertex2f(
			normalizeWidth(a.x), 
			normalizeHeight(a.y)
		); 
	}
	protected void drawColoredPoint(GL2 gl2, Point a){
		gl2.glColor3f(1, normalizeColor(a.alt), 0);	
		drawPoint(gl2, a);
	}
	
	protected float normalizeColor(double data){
		return (float) ((data - minAlt)/(maxAlt - minAlt));
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
	
	
	
	public void display( GL2 gl2 ){
		gl2.glClear( GL.GL_COLOR_BUFFER_BIT );
	    gl2.glLoadIdentity();
	        
		findExtremes();				
			
		drawContent(gl2);
			
	}
}
